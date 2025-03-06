package dao;

import db.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhoneDao {

    // 전체 휴대폰 목록 조회
    public static List<String[]> getAllPhones() {
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
}
