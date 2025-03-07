package dao;

import db.DBUtil;

import java.sql.*;

public class OrdersDao {

    // 사용자의 휴대폰 주문 목록 custid 로 조회
    public static String getRequestedPhones(int custId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder requestedPhones = new StringBuilder();

        String sql = "SELECT o.order_id, p.phone_id, p.brand, p.model, p.price, o.order_status, o.order_date " +
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
                int orderId = rs.getInt("order_id");
                String oStatus = rs.getString("order_status");
                requestedPhones
                        .append("["+rs.getDate("order_date")+"] ")
                        .append(rs.getString("brand")).append(" ")
                        .append(rs.getString("model")).append(" / 가격: ")
                        .append(rs.getInt("price")).append("원 / (판매자 승인여부: ").append(oStatus+")\n");
                if(oStatus.equals("거절")) {
                    requestedPhones.append("[거절 사유 : " + reasonWhyCanceled(orderId) + "]\n\n");
                }
                requestedPhones.append("\n\n");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return requestedPhones.toString();
    }

    // 메소드 명 그대로 !
    public static String reasonWhyCanceled(int orderId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT canceled_reason FROM orders WHERE order_id = ? AND order_status = '거절'";
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

             if (rs.next()) {
                String reason = rs.getString("canceled_reason");
                if (reason != null && !reason.isEmpty()) {
                    return reason;
                }else return "판매자가 거절 사유를 작성하지 않았습니다. 문의 바랍니다.";
            } return "해당 주문은 취소되지 않았습니다!";
        } catch (Exception e) {
            e.printStackTrace();
            return "데이터 조회 오류 발생";
        }finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
    }



    // 휴대폰 구매 (주문 등록)
    public static boolean orderPhone(int custId, int phoneId, int sellerId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String insertOrderSql = "INSERT INTO orders (cust_id, phone_id, seller_id, order_status) VALUES (?, ?, ?, '승인대기')";

        try {
            con = DBUtil.getConnection();
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
            DBUtil.releaseConnection(pstmt, con);
        }
    }
}
