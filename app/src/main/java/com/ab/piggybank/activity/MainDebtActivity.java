package com.ab.piggybank.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionMenu;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainDebtActivity extends AppCompatActivity {
    boolean isActionMenuExpanded = false;
    DatabaseHelper dbHelper;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_debt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().getThemedContext().getTheme().applyStyle(R.style.MyToolbarStyle,true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.debtFloatingMenu);
        floatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMenu();
                floatingActionMenu.toggle(true);
            }
        });
        final ListView listView = (ListView) findViewById(R.id.debt_relationships);
        dbHelper = new DatabaseHelper(this);
        if (dbHelper.getDebtRelationships().getCount() == 0) {
            dbHelper.insertDebtRelationship(-1, null);
        }
        CursorAdapter cursorAdapter = new CursorAdapter(this, dbHelper.getDebtRelationships());
        listView.setAdapter(cursorAdapter);
        com.github.clans.fab.FloatingActionButton floatingActionButton1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addDebtTransaction);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainDebtActivity.this, AddDebtTransaction.class);
                startActivity(i);
                onClickMenu();
                floatingActionMenu.toggle(true);
            }
        });

        com.github.clans.fab.FloatingActionButton floatingActionButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.addRelationship);
        floatingActionButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                onClickMenu();
                floatingActionMenu.toggle(true);
                if (isActionMenuExpanded) {
                    final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.debtFloatingMenu);
                    ImageView imageView = (ImageView) findViewById(R.id.debtActivityOverlay);
                    imageView.animate().alpha(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    isActionMenuExpanded = false;
                    floatingActionMenu.toggle(true);
                    imageView.setClickable(false);
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainDebtActivity.this);
                builder.setTitle(R.string.add_debt_relationship);
                View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.debt_relationship_editor_layout, null, false);
                final pic pic = new pic();
                pic.id = 0;
                final EditText nameEnter = (EditText) view1.findViewById(R.id.debt_name);
                nameEnter.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                final RoundedImageView roundedImageView = (RoundedImageView) view1.findViewById(R.id.relationship_icon);
                final Utils utils = new Utils();
                roundedImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[pic.id]), 120, 120, false));
                roundedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainDebtActivity.this);
                        View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grid_layout, null, false);
                        GridView gridView = (GridView) view2.findViewById(R.id.gridView);
                        GridViewAdapter gridViewAdapter = new GridViewAdapter();
                        gridView.setAdapter(gridViewAdapter);
                        final AlertDialog dialog = builder1.create();
                        dialog.setView(view2);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                dialog.dismiss();
                                pic.id = position;
                                YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(roundedImageView);
                                roundedImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[position]), 120, 120, false));
                                YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(roundedImageView);
                            }
                        });
                        dialog.show();
                    }
                });
                builder.setView(view1);
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.done), null);
                final AlertDialog dialog1 = builder.create();
                dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button okButton = dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (nameEnter.getText().toString().length() == 0) {
                                    Toast.makeText(getApplicationContext(), R.string.add_name_to_proceed, Toast.LENGTH_LONG).show();
                                    return;
                                }
                                dbHelper.insertDebtRelationship(pic.id, nameEnter.getText().toString());
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog1.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_debt_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.restoreRelationships) {
            if (dbHelper.getDeletedDebtRelationships().getCount() != 0) {
                Intent i = new Intent(this, RestoreRelationships.class);
                startActivity(i);
            }else {
                Toast.makeText(this, R.string.no_deleted_debt_relationships,Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class pic {
        int id;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView listView = (ListView) findViewById(R.id.debt_relationships);
        ((CursorAdapter) listView.getAdapter()).changeCursor(dbHelper.getDebtRelationships());
    }

    public void onClickMenu() {
        if (!isActionMenuExpanded) {
            ImageView imageView = (ImageView) findViewById(R.id.debtActivityOverlay);
            if (!imageView.isClickable()) {
                imageView.setClickable(true);
            }
            imageView.animate().alpha(0.7f).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isActionMenuExpanded) {
                        final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.debtFloatingMenu);
                        ImageView imageView = (ImageView) findViewById(R.id.debtActivityOverlay);
                        imageView.animate().alpha(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                        isActionMenuExpanded = false;
                        floatingActionMenu.toggle(true);
                        imageView.setClickable(false);
                    }
                }
            });
            isActionMenuExpanded = true;
        } else {
            ImageView imageView = (ImageView) findViewById(R.id.debtActivityOverlay);
            imageView.animate().alpha(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            isActionMenuExpanded = false;
            final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.debtFloatingMenu);
            floatingActionMenu.toggle(true);
            imageView.setClickable(false);
        }
    }

    private class CursorAdapter extends android.support.v4.widget.CursorAdapter {
        DatabaseHelper dbHelper;

        public CursorAdapter(Context context, Cursor c) {
            super(context, c,true);
            dbHelper = new DatabaseHelper(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.debt_relationship_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.debt_relationship_icon);
            viewHolder.name = (TextView) view.findViewById(R.id.debt_relationship_name);
            viewHolder.edit = (ImageButton) view.findViewById(R.id.editButton);
            viewHolder.go = (ImageButton) view.findViewById(R.id.goButton);
            viewHolder.amountIn = (TextView) view.findViewById(R.id.amountIn);
            viewHolder.amountOut = (TextView) view.findViewById(R.id.amountOut);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, final Context context, final Cursor cursor) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            final Utils utils = new Utils();

            double amountIngoing = dbHelper.sumOfDebtRelationIngoing(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            double amountOutgoing = dbHelper.sumOfDebtRelationOutgoing(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
            String currency = getResources().getStringArray(R.array.currency_abv)[preferences.getInt("country", 0) - 1];
            String amountInString;
            String amountOutString;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            if (amountIngoing > 1000000) {
                amountInString = decimalFormat.format(amountIngoing / 1000000) + " " + getString(R.string.mn);
            } else if (amountIngoing > 1000) {
                amountInString = decimalFormat.format(amountIngoing / 1000) + " " + getString(R.string.k);
            } else {

                amountInString = decimalFormat.format(amountIngoing);
            }


            if (amountOutgoing > 1000000) {
                amountOutString = decimalFormat.format(amountOutgoing / 1000000) + " " + getString(R.string.mn);
            } else if (amountOutgoing > 1000) {
                amountOutString = decimalFormat.format(amountOutgoing / 1000) + " " + getString(R.string.k);
            } else {
                amountOutString = decimalFormat.format(amountOutgoing);
            }
            final int id = (int) cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            viewHolder.amountIn.setText(amountInString + " " + currency);
            viewHolder.amountOut.setText(amountOutString + " " + currency);
            if (cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)) == 1) {
                viewHolder.edit.setVisibility(View.GONE);
                viewHolder.go.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                viewHolder.name.setText(R.string.the_bank);
                viewHolder.icon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bank), 120, 120, false));
            } else {
                viewHolder.icon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RELATIONSHIP_ICON))]), 120, 120, false));
                viewHolder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RELATIONSHIP_NAME)));
                final String initialName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RELATIONSHIP_NAME));
                final int initialId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RELATIONSHIP_ICON));
                viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainDebtActivity.this);
                        builder.setTitle(R.string.what_would_you_like_to_do);
                        builder.setItems(new String[]{getString(R.string.edit), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainDebtActivity.this);
                                    builder.setTitle(R.string.edit_debt_relationship);
                                    View view1 = LayoutInflater.from(context).inflate(R.layout.debt_relationship_editor_layout, null, false);
                                    final pic1 pic = new pic1();
                                    TextView textView = (TextView) view1.findViewById(R.id.textView16);
                                    textView.setTextColor(getResources().getColor(android.R.color.tertiary_text_light));
                                    pic.id = initialId;
                                    final EditText nameEnter = (EditText) view1.findViewById(R.id.debt_name);
                                    nameEnter.setText(initialName);
                                    final RoundedImageView roundedImageView = (RoundedImageView) view1.findViewById(R.id.relationship_icon);
                                    roundedImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[pic.id]), 120, 120, false));
                                    roundedImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                            View view2 = LayoutInflater.from(context).inflate(R.layout.grid_layout, null, false);
                                            GridView gridView = (GridView) view2.findViewById(R.id.gridView);
                                            GridViewAdapter gridViewAdapter = new GridViewAdapter();
                                            gridView.setAdapter(gridViewAdapter);
                                            final AlertDialog dialog = builder1.create();
                                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    dialog.dismiss();
                                                    pic.id = (int) id;
                                                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(roundedImageView);
                                                    roundedImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[position]), 120, 120, false));
                                                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(roundedImageView);
                                                }
                                            });

                                        }
                                    });
                                    builder.setView(view1);
                                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setPositiveButton(getString(R.string.done), null);
                                    final AlertDialog dialog1 = builder.create();
                                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(final DialogInterface dialog) {
                                            Button okButton = dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                                            okButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (pic.id == initialId && nameEnter.getText().toString().equals(initialName)) {
                                                        Toast.makeText(context, R.string.you_didnt_change_anything, Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                    Cursor cursor1 = dbHelper.returnedARowWithTheSameName(nameEnter.getText().toString());
                                                    if(cursor1.moveToFirst() && cursor1.getLong(cursor1.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)) != cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))){
                                                       Toast.makeText(context, R.string.theres_debt_relationship_with_same_name, Toast.LENGTH_LONG).show();
                                                        cursor1.close();
                                                        return;
                                                    }
                                                    cursor1.close();
                                                    dbHelper.updateDebtRelationship(id, pic.id, nameEnter.getText().toString());
                                                    ListView listView = (ListView) findViewById(R.id.debt_relationships);
                                                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                                    listView.deferNotifyDataSetChanged();
                                                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                                    dialog1.dismiss();
                                                }
                                            });
                                        }
                                    });
                                    dialog1.show();
                                }
                                if (which == 1) {
                                    dbHelper.deleteDebtRelationship(id);
                                    ListView listView = (ListView) findViewById(R.id.debt_relationships);
                                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                    listView.deferNotifyDataSetChanged();
                                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                    Snackbar snackbar = Snackbar.make((View) listView.getParent(), getString(R.string.deleted), Snackbar.LENGTH_LONG);
                                    snackbar.setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dbHelper.unDeleteDebtRelationship(id);
                                            ListView listView = (ListView) findViewById(R.id.debt_relationships);
                                            YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                            listView.deferNotifyDataSetChanged();
                                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                        }
                                    });
                                    snackbar.show();
                                    recreate();
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.setPositiveButton(R.string.nevermind, null);
                        builder.create().show();
                    }

                });

            }
            Log.i("id", String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))));

            viewHolder.go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbHelper.getDebtTransactionsOfRelationship(id).getCount() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.no_debt_transactions, Toast.LENGTH_LONG).show();
                        Log.i("id", String.valueOf(id));
                        Log.i("count", String.valueOf(dbHelper.getDebtTransactionsOfRelationshipNew(id).getCount()));
                    } else {
                        Intent intent = new Intent(getApplicationContext(), DetailDebtActivity.class);
                        Log.i("id", String.valueOf(id));
                        Log.i("count", String.valueOf(dbHelper.getDebtTransactionsOfRelationshipNew(id).getCount()));
                        intent.putExtra("id", (long) id);
                        startActivity(intent);
                    }
                }
            });


        }


        private class pic1 {
            int id;
        }

        private class ViewHolder {
            ImageView icon;
            TextView name;
            TextView amountIn;
            TextView amountOut;
            ImageButton go;
            ImageButton edit;
        }


    }

    private class GridViewAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            Utils utils = new Utils();
            return utils.debt_relations_icon().length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.image_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.imageView = (RoundedImageView) convertView.findViewById(R.id.image_item);
                convertView.setTag(viewHolder);
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            Utils utils = new Utils();
            viewHolder.imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[position]), 120, 120, false));
            return convertView;
        }

        private class ViewHolder {
            RoundedImageView imageView;
        }
    }

}
