package io.github.rosariopfernandes.thirtysecs.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.rosariopfernandes.thirtysecs.R;
import io.github.rosariopfernandes.thirtysecs.dao.TeamScore;
import io.realm.RealmResults;

/**
 * Created by rosariopfernandes on 8/13/17.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.CardViewHolder>{
    private Context context;
    private RealmResults<TeamScore> teamScores;
    private int cardColor;

    public ScoreAdapter(Context context, RealmResults<TeamScore> teamScores){
        this.context = context;
        this.teamScores = teamScores;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_score, parent,
                false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int i) {
        TeamScore score = teamScores.get(i);
        holder.txtTeam.setText(score.getTeamName());
        holder.txtScore.setText(String.valueOf(score.getScore()));
        if(score.getScore()%2==0)
        {
            cardColor = ContextCompat.getColor(context,
                    R.color.colorPrimary);
        }
        else
        {
            cardColor = ContextCompat.getColor(context,
                    R.color.colorAccent);
        }
        holder.cardView.setCardBackgroundColor(cardColor);
        /*holder.txtWord.setText(word);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return teamScores.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder
    {
        private CardView cardView;
        private TextView txtTeam, txtScore;

        CardViewHolder(View v)
        {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cardView);
            txtTeam = (TextView) v.findViewById(R.id.txtTeam);
            txtScore = (TextView) v.findViewById(R.id.txtScore);
        }
    }
}
