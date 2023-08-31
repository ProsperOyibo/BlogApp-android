package uk.ac.wlv.blogapp.database;

public class BlogDbSchema {
    public static final class BlogTable {
        public static final String NAME = "Blogs";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DETAIL = "detail";
            public static final String DATE = "date";
            public static final String FINISHED = "finished";
        }
    }
}
