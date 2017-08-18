package sfs2x.handler.room;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.extensions.GameExtension;
import sfs2x.logic.MainGame;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.Table;
import sfs2x.model.utils.SFSUtil;

public class LeaveRoomHandler extends BaseServerEventHandler{
    @Override
    public synchronized void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        GameExtension gameExt = (GameExtension) getParentExtension();
        Room room = gameExt.getParentRoom();
        Table table = (Table) room.getProperty(Global.TABLE);

        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);

        if (table.isGameStarted()){
            SFSUtil.offlinePlayer.put(player.getUserID(),room);
            System.out.println("游戏中,玩家断线");
            ISFSObject object = new SFSObject();
            object.putInt("seat",table.getSeatNo(player));
            send("offline",object,room.getUserList());
        }else {
            int seatNo = table.getSeatNo(player);
            table.leaveSeat(player);
            if (table.getOwner() == player)
                getApi().removeRoom(room);
            else {
                ISFSObject object = new SFSObject();
                object.putInt("seat",seatNo);
                send("exit",object,room.getUserList());
            }
        }
    }
}
