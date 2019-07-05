package co.acaia.firmwaretool.common;

/**
 * Created by hanjord on 15/8/13.
 */
public class DataVarifyer {
    public static final String TAG="DataVarifyer";
    public static void logOutput(byte data[]){
        for(int i=0;i!=data.length;i++){
            //Log.d(TAG,"["+String.valueOf(i)+"]=["+String.valueOf(data[i])+"]");
        }
    }
}
