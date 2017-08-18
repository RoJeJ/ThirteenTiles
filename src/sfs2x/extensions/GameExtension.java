package sfs2x.extensions;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.handler.room.DisconnectInRoom;
import sfs2x.handler.room.JoinRoomHandler;
import sfs2x.handler.room.LeaveRoomHandler;
import sfs2x.handler.room.RoomResponseHandler;
import sfs2x.logic.MainGame;
import sfs2x.model.Player;
import sfs2x.model.Table;
import sfs2x.model.utils.SFSUtil;

import java.util.Timer;
import java.util.TimerTask;

public class GameExtension extends SFSExtension {
    private MainGame mainGame;
    private boolean exitFlag = false;
    private Table table;
    private Player proposer;
    private Room room;
    private Timer GameTimer;
    private Timer waitOutTimer;
    private Timer exitTimer;

    public Timer getExitTimer()
    {
        return this.exitTimer;
    }

    public void cancelExitTimer()
    {
        this.exitTimer.cancel();
        this.exitTimer = new Timer(true);
    }

    private TimerTask task = new TimerTask()
    {
        public void run()
        {
            if (!GameExtension.this.table.isGameStarted()) {
                GameExtension.this.getApi().removeRoom(GameExtension.this.room);
            }
        }
    };
    private TimerTask mainGameTask = new TimerTask()
    {
        public void run()
        {
            try
            {
                GameExtension.this.mainGame.run();
                Thread.sleep(30L);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public TimerTask getExitTask()
    {
        return new TimerTask() {
            public void run()
            {
                if (GameExtension.this.exitFlag)
                {
                    SFSUtil.RecordGame(room);
                    send("count", SFSUtil.gameCount(GameExtension.this.table), GameExtension.this.room.getUserList());
                    GameExtension.this.getApi().removeRoom(GameExtension.this.room);
                }
            }
        };
    }

    public MainGame getMainGame()
    {
        return this.mainGame;
    }

    public void init()
    {
        this.room = getParentRoom();
        int count = this.room.getVariable("count").getIntValue();
        int person = this.room.getVariable("person").getIntValue();
        boolean hong = this.room.getVariable("hong").getBoolValue();
        boolean aa = this.room.getVariable("aa").getBoolValue();
        int ma = this.room.getVariable("ma").getIntValue();
        this.table = new Table(count, person, hong, aa, ma);
        this.room.setProperty("table", this.table);
        this.mainGame = new MainGame(this, this.table);

        this.waitOutTimer = new Timer(true);
        this.waitOutTimer.schedule(this.task, 600000L);

        this.GameTimer = new Timer(true);
        this.GameTimer.schedule(this.mainGameTask, 0L, 30L);

        this.exitTimer = new Timer();

        addEventHandler(SFSEventType.USER_JOIN_ROOM, JoinRoomHandler.class);
        addEventHandler(SFSEventType.USER_LEAVE_ROOM, LeaveRoomHandler.class);
        addEventHandler(SFSEventType.USER_LOGOUT, DisconnectInRoom.class);
        addEventHandler(SFSEventType.USER_DISCONNECT, DisconnectInRoom.class);
        addRequestHandler("game", RoomResponseHandler.class);
    }

    public void destroy()
    {
        trace("GameExtension is destroyed");
        this.GameTimer.cancel();
        this.waitOutTimer.cancel();
        this.exitTimer.cancel();
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

    public Player getProposer()
    {
        return this.proposer;
    }

    public void setProposer(Player proposer)
    {
        this.proposer = proposer;
    }
}

