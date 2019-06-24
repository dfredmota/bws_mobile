package com.developersd3.bwsmobile.model;

/**
 * Created by fred on 29/02/16.
 */
public class Country {

    String name = null;
    boolean selected = false;

    public Country(String code, String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}