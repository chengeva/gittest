package co.acaia.acaiaupdater.util;

import io.realm.Realm;

public class RealmUtil {
    public static Realm getRealm(){
        return Realm.getDefaultInstance();
    }

    public static void closeRealm(Realm realm){
        if(!realm.isClosed()){
            realm.close();
        }
    }
}