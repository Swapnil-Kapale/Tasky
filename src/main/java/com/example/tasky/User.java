package com.example.tasky;

public class User {
    int user_id;
    String name;
    String email;
    String role;
    int team_id;
    String team_name;
    boolean create_team;
    boolean create_task;



    User(String name, int user_id) {
        this.user_id = user_id;
        this.name = name;
    }

    User(int id, String username,
         String email,
         String created_at,
         String role,
         int team_id,
         String team_name,
         boolean create_team,
         boolean create_task) {
        this.user_id = id;
        this.name = username;
        this.email = email;
        this.role = role;
        this.team_id = team_id;
        this.team_name = team_name;
        this.create_team = create_team;
        this.create_task = create_task;
    }


    public String getName() {
        return name;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public int getTeam_id() {
        return team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public boolean getCreate_team() {
        return create_team;
    }

    public boolean getCreate_task() {
        return create_task;
    }

}
