package io.github.rosariopfernandes.thirtysecs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import io.github.rosariopfernandes.thirtysecs.fragment.AlertDialogFragment;

public class DiceActivity extends AppCompatActivity implements View.OnClickListener{
    private final int rollAnimations = 50;
    private final int[] diceNumbers = new int[] {0, 1, 2, 0, 1, 2};
    private int roll = 6;
    private Button btnRoll;
    private CardView dice;
    private TextView txtDiceNr, txtCurrentTeam;
    private Handler animationHandler;
    private boolean paused = false;
    private boolean wasRolled = false;
    private Intent intent;
    private MediaPlayer mp;

    @Override
    public void onClick(View v) {
        //try {
        if(wasRolled)
        {
            int nr = Integer.parseInt(txtDiceNr.getText().toString());
            intent.putExtra(MainActivity.PARAM_DICE_ROLL, nr);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
            rollDice();
        //} catch (Exception e) {}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        paused = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);
        setResult(RESULT_CANCELED);
        hideSystemUI();

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        dice = (CardView) findViewById(R.id.dice);
        txtDiceNr = (TextView) findViewById(R.id.txtDiceNr);
        txtCurrentTeam = (TextView) findViewById(R.id.txtCurrentTeam);
        btnRoll = (Button) findViewById(R.id.btnRoll);

        intent = getIntent();
        String teamName = intent.getExtras().getString(MainActivity.PARAM_TEAM_NAME, "Unnamed");
        txtCurrentTeam.setText(getString(R.string.prompt_roll, teamName));

        btnRoll.setOnClickListener(this);
        dice.setOnClickListener(this);
        animationHandler = new Handler() {
            public void handleMessage(Message msg) {
                txtDiceNr.setText(String.valueOf(diceNumbers[roll]));
            }
        };
        roll = (int) (Math.random() * 6);
        txtDiceNr.setText(String.valueOf(diceNumbers[roll]));
    }

    private void rollDice() {
        if (paused) return;
        float anim = (float) ((Math.random() * 500)-250);
        float animY = (float) ((Math.random() * 500)-500);
        ObjectAnimator rotateAnimation= new ObjectAnimator().ofFloat(dice, View.ROTATION, 360);
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setDuration(1000);
        ObjectAnimator transAnimation= new ObjectAnimator().ofFloat(dice, View.TRANSLATION_Y, animY);
        transAnimation.setRepeatCount(1);
        transAnimation.setRepeatMode(ValueAnimator.REVERSE);
        transAnimation.setDuration(500);
        //transAnimation.setInterpolator(new OvershootInterpolator());
        ObjectAnimator transAnim= new ObjectAnimator().ofFloat(dice, View.TRANSLATION_X, anim);
        transAnim.setRepeatCount(0);
        transAnim.setDuration(1000);
        transAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f);
        ObjectAnimator scaleAnimation= ObjectAnimator.ofPropertyValuesHolder(dice, pvhX, pvhY);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimation.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.play(rotateAnimation).with(transAnimation).with(scaleAnimation).with(transAnim);
        set.start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < rollAnimations; i++)
                    doRoll();
            }
        }).start();
        mp = MediaPlayer.create(this, R.raw.roll);
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();

        wasRolled = true;
        btnRoll.setText(R.string.action_begin);
    }

    private void doRoll() { // only does a single roll
        roll = (int) (Math.random() * 6);
        synchronized (getLayoutInflater()) {
            animationHandler.sendEmptyMessage(0);
        }
        try { // delay to alloy for smooth animation
            Thread.sleep(20);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        paused = false;
    }

    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mp!=null) {
            if(mp.isPlaying())
                mp.stop();
            mp.release();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.show(getSupportFragmentManager(), "EXIT");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            hideSystemUI();
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
}
