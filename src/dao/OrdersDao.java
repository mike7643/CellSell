package dao;

import db.DBUtil;

import java.sql.*;

public class OrdersDao {

    // 🔹 사용자의 주문 목록 조회
    public static String getRequestedPhonesByEmail(String customerEmail) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder requestedPhones = new StringBuilder();

        String sql = "SELECT p.phone_id, p.brand, p.model, p.price, o.order_status " +
                "FROM orders o " +
                "JOIN phone p ON o.phone_id = p.phone_id " +
                "JOIN customer c ON o.cust_id = c.cust_id " +
                "WHERE c.email = ? " +
                "ORDER BY o.order_date DESC";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, customerEmail);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                requestedPhones.append(rs.getInt("phone_id")).append(". ")
                        .append(rs.getString("brand")).append(" ")
                        .append(rs.getString("model")).append(" - $")
                        .append(rs.getInt("price")).append(" (상태: ")
                        .append(rs.getString("order_status")).append(")\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return requestedPhones.toString();
    }

    // 🔹 휴대폰 구매 (주문 등록)
    public static boolean purchasePhone(String customerEmail, int phoneId, int sellerId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String findCustomerSql = "SELECT cust_id FROM customer WHERE email = ?";
        String insertOrderSql = "INSERT INTO orders (cust_id, phone_id, seller_id, order_status) VALUES (?, ?, ?, 'pending')";

        try {
            con = DBUtil.getConnection();

            // 🔹 고객 ID 가져오기
            pstmt = con.prepareStatement(findCustomerSql);
            pstmt.setString(1, customerEmail);
            rs = pstmt.executeQuery();

            int custId = -1;
            if (rs.next()) {
                custId = rs.getInt("cust_id");
            }
            rs.close();
            pstmt.close();

            if (custId == -1) return false;

            // 🔹 주문 등록
            pstmt = con.prepareStatement(insertOrderSql);
            pstmt.setInt(1, custId);
            pstmt.setInt(2, phoneId);
            pstmt.setInt(3, sellerId);
            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
    }
}
