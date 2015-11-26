package com.happyhome.kkommanapall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

        import android.app.ProgressDialog;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        bindViewVariables();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        new RegisterUserTask().execute(name,email,password);

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void bindViewVariables(){
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
    }

    private class LoginStatus{
        public boolean success;
        public String IndividualName;
        public int IndividualId;
    }

    private class RegisterUserTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            String username = params[0];
            String email = params[1];
            String password = params[2];
            String uri = Constants.BASE_URL + "/createUserAndroid";
            LoginStatus st = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                JSONObject cred = new JSONObject();
                cred.put("username", username);
                cred.put("email", email);
                cred.put("password", password);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(cred.toString());
                wr.flush();
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                st = new Gson().fromJson(sb.toString(), LoginStatus.class);
            } catch (Exception ex) {
                Log.e(TAG, "Some Exception");
            }
            if(st.success){
                SharedPreferences.Editor editor = getSharedPreferences(Constants.USERDETAILS, MODE_PRIVATE).edit();
                Constants.Username = st.IndividualName;
                Constants.Userid = st.IndividualId;
                editor.putString("username", st.IndividualName);
                editor.putInt("userid", st.IndividualId);
                editor.commit();
            }
            return st.success;
        }//close doInBackground

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if(success){
                //navigate to Main Menu
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(i);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Sign up failed",
                        Toast.LENGTH_SHORT);

                toast.show();
                Log.v(TAG, "Incorrect username/password");
            }
        }//close onPostExecute
    }

}