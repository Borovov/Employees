package com.max.employees;

public class Company {
    private int id;
    private String name;

    Company(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
