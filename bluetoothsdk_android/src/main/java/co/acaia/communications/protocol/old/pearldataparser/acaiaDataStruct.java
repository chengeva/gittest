package co.acaia.communications.protocol.old.pearldataparser;

import j2me.nio.ByteBuffer;
import j2me.nio.ByteOrder;
import javolution.io.Struct;

/**
 * Created by hanjord on 15/9/22.
 * Base class for struct objects
 */

public class acaiaDataStruct extends Struct {

    public acaiaDataStruct(){

    }
    public acaiaDataStruct(byte[] b) {
        this.setByteBuffer(ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN), 0);
    }

    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }

    public void debug() {

    }
}
