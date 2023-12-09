package org.example.rmi;

import org.example.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteInterface extends Remote {
    void addUser(User user) throws RemoteException;
    List<User> getUserList() throws RemoteException;
}
