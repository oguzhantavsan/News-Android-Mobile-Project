package edu.sabanciuniv.cs310oguzhantavsanhw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class MainActivity extends AppCompatActivity implements AdapterRecNew.RecNewsListener {

    ProgressDialog prgDialog;
    ProgressDialog prgDialog2;
    RecyclerView newsRecView;
    List<NewsItem> data;
    List<NewsCategory> arry;
    AdapterRecNew adp;
    Spinner spNewsCategory;
    String[] categories;
    TextView txtDescription;
    TextView txtDate;
    ImageView imgView;
    Boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<>();
        arry = new ArrayList<>();
        newsRecView = findViewById(R.id.recviewnews);
        spNewsCategory = findViewById(R.id.spnewscategories);
        txtDate = findViewById(R.id.txtnewsdate);
        txtDescription = findViewById(R.id.txtnewsdescription);
        imgView = findViewById(R.id.imgnew);


        adp = new AdapterRecNew(this, data, new AdapterRecNew.RecNewsListener() {
            @Override
            public void rowClicked(NewsItem selectedNews) {
                Intent i = new Intent(MainActivity.this, NewsDetailActivity.class);
                i.putExtra("selectedNews", selectedNews);
                startActivity(i);
            }
        });

        newsRecView.setLayoutManager(new LinearLayoutManager(this));
        newsRecView.setAdapter(adp);
        GetNewsCategory ct = new GetNewsCategory();
        ct.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");

        GetAllNews tsk = new GetAllNews();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");


        spNewsCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index=0;
                String selectCategory = spNewsCategory.getSelectedItem().toString();
                for(int j=0;j<arry.size();j++){
                    if(arry.get(j).getName().equals(selectCategory))
                        index = j;
                }
                int catId = arry.get(index).getId();
                String strId = Integer.toString(catId);
                if(selectCategory=="All")
                {
                    GetAllNews tsk1 = new GetAllNews();
                    tsk1.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
                }
                else
                {
                    GetNewsByCategoryId tskbyid = new GetNewsByCategoryId();
                    tskbyid.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid", strId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class GetAllNews extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(MainActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please Wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlStr = strings[0];
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

                        long date = current.getLong("date");
                        Date objDate = new Date(date);

                        NewsItem item = new NewsItem(current.getInt("id"),
                                current.getString("title"),
                                current.getString("text"),
                                current.getString("image"),
                                objDate);

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
    class GetNewsCategory extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            prgDialog2 = new ProgressDialog(MainActivity.this);
            prgDialog2.setTitle("Loading");
            prgDialog2.setMessage("Please Wait...");
            prgDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog2.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String urlStr = strings[0];
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

            arry.clear();
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("serviceMessageCode")==1){

                    JSONArray arr = obj.getJSONArray("items");

                    NewsCategory all = new NewsCategory("All", 0);
                    arry.add(all);

                    for(int i = 0; i<arr.length();i++){

                        JSONObject current = (JSONObject)arr.get(i);
                        NewsCategory cat = new NewsCategory(current.getString("name"),
                                current.getInt("id"));

                        arry.add(cat);
                    }
                    categories = new String[arry.size()];
                    for(int i=0;i<arry.size();i++){


                        categories[i] = arry.get(i).getName();

                    }

                    ArrayAdapter<String> adp =
                            new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, categories);

                    spNewsCategory.setAdapter(adp);
                }
                else{


                }
                check=true;
            } catch (JSONException e) {
                Log.e("Dev",e.getMessage());
            }

            prgDialog2.dismiss();


        }
    }

    class GetNewsByCategoryId extends AsyncTask<String, Void, String>{


        @Override
        protected void onPreExecute() {
            prgDialog2 = new ProgressDialog(MainActivity.this);
            prgDialog2.setTitle("Loading");
            prgDialog2.setMessage("Please Wait...");
            prgDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog2.show();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prgDialog2.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {

            String id = strings[1];
            String urlStr = strings[0] + "/" + id;
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

                        long date = current.getLong("date");
                        Date objDate = new Date(date);

                        NewsItem item = new NewsItem(current.getInt("id"),
                                current.getString("title"),
                                current.getString("text"),
                                current.getString("image"),
                                objDate);

                        data.add(item);

                    }

                }
                else{


                }

                adp.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Dev",e.getMessage());
            }


        }


    }

    @Override
    public void rowClicked(NewsItem selectedNews){

    }
}
