package com.yeel.drc.model;

import java.util.ArrayList;
import java.util.List;

public class HomeMenu {
    private List<HomeMenuData> homeMenuList=new ArrayList<>();

    public HomeMenu(List<HomeMenuData> homeMenuList) {
        this.homeMenuList = homeMenuList;
    }

    public List<HomeMenuData> getHomeMenuList() {
        return homeMenuList;
    }

    public void setHomeMenuList(List<HomeMenuData> homeMenuList) {
        this.homeMenuList = homeMenuList;
    }
}
