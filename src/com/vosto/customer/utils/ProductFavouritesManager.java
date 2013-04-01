package com.vosto.customer.utils;

import android.content.Context;

public class ProductFavouritesManager extends FavouritesManager {

    public ProductFavouritesManager(Context context) {
        super(context);
    }

    @Override
    protected String getPreferenceKey() {
        return Constants.SharedPrefs.favouriteProducts;
    }

}
