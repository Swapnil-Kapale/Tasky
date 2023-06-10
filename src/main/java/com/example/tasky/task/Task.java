package com.example.tasky.task;

import com.example.tasky.database;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Task {
    int task_id;
    String task_title;
    String task_description;
    String assigned_by;
    String task_status;
    String task_type;
    String task_assigned_date;
    String task_due_date;

    String task_assigned_to = "";


    public Task(int id, String task_title, String task_description, String assigned_by, String task_type, String task_status, String task_assigned_date, String task_due_date) {
        this.task_id = id;
        this.task_title = task_title;
        this.task_description = task_description;
        this.assigned_by = assigned_by;
        this.task_type = task_type;
        this.task_status = task_status;
        this.task_assigned_date = task_assigned_date;
        this.task_due_date = task_due_date;

    }

    public void setAssignedTo(String assigned_to) {
        this.task_assigned_to = assigned_to;
    }

    public String getAssignedTo() {
        return task_assigned_to;
    }

    public String getAssignedBy() {
        return assigned_by;
    }

    public String getTask_title() {
        return task_title;
    }

    public int getTaskId() {
        return task_id;
    }

    public String getTask_description() {
        return task_description;
    }

    public String getTask_status() {
        return task_status;
    }

    public String getTask_type() {
        return task_type;
    }

    public String getTask_assigned_date() {
        return task_assigned_date;
    }

    public String getTask_due_date() {
        return task_due_date;
    }

    public boolean isOverdue() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dueDate;
        try {
            dueDate = sdf.parse(this.task_due_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        // Get the current date
        Date currentDate = new Date();

        // Check if the dueDate is before the currentDate
        return dueDate.before(currentDate);
    }

    public void updateTaskStatus() {
        if (isOverdue()) {
            Connection connection = database.connectDB();
            String sql = "UPDATE task SET task_status = 'overdue' WHERE task_id = " + task_id;
            try {
                connection.createStatement().executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
