package sfs2x.handler.zone;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.*;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.security.DefaultPermissionProfile;
import sfs2x.model.Player;
import sfs2x.model.utils.DBUtil;

import java.sql.*;

public class LoginZoneHandler extends BaseServerEventHandler {
    @Override
    public synchronized void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        System.out.println("---------->用户开始登录zone");
        String username = (String)isfsEvent.getParameter(SFSEventParam.LOGIN_NAME);
        String password = (String)isfsEvent.getParameter(SFSEventParam.LOGIN_PASSWORD);
        ISFSObject data = (ISFSObject)isfsEvent.getParameter(SFSEventParam.LOGIN_IN_DATA);
        String nickname = data.getUtfString("nickname");
        int sex = data.getInt("sex");
        String faceUrl = data.getUtfString("headimgurl");
        ISession session = (ISession)isfsEvent.getParameter(SFSEventParam.SESSION);
        String ip = session.getAddress();

        Connection con = null;
        PreparedStatement stm = null;
        try
        {
            if ((getApi().checkSecurePassword(session, "ll19891735", password)) &&
                    (!isExistAccount(username))) {
                addReg(username, nickname, sex, faceUrl, ip);
            }
            con = DBUtil.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ThirteenTilesDB");
            if (con == null)
            {
                SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
                errData.addParameter("SQL Error: missing connection");
                IErrorCode nCode = new IErrorCode()
                {
                    public short getId()
                    {
                        return 999;
                    }
                };
                errData.setCode(nCode);
                throw new SFSLoginException("A SQL Error occurred: missing connection", errData);
            }
            stm = con.prepareStatement("SELECT Nullity FROM UserInfo WHERE Accounts = '" + username + "' COLLATE Chinese_PRC_CS_AI_WS", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet = stm.executeQuery();
            if ((resultSet.next()) && (resultSet.getInt(1) == 1))
            {
                SFSErrorData errorData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
                errorData.addParameter("用户被禁止登录!");
                IErrorCode nCode = new IErrorCode()
                {
                    public short getId()
                    {
                        return 998;
                    }
                };
                errorData.setCode(nCode);
                throw new SFSLoginException("login error", errorData);
            }
            resultSet.close();
            stm = con.prepareStatement("UPDATE UserInfo SET NickName = '" + nickname + "',Gender = " + sex + ",FaceUrl = '" + faceUrl + "',IP = '" + ip + "',LastLogonDate = '" + new Timestamp(System.currentTimeMillis()) + "' WHERE Accounts = '" + username + "'");

            stm.execute();

            stm = con.prepareStatement("SELECT * FROM UserInfo WHERE Accounts = '" + username + "' COLLATE Chinese_PRC_CS_AI_WS", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (stm.execute())
            {
                ResultSet rs = stm.getResultSet();
                if (rs.next())
                {
                    Player player = new Player();
                    player.setUserID(rs.getInt("UserID"));

                    player.setDiamond(rs.getLong("Diamond"));
                    player.setGameCard(rs.getLong("GameCard"));
                    player.setScore(rs.getLong("Score"));
                    player.setFaceUrl(faceUrl);
                    player.setIp(ip);
                    player.setGender(rs.getInt("Gender"));
                    player.setName(rs.getString("NickName"));
                    session.setProperty("player", player);
                    session.setProperty("$permission", DefaultPermissionProfile.STANDARD);
                    rs.close(); return;
                }
                throw new SFSLoginException("a sql error occurred", new SFSErrorData(SFSErrorCode.GENERIC_ERROR));
            }
            throw new SFSLoginException("a sql error occurred", new SFSErrorData(SFSErrorCode.GENERIC_ERROR));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
            errData.addParameter("SQL Error: " + e.getMessage());
            IErrorCode nCode = new IErrorCode()
            {
                public short getId()
                {
                    return 999;
                }
            };
            errData.setCode(nCode);
            throw new SFSLoginException("A SQL Error occurred: " + e.getMessage(), errData);
        }
        finally
        {
            try
            {
                if ((con != null) && (!con.isClosed())) {
                    con.close();
                }
                if ((stm != null) && (!stm.isClosed())) {
                    stm.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void addReg(String username, String nickname, int sex, String faceurl, String ip)
            throws SFSLoginException
    {
        PreparedStatement stm = null;
        Connection con = DBUtil.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ThirteenTilesDB");
        if (con != null) {
            try
            {
                stm = con.prepareStatement("INSERT INTO UserInfo (Accounts, NickName, Gender,FaceUrl, IP, Nullity, MemberOrder, GameCard, Diamond, Score, WinCount, LostCount, DrawCount, FleeCount,RegisterDate,ShareDate, LastLogonDate, LastLogoutDate) VALUES ('" + username + "','" + nickname + "','" + sex + "','" + faceurl + "','" + ip + "',0,0," + 8L + ",0,0,0,0,0,0,'" + new Timestamp(System.currentTimeMillis()).toString() + "',NULL ,NULL ,NULL )");

                stm.execute();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                SFSErrorData errorData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
                throw new SFSLoginException("GENERIC_ERROR", errorData);
            }
            finally
            {
                try
                {
                    con.close();
                    if (stm != null) {
                        stm.close();
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isExistAccount(String username)
            throws SFSLoginException
    {
        PreparedStatement statement = null;
        ResultSet rs = null;
        Connection con = DBUtil.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ThirteenTilesDB");
        if (con != null) {
            try
            {
                statement = con.prepareCall("SELECT UserID FROM UserInfo WHERE Accounts = '" + username + "' COLLATE Chinese_PRC_CS_AI_WS", 1005, 1007);

                rs = statement.executeQuery();
                return rs.first();
            }
            catch (SQLException e)
            {
                SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
                errData.addParameter("SQL Error: " + e.getMessage());
                IErrorCode nCode = new IErrorCode()
                {
                    public short getId()
                    {
                        return 999;
                    }
                };
                errData.setCode(nCode);
                throw new SFSLoginException("A SQL Error occurred: " + e.getMessage(), errData);
            }
            finally
            {
                try
                {
                    con.close();
                    if (statement != null) {
                        statement.close();
                    }
                    if ((rs != null) && (!rs.isClosed())) {
                        rs.close();
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
        errData.addParameter("SQL Error: missing connection");
        IErrorCode nCode = new IErrorCode()
        {
            public short getId()
            {
                return 999;
            }
        };
        errData.setCode(nCode);
        throw new SFSLoginException("A SQL Error occurred: missing connection", errData);
    }
}

