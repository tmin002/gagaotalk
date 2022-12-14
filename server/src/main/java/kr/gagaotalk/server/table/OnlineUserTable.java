package kr.gagaotalk.server.table;

import kr.gagaotalk.server.DatabaseEG;
import kr.gagaotalk.server.ErrorInProcessingException;

import java.security.SecureRandom;
import java.sql.Connection;
import java.util.Date;

public class OnlineUserTable extends Table {
    public OnlineUserTable(Connection con) { super(con); }
    public OnlineUserTable(Connection con, String tableName) { super(con, tableName, schema, database); }

    public static OnlineUserTable onlineUserTableGlobal;
    public static void initializeOnlineUserTableGlobal() {
       onlineUserTableGlobal = new OnlineUserTable(DatabaseEG.con, "OnlineUserTable");
       onlineUserTableGlobal.makeTable();
    }

    public static String schema = "userID varchar(32) not null, sessionID varchar(33) not null, primary key(userID)";
    public static String database = "gagaotalkDB";

    public boolean doesExistSessionID(String sessionID) {
        StringBuilder t = executeQuery("select exists (select * from " + tableName + " where sessionID = '" + sessionID + "') as success;", 1);
        String tt = t.toString().trim();
        return tt.equals("1");
    }
    private String getRandomSessionID() {
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };

        StringBuilder sb;
        do {
            sb = new StringBuilder();
            SecureRandom sr = new SecureRandom();
            sr.setSeed(new Date().getTime());

            int idx = 0;
            int len = charSet.length;
            for (int i = 0; i < 32; i++) {
                // idx = (int) (len * Math.random());
                idx = sr.nextInt(len);
                sb.append(charSet[idx]);
            }
        } while (doesExistSessionID(sb.toString()));


        return sb.toString();
    }

    public boolean isOnline(String userID) {
        StringBuilder t = executeQuery("select exists (select * from " + tableName + " where userid = '" + userID + "') as success;", 1);
        String tt = t.toString().trim();
        return tt.equals("1");
    }

    public String getUserIDInOnlineTable(String sessionID) throws ErrorInProcessingException {
        if(doesExistSessionID(sessionID)) {
            StringBuilder userID = executeQuery("select userID from " + tableName + " where sessionID = '" + sessionID + "';", 1);
            return userID.toString().trim();
        }
        else throw new ErrorInProcessingException(1);
    }

    public String insertOnlineTableLoginUser(String userID) throws ErrorInProcessingException {
        if(!isOnline(userID)) {
            String sessionID = getRandomSessionID();
            executeUpdate("insert into " + tableName + " values ('" + userID + "', '" + sessionID + "');");
            return sessionID;
        }
        throw new ErrorInProcessingException(2, "already login");
    }

    public void deleteOnlineTableLogoutUser(String userID) {
        if(isOnline(userID)) {
            executeUpdate("delete from " + tableName + " where userID = '" + userID + "';");
        }
    }


}