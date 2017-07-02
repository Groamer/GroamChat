/**
 * Created by Tom Remeeus on 3-5-2016.
 */

package nl.computerinfor.groamchat.clients;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends Protocol
{
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    private List<String> messages;
    private List<String> unrelatedMessages;
    final private String clientName;
    final private String otherClientName;

    public ChatClient(String thisUserName, String otherUserName)
    {
        this.clientName = thisUserName;
        this.otherClientName = otherUserName;
        messages = new ArrayList<>();
        unrelatedMessages = new ArrayList<>();

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
            messages.add(clientName + " (You): " + message);
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

    private void receiveMessage(String sender, String receiver, String message)
    {
        if(sender.equals(otherClientName))
        {
            messages.add(sender + ": " + message);
        }

        if(!sender.equals(otherClientName))
        {
            unrelatedMessages.add(sender + ": " + message);
        }
    }

    public List getMessages()
    {
        return messages;
    }

    public List getUnrelatedMessages()
    {
        return unrelatedMessages;
    }

    public void setMessages(String inputMessages)
    {
        String messagesString = inputMessages;
        messagesString = messagesString.replace("[", "");
        messagesString = messagesString.replace("]", "");

        String[] messageArray = messagesString.split(", ");
        for(int i = 0; i < messageArray.length; i ++)
        {
            messages.add(messageArray[i]);
        }
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