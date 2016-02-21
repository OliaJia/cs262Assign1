package com.cs262.rmi.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

import com.cs262.rmi.server.ChatServerInt;

public class ChatClient extends UnicastRemoteObject implements ChatClientInt, Runnable {
   private static final long serialVersionUID = 1L;
   //List of fields for client
   private ChatServerInt server;
   private String accountName;
      
   // TODO: List of command, create enum for these
   private static final String LISTACCOUNT = ":listaccount";
   private static final String LISTGROUP = ":listgroup";
   private static final String TOACCOUNT = ":toaccount";
   private static final String TOGROUP = ":togroup";
   private static final String GROUP = ":group";
   private static final String SIGNOUT = ":signout";
   private static final String DELETE = ":delete"; 
   
   public ChatClient(ChatServerInt cs, String name) throws RemoteException {
       this.accountName = name;       
       this.server = cs;
       server.createAccount(name, this);
       List<String> offlineMessages = server.fetchUndeliveredMessages(name);
       if (offlineMessages != null){
    	   for (String m:offlineMessages){
        	   System.out.println(m);
           }
       }       
   }

   public synchronized void update(String name, String s) throws RemoteException {
       if (! this.accountName.equals(name)) {
           System.out.println(name + ": " + s);
       }
   }

   public void run() {
       Scanner in=new Scanner(System.in);
       String msg;

       while(true) {
           try {
               msg=in.nextLine();
               String[] msgs = msg.trim().split(" ");
               if (DELETE.equals(msgs[0])) {
                   server.deleteAccount(this);
                   in.close();
                   System.exit(0);
               } else if (LISTACCOUNT.equals(msgs[0])){
                   server.listAccount(this);
               } else if (LISTGROUP.equals(msgs[0])){
            	   server.listGroup(this);
               } else if (TOACCOUNT.equals(msgs[0])){
            	   server.sendMessageToAccount(this, msgs[1], msgs[2]);
               } else if (TOGROUP.equals(msgs[0])){
            	   server.sendMessageToGroup(this, msgs[1], msgs[2]);
               } else if (GROUP.equals(msgs[0])){
            	   server.createGroup(this, msgs[1], msgs[2]);
               } else if (SIGNOUT.equals(msgs[0])){
            	   server.logOff(this);
            	   in.close();
                   System.exit(0);
               } else {
            	   System.out.println("Command not found");
               }               
           } catch(Exception e) {
               e.printStackTrace();
           }
       }
   }

   public static void main(String[] args) {
       if (3 != args.length) {
           System.out.println("Usage: java ChatClient <server_ip> <server_port> <user_name>");
           System.out.println("Example: java ChatClient 127.0.0.1 2001 user1");
           return;
       }
       String host = args[0];
       int port = Integer.parseInt(args[1]);
       String name = args[2];

       try {
           Registry registry = LocateRegistry.getRegistry(host, port);
           ChatServerInt server = (ChatServerInt) registry.lookup("ChatServer");
           Thread t = new Thread(new ChatClient(server, name));
           t.start();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   public String getAccountName() {
       return accountName;
   }
  
   public void setAccountName(String accountName) {
	   this.accountName = accountName;
   }
}
