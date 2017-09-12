package sfs2x.handler.zone;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSConstants;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.*;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.security.DefaultPermissionProfile;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import sfs2x.extensions.SanExtension;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.utils.DBUtil;

import java.awt.geom.FlatteningPathIterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.*;

public class LoginZoneHandler extends BaseServerEventHandler {
    @Override
    public synchronized void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
//        System.out.println("---------->用户开始登录zone");
        String openid = (String)isfsEvent.getParameter(SFSEventParam.LOGIN_NAME);
        String password = (String)isfsEvent.getParameter(SFSEventParam.LOGIN_PASSWORD);
        ISFSObject data = (ISFSObject)isfsEvent.getParameter(SFSEventParam.LOGIN_IN_DATA);
//        System.out.println("openid:"+openid);
        String token = data.getUtfString("token");
//        System.out.println("token:"+token);
        ISession session = (ISession)isfsEvent.getParameter(SFSEventParam.SESSION);
        String ip = session.getAddress().trim();
        String url = String.format(Global.USERINFO_URI,token,openid);
        ISFSObject outData = (ISFSObject) isfsEvent.getParameter(SFSEventParam.LOGIN_OUT_DATA);

        Connection con = DBUtil.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ThirteenTilesDB");
        CallableStatement stm = null;
        try {
            if ((getApi().checkSecurePassword(session, "ll19891735", password))) {
                HttpGet get = new HttpGet(URI.create(url));
                HttpResponse response = SanExtension.httpClient.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                        builder.append(temp);
                    }
                    get.abort();
                    JSONObject jsonObject = new JSONObject(builder.toString().trim());
                    if (jsonObject.has("errcode")) {
                        SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
                        errData.addParameter("token_expire");
                        throw new SFSLoginException("token_expire", errData);
                    } else {
                        openid = jsonObject.getString("openid").trim();
                        String nickname = jsonObject.getString("nickname").trim();
                        int sex = jsonObject.getInt("sex");
                        String language = jsonObject.getString("language").trim();
                        String city = jsonObject.getString("city").trim();
                        String province = jsonObject.getString("province").trim();
                        String country = jsonObject.getString("country").trim();
                        String headimgurl = jsonObject.getString("headimgurl").trim();
                        String unionid = jsonObject.getString("unionid").trim();

                        stm = con.prepareCall("{? = call UpdateAccounts (?,?,?,?,?,?,?,?,?,?)}");
                        stm.registerOutParameter(1, Types.INTEGER);
                        stm.setString(2, openid);
                        stm.setString(3, nickname);
                        stm.setInt(4, sex);
                        stm.setString(5, country);
                        stm.setString(6, province);
                        stm.setString(7, city);
                        stm.setString(8, language);
                        stm.setString(9, headimgurl);
                        stm.setString(10, unionid);
                        stm.setString(11, ip);

                        if (stm.execute()) {
                            ResultSet resultSet = stm.getResultSet();
                            if (resultSet.next()) {
                                int nullity = resultSet.getInt("nullity");
                                int loggedIn = resultSet.getInt("loggedIn");
                                if (nullity != 0){
                                    SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BANNED_USER);
                                    errData.addParameter("用户被禁止登录");
                                    throw new SFSLoginException("login refuse",errData);
                                }
                                if (loggedIn != 0){
                                    SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_ALREADY_LOGGED);
                                    errData.addParameter("已经登录");
                                    throw new SFSLoginException("login refuse",errData);
                                }
//                                    Player player = new Player();
//                                    player.setUserID(resultSet.getInt("userid"));
//                                    player.setAgentID(resultSet.getInt("AgentID"));
//                                    player.setDiamond(resultSet.getLong("Diamond"));
//                                    player.setGameCard(resultSet.getLong("card"));
//                                    player.setScore(resultSet.getLong("score"));
//                                    player.setFaceUrl(headimgurl);
//                                    player.setIp(ip);
//                                    player.setGender(resultSet.getInt("sex"));
//                                    player.setName(resultSet.getString("nickname"));

                                int userid = resultSet.getInt("userid");
                                session.setProperty("userid",userid);
                                session.setProperty("nickname",resultSet.getString("nickname"));
                                session.setProperty("sex",resultSet.getInt("sex"));
                                session.setProperty("faceurl",resultSet.getString("faceurl"));
                                session.setProperty("ip",ip);
                                session.setProperty("score",resultSet.getLong("score"));
                                session.setProperty("card",resultSet.getLong("card"));
                                session.setProperty("diamond",resultSet.getLong("Diamond"));
                                session.setProperty("agentid",resultSet.getInt("AgentID"));

//                                    session.setProperty(Global.PLAYER, player);
                                session.setProperty("$permission", DefaultPermissionProfile.STANDARD);

                                DBUtil.setLoginInFlag(userid, 1);
                                resultSet.close();
                                return;
                            }
                        }
                    }
                }
            }
            SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
            errData.addParameter("登录失败");
            throw new SFSLoginException("login error",errData);
        } catch (Exception e) {
            if (e instanceof SFSLoginException){
                throw (SFSLoginException)e;
            }
            e.printStackTrace();
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
}

