package io.github.rosariopfernandes.thirtysecs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;

import io.github.rosariopfernandes.thirtysecs.adapter.CardAdapter;
import io.github.rosariopfernandes.thirtysecs.adapter.ScoreAdapter;
import io.github.rosariopfernandes.thirtysecs.dao.GameCard;
import io.github.rosariopfernandes.thirtysecs.dao.SortedCard;
import io.github.rosariopfernandes.thirtysecs.dao.TeamScore;
import io.github.rosariopfernandes.thirtysecs.fragment.AlertDialogFragment;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static io.github.rosariopfernandes.thirtysecs.TutorialActivity.PREFERENCES_TAG;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView txtTime, txtScore, txtDice;
    private LinearLayout linearLayout;
    private Realm realm;
    private RealmResults<TeamScore> results;
    private CardAdapter cardAdapter;
    private ScoreAdapter scoreAdapter;
    public static int correctAnswers = 0;
    private int secsLeft, currentCard, numPlayers = 0, dice = 0;
    private final static int REQUEST_DICE_ROLL = 1500;
    public final static String PARAM_DICE_ROLL = "io.github.rosariopfernandes.thirtysecs.dice";
    public final static String PARAM_TEAM_NAME = "io.github.rosariopfernandes.thirtysecs.teamName";
    private TeamScore currentTeam;
    private CountDownTimer timer;
    private ObjectAnimator anim;
    private boolean isShowingScore = false;
    private GameCard card;
    private SharedPreferences prefs;
    private Button btnPass, btnNext;
    private FrameLayout diceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
        realm = Realm.getDefaultInstance();

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtDice = (TextView) findViewById(R.id.txtDice);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btnPass = (Button) findViewById(R.id.btnPass);
        btnNext = (Button) findViewById(R.id.btnNext);
        diceView = (FrameLayout) findViewById(R.id.dice);

        results = realm.where(TeamScore.class).findAll();
        numPlayers = results.size();
        currentTeam = results.first();
        prefs = getSharedPreferences(PREFERENCES_TAG, MODE_PRIVATE);
        currentCard = prefs.getInt(TutorialActivity.LAST_CARD_TAG, -1);
        currentCard++;

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowingScore)
                {
                    startPlaying();
                }
                else {
                    rv.setAdapter(scoreAdapter);
                    runLayoutAnimation(rv, R.anim.layout_animation_slide_right);
                    txtDice.setText("");
                    txtTime.setText(R.string.scores);
                    if(currentTeam.getScore()>=35)
                    {
                        txtScore.setText(getString(R.string.team_won, currentTeam.getTeamName()));
                        txtTime.setText(R.string.game_over);
                        btnNext.setText(R.string.action_replay);
                        shuffleCards();
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for(int i=0; i<results.size();i++)
                                {
                                    realm.beginTransaction();
                                    results.get(i).setScore(0);
                                    realm.commitTransaction();
                                }
                                Intent intent = getIntent();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    else {
                        int finalScore = 0;
                        if(correctAnswers>=(dice+1))
                            finalScore = (correctAnswers-dice);
                        correctAnswers = 0;
                        txtScore.setText(getString(R.string.team_score, currentTeam.getTeamName(),
                                finalScore));
                        realm.beginTransaction();
                        currentTeam.setScore((currentTeam.getScore()+finalScore));
                        realm.commitTransaction();

                        int currentId = currentTeam.getId() + 1;
                        if (currentId < numPlayers)
                            currentTeam = results.get(currentId);
                        else
                            currentTeam = results.get(0);

                        RealmList<GameCard> cards =realm.where(SortedCard.class).findFirst().getCards();
                        int size = cards.size();
                        if(currentCard>=size)
                            currentCard = 0;
                        card = cards.get(currentCard++);
                        cardAdapter.setGameCard(card);
                        cardAdapter.setScore(currentTeam.getScore());
                        cardAdapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(TutorialActivity.LAST_CARD_TAG, currentCard);
                        editor.apply();
                        editor.commit();
                    }
                }
                isShowingScore = !isShowingScore;
            }
        });

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeUp();
            }
        });

        card = realm.where(SortedCard.class).findFirst().getCards().get(currentCard++);
        cardAdapter = new CardAdapter(getApplicationContext(), card, currentTeam.getScore());
        scoreAdapter = new ScoreAdapter(getApplicationContext(), results);
        startPlaying();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, int anim) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, anim);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DICE_ROLL)
        {
            if(resultCode == RESULT_OK) {
                dice = data.getExtras().getInt(PARAM_DICE_ROLL, 0);
                txtDice.setText(String.valueOf(dice));
                timer = getTimer(31000);
                timer.start();
                rv.setAdapter(cardAdapter);
                runLayoutAnimation(rv, R.anim.layout_animation_fall_down);
            }
            else
            {
                finish();
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void rollDice()
    {
        Intent rollDice = new Intent(MainActivity.this, DiceActivity.class);
        rollDice.putExtra(PARAM_TEAM_NAME, currentTeam.getTeamName());
        startActivityForResult(rollDice, REQUEST_DICE_ROLL);
    }

    private void startPlaying()
    {
        rollDice();
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        diceView.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.VISIBLE);
        txtTime.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                0.3f
        ));
        txtScore.setText("");
        cardAdapter.setClickable(true);
        btnNext.setVisibility(View.GONE);
    }

    private void shuffleCards()
    {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SortedCard db = realm.where(SortedCard.class).findFirst();
                RealmList <GameCard> cards = db.getCards();
                int index;
                for (int i = cards.size() - 1; i > 0; i--)
                {
                    index = (int) (Math.random() * (i+1));
                    Collections.swap(cards, index, i);
                }
            }
        });
    }

    private void timeUp()
    {
        txtTime.setText(R.string.time_up);
        if(anim!=null)
            anim.cancel();
        else
            txtTime.setText(R.string.passed);
        if(timer!=null)
            timer.cancel();
        linearLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                R.color.colorPrimaryDarker));

        diceView.setVisibility(View.GONE);
        btnPass.setVisibility(View.GONE);
        txtTime.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                0.9f
        ));

        txtTime.setAlpha(1.0f);

        cardAdapter.setClickable(false);
        cardAdapter.notifyDataSetChanged();

        btnNext.setVisibility(View.VISIBLE);
    }

    private CountDownTimer getTimer(long time)
    {
        return new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                secsLeft = (int) millisUntilFinished/1000;
                txtTime.setText(String.valueOf(secsLeft));
                if(secsLeft == 5)
                    getAnimation(txtTime, View.ALPHA, 0, ValueAnimator.INFINITE,
                            ValueAnimator.REVERSE, 150).start();
            }

            public void onFinish() {
                timeUp();
            }
        };
    }

    private ObjectAnimator getAnimation(View target, Property<View, Float> property, float val,
                 int repeatCount, int repeatMode, int duration) {
        anim = ObjectAnimator.ofFloat(target, property, val);
        anim.setRepeatCount(repeatCount);
        anim.setRepeatMode(repeatMode);
        anim.setDuration(duration);
        return anim;
    }

    @Override
    public void onBackPressed() {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.show(getSupportFragmentManager(), "EXIT");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        correctAnswers = 0;
        realm.close();
        timer.cancel();
    }
    private void hideSystemUI()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            hideSystemUI();
    }
}
