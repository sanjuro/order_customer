package com.vosto.customer.utils;

import android.content.Context;

public class StoreFavouritesManager extends FavouritesManager {

    public StoreFavouritesManager(Context context) {
        super(context);
    }

    @Override
    protected String getPreferenceKey() {
        return Constants.SharedPrefs.favouriteStores;
    }

}
