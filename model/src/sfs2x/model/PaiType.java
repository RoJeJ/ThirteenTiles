package sfs2x.model;

public class  PaiType implements Comparable<PaiType>{
    public static final int THIRTEEN_STRAIGHT_FLUSH = 11;//同花十三水
    public static final int THIRTEEN_STRAIGHT = 10;//十三水
    public static final int FIVE_SAME = 9;//五同
    public static final int STRAIGHT_FLUSH = 8;//同花顺
    public static final int FOUR_SAME = 7;//铁支
    public static final int GOURD = 6;//葫芦
    public static final int FLUSH = 5;//同花
    public static final int STRAIGHT = 4;//顺子
    public static final int THREE_SAME = 3;//三条
    public static final int TWO_PAIR = 2;//两对
    public static final int ONE_PAIR = 1;//一对
    public static final int CT_VOID = 0;//乌龙
    public static final int CT_INVALID = -1;
    public int type;
    public int[] assist;

    public PaiType(int type, int[] assist) {
        this.type = type;
        this.assist = assist;
    }

    public void setPaiType(int type,int[] assist){
        this.type = type;
        this.assist = assist;
    }

    // 比较大小 返回值大于0 为大 ,0 为相等, 小于0为小
    @Override
    public int compareTo(PaiType paiType){
        if (type != paiType.type)
            return type - paiType.type;
        else {
            int len;
            if (assist.length <= paiType.assist.length)
                len = assist.length;
            else len = paiType.assist.length;
            for (int i=0;i<len;i++){
                if (assist[i] != paiType.assist[i])
                    return assist[i] - paiType.assist[i];
            }
            return 0;
        }
    }
}
