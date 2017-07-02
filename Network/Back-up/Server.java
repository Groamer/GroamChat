/**
 * Created by Tom Remeeus on 3-5-2016.
 */

package main;

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
        startServer();
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
            System.out.println("SERVER: Setting up server...");
            serverClients = new ArrayList<>();
            server = new ServerSocket(6969, 100);
        }
        catch(IOException IOe) {
            System.out.println("SERVER: " + IOe);
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
                    while(true)
                    {
                        System.out.println("SERVER: Waiting for clients...");
                        serverClients.add(new ServerClient(server.accept()));
                        System.out.println("SERVER: Client connected");
                    }
                }
                catch(IOException IOe)
                {
                    System.out.println("SERVER: " + IOe);
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
                        Thread.sleep(10);
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
                        System.out.println("SERVER: " + e);
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
                        Thread.sleep(5000);
                        for(int i = 0; i < serverClients.size(); i ++)
                        {
                            if(!serverClients.get(i).getConnected())
                            {
                                serverClients.remove(i);
                                System.out.println("SERVER: Client disconnected");
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("SERVER: " + e);
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
                        Thread.sleep(2000);
                        
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
                        System.out.println("SERVER: " + e);
                    }
                }
            }
        }.start();
    }
}