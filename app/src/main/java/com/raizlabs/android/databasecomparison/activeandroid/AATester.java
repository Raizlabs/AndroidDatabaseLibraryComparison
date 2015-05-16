package com.raizlabs.android.databasecomparison.activeandroid;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.Loader;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.Saver;
import com.raizlabs.android.dbflow.runtime.TransactionManager;

import java.util.List;

/**
 * Description:
 */
public class AATester {

    public static void testAAAddressBooks(MainActivity mainActivity) {
        new Delete().from(AddressItem.class).execute();
        new Delete().from(Contact.class).execute();
        new Delete().from(AddressBook.class).execute();

        List<AddressBook> addressBooks =
                Generator.createAddressBooks(AddressBook.class,
                        Contact.class,
                        AddressItem.class,
                        MainActivity.ADDRESS_BOOK_COUNT);
        long startTime = System.currentTimeMillis();
        final List<AddressBook> finalAddressBooks = addressBooks;
        TransactionManager.transact(ActiveAndroid.getDatabase(), new Runnable() {
            @Override
            public void run() {
                Saver.saveAll(finalAddressBooks);
            }
        });
        mainActivity.logTime(startTime, "ActiveAndroid Save");

        startTime = System.currentTimeMillis();
        addressBooks = new Select().from(AddressBook.class).execute();
        Loader.loadAllInnerData(addressBooks);
        mainActivity.logTime(startTime, "ActiveAndroid Load");

        new Delete().from(AddressItem.class).execute();
        new Delete().from(Contact.class).execute();
        new Delete().from(AddressBook.class).execute();
    }

    public static void testAAAddressItems(MainActivity mainActivity) {
        new Delete().from(SimpleAddressItem.class).execute();

        final List<SimpleAddressItem> activeAndroidModels =
                Generator.getAddresses(SimpleAddressItem.class, MainActivity.LOOP_COUNT);

        long startTime = System.currentTimeMillis();
        // Reuse method so we don't have to write
        TransactionManager.transact(ActiveAndroid.getDatabase(), new Runnable() {
            @Override
            public void run() {
                Saver.saveAll(activeAndroidModels);
            }
        });
        mainActivity.logTime(startTime, "ActiveAndroid Save");

        startTime = System.currentTimeMillis();
        List<SimpleAddressItem> activeAndroidModelLoad =
                new Select().from(SimpleAddressItem.class).execute();
        mainActivity.logTime(startTime, "ActiveAndroid Load");

        new Delete().from(SimpleAddressItem.class).execute();
    }
}
