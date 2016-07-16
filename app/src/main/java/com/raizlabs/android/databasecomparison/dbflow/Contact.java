package com.raizlabs.android.databasecomparison.dbflow;

import com.raizlabs.android.databasecomparison.interfaces.IContact;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

/**
 * Description:
 */
@Table(name = "contact", database = DBFlowDatabase.class)
public class Contact extends BaseModel implements IContact<AddressBook> {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    String email;

    @ForeignKey(references =
            {@ForeignKeyReference(columnName = "addressBook",
                    foreignKeyColumnName = "id",
                    columnType = long.class)},
            saveForeignKeyModel = false)
    ForeignKeyContainer<AddressBook> addressBook;

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

    @Override
    public AddressBook getAddressBookField() {
        return addressBook.toModel();
    }

    @Override
    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = FlowManager.getContainerAdapter(AddressBook.class)
                .toForeignKeyContainer(addressBook);
    }

    @Override
    public void saveAll() {
        super.insert();
    }
}
