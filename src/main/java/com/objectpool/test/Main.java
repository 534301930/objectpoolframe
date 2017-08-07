package com.objectpool.test;

import com.objectpool.core.impl.DefaultObjectPool;
import com.objectpool.pooled.IPooledObject;
import com.objectpool.pooled.impl.DefaultPooledObjectFactory;
import com.objectpool.pooled.impl.DefaultPooledObjectPool;

public class Main {

	public static void main(String[] args) {
		// 池化对象池的使用
		usePooledObjectPool();
		// 普通对象池的使用
//		useObjectPool();
	}

	@SuppressWarnings("unused")
	private static void useObjectPool() {
		DefaultObjectPool<Person> pool = new DefaultObjectPool<>(1, new MyObjectFactory(), 1);
		pool.setCheckPerTime(10);
		pool.start();
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Person person = pool.borrowObject();
			System.out.println(person);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pool.returnObject(person);
		}
	}

	private static void usePooledObjectPool() {
		DefaultPooledObjectFactory<Person> factory = new DefaultPooledObjectFactory<>(new MyObjectFactory());
		DefaultPooledObjectPool<Person> objectPool = new DefaultPooledObjectPool<>(1, factory, 1);
		objectPool.setCheckPerTime(10);
		objectPool.start();
		while (true) {
			IPooledObject<Person> pooledObject = objectPool.borrowObject();
			Person person = pooledObject.getObject();
			System.out.println(person);
			objectPool.returnObject(pooledObject);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}