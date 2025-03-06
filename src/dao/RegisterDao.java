package dao;

import db.DBUtil;

import java.sql.*;

public class RegisterDao {

    // 🔹 사용자 등록 (가입 후 생성된 ID 반환)
    public static int rgCustomer(String name, String email, String phone) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        String sql = "INSERT INTO customer (name, email, phone) VALUES (?, ?, ?)";
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("사용자 등록 중 오류 발생: " + e.getMessage());
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }

        return generatedId; // 생성된 ID 반환 (-1이면 실패)
    }

    // 🔹 판매자 등록
    public static int rgSeller(String name, String email, String phone) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        String sql = "INSERT INTO seller (name, email, phone) VALUES (?, ?, ?)";
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("판매자 등록 중 오류 발생: " + e.getMessage());
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }

        return generatedId;
    }
}
