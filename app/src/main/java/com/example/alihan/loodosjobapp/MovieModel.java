package com.example.alihan.loodosjobapp;

public class MovieModel {
    String name, year, id, plot;


    MovieModel(String name, String id) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getPlot() {
        return plot;
    }

}
