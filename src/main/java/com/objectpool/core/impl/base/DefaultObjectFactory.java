package com.objectpool.core.impl.base;

import com.objectpool.core.IObjectFactory;

/**
 * 简单的原始对象的工厂，由用户自行定义和扩展对象操作
 * @author Lee
 * @param <T>
 */
public abstract class DefaultObjectFactory<T> implements IObjectFactory<T> {

	@Override
	public abstract T makeObject();
	
	@Override
	public abstract void destroyObject(T t);
	
	@Override
	public abstract boolean validObject(T t);
	
	@Override
	public String getStatus(T t) {
		boolean valid = validObject(t);
		int hashCode = t.hashCode();
		String objectId = Integer.toHexString(hashCode);
		return "{objectId : " + objectId + ", valid : " + valid + "}";
	}
}
