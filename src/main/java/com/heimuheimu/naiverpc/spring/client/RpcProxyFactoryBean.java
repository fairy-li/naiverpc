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

package com.heimuheimu.naiverpc.spring.client;

import com.heimuheimu.naiverpc.client.RpcClient;
import com.heimuheimu.naiverpc.client.RpcProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * RPC 远程调用代理实例生成 Spring 工厂。
 *
 * @author heimuheimu
 */
public class RpcProxyFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> clz;

    private final T target;

    public RpcProxyFactoryBean(Class<T> clz, RpcClient rpcClient) {
        this.clz = clz;
        this.target = RpcProxyFactory.build(clz, rpcClient);
    }

    @Override
    public T getObject() throws Exception {
        return target;
    }

    @Override
    public Class<?> getObjectType() {
        return clz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
