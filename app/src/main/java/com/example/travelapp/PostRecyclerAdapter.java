package com.example.travelapp;

import static com.example.travelapp.MyApplication.getMyContext;
import static com.example.travelapp.model.Model.isOnline;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.databinding.PostListRowBinding;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;


class PostViewHolder extends RecyclerView.ViewHolder {

    TextView nameTv;
    TextView idTv;
    ImageView img;
    CheckBox cb;
    List<Post> data;
    PostListRowBinding binding;

    // create 1 post row
    public PostViewHolder(@NonNull View itemView, PostRecyclerAdapter.OnItemClickListener listener, List<Post> data) {
        super(itemView);
        this.data = data;

        // binding:
        nameTv = itemView.findViewById(R.id.postlistrow_name_tv);
        idTv = itemView.findViewById(R.id.postlistrow_id_tv);
        cb = itemView.findViewById(R.id.postlistrow_cb);
        img = itemView.findViewById(R.id.postlistrow_avatar_img);

        // click on star box:
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) cb.getTag();
                Post post = data.get(pos);  // save the post in line "pos"(int)
                post.cb = cb.isChecked();  // star == true
                Model.instance().saveStar(post.name);  // save star in firebase
            }
        });

        // send the number line (pos) item to adapter when click on post in the list
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition(); // get the number line of post
                listener.onItemClick(pos);
            }
        });
    }



    // bind the post to element in the fragment

    // binding post with the row in number "pos"
    public void bind(Post post, int pos) {
        nameTv.setText(post.name);

        // set the star box true/false:
        if(isOnline(getMyContext())) {

            // check in firebase the star box
            Model.instance().getAllStars(stars -> {
                if (stars.contains(post.name)) {
                    cb.setChecked(true); // full star
                } else {
                    cb.setChecked(false); // empty star
                }
            });
        }
        cb.setTag(pos);  // set in the line "pos"(int)

        // binding to the image post
        if(post.getAvatarUrl() == "" || post.getAvatarUrl().isEmpty())
            img.setImageResource(R.drawable.nophoto); // is default photo , user don't entry photo
        else
            Picasso.get().load(post.getAvatarUrl()).error(R.drawable.nophoto).into(img);

    }
}



    // create all the row of posts list
    public class PostRecyclerAdapter extends RecyclerView.Adapter<PostViewHolder>{
        OnItemClickListener listener;

    public static interface OnItemClickListener{
        void onItemClick(int pos);
    }

    LayoutInflater inflater;
    List<Post> data;

    // save the list of posts
    public void setData(List<Post> data){
        this.data=data;
        notifyDataSetChanged();  // update if have change in the list
    }

    // set inflater and data list
    public PostRecyclerAdapter(LayoutInflater inflater, List<Post> data){
        this.inflater = inflater;
        this.data = data;
    }

    // set listener
    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    // create 1 row of post
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_list_row,parent,false); // binding to 1 row of post
        return new PostViewHolder(view,listener, data);    // create new 1 row
    }


    // binding post with the row "pos"
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = data.get(position);    // get post in row "pos" os list
        holder.bind(post,position);    // binding post with the row
    }

    // number of posts
    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        return data.size();
    }

}

