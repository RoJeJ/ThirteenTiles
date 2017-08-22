package sfs2x.model;


import java.util.ArrayList;
import java.util.HashMap;

public class Global {
    public static final String PLAYER = "player";
    public static ArrayList<Integer> roomNames2 = new ArrayList<Integer>();
    public static ArrayList<Integer> roomNames3 = new ArrayList<Integer>();
    public static ArrayList<Integer> roomNames4 = new ArrayList<Integer>();
    public static ArrayList<Integer> roomNames5 = new ArrayList<Integer>();
    public static final String RECOVERLOCK = "lock";
    public static final String TABLE = "table";
    public static final long EXIT_TIME = 3*60*1000;
    public static final long WAIT_TIMEOUT = 10*60*1000;
    public static final int SET_PAI = 3*60;
    public static final int HEAD = 1;
    public static final int MIDDLE = 1;
    public static final int TAIL = 1;
    public static final long PRESENT = 8;


    public static final int WAITREADY = 60;
    public static final  int CARD = 1;

    public static final int FRONT_THREE_SAME = 3;

    public static final int MID_GOURD = 2;
    public static final int MID_FOUR_SAME = 8;
    public static final int MID_STRAIGHT_FLUSH = 10;
    public static final int MID_FIVE_SAME = 20;


    public static final int BACK_FOUR_SAME = 4;
    public static final int BACK_STRAIGHT_FLUSH = 5;
    public static final int BACK_FIVE_SAME = 10;

    public static final int THIRTEEN_STRAIGHT_FLUSH = 52;
    public static final int THIRTEEN_STRAIGHT = 26;
}
