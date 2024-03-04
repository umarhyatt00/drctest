package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yeel.drc.R;
import com.yeel.drc.model.HomeMenu;

import java.util.List;

public class ViewPagerAdapterHomeSlider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    List<HomeMenu> homeMenuList;
    public ItemClickAdapterListener onClickListener;


    public ViewPagerAdapterHomeSlider(Context context, List<HomeMenu> homeMenuList, ItemClickAdapterListener onClickListener) {
        this.context = context;
        this.homeMenuList = homeMenuList;
        this.onClickListener = onClickListener;

    }

    @Override
    public int getCount() {
        return homeMenuList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.home_menu_slider, null);
        RecyclerView RVMenu=view.findViewById(R.id.RVMenu);
        HomeMenuItemAdapter homeMenuItemAdapter=new HomeMenuItemAdapter(homeMenuList.get(position).getHomeMenuList(), context, new HomeMenuItemAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(int innerPosition) {
                 onClickListener.itemClick(position,innerPosition);
            }
        });
        RVMenu.setAdapter(homeMenuItemAdapter);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    public interface ItemClickAdapterListener {
        void itemClick(int position,int innerPosition);
    }
}
