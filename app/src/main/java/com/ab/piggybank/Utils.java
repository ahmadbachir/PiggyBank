package com.ab.piggybank;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Utils {

    public int[] debt_relations_icon() {
        int[] ids = {R.drawable.boy, R.drawable.boy1, R.drawable.boy2, R.drawable.boy3, R.drawable.girl, R.drawable.girl1
                , R.drawable.girl2, R.drawable.girl3, R.drawable.girl4, R.drawable.girl5, R.drawable.hipster, R.drawable.man, R.drawable.man1, R.drawable.man2
                , R.drawable.man3, R.drawable.man4, R.drawable.man5, R.drawable.man6, R.drawable.man7, R.drawable.man8, R.drawable.punk, R.drawable.punk, R.drawable.woman,
                R.drawable.woman1, R.drawable.woman2, R.drawable.woman3, R.drawable.woman4, R.drawable.woman5, R.drawable.woman6, R.drawable.woman7, R.drawable.woman8};
        return ids;
    }

    public int[] flagIds() {
        int[] ids = {R.drawable.united_arab_emirates, R.drawable.afghanistan, R.drawable.albania, R.drawable.armenia, R.drawable.netherlands, R.drawable.angola, R.drawable.argentina, R.drawable.australia, R.drawable.aruba, R.drawable.azerbaijan, R.drawable.bosnia_and_herzegovina, R.drawable.barbados, R.drawable.bangladesh, R.drawable.bulgaria, R.drawable.bahrain, R.drawable.burundi, R.drawable.bermuda, R.drawable.brunei, R.drawable.bolivia, R.drawable.brazil, R.drawable.bahamas, R.drawable.bitcoin_icon, R.drawable.bhutan, R.drawable.botswana, R.drawable.belarus, R.drawable.belize, R.drawable.canada, R.drawable.democratic_republic_of_congo, R.drawable.switzerland, R.drawable.chile, R.drawable.chile, R.drawable.china, R.drawable.colombia, R.drawable.costa_rica, R.drawable.cuba, R.drawable.cuba, R.drawable.cape_verde, R.drawable.czech_republic, R.drawable.djibouti, R.drawable.denmark, R.drawable.dominican_republic, R.drawable.algeria, R.drawable.estonia, R.drawable.egypt, R.drawable.eritrea, R.drawable.ethiopia, R.drawable.european_union, R.drawable.fiji, R.drawable.falkland_islands, R.drawable.united_kingdom, R.drawable.georgia, R.drawable.ghana, R.drawable.gibraltar, R.drawable.gambia, R.drawable.guinea, R.drawable.guatemala, R.drawable.hong_kong, R.drawable.honduras, R.drawable.croatia, R.drawable.haiti, R.drawable.hungary, R.drawable.indonesia, R.drawable.isle_of_man, R.drawable.india, R.drawable.iraq, R.drawable.iran, R.drawable.iceland, R.drawable.jersey, R.drawable.jamaica, R.drawable.jordan, R.drawable.japan, R.drawable.kenya, R.drawable.kyrgyzstan, R.drawable.cambodia, R.drawable.comoros, R.drawable.north_korea, R.drawable.south_korea, R.drawable.kwait, R.drawable.cayman_islands, R.drawable.kazakhstan, R.drawable.laos, R.drawable.lebanon, R.drawable.sri_lanka, R.drawable.liberia, R.drawable.lesotho, R.drawable.lithuania, R.drawable.latvia, R.drawable.libya, R.drawable.morocco, R.drawable.moldova, R.drawable.republic_of_macedonia, R.drawable.myanmar, R.drawable.mongolia, R.drawable.mauritania, R.drawable.mauritius, R.drawable.maldives, R.drawable.malawi, R.drawable.mexico, R.drawable.malasya, R.drawable.mozambique, R.drawable.namibia, R.drawable.nigeria, R.drawable.nicaragua, R.drawable.norway, R.drawable.nepal, R.drawable.new_zealand, R.drawable.oman, R.drawable.panama, R.drawable.peru, R.drawable.papua_new_guinea, R.drawable.philippines, R.drawable.pakistan, R.drawable.poland, R.drawable.paraguay, R.drawable.qatar, R.drawable.romania, R.drawable.serbia, R.drawable.russia, R.drawable.rwanda, R.drawable.saudi_arabia, R.drawable.solomon_islands, R.drawable.seychelles, R.drawable.sudan, R.drawable.sweden, R.drawable.singapore, R.drawable.sierra_leone, R.drawable.suriname, R.drawable.sao_tome_and_prince, R.drawable.el_salvador, R.drawable.syria, R.drawable.swaziland, R.drawable.thailand, R.drawable.tajikistan, R.drawable.turkmenistan, R.drawable.tunisia, R.drawable.tonga, R.drawable.turkey, R.drawable.trinidad_and_tobago, R.drawable.taiwan, R.drawable.tanzania, R.drawable.ukraine, R.drawable.uganda, R.drawable.united_states, R.drawable.uruguay, R.drawable.uzbekistn, R.drawable.venezuela, R.drawable.vietnam, R.drawable.vanuatu, R.drawable.samoa, R.drawable.central_african_republic, R.drawable.yemen, R.drawable.south_africa, R.drawable.zambia, R.drawable.zimbabwe};

        return ids;
    }

    public int[] paymentMethodIcons() {
        int[] ids = {R.drawable.visa_pay_logo, R.drawable.visa_pay_logo, R.drawable.master_card, R.drawable.master_card, R.drawable.google_wallet, R.drawable.paypal_logo, R.drawable.cheque, R.drawable.apple, R.drawable.discover, R.drawable.wire_transfer_logo, R.drawable.question_mark};
        return ids;
    }

    public ArrayList<ArrayList<TransactionCategory>> categoryGroups(Context context) {
        ArrayList<ArrayList<TransactionCategory>> categoryGroups = new ArrayList<>();
        categoryGroups.add(expenseGroups(context));
        categoryGroups.add(incomeGroups(context));
        return categoryGroups;
    }

    public ArrayList<TransactionCategory> expenseGroups(Context context) {
        ArrayList<TransactionCategory> transactionCategories = new ArrayList<>();
        transactionCategories.add(gift(context));
        transactionCategories.add(charity(context));
        transactionCategories.add(food(context));
        transactionCategories.add(shelter(context));
        transactionCategories.add(bills(context));
        transactionCategories.add(clothing(context));
        transactionCategories.add(transportation(context));
        transactionCategories.add(medical(context));
        transactionCategories.add(insurance(context));
        transactionCategories.add(personal(context));
        transactionCategories.add(debtReduction(context));
        transactionCategories.add(retirement(context));
        transactionCategories.add(education(context));
        transactionCategories.add(savings(context));
        transactionCategories.add(leisure(context));
        Collections.sort(transactionCategories, new Comparator<TransactionCategory>() {
            @Override
            public int compare(TransactionCategory o1, TransactionCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        transactionCategories.add(new TransactionCategory(16, new ArrayList<TransactionSubCategory>(), context.getResources().getStringArray(R.array.expenseGroups)[16], R.drawable.question_mark));
        return transactionCategories;
    }

    public TransactionCategory gift(Context context) {
        String name = context.getResources().getStringArray(R.array.expenseGroups)[0];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        TransactionCategory transactionCategory = new TransactionCategory(0, transactionSubCategories, name, R.drawable.giftbox);
        return transactionCategory;
    }

    public TransactionCategory charity(Context context) {
        String name = context.getResources().getStringArray(R.array.expenseGroups)[1];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        TransactionCategory transactionCategory = new TransactionCategory(1, transactionSubCategories, name, R.drawable.charity);
        return transactionCategory;
    }

    public TransactionCategory food(Context context) {
        String name = context.getResources().getStringArray(R.array.expenseGroups)[2];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.foodChildren);
        int[] pics = {R.drawable.grocery, R.drawable.chef, R.drawable.bones};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(2, transactionSubCategories, name, R.drawable.cutlery);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory shelter(Context context) {
        int catId = 3;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.shelterChildren);
        int[] pics = {R.drawable.mortgage_loan, R.drawable.rent, R.drawable.tax, R.drawable.tools};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.home);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory bills(Context context) {
        int catId = 4;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.billsChildren);
        int[] pics = {R.drawable.light_bulb, R.drawable.faucet, R.drawable.phone, R.drawable.television, R.drawable.wifi};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.bill);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory clothing(Context context) {
        int catId = 5;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.shirt);
        return transactionCategory;
    }

    public TransactionCategory transportation(Context context) {
        int catId = 6;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.transportationChildren);
        int[] pics = {R.drawable.gasoline_pump, R.drawable.bus, R.drawable.car_oil, R.drawable.parking, R.drawable.tools, R.drawable.papers};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.car);
        return transactionCategory;
    }

    public TransactionCategory medical(Context context) {
        int catId = 7;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.medicalChildren);
        int[] pics = {R.drawable.nurse, R.drawable.dentist, R.drawable.microscope, R.drawable.pill, R.drawable.plaster};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.hospital);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory insurance(Context context) {
        int catId = 8;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.insuranceChildren);
        int[] pics = {R.drawable.hospital, R.drawable.home, R.drawable.rent, R.drawable.car, R.drawable.pulse, R.drawable.wheelchair};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.shield);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory personal(Context context) {
        int catId = 10;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.personalChildren);
        int[] pics = {R.drawable.dumbbell, R.drawable.hairdressing, R.drawable.hairdryer, R.drawable.mascara, R.drawable.baby_stroller, R.drawable.smiling_baby};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.user);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory debtReduction(Context context) {
        int catId = 11;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.debtReductionChildren);
        int[] pics = {R.drawable.mortgage_loan, R.drawable.credit_card, R.drawable.cheque, R.drawable.tuition};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.loss);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory retirement(Context context) {
        int catId = 12;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.retirementChildren);
        int[] pics = {R.drawable.planning, R.drawable.investment};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.retirement);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory education(Context context) {
        int catId = 13;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.educationChildren);
        int[] pics = {R.drawable.mortarboard, R.drawable.school_supplies, R.drawable.notebook, R.drawable.lecture};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.school);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory savings(Context context) {
        int catId = 14;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.savingsChildren);
        int[] pics = {R.drawable.siren};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.piggy_bank);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public TransactionCategory leisure(Context context) {
        int catId = 15;
        String name = context.getResources().getStringArray(R.array.expenseGroups)[catId];
        ArrayList<TransactionSubCategory> transactionSubCategories = new ArrayList<>();
        String[] children = context.getResources().getStringArray(R.array.leisureChildren);
        int[] pics = {R.drawable.pedestrian, R.drawable.game, R.drawable.vacation, R.drawable.mask, R.drawable.shopping_bag};
        for (int i = 0; i < children.length; i++) {
            transactionSubCategories.add(new TransactionSubCategory(i, children[i], pics[i]));
        }
        TransactionCategory transactionCategory = new TransactionCategory(catId, transactionSubCategories, name, R.drawable.smile);
        Collections.sort(transactionSubCategories, new Comparator<TransactionSubCategory>() {
            @Override
            public int compare(TransactionSubCategory o1, TransactionSubCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return transactionCategory;
    }

    public ArrayList<TransactionCategory> incomeGroups(Context context) {
        ArrayList<TransactionCategory> incomeGroups = new ArrayList<>();
        String[] names = context.getResources().getStringArray(R.array.incomeGroups);
        int[] pics = {R.drawable.cheque, R.drawable.bill, R.drawable.dividend, R.drawable.profits, R.drawable.retirement};
        for (int i = 0; i < names.length; i++) {
            incomeGroups.add(new TransactionCategory(i, new ArrayList<TransactionSubCategory>(), names[i], pics[i]));
        }

        Collections.sort(incomeGroups, new Comparator<TransactionCategory>() {
            @Override
            public int compare(TransactionCategory o1, TransactionCategory o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        return incomeGroups;
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }


}
