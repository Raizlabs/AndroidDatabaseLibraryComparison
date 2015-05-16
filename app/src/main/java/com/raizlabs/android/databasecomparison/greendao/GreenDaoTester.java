package com.raizlabs.android.databasecomparison.greendao;

import android.content.Context;

import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.greendao.gen.AddressBook;
import com.raizlabs.android.databasecomparison.greendao.gen.AddressBookDao;
import com.raizlabs.android.databasecomparison.greendao.gen.Contact;
import com.raizlabs.android.databasecomparison.greendao.gen.DaoMaster;
import com.raizlabs.android.databasecomparison.greendao.gen.DaoSession;
import com.raizlabs.android.databasecomparison.greendao.gen.SimpleAddressItem;
import com.raizlabs.android.databasecomparison.greendao.gen.SimpleAddressItemDao;

import java.util.List;

/**
 * Description:
 */
public class GreenDaoTester {

    public static void testAddressItems(MainActivity mainActivity) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mainActivity, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        final SimpleAddressItemDao simpleAddressItemDao = daoSession.getSimpleAddressItemDao();
        simpleAddressItemDao.deleteAll();

        final List<SimpleAddressItem> addressItemList = GreenDaoGenerator.getSimpleAddressItems(MainActivity.LOOP_COUNT);

        long startTime = System.currentTimeMillis();
        simpleAddressItemDao.insertOrReplaceInTx(addressItemList);
        mainActivity.logTime(startTime, "GreenDao Save");

        startTime = System.currentTimeMillis();
        simpleAddressItemDao.loadAll();
        mainActivity.logTime(startTime, "GreenDao Load");

        simpleAddressItemDao.deleteAll();

    }

    public static void testAddressBooks(MainActivity mainActivity) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mainActivity, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookDao addressBookDao = daoSession.getAddressBookDao();
        addressBookDao.deleteAll();
        daoSession.getAddressItemDao().deleteAll();
        daoSession.getContactDao().deleteAll();

        List<AddressBook> addressBooks = GreenDaoGenerator.createAddressBooks(MainActivity.ADDRESS_BOOK_COUNT);

        long startTime = System.currentTimeMillis();
        addressBookDao.insertInTx(addressBooks);
        for(AddressBook addressBook: addressBooks) {
            daoSession.getContactDao().insertInTx(addressBook.getContactList());
            daoSession.getAddressItemDao().insertInTx(addressBook.getAddressItemList());
        }
        mainActivity.logTime(startTime, "GreenDAO Save");

        startTime = System.currentTimeMillis();
        addressBooks = addressBookDao.loadAll();
        for (AddressBook addressBook : addressBooks) {
            addressBook.getAddressItemList();
            List<Contact> contactList = addressBook.getContactList();
            for(Contact contact: contactList) {
                contact.getAddressBook();
            }
        }
        mainActivity.logTime(startTime, "GreenDAO Load");

        addressBookDao.deleteAll();
        daoSession.getAddressItemDao().deleteAll();
        daoSession.getContactDao().deleteAll();
    }
}
