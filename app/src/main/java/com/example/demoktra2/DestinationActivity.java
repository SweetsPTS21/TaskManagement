package com.example.demoktra2;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoktra2.model.UserTask;

public class DestinationActivity extends AppCompatActivity {

    private TextView tvTaskId, tvTaskTitle, tvTaskDescription, tvTaskDate, tvTaskTime, tvTaskStatus;
    private Button btCancelAlarm, btBack;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private UserTask userTaskGet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        initViews();
        Intent intent = getIntent();
        UserTask userTask = (UserTask) intent.getParcelableExtra("task");
        intent.removeExtra("task");

        if (userTask == null) {
            Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
        }
        else {
            tvTaskTime.setText(userTask.getTime());
            tvTaskTitle.setText(userTask.getTitle());
            tvTaskDescription.setText(userTask.getDescription());
        }


        btCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
                btCancelAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg_4));
                btCancelAlarm.setEnabled(false);
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViews() {
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskTime = findViewById(R.id.tvTaskTime);
        btCancelAlarm = findViewById(R.id.btCancelAlarm);
        btBack = findViewById(R.id.btBack);
    }

    private void cancelAlarm() {

        Intent intent = new Intent(this, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE);

        if (alarmManager == null) {

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Đã hủy thông báo", Toast.LENGTH_SHORT).show();
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // retrieve the User object from the extras of the intent
            UserTask user = (UserTask) intent.getSerializableExtra("task");

            userTaskGet = user;
            // use the User object as needed
            // ...
        }
    }
}

