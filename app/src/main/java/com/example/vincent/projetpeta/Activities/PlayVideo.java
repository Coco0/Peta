package com.example.vincent.projetpeta.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.R;

/**
 * @author Vincent
 */
public class PlayVideo extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_videos);
		VideoView videoView = (VideoView) findViewById(R.id.videoView);

		Bundle bundle = getIntent().getExtras();
		String filePath = " Chemin du fichier non trouvé bolosse";
		if (bundle != null)
			filePath = bundle.getString("path");

		Log.i("Path", filePath);

		String videoPath = Constantes.PETA_REP + filePath;
		Uri videoURI = Uri.parse(videoPath);
		Log.i("URI", videoURI.toString());

		videoView.setVideoURI(videoURI);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		videoView.start();
	}
}
