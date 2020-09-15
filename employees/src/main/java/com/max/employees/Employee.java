package com.max.employees;

import com.vaadin.ui.Button;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String date;
    private String position;
    private int companyId;

    Employee(int id, String firstName, String lastName, String date, String position, int companyId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.position = position;
        this.companyId = companyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDate() {
        return date;
    }

    public String getPosition() {
        return position;
    }

    public int getCompany() {
        return companyId;
    }

    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setFName(String fName) {
        this.firstName = fName;
    }

    public void setValue(String fName, String lName, String date, String pos) {
        this.firstName = fName;
        this.lastName = lName;
        this.date = date;
        this.position = pos;
    }
}
