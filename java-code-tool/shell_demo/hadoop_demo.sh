hadoop dfs -rmr  "$out_hdfs_path"
hadoop streaming \
-D mapred.job.name="cdc_publish.dwd_prod_word_list.$data_date" \
-D mapred.reduce.tasks=100 \
-input "$word_list_path" \
-output "$out_hdfs_path" \
-mapper /bin/cat  

hadoop streaming -input "/app/ecom/rigelci/ttt/20140102/*/tc-sf-drd21.tc/*.log" -output "/app/ecom/rigelci/ttt/ouput" -mapper "iconv -c -f GBK -t UTF-8" 


hadoop streaming -input "/app/ecom/rigelci/hive/pub.db/dim_cmatch_mapping/" -output "/app/ecom/rigelci/hive/pub.db/dim_cmatch_mapping_add/" -mapper  "sh awk.sh"  -file "awk.sh"



hadoop streaming -input "/app/ecom/rigelci/cdp_data_dump/pdate=2014-01-20" -output "/app/ecom/rigelci/qa/hive/ods.db/fc_wordinfo_from_cdp_base/pdate=2014-01-20" -mapper  "sh awk.sh"  -file "awk.sh"


hadoop streaming -input "/app/ecom/rigelci/cdp_data_dump/pdate=2014-01-20" -output "/app/ecom/rigelci/qa/hive/ods.db/fc_wordinfo_from_cdp_base/pdate=2014-01-20" -mapper  "sh awk.sh"  -file "awk.sh"