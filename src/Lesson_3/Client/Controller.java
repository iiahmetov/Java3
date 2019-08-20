package Lesson_3.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class Controller {

    @FXML
    public HBox upperPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button authButton;
    @FXML
    public TextArea textArea;
    @FXML
    public HBox bottomPanel;
    @FXML
    public TextField textField;
    @FXML
    public Button sendButton;
    @FXML
    public ListView<String> clientList;
    @FXML
    public HBox loggedInPanel;
    @FXML
    public Button changeNickButton;
    @FXML
    public Button logOutButton;

    private boolean isAuthorized;
    ChangeNicknameController changeNicknameController = null;
    String login = "";

    Socket socket;
    DataOutputStream out;
    DataInputStream in;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    public void setAuthorized(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
        if(isAuthorized){
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
            loggedInPanel.setVisible(true);
            loggedInPanel.setManaged(true);
        } else {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
            loggedInPanel.setVisible(false);
            loggedInPanel.setManaged(false);
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //цикл авторизации
                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/authok")) {
                                setAuthorized(true);
                                System.out.println("Клиент подключился");
                                readMsgsFromFile(10);    //чтение N последних сообщений из файла
                                break;
                            }
                            if (str.equals("/end")) {
                                throw new RuntimeException("Клиент отключён за бездействие");
                            }
                            textArea.appendText(str + "\n");
                        }
                        //цикл работы
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    System.out.println("Клиент отключился");
                                    break;
                                }
                                if (str.startsWith("/clientlist")) {
                                    String[] clientListTokens = str.split(" +");
                                    Platform.runLater(() -> {
                                        clientList.getItems().clear();
                                        for (int i = 1; i < clientListTokens.length; i++) {
                                            clientList.getItems().add(clientListTokens[i]);
                                        }
                                    });
                                }
                                if  (str.startsWith("/changenickOK")) {
                                    String token[] = str.split(" +", 3);
                                    changeNicknameController.statusArea.appendText("Ник '" + token[1] + "' был успешно заменён на '" + token[2] + "'");
                                }
                                if (str.startsWith("/changenickSAME")) {
                                    changeNicknameController.statusArea.appendText("Ник не был изменён, т.к. вы ввели текущий ('" + changeNicknameController.nicknameField.getText() + "')");
                                }
                            } else {
                                textArea.appendText(str + "\n");
                                writeMsgToFile(str);
                            }
                        }
                    }catch (RuntimeException e){
                        System.out.println("Клиент отключён за бездействие");
                    }catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMSG(ActionEvent actionEvent) {
        try {
            if (!(textField.getText().equals(""))) {                  //условие для избежания ввода пустой строки
                out.writeUTF(textField.getText());
                textField.clear();
                textField.requestFocus();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMouseReleasedSendButton(MouseEvent mouseEvent) { //хотелось сделать именно клик по кнопке, чтобы избежать случайной отправки сообщения вслучае нажатия Tab-пробел
        ActionEvent actionEvent = new ActionEvent();
        sendMSG(actionEvent);
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if(socket == null || socket.isClosed()){
            connect();
        }
        login = "";
        try {
            //если строки логина и пароля не пустые и не забиты пробелами, формируем запрос на авторизацию
            if (!loginField.getText().replaceAll(" ", "").equals("") && !passwordField.getText().replaceAll(" ", "").equals("")){
                out.writeUTF("/auth "+loginField.getText()+" "
                        + passwordField.getText());
                login = loginField.getText();
                loginField.clear();
                passwordField.clear();
            } else {                //в противном случае очищаем строки логина и пароля
                loginField.clear();
                passwordField.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMouseReleasedTryToAuth(MouseEvent mouseEvent) {
        ActionEvent actionEvent = new ActionEvent();
        tryToAuth(actionEvent);
    }

    public void clickClientList(MouseEvent mouseEvent) {
        String msg = textField.getText();    //сохраняем сообщение из строки ввода
        if (msg.startsWith("/w")){           //проверяем содержит ли оно команду личного сообщения
            String[] token = msg.split(" +",3); //если да, то разбиваем на массив из 3 частей команда/ник/сообщение
            msg = token[2];                 //копируем только сообщение
        }
        String reciever = clientList.getSelectionModel().getSelectedItem(); //берём ник, по которому кликнули
        textField.setText("/w " + reciever + " " + msg); //копируем в строку ввода команду для приватного сообщения, ник по которому кликнули, сообщение из строки ввода(если было)
    }

    public void onMouseReleasedChangeNick(MouseEvent mouseEvent) {
        if (changeNicknameController == null){      //если окно смены пароля не создавалось, то делаем следующее
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("changeNickSample.fxml")); //ссылаемся на конфигурацию окна
                Parent root1 = (Parent) fxmlLoader.load();                          //создаём узел с конфигурацией окна
                Stage stage = new Stage();                                          //создаём окно
                stage.initModality(Modality.APPLICATION_MODAL);                     //даём ему свойство быть приоритетным (не даёт работать с программой, пока не закроется)

                ChangeNicknameController changeNicknameController = fxmlLoader.getController(); //привязываем контроллеры двух окон
                changeNicknameController.controller = this;

                this.changeNicknameController = changeNicknameController;

                stage.setTitle("Change Nickname");                  //задаём название окна
                stage.setScene(new Scene(root1));                   //создаём окно
                stage.show();                                       //делаем окно видимым
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Stage stage = (Stage) changeNicknameController.applyButton.getScene().getWindow();  //если окно уже было создано, то находим ссылку на него через любой элемент окна
            stage.show();       //делаем это окно видимым
        }


    }

    public void onMouseReleasedLogOut(MouseEvent mouseEvent) {
        try {
            out.writeUTF("/end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMsgToFile (String msg){        //метод для записи сообщений в файл на локальном клиенте
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/Lesson_3/" + login + "MessageLog.txt", true));
            writer.write (msg + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMsgsFromFile(int numberOfLastMessages) {        //метод для чтения N последних сообщений из файла
        try{
            BufferedReader reader = new BufferedReader(new FileReader("src/Lesson_3/" + login + "MessageLog.txt"));
            String str;
            LinkedList linkedList = new LinkedList();
            textArea.appendText("\n" + "----------Начало предыдущей сессии----------" + "\n");
            while ((str = reader.readLine()) != null) {
                if (linkedList.size() > (numberOfLastMessages - 1)){
                    linkedList.removeFirst();
                }
                linkedList.addLast(str);
            }
            for (int i = 0; i < linkedList.size(); i++) {
                textArea.appendText(linkedList.get(i) + "\n");
            }
            textArea.appendText("----------Конец предыдущей сессии----------" + "\n" + "\n");
            linkedList.clear();
        } catch (FileNotFoundException f) {
//            f.printStackTrace();
            textArea.appendText("История сообщений пуста" + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
