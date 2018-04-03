package com.app.seam.MyFunction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.app.seam.Database.MyDatabase;
import com.app.seam.Model.Profile;
import com.app.seam.R;
import com.app.seam.TableStructure.UserTable;

/**
 * Created by Colinares on 3/28/2018.
 */

public class MyFunction {

    private static Context mContext;
    private static SQLiteDatabase mDb;

    public static void init(Context context) {
        mContext = context;
        mDb = MyDatabase.getInstance(mContext).getWritableDatabase();
    }

    public static void insert(String tableName, ContentValues contentValues) {
        mDb.insert(tableName, null, contentValues);
    }

    public static boolean update(String tableName, int id, ContentValues values) {

        int result = mDb.update(tableName, values, UserTable.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        if (result > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static void showMessage(String message) {
        final AlertDialog.Builder msgDialog = new AlertDialog.Builder(mContext);

        msgDialog.setTitle(R.string.app_name);
        msgDialog.setMessage(message);
        msgDialog.setPositiveButton("OK", null);
        msgDialog.create().show();

    }

    public static String getUserType(String username) {
        String userType = "";

        String query = "SELECT " + UserTable.COLUMN_USER_TYPE +
                " FROM " + UserTable.TABLE_NAME +
                " WHERE " + UserTable.COLUMN_USERNAME +
                " = " + "'" + username + "'" +
                " LIMIT 1";

        Cursor cursor = mDb.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            userType = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_USER_TYPE));
        }

        return userType;
    }

    public static boolean isUsernameExists(String username) {
        String query = "SELECT * FROM " + UserTable.TABLE_NAME +
                " WHERE " + UserTable.COLUMN_USERNAME +
                " = " + "'" + username + "'";

        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public static boolean hasAccount(String username, String password) {
        String query = "SELECT * FROM " + UserTable.TABLE_NAME +
                " WHERE " + UserTable.COLUMN_USERNAME +
                " = " + "'" + username + "'" +
                " AND " + UserTable.COLUMN_PASSWORD +
                " = " + "'" + password + "'";

        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public static void getUserProfileInfo(String username, String userType) {
        String query = "SELECT * FROM " + UserTable.TABLE_NAME +
                " WHERE " + UserTable.COLUMN_USERNAME +
                " = " + "'" + username + "'" +
                " AND " + UserTable.COLUMN_USER_TYPE +
                " = " + "'" + userType + "'" +
                " LIMIT 1";

        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            int profile_id = cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_ID));
            String fullname = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_FULLNAME));
            String password = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_PASSWORD));

            Profile.profile_id = profile_id;
            Profile.fullName = fullname;
            Profile.userType = userType;
            Profile.userName = username;
            Profile.password = password;

        } else {
            Log.e("failed", " error in getting profile");
        }

    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conn.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || conn.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
                || conn.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED
                || conn.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING) {

            return true;
        }else if(conn.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTING
                || conn.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED){
            return false;
        }

        return false;
    }

}
