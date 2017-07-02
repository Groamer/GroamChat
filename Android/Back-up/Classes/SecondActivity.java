package nl.computerinfor.groamchat;

import java.util.List;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import nl.computerinfor.groamchat.connection.*;

public class SecondActivity extends AppCompatActivity
{
    private Client client;
    private String name;

    public SecondActivity()
    {
        startClient();
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
                client = new Client(deviceName);
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView username = (TextView)findViewById(R.id.userName);

        //get information from other client
        new Thread()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            while(true)
                            {
                                Bundle extras = getIntent().getExtras();
                                if (extras != null)
                                {
                                    name = extras.getString("name");
                                    byte[] client = extras.getByteArray("client");
                                    Log.d("SecondActivity", name + client.toString());
                                    break;
                                }
                            }
                        }
                        catch(Exception e)
                        {
                            Log.d("SecondActivity", e.toString());
                        }
                    }
                });
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
