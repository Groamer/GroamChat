package nl.computerinfor.groamchat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.computerinfor.groamchat.connection.*;

public class MainActivity extends AppCompatActivity
{
    private MainClient client;
    private List clients;
    private ListView clientListView;
    private ArrayAdapter<String> listViewAdapter;

    //starts client thread to connect with server and set list for messages
    public MainActivity()
    {
        startClient();
        updateClientList();
    }

    //connect to server
    private void startClient()
    {
        final String deviceName = Build.MODEL;
        new Thread()
        {
            @Override
            public void run()
            {
                client = new MainClient(deviceName);
            }
        }.start();
    }

    //get connected clients from server
    private void updateClientList()
    {
        clients = new ArrayList<>();
        new Thread()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        clients = client.getClients();
                        Thread.sleep(2000);
                    }
                    catch(Exception e)
                    {
                        Log.d("updateClientList", e.toString());
                    }
                }
            }
        }.start();
    }

    //start chat with selected client
    private void startChat(String clientName)
    {
        Intent i = new Intent(this, SecondActivity.class);
        i.putExtra("name", clientName);
        //i.putExtra("client", getClientInformation());
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //update listView
        listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, clients);
        clientListView = (ListView)findViewById(R.id.connectedClients);
        clientListView.setClickable(true);
        clientListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startChat(parent.toString());
            }
        });

        //button refresh action
        final Button refreshList = (Button)findViewById(R.id.refreshList);
        refreshList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    listViewAdapter.clear();
                    listViewAdapter.addAll(clients);
                    clientListView.setAdapter(listViewAdapter);
                    refreshList.setText("Reload list");
                }
                catch(Exception e)
                {
                    Log.d("RefreshButton", e.toString());
                }
            }
        });

        //button random user pick action
        final Button randomPick = (Button)findViewById(R.id.randomClient);
        randomPick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Random random = new Random();
                String randomUser = (String)clients.get(random.nextInt(clients.size()));
                startChat(randomUser);
            }
        });
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
