package com.raizlabs.android.databasecomparison.dbflow;

import com.raizlabs.android.databasecomparison.MainActivity;
import com.raizlabs.android.databasecomparison.interfaces.IAddressBook;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Collection;

/**
 * Description:
 */
@Table(name = "AddressBook", database = DBFlowDatabase.class,
        cachingEnabled = true,
        cacheSize = MainActivity.ADDRESS_BOOK_COUNT,
        orderedCursorLookUp = true)
@ModelContainer
public class AddressBook extends BaseModel implements IAddressBook<AddressItem, Contact> {

    @PrimaryKey(autoincrement = true)
    @Column
    long id;

    @Column(name = "name")
    String name;

    @Column(name = "author")
    String author;

    Collection<AddressItem> addresses;

    Collection<Contact> contacts;

    @Override
    public void setId(long id) {
        // not needed because we have autoincrementing keys
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAddresses(Collection<AddressItem> addresses) {
        this.addresses = addresses;
    }

    public Collection<AddressItem> getAddresses() {
        if (addresses == null) {
            addresses = SQLite.select().from(AddressItem.class)
                    .where(AddressItem_Table.addressBook.is(id)).queryList();
        }
        return addresses;
    }

    public Collection<Contact> getContacts() {
        if (contacts == null) {
            contacts = SQLite.select().from(Contact.class)
                    .where(Contact_Table.addressBook.is(id)).queryList();
        }
        return contacts;
    }

    public void setContacts(Collection<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public void saveAll() {
        super.insert();
        for (AddressItem addressItem : addresses) {
            addressItem.saveAll();
        }
        for (Contact contact : contacts) {
            contact.saveAll();
        }
    }

}