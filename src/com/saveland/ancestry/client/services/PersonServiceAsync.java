package com.saveland.ancestry.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.saveland.ancestry.client.models.Person;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PersonServiceAsync {
    void greetServer(String input, AsyncCallback<String> callback)
            throws IllegalArgumentException;

    void getPeople(AsyncCallback<ArrayList<Person>> asyncCallback);

    void savePerson(Person person, AsyncCallback<Person> asyncCallback);
}
