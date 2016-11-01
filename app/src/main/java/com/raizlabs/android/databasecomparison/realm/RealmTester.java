package com.raizlabs.android.databasecomparison.realm;

import android.content.Context;

import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.events.LogTestDataEvent;

import java.util.Collection;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * tester for Realm database
 */
public class RealmTester {
    public static final String FRAMEWORK_NAME = "Realm";

    public static void testAddressBooks(Context context) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(AddressItem.class).findAll().deleteAllFromRealm();
        realm.where(AddressBook.class).findAll().deleteAllFromRealm();
        realm.where(Contact.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();

        Collection<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class,
                Contact.class, AddressItem.class, MainActivity.ADDRESS_BOOK_COUNT);
        long startTime = System.currentTimeMillis();
        final Collection<AddressBook> finalAddressBooks = addressBooks;
        realm.beginTransaction();
        realm.insertOrUpdate(finalAddressBooks);
        realm.commitTransaction();
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        addressBooks = realm.where(AddressBook.class).findAll();
        assert(50 == addressBooks.size());
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        realm.beginTransaction();
        realm.where(AddressItem.class).findAll().deleteAllFromRealm();
        realm.where(AddressBook.class).findAll().deleteAllFromRealm();
        realm.where(Contact.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();

        realm.close();
    }

    public static void testAddressItems(Context context) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SimpleAddressItem.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();

        Collection<SimpleAddressItem> modelList = Generator.getAddresses(SimpleAddressItem.class, MainActivity.LOOP_COUNT);
        long startTime = System.currentTimeMillis();
        realm.beginTransaction();
        realm.insertOrUpdate(modelList);
        realm.commitTransaction();
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        modelList = realm.where(SimpleAddressItem.class).findAll();
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        realm.beginTransaction();
        realm.where(SimpleAddressItem.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();

        realm.close();
    }
}
