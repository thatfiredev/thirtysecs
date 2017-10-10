package io.github.rosariopfernandes.thirtysecs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import io.github.rosariopfernandes.thirtysecs.dao.SortedCard;
import io.realm.Realm;

public class TutorialActivity extends AppCompatActivity {

    public static final String PREFERENCES_TAG = "io.github.rosariopfernandes.thirtysecs.prefs";
    public static final String LAST_CARD_TAG = "io.github.rosariopfernandes.thirtysecs.lastCard";
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        hideSystemUI();

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        ImageView imageView = (ImageView) findViewById(R.id.btnAbout);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        final Button btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TutorialActivity.this, TeamActivity.class);
                startActivity(intent);
            }
        });

        SortedCard cards = realm.where(SortedCard.class).findFirst();
        if(cards == null)
        {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    InputStream is = getResources().openRawResource(R.raw.words);
                    realm.createOrUpdateObjectFromJson(SortedCard.class, is);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    btnPlay.setText(R.string.action_play);
                    btnPlay.setEnabled(true);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    ((TextView) findViewById(R.id.txtDescription)).setText(error.getMessage());
                    btnPlay.setText(R.string.error);
                }
            });
        }
        else
        {
            btnPlay.setText(R.string.action_play);
            btnPlay.setEnabled(true);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
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
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: Add Dialog for when the user is about to close the app
    }
}
