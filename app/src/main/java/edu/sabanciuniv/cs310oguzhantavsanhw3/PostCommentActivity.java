package edu.sabanciuniv.cs310oguzhantavsanhw3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostCommentActivity extends AppCompatActivity {

    EditText editName;
    EditText editComment;
    Button btnPost;
    ProgressDialog prgDialog;
    int newsId = 0;
    String strId = " ";
    ActionBar currentBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        editComment = findViewById(R.id.editcomment);
        editName = findViewById(R.id.editname);
        btnPost = findViewById(R.id.btnpostcomment);
        currentBar = getSupportActionBar();
        currentBar.setHomeButtonEnabled(true);
        currentBar.setDisplayHomeAsUpEnabled(true);
        currentBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_18dp);

        newsId = (int)getIntent().getSerializableExtra("newsId");
        strId = Integer.toString(newsId);




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            finish();
        }
        return true;
    }

    public void taskCallClicked(View v){

        PostCommentTask tsk = new PostCommentTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment",
                editName.getText().toString(),
                editComment.getText().toString(),
                strId);

    }

    class PostCommentTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(PostCommentActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please Wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder strbuilder = new StringBuilder();
            String urlstr = strings[0];
            String fullname = strings[1];
            String comment = strings[2];
            String id = strings[3];
            JSONObject obj = new JSONObject();
            try{
                obj.put("name", fullname);
                obj.put("text", comment);
                obj.put("news_id", id);
            } catch (JSONException e){
                e.printStackTrace();
            }

            try {
                URL url = new URL(urlstr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(obj.toString());
                String message = conn.getResponseMessage();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    Intent i = new Intent(PostCommentActivity.this, CommentActivity.class);
                    //i.putExtra("newsId", strId);
                    startActivity(i);
                }
                else{
                    Toast.makeText(PostCommentActivity.this,message, Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){

                e.printStackTrace();
            }

            return strbuilder.toString();
        }
        @Override
        protected void onPostExecute(String s) {

            prgDialog.dismiss();
            finish();
        }
    }
}
