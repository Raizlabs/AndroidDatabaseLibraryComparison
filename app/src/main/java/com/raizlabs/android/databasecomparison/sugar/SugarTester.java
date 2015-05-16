package com.raizlabs.android.databasecomparison.sugar;

import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.Loader;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.MainApplication;
import com.raizlabs.android.databasecomparison.Saver;
import com.raizlabs.android.dbflow.runtime.TransactionManager;

import java.util.List;

/**
 * Description:
 */
public class SugarTester {

    public static void testSugarAddressBooks(MainActivity mainActivity) {
        AddressItem.deleteAll(AddressItem.class);
        AddressBook.deleteAll(AddressBook.class);
        Contact.deleteAll(Contact.class);

        List<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class,
                Contact.class, AddressItem.class, MainActivity.ADDRESS_BOOK_COUNT);
        long startTime = System.currentTimeMillis();
        final List<AddressBook> finalAddressBooks = addressBooks;
        TransactionManager.transact(MainApplication.getSugarDatabase().getDB(), new Runnable() {
            @Override
            public void run() {
                Saver.saveAll(finalAddressBooks);
            }
        });
        mainActivity.logTime(startTime, "Sugar Save");

        startTime = System.currentTimeMillis();
        addressBooks = AddressBook.listAll(AddressBook.class);
        Loader.loadAllInnerData(addressBooks);
        mainActivity.logTime(startTime, "Sugar Load");

        AddressItem.deleteAll(AddressItem.class);
        AddressBook.deleteAll(AddressBook.class);
        Contact.deleteAll(Contact.class);
    }

    public static void testSugarAddressItems(MainActivity mainActivity) {
        SimpleAddressItem.deleteAll(SimpleAddressItem.class);

        List<SimpleAddressItem> sugarModelList = Generator.getAddresses(SimpleAddressItem.class, MainActivity.LOOP_COUNT);
        long startTime = System.currentTimeMillis();
        SimpleAddressItem.saveInTx(sugarModelList);
        mainActivity.logTime(startTime, "Sugar Save");

        startTime = System.currentTimeMillis();
        sugarModelList = SimpleAddressItem.listAll(SimpleAddressItem.class);
        mainActivity.logTime(startTime, "Sugar Load");

        SimpleAddressItem.deleteAll(SimpleAddressItem.class);
    }
}
