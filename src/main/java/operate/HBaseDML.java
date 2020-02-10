package operate;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import utils.HBaseUtil;


public class HBaseDML {

	private Table table;
	
	public HBaseDML(){
		this.table=HBaseUtil.getTable("bd20:file_info");
	}
	
	public void writeDataToTable() {
		
		Put put=new Put(Bytes.toBytes("1"));
		put.addColumn(Bytes.toBytes("i"), Bytes.toBytes("col1"), Bytes.toBytes("change"));
		put.addColumn(Bytes.toBytes("u"), Bytes.toBytes("ucol1"), Bytes.toBytes(333));
		try {
			table.put(put);
			System.out.println("数据保存成功");
		} catch (IOException e) {
			System.err.println("保存数据失败");
			e.printStackTrace();
		}
	}
	
	public void deleteDataFromTable() {
		
		Delete delete=new Delete(Bytes.toBytes("1"));
		delete.addFamily(Bytes.toBytes("i"));
		delete.addColumn(Bytes.toBytes("i"), Bytes.toBytes("col1"));
		try {
			table.delete(delete);
			System.out.println("删除数据成功");
		} catch (IOException e) {
			System.err.println("删除数据失败");
			e.printStackTrace();
		}
	}
	
	public void getDataFromTable() {
		
		Get get=new Get(Bytes.toBytes("1"));
		try {
			Result result=table.get(get);
			byte[]ucoll= result.getValue(Bytes.toBytes("u"), Bytes.toBytes("ucol1"));
			System.out.println("获取到的数据:"+Bytes.toInt(ucoll));
		} catch (IOException e) {
			System.out.println("get获取数据失败");
			e.printStackTrace();
		}
	}
	
	public void scanDataFromTable() {
		Scan scan=new Scan();
		scan.addFamily(Bytes.toBytes("i"));
		try {
			ResultScanner scanner = table.getScanner(scan);
			Result result = scanner.next();
//			HBaseUtil.printResult(result);
			
			int i=0;
			while (result!=null) {
//				System.out.println("rowkey:"+Bytes.toString(result.getRow())+",u:ucol1,value:"+Bytes.toInt(result.getValue(Bytes.toBytes("u"), Bytes.toBytes("ucol1"))));
				HBaseUtil.printResult(result);
				result=scanner.next();
			}
		} catch (IOException e) {
			System.out.println("扫描数据失败");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		HBaseDML test=new HBaseDML();
//		test.writeDataToTable();
//		test.deleteDataFromTable();
//		test.getDataFromTable();
		test.scanDataFromTable();
	}
}
