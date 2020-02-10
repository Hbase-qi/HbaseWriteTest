package operate;

import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import utils.HBaseUtil;
import utils.RowKeyGenerator;

public class IOTReadTest {

    public static void  searchByDate(String deviceId,String serviceId,String startDate,String stopDate) throws Exception {

        byte[] startRowKey=RowKeyGenerator.getRowKey(deviceId,serviceId,startDate);
        byte[] stopRowkey=RowKeyGenerator.getRowKey(deviceId,serviceId,stopDate);
        Scan scan = new Scan();
        scan.withStartRow(startRowKey);
        scan.withStopRow(stopRowkey);
        Table table=HBaseUtil.getTable("iot:T_datacenter_test");
        ResultScanner resultScanner = table.getScanner(scan);
        HBaseUtil.printResultScanner(resultScanner);
        resultScanner.close();
        table.close();
    }

    public static void main(String[] args) throws Exception {
        searchByDate("D143119XSZ","test_service","202001101821","202001102047");
    }
}
