package de.gishmo.gwt.editor;

import java.util.Date;

public class Person {

  private String name;
  private String firstName;

  private Date date;

  private Address address;

  public Person() {
  }

  public Person(String name,
                String firstName,
                Date date,
                Address address) {
    this.name = name;
    this.firstName = firstName;
    this.date = date;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
