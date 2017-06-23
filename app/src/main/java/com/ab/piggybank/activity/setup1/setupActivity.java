package com.ab.piggybank.activity.setup1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.activity.ChooseCountryActivity;
import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.ab.piggybank.activity.MainActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class setupActivity extends AppCompatActivity implements paymentMethodReturner {
    ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        paymentMethods.add(new PaymentMethod(getString(R.string.cash), -1, -1));
        final ListView listView = (ListView) findViewById(R.id.methodList);
        methodListAdapter listAdapter = new methodListAdapter(this, paymentMethods);
        listView.setAdapter(listAdapter);
        ImageButton showDialogButton = (ImageButton) findViewById(R.id.addButton);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMethodDialog addDialog = new addMethodDialog().newInstance(false, null, 0);
                addDialog.show(getSupportFragmentManager(), "add Method Fragment");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(setupActivity.this);
                    builder.setPositiveButton(getString(R.string.cancel), null);
                    builder.setItems(new String[]{getString(R.string.edit), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                addMethodDialog addDialog = new addMethodDialog().newInstance(true, paymentMethods.get(position), position);
                                addDialog.show(getSupportFragmentManager(), "");
                            }
                            if (which == 1) {
                                paymentMethods.remove(position);
                                listView.invalidateViews();
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        });

        ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(setupActivity.this, getString(R.string.why_we_are_asking), Toast.LENGTH_LONG);
                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                textView.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);
                textView.setTextColor(Color.parseColor("#E0E0E0"));
                toast.show();
            }
        });

        ImageButton doneButton = (ImageButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentMethods.size() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(setupActivity.this);
                    builder.setTitle(R.string.warning);
                    builder.setMessage(R.string.no_methods_you_want_to_proceed);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DatabaseHelper dbHelper = new DatabaseHelper(setupActivity.this);
                                    for (int i = 0; i < paymentMethods.size(); i++) {
                                        dbHelper.insertPaymentMethods(paymentMethods.get(i).getName(), paymentMethods.get(i).posAfterSort, paymentMethods.get(i).getType());
                                    }
                                }
                            });

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(setupActivity.this);
                            if (preferences.getInt("country", 0) == 0){
                                Intent i = new Intent(setupActivity.this, ChooseCountryActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Intent i = new Intent(setupActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseHelper dbHelper = new DatabaseHelper(setupActivity.this);
                            for (int i = 0; i < paymentMethods.size(); i++) {
                                dbHelper.insertPaymentMethods(paymentMethods.get(i).getName(), paymentMethods.get(i).posAfterSort, paymentMethods.get(i).getType());
                            }
                        }
                    });

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(setupActivity.this);
                    if (preferences.getInt("country", 0) == 0){
                        Intent i = new Intent(setupActivity.this, ChooseCountryActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Intent i = new Intent(setupActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }

            }
        });

    }

    class methodListAdapter extends ArrayAdapter<PaymentMethod> {
        ArrayList<PaymentMethod> paymentMethods;

        public methodListAdapter(@NonNull Context context, ArrayList<PaymentMethod> paymentMethods) {
            super(context, 0);
            this.paymentMethods = paymentMethods;
        }

        @Override
        public int getCount() {
            return paymentMethods.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (paymentMethods.get(position).getType() == -1) {
                convertView = LayoutInflater.from(setupActivity.this).inflate(R.layout.list_item_text_and_pic_cardview, parent, false);
                ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
                icon.setImageResource(R.drawable.cash);
                TextView name = (TextView) convertView.findViewById(R.id.item_text);
                name.setText(paymentMethods.get(position).getName());
            } else {
                Utils utils = new Utils();
                convertView = LayoutInflater.from(setupActivity.this).inflate(R.layout.list_item_2texts_and_pic, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_text);
                name.setText(paymentMethods.get(position).getName());
                TextView subText = (TextView) convertView.findViewById(R.id.item_subText);
                subText.setText(setupActivity.this.getResources().getStringArray(R.array.paymentMethodNames)[paymentMethods.get(position).getPosAfterSort()]);
                ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),utils.paymentMethodIcons()[paymentMethods.get(position).getPosAfterSort()]);
                icon.setImageBitmap(Bitmap.createScaledBitmap(bitmap,120,120,false));
            }
            return convertView;
        }

    }

    @Override
    public void returner(boolean edited, int position, int type, String name, int pos) {
        if (edited) {
            paymentMethods.set(position, new PaymentMethod(name, type, pos));
        } else {
            paymentMethods.add(new PaymentMethod(name, type, pos));
        }
        ListView listView = (ListView) findViewById(R.id.methodList);
        listView.invalidateViews();
    }


    public static class addMethodDialog extends DialogFragment {
        public addMethodDialog newInstance(boolean editing, PaymentMethod paymentMethod, int pos) {
            Bundle args = new Bundle();
            args.putBoolean("edit", editing);
            if (editing) {
                args.putInt("type", paymentMethod.getType());
                args.putString("name", paymentMethod.getName());
                args.putInt("posAfterSort", paymentMethod.getPosAfterSort());
                args.putInt("pos", pos);
            }
            addMethodDialog fragment = new addMethodDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(R.string.create_payment_method);
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_method_layout, null);
            Spinner spinner = (Spinner) view.findViewById(R.id.methodSpinner);
            final spinnerAdapter spinnerAdapter = new spinnerAdapter(getActivity());
            spinner.setAdapter(spinnerAdapter);
            if (getArguments().getBoolean("edit", false)) {
                EditText nameEnter = (EditText) view.findViewById(R.id.methodName);
                nameEnter.setText(getArguments().getString("name"));
                spinner.setSelection(getArguments().getInt("posAfterSort") + 3);
            }

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });


            builder.setView(view);
            builder.setPositiveButton(R.string.done, null);
            final AlertDialog mdialog = builder.create();

            mdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button button = mdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText nameEnter = (EditText) view.findViewById(R.id.methodName);
                            Spinner spinner = (Spinner) view.findViewById(R.id.methodSpinner);
                            if (nameEnter.getText().toString().trim().length() != 0 && spinner.getSelectedItemPosition() != 0) {
                                paymentMethodReturner paymentMethodReturner = (paymentMethodReturner) getActivity();

                                paymentMethodReturner.returner(getArguments().getBoolean("edit", false), getArguments().getInt("pos", 0), spinnerAdapter.methodTypes.get(spinner.getSelectedItemPosition()).getId(), nameEnter.getText().toString(), spinner.getSelectedItemPosition());
                                dismiss();
                            } else if (nameEnter.getText().toString().trim().length() == 0) {
                                YoYo.with(Techniques.Shake).duration(getActivity().getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(nameEnter);
                            } else {
                                YoYo.with(Techniques.Shake).duration(getActivity().getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(spinner);
                            }

                        }

                    });

                }
            });

            return mdialog;
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
                    view = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
                    TextView name = (TextView) view.findViewById(android.R.id.text1);
                    name.setText(methodTypes.get(position).getName());
                } else {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_text_and_pic, parent, false);
                    final ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
                    new AsyncTask<Integer, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Integer... params) {
                            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), params[0]),120,120,false);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            icon.setImageBitmap(bitmap);
                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(icon);
                        }
                    }.execute(methodTypes.get(position).getPicId());
                    TextView name = (TextView) view.findViewById(R.id.item_text);
                    name.setText(methodTypes.get(position).getName());
                }
                return view;
            }

            @Override
            public View getDropDownView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view;
                if (position == 0) {
                    view = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
                    TextView name = (TextView) view.findViewById(android.R.id.text1);
                    name.setText(methodTypes.get(position).getName());
                } else {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_text_and_pic, parent, false);
                    final ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
                    new AsyncTask<Integer, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Integer... params) {
                            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), params[0]),120,120,false);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            icon.setImageBitmap(bitmap);
                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(icon);
                        }
                    }.execute(methodTypes.get(position).getPicId());
                    TextView name = (TextView) view.findViewById(R.id.item_text);
                    name.setText(methodTypes.get(position).getName());
                }
                return view;
            }

            @Override
            public long getItemId(int position) {
                return methodTypes.get(position).getId();
            }
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
