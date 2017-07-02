package main;

/*
Created by 'Groamer' also known as 'Tom Remeeus'.
Published by ComputerInfor.

Feel free to edit and use my source code.
Just make sure you credit me, and I'll be fine!
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
    final private String startProtocol = "@GroamChat@";
    final private String endProtocol = "#GroamChat#";
    final private String messageProtocol = "1";
    final private String messageAllProtocol = "2";
    final private String sendAvailableClientsProtocol = "3";
    private ServerSocket server;
    private List<ServerClient> serverClients;

    public Server()
    {
        //start threads
        startServer();
        viewClientList();
        connectClients();
        readClients();
        checkConnections();
        sendAvailableClients();
    }

    //start the server
    private void startServer()
    {
        try
        {
            //print message
            System.out.println("SERVER: Setting up server...");
            
            //set server data
            serverClients = new ArrayList<>();
            server = new ServerSocket(6969, 100);
            
            //print message
            System.out.println("SERVER: Server is online.");
        }
        catch(IOException IOe) {
            //print error message
            System.out.println("SERVER STARTSERVER(): " + IOe);
        }
    }
    
    //connect to clients
    private void connectClients()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    //print message
                    System.out.println("SERVER: Waiting for clients...");
                    
                    //wait for connected clients and add them to the serverclient list
                    while(true)
                    {         
                        serverClients.add(new ServerClient(server.accept()));
                        
                        //print message
                        System.out.println("SERVER: " + serverClients.get(serverClients.size() - 1).getClientName() + " (" + serverClients.get(serverClients.size() - 1).getIP() + ") connected");
                        
                        //check amount of connected clients 
                        if(serverClients.size() > 1)
                        {
                            for(int i = 0; i < serverClients.size() - 1; i ++)
                            {
                                int check = serverClients.get(serverClients.size() - 1).compareTo(serverClients.get(i));
                                if(check > 0)
                                {
                                    System.out.println("SERVER: " + serverClients.get(serverClients.size() - 1).getClientName() + " (" + serverClients.get(serverClients.size() - 1).getIP() + ") reconnected");
                                    serverClients.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
                catch(IOException IOe)
                {
                    System.out.println("SERVER CONNECTCLIENTS(): " + IOe);
                }
            }
        }.start();
    }
    
    private void readClients()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(50);
                        for(int i = 0; i < serverClients.size(); i ++)
                        {
                            String dataString = serverClients.get(i).getDataString();
                            
                            if (dataString != null)
                            {
                                readDataString(dataString);
                                serverClients.get(i).flushDataString();
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("SERVER READCLIENTS(): " + e + "\n");
                    }
                }
            }
        }.start();
    }
    
    //reads data from client
    private void readDataString(String dataString)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                String[] dataSegments = dataString.split("\\|", -1);
                
                //check if command is currupt
                if(dataSegments.length == 6)
                {
                    if(dataSegments[0].equals(startProtocol) && dataSegments[5].equals(endProtocol))
                    {
                        int command = Integer.parseInt(dataSegments[3]);
                        switch (command)
                        {
                            case 1:
                                sendMessage(dataSegments[1], dataSegments[2], dataSegments[4]);
                                break;
                            
                            case 2:
                                sendMessageAll(dataSegments[1], dataSegments[4]);
                                break;
                                
                            case 4:
                                removeClient(dataSegments[1]);
                        }
                    }
                }
            }
        }.start();
    }
    
    //check disconnections and remove them from the list and view available clients
    private void checkConnections()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(100);
                        
                        for(int i = 0; i < serverClients.size(); i ++)
                        {
                            if(!serverClients.get(i).getConnected())
                            {
                                serverClients.remove(i);
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("SERVER CHECKCONNECTIONS(): " + e + "\n");
                    }
                }
            }
        }.start();
    }
    
    private void sendMessage(String sender, String receiver, String message)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                String data = startProtocol + "|" + sender + "|" + receiver + 
                    "|" + messageProtocol + "|" + message + "|" + endProtocol;
                
                for(int i = 0; i < serverClients.size(); i ++)
                {
                    if(serverClients.get(i).getClientName().equals(receiver))
                    {
                        serverClients.get(i).sendData(data);
                    }
                }
            }
        }.start();
    }
    
    private void sendMessageAll(String sender, String message)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                String data = startProtocol + "|" + sender + "|" + null + 
                    "|" + messageAllProtocol + "|" + message + "|" + endProtocol;
                
                for(int i = 0; i < serverClients.size(); i ++)
                {
                    if(!serverClients.get(i).getClientName().equals(sender))
                        serverClients.get(i).sendData(data);
                }
            }
        }.start();
    }
    
    //send all connected clients to all clients
    private void sendAvailableClients()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                List<String> availableClients = new ArrayList<>();
                
                while(true)
                {
                    try
                    {
                        Thread.sleep(100);
                        
                        for(int i = 0; i < serverClients.size(); i ++)
                        {
                            availableClients.add(serverClients.get(i).getClientName());
                        }
                        
                        String data = startProtocol + "|" + null + "|" + null + 
                            "|" + sendAvailableClientsProtocol + "|" + availableClients.toString() + "|" + endProtocol;
                        
                        for(int i = 0; i < serverClients.size(); i ++)
                        {
                            serverClients.get(i).sendData(data);
                        }
                        
                        availableClients.clear();
                    }
                    catch(Exception e)
                    {
                        System.out.println("SERVER SENDAVAILABLECLIENTS(): " + e);
                    }
                }
            }
        }.start();
    }
    
    private void removeClient(String client)
    {
        for(int i = 0; i < serverClients.size(); i ++)
        {
            if(serverClients.get(i).getClientName().equals(client))
            {
                serverClients.get(i).setConnectedFalse();
            }
        }
    }
    
    private void viewClientList()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(30000);
                        if(serverClients.size() > 0)
                        {
                            System.out.println("SERVER: Current connected clients");
                            
                            for(int i = 0; serverClients.size() > i; i ++)
                            {
                                System.out.println(serverClients.get(i).getClientName());
                            }
                            
                            System.out.println("\n");
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("SERVER VIEWCLIENTLIST(): " + e + "\n");
                    }
                }
            }
        }.start();
    }
}