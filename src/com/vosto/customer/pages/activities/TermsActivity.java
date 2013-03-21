package com.vosto.customer.pages.activities;

import java.io.InputStream;
import android.os.Bundle;
import android.content.res.Resources;
import android.widget.TextView;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;


/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/03/19
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TermsActivity extends VostoBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        TextView largeText = (TextView)findViewById(R.id.largeText);

        try { Resources res = getResources(); InputStream in_s = res.openRawResource(R.raw.terms);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            largeText.setText(new String(b));
        } catch (Exception e) {
            // e.printStackTrace();
            largeText.setText("Error: can't show terms.");
        }
    }
}
