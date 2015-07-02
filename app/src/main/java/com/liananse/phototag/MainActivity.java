package com.liananse.phototag;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.liananse.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity implements PhotoViewAttacher.OnMatrixChangedListener {

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    private TagView mTagView;
    private int mTagViewHeight;
    private int mTagViewWidth;
    private int mTagViewPointSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTagViewPointSize = getResources().getDimensionPixelOffset(R.dimen.tag_view_point_size);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mTagView = (TagView) findViewById(R.id.tag_view);

        // Set the Drawable displayed
        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
        mImageView.setImageDrawable(bitmap);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnMatrixChangeListener(this);

        mTagView.measure(0, 0);
        mTagViewWidth = mTagView.getMeasuredWidth();
        mTagViewHeight = mTagView.getMeasuredHeight();

        Log.d("MainActivity", "tagViewWidth " + mTagViewWidth + " tagViewHeight " + mTagViewHeight);
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
        mTagView.setTranslationX((int) rect.centerX() - mTagViewPointSize / 2);
        mTagView.setTranslationY((int) rect.centerY() - mTagViewHeight / 2);
    }
}
