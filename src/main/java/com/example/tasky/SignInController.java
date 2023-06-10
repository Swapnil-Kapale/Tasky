package com.example.tasky;

import com.example.tasky.task.Task;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignInController {
    public boolean DEBUG_MODE = DashboardController.getDebugMode();
    static User USER;

    @FXML
    private PasswordField passwordTextFeild;

    @FXML
    private Button signinButton;

    @FXML
    private Label signinStatusLable;

    @FXML
    private TextField usernameTextFeild;


    //database veriables
    private Connection connect = null;
    private PreparedStatement prepare = null;
    private ResultSet result = null;

    //Default database schema creator
    public void databaseCreation() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ( id SERIAL PRIMARY KEY, username VARCHAR (255) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, email VARCHAR (255) UNIQUE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, role VARCHAR(255) NOT NULL DEFAULT 'team_member', team_id INTEGER REFERENCES teams(id) ON DELETE CASCADE, team_name VARCHAR(255) NOT NULL DEFAULT 'No Team', create_team BOOLEAN NOT NULL DEFAULT FALSE, create_task BOOLEAN NOT NULL DEFAULT FALSE);";
        String createTeamsTable = "CREATE TABLE IF NOT EXISTS teams ( id SERIAL PRIMARY KEY, name VARCHAR (255) UNIQUE NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE ); ";
        String createTasksTable = "CREATE TABLE IF NOT EXISTS task( task_id integer auto_increment primary key, task_title text, task_description text, issue_by text, task_type text, task_status text, task_issue_date date, task_due_date date ,task_completion_date date); ";
        String createTaskAssignTable = "CREATE TABLE IF NOT EXISTS task_assign( task_id integer references task (task_id), assigned_to_user_id integer references users(id) ); ";
        String addAdmin = "INSERT INTO users (username, password, email, role, create_team, create_task) SELECT 'admin', 'admin', 'admin@admin.org', 'admin', TRUE, TRUE  WHERE NOT EXISTS (SELECT * FROM users WHERE username = 'admin')";


        connect = database.connectDB();
        try {
            prepare = connect.prepareStatement(createUsersTable);
            prepare.execute();
            prepare = connect.prepareStatement(createTeamsTable);
            prepare.execute();
            prepare = connect.prepareStatement(createTasksTable);
            prepare.execute();
            prepare = connect.prepareStatement(createTaskAssignTable);
            prepare.execute();
            connect.createStatement().executeUpdate(addAdmin);
        } catch (Exception e) {
            if (DEBUG_MODE){
                e.printStackTrace();
            }
        }
    }

    public void signin(ActionEvent event) throws IOException {

        String username = usernameTextFeild.getText();
        String password = passwordTextFeild.getText();

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, username);
            prepare.setString(2, password);
            result = prepare.executeQuery();

            if (username.isEmpty() || password.isEmpty()) {
                signinStatusLable.setText("Please enter username and password");
            } else {
                if (result.next()) {

                    USER = new User(result.getInt("id"), result.getString("username"), result.getString("password"),
                            result.getString("email"), result.getString("role"), result.getInt("team_id"),
                            result.getString("team_name"), result.getBoolean("create_team"),
                            result.getBoolean("create_task"));
                    signinStatusLable.setText("Login Success");
                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 1920, 1000);
                    DashboardController dashboardController = fxmlLoader.getController();

                    //NOTE: From here, we have to change all 'result.variable' to 'USER.variable'
                    dashboardController.setUsernameLabel(result.getString("username"));
                    dashboardController.setRoleLabel(result.getString("role"));
                    dashboardController.setupDashboard(result.getInt("create_team"), result.getInt("create_task"));
                    dashboardController.setupTable(result.getInt("id"));
                    dashboardController.setupProgressBarsAndCounters(result.getInt("id"));
                    dashboardController.setupTaskAssignTable(result.getString("username"));
                    dashboardController.fillUserList();
                    dashboardController.fillTeamsList();
                    MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
                    Stage stage = new Stage();
                    stage.setTitle("Sign in");
                    stage.setScene(scene);
                    stage.show();
                    closeSignInWindow((Stage) signinButton.getScene().getWindow());

                } else {
                    signinStatusLable.setText("Login Failed");
                }
            }

        } catch (Exception ex) {
            if (DEBUG_MODE){
                ex.printStackTrace();
            }
        }

    }

    public static User getCurrentUser(){
        return USER;
    }

    //close signin window
    public void closeSignInWindow(Stage stage) throws IOException {
        stage.close();
    }

}
