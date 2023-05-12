package com.example.demoktra2.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoktra2.adapter.RecycleViewAdapter;
import com.example.demoktra2.dal.FirebaseHelper;
import com.example.demoktra2.dal.SQLiteHelper;
import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.example.demoktra2.model.Item;
import com.example.demoktra2.R;
import com.example.demoktra2.model.ItemTK;
import com.example.demoktra2.model.UserTask;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentSearch extends Fragment implements RecycleViewAdapter.ItemListener, View.OnClickListener{

    private RecyclerView recyclerView;
    private Button btSearch;
    private SearchView svName, svDes;
    private Spinner spCategory;
    private RecycleViewAdapter adapter;
    private SQLiteHelper db;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private FirebaseUser user;
    private GoogleSignInAccount account;
    private List<UserTask> userTaskList;
    private String userId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        adapter=new RecycleViewAdapter();
        userTaskList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        if(FBUser.getCurrent_user() != null) {
            user = FBUser.getCurrent_user();
            userId = user.getUid();
        } else {
            account = GGUser.getCurrent_user();
            userId = account.getId();
        }
        getAllTask(userId);

        svName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String searchType = "title";
                findTaskByTitle(userId, s, searchType);
                return true;
            }
        });
        svDes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String searchType = "description";
                if(FBUser.getCurrent_user() != null) {
                    user = FBUser.getCurrent_user();
                    findTaskByTitle(user.getUid(), s, searchType);
                } else {
                    account = GGUser.getCurrent_user();
                    findTaskByTitle(account.getId(), s, searchType);
                }
                return true;
            }
        });

        btSearch.setOnClickListener(this);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s=spCategory.getItemAtPosition(position).toString();
                String searchType = "status";

                if(s.equalsIgnoreCase("Tất cả tình trạng")) {
                    getAllTask(userId);
                } else {
                    findTaskByTitle(userId, s, searchType);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void initView(View view) {
        recyclerView=view.findViewById(R.id.recycleView);
        btSearch=view.findViewById(R.id.btSearch);
        svName=view.findViewById(R.id.search);
        svDes = view.findViewById(R.id.search2);
        spCategory=view.findViewById(R.id.spCategory);

        String[] arr = getResources().getStringArray(R.array.category);
        String[] arr1=new String[arr.length+1];
        arr1[0]="Tất cả tình trạng";
        for(int i=0;i<arr.length;i++){
            arr1[i+1]=arr[i];
        }
        spCategory.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.item_spinner,arr1));

    }

    @Override
    public void onClick(View view) {
        if(view==btSearch){

        }
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
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;
                        ArrayList<Map.Entry<String, Object>> list = new ArrayList<>(hashMap.entrySet());
                        for(Map.Entry<String, Object> entry : list) {
                            Object value = entry.getValue();
                            HashMap<String, String> hashMap1 = (HashMap<String, String>) value;

                            String id = (String) hashMap1.get("id");
                            String title = (String) hashMap1.get("title");
                            String date = (String) hashMap1.get("date");
                            String time = (String) hashMap1.get("time");
                            String description = (String) hashMap1.get("description");
                            String status = (String) hashMap1.get("status");
                            String category = (String) hashMap1.get("category");
                            UserTask userTask = new UserTask(id, title, date, time, status, category, description);
                            userTaskList.add(userTask);
                        }
                        adapter.setList(userTaskList);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);
                        adapter.setItemListener(FragmentSearch.this);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void findTaskByTitle(String userId, String key, String searchType) {
        DatabaseReference userRef = ref.child("UserTask").child(userId);
        List<UserTask> userTaskList = new ArrayList<>();
        Query query = userRef.orderByChild(searchType).startAt(key).endAt(key + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userTaskList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserTask userTask = dataSnapshot.getValue(UserTask.class);
                    userTaskList.add(userTask);
                }
                adapter.setList(userTaskList);
                LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                adapter.setItemListener(FragmentSearch.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getAllTask(userId);
    }
}
