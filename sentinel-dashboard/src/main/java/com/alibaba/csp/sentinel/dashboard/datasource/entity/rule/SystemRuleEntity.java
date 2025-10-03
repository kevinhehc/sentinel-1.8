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
package com.alibaba.csp.sentinel.dashboard.datasource.entity.rule;

import com.alibaba.csp.sentinel.slots.system.SystemRule;

import java.util.Date;

/**
 * SystemRuleEntity 表示系统规则的实体类，
 * 用于存储和管理系统保护规则的配置信息。
 * 实现了 RuleEntity 接口。
 */
public class SystemRuleEntity implements RuleEntity {

    /** 规则的唯一标识符 */
    private Long id;

    /** 应用名称 */
    private String app;

    /** 应用运行所在的机器 IP 地址 */
    private String ip;

    /** 应用运行所在的机器端口号 */
    private Integer port;

    /**
     * 系统的最大负载阈值（如 CPU load 或系统平均负载）。
     * 超过该阈值时可能会触发系统保护。
     */
    private Double highestSystemLoad;

    /**
     * 系统允许的最大平均响应时间（毫秒）。
     * 如果请求平均响应时间超过该值，则可能触发限流或降级。
     */
    private Long avgRt;

    /**
     * 系统允许的最大并发线程数。
     * 超过该值时新请求可能会被拒绝。
     */
    private Long maxThread;

    /**
     * 系统允许的最大每秒查询数（QPS）。
     * 用于控制系统的流量峰值。
     */
    private Double qps;

    /**
     * 系统允许的最大 CPU 使用率。
     * 范围通常是 0.0 - 1.0（如 0.8 表示 80%）。
     */
    private Double highestCpuUsage;

    /** 规则创建时间 */
    private Date gmtCreate;

    /** 规则最近修改时间 */
    private Date gmtModified;

    public static SystemRuleEntity fromSystemRule(String app, String ip, Integer port, SystemRule rule) {
        SystemRuleEntity entity = new SystemRuleEntity();
        entity.setApp(app);
        entity.setIp(ip);
        entity.setPort(port);
        entity.setHighestSystemLoad(rule.getHighestSystemLoad());
        entity.setHighestCpuUsage(rule.getHighestCpuUsage());
        entity.setAvgRt(rule.getAvgRt());
        entity.setMaxThread(rule.getMaxThread());
        entity.setQps(rule.getQps());
        return entity;
    }

    @Override
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Double getHighestSystemLoad() {
        return highestSystemLoad;
    }

    public void setHighestSystemLoad(Double highestSystemLoad) {
        this.highestSystemLoad = highestSystemLoad;
    }

    public Long getAvgRt() {
        return avgRt;
    }

    public void setAvgRt(Long avgRt) {
        this.avgRt = avgRt;
    }

    public Long getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(Long maxThread) {
        this.maxThread = maxThread;
    }

    public Double getQps() {
        return qps;
    }

    public void setQps(Double qps) {
        this.qps = qps;
    }

    public Double getHighestCpuUsage() {
        return highestCpuUsage;
    }

    public void setHighestCpuUsage(Double highestCpuUsage) {
        this.highestCpuUsage = highestCpuUsage;
    }

    @Override
    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public SystemRule toRule() {
        SystemRule rule = new SystemRule();
        rule.setHighestSystemLoad(highestSystemLoad);
        rule.setAvgRt(avgRt);
        rule.setMaxThread(maxThread);
        rule.setQps(qps);
        rule.setHighestCpuUsage(highestCpuUsage);
        return rule;
    }
}
