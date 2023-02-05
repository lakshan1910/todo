package controller;

import DB.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public TextField txtUserName;
    public TextField txtEmail;
    public TextField txtNewPassword;
    public TextField txtConfirmPassword;
    public Button btnRegister;
    public Label lblUserID;
    public Label lblNewPasswordConfirm;
    public Label lblConfirmPassword;
    public AnchorPane root;


    public void initialize(){

        txtUserName.setDisable(true);
        txtEmail.setDisable(true);
        txtConfirmPassword.setDisable(true);
        txtNewPassword.setDisable(true);
        txtConfirmPassword.setDisable(true);
        btnRegister.setDisable(true);
        lblNewPasswordConfirm.setVisible(false);
        lblConfirmPassword.setVisible(false);
        

    }
    

    public void btnAddNewUerOnAction(ActionEvent actionEvent) {

        txtUserName.setDisable(false);
        txtEmail.setDisable(false);
        txtConfirmPassword.setDisable(false);
        txtNewPassword.setDisable(false);
        txtConfirmPassword.setDisable(false);
        btnRegister.setDisable(false);
        txtUserName.requestFocus();

        autoGenerateID();


    }

    public void autoGenerateID(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist){

                String oldID = resultSet.getString(1);

                int length =oldID.length();

                String id = oldID.substring(1,length);

                int intID = Integer.parseInt(id);

                intID += 1;
                if (intID<10){
                    lblUserID.setText("U00" + intID);
                }else if (intID<100){
                    lblUserID.setText("U0" + intID);
                }else{
                    lblUserID.setText("U" + intID);
                }

            }else{
                lblUserID.setText("U001");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void btnRegisterOnClick(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        boolean isEqual = newPassword.equals(confirmPassword);

        if (isEqual){
            txtNewPassword.setStyle("-fx-border-color: transparent");
            txtConfirmPassword.setStyle("-fx-border-color: transparent");
            lblNewPasswordConfirm.setVisible(false);
            lblConfirmPassword.setVisible(false);
            register();
        }else{
            txtNewPassword.setStyle("-fx-border-color: red");
            txtConfirmPassword.setStyle("-fx-border-color: red");
            txtNewPassword.requestFocus();
            lblNewPasswordConfirm.setVisible(true);
            lblConfirmPassword.setVisible(true);

        }

    }

    public void register(){
        String id = lblUserID.getText();
        String userName = txtUserName.getText();
        String password = txtNewPassword.getText();
        String email = txtEmail.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,password);
            preparedStatement.setObject(4,email);

            int i = preparedStatement.executeUpdate();

            if (i != 0){ //if data insert is successful
                new Alert(Alert.AlertType.CONFIRMATION,"Success....").showAndWait();

                Parent parent= FXMLLoader.load(this.getClass().getResource("../view/LogInForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();

            }else{
                new Alert(Alert.AlertType.CONFIRMATION,"Something went wrong.....").showAndWait();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
}
