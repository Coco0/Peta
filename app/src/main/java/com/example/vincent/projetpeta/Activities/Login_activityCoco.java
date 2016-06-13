package com.example.vincent.projetpeta.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vincent.projetpeta.Constantes;
import com.example.vincent.projetpeta.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

import static android.Manifest.permission.READ_CONTACTS;

public class Login_activityCoco extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int REQUEST_READ_CONTACTS = 0;
	// Sevice connexion
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView loginView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("sharedpref", "" + getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1));
		if (getSharedPreferences(Constantes.INFO_USER, 0).getInt(Constantes.ID_USER, -1) != -1) {
			Intent intent = new Intent(Login_activityCoco.this, ListUserActivity.class);
			startActivity(intent);
		} else {
			setContentView(R.layout.login_activity);
			// Set up the login form.
			loginView = (AutoCompleteTextView) findViewById(R.id.login_register);
			populateAutoComplete();

			mPasswordView = (EditText) findViewById(R.id.password_register);
			mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
					if (id == R.id.name_register || id == EditorInfo.IME_NULL) {
						attemptLogin();
						return true;
					}
					return false;
				}
			});

			Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
			if (mEmailSignInButton != null) {
				mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
//						Intent intent = new Intent(Login_activityCoco.this, ListUserActivity.class);
//						startActivity(intent);

					}
				});
			}

			Button mRegisterButton = (Button) findViewById(R.id.btnLinkToRegisterScreen);
			if (mRegisterButton != null) {
				mRegisterButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(Login_activityCoco.this, RegisterActivity.class);
						startActivity(intent);

					}
				});
			}

			mLoginFormView = findViewById(R.id.login_form);
			mProgressView = findViewById(R.id.login_progress);
		}
	}

	private void populateAutoComplete() {
		if (!mayRequestContacts()) {
			return;
		}

		getLoaderManager().initLoader(0, null, this);
	}

	private boolean mayRequestContacts() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(loginView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener() {
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v) {
							requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
						}
					});
		} else {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				populateAutoComplete();
			}
		}
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		loginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = loginView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			loginView.setError(getString(R.string.error_field_required));
			focusView = loginView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask(email, password);
			mAuthTask.execute((Void) null);
		}
	}

	private boolean isPasswordValid(String password) {
		//TODO: Replace this with your own logic
		return password.length() > 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(Login_activityCoco.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		loginView.setAdapter(adapter);
	}


	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String login;
		private final String password;
		private int idUser = -1;

		UserLoginTask(String login, String password) {
			this.login = login;
			this.password = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean loginSuccess = false;
			try {
				JSONObject json = new JSONObject();
				json.put("login", login);
				json.put("password", password);

				HttpClient client = HttpClientBuilder.create().build();
				HttpPost post = new HttpPost(Constantes.URL_LOGIN_SERVICE);
				StringEntity se = new StringEntity("json=" + json.toString());
				post.addHeader("content-type", "application/x-www-form-urlencoded");
				post.setEntity(se);

				HttpResponse response = client.execute(post);


				String resFromServer = EntityUtils.toString(response.getEntity());
				Log.i("Response from server", resFromServer);
				JSONObject jsonResponse = new JSONObject(resFromServer);
				loginSuccess = jsonResponse.getBoolean("connected");
				if (loginSuccess)
					this.idUser = jsonResponse.getInt("idUser");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return loginSuccess;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Context context = getApplicationContext();
				CharSequence text = "Bienvenue " + this.login + " !";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();

				SharedPreferences.Editor editor = getSharedPreferences(Constantes.INFO_USER, 0).edit();
				editor.putInt(Constantes.ID_USER, this.idUser);
				editor.putString(Constantes.LOGIN, this.login);
				editor.apply();
				Log.i("Login", "finished");
				Intent intent = new Intent(Login_activityCoco.this, ListUserActivity.class);
				startActivity(intent);

			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}



