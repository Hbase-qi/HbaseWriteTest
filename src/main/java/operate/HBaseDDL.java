package operate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import utils.HBaseUtil;

public class HBaseDDL {

	public Table geTable(String tableName) {
		return HBaseUtil.getTable(tableName);
	}

	public void deleteTable(String tableName) {

		TableName tabName = TableName.valueOf(tableName);

		try {
			Admin admin = HBaseUtil.getAdmin();
			admin.disableTable(tabName);
			admin.deleteTable(tabName);
			admin.close();
			System.out.println("删除成功");

		} catch (Exception e) {
			System.out.println("删除失败");
			e.printStackTrace();
		}

	}

	public void createNamespace(String namespace) {
		Admin admin = HBaseUtil.getAdmin();
		try {
			admin.createNamespace(NamespaceDescriptor.create(namespace).build());
			admin.close();
			System.out.println("namespace创建成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void printTables() throws IOException {

		Admin admin = HBaseUtil.getAdmin();
		HTableDescriptor[] tablesDes = admin.listTables();
		for (int i = 0; i < tablesDes.length; i++) {
			System.out.println(tablesDes[i].getNameAsString());
		}
		admin.close();
	}

	public void isTableExists(String table) {

		TableName tName = TableName.valueOf(table);
		Admin admin = HBaseUtil.getAdmin();
		try {
			boolean tableExists = admin.tableExists(tName);
			System.out.println(tableExists);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listNamespaceTables(String namespace) {
		Admin admin = HBaseUtil.getAdmin();
		try {
			TableName[] listTableNamesByNamespace = admin.listTableNamesByNamespace(namespace);
			for (TableName tableName : listTableNamesByNamespace) {
				System.out.println(tableName.getNameWithNamespaceInclAsString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void describeTable(String tName) {
		Admin admin = HBaseUtil.getAdmin();
		TableName tableName = TableName.valueOf(tName);
		try {
			HTableDescriptor tableDescriptor = admin.getTableDescriptor(tableName);
			System.out.println(tableDescriptor.getNameAsString() + "," + tableDescriptor.getColumnFamilies());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insertMany() {

		Table table = geTable("bd20:fromjava4");
		List<Put> list = new ArrayList<Put>();

		Put put = new Put(Bytes.toBytes("1"));
		put.addColumn(Bytes.toBytes("i"), Bytes.toBytes("col1"), Bytes.toBytes("change"));
		put.addColumn(Bytes.toBytes("u"), Bytes.toBytes("ucol1"), Bytes.toBytes(333));
		list.add(put);
		Put put2 = new Put(Bytes.toBytes("2"));
		put2.addColumn(Bytes.toBytes("i"), Bytes.toBytes("col1"), Bytes.toBytes("change2"));
		put2.addColumn(Bytes.toBytes("u"), Bytes.toBytes("ucol1"), Bytes.toBytes(3332));
		list.add(put2);
		try {
			table.put(list);
			table.close();
			System.out.println("添加成功");
		} catch (IOException e) {
			System.out.println("添加失败");
			e.printStackTrace();
		}

	}

	public void deleteMany() {

		Table table = geTable("bd20:fromjava4");
		List<Delete> list = new ArrayList<Delete>();

		Delete delete = new Delete(Bytes.toBytes("1"));
		list.add(delete);
		Delete delete2 = new Delete(Bytes.toBytes("2"));
		list.add(delete2);
		try {
			table.delete(list);
			table.close();
			System.out.println("删除成功");
		} catch (IOException e) {
			System.out.println("删除失败");
			e.printStackTrace();
		}
	}

	public void getMany(List<String> rowkeyList) throws IOException {
		Table table = geTable("bd20:fromjava4");
		for (String rowkey : rowkeyList) {
			Get get = new Get(Bytes.toBytes(rowkey));
			Result result = table.get(get);
			for (Cell kv : result.rawCells()) {
				String value = Bytes.toString(CellUtil.cloneValue(kv));
				System.out.println(value);
			}
		}

	}

	public void scanAndPrintMany() throws IOException {
		Table table = geTable("bd20:fromjava4");
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes("u"));

		ResultScanner scanner = table.getScanner(scan);
		Result result = scanner.next();
		HBaseUtil.printResult(result);
	}

	public static void main(String[] args) throws IOException {

		HBaseDDL test = new HBaseDDL();
		test.printTables();
	}
}
