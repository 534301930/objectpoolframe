package com.objectpool.core.impl.base;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectpool.core.IObjectFactory;
import com.objectpool.core.IObjectPool;
import com.objectpool.thread.CheckThread;
import com.objectpool.util.Utils;

/**
 * 缺省的对象池，实现了对象池的基本需求，用户可自行在此基础上进行扩展
 * @author Lee
 * @param <T>
 */
public class DefaultObjectPool<T> implements IObjectPool<T> {
	
	protected Logger logger = LoggerFactory.getLogger(DefaultObjectPool.class);
	
	/** 内置对象池结构 */
	protected BlockingQueue<T> pool;
	
	/** 对象工厂 */
	protected IObjectFactory<T> factory;
	
	/** 从池中拿出对象时是否检测对象可用 */
	protected boolean testOnBorrow = Boolean.FALSE;
	
	/** 在归还对象到池中时是否检测对象可用 */
	protected boolean testOnReturn = Boolean.FALSE;
	
	/** 将通过工厂创建的对象放入池中时是否验证可用 */
	protected boolean testOnAdd = Boolean.FALSE;
	
	/** 使用工厂创建对象失败后的重试次数 */
	protected int retryTime4makeObject = 3;
	
	/** 向池中加入对象所需时间，即在多长时间内完成该操作 */
	protected int waitTime4Offer = 5;
	
	/** 从池中取出对象所需时间，即在多长时间内完成该操作 */
	protected int waitTime4Poll = 3;
	
	/** 初始化对象个数 */
	protected int initialSize = 0;
	
	/** 每隔多少秒对对象池中对象做一次检查 */
	protected int checkStatusPerTime = 60;
	
	/** 对象池监控线程 */
	protected Runnable checkThread;

	public DefaultObjectPool(IObjectFactory<T> factory) {
		this.pool = new LinkedBlockingDeque<>();
		this.factory = factory;
		applySettings();
		init();
	}
	
	/**
	 * @param initialSize 初始化数量
	 */
	public DefaultObjectPool(int initialSize, IObjectFactory<T> factory) {
		this.pool = new LinkedBlockingDeque<>();
		this.factory = factory;
		this.initialSize = initialSize;
		applySettings();
		init();
	}
	
	/**
	 * @param initialSize 初始化数量
	 * @param factory 对象工厂
	 * @param capacity 对象池容量
	 */
	public DefaultObjectPool(int initialSize, IObjectFactory<T> factory, int capacity) {
		this.pool = new LinkedBlockingQueue<>(capacity);
		this.factory = factory;
		this.initialSize = initialSize;
		applySettings();
		init();
	}

	/**
	 * 加载配置
	 */
	protected void applySettings() {
		
	}

	/**
	 * 对象池初始化
	 */
	public void init() {
		logger.debug("initializing object to the object pool...");
		int size = this.pool.size();
		if (size != 0 && size < this.initialSize) {
			// 自定义的初始容量超过了上限
//			throw new RuntimeException("自定义的初始容量超过了上限：initialSize=" + initialSize + ", poolSize=" + size);
			throw new RuntimeException("The initial size is great than the object pool size! initialSize=" + initialSize + ", poolSize=" + size);
		}
		int retryTime = 0;
		// 初始化对象个数
		for (int i = 0; i < this.initialSize; i++) {
			if (retryTime == this.retryTime4makeObject) {
//				throw new RuntimeException("初始化对象时在经过" + this.retryTime4makeObject + "后依然失败！");
				throw new RuntimeException("The error still exists after " + this.retryTime4makeObject + " times when initializing object!");
			}
			T t = this.factory.makeObject();
			if (testOnAdd) {
				if (!this.factory.validObject(t)) {
					retryTime++;
					i--;
				}
			}
			try {
				this.pool.offer(t, waitTime4Offer, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("done.");
	}

	/**
	 * 
	 */
	@Override
	public T borrowObject() {
		StringBuffer buffer = new StringBuffer("--------------borrow object---------------");
		T t = null;
		try {
			t = pool.poll(waitTime4Poll, TimeUnit.SECONDS);
			// 若池中没有对象则利用工厂创建对象
			if (t == null) {
				buffer.append(Utils.lineSeparator + "Could't get the object from the pool,so we ask the factory to make the object...");
				t = factory.makeObject();
				// 拿到对象后，根据属性选择是否要检测对象可用
				if (testOnBorrow) {
					buffer.append(Utils.lineSeparator + "We are checking the object if it's valid...");
					/* 若对象不可用，则利用工厂创建对象，直到对象可用为止，若设置了重试次数，
					 * 则在重试次数之后对象仍不可用，则抛出异常
					 */
					int index = 0;
					while (!factory.validObject(t)) {
						t = factory.makeObject();
						if (this.retryTime4makeObject != 0 && (index++) <= this.retryTime4makeObject) {
							continue;
						} else {
							throw new RuntimeException("The error exists after " + this.retryTime4makeObject + "time(s) when initializing object!");
						}
					}
				}
			} else {// 若池中有对象则继续
				buffer.append(Utils.lineSeparator + "We get the object from the pool.");
				if (testOnBorrow) {
					buffer.append(Utils.lineSeparator + "Checking object.");
					if (!factory.validObject(t)) {
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
		buffer.append(Utils.lineSeparator + "-----------------------------");
		logger.debug(buffer.toString());
		return t;
	}
	
	@Override
	public void returnObject(T t) {
		// 若设置检测，检测通过则允许归还对象；检测不通过，则直接丢弃
		boolean canReturn = true;
		StringBuffer buffer = new StringBuffer("--------------return object--------------");
		if (this.testOnReturn) {
			canReturn = this.factory.validObject(t);
			buffer.append(Utils.lineSeparator + "This object " + (canReturn ? "can" : "can't") + " be returned to the pool.");
		}
		if (canReturn) {
			try {
				boolean offer = this.pool.offer(t, waitTime4Offer, TimeUnit.SECONDS);
				buffer.append(Utils.lineSeparator + "This object " + (offer ? "has" : "hasn't") + " returned to the pool" + (offer ? "." : ",because the pool is fulled."));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			this.factory.destroyObject(t);
		}
		buffer.append(Utils.lineSeparator + "----------------------------");
		logger.debug(buffer.toString());
	}

	@Override
	public void start() {
		// 定时监控任务
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (checkThread == null) {
					logger.debug("object pool frame: use default class to monitor object pool!");
					checkThread = new CheckThread<>(DefaultObjectPool.this);
				}
				Thread thread = new Thread(checkThread);
				thread.start();
			}
		}, 1000, this.checkStatusPerTime * 1000);
		logger.debug("The object pool start successful!");
	}
	
	public BlockingQueue<T> getPool() {
		return pool;
	}
	
	public IObjectFactory<T> getFactory() {
		return factory;
	}
	
	public void setTestOnAdd(boolean testOnAdd) {
		this.testOnAdd = testOnAdd;
	}
	
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}
	
	public void setCheckPerTime(int checkPerTime) {
		this.checkStatusPerTime = checkPerTime;
	}
	
	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}
	
	public void setRetryTime4makeObject(int retryTime4makeObject) {
		this.retryTime4makeObject = retryTime4makeObject;
	}
	
	public void setWaitTime4Offer(int waitTime4Offer) {
		this.waitTime4Offer = waitTime4Offer;
	}
	
	public void setWaitTime4Poll(int waitTime4Poll) {
		this.waitTime4Poll = waitTime4Poll;
	}
	
	public void setCheckThread(Runnable checkThread) {
		this.checkThread = checkThread;
	}
}