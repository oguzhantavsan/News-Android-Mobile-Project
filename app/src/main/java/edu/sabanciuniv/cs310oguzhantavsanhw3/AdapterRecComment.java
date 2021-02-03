package edu.sabanciuniv.cs310oguzhantavsanhw3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRecComment extends RecyclerView.Adapter<AdapterRecComment.ComViewHolder> {


    Context context;
    List<CommentItems> comItems;
    RecComListener listener;
    ComViewHolder holder;

    public AdapterRecComment(Context context, List<CommentItems> comItems, RecComListener listener) {
        this.context = context;
        this.comItems = comItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.comments_row_layout, parent, false);

        holder = new ComViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComViewHolder holder, final int position) {
        holder.txtName.setText(comItems.get(position).getName());
        holder.txtComment.setText(comItems.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return comItems.size();
    }


    public interface RecComListener{
    }

    class ComViewHolder extends RecyclerView.ViewHolder{

        TextView txtName;
        TextView txtComment;
        LinearLayout root;
        public ComViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txtcomment);
            txtName = itemView.findViewById(R.id.txtname);
            root = itemView.findViewById(R.id.container);
        }
    }
}
