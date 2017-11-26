package com.dariaemacs.cmoney;

/**
 * Created by dariaemacs on 18.11.17.
 */
class CardEntry {
    public static final String TABLE_NAME = "cards";
    public static final String COLUMN_NAME_ENTRY_ID = "id";
    public static final String COLUMN_NAME_VALUE = "value";

    public static final String[] RETURN_VALUE_COLUMN = new String[] {COLUMN_NAME_VALUE};
    public static final String SELECTION = COLUMN_NAME_ENTRY_ID + " = ?";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_NAME_ENTRY_ID + " INTEGER NOT NULL PRIMARY KEY,"
            + COLUMN_NAME_VALUE + " INTEGER CHECK ("
            + COLUMN_NAME_VALUE + " >= 0)  NOT NULL)";
}
