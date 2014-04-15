package com.baidu.cdc.hbase.coprocessor;

import java.io.IOException;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public interface RowCountProtocol extends CoprocessorProtocol {
	long getRowCount() throws IOException;  
}
