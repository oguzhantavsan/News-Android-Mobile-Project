package edu.sabanciuniv.cs310oguzhantavsanhw3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterRecNew extends RecyclerView.Adapter<AdapterRecNew.NewViewHolder> {


    Context context;
    List<NewsItem> newsItems;
    RecNewsListener listener;
    NewViewHolder holder;

    public AdapterRecNew(Context context, List<NewsItem> newsItems, RecNewsListener listener) {
        this.context = context;
        this.newsItems = newsItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.news_row_layout, parent, false);

        holder = new NewViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, final int position) {

        holder.date.setText(new SimpleDateFormat("dd/MM/yyy").format(newsItems.get(position).getNewsDate()));
        holder.textTitle.setText(newsItems.get(position).getTitle());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.rowClicked(newsItems.get(position));
            }
        });

        if(newsItems.get(position).getBitmap()==null){

            new ImageDownloadTask(holder.imgNews).execute(newsItems.get(position));

        }
        else{
            holder.imgNews.setImageBitmap(newsItems.get(position).getBitmap());
        }


    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }


    public interface RecNewsListener{

        public void rowClicked(NewsItem selectedNews);

    }

    class NewViewHolder extends RecyclerView.ViewHolder{

        ImageView imgNews;
        TextView textTitle;
        TextView date;
        LinearLayout root;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNews = itemView.findViewById(R.id.imgnew);
            textTitle = itemView.findViewById(R.id.txtnewsdescription);
            date = itemView.findViewById(R.id.txtnewsdate);
            root = itemView.findViewById(R.id.container);
        }
    }
}
