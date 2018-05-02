package com.example.alihan.loodosjobapp;

public class MovieModel {
    String name, id;


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

    public String getId() {
        return id;
    }


}
