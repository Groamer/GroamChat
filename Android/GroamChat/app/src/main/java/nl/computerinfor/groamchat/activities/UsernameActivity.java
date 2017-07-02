package nl.computerinfor.groamchat.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nl.computerinfor.groamchat.R;

/**
 * Created by Tom Remeeus on 26-5-2016.
 */

public class UsernameActivity extends AppCompatActivity
{
    private String connectedUsers;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        //instantiate vars
        final Button setUsername = (Button)findViewById(R.id.setUsername);
        final EditText editUsername = (EditText)findViewById(R.id.editUsername);
        TextView viewUsername = (TextView)findViewById(R.id.viewUsername);

        //get connected users and view current username
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            connectedUsers = extras.getString("connectedUsers");
            currentUsername = extras.getString("currentUsername");
            viewUsername.setText("Current username:\n" + currentUsername);
        }

        //set username
        setUsername.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(editUsername.getText().length() != 0)
                {
                    boolean nameExists = false;

                    String editConnectedUsers = connectedUsers;
                    editConnectedUsers = editConnectedUsers.replace("[", "");
                    editConnectedUsers = editConnectedUsers.replace("]", "");
                    String userArray[] = editConnectedUsers.split(", ");

                    for(int i = 0; userArray.length > i; i ++)
                    {
                        if(userArray[i].equals(editUsername.getText().toString()))
                        {
                            nameExists = true;
                        }
                    }

                    if (nameExists)
                    {
                        nameExists();
                    }
                    else
                    {
                        changeUsername(editUsername.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent sameUsername = new Intent(this, MainActivity.class);
        sameUsername.putExtra("ownUsername", currentUsername);
        startActivity(sameUsername);
    }

    private void changeUsername(String username)
    {
        final Intent newUsername = new Intent(this, MainActivity.class);
        newUsername.putExtra("ownUsername", username);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("All previous chats and notifications will be closed when you change your username. Do you still want to continue?");
        builder.setCancelable(false);
        builder.setNegativeButton("No thanks", null);
        builder.setPositiveButton
        (
            "Yes please",
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NM.cancelAll();

                    startActivity(newUsername);
                }
            }
        );

        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getConnectedUsers()
    {
        return connectedUsers;
    }

    private void nameExists()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This name already exists, please pick another name.");
        builder.setCancelable(false);
        builder.setPositiveButton("Sure thing", null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
