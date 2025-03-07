package dao;

import db.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 판매자 이름을 ID로 가져오기
// 판매자가 새로운 휴대폰을 등록하고 판매 목록에 추가
// 판매자가 등록한 휴대폰 판매 목록 조회
// 특정 휴대폰을 판매한 판매자 ID 조회
// 판매자의 주문받은 목록 조회
// 주문 상태 변경

public class SellerDao {

    public static boolean deletePhone(int phoneId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM phone WHERE phone_id = ?";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, phoneId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.releaseConnection(pstmt, con);
        }
    }

    public static boolean updatePhone(int phoneId, String model, String brand, int price, String specs, LocalDate releaseDate) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String phoneSql = "UPDATE phone SET model = ?, brand = ?, price = ?, spec = ?, released_at = ? WHERE phone_id = ?";

        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);

            pstmt = con.prepareStatement(phoneSql);
            pstmt.setString(1, model);
            pstmt.setString(2, brand);
            pstmt.setInt(3, price);
            pstmt.setString(4, specs);
            pstmt.setDate(5, Date.valueOf(releaseDate));
            pstmt.setInt(6, phoneId);
            pstmt.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.releaseConnection(pstmt, con);
        }
    }



    // 판매자 이름을 ID로 가져오기
    public static String getSellerNameById(int sellerId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sellerName = null;

        String sql = "SELECT name FROM seller WHERE seller_id = ?";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                sellerName = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }

        return sellerName;
    }

    // 판매자가 새로운 휴대폰을 등록하고 판매 목록에 추가
    public static boolean addNewPhone(String model, String brand, int price, String spec, LocalDate releasedAt, int sellerId, int quantity) {
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement stockPstmt = null;
        ResultSet rs = null;

        String phoneSql = "INSERT INTO phone (model, brand, price, spec, released_at) VALUES (?, ?, ?, ?, ?)";
        String stockSql = "INSERT INTO stock (phone_id, seller_id, quantity) VALUES (?, ?, ?)";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(phoneSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, model);
            pstmt.setString(2, brand);
            pstmt.setInt(3, price);
            pstmt.setString(4, spec);
            pstmt.setDate(5, Date.valueOf(releasedAt));
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();

            int phoneId = -1;

            if (rs.next()) {
                phoneId = rs.getInt(1);
            }
            stockPstmt = con.prepareStatement(stockSql);
            stockPstmt.setInt(1, phoneId);
            stockPstmt.setInt(2, sellerId);
            stockPstmt.setInt(3, quantity);
            int rowsAffected = stockPstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
            DBUtil.releaseConnection(stockPstmt);
        }
        return false;
    }


    // 판매자가 등록한 휴대폰 판매 목록 조회
    public static List<String[]> getPhonesBySellerId(int sellerId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String[]> salesList = new ArrayList<>();

        String sql = "SELECT p.phone_id, p.model, p.brand, p.price, p.released_at, s.quantity " +
                "FROM phone p " +
                "JOIN stock s ON p.phone_id = s.phone_id " +
                "WHERE s.seller_id = ?";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                salesList.add(new String[]{
                        String.valueOf(rs.getInt("phone_id")),
                        rs.getString("model"),
                        rs.getString("brand"),
                        String.valueOf(rs.getInt("price")),
                        rs.getDate("released_at").toString(),
                        String.valueOf(rs.getInt("quantity"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return salesList;
    }

    // 특정 휴대폰을 판매한 판매자 ID 조회
    public static int getSellerIdByPhoneId(int phoneId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int sellerId = -1;

        String sql = "SELECT seller_id FROM stock WHERE phone_id = ?";
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, phoneId);
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

    // 판매자의 주문받은 목록 조회
    public static List<String[]> getOrdersForSeller(int sellerId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String[]> orderList = new ArrayList<>();

        String sql = "SELECT o.order_id, c.name, c.email, p.model, p.brand, p.price, o.order_status " +
                "FROM orders o " +
                "JOIN phone p ON o.phone_id = p.phone_id " +
                "JOIN customer c ON o.cust_id = c.cust_id " +
                "WHERE o.seller_id = ?";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                orderList.add(new String[]{
                        String.valueOf(rs.getInt("order_id")),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("model"),
                        rs.getString("brand"),
                        String.valueOf(rs.getInt("price")),
                        rs.getString("order_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return orderList;
    }

    // 주문 상태 변경
    public static boolean updateOrderStatus(int orderId, String newStatus, String canceledReason) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String sql;
        if (newStatus.equals("거절")) {
            sql = "UPDATE orders SET order_status = ?, canceled_reason = ? WHERE order_id = ?";
        } else {
            sql = "UPDATE orders SET order_status = ?, canceled_reason = NULL WHERE order_id = ?";
        }

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newStatus);

            if (newStatus.equals("거절")) {
                pstmt.setString(2, canceledReason);
                pstmt.setInt(3, orderId);
            } else {
                pstmt.setInt(2, orderId);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.releaseConnection(pstmt, con);
        }
    }
}