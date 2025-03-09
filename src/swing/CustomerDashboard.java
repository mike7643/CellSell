package swing;

import dao.CustomerDao;
import dao.PhoneDao;
import dao.OrdersDao;
import dao.SellerDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerDashboard {
    private static JTable table;
    private static DefaultTableModel model;

    public static void openUserDashboard(int custId) {
        String customerName = CustomerDao.getCustomerNameById(custId);
        String greetingMessage = customerName + " 고객님, 반갑습니다!";

        JFrame frame = new JFrame("사용자 대시보드");
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel greetingLabel = new JLabel(greetingMessage, JLabel.CENTER);
        greetingLabel.setFont(new Font("Dialog", Font.PLAIN, 20));// 폰트 적용함. 글자 크기 작아서

        greetingLabel.setHorizontalAlignment(JLabel.CENTER);
        greetingLabel.setVerticalAlignment(JLabel.CENTER);
        frame.add(greetingLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton requestListButton = new JButton("휴대폰 신청 목록");
        JButton browsePhonesButton = new JButton("휴대폰 구매 하러가기");

        buttonPanel.add(requestListButton);
        buttonPanel.add(browsePhonesButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        requestListButton.addActionListener(e -> viewRequestedPhones(custId));
        browsePhonesButton.addActionListener(e -> viewAllPhones(custId));
    }



    private static void viewRequestedPhones(int custId) {
        String phones = OrdersDao.getRequestedPhones(custId);
        JOptionPane.showMessageDialog(null, phones.isEmpty() ? "신청한 휴대폰이 없습니다." : phones, "신청 목록", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void viewAllPhones(int custId) {
        JFrame browseFrame = new JFrame("휴대폰 목록");
        browseFrame.setSize(900, 600);
        browseFrame.setLayout(new BorderLayout());

        String[] columnNames = {"모델", "브랜드", "가격", "출시일", "판매자"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        browseFrame.add(scrollPane, BorderLayout.CENTER);

        //검색 패널 세로 정렬
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField modelField = new JTextField(10);
        JButton modelSearchButton = new JButton("검색");
        searchPanel.add(new JLabel("모델:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(modelField, gbc);
        gbc.gridx = 2;
        searchPanel.add(modelSearchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JTextField brandField = new JTextField(10);
        JButton brandSearchButton = new JButton("검색");
        searchPanel.add(new JLabel("브랜드:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(brandField, gbc);
        gbc.gridx = 2;
        searchPanel.add(brandSearchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JTextField sellerField = new JTextField(10);
        JButton sellerSearchButton = new JButton("검색");
        searchPanel.add(new JLabel("판매자:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(sellerField, gbc);
        gbc.gridx = 2;
        searchPanel.add(sellerSearchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JTextField releaseDateField = new JTextField(10);
        JButton releaseDateSearchButton = new JButton("검색");
        searchPanel.add(new JLabel("출시일:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(releaseDateField, gbc);
        gbc.gridx = 2;
        searchPanel.add(releaseDateSearchButton, gbc);

        // 가격 검색
        gbc.gridx = 0;
        gbc.gridy++;
        searchPanel.add(new JLabel("가격 (~이하):"), gbc);

        gbc.gridx = 1;
        JTextField maxPriceField = new JTextField(10);
        searchPanel.add(maxPriceField, gbc);

        gbc.gridx = 2;
        JButton priceSearchButton = new JButton("검색");
        searchPanel.add(priceSearchButton, gbc);

        // 전체 목록 보기 버튼
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        JButton allPhonesButton = new JButton("전체 목록 보기");
        searchPanel.add(allPhonesButton, gbc);

        browseFrame.add(searchPanel, BorderLayout.WEST);


        modelSearchButton.addActionListener(e -> updatePhoneTable(PhoneDao.searchPhonesByModel(modelField.getText().trim())));
        brandSearchButton.addActionListener(e -> updatePhoneTable(PhoneDao.searchPhonesByBrand(brandField.getText().trim())));
        sellerSearchButton.addActionListener(e -> updatePhoneTable(PhoneDao.searchPhonesBySeller(sellerField.getText().trim())));
        releaseDateSearchButton.addActionListener(e -> updatePhoneTable(PhoneDao.searchPhonesByReleaseDate(releaseDateField.getText().trim())));
        priceSearchButton.addActionListener(e -> {
            try {
                int max = Integer.parseInt(maxPriceField.getText().trim());
                updatePhoneTable(PhoneDao.searchPhonesByPriceRange(max));  // 0부터 max 가격 이하로 검색
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(browseFrame, "가격은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            }
        });

        allPhonesButton.addActionListener(e -> updatePhoneTable(PhoneDao.getAllPhones()));

        // 주문 신청 버튼 추가
        JButton purchaseButton = new JButton("주문 신청하기");
        purchaseButton.setPreferredSize(new Dimension(200, 50));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(purchaseButton);
        browseFrame.add(buttonPanel, BorderLayout.SOUTH);

        purchaseButton.addActionListener(e -> handlePurchase(custId, browseFrame));

        updatePhoneTable(PhoneDao.getAllPhones());

        browseFrame.setVisible(true);
    }




    private static void updatePhoneTable(List<String[]> phoneList) {
        model.setRowCount(0);
        for (String[] phone : phoneList) {
            model.addRow(new Object[]{phone[1], phone[2], phone[3] + " 만원", phone[4], phone[5]});
        }
    }

    private static void handlePurchase(int custId, JFrame browseFrame) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(browseFrame, "구매할 휴대폰을 선택하세요.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int phoneId = Integer.parseInt(PhoneDao.getAllPhones().get(selectedRow)[0]);
            String selectedModel = table.getValueAt(selectedRow, 0).toString();
            String selectedBrand = table.getValueAt(selectedRow, 1).toString();
            String selectedPrice = table.getValueAt(selectedRow, 2).toString();
            String selectedSellerName = table.getValueAt(selectedRow, 4).toString();

            int sellerId = SellerDao.getSellerIdByPhoneId(phoneId);

            int confirm = JOptionPane.showConfirmDialog(browseFrame,
                    "다음 휴대폰을 신청하시겠습니까?\n\n모델: " + selectedModel + "\n브랜드: " + selectedBrand + "\n가격: " + selectedPrice +
                            "\n판매자: " + selectedSellerName,
                    "신청 최종 확인",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = OrdersDao.orderPhone(custId, phoneId, sellerId);
                JOptionPane.showMessageDialog(browseFrame, success ? "신청이 완료되었습니다!" : "신청 실패. 다시 시도하세요.", success ? "신청 완료" : "오류", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(browseFrame, "ID 변환 오류: " + ex.getMessage(), "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
