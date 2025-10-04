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
package com.alibaba.csp.sentinel.slots.block;

import com.alibaba.csp.sentinel.node.IntervalProperty;

/**
 * @author youji.zj
 * @author jialiang.linjl
 */
public final class RuleConstant {

    public static final int FLOW_GRADE_THREAD = 0;
    public static final int FLOW_GRADE_QPS = 1;

    public static final int DEGRADE_GRADE_RT = 0;
    /**
     * Degrade by biz exception ratio in the current {@link IntervalProperty#INTERVAL} second(s).
     */
    public static final int DEGRADE_GRADE_EXCEPTION_RATIO = 1;
    /**
     * Degrade by biz exception count in the last 60 seconds.
     */
    public static final int DEGRADE_GRADE_EXCEPTION_COUNT = 2;

    public static final int DEGRADE_DEFAULT_SLOW_REQUEST_AMOUNT = 5;
    public static final int DEGRADE_DEFAULT_MIN_REQUEST_AMOUNT = 5;

    public static final int AUTHORITY_WHITE = 0;
    public static final int AUTHORITY_BLACK = 1;

    // 直接针对资源本身。 对某个资源（比如接口 A）单独做限流，不管是谁调用。
    public static final int STRATEGY_DIRECT = 0;
    // 根据关联资源。 对一个资源 A 的流量控制，会参考另一个资源 B 的情况。比如，当 B 超过阈值时，对 A 也限流。
    public static final int STRATEGY_RELATE = 1;
    // 根据调用链入口
    // 只在指定的调用链入口下，对目标资源做流控。
    // 换句话说，只有当资源是从某个入口资源调用进来的时候，才会触发限流。
    // 举个例子：
    //	•	系统里有两个入口：入口1 -> ServiceA，入口2 -> ServiceA。
    //	•	如果你给 ServiceA 设置链路模式，并指定入口是 入口1：
    //	        • 当 入口1 调用 ServiceA 时，限流规则会生效。
    //	        • 当 入口2 调用 ServiceA 时，不受这个规则约束。
    public static final int STRATEGY_CHAIN = 2;

    // 快速失败
    public static final int CONTROL_BEHAVIOR_DEFAULT = 0;
    // 预热
    public static final int CONTROL_BEHAVIOR_WARM_UP = 1;
    // 排队等待
    public static final int CONTROL_BEHAVIOR_RATE_LIMITER = 2;
    public static final int CONTROL_BEHAVIOR_WARM_UP_RATE_LIMITER = 3;

    public static final int DEFAULT_BLOCK_STRATEGY = 0;
    public static final int TRY_AGAIN_BLOCK_STRATEGY = 1;
    public static final int TRY_UNTIL_SUCCESS_BLOCK_STRATEGY = 2;

    public static final int DEFAULT_RESOURCE_TIMEOUT_STRATEGY = 0;
    public static final int RELEASE_RESOURCE_TIMEOUT_STRATEGY = 1;
    public static final int KEEP_RESOURCE_TIMEOUT_STRATEGY = 2;

    public static final String LIMIT_APP_DEFAULT = "default";
    public static final String LIMIT_APP_OTHER = "other";

    public static final int DEFAULT_SAMPLE_COUNT = 2;
    public static final int DEFAULT_WINDOW_INTERVAL_MS = 1000;

    private RuleConstant() {}
}
