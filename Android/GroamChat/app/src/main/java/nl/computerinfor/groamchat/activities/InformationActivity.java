package nl.computerinfor.groamchat.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import nl.computerinfor.groamchat.R;

/**
 * Created by Tom Remeeus on 27-5-2016.
 */

public class InformationActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        final TextView rateText = (TextView)findViewById(R.id.rateText);

        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                String ratingValue = String.valueOf(rating);

                switch (ratingValue)
                {
                    case "0.5":
                        rateText.setText("Worst app ever.");
                        break;

                    case "1.0":
                        rateText.setText("This is #^$&#!");
                        break;

                    case "1.5":
                        rateText.setText("Yugh!");
                        break;

                    case "2.0":
                        rateText.setText("Waste of time.");
                        break;

                    case "2.5":
                        rateText.setText("Meh...");
                        break;

                    case "3.0":
                        rateText.setText("It's OK...");
                        break;

                    case "3.5":
                        rateText.setText("Good.");
                        break;

                    case "4.0":
                        rateText.setText("Very good.");
                        break;

                    case "4.5":
                        rateText.setText("Supurb!");
                        break;

                    case "5.0":
                        rateText.setText("Top kek m8!");
                        break;
                }
            }
        });

        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                backMain();
            }
        });
    }

    private void backMain()
    {
        this.finish();
    }
}
