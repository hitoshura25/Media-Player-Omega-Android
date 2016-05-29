package com.vmenon.mpo;

import android.app.Application;

public class MPOApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.appComponent = DaggerAppComponent.builder().appModule(
                new AppModule((this))).build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
