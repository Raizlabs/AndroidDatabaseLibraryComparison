package com.raizlabs.android.databasecomparison.aorm;

import android.content.Context;

import com.raizlabs.android.databasecomparison.Generator;
import com.raizlabs.android.databasecomparison.Loader;
import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.events.LogTestDataEvent;

import java.sql.SQLException;
import java.util.Collection;

import cn.ieclipse.aorm.Aorm;
import cn.ieclipse.aorm.Criteria;
import cn.ieclipse.aorm.Restrictions;
import cn.ieclipse.aorm.Session;
import de.greenrobot.event.EventBus;

/**
 * Description
 *
 * @author Jamling
 */

public class AormTester {
    public static final String FRAMEWORK_NAME = "Aorm";

    public static void testAddressBooks(Context context) {
        Session session = AormTestContentProvider.getSession();
        try {
            session.deleteAll(AddressBook.class);
            session.deleteAll(AddressItem.class);
            session.deleteAll(Contact.class);
        } catch (Exception e) {
            Aorm.logi(e.getMessage());
        }

        Collection<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class, Contact.class,
            AddressItem.class, MainActivity.ADDRESS_BOOK_COUNT);
        long startTime = System.currentTimeMillis();

        try {
            session.beginTransaction();
            session.batchInsert(addressBooks);
            for (AddressBook addressBook : addressBooks) {
                addressBook.insertAddressItem(session);
                addressBook.insertContact(session);
            }
            session.endTransaction();
            EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

            startTime = System.currentTimeMillis();
            addressBooks = session.list(AddressBook.class);
            for (AddressBook addressBook : addressBooks) {
                Criteria criteria = Criteria.create(Contact.class).add(Restrictions.eq("addressbook", addressBook
                    .getId()));
                addressBook.setContacts(session.list(criteria));
                criteria = Criteria.create(AddressItem.class).add(Restrictions.eq("addressbook", addressBook
                    .getId()));
                addressBook.setAddresses(session.list(criteria));
            }

            Loader.loadAllInnerData(addressBooks);
            EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

            // clean out DB for next run
            session.deleteAll(AddressBook.class);
            session.deleteAll(AddressItem.class);
            session.deleteAll(Contact.class);
        } catch (Exception e) {
            Aorm.logi(e.getMessage());
        }
    }

    public static void testAddressItems(Context context) {

        Session session = AormTestContentProvider.getSession();

        session.deleteAll(AddressItem.class);

        Collection<AddressItem> simpleAddressItems = Generator.getAddresses(AddressItem.class, MainActivity.LOOP_COUNT);
        long startTime = System.currentTimeMillis();

        try {
            session.batchInsert(simpleAddressItems);
            for (AddressItem simpleAddressItem : simpleAddressItems) {
                //session.insertNative(simpleAddressItem);
            }
        } catch (SQLException e){
            Aorm.logi(e.getMessage());
        }
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.SAVE_TIME));

        startTime = System.currentTimeMillis();
        simpleAddressItems = session.list(AddressItem.class);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, FRAMEWORK_NAME, MainActivity.LOAD_TIME));

        // clean out DB for next run
        session.deleteAll(AddressItem.class);
    }
}