package com.find.me.dbHelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.find.me.List;
import com.find.me.model.Task;
import com.find.me.model.User;

public class UsersDB {

    final String TAG = "UsersDB";

    // database constants
    public static final String DB_NAME = "tasklist.db";
    public static final int DB_VERSION = 2;

    //users table constants
    public static final String USERS_TABLE = "users";
    public static final String Contacts_TABLE = "contacts";

    public static final String USERS_ID = "_id";
    public static final int USERS_ID_COL = 0;

    public static final String USERS_USERNAME = "_username";
    public static final String Contact_NAME = "_contactname";
    public static final int USERS_USERNAME_COL = 1;
    public static final int CONTACT_NAME_COL = 2;

    public static final String USERS_PASSWORD = "_password";
    public static final int USERS_PASSWORD_COL = 2;

    public static final String USERS_PHONE = "_phone";
    public static final int USERS_PHONE_COL = 3;

    public static final String USERS_IQAMA = "_iqama";
    public static final int USERS_IQAMA_COL = 4;

    public static final String USERS_DOB = "_dob";
    public static final int USERS_DOB_COL = 5;

    // list table constants
    public static final String LIST_TABLE = "list";

    public static final String LIST_ID = "_id";
    public static final int LIST_ID_COL = 0;

    public static final String LIST_NAME = "list_name";
    public static final int LIST_NAME_COL = 1;

    // task table constants
    public static final String TASK_TABLE = "task";

    public static final String TASK_ID = "_id";
    public static final int TASK_ID_COL = 0;

    public static final String TASK_LIST_ID = "list_id";
    public static final int TASK_LIST_ID_COL = 1;

    public static final String TASK_NAME = "task_name";
    public static final int TASK_NAME_COL = 2;

    public static final String TASK_NOTES = "notes";
    public static final int TASK_NOTES_COL = 3;

    public static final String TASK_COMPLETED = "date_completed";
    public static final int TASK_COMPLETED_COL = 4;

    public static final String TASK_HIDDEN = "hidden";
    public static final int TASK_HIDDEN_COL = 5;

    public static final String TASK_FEE = "fee";
    public static final int TASK_FEE_COL = 6;

    // CREATE and DROP TABLE statements
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USERS_TABLE + " (" +
                    USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USERS_USERNAME + " TEXT, " +
                    USERS_PASSWORD + " TEXT, " +
                    USERS_IQAMA + " TEXT, " +
                    USERS_DOB + " TEXT, " +
                    USERS_PHONE + " TEXT);";
    public static final String CREATE_Contacts_TABLE =
            "CREATE TABLE " + Contacts_TABLE + " (" +
                    USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USERS_USERNAME + " TEXT, " +
                    Contact_NAME + " TEXT, " +
                    USERS_PHONE + " TEXT);";

    public static final String CREATE_LIST_TABLE =
            "CREATE TABLE " + LIST_TABLE + " (" +
                    LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LIST_NAME + " TEXT    NOT NULL UNIQUE);";

    public static final String CREATE_TASK_TABLE =
            "CREATE TABLE " + TASK_TABLE + " (" +
                    TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_LIST_ID + " INTEGER NOT NULL, " +
                    TASK_NAME + " TEXT    NOT NULL, " +
                    TASK_NOTES + " TEXT, " +
                    TASK_COMPLETED + " TEXT, " +
                    TASK_HIDDEN + " TEXT, " +
                    TASK_FEE + " DOUBLE);";

    public static final String DROP_USERS_TABLE =
            "DROP TABLE IF EXISTS " + USERS_TABLE;
    public static final String DROP_COntacts_TABLE =
            "DROP TABLE IF EXISTS " + Contacts_TABLE;

    public static final String DROP_LIST_TABLE =
            "DROP TABLE IF EXISTS " + LIST_TABLE;

    public static final String DROP_TASK_TABLE =
            "DROP TABLE IF EXISTS " + TASK_TABLE;

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_LIST_TABLE);
            db.execSQL(CREATE_TASK_TABLE);
            db.execSQL(CREATE_Contacts_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {

            Log.d("Task list", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);

            db.execSQL(UsersDB.DROP_USERS_TABLE);
            db.execSQL(UsersDB.DROP_LIST_TABLE);
            db.execSQL(UsersDB.DROP_TASK_TABLE);
            db.execSQL(UsersDB.DROP_COntacts_TABLE);
            onCreate(db);
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public UsersDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    // public methods

    //NOTE: Only the USER methods are used in this app, the rest are there for reference since they were
    //in the DB code that I based myself on

    //USER METHODS
    /////////////////////////////////////////////////

    /**
     * Gets all users from the database
     *
     * @return ArrayList of users
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        this.openReadableDB();
        Cursor cur = db.rawQuery("SELECT * FROM " + USERS_TABLE, null);
        boolean exist = (cur.getCount() > 0);

        if (exist) {
            while (cur.moveToNext()) {
                users.add(getUserFromCursor(cur));
                Log.d(TAG, "Got a user");
            }
        }
        if (cur != null)
            cur.close();
        this.closeDB();
        return users;
    }

    public ArrayList<User> getRelativeList() {
        ArrayList<User> users = new ArrayList<>();
        this.openReadableDB();
        Cursor cur = db.rawQuery("SELECT * FROM " + Contacts_TABLE, null);
        boolean exist = (cur.getCount() > 0);
        if (exist) {
            while (cur.moveToNext()) {
                users.add(getRelatoveFromCursor(cur));
                Log.d(TAG, "Got a user");
            }
        }
        if (cur != null)
            cur.close();
        this.closeDB();
        return users;
    }

    /**
     * Checks if the username and password match to what is on the database
     *
     * @param user
     * @return true if they match
     */
    public boolean checkCredentials(User user) {
        boolean success = false;
        if (isPasswordValid(user)) {
            success = true;
        } else
            success = false;

        return success;

    }

    /**
     * Checks if the password correct
     *
     * @param user
     * @return true if password is correct
     */
    private boolean isPasswordValid(User user) {
        boolean success = false;
        String username = user.getUsername();

        //checks if the username exists
        if (userExists(username)) {
            User userFromDB = getUser(username);

            //checks if the passwords equal to each other
            if (userFromDB.getPassword().equals(user.getPassword()))
                success = true;
            else
                success = false;
            Log.i("tariq", "isPasswordValid: " + username + userFromDB + userFromDB.getPassword() + user.getPassword());

        } else {
            success = false;
        }
        return success;
    }

    /**
     * Checks the database to see if the user exists
     *
     * @param aUser
     * @return true if the user exists
     */
    public boolean userExists(String aUser) {
        this.openReadableDB();
        Cursor cur = db.rawQuery("SELECT * FROM " + USERS_TABLE + " WHERE " + USERS_USERNAME + " = '" + aUser + "'", null);
        boolean exist = (cur.getCount() > 0);
        cur.close();
        this.closeDB();
        return exist;

    }

    public boolean contactExists(String numner, String aUser) {
        this.openReadableDB();
        Cursor cur = db.rawQuery("SELECT * FROM " + Contacts_TABLE + " WHERE " + USERS_PHONE + " = '" + numner + "'", null);
        boolean exist = (cur.getCount() > 0);
        cur.close();
        this.closeDB();
        return exist;

    }

    /**
     * Gets a User from the cursor
     *
     * @param cursor
     * @return User
     */
    private static User getUserFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        } else {
            try {
                User user = new User(
                        cursor.getString(USERS_USERNAME_COL),
                        cursor.getString(USERS_PASSWORD_COL),
                        cursor.getString(USERS_PHONE_COL),
                        cursor.getString(USERS_IQAMA_COL),
                        cursor.getString(USERS_DOB_COL));

                return user;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static User getRelatoveFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        } else {
            try {
                User user = new User(
                        cursor.getString(USERS_USERNAME_COL),
                        cursor.getString(CONTACT_NAME_COL),
                        cursor.getString(USERS_PHONE_COL));

                return user;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Gets a user from username
     *
     * @param username
     * @return User
     */
    public User getUser(String username) {
        String where = USERS_USERNAME + "= ?";
        String[] whereArgs = {username};

        this.openReadableDB();
        Cursor cursor = db.query(USERS_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        User user = getUserFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return user;
    }

    /**
     * Inserts a user to the database
     *
     * @param user
     * @return rowID as an int
     */
    public long insertUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(USERS_USERNAME, user.getUsername());
        cv.put(USERS_PASSWORD, user.getPassword());
        cv.put(USERS_PHONE, user.getPhone_number());
        cv.put(USERS_IQAMA, user.getIqama());
        cv.put(USERS_DOB, user.getDOB());

        this.openWriteableDB();
        long rowID = db.insert(USERS_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public long insertContact(String name, String contact, String username) {
        ContentValues cv = new ContentValues();
        cv.put(Contact_NAME, name);
        cv.put(USERS_PHONE, contact);
        cv.put(USERS_USERNAME, username);
        this.openWriteableDB();
        long rowID = db.insert(Contacts_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public int updateuser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(USERS_USERNAME, user.getUsername());
        cv.put(USERS_PASSWORD, user.getPassword());
        cv.put(USERS_PHONE, user.getPhone_number());
        cv.put(USERS_IQAMA, user.getIqama());
        cv.put(USERS_DOB, user.getDOB());

        String where = USERS_USERNAME + "= ?";
        String[] whereArgs = {String.valueOf(user.getUsername())};

        this.openWriteableDB();
        int rowCount = db.update(USERS_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    //END USER METHODS
    //////////////////////////////////////////////////////////////////////////////////


    public ArrayList<List> getLists() {
        ArrayList<List> lists = new ArrayList<List>();
        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE,
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            List list = new List();
            list.setId(cursor.getInt(LIST_ID_COL));
            list.setName(cursor.getString(LIST_NAME_COL));

            lists.add(list);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return lists;
    }

    public List getList(String name) {
        String where = LIST_NAME + "= ?";
        String[] whereArgs = {name};

        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE, null,
                where, whereArgs, null, null, null);
        List list = null;
        cursor.moveToFirst();
        list = new List(cursor.getInt(LIST_ID_COL),
                cursor.getString(LIST_NAME_COL));
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return list;
    }

    public ArrayList<Task> getTasks(String listName) {
        String where = TASK_LIST_ID + "= ?";
        int listID = getList(listName).getId();
        String[] whereArgs = {Integer.toString(listID)};

        this.openReadableDB();
        Cursor cursor = db.query(TASK_TABLE, null,
                where, whereArgs,
                null, null, null);
        ArrayList<Task> tasks = new ArrayList<Task>();
        while (cursor.moveToNext()) {
            tasks.add(getTaskFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return tasks;
    }

    public Task getTask(int id) {
        String where = TASK_ID + "= ?";
        String[] whereArgs = {Integer.toString(id)};

        this.openReadableDB();
        Cursor cursor = db.query(TASK_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Task task = getTaskFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return task;
    }


    private static Task getTaskFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        } else {
            try {
                Task task = new Task(
                        cursor.getInt(TASK_ID_COL),
                        cursor.getInt(TASK_LIST_ID_COL),
                        cursor.getString(TASK_NAME_COL),
                        cursor.getString(TASK_NOTES_COL),
                        cursor.getString(TASK_COMPLETED_COL),
                        cursor.getString(TASK_HIDDEN_COL),
                        cursor.getDouble(TASK_FEE_COL));
                return task;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public long insertTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_LIST_ID, task.getListId());
        cv.put(TASK_NAME, task.getName());
        cv.put(TASK_NOTES, task.getNotes());
        cv.put(TASK_COMPLETED, task.getCompletedDate());
        cv.put(TASK_HIDDEN, task.getHidden());
        cv.put(TASK_FEE, task.getFee());

        this.openWriteableDB();
        long rowID = db.insert(TASK_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public int updateTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_LIST_ID, task.getListId());
        cv.put(TASK_NAME, task.getName());
        cv.put(TASK_NOTES, task.getNotes());
        cv.put(TASK_COMPLETED, task.getCompletedDate());
        cv.put(TASK_HIDDEN, task.getHidden());
        cv.put(TASK_FEE, task.getFee());

        String where = TASK_ID + "= ?";
        String[] whereArgs = {String.valueOf(task.getId())};

        this.openWriteableDB();
        int rowCount = db.update(TASK_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }


    public int deleteTask(long id) {
        String where = TASK_ID + "= ?";
        String[] whereArgs = {String.valueOf(id)};

        this.openWriteableDB();
        int rowCount = db.delete(TASK_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }
}
