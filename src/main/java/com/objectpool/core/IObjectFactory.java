package com.objectpool.core;

/**
 * 对象工厂的接口，定义了对对象的行为
 * @author Lee
 * @param <T>
 */
public interface IObjectFactory<T> {

	/**
	 * 创建对象
	 * @return
	 */
	public T makeObject();
	
	/**
	 * 销毁对象
	 * @param t
	 */
	public void destroyObject(T t);
	
	/**
	 * 验证对象可用性
	 * @param t
	 */
	public boolean validObject(T t);
	
	/**
	 * 获取对象的状态信息
	 * @param t
	 * @return
	 */
	public String getStatus(T t);
}
