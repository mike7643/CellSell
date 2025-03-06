package dao;

import db.DBUtil;

import java.sql.*;

public class OrdersDao {

    // 사용자의 휴대폰 주문 목록 custid로 조회
    public static String getRequestedPhones(int custId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder requestedPhones = new StringBuilder();

        String sql = "SELECT p.phone_id, p.brand, p.model, p.price, o.order_status " +
                "FROM orders o " +
                "JOIN phone p ON o.phone_id = p.phone_id " +
                "JOIN customer c ON o.cust_id = c.cust_id " +
                "WHERE c.cust_id = ? ";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, custId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                requestedPhones
                        .append(rs.getString("brand")).append(" ")
                        .append(rs.getString("model")).append(" - ")
                        .append(rs.getInt("price")).append("원 / (판매자 확인: ")
                        .append(rs.getString("order_status")).append(")\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return requestedPhones.toString();
    }

    // 휴대폰 구매 (주문 등록)
    public static boolean orderPhone(int custId, int phoneId, int sellerId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String insertOrderSql = "INSERT INTO orders (cust_id, phone_id, seller_id, order_status) VALUES (?, ?, ?, 'pending')";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(insertOrderSql);
            pstmt.setInt(1, custId);  //직접 전달된 고객 ID 사용
            pstmt.setInt(2, phoneId);
            pstmt.setInt(3, sellerId);
            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.releaseConnection(pstmt, con);
        }
    }
}
