package com.yeel.drc.activity.main.fragments.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.search.TransactionSearchActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {
    View view;
    TabLayout mTransactionTabs;
    ViewPager mViewpager;
    AllTransactionFragment tab1;
    CreditTransactionFragment tab2;
    DebitTransactionFragment tab3;
    ViewPagerAdapter adapter;
    Context context;
    ImageView ivSearch;

    public TransactionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(String param1, String param2) {
        TransactionFragment fragment = new TransactionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_transaction, container, false);
        context=getContext();
        initView();
        setItemListeners();
        return view;
    }

    private void initView() {
        mTransactionTabs = view.findViewById(R.id.transactionTabs);
        mViewpager = view.findViewById(R.id.viewpager);
        mViewpager.setOffscreenPageLimit(2);
        setupViewPager(mViewpager);
        mTransactionTabs.setupWithViewPager(mViewpager);
        ivSearch=view.findViewById(R.id.iv_search);
    }

    private void setItemListeners() {
        ivSearch.setOnClickListener(view -> {
            Intent in=new Intent(context, TransactionSearchActivity.class);
            startActivity(in);
        });
    }

    private void setupViewPager(ViewPager mViewpager) {
        try {
            tab1 = new AllTransactionFragment();
            tab2 = new CreditTransactionFragment();
            tab3 = new DebitTransactionFragment();
            adapter = new ViewPagerAdapter(getChildFragmentManager());
            adapter.addFragment(tab1,getString(R.string.all));
            adapter.addFragment(tab2, getString(R.string.money_in));
            adapter.addFragment(tab3, getString(R.string.money_out));
            mViewpager.setAdapter(adapter);
        }catch (Exception e){

        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}