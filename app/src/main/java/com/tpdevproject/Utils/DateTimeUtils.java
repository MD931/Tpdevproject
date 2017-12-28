package com.tpdevproject.Utils;

import android.icu.text.DateIntervalFormat;
import android.icu.util.DateInterval;

/**
 * Created by root on 26/12/17.
 */

public class DateTimeUtils {

    public static String elapsedTimes(Long startDate, Long endDate){
        Long dif = (endDate - startDate)/1000;
        Long mins = dif/60;
        if(mins > 0){
            Long hours = mins/60;
            if(hours>0){
                Long days = hours/24;
                if(days>0){
                    return days+"d";
                }else{
                    return hours+"h";
                }
            }else{
                return mins+"m";
            }
        }else{
            return dif+"s";
        }
    }
}
