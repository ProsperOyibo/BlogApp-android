package uk.ac.wlv.blogapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import uk.ac.wlv.blogapp.Blog;

public class BlogCursorWrapper extends CursorWrapper {
    public BlogCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Blog getBlog() {
        String uuidString = getString(getColumnIndex(BlogDbSchema.BlogTable.Cols.UUID));
        String title = getString(getColumnIndex((BlogDbSchema.BlogTable.Cols.TITLE)));
        String detail = getString(getColumnIndex(BlogDbSchema.BlogTable.Cols.DETAIL));
        long date = getLong(getColumnIndex(BlogDbSchema.BlogTable.Cols.DATE));
        int isFinished = getInt(getColumnIndex(BlogDbSchema.BlogTable.Cols.FINISHED));
        Blog blog = new Blog(UUID.fromString(uuidString));
        blog.setTitle(title);
        blog.setDetails(detail);
        blog.setDate(new Date(date));
        blog.setComplete(isFinished != 0);
        return blog;
    }
}
