#!/bin/sh

function is_should_sleep()
{
   	hour=`date "+%H"`
	minute=`date "+%M"`
	echo "hour is "$hour ", minute is "$minute
	if [[ $hour -lt 9 ]] || [[ $hour -ge 21 ]];then
		return 0
	else
		return 1
	fi
}

etl_path=/home/work/etl
dm_path=$etl_path/cdc-dm
publish_path=$etl_path/cdc-publish
log_path=$dm_path/log

begin_date=$1
end_date=$2

rundate=$begin_date

while [ $rundate -le $end_date ]
do
   	while is_should_sleep  ;do
		sleep 60
		echo "on_hadoop is high load ,this script sleep 60 second "
	done	 
   	echo `date +"%Y-%m-%d %H:%M:%S"`" - fc show history daily[$rundate] will be processed..."
	sh insight3_fc_show_daily.sh $rundate > $log_path/fc_show_history.$rundate.log 2>&1
	echo `date +"%Y-%m-%d %H:%M:%S"`" - fc show history daily[$rundate] finished"
   	sleep 20m
	rundate=`date -d "$rundate next day" +"%Y%m%d"`
done
