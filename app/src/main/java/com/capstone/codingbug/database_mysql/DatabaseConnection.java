package com.capstone.codingbug.database_mysql;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.capstone.codingbug.database_mysql.keyword.UserDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    Context context;
    private Connection connection = null;
    private Statement statement;
    public DatabaseConnection(Context context) {
        this.context = context;
        connection_database();

    }
    public void connection_database(){
        try {
            // JDBC 드라이버를 로드합니다.
            Class.forName("com.mysql.jdbc.Driver");
            /*try {
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            } catch (SQLException e) {
                System.out.println("Error while registering the driver: " + e.getMessage());
            }*/
            // 데이터베이스 연결 정보를 설정합니다.
            //String url = "jdbc:mysql://dbcodingbug.cerbxb4k9nuo.ap-northeast-2.rds.amazonaws.com:3306/dbcodingbug";

            String url = "jdbc:mysql://dbcodingbug.cerbxb4k9nuo.ap-northeast-2.rds.amazonaws.com:3306/dbcodingbug?serverTimezone=Asia/Seoul&useSSL=false";
            String username = "codingbug";
            String password = "capstone3!";


            // 데이터베이스에 연결합니다.
            connection = DriverManager.getConnection(url, username, password);


            // 데이터베이스에서 데이터를 조회합니다.
            statement = connection.createStatement();
            /*ResultSet resultSet = statement.executeQuery("SELECT * FROM "+UserDB.USER_TABLE_NAME);
            // 조회 결과를 봅니다.
            while (resultSet.next()) {
                Log.d("Database", resultSet.getString("user_mail"));
            }*/

        } catch (Exception e) {
            Log.e("Database", e.getMessage());
        }
        /*if(connection != null)
            return connection;
        return null;*/
    }
    public Statement get_mysql(){
        return statement;
    }
    public PreparedStatement create_pStatement(String query){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement;
        } catch (SQLException e) {
            Log.e("이스케이핑 오류",e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
