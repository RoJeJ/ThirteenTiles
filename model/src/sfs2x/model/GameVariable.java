package sfs2x.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameVariable{
    private boolean ready;
    private int sortPai;
    private ArrayList<Integer> handCard;
    private ArrayList<Integer> begin;
    private ArrayList<Integer> middle;
    private ArrayList<Integer> end;
    private PaiType preSpeType;
    private PaiType[] paiType;
//    private int[][] assist;
    private int[] headShui;
    private int[] middleShui;
    private int[] tailShui;
    private int[] gaffShui;
    private int quit = 0;
    private long totalScore;
    private int winCount;
    private int loseCount;
    private int drawCount;

    public long getTotalScore(){
        return totalScore;
    }

    public int getWinCount(){
        return winCount;
    }
    public int getLoseCount(){
        return loseCount;
    }
    public int getDrawCount(){
        return drawCount;
    }


//    public int[] getAssist(int index){
//        return assist[index];
//    }
    public GameVariable(Table table) {
        preSpeType = new PaiType(PaiType.CT_INVALID,null);
        handCard = new ArrayList<>();
        begin = new ArrayList<>();
        middle = new ArrayList<>();
        end = new ArrayList<>();
        ready = false;
        sortPai = 0;
        paiType = new PaiType[4];
        for (int i=0;i<4;i++)
            paiType[i] = new PaiType(PaiType.CT_INVALID,null);
//        assist = new int[4][7];
        headShui = new int[table.getPerson()];
        middleShui = new int[table.getPerson()];
        tailShui = new int[table.getPerson()];
        gaffShui = new int[table.getPerson()];
        quit = 0;
        totalScore = 0;
        winCount = 0;
        loseCount = 0;
        drawCount = 0;
        totalScore = 0;
    }

    public int[] getHeadShui(){
        return headShui;
    }
    public int[] getMiddleShui(){
        return middleShui;
    }
    public int[] getTailShui(){
        return tailShui;
    }
    public int[] getGaffShui(){
        return gaffShui;
    }


    public int getScore(int index){
        int[] shui = null;
        if (index == 0)
            shui = gaffShui;
        if (index == 1)
            shui = headShui;
        if (index == 2)
            shui = middleShui;
        if (index == 3)
            shui = tailShui;
        if (shui == null)
            return 0;
        int s = 0;
        for (int aShui : shui) s = s + aShui;
        return s;
    }

    public int jiesuan(){
        int curScore = getScore(0)+getScore(1)+getScore(2)+getScore(3);
        totalScore = totalScore + curScore;
        if (curScore > 0)
            winCount++;
        else if (curScore < 0)
            loseCount--;
        else drawCount++;
        return curScore;
    }

    public void init(){
//        for (int[] anAssist : assist)
//            Arrays.fill(anAssist, 0);
        for (PaiType aPaiType : paiType)
            aPaiType.setPaiType(PaiType.CT_INVALID, null);
        preSpeType.setPaiType(PaiType.CT_INVALID,null);
        ready = false;
        sortPai = 0;
        handCard.clear();
        begin.clear();
        middle.clear();
        end.clear();
        Arrays.fill(gaffShui,0);
        Arrays.fill(headShui,0);
        Arrays.fill(middleShui,0);
        Arrays.fill(tailShui,0);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getSortPai() {
        return sortPai;
    }

    public void setSortPai(int sortPai) {
        this.sortPai = sortPai;
    }

    public ArrayList<Integer> getHandCard() {
        return handCard;
    }

    public void setHandCard(ArrayList<Integer> handCard) {
        Collections.sort(handCard,Collections.<Integer>reverseOrder());
        this.handCard = handCard;
    }

    public ArrayList<Integer> getBegin() {
        return begin;
    }

    public void setBegin(ArrayList<Integer> begin) {
        this.begin = begin;
    }

    public ArrayList<Integer> getMiddle() {
        return middle;
    }

    public void setMiddle(ArrayList<Integer> middle) {
        this.middle = middle;
    }

    public ArrayList<Integer> getEnd() {
        return end;
    }

    public void setEnd(ArrayList<Integer> end) {
        this.end = end;
    }

    public PaiType getPaiTypeIndex(int index) {
        return paiType[index];
    }

    public void setPaiType(int index,PaiType paiType) {
        this.paiType[index] = paiType;
    }




    //清龙
//    public boolean thirteenStaightFlush(){
//        for (int i=1;i<handCard.size();i++){
//            if (handCard.get(i) + 4 != handCard.get(i-1))
//                return false;
//        }
//
//        int[] temArr = {handCard.get(0)/4};
//        assist[0] = temArr;
//        return true;
//    }

    //一条龙
//    public boolean thirteenStaight(){
//        Collections.sort(handCard);
//        for (int i=1;i<handCard.size();i++) {
//            if (handCard.get(i) / 4 - 1 != handCard.get(i-1)/4) {
//                return false;
//            }
//        }
//        int[] temArr = ArrayUtils.toPrimitive(handCard.toArray(new Integer[handCard.size()]));
//        assist[0] = temArr;
//        return true;
//    }


    //三同花顺
//    public boolean santonghuashun(){
//        ArrayList<Integer> spade = new ArrayList<Integer>();
//        ArrayList<Integer> heart = new ArrayList<Integer>();
//        ArrayList<Integer> club = new ArrayList<Integer>();
//        ArrayList<Integer> diamond = new ArrayList<Integer>();
//        for (int aHandCard : handCard) {
//            if (aHandCard % 4 == 0)
//                spade.add(aHandCard);
//            else if (aHandCard % 4 == 1)
//                heart.add(aHandCard);
//            else if (aHandCard % 4 == 2)
//                club.add(aHandCard);
//            else if (aHandCard % 4 == 3)
//                diamond.add(aHandCard);
//        }
//        ArrayList<ArrayList<Integer>> all = new ArrayList<ArrayList<Integer>>();
//        if (spade.size() != 0)
//            all.add(spade);
//        if (heart.size() != 0)
//            all.add(heart);
//        if (club.size() != 0)
//            all.add(club);
//        if (diamond.size() != 0)
//            all.add(diamond);
//        if (all.size() != 2 && all.size() != 3)
//            return false;
//        else if (all.size() == 2){
//            ArrayList<Integer> a = all.get(0);
//            ArrayList<Integer> b = all.get(1);
//            if (a.size() == 3 && b.size() == 10 || a.size() == 10 && b.size() == 3){
//                ArrayList<Integer> first,second;
//                if (a.size() > b.size()){
//                    first = b;
//                    second = a;
//                }else {
//                    first = a;
//                    second = b;
//                }
//                if (tonghuashun(-1,first)){
//                    Collections.sort(second);
//                    ArrayList<Integer> b1 = new ArrayList<Integer>();
//                    ArrayList<Integer> b2 = new ArrayList<Integer>();
//                    for (int i=0;i<5;i++){
//                        b1.add(second.get(i));
//                        b2.add(second.get(i+5));
//                    }
//                    for (int i=0)
//                    return tonghuashun(-1,b1) && tonghuashun(-1,b2);
//                }else
//                    return false;
//            }else if (a.size() == 5 && b.size() == 8 || a.size() == 8 && b.size() == 5){
//                ArrayList<Integer> first,second;
//                if (a.size() > b.size()){
//                    first = b;
//                    second = a;
//                }else {
//                    first = a;
//                    second = b;
//                }
//                if (tonghuashun(-1,first)){
//                    Collections.sort(second);
//                    ArrayList<Integer> b1 = new ArrayList<Integer>();
//                    ArrayList<Integer> b2 = new ArrayList<Integer>();
//                    ArrayList<Integer> b3 = new ArrayList<Integer>();
//                    ArrayList<Integer> b4 = new ArrayList<Integer>();
//                    for (int i=0;i<second.size();i++){
//                        if (i>2)
//                            b2.add(second.get(i));
//                        else
//                            b1.add(second.get(i));
//                        if (i>4)
//                            b4.add(second.get(i));
//                        else
//                            b3.add(second.get(i));
//                    }
//                    return tonghuashun(-1,b1) && tonghuashun(-1,b2) || tonghuashun(-1,b3) && tonghuashun(-1,b4);
//                }else
//                    return false;
//            }else
//                return false;
//        }else if (all.size() == 3){
//            ArrayList<Integer> a = all.get(0);
//            ArrayList<Integer> b = all.get(1);
//            ArrayList<Integer> c = all.get(2);
//            if (tonghuashun(-1,a) && tonghuashun(-1,b) && tonghuashun(-1,c))
//                return true;
//            return false;
//        }else
//            return false;
//    }

    //三分天下
//    public boolean sanfentianxia(){
//        int[] c = new int[13];
//        for (int aHandCard : handCard)
//            c[aHandCard / 4]++;
//        ArrayList<Integer> m = new ArrayList<Integer>();
//        ArrayList<Integer> k = new ArrayList<Integer>();
//        ArrayList<Integer> n = new ArrayList<Integer>();
//        for (int i=0;i<13;i++) {
//            if (c[i] == 4) {
//                m.add(i);
//            }
//            if (c[i] == 5)
//                k.add(i);
//            if (c[i] == 1)
//                n.add(i);
//        }
//        if (m.size() == 3 && n.size() == 1&&k.size() == 0){
//            Collections.sort(m,Collections.<Integer>reverseOrder());
//            assist[0] =m.get(0);
//            return true;
//        }else if (m.size() == 2 && k.size() == 1 && n.size() == 0){
//            assist[0] = k.get(0)*13;
//            return true;
//        }else
//            return false;
//    }

//    //四套三条
//    public boolean sitaosantiao(){
//        int[] c = new int[13];
//        int n = -1;
//        for (int a : handCard)
//            c[a / 4]++;
//        ArrayList<Integer> m = new ArrayList<Integer>();
//        for (int i=0;i<13;i++){
//            if (c[i] == 3) {
//                m.add(i);
//            }
//            if (c[i] == 1)
//                n = i;
//        }
//        if (m.size() == 4 && n > -1){
//            Collections.sort(m);
//            m.add(0,n);
//            for (int i=0;i<m.size();i++){
//                assist[0] = assist[0] + m.get(i)*(int)Math.pow(13,i);
//            }
//            return true;
//        }else
//            return false;
//    }

//    //六对半
//    public boolean liuduiban(){
//        int[] c = new int[13];
//        int k = -1;
//        for (int a :handCard)
//            c[a / 4]++;
//        ArrayList<Integer> m = new ArrayList<Integer>();
//        ArrayList<Integer> n = new ArrayList<Integer>();
//        for (int i=0;i<13;i++){
//            if (c[i] == 4)
//                n.add(i);
//            if (c[i] == 2)
//                m.add(i);
//            if (c[i] == 1)
//                k = i;
//        }
//        if ((n.size() == 0 && m.size() == 6 && k > -1) || (n.size() == 1 && m.size() == 4 && k > -1) || (n.size() == 2 && m.size() == 2 && k > -1)){//匹配六对半
//           Collections.sort(n);
//           Collections.sort(m);
//           m.addAll(n);
//           m.add(0,k);
//           for (int i=0;i<m.size();i++){
//               assist[0] = assist[0] + m.get(i)*Math.pow(13,i);
//           }
//           return true;
//        }else
//            return false;
//    }
//
//    public boolean isTieZhi(){
//        int[] c = new int[13];
//        int k = -1;
//        for (int a :handCard)
//            c[a / 4]++;
//        ArrayList<Integer> n = new ArrayList<Integer>();
//        for (int i=0;i<13;i++){
//            if (c[i] == 4)
//                n.add(i);
//        }
//        return n.size() > 0;
//    }

//    //三顺子
//    public boolean sanshunzi(){
//
//        int[] n = new int[14];
//        int a = 0;
//        for (int i=0;i<handCard.size();i++) {
//            if (handCard.get(i)/4 == 12)
//                a++;
//            else
//                n[handCard.get(i) / 4 + 1]++;
//        }
//        if (a == 0){
//            n[0] = 0;
//            n[13] = 0;
//            if (fuzhuSanShunZi(n))
//                return true;
//        }else if (a == 1){
//            n[0] = 1;
//            n[13] = 0;
//            if (fuzhuSanShunZi(n))
//                return true;
//            n[0] = 0;
//            n[13] = 1;
//            if (fuzhuSanShunZi(n))
//                return true;
//        }else if (a == 2){
//            n[0] = 2;
//            n[13] = 0;
//            if (fuzhuSanShunZi(n))
//                return true;
//            n[0] = 1;
//            n[13] = 1;
//            if (fuzhuSanShunZi(n))
//                return true;
//            n[0] = 0;
//            n[13] = 2;
//            if (fuzhuSanShunZi(n))
//                return true;
//
//        }else if (a == 3){
//            n[0] = 3;
//            n[13] = 0;
//            if (fuzhuSanShunZi(n))
//                return true;
//            n[0] = 2;
//            n[13] = 1;
//            if (fuzhuSanShunZi(n))
//                return true;
//            n[0] = 1;
//            n[13] = 2;
//            if (fuzhuSanShunZi(n))
//                return true;
//            n[0] = 0;
//            n[13] = 3;
//            if (fuzhuSanShunZi(n))
//                return true;
//        }
//        return false;
//    }




//    //三同花
//    public boolean santonghua(){
//        ArrayList<Integer> spade = new ArrayList<Integer>();
//        ArrayList<Integer> heart = new ArrayList<Integer>();
//        ArrayList<Integer> club = new ArrayList<Integer>();
//        ArrayList<Integer> diamond = new ArrayList<Integer>();
//        for (Integer aHandCard : handCard) {
//            if (aHandCard % 4 == 0)
//                spade.add(aHandCard);
//            else if (aHandCard % 4 == 1)
//                heart.add(aHandCard);
//            else if (aHandCard % 4 == 2)
//                club.add(aHandCard);
//            else if (aHandCard % 4 == 3)
//                diamond.add(aHandCard);
//        }
//        ArrayList<ArrayList<Integer>> all = new ArrayList<ArrayList<Integer>>();
//        if (spade.size() != 0)
//            all.add(spade);
//        if (heart.size() != 0)
//            all.add(heart);
//        if (club.size() != 0)
//            all.add(club);
//        if (diamond.size() != 0)
//            all.add(diamond);
//        if (all.size() == 2) {
//            ArrayList<Integer> a = all.get(0);
//            ArrayList<Integer> b = all.get(1);
//            if (a.size() == 3 && b.size() == 10 || a.size() == 10 && b.size() == 3) {
//                ArrayList<Integer> temp;
//                if (a.size() > b.size())
//                    temp = a;
//                else temp = b;
//                Collections.sort(temp);
//                ArrayList<Integer> k1 = new ArrayList<Integer>();
//                ArrayList<Integer> k2 = new ArrayList<Integer>();
//                for (int i=0;i<temp.size()/2;i++){
//                    k1.add(temp.get(i));
//                    k2.add(temp.get(i+5));
//                }
//                if (tonghuashun(-1,k1) || tonghuashun(-1,k2)){
//                    assist[0] = 1;
//                }
//                assist[0] = 0;
//                return true;
//            } else if (a.size() == 5 && b.size() == 8 || a.size() == 8 && b.size() == 5) {
//                ArrayList<Integer> temp;
//                if (a.size() > b.size())
//                    temp = a;
//                else temp = b;
//                Collections.sort(temp);
//                ArrayList<Integer> k1 = new ArrayList<Integer>();
//                ArrayList<Integer> k2 = new ArrayList<Integer>();
//                ArrayList<Integer> k3 = new ArrayList<Integer>();
//                ArrayList<Integer> k4 = new ArrayList<Integer>();
//                for (int i=0;i<temp.size();i++){
//                    if (i>3)
//                        k1.add(temp.get(i));
//                    else k2.add(temp.get(i));
//                    if (i>5)
//                        k3.add(temp.get(i));
//                    else k4.add(temp.get(i));
//                }if (tonghuashun(-1,k1) || tonghuashun(-1,k2) || tonghuashun(-1,k3) || tonghuashun(-1,k4))
//                    assist[0] = 1;
//                assist[0] = 0;
//                return true;
//            }else
//                return false;
//        }else if (all.size() == 3){
//            ArrayList<Integer> a = all.get(0);
//            ArrayList<Integer> b = all.get(1);
//            ArrayList<Integer> c = all.get(2);
//            if (a.size() == 3 && b.size() == 5 && c.size() == 5 ||
//                    a.size() == 5 && b.size() == 3 && c.size() == 5 ||
//                    a.size() == 5 && b.size() == 5 && c.size() == 3) {
//                if (tonghuashun(-1,a) || tonghuashun(-1,b) || tonghuashun(-1,c))
//                    assist[0] = 1;
//                assist[0] = 0;
//                return true;
//            }else
//                return false;
//        }else
//            return false;
//    }



    //同花顺判定
//    public boolean straightFlush(int index,ArrayList<Integer> temp){
//        Collections.sort(temp,Collections.<Integer>reverseOrder());
//        if (temp.size() != 5)
//            return false;
//        for (int i=1;i<temp.size();i++)
//            if (temp.get(i) % 4 != temp.get(i-1) % 4)//不是同花
//                return false;
//        //A,1,2,3,4,为顺
//        if ((temp.get(0)/4 == 12 && temp.get(1)/4 == 3 && temp.get(2)/4 == 2 && temp.get(3)/4 == 1 && temp.get(4)/4 == 0) ||
//                (temp.get(0)/4 == temp.get(1)/4+1 && temp.get(1)/4 == temp.get(2)/4+1 && temp.get(2)/4 == temp.get(3)/4+1&&
//                temp.get(3)/4 == temp.get(4)/4+1)) {
//            assist[index] = ArrayUtils.toPrimitive(temp.toArray(new Integer[temp.size()]));
//            return true;
//        }
//        return false;
//    }

    //顺子
//    public boolean straight (int index,ArrayList<Integer> temp){
//        Collections.sort(temp,Collections.<Integer>reverseOrder());
//        if (temp.size() != 5)
//            return false;
//        if ((temp.get(0)/4 == 12 && temp.get(1)/4 == 3 && temp.get(2)/4 == 2 && temp.get(3)/4 == 1 && temp.get(4)/4 == 0) ||
//                (temp.get(0)/4 == temp.get(1)/4+1 && temp.get(1)/4 == temp.get(2)/4+1 && temp.get(2)/4 == temp.get(3)/4+1&&
//                        temp.get(3)/4 == temp.get(4)/4+1)) {
//            assist[index] = ArrayUtils.toPrimitive(temp.toArray(new Integer[temp.size()]));
//            return true;
//        }
//        return false;
//    }
    //五同
//    public boolean fiveSame(int index,ArrayList<Integer> temp){
//        if (temp.size() != 5)
//            return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp)
//            n.get(a/4).add(a);
//        for (int i=0;i<n.size();i++){
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 5){
//                int[] tempArr = {i};
//                assist[index] = tempArr;
//                return true;
//            }
//        }
//        return false;
//    }

    //铁支
//    public boolean fourSame(int index,ArrayList<Integer> temp) {
//        if (temp.size() != 5)
//            return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp) {
//            n.get(a/4).add(a);
//        }
//        ArrayList<Integer> p = new ArrayList<Integer>(),s = new ArrayList<Integer>();
//        for (int i = 0; i < n.size(); i++) {
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 4)
//                p = a;
//            else if (a.size() == 1)
//                s = a;
//        }
//        if ( p.size() == 4 && s.size() == 1) {
//            int[] temArr = {p.get(0)/4,s.get(0)};
//            assist[index] = temArr;
//            return true;
//        }
//        return false;
//    }

    //葫芦
//    public boolean gourd(int index,ArrayList<Integer> temp){
//        if (temp.size() != 5)
//            return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp) {
//            n.get(a/4).add(a);
//        }
//        ArrayList<Integer> threeSame = new ArrayList<Integer>(),twoSame = new ArrayList<Integer>();
//        for (int i=0;i<n.size();i++){
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 3)
//                threeSame = a;
//            else if (a.size() == 2)
//                twoSame = a;
//        }
//        if (threeSame.size() == 3 && twoSame.size() == 2){
//            int[] temArr = {threeSame.get(0)/4,twoSame.get(0)/4};
//            assist[index] = temArr;
//            return true;
//        }
//        return false;
//    }

    //同花
//    public boolean flush(int index,ArrayList<Integer> temp){
//        if (temp.size() != 5)
//            return false;
//        for (int i=1;i<temp.size();i++)
//            if (temp.get(i)%4 != temp.get(0)%4)
//                return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp) {
//            n.get(a/4).add(a);
//        }
//        ArrayList<Integer> m = new ArrayList<Integer>();
//        ArrayList<Integer> s = new ArrayList<Integer>();
//        for (int i=0;i<n.size();i++){
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 2)
//                m.add(i);
//            else if (a.size() == 1)
//                s.add(a.get(0));
//        }
//        if (m.size() > 1)
//            Collections.sort(m,Collections.<Integer>reverseOrder());
//        Collections.sort(s,Collections.<Integer>reverseOrder());
//        if (m.size() == 0){
//            int[] temArr = new int[2+s.size() + 1];
//            temArr[0] = -1;
//            temArr[1] = -1;
//            for (int i=0;i<s.size();i++){
//                temArr[2+i] = s.get(i)/4;
//            }
//            temArr[temArr.length - 1] = s.get(0)%4;
//            assist[index] = temArr;
//        }else if (m.size() == 1){
//            int[] temArr = new int[2+s.size() + 1];
//            temArr[0] = -1;
//            temArr[1] = m.get(0);
//            for (int i=0;i<s.size();i++)
//                temArr[2+i] = s.get(i)/4;
//            temArr[temArr.length - 1] = s.get(0)%4;
//            assist[index] = temArr;
//        }else if (m.size() == 2){
//            int[] temArr = new int[2+s.size() + 1];
//            temArr[0] = m.get(0);
//            temArr[1] = m.get(1);
//            for (int i=0;i<s.size();i++)
//                temArr[2+i] = s.get(i)/4;
//            temArr[temArr.length - 1] = s.get(0)%4;
//            assist[index] = temArr;
//        }
//        return true;
//    }

    //三条
//    public boolean threeSame(int index,ArrayList<Integer> temp){
//        if (temp.size() != 5 && temp.size() !=3)
//            return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp) {
//            n.get(a/4).add(a);
//        }
//        ArrayList<Integer> m = new ArrayList<Integer>();
//        ArrayList<Integer> k = new ArrayList<Integer>();
//        for (int i=0;i<n.size();i++){
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 3)
//                m.add(i);
//            if (a.size() == 1)
//                k.add(a.get(0));
//        }
//        if (m.size() == 1 && (k.size() == 0 || k.size() == 2)){
//            if (k.size() == 2)
//                Collections.sort(k,Collections.<Integer>reverseOrder());
//            if (k.size() == 0){
//                int[] temArr = {m.get(0)};
//                assist[index] = temArr;
//            }else if (k.size() == 2){
//                int[] temArr = {m.get(0),k.get(0),k.get(1)};
//                assist[index] = temArr;
//            }
//            return true;
//        }
//        return false;
//    }

    //两对
//    public boolean twoPair(int index,ArrayList<Integer> temp){
//        if (temp.size() != 5)
//            return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp) {
//            n.get(a/4).add(a);
//        }
//       ArrayList<Integer> m = new ArrayList<Integer>();
//        ArrayList<Integer> k = new ArrayList<Integer>();
//        for (int i=0;i<n.size();i++){
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 2)
//                m.add(i);
//            if (a.size() == 1)
//                k.add(a.get(0));
//        }
//        if (m.size() == 2 && k.size() == 1){
//            Collections.sort(m,Collections.<Integer>reverseOrder());
//            int[] temArr = {m.get(0),m.get(1),k.get(0)};
//            assist[index] = temArr;
//            return true;
//        }
//        return false;
//    }
    //对子
//    public boolean onePair(int index,ArrayList<Integer> temp){
//        if (temp.size() != 5 && temp.size() != 3)
//            return false;
//        ArrayList<ArrayList<Integer>> n = new ArrayList<ArrayList<Integer>>();
//        for (int i=0;i<13;i++){
//            n.add(new ArrayList<Integer>());
//        }
//        for (int a : temp) {
//            n.get(a/4).add(a);
//        }
//        ArrayList<Integer> m = new ArrayList<Integer>();
//        ArrayList<Integer> k = new ArrayList<Integer>();
//        for (int i=0;i<n.size();i++){
//            ArrayList<Integer> a = n.get(i);
//            if (a.size() == 2)
//                m.add(i);
//            if (a.size() == 1)
//                k.add(a.get(0));
//        }
//        if (m.size() == 1 && (k.size() == 1 || k.size() == 3)){
//            if (k.size() == 3)
//                Collections.sort(k,Collections.<Integer>reverseOrder());
//            if (k.size() == 1){
//                int[] temArr = {m.get(0),k.get(0)};
//                assist[index] = temArr;
//            }else if (k.size() == 3){
//                int[] temArr = {m.get(0),k.get(0),k.get(1),k.get(2)};
//                assist[index] = temArr;
//            }
//            return true;
//        }
//        return false;
//    }

    //乌龙
//    public void none(int index, ArrayList<Integer> temp){
//        Collections.sort(temp,Collections.<Integer>reverseOrder());
//        assist[index] = ArrayUtils.toPrimitive(temp.toArray(new Integer[temp.size()]));
//    }


    public PaiType getPreSpeType() {
        return preSpeType;
    }

    public void setPreSpeType(PaiType preSpeType) {
        this.preSpeType = preSpeType;
    }

    public int getQuit() {
        return quit;
    }

    public void setQuit(int quit) {
        this.quit = quit;
    }

}
