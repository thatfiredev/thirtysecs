package io.github.rosariopfernandes.thirtysecs.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rosariopfernandes on 8/13/17.
 */

public class TeamScore extends RealmObject{

    @PrimaryKey
    private int id;
    private String teamName;
    private int score;

    public TeamScore(int id, String teamName, int score) {
        this.id = id;
        this.teamName = teamName;
        this.score = score;
    }

    public TeamScore() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String toString() {
        return "TeamScore{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                ", score=" + score +
                '}';
    }
}
