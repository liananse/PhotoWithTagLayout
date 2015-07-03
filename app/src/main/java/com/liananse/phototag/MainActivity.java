package com.liananse.phototag;

import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.liananse.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PhotoViewAttacher.OnMatrixChangedListener {

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    private TagView mTagView;
    private int mTagViewHeight;
    private int mTagViewWidth;
    private int mTagViewPointSize;

    private TagImageView mTagImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mTagViewPointSize = getResources().getDimensionPixelOffset(R.dimen.tag_view_point_size);
//
//        mImageView = (ImageView) findViewById(R.id.imageView);
//        mTagView = (TagView) findViewById(R.id.tag_view);
//
//        // Set the Drawable displayed
//        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
//        mImageView.setImageDrawable(bitmap);
//
//        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
//        mAttacher = new PhotoViewAttacher(mImageView);
//        mAttacher.setOnMatrixChangeListener(this);
//
//        mTagView.measure(0, 0);
//        mTagViewWidth = mTagView.getMeasuredWidth();
//        mTagViewHeight = mTagView.getMeasuredHeight();
//
//        Log.d("MainActivity", "tagViewWidth " + mTagViewWidth + " tagViewHeight " + mTagViewHeight);

        mTagImageView = (TagImageView) findViewById(R.id.tag_image_view);


        List<TagModel> tagModelList = new ArrayList<>();

        TagModel tagModel1 = new TagModel();
        tagModel1.type = TagModel.Type.NORMAL;
        tagModel1.position = new Point(300, 200);
        tagModel1.direction = TagModel.Direction.LEFT;
        tagModel1.name = "tag1 left";
        tagModelList.add(tagModel1);

        TagModel tagModel2 = new TagModel();
        tagModel2.type = TagModel.Type.NORMAL;
        tagModel2.position = new Point(300, 300);
        tagModel2.direction = TagModel.Direction.LEFT;
        tagModel2.name = "tag2 left";
        tagModelList.add(tagModel2);

        TagModel tagModel3 = new TagModel();
        tagModel3.type = TagModel.Type.NORMAL;
        tagModel3.position = new Point(300, 300);
        tagModel3.direction = TagModel.Direction.RIGHT;
        tagModel3.name = "tag3";
        tagModelList.add(tagModel3);

        mTagImageView.setTagList(tagModelList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMatrixChanged(RectF rect) {

//        // left
////        mTagView.setTranslationX((int) rect.centerX() - mTagViewPointSize / 2);
////        mTagView.setTranslationY((int) rect.centerY() - mTagViewHeight / 2);
//
//        // right
//        mTagView.setTranslationX((int) rect.centerX() - mTagViewWidth + mTagViewPointSize / 2);
//        mTagView.setTranslationY((int) rect.centerY() - mTagViewHeight / 2);
    }
}
