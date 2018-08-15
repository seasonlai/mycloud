package com.season.common.util;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2018/5/4.
 */
public class JsonUtils {


    public static String parseBeanToJson(Object o) {

        if (o == null)
            return null;

        Gson gson = getInstance();

        return gson.toJson(o);
    }

    private static Gson getInstance(){
        return holder.gson;
    }

    public static class holder{
        static Gson gson = new Gson();
    }
}
