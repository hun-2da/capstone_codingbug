package com.capstone.codingbug.database_mysql.keyword;

/**쿼리 오류 방지를 위한 클래스*/
public class UserDB {
    public static final String DATABASE_NAME = "dbcodingbug";
    public static final String USER_TABLE_NAME = "user_log";
    public static final String USER_ID = "user_id";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_NAME = "user_name";
    public static final String USER_MAIL = "user_mail";
    public static final String USER_PHONE = "user_phone";
    public static final String[] USER_COLUMNS = {USER_ID,USER_PASSWORD,USER_NAME,USER_MAIL,USER_PHONE};

}
