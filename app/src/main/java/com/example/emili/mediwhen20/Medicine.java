package com.example.emili.mediwhen20;

import java.io.Serializable;

/**
 * Created by emili on 2019-02-12.
 */
//This code was retrieved and modified from https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable
public class Medicine implements Serializable{
//ends
    private String nameOfMed, course, date;
    private int howMany, id;
    private boolean mor, day, eve;

    Medicine (String nameOfMed, String course, boolean mor, boolean day, boolean eve, int howMany, int id, String date){//constructor of the class Medicine
        this.id = id;
        this.nameOfMed = nameOfMed;
        this.course = course;
        this.mor = mor;
        this.day = day;
        this.eve = eve;
        this.howMany = howMany;
        this.date = date;
    }

    //various setters and getters as the variables are private

    public void setNameOfMed(String nameOfMed) {
        this.nameOfMed = nameOfMed;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDay(boolean day) {
        this.day = day;
    }

    public void setEve(boolean eve) {
        this.eve = eve;
    }

    public void setHowMany(int howMany) {
        this.howMany = howMany;
    }

    public void setMor(boolean mor) {
        this.mor = mor;
    }

    public String getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }

    public int getHowMany() {
        return howMany;
    }

    public String getNameOfMed() {
        return nameOfMed;
    }

    public boolean getMor(){
        return mor;
    }

    public boolean getDay(){
        return day;
    }

    public boolean getEve(){
        return eve;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString (){
        return nameOfMed + "," + course + "," + mor + "," + day + "," + eve + "," + howMany + "," + id + "," + date +",\n";
    }
}
