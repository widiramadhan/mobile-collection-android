package sfi.mobile.collection;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class notifytest extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle,editTextMessage;
    private Button sendOnChannel1,sendOnChannel2;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifytest);

        notificationManager = NotificationManagerCompat.from(this);

        editTextMessage = (EditText) findViewById(R.id.edit_text_message);
        editTextTitle = (EditText) findViewById(R.id.edit_text_title);
        /*sendOnChannel1 = (Button) findViewById(R.id.sendOnChannel1);
        sendOnChannel2 = (Button) findViewById(R.id.sendOnChannel2);*/
        createNotificationChannels();
    }

    public void sendOnChannel2(View v) {
        final int progressMax = 100;
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_button_save)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax,0,false);

        notificationManager.notify(2, notification.build());

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                for (int progress = 0; progress <= progressMax; progress +=10){
                    notification.setProgress(progressMax,progress,false);
                    notificationManager.notify(2,notification.build());
                    SystemClock.sleep(1000);
                }
                notification.setContentText("Download Finished").setProgress(0,0,false)
                .setOngoing(false);
                notificationManager.notify(2,notification.build());
            }
        }).start();
    }


//-------------------------------------------------------------------------
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
