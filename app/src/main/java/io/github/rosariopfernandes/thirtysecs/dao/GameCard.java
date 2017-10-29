package io.github.rosariopfernandes.thirtysecs.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rosariopfernandes on 8/14/17.
 */

public class GameCard extends RealmObject{
    @PrimaryKey
    private int id;
    private CardSide blue;
    private CardSide yellow;

    public GameCard(int id, CardSide blue, CardSide yellow) {
        this.id = id;
        this.blue = blue;
        this.yellow = yellow;
    }

    public GameCard(int id)
    {
        this.id = id;
    }

    public void addBlueCard(String s1, String s2, String s3, String s4, String s5)
    {
        blue = new CardSide(s1, s2, s3, s4, s5, id);
    }

    public void addYellowCard(String s1, String s2, String s3, String s4, String s5)
    {
        yellow = new CardSide(s1, s2, s3, s4, s5, id);
    }

    public GameCard() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CardSide getBlue() {
        return blue;
    }

    public void setBlue(CardSide blue) {
        this.blue = blue;
    }

    public CardSide getYellow() {
        return yellow;
    }

    public void setYellow(CardSide yellow) {
        this.yellow = yellow;
    }

    public String toString() {
        return "GameCard{" +
                "id=" + id +
                ", blue=" + blue +
                ", yellow=" + yellow +
                '}';
    }
}
