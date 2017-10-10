package io.github.rosariopfernandes.thirtysecs.dao;

import io.realm.RealmObject;

/**
 * Created by rosariopfernandes on 8/14/17.
 */

public class CardSide extends RealmObject{

    private String word1;
    private String word2;
    private String word3;
    private String word4;
    private String word5;

    private int cardId;

    public CardSide(String word1, String word2, String word3, String word4, String word5, int cardId) {
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
        this.word4 = word4;
        this.word5 = word5;
        this.cardId = cardId;
    }

    public CardSide() {}

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getWord3() {
        return word3;
    }

    public void setWord3(String word3) {
        this.word3 = word3;
    }

    public String getWord4() {
        return word4;
    }

    public void setWord4(String word4) {
        this.word4 = word4;
    }

    public String getWord5() {
        return word5;
    }

    public void setWord5(String word5) {
        this.word5 = word5;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String toString() {
        return "CardSide{" +
                "word1='" + word1 + '\'' +
                ", word2='" + word2 + '\'' +
                ", word3='" + word3 + '\'' +
                ", word4='" + word4 + '\'' +
                ", word5='" + word5 + '\'' +
                ", cardId=" + cardId +
                '}';
    }
}
