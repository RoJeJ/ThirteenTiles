package sfs2x.model.utils;

import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SFSUtil {

    public static Map<Integer,Room> offlinePlayer = new HashMap<Integer,Room>();
    public static ArrayList<Player> ranField = new ArrayList<Player>();

    public static void waitTime(long time){
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.interrupted();
        }
    }

    public static ISFSObject roomDetail(Room room,Player p){
        Table table = (Table) room.getProperty(Global.TABLE);
        SFSArray array = new SFSArray();
        for (Seat seat : table.getSeats()){
            Player player = seat.getPlayer();
            if (player != null) {
                ISFSObject data = player.playerToSFSObject();
                data.putInt("seat",seat.getNo());
                data.putBool("isMe", player == p);
                data.putBool("ready",player.getGameVar().isReady());
                data.putLong("score",player.getGameVar().getTotalScore());
                array.addSFSObject(data);
            }
        }
        ISFSObject object = new SFSObject();
        object.putSFSArray("users",array);
        object.putInt("count",table.getCount());
        object.putInt("person",table.getPerson());
        object.putBool("hong",table.isHong());
        object.putBool("aa",table.isAA());
        object.putInt("ma",table.getMa());
        object.putInt("curCount",table.getCurCount());
        object.putUtfString("name",room.getName());

        return object;
    }

    public static ISFSObject quitInfo(Table table){
        SFSArray array = new SFSArray();
        for (Seat seat : table.getSeats()){
            Player player = seat.getPlayer();
            if (player != null){
                ISFSObject p = player.playerToSFSObject();
                p.putInt("seat",seat.getNo());
                p.putInt("quit",player.getGameVar().getQuit());
                array.addSFSObject(p);
            }
        }
        ISFSObject object = new SFSObject();
        object.putSFSArray("quitinfo",array);
        return object;
    }

    public static ISFSObject gameCount(Table table){
        SFSArray array = new SFSArray();
        for (Seat seat : table.getSeats()){
            Player p = seat.getPlayer();
            if (p != null){
                ISFSObject po = p.playerToSFSObject();
                po.putInt("seat",seat.getNo());
                po.putLong("score",p.getGameVar().getTotalScore());
                po.putInt("win",p.getGameVar().getWinCount());
                po.putInt("lose",p.getGameVar().getLoseCount());
                po.putInt("draw",p.getGameVar().getDrawCount());
                array.addSFSObject(po);
            }
        }
        ISFSObject object = new SFSObject();
        object.putSFSArray("count",array);
        return object;
    }

    public static void RecordGame(Room room){
        Table table = (Table) room.getProperty(Global.TABLE);
        Connection connection = null;
        CallableStatement statement = null;
        try {
            connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
            statement = connection.prepareCall("{?=call dbo.GSP_GP_RecordGame(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}", ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            statement.registerOutParameter(1,Types.INTEGER);
            statement.setString(2,room.getName());
            if (table.getSeat(0) != null && table.getSeat(0).getPlayer() != null) {
                statement.setInt(3, table.getSeat(0).getPlayer().getUserID());
                statement.setString(4, table.getSeat(0).getPlayer().getName());
                statement.setLong(5,table.getSeat(0).getPlayer().getGameVar().getTotalScore());
            }else {
                statement.setInt(3,0);
                statement.setString(4,"");
                statement.setLong(5,0);
            }
            if (table.getSeat(1) != null && table.getSeat(1).getPlayer() != null) {
                statement.setInt(6, table.getSeat(1).getPlayer().getUserID());
                statement.setString(7, table.getSeat(1).getPlayer().getName());
                statement.setLong(8,table.getSeat(1).getPlayer().getGameVar().getTotalScore());
            }else {
                statement.setInt(6,0);
                statement.setString(7,"");
                statement.setLong(8,0);
            }
            if (table.getSeat(2) != null && table.getSeat(2).getPlayer() != null) {
                statement.setInt(9, table.getSeat(2).getPlayer().getUserID());
                statement.setString(10, table.getSeat(2).getPlayer().getName());
                statement.setLong(11,table.getSeat(2).getPlayer().getGameVar().getTotalScore());
            }else {
                statement.setInt(9,0);
                statement.setString(10,"");
                statement.setLong(11,0);
            }
            if (table.getSeat(3) != null && table.getSeat(3).getPlayer() != null) {
                statement.setInt(12, table.getSeat(3).getPlayer().getUserID());
                statement.setString(13, table.getSeat(3).getPlayer().getName());
                statement.setLong(14,table.getSeat(3).getPlayer().getGameVar().getTotalScore());
            }else {
                statement.setInt(12,0);
                statement.setString(13,"");
                statement.setLong(14,0);
            }
            if (table.getSeat(4) != null && table.getSeat(4).getPlayer() != null) {
                statement.setInt(15, table.getSeat(4).getPlayer().getUserID());
                statement.setString(16, table.getSeat(4).getPlayer().getName());
                statement.setLong(17,table.getSeat(4).getPlayer().getGameVar().getTotalScore());
            }else {
                statement.setInt(15,0);
                statement.setString(16,"");
                statement.setLong(17,0);
            }
            if (table.getSeat(5) != null && table.getSeat(5).getPlayer() != null) {
                statement.setInt(18, table.getSeat(5).getPlayer().getUserID());
                statement.setString(19, table.getSeat(5).getPlayer().getName());
                statement.setLong(20,table.getSeat(5).getPlayer().getGameVar().getTotalScore());
            }else {
                statement.setInt(18,0);
                statement.setString(19,"");
                statement.setLong(20,0);
            }

            statement.setInt(21,table.getCount());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            statement.setString(22,timestamp.toString());
            statement.execute();
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
                if (statement != null && !statement.isClosed())
                    statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static SFSObject getGameRecord(int GameID){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SFSArray array = new SFSArray();
        try {
            connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
            statement=connection.prepareStatement("SELECT * FROM RecordGame WHERE UserID1 = "+GameID+" OR UserID2 = "+GameID+" OR UserID3 = "+GameID+" OR UserID4 = "+GameID+" OR UserID5 = "+GameID+" OR UserID6 = "+GameID+" AND DateDiff(dd,Date,GetDate())=0 ORDER BY Date DESC " ,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                SFSObject object = new SFSObject();
                object.putInt("UserID1",resultSet.getInt("UserID1"));
                object.putLong("score1",resultSet.getLong("Score1"));
                object.putUtfString("name1",resultSet.getString("NickName1"));

                object.putInt("UserID2",resultSet.getInt("UserID2"));
                object.putLong("score2",resultSet.getLong("Score2"));
                object.putUtfString("name2",resultSet.getString("NickName2"));

                object.putInt("UserID3",resultSet.getInt("UserID3"));
                object.putLong("score3",resultSet.getLong("Score3"));
                object.putUtfString("name3",resultSet.getString("NickName3"));

                object.putInt("UserID4",resultSet.getInt("UserID4"));
                object.putLong("score4",resultSet.getLong("Score4"));
                object.putUtfString("name4",resultSet.getString("NickName4"));

                object.putInt("UserID5",resultSet.getInt("UserID5"));
                object.putLong("score5",resultSet.getLong("Score5"));
                object.putUtfString("name5",resultSet.getString("NickName5"));

                object.putInt("UserID6",resultSet.getInt("UserID6"));
                object.putLong("score6",resultSet.getLong("Score6"));
                object.putUtfString("name6",resultSet.getString("NickName6"));

                object.putUtfString("roomID",resultSet.getString("RoomID"));
                object.putInt("count",resultSet.getInt("Ju"));
                object.putUtfString("date",resultSet.getString("Date"));
                array.addSFSObject(object);
            }
            SFSObject object = new SFSObject();
            object.putSFSArray("record",array);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
                if (statement != null && !statement.isClosed())
                    statement.close();
                if (resultSet != null && !resultSet.isClosed())
                    resultSet.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        SFSObject object = new SFSObject();
        object.putSFSArray("record",array);
        return object;
    }

//    public static byte int2ByteForTile(int value){
//        switch (value/4){
//            case 0:
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//            case 6:
//            case 7:
//            case 8:
//            case 9:
//            case 10:
//            case 11:
//                switch (value % 4){
//                    case 0:
//                        return (byte) (0x30+(byte)(value/4+2));
//                    case 1:
//                        return (byte) (0x20+(value/4+2));
//                    case 2:
//                        return (byte) (0x10+(value/4+2));
//                    case 3:
//                        return (byte) (value / 4 + 2);
//                }
//                break;
//            case 12:
//                switch (value % 4){
//                    case 0:
//                        return (byte) (0x30+1);
//                    case 1:
//                        return (byte) (0x20+1);
//                    case 2:
//                        return (byte) (0x10+1);
//                    case 3:
//                        return (byte) (0x1);
//                }
//                break;
//        }
//        return 0;
//    }
//
//    public static int byte2IntForTile(byte value){
//        return (GameLogic.GetCardLogicValue(value)-2)*4+(3-GameLogic.GetCardColor(value));
//    }
}
