package Lesson_3.Server;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class ClientHandler {

    private Server server;
    private Socket socket;
    DataOutputStream out;
    DataInputStream in;
    String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            new Thread(() -> {
                try {
                    socket.setSoTimeout(7000);
//                    new Thread(() -> {    //метод через таймер
//                        try {
//                            Thread.sleep(120000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        if (nick == null) {
//                            sendMsg("/end");      //с этой строкой работает, т.к. ловит эксепшн с потерей потока
//                            throw new RuntimeException("Клиент отключён за бездействие"); //!!!не идёт обработка catch в этом потоке
//                        }
//                    }).start();
                    // цикл авторизации.
                    String login;
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth")) {
                            String[] token = str.split(" +");
                            login = token[1];           //сохраняем логин для корретного переключения статуса "онлайн"
                            String newNick = AuthService.getNickByLoginAndPass(token[1], token[2]);
                            if (newNick != null) {
                                if (newNick.equals("/alreadyOnline")) {     //если получено данное сообщение
                                    sendMsg("Учётная запись уже активна");  //то информируем пользователя
                                } else {
                                    sendMsg("/authok");
                                    nick = newNick;
                                    server.subscribe(this);
                                    socket.setSoTimeout(0);
                                    break;
                                }
                            } else {
                                sendMsg("Неверный логин / пароль");
                            }
                        }
                    }
                    //Цикл для работы
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/end");
//                            System.out.println("Клиент отключился");
                            AuthService.SwitchOffline(login);     //в случае выхода переключаем статус "онлайн" на ложь
                            break;
                        }
                        if (str.startsWith("/changenick ")) {
                            String oldNick = getNick();
                            String[] token = str.split(" +", 2);
                            if (token[1].equals(oldNick)) {
                                out.writeUTF("/changenickSAME");
                            } else {
                                AuthService.ChangeNick(login, token[1]);
                                out.writeUTF("/changenickOK " + oldNick + " " + token[1]);
                                server.unsubscribe(this);
                                nick = token[1];
                                server.subscribe(this);
                            }
                        } else if (str.startsWith("/w ")) {             //в случае ввода префикса приватного сообщения
                            String[] prv = str.split(" +", 3); //создаю строчный массив из 3 элементов
                            server.sendPrvMsg(prv[2], getNick(), prv[1]); //отправляю на сервер приватное сообщение
                            System.out.println("Private message from [ " + getNick() + " ] to [ " + prv[1] + " ]: " + prv[2]);
                        } else {
                            System.out.println(getNick() + ": " + str);
                            server.broadcastMsg(str, getNick());
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Клиент отключён за бездействие");
                    sendMsg("/end");
//                } catch (RuntimeException e){ //!!!не обрабатывает данный эксепшн из дополнительного потока с таймером таймаута
//                    System.out.println("Клиент отключён за бездействие");
//                    sendMsg("/end");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
                    System.out.println("Клиент отключился");
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String str){
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {return nick;}
}
