package swing;

import dao.PhoneDao;
import dao.OrdersDao;
import dao.SellerDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

class CustomerDashboard {
    public static void openUserDashboard(String email) {
        JFrame frame = new JFrame("사용자 대시보드");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(2, 1, 10, 10));

        JButton requestListButton = new JButton("휴대폰 신청 목록");
        JButton browsePhonesButton = new JButton("휴대폰 구매 하러가기");

        frame.add(requestListButton);
        frame.add(browsePhonesButton);

        frame.setVisible(true);

        requestListButton.addActionListener(e -> viewRequestedPhones(email));
        browsePhonesButton.addActionListener(e -> browsePhones(email));
    }

    private static void viewRequestedPhones(String email) {
        String phones = OrdersDao.getRequestedPhonesByEmail(email);
        JOptionPane.showMessageDialog(null, phones.isEmpty() ? "신청한 휴대폰이 없습니다." : phones, "신청 목록", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void browsePhones(String customerEmail) {
        List<String[]> phoneList = PhoneDao.getAllPhonesWithSeller();

        if (phoneList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "등록된 휴대폰이 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFrame browseFrame = new JFrame("휴대폰 목록");
        browseFrame.setSize(700, 400);
        browseFrame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "모델", "브랜드", "가격", "출시일", "판매자", "선택"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] phone : phoneList) {
            model.addRow(new Object[]{phone[0], phone[1], phone[2], phone[3], phone[4], phone[5], "선택"});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("선택").setPreferredWidth(70);

        JScrollPane scrollPane = new JScrollPane(table);
        browseFrame.add(scrollPane, BorderLayout.CENTER);

        JButton purchaseButton = new JButton("구매하기");
        browseFrame.add(purchaseButton, BorderLayout.SOUTH);

        browseFrame.setVisible(true);

        purchaseButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(browseFrame, "구매할 휴대폰을 선택하세요.", "오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int phoneId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                String selectedModel = table.getValueAt(selectedRow, 1).toString();
                String selectedBrand = table.getValueAt(selectedRow, 2).toString();
                String selectedPrice = table.getValueAt(selectedRow, 3).toString();
                String selectedSellerName = table.getValueAt(selectedRow, 5).toString();

                // 🔹 판매자 ID 조회
                int sellerId = SellerDao.getSellerIdByPhoneId(phoneId);

                int confirm = JOptionPane.showConfirmDialog(browseFrame,
                        "다음 휴대폰을 구매하시겠습니까?\n\n모델: " + selectedModel + "\n브랜드: " + selectedBrand + "\n가격: $" + selectedPrice + "\n판매자: " + selectedSellerName,
                        "구매 확인",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = OrdersDao.purchasePhone(customerEmail, phoneId, sellerId);
                    if (success) {
                        JOptionPane.showMessageDialog(browseFrame, "구매가 완료되었습니다!", "구매 완료", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(browseFrame, "구매 실패. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(browseFrame, "ID 변환 오류: " + ex.getMessage(), "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
