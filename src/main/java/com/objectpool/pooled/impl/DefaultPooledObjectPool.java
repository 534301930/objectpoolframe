package com.objectpool.pooled.impl;

import java.util.concurrent.TimeUnit;

import com.objectpool.core.IObjectFactory;
import com.objectpool.core.impl.base.DefaultObjectPool;
import com.objectpool.pooled.IPooledObject;
import com.objectpool.util.Utils;

/**
 * 缺省的池化对象池，实现了对象池的基本需求，用户可自行在此基础上进行扩展
 * @author Lee
 * @param <T>
 */
public class DefaultPooledObjectPool<T> extends DefaultObjectPool<IPooledObject<T>> {

	public DefaultPooledObjectPool(IObjectFactory<IPooledObject<T>> factory) {
		super(factory);
	}
	
	public DefaultPooledObjectPool(int initialSize, IObjectFactory<IPooledObject<T>> factory) {
		super(initialSize, factory);
	}
	
	public DefaultPooledObjectPool(int initialSize, IObjectFactory<IPooledObject<T>> factory, int capacity) {
		super(initialSize, factory, capacity);
	}

	
	@Override
	public void init() {
		logger.debug("initializing object to the object pool...");
		int retryTime = 0;
		// 初始化对象个数
		for (int i = 0; i < this.initialSize; i++) {
			if (retryTime == this.retryTime4makeObject) {
				throw new RuntimeException("初始化对象时在经过" + this.retryTime4makeObject + "后依然失败！");
			}
			IPooledObject<T> pooledObject = this.factory.makeObject();
			if (testOnAdd) {
				if (!this.factory.validObject(pooledObject)) {
					retryTime++;
					i--;
				}
			}
			try {
				this.pool.offer(pooledObject, waitTime4Offer, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("done.");
	}
	
	@Override
	public IPooledObject<T> borrowObject() {
		StringBuffer buffer = new StringBuffer("----------------borrow object------------------");
		IPooledObject<T> pooledObject = null;
		try {
			pooledObject = this.pool.poll(waitTime4Poll, TimeUnit.SECONDS);
			// 若池中没有对象则利用工厂创建对象
			if (pooledObject == null) {
				buffer.append(Utils.lineSeparator + "Could't get the object from the pool,so we ask the factory to make the object...");
				pooledObject = this.factory.makeObject();
				// 拿到对象后，根据属性选择是否要检测对象可用
				if (testOnBorrow) {
					/* 若对象不可用，则利用工厂创建对象，直到对象可用为止，若设置了重试次数，
					 * 则在重试次数之后对象仍不可用，则抛出异常
					 */
					buffer.append(Utils.lineSeparator + "We are checking the object if it's valid...");
					int index = 0;
					while (!factory.validObject(pooledObject)) {
						pooledObject = factory.makeObject();
						if (this.retryTime4makeObject != 0 && (index++) <= this.retryTime4makeObject) {
							continue;
						} else {
							throw new RuntimeException("在尝试" + this.retryTime4makeObject + "次创建对象后仍然失败!请检查相关配置");
						}
					}
				}
			} else {// 若池中有对象则继续
				buffer.append(Utils.lineSeparator + "We get the object from the pool.");
				if (testOnBorrow) {
					buffer.append(Utils.lineSeparator + "Checking object.");
					if (!this.factory.validObject(pooledObject)) {
						buffer.append(Utils.lineSeparator + "It's not valid,so we ask ourselves.");
						return borrowObject();
					} 
				}
			}
			if (testOnBorrow) {
				buffer.append(Utils.lineSeparator + "Ok!Return the object.");
			} else {
				buffer.append(Utils.lineSeparator + "We just return the object without checking.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 触发池对象的从池中取出事件 
		if (pooledObject != null) {
			pooledObject.onBorrow();
		}
		logger.debug(buffer.toString());
		return pooledObject;
	}
	
	@Override
	public void returnObject(IPooledObject<T> pooledObject) {
		// 若设置检测，检测通过则允许归还对象；检测不通过，则直接丢弃
		boolean canReturn = true;
		StringBuffer buffer = new StringBuffer("--------------return object--------------");
		if (this.testOnReturn) {
			canReturn = this.factory.validObject(pooledObject);
			buffer.append(Utils.lineSeparator + "This object " + (canReturn ? "can" : "can't") + " be returned to the pool.");
		}
		if (canReturn) {
			try {
				boolean offer = this.pool.offer(pooledObject, waitTime4Offer, TimeUnit.SECONDS);
				pooledObject.onReturn();
				buffer.append(Utils.lineSeparator + "This object " + (offer ? "has" : "hasn't") + " returned to the pool" + (offer ? "." : ",because the pool is fulled."));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			this.factory.destroyObject(pooledObject);
		}
		buffer.append(Utils.lineSeparator + "----------------------------");
		logger.debug(buffer.toString());
	}
}
