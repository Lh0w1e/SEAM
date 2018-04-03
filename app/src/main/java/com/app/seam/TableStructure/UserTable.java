package com.app.seam.TableStructure;

/**
 * Created by Colinares on 3/28/2018.
 */

public class UserTable {

    public static final String TABLE_NAME = "tbl_users";


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_USER_TYPE = "user_type";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TIME_CREATED = "time_created";
    public static final String COLUMN_DATE_CREATED = "date_created";


    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_FULLNAME + " TEXT, " +
                    COLUMN_USER_TYPE + " TEXT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_TIME_CREATED + " TEXT, " +
                    COLUMN_DATE_CREATED + " TEXT " +
                    ")";


}
