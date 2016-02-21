package com.cs262.rmi.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInt extends Remote {
	void update(String name, String s) throws RemoteException;
	String getAccountName() throws RemoteException;
}
