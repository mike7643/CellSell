package dao;

import db.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhoneDao {

    // 🔹 휴대폰 등록 (판매자 정보 포함)
    public static void rgPhone(String model, String brand, int price, String spec, LocalDate releasedAt, String sellerEmail) {
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

            if (phoneId != -1) {
                int sellerId = SellerDao.getSellerIdByEmail(sellerEmail);
                if (sellerId != -1) {
                    stockPstmt = con.prepareStatement(stockSql);
                    stockPstmt.setInt(1, phoneId);
                    stockPstmt.setInt(2, sellerId);
                    stockPstmt.setInt(3, 1);
                    stockPstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
            DBUtil.releaseConnection(stockPstmt);
        }
    }

    // 🔹 판매자 포함 전체 휴대폰 목록 조회
    public static List<String[]> getAllPhonesWithSeller() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String[]> phoneList = new ArrayList<>();

        String sql = "SELECT p.phone_id, p.model, p.brand, p.price, p.released_at, s.name " +
                "FROM phone p " +
                "JOIN stock st ON p.phone_id = st.phone_id " +
                "JOIN seller s ON st.seller_id = s.seller_id " +
                "ORDER BY p.phone_id ASC";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                phoneList.add(new String[]{
                        String.valueOf(rs.getInt("phone_id")),
                        rs.getString("model"),
                        rs.getString("brand"),
                        String.valueOf(rs.getInt("price")),
                        rs.getDate("released_at").toString(),
                        rs.getString("name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return phoneList;
    }

    // 🔹 판매자가 등록한 휴대폰 조회
    public static List<String> getPhonesBySellerEmail(String sellerEmail) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> phoneList = new ArrayList<>();

        String sql = "SELECT p.phone_id, p.brand, p.model, p.price, p.released_at " +
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
                phoneList.add(rs.getInt("phone_id") + ". " + rs.getString("brand") + " " +
                        rs.getString("model") + " - $" + rs.getInt("price") +
                        " (출시일: " + rs.getDate("released_at") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }
        return phoneList;
    }
}
