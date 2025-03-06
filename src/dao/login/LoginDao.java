package dao.login;

import db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {

    public static boolean findUserByEmailAndPhone(String userType, String email, String phone) {
        String table = userType.equals("customer") ? "customer" : "seller";
        String sql = "SELECT 1 FROM " + table + " WHERE email = ? AND phone = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, phone);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
