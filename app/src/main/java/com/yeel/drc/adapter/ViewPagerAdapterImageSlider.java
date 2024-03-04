package com.yeel.drc.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.yeel.drc.R;
import com.yeel.drc.model.SliderImagesModel;
import java.util.List;

public class ViewPagerAdapterImageSlider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    List<SliderImagesModel> imagesList;
    public ItemClickAdapterListener onClickListener;


    public ViewPagerAdapterImageSlider(Context context, List<SliderImagesModel> imagesList, ItemClickAdapterListener onClickListener) {
        this.context = context;
        this.imagesList = imagesList;
        this.onClickListener = onClickListener;

    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout_image_slider, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_flag_country_code);
        String imageURL=imagesList.get(position).getImageUrl();
        Glide.with(context)
                .load(imageURL)
                .into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.itemClick(view, position);
            }
        });
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
    }
}
