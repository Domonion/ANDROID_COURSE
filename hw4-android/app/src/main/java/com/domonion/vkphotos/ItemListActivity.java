package com.domonion.vkphotos;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.domonion.vkphotos.Constants.LOG_TAG;
import static java.lang.Math.min;

public class ItemListActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean mTwoPane;
    String dataKey = "com.domonion.vkPhotos.dataKey";
    boolean binded = false;
    ServiceConnection scn = null;
    ImageData[] data = null;
    Button serachButton = null;
    Button showFavButton = null;
    EditText searchText = null;
    DBHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        helper = App.getDB();
        showFavButton = findViewById(R.id.showFavButton);
        Log.d(LOG_TAG, "showFavButton found");
        showFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "CLICKED");
                setupRecyclerView(helper.getData());
            }
        });
        serachButton = findViewById(R.id.searchButton);
        searchText = findViewById(R.id.searchText);
        serachButton.setOnClickListener(this);
        if (savedInstanceState != null && savedInstanceState.getParcelableArray(dataKey) != null) {
            setupRecyclerView((ImageData[]) savedInstanceState.getParcelableArray(dataKey));
            Log.d(LOG_TAG, "saves retored");
        }
    }

    @Override
    public void onClick(View v) {
        Call<JsonNode> req = App.getApi().getData(searchText.getText().toString(), 100, "5.92", "169a4327169a4327169a43272716f26e1b1169a169a43274adf06531ceabf9ce993e0d7");
        req.enqueue(new Callback<JsonNode>() {
            @Override
            public void onResponse(@NotNull Call<JsonNode> call, @NotNull Response<JsonNode> response) {
                assert response.body() != null;
                int countN = Constants.listN;
                ImageData[] data = new ImageData[countN];
                JsonNode root = response.body();
                JsonNode items = root.path("response").path("items");
                for (int i = 0; i < countN; i++) {
                    JsonNode images = items.path(i).path("sizes");
                    JsonNode best = items.path(i).path("sizes").path(0);
                    for (JsonNode id : images)
                        if (id.path("width").asInt() * id.path("height").asInt() >
                                best.path("width").asInt() * best.path("height").asInt())
                            best = id;
                    data[i] = new ImageData(i,
                            best.path("url").asText(),
                            items.path(i).path("text").asText().substring(0, min(40, items.path(i).path("text").asText().length())));
                }
                setupRecyclerView(data);
            }

            @Override
            public void onFailure(@NotNull Call<JsonNode> call, @NotNull Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "LOG TAR OGAR!");
        if (data != null) {
            outState.putParcelableArray(dataKey, data);
        }
    }

    private void setupRecyclerView(ImageData[] data) {
        this.data = data;
        RecyclerView recyclerView = findViewById(R.id.item_list);
        recyclerView.setAdapter(new MyAdapter(this, data, mTwoPane));
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
                    arguments.putParcelable(ItemDetailFragment.ITEM_ID, item);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ITEM_ID, item);

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

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.mIdView.setText(Integer.toString(position));
            holder.mContentView.setText(mValues[position].getDescription());

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
