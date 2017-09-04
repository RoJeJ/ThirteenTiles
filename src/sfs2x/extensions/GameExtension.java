package sfs2x.extensions;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.handler.room.DisconnectInRoom;
import sfs2x.handler.room.JoinRoomHandler;
import sfs2x.handler.room.LeaveRoomHandler;
import sfs2x.handler.room.RoomResponseHandler;
import sfs2x.logic.MainGame;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.Table;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class GameExtension extends SFSExtension {
    private MainGame mainGame;
    private boolean exitFlag = false;
    private Table table;
    private Player proposer;
    private Room room;
    private Timer exitTimer;
    private boolean runFlag;

    private Runnable mainGameTask = new Runnable()
    {
        public void run()
        {
            try
            {
                if (runFlag){
                    mainGame.run();
                    SmartFoxServer.getInstance().getTaskScheduler().schedule(mainGameTask,30,TimeUnit.MILLISECONDS);
                }
            }
            catch (Exception e)
            {
                trace(e.getMessage());
            }
        }
    };

    public Timer getExitTimer() {
        return exitTimer;
    }

    public MainGame getMainGame()
    {
        return this.mainGame;
    }

    @Override
    public void init() {
        exitTimer = new Timer();
        this.room = getParentRoom();
        int count = this.room.getVariable("count").getIntValue();
        int person = this.room.getVariable("person").getIntValue();
        boolean hong = this.room.getVariable("hong").getBoolValue();
        boolean aa = this.room.getVariable("aa").getBoolValue();
        int ma = this.room.getVariable("ma").getIntValue();
        this.table = new Table(count, person, hong, aa, ma);
        this.room.setProperty("table", this.table);
        this.mainGame = new MainGame(this, this.table);

        runFlag = true;
        SmartFoxServer.getInstance().getTaskScheduler().schedule(this.mainGameTask, 30, TimeUnit.MILLISECONDS);

        SmartFoxServer.getInstance().getTaskScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                if (!GameExtension.this.table.isGameStarted()) {
                    GameExtension.this.getApi().removeRoom(GameExtension.this.room);
                }
            }
        }, Global.WAIT_TIMEOUT,TimeUnit.MILLISECONDS);



        addEventHandler(SFSEventType.USER_JOIN_ROOM, JoinRoomHandler.class);
        addEventHandler(SFSEventType.USER_LEAVE_ROOM, LeaveRoomHandler.class);
        addEventHandler(SFSEventType.USER_LOGOUT, DisconnectInRoom.class);
        addEventHandler(SFSEventType.USER_DISCONNECT, DisconnectInRoom.class);
        addRequestHandler("game", RoomResponseHandler.class);
    }

    @Override
    public void destroy() {
        trace("GameExtension is destroyed");
        runFlag = false;
        super.destroy();
    }

    public boolean isExitFlag()
    {
        return this.exitFlag;
    }

    public void setExitFlag(boolean exitFlag)
    {
        this.exitFlag = exitFlag;
    }

    public void cancelTimer(){
        if (exitTimer != null) {
            exitTimer.cancel();
            exitTimer = new Timer();
        }
    }

    public Player getProposer()
    {
        return this.proposer;
    }

    public void setProposer(Player proposer)
    {
        this.proposer = proposer;
    }
}

