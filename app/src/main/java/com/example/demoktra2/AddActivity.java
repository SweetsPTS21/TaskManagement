package com.example.demoktra2;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoktra2.dal.SQLiteHelper;
import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.example.demoktra2.model.Item;
import com.example.demoktra2.model.UserTask;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity implements View.OnClickListener{
    public Spinner sp,sp2;
    private EditText eTitle,eTitle2,eDate, eTime;
    private Button btUpdate,btCancel, btSetAlarm;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseUser user;
    private GoogleSignInAccount account;
    private String userId = "";
    private UserTask userTaskSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
        btUpdate.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        eDate.setOnClickListener(this);
        eTime.setOnClickListener(this);
        btSetAlarm.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        createNotificationChannel();

        if(FBUser.getCurrent_user() != null) {
            user = FBUser.getCurrent_user();
            userId = user.getUid();
        } else {
            account = GGUser.getCurrent_user();
            userId = account.getId();
        }
    }

    private void initView() {
        sp=findViewById(R.id.spCategory);
        eTitle=findViewById(R.id.tvTitle);
        eTitle2=findViewById(R.id.tvTitle2);
        eDate=findViewById(R.id.tvDate);
        eTime=findViewById(R.id.tvTime);
        btUpdate=findViewById(R.id.btUpdate);
        btCancel=findViewById(R.id.btCancel);
        btSetAlarm = findViewById(R.id.btSetAlarm);

        btSetAlarm.setEnabled(false);
        btSetAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg_4));

        sp2=findViewById(R.id.spCategory2);
        sp.setAdapter(new ArrayAdapter<String>(this,R.layout.item_spinner,getResources().getStringArray(R.array.category)));
        sp2.setAdapter(new ArrayAdapter<String>(this,R.layout.item_spinner,getResources().getStringArray(R.array.category2)));

    }

    @Override
    public void onClick(View view) {
        if(view==eDate){
            final Calendar c=Calendar.getInstance();
            int year= c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int y, int m, int d) {
                    String date="";
                    if(d>8){
                        if(m>8){
                            date = d +"/"+(m+1)+"/"+y;
                        }else{
                            date=d+"/0"+(m+1)+"/"+y;
                        }
                    }else{
                        if(m>8){
                            date = d +"/"+(m+1)+"/"+y;
                        }else{
                            date="0"+d+"/0"+(m+1)+"/"+y;
                        }

                    }

                    eDate.setText(date);
                }
            },year,month,day);
            dialog.show();
        }
        if (view == eTime) {
            showTimePicker();
            if (picker == null && eTime.getText().toString().isEmpty()) {
                return;
            }
            btSetAlarm.setEnabled(true);
            btSetAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg));
        }
        if(view==btCancel){
            finish();
        }
        if(view==btUpdate){
            String title =eTitle.getText().toString();
            String description =eTitle2.getText().toString();
            String date =eDate.getText().toString();
            String time = eTime.getText().toString();
            String status = sp.getSelectedItem().toString();
            String category =sp2.getSelectedItem().toString();
            if(!title.isEmpty() && !description.isEmpty()){
                UserTask userTask = new UserTask(title, date, time, status, category, description);
                addTask(userId, userTask);
                userTaskSet = userTask;
                finish();
            }
        }
        if(view == btSetAlarm) {
            setAlarm();
            btSetAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg_4));
            btSetAlarm.setEnabled(false);
        }
    }
    public void addTask(String userId, UserTask userTask) {
        DatabaseReference userRef = ref.child("UserTask").child(userId);
        Query lastQuery = userRef.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            int lastKey = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Object obj = dataSnapshot.getValue();
                    try {
                        if (obj instanceof HashMap) {
                            HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;
                            ArrayList<Map.Entry<String, Object>> list = new ArrayList<>(hashMap.entrySet());
                            Map.Entry<String, Object> lastEntry = list.get(list.size() - 1);

                            lastKey = Integer.parseInt(lastEntry.getKey());
                        }
                        else {
                            ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) obj;
                            lastKey = Integer.parseInt(list.get(list.size() - 1).get("id").toString());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                int newKey = lastKey + 1;
                UserTask newUserTask = new UserTask(String.valueOf(newKey), userTask.getTitle(), userTask.getDate(), userTask.getTime() ,userTask.getStatus(), userTask.getCategory(), userTask.getDescription());
                userRef.child(String.valueOf(newKey)).setValue(newUserTask);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(), "foxandroid");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picker.getHour() > 12) {

                   eTime.setText(
                            String.format("%02d", (picker.getHour() - 12)) + " : " + String.format("%02d", picker.getMinute()) + " PM"
                    );

                } else {

                    eTime.setText(picker.getHour() + " : " + picker.getMinute() + " AM");

                }

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

            }
        });
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

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
//        intent.putExtra("task", (Parcelable) userTask);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Đã đặt thông báo", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Manager Reminder Channel";
            String description = "Cấp quyền để nhận thông báo cho ứng dụng";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("taskmanagement", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }
}