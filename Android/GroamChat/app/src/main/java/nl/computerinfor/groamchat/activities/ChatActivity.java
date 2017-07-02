package nl.computerinfor.groamchat.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.computerinfor.groamchat.R;
import nl.computerinfor.groamchat.clients.*;

public class ChatActivity extends AppCompatActivity
{
    private ChatClient client;
    private String ownUsername;
    private List<String> unreadMessages = new ArrayList<>();
    private String otherUsername;
    private int unreadMessagesCounter;
    private boolean active;
    private TextView viewOtherUserName;
    private TextView viewMessages;
    private ScrollView scrollMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //instantiate vars
        final EditText message = (EditText) findViewById(R.id.message);
        final Button sendMessage = (Button)findViewById(R.id.sendMessage);
        viewOtherUserName = (TextView)findViewById(R.id.userName);
        viewMessages = (TextView) findViewById(R.id.viewMessages);
        scrollMessages = (ScrollView) findViewById(R.id.scrollMessages);

        //set app active
        active = true;

        //set username of other client and start client
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null)
                    {
                        //get own username
                        ownUsername = extras.getString("ownUsername");
                        //get username from other client
                        otherUsername = extras.getString("otherUsername");
                        viewOtherUserName.setText("Chatting with " + otherUsername);
                        //start client and connect to other user
                        startClient(otherUsername);
                        //get messages from client
                        while(true)
                        {
                            if(client != null)
                            {
                                client.setMessages(extras.getString("messages").toString());
                                break;
                            }
                        }
                    }
                }
                catch(Exception e)
                {
                    Log.d("ChatActivity", e.toString());
                }
            }
        });

        //update message
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while(active)
                    {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    viewMessages.setText(updateMessages());
                                    updateUnrelatedMessages();
                                }
                                catch(Exception e)
                                {
                                    System.out.println(e);
                                    active = false;
                                }
                            }
                        });
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
        }.start();

        //send message
        sendMessage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(!message.getText().toString().isEmpty())
                {
                    client.sendMessage(message.getText().toString(), otherUsername);
                    message.setText(null);
                    scrollMessages.scrollTo(0, viewMessages.getHeight());
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        active = false;
        client.closeConnection();
        this.finish();

        Intent mainMenu = new Intent(this, MainActivity.class);
        mainMenu.putExtra("ownUsername", ownUsername);
        startActivity(mainMenu);
    }

    private void startClient(final String otherUserName)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                client = new ChatClient(ownUsername, otherUserName);
            }
        }.start();
    }

    private String updateMessages()
    {
        try
        {
            if(!client.getMessages().toString().equals("[]"))
            {
                String messageList = "";

                for(int i = 0; i < client.getMessages().size(); i ++)
                {
                    String message = client.getMessages().get(i).toString() + "\n\n";
                    messageList += message;
                }

                return messageList;
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    private void updateUnrelatedMessages()
    {
        if(unreadMessagesCounter < client.getUnrelatedMessages().size())
        {
            unreadMessages = client.getUnrelatedMessages();
            unreadMessagesCounter = client.getUnrelatedMessages().size();

            showNotification(client.getUnrelatedMessages().get(client.getUnrelatedMessages().size() - 1).toString());
        }
    }

    private void showNotification(String message)
    {
        //set new activity when notification is clicked
        Intent newChat = new Intent(this, NotificationActivity.class);
        newChat.putExtra("ownUsername", ownUsername);
        newChat.putExtra("messages", unreadMessages.toString());
        PendingIntent newChatPending = PendingIntent.getActivity(this, 0, newChat, PendingIntent.FLAG_UPDATE_CURRENT);

        //build and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("GroamChat");
        builder.setContentText(message);
        builder.setContentIntent(newChatPending);
        builder.setAutoCancel(true);
        builder.setGroup("GroamChat");
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }
}
