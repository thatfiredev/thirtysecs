package io.github.rosariopfernandes.thirtysecs.dao;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rosariopfernandes on 10/5/17.
 */

public class SortedCard extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private RealmList<GameCard> cards;

    public SortedCard() {
        cards = new RealmList<>();
    }

    public RealmList<GameCard> getCards() {
        return cards;
    }

    public void setCards(RealmList<GameCard> cards) {
        this.cards = cards;
    }

    public String toString() {
        return "SortedCard{" +
                "id=" + id +
                ", cards=" + cards +
                '}';
    }
}
