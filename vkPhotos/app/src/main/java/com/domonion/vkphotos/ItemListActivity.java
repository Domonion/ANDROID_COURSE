package com.domonion.vkphotos;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class ItemListActivity extends AppCompatActivity {

    private boolean isTwoPane;
    final String DATA_KEY = "com.domonion.vkPhotos.DATA_KEY";
    boolean binded = false;
    ServiceConnection scn = null;
    ImageData[] data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        if (findViewById(R.id.item_detail_container) != null) {
            isTwoPane = true;
        }
        if (savedInstanceState == null || savedInstanceState.getParcelableArray(DATA_KEY) == null) {
            scn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ListLoader.MyBinder binder = ((ListLoader.MyBinder) service);
                    binder.setCallback(new MyAsyncTask.Listener() {
                        @Override
                        public void onSuccess(ImageData[] data) {
                            setupRecyclerView(data);
                        }
                    });
                    binded = true;
                    binder.start();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    binded = false;
                }
            };
            Intent listLoaderIntent = new Intent(this, ListLoader.class);
            bindService(listLoaderIntent, scn, BIND_AUTO_CREATE);
            startService(listLoaderIntent);
        } else {
            setupRecyclerView((ImageData[]) savedInstanceState.getParcelableArray(DATA_KEY));
            Log.d(LOG_TAG, "saves retored");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "LOG TAR OGAR!");
        if (data != null) {
            outState.putParcelableArray(DATA_KEY, data);
        }
    }

    private void setupRecyclerView(ImageData[] data) {
        this.data = data;
        RecyclerView recyclerView = findViewById(R.id.item_list);
        recyclerView.setAdapter(new MyAdapter(this, data, isTwoPane));
    }

    public static class MyAdapter
            extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private final ItemListActivity mParentActivity;
        private final ImageData[] mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageData item = (ImageData) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(ItemDetailFragment.ARG_ITEM_ID, item);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item);

                    context.startActivity(intent);
                }
            }
        };

        MyAdapter(ItemListActivity parent,
                  ImageData[] data,
                  boolean twoPane) {
            mValues = data;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.mIdView.setText(((Integer) (position)).toString());
            holder.mContentView.setText(mValues[position].description);

            holder.itemView.setTag(mValues[position]);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            MyViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.content);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binded)
            unbindService(scn);
    }
}
