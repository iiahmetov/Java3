package Lesson_2.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {

    Vector<ClientHandler> clients;

    public Server() throws SQLException {
        AuthService.connect();
        AuthService.resetAllClients();  //сбрасываем статус "онлайн" всех пользователей на ложь

        ServerSocket server = null;
        Socket socket = null;

        try {
            clients = new Vector<>();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while(true){
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public void broadcastMsg(String str, String sender){
        AuthService.addMsgToDatabase(sender, null, str,"00.00");
        for (ClientHandler o: clients) {
            if (o.getNick().equals(sender)){            //для себя отправляется сообщение не с ником, а с надписью "Я:"
                o.sendMsg("Me: " + str);
            } else {
                o.sendMsg(sender + ": " + str);
            }
        }
    }

    public void sendPrvMsg(String str, String sender, String receiver){ //метод для отправки приватных сообщений
        AuthService.addMsgToDatabase(sender, receiver, str,"00.00");
        boolean flag = false;                                       //флаг для проверки дошло ли сообщение
        for (ClientHandler o: clients) {
            if (o.getNick().equals(receiver)) {      //если ник совпадает с ником получателя, то отправляем сообщение
                o.sendMsg("private from [ " + sender + " ]: " + str);
                flag = true;                                        //меняем флаг на истину
            }
            if (o.getNick().equals(sender)) {                       //для себя тоже отправляем дубликат сообщения
                o.sendMsg("private to [ " + receiver + " ]: " + str);
            }
        }
        if (!flag) {                                            //проверяем статус сообщения
            for (ClientHandler o: clients) {
                if (o.getNick().equals(sender)) {    //в случае провала сообщаем о неудачной попытке отправить сообщение
                    o.sendMsg("Attempt to send a private message to [ " + receiver + " ] failed!");
                    System.out.println("Attempt to send a private message from [ " + sender + " ] to [ " + receiver + " ] failed!");
                }
            }
        }
    }


    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientList();
        clientHandler.sendMsg(AuthService.getMessagesFromDatabase(clientHandler.getNick()));
    }

    public void update(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientList();
        clientHandler.sendMsg(AuthService.getMessagesFromDatabase(clientHandler.getNick()));
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientList();
    }



    public void broadcastClientList(){
        StringBuilder strb = new StringBuilder();
        strb.append("/clientlist ");
        for (ClientHandler o: clients) {
            strb.append(o.getNick() + " ");
        }
        String str = strb.toString();
        for (ClientHandler o: clients) {
            o.sendMsg(str);
        }

    }
}
