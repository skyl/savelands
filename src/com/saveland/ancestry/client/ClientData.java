package com.saveland.ancestry.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.saveland.ancestry.client.models.Person;
import com.saveland.ancestry.client.services.PersonService;
import com.saveland.ancestry.client.services.PersonServiceAsync;

public class ClientData {

    private final PersonServiceAsync pService = GWT.create(PersonService.class);
    // private final PersonService pService = GWT.create(PersonService.class);

    public Person currentPerson = new Person();

    public ArrayList<Person> people;
    // maps display name to person object - for the selects in the form :/
    public HashMap<String, Object> peopleMap = new HashMap<String, Object>();
    // maps key to person ...
    public HashMap<String, Object> peopleKeyMap = new HashMap<String, Object>();

    private ArrayList<ClientDataObserver> observers = new ArrayList<ClientDataObserver>();

    public ClientData() {
    }

    public void addObserver(ClientDataObserver observer) {
        observers.add(observer);
    }

    /*
     * fetchPeople calls getPeople on the service and notifies observers when
     * the page loads.
     */
    public void fetchPeople() {
        pService.getPeople(new AsyncCallback<ArrayList<Person>>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.toString());
                GWT.log("fail!");
                notifyFetchPeopleFailure();
            }

            @Override
            public void onSuccess(ArrayList<Person> peoplelist) {
                GWT.log("onSuccess");
                people = peoplelist;
                for (Person person : peoplelist) {
                    peopleMap.put(person.getDisplayName(), person);
                    peopleKeyMap.put(person.key, person);
                }
                notifyFetchPeopleSuccess();
            }
        });
    }

    private void notifyFetchPeopleFailure() {
        GWT.log("notifyFetchPeopleFail");
        for (ClientDataObserver observer : observers) {
            observer.onFetchPeopleFailure();
        }
    }

    private void notifyFetchPeopleSuccess() {
        GWT.log("notifyFetchPeopleSuccess");
        for (ClientDataObserver observer : observers) {
            observer.onFetchPeopleSuccess();
        }
    }

    /*
     * savePerson
     */
    public void savePerson() {
        pService.savePerson(currentPerson, new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("FAIL ON SAVE!");
                GWT.log(caught.toString());
                notifySavePersonFailure();
            }

            @Override
            public void onSuccess(Person person) {
                GWT.log("onSuccess savePerson!!");
                people.add(person);
                peopleMap.put(person.getDisplayName(), person);
                peopleKeyMap.put(person.key, person);
                notifySavePersonSuccess(person);
            }
        });
    }

    protected void notifySavePersonSuccess(Person person) {
        for (ClientDataObserver observer : observers) {
            observer.onSavePersonSuccess(person);
        }
    }

    protected void notifySavePersonFailure() {
        for (ClientDataObserver observer : observers) {
            observer.onSavePersonFailure();
        }
    }

    /*
     * setCurrentPerson not async so there is no failure ..
     */
    public void setCurrentPerson(String key) {
        // if key is null then we set the currentPerson
        if (key == null) {
            GWT.log("setCurrentPerson to new Person()");
            currentPerson = new Person();
            // return;
        } else {
            currentPerson = (Person) peopleKeyMap.get(key);
        }
        for (ClientDataObserver observer : observers) {
            // get person from cData.currentPerson.
            observer.onSetCurrentPerson();
        }
    }

}
