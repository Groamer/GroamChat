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

public class Client
{
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    private List<String> messages;
    private List<String> clients;
    final private String clientName;
    final private String startProtocol = "@GroamChat@";
    final private String endProtocol = "#GroamChat#";
    final private String sendClientDataProtocol = "0";
    final private String messageProtocol = "1";
    final private String messageAllProtocol = "2";
    final private String removeClientFromServer = "4";


    public Client(String name)
    {
        this.clientName = name;
        messages = new ArrayList<>();
        clients = new ArrayList<>();

        connect();
        sendUserData();
        receiveData();
    }

    //connect client to server
    private void connect()
    {
        try
        {
            //connection = new Socket("37.97.180.203", 6969);
            connection = new Socket("127.0.0.1", 6969); //TEST VALUE
            output = new DataOutputStream(connection.getOutputStream());
        }
        catch(IOException IOe)
        {
            System.out.println(clientName + ": " + IOe);
        }
    }

    //send userdata to server
    private void sendUserData()
    {
        String data = startProtocol + "|" + clientName + "|" + null +
                "|" + sendClientDataProtocol + "|" + null + "|" + endProtocol;

        try
        {
            output.writeUTF(data);
            output.flush();
        }
        catch(Exception e)
        {
            System.out.println(clientName + ": " + e);
        }
    }

    public void sendMessage(String message, String receiver)
    {
        String data = startProtocol + "|" + clientName + "|" + receiver +
                "|" + messageProtocol + "|" + message + "|" + endProtocol;

        try
        {
            output.writeUTF(data);
            output.flush();
        }
        catch(Exception e)
        {
            System.out.println(clientName + ": " + e);
        }
    }

    public void sendMessageAll(String message)
    {
        String data = startProtocol + "|" + clientName + "|" + null +
                "|" + messageAllProtocol + "|" + message + "|" + endProtocol;

        try
        {
            output.writeUTF(data);
            output.flush();
        }
        catch(Exception e)
        {
            System.out.println(clientName + ": " + e);
        }
    }

    private void receiveData()
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
                        input = new DataInputStream(connection.getInputStream());
                        String dataString = input.readUTF();

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
                                        receiveMessage(dataSegments[1], dataSegments[4]);
                                        break;

                                    case 2:
                                        receiveAllMessage(dataSegments[1], dataSegments[4]);
                                        break;

                                    case 3:
                                        receiveClients(dataSegments[4]);
                                        break;
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println(clientName + ": " + e);
                    }
                }
            }
        }.start();
    }

    private void receiveMessage(String sender, String message)
    {
        System.out.println(sender + ": " + message);
        messages.add(sender + ": " + message);
    }
    
    private void receiveAllMessage(String sender, String message)
    {
        System.out.println("(PUBLIC MESSAGE) " + sender + ": " + message);
        messages.add("(PUBLIC MESSAGE) " + sender + ": " + message);
    }
    
    private void receivePicture(String sender, String picture)
    {
        
    }

    private void receiveClients(String data)
    {
        String tempclients = data;
        tempclients = tempclients.replace("[", "");
        tempclients = tempclients.replace("]", "");
        String[] clientArray = tempclients.split("\\, ", -1);

        clients.clear();
        for(int i = 0; i < clientArray.length; i ++)
        {
            clients.add(clientArray[i]);
        }
    }

    public List getMessages()
    {
        return messages;
    }
    
    public List getClients()
    {
        return clients;
    }
    
    public void closeConnection()
    {
        String data = startProtocol + "|" + clientName + "|" + null +
                "|" + removeClientFromServer + "|" + null + "|" + endProtocol;

        try
        {
            output.writeUTF(data);
            output.flush();
        }
        catch(Exception e)
        {
            System.out.println(clientName + ": " + e);
        }
    }
}