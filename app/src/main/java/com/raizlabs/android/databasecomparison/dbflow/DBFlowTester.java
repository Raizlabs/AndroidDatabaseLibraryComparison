package com.raizlabs.android.databasecomparison.dbflow;

import android.content.Context;

import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.events.LogTestDataEvent;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Collection;

import de.greenrobot.event.EventBus;

/**
 * Description:
 */
public class DBFlowTester {
    public static final String FRAMEWORK_NAME = "DBFlow";

    public static void testAddressBooks(Context context) {
        Delete.tables(AddressItem.class, Contact.class, AddressBook.class);

        Collection<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class,
                Contact.class, AddressItem.class,
                MainActivity.ADDRESS_BOOK_COUNT);

        long startTime = System.currentTimeMillis();
        final Collection<AddressBook> finalAddressBooks = addressBooks;
        FlowManager.getDatabase(DBFlowDatabase.class)
                .executeTransaction(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        for (AddressBook addressBook : finalAddressBooks) {
                            addressBook.insert();
                        }
                    }
                });
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        addressBooks = SQLite.select().from(AddressBook.class).queryList();

        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));


        Delete.tables(AddressItem.class,
                Contact.class, AddressBook.class);
    }

    public static void testAddressItems(Context context) {
        Delete.table(SimpleAddressItem.class);
        Collection<SimpleAddressItem> dbFlowModels =
                Generator.getAddresses(SimpleAddressItem.class, MainActivity.LOOP_COUNT);
        long startTime = System.currentTimeMillis();
        final Collection<SimpleAddressItem> finalDbFlowModels = dbFlowModels;
        FlowManager.getDatabase(DBFlowDatabase.class)
                .executeTransaction(FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(SimpleAddressItem.class))
                        .addAll(finalDbFlowModels)
                        .build());
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        dbFlowModels = new Select().from(SimpleAddressItem.class).queryList();
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        Delete.table(SimpleAddressItem.class);
    }
}
