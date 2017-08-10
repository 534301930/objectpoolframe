package com.objectpool.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.objectpool.core.impl.base.DefaultObjectPool;
import com.objectpool.pooled.IPooledObject;
import com.objectpool.pooled.impl.DefaultPooledObjectFactory;
import com.objectpool.pooled.impl.DefaultPooledObjectPool;

public class Main {

	public static void main(String[] args) {
		// 池化对象池的使用
//		usePooledObjectPool();
		// 普通对象池的使用
//		useObjectPool();
		// 使用spring
		useSpring();
	}

	private static void useSpring() {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//		@SuppressWarnings("unchecked")
//		DefaultObjectPool<Person> pool = context.getBean(DefaultObjectPool.class);
		@SuppressWarnings("unchecked")
		DefaultObjectPool<Person> pool = (DefaultObjectPool<Person>) context.getBean("pool");
		// 若配置bean时没有声明init-method，则需要手动调用pool.start()
		System.out.println(pool);
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

	@SuppressWarnings("unused")
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