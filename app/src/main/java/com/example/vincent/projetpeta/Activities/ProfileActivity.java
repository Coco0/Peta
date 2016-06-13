package com.example.vincent.projetpeta.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.R;
import com.example.vincent.projetpeta.User;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private User profile_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ViewCompat.setTransitionName(findViewById(R.id.app_bar), "Name");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ImageView profile_photo = (ImageView) findViewById(R.id.profile_photo);
        String url = getSharedPreferences(Constantes.INFO_USER, 0).getString(Constantes.PATH_PP, "not find");
        Glide.with(this).load(Constantes.PETA_REP + url).into(profile_photo);
        TextView prenomUser = (TextView) findViewById(R.id.prenom_profile);
        String prenom = getSharedPreferences(Constantes.INFO_USER, 0).getString(Constantes.PRENOM, "unknow");
        String date = getSharedPreferences(Constantes.INFO_USER, 0).getString(Constantes.DATE, "unknow");
        prenomUser.setText(prenom + "," + date);


        TextView info = (TextView) findViewById(R.id.info_profile);
        info.setText(datet(date));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, ListUser_Activity.class);
        startActivity(intent);
    }
    public String datet(String dateN){
        Calendar now = Calendar.getInstance();
        int i = now.get(Calendar.YEAR);
        String dateY = String.valueOf(i);
        String a_letter = dateN.substring(0,4);
        int j = Integer.parseInt(a_letter);
        int finale = i-j ;

        System.out.println( a_letter + "," + dateN + ","+ finale ); // Prints f

        return String.valueOf(finale);
    }
}
