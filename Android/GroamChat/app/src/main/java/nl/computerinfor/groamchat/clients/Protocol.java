package nl.computerinfor.groamchat.clients;

/**
 * Created by Tom Remeeus on 17-5-2016.
 */
public class Protocol
{
    final private String ip = "37.97.180.203";
    final private int port = 6969;
    final private String start = "@GroamChat@";
    final private String end = "#GroamChat#";
    final private String sendClientData = "0";
    final private String message = "1";
    final private String messageAll = "2";
    final private String removeClientFromServer = "4";

    protected String ipAddress() { return ip; }
    protected int portAddress() { return port; }
    protected String startProtocol()
    {
        return start;
    }
    protected String endProtocol()
    {
        return end;
    }
    protected String sendClientDataProtocol()
    {
        return sendClientData;
    }
    protected String messageProtocol()
    {
        return message;
    }
    protected String messageAllProtocol()
    {
        return messageAll;
    }
    protected String removeClientFromServerProtocol()
    {
        return removeClientFromServer;
    }
}
