package Lesson_3.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;


public class ChangeNicknameController {
    @FXML
    public HBox changeNickPanel;
    @FXML
    public TextField nicknameField;
    @FXML
    public Button applyButton;
    @FXML
    public TextArea statusArea;

    Controller controller;

    public void onMouseReleasedApply(MouseEvent mouseEvent) {
        ActionEvent actionEvent = new ActionEvent();
        onActionApply(actionEvent);
    }


    public void onActionApply(ActionEvent actionEvent) {
        String str = nicknameField.getText();
        if (str.contains(" ") || str.equals("")){
            statusArea.clear();
            nicknameField.clear();
            statusArea.appendText("Введите корректный никнейм.\nНикнейм не должен содержать пробелы.");
        } else {
            try {
                StringBuilder msg = new StringBuilder();
                msg.append("/changenick " + str);
                statusArea.clear();
                controller.out.writeUTF(msg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
