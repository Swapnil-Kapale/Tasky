package com.example.tasky;

import com.example.tasky.task.Task;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.animation.Animation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;


public class DashboardController implements Initializable {
    public static boolean DEBUG_MODE = true;
    public static boolean getDebugMode(){
        return DEBUG_MODE;
    }
    User USER = SignInController.getCurrentUser();
    //database variables
    private Connection connect = null;
    private PreparedStatement prepare = null;
    private ResultSet result = null;

    //counters and progress bars
    @FXML
    private Label completeTaskCounter;


    @FXML
    private MFXProgressBar completeTaskProgressBar;

    @FXML
    private Label overdueTaskCounter;

    @FXML
    private MFXProgressBar overdueTaskProgressBar;

    @FXML
    private Label pendingTaskCounter;

    @FXML
    private MFXProgressBar pendingTaskProgressBar;

    //set username and role
    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label usernameLabel1;
    @FXML
    private Label roleLabel1;
    @FXML
    private Label usernameLabel11;
    @FXML
    private Label roleLabel11;


    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
        usernameLabel1.setText(username);
        usernameLabel11.setText(username);
    }


    public void setRoleLabel(String role) {
        roleLabel.setText(role);
        roleLabel1.setText(role);
        roleLabel11.setText(role);
    }

    //setup dashboard based on permissions
    @FXML
    private MFXButton createTeamButtonPanel;

    @FXML
    private MFXButton dashboardButtonPanel;
    @FXML
    private MFXButton createTaskButtonPanel;

    @FXML
    private AnchorPane dashboardMainPanel;

    @FXML
    private AnchorPane createTaskPanel;

    @FXML
    private AnchorPane createTeamPanel;


    @FXML
    public void setupDashboard(int create_team, int create_task) {
        createTaskPanel.setVisible(false);
        createTeamPanel.setVisible(false);
        createTeamButtonPanel.setVisible(false);
        createTaskButtonPanel.setVisible(false);
        if (create_team == 1) {
            createTeamButtonPanel.setVisible(true);
        }
        if (create_task == 1) {
            createTaskButtonPanel.setVisible(true);
        }
    }

    public void dashboardNavigation(ActionEvent event) {
//        dashboardMainPanel.setVisible(true);
        createTaskPanel.setVisible(false);
        createTeamPanel.setVisible(false);
        if (event.getSource() == createTeamButtonPanel) {
            createTeamPanel.setVisible(true);
            dashboardMainPanel.setVisible(false);
            createTaskPanel.setVisible(false);
        }
        if (event.getSource() == createTaskButtonPanel) {
            createTaskPanel.setVisible(true);
            dashboardMainPanel.setVisible(false);
            createTeamPanel.setVisible(false);
        }
        if (event.getSource() == dashboardButtonPanel) {
            dashboardMainPanel.setVisible(true);
            createTaskPanel.setVisible(false);
            createTeamPanel.setVisible(false);
        }
    }

    //task tab
    @FXML
    private MFXTableView<Task> taskTable;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskTable.autosizeColumnsOnInitialization();
        assignedTaskTable.autosizeColumnsOnInitialization();
    }

    public void setupProgressBarsAndCounters(int userid) {
        connect = database.connectDB();
        int totalTaskCounter = 0;
        String sql = "SELECT COUNT(task_status) FROM task join task_assign on task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ?";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();
            if (result.next()) {
                totalTaskCounter = result.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql = "SELECT COUNT(*) FROM task JOIN task_assign ON task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ? AND task.task_status = 'complete'";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();
            if (result.next()) {
                completeTaskCounter.setText(result.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sql = "SELECT COUNT(*) FROM task JOIN task_assign ON task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ? AND task.task_status = 'pending'";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();
            if (result.next()) {
                pendingTaskCounter.setText(result.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sql = "SELECT COUNT(*) FROM task JOIN task_assign ON task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ? AND task.task_status = 'overdue'";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();
            if (result.next()) {
                overdueTaskCounter.setText(result.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double completeTaskCounterValue = Integer.parseInt(this.completeTaskCounter.getText());
        double pendingTaskCounterValue = Integer.parseInt(this.pendingTaskCounter.getText());
        double overdueTaskCounterValue = Integer.parseInt(this.overdueTaskCounter.getText());

        // Also, we only have to set multiple ranges if we want to show multiple animation keyframes for the progress bar.
        //For now we are only using Range1.
        completeTaskProgressBar.getRanges1().add(NumberRange.of(0.0, completeTaskCounterValue / totalTaskCounter));
        pendingTaskProgressBar.getRanges1().add(NumberRange.of(0.0, pendingTaskCounterValue / totalTaskCounter));
        overdueTaskProgressBar.getRanges1().add(NumberRange.of(0.0, overdueTaskCounterValue / totalTaskCounter));

        createAndPlayAnimation(completeTaskProgressBar, completeTaskCounterValue / totalTaskCounter);
        createAndPlayAnimation(pendingTaskProgressBar, pendingTaskCounterValue / totalTaskCounter);
        createAndPlayAnimation(overdueTaskProgressBar, overdueTaskCounterValue / totalTaskCounter);
    }

    private void createAndPlayAnimation(ProgressIndicator indicator, double endValue) {
        AnimationUtils.TimelineBuilder build = AnimationUtils.TimelineBuilder.build();
        build.add(AnimationUtils.KeyFrames.of(3000, indicator.progressProperty(), endValue, Interpolators.INTERPOLATOR_V1)

                // We can add more keyframes to make the animation more smooth.
//            AnimationUtils.KeyFrames.of(6000, indicator.progressProperty(), 1.0, Interpolators.INTERPOLATOR_V1)
        );
        Animation a1 = build.getAnimation();
        a1.play();
    }

    public void setupTable(int userid) {

        //RowFactory allows setting things for individual rows, like context menus and dialogs
        //Sample code for setTableRowFactory
/*
        taskTable.setTableRowFactory(t -> {
            MFXTableRow<Task> row = new MFXTableRow<>(taskTable, t);
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    System.out.println("Primary button pressed");
                } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                    System.out.println("Secondary Button pressed");
                }
            });
            return row;
        });
*/


        //sample code for click on taskIdColumn cell of each task
/*
                taskTable.setTableRowFactory(t -> {
            MFXTableRow<Task> row = new MFXTableRow<>(taskTable, t);
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    System.out.println("Primary button pressed");
                    showDialog();
                } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                    System.out.println("Secondary Button pressed");
                }
            });
            return row;
        });
*/


        connect = database.connectDB();
        MFXTableColumn<Task> taskIdColumn = new MFXTableColumn<>("Task ID", true, Comparator.comparing(Task::getTaskId));
        MFXTableColumn<Task> tasktitleColumn = new MFXTableColumn<>("Task", true, Comparator.comparing(Task::getTask_title));
        MFXTableColumn<Task> assignedByColumn = new MFXTableColumn<>("Assigned By", true, Comparator.comparing(Task::getAssignedBy));
        MFXTableColumn<Task> taskTypeColumn = new MFXTableColumn<>("Type", true, Comparator.comparing(Task::getTask_type));
        MFXTableColumn<Task> taskStatusColumn = new MFXTableColumn<>("Task Status", true, Comparator.comparing(Task::getTask_status));
        MFXTableColumn<Task> taskAssignedDateColumn = new MFXTableColumn<>("Assigned Date", true, Comparator.comparing(Task::getTask_assigned_date));
        MFXTableColumn<Task> taskDueDateColumn = new MFXTableColumn<>("Due Date", true, Comparator.comparing(Task::getTask_due_date));

        taskIdColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTaskId));
        tasktitleColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_title));
        assignedByColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getAssignedBy));
        taskTypeColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_type));
        taskStatusColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_status));
        taskAssignedDateColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_assigned_date));
        taskDueDateColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_due_date));


        taskTable.getTableColumns().addAll(taskIdColumn, tasktitleColumn, assignedByColumn, taskTypeColumn, taskStatusColumn, taskAssignedDateColumn, taskDueDateColumn);

//        ObservableList<Task> data = FXCollections.observableArrayList(
//                new Task(1, "Task 1", "John Doe", "Task Type 1", "Task Status 1", "Task Assigned Date 1", "Task Due Date 1"),
//                new Task(2, "Task 2", "Jane", "Task Type 2", "Task Status 2", "Task Assigned Date 2", "Task Due Date 2"),
//                new Task(3, "Task 3", "Alice", "Task Type 3", "Task Status 3", "Task Assigned Date 3", "Task Due Date 3")
//        );
        //    taskTable.setItems(data);

        fillTaskTable(userid);
        //filters for tasks
        taskTable.getFilters().addAll(
                new IntegerFilter<>("Task ID", Task::getTaskId),
                new StringFilter<>("Description", Task::getTask_description),
                new StringFilter<>("Task", Task::getTask_description),
                new StringFilter<>("Assigned By", Task::getAssignedBy),
                new StringFilter<>("Type", Task::getTask_type), new StringFilter<>("Task Status", Task::getTask_status),

                //Date filter work but as strings. So user must enter the date in the correct format. (YYYY-MM-DD)
                new StringFilter<>("Assigned Date", Task::getTask_assigned_date),
                new StringFilter<>("Due Date", Task::getTask_due_date));

//        setting popups for tasks
        taskTable.setTableRowFactory(t -> {
            MFXTableRow<Task> row = new MFXTableRow<>(taskTable, t);
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    showDialog(t);
                }
            });
            return row;
        });
    }
    public void fillTaskTable(int userid){

        String sql = "SELECT * FROM task JOIN task_assign ON task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ?;";

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();

            ObservableList<Task> taskList = FXCollections.observableArrayList();

            while (result.next()) {
                Task t = new Task(result.getInt("task_id"), result.getString("task_title"), result.getString("task_description"), result.getString("issue_by"), result.getString("task_type"), result.getString("task_status"), result.getString("task_issue_date"), result.getString("task_due_date"));
                t.updateTaskStatus();
            }
            result = prepare.executeQuery();
            while (result.next()) {
                Task t = new Task(result.getInt("task_id"), result.getString("task_title"), result.getString("task_description"), result.getString("issue_by"), result.getString("task_type"), result.getString("task_status"), result.getString("task_issue_date"), result.getString("task_due_date"));
                taskList.add(t);
            }

            taskTable.setItems(taskList);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //task create tab
    @FXML
    private MFXTextField titleTextField;

    @FXML
    private MFXDatePicker dueDateField;

    @FXML
    private MFXTextField assignToField;
    @FXML
    private MFXTextField typeTextField;

    @FXML
    private TextArea descriptionField;

    @FXML
    public void createTaskfromInput(ActionEvent event) {
        String title = titleTextField.getText();
        String description = descriptionField.getText();
        String type = typeTextField.getText();
        String dueDate = dueDateField.getValue().toString();
        String assignedTo = assignToField.getText();
        String assignedBy = usernameLabel.getText();
        String status = "pending";

        //database connection
        String sql = "INSERT INTO task (task_title, task_description, issue_by, task_type, task_status, task_issue_date, task_due_date) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, title);
            prepare.setString(2, description);
            prepare.setString(3, assignedBy);
            prepare.setString(4, type);
            prepare.setString(5, status);
            prepare.setString(6, LocalDate.now().toString());//issue date
            prepare.setString(7, dueDate);
            prepare.executeUpdate();

            //get the task id of the newly created task
            sql = "SELECT task_id FROM task WHERE task_title = ? AND task_description = ? AND issue_by = ? AND task_type = ? AND task_status = ? AND task_issue_date = ? AND task_due_date = ?;";
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, title);
            prepare.setString(2, description);
            prepare.setString(3, assignedBy);
            prepare.setString(4, type);
            prepare.setString(5, status);
            prepare.setString(6, LocalDate.now().toString());
            prepare.setString(7, dueDate);
            result = prepare.executeQuery();
            int task_id = 0;
            while (result.next()) {
                task_id = result.getInt("task_id");
            }

            //get the user id of the assigned user
            sql = "SELECT id FROM users WHERE username = ?;";
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, assignedTo);
            result = prepare.executeQuery();
            int user_id = 0;
            while (result.next()) {
                user_id = result.getInt("id");
            }

            //assign the task to the user
            sql = "INSERT INTO task_assign (task_id, assigned_to_user_id) VALUES (?, ?);";
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, task_id);
            prepare.setInt(2, user_id);
            prepare.executeUpdate();

            fillAssignedTaskTable(assignedBy);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //task assign tab
    @FXML
    private MFXTableView<Task> assignedTaskTable;

    public void setupTaskAssignTable(String temp_username) {

        MFXTableColumn<Task> taskIdColumn = new MFXTableColumn<>("Task ID", true, Comparator.comparing(Task::getTaskId));
        MFXTableColumn<Task> tasktitleColumn = new MFXTableColumn<>("Task", true, Comparator.comparing(Task::getTask_title));
        MFXTableColumn<Task> assignedToColumn = new MFXTableColumn<>("Assigned To", true, Comparator.comparing(Task::getAssignedTo));
        MFXTableColumn<Task> taskTypeColumn = new MFXTableColumn<>("Type", true, Comparator.comparing(Task::getTask_type));
        MFXTableColumn<Task> taskStatusColumn = new MFXTableColumn<>("Task Status", true, Comparator.comparing(Task::getTask_status));
        MFXTableColumn<Task> taskAssignedDateColumn = new MFXTableColumn<>("Assigned Date", true, Comparator.comparing(Task::getTask_assigned_date));
        MFXTableColumn<Task> taskDueDateColumn = new MFXTableColumn<>("Due Date", true, Comparator.comparing(Task::getTask_due_date));

        taskIdColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTaskId));
        tasktitleColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_title));
        assignedToColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getAssignedTo));
        taskTypeColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_type));
        taskStatusColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_status));
        taskAssignedDateColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_assigned_date));
        taskDueDateColumn.setRowCellFactory(task -> new MFXTableRowCell<>(Task::getTask_due_date));

        assignedTaskTable.getTableColumns().addAll(taskIdColumn, tasktitleColumn, assignedToColumn, taskTypeColumn, taskStatusColumn, taskAssignedDateColumn, taskDueDateColumn);
        fillAssignedTaskTable(temp_username);

//        ObservableList<Task> data = FXCollections.observableArrayList(
//                new Task(1, "Task 1", "John Doe", "Task Type 1", "Task Status 1", "Task Assigned Date 1", "Task Due Date 1"),
//                new Task(2, "Task 2", "Jane", "Task Type 2", "Task Status 2", "Task Assigned Date 2", "Task Due Date 2"),
//                new Task(3, "Task 3", "Alice", "Task Type 3", "Task Status 3", "Task Assigned Date 3", "Task Due Date 3")
//        );
        //    taskTable.setItems(data);

    }

    public void fillAssignedTaskTable(String temp_username) {
        connect = database.connectDB();
        String sql = "SELECT * FROM task JOIN task_assign ON task.task_id = task_assign.task_id WHERE issue_by = ?;";

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, temp_username);
            result = prepare.executeQuery();

            ObservableList<Task> taskList = FXCollections.observableArrayList();

            while (result.next()) {
                Task t = new Task(result.getInt("task_id"), result.getString("task_title"), result.getString("task_description"), result.getString("issue_by"), result.getString("task_type"), result.getString("task_status"), result.getString("task_issue_date"), result.getString("task_due_date"));
                t.updateTaskStatus();
            }
            result = prepare.executeQuery();
            while (result.next()) {
                Task t = new Task(result.getInt("task_id"), result.getString("task_title"), result.getString("task_description"), result.getString("issue_by"), result.getString("task_type"), result.getString("task_status"), result.getString("task_issue_date"), result.getString("task_due_date"));
                t.setAssignedTo(result.getString("assigned_to_user_id"));
                taskList.add(t);
            }

            assignedTaskTable.setItems(taskList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assignedTaskTable.getFilters().addAll(new IntegerFilter<>("Task ID", Task::getTaskId), new StringFilter<>("Description", Task::getTask_description), new StringFilter<>("Task", Task::getTask_description), new StringFilter<>("Assigned To", Task::getAssignedTo), new StringFilter<>("Type", Task::getTask_type), new StringFilter<>("Task Status", Task::getTask_status),

                //Date filter work but as strings. So user must enter the date in the correct format. (YYYY-MM-DD)
                new StringFilter<>("Assigned Date", Task::getTask_assigned_date), new StringFilter<>("Due Date", Task::getTask_due_date));

    }

    //create team or user
    @FXML
    private MFXTextField CreateTeamLeaderField;

    @FXML
    private MFXTextField CreateTeamNameField;

    @FXML
    private MFXTextField CreateUserEmailField;

    @FXML
    private MFXPasswordField CreateUserPasswordField;

    @FXML
    private MFXTextField CreateUserRoleField;

    @FXML
    private MFXTextField CreateUserTeamIdField;

    @FXML
    private MFXTextField CreateUserUsernameField;

    @FXML
    private MFXCheckbox allowCreateTask;

    @FXML
    private MFXCheckbox allowCreateTeam;

    @FXML
    private Label createTeamStatusLabel;

    @FXML
    private Label createUserStatusLabel;

    @FXML
    private MFXListView<User> createUserUserList;

    @FXML
    private MFXListView<Team> createTeamTeamList;

    @FXML
    public void createNewUser(ActionEvent event) {

        String sql = "INSERT INTO users (username, password, email, role, create_team, create_task ,team_id) VALUES (? , ? , ? , ?, ? , ?,?);";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, CreateUserUsernameField.getText());
            prepare.setString(2, CreateUserPasswordField.getText());
            prepare.setString(3, CreateUserEmailField.getText());
            prepare.setString(4, CreateUserRoleField.getText());
            prepare.setBoolean(5, allowCreateTeam.isSelected());
            prepare.setBoolean(6, allowCreateTask.isSelected());
            int temp_teamid = Integer.parseInt(this.CreateUserTeamIdField.getText());
            prepare.setInt(7, temp_teamid);
            if (DEBUG_MODE) {
                System.out.println("User creation success");
            }
            createUserStatusLabel.setText("User created Successfully");


            prepare.executeUpdate();
            fillUserList();
        } catch (Exception e) {
            if (DEBUG_MODE) {
                e.printStackTrace();
                System.out.println("User creation failed");
            }
            createUserStatusLabel.setText("Please Enter Valid values");
        }

    }

    public void createTeam(ActionEvent event) {

        String sql = "INSERT INTO teams (name, owner_id) VALUES (?, ?);";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, CreateTeamNameField.getText());
            int temp_leaderid = Integer.parseInt(this.CreateTeamLeaderField.getText());
            prepare.setInt(2, temp_leaderid);
            if (DEBUG_MODE) {
                System.out.println("Team creation success");
            }
            createTeamStatusLabel.setText("Team created Successfully");

            prepare.executeUpdate();
            fillTeamsList();

        } catch (Exception e) {
            if (DEBUG_MODE) {
                System.out.println("Team creation failed");
                e.printStackTrace();
            }
            createTeamStatusLabel.setText("Please Enter Valid values");
        }
    }


    @FXML
    public void fillUserList() {

        String sql = "SELECT * FROM users";
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            ObservableList<User> userList = FXCollections.observableArrayList();
            StringConverter<User> converter = FunctionalStringConverter.to(user -> (user == null) ? "" : user.getName() + " (" + user.getUser_id() + ")");


            while (result.next()) {
                User u = new User(result.getString("username"), result.getInt("id"));
                userList.add(u);
            }

            if (DEBUG_MODE) {
                System.out.println("User List: ");
                System.out.println(userList);
            }
            createUserUserList.setItems(userList);
            createUserUserList.setConverter(converter);
            createUserUserList.setCellFactory(user -> new PersonCellFactory(createUserUserList, user));

            //TODO: Cool effects, but not working properly

//            createUserUserList.features().enableBounceEffect();
//            createUserUserList.features().enableSmoothScrolling(0.5);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //    fill the Teams list
    @FXML
    public void fillTeamsList() {

        String sql = "SELECT * FROM teams";
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            ObservableList<Team> teamList = FXCollections.observableArrayList();
            StringConverter<Team> converter = FunctionalStringConverter.to(team -> (team == null) ? "" : team.getName() + " (" + team.getOwner_id() + ")");


            while (result.next()) {
                Team t = new Team(result.getString("name"), result.getInt("owner_id"));
                teamList.add(t);
            }

            if (DEBUG_MODE) {
                System.out.println(teamList);
            }
            createTeamTeamList.setItems(teamList);
            createTeamTeamList.setConverter(converter);
            createTeamTeamList.setCellFactory(team -> new TeamCellFactory(createTeamTeamList, team));
//            createTeamTeamList.features().enableBounceEffect();
//            createTeamTeamList.features().enableSmoothScrolling(0.5);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //logout
    @FXML
    private MFXButton logoutButton;

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign-in.fxml")));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            if (DEBUG_MODE) {
                System.out.println("Logout");
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (DEBUG_MODE) {
                System.out.println("Logout failed");
            }
        }
    }

    private static class PersonCellFactory extends MFXListCell<User> {

        public PersonCellFactory(MFXListView<User> listView, User data) {
            super(listView, data);
            render(data);
        }

        @Override
        protected void render(User data) {
            super.render(data);
        }
    }

    private static class TeamCellFactory extends MFXListCell<Team> {

        public TeamCellFactory(MFXListView<Team> listView, Team data) {
            super(listView, data);
            render(data);
        }

        @Override
        protected void render(Team data) {
            super.render(data);
        }
    }

    //    //show a dialog when user clicks on create user button
    public void showDialog(Task task) {

//    dialogs work, need to map cancel and ok buttons, few minor bugs
        try {
            Connection connect = database.connectDB();
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Task details");
            dialog.setHeaderText("Description: ");
            dialog.setContentText(task.getTask_description());

            // Add buttons to the dialog
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType doneButton = new ButtonType("Mark as Done", ButtonBar.ButtonData.APPLY);
            ButtonType deleteButton = new ButtonType("Delete Task", ButtonBar.ButtonData.OTHER);
            if (USER.getRole().equals("admin")) {
                dialog.getDialogPane().
                        getButtonTypes().
                        addAll(deleteButton, doneButton, okButton);
            } else {
                dialog.getDialogPane().getButtonTypes().addAll(doneButton, okButton);
            }

            dialog.setResultConverter(buttonType -> {
                if (buttonType == okButton) {
                    return "OK";
                } else if (buttonType == doneButton) {
                    return "DONE";
                } else if (buttonType == deleteButton) {
                    return "DELETE";
                }
                return null;
            });

            // Show the dialog and wait for a button to be pressed
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && result.get().
                    equals("OK")) {
                if (DEBUG_MODE) {
                    System.out.println("Ok pressed");
                }
            } else if (result.isPresent() && result.get().equals("DONE")) {

                String sql = "UPDATE task SET task_status = 'complete', task_completion_date = ? WHERE task_id = ?";
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, String.valueOf(LocalDate.now()));
                prepare.setInt(2, task.getTaskId());
                prepare.executeUpdate();
                setupProgressBarsAndCounters(USER.getUser_id());
                fillTaskTable(USER.getUser_id());

                if (DEBUG_MODE) {
                    System.out.println("Done pressed / Task completed");
                }

            } else if (result.isPresent() && result.get().equals("DELETE")) {
                String sql = "DELETE FROM task WHERE task_id = ?";
                prepare = connect.prepareStatement(sql);
                prepare.setInt(1, task.getTaskId());
                prepare.executeUpdate();
                setupProgressBarsAndCounters(USER.getUser_id());
                fillTaskTable(USER.getUser_id());
                fillAssignedTaskTable(USER.getName());

                if (DEBUG_MODE) {
                    System.out.println("Delete Pressed / Task deleted");
                }

            }
        } catch (Exception e) {
            if (DEBUG_MODE) {
                e.printStackTrace();
            }
        }

    }
}


//Latest changes (2 Jun):
//1. Added sample code for row and cell clicks (popup should show on row click. Cell clicks can be used in future)
//2. Created a global user 'USER' on signin to store user details
//3. Added popups for rows in task table.
//4. clicking on task for admin shows 3 buttons(delete, mark as done, ok), for user shows 2 buttons(mark as done, ok). Need to map those buttons
//5. status is changed to complete on mark as done.
//6. Dialogs have current task in parameter, so attributes for current task for writing queries can be used from that parameter object
//7. fillTaskTable and fillAssignedTaskTable methods are now separate. these can be called on any action to update tables
//8. taskTable and counters are updated on mark as done and delete
//9. a new column 'task_completion_date' is added to task table. this is updated on mark as done

//BUGS:
//1.Team list is populated on first start?
//2. assigned tasks table should be updated when marking tasks as complete/ deleting tasks
//3. popups not implemented for assigned tasks table (need small changes)

