package com.saveland.ancestry.server;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.saveland.ancestry.client.services.PersonService;
import com.saveland.ancestry.client.models.Person;
import com.saveland.ancestry.server.models.PersonEntity;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PersonServiceImpl extends RemoteServiceServlet implements
        PersonService {

    private DatastoreService datastore = DatastoreServiceFactory
            .getDatastoreService();

    @Override
    public String greetServer(String input) throws IllegalArgumentException {
        return input;
    }

    @Override
    public ArrayList<Person> getPeople() {
        ArrayList<Person> people = new ArrayList<Person>();
        Query q = new Query("Person");
        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = datastore.prepare(q);
        for (Entity e : pq.asIterable()) {
            people.add(PersonEntity.createPersonFromEntity(e));
        }
        return people;
    }

    @Override
    public Person savePerson(Person person) {
        try {
            log("PERSONENTITY.savePerson");
            return PersonEntity.savePerson(person);
        } catch (EntityNotFoundException e) {
            log("ENTITY NOT FOUND");
            // TODO - how should we handle this?
            // The client interface can not throw a datastore exception .. hrm
            // ...
            e.printStackTrace();
            return person;
        }
    }

}
