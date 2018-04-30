package it.antedesk.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.antedesk.popularmovies.R;
import it.antedesk.popularmovies.model.Review;

/**
 * Created by Antedesk on 24/04/2018.
 *
 */

public class ReviewViewAdapter extends RecyclerView.Adapter<ReviewViewAdapter.ReviewAdapterViewHolder> {

    private List<Review> reviewsList;
    private Context parentContex;
    private final ReviewViewAdapter.ReviewViewAdapterOnClickHandler mClickHandler;

    public ReviewViewAdapter(ReviewViewAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public interface ReviewViewAdapterOnClickHandler {
        void onClick(Review selectedReview);
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContex = parent.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(parentContex);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewViewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.mReviewerAuthor.setText(review.getAuthor());
        String preview = review.getContent();
        if(preview.length()>150)
            preview=preview.substring(0,100);
        preview += " ... "+parentContex.getString(R.string.preview);
        holder.mReviewContent.setText(preview);
    }

    @Override
    public int getItemCount() {
        if (reviewsList ==null || reviewsList.isEmpty()) return 0;
        return reviewsList.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.reviewer_name_tv) TextView mReviewerAuthor;
        @BindView(R.id.review_content_tv) TextView mReviewContent;

        // Using view.findViewById to get a reference to the TextViews
        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review selectedReview = reviewsList.get(adapterPosition);
            mClickHandler.onClick(selectedReview);
        }
    }
    public void setReviewsData(List<Review> reviewsData) {
        reviewsList = reviewsData;
        notifyDataSetChanged();
    }
}
