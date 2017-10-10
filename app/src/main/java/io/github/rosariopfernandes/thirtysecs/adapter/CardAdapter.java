package io.github.rosariopfernandes.thirtysecs.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.rosariopfernandes.thirtysecs.MainActivity;
import io.github.rosariopfernandes.thirtysecs.R;
import io.github.rosariopfernandes.thirtysecs.dao.CardSide;
import io.github.rosariopfernandes.thirtysecs.dao.GameCard;

/**
 * Created by rosariopfernandes on 8/13/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>{
    private Context context;
    private int cardColor;
    private int teamScore;
    private boolean isClickable;
    private GameCard gameCard;

    public CardAdapter(Context context, GameCard card, int teamScore){
        this.context = context;
        this.gameCard = card;
        this.teamScore = teamScore;
        isClickable = true;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_gameplay, parent,
                false);
        return new CardViewHolder(v);
    }

    public void setGameCard(GameCard card)
    {
        this.gameCard = card;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int i) {
        String word = "";
        CardSide side;
        if(teamScore%2==0)
        {
            cardColor = ContextCompat.getColor(context,
                    R.color.colorPrimary);
            side = gameCard.getBlue();
        }
        else
        {
            cardColor = ContextCompat.getColor(context,
                    R.color.colorAccent);
            side = gameCard.getYellow();
        }
        switch (i)
        {
            case 0: word = side.getWord1(); break;
            case 1: word = side.getWord2(); break;
            case 2: word = side.getWord3(); break;
            case 3: word = side.getWord4(); break;
            case 4: word = side.getWord5(); break;
        }
        holder.txtWord.setText(word);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClickable) {
                    MainActivity.correctAnswers++;
                    holder.cardView.setCardBackgroundColor(cardColor);
                }
                else
                {
                    MainActivity.correctAnswers--;
                    holder.cardView.setBackgroundColor(ContextCompat.getColor(context,
                            android.R.color.white));
                }
                holder.cardView.setOnClickListener(null);
            }
        });
    }

    public void setClickable(boolean isClickable){
        this.isClickable = isClickable;
    }

    public void setScore(int teamScore)
    {
        this.teamScore = teamScore;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder
    {
        private CardView cardView;
        private TextView txtWord;

        CardViewHolder(View v)
        {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cardView);
            txtWord = (TextView) v.findViewById(R.id.txtWord);
        }
    }
}
