package utils;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.nio.ByteBuffer;

public class RowKeyGenerator {

    public static final byte BLANK_STR = 0x1F;
    public static final byte ZERO = 0x00;
    public static final byte ONE = 0x01;

    public static byte[] coverLeft(String source, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(Bytes.toBytes(source));
        while (buffer.hasRemaining()) {
            buffer.put(BLANK_STR);
        }
        return buffer.array();
    }

    public static byte[] getRowKey(String deviceId,String serviceId,String cTime){

        //deviceID+cTime虽然能保证唯一性，但不能使用其hash值作为散列值。startDate,与stopDate
        //会得到两条散列值不同的rowKey.在查询时，根据定值查询的字段必须前边,范围查询放后边，例如时间字段
        String hashStr=MD5Hash.getMD5AsHex(deviceId.getBytes()).substring(0, 8);
        String deviceHashStr= hashStr+deviceId;
        byte[] deviceByte=coverLeft(deviceHashStr,20);
        byte[] serviceByte=coverLeft(serviceId,12);
        byte[] cTimeByte=Bytes.toBytes(cTime);
        ByteBuffer rowKeyBuffer=ByteBuffer.allocate(44);
        rowKeyBuffer.put(deviceByte);
        rowKeyBuffer.put(serviceByte);
        rowKeyBuffer.put(cTimeByte);
        return rowKeyBuffer.array();
    }
}
