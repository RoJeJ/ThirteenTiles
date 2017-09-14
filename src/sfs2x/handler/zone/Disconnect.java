package sfs2x.handler.zone;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.utils.DBUtil;

import java.io.IOException;

public class Disconnect extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        player.setUser(null);
        int userid = (int) user.getSession().getProperty("userid");
        DBUtil.setLoginInFlag(userid,0);
    }
}
