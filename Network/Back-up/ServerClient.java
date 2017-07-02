/**
 * @author Tom Remeeus
 */

package main;

import java.io.*;
import java.net.*;

public class ServerClient
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
            System.out.println("SERVERCLIENT: " + IOe);
        }
    }
    
    private void setClientData()
    {
        while (connection.isConnected())
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
                System.out.println("SERVERCLIENT: " + e);
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
                    while(connection.isConnected())
                    {
                        input = new DataInputStream(connection.getInputStream());
                        dataString = input.readUTF();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("SERVERCLIENT" + clientName + ": " + e);
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
            System.out.println("SERVERCLIENT" + clientName + ": " + IOe);
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
    
    public boolean getConnected()
    {
        return connected;
    }
    
    public String getClientName()
    {
        return clientName;
    }
}