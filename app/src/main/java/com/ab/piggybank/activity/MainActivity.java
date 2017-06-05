package com.ab.piggybank.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.EditPaymentMethods;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    Calendar calendar = Calendar.getInstance();
    ArrayList<Month> months = new ArrayList<>();
    Boolean isActionMenuExpanded = false;
    AdView adView;
    Boolean createdBefore = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        adView = (AdView) findViewById(R.id.main_activity_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        TextView title = (TextView) findViewById(R.id.title);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().getThemedContext().getTheme().applyStyle(R.style.MyToolbarStyle, true);
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "VarelaRound-Regular.ttf");
        title.setText(getString(R.string.app_name).toUpperCase());
        title.setTypeface(titleFont);

        final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.mainFloatingActionMenu);
        floatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMenu();
                floatingActionMenu.toggle(true);
            }
        });
        addOneYear();
        com.github.clans.fab.FloatingActionButton floatingActionButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddTransactionActivity.class);
                startActivity(i);
                finish();
            }
        });
        com.github.clans.fab.FloatingActionButton floatingActionButton2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainDebtActivity.class);
                startActivity(i);
                finish();
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_ViewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), months);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (createdBefore) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.main_ViewPager);
            viewPager.invalidate();
        }
        if (adView != null) {
            adView.resume();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_methods) {
            Intent i = new Intent(this, EditPaymentMethods.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickMenu() {
        if (!isActionMenuExpanded) {
            ImageView imageView = (ImageView) findViewById(R.id.mainActivityOverlay);
            if (!imageView.isClickable()) {
                imageView.setClickable(true);
            }
            imageView.animate().alpha(0.7f).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isActionMenuExpanded) {
                        final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.mainFloatingActionMenu);
                        ImageView imageView = (ImageView) findViewById(R.id.mainActivityOverlay);
                        imageView.animate().alpha(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                        isActionMenuExpanded = false;
                        floatingActionMenu.toggle(true);
                        imageView.setClickable(false);
                    }
                }
            });
            isActionMenuExpanded = true;
        } else {
            ImageView imageView = (ImageView) findViewById(R.id.mainActivityOverlay);
            imageView.animate().alpha(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            isActionMenuExpanded = false;
            final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.mainFloatingActionMenu);
            floatingActionMenu.toggle(true);
            imageView.setClickable(false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Month> months;
        DatabaseHelper dbHelper;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Month> months) {
            super(fm);
            this.months = months;
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        @Override
        public Fragment getItem(int position) {
            if (dbHelper.getTransactionsInMonth(months.get(position).month, months.get(position).year).getCount() != 0) {
                return MonthFragment.newInstance(months.get(position).month, months.get(position).year);
            } else {
                return EmptyFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return months.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Month month = months.get(position);
            switch (Locale.getDefault().getDisplayLanguage()) {
                default:
                    switch (month.month) {
                        case 0:
                            return "Jan" + "/" + month.year;
                        case 1:
                            return "Feb" + "/" + month.year;
                        case 2:
                            return "Mar" + "/" + month.year;
                        case 3:
                            return "Apr" + "/" + month.year;
                        case 4:
                            return "May" + "/" + month.year;
                        case 5:
                            return "Jun" + "/" + month.year;
                        case 6:
                            return "Jul" + "/" + month.year;
                        case 7:
                            return "Aug" + "/" + month.year;
                        case 8:
                            return "Sep" + "/" + month.year;
                        case 9:
                            return "Oct" + "/" + month.year;
                        case 10:
                            return "Nov" + "/" + month.year;
                        case 11:
                            return "Dec" + "/" + month.year;


                    }
            }


            return super.getPageTitle(position);
        }
    }

    public static class EmptyFragment extends android.support.v4.app.Fragment {
        public static EmptyFragment newInstance() {

            Bundle args = new Bundle();

            EmptyFragment fragment = new EmptyFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.empty_month_layout, container, false);
        }
    }

    public static class MonthFragment extends android.support.v4.app.Fragment {

        public static MonthFragment newInstance(int month, int year) {
            Bundle args = new Bundle();
            args.putInt("month", month);
            args.putInt("year", year);
            MonthFragment fragment = new MonthFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.month_fragment, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            Cursor daysInMonthExpense = dbHelper.getDaysInMonth(getArguments().getInt("month"), getArguments().getInt("year"));
            if (daysInMonthExpense.getCount() != 0) {
                BarChart lineChart = (BarChart) view.findViewById(R.id.expenseBarChart);
                List<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < daysInMonthExpense.getCount(); i++) {
                    entries.add(new BarEntry(daysInMonthExpense.getInt(daysInMonthExpense.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DAY)), dbHelper.sumOfExpenseTransactionsDay(daysInMonthExpense.getInt(daysInMonthExpense.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DAY)), getArguments().getInt("month"), getArguments().getInt("year"))));
                    daysInMonthExpense.moveToNext();
                }

                BarDataSet dataSet = new BarDataSet(entries, "Amount spent");
                dataSet.setValueTextSize(12);
                dataSet.setHighlightEnabled(true);
                BarData barData = new BarData(dataSet);
                dataSet.setValueFormatter(new LargeValueFormatter());
                lineChart.getLegend().setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"SourceSansPro-Regular.ttf"));
                dataSet.setColor(getResources().getColor(R.color.colorAccent));
                barData.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(),"SourceSansPro-Regular.ttf"));
                lineChart.setData(barData);
                lineChart.setTouchEnabled(false);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGridColor(Color.TRANSPARENT);
                xAxis.setGranularity(1f);
                YAxis yAxisLeft = lineChart.getAxisLeft();
                YAxis yAxisRight = lineChart.getAxisRight();
                yAxisLeft.setGridColor(getResources().getColor(android.R.color.tertiary_text_light));
                yAxisRight.setGridColor(getResources().getColor(android.R.color.tertiary_text_light));
                yAxisLeft.setTextColor(getResources().getColor(android.R.color.tertiary_text_light));
                yAxisRight.setTextColor(Color.TRANSPARENT);
                Description description = new Description();
                description.setText("");
                lineChart.setDescription(description);
                lineChart.invalidate();
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(lineChart);
                TextView textView3 = (TextView) view.findViewById(R.id.textView24);
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(textView3);
                PieChart pieChart = (PieChart) view.findViewById(R.id.expensePieChart);
                List<PieEntry> pieEntries = new ArrayList<>();
                Utils utils = new Utils();
                for (int i = 0; i < utils.expenseGroups(getActivity()).size(); i++) {
                    if (dbHelper.sumOfExpenseCategoryInMonth(getArguments().getInt("month"), getArguments().getInt("year"), i) != 0) {
                        float percentage = (dbHelper.sumOfExpenseCategoryInMonth(getArguments().getInt("month"), getArguments().getInt("year"), i) / dbHelper.sumOfExponseTransactionsMonth(getArguments().getInt("month"), getArguments().getInt("year"))) * 100;
                        pieEntries.add(new PieEntry(percentage, utils.expenseGroups(getActivity()).get(i).getName()));
                    }
                }
                Legend legend = pieChart.getLegend();
                legend.setEnabled(false);
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
                pieDataSet.setColors(getResources().getColor(R.color.colorAccent));
                pieDataSet.setValueTextColor(getResources().getColor(android.R.color.white));
                pieDataSet.setValueTextSize(12);
                pieDataSet.setSliceSpace(1.5f);
                pieChart.setEntryLabelTypeface(Typeface.createFromAsset(getActivity().getAssets(),"SourceSansPro-Regular.ttf"));
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));
                pieChart.setEntryLabelTextSize(12);
                Description desc = new Description();
                desc.setTextSize(0);
                desc.setText("");
                pieChart.setDescription(desc);
                pieChart.setUsePercentValues(true);
                pieChart.setCenterText("");
                pieChart.invalidate();
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(pieChart);
                TextView textView4 = (TextView) view.findViewById(R.id.textView25);
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(textView4);

            } else {
                BarChart lineChart = (BarChart) view.findViewById(R.id.expenseBarChart);
                lineChart.setVisibility(GONE);
                PieChart pieChart = (PieChart) view.findViewById(R.id.expensePieChart);
                pieChart.setVisibility(GONE);
                TextView textView = (TextView) view.findViewById(R.id.textView8);
                textView.setVisibility(GONE);
                TextView textView1 = (TextView) view.findViewById(R.id.textView9);
                textView1.setVisibility(GONE);
                TextView textView2 = (TextView) view.findViewById(R.id.textView25);
                textView2.setVisibility(GONE);
                TextView textView3 = (TextView) view.findViewById(R.id.textView24);
                textView3.setVisibility(GONE);

            }
            daysInMonthExpense.close();
            Cursor daysInMonthIncome = dbHelper.getDaysInMonthIncome(getArguments().getInt("month"), getArguments().getInt("year"));
            if (daysInMonthIncome.getCount() != 0) {
                BarChart lineChart = (BarChart) view.findViewById(R.id.incomeBarChart);
                List<BarEntry> entries = new ArrayList<>();

                for (int i = 0; i < daysInMonthIncome.getCount(); i++) {
                    entries.add(new BarEntry(daysInMonthIncome.getInt(daysInMonthIncome.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DAY)), dbHelper.sumOfIncomeTransactionsDay(daysInMonthIncome.getInt(daysInMonthIncome.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DAY)), getArguments().getInt("month"), getArguments().getInt("year"))));
                    daysInMonthIncome.moveToNext();
                }

                BarDataSet dataSet = new BarDataSet(entries, "Amount earned");
                dataSet.setValueTextSize(12);
                dataSet.setHighlightEnabled(true);
                BarData barData = new BarData(dataSet);
                dataSet.setValueFormatter(new LargeValueFormatter());
                dataSet.setColor(getResources().getColor(R.color.colorAccent));
                lineChart.setData(barData);
                barData.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(),"SourceSansPro-Regular.ttf"));
                lineChart.setTouchEnabled(false);
                lineChart.getLegend().setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"SourceSansPro-Regular.ttf"));
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGridColor(Color.TRANSPARENT);
                xAxis.setGranularity(1f);
                YAxis yAxisLeft = lineChart.getAxisLeft();
                YAxis yAxisRight = lineChart.getAxisRight();
                yAxisLeft.setGridColor(getResources().getColor(android.R.color.tertiary_text_light));
                yAxisRight.setGridColor(getResources().getColor(android.R.color.tertiary_text_light));
                yAxisLeft.setTextColor(getResources().getColor(android.R.color.tertiary_text_light));
                yAxisLeft.setGranularity(1f);
                yAxisRight.setTextColor(Color.TRANSPARENT);
                Description description = new Description();
                description.setText("");
                description.setTextColor(getResources().getColor(android.R.color.tertiary_text_light));
                description.setTextSize(12f);
                lineChart.setDescription(description);
                lineChart.invalidate();
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(lineChart);
                TextView textView3 = (TextView) view.findViewById(R.id.textView26);
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(textView3);

                PieChart pieChart = (PieChart) view.findViewById(R.id.incomePieChart);
                List<PieEntry> pieEntries = new ArrayList<>();
                Utils utils = new Utils();
                for (int i = 0; i < utils.incomeGroups(getActivity()).size(); i++) {
                    if (dbHelper.sumOfIncomeCategoryInMonth(getArguments().getInt("month"), getArguments().getInt("year"), i) != 0) {
                        float percentage = (dbHelper.sumOfIncomeCategoryInMonth(getArguments().getInt("month"), getArguments().getInt("year"), i) / dbHelper.sumOfIncomeTransactionsMonth(getArguments().getInt("month"), getArguments().getInt("year"))) * 100;
                        pieEntries.add(new PieEntry(percentage, utils.incomeGroups(getActivity()).get(i).getName()));
                    }
                }
                Legend legend = pieChart.getLegend();
                legend.setEnabled(false);
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
                pieDataSet.setColors(getResources().getColor(R.color.colorAccent));
                pieDataSet.setValueTextColor(getResources().getColor(android.R.color.white));
                pieDataSet.setValueTextSize(12);
                pieDataSet.setSliceSpace(1.5f);
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.setEntryLabelTypeface(Typeface.createFromAsset(getActivity().getAssets(),"SourceSansPro-Regular.ttf"));
                pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));
                pieChart.setEntryLabelTextSize(12);
                pieChart.setUsePercentValues(true);
                Description desc = new Description();
                desc.setTextSize(0);
                desc.setText("");
                pieChart.setDescription(desc);
                pieChart.setCenterText("");
                pieChart.invalidate();
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(pieChart);
                TextView textView4 = (TextView) view.findViewById(R.id.textView27);
                YoYo.with(Techniques.FadeInUp).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(textView4);

            } else {
                BarChart lineChart = (BarChart) view.findViewById(R.id.incomeBarChart);
                lineChart.setVisibility(GONE);
                PieChart pieChart = (PieChart) view.findViewById(R.id.incomePieChart);
                pieChart.setVisibility(GONE);
                TextView textView = (TextView) view.findViewById(R.id.textView11);
                textView.setVisibility(GONE);
                TextView textView1 = (TextView) view.findViewById(R.id.textView13);
                textView1.setVisibility(GONE);
                TextView textView2 = (TextView) view.findViewById(R.id.textView27);
                textView2.setVisibility(GONE);
                TextView textView3 = (TextView) view.findViewById(R.id.textView26);
                textView3.setVisibility(GONE);
            }
            daysInMonthIncome.close();

            ArrayList<Week> weeks = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            calendar.set(getArguments().getInt("year"), getArguments().getInt("month"), 1);
            for (int i = 0; i < calendar.getActualMaximum(Calendar.WEEK_OF_MONTH); i++) {
                if (dbHelper.getDaysInWeek(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 6, getArguments().getInt("month"), getArguments().getInt("year")).getCount() != 0) {
                    if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) > 7) {
                        weeks.add(new Week(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 6, getArguments().getInt("month"), getArguments().getInt("year")));
                    } else {
                        weeks.add(new Week(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH) + calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH), getArguments().getInt("month"), getArguments().getInt("year")));
                    }
                }
                calendar.add(Calendar.WEEK_OF_MONTH, +1);
            }
            MyListAdapter listAdapter = new MyListAdapter(getActivity(), weeks);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.week_list);
            View convertView = null;
            for (int i = 0; i < weeks.size(); i++) {
                linearLayout.addView(listAdapter.getView(i, convertView, linearLayout));
            }

        }

        private class Week {
            int firstDay;
            int lastDay;
            int month;
            int year;

            public Week(int firstDay, int lastDay, int month, int year) {
                this.firstDay = firstDay;
                this.lastDay = lastDay;
                this.month = month;
                this.year = year;
            }
        }


        class MyListAdapter extends ArrayAdapter<Month> {
            ArrayList<Week> weeks;
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

            public MyListAdapter(@NonNull Context context, ArrayList<Week> weeks) {
                super(context, 0);
                this.weeks = weeks;
            }

            @Override
            public int getCount() {
                return weeks.size();
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.week_item_layout, parent, false);
                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.title = (TextView) convertView.findViewById(R.id.weekTitle);
                    viewHolder.list = (LinearLayout) convertView.findViewById(R.id.weekList);
                    convertView.setTag(viewHolder);
                }
                ViewHolder viewHolder = (ViewHolder) convertView.getTag();
                switch (Locale.getDefault().getDisplayLanguage()) {
                    case "Arabic":
                        viewHolder.title.setText(getString(R.string.week) + " " + (position + 1));
                        break;
                    default:
                        viewHolder.title.setText(getString(R.string.week) + " " + (position + 1));
                }
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "SourceSansPro-Semibold.ttf");
                viewHolder.title.setTypeface(typeface);
                LinearLayout list = viewHolder.list;
                Cursor cursor = dbHelper.getDaysInWeek(weeks.get(position).firstDay, weeks.get(position).lastDay, weeks.get(position).month, weeks.get(position).year);
                cursor.moveToPosition(0);
                View view = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    list.addView(dayView(view, list, cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DAY)), cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_MONTH)), cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_YEAR))));
                    if (!cursor.isLast()) {
                        cursor.moveToNext();
                    } else {
                        cursor.close();
                    }
                }
                return convertView;
            }

            private class ViewHolder {
                TextView title;
                LinearLayout list;
            }

            private class DayViewHolder {
                TextView name;
                LinearLayout list;
            }

            private View dayView(View v, ViewGroup parent, final int day, final int month, final int year) {
                if (v == null) {
                    v = LayoutInflater.from(getActivity()).inflate(R.layout.day_item, parent, false);
                    DayViewHolder dayViewHolder = new DayViewHolder();
                    dayViewHolder.name = (TextView) v.findViewById(R.id.day_title);
                    dayViewHolder.list = (LinearLayout) v.findViewById(R.id.day_list);
                    v.setTag(dayViewHolder);
                }
                DayViewHolder viewHolder = (DayViewHolder) v.getTag();
                viewHolder.name.setText(DateFormat.getDateInstance().format(new Date(year - 1900, month, day)));
                final Cursor cursor = dbHelper.getTransactionsInDay(day, month, year);
                cursor.moveToPosition(0);
                View oneTransactionView = null;
                if (cursor.getCount() != 0) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        View v1;
                        if (oneTransactionView != null) {
                            v1 = transactionView(oneTransactionView, (ViewGroup) oneTransactionView.getParent(), cursor);
                        } else {
                            v1 = transactionView(oneTransactionView, viewHolder.list, cursor);
                        }
                        final long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                        v1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), DetailTransaction.class);
                                i.putExtra("id", id);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
                        final double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
                        final int type = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ENTRYTYPE));
                        final int cat = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));
                        final int subCat = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBCATEGORY));
                        v1.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getResources().getString(R.string.what_would_you_like_to_do));
                                builder.setItems(new String[]{getString(R.string.edit), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent i = new Intent(getActivity(), AddTransactionActivity.class);
                                            i.putExtra("editing", true);
                                            i.putExtra("id", id);
                                            i.putExtra("amount", amount);
                                            i.putExtra("day", day);
                                            i.putExtra("month", month);
                                            i.putExtra("year", year);
                                            i.putExtra("type", type);
                                            i.putExtra("cat", cat);
                                            i.putExtra("subCat", subCat);
                                            startActivity(i);
                                            getActivity().finish();
                                        } else {
                                            dbHelper.deleteTransaction(id);
                                            getActivity().finish();
                                            startActivity(getActivity().getIntent());
                                        }
                                    }
                                });
                                builder.setPositiveButton(getString(R.string.cancel), null);
                                builder.create().show();
                                return true;
                            }
                        });
                        Log.i("onClick", String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))));
                        viewHolder.list.addView(v1);

                        if (!cursor.isLast()) {
                            cursor.moveToNext();
                        } else {
                            cursor.close();
                        }

                    }
                }

                return v;
            }


            private View transactionView(View v, ViewGroup parent, final Cursor cursor) {
                if (v == null) {
                    v = LayoutInflater.from(getActivity()).inflate(R.layout.transaction_list_item, parent, false);
                    TransactionViewHolder viewHolder = new TransactionViewHolder();
                    viewHolder.icon = (ImageView) v.findViewById(R.id.item_icon);
                    viewHolder.amountText = (TextView) v.findViewById(R.id.amountText);
                    viewHolder.currencyText = (TextView) v.findViewById(R.id.currencyText);
                    viewHolder.mainText = (TextView) v.findViewById(R.id.item_text);
                    viewHolder.subText = (TextView) v.findViewById(R.id.item_subText);
                    v.setTag(viewHolder);
                }
                final TransactionViewHolder transactionViewHolder = (TransactionViewHolder) v.getTag();
                final Utils utils = new Utils();
                final int type = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ENTRYTYPE));
                final int cat = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));
                final int subCat = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBCATEGORY));
                if (subCat != -1) {
                    transactionViewHolder.mainText.setText(utils.categoryGroups(getActivity()).get(type).get(cat).getTransactionSubCategories().get(subCat).getName());
                    new AsyncTask<Void, Void, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.categoryGroups(getActivity()).get(type).get(cat).getTransactionSubCategories().get(subCat).getPicId()), 120, 120, false);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            transactionViewHolder.icon.setImageBitmap(bitmap);
                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_longAnimTime)).playOn(transactionViewHolder.icon);
                        }
                    }.execute();


                } else {
                    transactionViewHolder.mainText.setText(utils.categoryGroups(getActivity()).get(type).get(cat).getName());
                    new AsyncTask<Void, Void, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.categoryGroups(getActivity()).get(type).get(cat).getPicId()), 120, 120, false);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            transactionViewHolder.icon.setImageBitmap(bitmap);
                            YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_longAnimTime)).playOn(transactionViewHolder.icon);
                        }
                    }.execute();
                }
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
                String amountString;
                if (type == 0) {
                    transactionViewHolder.subText.setText(getResources().getString(R.string.expense));
                } else {
                    transactionViewHolder.subText.setText(getResources().getString(R.string.income));
                }
                if (amount > 1000000) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    amountString = decimalFormat.format(amount / 1000000) + " " + getString(R.string.mn);
                } else if (amount > 1000) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    amountString = decimalFormat.format(amount / 1000) + " " + getString(R.string.k);
                } else {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    amountString = decimalFormat.format(amount);
                }
                transactionViewHolder.amountText.setText(amountString);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                transactionViewHolder.currencyText.setText(getResources().getStringArray(R.array.currency_abv)[preferences.getInt("country", 1) - 1]);
                return v;
            }
        }

        private class TransactionViewHolder {
            ImageView icon;
            TextView mainText;
            TextView subText;
            TextView amountText;
            TextView currencyText;
        }


    }


    private void addOneYear() {
        for (int i = 0; i < 12; i++) {
            months.add(new Month(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)));
            calendar.add(Calendar.MONTH, -1);
        }
    }

    private class Month {
        int month;
        int year;

        Month(int month, int year) {
            this.month = month;
            this.year = year;
        }
    }

}
