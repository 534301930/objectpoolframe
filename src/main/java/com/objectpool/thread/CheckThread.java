package com.objectpool.thread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectpool.core.IObjectFactory;
import com.objectpool.core.IObjectPool;
import com.objectpool.util.Utils;

/**
 * 根据给定的对象池，每隔一段时间触发此线程，对对象池中的对象进行状态监测
 * 1.对象池的状态监测
 * 2.对象池中的对象的状态监测
 * @author Lee
 * @param <T>
 */
public class CheckThread<T> implements Runnable {

	/**
	 * 给定的对象池
	 */
	protected IObjectPool<T> objectPool;
	protected IObjectFactory<T> factory;
	protected Logger logger = LoggerFactory.getLogger(CheckThread.class);
	
	public CheckThread(IObjectPool<T> objectPool) {
		this.objectPool = objectPool;
		this.factory = this.objectPool.getFactory();
	}
	
	@Override
	public void run() {
		BlockingQueue<T> pool = objectPool.getPool();
		StringBuffer buffer = new StringBuffer();
		buffer.append("-------------check object pool--------------");
		buffer.append(Utils.lineSeparator + "object pool info[");
		boolean empty = pool.isEmpty();
		if (empty) {
			buffer.append("null");
		} else {
			for (T t : pool) {
				String status = factory.getStatus(t);
				buffer.append(status);
				buffer.append(", ");
			}
			int length = buffer.length();
			buffer.delete(length - 2, length);
		}
		buffer.append("]");
		buffer.append(Utils.lineSeparator + "object items=" + pool.size());
		buffer.append(Utils.lineSeparator + "---------------------------");
		infoLog(buffer);
		if (empty) {
			this.objectPool.init();
		}
	}

	/**
	 * 日志输出方式，默认打印于控制台
	 * @param buffer 
	 */
	protected void infoLog(StringBuffer buffer) {
		logger.debug(buffer.toString());
	}

}
