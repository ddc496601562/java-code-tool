set mapred.job.name=dwa_fc_nor_doris_cmatch_show_daily_'$YYYY-$mm-$dd' ;
SET hive.exec.compress.output=true;
set hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
set mapred.max.split.size=556000000;
--- 展现日志天汇总，修改计费名过滤规则和cmatch规则，计费名统一过滤2个，
--- 小时---》天汇总

INSERT OVERWRITE TABLE $db_dwa.log_winfo_fc_show_day PARTITION(pdate='$YYYY-$mm-$dd')
SELECT /*+mapjoin(d)*/
fc_hour_show.st_date                AS st_date,
fc_hour_show.st_hour                AS st_hour,
fc_hour_show.winfo_id               AS winfo_id,
fc_hour_show.word_id                AS word_id,
fc_hour_show.idea_id                AS idea_id,
fc_hour_show.acct_id                AS acct_id,
fc_hour_show.plan_id                AS plan_id,
fc_hour_show.unit_id                AS unit_id,
fc_hour_show.is_spam_flg            AS is_spam_flg,
fc_hour_show.cmatch                 AS cmatch,
fc_hour_show.wmatch                 AS wmatch,
fc_hour_show.owmatch                AS owmatch,
fc_hour_show.bmm_type               AS bmm_type,
fc_hour_show.bmm_trigger_type_id    AS bmm_trigger_type_id,
fc_hour_show.data_src_num           AS data_src_num,
fc_hour_show.is_brand_promotion     AS is_brand_promotion,
fc_hour_show.prod_line_name         AS prod_line_name,
fc_hour_show.return_ad_num          AS return_ad_num,
fc_hour_show.prod_line_id           AS prod_line_id,
fc_hour_show.matter_type_id         AS matter_type_id,
fc_hour_show.channel_id             AS channel_id,
d.cn_id                             AS cn_id,
fc_hour_show.cn_name                AS cn_name,
d.cn_group                          AS cn_group,
fc_hour_show.prov_id                AS prov_id,
fc_hour_show.city_id                AS city_id,
fc_hour_show.tn                     AS tn,
fc_hour_show.minbid                 AS minbid,
fc_hour_show.target_value           AS target_value,
fc_hour_show.new_matter_type_id     AS new_matter_type_id,
fc_hour_show.query_src              AS query_src,
fc_hour_show.ad_src_type_id         AS ad_src_type_id,
fc_hour_show.query_prov_id          AS query_prov_id,
fc_hour_show.query_city_id          AS query_city_id,
fc_hour_show.region_match_type_id   AS region_match_type_id,
fc_hour_show.is_direct_ad           AS is_direct_ad,
fc_hour_show.mobile_tag             AS mobile_tag,
fc_hour_show.is_mobile_site         AS is_mobile_site,
fc_hour_show.site_type              AS site_type,
sum(fc_hour_show.shw)               AS shw,
fc_hour_show.is_first_page          AS is_first_page,
fc_hour_show.rank                   AS rank,
fc_hour_show.mt_id                  AS mt_id,
fc_hour_show.mc_id                  AS mc_id
FROM 

(
SELECT
st_date                ,
st_hour                ,
winfo_id               ,
word_id                ,
idea_id                ,
acct_id                ,
plan_id                ,
unit_id                ,
is_spam_flg            ,
cmatch                 ,
wmatch                 ,
owmatch                ,
bmm_type               ,
bmm_trigger_type_id    ,
data_src_num           ,
is_brand_promotion     ,
prod_line_name         ,
return_ad_num          ,
prod_line_id           ,
matter_type_id         ,
channel_id             ,
cn_name                ,
prov_id                ,
city_id                ,
tn                     ,
minbid                 ,
target_value           ,
new_matter_type_id     ,
query_src              ,
ad_src_type_id         ,
query_prov_id          ,
query_city_id          ,
region_match_type_id   ,
is_direct_ad           ,
mobile_tag             ,
is_mobile_site         ,
site_type              ,
is_first_page          ,
rank,
shw,
mt_id,
mc_id
FROM
$db_dwa.log_fc_show_hour 
WHERE 
$cmatch_doris_fc --- 过滤cmatch
AND
(pdate='$YYYY-$mm-$dd' OR (pdate=date_add('$YYYY-$mm-$dd', -1) AND hour='2330') 
   OR (pdate=date_add('$YYYY-$mm-$dd', 1) AND hour='0000'))
--- 天汇总要前后多取几个分片  
AND st_date='$YYYY-$mm-$dd'  --- 展现时间为今天

AND $cn_doris_filter) fc_hour_show
 LEFT OUTER JOIN
 	 (
 	    SELECT cntnid as cn_id,cntnname,cntngroup AS cn_group FROM $db_cdc_dm.dm_cntmate WHERE dt='$YYYY-$mm-$dd' --- 计费名表
 	 ) d 
  ON fc_hour_show.cn_name=d.cntnname


GROUP BY
fc_hour_show.st_date,
fc_hour_show.st_hour,
fc_hour_show.winfo_id,
fc_hour_show.word_id,
fc_hour_show.idea_id,
fc_hour_show.acct_id,
fc_hour_show.plan_id,
fc_hour_show.unit_id,
fc_hour_show.is_spam_flg,
fc_hour_show.cmatch,
fc_hour_show.wmatch,
fc_hour_show.owmatch,
fc_hour_show.bmm_type,
fc_hour_show.bmm_trigger_type_id,
fc_hour_show.data_src_num,
fc_hour_show.is_brand_promotion,
fc_hour_show.prod_line_name,
fc_hour_show.return_ad_num,
fc_hour_show.prod_line_id,
fc_hour_show.matter_type_id,
fc_hour_show.channel_id,
d.cn_id,
fc_hour_show.cn_name,
d.cn_group,
fc_hour_show.prov_id,
fc_hour_show.city_id,
fc_hour_show.tn,
fc_hour_show.minbid,
fc_hour_show.target_value,
fc_hour_show.new_matter_type_id,
fc_hour_show.query_src,
fc_hour_show.ad_src_type_id,
fc_hour_show.query_prov_id,
fc_hour_show.query_city_id,
fc_hour_show.region_match_type_id,
fc_hour_show.is_direct_ad,
fc_hour_show.mobile_tag,
fc_hour_show.is_mobile_site,
fc_hour_show.site_type,
fc_hour_show.is_first_page,
fc_hour_show.rank,
fc_hour_show.mt_id,
fc_hour_show.mc_id;
