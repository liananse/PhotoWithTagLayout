package com.liananse.phototag;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPTRefreshLayout = (PTRefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ViewHolder holder = new ViewHolder(v);
                holder.tagImageView = (TagImageView) v.findViewById(R.id.tag_image_view);

                return holder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Random random = new Random();
                // taglist不能在这里随机
                ((ViewHolder) holder).tagImageView.setTagList(getTagModelList(position + "", random.nextInt(5) + 1));
            }

            @Override
            public int getItemCount() {
                return 30;
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

        TagImageView tagImageView;

        public ViewHolder(View v) {
            super(v);
        }

    }
}
