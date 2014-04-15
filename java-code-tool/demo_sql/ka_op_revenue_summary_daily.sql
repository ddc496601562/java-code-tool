set mapred.job.name=job_cdc_dm_dm_ka_op_revenue_summary_daily_refactor_'$YYYY-$mm-$dd';
----@input dwd_alb_confrec dwd_kacrm_operations_rec dwd_alb_contract_line dwd_alb_contract dwd_alb_frame_policy  dwd_alb_contract_line_inapprove dwd_alb_schedule dwd_alb_customer_url
INSERT OVERWRITE TABLE $db_cdc_dm.dm_ka_op_revenue_summary_daily_refactor PARTITION(dt='$YYYY-$mm-$dd')
select /*+MAPJOIN(cLine,con,fram,lia,schedule,url)*/
cleanAlb.happendate , 
url.crmurlid ,
cLine.adsrcid ,
con.oldcontractno,
con.contractno ,
cleanAlb.contractlineid ,
cleanAlb.prodlineid ,
cleanAlb.agentid ,
cleanAlb.userid ,
cleanAlb.customerid ,
cleanAlb.groupid ,
cleanAlb.cltmgrid ,
cleanAlb.assistantid ,
cleanAlb.channelmgrid ,
kacrmRec.clt_mgr_albid ,
kacrmRec.inside_albid ,
kacrmRec.channel_mgr_albid ,
cleanAlb.total_confamt ,
----合同正本返回，可确认的为财务收入
case when ( con.contbackflag=1 and con.confflag =1 ) then cleanAlb.total_confamt else 0.0 end as alb_fin_amt ,
----合同正本未返回但是是可确认的为递延收入
case when ( con.contbackflag=0 and con.confflag =1 ) then cleanAlb.total_confamt else 0.0 end as alb_delay_amt ,
kacrmRec.money ,
case when kacrmRec.contractline_id is null then 0 else 1 end as is_kacrm ,
kacrmRec.if_effective ,
---框架收入类型：
---0------无效框架收入,框架存在且合同类型不在（1,2,5）内的
---1------框架存在且合同类型在(1,2,5),收入发生在框架时间范围内
---2------框架存在且合同类型在(1,2,5),收入发生日期小于框架开始日期
---3------框架存在且合同类型在(1,2,5),收入发生日期大于框架结束日期
---null---关联不到框架的  或者非  kacrm的 或者 
case 
    when(fram.oldcontractno is not null and kacrmRec.contractline_id is not null and con.contracttype<>1 and con.contracttype<>2 and con.contracttype<>5) then 0 
    when(fram.oldcontractno is not null and kacrmRec.contractline_id is not null and con.contracttype in (1,2,5) and cleanAlb.happendate>=fram.startdate and cleanAlb.happendate<=enddate) then 1 
    when(fram.oldcontractno is not null and kacrmRec.contractline_id is not null and con.contracttype in (1,2,5) and cleanAlb.happendate<fram.startdate ) then 2
    when(fram.oldcontractno is not null and kacrmRec.contractline_id is not null and con.contracttype in (1,2,5) and cleanAlb.happendate >fram.enddate) then 3 
    else null 
    end as is_in_frame_effective_date
from
(
      select a.contractlineid,a.happendate,a.total_confamt,
      contractid,cltmgrid,assistantid,channelmgrid,groupid,agentid,userid,customerid,prodlineid,confdate
      from 
      (
      select round(sum(confamt*100)/100,2)  as total_confamt,max(confrecid) as max_confrecid,contractlineid,happendate
      from $db_cdc_cust_feature.dwd_alb_confrec
      where dt='$YYYY-$mm-$dd'
      group by contractlineid,happendate
      ) a
      join 
      (
      select confrecid,contractlineid,happendate,contractid,cltmgrid,assistantid,channelmgrid,groupid,agentid,userid,customerid,prodlineid,confdate,
      deferflag,checkstatus,typeflag,deferconfdate
      from $db_cdc_cust_feature.dwd_alb_confrec
      where dt='$YYYY-$mm-$dd'
      ) b
      on(a.contractlineid=b.contractlineid and a.happendate=b.happendate and a.max_confrecid=b.confrecid)
) cleanAlb
----关联上kacrm中的运营收入，获取业绩归属属性
left outer join 
(select contractline_id,to_date(ref_date) as ref_date,clt_mgr_albid,channel_mgr_albid,inside_albid,money,if_effective from $db_cdc_cust_feature.dwd_kacrm_operations_rec 
where dt='$YYYY-$mm-$dd') kacrmRec 
on (kacrmRec.contractline_id=cleanAlb.contractlineid and kacrmRec.ref_date=cleanAlb.happendate)
-----关联订单行获取合同信息，以及审批前订单行信息
left outer join 
(select contractlineid,prodlineid,adsrcid,contractid,clinapproveid from $db_cdc_cust_feature.dwd_alb_contract_line where dt='$YYYY-$mm-$dd') cLine
on(cleanAlb.contractlineid=cLine.contractlineid)
----关联合同取得合同号、框架协议号、是否是坏账（0：非坏账 1：坏账）
left outer join
(select contractid,contractno,oldcontractno  ,contbackflag ,confflag ,contracttype from $db_cdc_cust_feature.dwd_alb_contract where dt='$YYYY-$mm-$dd') con
on(con.contractid=cLine.contractid)
left outer join 
(select oldcontractno ,startdate,enddate  from $db_cdc_cust_feature.dwd_alb_frame_policy where dt='$YYYY-$mm-$dd') fram
on(fram.oldcontractno=con.oldcontractno)
----以下3个表的关联都是为了获得订单行对应的url
left outer join
(select contractlineid, scheduleid from $db_cdc_cust_feature.dwd_alb_contract_line_inapprove where dt = '$YYYY-$mm-$dd' ) lia
on(lia.contractlineid=cLine.contractlineid)
left outer join 
( select scheduleid ,urlid from $db_cdc_cust_feature.dwd_alb_schedule where dt = '$YYYY-$mm-$dd' ) schedule
on(schedule.scheduleid=lia.scheduleid)
left outer join
(select id ,crmurlid from $db_cdc_cust_feature.dwd_alb_customer_url where dt = '$YYYY-$mm-$dd') url
on(url.id=schedule.urlid)
