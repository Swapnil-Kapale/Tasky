package com.example.tasky;

public class Team {

    String name;
    int owner_id;

    Team(String name, int owner_id){
        this.name = name;
        this.owner_id = owner_id;
    }

    public String getName(){
        return name;
    }

    public int getOwner_id(){
        return owner_id;
    }

}
