package edu.sabanciuniv.cs310oguzhantavsanhw3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsDetailActivity extends AppCompatActivity {

    List<NewsItem> data;
    ProgressDialog prgDialog;
    AdapterRecNew adp;
    TextView txtTitle;
    TextView txtDate;
    TextView txtDescription;
    ImageView imgNew;
    NewsItem selectNew;
    ActionBar currentBar;
    int id =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        txtTitle = findViewById(R.id.txtnewstitle);
        txtDate = findViewById(R.id.txtdate);
        txtDescription = findViewById(R.id.txtdescription);
        imgNew = findViewById(R.id.imgnews);
        data = new ArrayList<>();
        currentBar = getSupportActionBar();
        currentBar.setHomeButtonEnabled(true);
        currentBar.setDisplayHomeAsUpEnabled(true);
        currentBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_18dp);

        txtTitle.setText("");

        selectNew = (NewsItem)getIntent().getSerializableExtra("selectedNews");
        id = selectNew.getId();
        String strId = Integer.toString(id);
        GetNewsById tskbyid = new GetNewsById();
        tskbyid.execute("http://94.138.207.51:8080/NewsApp/service/news/getnewsbyid", strId);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            finish();
        }
        else if(item.getItemId() == R.id.mn_comment){
            Intent i = new Intent(NewsDetailActivity.this, CommentActivity.class);
            i.putExtra("newsId", id);
            startActivity(i);
        }
        return true;
    }

    class GetNewsById extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(NewsDetailActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please Wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            String id = strings[1];
            String urlStr = strings[0] + "/" + id;
            Log.i("Dev", urlStr);
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


                    JSONObject current = (JSONObject)arr.get(0);

                    long date = current.getLong("date");
                    Date objDate = new Date(date);
                    NewsItem item = new NewsItem(current.getInt("id"),
                            current.getString("title"),
                            current.getString("text"),
                            current.getString("image"),
                            objDate);


                    txtTitle.setText(current.getString("title"));
                    txtDescription.setText(current.getString("text"));
                    txtDate.setText(new SimpleDateFormat("dd/MM/yyy").format(item.getNewsDate()));

                    new ImageDownloadTask(imgNew).execute(item);

                }
                else{


                }
            } catch (JSONException e) {
                Log.e("Dev",e.getMessage());
            }

            prgDialog.dismiss();



        }

    }
}
