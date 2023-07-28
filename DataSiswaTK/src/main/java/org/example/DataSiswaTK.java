package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DataSiswaTK extends JFrame{
    private JTextField idField, namaField, kelasField, ttlField, alamatField;
    private JButton addButton, updateButton, deleteButton;
    private DefaultTableModel tableModel;
    private JTable siswaTable;

    private Connection connection;

    public DataSiswaTK()
    {
        super("Data Siswa TK");

        String dbUrl = "jdbc:mysql://localhost:3306/data_siswa_tk";
        String username = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
        }catch (SQLException e){
            e.printStackTrace();
        }

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Nama");
        tableModel.addColumn("Kelas");
        tableModel.addColumn("TTL");
        tableModel.addColumn("Alamat");

        siswaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(siswaTable);

        siswaTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting())
            {
                int selectedRow = siswaTable.getSelectedRow();
                if(selectedRow != -1)
                {
                    String id = String.valueOf(siswaTable.getValueAt(selectedRow, 0));
                    String nama = (String) siswaTable.getValueAt(selectedRow, 1);
                    String kelas = (String) siswaTable.getValueAt(selectedRow, 2);
                    String ttl = (String) siswaTable.getValueAt(selectedRow, 3);
                    String alamat = (String) siswaTable.getValueAt(selectedRow, 4);

                    idField.setText(id);
                    namaField.setText(nama);
                    kelasField.setText(kelas);
                    ttlField.setText(ttl);
                    alamatField.setText(alamat);
                }
            }
        });

        idField = new JTextField(10);
        namaField = new JTextField(20);
        kelasField = new JTextField(10);
        ttlField = new JTextField(20);
        alamatField = new JTextField(20);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSiswa();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSiswa();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSiswa();
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(6,2));
        inputPanel.add(new JLabel("ID: "));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Nama: "));
        inputPanel.add(namaField);
        inputPanel.add(new JLabel("Kelas: "));
        inputPanel.add(kelasField);
        inputPanel.add(new JLabel("TTL: "));
        inputPanel.add(ttlField);
        inputPanel.add(new JLabel("Alamat: "));
        inputPanel.add(alamatField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(deleteButton, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.WEST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(470, 480);
        setLocationRelativeTo(null);
        setVisible(true);

        loadSiswa();
    }

    private void addSiswa()
    {
        String nama = namaField.getText();
        String kelas = kelasField.getText();
        String ttl = ttlField.getText();
        String alamat = alamatField.getText();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO siswa (nama, kelas, ttl, alamat) VALUES(?, ?, ?, ?)");
            statement.setString(1, nama);
            statement.setString(2, kelas);
            statement.setString(3, ttl);
            statement.setString(4, alamat);
            statement.executeUpdate();
            statement.close();
            loadSiswa();
            clearFields();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void updateSiswa()
    {
        String id = idField.getText();
        String nama = namaField.getText();
        String kelas = kelasField.getText();
        String ttl = ttlField.getText();
        String alamat = alamatField.getText();

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE siswa SET nama=?, kelas=?, ttl=?, alamat=? WHERE id=?");
            statement.setString(1, nama);
            statement.setString(2, kelas);
            statement.setString(3, ttl);
            statement.setString(4, alamat);
            statement.setInt(5, Integer.parseInt(id));
            statement.executeUpdate();
            statement.close();
            loadSiswa();
            clearFields();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void deleteSiswa()
    {
        String id = idField.getText();

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM siswa WHERE id = ?");
            statement.setInt(1, Integer.parseInt(id));
            statement.executeUpdate();
            statement.close();
            loadSiswa();
            clearFields();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void clearFields()
    {
        idField.setText("");
        namaField.setText("");
        kelasField.setText("");
        ttlField.setText("");
        alamatField.setText("");
    }

    private void loadSiswa()
    {
        clearTable();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM siswa");

            while(resultSet.next())
            {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                String kelas = resultSet.getString("kelas");
                String ttl = resultSet.getString("ttl");
                String alamat = resultSet.getString("alamat");

                tableModel.addRow(new Object[]{id, nama, kelas, ttl, alamat});
            }

            resultSet.close();
            statement.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void clearTable()
    {
        int rowCount = tableModel.getRowCount();
        for(int i = rowCount-1; i >= 0; i--)
        {
            tableModel.removeRow(i);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DataSiswaTK();
            }
        });
    }
}