package com.tpdevproject.parsers;

import com.google.android.gms.maps.model.LatLng;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
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

    public static Annonce parseAnnonce(String id, JSONObject json) throws JSONException {
        Annonce annonce = new Annonce();
        annonce.setId(id);
        if(json.has(Database.COLUMN_TITLE))
            annonce.setTitle(json.getString(Database.COLUMN_TITLE));
        if(json.has(Database.COLUMN_DESCRIPTION))
            annonce.setDescription(json.getString(Database.COLUMN_DESCRIPTION));
        if(json.has(Database.COLUMN_PRICE_DEAL))
            annonce.setPriceDeal(json.getDouble(Database.COLUMN_PRICE_DEAL));
        if(json.has(Database.COLUMN_ADDRESS))
            annonce.setAddress(json.getString(Database.COLUMN_ADDRESS));
        if(json.has(Database.COLUMN_DATE_POST))
            annonce.setDatePost(json.getLong(Database.COLUMN_DATE_POST));
        if(json.has(Database.COLUMN_DATE_BEGIN))
            annonce.setDateBegin(json.getString(Database.COLUMN_DATE_BEGIN));
        if(json.has(Database.COLUMN_DATE_END))
            annonce.setDateEnd(json.getString(Database.COLUMN_DATE_END));
        if(json.has(Database.COLUMN_IMAGES))
            if(json.getJSONObject(Database.COLUMN_IMAGES).has(Database.COLUMN_THUMBNAIL))
                annonce.setImage(json.getJSONObject(Database.COLUMN_IMAGES)
                        .getString(Database.COLUMN_THUMBNAIL));
        if(json.has(Database.COLUMN_ORDER)) {
            annonce.setOrder(json.getInt(Database.COLUMN_ORDER));
            annonce.setScore(json.getInt(Database.COLUMN_ORDER)*-1);
        }
        if(json.has(Database.COLUMN_VOTES))
            annonce.setVotes(parseVotes(json.getJSONObject(Database.COLUMN_VOTES)));
        if(json.has(Database.COLUMN_PRICE))
            annonce.setPrice(json.getDouble(Database.COLUMN_PRICE));
        if(json.has(Database.COLUMN_LINK))
            annonce.setLink(json.getString(Database.COLUMN_LINK));
        if(json.has(Database.COLUMN_USER_ID))
            annonce.setUserId(json.getString(Database.COLUMN_USER_ID));

        return annonce;
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
