package it.antedesk.popularmovies.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import it.antedesk.popularmovies.R;

/**
 * Created by Antedesk on 10/05/2018.
 */

public class ReviewDialog extends DialogFragment {

    public final static String AUTHOR = "author";
    public final static String CONTENT = "content";

    public ReviewDialog() {
    }

    public static ReviewDialog newInstance(String author, String content) {

        Bundle args = new Bundle();

        ReviewDialog fragment = new ReviewDialog();
        args.putString(AUTHOR, author);
        args.putString(CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_review, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView reviewerName = view.findViewById(R.id.reviewer_name_tv);
        TextView reviewContent = view.findViewById(R.id.review_content_tv);
        reviewerName.setText(getArguments().getString(AUTHOR, ""));
        reviewContent.setText(getArguments().getString(CONTENT, ""));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
