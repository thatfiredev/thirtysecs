package io.github.rosariopfernandes.thirtysecs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import io.github.rosariopfernandes.thirtysecs.dao.GameCard;
import io.github.rosariopfernandes.thirtysecs.dao.SortedCard;
import io.github.rosariopfernandes.thirtysecs.dao.TeamScore;
import io.realm.Realm;
import io.realm.RealmList;

public class MenuActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static EditText[] txtTeams;

    private Realm realm;
    private Button btnPlay;

    public static final String PREFERENCES_TAG = "io.github,rosariopfernandes.thirtysecs.prefs";
    public static final String LAST_CARD_TAG = "io.github,rosariopfernandes.thirtysecs.prefs.lastCard";
    public static final String DECK_SIZE_TAG = "io.github,rosariopfernandes.thirtysecs.prefs.deck";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        hideSystemUI();

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        deleteAll();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        prefs = getSharedPreferences(PREFERENCES_TAG, MODE_PRIVATE);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewPager.getCurrentItem()==0)
                {
                    mViewPager.setCurrentItem(1, true);
                }
                else
                {
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
                        Toast.makeText(MenuActivity.this, R.string.prompt_teams,
                                Toast.LENGTH_SHORT).show();
                        deleteAll();
                    }
                    else {
                        //TODO: Roll the dice to decide which team starts
                        startGame();
                        for (EditText txtTeam : txtTeams)
                            txtTeam.setText("");
                        mViewPager.setCurrentItem(0);
                    }
                }
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.btnAbout);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        final SortedCard sortedCards = realm.where(SortedCard.class).findFirst();
        if(sortedCards == null)
        {
            readInsertCards(sortedCards);
        }
        else
        {
            int currentDeckSize = prefs.getInt(DECK_SIZE_TAG, 999);
            /*if(currentDeckSize<) TODO: Read size on the file and check if its larger
            if(sortedCards.getCards().size())*/
            btnPlay.setText(R.string.action_play);
            btnPlay.setEnabled(true);
        }
    }

    private void readInsertCards(final SortedCard sortedCards)
    {
        final InputStream is = getResources().openRawResource(R.raw.words);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateObjectFromJson(SortedCard.class, is);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                shuffleCards(sortedCards);
                btnPlay.setText(R.string.action_play);
                btnPlay.setEnabled(true);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                ((TextView) findViewById(R.id.txtDescription)).setText(error.getMessage());
                error.printStackTrace();
                btnPlay.setText(R.string.error);
            }
        });
    }

    private void shuffleCards(final SortedCard sortedCard)
    {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm){
                RealmList<GameCard> cards = sortedCard.getCards();
                int index;
                for (int i = cards.size() - 1; i > 0; i--)
                {
                    index = (int) (Math.random() * (i+1));
                    swap(cards, index, i);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(DECK_SIZE_TAG, cards.size());
                editor.apply();
                editor.commit();
            }
        });
    }

    private void swap(RealmList<GameCard> list, int i, int j) {
        final RealmList l = list;
        l.set(i, l.set(j, l.get(i)));
    }

    private void hideSystemUI()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        deleteAll();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(mViewPager.getCurrentItem() == 0)
        {
            super.onBackPressed();
            //TODO: Add Dialog for when the user is about to close the app
        }
        else
        {
            mViewPager.setCurrentItem(0, true);
        }
    }

    private void startGame()
    {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivityForResult(intent, 101);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            if(page == 0) {
                rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            }
            else
            {
                rootView = inflater.inflate(R.layout.fragment_team, container, false);
                TextInputLayout[] inputTeams = new TextInputLayout[4];
                txtTeams = new EditText[4];
                inputTeams[0] = (TextInputLayout) rootView.findViewById(R.id.inputTeam1);
                inputTeams[1] = (TextInputLayout) rootView.findViewById(R.id.inputTeam2);
                inputTeams[2] = (TextInputLayout) rootView.findViewById(R.id.inputTeam3);
                inputTeams[3] = (TextInputLayout) rootView.findViewById(R.id.inputTeam4);
                txtTeams[0] = (EditText) rootView.findViewById(R.id.txtTeam1);
                txtTeams[1] = (EditText) rootView.findViewById(R.id.txtTeam2);
                txtTeams[2] = (EditText) rootView.findViewById(R.id.txtTeam3);
                txtTeams[3] = (EditText) rootView.findViewById(R.id.txtTeam4);

                for(int i= 0; i<inputTeams.length; i++) {
                    inputTeams[i].setHint(getString(R.string.team, (i+1)));
                }
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
