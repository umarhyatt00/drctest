package com.yeel.drc.model;

public class HomeMenuData {
    private String heading;
    private String type;
    private  int image;

    public HomeMenuData(String heading, String type, int image) {
        this.heading = heading;
        this.type = type;
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
