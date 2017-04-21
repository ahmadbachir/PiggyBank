package com.ab.piggybank.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RestoreRelationships extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_relationships);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ListView listView = (ListView) findViewById(R.id.deletedRelationshipsList);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        MyListAdapter listAdapter = new MyListAdapter(this,dbHelper.getDeletedDebtRelationships());
        listView.setAdapter(listAdapter);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyListAdapter extends CursorAdapter{
        DatabaseHelper dbHelper;
        public MyListAdapter(Context context, Cursor c) {
            super(context, c);
            dbHelper = new DatabaseHelper(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.deleted_relationship_layout,parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.debt_relationship_icon);
            viewHolder.name = (TextView) view.findViewById(R.id.debt_relationship_name);
            viewHolder.restoreButton = (ImageButton) view.findViewById(R.id.restoreButton);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            final Utils utils = new Utils();
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),utils.debt_relations_icon()[cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_ICON))]),120,120,false);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    viewHolder.icon.setImageBitmap(bitmap);
                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(viewHolder.icon);
                }
            }.execute();
            viewHolder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_NAME)));
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID));
            viewHolder.restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListView listView = (ListView) findViewById(R.id.deletedRelationshipsList);
                    if(listView.getAdapter().getCount() == 1){
                        dbHelper.unDeleteDebtRelationship(id);
                        finish();
                    }
                    else {
                        dbHelper.unDeleteDebtRelationship(id);
                        YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                        MyListAdapter listAdapter = (MyListAdapter) listView.getAdapter();
                        listAdapter.changeCursor(dbHelper.getDeletedDebtRelationships());
                        listView.invalidateViews();
                        YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                    }
                }
            });
        }

        private class ViewHolder{
            ImageView icon;
            TextView name;
            ImageButton restoreButton;
        }
    }

}
