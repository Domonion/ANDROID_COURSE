package com.domonion.vkphotos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class ItemDetailFragment extends Fragment implements View.OnClickListener {
    public static final String ITEM_ID = "item_id";
    View rootView = null;
    Button favButton = null;
    boolean added = false;
    private ImageData item;
    DBHelper helper = null;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "itemDetailFragment onCreate");
        assert getArguments() != null;
        if (getArguments().containsKey(ITEM_ID)) {
            item = getArguments().getParcelable(ITEM_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_detail, container, false);

        ((TextView) rootView.findViewById(R.id.item_detail)).setText(item.getDescription());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        helper = App.getDB();
        favButton = rootView.findViewById(R.id.favButton);
        Log.d(LOG_TAG, "checking item in DB");
        Log.d(LOG_TAG, "item: " + item.description + " " + item.URL);
        if (!helper.check(item.description, item.URL)) {
            favButton.setText("Add");
            added = false;
        } else {
            favButton.setText("Delete");
            added = true;
        }
        Log.d(LOG_TAG, "item checked");
        favButton.setOnClickListener(this);
        Picasso.get().load(item.URL).into((ImageView) rootView.findViewById(R.id.itemDetailImageView));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "favButton clicked");
        if (added) {
            favButton.setText("Add");
            helper.delete(item.description, item.URL);
        } else {
            favButton.setText("Delete");
            helper.add(item.description, item.URL);
        }
        added = !added;
    }
}
