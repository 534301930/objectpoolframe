package com.objectpool.pooled.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.objectpool.core.impl.base.DefaultObjectFactory;
import com.objectpool.pooled.IPooledObject;
import com.objectpool.pooled.IPooledObjectFactory;

/**
 * 缺省的池化对象工厂
 * @author Lee
 * @param <T>
 */
public class DefaultPooledObjectFactory<T> implements IPooledObjectFactory<IPooledObject<T>> {

	private DefaultObjectFactory<T> factory;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	public DefaultPooledObjectFactory(DefaultObjectFactory<T> factory) {
		this.factory = factory;
	}
	
	@Override
	public boolean validObject(IPooledObject<T> pooledObject) {
		T t = pooledObject.getObject();
		return this.factory.validObject(t);
	}

	@Override
	public IPooledObject<T> makeObject() {
		T t = this.factory.makeObject();
		DefaultPooledObject<T> pooledObject = new DefaultPooledObject<T>(t);
		pooledObject.init();
		return pooledObject;
	}

	@Override
	public void destroyObject(IPooledObject<T> pooledObject) {
		T t = pooledObject.getObject();
		this.factory.destroyObject(t);
	}

	@Override
	public String getStatus(IPooledObject<T> pooledObject) {
		long createTime = pooledObject.getCreateTime();
		long lastBorrowTime = pooledObject.getLastBorrowTime();
		long lastReturnTime = pooledObject.getLastReturnTime();
		boolean valid = validObject(pooledObject);
		T t = pooledObject.getObject();
		String objectId = Integer.toHexString(t.hashCode());
		return "{object : " + t.getClass().getName() + "@" + objectId + ", createTime : "
				+ getTime(createTime) + ", lastBorrowTime : "
				+ getTime(lastBorrowTime) + ", lastReturnTime : "
				+ getTime(lastReturnTime) + ", valid : " + valid + "}";
	}

	private String getTime(long time) {
		return time == 0 ? "null" : sdf.format(new Date(time));
	}
}
