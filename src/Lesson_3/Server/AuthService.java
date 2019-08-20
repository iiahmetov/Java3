package Lesson_3.Server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/Lesson_3/clients.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void resetAllClients(){       //метод для сброса параметра "онлайн" для всех клиентовв базе данных
        String sqlSwitchAllOffline = String.format("UPDATE base\n" +
                "SET online = 0");
        try {
            stmt.execute(sqlSwitchAllOffline);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass){
        String sqlLogPass = String.format("SELECT nickname FROM base\n" +   //создаём запрос ника по паре логин/пароль
                "WHERE login = '%s'\n" +
                "AND password = '%s'", login, pass);
        String sqlOnline = String.format("SELECT online FROM base\n" +      //создаём запрос статуса "онлайн" по логину
                "WHERE login = '%s'\n", login);                             //т.к. логины уникальны, а ники нет
        try {
            ResultSet rsLogPas = stmt.executeQuery(sqlLogPass);             //выполняем запрос ника
            if (rsLogPas.next()){                                           //если ответ релевантный то
                String rsNick = rsLogPas.getString("nickname"); //сохраняем ник
                ResultSet rsOnline = stmt.executeQuery(sqlOnline);          //выполняем запрос статуса "онлайн"
                if (rsOnline.getBoolean(1)){                    //если статус "онлайн" истина
                    return ("/alreadyOnline");                              //возвращаем команду "уже онлайн"
                } else {                                                    //если статус "онлайн" ложь
                    SwitchOnline(login);                                    //переключаем статус "онлайн" на истину
                    return rsNick;                                          //передаём ник
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void SwitchOnline (String login){       //метод для переключения статуса "онлайн" на истину
        String sqlSwitchOnline = String.format("UPDATE base\n" +
                "SET online = 1\n" +
                "WHERE login = '%s'", login);
        try {
            stmt.execute(sqlSwitchOnline);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void SwitchOffline (String login){        //метод переключения статуса "онлайн" на ложь
        String sqlSwitchOnline = String.format("UPDATE base\n" +
                "SET online = 0\n" +
                "WHERE login = '%s'", login);
        try {
            stmt.execute(sqlSwitchOnline);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void addMsgToDatabase(String sender, String receiver, String text, String date){    //метод добавления сообщение в базу
//        String sql = String.format("INSERT INTO messages(sender, receiver, text, date)\n" +
//                "VALUES('%s', '%s', '%s', '%s')", sender, receiver, text, date);
//        try {
//            stmt.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public static String getMessagesFromDatabase(String nick){            //метод извлечения сообщений из базы
//        String sql = String.format("SELECT * FROM messages\n" +
//                "WHERE sender = '%s'\n" +
//                "OR receiver = '%s'\n" +
//                "OR receiver = 'null'", nick, nick);
//        StringBuilder sb = new StringBuilder();
//        try {
//            ResultSet rs = stmt.executeQuery(sql);
//            while (rs.next()){
//                String sender = rs.getString("sender");
//                String receiver = rs.getString("receiver");
//                String text = rs.getString("text");
//                String date = rs.getString("date");
//                if (receiver.equals("null")){
//                    if (sender.equals(nick)){
//                        sb.append("Me: " + text + "\n");
//                    } else {
//                        sb.append(sender + ": " + text + "\n");
//                    }
//                } else {
//                    if (sender.equals(nick)){
//                        sb.append("private to [ " + receiver + " ]: " + text + "\n");
//                    } else {
//                        sb.append("private from [ " + sender + " ]: " + text + "\n");
//                    }
//
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return  sb.toString();
//    }

    public static void ChangeNick(String login, String nick){
        String sqlChangeNick = String.format("UPDATE base\n" +                 //создаём запрос смены ника по логину
                "SET nickname = '%s'\n" +
                "WHERE login = '%s'", nick, login);
        try {
            stmt.executeUpdate(sqlChangeNick);                               //выполняем запрос смены ника
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
