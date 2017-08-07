package com.objectpool.pooled;

import com.objectpool.core.IObjectFactory;

/**
 * 池化对象工厂，行为与IObjectFactory定义行为相似，区别在于操作对象是包装了普通对象的池化对象
 * @author Lee
 * @param <T>
 */
public interface IPooledObjectFactory<T> extends IObjectFactory<T> {

	@Override
	public T makeObject();
	
	@Override
	public void destroyObject(T t);
	
	@Override
	public boolean validObject(T t);
}
