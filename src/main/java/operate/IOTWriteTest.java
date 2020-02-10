package operate;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import utils.HBaseUtil;
import utils.RowKeyGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IOTWriteTest {

    public static String tableName = "iot_t1";

    public static void bathWrite(Table table, long dataCount) throws IOException {

        List<Put> list = new ArrayList<Put>();
        byte[] buffer = new byte[32];
        Random rand = new Random();
        String[] deviceIds={"1fkw7ff8d8","mvje08f79g","nvhdflgj10","ns8qb57flh","lak893ncga","malf83hv6e","mvndgfi846","vbhg9smgy2","snamfjty74","amfdhuejf8"};
        long createDate=202001070101L;

        for (int i = 0; i < dataCount; i++) {
            String deviceId=deviceIds[rand.nextInt(10)];
            String serviceId="service"+Integer.toString(rand.nextInt(10));
            String cTime=Long.toString(createDate+=5);
            byte[] rowKey=RowKeyGenerator.getRowKey(deviceId,serviceId,cTime);
            Put put = new Put(rowKey);
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("deviceId"), Bytes.toBytes(deviceId));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("serviceId"), Bytes.toBytes(serviceId));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("cTime"), Bytes.toBytes(cTime));
            rand.nextBytes(buffer);
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("data"), buffer);
            list.add(put);
            if (i % 1000 == 0) {
                table.put(list);
                list.clear();
            }
        }
    }

    public static void SingleThreadInsert(long dataCount) throws IOException {
        System.out.println("---------开始SingleThreadInsert测试----------");
        long start = System.currentTimeMillis();
        Table table = HBaseUtil.getTable(tableName);
        bathWrite(table, dataCount);
        table.close();
        long stop = System.currentTimeMillis();
        System.out.println("插入数据：" + dataCount + "共耗时：" + (stop - start) * 1.0 / 1000 + "s");
        System.out.println("---------结束SingleThreadInsert测试----------");
    }

    public static void MultThreadInsert(long dataCount, int theadCount) throws InterruptedException {
        System.out.println("---------开始MultThreadInsert测试----------");
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[theadCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ImportThread(dataCount);
            threads[i].start();
        }
        for (int j = 0; j < threads.length; j++) {
            (threads[j]).join();
        }
        long stop = System.currentTimeMillis();

        System.out.println("MultThreadInsert：" + theadCount * dataCount + "共耗时：" + (stop - start) * 1.0 / 1000 + "s");
        System.out.println("---------结束MultThreadInsert测试----------");
    }

    public static void InsertProcess(Table table, long dataCount) throws IOException {
        long start = System.currentTimeMillis();
        bathWrite(table, dataCount);
        table.close();
        long stop = System.currentTimeMillis();
        System.out.println("线程:" + Thread.currentThread().getId() + "插入数据：" + dataCount + "共耗时：" + (stop - start) * 1.0 / 1000 + "s");
    }

    public static class ImportThread extends Thread {

        Table table;
        long dataCount;
        private ImportThread(long dataCount){
             this.table = HBaseUtil.getTable(tableName);
             this.dataCount=dataCount;
        }
        @Override
        public void run() {
            try {
                InsertProcess(table,dataCount);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.gc();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SingleThreadInsert(1000);

        //MultThreadInsert(1000,100);
    }
}
