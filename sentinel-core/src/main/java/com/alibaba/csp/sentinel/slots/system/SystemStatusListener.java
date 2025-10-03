/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.system;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.StringUtil;

import com.sun.management.OperatingSystemMXBean;

/**
 * @author jialiang.linjl
 */
public class SystemStatusListener implements Runnable {

    volatile double currentLoad = -1;
    volatile double currentCpuUsage = -1;

    volatile String reason = StringUtil.EMPTY;

    volatile long processCpuTime = 0;
    volatile long processUpTime = 0;

    public double getSystemAverageLoad() {
        return currentLoad;
    }

    public double getCpuUsage() {
        return currentCpuUsage;
    }

    @Override
    public void run() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            // 	含义
            //	•	这个指标是 操作系统的负载（Load Average），单位时间（通常 1 分钟）内的 平均就绪队列长度。
            //	•	就绪队列长度 = 等待 CPU 的进程数（包含正在运行的 + 等待调度的）。
            //	举例
            //	•	如果 systemLoadAverage = 4.0，表示过去 1 分钟平均有 4 个任务在竞争 CPU。
            //	•	假设机器有 4 核 CPU：
            //	•	如果 load ≈ 4，说明系统比较健康（刚好饱和）。
            //	•	如果 load ≫ 4（比如 10），说明任务太多，CPU 已经处理不过来，有进程在排队等。
            //	•	如果 load < 4，说明 CPU 比较空闲。
            //	特点
            //	•	Load 是一个 队列长度，而不是占用率。
            //	•	可以 > 核心数，比如 8 核机器上看到 load=20，说明平均有 12 个进程在等 CPU。
            //	•	更能反映 系统整体是否过载（不仅仅是 CPU，还可能因为 I/O 卡住导致任务积压）。
            currentLoad = osBean.getSystemLoadAverage();

            /*
             * Java Doc copied from {@link OperatingSystemMXBean#getSystemCpuLoad()}:</br>
             * Returns the "recent cpu usage" for the whole system. This value is a double in the [0.0,1.0] interval.
             * A value of 0.0 means that all CPUs were idle during the recent period of time observed, while a value
             * of 1.0 means that all CPUs were actively running 100% of the time during the recent period being
             * observed. All values between 0.0 and 1.0 are possible depending of the activities going on in the
             * system. If the system recent cpu usage is not available, the method returns a negative value.
             */
            // getSystemCpuLoad() 表示 整个系统（宿主机）的 CPU 使用率，范围是 [0.0, 1.0]。
            // 缺点：在容器环境里（比如 Docker/K8s），它返回的是宿主机 CPU 的使用率，而不是容器自身的使用率，
            // 可能会导致 Sentinel 的限流判断失真。
            double systemCpuUsage = osBean.getSystemCpuLoad();

            // calculate process cpu usage to support application running in container environment
            RuntimeMXBean runtimeBean = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
            // 当前 JVM 进程总消耗 CPU 时间（纳秒） 。单位是 纳秒，累加值。
            long newProcessCpuTime = osBean.getProcessCpuTime();
            // JVM 启动到现在的存活时间（毫秒）
            long newProcessUpTime = runtimeBean.getUptime();
            int cpuCores = osBean.getAvailableProcessors();
            // 	processCpuTimeDiffInMs：在这一次采样间隔中，JVM 进程实际消耗的 CPU 时间。
            long processCpuTimeDiffInMs = TimeUnit.NANOSECONDS
                    .toMillis(newProcessCpuTime - processCpuTime);
            // 这一次采样间隔中，真实流逝的时间。
            long processUpTimeDiffInMs = newProcessUpTime - processUpTime;
            // 再计算占比
            double processCpuUsage = (double) processCpuTimeDiffInMs / processUpTimeDiffInMs / cpuCores;

            // 记录现在的值，利于下次做差分
            processCpuTime = newProcessCpuTime;
            processUpTime = newProcessUpTime;

            // 	•容器环境：宿主机可能有很多进程（别的容器也在跑），systemCpuUsage 可能很低（宿主机空闲），
            // 	    但 JVM 进程其实把容器配额内的 CPU 用爆了。这时用 processCpuUsage 更准确。
            //	•非容器环境：systemCpuUsage 能直接反映系统的真实负载情况，有时更敏感。
            //	•取 max 是为了 保守估计，避免低估 CPU 占用率导致限流失效。
            currentCpuUsage = Math.max(processCpuUsage, systemCpuUsage);

            if (currentLoad > SystemRuleManager.getSystemLoadThreshold()) {
                writeSystemStatusLog();
            }
        } catch (Throwable e) {
            RecordLog.warn("[SystemStatusListener] Failed to get system metrics from JMX", e);
        }
    }

    private void writeSystemStatusLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Load exceeds the threshold: ");
        sb.append("load:").append(String.format("%.4f", currentLoad)).append("; ");
        sb.append("cpuUsage:").append(String.format("%.4f", currentCpuUsage)).append("; ");
        sb.append("qps:").append(String.format("%.4f", Constants.ENTRY_NODE.passQps())).append("; ");
        sb.append("rt:").append(String.format("%.4f", Constants.ENTRY_NODE.avgRt())).append("; ");
        sb.append("thread:").append(Constants.ENTRY_NODE.curThreadNum()).append("; ");
        sb.append("success:").append(String.format("%.4f", Constants.ENTRY_NODE.successQps())).append("; ");
        sb.append("minRt:").append(String.format("%.2f", Constants.ENTRY_NODE.minRt())).append("; ");
        sb.append("maxSuccess:").append(String.format("%.2f", Constants.ENTRY_NODE.maxSuccessQps())).append("; ");
        RecordLog.info(sb.toString());
    }
}
