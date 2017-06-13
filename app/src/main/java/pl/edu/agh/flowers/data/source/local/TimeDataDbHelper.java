package pl.edu.agh.flowers.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TimeDataDbHelper extends SQLiteOpenHelper {
    public static abstract class TimeDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "timedata";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_TASK_ID = "taskid";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "TimeData.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String DOUBLE_TYPE = " REAL";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TimeDataEntry.TABLE_NAME + " (" +
                    TimeDataEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    TimeDataEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TimeDataEntry.COLUMN_NAME_TASK_ID + TEXT_TYPE + COMMA_SEP +
                    TimeDataEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    TimeDataEntry.COLUMN_NAME_VALUE + DOUBLE_TYPE +
            " )";

    public TimeDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<Entry> getElements(String taskId) {
        List<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = { TimeDataEntry.COLUMN_NAME_VALUE };
        String selection = TimeDataEntry.COLUMN_NAME_TASK_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        Cursor c = db.query(
                TimeDataEntry.TABLE_NAME,
                projection, selection, selectionArgs,
                null, null, null
        );

        int i = 0;
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                double value = c.getFloat(c.getColumnIndexOrThrow(TimeDataEntry.COLUMN_NAME_VALUE));
                entries.add(new Entry(i, (float) value));
                i++;
            } while (c.moveToNext());
        }

        if (c != null) { c.close(); }
        db.close();

        return entries;
    }

    public void addElement(String taskId, Long timeStamp, Double value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues timeDataValues = new ContentValues();
        timeDataValues.put(TimeDataEntry.COLUMN_NAME_ENTRY_ID, UUID.randomUUID().toString());
        timeDataValues.put(TimeDataEntry.COLUMN_NAME_TASK_ID, taskId);
        timeDataValues.put(TimeDataEntry.COLUMN_NAME_TIMESTAMP, timeStamp);
        timeDataValues.put(TimeDataEntry.COLUMN_NAME_VALUE, value);
        db.insert(TimeDataEntry.TABLE_NAME, null, timeDataValues);
        db.close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
