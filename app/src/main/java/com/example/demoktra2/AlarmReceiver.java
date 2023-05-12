package com.example.demoktra2;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcelable;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.demoktra2.model.UserTask;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, DestinationActivity.class);
        UserTask userTask = intent.getParcelableExtra("task");
        i.putExtra("task", (Parcelable) userTask);
        intent.removeExtra("task");

        String message = "Bạn có công việc cần làm: " + userTask.getTitle();
        String action = "Nhấn vào đây để xem chi tiết";

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "taskmanagement")
                .setSmallIcon(R.drawable.ic_task)
                .setContentTitle(message)
                .setContentText(action)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        try {
            notificationManagerCompat.notify(123, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
}
