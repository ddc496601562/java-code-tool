set mapred.job.name='job_cdc_cust_feature_dwa_ka_frame_date_income';
--框架累计收入汇总表
--1.先把    框架协议表 按时间维度 弄好，然后 去left join  kaalb日订单行运营收入汇总
-- 由于日订单行 运营收入 汇总表的 金额 都是乘了1000的，所以此处 我们 除以1000

INSERT OVERWRITE TABLE $db_cdc_cust_feature.dwa_ka_frame_date_income PARTITION(dt='$YYYY-$mm-$dd')

select 
c.date_id,
c.frame_policy_no,
COALESCE(d.frame_operation_amt,0.0),
COALESCE(d.frame_finance_amt,0.0)
from 
   (
      select a.date_desc as date_id,b.frame_policy_no
      from 
      (
        select date_desc
        from $db_cdc_dm.dm_date
        where date_desc>='2006-05-01' and date_desc<='$YYYY-$mm-$dd'
      )a
      join 
      (
		select frame_policy_no,start_date,end_date
		from $db_dwd.prod_frame_policy
		where pdate='$YYYY-$mm-$dd' and start_date<='$YYYY-$mm-$dd'
      )b
      where a.date_desc>=b.start_date and a.date_desc<=b.end_date
   )c
   
left outer join

   (            
	  select
	  frame_policy_no,
	  st_date,
	  round(sum(conf_amt)/1000,2)  as frame_operation_amt,
	  round(sum(fin_amt)/1000,2)  as frame_finance_amt
	  from $db_dwa.contline_revenue_day
	  where pdate='$YYYY-$mm-$dd' and frame_policy_no is not null and is_frame_revenue=1
	  group by frame_policy_no,st_date
   )d
on c.frame_policy_no=d.frame_policy_no and c.date_id=d.st_date
