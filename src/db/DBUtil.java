package db;

import java.sql.*;

public class DBUtil {

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PWD);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void releaseConnection(PreparedStatement pstmt) {
        try {
            pstmt.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static void releaseConnection(PreparedStatement pstmt, Connection con) {
        try {
            pstmt.close();
            con.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void releaseConnection(ResultSet rs, PreparedStatement pstmt, Connection con) {
        try {
            rs.close();
            pstmt.close();
            con.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
}