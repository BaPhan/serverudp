package org.example.rmi;

import org.example.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements RemoteInterface {
    private List<User> userList;

    public Server() throws RemoteException {
        super();
        userList = new ArrayList<>();
    }

    @Override
    public void addUser(User user) throws RemoteException {
        userList.add(user);
        System.out.println("User added on server: " + user);
    }

    public List<User> getUserList() throws RemoteException {
        return userList;
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry ready.");
            Server server = new Server();
            java.rmi.Naming.rebind("rmi://localhost/RemoteServer", server);
            System.out.println("Server ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
