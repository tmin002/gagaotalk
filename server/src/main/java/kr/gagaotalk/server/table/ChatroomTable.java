package kr.gagaotalk.server.table;

import kr.gagaotalk.server.DatabaseEG;
import kr.gagaotalk.server.ErrorInProcessingException;

import java.io.*;
import java.security.SecureRandom;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// The table with entire chatrooms in **server**
// NOTE: chatroomID is int
public class ChatroomTable extends Table {
    public ChatroomTable(Connection con) { super(con); }
    public ChatroomTable(Connection con, String tableName) { super(con, tableName, schema, database); }

    public static ChatroomTable chatroomTableGlobal;
    public static void initializeChatroomTableGlobal() {
        chatroomTableGlobal = new ChatroomTable(DatabaseEG.con, "ChatroomTable");
        chatroomTableGlobal.makeTable();
    }

    private final int fileIDLength = 8;

    public static String schema = "chatroomID varchar(16) not null, chatroomName varchar(32) not null, contentAddress varchar(32) not null, primary key(chatroomID)";
    public static String database = "gagaotalkDB";

    public boolean doesExistChatID(String chatroomID) {
        StringBuilder checkExists = executeQuery("select exists (select from " + tableName + " where chatroomID = '" + chatroomID + "') as success;", 1);
        return checkExists.toString().trim().equals("1");
    }

    private String getRandomChatroomID() {
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        };

        StringBuilder sb;
        do {
            sb = new StringBuilder();
            SecureRandom sr = new SecureRandom();
            sr.setSeed(new Date().getTime());

            int idx = 0;
            int len = charSet.length;
            for (int i = 0; i < fileIDLength; i++) {
                // idx = (int) (len * Math.random());
                idx = sr.nextInt(len);
                sb.append(charSet[idx]);
            }
        } while (doesExistChatID(sb.toString()));
        return sb.toString();
    }

    public int getNumberOfParticipants(String chatroomID) {
        ParticipantsTables participantsTables = new ParticipantsTables(con, chatroomID);
        return participantsTables.getNumberOfParticipants();
    }

    //non-finished
    //mkCtRm
    public String createChatroom(ArrayList<String> participants, String chatroomName) {
        //if(chatroomName.length() > 16)  limit word length
        String chatroomID = getRandomChatroomID();
        String contentAddress = "./database/chatrooms/" + chatroomID + ".txt";
        File chatroomContentFile = new File(contentAddress);

        ParticipantsTables participantsTable = new ParticipantsTables(con, chatroomID);
        participantsTable.addUsersToChatroom(participants);

        try {
            chatroomContentFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        executeUpdate("insert into " + tableName + " values ('" + chatroomID + "', '" + chatroomName + "', '" + contentAddress + "');");
        return "";
    }

    //non-finished
    //addCtRm
    public String inviteUsersToChatroom(ArrayList<String> participants, String chatroomID) {
        ParticipantsTables participantsTables = new ParticipantsTables(con, chatroomID);
        participantsTables.addUsersToChatroom(participants);
        return "";
    }

    //non-finished
    public String leaveChatroom(String sessionID, String chatroomID) throws ErrorInProcessingException {
        String myUserID = OnlineUserTable.onlineUserTableGlobal.getUserIDInOnlineTable(sessionID);
        ParticipantsTables participantsTables = new ParticipantsTables(con, chatroomID);
        participantsTables.deleteUserFromChatroom(myUserID);
        ChatroomTables chatroomInUser = new ChatroomTables(con, myUserID);
        chatroomInUser.deleteChatroomInUser(chatroomID);
        return "";
    }


    //non-finished
    // ** NOTE : delimiter is ',' in a line
    public String sendMessageText(String chatroomID, String type, String content, String userID) {
        StringBuilder chatroomContentAddress = executeQuery("select contentAddress from " + tableName + " where chatroomID = " + chatroomID + ";", 1);
        File chatroomContentFile = new File(chatroomContentAddress.toString());
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String currentDate = formatter.format(date).toString();

            BufferedWriter bw = new BufferedWriter(new FileWriter(chatroomContentFile, true));
            // *** text format : userID, type, date and time, content
            bw.write(userID + "," + type + ","  + currentDate + "," + content + '\n');

            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public String sendMessageFile(String chatroomID, String type, String fileName, String fileID, String userID) {
        StringBuilder chatroomContentAddress = executeQuery("select contentAddress from " + tableName + " where chatroomID = " + chatroomID + ";", 1);
        File chatroomContentFile = new File(chatroomContentAddress.toString());
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String currentDate = formatter.format(date).toString();

            BufferedWriter bw = new BufferedWriter(new FileWriter(chatroomContentFile, true));
            // *** file format : userID, type, date and time, fileName, fileID
            bw.write(userID + "," + type + ","  + currentDate + "," + fileName + "," + fileID + "\n");

            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    // need to extract only the message part
    public StringBuilder getMessage(String chatroomID) {
        StringBuilder contentsAddress = executeQuery("select contentAddress from " + tableName + " where id = " + chatroomID + ";", 1);
        StringBuilder content = new StringBuilder("");
        try {
            File addressFile = new File(contentsAddress.toString());
            BufferedReader reader = new BufferedReader(new FileReader(addressFile));

            String line = "";
            while((line = reader.readLine()) != null) {
                content.append(line + "\n");

                reader.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public String getChatroomName(String chatroomID) {
        StringBuilder chatroomName = executeQuery("select chatroomName from " + tableName + " where chatroomName = '" + chatroomID + "';", 1);
        return chatroomName.toString();
    }


}
