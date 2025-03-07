package dao;

import db.DBUtil;

import java.sql.*;

public class CustomerDao {

    // 고객 이름을 ID로 가져오기
    public static String getCustomerNameById(int custId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String customerName = null;

        String sql = "SELECT name FROM customer WHERE cust_id = ?";

        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, custId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                customerName = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.releaseConnection(rs, pstmt, con);
        }

        return customerName;
    }
}
