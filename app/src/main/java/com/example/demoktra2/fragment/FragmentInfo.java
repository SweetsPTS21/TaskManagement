package com.example.demoktra2.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoktra2.LoginActivity;
import com.example.demoktra2.MainActivity;
import com.example.demoktra2.UserInfoActivity;
import com.example.demoktra2.adapter.RecycleViewAdapter;
import com.example.demoktra2.dal.SQLiteHelper;
import com.example.demoktra2.R;
import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.example.demoktra2.model.People;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class FragmentInfo extends Fragment  {
    private RecycleViewAdapter adapter;
    private RecyclerView recycleView;
    private SQLiteHelper db;
    private TextView tvName,tvDes;
    private ImageView img;
    private Button btnInfoCenter;
    private AppCompatButton btnLogout;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount account;
    private GoogleSignInClient googleSignInClient;

//    implements RecycleViewAdapter.ItemListener
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info,container,false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        img= view.findViewById(R.id.img);
        user = FBUser.getCurrent_user();
        account = GGUser.getCurrent_user();
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        img.setImageResource(R.drawable.img);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnInfoCenter = view.findViewById(R.id.btnInfoCenter);

        String userEmail = "";
        String userName = "";
        if(user != null){
            userEmail = user.getEmail();
            userName = user.getDisplayName();
            Picasso.get().load(user.getPhotoUrl()).into(img);
        }
        else {
            userEmail = account.getEmail();
            userName = account.getDisplayName();
            Picasso.get().load(account.getPhotoUrl()).into(img);
        }
        tvName.setText(userName);
        tvDes.setText(userEmail);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        btnInfoCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FBUser.signOut();
                        GGUser.signOut();
                        mAuth.signOut();
                        googleSignInClient.signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Hủy", null);
        builder.create().show();
    }

    public void onItemClick(View view, int position) {

    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).hideFab();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).showFab();
    }
}
