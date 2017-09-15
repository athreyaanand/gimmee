package xyz.tracestudios.gimmee;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class GridAdapterUpload  extends RecyclerView.Adapter<GridAdapterUpload.ViewHolder> {
    private ArrayList<String> mItems;
    private  MyClickListener myClickListener;
    private Context mContext;
    private String TAG="GridAdapter";
    //private ProgressBar mProgressBar;

    public GridAdapterUpload(ArrayList<String> items,Context mContext) {
        this.mItems = items;
        this.mContext= mContext;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.mProgbar.setVisibility(View.VISIBLE);
        viewHolder.mProgbar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(mContext,
                R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);

        Picasso.with(mContext)
                .load(mItems.get(position))
                .into(viewHolder.imgThumbnail, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.mProgbar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError() {
                        viewHolder.mProgbar.setVisibility(View.INVISIBLE);
                    }
                });
        viewHolder.imgThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgThumbnail;
        public ProgressBar mProgbar;
        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            mProgbar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            // itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            myClickListener.onItemClick(getLayoutPosition(), v);
        }
    }
    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


}
