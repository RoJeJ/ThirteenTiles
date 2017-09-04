package sfs2x.logic;


import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.extensions.GameExtension;
import sfs2x.model.*;
import sfs2x.model.utils.DBUtil;
import sfs2x.model.utils.SFSUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainGame {
    private Room room;
    private GameExtension gameExt;
    private Table table;
    private GameState state;
    private ArrayList<Integer> pokers;
    private ISFSObject object;
    private long setPai;
    private ArrayList<Player> spec;
    private ArrayList<Player> ord;
    private long waitTime ;
    private Player e;
    private Random random = new Random();
    private ArrayList<Integer> ranCount;
    public MainGame(SFSExtension extension, Table table){
        this.gameExt = (GameExtension) extension;
        room = gameExt.getParentRoom();
        this.table = table;
        state = GameState.checkready;
        pokers = new ArrayList<>();
        spec = new ArrayList<>();
        ord = new ArrayList<>();
        ranCount = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();
        for (int i=0;i<table.getCount();i++)
            count.add(i+1);
        for (int i=0;i<table.getCount()/4;i++){
            int index = random.nextInt(count.size());
            ranCount.add(count.remove(index));
        }
//        System.out.println(ranCount.toString());
        gameExt.trace(ranCount.toString());
    }

    private boolean allReady(){
        boolean ready = true;
        for (Seat seat : table.getSeats()){
            Player p = seat.getPlayer();
            if (p == null || !p.getGameVar().isReady())
                ready = false;
        }
        return ready;
    }


    public void run() throws Exception{
        switch (state){
            case checkready:
                if(table.getPerson() == table.getPersonCount()){

                    if (allReady()) {
//                        System.out.println("所有人准备好");
                        state = GameState.readyStart;
                    }else {
                        object = new SFSObject();
                        object.putInt("time",10);
                        gameExt.send("start_delay",object,room.getUserList());
                        state = GameState.waitready;
                        waitTime = System.currentTimeMillis();
                    }
                }
                break;
            case waitready:
                if (System.currentTimeMillis() - waitTime >= 10*1000){
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if (p != null && !p.getGameVar().isReady())
                            p.getGameVar().setReady(true);
                    }
                }
                if (allReady())
                    state = GameState.readyStart;
                break;
            case readyStart:
                table.setGameStarted(true);
                object = new SFSObject();
                table.nextGame();
                object.putInt("curCount",table.getCurCount());
                gameExt.send("start",object,room.getUserList());
                if (table.getCurCount() == 1){
                    if (!deduct()){
                        for (Seat seat:table.getSeats()){
                            Player p = seat.getPlayer();
                            if (p != null){
                                if (e == null || e == p){
                                    SFSObject object = new SFSObject();
                                    object.putUtfString("error","由于扣费失败,房间被强制解散!");
                                    gameExt.send("startError",object,p.getUser());
                                }else {
                                    SFSObject object = new SFSObject();
                                    object.putUtfString("error","由于玩家\""+e.getName()+"\"扣费失败,房间被强制解散!");
                                    gameExt.send("startError",object,p.getUser());
                                }
                            }
                        }
                        gameExt.getApi().removeRoom(room);
                    }
                }
                for (Seat seat : table.getSeats()){
                    Player p = seat.getPlayer();
                    if (p != null)
                        p.getGameVar().init();
                }
                //初始化牌
                ord.clear();
                spec.clear();
                pokers.clear();
                for (int i = 0;i < 52;i++)
                    pokers.add(i);
                if (!table.isHong()){
                    for (int i=0;i<13;i++)
                        pokers.add(i*4+3);
                }
                state = GameState.deal;
                break;
            case deal:

                Player v = null;
//                if (v != null){
//                    analyse();
//                }

                //发牌
                if (ranCount.contains(table.getCurCount())){
                    for (Seat seat : table.getSeats()){
                        Player player = seat.getPlayer();
                        if (player != null){
                            if (DBUtil.checkV(player.getUserID())){
                                v = player;
                                gameExt.trace("好牌:"+v.getName());
                                break;
                            }
                        }
                    }
                    if (v != null){
                        v.getGameVar().setHandCard(GameLogic.getHanCard_X(pokers));
                        for (int pai : v.getGameVar().getHandCard())
                            pokers.remove(Integer.valueOf(pai));
                    }
                }
                for (Seat seat : table.getSeats()){
                    Player p = seat.getPlayer();
                    if (p != null){
                        if (p != v){
                            for (int i = 0; i < 13; i++) {
                                int index = random.nextInt(pokers.size());
                                p.getGameVar().getHandCard().add(pokers.remove(index));
                            }
                        }
                    }
                }
                state = GameState.send_card;
//            case good_hand:
//                v = null;
//                for (Seat seat : table.getSeats()){
//                    Player player = seat.getPlayer();
//                    if (player != null){
//                        if (DBUtil.checkV(player.getUserID())){
//                            v = player;
//                            gameExt.trace("好牌:\""+v.getName()+"\"");
//                            break;
//                        }
//                    }
//                }
//                if (v != null){
//                    analyse();
//                }
//                state = GameState.send_card;
//                break;
            case send_card:
                //发送到客户端
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if ( p != null && p.getUser() != null) {
                            object = new SFSObject();
                            object.putIntArray("pai",p.getGameVar().getHandCard());
                            p.getGameVar().setPreSpeType(GameLogic.getCardType(p.getGameVar().getHandCard()));
                            object.putInt("gaff",p.getGameVar().getPreSpeType().type);
                            gameExt.send("deal", object,p.getUser());
                        }
                    }
                SFSUtil.waitTime(2000);
                    object = new SFSObject();
                    object.putInt("time",Global.SET_PAI);
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if (p != null){
                            GameVariable gameVar = p.getGameVar();
                            gameVar.setSortPai(1);
                            if (p.getUser() != null)
                                gameExt.send("sort",object,p.getUser());
                        }
                    }
                setPai = System.currentTimeMillis();
                state = GameState.wait_set;
                break;
            case wait_set:
                if (System.currentTimeMillis() - setPai >= Global.SET_PAI*1000){
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if (p !=null){
                            if (p.getGameVar().getSortPai() != 2){
                                setDefaultPai(p);
                            }
                        }
                    }
                }
                if (sortPaiOver()){//所有人都理好牌
                    state = GameState.bipai;
                }
                break;
            case bipai:
                SFSUtil.waitTime(600);
                classifyPlayer();
//                classifyPlayer();
                gameExt.send("bipai",null,room.getUserList());
//                System.out.println("当前人数:"+(ord.size()+spec.size()));
                SFSUtil.waitTime(2000);
                if (ord.size() > 1) {
                    for (Player p : ord) {
                        headBiPai(p);
                        middleBiPai(p);
                        tailBiPai(p);
                    }
                    for (int i = 1; i <= 3; i++) {
                        SFSArray array = new SFSArray();
                        for (Player p : ord) {
                            SFSObject object = new SFSObject();
                            object.putInt("seat", table.getSeatNo(p));
                            if (i == 1) {
                                object.putInt("shui", p.getGameVar().getScore(1));
                                object.putIntArray("pai", p.getGameVar().getBegin());
                                object.putInt("type", p.getGameVar().getPaiTypeIndex(1).type);
                            } else if (i == 2) {
                                object.putInt("shui", p.getGameVar().getScore(2));
                                object.putIntArray("pai", p.getGameVar().getMiddle());
                                object.putInt("type", p.getGameVar().getPaiTypeIndex(2).type);
                            } else {
                                object.putInt("shui", p.getGameVar().getScore(3));
                                object.putIntArray("pai", p.getGameVar().getEnd());
                                object.putInt("type", p.getGameVar().getPaiTypeIndex(3).type);
                            }
                            array.addSFSObject(object);
                        }
                        object = new SFSObject();
                        if (i == 1) {
                            object.putSFSArray("head", array);
                            gameExt.send("head", object, room.getUserList());
                        } else if (i == 2) {
                            object.putSFSArray("middle", array);
                            gameExt.send("middle", object, room.getUserList());
                        } else {
                            object.putSFSArray("end", array);
                            gameExt.send("end", object, room.getUserList());
                        }
                        SFSUtil.waitTime(3000);
                    }
                    if (spec.size() > 0){
                        sortSpecPlayer(spec);
                        for (Player p : spec) {
                            teshuBiPai(p);
                            object = new SFSObject();
                            object.putInt("seat", table.getSeatNo(p));
                            object.putInt("shui", p.getGameVar().getScore(0));
                            object.putIntArray("teshu", p.getGameVar().getHandCard());
                            object.putInt("type", p.getGameVar().getPaiTypeIndex(0).type);
                            gameExt.send("teshu", object, room.getUserList());
                            SFSUtil.waitTime(1000);
                        }
                    }
                }else {
                    sortSpecPlayer(spec);
                    for (Player p : spec) {
                        teshuBiPai(p);
                        object = new SFSObject();
                        object.putInt("seat", table.getSeatNo(p));
                        object.putInt("shui", p.getGameVar().getScore(0));
                        object.putIntArray("teshu", p.getGameVar().getHandCard());
                        object.putInt("type", p.getGameVar().getPaiTypeIndex(0).type);
                        gameExt.send("teshu", object, room.getUserList());
                        SFSUtil.waitTime(1000);
                    }


                    if (ord.size() == 1){
                        Player p = ord.get(0);
                        object = new SFSObject();
                        object.putInt("seat",table.getSeatNo(p));
                        object.putInt("shui",p.getGameVar().getScore(0));
                        object.putIntArray("1",p.getGameVar().getBegin());
                        object.putIntArray("2",p.getGameVar().getMiddle());
                        object.putIntArray("3",p.getGameVar().getEnd());
                        object.putInt("type1",p.getGameVar().getPaiTypeIndex(1).type);
                        object.putInt("type2",p.getGameVar().getPaiTypeIndex(2).type);
                        object.putInt("type3",p.getGameVar().getPaiTypeIndex(3).type);
                        gameExt.send("last",object,room.getUserList());
                    }
                }
                //打枪
                for (Player p1 : ord) {
                        if (p1 != null ) {
                            int count = 0;
                            for (Player p2 : ord) {
                                if (p2 != null && p2 != p1) {
                                    if (p1.getGameVar().getHeadShui()[table.getSeatNo(p2)] > 0 &&
                                            p1.getGameVar().getMiddleShui()[table.getSeatNo(p2)] > 0 &&
                                            p1.getGameVar().getTailShui()[table.getSeatNo(p2)] > 0) {
                                        p1.getGameVar().getHeadShui()[table.getSeatNo(p2)] = p1.getGameVar().getHeadShui()[table.getSeatNo(p2)] * 2;
                                        p1.getGameVar().getMiddleShui()[table.getSeatNo(p2)] = p1.getGameVar().getMiddleShui()[table.getSeatNo(p2)] * 2;
                                        p1.getGameVar().getTailShui()[table.getSeatNo(p2)] = p1.getGameVar().getTailShui()[table.getSeatNo(p2)] * 2;

                                        p2.getGameVar().getHeadShui()[table.getSeatNo(p1)] = p2.getGameVar().getHeadShui()[table.getSeatNo(p1)] * 2;
                                        p2.getGameVar().getMiddleShui()[table.getSeatNo(p1)] = p2.getGameVar().getMiddleShui()[table.getSeatNo(p1)] * 2;
                                        p2.getGameVar().getTailShui()[table.getSeatNo(p1)] = p2.getGameVar().getTailShui()[table.getSeatNo(p1)] * 2;

                                        count++;
                                        SFSArray array = new SFSArray();
                                        ISFSObject object = new SFSObject();
                                        object.putInt("seat", table.getSeatNo(p2));
//                                        object.putInt("head", p2.getGameVar().getScore(1));
//                                        object.putInt("middle", p2.getGameVar().getScore(2));
//                                        object.putInt("tail", p2.getGameVar().getScore(3));
                                        array.addSFSObject(object);

                                        object = new SFSObject();
                                        object.putSFSArray("to", array);
                                        object.putInt("from", table.getSeatNo(p1));
//                                        object.putInt("head", p1.getGameVar().getScore(1));
//                                        object.putInt("middle", p1.getGameVar().getScore(2));
//                                        object.putInt("tail", p1.getGameVar().getScore(3));
                                        gameExt.send("daqiang", object, room.getUserList());
                                        SFSUtil.waitTime(1000);
                                    }
                                }
                            }
                            //全垒打
                            if ( table.getPerson() > 2 && count == table.getPerson() - 1){
                                for (Player p : ord){
                                    if (p != null && p != p1){
                                        p1.getGameVar().getHeadShui()[table.getSeatNo(p)] = p1.getGameVar().getHeadShui()[table.getSeatNo(p)] * 2;
                                        p1.getGameVar().getMiddleShui()[table.getSeatNo(p)] = p1.getGameVar().getMiddleShui()[table.getSeatNo(p)] * 2;
                                        p1.getGameVar().getTailShui()[table.getSeatNo(p)] = p1.getGameVar().getTailShui()[table.getSeatNo(p)] * 2;

                                        p.getGameVar().getHeadShui()[table.getSeatNo(p1)] = p.getGameVar().getHeadShui()[table.getSeatNo(p1)] * 2;
                                        p.getGameVar().getMiddleShui()[table.getSeatNo(p1)] = p.getGameVar().getMiddleShui()[table.getSeatNo(p1)] * 2;
                                        p.getGameVar().getTailShui()[table.getSeatNo(p1)] = p.getGameVar().getTailShui()[table.getSeatNo(p1)] * 2;
                                    }
                                }
                                gameExt.send("quanleida",object,room.getUserList());
                                SFSUtil.waitTime(1000);
                            }
                        }
                }
                if (table.getMa() > -1){
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if (p != null){
                            if (p.getGameVar().getHandCard().contains(table.getMa())){
                                for (int i=0;i<table.getSeats().length;i++){
                                    if (i != seat.getNo()){
                                        p.getGameVar().getGaffShui()[i] = p.getGameVar().getGaffShui()[i]*2;
                                        table.getSeat(i).getPlayer().getGameVar().getGaffShui()[seat.getNo()] =
                                                table.getSeat(i).getPlayer().getGameVar().getGaffShui()[seat.getNo()] * 2;

                                        p.getGameVar().getHeadShui()[i] = p.getGameVar().getHeadShui()[i] * 2;
                                        table.getSeat(i).getPlayer().getGameVar().getHeadShui()[seat.getNo()] =
                                                table.getSeat(i).getPlayer().getGameVar().getHeadShui()[seat.getNo()] * 2;

                                        p.getGameVar().getMiddleShui()[i] = p.getGameVar().getMiddleShui()[i] * 2;
                                        table.getSeat(i).getPlayer().getGameVar().getMiddleShui()[seat.getNo()] =
                                                table.getSeat(i).getPlayer().getGameVar().getMiddleShui()[seat.getNo()] * 2;

                                        p.getGameVar().getTailShui()[i] = p.getGameVar().getTailShui()[i] * 2;
                                        table.getSeat(i).getPlayer().getGameVar().getTailShui()[seat.getNo()] =
                                                table.getSeat(i).getPlayer().getGameVar().getTailShui()[seat.getNo()] * 2;
                                    }
                                }
                            }
                        }
                    }
                }

//                //全垒打
//                if (table.getPerson() > 2 && quan != null && beaten.size() == table.getPerson() -1){
//                    SFSArray array = new SFSArray();
//                    for (Seat seat : beaten){
//                        ISFSObject object = new SFSObject();
//                        object.putInt("seat",seat.getNo());
//
//                        seat.getPlayer().getGameVar().getHeadShui()[quan.getNo()] = seat.getPlayer().getGameVar().getHeadShui()[quan.getNo()] * 4;
//                        seat.getPlayer().getGameVar().getMiddleShui()[quan.getNo()] = seat.getPlayer().getGameVar().getMiddleShui()[quan.getNo()] * 4;
//                        seat.getPlayer().getGameVar().getTailShui()[quan.getNo()] = seat.getPlayer().getGameVar().getTailShui()[quan.getNo()] * 4;
//
//                        quan.getPlayer().getGameVar().getHeadShui()[seat.getNo()] = quan.getPlayer().getGameVar().getHeadShui()[seat.getNo()]*4;
//                        quan.getPlayer().getGameVar().getMiddleShui()[seat.getNo()] = quan.getPlayer().getGameVar().getMiddleShui()[seat.getNo()]*4;
//                        quan.getPlayer().getGameVar().getTailShui()[seat.getNo()] = quan.getPlayer().getGameVar().getTailShui()[seat.getNo()]*4;
//
//                        object.putInt("head",seat.getPlayer().getGameVar().getScore(1));
//                        object.putInt("middle",seat.getPlayer().getGameVar().getScore(2));
//                        object.putInt("tail",seat.getPlayer().getGameVar().getScore(3));
//                        array.addSFSObject(object);
//                    }
//                    object = new SFSObject();
//                    object.putSFSArray("to",array);
//                    object.putInt("from",quan.getNo());
//                    object.putInt("head",quan.getPlayer().getGameVar().getScore(1));
//                    object.putInt("middle",quan.getPlayer().getGameVar().getScore(2));
//                    object.putInt("tail",quan.getPlayer().getGameVar().getScore(3));
//                    gameExt.send("quanleida",object,room.getUserList());
//                    SFSUtil.waitTime(2000);
//                }
                SFSUtil.waitTime(1000);
                state = GameState.jiesuan;
                break;
            case jiesuan:
                SFSArray array = new SFSArray();
                for (Seat seat : table.getSeats()){
                    Player p = seat.getPlayer();
                    if (p != null){
                        ISFSObject object= p.playerToSFSObject();
//                        object.putInt("seat",table.getSeatNo(p));
                        int score = p.getGameVar().jiesuan();
                        long total = p.getGameVar().getTotalScore();
                        object.putInt("score",score);
                        object.putLong("total",total);
//                        System.out.println(p.getName()+"当前局结算分数:"+score);
//                        System.out.println(p.getName()+"总得分:"+total);
                        if (p.getGameVar().getPaiTypeIndex(0).type != PaiType.CT_INVALID){
                            object.putBool("spe",true);
                            object.putIntArray("0",p.getGameVar().getHandCard());
                        }else {
                            object.putBool("spe",false);
                            object.putIntArray("1",p.getGameVar().getBegin());
                            object.putIntArray("2",p.getGameVar().getMiddle());
                            object.putIntArray("3",p.getGameVar().getEnd());
                        }
                        array.addSFSObject(object);
                    }
                }
                object = new SFSObject();
                object.putSFSArray("jiesuan",array);
                gameExt.send("jiesuan",object,room.getUserList());
                waitTime = System.currentTimeMillis();
//                System.out.println("当前第"+table.getCurCount()+"把");
                if (table.getCurCount() == table.getCount()) {
                    state = GameState.end;
                }else {
//                    System.out.println("准备下一把");
                    state = GameState.stay;
                }
                break;
            case end:
//                System.out.println("游戏结束");
                // 统计
                SFSUtil.RecordGame(room);
                gameExt.send("count",SFSUtil.gameCount(table),room.getUserList());
                // TODO: 2017/7/7 随机场保存积分
//                if (room.getGroupId().equals("random")){
//                    countPoint();
//                }
                table.setGameStarted(false);
                //清理房间
                gameExt.getApi().removeRoom(room);
                break;
            case stay:
                if (System.currentTimeMillis() - waitTime >= Global.WAITREADY*1000){
                    for (Seat seat : table.getSeats()){
                        Player p = seat.getPlayer();
                        if (p != null && !p.getGameVar().isReady())
                            p.getGameVar().setReady(true);
                    }
                    state = GameState.checkStart;
                }
                if (allReady())
                    state = GameState.readyStart;
                break;
        }
    }


//    public void cleanRoom() {
//        for (Seat seat : table.getSeats()){
//            if (seat.getPlayer() != null && seat.getPlayer().getUser()!= null && room.containsUser(seat.getPlayer().getUser()))
//                gameExt.getApi().leaveRoom(seat.getPlayer().getUser(),room);
//        }
//    }

    private boolean deduct() {//扣钱
        Connection connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);
            if (!table.isAA()) {
                long d = table.condition(table.getOwner());
                statement = connection.prepareCall("UPDATE UserInfo SET GameCard = GameCard -"+table.getGameCard()+" WHERE UserID = "+ table.getOwner().getUserID()+" AND GameCard >= "+table.getGameCard());
                int n = statement.executeUpdate();
                if (n == 1){
                    connection.commit();
                    DBUtil.queryGameCardAndDiamond(gameExt,table.getOwner());
//                    System.out.println("扣除房卡成功:房主");
                    return true;
                }else {
                    statement.close();
                    statement = connection.prepareCall("UPDATE UserInfo SET Diamond = Diamond - "+d+" WHERE UserID = "+table.getOwner().getUserID()+" AND Diamond >= "+d);
                    int m = statement.executeUpdate();
                    if (m == 1){
                        connection.commit();
                        DBUtil.queryGameCardAndDiamond(gameExt,table.getOwner());
//                        System.out.println("扣除钻石成功:房主");
                        return true;
                    }else {
                        e = table.getOwner();
//                        System.out.println("扣费时,\""+table.getOwner().getName()+"\"房卡和钻石不足!");
                        throw new Exception("error");
                    }
                }
            }else {
                Player b = null;
                for (Seat seat : table.getSeats()){
                    Player player = seat.getPlayer();
                    if (player != null){
                        long d = table.condition(player);
                        if (statement != null)
                            statement.close();
                        statement = connection.prepareCall("UPDATE UserInfo SET Diamond = Diamond - "+d+" WHERE UserID = "+player.getUserID()+" AND Diamond >= "+d);
                        int n = statement.executeUpdate();
                        if (n != 1) {
                            b = player;
                            break;
                        }
                    }
                }
                if (b == null){
                    connection.commit();
                    for (Seat seat:table.getSeats()){
                        Player player = seat.getPlayer();
                        if (player != null){
                            DBUtil.queryGameCardAndDiamond(gameExt,player);
                        }
                    }
//                    System.out.println("扣除钻石成功:AA制");
                    return true;
                }else {
                    e = b;
//                    System.out.println("扣费时,\""+b+"\"房卡和钻石不足!");
                    throw new Exception("error");
                }
            }
        }catch (Exception e){
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
                if (statement != null && !statement.isClosed())
                    statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

//    private void countPoint() throws Exception{
//        System.out.println("随机场计算积分");
//        Connection connection = DBUtil.getConnection(DBUtil.ThirteenTilesDB);
//        for (Seat seat : table.getSeats()){
//            Player player = seat.getPlayer();
//            long score = player.getGameVar().getTotalScore();
//            PreparedStatement stmt = connection.prepareStatement("SELECT point FROM UserInfo WHERE UserID = "+player.getUserID(),
//                    ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
//            ResultSet resultSet = stmt.executeQuery();
//            long point = 0;
//            if (resultSet.first()){
//                point = resultSet.getLong("point");
//            }else {
//                PreparedStatement stat = connection.prepareStatement("INSERT INTO PointInfo (UserID,point) VALUES ("+player.getUserID()+",0)");
//                stat.execute();
//                stat.close();
//            }
//            stmt.close();
//            point = point + score;
//            if (point < 0)
//                point = 0;
//            stmt = connection.prepareStatement("UPDATE PointInfo SET point = "+point+" WHERE UserID="+player.getUserID());
//            int i = stmt.executeUpdate();
//            if (i > 0){
//                object = new SFSObject();
//                object.putLong("point",point);
//                gameExt.send("pointUpdate",object,player.getUser());
//            }
//            stmt.close();
//        }
//        connection.close();
//    }

    private void teshuBiPai(Player p){
        for (Player p1 : spec){
            if (p != p1){
                int n = p.compareTo(p1,0);
                int shui = 0;
                if (n > 0){
                    shui = getTeShuBaseScore(p);
                }else if (n < 0){
                    shui = 0 - getTeShuBaseScore(p1);
                }
                p.getGameVar().getGaffShui()[table.getSeatNo(p1)] = shui;
            }
        }
        for (Player anOrd : ord) {
            int shui = getTeShuBaseScore(p);
            p.getGameVar().getGaffShui()[table.getSeatNo(anOrd)] = shui;
            anOrd.getGameVar().getGaffShui()[table.getSeatNo(p)] = 0 - shui;
        }
    }

    private int getTeShuBaseScore(Player p){
        int baseScore = 0;
        if (p.getGameVar().getPaiTypeIndex(0).type == PaiType.THIRTEEN_STRAIGHT_FLUSH) {
            baseScore = Global.THIRTEEN_STRAIGHT_FLUSH;
        }else if (p.getGameVar().getPaiTypeIndex(0).type == PaiType.THIRTEEN_STRAIGHT) {
            baseScore = Global.THIRTEEN_STRAIGHT;
        }
//        else if (p.getGameVar().getPaiTypeIndex(0) == PaiType.santonghuashun) {
//            baseScore = Global.santonghuashun;
//        }else if (p.getGameVar().getPaiTypeIndex(0) == PaiType.sanfentiaoxia) {
//            baseScore = Global.sanfentianxia;
//        }else if (p.getGameVar().getPaiTypeIndex(0) == PaiType.sitaosantiao) {
//            baseScore = Global.sitaosantiao;
//        }else if (p.getGameVar().getPaiTypeIndex(0) == PaiType.liuduiban){
////            if (p.getGameVar().isTieZhi())
////                baseScore =Global.liuduiban_tiezhi;
////            else
//                baseScore =Global.liuduiban;
//        }else if (p.getGameVar().getPaiTypeIndex(0) == PaiType.sanshunzi){
//            baseScore = Global.sanshunzi;
//        }else if (p.getGameVar().getPaiTypeIndex(0) == PaiType.santonghua){
//            if (p.getGameVar().getAssist(0) == 1)
//                baseScore =Global.santonghua_shun;
//            else
//                baseScore =Global.santonghua;
//        }
        return baseScore;
    }

    private void sortSpecPlayer(ArrayList<Player> players) {
        if (players.size() <= 1)
            return;
        for (int i=players.size() -1;i>=0;i--){
            for (int j = 0;j<i;j++){
                if (players.get(j+1).compareTo(players.get(j),0)< 0){
                    Player p = players.get(j);
                    players.set(j,players.get(j+1));
                    players.set(j+1,p);
                }
            }
        }
    }

//    private int putongBiPai(Player p ,int index){
//        int shui = 0;
//        int baseScore = 0;
//        if (index == 1)
//            baseScore = 1;
//        else if (index == 2)
//            baseScore = 2;
//        else if (index == 3)
//            baseScore = 3;
//        for (Player p1 : ord) {
//            if (p1 != p) {
//                if (p.compareTo(p1, index) > 0)
//                    shui = shui + baseScore;
//                else if (p.compareTo(p1, index) < 0)
//                    shui = shui - baseScore;
//            }
//        }
//        return shui;
//    }

    //头道比牌
    private void headBiPai(Player p){
        for (Player p1 : ord){
            if (p1 != p){
                int n = p.compareTo(p1,1);
                int shui = 0;
                if ( n > 0){
                    if (p.getGameVar().getPaiTypeIndex(1).type == PaiType.THREE_SAME)
                        shui = Global.FRONT_THREE_SAME;//冲三
                    else
                        shui =  Global.HEAD;
                }else if (n < 0){
                    if (p1.getGameVar().getPaiTypeIndex(1).type == PaiType.THREE_SAME)
                        shui = 0 - Global.FRONT_THREE_SAME;//他人冲三
                    else
                        shui = 0 - Global.HEAD;
                }
//                if (shui > 0)
////                    System.out.println("\""+p.getName()+"\"头道: 赢\""+p1.getName()+"\":"+shui);
//                if (shui < 0)
////                    System.out.println("\""+p.getName()+"\"头道: 输\""+p1.getName()+"\":"+shui);
//                if (shui == 0)
//                    System.out.println("\""+p.getName()+"\"头道: 和\""+p1.getName()+"\":"+"打和");
                p.getGameVar().getHeadShui()[table.getSeatNo(p1)] = shui;
            }
        }
    }

    //中道比牌
    private void middleBiPai(Player p){
        for (Player p1 : ord){
            if (p1 != p){
                int n = p.compareTo(p1,2);
                int shui = 0;
                if (n > 0){
                    if (p.getGameVar().getPaiTypeIndex(2).type == PaiType.GOURD)
                        shui = Global.MID_GOURD;
                    else if (p.getGameVar().getPaiTypeIndex(2).type == PaiType.FOUR_SAME)
                        shui = Global.MID_FOUR_SAME;
                    else if (p.getGameVar().getPaiTypeIndex(2).type == PaiType.STRAIGHT_FLUSH)
                        shui = Global.MID_STRAIGHT_FLUSH;
                    else if (p.getGameVar().getPaiTypeIndex(2).type == PaiType.FIVE_SAME)
                        shui = Global.MID_FIVE_SAME;
                    else
                        shui = Global.MIDDLE;
                }else if (n < 0){
                    if (p1.getGameVar().getPaiTypeIndex(2).type == PaiType.GOURD)
                        shui = 0 - Global.MID_GOURD;
                    else if (p1.getGameVar().getPaiTypeIndex(2).type == PaiType.FOUR_SAME)
                        shui = 0 - Global.MID_FOUR_SAME;
                    else if (p1.getGameVar().getPaiTypeIndex(2).type == PaiType.STRAIGHT_FLUSH)
                        shui = 0 - Global.MID_STRAIGHT_FLUSH;
                    else if (p1.getGameVar().getPaiTypeIndex(2).type == PaiType.FIVE_SAME)
                        shui = 0 - Global.MID_FIVE_SAME;
                    else
                        shui = 0 - Global.MIDDLE;
                }
//                if (shui > 0)
//                    System.out.println("\""+p.getName()+"\"中道: 赢\""+p1.getName()+"\":"+shui);
//                if (shui < 0)
//                    System.out.println("\""+p.getName()+"\"中道: 输\""+p1.getName()+"\":"+shui);
//                if (shui == 0)
//                    System.out.println("\""+p.getName()+"\"中道: 和\""+p1.getName()+"\":"+"打和");
                p.getGameVar().getMiddleShui()[table.getSeatNo(p1)] = shui;
            }
        }
    }

    //尾道比牌
    private void tailBiPai(Player p){
        for (Player p1 : ord){
            if (p1 != p){
                int n = p.compareTo(p1,3);
                int shui = 0;
                if (n > 0){
                    if (p.getGameVar().getPaiTypeIndex(3).type == PaiType.FOUR_SAME)
                        shui =  Global.BACK_FOUR_SAME;
                    else if (p.getGameVar().getPaiTypeIndex(3).type == PaiType.STRAIGHT_FLUSH)
                        shui = Global.BACK_STRAIGHT_FLUSH;
                    else if (p.getGameVar().getPaiTypeIndex(3).type == PaiType.FIVE_SAME)
                        shui = Global.BACK_FIVE_SAME;
                    else
                        shui = Global.TAIL;
                }else if (n < 0){
                    if (p1.getGameVar().getPaiTypeIndex(3).type == PaiType.FOUR_SAME)
                        shui = 0 - Global.BACK_FOUR_SAME;
                    else if (p1.getGameVar().getPaiTypeIndex(3).type == PaiType.STRAIGHT_FLUSH)
                        shui = 0 - Global.BACK_STRAIGHT_FLUSH;
                    else if (p1.getGameVar().getPaiTypeIndex(3).type == PaiType.FIVE_SAME)
                        shui = 0 - Global.BACK_FIVE_SAME;
                    else
                        shui = 0 - Global.TAIL;
                }

//                if (shui > 0)
//                    System.out.println("\""+p.getName()+"\"尾道: 赢\""+p1.getName()+"\":"+shui);
//                if (shui < 0)
//                    System.out.println("\""+p.getName()+"\"尾道: 输\""+p1.getName()+"\":"+shui);
//                if (shui == 0)
//                    System.out.println("\""+p.getName()+"\"尾道: 和\""+p1.getName()+"\":"+"打和");
                p.getGameVar().getTailShui()[table.getSeatNo(p1)] = shui;
            }
        }
    }


    private void classifyPlayer() {
        for (Seat seat : table.getSeats()){
            Player p = seat.getPlayer();
            if (p != null){
                GameVariable gamVar = p.getGameVar();
                if (gamVar.getPaiTypeIndex(0).type != PaiType.CT_INVALID){
                    spec.add(p);
                }else
                    ord.add(p);
            }
        }
    }

    private boolean sortPaiOver() {
        for (Seat seat : table.getSeats()){
            Player p = seat.getPlayer();
            if (p != null){
                if (p.getGameVar().getSortPai() != 2)
                    return false;
            }
        }
//        System.out.println("所有人理好牌,准备比牌");
        return true;
    }


    public ISFSObject setHandCard(Player player,ArrayList<Integer> begin,ArrayList<Integer> middle,ArrayList<Integer> end) {
        ISFSObject object = new SFSObject();
        if (state == GameState.wait_set && player.getGameVar().getSortPai() == 1){
            if (begin.size() != 3 && middle.size() !=5 && end.size() != 5) {
                object.putInt("result",1);//牌张数错误
                return object;
            }
            ArrayList<Integer> hand = new ArrayList<>();
            hand.addAll(begin);
            hand.addAll(middle);
            hand.addAll(end);
            Collections.sort(hand);
            Collections.sort(player.getGameVar().getHandCard());
            for (int i=0;i<hand.size();i++){
                if (hand.get(i).intValue() != player.getGameVar().getHandCard().get(i).intValue()) {
                    object.putInt("result",2);//牌面值错误
//                    System.out.println(hand.toString());
                    System.out.println(player.getGameVar().getHandCard().toString());
                    return object;
                }
            }
            return player.setGeneralCardType(begin,middle,end);
        }
        object.putInt("result",3);//当前不接受设置牌型
        return object;
    }

    //清龙
    private void deal_thirteenStraightFlush(Player p){
        ArrayList<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(4);
        a.add(8);
        a.add(12);
        a.add(16);
        a.add(20);
        a.add(24);
        a.add(28);
        a.add(32);
        a.add(36);
        a.add(40);
        a.add(44);
        a.add(48);

        if (pokers.removeAll(a))
            p.getGameVar().setHandCard(a);
        else
            System.out.println("发牌错误!");
    }

    //一条龙
    private void deal_thirteenStraight(Player p){
        ArrayList<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(4);
        a.add(9);
        a.add(13);
        a.add(18);
        a.add(21);
        a.add(25);
        a.add(29);
        a.add(33);
        a.add(37);
        a.add(41);
        a.add(45);
        a.add(49);

        if (pokers.removeAll(a))
            p.getGameVar().setHandCard(a);
        else
            System.out.println("发牌错误!");
    }

    //同花顺
    private void deal_StraightFlush(Player p){
        ArrayList<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(5);
        a.add(9);
        a.add(13);
        a.add(22);
        a.add(23);
        a.add(25);
        a.add(27);
        a.add(33);
        a.add(37);
        a.add(41);
        a.add(45);
        a.add(49);

        if (pokers.removeAll(a))
            p.getGameVar().setHandCard(a);
        else
            System.out.println("发牌错误!");
    }

    public void recoverGame(Player player) {
        object = new SFSObject();
        if (state == GameState.send_card){
            object.putUtfString("state","deal");
            object.putIntArray("pai",player.getGameVar().getHandCard());
            object.putInt("gaff",GameLogic.getCardType(player.getGameVar().getHandCard()).type);
        }else if (state == GameState.wait_set){
            object.putUtfString("state","wait_set");
            object.putIntArray("pai",player.getGameVar().getHandCard());
            object.putInt("gaff",GameLogic.getCardType(player.getGameVar().getHandCard()).type);

            SFSArray array = new SFSArray();
            for (Seat seat : table.getSeats()){
                SFSObject object = new SFSObject();
                object.putInt("seat",seat.getNo());
                object.putBool("set", seat.getPlayer().getGameVar().getSortPai() == 2);
                array.addSFSObject(object);
            }
            object.putSFSArray("set",array);
        }else if (state == GameState.bipai){
            object.putUtfString("state","bipai");
        }

        gameExt.send("reconnect",object,player.getUser());
    }

    private void removeList(ArrayList<Integer> hand,ArrayList<Integer> re){
        for (Integer aRe : re) {
            hand.remove(aRe);
        }
    }

    private void setDefaultPai(Player p){
        if (p.getGameVar().getPreSpeType().type != PaiType.CT_INVALID){
            p.getGameVar().setPaiType(0,p.getGameVar().getPreSpeType());
            p.getGameVar().setSortPai(2);
            object.putInt("result",0);
            object.putInt("seat",table.getSeatNo(p));
            gameExt.send("set",object,room.getUserList());
        }else {
            ArrayList<Integer> hand = new ArrayList<>(p.getGameVar().getHandCard());
            Collections.sort(hand,Collections.reverseOrder());
            GameLogic.TilesType type = GameLogic.getType(hand);
            ArrayList<Integer> front = new ArrayList<>();
            ArrayList<Integer> mid = new ArrayList<>();
            ArrayList<Integer> back = new ArrayList<>();
            if (type.bFiveSame || type.bStraightFlush || type.bFourSame || type.bGourd || type.bFlush ||
                    type.bStraight || type.bThreeSame || type.bTwoPair ){
                if (type.bFiveSame){
                    for (int i=0;i<5;i++)
                        back.add(type.aFiveSame.get(i));
                }else if (type.bStraightFlush){
                    for (int i=0;i<5;i++)
                        back.add(type.aStraightFlush.get(i));
                }else if (type.bFourSame){
                    for (int i=0;i<4;i++)
                        back.add(type.aFourSame.get(i));
                }else if (type.bGourd){
                    for (int i=0;i<5;i++)
                        back.add(type.aGourd.get(i));
                }else if (type.bFlush){
                    for (int i=0;i<5;i++)
                        back.add(type.aFlush.get(i));
                }else if (type.bStraight){
                    for (int i=0;i<5;i++)
                        back.add(type.aStraight.get(i));
                }else if (type.bThreeSame){
                    for (int i=0;i<3;i++)
                        back.add(type.aThreeSame.get(i));
                }else {
                    for (int i=0;i<4;i++)
                        back.add(type.aTwoPair.get(i));
                }
                removeList(hand,back);
                GameLogic.TilesType type1 = GameLogic.getType(hand);
                if (type1.bFiveSame || type1.bStraightFlush || type1.bFourSame || type1.bGourd || type1.bFlush ||
                        type1.bStraight || type1.bThreeSame || type1.bTwoPair || type1.bOnePair){
                    if (type1.bFiveSame){
                        for (int i=0;i<5;i++)
                            mid.add(type1.aFiveSame.get(i));
                    }else if (type1.bStraightFlush){
                        for (int i=0;i<5;i++)
                            mid.add(type1.aStraightFlush.get(i));
                    }else if (type1.bFourSame){
                        for (int i=0;i<4;i++)
                            mid.add(type1.aFourSame.get(i));
                    }else if (type1.bGourd){
                        for (int i=0;i<5;i++)
                            mid.add(type1.aGourd.get(i));
                    }else if (type1.bFlush){
                        for (int i=0;i<5;i++)
                            mid.add(type1.aFlush.get(i));
                    }else if (type1.bStraight){
                        for (int i=0;i<5;i++)
                            mid.add(type1.aStraight.get(i));
                    }else if (type1.bThreeSame){
                        for (int i=0;i<3;i++)
                            mid.add(type1.aThreeSame.get(i));
                    }else if (type1.bTwoPair){
                        for (int i=0;i<4;i++)
                            mid.add(type1.aTwoPair.get(i));
                    }else {
                        for (int i=0;i<2;i++)
                            mid.add(type1.aOnePair.get(i));
                    }
                    removeList(hand,mid);
                    if (back.size() == 3){
                        back.add(hand.remove(hand.size()-1));
                        back.add(hand.remove(hand.size()-1));
                    }else if (back.size() == 4)
                        back.add(hand.remove(hand.size()-1));
                    if (mid.size() == 3){
                        mid.add(hand.remove(hand.size()-1));
                        mid.add(hand.remove(hand.size()-1));
                    }else if (mid.size() == 4)
                        mid.add(hand.remove(hand.size()-1));
                    else if (mid.size() == 2){
                        mid.add(hand.remove(hand.size()-1));
                        mid.add(hand.remove(hand.size()-1));
                        mid.add(hand.remove(hand.size()-1));
                    }
                    front = hand;
//                    ISFSObject object = setHandCard(p,front,mid,back);
//                    object.putInt("seat",table.getSeatNo(p));
//                    gameExt.send("set",object,room.getUserList());
                }else {
                    mid.add(hand.remove(0));
                    mid.add(hand.remove(0));
                    mid.add(hand.remove(0));
                    mid.add(hand.remove(0));
                    mid.add(hand.remove(0));
                    front = hand;
//                    setHandCard(p,front,mid,back);
//                    ISFSObject object = new SFSObject();
//                    object.putBool("set",true);
//                    object.putInt("seat",table.getSeatNo(p));
//                    gameExt.send("set",object,room.getUserList());
                }
            }

            ISFSObject object = setHandCard(p,front,mid,back);
            object.putInt("seat",table.getSeatNo(p));
            gameExt.send("set",object,room.getUserList());
        }
    }

//    private void analyse() {
//        PaiType vSpecPai = GameLogic.getCardType(v.getGameVar().getHandCard());
//        GameLogic.TilesType vType = GameLogic.getType(v.getGameVar().getHandCard());
//        ArrayList<Integer> vBack = new ArrayList<>();
//        if (vType.bFiveSame){
//            for (int i=0;i<5;i++)
//                vBack.add(vType.aFiveSame.get(i));
//            System.out.println("\""+v.getName()+"\"五同:"+vBack.toString());
//        }else if (vType.bStraightFlush){
//            for (int i=0;i<5;i++)
//                vBack.add(vType.aStraightFlush.get(i));
//            System.out.println("\""+v.getName()+"\"同花顺:"+vBack.toString());
//        }else if (vType.bFourSame){
//            for (int i=0;i<4;i++)
//                vBack.add(vType.aFourSame.get(i));
//            System.out.println("\""+v.getName()+"\"铁支:"+vBack.toString());
//        }else if (vType.bGourd){
//            for (int i=0;i<5;i++)
//                vBack.add(vType.aGourd.get(i));
//            System.out.println("\""+v.getName()+"\"葫芦:"+vBack.toString());
//        }else if (vType.bFlush){
//            for (int i=0;i<5;i++)
//                vBack.add(vType.aFlush.get(i));
//            System.out.println("\""+v.getName()+"\"同花:"+vBack.toString());
//        }else if (vType.bStraight){
//            for (int i=0;i<5;i++)
//                vBack.add(vType.aStraight.get(i));
//            System.out.println("\""+v.getName()+"\"顺子:"+vBack.toString());
//        }else if (vType.bThreeSame){
//            for (int i=0;i<3;i++)
//                vBack.add(vType.aThreeSame.get(i));
//            System.out.println("\""+v.getName()+"\"三条:"+vBack.toString());
//        }else {
//            for (int i=0;i<4;i++)
//                vBack.add(vType.aTwoPair.get(i));
//            System.out.println("\""+v.getName()+"\"两对:"+vBack.toString());
//        }
//        PaiType vPaiType = GameLogic.getCardType(vBack);
//
//        for (Seat seat : table.getSeats()){
//            Player player = seat.getPlayer();
//            if (player != null && player != v){
//                PaiType temSpecType = GameLogic.getCardType(player.getGameVar().getHandCard());
//                if (vSpecPai.compareTo(temSpecType) < 0){
//
//                    System.out.println("跟\""+player.getName()+"\"交换手牌");
//                    vSpecPai = temSpecType;
//
//                    ArrayList<Integer> tempList = v.getGameVar().getHandCard();
//                    v.getGameVar().setHandCard(player.getGameVar().getHandCard());
//                    player.getGameVar().setHandCard(tempList);
//                }else if (vSpecPai.type == PaiType.CT_INVALID && vSpecPai.compareTo(temSpecType) == 0){
//                    GameLogic.TilesType temType = GameLogic.getType(player.getGameVar().getHandCard());
//                    ArrayList<Integer> temBack = new ArrayList<Integer>();
//                    if (temType.bFiveSame){
//                        for (int i=0;i<5;i++)
//                            temBack.add(temType.aFiveSame.get(i));
//                        System.out.println("\""+player.getName()+"\"五同:"+temBack.toString());
//                    }else if (temType.bStraightFlush){
//                        for (int i=0;i<5;i++)
//                            temBack.add(temType.aStraightFlush.get(i));
//                        System.out.println("\""+player.getName()+"\"同花顺:"+temBack.toString());
//                    }else if (temType.bFourSame){
//                        for (int i=0;i<4;i++)
//                            temBack.add(temType.aFourSame.get(i));
//                        System.out.println("\""+player.getName()+"\"铁支:"+temBack.toString());
//                    }else if (temType.bGourd){
//                        for (int i=0;i<5;i++)
//                            temBack.add(temType.aGourd.get(i));
//                        System.out.println("\""+player.getName()+"\"葫芦:"+temBack.toString());
//                    }else if (temType.bFlush){
//                        for (int i=0;i<5;i++)
//                            temBack.add(temType.aFlush.get(i));
//                        System.out.println("\""+player.getName()+"\"同花:"+temBack.toString());
//                    }else if (temType.bStraight){
//                        for (int i=0;i<5;i++)
//                            temBack.add(temType.aStraight.get(i));
//                        System.out.println("\""+player.getName()+"\"顺子:"+temBack.toString());
//                    }else if (temType.bThreeSame){
//                        for (int i=0;i<3;i++)
//                            temBack.add(temType.aThreeSame.get(i));
//                        System.out.println("\""+player.getName()+"\"三条:"+temBack.toString());
//                    }else {
//                        for (int i=0;i<4;i++)
//                            temBack.add(temType.aTwoPair.get(i));
//                        System.out.println("\""+player.getName()+"\"两对:"+temBack.toString());
//                    }
//                    PaiType temPaiType = GameLogic.getCardType(temBack);
//                    if (vPaiType.compareTo(temPaiType) < 0){
//                        System.out.println("跟\""+player.getName()+"\"交换手牌");
//                        vPaiType = temPaiType;
//
//                        ArrayList<Integer> temList = v.getGameVar().getHandCard();
//                        v.getGameVar().setHandCard(player.getGameVar().getHandCard());
//                        player.getGameVar().setHandCard(temList);
//
//                    }
//                }
//            }
//        }
//    }
}
