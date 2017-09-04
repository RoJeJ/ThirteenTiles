package sfs2x.handler.room;

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
import sfs2x.model.Table;
import sfs2x.model.utils.SFSUtil;

public class DisconnectInRoom extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        Room room = getParentExtension().getParentRoom();
        Table table = (Table) room.getProperty(Global.TABLE);
        if (table.getPlayer(player.getUserID()) == player){
//            trace("玩家("+player.getUserID()+")断线");
            SFSUtil.offlinePlayer.put(player.getUserID(),room);
            ISFSObject object = new SFSObject();
            object.putInt("seat",table.getSeatNo(player));
            send("offline",object,room.getUserList());
        }
    }
}
