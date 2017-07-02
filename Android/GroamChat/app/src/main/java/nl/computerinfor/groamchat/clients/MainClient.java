/**
 * Created by Tom Remeeus on 3-5-2016.
 */

package nl.computerinfor.groamchat.clients;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainClient extends Protocol
{
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    private List<String> messages;
    private List<String> clients;
    final private String clientName;

    public MainClient(String name)
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
            connection = new Socket(ipAddress(), portAddress());
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
        String data = startProtocol() + "|" + clientName + "|" + null +
                "|" + sendClientDataProtocol() + "|" + null + "|" + endProtocol();

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
        String data = startProtocol() + "|" + clientName + "|" + receiver +
                "|" + messageProtocol() + "|" + message + "|" + endProtocol();

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
        String data = startProtocol() + "|" + clientName + "|" + null +
                "|" + messageAllProtocol() + "|" + message + "|" + endProtocol();

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
                            if(dataSegments[0].equals(startProtocol()) && dataSegments[5].equals(endProtocol()))
                            {
                                int command = Integer.parseInt(dataSegments[3]);
                                switch (command)
                                {
                                    case 1:
                                        receiveMessage(dataSegments[1], dataSegments[2], dataSegments[4]);
                                        break;

                                    case 2:
                                        System.out.println("ALLMessage from client");
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
                        break;
                    }
                }
            }
        }.start();
    }

    private void receiveMessage(String sender, String receiver, String message)
    {
        if(!sender.equals(clientName) && receiver.equals(clientName))
        {
            messages.add(sender + ": " + message);
        }
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
        List<String> connectedClients = new ArrayList<>();

        for(int i = 0; i < clients.size(); i ++)
        {
            if(!clients.get(i).equals(clientName))
            {
                connectedClients.add(clients.get(i));
            }
        }
        return connectedClients;
    }

    public void closeConnection()
    {
        String data = startProtocol() + "|" + clientName + "|" + null +
                "|" + removeClientFromServerProtocol() + "|" + null + "|" + endProtocol();

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