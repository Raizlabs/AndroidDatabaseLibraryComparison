package com.raizlabs.android.databasecomparison;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.orm.Database;
import com.orm.SugarApp;
import com.raizlabs.android.databasecomparison.activeandroid.AddressBook;
import com.raizlabs.android.databasecomparison.activeandroid.AddressItem;
import com.raizlabs.android.databasecomparison.activeandroid.Contact;
import com.raizlabs.android.databasecomparison.activeandroid.SimpleAddressItem;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ollie.Ollie;
import se.emilsjolander.sprinkles.Sprinkles;

/**
 * Description:
 */
public class MainApplication extends SugarApp {

    private static Database mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(new Configuration.Builder(this)
                                         .setDatabaseName("activeandroid")
                                         .setDatabaseVersion(1)
                                         .setModelClasses(SimpleAddressItem.class, AddressItem.class,
                                                          AddressBook.class, Contact.class).create());

        Ollie.with(this)
                .setName("ollie")
                .setVersion(1)
                .setLogLevel(Ollie.LogLevel.FULL)
                .init();

        FlowManager.init(this);

        Sprinkles.init(this, "sprinkles.db", 2);

        Realm.init(this);

        mDatabase = getDatabase();
    }

    public static Database getSugarDatabase() {
        return mDatabase;
    }
}
