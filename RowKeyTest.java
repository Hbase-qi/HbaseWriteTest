import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import utils.UUIDUtils;

class Scratch {
    public static void main(String[] args) {
        long currentId = 2L;
        byte [] rowkey = Bytes.add(MD5Hash.getMD5AsHex(Bytes.toBytes(currentId)).substring(0, 8).getBytes(),
                Bytes.toBytes(currentId));

        //System.out.println(StringUtils.byteToHexString(rowkey));

        for (int i = 0; i <100 ; i++) {
            System.out.println(UUIDUtils.getUUID());
        }

    }
}