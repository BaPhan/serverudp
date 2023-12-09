package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class HomeFrame extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField addressField;

    private DefaultTableModel tableModel;
    private JTable userTable;

    private List<User> userList;

    private static final int SERVER_PORT = 6666;

    public HomeFrame() {
        super("UDP");

        idField = new JTextField(20);
        nameField = new JTextField(20);
        addressField = new JTextField(20);

        JButton addButton = new JButton("Add User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        JButton showListButton = new JButton("Show User List");
        showListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserList();
            }
        });
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Address");

        userList = new ArrayList<>();
        userTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(userTable);


        JPanel panel = new JPanel();
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(showListButton);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        getContentPane().add(panel);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void addUser() {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            // Create a User object
            User user = new User();
            user.setId(Integer.valueOf(idField.getText()));
            user.setName(nameField.getText());
            user.setAddress(addressField.getText());

            // Convert User object to byte array
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)) {
                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
            }

            byte[] sendData = byteStream.toByteArray();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = SERVER_PORT;

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);

            // Receive acknowledgment from the server
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String acknowledgment = new String(receivePacket.getData(), 0, receivePacket.getLength());
            JOptionPane.showMessageDialog(this, acknowledgment, "Server acknowledgment", JOptionPane.INFORMATION_MESSAGE);
            // Clear input fields after adding a user
            idField.setText("");
            nameField.setText("");
            addressField.setText("");

            // Refresh user list
            showUserList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String userId = (String) tableModel.getValueAt(selectedRow, 0);

            // Implement logic to send delete request to server (similar to addUser method)

            // Refresh user list
            showUserList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUserList() {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            // Implement logic to send request for user list to server

            // Receive user list from server and update tableModel
            // For now, we'll simulate a static user list
            userList.clear();
            userList.add(new User(1, "name 1", "123 Main St"));
            userList.add(new User(2, "name 2", "456 Oak St"));

            updateTableModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTableModel() {
        tableModel.setRowCount(0); // Clear existing rows

        for (User user : userList) {
            Object[] rowData = {user.getId(), user.getName(), user.getAddress()};
            tableModel.addRow(rowData);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HomeFrame().setVisible(true);
            }
        });
    }
}
/////////////////////////////////////////////////////////////////////////////////////////////////
rmi//////////////////////////
package org.example.rmi;

import org.example.User;

import java.rmi.Naming;

public class ClientRmi {
    public static void main(String[] args) {
        try {
            RemoteInterface remoteServer = (RemoteInterface) Naming.lookup("rmi://localhost/RemoteServer");

            // Create a new User
            User newUser = new User(1, "John Doe", "123 Main St");

            // Add User to the server
            remoteServer.addUser(newUser);

            // Retrieve the updated User list from the server
            System.out.println("User List on Client: " + remoteServer.getUserList());
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
///////////////////////////////////interface
package org.example.rmi;

import org.example.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteInterface extends Remote {
    void addUser(User user) throws RemoteException;
    List<User> getUserList() throws RemoteException;
}
