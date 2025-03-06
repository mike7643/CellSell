package dao;

import db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {

    public static boolean findUser(String userType, String email, String phone) {

        Connection con;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        String table = userType.equals("customer") ? "customer" : "seller";
        String sql = "SELECT 1 FROM " + table + " WHERE email = ? AND phone = ?";

        con = DBUtil.getConnection();
        try {
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            rs = pstmt.executeQuery();

            if(rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs,pstmt,con);
        }
        return false;
    }
    // 고객 ID 조회 (이메일 기반)
    public static int getCustomerIdByEmail(String email) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int custId = -1;

        String sql = "SELECT cust_id FROM customer WHERE email = ?";
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                custId = rs.getInt("cust_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return custId;
    }

    // 판매자 ID 조회 (이메일 기반)
    public static int getSellerIdByEmail(String email) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int sellerId = -1;

        String sql = "SELECT seller_id FROM seller WHERE email = ?";
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                sellerId = rs.getInt("seller_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return sellerId;
    }
}
