package by.backstagedevteam.safetraffic;
/*
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

} */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notification {
    private NotificationManager mManager;
    public static final String WARNING_CHANNEL_ID = "by.backstagedevteam.safetraffic.WARNING";
    public static final String WARNING_CHANNEL_NAME = "ANDROID CHANNEL";

    public static final int NOTIFICATION_ID = 1;

    public Notification() {
    }

    public static void sendNotification(Context context, String text) {

        if (Build.VERSION.SDK_INT < 23) {
            // BEGIN_INCLUDE(build_action)
            /** Create an intent that will be fired when the user clicks the notification.
             * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
             * notification service can fire it on our behalf.
             */
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // END_INCLUDE(build_action)

            // BEGIN_INCLUDE (build_notification)
            /**
             * Use NotificationCompat.Builder to set up our notification.
             */
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            /** Set the icon that will appear in the notification bar. This icon also appears
             * in the lower right hand corner of the notification itself.
             *
             * Important note: although you can use any drawable as the small icon, Android
             * design guidelines state that the icon should be simple and monochrome. Full-color
             * bitmaps or busy images don't render well on smaller screens and can end up
             * confusing the user.
             */
            builder.setSmallIcon(R.drawable.ic_stat_notification);

            // Set the intent that will fire when the user taps the notification.
            builder.setContentIntent(pendingIntent);

            // Set the notification to auto-cancel. This means that the notification will disappear
            // after the user taps it, rather than remaining until it's explicitly dismissed.
            builder.setAutoCancel(true);

            /**
             *Build the notification's appearance.
             * Set the large icon, which appears on the left of the notification. In this
             * sample we'll set the large icon to be the same as our app icon. The app icon is a
             * reasonable default if you don't have anything more compelling to use as an icon.
             */
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));

            /**
             * Set the text of the notification. This sample sets the three most commononly used
             * text areas:
             * 1. The content title, which appears in large type at the top of the notification
             * 2. The content text, which appears in smaller text below the title
             * 3. The subtext, which appears under the text on newer devices. Devices running
             *    versions of Android prior to 4.2 will ignore this field, so don't use it for
             *    anything vital!
             */
            builder.setContentTitle("WARNING");
            builder.setContentText(text);
            builder.setSubText("by Safe Traffic");

            // END_INCLUDE (build_notification)

            // BEGIN_INCLUDE(send_notification)
            /**
             * Send the notification. This will immediately display the notification icon in the
             * notification bar.
             */
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            // END_INCLUDE(send_notification)
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

    }
}
