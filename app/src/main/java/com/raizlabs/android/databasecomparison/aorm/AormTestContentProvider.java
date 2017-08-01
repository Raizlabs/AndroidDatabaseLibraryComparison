package com.raizlabs.android.databasecomparison.aorm;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.ieclipse.aorm.Aorm;
import cn.ieclipse.aorm.Session;

/**
 * Description
 *
 * @author Jamling
 */

public class AormTestContentProvider extends ContentProvider {
    private static Session session;
    private SQLiteOpenHelper mOpenHelper;

    public static Session getSession() {
        return session;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SQLiteOpenHelper(this.getContext(), "aorm.db", null, 1) {
            public void onCreate(SQLiteDatabase db) {
                Aorm.createTable(db, AddressBook.class);
                Aorm.createTable(db, AddressItem.class);
                Aorm.createTable(db, Contact.class);
            }

            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Aorm.updateTable(db, AddressBook.class);
                Aorm.updateTable(db, AddressItem.class);
                Aorm.updateTable(db, Contact.class);
            }
        };
        session = new Session(mOpenHelper, getContext().getContentResolver());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
