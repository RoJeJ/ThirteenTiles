package sfs2x.handler.zone;

import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.extensions.GameExtension;
import sfs2x.model.Global;
import sfs2x.model.Player;
import sfs2x.model.Table;
import sfs2x.model.utils.DBUtil;
import sfs2x.model.utils.SFSUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@MultiHandler
public class ZoneResponseHandler extends BaseClientRequestHandler {
    @Override
    public synchronized void handleClientRequest(User user, ISFSObject isfsObject) {
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        String cmd = isfsObject.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);
        if (cmd.equals("create")){
            createRoom(user,isfsObject);
        }else if (cmd.equals("join")){
            joinRoom(user,isfsObject);
        }else if (cmd.equals("ping")){
            send("ping",null,user);
        }else if (cmd.equals("update")){
            DBUtil.queryGameCardAndDiamond(getParentExtension(),player);
        }
//        else if (cmd.equals("matching")){
//            match(user);
//        }else if (cmd.equals("match")){
//            Player p  = (Player) user.getSession().getProperty(Global.PLAYER);
//            if (SFSUtil.ranField.contains(p))
//                SFSUtil.ranField.remove(p);
//        }
        else if (cmd.equals("share")){
            presentCard(player);
        }else if (cmd.equals("record")){
            send("record",SFSUtil.getGameRecord(player.getUserID()),user);
        }else if (cmd.equals("lott")){
            SFSObject object = new SFSObject();
            if (DBUtil.draw(getParentExtension(),player)){
                int zz;
                Random random = new Random();
                int ran = random.nextInt(10)+1;
                if (ran <= 7){
                    zz = 0;
                }else if (ran == 8 || ran == 9){
                    int ran2 = random.nextInt(10)+1;
                    if (ran2 == 1 || ran2 == 2 || ran2 == 3){
                        zz = 1;
                    }else if (ran2 == 4 || ran2 == 5 || ran2 == 6){
                        zz = 2;
                    }else if (ran2 == 7 || ran2 == 8){
                        zz = 3;
                    }else if (ran2 == 9){
                        zz = 4;
                    }else {
                        zz = 5;
                    }
                }else {
                    int ran2 = random.nextInt(10)+1;
                    if (ran2 == 1 || ran2 == 2 || ran2 == 3){
                        zz = 6;
                    }else if (ran2 == 4 || ran2 == 5 || ran2 == 6){
                        zz = 7;
                    }else if (ran2 == 7 || ran2 == 8){
                        zz = 8;
                    }else if (ran2 == 9){
                        zz = 9;
                    }else {
                        zz = 10;
                    }
                }
                if (DBUtil.cashLottery(zz,player)){
                    object.putInt("lott",zz);
                    send("lott",object,user);
                }
            }else {
                object.putInt("lott",-1);
                send("lott",object,user);
            }
        }
    }

    private void presentCard(Player p){
        try {
            Connection connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
            PreparedStatement statement = connection.prepareStatement("UPDATE UserInfo SET GameCard = GameCard +"+Global.CARD+",ShareDate = '"+new Timestamp(System.currentTimeMillis())+"' WHERE UserID = "+p.getUserID()+" AND (ShareDate IS NULL OR datediff(DD,ShareDate,getdate()) > 0)");
            if (statement.executeUpdate() == 1) {
                DBUtil.queryGameCardAndDiamond(getParentExtension(),p);
            }
            if (!connection.isClosed())
                connection.close();
            if (!statement.isClosed())
                statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void dealShare(User user) {
//        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
//        if (!player.isTaskDone()){
//            try {
//                Connection con = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                PreparedStatement statement = con.prepareStatement("UPDATE UserInfo SET shareDate= '"+timestamp+"' WHERE UserID = "+player.getUserID());
//
//
//                statement.execute();
//                presentCard(player);
//                statement.close();
//                con.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    //随机场
//    private void match(User user) {
//        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
//        ISFSObject object;
//        if (player.getDiamond() < 2){
//            object = new SFSObject();
//            object.putBool("match",false);
//            object.putUtfString("error","匹配失败,钻石不足!");
//            send("match",object,user);
//            return;
//        }
//        if (!SFSUtil.ranField.contains(player)){
//            SFSUtil.ranField.add(player);
//            object = new SFSObject();
//            object.putBool("match",true);
//            send("match",object,user);
//
//        }
//        if (SFSUtil.ranField.size() >= 4){
//            ArrayList<Player> users = new ArrayList<Player>();
//            users.add(SFSUtil.ranField.remove(0));
//            users.add(SFSUtil.ranField.remove(0));
//            users.add(SFSUtil.ranField.remove(0));
//            users.add(SFSUtil.ranField.remove(0));
//            CreateRoomSettings createRoomSettings = new CreateRoomSettings();
//            int index = new Random().nextInt(Global.roomNames.size());
//            int roomName = Global.roomNames.remove(index);
//            createRoomSettings.setName(String.valueOf(roomName));
//
//            createRoomSettings.setMaxVariablesAllowed(8);
//            createRoomSettings.setMaxUsers(4);
//            createRoomSettings.setMaxSpectators(0);
//            createRoomSettings.setGame(true);
//            createRoomSettings.setGroupId("random");
//            List<RoomVariable> list = new ArrayList<RoomVariable>();
//            list.add(new SFSRoomVariable("count",1));
//            list.add(new SFSRoomVariable("person",4));
//            list.add(new SFSRoomVariable("ba",false));
//            list.add(new SFSRoomVariable("aa",true));
//            list.add(new SFSRoomVariable("ma",false));
//            list.add(new SFSRoomVariable("color",0));
//            createRoomSettings.setRoomVariables(list);
//
//            createRoomSettings.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
//            createRoomSettings.setDynamic(true);
//
//            CreateRoomSettings.RoomExtensionSettings extensionSettings = new CreateRoomSettings.
//                    RoomExtensionSettings("sss","sfs2x.extensions.GameExtension");
//            createRoomSettings.setExtension(extensionSettings);
//            try {
//                Room room = getApi().createRoom(getParentExtension().getParentZone(),createRoomSettings,null);
//                for (Player p : users) {
//                    getApi().joinRoom(p.getUser(), room,null,false,p.getUser().getLastJoinedRoom());
//                    SFSUtil.waitTime(800);
//                }
//            }catch (SFSCreateRoomException e) {
//                e.printStackTrace();
//            }catch (SFSJoinRoomException e1){
//                e1.printStackTrace();
//            }
//        }
//    }

    //加入房间
    private void joinRoom(User user, ISFSObject isfsObject) {
        String roomName = isfsObject.getUtfString("name");
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        SFSObject object = new SFSObject();
        if (roomName == null || roomName.isEmpty() || roomName.length() != 6){
            object.putBool("join",false);
            object.putUtfString("error","加入失败,请输入正确的房间号!");
            send("join",object,user);
            return;
        }
        Room room = getParentExtension().getParentZone().getRoomByName(roomName);
        if (room == null || room.getGroupId().equals("random")){
            object.putBool("join",false);
            object.putUtfString("error","加入失败,房间不存在!");
            send("join",object,user);
            return;
        }
        if (room.getGroupId().equals("random")){
            object.putBool("join",false);
        }
        Table table = (Table) room.getProperty(Global.TABLE);
        if (table == null){
            object.putBool("join",false);
            object.putUtfString("error","加入失败,无法读取房间设置!");
            send("join",object,user);
            return;
        }
        if (table.isAA()){
            long d = table.condition(player);
            if (d == -1){
                object.putBool("join",false);
                object.putUtfString("error","获取房费失败");
                send("join",object,user);
                return;
            }
            Connection con = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                stmt = con.prepareStatement("SELECT * FROM UserInfo WHERE UserID = "+player.getUserID()+" AND Diamond >= "+d,
                        ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery();
                if (!rs.next()){
                    object.putBool("join",false);
                    object.putUtfString("error","加入失败,钻石不足!");
                    send("join",object,user);
                    return;
                }
            }catch (SQLException e){
                object.putBool("join",false);
                object.putUtfString("error","加入失败,钻石不足!");
                send("join",object,user);
                return;
            }finally {
                try {
                    if (con != null && !con.isClosed())
                        con.close();
                    if (stmt!= null && !stmt.isClosed())
                        stmt.close();
                    if (rs != null && !rs.isClosed())
                        rs.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
//            if (!table.isSettled()){
//                long d = table.condition(player);
//                if (d == -1) {
//                    object.putBool("join",false);
//                    object.putUtfString("error","获取房费失败!");
//                    send("join",object,user);
//                    return;
//                }
//                if (player.getDiamond() < d){
//                    object.putBool("join",false);
//                    object.putUtfString("error","加入失败,钻石不足!");
//                    send("join",object,user);
//                    return;
//                }
//            }
//        }
        try {
            getApi().joinRoom(user,room,null,false,user.getLastJoinedRoom());
        } catch (SFSJoinRoomException e) {
            e.printStackTrace();
        }
    }


    //创建房间
    private void createRoom(User user, ISFSObject isfsObject) {
        Player player = (Player) user.getSession().getProperty(Global.PLAYER);
        SFSObject object = new SFSObject();
        Integer count = isfsObject.getInt("count");
        Integer person = isfsObject.getInt("person");
        boolean hong = isfsObject.getBool("hong");
        boolean aa = isfsObject.getBool("aa");
        int ma = isfsObject.getInt("ma");
        if (count == null || count != 12 && count != 24 && count != 36) {
            object.putBool("create",false);
            object.putUtfString("error","创建失败,没有设置局数或局数设置错误!");
            send("create",object,user);
            return;
        }
        if (person == null || person != 2 && person != 3 && person != 4 && person != 5){
            object.putBool("create",false);
            object.putUtfString("error","创建失败,没有设置人数或人数设置错误!");
            send("create",object,user);
            return;
        }

        if (person == 2 && Global.roomNames2.size() == 0 || person == 3 && Global.roomNames3.size() == 0 ||
                person == 4 && Global.roomNames4.size() == 0 || person == 5 && Global.roomNames5.size() == 0){
            object.putBool("create",false);
            object.putUtfString("error","创建失败,房间数已达到上限,不能再创建房间了!");
            send("create",object,user);
            return;
        }
        Table table = new Table(count,person,hong,aa,ma);
        table.setOwner(player);

        int d = table.condition(player);
        if (d == -1) {
            object.putBool("create",false);
            object.putUtfString("error","获取房费失败!");
            send("create",object,user);
            return;
        }
        DBUtil.queryGameCardAndDiamond(getParentExtension(),player);

        Connection con = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            if (!table.isAA()) {
                statement = con.prepareStatement("SELECT * FROM UserInfo WHERE UserID = "+player.getUserID()+" AND (GameCard >= "+table.getGameCard()+" OR Diamond >= "+d+")",
                        ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
                rs = statement.executeQuery();
                if (!rs.next()){
                    object.putBool("create", false);
                    object.putUtfString("error", "创建失败,房卡和钻石不足!");
                    send("create", object, user);
                    return;
                }
            } else {
                statement = con.prepareStatement("SELECT * FROM UserInfo WHERE UserID = "+player.getUserID()+" AND  Diamond >= "+d,
                        ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                rs = statement.executeQuery();
                if (!rs.next()){
                    object.putBool("create", false);
                    object.putUtfString("error", "创建失败,房卡和钻石不足!");
                    send("create", object, user);
                    return;
                }
            }
        }catch (SQLException e){
            object.putBool("create", false);
            object.putUtfString("error", "创建房间错误,请重试!");
            send("create", object, user);
            return;
        }finally {
            try {
                if (con != null && !con.isClosed())
                    con.close();
                if (statement != null && !statement.isClosed())
                    statement.close();
                if (rs != null && !rs.isClosed())
                    rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        CreateRoomSettings createRoomSettings = new CreateRoomSettings();
        int roomName = 0;
        if (person == 2){
            int index = new Random().nextInt(Global.roomNames2.size());
            roomName = Global.roomNames2.remove(index);
        }else if (person == 3){
            int index = new Random().nextInt(Global.roomNames3.size());
            roomName = Global.roomNames3.remove(index);
        }else if (person == 4){
            int index = new Random().nextInt(Global.roomNames4.size());
            roomName = Global.roomNames4.remove(index);
        }else {
            int index = new Random().nextInt(Global.roomNames5.size());
            roomName = Global.roomNames5.remove(index);
        }
        createRoomSettings.setName(String.valueOf(roomName));

        createRoomSettings.setMaxVariablesAllowed(8);
        createRoomSettings.setMaxUsers(person);
        createRoomSettings.setMaxSpectators(0);
        createRoomSettings.setGame(true);
        List<RoomVariable> list = new ArrayList<RoomVariable>();
        list.add(new SFSRoomVariable("count",count));
        list.add(new SFSRoomVariable("person",person));
        list.add(new SFSRoomVariable("hong",hong));
        list.add(new SFSRoomVariable("aa",aa));
        list.add(new SFSRoomVariable("ma",ma));
        createRoomSettings.setRoomVariables(list);

        createRoomSettings.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        createRoomSettings.setDynamic(true);

        CreateRoomSettings.RoomExtensionSettings extensionSettings = new CreateRoomSettings.
                RoomExtensionSettings("sss","sfs2x.extensions.GameExtension");
        createRoomSettings.setExtension(extensionSettings);
        try {
            Room room = getApi().createRoom(getParentExtension().getParentZone(),createRoomSettings,null);
            getApi().joinRoom(user,room,null,false,user.getLastJoinedRoom());
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }catch (SFSJoinRoomException e1){
            e1.printStackTrace();
        }
    }
}
