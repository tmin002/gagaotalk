package kr.gagaotalk.server;

import kr.gagaotalk.server.table.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseEG {
    public static Connection con = null;

    private static void makeConnection(String url, String userName, String password) throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, userName, password);
            System.out.println(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws ClassNotFoundException, ErrorInProcessingException {
        /*System.out.println("Enter the SQL server password:");
        Scanner kb = new Scanner(System.in);
        String pw = kb.nextLine(); // or create password String variable*/
        String pw = "1234";
        makeConnection("jdbc:mysql://localhost", "root", pw);

        // initialize table instances
        ChatroomTable.initializeChatroomTableGlobal();
        FileTable.initializeFileTableGlobal();
        OnlineUserTable.initializeOnlineUserTableGlobal();
        UserTable.initializeUserTableGlobal();

        ChatroomTables chatroomInUser1 = new ChatroomTables(con, "user1");
        chatroomInUser1.makeTable();

        OnlineUserTable.onlineUserTableGlobal.dropTable("OnlineUserTable");
        OnlineUserTable.onlineUserTableGlobal.makeTable();

       //UserTable.userTableGlobal.signup("user1", "ddong", "01012345678", "20021001", "1234");
        //UserTable.userTableGlobal.unregister("user1");

        // below : just test code
       /* System.out.println(user1.signup("user1", "ddong", "01012345678", "20021001", "1234"));
        System.out.println(user1.updatePassword("user1", "1111", "1234"));
        System.out.println(user1.updateUserInfo("user1", "ddong", "20021001", "sss"));
        System.out.println(user1.getUserInfo("user"));
        //System.out.println(user1.findPW("user1", "01012345678"));
        System.out.println(user1.login("user1", "1233"));
        System.out.println(user1.logout("user1"));
        System.out.println(user1.login("user1", "1233"));*/
        //System.out.println(user)

        GagaoTalkServer.mainLoop();

    }
}
