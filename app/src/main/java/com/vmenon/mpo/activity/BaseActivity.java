package com.vmenon.mpo.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.vmenon.mpo.AppComponent;
import com.vmenon.mpo.MPOApplication;

public class BaseActivity extends AppCompatActivity {
    protected AppComponent getAppComponent() {
        return ((MPOApplication) getApplication()).getAppComponent();
    }
}
