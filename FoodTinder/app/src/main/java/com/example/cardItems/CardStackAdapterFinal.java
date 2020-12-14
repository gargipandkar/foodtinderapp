package com.example.cardItems;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodtinder.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CardStackAdapterFinal extends RecyclerView.Adapter<CardStackAdapterFinal.ViewHolder> {

    private ArrayList<String> listRestName, listRestAddr;
    HashMap<String, ArrayList<String>> listRestPhotos;
    ArrayList<String> itemPhotos;
    TextView noImageLabel;


    public CardStackAdapterFinal(ArrayList<String> listRestName, ArrayList<String> listRestAddr, HashMap<String, ArrayList<String>> listRestPhotos) {
        this.listRestName = listRestName;
        this.listRestAddr = listRestAddr;
        this.listRestPhotos = listRestPhotos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.swipe_card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String rest = (String) listRestName.get(position);
        if (listRestPhotos.containsKey(rest)){
            itemPhotos = listRestPhotos.get(rest);
            Log.i("check photolist: ", itemPhotos.toString());
//            holder.image.setImageBitmap(new DownloadImageTask().doInBackground(itemPhotos.toString()));
            new LoadImage(holder.image).execute(itemPhotos.get(0));
        } else {
//            holder.image.setImageResource(R.drawable.logo_temporary);
            holder.noImageLabel.setText("No image available");
            holder.name.setTextColor(Color.parseColor("#000000"));
            holder.addr.setTextColor(Color.parseColor("#000000"));
        }
        holder.name.setText(listRestName.get(position));
        holder.addr.setText(listRestAddr.get(position));
    }

    @Override
    public int getItemCount() {
        return listRestName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, addr, noImageLabel;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            addr = itemView.findViewById(R.id.item_addr);
            noImageLabel = itemView.findViewById(R.id.no_image_label);

        }

    }

}




class LoadImage extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    public LoadImage(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return downloadBitmap(params[0]);
        } catch (Exception e) {
            // log error
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.logo_temporary);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                return null;
//            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

