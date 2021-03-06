package com.liananse.phototag;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liananse.ptrefreshlayout.PTRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private PTRefreshLayout mPTRefreshLayout;
    private RecyclerView mRecyclerView;

    private List<List<TagModel>> mTagList;

    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i = 0;
        mPTRefreshLayout = (PTRefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Random r = new Random();
        mTagList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mTagList.add(getTagModelList(i+"", (r.nextInt(5) + 1)));
        }
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ViewHolder holder = new ViewHolder(v);
                holder.tagImageView = (TagContainer2) v.findViewById(R.id.tag_image_view);
                holder.tagImageView.setTag(i + "");
                i++;
                return holder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ViewHolder) holder).tagImageView.setTagList(mTagList.get(position));

                Log.d("Tag", "onBindViewHolder main " + ((ViewHolder) holder).tagImageView.getTag() + " p " + position);
            }

            @Override
            public int getItemCount() {
                return 20;
            }

            @Override
            public void setHasStableIds(boolean hasStableIds) {
                super.setHasStableIds(hasStableIds);
            }
        });

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int padding = (int) 10;
                outRect.set(padding, padding, padding, padding);
            }
        });
    }


    private List<TagModel> getTagModelList(String tagOn, int tagCount) {
        List<TagModel> tagModelList = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < tagCount; i++) {
            TagModel tagModel = new TagModel();
            tagModel.type = TagModel.Type.NORMAL;
            tagModel.direction = (random.nextInt(2) % 2) == 0 ? TagModel.Direction.LEFT : TagModel.Direction.RIGHT;
            tagModel.name = tagOn + " " + i + (tagModel.direction == TagModel.Direction.LEFT ? " left" : " right");
            tagModel.position = new Point(random.nextInt(600) + 60, random.nextInt(480) + 60);
            tagModelList.add(tagModel);
        }

        return tagModelList;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        TagContainer2 tagImageView;

        public ViewHolder(View v) {
            super(v);
        }

    }
}
