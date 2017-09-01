/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 heimuheimu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.heimuheimu.naiverpc.monitor.server.falcon;

import com.heimuheimu.naivemonitor.falcon.support.AbstractExecutionDataCollector;
import com.heimuheimu.naivemonitor.monitor.ExecutionMonitor;
import com.heimuheimu.naiverpc.monitor.FalconReporterConstant;
import com.heimuheimu.naiverpc.monitor.server.RpcServerExecutionMonitorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC 服务端使用的执行信息采集器
 *
 * @author heimuheimu
 */
public class RpcServerExecutionDataCollector extends AbstractExecutionDataCollector {

    private static final Map<Integer, String> ERROR_METRIC_SUFFIX_MAP;

    static {
        ERROR_METRIC_SUFFIX_MAP = new HashMap<>();
        ERROR_METRIC_SUFFIX_MAP.put(RpcServerExecutionMonitorFactory.ERROR_CODE_INVOCATION_ERROR, "_error");
    }

    private final String collectorName;

    private final List<ExecutionMonitor> executionMonitorList;

    /**
     * 构造一个 RPC 服务端使用的执行信息采集器，将会采集所有 RPC 服务端的执行信息信息
     */
    public RpcServerExecutionDataCollector() {
        this.collectorName = "server";
        this.executionMonitorList = null;
    }

    /**
     * 构造一个 RPC 服务端使用的执行信息采集器，仅采集指定监听端口的 RPC 服务端执行信息
     *
     * @param serverName 该监听端口对应的 RPC 服务名称，Collector 的 name 为 server_${serverName}
     * @param listenPort RPC 服务监听端口
     */
    public RpcServerExecutionDataCollector(String serverName, int listenPort) {
        this.collectorName = "server_" + serverName;
        this.executionMonitorList = new ArrayList<>();
        executionMonitorList.add(RpcServerExecutionMonitorFactory.get(listenPort));
    }

    @Override
    protected List<ExecutionMonitor> getExecutionMonitorList() {
        if (executionMonitorList != null) {
            return executionMonitorList;
        } else {
            return RpcServerExecutionMonitorFactory.getAll();
        }
    }

    @Override
    protected String getModuleName() {
        return FalconReporterConstant.MODULE_NAME;
    }

    @Override
    protected String getCollectorName() {
        return collectorName;
    }

    @Override
    protected Map<Integer, String> getErrorMetricSuffixMap() {
        return ERROR_METRIC_SUFFIX_MAP;
    }

    @Override
    public int getPeriod() {
        return FalconReporterConstant.REPORT_PERIOD;
    }
}