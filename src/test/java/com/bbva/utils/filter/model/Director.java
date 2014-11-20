package com.bbva.utils.filter.model;

/**
* Created by Alejandro on 19/11/2014.
*/
public class Director {
    private String name;
    private String lastName;
    private Awards awards;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Awards getAwards() {
        return awards;
    }

    public void setAwards(Awards awards) {
        this.awards = awards;
    }
}
