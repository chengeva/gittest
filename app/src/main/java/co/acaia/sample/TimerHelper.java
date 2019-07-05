package co.acaia.sample;

/**
 * Created by hanjord on 15/3/17.
 */
public class TimerHelper {
   public static  String secondsToString(int pTime) {
        final int min = pTime / 60;
        final int sec = pTime % 60;

        final String strMin = placeZeroIfNeeded(min);
        final String strSec = placeZeroIfNeeded(sec);
        return String.format("%s:%s",strMin,strSec);
    }

    private static String placeZeroIfNeeded(int number) {
        return (number >=10)? Integer.toString(number):String.format("0%s",Integer.toString(number));
    }

//    private static  String putTimeInXX(String inputDescription,String pTime) {
//        return inputDescription.replace("XX",pTime);
//    }

}
