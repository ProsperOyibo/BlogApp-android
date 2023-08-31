package uk.ac.wlv.blogapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlogBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "blogBase.db";
    public BlogBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BlogDbSchema.BlogTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                BlogDbSchema.BlogTable.Cols.UUID + ", " +
                BlogDbSchema.BlogTable.Cols.TITLE + ", " +
                BlogDbSchema.BlogTable.Cols.DETAIL + ", " +
                BlogDbSchema.BlogTable.Cols.DATE + ", " +
                BlogDbSchema.BlogTable.Cols.FINISHED +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
