package org.example;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try(DatagramSocket socket = new DatagramSocket(6666)) {
            byte[] receiveData = new byte[1024];

            while (true){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
                try(ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                    User user = (User) objectInputStream.readObject();
                    System.out.println("Recieved user from client: " + user);

                    // Add user to database
                    addUserToDatabase(user);

                    InetAddress clientAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();

                    // Send tt đến client
                    String acknowledgment = "User added successfully";
                    byte[] sendData = acknowledgment.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    socket.send(sendPacket);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void addUserToDatabase(User user) {
        // JDBC connection details
        String url = "jdbc:mysql://localhost:3306/udp";
        String username = "root";
        String password = "phanthanhba321";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "INSERT INTO users (id, name, address) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getAddress());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Search user by ID
    public static User searchUserById(int userId) {
        // JDBC connection details
        String url = "jdbc:mysql://localhost:3306/udp";
        String username = "root";
        String password = "phanthanhba321";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String address = resultSet.getString("address");
                        return new User(id, name, address);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found
    }

    // Delete user by ID
    public static void deleteUserById(int userId) {
        // JDBC connection details
        String url = "jdbc:mysql://localhost:3306/udp";
        String username = "root";
        String password = "phanthanhba321";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update user by ID
    public static void updateUserById(int userId, User updatedUser) {
        // JDBC connection details
        String url = "jdbc:mysql://localhost:3306/udp";
        String username = "root";
        String password = "phanthanhba321";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "UPDATE users SET name = ?, address = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, updatedUser.getName());
                preparedStatement.setString(2, updatedUser.getAddress());
                preparedStatement.setInt(3, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}