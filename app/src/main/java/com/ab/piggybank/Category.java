package com.ab.piggybank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.piggybank.activity.AddTransactionActivity;

import java.util.ArrayList;

public class Category extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_close);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ViewPager viewPager = (ViewPager) findViewById(R.id.categoryViewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.categoryTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, AddTransactionActivity.class);
            if (getIntent().getExtras() != null) {
                i.putExtras(getIntent().getExtras());
            }
            startActivity(i);

        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return  getString(R.string.expense);
            }
            else {
                return getString(R.string.income);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class fragment extends Fragment {
        int lastExpandedPos = -1;

        public static fragment newInstance(int pos) {

            Bundle args = new Bundle();
            args.putInt("pos", pos);
            fragment fragment = new fragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.category_list, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            final AnimatedExpandableListView list = (AnimatedExpandableListView) view.findViewById(R.id.list);
            Utils utils = new Utils();
            final ListAdapter listAdapter;
            if (getArguments().getInt("pos") == 0) {
                listAdapter = new ListAdapter(utils.expenseGroups(getActivity()));
            }
            else {
                listAdapter = new ListAdapter(utils.incomeGroups(getActivity()));
            }
            list.setAdapter(listAdapter);
            list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (listAdapter.getChildrenCount(groupPosition) == 0) {
                        Intent i = new Intent(getActivity(), AddTransactionActivity.class);
                        if (getActivity().getIntent().getExtras() != null) {
                            i.putExtras(getActivity().getIntent().getExtras());
                        }
                        i.putExtra("type", getArguments().getInt("pos"));
                        i.putExtra("cat", groupPosition);
                        i.putExtra("subCat", -1);
                        startActivity(i);
                    } else {
                        if (list.isGroupExpanded(groupPosition)) {
                            list.collapseGroupWithAnimation(groupPosition);
                        } else {
                            list.expandGroupWithAnimation(groupPosition);
                        }
                    }
                    return true;
                }
            });
            list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Intent i = new Intent(getActivity(), AddTransactionActivity.class);
                    if (getActivity().getIntent().getExtras() != null) {
                        i.putExtras(getActivity().getIntent().getExtras());
                    }

                    i.putExtra("type", getArguments().getInt("pos"));
                    i.putExtra("cat", groupPosition);
                    i.putExtra("subCat", childPosition);
                    startActivity(i);
                    return true;
                }
            });
            list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (lastExpandedPos != -1 && groupPosition != lastExpandedPos) {
                        list.collapseGroupWithAnimation(lastExpandedPos);
                    }
                    lastExpandedPos = groupPosition;
                }
            });
        }

        class ListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
            ArrayList<TransactionCategory> transactionCategories;

            public ListAdapter(ArrayList<TransactionCategory> transactionCategories) {
                this.transactionCategories = transactionCategories;
            }

            @Override
            public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.category_child_layout, parent, false);
                    ChildViewHolder viewHolder = new ChildViewHolder();
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.catIcon);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.catName);
                    convertView.setTag(viewHolder);
                }
                TransactionSubCategory cat = (TransactionSubCategory) getChild(groupPosition, childPosition);
                ChildViewHolder viewHolder = (ChildViewHolder) convertView.getTag();
                viewHolder.name.setText(cat.name);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), cat.picId);
                viewHolder.icon.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));

                return convertView;
            }

            @Override
            public int getRealChildrenCount(int groupPosition) {
                return transactionCategories.get(groupPosition).transactionSubCategories.size();
            }

            @Override
            public int getGroupCount() {
                return transactionCategories.size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return transactionCategories.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return transactionCategories.get(groupPosition).transactionSubCategories.get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return transactionCategories.get(groupPosition).id;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return transactionCategories.get(groupPosition).transactionSubCategories.get(childPosition).id;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.category_group_layout, parent, false);
                    GroupViewHolder viewHolder = new GroupViewHolder();
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.catIcon);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.catName);
                    viewHolder.indicator = (ImageView) convertView.findViewById(R.id.indicator);
                    convertView.setTag(viewHolder);
                }
                TransactionCategory cat = (TransactionCategory) getGroup(groupPosition);
                GroupViewHolder viewHolder = (GroupViewHolder) convertView.getTag();
                viewHolder.name.setText(cat.name);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), cat.picId);
                viewHolder.icon.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
                if (getChildrenCount(groupPosition) != 0) {
                    viewHolder.indicator.setImageResource(isExpanded ? R.drawable.ic_action_up : R.drawable.ic_action_down);
                } else {
                    viewHolder.indicator.setImageResource(R.drawable.ic_action_empty_group);
                }
                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

            private class GroupViewHolder {
                ImageView icon;
                TextView name;
                ImageView indicator;
            }

            private class ChildViewHolder {
                ImageView icon;
                TextView name;
            }
        }
    }

}
