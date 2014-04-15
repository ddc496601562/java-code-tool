package com.baidu.cdc.hbase.coprocessor;

import java.io.IOException;

import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;

public class RowCountEndpoint extends BaseEndpointCoprocessor  implements RowCountProtocol {
	@Override
	public long getRowCount() throws IOException {
		return 0;
	}

}
