package com.example.tasky;

import com.example.tasky.task.Task;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;


public class DashboardController implements Initializable {
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

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public void setRoleLabel(String role) {
        roleLabel.setText(role);
    }

    //task tab
    @FXML
    private MFXTableView<Task> taskTable;

    //database connection

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        setupTable();
//        setupProgressBarsAndCounters();
        taskTable.autosizeColumnsOnInitialization();
    }

    public void setupProgressBarsAndCounters(int userid) {
        connect = database.connectDB();
        int totalTaskCounter = 0;
        String sql = "SELECT COUNT(task_status) FROM task join task_assign on task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ?";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();
            if(result.next()) {
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
            if(result.next()){
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
            if(result.next()){
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
            if(result.next()){
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

    private void createAndPlayAnimation(ProgressIndicator indicator,double endValue){
        AnimationUtils.TimelineBuilder build = AnimationUtils.TimelineBuilder.build();
        build.add(
                AnimationUtils.KeyFrames.of(3000, indicator.progressProperty(), endValue, Interpolators.INTERPOLATOR_V1)

                // We can add more keyframes to make the animation more smooth.
//            AnimationUtils.KeyFrames.of(6000, indicator.progressProperty(), 1.0, Interpolators.INTERPOLATOR_V1)
        );
        Animation a1 = build.getAnimation();

        a1.play();
    }

    public void setupTable(int userid) {

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

        //database connection
//        String sql = "SELECT * FROM task";
        String sql = "SELECT * FROM task JOIN task_assign ON task.task_id = task_assign.task_id WHERE task_assign.assigned_to_user_id = ?;";

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, userid);
            result = prepare.executeQuery();

            ObservableList<Task> taskList = FXCollections.observableArrayList();

            while (result.next()) {
                Task t = new Task(result.getInt("task_id"),result.getString("task_title"),result.getString("task_description"),  result.getString("issue_by"), result.getString("task_type"), result.getString("task_status"), result.getString("task_issue_date"), result.getString("task_due_date"));
                t.updateTaskStatus();
            }
            result = prepare.executeQuery();
            while (result.next()){
                Task t = new Task(result.getInt("task_id"),result.getString("task_title"),result.getString("task_description"),  result.getString("issue_by"), result.getString("task_type"), result.getString("task_status"), result.getString("task_issue_date"), result.getString("task_due_date"));
                taskList.add(t);
            }

            taskTable.setItems(taskList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        taskTable.getFilters().addAll(
                new IntegerFilter<>("Task ID", Task::getTaskId),
                new StringFilter<>("Description", Task::getTask_description),
                new StringFilter<>("Task", Task::getTask_description),
                new StringFilter<>("Assigned By", Task::getAssignedBy),
                new StringFilter<>("Type", Task::getTask_type),
                new StringFilter<>("Task Status", Task::getTask_status),

                //Date filter work but as strings. So user must enter the date in the correct format. (YYYY-MM-DD)
                new StringFilter<>("Assigned Date", Task::getTask_assigned_date),
                new StringFilter<>("Due Date", Task::getTask_due_date)
        );
    }

    //task create tab

    @FXML
    private AnchorPane tasktab;
    @FXML
    private Button createTaskButton;
    @FXML
    private Button TasksButton;

    public void createTaskButtonAction(ActionEvent event) {
        tasktab.setVisible(false);
    }
    public void TasksButtonAction(ActionEvent event) {
        tasktab.setVisible(true);
    }


    //logout
    @FXML
    private MFXButton logoutButton;
    @FXML
    private void logout(ActionEvent event){
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign-in.fxml")));
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                System.out.println("Logout");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Logout failed");
            }
    }
}
