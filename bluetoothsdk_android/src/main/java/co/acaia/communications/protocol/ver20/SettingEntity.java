package co.acaia.communications.protocol.ver20;

/**
 * Created by hanjord on 15/3/17.
 */
public class SettingEntity {
    private short ln_item;
    private short ln_value;
    public SettingEntity(short item,short value){
        ln_item=item;
        ln_value=value;
    }
    public short getItem(){
        return ln_item;
    }
    public short getValue(){
        return ln_value;
    }
}
