package com.ab.piggybank;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.activity.setup1.MethodType;
import com.ab.piggybank.activity.setup1.setupActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditPaymentMethods extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment_methods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        dbHelper = new DatabaseHelper(this);
        getSupportActionBar().getThemedContext().getTheme().applyStyle(R.style.MyToolbarStyle, true);
        ListView listView = (ListView) findViewById(R.id.methodList);
        updateListStatus(true);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Wuz gucci",Toast.LENGTH_LONG).show();
            }
        });
        listView.setItemsCanFocus(true);
    }

    private void updateListStatus(boolean oncreate) {
        RelativeLayout emptyView = (RelativeLayout) findViewById(R.id.empty_view);
        emptyView.setClickable(false);
        ListView listView = (ListView) findViewById(R.id.methodList);
        Cursor cursor = dbHelper.getPaymentMethodsOtherThanCash();
        if (cursor.getCount() > 0) {
            if (oncreate) {
                emptyView.setAlpha(0f);
                emptyView.setVisibility(View.GONE);



            } else {
                if (emptyView.getAlpha() != 0f) {
                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(emptyView);
                    emptyView.setVisibility(View.GONE);
                }
                YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
            }

            ListAdapter listAdapter = new ListAdapter(this, cursor);
            listView.setAdapter(listAdapter);
            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
        } else {
            if (emptyView.getVisibility() == View.GONE) {
                emptyView.setVisibility(View.VISIBLE);
            }
            if (!oncreate) {

                YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(emptyView);
                YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
            }

            ListAdapter listAdapter = new ListAdapter(this, cursor);
            listView.setAdapter(listAdapter);
            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_payment_method_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.add_payment_method) {
            showAddMethodDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditMethodDialog(final long id) {
        Cursor cursor = dbHelper.getPaymentMethodAtId(id);
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_PAYMENT_METHOD_NAME));
        final int spinnerPos = cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_new_payment_method);
        View view = getLayoutInflater().inflate(R.layout.add_method_layout, null, false);
        final Spinner methodSpinner = (Spinner) view.findViewById(R.id.methodSpinner);
        spinnerAdapter spinnerAdapter = new spinnerAdapter(this);
        methodSpinner.setAdapter(spinnerAdapter);
        methodSpinner.setSelection(spinnerPos);
        final EditText nameEnter = (EditText) view.findViewById(R.id.methodName);
        nameEnter.setText(name);
        builder.setView(view);
        builder.setPositiveButton(R.string.done, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (nameEnter.getText().toString().length() == 0) {
                            Toast.makeText(EditPaymentMethods.this, getString(R.string.need_name_to_proceed), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (methodSpinner.getSelectedItemPosition() == 0) {
                            Toast.makeText(EditPaymentMethods.this, R.string.you_need_to_choose_a_type_to_proceed, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (nameEnter.getText().toString().equals(name) && methodSpinner.getSelectedItemPosition() == spinnerPos) {
                            Toast.makeText(EditPaymentMethods.this, getString(R.string.you_didnt_change_anything), Toast.LENGTH_LONG).show();
                            return;
                        }
                        dbHelper.updatePaymentMethods(id, nameEnter.getText().toString(), (int) methodSpinner.getSelectedItemId(), methodSpinner.getSelectedItemPosition());
                        dialog.dismiss();
                        updateListStatus(false);

                    }
                });
            }
        });

        dialog.show();


    }

    private void showAddMethodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_new_payment_method);
        View view = getLayoutInflater().inflate(R.layout.add_method_layout, null, false);
        final Spinner methodSpinner = (Spinner) view.findViewById(R.id.methodSpinner);
        spinnerAdapter spinnerAdapter = new spinnerAdapter(this);
        methodSpinner.setAdapter(spinnerAdapter);
        final EditText nameEnter = (EditText) view.findViewById(R.id.methodName);
        builder.setView(view);
        builder.setPositiveButton(R.string.done, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (nameEnter.getText().toString().length() == 0) {
                            Toast.makeText(EditPaymentMethods.this, getString(R.string.need_name_to_proceed), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (methodSpinner.getSelectedItemPosition() == 0) {
                            Toast.makeText(EditPaymentMethods.this, R.string.you_need_to_choose_a_type_to_proceed, Toast.LENGTH_LONG).show();
                            return;
                        }
                        dbHelper.insertPaymentMethods(nameEnter.getText().toString(), (int) methodSpinner.getSelectedItemId(), methodSpinner.getSelectedItemPosition());
                        dialog.dismiss();
                        updateListStatus(false);
                    }
                });
            }
        });

        dialog.show();


    }

    private class ListAdapter extends CursorAdapter {
        DatabaseHelper dbHelper;

        public ListAdapter(Context context, Cursor c) {
            super(context, c);
            dbHelper = new DatabaseHelper(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.edit_payment_method_item_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.item_icon);
            viewHolder.mainText = (TextView) view.findViewById(R.id.item_text);
            viewHolder.subText = (TextView) view.findViewById(R.id.item_subText);
            viewHolder.moreButton = (ImageButton) view.findViewById(R.id.moreButton);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            final Utils utils = new Utils();
            new AsyncTask<Integer, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Integer... params) {
                    return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.paymentMethodIcons()[params[0]]), 120, 120, false);

                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    viewHolder.icon.setImageBitmap(bitmap);
                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(viewHolder.icon);
                }
            }.execute(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT)));
            viewHolder.mainText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_PAYMENT_METHOD_NAME)));
            viewHolder.mainText.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            viewHolder.subText.setText(getResources().getStringArray(R.array.paymentMethodNames)[cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT))]);
            viewHolder.subText.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID));
            viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterPopup(v,id);
                }
            });
        }

        private class ViewHolder {
            ImageView icon;
            TextView mainText;
            TextView subText;
            ImageButton moreButton;
        }

        private void showFilterPopup(View v, final long id) {
            PopupMenu popup = new PopupMenu(EditPaymentMethods.this, v);
            // Inflate the menu from xml
            popup.getMenuInflater().inflate(R.menu.edit_payment_method_item_menu, popup.getMenu());
            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            showEditMethodDialog(id);
                            return true;
                        case R.id.delete:
                            dbHelper.deletePaymentMethods(id);
                            updateListStatus(false);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Handle dismissal with: popup.setOnDismissListener(...);
            // Show the menu
            popup.show();
        }

    }


    class spinnerAdapter extends ArrayAdapter<PaymentMethod> {
        List<MethodType> methodTypes = new ArrayList<>();

        public spinnerAdapter(@NonNull Context context) {
            super(context, 0);
            Utils utils = new Utils();
            String[] names = context.getResources().getStringArray(R.array.paymentMethodNames);
            int[] icons = utils.paymentMethodIcons();
            for (int i = 0; i < names.length - 1; i++) {
                methodTypes.add(new MethodType(names[i], icons[i], i));
            }


            Collections.sort(methodTypes, new Comparator<MethodType>() {
                @Override
                public int compare(MethodType o1, MethodType o2) {

                    return o1.getName().compareTo(o2.getName());
                }
            });
            methodTypes.add(0, new MethodType(context.getString(R.string.choose_payment_method), 0, 0));
            methodTypes.add(new MethodType(names[10], icons[10], 10));
        }

        @Override
        public int getCount() {
            return methodTypes.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            if (position == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                name.setText(methodTypes.get(position).getName());
            } else {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item_text_and_pic, parent, false);
                final ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), methodTypes.get(position).getPicId()), 120, 120, false);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        icon.setImageBitmap(bitmap);
                        YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(icon);
                    }
                }.execute();
                TextView name = (TextView) view.findViewById(R.id.item_text);
                name.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                name.setText(methodTypes.get(position).getName());
            }
            return view;
        }

        @Override
        public View getDropDownView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            if (position == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                name.setText(methodTypes.get(position).getName());
                name.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            } else {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item_text_and_pic, parent, false);
                final ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
                icon.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), methodTypes.get(position).getPicId());
                        icon.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
                    }
                });
                TextView name = (TextView) view.findViewById(R.id.item_text);
                name.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                name.setText(methodTypes.get(position).getName());
            }
            return view;
        }

        @Override
        public long getItemId(int position) {
            return methodTypes.get(position).getId();
        }
    }

    class PaymentMethod {
        String name;

        public int getPosAfterSort() {
            return posAfterSort;
        }

        public void setPosAfterSort(int posAfterSort) {
            this.posAfterSort = posAfterSort;
        }

        int posAfterSort;


        int type;

        public PaymentMethod(String name, int posAfterSort, int type) {
            this.name = name;
            this.posAfterSort = posAfterSort;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

}

