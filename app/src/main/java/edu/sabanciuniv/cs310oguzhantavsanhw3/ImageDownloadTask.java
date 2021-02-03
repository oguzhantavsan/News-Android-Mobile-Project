package edu.sabanciuniv.cs310oguzhantavsanhw3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import edu.sabanciuniv.cs310oguzhantavsanhw3.NewsItem;

public class ImageDownloadTask extends AsyncTask<NewsItem, Void, Bitmap> {

    ImageView imgView;

    public ImageDownloadTask(ImageView imgNews) {
        this.imgView = imgNews;
    }

    @Override
    protected Bitmap doInBackground(NewsItem... newsItems) {

        NewsItem current = newsItems[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(current.getImagePath());
            InputStream is = new BufferedInputStream(url.openStream());

            bitmap = BitmapFactory.decodeStream(is);
            current.setBitmap(bitmap);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        imgView.setImageBitmap(bitmap);

    }
}
