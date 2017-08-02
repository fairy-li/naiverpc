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
package com.heimuheimu.naiverpc.server.executors;

import com.heimuheimu.naiverpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * RPC 服务画像
 *
 * @author heimuheimu
 */
public class RpcServiceDepiction {

	private static final Logger LOG = LoggerFactory.getLogger(RpcServiceDepiction.class);

	/**
	 * 提供服务的实例
	 */
	private final Object target;

	/**
	 * 可提供服务的接口列表
	 */
	private final Class<?>[] interfaces;

	/**
	 * RPC 可提供的方法 Map, Key 为方法唯一标识，Value 为方法
	 * @see ReflectUtil#getMethodUniqueName(Method)
	 */
	private final HashMap<String, Method> methodMap;

	/**
	 * 根据提供服务的实例创建 RPC 服务画像
	 *
	 * @param target 提供服务的实例
	 * @throws IllegalArgumentException 如果提供服务的实例没有实现任何接口，将会抛出此异常
	 */
	public RpcServiceDepiction(Object target) throws IllegalArgumentException {
		if (target == null) {
			throw new IllegalArgumentException("Object could not be null.");
		}
		this.target = target;
		this.interfaces = getAllInterfaces(target.getClass()).toArray(new Class<?>[]{});
		if (this.interfaces.length == 0) {
			throw new IllegalArgumentException("This object represents a class that implements no interfaces: "
					+ target);
		}
		Method[] methods = target.getClass().getMethods();
		methodMap = new HashMap<>();
		for (Method method : methods) {
			String key = ReflectUtil.getMethodUniqueName(method);
			if (methodMap.containsKey(key)) {
				LOG.error("Method `{}` is existed. It will be overridden. Target object: `{}`.",
						method.getName(), Arrays.toString(method.getParameterTypes()), target);
			}
			methodMap.put(key, method);
		}
	}

	/**
	 * 获得可提供服务的接口列表
	 *
	 * @return 可提供服务的接口列表
	 */
	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	/**
	 * 根据方法名、方法参数类型数组、参数数组反射执行对应的方法，并返回结果
	 *
	 * @param methodUniqueName 唯一方法名
	 * @param arguments 参数数组
	 * @return 方法返回结果
	 * @throws NullPointerException 如果方法不存在，将会抛出此异常
	 * @throws Exception 如果方法执行过程中遇到错误，将会抛出此异常
	 */
	public Object execute(String methodUniqueName, Object[] arguments)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method m = methodMap.get(methodUniqueName);
		if (m != null) {
			return m.invoke(target, arguments);
		} else {
			throw new NoSuchMethodException("No such method. Class: `"
					+ target.getClass().getName() + "`. Method unique name: `" + methodUniqueName
					+ "`. Arguments: `" + Arrays.toString(arguments) + "`.");
		}
	}

	/**
	 * 获得对象 Class 实现的所有接口数组，包含被继承的父接口
	 *
	 * @param clazz 需要查询的对象 Class
	 * @return 对象实现的所有接口数组，包含被继承的父接口
	 */
	private Set<Class<?>> getAllInterfaces(Class<?> clazz) {
		Set<Class<?>> allInterfaceSet = new LinkedHashSet<>();
		while (clazz != null) {
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces.length > 0) {
				allInterfaceSet.addAll(Arrays.asList(interfaces));
				for (Class<?> i : interfaces) {
					allInterfaceSet.addAll(getAllInterfaces(i));
				}
			}
			clazz = clazz.getSuperclass();
		}
		return allInterfaceSet;
	}

}
