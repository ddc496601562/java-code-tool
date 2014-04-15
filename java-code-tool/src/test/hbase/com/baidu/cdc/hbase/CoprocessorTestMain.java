package com.baidu.cdc.hbase;

import com.baidu.cdc.hbase.coprocessor.RowCountProtocol;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.util.Bytes;

public class CoprocessorTestMain
{
  public static void main(String[] args)
    throws Throwable
  {
    Configuration conf = HBaseConfiguration.create();
    conf.set("hbase.zookeeper.quorum", "cq01-crm-lin2rd52.vm,cq01-crm-lin2rd53.vm,cq01-crm-lin2rd54.vm");
    conf.set("hbase.zookeeper.property.clientPort", "2181");
    HTable table = new HTable(conf, "cdc_acct_info");
    Map<byte[],Long> results = table.coprocessorExec(
      RowCountProtocol.class, 
      "167413".getBytes(), 
      "167513".getBytes(), 
      new Batch.Call<RowCountProtocol,Long>(){
      public Long call(RowCountProtocol instance) throws IOException{
        return instance.getRowCount();
      }
    });
    long total = 0L;
    for (Map.Entry<byte[],Long> entry : results.entrySet()) {
      total += ((Long)entry.getValue()).longValue();
      System.out.println("Region: " + Bytes.toString((byte[])entry.getKey()) + 
        ", Count: " + entry.getValue());
    }
    System.out.println("Total Count: " + total);
    table.close();
  }
}

