package com.raizlabs.android.databasecomparison.aorm;

import com.raizlabs.android.databasecomparison.interfaces.IAddressBook;

import java.sql.SQLException;
import java.util.Collection;

import cn.ieclipse.aorm.Session;
import cn.ieclipse.aorm.annotation.Column;
import cn.ieclipse.aorm.annotation.Table;

/**
 * Description
 *
 * @author Jamling
 */

@Table(name = "addressbook")
public class AddressBook implements IAddressBook<AddressItem, Contact> {

    @Column(name = "id", id = true)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "author")
    private String author;

    private Collection<AddressItem> addresses;

    private Collection<Contact> contacts;

    @Override
    public void saveAll() {

    }

    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public Collection<AddressItem> getAddresses() {
        return addresses;
    }

    @Override
    public void setAddresses(Collection<AddressItem> addresses) {
        this.addresses = addresses;
    }

    @Override
    public Collection<Contact> getContacts() {
        return contacts;
    }

    @Override
    public void setContacts(Collection<Contact> contacts) {
        this.contacts = contacts;
    }

    public void insertAddressItem(Session session) throws SQLException{
        session.batchInsert(getAddresses());
    }

    public void insertContact(Session session) throws SQLException{
        session.batchInsert(getContacts());
    }
}
