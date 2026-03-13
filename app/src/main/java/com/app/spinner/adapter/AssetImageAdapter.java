package com.app.spinner.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spinner.R;
import com.app.spinner.util.OnItemClickListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class AssetImageAdapter extends RecyclerView.Adapter<AssetImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> imagePaths;
    private OnItemClickListener listener;

    public AssetImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imagePath = imagePaths.get(position);
        Uri assetUri = Uri.parse(holder.imagePath);
        Glide.with(context).load(assetUri).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });

        try {
            if (holder.imagePath.contains("s_0.png")) {
                holder.txtDrawNew.setVisibility(VISIBLE);
                return;
            }
        } catch (Exception e) {
        }
        holder.txtDrawNew.setVisibility(GONE);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        String imagePath;
        ImageView imageView;
        TextView txtDrawNew;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_grid_spinner_img);
            txtDrawNew = itemView.findViewById(R.id.item_grid_spinner_txt);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}