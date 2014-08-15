package com.saveland.ancestry.client.models;

import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable {

    private static final long serialVersionUID = -646754221098495280L;

    public String key;
    public String firstName;
    public String lastName;
    public Boolean gender; // true is male, false is female
    public Date birthDate;
    public String motherKey;
    public String fatherKey;
    // GeoPt is not serializable
    public float lat;
    public float lon;
    public String notes;

    public String getDisplayName() {
        // GWT has no String.format? ballocks ..
        // return String.format("%s %s", this.firstName, this.lastName);
        return this.firstName + " " + this.lastName;
    }
}