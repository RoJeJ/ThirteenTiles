package sfs2x.extensions;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.handler.zone.*;
import sfs2x.model.Global;


public class SanExtension extends SFSExtension {

    @Override
    public void init() {
        for (int i=100000;i<200000;i++)
            Global.roomNames2.add(i);
        for (int i=300000;i<400000;i++)
            Global.roomNames3.add(i);
        for (int i=600000;i<700000;i++)
            Global.roomNames4.add(i);
        for (int i=800000;i<900000;i++)
            Global.roomNames5.add(i);
        addEventHandler(SFSEventType.USER_LOGIN,LoginZoneHandler.class);
        addEventHandler(SFSEventType.USER_JOIN_ZONE,ZoneJoinedHandler.class);
//      addEventHandler(SFSEventType.USER_DISCONNECT,Disconnect.class);
//      addEventHandler(SFSEventType.USER_LOGOUT,Disconnect.class);
        addRequestHandler("cmd",ZoneResponseHandler.class);
        addEventHandler(SFSEventType.ROOM_REMOVED,RoomRemovedHandler.class);
}

    @Override
    public void destroy() {
        super.destroy();
        trace("zone extension destroyed");
    }

}
