package com.saveland.ancestry.client;

import com.saveland.ancestry.client.models.Person;

public interface ClientDataObserver {

    // can get the people off of the observable.
    void onFetchPeopleSuccess();

    void onFetchPeopleFailure();

    void onSavePersonSuccess(Person person);

    void onSavePersonFailure();

    void onSetCurrentPerson();
    // can get the person from the cData object.
    // not async ...
    // void onSetCurrentPersonFailure();

}
