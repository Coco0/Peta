package com.example.vincent.projetpeta.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.etsy.android.grid.StaggeredGridView;
import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.R;
import com.example.vincent.projetpeta.User;
import com.example.vincent.projetpeta.UserAdaptater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ListUserActivity extends AppCompatActivity implements AbsListView.OnItemClickListener {
	private ArrayList<User> users = new ArrayList<>();
	private StaggeredGridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stagg);
		mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
		if (mGridView != null)
			mGridView.setOnItemClickListener(this);
		new UserListTask().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Intent intent = new Intent(ListUserActivity.this, ConversationActivity.class);
		intent.putExtra("idUserClicked", users.get(position).getIdUser());
		startActivity(intent);
	}


	public class UserListTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			String resFromServer = null;
			try {
				//	JSONObject json = new JSONObject();

				HttpClient client = HttpClientBuilder.create().build();
				HttpPost post = new HttpPost(Constantes.URL_GET_USERS_SERVICE);

//				HttpClient client = new DefaultHttpClient();
//				HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);
//				HttpPost post = new HttpPost(Constantes.URL_GET_USERS_SERVICE);

			/*	StringEntity se = new StringEntity("json=" + json.toString());
				post.addHeader("content-type", "application/x-www-form-urlencoded");
				post.setEntity(se);
			*/
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
			UserAdaptater mAdapter = new UserAdaptater(ListUserActivity.this, R.layout.activity_list_users, users);
			mGridView.setAdapter(mAdapter);
		}
	}
}