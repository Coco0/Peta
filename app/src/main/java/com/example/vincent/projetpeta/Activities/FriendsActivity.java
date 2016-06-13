package com.example.vincent.projetpeta.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by Vincent on 24/05/2016.
 */
public class FriendsActivity extends AppCompatActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FriendsTransmitter friendsTransmitter = new FriendsTransmitter();
		friendsTransmitter.execute();
	}

	public class FriendsTransmitter extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			JSONObject json = new JSONObject();
			JSONArray jsonResponse = null;
			try {
				json.put("idUser", getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1));
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

				HttpPost post = new HttpPost(Constantes.IP + "/peta/services/getFriends.php");
				StringEntity se = new StringEntity("json=" + json.toString());
				post.addHeader("content-type", "application/x-www-form-urlencoded");
				post.setEntity(se);

				HttpResponse response = client.execute(post);
				String resFromServer = EntityUtils.toString(response.getEntity());
				Log.i("Response from server", resFromServer);

				jsonResponse = new JSONArray(resFromServer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonResponse;
		}

		protected void onPostExecute(final JSONArray jsonArray) {
			try {
				ListView listView = (ListView) findViewById(R.id.friendListView);
				List<HashMap<String, String>> amis = new ArrayList<>();
				HashMap<String, String> prenomAmi;
				final ArrayList<Integer> idConversation = new ArrayList<>();
				final ArrayList<String> loginAmi = new ArrayList<>();
				for (int i = 0; i < jsonArray.length(); i++) {
					prenomAmi = new HashMap<>();
					JSONObject ami = jsonArray.getJSONObject(i);
					prenomAmi.put("prenom", ami.getString("prenom"));
					prenomAmi.put("contacter", "Peta le !");

					idConversation.add(ami.getInt("idConversation"));
					loginAmi.add(ami.getString("login"));
					amis.add(prenomAmi);
				}

				ListAdapter listAdapter = new SimpleAdapter(FriendsActivity.this, amis, android.R.layout.simple_list_item_2, new String[]{"prenom", "contacter"}, new int[]{android.R.id.text1, android.R.id.text2});
				listView.setAdapter(listAdapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Intent intent = new Intent(FriendsActivity.this, ConversationActivity.class);
						intent.putExtra("idConversation", idConversation.get(position));
						intent.putExtra("loginReceiver", loginAmi.get(position));
						startActivity(intent);
						finish();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}