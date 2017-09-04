package sfs2x.model;

import org.apache.commons.lang.ArrayUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GameLogic {
    private static int[][] allStraight= new int[][]{{0,1,2,3,4},{1,2,3,4,5},{2,3,4,5,6},{3,4,5,6,7},{4,5,6,7,8},{5,6,7,8,9},{6,7,8,9,10},
            {7,8,9,10,11},{8,9,10,11,12},{0,1,2,3,12}};
    //获取n个元素的组合
    private static void getCombine(ArrayList<Integer> t, ArrayList<Integer> list, int dataIndex, int[] resultList, int resultIndex) {
        int resultCount = resultIndex + 1;
        if (resultCount > resultList.length) {
            int[] temp = new int[resultList.length];
            System.arraycopy(resultList, 0, temp, 0, resultList.length);
            Arrays.sort(temp);
            if (t.size() == 0) {
                for (int i : temp)
                    t.add(i);
                return;
            }
            boolean add = true;
            for (int i = 0; i + temp.length <= t.size(); i = i + temp.length) {
                boolean b = true;
                for (int j = 0; j < temp.length; j++) {
                    if (t.get(i + j) != temp[j]) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    add = false;
                    break;
                }
            }
            if (add) {
                for (int i : temp)
                    t.add(i);
            }
            return;
        }

        for (int i = dataIndex; i < list.size() + resultCount - resultList.length; i++) {
            resultList[resultIndex] = list.get(i);
            getCombine(t, list, i + 1, resultList, resultIndex + 1);
        }
    }

    public static TilesType getType(ArrayList<Integer> hand) {
        TilesType type = new TilesType();
        ArrayList<ArrayList<Integer>> n = new ArrayList<>();
        for (int i = 0; i < 13; i++)
            n.add(new ArrayList<Integer>());
        for (int i : hand) {
            n.get(i / 4).add(i);
        }
        for (int i = n.size() - 1; i >= 0; i--) {
            if (n.get(i).size() == 1) {
                type.aSingle.add(n.get(i).get(0));
            }
            if (n.get(i).size() > 1) {//一对

//                for (Integer aTempSame : tempSame) {
//                    if (!same.contains(aTempSame))
//                        same.add(aTempSame);
//                }

                ArrayList<Integer> a = new ArrayList<>();
                int[] result = new int[2];
                getCombine(a, n.get(i), 0, result, 0);
                type.aOnePair.addAll(a);
            }
            if (n.get(i).size() > 2) {//三条
                ArrayList<Integer> a = new ArrayList<>();
                int[] result = new int[3];
                getCombine(a, n.get(i), 0, result, 0);
                type.aThreeSame.addAll(a);
            }
            if (n.get(i).size() > 3) {//铁支
                ArrayList<Integer> tempSame = new ArrayList<>();
                for (Integer aHand : hand) {
                    if (aHand / 4 == i) {
                        tempSame.add(aHand);
                    }
                }
                ArrayList<Integer> a = new ArrayList<>();
                int[] result = new int[4];
                getCombine(a, n.get(i), 0, result, 0);
                type.aFourSame.addAll(a);
            }
            if (n.get(i).size() > 4) {//五同
                ArrayList<Integer> a = new ArrayList<>();
                int[] result = new int[5];
                getCombine(a, n.get(i), 0, result, 0);
                type.aFiveSame.addAll(a);
            }
        }

        //两对
        if (type.aOnePair.size() / 2 >= 2) {
            int[] temp = new int[type.aOnePair.size() / 2];
            for (int i = 0; i < temp.length; i++)
                temp[i] = i;
            for (int i = 0; i < temp.length; i++) {
                for (int j = i + 1; j < temp.length; j++) {
                    if (type.aOnePair.get(i * 2) / 4 == type.aOnePair.get(j * 2) / 4)
                        continue;
                    type.aTwoPair.add(type.aOnePair.get(i * 2));
                    type.aTwoPair.add(type.aOnePair.get(i * 2 + 1));
                    type.aTwoPair.add(type.aOnePair.get(j * 2));
                    type.aTwoPair.add(type.aOnePair.get(j * 2 + 1));
                }
            }
        }

        //葫芦
        for (int i = n.size() - 1; i >= 0; i--) {
            if (n.get(i).size() == 3) {
                for (int k = 0; k < n.size(); k++) {
                    if (k != i && n.get(k).size() == 2) {
                        type.aGourd.addAll(n.get(i));
                        type.aGourd.addAll(n.get(k));
                    }
                }
            }
        }
        for (int i = n.size() - 1; i >= 0; i--) {
            if (n.get(i).size() >= 3) {
                ArrayList<Integer> three = new ArrayList<>();
                int[] temp = new int[3];
                getCombine(three, n.get(i), 0, temp, 0);
                for (int j = 0; j + 3 <= three.size(); j = j + 3) {
                    for (int k = 0; k < n.size(); k++) {
                        if (k != i && (( n.get(i).size() == 3 && n.get(k).size() > 2) ||(n.get(i).size() > 3 && n.get(k).size() > 1 ))) {
                            ArrayList<Integer> two = new ArrayList<>();
                            int[] temp1 = new int[2];
                            getCombine(two, n.get(k), 0, temp1, 0);
                            for (int m = 0; m + 2 <= two.size(); m = m + 2) {
                                type.aGourd.add(three.get(j));
                                type.aGourd.add(three.get(j + 1));
                                type.aGourd.add(three.get(j + 2));
                                type.aGourd.add(two.get(m));
                                type.aGourd.add(two.get(m + 1));
                            }
                        }
                    }
                }
            }
        }


        //同花及同花顺
        ArrayList<Integer> spade = new ArrayList<>();
        ArrayList<Integer> heart = new ArrayList<>();
        ArrayList<Integer> club = new ArrayList<>();
        ArrayList<Integer> diamond = new ArrayList<>();
        for (Integer aHand : hand) {
            if (aHand % 4 == 0)
                spade.add(aHand);
            else if (aHand % 4 == 1)
                heart.add(aHand);
            else if (aHand % 4 == 2)
                club.add(aHand);
            else if (aHand % 4 == 3)
                diamond.add(aHand);
        }
        Flush(type, spade);
        Flush(type, heart);
        Flush(type, club);
        Flush(type, diamond);

        for (int i=type.aFlush.size()/5 - 1 ;i>=0;i--){
            for (int j=0;j<i;j++){
                ArrayList<ArrayList<Integer>> nJ = new ArrayList<>();
                ArrayList<ArrayList<Integer>> nJ1 = new ArrayList<>();
                ArrayList<Integer> pairJ = new ArrayList<>();
                ArrayList<Integer> singleJ = new ArrayList<>();
                ArrayList<Integer> pairJ1 = new ArrayList<>();
                ArrayList<Integer> singleJ1 = new ArrayList<>();

                for (int k=0;k<13;k++) {
                    nJ.add(new ArrayList<Integer>());
                    nJ1.add(new ArrayList<Integer>());
                }
                for (int k=0;k<5;k++){
                    nJ.get(type.aFlush.get(5*j+k)/4).add(type.aFlush.get(5*j+k));
                    nJ1.get(type.aFlush.get(5*(j+1)+k)/4).add(type.aFlush.get(5*(j+1)+k));
                }
                for (int k=12;k>=0;k--){
                    if (nJ.get(k).size() == 2)
                        pairJ.add(k);
                    if (nJ.get(k).size() == 1)
                        singleJ.add(nJ.get(k).get(0));
                    if (nJ1.get(k).size() == 2)
                        pairJ1.add(k);
                    if (nJ1.get(k).size() == 1)
                        singleJ1.add(nJ1.get(k).get(0));
                }
                int[] arrJ,arrJ1;
                if (pairJ.size() == 2 && singleJ.size() == 1)
                    arrJ = new int[]{pairJ.get(0),pairJ.get(1),singleJ.get(0)};
                else if (pairJ.size() == 1 && singleJ.size() == 3)
                    arrJ = new int[]{-1,pairJ.get(0),singleJ.get(0)/4,singleJ.get(1)/4,singleJ.get(2)/4,singleJ.get(0)%4};
                else arrJ = new int[]{-1,-1,singleJ.get(0)/4,singleJ.get(1)/4,
                            singleJ.get(2)/4,
                            singleJ.get(3)/4,singleJ.get(4)/4,singleJ.get(0)%4};
                if (pairJ1.size() == 2 && singleJ1.size() == 1)
                    arrJ1 = new int[]{pairJ1.get(0),pairJ1.get(1),singleJ1.get(0)};
                else if (pairJ1.size() == 1 && singleJ1.size() == 3)
                    arrJ1 = new int[]{-1,pairJ1.get(0),singleJ1.get(0)/4,singleJ1.get(1)/4,singleJ1.get(2)/4,singleJ1.get(0)%4};
                else arrJ1 = new int[]{-1,-1,singleJ1.get(0)/4,singleJ1.get(1)/4,singleJ1.get(2)/4,singleJ1.get(3)/4,singleJ1.get(4)/4,singleJ1.get(0)%4};
                if (assistCompare(arrJ,arrJ1) < 0){
                    int temp1 = type.aFlush.get(5*j);
                    int temp2 = type.aFlush.get(5*j+1);
                    int temp3 = type.aFlush.get(5*j+2);
                    int temp4 = type.aFlush.get(5*j+3);
                    int temp5 = type.aFlush.get(5*j+4);

                    type.aFlush.set(5*j,type.aFlush.get(5*(j+1)));
                    type.aFlush.set(5*j+1,type.aFlush.get(5*(j+1)+1));
                    type.aFlush.set(5*j+2,type.aFlush.get(5*(j+1)+2));
                    type.aFlush.set(5*j+3,type.aFlush.get(5*(j+1)+3));
                    type.aFlush.set(5*j+4,type.aFlush.get(5*(j+1)+4));

                    type.aFlush.set(5*(j+1),temp1);
                    type.aFlush.set(5*(j+1)+1,temp2);
                    type.aFlush.set(5*(j+1)+2,temp3);
                    type.aFlush.set(5*(j+1)+3,temp4);
                    type.aFlush.set(5*(j+1)+4,temp5);
                }
            }
        }


        //顺子
        for (int i = n.size() - 1; i >= 0; i--) {
            boolean flush1 = i - 4 >= 0 && n.get(i).size() > 0 && n.get(i - 1).size() > 0 && n.get(i - 2).size() > 0 && n.get(i - 3).size() > 0 && n.get(i - 4).size() > 0;
            boolean flush2 = i - 4 > 0 && i == 12 && n.get(0).size() > 0 && n.get(1).size() > 0 && n.get(2).size() > 0 && n.get(3).size() > 0;
            if (flush1) {
                ArrayList<ArrayList<Integer>> list = new ArrayList<>();
                for (int j = 0; j < 5; j++)
                    list.add(new ArrayList<Integer>());
                for (int value : n.get(i))
                    if (!list.get(0).contains(value))
                        list.get(0).add(value);
                for (int value : n.get(i - 1))
                    if (!list.get(1).contains(value))
                        list.get(1).add(value);
                for (int value : n.get(i - 2))
                    if (!list.get(2).contains(value))
                        list.get(2).add(value);
                for (int value : n.get(i - 3))
                    if (!list.get(3).contains(value))
                        list.get(3).add(value);
                for (int value : n.get(i - 4))
                    if (!list.get(4).contains(value))
                        list.get(4).add(value);
                ArrayList<Integer> result = new ArrayList<>();
                int[] temp = new int[list.size()];
                straight(result, temp, list, 0);
                type.aStraight.addAll(result);
            }
            if (flush2) {
                ArrayList<ArrayList<Integer>> list = new ArrayList<>();
                for (int j = 0; j < 5; j++)
                    list.add(new ArrayList<Integer>());
                for (int value : n.get(12))
                    if (!list.get(0).contains(value))
                        list.get(0).add(value);
                for (int value : n.get(0))
                    if (!list.get(1).contains(value))
                        list.get(1).add(value);
                for (int value : n.get(1))
                    if (!list.get(2).contains(value))
                        list.get(2).add(value);
                for (int value : n.get(2))
                    if (!list.get(3).contains(value))
                        list.get(3).add(value);
                for (int value : n.get(3))
                    if (!list.get(4).contains(value))
                        list.get(4).add(value);
                ArrayList<Integer> result = new ArrayList<>();
                int[] temp = new int[list.size()];
                straight(result, temp, list, 0);
                type.aStraight.addAll(result);
            }
        }
        type.bSingle = type.aSingle.size() > 0;
        type.bStraight = type.aStraight.size() > 0;
        type.bFlush = type.aFlush.size() > 0;
        type.bStraightFlush = type.aStraightFlush.size() > 0;
        type.bGourd = type.aGourd.size() > 0;
        type.bTwoPair = type.aTwoPair.size() > 0;
        type.bFiveSame = type.aFiveSame.size() > 0;
        type.bFourSame = type.aFourSame.size() > 0;
        type.bThreeSame = type.aThreeSame.size() > 0;
        type.bOnePair = type.aOnePair.size() > 0;
        return type;
    }

    //判断同花中含有5个同花 及同花顺
    private static void Flush(TilesType type, ArrayList<Integer> flush) {
        if (flush.size() >= 5) {
            Collections.sort(flush, Collections.<Integer>reverseOrder());
            int[] temp = new int[5];
            ArrayList<Integer> tempList = new ArrayList<>();
            getCombine(tempList, flush, 0, temp, 0);
            for (int i = 0; i + 5 <= tempList.size(); i = i + 5) {
                ArrayList<Integer> flush5 = new ArrayList<>();
                flush5.add(tempList.get(i));
                flush5.add(tempList.get(i + 1));
                flush5.add(tempList.get(i + 2));
                flush5.add(tempList.get(i + 3));
                flush5.add(tempList.get(i + 4));
                Collections.sort(flush5, Collections.<Integer>reverseOrder());
                if ((flush5.get(0) / 4 == flush5.get(1) / 4 + 1 &&
                        flush5.get(1) / 4 == flush5.get(2) / 4 + 1 &&
                        flush5.get(2) / 4 == flush5.get(3) / 4 + 1 &&
                        flush5.get(3) / 4 == flush5.get(4) / 4 + 1) || (
                        flush5.get(0) / 4 == 12 && flush5.get(1) / 4 == 3 && flush5.get(2) / 4 == 2 && flush5.get(3) / 4 == 1 &&
                                flush5.get(4) / 4 == 0)) {
                    type.aStraightFlush.addAll(flush5);

                } else {
                    type.aFlush.addAll(flush5);
                }
            }
        }
    }

    private static int assistCompare(int[] a, int[] b) {
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

    private static void straight(ArrayList<Integer> result, int[] temp, ArrayList<ArrayList<Integer>> list, int index) {
        if (index < list.size()) {
            ArrayList<Integer> arrayList = list.get(index);
            for (int a : arrayList) {
                temp[index] = a;
                straight(result, temp, list, index + 1);
            }
        } else {
//            boolean add = true;
            for (int i = 1; i < temp.length; i++) {
                if (temp[i] % 4 != temp[0] % 4) {
                    for (int aTemp : temp)
                        result.add(aTemp);
                }
            }
        }
    }

    public static PaiType getCardType(ArrayList<Integer> temp){
        Collections.sort(temp,Collections.<Integer>reverseOrder());
        ArrayList<ArrayList<Integer>> n = new ArrayList<>();
        for (int i=0;i<13;i++){
            n.add(new ArrayList<Integer>());
        }
        for (int a : temp) {
            n.get(a/4).add(a);
        }
        switch (temp.size()){
            case 3:
                //三条
                if (temp.get(0)/4 == temp.get(1)/4 &&temp.get(0)/4 == temp.get(2)/4){
                    return new PaiType(PaiType.THREE_SAME,new int[]{temp.get(0)/4});
                }
                //两对
                if (temp.get(0)/4 == temp.get(1)/4){
                    return new PaiType(PaiType.ONE_PAIR,new int[]{temp.get(0)/4,temp.get(2)});
                }
                if (temp.get(0)/4 == temp.get(2)/4){
                    return new PaiType(PaiType.ONE_PAIR,new int[]{temp.get(0)/4,temp.get(1)});
                }
                if (temp.get(1)/4 == temp.get(2)/4){
                    return new PaiType(PaiType.ONE_PAIR,new int[]{temp.get(1)/4,temp.get(0)});
                }
                //乌龙
                return new PaiType(PaiType.CT_VOID,new int[]{temp.get(0)/4,temp.get(1)/4,temp.get(2)/4,temp.get(0),temp.get(1),temp.get(2)});
            case 4:
                ArrayList<Integer> fourSame = new ArrayList<>();
                ArrayList<Integer> pair1 = new ArrayList<>();
                for (int i=n.size() - 1;i>=0;i--){
                    if (n.get(i).size() == 4)
                        fourSame.add(i);
                    if (n.get(i).size() == 2)
                        pair1.add(i);
                }
                if (fourSame.size() == 1)
                    return new PaiType(PaiType.FOUR_SAME,new int[]{fourSame.get(0)});
                else
                    return new PaiType(PaiType.TWO_PAIR,new int[]{pair1.get(0),pair1.get(1)});
            case 5:
                //五同
                if (temp.get(0)/4 == temp.get(1)/4 && temp.get(0)/4 == temp.get(2)/4 && temp.get(0) /4 == temp.get(3)/4 &&
                        temp.get(0)/4 == temp.get(4)/4)
                    return new PaiType(PaiType.FIVE_SAME,new int[]{temp.get(0)/4});
                //同花顺
                boolean straight = true;
                boolean flush = true;
                for (int i=0;i+1<temp.size();i++){
                    if (temp.get(i)%4 != temp.get(i+1)%4)
                        flush = false;
                    if (temp.get(i)/4 == 12){
                        if (temp.get(i+1)/4 != 3 && temp.get(i+1)/4 != 11)
                            straight = false;
                    }else {
                        if (temp.get(i)/4 != temp.get(i+1)/4 +1 )
                            straight = false;
                    }
                    if (!flush &&!straight)
                        break;
                }
                if (straight && flush)
                    return new PaiType(PaiType.STRAIGHT_FLUSH,new int[]{temp.get(0)/4,temp.get(1)/4,temp.get(0)%4});

                ArrayList<Integer> k1 = new ArrayList<>();
                ArrayList<Integer> k2 = new ArrayList<>();
                ArrayList<Integer> k3 = new ArrayList<>();
                ArrayList<Integer> k4 = new ArrayList<>();
                for (int i=n.size() - 1;i >=0;i--){
                    if (n.get(i).size() == 3)
                        k3.add(i);
                    else if (n.get(i).size() == 2)
                        k2.add(i);
                    else if (n.get(i).size() == 4)
                        k4.add(i);
                    else if (n.get(i).size() == 1)
                        k1.add(n.get(i).get(0));
                }
                //铁支
                if (k4.size() == 1 && k1.size() == 1)
                    return new PaiType(PaiType.FOUR_SAME,new int[]{k4.get(0),k1.get(0)});
                //葫芦
                if ( k3.size() == 1 && k2.size() == 1)
                    return new PaiType(PaiType.GOURD,new int[]{k3.get(0),k2.get(0)});
                //同花
                if (flush){
                    ArrayList<Integer> pair = new ArrayList<>();
                    ArrayList<Integer> single = new ArrayList<>();
                    for (int i=n.size() - 1;i>=0;i--){
                        if (n.get(i).size() == 2)
                            pair.add(i);
                        else if (n.get(i).size() == 1)
                            single.add(n.get(i).get(0));
                    }
                    if (pair.size() == 2 &&single.size() == 1){
                        return new PaiType(PaiType.FLUSH,new int[]{pair.get(0),pair.get(1),single.get(0)});
                    }else if (pair.size() == 1 && single.size() == 3){
                        return new PaiType(PaiType.FLUSH,new int[]{-1,pair.get(0),single.get(0)/4,single.get(1)/4,single.get(2)/4,single.get(0)%4});
                    }else if (pair.size() == 0 && single.size() == 5)
                        return new PaiType(PaiType.FLUSH,new int[]{-1,-1,single.get(0)/4,single.get(1)/4,single.get(2)/4,single.get(3)/4,single.get(4)/4,single.get(0)%4});
                }
                //顺子
                if (straight){
                    return new PaiType(PaiType.STRAIGHT,new int[]{temp.get(0)/4,temp.get(1)/4,temp.get(0),temp.get(1),temp.get(2),temp.get(3),temp.get(4)});
                }
                //三条
                if (k3.size() == 1 && k1.size() == 2){
                    return new PaiType(PaiType.THREE_SAME,new int[]{k3.get(0),k1.get(0)/4,k1.get(1)/4,k1.get(0),k1.get(1)});
                }
                //两对
                if (k2.size() == 2 && k1.size() == 1){
                    return new PaiType(PaiType.TWO_PAIR,new int[]{k2.get(0),k2.get(1),k1.get(0)});
                }
                //一对
                if (k2.size() == 1 && k1.size() == 3)
                    return new PaiType(PaiType.ONE_PAIR,new int[]{k2.get(0),k1.get(0)/4,k1.get(1)/4,k1.get(2)/4,k1.get(0),k1.get(1),k1.get(2)});
                //乌龙
                return new PaiType(PaiType.CT_VOID,new int[]{k1.get(0)/4,k1.get(1)/4,k1.get(2)/4,k1.get(3)/4,
                        k1.get(4)/4,k1.get(0),k1.get(1),k1.get(2),k1.get(3),k1.get(4)});
            case 13:
                boolean thirteenStraight = true;
                boolean thirteenFlush = true;
                for (int i=0;i+1<13;i++){
                    if (temp.get(i)%4 !=temp.get(i+1)%4)
                        thirteenFlush = false;
                    if (temp.get(i)/4 != temp.get(i+1)/4+1)
                        thirteenStraight = false;
                    if (!thirteenFlush && !thirteenStraight)
                        break;
                }
                if (thirteenFlush && thirteenStraight)
                    return new PaiType(PaiType.THIRTEEN_STRAIGHT_FLUSH,new int[]{temp.get(0)%4});
                if (thirteenStraight)
                    return new PaiType(PaiType.THIRTEEN_STRAIGHT, ArrayUtils.toPrimitive(temp.toArray(new Integer[13])));
                return new PaiType(PaiType.CT_INVALID,new int[]{});
        }
        return new PaiType(PaiType.CT_INVALID,new int[]{});
    }

    public static final class TilesType {
        boolean bSingle;
        ArrayList<Integer> aSingle;
        public boolean bOnePair;// 一对
        public ArrayList<Integer> aOnePair; //所有一对
        public boolean bTwoPair;// 两对
        public ArrayList<Integer> aTwoPair;
        public boolean bThreeSame;//三条
        public ArrayList<Integer> aThreeSame;
        public boolean bFourSame; //四条
        public ArrayList<Integer> aFourSame;
        public boolean bFiveSame;
        public ArrayList<Integer> aFiveSame;
        public boolean bFlush;
        public ArrayList<Integer> aFlush;
        public boolean bStraight;
        public ArrayList<Integer> aStraight;
        public boolean bStraightFlush;
        public ArrayList<Integer> aStraightFlush;
        public boolean bGourd;
        public ArrayList<Integer> aGourd;

        TilesType() {
            bSingle = false;
            aSingle = new ArrayList<>();
            bOnePair = false;
            aOnePair = new ArrayList<>();
            bThreeSame = false;
            aThreeSame = new ArrayList<>();
            bFourSame = false;
            aFourSame = new ArrayList<>();
            bFiveSame = false;
            aFiveSame = new ArrayList<>();
            bTwoPair = false;
            aTwoPair = new ArrayList<>();
            bFlush = false;
            aFlush = new ArrayList<>();
            bStraight = false;
            aStraight = new ArrayList<>();
            bStraightFlush = false;
            aStraightFlush = new ArrayList<>();
            bGourd = false;
            aGourd = new ArrayList<>();
        }
    }

    //(较好)好牌
    public static ArrayList<Integer> getHandCard(ArrayList<Integer> pai) {
        ArrayList<Integer> hand = new ArrayList<>();
        ArrayList<ArrayList<Integer>> nCount = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            nCount.add(new ArrayList<Integer>());
        }
        int cardNum = nCount.get(0).size();
        for (int i : pai)
            nCount.get(i / 4).add(i);
        Random random = new Random();
        int ranIndex;
        //一副牌
        ranIndex = random.nextInt(100) + 1;
        if (ranIndex <= 50) {
            //尾道葫芦
            System.out.println("尾道葫芦");
            //先随机拿出三条
            ranIndex = random.nextInt(5) + 8;//随机选出10以上的三条
            Collections.shuffle(nCount.get(ranIndex));//打乱牌序
            for (int i = 0; i < 3; i++) {
                int cardValue = nCount.get(ranIndex).remove(0);
                pai.remove(Integer.valueOf(cardValue));
                hand.add(cardValue);
            }
            ranIndex = random.nextInt(100) + 1;
            if (ranIndex <= 40) {
                //中道顺子
                System.out.println("中道顺子");
                ranIndex = random.nextInt(allStraight.length);
                boolean straight = true;
                for (int aTemStr :allStraight[ranIndex]){
                    if (nCount.get(aTemStr).size() != cardNum) {
                        straight = false;
                        break;
                    }
                }
                while (!straight){
                    int count = 0;
                    ranIndex = ranIndex+1==allStraight.length?0:ranIndex+1;
                    for (int aTemStr : allStraight[ranIndex]){
                        if (nCount.get(aTemStr).size() == cardNum)
                            count++;
                    }
                    straight = count == 5;
                }
                for (int aTemStr : allStraight[ranIndex]) {
                    ArrayList<Integer> indexs = nCount.get(aTemStr);
                    Collections.shuffle(indexs);
                    int cardValue = indexs.remove(0);
                    pai.remove(Integer.valueOf(cardValue));
                    hand.add(cardValue);
                }
                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two,Collections.<Integer>reverseOrder());
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }

                    //尾道葫芦对子
                    two.clear();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //头道单张
                    ranIndex = random.nextInt(pai.size());
                    Integer cardValue = pai.remove(ranIndex);
                    nCount.get(cardValue/4).remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                } else {
                    //头道三条
                    System.out.println("头道三条");
                    ArrayList<Integer> three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }

                    //尾道葫芦一对
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    return hand;
                }
            } else if (ranIndex <= 80) {
                //中道同花
                System.out.println("中道同花");
                int color = random.nextInt(4);
                ArrayList<Integer> flushValue = new ArrayList<>();
                for (Integer aPai : pai) {
                    if (aPai % 4 == color)
                        flushValue.add(aPai);
                }
                while (flushValue.size() > 5){
                    Collections.shuffle(flushValue);
                    flushValue.remove(0);
                }
                for (Integer aFlushFace : flushValue) {
                    nCount.get(aFlushFace/4).remove(aFlushFace);
                    pai.remove(aFlushFace);
                    hand.add(aFlushFace);
                }
                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two);
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //尾道葫芦对子
                    two.clear();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //头道单张
                    ranIndex = random.nextInt(pai.size());
                    Integer cardValue = pai.remove(ranIndex);
                    nCount.get(cardValue/4).remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                } else {
                    //头道三条
                    System.out.println("头道三条");
                    ArrayList<Integer> three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //尾道葫芦一对
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    return hand;
                }
            } else {
                //中道葫芦
                System.out.println("中道葫芦");
                ArrayList<Integer> three = new ArrayList<>();
                for (int i=0;i<nCount.size();i++){
                    if (nCount.get(i).size() > 2)
                        three.add(i);
                }
                ranIndex = random.nextInt(three.size());
                ranIndex = three.remove(ranIndex);
                Collections.shuffle(nCount.get(ranIndex));
                for (int i=0;i<3;i++){
                    int cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(Integer.valueOf(cardValue));
                    hand.add(cardValue);
                }
                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two);
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //中道,尾道的对子
                    for (int i=0;i<2;i++){
                        two.clear();
                        for (int k=0;k<nCount.size();k++){
                            if (nCount.get(k).size() > 1)
                                two.add(k);
                        }
                        ranIndex = random.nextInt(two.size());
                        ranIndex = two.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            int cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(Integer.valueOf(cardValue));
                            hand.add(cardValue);
                        }
                    }
                    //头道单张
                    ranIndex = random.nextInt(pai.size());
                    Integer cardValue = pai.remove(ranIndex);
                    nCount.get(cardValue/4).remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                } else {
                    //头道三条
                    System.out.println("头道三条");
                    three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //中道,尾道的对子
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<2;i++){
                        two.clear();
                        for (int k=0;k<nCount.size();k++){
                            if (nCount.get(k).size() > 1)
                                two.add(k);
                        }
                        ranIndex = random.nextInt(two.size());
                        ranIndex = two.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            int cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(Integer.valueOf(cardValue));
                            hand.add(cardValue);
                        }
                    }
                    return hand;
                }
            }
        } else if (ranIndex <= 80) {
            //尾道铁支
            System.out.println("尾道铁支");
            ArrayList<Integer> four = new ArrayList<>();
            for (int i=0;i<nCount.size();i++){
                if (nCount.get(i).size() > 3)
                    four.add(i);
            }

            ranIndex = random.nextInt(four.size());
            ranIndex = four.get(ranIndex);
            Collections.shuffle(nCount.get(ranIndex));
            for (int i=0;i<4;i++){
                int cardValue = nCount.get(ranIndex).remove(0);
                pai.remove(Integer.valueOf(cardValue));
                hand.add(cardValue);
            }

            ranIndex = random.nextInt(100) + 1;
            if (ranIndex <= 40) {
                //中道顺子
                System.out.println("中道顺子");
                ranIndex = random.nextInt(allStraight.length);
                boolean straight = true;
                for (int aTemStr :allStraight[ranIndex]){
                    if (nCount.get(aTemStr).size() < 1) {
                        straight = false;
                        break;
                    }
                }
                while (!straight){
                    int count = 0;
                    ranIndex = ranIndex+1==allStraight.length?0:ranIndex+1;
                    for (int aTemStr : allStraight[ranIndex]){
                        if (nCount.get(aTemStr).size() > 0)
                            count++;
                    }
                    straight = count == 5;
                }
                for (int aTemStr : allStraight[ranIndex]) {
                    ArrayList<Integer> indexs = nCount.get(aTemStr);
                    Collections.shuffle(indexs);
                    int cardValue = indexs.remove(0);
                    pai.remove(Integer.valueOf(cardValue));
                    hand.add(cardValue);
                }

                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道的对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two);
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //头道和尾道的单张
                    for (int i = 0; i < 2; i++) {
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                } else {
                    //头道三条
                    ArrayList<Integer> three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //尾道单张
                    ranIndex = random.nextInt(pai.size());
                    Integer cardValue = pai.remove(ranIndex);
                    nCount.get(cardValue/4).remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                }
            } else if (ranIndex <= 80) {
                //中道同花
                System.out.println("中道同花");
                int color = random.nextInt(4);
                ArrayList<Integer> flushValue = new ArrayList<>();
                for (Integer aPai : pai) {
                    if (aPai % 4 == color)
                        flushValue.add(aPai);
                }
                while (flushValue.size() > 5){
                    Collections.shuffle(flushValue);
                    flushValue.remove(0);
                }
                for (Integer aFlushFace : flushValue) {
                    nCount.get(aFlushFace/4).remove(aFlushFace);
                    pai.remove(aFlushFace);
                    hand.add(aFlushFace);
                }
                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道的对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two);
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //头道和尾道的单张
                    for (int i = 0; i < 2; i++) {
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                } else {
                    //头道三条
                    System.out.println("头道三条");
                    ArrayList<Integer> three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //尾道单张
                    ranIndex = random.nextInt(pai.size());
                    Integer cardValue = pai.remove(ranIndex);
                    nCount.get(cardValue/4).remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                }
            } else if (ranIndex <= 90) {
                //中道葫芦
                System.out.println("中道葫芦");
                ArrayList<Integer> three = new ArrayList<>();
                for (int i=0;i<nCount.size();i++){
                    if (nCount.get(i).size() > 2)
                        three.add(i);
                }
                ranIndex = random.nextInt(three.size());
                ranIndex = three.remove(ranIndex);
                Collections.shuffle(nCount.get(ranIndex));
                for (int i=0;i<3;i++){
                    int cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(Integer.valueOf(cardValue));
                    hand.add(cardValue);
                }
                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two);
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //中道对子
                    two.clear();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //头道和尾道的单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                } else {
                    //头道三条
                    three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //中道对子
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }

                    //尾道单张
                    ranIndex = random.nextInt(pai.size());
                    Integer cardValue = pai.remove(ranIndex);
                    nCount.get(cardValue/4).remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                }
            } else {
                //中道铁支
                System.out.println("中道铁支");
                four = new ArrayList<>();
                for (int i=0;i<nCount.size();i++){
                    if (nCount.get(i).size() > 3)
                        four.add(i);
                }

                ranIndex = random.nextInt(four.size());
                ranIndex = four.get(ranIndex);
                Collections.shuffle(nCount.get(ranIndex));
                for (int i=0;i<4;i++){
                    int cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(Integer.valueOf(cardValue));
                    hand.add(cardValue);
                }
                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 90) {
                    //头道对子
                    System.out.println("头道对子");
                    ArrayList<Integer> two = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 1)
                            two.add(i);
                    }
                    Collections.sort(two);
                    while (two.size() > 5){
                        two.remove(0);
                    }
                    ranIndex = random.nextInt(two.size());
                    ranIndex = two.get(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //头道,中道,尾道的单张
                    for (int i = 0; i < 3; i++) {
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                } else {
                    //头道三条
                    ArrayList<Integer> three = new ArrayList<>();
                    for (int i=0;i<nCount.size();i++){
                        if (nCount.get(i).size() > 2)
                            three.add(i);
                    }
                    ranIndex = random.nextInt(three.size());
                    ranIndex = three.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        int cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    //尾道和中道的单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }
            }

        } else {
//            if (pai.size() > 53 && ranIndex > 90 && ranIndex <= 100) {
//                //尾道五同
//            } else
            {
                //尾道同花顺
                System.out.println("尾道同花顺");
                ranIndex = random.nextInt(10);
                int color = random.nextInt(4);
                pai.remove(Integer.valueOf(allStraight[ranIndex][0]*4+color));
                pai.remove(Integer.valueOf(allStraight[ranIndex][1]*4+color));
                pai.remove(Integer.valueOf(allStraight[ranIndex][2]*4+color));
                pai.remove(Integer.valueOf(allStraight[ranIndex][3]*4+color));
                pai.remove(Integer.valueOf(allStraight[ranIndex][4]*4+color));

                nCount.get(allStraight[ranIndex][0]).remove(Integer.valueOf(allStraight[ranIndex][0]*4+color));
                nCount.get(allStraight[ranIndex][1]).remove(Integer.valueOf(allStraight[ranIndex][1]*4+color));
                nCount.get(allStraight[ranIndex][2]).remove(Integer.valueOf(allStraight[ranIndex][2]*4 + color));
                nCount.get(allStraight[ranIndex][3]).remove(Integer.valueOf(allStraight[ranIndex][3]* 4 + color));
                nCount.get(allStraight[ranIndex][4]).remove(Integer.valueOf(allStraight[ranIndex][4]* 4 + color));

                hand.add(allStraight[ranIndex][0]*4+color);
                hand.add(allStraight[ranIndex][1]*4+color);
                hand.add(allStraight[ranIndex][2]*4+color);
                hand.add(allStraight[ranIndex][3]*4+color);
                hand.add(allStraight[ranIndex][4]*4+color);


                ranIndex = random.nextInt(100) + 1;
                if (ranIndex <= 40) {
                    //中道顺子
                    System.out.println("中道顺子");
                    ranIndex = random.nextInt(allStraight.length);
                    for (int aTemStr : allStraight[ranIndex]) {
                        ArrayList<Integer> indexs = nCount.get(aTemStr);
                        Collections.shuffle(indexs);
                        int cardValue = indexs.remove(0);
                        pai.remove(Integer.valueOf(cardValue));
                        hand.add(cardValue);
                    }
                    ranIndex = random.nextInt(100) + 1;
                    if (ranIndex <= 90) {
                        //头道对子
                        System.out.println("头道对子");
                        ArrayList<Integer> two = new ArrayList<>();
                        for (int i=0;i<nCount.size();i++){
                            if (nCount.get(i).size() > 1)
                                two.add(i);
                        }
                        Collections.sort(two);
                        while (two.size() > 5){
                            two.remove(0);
                        }
                        ranIndex = random.nextInt(two.size());
                        ranIndex = two.get(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int i=0;i<2;i++){
                            int cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(Integer.valueOf(cardValue));
                            hand.add(cardValue);
                        }
                        //头道单张
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                        return hand;
                    } else {
                        //头道三条
                        System.out.println("头道三条");
                        ArrayList<Integer> three = new ArrayList<>();
                        for (int i=0;i<nCount.size();i++){
                            if (nCount.get(i).size() > 2)
                                three.add(i);
                        }
                        ranIndex = random.nextInt(three.size());
                        ranIndex = three.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int i=0;i<3;i++){
                            int cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(Integer.valueOf(cardValue));
                            hand.add(cardValue);
                        }
                        return hand;
                    }
                }else /*if (ranIndex <= 70)*/{
                    //中道同花
                    System.out.println("中道同花");
                    int temColor = random.nextInt(4);
                    if (temColor == color)
                        temColor = temColor+1 == 4?0:temColor+1;
                    ArrayList<Integer> flushValue = new ArrayList<>();
                    for (Integer aPai : pai) {
                        if (aPai % 4 == temColor)
                            flushValue.add(aPai);
                    }
                    while (flushValue.size() > 5){
                        Collections.shuffle(flushValue);
                        flushValue.remove(0);
                    }
                    for (Integer aFlushFace : flushValue) {
                        nCount.get(aFlushFace/4).remove(aFlushFace);
                        pai.remove(aFlushFace);
                        hand.add(aFlushFace);
                    }
                    ranIndex = random.nextInt(100) + 1;
                    if (ranIndex <= 90) {
                        //头道对子
                        System.out.println("头道对子");
                        ArrayList<Integer> two = new ArrayList<>();
                        for (int i=0;i<nCount.size();i++){
                            if (nCount.get(i).size() > 1)
                                two.add(i);
                        }
                        Collections.sort(two);
                        while (two.size() > 5){
                            two.remove(0);
                        }
                        ranIndex = random.nextInt(two.size());
                        ranIndex = two.get(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int i=0;i<2;i++){
                            int cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(Integer.valueOf(cardValue));
                            hand.add(cardValue);
                        }
                        //头道单张
                        ranIndex = random.nextInt(pai.size());
                        Integer cardValue = pai.remove(ranIndex);
                        nCount.get(cardValue/4).remove(cardValue);
                        hand.add(cardValue);
                        return hand;
                    } else {
                        //头道三条
                        System.out.println("头道三条");
                        ArrayList<Integer> three = new ArrayList<>();
                        for (int i=0;i<nCount.size();i++){
                            if (nCount.get(i).size() > 2)
                                three.add(i);
                        }
                        ranIndex = random.nextInt(three.size());
                        ranIndex = three.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int i=0;i<3;i++){
                            int cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(Integer.valueOf(cardValue));
                            hand.add(cardValue);
                        }
                        return hand;
                    }
                }
//                else if (ranIndex <= 80){
//                    //中道葫芦
//
//                }
            }
        }
    }

    public static ArrayList<Integer> getHanCard_X(ArrayList<Integer> pokers){
        ArrayList<Integer> pai = new ArrayList<>(pokers);
        ArrayList<Integer> hand = new ArrayList<>();
        ArrayList<ArrayList<Integer>> nCount = new ArrayList<>();
        ArrayList<Integer> remainCard = new ArrayList<>();
        for (int i=0;i<13;i++){
            nCount.add(new ArrayList<Integer>());
            remainCard.add(i);
        }
        for (Integer aPai1 : pai)
            nCount.get(aPai1 / 4).add(aPai1);
        Random random = new Random();
        int ranIndex = random.nextInt(100)+1;
        if (ranIndex <= 95){//尾:葫芦
            ranIndex = random.nextInt(100)+1;
            if (ranIndex <= 40){//中:顺子
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:葫芦");
//                    System.out.println("中:顺子");
//                    System.out.println("头:对子");

                    //顺子
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        Collections.shuffle(nCount.get(face));
                        Integer cardValue = nCount.get(face).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //葫芦和头道的对子
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                }else {//头:三条
//                    System.out.println("尾:葫芦");
//                    System.out.println("中:顺子");
//                    System.out.println("头:三条");

                    //顺子
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        Collections.shuffle(nCount.get(face));
                        Integer cardValue = nCount.get(face).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //尾道,头道的三条
                    for (int i=0;i<2;i++)
                    {
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<3;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }

                    //葫芦的一对
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<2;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }
            }else if (ranIndex <= 80){//中:同花
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:葫芦");
//                    System.out.println("中:同花");
//                    System.out.println("头:对子");
                    //同花
                    int color = random.nextInt(4);
                    ArrayList<Integer> flush = new ArrayList<>();
                    for (Integer aPai : pai)
                        if (aPai % 4 == color && remainCard.contains(aPai/4))
                            flush.add(aPai);
                    Collections.shuffle(flush);
                    while (flush.size() > 5){
                        flush.remove(0);
                    }
                    for (Integer i : flush){
                        remainCard.remove(Integer.valueOf(i/4));
                        nCount.get(i/4).remove(i);
                        pai.remove(i);
                        hand.add(i);
                    }

                    //三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //对子
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }

                    //单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;
                }else {//头:三条
//                    System.out.println("尾:葫芦");
//                    System.out.println("中:同花");
//                    System.out.println("头:三条");
                    //中道同花
                    int color = random.nextInt(4);
                    ArrayList<Integer> flush = new ArrayList<>();
                    for (Integer aPai : pai)
                        if (aPai % 4 == color && remainCard.contains(aPai/4))
                            flush.add(aPai);
                    while (flush.size() > 5){
                        Collections.shuffle(flush);
                        flush.remove(0);
                    }
                    for (Integer i : flush){
                        remainCard.remove(Integer.valueOf(i/4));
                        nCount.get(i/4).remove(i);
                        pai.remove(i);
                        hand.add(i);
                    }
                    //头道,尾道的三条
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<3;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }

                    //尾道的一对
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }
            }else {//中:葫芦
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:葫芦");
//                    System.out.println("中:葫芦");
//                    System.out.println("头:对子");

                    //中道,尾道的三条
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<3;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //头,中,尾的一对
                    for (int i=0;i<3;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;
                }else {//头:三条
//                    System.out.println("尾:葫芦");
//                    System.out.println("中:葫芦");
//                    System.out.println("头:三条");

                    //头道,中道,尾道的三条
                    for (int i=0;i<3;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<3;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }

                    //中道尾道的对子
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    return hand;
                }
            }
        }else if (ranIndex <= 98){//尾:铁支
            ranIndex = random.nextInt(100)+1;
            if (ranIndex <= 40){//中:顺子
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:铁支");
//                    System.out.println("中:顺子");
//                    System.out.println("头:对子");
                    //顺子
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        Collections.shuffle(nCount.get(face));
                        Integer cardValue = nCount.get(face).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //尾道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<4;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头,尾单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }else {//头:三条
//                    System.out.println("尾:铁支");
//                    System.out.println("中:顺子");
//                    System.out.println("头:三条");
                    //顺子
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        Collections.shuffle(nCount.get(face));
                        Integer cardValue = nCount.get(face).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //尾道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<4;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //尾道单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;
                }

            }else if (ranIndex <= 80){//中:同花
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:铁支");
//                    System.out.println("中:同花");
//                    System.out.println("头:对子");

                    //中道同花
                    int color = random.nextInt(4);
                    ArrayList<Integer> flush = new ArrayList<>();
                    for (Integer aPai : pai)
                        if (aPai % 4 == color && remainCard.contains(aPai/4))
                            flush.add(aPai);
                    while (flush.size() > 5){
                        Collections.shuffle(flush);
                        flush.remove(0);
                    }
                    for (Integer i : flush){
                        remainCard.remove(Integer.valueOf(i/4));
                        nCount.get(i/4).remove(i);
                        pai.remove(i);
                        hand.add(i);
                    }

                    //尾道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<4;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头,尾单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    return hand;

                }else {//头:三条
//                    System.out.println("尾:铁支");
//                    System.out.println("中:同花");
//                    System.out.println("头:三条");

                    //中道同花
                    int color = random.nextInt(4);
                    ArrayList<Integer> flush = new ArrayList<>();
                    for (Integer aPai : pai)
                        if (aPai % 4 == color && remainCard.contains(aPai/4))
                            flush.add(aPai);
                    Collections.shuffle(flush);
                    while (flush.size() > 5){
                        flush.remove(0);
                    }
                    for (Integer i : flush){
                        remainCard.remove(Integer.valueOf(i/4));
                        nCount.get(i/4).remove(i);
                        pai.remove(i);
                        hand.add(i);
                    }

                    //尾道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<4;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //尾道单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;
                }

            }else if (ranIndex <= 90){//中:葫芦
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:铁支");
//                    System.out.println("中:葫芦");
//                    System.out.println("头:对子");

                    //尾道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<4;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //中道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<3;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头,中对子
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }

                    //头,尾单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    return hand;

                }else {//头:三条
//                    System.out.println("尾:铁支");
//                    System.out.println("中:葫芦");
//                    System.out.println("头:三条");

                    //尾道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int i=0;i<4;i++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //中,头的三条
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<3;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //中道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;

                }

            }else {//中:铁支
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:铁支");
//                    System.out.println("中:铁支");
//                    System.out.println("头:对子");

                    //中道尾道的铁支
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<4;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //头道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //头,中,尾单张
                    for (int i=0;i<3;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }


                    return hand;
                }else {//头:三条
//                    System.out.println("尾:铁支");
//                    System.out.println("中:铁支");
//                    System.out.println("头:三条");

                    //中道尾道的铁支
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<4;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //头道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //中,尾单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    return hand;
                }
            }
        }else {//尾:同花顺
            ranIndex = random.nextInt(100)+1;
            if (ranIndex<= 40){//中:顺子
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:顺子");
//                    System.out.println("头:对子");


                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }

                    //顺子
                    ranIndex = random.nextInt(allStraight.length);
                    straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        Collections.shuffle(nCount.get(face));
                        Integer cardValue = nCount.get(face).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }


                    //头道单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;

                }else {//头:三条
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:顺子");
//                    System.out.println("头:三条");
                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }

                    //顺子
                    ranIndex = random.nextInt(allStraight.length);
                    straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        Collections.shuffle(nCount.get(face));
                        Integer cardValue = nCount.get(face).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    //头道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }

                    return hand;
                }
            }else if (ranIndex <= 80){//中:同花
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:同花");
//                    System.out.println("头:对子");

                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }

                    //同花
                    int color2 = random.nextInt(4);
                    while (color2 == color){
                        color2 = color2+1 == 4? 0:color2+1;
                    }

                    ArrayList<Integer> flush = new ArrayList<>();
                    for (Integer aPai : pai)
                        if (aPai % 4 == color2)
                            flush.add(aPai);
                    while (flush.size() > 5){
                        Collections.shuffle(flush);
                        flush.remove(0);
                    }
                    for (Integer i : flush){
                        remainCard.remove(Integer.valueOf(i/4));
                        nCount.get(i/4).remove(i);
                        pai.remove(i);
                        hand.add(i);
                    }

                    //头道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }


                    //头道单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);

                    return hand;
                }else {//头:三条
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:同花");
//                    System.out.println("头:三条");

                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }

                    //中道同花
                    int color2 = random.nextInt(4);
                    while (color2 == color){
                        color2 = color2+1 == 4? 0:color2+1;
                    }

                    ArrayList<Integer> flush = new ArrayList<>();
                    for (Integer aPai : pai)
                        if (aPai % 4 == color2)
                            flush.add(aPai);
                    while (flush.size() > 5){
                        Collections.shuffle(flush);
                        flush.remove(0);
                    }
                    for (Integer i : flush){
                        remainCard.remove(Integer.valueOf(i/4));
                        nCount.get(i/4).remove(i);
                        pai.remove(i);
                        hand.add(i);
                    }
                    //头道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }
            }else if (ranIndex <= 90){//中:葫芦
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:葫芦");
//                    System.out.println("头:对子");

                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }

                    //中道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //中道,头道对子
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<2;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }
                    //头道单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                }else {//头:三条
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:葫芦");
//                    System.out.println("头:三条");
                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }

                    //头,中三条
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        for (int j=0;j<3;j++){
                            Integer cardValue = nCount.get(ranIndex).remove(0);
                            pai.remove(cardValue);
                            hand.add(cardValue);
                        }
                    }

                    //中道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }
            }else{//中:铁支
                ranIndex = random.nextInt(100)+1;
                if (ranIndex <= 95){//头:对子
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:铁支");
//                    System.out.println("头:对子");
                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }
                    //铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<4;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //头道对子
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<2;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //头,中单张
                    for (int i=0;i<2;i++){
                        ranIndex = random.nextInt(remainCard.size());
                        ranIndex = remainCard.remove(ranIndex);
                        Collections.shuffle(nCount.get(ranIndex));
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    return hand;
                }else {//头:三条
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:铁支");
//                    System.out.println("头:三条");
                    //尾道同花顺
                    int color = random.nextInt(4);
                    ranIndex = random.nextInt(allStraight.length);
                    int[] straight = allStraight[ranIndex];
                    for (int face:straight){
                        remainCard.remove(Integer.valueOf(face));
                        nCount.get(face).remove(Integer.valueOf(face*4+ color));
                        pai.remove(Integer.valueOf(face*4+ color));
                        hand.add(face*4+ color);
                    }
                    //中道铁支
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<4;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //头道三条
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    for (int j=0;j<3;j++){
                        Integer cardValue = nCount.get(ranIndex).remove(0);
                        pai.remove(cardValue);
                        hand.add(cardValue);
                    }
                    //中道单张
                    ranIndex = random.nextInt(remainCard.size());
                    ranIndex = remainCard.remove(ranIndex);
                    Collections.shuffle(nCount.get(ranIndex));
                    Integer cardValue = nCount.get(ranIndex).remove(0);
                    pai.remove(cardValue);
                    hand.add(cardValue);
                    return hand;
                }
            }
//            else {//中:同花顺
//                ranIndex = random.nextInt(100)+1;
//                if (ranIndex <= 80){//头:对子
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:同花顺");
//                    System.out.println("头:对子");
//                }else {//头:三条
//                    System.out.println("尾:同花顺");
//                    System.out.println("中:同花顺");
//                    System.out.println("头:三条");
//                }
//            }
        }
    }
}
