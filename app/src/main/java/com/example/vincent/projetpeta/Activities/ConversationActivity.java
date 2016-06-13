package com.example.vincent.projetpeta.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.R;
import com.example.vincent.projetpeta.VideoFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ConversationActivity extends AppCompatActivity {
	private static final int TAKE_VIDEO_REQUEST = 1;
	private ProgressDialog dialog;
	private List<VideoFile> videoFilesList;
	protected SwipeRefreshLayout swipeRefreshLayout;
	private ConversationTransmitter conversationTransmitter;
	private int idUserClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.videoFilesList = new ArrayList<>();

		Bundle bundle = getIntent().getExtras();
		this.idUserClicked = -1;
		if (bundle != null)
			this.idUserClicked = bundle.getInt("idUserClicked");

		conversationTransmitter = new ConversationTransmitter();
		conversationTransmitter.execute(this.idUserClicked);
	}

	private void updateRecyclerUI() {
		setContentView(R.layout.activity_conversation);
		RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
		if (rv != null) {
			rv.setHasFixedSize(true);
			LinearLayoutManager llm = new LinearLayoutManager(ConversationActivity.this);
			rv.setLayoutManager(llm);
			RVAdapter adapter = new RVAdapter(videoFilesList);
			rv.setAdapter(adapter);
		}
		FloatingActionButton newVideoFAB = (FloatingActionButton) findViewById(R.id.newVideoFAB);
		if (newVideoFAB != null) {
			newVideoFAB.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					if (takeVideoIntent.resolveActivity(getPackageManager()) != null)
						startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
				}
			});
		}
		this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		if (swipeRefreshLayout != null) {
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					conversationTransmitter = new ConversationTransmitter();
					conversationTransmitter.execute(idUserClicked);
				}
			});
		}
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_VIDEO_REQUEST)
			if (resultCode == RESULT_OK) {
				final Uri videoUri = data.getData();
				dialog = ProgressDialog.show(ConversationActivity.this, "", "Uploading file...", true);
				new Thread(new Runnable() {
					public void run() {
						uploadFile(videoUri);
					}
				}).start();
//				dialog.hide();
			}
	}

	public int uploadFile2(Uri uriFileToSend) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(Constantes.URL_UPLOAD);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		FileBody fileBody = null;
		try {
			fileBody = new FileBody(ConversationActivity.createFileFromInputStream(getContentResolver().openInputStream(uriFileToSend),
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/tmpVideoFile.mp4"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		System.out.println("URI " + uriFileToSend);
//		fileBody = new FileBody(new File(uriFileToSend.getEncodedPath()));
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (fileBody == null)
			Log.i("FileBody", "est null");
		builder.addPart("uploaded_file", fileBody);
		builder.addTextBody("idSender", "" + getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1));
		builder.addTextBody("idReceiver", this.idUserClicked + "");

		HttpEntity entity = builder.build();

		request.setEntity(entity);
		try {
			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			Log.i("Code réponse du serveur", "" + responseCode);
			Log.i("Réponse du serveur", EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int uploadFile(Uri uriFileToSend) {
		String[] perms = {"android.permission.INTERNET, android.permission.ACCESS_NETWORK_STATE"};
		requestPermissions(perms, 200);

		int serverResponseCode = -1;
		HttpURLConnection conn;
		DataOutputStream dos;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024 * 1024;
		String uploadFileName = "tmp.mp4";

		try {
			// open a URL connection to the Servlet
			InputStream fileInputStream = getContentResolver().openInputStream(uriFileToSend);
			URL url = new URL(Constantes.URL_UPLOAD);

			// Open a HTTP  connection to  the URL
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", uploadFileName);
//			conn.setRequestProperty("tmp_name", "temporaire.tmp");

			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + uploadFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of  maximum size
			if (fileInputStream != null) {
				bytesAvailable = fileInputStream.available();


				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);

				// On met les données du JSON
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("idReceiver", this.idUserClicked);
				jsonObject.put("idSender", getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1));
				dos.writeBytes(twoHyphens + boundary);
				dos.writeBytes(lineEnd);
				dos.writeBytes("Content-Type: application/json");
				dos.writeBytes(lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"data\"");
				dos.writeBytes(lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes(jsonObject.toString());
				dos.writeBytes(lineEnd);

				// La fin du paquet HTTP
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {
					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
					StringBuilder sb = new StringBuilder();
					String output;
					while ((output = br.readLine()) != null)
						sb.append(output);
					Log.i("Server response", sb.toString());

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(ConversationActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
						}
					});

					ConversationTransmitter conversationTransmitter = new ConversationTransmitter();
					conversationTransmitter.execute(this.idUserClicked);
				}

				//close the streams //
				fileInputStream.close();
			}
			dos.flush();
			dos.close();

		} catch (MalformedURLException ex) {
			dialog.dismiss();
			ex.printStackTrace();
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(ConversationActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
				}
			});
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {
			dialog.dismiss();
			e.printStackTrace();
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(ConversationActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
				}
			});
			Log.e("Upload file to serv ex", "Exception : " + e.getMessage(), e);
		}
		dialog.dismiss();
		return serverResponseCode;
	}

	private static File createFileFromInputStream(InputStream inputStream, String fileName) {

		try {
			File f = new File(fileName);
			f.setWritable(true, false);
			OutputStream outputStream = new FileOutputStream(f);
			byte buffer[] = new byte[1024];
			int length;

			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			outputStream.close();
			inputStream.close();

			return f;
		} catch (IOException e) {
			System.out.println("error in creating a file");
			e.printStackTrace();
		}

		return null;
	}

	public class ConversationTransmitter extends AsyncTask<Integer, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Integer... params) {
			JSONObject json = new JSONObject();
			JSONArray jsonResponse = null;
			try {
				json.put("idUser", getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1));
				json.put("idReceiver", params[0]);
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

				HttpPost post = new HttpPost(Constantes.IP + "/peta/services/getConversation.php");
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
				videoFilesList = new ArrayList<>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject file = jsonArray.getJSONObject(i);
					videoFilesList.add(new VideoFile(Constantes.CONVERSATIONS_REP + file.getString("pathFile"), file.getString("date"), file.getInt("idSender"), R.drawable.play_video));
				}

				updateRecyclerUI();

				if (swipeRefreshLayout.isRefreshing())
					swipeRefreshLayout.setRefreshing(false);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class RVAdapter extends RecyclerView.Adapter<RVAdapter.VideoViewHolder> {
		private List<VideoFile> videoFiles;

		public RVAdapter(List<VideoFile> videoFiles) {
			this.videoFiles = videoFiles;
		}

		public class VideoViewHolder extends RecyclerView.ViewHolder {
			CardView cv;
			//			TextView videoName;
//			TextView videoDate;
			ImageView videoMiniature;

			VideoViewHolder(View itemView) {
				super(itemView);
				cv = (CardView) itemView.findViewById(R.id.cv);
				videoMiniature = (ImageView) itemView.findViewById(R.id.video_image);
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ConversationActivity.this, PlayVideo.class);
						intent.putExtra("path", videoFiles.get(getLayoutPosition()).getName());
						startActivity(intent);
					}
				});
			}
		}

		@Override
		public int getItemCount() {
			return videoFiles.size();
		}

		@Override
		public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
			return new VideoViewHolder(v);
		}

		@Override
		public void onBindViewHolder(VideoViewHolder personViewHolder, int i) {
			personViewHolder.videoMiniature.setImageResource(videoFiles.get(i).getIdMiniature());
		}

		@Override
		public void onAttachedToRecyclerView(RecyclerView recyclerView) {
			super.onAttachedToRecyclerView(recyclerView);
		}
	}
}
