package com.example.vincent.projetpeta.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vincent.projetpeta.R;
import com.example.vincent.projetpeta.User;

public class OtherUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);
        User user = getIntent().getExtras().getParcelable("user");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_perso);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView photoUser = (ImageView) findViewById(R.id.photo_user);
        Glide.with(this).load(user.getPathPP()).into(photoUser);

        TextView prenomUser = (TextView) findViewById(R.id.prenom_profile);
        prenomUser.setText(user.getPrenom()+","+ user.getDateNaissance());
    }
}
