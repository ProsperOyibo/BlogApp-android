package uk.ac.wlv.blogapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import uk.ac.wlv.blogapp.database.BlogBaseHelper;
import uk.ac.wlv.blogapp.database.BlogCursorWrapper;
import uk.ac.wlv.blogapp.database.BlogDbSchema;

public class BlogPost {
    private static BlogPost sBlogPost;
    private List<Blog> mBlogs;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public File getPhotoFile(Blog blog) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, blog.getPhotoFilename());
    }

    public static BlogPost get(Context context) {
        if (sBlogPost == null) {
            sBlogPost = new BlogPost(context);
        }
        return sBlogPost;
    }
    private BlogPost(Context context) {
        mBlogs = new ArrayList<>();
        mContext = context.getApplicationContext();
        mDatabase = new BlogBaseHelper(mContext).getWritableDatabase();
    }

    public List<Blog> getBlogs() {
        List<Blog> blogs = new ArrayList<>();
        BlogCursorWrapper cursor = queryBlogs(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                blogs.add(cursor.getBlog());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return blogs;
    }

    public void addBlog(Blog blog) {
        ContentValues values = getContentValues(blog);
        mDatabase.insert(BlogDbSchema.BlogTable.NAME, null, values);
    }

    public void deleteBlog(Blog blog) {
        String uuidString = blog.getId().toString();
        mDatabase.delete(BlogDbSchema.BlogTable.NAME,
                BlogDbSchema.BlogTable.Cols.UUID + " = ?",
                new String[]{uuidString} );
    }

    public void updateBlog(Blog blog) {
        String uuidString = blog.getId().toString();
        ContentValues values = getContentValues(blog);
        mDatabase.update(BlogDbSchema.BlogTable.NAME, values,
                BlogDbSchema.BlogTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public Blog getBlog(UUID id) {
        BlogCursorWrapper cursor = queryBlogs(
                BlogDbSchema.BlogTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBlog();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Blog blog) {
        ContentValues values = new ContentValues();
        values.put(BlogDbSchema.BlogTable.Cols.UUID, blog.getId().toString());
        values.put(BlogDbSchema.BlogTable.Cols.TITLE, blog.getTitle());
        values.put(BlogDbSchema.BlogTable.Cols.DETAIL, blog.getDetails());
        values.put(BlogDbSchema.BlogTable.Cols.DATE, blog.getDate().getTime());
        values.put(BlogDbSchema.BlogTable.Cols.FINISHED, blog.isComplete() ? 1 : 0);
        return values;
    }

    private BlogCursorWrapper queryBlogs(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BlogDbSchema.BlogTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new BlogCursorWrapper(cursor);
    }
    
}
