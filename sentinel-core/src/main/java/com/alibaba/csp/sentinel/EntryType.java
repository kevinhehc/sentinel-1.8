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
package com.alibaba.csp.sentinel;

/**
 * An enum marks resource invocation direction.
 *
 * @author jialiang.linjl
 * @author Yanming Zhou
 */
public enum EntryType {
    /**
     * Inbound traffic
     * 表示“入口流量”或“入站调用”。
     * 一般是指外部请求进入本服务（比如 API 接口、RPC 服务端接收的请求）。
     * Sentinel 在处理入口流量时会进行系统保护（SystemRule）、流量控制等，防止整体系统过载。
     */
    IN,
    /**
     * Outbound traffic
     * 表示“出口流量”或“出站调用”。
     * 指本服务调用外部依赖（如访问数据库、调用下游服务的 RPC/HTTP）。
     * Sentinel 在处理出口流量时，更多是做依赖保护（例如给下游调用加限流、熔断降级），避免因为下游慢或不可用导致本地雪崩。
     */
    OUT;

}
