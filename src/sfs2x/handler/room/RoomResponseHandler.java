package sfs2x.handler.room;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.extensions.GameExtension;
import sfs2x.logic.MainGame;
import sfs2x.model.*;
import sfs2x.model.utils.SFSUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@MultiHandler
public class RoomResponseHandler extends BaseClientRequestHandler{
    @Override
    public synchronized void handleClientRequest(User user, ISFSObject isfsObject) {
        final GameExtension gameExt = (GameExtension) getParentExtension();
        MainGame mainGame = gameExt.getMainGame();
        final Room room = gameExt.getParentRoom();
        final Table table = (Table) room.getProperty(Global.TABLE);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        String cmd = isfsObject.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);

        if (cmd.equals("set")){
            if (player.getGameVar().getSortPai() != 1)
                return;
            boolean gaff = isfsObject.getBool("gaff");
            if (gaff){
                if (player.getGameVar().getPreSpeType().type == PaiType.CT_INVALID){
                    System.out.println("设置特殊牌失败");
                    ISFSObject object =new SFSObject();
                    object.putBool("set",false);
                    send("set",object,user);
                }else {
                   player.getGameVar().setPaiType(0,player.getGameVar().getPreSpeType());
                   player.getGameVar().setSortPai(2);
                   ISFSObject object = new SFSObject();
                   object.putBool("set",true);
                   object.putInt("seat",table.getSeatNo(player));
                   send("set",object,room.getUserList());
                }
            }else {
                ArrayList<Integer> begin = (ArrayList<Integer>) isfsObject.getIntArray("head");
                ArrayList<Integer> middle = (ArrayList<Integer>) isfsObject.getIntArray("middle");
                ArrayList<Integer> end = (ArrayList<Integer>) isfsObject.getIntArray("tail");
                ISFSObject object = mainGame.setHandCard(player, begin, middle, end);
                object.putInt("seat",table.getSeatNo(player));
                if (object.getInt("result") == 0) {
                    send("set", object, room.getUserList());
                }else
                    send("set",object,user);
            }
        }else if ("quit".equals(cmd)){
            if (!table.isGameStarted()){
                getApi().leaveRoom(user,room);
            }else {
                if (gameExt.isExitFlag()){
                    if (player.getGameVar().getQuit() == 1){
                        boolean b = isfsObject.getBool("quit");
                        if (b) {
                            player.getGameVar().setQuit(2);

                        }else {
                            player.getGameVar().setQuit(-1);
                        }
                    }
                    ISFSObject object = SFSUtil.quitInfo(table);
                    object.putUtfString("proposer",gameExt.getProposer().getName());
                    send("quit",object,room.getUserList());
                    int n = 0;
                    int m = 0;
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if (p != null && p.getGameVar().getQuit() == 2)
                            n++;
                        if (p != null && p.getGameVar().getQuit() == -1)
                            m++;
                    }
                    if (m > 0) {
                        for (Seat seat : table.getSeats()) {
                            Player p = seat.getPlayer();
                            if (p != null)
                                p.getGameVar().setQuit(0);
                        }
                        gameExt.setExitFlag(false);
                        gameExt.cancelExitTimer();
                        send("quitCancel", null, room.getUserList());
                        return;
                    }
                    if (n == table.getPerson()){
                        gameExt.setExitFlag(false);
                        gameExt.cancelExitTimer();
                        SFSUtil.RecordGame(room);
                        send("count",SFSUtil.gameCount(table),room.getUserList());
                        getApi().removeRoom(room);
                    }
                }else {
                    gameExt.setExitFlag(true);
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if ( p!=null)
                            p.getGameVar().setQuit(1);
                    }
                    player.getGameVar().setQuit(2);
                    gameExt.setProposer(player);
                    ISFSObject object = SFSUtil.quitInfo(table);
                    object.putUtfString("proposer",gameExt.getProposer().getName());
                    send("quit",object,room.getUserList());

                    gameExt.getExitTimer().schedule(gameExt.getExitTask(),Global.EXIT_TIME);
                }
            }
        }else if (cmd.equals("msg")){
            System.out.println("收到消息");
            getApi().sendPublicMessage(room,user,"msg",isfsObject);
        }else if (cmd.equals("ready")){
            if (!player.getGameVar().isReady()) {
                player.getGameVar().setReady(true);
                ISFSObject object = new SFSObject();
                object.putBool("ready",true);
                object.putInt("seat",table.getSeatNo(player));
                send("ready",object,room.getUserList());
            }
        }
//        else if (cmd.equals("quicksort")){
//            if (player.getGameVar().getPreSpeType() !=PaiType.none){
//                ISFSObject object = new SFSObject();
//                object.putBool("spec",true);
//                send("quicksort",object,user);
//            }else {
//                byte[] b = new byte[player.getGameVar().getHandCard().size()];
//                for (int i = 0; i < b.length; i++)
//                    b[i] = SFSUtil.int2ByteForTile(player.getGameVar().getHandCard().get(i));
//                GameLogic.tagAnalyseType type = GameLogic.GetType(b, (byte) b.length);
//                byte[] front = new byte[3];
//                byte[] mid = new byte[5];
//                byte[] back = new byte[5];
//                GameLogic.TheBestCard(type, b, (byte) b.length, front, mid, back);
//                ArrayList<Integer> f = new ArrayList<Integer>();
//                ArrayList<Integer> m = new ArrayList<Integer>();
//                ArrayList<Integer> ba = new ArrayList<Integer>();
//                for (int i = 0;i<5;i++){
//                    if (i<front.length)
//                        f.add(SFSUtil.byte2IntForTile(front[i]));
//                    m.add(SFSUtil.byte2IntForTile(mid[i]));
//                    ba.add(SFSUtil.byte2IntForTile(back[i]));
//                }
//                ISFSObject object = new SFSObject();
//                object.putBool("spec",false);
//                object.putIntArray("head",f);
//                object.putIntArray("mid",m);
//                object.putIntArray("back",ba);
//                send("quicksort",object,user);
//            }
//        }
    }
}
