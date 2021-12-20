package co.acaia.acaiaupdater;

import android.app.Application;
import android.util.Log;
import android.view.View;

import co.acaia.acaiaupdater.Events.ConnectionEvent;
import co.acaia.acaiaupdater.Events.UpdateISPEvent;
import co.acaia.acaiaupdater.entity.AcaiaFirmware;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.firmwarelunar.IspHelper;
import co.acaia.ble.events.ScaleConnectedEvent;
import co.acaia.communications.events.ScaleDataEvent;
import co.acaia.communications.events.ScaleFirmwareVersionEvent;
import co.acaia.communications.events.WeightEvent;
import co.acaia.communications.scaleevent.NewScaleConnectionStateEvent;
import co.acaia.communications.scaleevent.ProtocolModeEvent;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEvent;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AcaiaUpdater extends Application {
    public static AcaiaDevice currentAcaiaDevice;
    public static AcaiaFirmware currentFirmware;
    public static String currentConnectedDeviceVersion;

    // Improve this into Singleton in the future, but not now...
    public static IspHelper ispHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        //EventBus.getDefault().register(this);
        //// Log.v("AcaiaUpdater","Acaia Updater start!");
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
        EventBus.getDefault().register(this);
    }

    public void onEvent(final WeightEvent event) {

    }

    public void onEvent(final NoSubscriberEvent event) {

    }

    public void onEvent(final ScaleSettingUpdateEvent event) {

    }

    public void onEvent(final UpdateISPEvent event) {

    }

    public void onEvent(final ScaleFirmwareVersionEvent event) {

    }

    public void onEvent(final ProtocolModeEvent event) {

    }

    public void onEvent(final NewScaleConnectionStateEvent event) {

    }

    public void onEvent(final ScaleConnectedEvent event) {

    }

    public void onEvent(final ConnectionEvent event) {

    }

    public void onEvent(final ScaleDataEvent event) {

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }
}
