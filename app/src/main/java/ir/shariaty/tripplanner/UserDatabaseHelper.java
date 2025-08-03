package ir.shariaty.tripplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TripPlanner.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";

    public static final String TABLE_TRIPS = "trips";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_DEPARTURE_DATE = "departure_date";
    public static final String COLUMN_RETURN_DATE = "return_date";
    public static final String COLUMN_COMPANIONS = "companions";
    public static final String COLUMN_TRIP_TYPE = "trip_type";
    public static final String COLUMN_BUDGET = "budget";
    public static final String COLUMN_PACKING_LIST = "packing_list";
    public static final String COLUMN_REMINDER = "reminder";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT NOT NULL," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        String createTripsTable = "CREATE TABLE " + TABLE_TRIPS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESTINATION + " TEXT, " +
                COLUMN_DEPARTURE_DATE + " TEXT, " +
                COLUMN_RETURN_DATE + " TEXT, " +
                COLUMN_COMPANIONS + " TEXT, " +
                COLUMN_TRIP_TYPE + " TEXT, " +
                COLUMN_BUDGET + " TEXT, " +
                COLUMN_PACKING_LIST + " TEXT, " +
                COLUMN_REMINDER + " INTEGER)";
        db.execSQL(createTripsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        onCreate(db);
    }

    public boolean insertUser(String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("email", email);
        values.put("username", username);
        values.put("password", password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean insertTrip(String destination, String departureDate, String returnDate,
                              String companions, String tripType, String budget,
                              String packingList, boolean reminderChecked) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DESTINATION, destination);
        values.put(COLUMN_DEPARTURE_DATE, departureDate);
        values.put(COLUMN_RETURN_DATE, returnDate);
        values.put(COLUMN_COMPANIONS, companions);
        values.put(COLUMN_TRIP_TYPE, tripType);
        values.put(COLUMN_BUDGET, budget);
        values.put(COLUMN_PACKING_LIST, packingList);
        values.put(COLUMN_REMINDER, reminderChecked ? 1 : 0);

        long result = db.insert(TABLE_TRIPS, null, values);
        db.close();

        return result != -1;
    }

    public Cursor getAllTrips() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRIPS, null);
    }
}

