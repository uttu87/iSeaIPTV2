package com.iseasoft.iseaiptv.models;

import android.arch.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.ArrayList;

public class Catalog extends ViewModel implements Serializable {
    private int id;
    private String name;
    private String description;
    private ArrayList<Channel> channels;

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

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }
}
