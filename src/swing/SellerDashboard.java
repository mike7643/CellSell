package swing;

import dao.SellerDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SellerDashboard {
    public static void openSellerDashboard(int sellerId) {
        JFrame frame = new JFrame("판매자 대시보드");
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(3, 1, 10, 10));

        JButton viewMyPhonesButton = new JButton("내 휴대폰 보기");
        JButton manageOrdersButton = new JButton("주문 관리");
        JButton sellPhoneButton = new JButton("휴대폰 판매");

        frame.add(viewMyPhonesButton);
        frame.add(manageOrdersButton);
        frame.add(sellPhoneButton);

        frame.setVisible(true);

        viewMyPhonesButton.addActionListener(e -> viewMyPhonesTable(sellerId));
        manageOrdersButton.addActionListener(e -> managePendingOrders(sellerId));
        sellPhoneButton.addActionListener(e -> sellPhone(sellerId));
    }

    private static void sellPhone(int sellerId) {
        JFrame frame = new JFrame("휴대폰 판매");
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel modelLabel = new JLabel("모델:");
        JTextField modelField = new JTextField(15);
        JLabel brandLabel = new JLabel("브랜드:");
        JTextField brandField = new JTextField(15);
        JLabel priceLabel = new JLabel("가격 (만원):");
        JTextField priceField = new JTextField(15);
        JLabel specsLabel = new JLabel("스펙:");
        JTextField specsField = new JTextField(15);
        JLabel releaseDateLabel = new JLabel("출시일 (YYYY-MM-DD):");
        JTextField releaseDateField = new JTextField(15);
        JLabel quantityLabel = new JLabel("판매 수량:");
        JTextField quantityField = new JTextField(15);


        JButton addButton = new JButton("추가하기");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(modelLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(modelField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(brandLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(brandField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(priceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(priceField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(specsLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(specsField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(releaseDateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(releaseDateField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(quantityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(quantityField, gbc);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        addButton.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                String brand = brandField.getText().trim();
                String specs = specsField.getText().trim();
                int price = Integer.parseInt(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (price < 0 || quantity <= 0) {
                    JOptionPane.showMessageDialog(frame, "가격과 수량은 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate releaseDate = LocalDate.parse(releaseDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                boolean success = SellerDao.addNewPhone(model, brand, price, specs, releaseDate, sellerId, quantity);
                if (success) {
                    JOptionPane.showMessageDialog(frame, "판매 목록에 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "추가 실패. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "가격 및 수량은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "출시일 형식이 잘못되었습니다. (YYYY-MM-DD 형식으로 입력하세요)", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // 판매자가 등록한 휴대폰을 테이블로 보기
    private static void viewMyPhonesTable(int sellerId) {
        List<String[]> phoneList = SellerDao.getPhonesBySellerId(sellerId);

        JFrame frame = new JFrame("내 휴대폰 목록");
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "모델", "브랜드", "가격", "출시일"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] phone : phoneList) {
            model.addRow(new String[]{phone[0], phone[1], phone[2], phone[3] + " 만원", phone[4]});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // 주문 관리 (주문 상태 표시 + 상태 변경)
    private static void managePendingOrders(int sellerId) {
        List<String[]> orders = SellerDao.getPendingOrdersForSeller(sellerId);

        JFrame frame = new JFrame("주문 관리");
        frame.setSize(750, 400);
        frame.setLayout(new BorderLayout());

        // 테이블 컬럼 (현재 상태 포함)
        String[] columnNames = {"주문 ID", "고객 이름", "이메일", "모델", "브랜드", "가격", "현재 상태"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] order : orders) {
            model.addRow(new String[]{order[0], order[1], order[2], order[3], order[4], order[5] + " 만원", order[6]});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("상태 변경하기");
        updateButton.setPreferredSize(new Dimension(200, 50));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "주문을 선택하세요.", "오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int orderId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
            String[] options = {"승인", "거절"};
            String newStatus = (String) JOptionPane.showInputDialog(frame, "새 상태 선택:", "상태 변경",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (newStatus != null) {
                String canceledReason = null;

                if (newStatus.equals("거절")) {
                    canceledReason = JOptionPane.showInputDialog(frame, "거절 사유를 입력하세요:", "거절 사유 입력",
                            JOptionPane.QUESTION_MESSAGE);

                    if (canceledReason == null || canceledReason.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "거절 사유를 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                boolean success = SellerDao.updateOrderStatus(orderId, newStatus, canceledReason);
                if (success) {
                    table.setValueAt(newStatus, selectedRow, 6);
                    JOptionPane.showMessageDialog(frame, "주문 상태가 변경되었습니다.", "변경 완료", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

}
