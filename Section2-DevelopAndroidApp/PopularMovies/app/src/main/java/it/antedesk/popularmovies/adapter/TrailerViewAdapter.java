package it.antedesk.popularmovies.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

import it.antedesk.popularmovies.R;
import it.antedesk.popularmovies.model.Trailer;

import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.YOUTUBE_API_KEY;

/**
 * Created by Antedesk on 25/04/2018.
 */

public class TrailerViewAdapter extends RecyclerView.Adapter<TrailerViewAdapter.TrailerAdapterViewHolder>{

    private List<Trailer> trailersList;
    private Context parentContex;
    private int selectedPosition = 0;
    private final String TAG = TrailerViewAdapter.class.getName();
    private final TrailerViewAdapter.TrailerViewAdapterOnClickHandler mClickHandler;

    public TrailerViewAdapter(TrailerViewAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public interface TrailerViewAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer);
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContex = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerViewAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        final int currentPosition = position;
//        if (selectedPosition == currentPosition) {
//            holder.mTrailerCardView.setCardBackgroundColor(ContextCompat.getColor(parentContex, R.color.red_netflix));
//        } else {
//            holder.mTrailerCardView.setCardBackgroundColor(ContextCompat.getColor(parentContex, R.color.grayWhite));
//        }

        holder.mYouTubeThumbnailView.initialize(YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(trailersList.get(currentPosition).getKey());

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        Log.e(TAG, "Youtube Thumbnail Error");
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(TAG, "Youtube Initialization Failure");
            }
        });

    }

    @Override
    public int getItemCount() {
        if (trailersList.isEmpty()) return 0;
        return trailersList.size();
    }


    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public YouTubeThumbnailView mYouTubeThumbnailView;
        public CardView mTrailerCardView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerCardView = itemView.findViewById(R.id.trailer_cv);
            mYouTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectedPosition = getAdapterPosition(); //updating the selected position
            Log.d(TAG, "new position: "+selectedPosition);
            Trailer selectedTrailer = trailersList.get(selectedPosition);
            mClickHandler.onClick(selectedTrailer);
        }
    }
    public void setTrailersData(List<Trailer> trailersData) {
        trailersList = trailersData;
        notifyDataSetChanged();
    }
}
