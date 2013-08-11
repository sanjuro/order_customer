/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vosto.customer.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = "http://107.22.211.58:9000/api/v1";

    /**
     * IMAGE URL where all image resources are stored
     */
    public static final String STORE_IMAGE_SERVER_URL = "http://m.vosto.co.za/stores";

    /**
     * IMAGE URL where all image resources are stored
     */
    public static final String DEAL_IMAGE_SERVER_URL = "http://m.vosto.co.za/deals";

    /**
     * Google API project id registered to use GCM.
     * 1091536520954 - shadley's account
     * 263607631818 - flippie
     */
    public static final String SENDER_ID = "1091536520954";

    /**
     * Google API project id registered to use GCM.
     * 1091536520954 - shadley's account
     * 263607631818 - flippie
     */
    public static final String SERVER_AUTHENTICATION_TOKEN = "DXTTTTED2ASDBSD3";


    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
