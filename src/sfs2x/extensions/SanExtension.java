package sfs2x.extensions;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import sfs2x.handler.zone.*;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.utils.DBUtil;


public class SanExtension extends SFSExtension {
    public static HttpClient httpClient;

    @Override
    public void init() {
        httpClient = new DefaultHttpClient();
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
        addEventHandler(SFSEventType.USER_DISCONNECT,Disconnect.class);
        addEventHandler(SFSEventType.USER_LOGOUT,Disconnect.class);
        addRequestHandler("cmd",ZoneResponseHandler.class);
        addEventHandler(SFSEventType.ROOM_REMOVED,RoomRemovedHandler.class);
}

    @Override
    public void destroy() {
        super.destroy();
        trace("zone extension destroyed");
    }

    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        int userID = Integer.parseInt((String) params);
        if (cmdName.equals("updateDia")){
            for (User user : getParentZone().getUserList()){
                Player player = (Player) user.getSession().getProperty(Global.PLAYER);
                if (player.getUserID() == userID){
                    DBUtil.queryGameCardAndDiamond(this,player);
                }
            }
        }
        return "success";
    }
}
