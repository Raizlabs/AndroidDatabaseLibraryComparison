package com.raizlabs.android.databasecomparison.aorm;

import com.raizlabs.android.databasecomparison.interfaces.IAddressItem;

import cn.ieclipse.aorm.annotation.Column;
import cn.ieclipse.aorm.annotation.Table;

/**
 * Description
 *
 * @author Jamling
 */

@Table(name = "addressitem")
public class AddressItem implements IAddressItem<AddressBook> {

    @Column(name = "id", id = true)
    long id;

    @Column(name = "name")
    String name;

    @Column(name = "address")
    String address;

    @Column(name = "city")
    String city;

    @Column(name = "state")
    String state;

    @Column(name = "phone")
    long phone;

    @Column(name = "aid", type = "Integer")
    long addressBookId;

    @Override
    public void saveAll() {

    }

    public long getId() {
        return id;
    }

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

    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    public long getPhone() {
        return phone;
    }

    @Override
    public void setPhone(long phone) {
        this.phone = phone;
    }

    @Override
    public void setAddressBook(AddressBook addressBook) {
        if (addressBook != null) {
            this.addressBookId = addressBook.getId();
        }
    }
}
