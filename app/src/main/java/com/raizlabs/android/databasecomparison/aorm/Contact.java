package com.raizlabs.android.databasecomparison.aorm;

import com.raizlabs.android.databasecomparison.interfaces.IContact;

import cn.ieclipse.aorm.Criteria;
import cn.ieclipse.aorm.Restrictions;
import cn.ieclipse.aorm.annotation.Column;
import cn.ieclipse.aorm.annotation.Table;

/**
 * Description
 *
 * @author Jamling
 */

@Table(name = "contact")
public class Contact implements IContact<AddressBook> {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "aid")
    private long addressBookId;

    private AddressBook addressBook;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public AddressBook getAddressBookField() {
        if (addressBook == null) {
            addressBook = (AddressBook) AormTestContentProvider.getSession().get(
                Criteria.create(AddressBook.class).add(Restrictions.eq("id", addressBookId)));
        }
        return addressBook;
    }

    @Override
    public void setAddressBook(AddressBook addressBook) {
        if (addressBook != null) {
            this.addressBookId = addressBook.getId();
        }
    }

    @Override
    public void saveAll() {

    }
}
