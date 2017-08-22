package sfs2x.model;


import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Player{
    private int UserID;

    public long getGameCard() {
        return GameCard;
    }

    public void setGameCard(long gameCard) {
        GameCard = gameCard;
    }


    private long GameCard;
    private long Score;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String name;
    private String ip;
    private String faceUrl;
    private long diamond;
    private User user;
    private int gender;
    private GameVariable gameVar;
    private int agentID;

    public GameVariable getGameVar() {
        return gameVar;
    }

    public void setGameVar(GameVariable gameVar) {
        this.gameVar = gameVar;
    }

    public long getDiamond() {
        return diamond;
    }

    public void setDiamond(long diamond) {
        this.diamond = diamond;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

//    public int getGameID() {
//        return GameID;
//    }
//
//    public void setGameID(int gameID) {
//        GameID = gameID;
//    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ISFSObject playerToSFSObject() {
        ISFSObject object = new SFSObject();
        object.putInt("UserID", UserID);
//        object.putInt("GameID", GameID);
        object.putUtfString("name", name);
        object.putUtfString("faceUrl", faceUrl);
        object.putUtfString("ip", ip);
        object.putLong("dia", diamond);
        object.putLong("score",Score);
        object.putLong("card",GameCard);
        object.putInt("gender",gender);
        return object;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getFaceUrl() {
        return faceUrl;
    }


    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int compareTo(Player o,int index) {
        if (gameVar == null || o == null || o.gameVar == null) {
            try {
                throw new Exception("比牌错误!");
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        //先比特殊牌
        if (gameVar.getPaiTypeIndex(0).type != PaiType.CT_INVALID && o.gameVar.getPaiTypeIndex(0).type == PaiType.CT_INVALID)
            return 1;
        else if (gameVar.getPaiTypeIndex(0).type == PaiType.CT_INVALID && o.gameVar.getPaiTypeIndex(0).type !=PaiType.CT_INVALID)
            return -1;
        else if (gameVar.getPaiTypeIndex(0).type != PaiType.CT_INVALID && o.gameVar.getPaiTypeIndex(0).type != PaiType.CT_INVALID){
            return gameVar.getPaiTypeIndex(0).compareTo(o.getGameVar().getPaiTypeIndex(0));
        }else if (gameVar.getPaiTypeIndex(0).type == PaiType.CT_INVALID && o.gameVar.getPaiTypeIndex(0).type == PaiType.CT_INVALID){
//            if (gameVar.getPaiTypeIndex(index) != o.gameVar.getPaiTypeIndex(index))
            return gameVar.getPaiTypeIndex(index).compareTo(o.getGameVar().getPaiTypeIndex(index));
//            else
//                return assistCompare(gameVar.getAssist(index),o.gameVar.getAssist(index));
//            if (index == 1){
//                if (gameVar.getPaiTypeIndex(index).ordinal() > o.gameVar.getPaiTypeIndex(index).ordinal())
//                    return 1;
//                else if (gameVar.getPaiTypeIndex(index).ordinal() < o.gameVar.getPaiTypeIndex(index).ordinal())
//                    return -1;
//                else {
//                    if ((gameVar.getPaiTypeIndex(index) == PaiType.santiao && o.gameVar.getPaiTypeIndex(index) == PaiType.santiao) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.duizi && o.gameVar.getPaiTypeIndex(index) == PaiType.duizi)){
//                        if (gameVar.getAssist(index) > o.gameVar.getAssist(index))
//                            return 1;
//                        else if (gameVar.getAssist(index) < o.gameVar.getAssist(index))
//                            return -1;
//                        return 0;
//                    }else if (gameVar.getPaiTypeIndex(index) == PaiType.wulong && o.gameVar.getPaiTypeIndex(index) == PaiType.wulong)
//                        return fuzhuBiPai(o,index);
//                    return 0;
//                }
//            }else if (index == 2 || index == 3){
//                if (gameVar.getPaiTypeIndex(index).ordinal() > o.gameVar.getPaiTypeIndex(index).ordinal())
//                    return 1;
//                else if (gameVar.getPaiTypeIndex(index).ordinal() < o.gameVar.getPaiTypeIndex(index).ordinal())
//                    return -1;
//                else{
//                    if ((gameVar.getPaiTypeIndex(index) == PaiType.duizi && o.gameVar.getPaiTypeIndex(index) == PaiType.duizi) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.santiao && o.gameVar.getPaiTypeIndex(index) == PaiType.santiao) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.liangdui && o.gameVar.getPaiTypeIndex(index) == PaiType.liangdui) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.hulu && o.gameVar.getPaiTypeIndex(index) == PaiType.hulu) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.tiezhi && o.gameVar.getPaiTypeIndex(index) == PaiType.tiezhi)){
//                        if (gameVar.getAssist(index) > o.gameVar.getAssist(index))
//                            return 1;
//                        else if (gameVar.getAssist(index) < o.gameVar.getAssist(index))
//                            return -1;
//                        else
//                            return 0;
//                    }else if ((gameVar.getPaiTypeIndex(index) == PaiType.tonghua && o.gameVar.getPaiTypeIndex(index) == PaiType.tonghua) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.shunzi && o.gameVar.getPaiTypeIndex(index) == PaiType.shunzi) ||
//                            (gameVar.getPaiTypeIndex(index) == PaiType.tonghuashun && o.gameVar.getPaiTypeIndex(index) == PaiType.tonghuashun)) {
//                        return fuzhuBiPai(o, index);
//                    }
//                }
//            }
        }
        return 0;
    }

//    public int fuzhuBiPai(Player o, int index){
//        ArrayList<Integer> temp1 = new ArrayList<Integer>();
//        ArrayList<Integer> temp2 = new ArrayList<Integer>();
//        if (index == 0){
//            temp1.addAll(gameVar.getHandCard());
//            temp2.addAll(o.gameVar.getHandCard());
//        }else if (index == 1){
//            temp1.addAll(gameVar.getBegin());
//            temp2.addAll(o.gameVar.getBegin());
//        }else if (index == 2){
//            temp1.addAll(gameVar.getMiddle());
//            temp2.addAll(o.gameVar.getMiddle());
//        }else if (index == 3){
//            temp1.addAll(gameVar.getEnd());
//            temp2.addAll(o.gameVar.getEnd());
//        }
//        Collections.sort(temp1);
//        Collections.sort(temp2);
//        double a1 = 0,a2 = 0;
//        for (int i=0;i<temp1.size();i++){
//            temp1.set(i,temp1.get(i)/4);
//            temp2.set(i,temp2.get(i)/4);
//        }
//        for (int i=0;i<temp1.size();i++){
//            a1 = a1 + temp1.get(i)*Math.pow(13,i);
//            a2 = a2 + temp2.get(i)*Math.pow(13,i);
//        }
//        return (int) (a1 - a2);
//    }



    public ISFSObject setGeneralCardType(ArrayList<Integer> begin, ArrayList<Integer> middle, ArrayList<Integer> end) {
        PaiType frontType = GameLogic.getCardType(begin);
        PaiType midType = GameLogic.getCardType(middle);
        PaiType backType = GameLogic.getCardType(end);

        ISFSObject object = new SFSObject();

        if (midType.compareTo(frontType) < 0){
            object.putInt("result",4);//首道大于中道
            return object;
        }
        if (backType.compareTo(midType) < 0){
            object.putInt("result",5); //中道大于尾道
            return object;
        }

        gameVar.setBegin(begin);
        gameVar.setMiddle(middle);
        gameVar.setEnd(end);
        gameVar.setPaiType(1,frontType);
        gameVar.setPaiType(2,midType);
        gameVar.setPaiType(3,backType);
        gameVar.getPaiTypeIndex(0).setPaiType(PaiType.CT_INVALID,null);
        gameVar.setSortPai(2);
        object.putInt("result",0);//设置成功
        return object;
    }

    //相同牌型比较数组
    public int assistCompare(int[] a,int[] b) {
        int len;
        if (a.length >= b.length) {
            len = b.length;
        } else
            len = a.length;
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i])
                return a[i] - b[i];
        }
        return 0;
    }

    public long getScore() {
        return Score;
    }

    public void setScore(long score) {
        Score = score;
    }

    public int getAgentID() {
        return agentID;
    }

    public void setAgentID(int agentID) {
        this.agentID = agentID;
    }

}
