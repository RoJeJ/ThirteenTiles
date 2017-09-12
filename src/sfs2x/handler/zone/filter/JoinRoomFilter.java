package sfs2x.handler.zone.filter;

import com.smartfoxserver.v2.controllers.filter.SysControllerFilter;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.filter.FilterAction;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.Table;

public class JoinRoomFilter extends SysControllerFilter{
    private Zone zone;
    public JoinRoomFilter(Zone zone){
        this.zone = zone;
    }
    @Override
    public FilterAction handleClientRequest(User user, ISFSObject isfsObject) throws SFSException {
        Room room = zone.getRoomByName(isfsObject.getUtfString("n"));
        Table table = (Table) room.getProperty(Global.TABLE);
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        if (table.getSeatNo(player) == -1 && table.getPersonCount() == table.getPerson())
            return FilterAction.HALT;
        return FilterAction.CONTINUE;
    }
}
