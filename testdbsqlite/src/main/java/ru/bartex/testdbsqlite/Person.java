package ru.bartex.testdbsqlite;


import java.io.Serializable;

public class Person implements Serializable{

    private long person_id;
    private  String person_name;
    private boolean person_choose;

    //пустой конструктор
    public Person(){    }

    //основной конструктор
    public Person(String name, boolean choose){
        person_name = name;
        person_choose = choose;
    }

    //конструктор, задающий id
    public Person(long id, String name, boolean choose){
        person_id = id;
        person_name = name;
        person_choose = choose;
    }

    public long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(long person_id) {
        this.person_id = person_id;
    }

    public  String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public  boolean getPerson_choose() {
        return person_choose;
    }

    public  void setPerson_choose(boolean choose) {
        person_choose = choose;
    }

}
