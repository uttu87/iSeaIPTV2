package com.iseasoft.iseaiptv.models;

import android.arch.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.ArrayList;

public class Catalog extends ViewModel implements Serializable {
    private int id;
    private String name;
    private String description;
    private ArrayList<M3UItem> channels;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<M3UItem> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<M3UItem> channels) {
        this.channels = channels;
    }
}
