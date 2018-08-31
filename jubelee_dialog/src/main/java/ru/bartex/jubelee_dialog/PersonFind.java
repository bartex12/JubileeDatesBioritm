package ru.bartex.jubelee_dialog;

public class PersonFind extends Person {

    private boolean select_find;

    public PersonFind(){};

    public PersonFind(String name, String day, String month,String year, boolean select){
        super(name,day,month,year);
        select_find = select;
    }

    public PersonFind(long id, String name, String day, String month,String year, boolean select){
        super(id, name,day,month,year);
        select_find = select;
    }

    public boolean isSelect_find() {
        return select_find;
    }

    public void setSelect_find(boolean select_find) {
        this.select_find = select_find;
    }
}
