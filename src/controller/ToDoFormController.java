package controller;

import DB.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public TextField txtTaskName;
    public Label lblWelcomeNote;
    public Label lblID;
    public Pane subRoot;
    public AnchorPane root;
    public ListView<ToDoTM> lstToDos;
    public Button btnDelete;
    public Button btnUpdate;
    public TextField txtSelectedText;

    public String id;

    public void initialize(){
        lblID.setText(LoginFormController.enteredUserID);
        lblWelcomeNote.setText("Hi " + LoginFormController.enteredUserName + " Welcome !");
        subRoot.setVisible(false);
        loadList();

        txtSelectedText.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        lstToDos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                txtSelectedText.setDisable(false);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);

                txtSelectedText.requestFocus();
                subRoot.setVisible(false);

                ToDoTM selectedItem = lstToDos.getSelectionModel().getSelectedItem();

            if (newValue == null){
                return;
            }
                String description = newValue.getDescription();

                txtSelectedText.setText(description);

                id = newValue.getId();
            }
        });
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todos where id = ?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();
                loadList();
                txtSelectedText.clear();
                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);
                txtSelectedText.setDisable(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedText.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todos set description = ? where id = ?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            preparedStatement.executeUpdate();
            loadList();
            txtSelectedText.clear();
            txtSelectedText.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        txtTaskName.requestFocus();

        txtSelectedText.setDisable(false);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String id = autoGenerateID();
        String description = txtTaskName.getText();
        String user_ID = lblID.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todos values(?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_ID);
            preparedStatement.executeUpdate();

            subRoot.setVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        String newID = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todos order by id desc limit 1");
            boolean isExist = resultSet.next();

            if (isExist){
                String oldID = resultSet.getString(1);
                oldID = oldID.substring(1, oldID.length());
                int intID = Integer.parseInt(oldID);
                intID++;

                if (intID<10){
                    newID = "T00" + intID;
                }else if (intID<100){
                    newID = "T0" + intID;
                }else{
                    newID = "T" + newID;
                }

            }else{
                newID = "T001";
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LogInForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) this.root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }
    }

    public void loadList(){

        ObservableList<ToDoTM> todos = lstToDos.getItems();
        todos.clear();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todos where user_id = ?");
            preparedStatement.setObject(1,LoginFormController.enteredUserID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);;
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id,description,user_id);

                todos.add(toDoTM);
            }
            lstToDos.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
