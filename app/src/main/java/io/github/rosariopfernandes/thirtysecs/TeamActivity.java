package io.github.rosariopfernandes.thirtysecs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.github.rosariopfernandes.thirtysecs.dao.TeamScore;
import io.realm.Realm;

public class TeamActivity extends AppCompatActivity {
    private EditText [] txtTeams;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        hideSystemUI();

        realm = Realm.getDefaultInstance();
        deleteAll();

        TextInputLayout [] inputTeams = new TextInputLayout[4];
        txtTeams = new EditText[4];
        inputTeams[0] = (TextInputLayout) findViewById(R.id.inputTeam1);
        inputTeams[1] = (TextInputLayout) findViewById(R.id.inputTeam2);
        inputTeams[2] = (TextInputLayout) findViewById(R.id.inputTeam3);
        inputTeams[3] = (TextInputLayout) findViewById(R.id.inputTeam4);
        txtTeams[0] = (EditText) findViewById(R.id.txtTeam1);
        txtTeams[1] = (EditText) findViewById(R.id.txtTeam2);
        txtTeams[2] = (EditText) findViewById(R.id.txtTeam3);
        txtTeams[3] = (EditText) findViewById(R.id.txtTeam4);

        for(int i= 0; i<inputTeams.length; i++) {
            inputTeams[i].setHint(getString(R.string.team, (i+1)));
        }

        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamName;
                int currentIndex = 0;
                for (EditText txtTeam : txtTeams) {
                    teamName = txtTeam.getText().toString();
                    if(realm.where(TeamScore.class).equalTo("teamName", teamName).findFirst() != null)
                        teamName = teamName + (currentIndex+1)*teamName.length();
                    if (!teamName.equalsIgnoreCase("")) {
                        realm.beginTransaction();
                        TeamScore teamScore = realm.createObject(TeamScore.class,
                                currentIndex + "");
                        if(teamName.length()>24)
                            teamName = teamName.substring(0,24);
                        teamScore.setTeamName(teamName);
                        teamScore.setScore(0);
                        realm.commitTransaction();
                        currentIndex++;
                    }
                }
                if(currentIndex<2)
                {
                    Toast.makeText(TeamActivity.this, R.string.prompt_teams,
                            Toast.LENGTH_SHORT).show();
                    deleteAll();
                }
                else {
                    //TODO: Roll the dice to decide which team starts
                    startGame();
                }
            }
        });


        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startGame()
    {
        Intent intent = new Intent(TeamActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteAll()
    {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(TeamScore.class).findAll().deleteAllFromRealm();
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
