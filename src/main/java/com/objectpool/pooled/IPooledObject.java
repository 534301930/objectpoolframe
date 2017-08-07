package com.objectpool.pooled;

/**
 * 池化对象，对原始对象进行包装，主要存储了对象在池中所需的一些状态信息
 * @author Lee
 * @param <T>
 */
public interface IPooledObject<T> {

	long getCreateTime();
	
	long getLastBorrowTime();
	
	long getLastReturnTime();
	
	/**
	 * 池对象初始化
	 */
	void init();
	
	T getObject();

	/**
	 * 对象从池中被借出时触发此事件
	 */
	void onBorrow();

	/**
	 * 对象归还到池中时触发此事件
	 */
	void onReturn();
}
