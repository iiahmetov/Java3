package Lesson_3.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Chatterbox");
        primaryStage.setScene(new Scene(root, 500, 475));
        primaryStage.setMinWidth(250);
        primaryStage.setMinHeight(250);
        primaryStage.show();
    }


    public static void main(String[] args) {launch(args);}
}
