package dao;

import db.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneDao {
    // 공통 SQL 실행 메서드
    private static List<String[]> executePhoneQuery(String condition, Object... params) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String[]> phoneList = new ArrayList<>();

        String sql = "SELECT p.phone_id, p.model, p.brand, p.price, p.released_at, s.name " +
                "FROM phone p " +
                "JOIN stock st ON p.phone_id = st.phone_id " +
                "JOIN seller s ON st.seller_id = s.seller_id " +
                (condition.isEmpty() ? "" : " WHERE " + condition) +
                " ORDER BY p.phone_id ASC";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String) {
                    pstmt.setString(i + 1, (String) params[i]); //String으로 넘어오면
                } else {
                    pstmt.setInt(i + 1, (int) params[i]); //int로 넘어오면
                }
            }
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

    // 모델
    public static List<String[]> searchPhonesByModel(String model) {
        return executePhoneQuery("p.model LIKE ?", "%" + model + "%");
    }

    // 판매자
    public static List<String[]> searchPhonesBySeller(String sellerName) {
        return executePhoneQuery("s.name LIKE ?", "%" + sellerName + "%");
    }

    // 가격 (최대 가격 이하로 검색)
    public static List<String[]> searchPhonesByPriceRange(int maxPrice) {
        return executePhoneQuery("p.price <= ?", maxPrice);
    }

    // 출시일
    public static List<String[]> searchPhonesByReleaseDate(String releaseDate) {
        return executePhoneQuery("p.released_at LIKE ?", releaseDate + "%");
    }

    // 브랜드 검색
    public static List<String[]> searchPhonesByBrand(String brand) {
        return executePhoneQuery("p.brand LIKE ?", "%" + brand + "%");
    }

    // 전체 휴대폰 목록 조회
    public static List<String[]> getAllPhones() {
        return executePhoneQuery("");
    }
}
