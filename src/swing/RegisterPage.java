package swing;

import dao.RegisterDao;
import dao.LoginDao;

import javax.swing.*;
import java.awt.*;

public class RegisterPage {
    public RegisterPage() {
        JFrame frame = new JFrame("휴대폰 판매 관리 시스템");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1, 10, 10));

        JButton userButton = new JButton("사용자로 입장");
        JButton sellerButton = new JButton("판매자로 입장");
        JButton registerButton = new JButton("가입");

        frame.add(userButton);
        frame.add(sellerButton);
        frame.add(registerButton);

        frame.setVisible(true);

        userButton.addActionListener(e -> openLoginWindow("customer"));
        sellerButton.addActionListener(e -> openLoginWindow("seller"));
        registerButton.addActionListener(this::openRegisterWindow);
    }

    private void openLoginWindow(String userType) {
        JFrame loginFrame = new JFrame(userType.equals("customer") ? "사용자 로그인" : "판매자 로그인");
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel emailLabel = new JLabel("이메일:");
        JTextField emailField = new JTextField();
        JLabel phoneLabel = new JLabel("전화번호:");
        JTextField phoneField = new JTextField();
        JButton loginButton = new JButton("로그인");

        loginFrame.add(emailLabel);
        loginFrame.add(emailField);
        loginFrame.add(phoneLabel);
        loginFrame.add(phoneField);
        loginFrame.add(loginButton);

        loginFrame.setVisible(true);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "이메일과 전화번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean exists = LoginDao.findUser(userType, email, phone);
            if (!exists) {
                JOptionPane.showMessageDialog(loginFrame, "계정이 없습니다. 가입하세요.", "로그인 실패", JOptionPane.WARNING_MESSAGE);
//                openRegisterWindow(null); // 회원가입 창 자동 열기
            } else {
                JOptionPane.showMessageDialog(loginFrame, "로그인 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose();
                if (userType.equals("customer")) {
                    int custId = LoginDao.getCustomerIdByEmail(email);
                    CustomerDashboard.openUserDashboard(custId);
                } else {
                    int sellerId = LoginDao.getSellerIdByEmail(email);
                    SellerDashboard.openSellerDashboard(sellerId);
                }
            }
        });
    }

    private void openRegisterWindow(java.awt.event.ActionEvent e) {
        JFrame registerFrame = new JFrame("회원가입");
        registerFrame.setSize(350, 250);
        registerFrame.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel nameLabel = new JLabel("이름:");
        JTextField nameField = new JTextField();
        JLabel emailLabel = new JLabel("이메일:");
        JTextField emailField = new JTextField();
        JLabel phoneLabel = new JLabel("전화번호:");
        JTextField phoneField = new JTextField();
        JLabel roleLabel = new JLabel("역할 선택:");
        String[] roles = {"사용자", "판매자"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        JButton registerButton = new JButton("가입하기");

        registerFrame.add(nameLabel);
        registerFrame.add(nameField);
        registerFrame.add(emailLabel);
        registerFrame.add(emailField);
        registerFrame.add(phoneLabel);
        registerFrame.add(phoneField);
        registerFrame.add(roleLabel);
        registerFrame.add(roleComboBox);
        registerFrame.add(new JLabel()); // 빈 공간 추가
        registerFrame.add(registerButton);

        registerFrame.setVisible(true);

        registerButton.addActionListener(ev -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int custId = -1;
            int sellerId = -1;
            if (role.equals("사용자")) {
                custId = RegisterDao.rgCustomer(name, email, phone);
            } else {
                sellerId = RegisterDao.rgSeller(name, email, phone);
            }

            if (custId != -1 || sellerId !=-1) {
                JOptionPane.showMessageDialog(registerFrame, "회원가입 성공!", "가입 완료", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();

                // 자동 로그인 후 대시보드로 이동
                if (role.equals("사용자")) {
                    CustomerDashboard.openUserDashboard(custId);
                } else {
                    SellerDashboard.openSellerDashboard(sellerId);
                }
            } else {
                JOptionPane.showMessageDialog(registerFrame, "이미 가입된 이메일입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}