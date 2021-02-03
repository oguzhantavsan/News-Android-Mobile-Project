package edu.sabanciuniv.cs310oguzhantavsanhw3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements AdapterRecComment.RecComListener {

    ProgressDialog prgDialog;
    RecyclerView comRecView;
    List<CommentItems> data;
    AdapterRecComment adp;
    TextView txtName;
    TextView txtComment;
    int id = 0;
    ActionBar currentBar;
    String newsid=" ";
    String strId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        txtComment = findViewById(R.id.txtcomment);
        txtName = findViewById(R.id.txtname);
        comRecView =  findViewById(R.id.recviewcom);
        data = new ArrayList<>();
        currentBar = getSupportActionBar();
        currentBar.setHomeButtonEnabled(true);
        currentBar.setDisplayHomeAsUpEnabled(true);
        currentBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_18dp);

        adp =  new AdapterRecComment(this, data, new AdapterRecComment.RecComListener() {
        });

        comRecView.setLayoutManager(new LinearLayoutManager(this));
        comRecView.setAdapter(adp);

        id = (int)getIntent().getSerializableExtra("newsId");
        strId = Integer.toString(id);
        GetCommentsByNewsId coms = new GetCommentsByNewsId();
        coms.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid", strId);



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GetCommentsByNewsId coms = new GetCommentsByNewsId();
        coms.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid", strId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comm_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            finish();
        }
        else if(item.getItemId()== R.id.add_com){

            Log.i("DEV", "I AM HERE-----");
            Intent i = new Intent(CommentActivity.this, PostCommentActivity.class);
            i.putExtra("newsId", id);
            startActivity(i);

        }
        return true;
    }

    class GetCommentsByNewsId extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(CommentActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please Wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String comId = strings[1];
            String urlStr = strings[0] + "/" + comId;
            StringBuilder buffer = new StringBuilder();
            URL url = null;
            try {
                url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while ((line = reader.readLine())!=null){

                    buffer.append(line);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();

        }

        @Override
        protected void onPostExecute(String s) {

            data.clear();
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("serviceMessageCode")==1){

                    JSONArray arr = obj.getJSONArray("items");

                    for(int i = 0; i<arr.length();i++){

                        JSONObject current = (JSONObject)arr.get(i);

                        CommentItems item = new CommentItems(current.getString("name"),
                                current.getString("text"));

                        data.add(item);
                    }

                }
                else{


                }

                adp.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Dev",e.getMessage());
            }
            prgDialog.dismiss();
        }

    }
}
