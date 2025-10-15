package com.t4app.t4everandroid.main;

import com.t4app.t4everandroid.main.Models.LegacyProfile;

import java.util.List;

public class GlobalDataCache {
    public static List<LegacyProfile> legacyProfiles = null;

    public static void clearData(){
        legacyProfiles = null;
    }
}
