package com.objectpool.pooled.impl;

import java.util.UUID;

import com.objectpool.pooled.IPooledObject;

/**
 * 默认的对象池的实现
 * @author Lee
 * @param <T>
 */
public class DefaultPooledObject<T> implements IPooledObject<T> {

	protected String id;// 对象id
	
	protected long createTime;// 创建时间
	protected long lastBorrowTime;// 上次从池中取出时间 
	protected long lastReturnTime;// 上次回到池中时间
	protected T object;// 原始对象
	
	public DefaultPooledObject(T t) {
		id = UUID.randomUUID().toString();
		this.object = t;
	}
	
	public long getCreateTime() {
		return createTime;
	}
	
	public long getLastBorrowTime() {
		return lastBorrowTime;
	}
	
	public long getLastReturnTime() {
		return lastReturnTime;
	}

	public T getObject() {
		return object;
	}

	@Override
	public void onReturn() {
		this.lastBorrowTime = System.currentTimeMillis();
	}
	
	@Override
	public void onBorrow() {
		this.lastBorrowTime = System.currentTimeMillis();
	}
	
	@Override
	public void init() {
		long currentTimeMillis = System.currentTimeMillis();
		this.createTime = currentTimeMillis;
	}
}
