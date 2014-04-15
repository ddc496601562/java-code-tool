set mapred.job.name=dwa_ka_op_revenue_full_amount_monthly_'$YYYY-$mm-$dd'; 

INSERT OVERWRITE TABLE $db_cdc_cust_feature.dwa_ka_op_revenue_full_amount_monthly PARTITION(dt='$YYYY-$mm-$dd')
select   /*+MAPJOIN(cLine,con)*/
rec_summary.mon_id ,
rec_summary.kacrm_clt_mgr_id ,
rec_summary.kacrm_channel_mgr_id,
rec_summary.is_kacrm ,
rec_summary.contract_line_id, 
rec_summary.prod_line_id ,
rec_summary.url_id ,
cLine.adsrcid,
con.contractno ,
con.oldcontractno,
con.cltmgrid ,
con.assistantid ,
con.channelmgrid ,
con.agentid ,
con.userid ,
rec_summary.alb_conf_amt ,
rec_summary.kacrm_conf_amt_effective ,
rec_summary.alb_fin_amt ,
rec_summary.alb_delay_amt 
from 
(
     select /*+MAPJOIN(tb)*/
     mon_id , kacrm_clt_mgr_id, kacrm_channel_mgr_id, is_kacrm, contract_line_id, prod_line_id, url_id , sum(alb_conf_amt) as alb_conf_amt , 
     sum( if(is_effective_in_kacrm=1 ,alb_conf_amt ,0.0)) as kacrm_conf_amt_effective ,sum(alb_fin_amt) as alb_fin_amt ,sum(alb_delay_amt) as alb_delay_amt
     from 
     ( select * from $db_cdc_dm.dm_ka_op_revenue_summary_daily where dt='$YYYY-$mm-$dd' ) rev_daily
     left outer join 
     $db_cdc_dm.dm_date tb
     on(tb.date_desc=rev_daily.date_id)
     group by mon_id,kacrm_clt_mgr_id,kacrm_channel_mgr_id,is_kacrm,contract_line_id,prod_line_id,url_id 
) rec_summary 
left outer join
(select contractlineid,prodlineid,adsrcid,contractid from $db_cdc_cust_feature.dwd_alb_contract_line where dt='$YYYY-$mm-$dd') cLine
on(cLine.contractlineid=rec_summary.contract_line_id)
left outer join
(select contractid,contractno,oldcontractno, cltmgrid,assistantid,channelmgrid ,agentid ,userid from $db_cdc_cust_feature.dwd_alb_contract where dt='$YYYY-$mm-$dd') con
on(con.contractid=cLine.contractid)