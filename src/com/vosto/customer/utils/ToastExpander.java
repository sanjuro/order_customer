package com.vosto.customer.utils;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/03/31
 * Time: 8:00 PM
 * To change this template use File | Settings | File Templates.
 */

import android.util.Log;
import android.widget.Toast;

public class ToastExpander {

    public static final String TAG = "ToastExpander";

    public static void showFor(final Toast aToast, final long durationInMilliseconds) {

        aToast.setDuration(Toast.LENGTH_SHORT);

        Thread t = new Thread() {
            long timeElapsed = 0l;

            public void run() {
                try {
                    while (timeElapsed <= durationInMilliseconds) {
                        long start = System.currentTimeMillis();
                        aToast.show();
                        sleep(1750);
                        timeElapsed += System.currentTimeMillis() - start;
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }
            }
        };
        t.start();
    }
}
