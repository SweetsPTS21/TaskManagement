package com.example.demoktra2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoktra2.UpdateDeleteActivity;
import com.example.demoktra2.adapter.RecycleViewAdapter;
import com.example.demoktra2.dal.FirebaseHelper;
import com.example.demoktra2.dal.SQLiteHelper;
import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.example.demoktra2.model.Item;
import com.example.demoktra2.R;
import com.example.demoktra2.model.UserTask;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentList extends Fragment implements RecycleViewAdapter.ItemListener {
    private RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    private FirebaseDatabase database;
    private FirebaseUser user;
    private GoogleSignInAccount account;
    private DatabaseReference ref;
    private String userId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleView);
        adapter = new RecycleViewAdapter();
        db = new SQLiteHelper(getContext());
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        //List<Item> list = db.getAll();

        if(FBUser.getCurrent_user() != null) {
            user = FBUser.getCurrent_user();
            userId = user.getUid();
        } else {
            account = GGUser.getCurrent_user();
            userId = account.getId();
        }
        getAllTask(userId);
    }


    @Override
    public void onItemClick(View view, int position) {
        //Item item = adapter.getItem(position);
        UserTask userTask = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), UpdateDeleteActivity.class);
        intent.putExtra("userTask", (Serializable) userTask);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllTask(userId);

    }
    public void getAllTask(String userId) {
        DatabaseReference userRef = ref.child("UserTask").child(userId);
        List<UserTask> userTaskList = new ArrayList<>();

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().getValue() != null) {
                    Object obj = task.getResult().getValue();
                    System.out.println("obj" + obj + " " + obj.getClass());
                    try {
                        ArrayList<Object> list = new ArrayList<>();
                        if (obj instanceof ArrayList) {
                            list = (ArrayList<Object>) obj;

                        }
                        if (obj instanceof HashMap) {
                            HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;

                            ArrayList<Object> arrayList = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {

                                HashMap<String, Object> map = new HashMap<>();
                                map.put(entry.getKey(), entry.getValue());
                                arrayList.add(entry.getValue());
                            }
                            list = (ArrayList<Object>) arrayList;
                        }

                        for (Object entry : list) {
                            if(entry == null)
                                continue;
                            JSONObject jsonObject = new JSONObject((Map) entry);
                            String id = (String) jsonObject.get("id");
                            String title = (String) jsonObject.get("title");
                            String date = (String) jsonObject.get("date");
                            String time = (String) jsonObject.get("time");
                            String description = (String) jsonObject.get("description");
                            String status = (String) jsonObject.get("status");
                            String category = (String) jsonObject.get("category");
                            UserTask userTask = new UserTask(id, title, date, time, status, category, description);
                            userTaskList.add(userTask);
                        }
                        adapter.setList(userTaskList);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);
                        adapter.setItemListener(FragmentList.this);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
