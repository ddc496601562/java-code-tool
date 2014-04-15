package com.baidu.cdc.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseReadTest
{
  public static void main(String[] args)
    throws Exception
  {
    Configuration conf = HBaseConfiguration.create();
    conf.set("hbase.zookeeper.quorum", "cq01-crm-lin2rd52.vm,cq01-crm-lin2rd53.vm,cq01-crm-lin2rd54.vm");
    conf.set("hbase.zookeeper.property.clientPort", "2181");
    HTable table = new HTable(conf, "cdc_acct_info");
    Scan scan = new Scan();
    scan.setStartRow("167413".getBytes());
    scan.setStopRow("167483".getBytes());
    scan.setBatch(1000);
    scan.addFamily("base_info".getBytes());
    ResultScanner rs = table.getScanner(scan);

    int i = 0;
    for (Result r : rs) {
      i++;
      System.out.println("***********************************");
      for (KeyValue kv : r.raw()) {
        System.out.println(String.format("row:%s, family:%s, qualifier:%s, qualifiervalue:%s, timestamp:%s.", new Object[] { 
          Bytes.toString(kv.getRow()), 
          Bytes.toString(kv.getFamily()), 
          Bytes.toString(kv.getQualifier()), 
          Bytes.toString(kv.getValue()), 
          Long.valueOf(kv.getTimestamp()) }));
      }
    }
    rs.close();
    System.out.println("***********************************");
    System.out.println("sum is " + i);
  }
}