package sfs2x.handler.zone;


import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.utils.SFSUtil;
import sfs2x.model.Table;

public class ZoneJoinedHandler extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        System.out.println("---------->用户加入到zone");
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        if (player.getAgentID() == 0)
            send("setAgent",null,user);
        if (SFSUtil.offlinePlayer.containsKey(player.getUserID())){
            Room room = SFSUtil.offlinePlayer.get(player.getUserID());
            if (room != null){
                Table table = (Table) room.getProperty(Global.TABLE);
                if (table != null){
                    Player p = table.getPlayer(player.getUserID());
                    if (p != null){
                        System.out.println("---------->断线重连");
                        p.setUser(user);
                        send("user",p.playerToSFSObject(),user);
                        user.getSession().setProperty(Global.PLAYER,p);
                        SFSUtil.waitTime(800);
                        getApi().joinRoom(user,room);
                        return;
                    }else {
                        SFSUtil.offlinePlayer.remove(player.getUserID());
                    }
                }else
                    SFSUtil.offlinePlayer.remove(player.getUserID());
            }else
                SFSUtil.offlinePlayer.remove(player.getUserID());
        }else {
            send("user",player.playerToSFSObject(),user);
        }
        player.setUser(user);
    }
}
