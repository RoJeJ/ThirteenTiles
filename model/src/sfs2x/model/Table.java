package sfs2x.model;


public class Table {
    private int count ;
    private int person;
    private boolean hong;
    private boolean aa;
    private int ma;
    private Seat[] seats;
    private Player owner;
    private boolean gameStarted;
    private boolean usingCard;
    private int gameCard;
    private int curCount;
    public Table(int count,int person,boolean hong,boolean aa,int ma){
        this.count = count;
        this.person = person;
        this.hong = hong;
        this.aa = aa;
        this.ma = ma;
        curCount = 0;

        seats = new Seat[person];
        for (int i=0;i<person;i++)
            seats[i] = new Seat(i);
        for (int i=0;i<person;i++){
            if (i == person -1)
                seats[i].setNext(seats[0]);
            else
                seats[i].setNext(seats[i+1]);
        }
    }


    public Seat[] getSeats(){
        return seats;
    }


    public Seat getEmptySeat(){
        for (int i=0;i<person;i++)
            if (seats[i].getPlayer() == null) {
                return seats[i];
            }
        System.out.println("没有空位置");
        return null;
    }

    public int getSeatNo(Player player){
        for (int i=0;i<person;i++){
            if (player != null && seats[i].getPlayer() == player)
                return i;
        }
        return -1;
    }

    public Seat getSeat(int no){
        if (no >=0 && no < seats.length)
            return seats[no];
        return null;
    }

    public boolean contains(Player player){
        if (player == null)
            return false;
        for (Seat seat : seats){
            Player p = seat.getPlayer();
            if (p != null){
                if (p.getUserID() == player.getUserID())
                    return true;
            }
        }
        return false;
    }

    public Player getPlayer(int UserID){
        for (Seat seat : seats){
            Player p = seat.getPlayer();
            if (p != null){
                if (p.getUserID() == UserID)
                    return p;
            }
        }
        return null;
    }

    public int getPersonCount(){
        int n = 0;
        for (Seat seat : seats){
            Player p = seat.getPlayer();
            if (p != null)
                n++;
        }
        return n;
    }

    public int condition(Player player){
        if (aa ) {
            usingCard = false;
            switch (count){
                case 12:
                    return 8;
                case 24:
                    return 16;
                case 36:
                    return 24;
            }
        }else {
            if (player == getOwner()){
                switch (count){
                    case 12:
                        usingCard = true;
                        gameCard = 1;
                        switch (person){
                            case 2:
                                return 15;
                            case 3:
                                return 20;
                            case 4:
                                return 28;
                            case 5:
                                return 36;
                        }
                        break;
                    case 24:
                        usingCard = true;
                        gameCard = 2;
                        switch (person){
                            case 2:
                                return 30;
                            case 3:
                                return 40;
                            case 4:
                                return 56;
                            case 5:
                                return 72;
                        }
                        break;
                    case 36:
                        usingCard = true;
                        gameCard = 3;
                        switch (person){
                            case 2:
                                return 45;
                            case 3:
                                return 60;
                            case 4:
                                return 84;
                            case 5:
                                return 108;
                        }
                        break;
                }
            }else {
                usingCard = false;
                return 0;
            }
        }
        return -1;
    }


    public int getCount() {
        return count;
    }

    public int getPerson() {
        return person;
    }

    public boolean isHong() {
        return hong;
    }

    public boolean isAA() {
        return aa;
    }

    public int getMa() {
        return ma;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }


    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    

    public void leaveSeat(Player player) {
        for (Seat seat : seats){
            Player p = seat.getPlayer();
            if (p == player)
                seat.setPlayer(null);
        }
    }

    public boolean isUsingCard() {
        return usingCard;
    }

    public void setUsingCard(boolean usingCard) {
        this.usingCard = usingCard;
    }

    public int getGameCard() {
        return gameCard;
    }

    public void setGameCard(int gameCard) {
        this.gameCard = gameCard;
    }

    public int getCurCount() {
        return curCount;
    }

    public void setCurCount(int curCount) {
        this.curCount = curCount;
    }
}
