package com.domonion.vkphotos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class ItemDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    PhotoLoader.PhotoLoaderBinder binder = null;
    ServiceConnection scn = null;
    View rootView = null;
    private ImageData item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "itemDetailFragment onCreate");
        Log.d(LOG_TAG, "ServiceConnection started");
        scn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(LOG_TAG, "PhotoLoader binded");
                Log.d(LOG_TAG, Boolean.toString(service == null));
                binder = (PhotoLoader.PhotoLoaderBinder) service;
                Log.d(LOG_TAG, "YOU ARE HERE");
                binder.setCallback(new PhotoLoader.Listener() {
                    @Override
                    public void onSuccess(Bitmap img) {
                        Log.d(LOG_TAG, "onSuccess");
                        if (img == null) {
                            Log.d(LOG_TAG, "img is null");
                        }
                        if (rootView.findViewById(R.id.itemDetailImageView) == null) {
                            Log.d(LOG_TAG, "imgView not found");
                        }
                        ((ImageView) rootView.findViewById(R.id.itemDetailImageView)).setImageBitmap(img);
                    }
                });
                binder.setItem(item);
                binder.start();
                Log.d(LOG_TAG, "NOW HERE");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Log.d(LOG_TAG, "ServiceConnection ended");
        assert getArguments() != null;
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            item = getArguments().getParcelable(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_detail, container, false);

        ((TextView) rootView.findViewById(R.id.item_detail)).setText(item.description);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent photoLoaderIntent = new Intent(getActivity(), PhotoLoader.class);
        photoLoaderIntent.putExtra(ARG_ITEM_ID, item);
        getActivity().bindService(photoLoaderIntent, scn, Context.BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "intent tried");
        getActivity().startService(photoLoaderIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(scn);
    }
}
