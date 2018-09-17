package ru.bartex.y;

import java.io.Serializable;

public class Person implements Serializable {

    private Long person_id;
    private String mName;
    private String mCity;

    public Person(String name, String city){
        mName = name;
        mCity = city;
    }

    public Person(long id, String name, String city){
        person_id = id;
        mName = name;
        mCity = city;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

}
