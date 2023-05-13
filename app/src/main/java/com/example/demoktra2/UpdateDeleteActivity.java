package com.example.demoktra2;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.demoktra2.model.UserTask;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateDeleteActivity extends AppCompatActivity implements View.OnClickListener{
    public Spinner sp,sp2;
    private EditText eTitle,eTitle2,eDate, eTime;
    private Button btUpdate,btBack,btRemove, btSetAlarm;
    private UserTask userTask;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseUser user;
    private GoogleSignInAccount account;
    private String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        initView();
        btUpdate.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btRemove.setOnClickListener(this);
        eDate.setOnClickListener(this);
        eTime.setOnClickListener(this);
        btSetAlarm.setOnClickListener(this);
        Intent intent = getIntent();
        userTask=(UserTask) intent.getSerializableExtra("userTask");
        intent.removeExtra("userTask");

        System.out.println("userTaskId: " + userTask.getId());
        eTitle.setText(userTask.getTitle());
        eTitle2.setText(userTask.getDescription());
        eDate.setText(userTask.getDate());
        eTime.setText(userTask.getTime());
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

        int p=0;
        for(int i=0; i<sp.getCount();i++){
            if(sp.getItemAtPosition(i).toString().equalsIgnoreCase(userTask.getDescription())){
                p=i;
                break;
            }
        }
        int p2=0;
        for(int i=0; i<sp2.getCount();i++){
            if(sp2.getItemAtPosition(i).toString().equalsIgnoreCase(userTask.getCategory())){
                p2=i;
                break;
            }
        }

    }

    private void initView() {
        sp=findViewById(R.id.spCategory);
        sp2=findViewById(R.id.spCategory2);
        eTitle=findViewById(R.id.tvTitle);
        eTitle2=findViewById(R.id.tvTitle2);
        eDate=findViewById(R.id.tvDate);
        eTime=findViewById(R.id.tvTime);
        btUpdate=findViewById(R.id.btUpdate);
        btBack=findViewById(R.id.btBack);
        btRemove=findViewById(R.id.btRemove);
        btSetAlarm = findViewById(R.id.btSetAlarm);

        btSetAlarm.setEnabled(false);
        btSetAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg_4));

        sp.setAdapter(new ArrayAdapter<String>(this,R.layout.item_spinner,getResources().getStringArray(R.array.category)));
        sp2.setAdapter(new ArrayAdapter<String>(this,R.layout.item_spinner,getResources().getStringArray(R.array.category2)));

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        if(view==eDate){
            final Calendar c=Calendar.getInstance();
            int year= c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(UpdateDeleteActivity.this, new DatePickerDialog.OnDateSetListener() {
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
            if (picker == null || eTime.getText().toString().isEmpty()) {
                return;
            }
            btSetAlarm.setEnabled(true);
            btSetAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg));
        }
        if(view==btBack){
            finish();
        }
        if(view==btUpdate){
            String title =eTitle.getText().toString();
            String description =eTitle2.getText().toString();
            String date = eDate.getText().toString();
            String status =sp.getSelectedItem().toString();
            String category =sp2.getSelectedItem().toString();
            String time = eTime.getText().toString();

            if(!title.isEmpty() && !description.isEmpty() && !date.isEmpty()){
                UserTask newUserTask = new UserTask(userTask.getId(),title, date, time, status, category, description);
                updateTask(userId, newUserTask);
                finish();
            }

        }
        if(view==btRemove){
            //int id= userTask.getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Thông báo xóa");
            builder.setMessage("Bạn có chắc muốn xóa "+userTask.getTitle()+" không?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ref.child("UserTask").child(userId).child(userTask.getId()).removeValue();
                    finish();
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (view == btSetAlarm) {
            setAlarm();
            btSetAlarm.setBackground(getResources().getDrawable(R.drawable.button_bg_4));
            btSetAlarm.setEnabled(false);
        }
    }
    public void updateTask(String userId, UserTask userTask) {
        DatabaseReference userRef = ref.child("UserTask").child(userId);
        Map<String, Object> userTaskValues = userTask.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userTask.getId(), userTaskValues);
        System.out.println("childUpdates: " + childUpdates);
        userRef.updateChildren(childUpdates);
    }

    private void showTimePicker() {
        calendar = Calendar.getInstance();
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(), "taskmanagement");

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
                userTask.setTime(eTime.getText().toString());

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

    @SuppressLint("UnspecifiedImmutableFlag")
    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("task", (Parcelable) userTask);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Đã đặt thông báo", Toast.LENGTH_SHORT).show();
        intent.removeExtra("task");
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