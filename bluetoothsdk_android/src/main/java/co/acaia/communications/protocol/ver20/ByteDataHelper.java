package co.acaia.communications.protocol.ver20;

import co.acaia.communications.CommLogger;
import javolution.io.Struct;

/**
 * Created by hanjord on 15/3/24.
 */
public class ByteDataHelper {
    public static byte[] getByteArrayFromU1(Struct.Unsigned8[] s_param,int start,int end){
        //0 4
        // 0 1 2 3
        int len=end;
        int get_byte_pt=0;
        byte[] getByteArray=new byte[len];
        for(int i=start;i!=start+end;i++){

            getByteArray[get_byte_pt]=(byte)(s_param[i].get());
            get_byte_pt++;
        }

        return getByteArray;
    }
    public static int getUnsignedByte(byte in){
        // warning: need to be tested...
        return in& 0xFF;
    }
    public static int left_shift_8(int in){
        return in<<8;
    }

    public static int getUnsignedShort(short in){
        return in& 0xffff;
    }

    public static int calc_sum(Struct.Unsigned8[] s_in, Struct.Unsigned8 n_len)
    {
        // warning: need to be tested
        short ln_loop = 0;
        short ln_sum1 = 0, ln_sum2 = 0;
        int ln_sum = 0;
        int lb_odd = 1;
//        CommLogger.logv("ByteDataHelper","n_len len="+String.valueOf(n_len));
        if(n_len.get()>=20){
            return 0;
        }
        for (ln_loop = 0; ln_loop < n_len.get(); ln_loop++) {
            //System.out.println("debug"+s_in.struct().getByteBuffer().toString());
            if (lb_odd == 1){
                ln_sum1 += s_in[ln_loop].get();
            }
            else{
                ln_sum2 += s_in[ln_loop].get();
            }
            if (lb_odd == 1)
                lb_odd = 0;
            else
                lb_odd = 1;
        }
        ln_sum = (ln_sum1 & 0xff) << 8 | (ln_sum2 & 0xff);
        return getUnsignedShort((short)ln_sum);
    }

    public static  Struct.Unsigned8 u_short_to_u_char(Struct.Unsigned16 in){
        ScaleProtocol.convert_struct convertstruct=new ScaleProtocol.convert_struct();
        convertstruct.temp_16.set(in.get());
        convertstruct.temp_8.set((short)convertstruct.temp_16.get());
        return convertstruct.temp_8;
    }

    public static void reverseBitsInByteArray(byte[] data){
        for(int i=0;i!=data.length;i++){
            data[i]=reverseBitsByte(data[i]);
        }

    }
    public static byte reverseBitsByte(byte x) {
        int intSize = 8;
        byte y=0;
        for(int position=intSize-1; position>0; position--){
            y+=((x&1)<<position);
            x >>= 1;
        }
        return y;
    }

   public static int getBit(byte[] data, int pos) {
        int posByte = pos/8;
        int posBit = pos%8;
        byte valByte = data[posByte];
        int valInt = valByte>>(8-(posBit+1)) & 0x0001;
        return valInt;
    }

    public static int byteArrayToInt(byte[] b) {
        if (b.length == 4)
            return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
                    | (b[3] & 0xff);
        else if (b.length == 2)
            return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

        return 0;
    }
}
