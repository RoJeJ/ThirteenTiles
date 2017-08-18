package sfs2x.model;


public class Seat {
    private int no;
    private Player player;
    private Seat next;

    Seat(int no) {
        this.no = no;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Seat getNext() {
        return next;
    }

    public void setNext(Seat next) {
        this.next = next;
    }

    public int getNo() {
        return no;
    }
}
