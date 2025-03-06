package dao;

import db.DBUtil;

import java.sql.*;

public class CustomerDao {

    // 🔹 고객 ID 조회 (이메일 기반)
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
}
