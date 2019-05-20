package com.iseasoft.iseaiptv.models;

import com.iseasoft.iseaiptv.App;

import java.io.Serializable;

public class M3UItem implements Serializable {

    private String itemDuration;

    private String itemName;

    private String itemUrl;

    private String itemIcon;

    private String itemGroup;

    private int level;

    public String getItemDuration() {
        return itemDuration;
    }

    public void setItemDuration(String itemDuration) {
        this.itemDuration = itemDuration;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isVisible() {
        if (level <= 0) {
            return true;
        }

        int userLevel = App.getUserLevel();
        return userLevel >= level;
    }
}
