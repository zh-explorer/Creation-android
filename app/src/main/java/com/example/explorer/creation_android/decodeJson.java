package com.example.explorer.creation_android;

import com.google.gson.Gson;

/**
 * Created by explorer on 15-11-24.
 */
public class decodeJson {
    public static Data decode(String jsonData) {
        Gson gson = new Gson();
        Data data = gson.fromJson(jsonData, Data.class);
        return data;
    }
}
