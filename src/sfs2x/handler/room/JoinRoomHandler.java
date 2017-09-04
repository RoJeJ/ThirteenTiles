package sfs2x.handler.room;


import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.extensions.GameExtension;
import sfs2x.logic.MainGame;
import sfs2x.model.*;
import sfs2x.model.utils.SFSUtil;


public class JoinRoomHandler extends BaseServerEventHandler {
    @Override
    public synchronized void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        Room room = getParentExtension().getParentRoom();

        Table table = (Table) room.getProperty(Global.TABLE);

        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        int seatNo = table.getSeatNo(player);
        if (seatNo == -1){
//            System.out.println("---------->用户加入房间");
            Seat seat = table.getEmptySeat();
            if (seat != null) {
                if (table.getPersonCount() == 0) {
                    table.setOwner(player);
                    send("owner",null,user);
                }
                GameVariable var = new GameVariable(table);
                player.setGameVar(var);
                seat.setPlayer(player);
                send("users", SFSUtil.roomDetail(room,player),user);

                ISFSObject object = player.playerToSFSObject();
                object.putInt("seat",table.getSeatNo(player));
                for (Seat s : table.getSeats()){
                    Player p = s.getPlayer();
                    if (p != null && p != player && p.getUser() != null){
                        send("userJoin",object, p.getUser());
                    }
                }
            }else {
                getApi().leaveRoom(user, room);
            }
        }else {
            SFSUtil.offlinePlayer.remove(player.getUserID());
//            System.out.println("---------->用户重新连接");
            send("users",SFSUtil.roomDetail(room,player),user);

            ISFSObject object = player.playerToSFSObject();
            object.putInt("seat",seatNo);
            for (Seat s : table.getSeats()){
                Player p = s.getPlayer();
                if (p != null && p != player && p.getUser() != null){
                    send("online",object,p.getUser());
                }
            }

            MainGame mainGame= ((GameExtension)getParentExtension()).getMainGame();
            mainGame.recoverGame(player);
        }
    }
}
