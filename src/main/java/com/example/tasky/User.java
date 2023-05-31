package com.example.tasky;

public class User {
    int user_id;
    String name;

    User(String name, int user_id){
        this.user_id = user_id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getUser_id(){
        return user_id;
    }
}
