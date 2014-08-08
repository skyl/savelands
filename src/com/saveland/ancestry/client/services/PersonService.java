package com.saveland.ancestry.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.saveland.ancestry.client.models.Person;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("person")
public interface PersonService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;

	ArrayList<Person> getPeople();

	Person savePerson(Person person);
}
