package co.acaia.acaiaupdater;

import android.app.Application;
import android.util.Log;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AcaiaUpdater extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //EventBus.getDefault().register(this);
        Log.v("AcaiaUpdater","Acaia Updater start!");
        Realm.init(getApplicationContext());

        // create your Realm configuration
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .schemaVersion(1) // Must be bumped when the schema changes
//                .migration(new Migration()) // Migration to run instead of throwing an exception
//                .build();
        RealmConfiguration config = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
       // EventBus.getDefault().unregister(this);
    }
}
