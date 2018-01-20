package com.tpdevproject.parsers;

import com.google.android.gms.maps.model.LatLng;
import com.tpdevproject.utils.GlobalVars;
import com.tpdevproject.entities.Deal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 27/12/17.
 */

public class Parser {

    public static LatLng parseLocation(JSONObject json) throws JSONException {
        double[] latLng = {0.0,0.0};
        if(json.has("results")){
            JSONObject tmp = json.getJSONArray("results").getJSONObject(0);
            Double lat = tmp.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            Double lng = tmp.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            return new LatLng(lat,lng);
        }
        return null;

    }

    public static List<Deal> getDeals(JSONObject response) throws JSONException{
        List<Deal> listDeal = new ArrayList();
        Iterator<String> keys = response.keys();
        while(keys.hasNext()){
            String key = keys.next();
            listDeal.add(Parser.parseDeal(key, response.getJSONObject(key)));
        }

        return listDeal;
    }
    public static Deal parseDeal(String id, JSONObject json) throws JSONException {
        Deal deal = new Deal();
        deal.setId(id);
        if(json.has(GlobalVars.COLUMN_TITLE))
            deal.setTitle(json.getString(GlobalVars.COLUMN_TITLE));
        if(json.has(GlobalVars.COLUMN_DESCRIPTION))
            deal.setDescription(json.getString(GlobalVars.COLUMN_DESCRIPTION));
        if(json.has(GlobalVars.COLUMN_PRICE_DEAL))
            deal.setPriceDeal(json.getDouble(GlobalVars.COLUMN_PRICE_DEAL));
        if(json.has(GlobalVars.COLUMN_ADDRESS))
            deal.setAddress(json.getString(GlobalVars.COLUMN_ADDRESS));
        if(json.has(GlobalVars.COLUMN_COMMENTAIRES))
            deal.setNumberCommentaires(new Long(json.getJSONObject(GlobalVars.COLUMN_COMMENTAIRES).length()));
        if(json.has(GlobalVars.COLUMN_DATE_POST))
            deal.setDatePost(json.getLong(GlobalVars.COLUMN_DATE_POST));
        if(json.has(GlobalVars.COLUMN_DATE_BEGIN))
            deal.setDateBegin(json.getString(GlobalVars.COLUMN_DATE_BEGIN));
        if(json.has(GlobalVars.COLUMN_DATE_END))
            deal.setDateEnd(json.getString(GlobalVars.COLUMN_DATE_END));
        if(json.has(GlobalVars.COLUMN_IMAGES))
            if(json.getJSONObject(GlobalVars.COLUMN_IMAGES).has(GlobalVars.COLUMN_THUMBNAIL))
                deal.setImage(json.getJSONObject(GlobalVars.COLUMN_IMAGES)
                        .getString(GlobalVars.COLUMN_THUMBNAIL));
        if(json.has(GlobalVars.COLUMN_ORDER)) {
            deal.setOrder(json.getInt(GlobalVars.COLUMN_ORDER));
            deal.setScore(json.getInt(GlobalVars.COLUMN_ORDER)*-1);
        }
        if(json.has(GlobalVars.COLUMN_VOTES))
            deal.setVotes(parseVotes(json.getJSONObject(GlobalVars.COLUMN_VOTES)));
        if(json.has(GlobalVars.COLUMN_PRICE))
            deal.setPrice(json.getDouble(GlobalVars.COLUMN_PRICE));
        if(json.has(GlobalVars.COLUMN_LINK))
            deal.setLink(json.getString(GlobalVars.COLUMN_LINK));
        if(json.has(GlobalVars.COLUMN_USER_ID))
            deal.setUserId(json.getString(GlobalVars.COLUMN_USER_ID));

        return deal;
    }

    private static Map<String,Integer> parseVotes(JSONObject jsonObject) throws JSONException{
        Map<String, Integer> m = new HashMap();
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()){
            String key = keys.next();
            m.put(key, jsonObject.getInt(key) );
        }
        return m;
    }


}
