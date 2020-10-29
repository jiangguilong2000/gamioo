/*
 * Copyright 2015-2020 Gamioo Authors.
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
package io.gamioo.ioc.wrapper;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Method;

/**
 * 方法的包装类
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

public class MethodWrapper {
	private MethodAccess access;
	private int index;// 方法索引
	private Object instance;
	private Class<?> paramClazz;// 参数类型
	private String name;


	public MethodWrapper( Object instance,Method method,MethodAccess access,int index) {
		this.name = method.getName();
		this.access = access;
		this.index = index;
		this.instance = instance;
		this.paramClazz = method.getParameterTypes()[0];
	}

	public MethodWrapper(Method method, Object instance) {
		this.name = method.getName();
		this.access = MethodAccess.get(instance.getClass());
		this.index = access.getIndex(method.getName(), method.getParameterTypes());
		this.instance = instance;
		this.paramClazz = method.getParameterTypes()[0];
	}

	public Object invoke(Object... args) {
		Object ret = null;
		if (args == null) {
			// 无参数调用
			ret = access.invoke(instance, index);
		} else {
			// 有参数调用
			ret = access.invoke(instance, index, args);
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getParamClazz() {
		return paramClazz;
	}

	public void setParamClazz(Class<?> paramClazz) {
		this.paramClazz = paramClazz;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
