package co.acaia.communications.protocol.old.pearldataparser;
import javolution.io.Struct;

/**
 * Created by hanjord on 15/9/15.
 */
public class ByteDataHelper {
    public static int fourBytesToUInt(Struct.Unsigned8 byte1, Struct.Unsigned8 byte2, Struct.Unsigned8 byte3, Struct.Unsigned8 byte4) {
        int intReturn = 0;
        int int1=byte1.get();
        int int2=byte2.get();
        int int3=byte3.get();
        int int4=byte4.get();
        intReturn = int4 << 24 | int3 << 16 | int2 << 8 | int1;
        return intReturn;
    }

    public static int getUnsignedByte(byte in) {
        return in & 0xFF;
    }

    public static void getUnsignedArray(byte[] s_in) {
        for (int i = 0; i != s_in.length; i++) {
            s_in[i] = (byte) ByteDataHelper.getUnsignedByte(s_in[i]);
        }
    }

    public static byte[] getByteArrayFromU1(Struct.Unsigned8[] s_param, int start, int end) {
        //0 4
        // 0 1 2 3
        int len = end;
        int get_byte_pt = 0;
        byte[] getByteArray = new byte[len];
        for (int i = start; i != start + end; i++) {

            getByteArray[get_byte_pt] = (byte) (s_param[i].get());
            get_byte_pt++;
        }
        return getByteArray;
    }

}
