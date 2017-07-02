package main;

/*
Created by 'Groamer' also known as 'Tom Remeeus'.
Published by ComputerInfor.

Feel free to edit and use my source code.
Just make sure you credit me, and I'll be fine!
*/

import java.io.*;
import java.net.*;

public class ServerClient implements Comparable
{
    final private String startProtocol = "@GroamChat@";
    final private String endProtocol = "#GroamChat#";
    final private Socket connection;
    private String clientName;
    private DataInputStream input;
    private DataOutputStream output;
    private String dataString;
    private boolean connected;
    
    ServerClient(Socket connection)
    {
        this.connection = connection;
        
        connect();
        setClientData();
        receiveData();
    }
    
    @Override
    public int compareTo(Object input)
    {
        ServerClient compare = (ServerClient) input;
        
        String currentName = getClientName();
        String inputName = compare.getClientName();
        
        if(currentName.equals(inputName))
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
    
    //connect ServerClient to Client
    private void connect()
    {
        connected = true;
        
        try
        {
            output = new DataOutputStream(connection.getOutputStream());
        }
        catch(IOException IOe)
        {
            System.out.println("SERVERCLIENT CONNECT(): " + IOe);
        }
    }
    
    private void setClientData()
    {
        while (connected)
        {
            try
            {
                input = new DataInputStream(connection.getInputStream());
                dataString = input.readUTF();
                String[] dataSegments = dataString.split("\\|", -1);
                //check if command is currupt
                if(dataSegments.length == 6)
                {
                    if(dataSegments[0].equals(startProtocol) && dataSegments[5].equals(endProtocol) && 
                        dataSegments[3].equals("0"))
                    {
                        clientName = dataSegments[1];
                        flushDataString();
                        break;
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("SERVERCLIENT SETCLIENTDATA(): " + e);
                connected = false;
            }
        }
    }
    
    //receive data from clients
    private void receiveData()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while(connected)
                    {
                        input = new DataInputStream(connection.getInputStream());
                        dataString = input.readUTF();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("SERVERCLIENT RECEIVEDATA() " + clientName + ": " + e);
                    connected = false;
                }                
            }
        }.start();
    }
    
    public void sendData(String data)
    {
        try
        {   
            output.writeUTF(data);
            output.flush();
        }
        catch(IOException IOe)
        {
            System.out.println("SERVERCLIENT SENDDATA() " + clientName + ": " + IOe);
        }       
    }
    
    public String getDataString()
    {
        return dataString;
    }
    
    public void flushDataString()
    {
        dataString = null;
    }
    
    public void setConnectedFalse()
    {
        connected = false;
    }
    
    public boolean getConnected()
    {
        return connected;
    }
    
    public String getClientName()
    {
        return clientName;
    }
    
    public String getIP()
    {        
        return connection.getLocalAddress().toString();
    }
}