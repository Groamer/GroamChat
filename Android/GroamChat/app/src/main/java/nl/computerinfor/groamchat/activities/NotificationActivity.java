package nl.computerinfor.groamchat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom Remeeus on 17-5-2016.
 */
public class NotificationActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            while(true)
            {
                Bundle extras = getIntent().getExtras();

                String ownUsername = extras.getString("ownUsername");
                String messages = extras.getString("messages");

                if(messages != null)
                {
                    launchChat(ownUsername, messages);
                    break;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    private void launchChat(String ownUsername, String messages)
    {
        List<String> messageList = new ArrayList<>();
        String messageString = messages;
        String otherUserName;

        messageString = messageString.replace("[", "");
        messageString = messageString.replace("]", "");
        String[] messageArray = messageString.split(", ");
        otherUserName = messageArray[messageArray.length - 1].split(":")[0];

        for(int i = 0; i < messageArray.length; i ++)
        {
            if(messageArray[i].contains(otherUserName))
            {
                messageList.add(messageArray[i]);
            }
        }


        Intent newChat = new Intent(this, ChatActivity.class);
        newChat.putExtra("ownUsername", ownUsername);
        newChat.putExtra("otherUsername", otherUserName);
        newChat.putExtra("messages", messageList.toString());
        startActivity(newChat);
    }
}
