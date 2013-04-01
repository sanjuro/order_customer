package com.vosto.customer.utils;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public abstract class FavouritesManager {

    private Context context;

    private final Object lock = new Object();

    public FavouritesManager(Context context) {
        this.context = context;
    }

    public void add(String id) {
        synchronized (lock) {
            Set<String> ids = load();
            ids.add(id);
            save(ids);
        }
    }

    public void remove(String id) {
        synchronized (lock) {
            Set<String> ids = load();
            ids.remove(id);
            save(ids);
        }
    }

    public boolean isFavourite(String id) {
        Set<String> ids = load();
        return ids.contains(id);
    }

    private void save(Set<String> values) {
        values.remove("");
        values.remove(" ");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String raw = Joiner.on(',').join(values);
        prefs.edit().putString(getPreferenceKey(), raw).commit();
    }

    public Set<String> load() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String raw = prefs.getString(getPreferenceKey(), new String());
        Set<String> ids = new HashSet<String>();
        for (String value : Splitter.on(',').split(raw)) {
            if (value.equals("") || value.equals(" ")) {
                continue;
            }
            ids.add(value);
        }
        return ids;
    }

    protected abstract String getPreferenceKey();
}
