package com.example.vincent.projetpeta.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;

import com.etsy.android.grid.StaggeredGridView;
import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.DataAdapter;
import com.example.vincent.projetpeta.R;
import com.example.vincent.projetpeta.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListUser_Activity extends AppCompatActivity implements AbsListView.OnItemClickListener {
    private ArrayList<User> users = new ArrayList<>();
    private StaggeredGridView mGridView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stagg);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar_perso);
        setSupportActionBar(toolbar);


        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, 0, 0);


        //afficher le bouton retour
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        this.drawerLayout.addDrawerListener(this.drawerToggle);

        Button button = (Button) findViewById(R.id.buttond);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListUser_Activity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button button_deconnexion = (Button) findViewById(R.id.button_deconnexion);
        button_deconnexion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deconnexion();
                Intent intent = new Intent(ListUser_Activity.this, Login_activityCoco.class);
                startActivity(intent);
            }
        });
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        if (mGridView != null)
            mGridView.setOnItemClickListener(this);
        new UserListTask().execute();
    }

    public void deconnexion() {
        SharedPreferences.Editor editor = getSharedPreferences(Constantes.INFO_USER, 0).edit();
        editor.clear();
        editor.commit();
    }
    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        User user = new User();
        user = users.get(position);
        Intent intent = new Intent(ListUser_Activity.this, OtherUserActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    public class UserListTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            String resFromServer = null;
            try {

                JSONObject json = new JSONObject();
                json.put("idUser", getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1));

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);
                HttpPost post = new HttpPost(Constantes.URL_GET_USERS_SERVICE);
                StringEntity se = new StringEntity("json=" + json.toString());
                post.addHeader("content-type", "application/x-www-form-urlencoded");
                post.setEntity(se);

                HttpResponse response = client.execute(post);
                resFromServer = EntityUtils.toString(response.getEntity());
                Log.i("GetUsers", resFromServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resFromServer;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray usersJsonArray = new JSONArray(result);

                for (int i = 0, count = usersJsonArray.length(); i < count; i++) {
                    // TODO Simplifier tout ça
                    JSONObject userJSONObject = usersJsonArray.getJSONObject(i);
                    User user = new User(
                            userJSONObject.getInt("idUser"),
                            userJSONObject.getString("dateNaissance"),
                            userJSONObject.getString("sexe").charAt(0),
                            userJSONObject.getString("prenom"),
                            Constantes.PETA_REP + userJSONObject.getString("pathPP")
                    );
                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
            DataAdapter mAdapter = new DataAdapter(ListUser_Activity.this, R.layout.list_item_sample, users);
            mGridView.setAdapter(mAdapter);
        }
    }
}