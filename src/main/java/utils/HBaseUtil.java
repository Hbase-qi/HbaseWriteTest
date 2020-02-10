package utils;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseUtil {

    public static Connection connection;
    public static Configuration hbaseConfig;
    public static Admin admin;
    public static Table table;

    private HBaseUtil() {
    }

    static {
        hbaseConfig = new Configuration();
        //HBASE_CONFIG.set("hbase.master", "192.168.230.133:60000");
        hbaseConfig.set("hbase.zookeeper.quorum", "172.16.4.14,172.16.4.15,172.16.4.16,172.16.4.17");
        hbaseConfig.set("hbase.zookeeper.property.clientPort", "2181");
        hbaseConfig.set("hbase.client.write.buffer", "8388608");
        hbaseConfig.set("dfs.socket.timeout", "180000");
    }

    public static void setConnection() {
        try {
            connection = ConnectionFactory.createConnection(hbaseConfig);
        } catch (IOException e) {
            System.err.println("连接hbase失败");
            e.printStackTrace();
        }

    }

    public static Admin getAdmin() {
        if (connection == null) {
            setConnection();
        }
        try {
            return connection.getAdmin();
        } catch (IOException e) {
            System.err.println("获取admin对象失败");
            e.printStackTrace();
            return null;
        }
    }

    public static Table getTable(String tName) {
        if (connection == null) {
            setConnection();
        }
        try {
            return connection.getTable(TableName.valueOf(tName));
        } catch (IOException e) {
            System.err.println("获取表" + tName + "失败");
            e.printStackTrace();
            return null;
        }
    }

    public static void printResult(Result result) {

        while (result.advance()) {
            Cell cell = result.current();
            byte[] rowKey = CellUtil.cloneRow(cell);
            byte[] family = CellUtil.cloneFamily(cell);
            byte[] qualify = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);
            long ts = cell.getTimestamp();
            System.out.println("rowKey:" + Bytes.toString(rowKey) +
                    ",family:" + Bytes.toString(family) +
                    ",qualify:" + Bytes.toString(qualify) +
                    ",timestamp:" + ts +
                    ",value:" + Bytes.toString(value));
        }

    }

    public static void printResult2(Result result) throws IOException {

        CellScanner scanner = result.cellScanner();
        while (scanner.advance()) {
            Cell cell = result.current();
            byte[] rowKey = CellUtil.cloneRow(cell);
            byte[] family = CellUtil.cloneFamily(cell);
            byte[] qualify = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);
            long ts = cell.getTimestamp();
            System.out.println("rowKey:" + Bytes.toString(rowKey) +
                    ",family:" + Bytes.toString(family) +
                    ",qualify:" + Bytes.toString(qualify) +
                    ",timestamp:" + ts +
                    ",value:" + Bytes.toString(value));
        }
    }

    public static void printResult3(Result result) {

        List<Cell> listCells = result.listCells();//容易产生内存溢出
        for (Cell cell : listCells) {
            byte[] rowKey = CellUtil.cloneRow(cell);
            byte[] family = CellUtil.cloneFamily(cell);
            byte[] qualify = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);
            long ts = cell.getTimestamp();
            System.out.println("rowKey:" + Bytes.toString(rowKey) +
                    ",family:" + Bytes.toString(family) +
                    ",qualify:" + Bytes.toString(qualify) +
                    ",timestamp:" + ts +
                    ",value:" + Bytes.toString(value));
        }
    }

    public static void printResultScanner(ResultScanner resultScanner) throws IOException {

        Result result = resultScanner.next();
        while (result != null) {
            printResult3(result);
            result = resultScanner.next();
        }
    }

}
