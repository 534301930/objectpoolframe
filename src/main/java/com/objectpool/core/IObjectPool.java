package com.objectpool.core;

import java.util.concurrent.BlockingQueue;

/**
 * 对象池的定义
 * @author Lee
 * @param <T>
 */
public interface IObjectPool<T> {

	/**
	 * 从池中拿出对象
	 * 获取失败时，返回null
	 * @return
	 */
	public T borrowObject();
	
	/**
	 * 归还对象到池中
	 * @param t
	 * @return 
	 */
	public void returnObject(T t);
	
	/**
	 * 获取队列
	 * @return
	 */
	public BlockingQueue<T> getPool();
	
	/**
	 * 获取对象工厂
	 * @return
	 */
	public IObjectFactory<T> getFactory();
	
	/**
	 * 初始化对象池
	 */
	public void init();
	
	/**
	 * 开启对象池
	 */
	public void start();
}
