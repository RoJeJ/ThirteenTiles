package sfs2x.model.utils;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.model.Player;

import java.sql.*;

public class DBUtil {
    public static final String ThirteenTilesDB = "jdbc:sqlserver://localhost:1433;databaseName=ThirteenTilesDB";



    public static Connection getConnection(String dbUrl){
        Connection connection;
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String userName = "sa";
        String password = "A@b1D38DfBHm9K$%wR";
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(dbUrl, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
            connection = null;
        }
        return connection;
    }

    public static void setLoginInFlag(int userid,int flag){
        Connection connection = getConnection(ThirteenTilesDB);
        PreparedStatement statement = null;
        try {
            if (flag == 1) {
                statement = connection.prepareStatement("UPDATE UserInfo SET loggedIn = 1,LastLogonDate = getdate() WHERE loggedIn = 0 AND userid = " + userid);
                statement.executeUpdate();
            }else if (flag == 0){
                statement = connection.prepareStatement("UPDATE UserInfo SET loggedIn = 0,LastLogoutDate = getdate() WHERE loggedIn = 1 AND userid = " + userid);
                statement.executeUpdate();
            }
        }catch (SQLException e){
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


    //查询房卡和钻石
    public static void queryGameCardAndDiamond(SFSExtension extension,Player player) {
        try {
            Connection connection = DBUtil.getConnection(ThirteenTilesDB);
            PreparedStatement statement = connection.prepareStatement("SELECT GameCard,Diamond FROM UserInfo WHERE userid = " + player.getUserID(),
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                player.setGameCard(rs.getLong(1));
                player.setDiamond(rs.getLong(2));
            }
            SFSObject object = new SFSObject();
            object.putLong("card", player.getGameCard());
            if (player.getUser() != null)
                extension.send("cardUpdate", object, player.getUser());

            object = new SFSObject();
            object.putLong("dia", player.getDiamond());
            if (player.getUser() != null)
                extension.send("diaUpdate", object, player.getUser());
            connection.close();
            statement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkV(int userID) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            boolean b = false;
            connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
            statement= connection.prepareStatement("SELECT * FROM xy_user_cards WHERE userid = "+userID,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                b = true;
            rs.close();
            return b;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
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

    //抽奖扣除钻石
    public static boolean draw(SFSExtension extension,Player player){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
            statement = connection.prepareStatement("UPDATE  UserInfo SET Diamond = Diamond - 18 WHERE userid = "+player.getUserID()+" AND Diamond >= 18");
            int n = statement.executeUpdate();
            if (n == 1){
                queryGameCardAndDiamond(extension,player);
                return true;
            }
            return false;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
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

    //兑奖
    public static boolean  cashLottery(int n,int userid){
        if (n == 0)
            return true;
        if (n < 0 || n > 10)
            return false;
        Connection connection = getConnection(ThirteenTilesDB);
        PreparedStatement stmt = null;
        try {
            if (n == 1){
                stmt = connection.prepareStatement("UPDATE UserInfo SET GameCard = GameCard + 1 WHERE userid = "+userid);
            }else if (n == 2){
                stmt = connection.prepareStatement("UPDATE UserInfo SET GameCard = GameCard + 2 WHERE userid = "+userid);
            }else if (n == 3){
                stmt = connection.prepareStatement("UPDATE UserInfo SET GameCard = GameCard + 3 WHERE userid = "+userid);
            }else if (n == 4){
                stmt = connection.prepareStatement("UPDATE UserInfo SET GameCard = GameCard + 5 WHERE userid = "+userid);
            }else if (n == 5){
                stmt = connection.prepareStatement("UPDATE UserInfo SET GameCard = GameCard + 10 WHERE userid = "+userid);
            }else if (n == 6){
                stmt = connection.prepareStatement("UPDATE UserInfo SET Diamond = UserInfo.Diamond + 28 WHERE userid = "+userid);
            }else if (n == 7){
                stmt = connection.prepareStatement("UPDATE UserInfo SET Diamond = UserInfo.Diamond + 48 WHERE userid = "+userid);
            }else if (n == 8){
                stmt = connection.prepareStatement("UPDATE UserInfo SET Diamond = UserInfo.Diamond + 88 WHERE userid = "+userid);
            }else if (n == 9){
                stmt = connection.prepareStatement("UPDATE UserInfo SET Diamond = UserInfo.Diamond + 280 WHERE userid = "+userid);
            }else {
                stmt = connection.prepareStatement("UPDATE UserInfo SET Diamond = UserInfo.Diamond + 480 WHERE userid = "+userid);
            }
            return stmt.executeUpdate() == 1;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
                if (stmt != null && !stmt.isClosed())
                    stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static ISFSObject setAgent(Player player, int agentID) {
        ISFSObject object = new SFSObject();
        Connection connection = getConnection(ThirteenTilesDB);
        PreparedStatement statement = null;
        try {
             statement = connection.prepareStatement("SELECT id FROM xy_agent WHERE snid = "+agentID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                int id = rs.getInt(1);
                statement.close();
                statement = connection.prepareStatement("UPDATE UserInfo SET ParentID = "+id+" WHERE ParentID = 0 AND userid = "+player.getUserID());
                if (statement.executeUpdate() == 1) {
                    player.setAgentID(agentID);
                    object.putBool("code",true);
                    return object;
                }
            }
            object.putBool("code",false);
            return object;
        }catch (SQLException e){
            object.putBool("code",false);
            return object;
        }finally {
            try {
                connection.close();
                if (statement != null)
                    statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    //设置离线
    public static void setOffline(int userid){
        Connection connection = getConnection(ThirteenTilesDB);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("UPDATE UserInfo SET LastLogoutDate = '"+new Timestamp(System.currentTimeMillis())+"' WHERE userid = "+userid);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                if (!connection.isClosed())
                    connection.close();
                if (statement != null && !statement.isClosed())
                    statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
