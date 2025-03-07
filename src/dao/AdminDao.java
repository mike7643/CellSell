package dao;

import db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {

    //가입

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

    // 특정 판매자가 등록한 휴대폰 목록 조회 (테이블 형태)
    public static List<String[]> getPhonesBySellerEmail(String sellerEmail) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String[]> phoneList = new ArrayList<>();

        String sql = "SELECT p.phone_id, p.model, p.brand, p.price, p.released_at " +
                "FROM phone p " +
                "JOIN stock s ON p.phone_id = s.phone_id " +
                "JOIN seller sel ON s.seller_id = sel.seller_id " +
                "WHERE sel.email = ?";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, sellerEmail);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                phoneList.add(new String[]{
                        String.valueOf(rs.getInt("phone_id")),
                        rs.getString("model"),
                        rs.getString("brand"),
                        String.valueOf(rs.getInt("price")),
                        rs.getDate("released_at").toString()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return phoneList;
    }


}
