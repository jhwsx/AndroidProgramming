package com.wzc.criminalintent.database;

/**
 * Created by wzc on 2017/7/26.
 *
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        // 数据库表名
        public static final String NAME = "crimes";

        public static final class Cols {
            // 字段名
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";

        }

    }
}
