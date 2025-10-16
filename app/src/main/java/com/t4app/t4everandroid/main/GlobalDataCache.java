package com.t4app.t4everandroid.main;

import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.QuestionTest;

import java.util.ArrayList;
import java.util.List;

public class GlobalDataCache {
    public static List<LegacyProfile> legacyProfiles = null;
    public static List<QuestionTest> questions = new ArrayList<>();
    public static LegacyProfile legacyProfileSelected = null;

    public static void clearData(){
        legacyProfiles = null;
        legacyProfileSelected = null;
        questions = new ArrayList<>();
    }
}
