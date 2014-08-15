package com.saveland.ancestry.server.models;

import java.util.Date;
import java.util.Map;
//import java.util.logging.Logger;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.KeyFactory;
import com.saveland.ancestry.client.models.Person;

public class PersonEntity {

    private final static Logger logger = Logger.getLogger(PersonEntity.class
            .getName());
    private static final DatastoreService datastore = DatastoreServiceFactory
            .getDatastoreService();

    /*
     * Create a new person or update and existing person
     */
    public static Person savePerson(Person person)
            throws EntityNotFoundException {
        logger.severe("OK SERVERSIDE savePerson");
        System.out.println("OK HOW ABOUT HIS?");
        Entity e;
        if (person.key != null) {
            e = datastore.get(KeyFactory.stringToKey(person.key));
        } else {
            e = new Entity("Person");
        }
        e.setProperty("firstName", person.firstName);
        e.setProperty("lastName", person.lastName);
        e.setProperty("gender", person.gender);
        e.setProperty("birthDate", person.birthDate);
        if (person.fatherKey != null) {
            e.setProperty("fatherKey", KeyFactory.stringToKey(person.fatherKey));
        }
        if (person.motherKey != null) {
            e.setProperty("motherKey", KeyFactory.stringToKey(person.motherKey));
        }
        GeoPt geopt = new GeoPt(person.lat, person.lon);
        e.setProperty("geopt", geopt);
        e.setProperty("notes", person.notes);
        Key key = datastore.put(e);
        person.key = KeyFactory.keyToString(key);
        // logger.severe(key.toString());
        return person;
    }

    public static Person createPersonFromEntity(Entity e) {
        Person p = new Person();
        Map<String, Object> properties = e.getProperties();
        p.key = KeyFactory.keyToString(e.getKey());
        p.firstName = (String) properties.get("firstName");
        p.lastName = (String) properties.get("lastName");
        p.gender = (Boolean) properties.get("gender");
        p.birthDate = (Date) properties.get("birthDate");
        if (properties.get("fatherKey") != null) {
            p.fatherKey = KeyFactory.keyToString((Key) properties
                    .get("fatherKey"));
        }
        if (properties.get("motherKey") != null) {
            p.motherKey = KeyFactory.keyToString((Key) properties
                    .get("motherKey"));
        }
        GeoPt geopt = (GeoPt) properties.get("geopt");
        p.lat = geopt.getLatitude();
        p.lon = geopt.getLongitude();
        p.notes = (String) properties.get("notes");
        return p;
    }

    public static Person updatePerson(Person person) {
        return person;
    }
}
