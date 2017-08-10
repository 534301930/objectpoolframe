package com.objectpool.test;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.objectpool.core.impl.base.DefaultObjectFactory;

@Component
public class MyObjectFactory extends DefaultObjectFactory<Person> {

	@Override
	public Person makeObject() {
		Person person = new Person();
		String name = UUID.randomUUID().toString();
		person.setName(name);
		return person;
	}

	@Override
	public void destroyObject(Person t) {
	}

	@Override
	public boolean validObject(Person t) {
		return true;
	}

}
