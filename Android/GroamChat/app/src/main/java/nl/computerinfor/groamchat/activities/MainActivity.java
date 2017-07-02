package nl.computerinfor.groamchat.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.computerinfor.groamchat.R;
import nl.computerinfor.groamchat.clients.*;

public class MainActivity extends AppCompatActivity
{
    private MainClient client;
    private List clients;
    private ListView clientListView;
    private ArrayAdapter<String> listViewAdapter;
    private int unreadMessagesCounter;
    private List<String> unreadMessages = new ArrayList<>();
    private boolean active;
    private String thisClientName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate vars
        final Button refreshList = (Button)findViewById(R.id.refreshList);
        final Button randomPick = (Button)findViewById(R.id.randomClient);

        //set name
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            thisClientName = extras.getString("ownUsername");
        }

        //start MainClient
        startClient();
        active = true;

        //update listView
        updateClientList();
        clientListView = (ListView)findViewById(R.id.connectedClients);
        clientListView.setClickable(true);
        clientListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startChat(clientListView.getItemAtPosition(position).toString());
                closeMainClient();
            }
        });

        //button refresh action
        listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, clients);
        refreshList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    updateClientList();
                    listViewAdapter.clear();
                    listViewAdapter.addAll(clients);
                    clientListView.setAdapter(listViewAdapter);
                    refreshList.setText("Reload list");

                    if(clients.size() == 0)
                    {
                        showDialog("There are no users online at this moment. Please try again later.");
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
        });

        //button random user pick action
        randomPick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    updateClientList();
                    Random random = new Random();
                    String randomUser = (String)clients.get(random.nextInt(clients.size()));
                    startChat(randomUser);
                    closeMainClient();
                }
                catch(Exception e)
                {
                    showDialog("GroamChat failed to connect to random user." +
                                " Make sure you are connected to the internet and that other users are online." + "" +
                                " You can view online users by clicking the 'LOAD LIST' button.");
                    System.out.println(e);
                }
            }
        });

        //update messages
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while(active)
                    {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    updateMessages();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setName)
        {
            String connectedUsers = client.getClients().toString();
            closeMainClient();
            Intent setUsername = new Intent(this, UsernameActivity.class);
            setUsername.putExtra("connectedUsers", connectedUsers);
            setUsername.putExtra("currentUsername", thisClientName);
            startActivity(setUsername);
            return true;
        }

        if (id == R.id.action_information)
        {
            Intent viewInformation = new Intent(this, InformationActivity.class);
            startActivity(viewInformation);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to exit? " +
                "All chats and notifications from GroamChat will be closed.");
        builder.setNegativeButton("Resume", null);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity.super.onBackPressed();
                closeApp();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startClient()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                if(thisClientName == null)
                {
                    Random random = new Random();

                    String digit0 = Integer.toString(random.nextInt(9));
                    String digit1 = Integer.toString(random.nextInt(9));
                    String digit2 = Integer.toString(random.nextInt(9));
                    String digit3 = Integer.toString(random.nextInt(9));
                    String digit4 = Integer.toString(random.nextInt(9));
                    String digit5 = Integer.toString(random.nextInt(9));

                    thisClientName = "GroamChatter " + digit0 + digit1 + digit2 + digit3 + digit4 + digit5;
                }

                client = new MainClient(thisClientName);
            }
        }.start();
    }

    //close client
    private void closeMainClient()
    {
        client.closeConnection();
        client = null;
    }

    private void closeApp()
    {
        closeMainClient();
        this.finish();
    }

    //get connected clients from server
    private void updateClientList()
    {
        try
        {
            clients = new ArrayList<>();
            clients = client.getClients();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    //start chat with selected client
    private void startChat(String otherUsername)
    {
        Intent chatActivity = new Intent(this, ChatActivity.class);

        chatActivity.putExtra("ownUsername", thisClientName);
        chatActivity.putExtra("otherUsername", otherUsername);

        List<String> userMessages = client.getMessages();
        for(int i = 0; i < userMessages.size(); i ++)
        {
            if(!userMessages.get(i).contains(otherUsername))
            {
                userMessages.remove(i);
            }
        }
        chatActivity.putExtra("messages", userMessages.toString());

        startActivity(chatActivity);
    }

    private void showNotification(String message)
    {
        //set new activity when notification is clicked
        Intent newChat = new Intent(this, NotificationActivity.class);
        newChat.putExtra("ownUsername", thisClientName);
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

    private void showDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton
        (
            "OK",
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                }
            }
        );

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateMessages()
    {
        if(unreadMessagesCounter < client.getMessages().size())
        {
            unreadMessages = client.getMessages();
            unreadMessagesCounter = client.getMessages().size();

            showNotification(client.getMessages().get(client.getMessages().size() - 1).toString());
        }
    }
}