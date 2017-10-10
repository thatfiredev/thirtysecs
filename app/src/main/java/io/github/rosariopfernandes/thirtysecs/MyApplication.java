package io.github.rosariopfernandes.thirtysecs;

import android.app.Application;

import io.github.rosariopfernandes.thirtysecs.dao.SortedCard;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by rosariopfernandes on 10/5/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(new SortedCard());
                    }
                })
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
