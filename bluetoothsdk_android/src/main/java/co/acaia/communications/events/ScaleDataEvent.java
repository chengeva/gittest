package co.acaia.communications.events;

/**
 * Created by hanjord on 15/3/17.
 */
public class ScaleDataEvent {
    public static final int data_weight=0;
    public static final int data_battery=1;
    public static final int data_timer=2;
    public static final int data_key=3;

    public double weight=0;
    private double battery_life_val=0;
    private timer_struct timer_val;
    private int data_type=-1;

    private int key=-1;
    public ScaleDataEvent(){

    }
    public timer_struct get_timer_val(){
        return timer_val;
    }
    public int get_data_type(){
        return data_type;
    }
    public double get_Battery_life_val(){
        return battery_life_val;
    }
    public ScaleDataEvent(int data_type_in,int min,int sec, int dse){
        data_type=data_type_in;
        timer_struct timer=new timer_struct(min,sec,dse);
        timer_val=timer;
    }
    public int getKeyVal(){
        return key;
    }
    public ScaleDataEvent(int data_type_in,double data_val){
        data_type=data_type_in;
        switch (data_type_in){
            case data_weight:
                weight=data_val;
                break;
            case data_battery:
                battery_life_val=data_val;
                break;
            case data_key:
                key=(int)data_val;
            default:
                break;
        }
    }
   public class timer_struct{
        public int n_minutes;
        public int n_seconds;
        public int n_dseconds;
        public timer_struct(int min,int sec, int dse){
            n_minutes=min;
            n_seconds=sec;
            n_dseconds=dse;
        }

    }
}
