package com.t4app.t4everandroid.main.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.t4app.t4everandroid.main.Models.LegacyProfile;

public class MainViewModel extends ViewModel {

    private MutableLiveData<LegacyProfile> mutableLegacyProfile = new MutableLiveData<>();
    private MutableLiveData<String> mutableSessionId = new MutableLiveData<>();

    public MutableLiveData<LegacyProfile> getMutableLegacyProfile() {
        return mutableLegacyProfile;
    }

    public void setMutableLegacyProfile(LegacyProfile legacyProfile) {
        mutableLegacyProfile.setValue(legacyProfile);
    }

    public MutableLiveData<String> getMutableSessionId() {
        return mutableSessionId;
    }

    public void setMutableSessionId(String sessionId) {
        mutableSessionId.setValue(sessionId);
    }
}
