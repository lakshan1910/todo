package controller;

import DB.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginFormController {
    public TextField txtUserName;
    public TextField txtPassword;
    public AnchorPane root;

    public static String enteredUserName;
    public static String enteredUserID;

    public void btnLoginOnAction(ActionEvent actionEvent) {

        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where user_name = ? and password = ?;");

            preparedStatement.setObject(1,userName);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isExist = resultSet.next();
//            System.out.println(isExist);

            if (isExist){

                enteredUserID = resultSet.getString(1);
                enteredUserName = resultSet.getString(2);

                Parent parent= FXMLLoader.load(this.getClass().getResource("../view/ToDoForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("To Do List");
                primaryStage.centerOnScreen();

            }else{
                new Alert(Alert.AlertType.CONFIRMATION,"Invalid Username and password").showAndWait();
                txtUserName.clear();;
                txtPassword.clear();

                txtUserName.requestFocus();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();

        }
    }

    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {

        Parent parent = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("../view/CreateNewAccountForm.fxml")));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account");
        primaryStage.centerOnScreen();


    }
}
