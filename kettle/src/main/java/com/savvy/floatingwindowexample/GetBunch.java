package com.savvy.floatingwindowexample;


import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Administrator on 2016/8/15.
 */
public class GetBunch {
    private static final String TAG = "*** GetBunch";
    private static final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    private boolean idata = true;
    /**
     * get current date and time as String
     * @return
     */
    public void GetBunch() {}

    public boolean getData() {
        final GregorianCalendar now = new GregorianCalendar();
        int year = now.get(GregorianCalendar.YEAR);
        int month = now.get(GregorianCalendar.MONTH) + 1;
        int day = now.get(GregorianCalendar.DAY_OF_MONTH);

        if(year == 2016 && month == 8 && day <= 20){
            idata = false;
        }

        return idata;
    }
}
