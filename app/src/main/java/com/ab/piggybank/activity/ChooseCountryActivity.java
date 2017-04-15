package com.ab.piggybank.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChooseCountryActivity extends AppCompatActivity {
    Boolean startedCursor = false;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);
        final int langID;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Arabic":
                langID = 1;
                break;
            default:
                langID = 0;
        }
        final ListView listView = (ListView) findViewById(R.id.countryList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseCountryActivity.this);
                TextView text = (TextView) view.findViewById(R.id.currency_name);
                builder.setTitle(R.string.are_you_sure);
                builder.setMessage(getString(R.string.Are_you_sure_you_want_to_proceed_with)+ " " + text.getText().toString() + " " + getString(R.string.sure_currency_choose) + "\n" + getString(R.string.You_will_not_be_to_change) + "\n" + getString(R.string.dont_worry));
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChooseCountryActivity.this);
                        preferences.edit().putInt("country", (int) id).apply();
                        Intent i = new Intent(ChooseCountryActivity.this, MainActivity.class);
                        startActivity(i);

                    }
                });
                builder.create().show();
            }
        });
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor;
        switch (langID) {
            default:
                cursor = dbHelper.getCurrencyDataEng("");
        }
        final ListViewAdapter listAdapter = new ListViewAdapter(this, cursor);
        EditText enterCountry = (EditText) findViewById(R.id.enterNameOrABV);
        listView.setFastScrollEnabled(true);
        listView.setScrollingCacheEnabled(false);
        enterCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() >= 3) {
                    Cursor cursor;
                    TextView textView = (TextView) findViewById(R.id.messageText);
                    switch (langID) {
                        default:
                            cursor = dbHelper.getCurrencyDataEng(s.toString());
                            textView.setText(R.string.go_ahead_choose_currency);
                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(textView);
                    }


                    if (!startedCursor) {
                        startedCursor = true;
                        listView.setAdapter(listAdapter);
                        listAdapter.changeCursor(cursor);
                        listView.smoothScrollToPosition(0);

                        YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_shortAnimTime)).playOn(listView);
                    } else {
                        listAdapter.changeCursor(cursor);
                        listView.smoothScrollToPosition(0);
                        YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_shortAnimTime)).playOn(listView);
                    }
                    if (!listView.isEnabled()) {
                        listView.setEnabled(true);
                    }

                } else if (s.toString().trim().length() < 3) {
                    TextView textView = (TextView) findViewById(R.id.messageText);
                    switch (langID) {
                        default:
                            if (3 - s.toString().trim().length() > 1) {
                                textView.setText(3 - s.toString().trim().length() + " " + getString(R.string.letter_to_go));
                            } else {
                                textView.setText(3 - s.toString().trim().length() + " " + getString(R.string.more_letter_to_go));
                            }
                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(textView);
                    }
                    if (listView.getAlpha() > 0 && startedCursor) {
                        YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_shortAnimTime)).playOn(listView);
                        listView.setEnabled(false);
                    }
                }
            }
        });


    }


    class ListViewAdapter extends CursorAdapter {
        public ListViewAdapter(Context context, Cursor c) {
            super(context, c);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.country_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.flag = (RoundedImageView) view.findViewById(R.id.country_flag);
            viewHolder.name = (TextView) view.findViewById(R.id.currency_name);
            viewHolder.abv = (TextView) view.findViewById(R.id.currency_abv);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            final Utils utils = new Utils();
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            viewHolder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CURRENCY_NAME)));
            viewHolder.abv.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ABV)));
            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), utils.flagIds()[cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID)) - 1]);
            viewHolder.flag.post(new Runnable() {
                @Override
                public void run() {
                    viewHolder.flag.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
                }
            });
        }

        private class ViewHolder {
            RoundedImageView flag;
            TextView name;
            TextView abv;
        }
    }

}
