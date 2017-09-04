package sfs2x.handler.zone;


import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.extensions.GameExtension;
import sfs2x.model.Global;
import sfs2x.model.Table;
import sfs2x.model.utils.SFSUtil;

import java.util.Iterator;
import java.util.Map;

public class RoomRemovedHandler extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        Room room = (Room) isfsEvent.getParameter(SFSEventParam.ROOM);
//        trace("---------->房间:"+room.getName()+"被移除");
        Iterator<Map.Entry<Integer,Room>> iterator = SFSUtil.offlinePlayer.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer,Room> entry = iterator.next();
            if (entry.getValue() == room)
                iterator.remove();
        }
        int index = Integer.parseInt(room.getName().substring(0,1));
        if (index == 1){
            if (!Global.roomNames2.contains(Integer.parseInt(room.getName())))
                Global.roomNames2.add(Integer.parseInt(room.getName()));
        }else if (index == 3){
            if (!Global.roomNames3.contains(Integer.parseInt(room.getName())))
                Global.roomNames3.add(Integer.parseInt(room.getName()));
        }else if (index == 6){
            if (!Global.roomNames4.contains(Integer.parseInt(room.getName())))
                Global.roomNames4.add(Integer.parseInt(room.getName()));
        }else if (index == 8){
            if (!Global.roomNames5.contains(Integer.parseInt(room.getName())))
                Global.roomNames5.add(Integer.parseInt(room.getName()));
        }
    }
}
