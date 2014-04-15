package com.baidu.cdc.hbase;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;

public class HbaseTestMain
{
  public static void main(String[] args)
    throws Exception
  {
    Configuration conf = HBaseConfiguration.create();
    conf.set("hbase.zookeeper.quorum", "cq01-crm-lin2rd52.vm,cq01-crm-lin2rd53.vm,cq01-crm-lin2rd54.vm");
    conf.set("hbase.zookeeper.property.clientPort", "2181");
    conf.set("dfs.socket.timeout", "360000");

    int beginint = Integer.parseInt(args[0]);
    HTable table = new HTable(conf, "cdc_acct_info");
    table.setAutoFlush(false);
    table.setWriteBufferSize(10485760L);
    List lp = new ArrayList();
    String line = null;
    byte[] family = "base_info".getBytes();
    long start = System.currentTimeMillis();

    int endint = Integer.parseInt(args[1]);
    for (int j = beginint; j < endint; j++) {
      int i = 1;
      int beatchNum = 100000;

      LineNumberReader lineReader = new LineNumberReader(new FileReader(args[2]));
      long allStart = System.currentTimeMillis();
      while ((line = lineReader.readLine()) != null) {
        String[] split = line.split("\t");
        Put p = new Put((split[0] + "_" + j).getBytes());
        p.add(family, "account_name".getBytes(), split[1].getBytes());
        p.add(family, "user_level".getBytes(), split[2].getBytes());
        p.add(family, "contact_email".getBytes(), split[6].getBytes());
        p.add(family, "contact_name".getBytes(), split[7].getBytes());
        p.add(family, "website_name".getBytes(), split[8].getBytes());
        p.setDurability(Durability.SKIP_WAL);
        lp.add(p);
        if (lp.size() == beatchNum) {
          table.put(lp);
          table.flushCommits();
          lp = new ArrayList();
          System.out.println("write " + beatchNum + " records ， " + 
            i++ + " ,time cost is " + 
            (System.currentTimeMillis() - start) / 1000L);
          start = System.currentTimeMillis();
        }
      }
      table.put(lp);
      System.out.println(j + "  次  ,all cost is " + (System.currentTimeMillis() - allStart) / 1000L);
      lineReader.close();
    }
  }
}