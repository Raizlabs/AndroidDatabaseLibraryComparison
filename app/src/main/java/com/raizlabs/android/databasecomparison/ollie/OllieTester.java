package com.raizlabs.android.databasecomparison.ollie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.Loader;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.Saver;
import com.raizlabs.android.databasecomparison.events.LogTestDataEvent;

import java.util.Collection;

import de.greenrobot.event.EventBus;
import ollie.Ollie;
import ollie.query.Delete;
import ollie.query.Select;

/**
 * Created by Tjones on 8/16/15.
 */
public class OllieTester {
    public static final String FRAMEWORK_NAME = "Ollie";

    public static void testAddressBooks(Context context) {
        Delete.from(AddressItem.class).execute();
        Delete.from(Contact.class).execute();
        Delete.from(AddressBook.class).execute();

        Collection<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class, Contact.class, AddressItem.class, MainActivity.ADDRESS_BOOK_COUNT);

        long startTime = System.currentTimeMillis();
        final Collection<AddressBook> finalAddressBooks = addressBooks;
        SQLiteDatabase database = Ollie.getDatabase();
        database.beginTransaction();
        try {
            Saver.saveAll(finalAddressBooks);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        addressBooks = Select.from(AddressBook.class).fetch();
        Loader.loadAllInnerData(addressBooks);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        Delete.from(AddressItem.class).execute();
        Delete.from(Contact.class).execute();
        Delete.from(AddressBook.class).execute();
    }

    public static void testAddressItems(Context context) {
        Delete.from(SimpleAddressItem.class).execute();

        final Collection<SimpleAddressItem> ollieModels =
                Generator.getAddresses(SimpleAddressItem.class, MainActivity.LOOP_COUNT);

        long startTime = System.currentTimeMillis();
        SQLiteDatabase database = Ollie.getDatabase();
        database.beginTransaction();
        try {
            Saver.saveAll(ollieModels);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        Collection<SimpleAddressItem> activeAndroidModelLoad =
                Select.from(SimpleAddressItem.class).fetch();
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        Delete.from(SimpleAddressItem.class).execute();
    }
}
