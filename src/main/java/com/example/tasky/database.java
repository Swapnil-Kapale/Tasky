package com.example.tasky;
import java.sql.Connection;
import java.sql.DriverManager;

public class database {

    public static boolean DEBUG_MODE = DashboardController.getDebugMode();
    public static Connection connectDB(){
        Connection con = null;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tasky","root","Abd@88477");
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tasky","root","");
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/task_new_tables","root","");
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tasky","root","root");
            if (DEBUG_MODE){
                System.out.println("Connected");
            }
        }catch(Exception ex){
            if (DEBUG_MODE){
                System.out.println(ex.getMessage());
            }
        }
        return con;
    }

}
