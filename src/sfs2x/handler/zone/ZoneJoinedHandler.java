package sfs2x.handler.zone;


import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.Table;
import sfs2x.model.utils.SFSUtil;

public class ZoneJoinedHandler extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
//        System.out.println("---------->用户加入到zone");
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        int agenid = (int) user.getSession().getProperty("agentid");
        int userid = (int) user.getSession().getProperty("userid");
        if (agenid == 0)
            send("setAgent",null,user);
        if (SFSUtil.offlinePlayer.containsKey(userid)){
            Room room = SFSUtil.offlinePlayer.get(userid);
            if (room != null){
                Table table = (Table) room.getProperty(Global.TABLE);
                if (table != null){
                    Player p = table.getPlayer(userid);
                    if (p != null && p.getUser() == null){
//                        System.out.println("---------->断线重连");
                        p.setUser(user);
                        send("user",p.playerToSFSObject(),user);
                        user.getSession().setProperty(Global.PLAYER,p);
                        SFSUtil.waitTime(200);
                        getApi().joinRoom(user,room);
                        return;
                    }else {
                        SFSUtil.offlinePlayer.remove(userid);
                    }
                }else
                    SFSUtil.offlinePlayer.remove(userid);
            }else
                SFSUtil.offlinePlayer.remove(userid);
        }
        String nickname = (String) user.getSession().getProperty("nickname");
        int sex = (int) user.getSession().getProperty("sex");
        String faceurl = (String) user.getSession().getProperty("faceurl");
        String ip = (String) user.getSession().getProperty("ip");
        long score = (long) user.getSession().getProperty("score");
        long card = (long) user.getSession().getProperty("card");
        long diamond = (long) user.getSession().getProperty("diamond");

        Player player = new Player();
        user.getSession().setProperty(Global.PLAYER,player);
        player.setUserID(userid);
        if (player.getAgentID() == 0)
            player.setAgentID(agenid);
        player.setName(nickname);
        player.setGender(sex);
        player.setFaceUrl(faceurl);
        player.setIp(ip);
        player.setScore(score);
        player.setGameCard(card);
        player.setDiamond(diamond);
        player.setUser(user);

        send("user",player.playerToSFSObject(),user);
    }
}
