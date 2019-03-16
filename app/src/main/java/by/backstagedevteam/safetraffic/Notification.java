package by.backstagedevteam.safetraffic;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notification extends Activity{

    private NotificationManager nm;
    private final int NOTIFICATION__ID = 101;


    @Override
    protected void onCreate(Bundle savedInstranceState){
        super.onCreate(savedInstranceState);
        setContentView(R.layout.app_bar_main);

        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public Notification(View view){
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), FinishNotification.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.ic_launcher))
                .setTicker("Уведомление")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Новое")
                .setContentText("Жмяк");





    }

    private class Builder {
        public Builder(Context applicationContext) {


        }

        public NotificationCompat.Builder setContentIntent(PendingIntent pendingIntent) {
            return null;
        }


    }
}