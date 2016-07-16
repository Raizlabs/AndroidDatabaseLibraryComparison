package com.raizlabs.android.databasecomparison.activeandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.Loader;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.Saver;
import com.raizlabs.android.databasecomparison.events.LogTestDataEvent;

import java.util.Collection;

import de.greenrobot.event.EventBus;

/**
 * Description:
 */
public class AATester {
    public static final String FRAMEWORK_NAME = "ActiveAndroid";

    public static void testAddressBooks(Context context) {
        new Delete().from(AddressItem.class).execute();
        new Delete().from(Contact.class).execute();
        new Delete().from(AddressBook.class).execute();

        Collection<AddressBook> addressBooks =
                Generator.createAddressBooks(AddressBook.class,
                        Contact.class,
                        AddressItem.class,
                        MainActivity.ADDRESS_BOOK_COUNT);
        long startTime = System.currentTimeMillis();
        final Collection<AddressBook> finalAddressBooks = addressBooks;
        SQLiteDatabase database = ActiveAndroid.getDatabase();
        database.beginTransaction();
        try {
            Saver.saveAll(finalAddressBooks);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        addressBooks = new Select().from(AddressBook.class).execute();
        Loader.loadAllInnerData(addressBooks);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        new Delete().from(AddressItem.class).execute();
        new Delete().from(Contact.class).execute();
        new Delete().from(AddressBook.class).execute();
    }

    public static void testAddressItems(Context context) {
        new Delete().from(SimpleAddressItem.class).execute();

        final Collection<SimpleAddressItem> activeAndroidModels =
                Generator.getAddresses(SimpleAddressItem.class, MainActivity.LOOP_COUNT);

        long startTime = System.currentTimeMillis();
        SQLiteDatabase database = ActiveAndroid.getDatabase();
        database.beginTransaction();
        try {
            Saver.saveAll(activeAndroidModels);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        Collection<SimpleAddressItem> activeAndroidModelLoad =
                new Select().from(SimpleAddressItem.class).execute();
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        new Delete().from(SimpleAddressItem.class).execute();
    }
}
