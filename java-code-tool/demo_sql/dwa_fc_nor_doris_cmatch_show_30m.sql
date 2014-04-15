add jar $CDC_DM_PATH/udf_lib/cdc-etl-hive-util.jar;
set hive.partition.file.filter= @manifest.*;
set mapred.input.pathFilter.class= com.baidu.cdc.data.util.HiveInputFormatFilter;
SET hive.exec.compress.output=true;
SET hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
SET mapred.max.split.size=556000000;
set mapred.job.name=dwa_fc_nor_doris_cmatch_show_30m_'$YYYY-$mm-$dd-$HH-$MM';
set mapred.reduce.tasks=53;

--- 重构： 一个是dwd明细，一个是dwa预汇总
--- 先执行 dwa半小时预汇总

--- 模型修改，半小时汇总不要计费名组属性     start
---- 1. ods -- > dwa 半小时汇总
INSERT OVERWRITE TABLE $db_dwa.log_fc_show_hour PARTITION(pdate='$YYYY-$mm-$dd',hour='$HH$MM')
SELECT
substr(time,0,10) AS st_date,
substr(time,12,2) AS st_hour,
winfoid           AS winfo_id,
wordid            AS word_id,
ideaid            AS idea_id,
userid            AS acct_id,
planid            AS plan_id,
unitid            AS unit_id,
0                         AS is_spam_flg,
cmatch            AS cmatch,
wmatch            AS wmatch,
owmatch           AS owmatch,
bmm_type          AS bmm_type,
null                      AS bmm_trigger_type_id,
null                      AS data_src_num,
null                      AS is_brand_promotion,
null                      AS prod_line_name,
null                      AS return_ad_num,
null                      AS prod_line_id,
null                      AS matter_type_id,
null                      AS channel_id,
cn                 AS cn_name,
pid               AS prov_id,
cid               AS city_id,
null                      AS tn,
minbid*1000       AS minbid,
null                      AS target_value,
null                      AS new_matter_type_id,
null                      AS query_src,
null                      AS ad_src_type_id,
null                      AS query_prov_id,
null                      AS query_city_id,
null                      AS region_match_type_id,
null                      AS is_direct_ad,
CASE WHEN cmatch IN $cmatch_fc_mobile  THEN 1 ELSE 0 END             AS mobile_tag,  --- 1 手机  0 pc
CASE WHEN mt_id LIKE '%2011%' THEN 1 ELSE 0 END             AS is_mobile_site,  --- 包含2011 为手机站
--- 建站方式   0->自主建站  1->siteapp  2->手机名片，当为手机站，但是siteapp为空时，默认为0 自主建站 --- taojing确认
CASE WHEN mt_id LIKE '%2011%' THEN if(site_type IS NULL,0,site_type) ELSE 9 END AS site_type,
sum(shw)                                                            AS shw,
page_no                                                             AS page_no, --- 翻页号
case when page_no=0 THEN 1 else 0 END                               AS is_first_page, -- 是否为首页
rank                                                                AS rank,
mt_id                                                               AS mt_id,
mc_id                                                               AS mc_id
FROM $db_dwd.fc_nor_cmatchall_show_log_30m 
		WHERE dt='$YYYY-$mm-$dd' and hour='$HH$MM' AND userid is not null and rank is not null and cmatch is not null 
GROUP BY
substr(time,0,10),
substr(time,12,2),
winfoid,
wordid,
ideaid,
userid,
planid,
unitid,
cmatch,
wmatch,
owmatch,
bmm_type,
cn,
pid,
cid,
minbid*1000,
(CASE WHEN cmatch IN $cmatch_fc_mobile  THEN 1 ELSE 0 END),
(CASE WHEN mt_id LIKE '%2011%' THEN 1 ELSE 0 END),
(CASE WHEN mt_id LIKE '%2011%' THEN if(site_type IS NULL,0,site_type) ELSE 9 END),
page_no,
(case when page_no=0 THEN 1 else 0 END),
rank,mc_id,mt_id;

--- 模型修改，半小时汇总不要计费名组属性     end

---- 2 . ods ---> dwd明细

INSERT OVERWRITE TABLE $db_dwd.log_fc_show_info PARTITION(pdate='$YYYY-$mm-$dd',hour='$HH$MM')
SELECT
time                  AS disp_time              ,--STRING COMMENT'展现时间',
0                     AS is_spam_flg            ,--INT    COMMENT '是否过滤掉的日志，0：normal日志，1：过滤掉的(spam)日志  默认为0',
userid                AS acct_id 				,--BIGINT COMMENT'用户id',
planid                AS plan_id 				,--BIGINT COMMENT'推广计划id',
unitid                AS unit_id 				,--BIGINT COMMENT'推广单元id',
winfoid               AS winfo_id				,--BIGINT COMMENT'关键字id',
wordid                AS word_id 				,--BIGINT COMMENT'关键词id',
ideaid                AS idea_id 				,--BIGINT COMMENT'创意id',
cmatch                AS cmatch 				,--INT COMMENT'cmatch信息',
wmatch                AS wmatch 				,--INT COMMENT'匹配模式',
owmatch               AS owmatch 				,--STRING COMMENT'广告主选择的匹配模式',
bmm_type              AS bmm_type 			    ,--STRING COMMENT'高短短语子类型',
NULL                  AS bmm_trigger_type_id 	,--STRING COMMENT'高短312触发类型区分标记 1：字面，2:同义,0：其他',
NULL                  AS data_src_num 			,--STRING COMMENT'数据源号',
NULL                  AS is_brand_promotion 	,--STRING COMMENT'请求广告的偏移，品牌推广：1，其他：0',
rank                  AS rank 					,--STRING COMMENT'广告相对排名',
page_no               AS page_no                ,--STRING COMMENT'翻页号',
cookieid              AS baidu_id 				,--STRING COMMENT'cookie标记',
NULL                  AS prod_line_name 		,--STRING COMMENT'产品线名称',
NULL                  AS return_ad_num 			,--STRING COMMENT'返回广告数',
NULL                  AS prod_line_id 			,--STRING COMMENT'广告的产品线id',
NULL                  AS matter_type_id 		,--STRING COMMENT'广告的物料类型id',
NULL                  AS channel_id 			,--STRING COMMENT'联盟渠道id',
cn                    AS cn_name 				,--STRING COMMENT'计费名NAME',
NULL                  AS tn 					,--STRING COMMENT'模板名',
searchid              AS search_id   		    ,--STRING COMMENT'检索唯一值',
minbid*1000           AS minbid 				,--STRING COMMENT'最低展现出价',*1000
NULL                  AS target_value 			,--STRING COMMENT'广告被定向的类型',
NULL                  AS new_matter_type_id 	,--STRING COMMENT'新物料类型（区分自主，联盟，hao123）',
NULL                  AS new_matter_id 			,--STRING COMMENT'新物料id',
NULL                  AS query_src 				,--STRING COMMENT'query的来源',
NULL                  AS ad_src_type_id 		,--STRING COMMENT'广告来源标志 1：凤巢，2：nks',
NULL                  AS query_prov_id 			,--STRING COMMENT'从query解析出来的1级地域',
NULL                  AS query_city_id 			,--STRING COMMENT'从query解析出来的2级地域',
pid                   AS prov_id 		        ,--STRING COMMENT'ui请求ip对应的省份id',
cid                   AS city_id 	            ,--STRING COMMENT'ui请求ip对应的城市id',
NULL                  AS region_match_type_id	,--STRING COMMENT'winfo使用过query地域还是ip地域匹配出来',
NULL                  AS is_direct_ad 			,--STRING COMMENT'触发到winfo的广告的标记位',
NULL                  AS direct_ad_query 		,--STRING COMMENT'触发到winfo的广告的签名',
shw                   AS dis 				    ,--STRING COMMENT'展现广告条数',
1                     AS refund_flg 			,--STRING COMMENT'返展现标识 返展现:-1,非返展现:1',
CASE WHEN mt_id LIKE '%2011%' THEN 1 ELSE 0 END                                  AS is_mobile_site, --STRING COMMENT'是PC站还是无线站 0:pc，1:无线 规则：mt-id:包含2011为手机站，不包含2011为PC站1',
---------site_type 为空情况  9代表pc站
CASE WHEN mt_id LIKE '%2011%' THEN if(site_type IS NULL,0,site_type) ELSE 9 END  AS site_type, --STRING COMMENT'建站方式 0->自主建站 1->siteapp 2->手机名片'
case when page_no=0 THEN 1 else 0 END                                                     AS is_first_page -- 是否为首页
,mt_id  AS mt_id,
mc_id AS mc_id
FROM 
--- 三十分钟展现表
    $db_dwd.fc_nor_cmatchall_show_log_30m WHERE dt='$YYYY-$mm-$dd' AND hour='$HH$MM' 
    AND userid IS NOT NULL AND rank IS NOT NULL AND cmatch IS NOT NULL;
    
