<?php
/* vim: set expandtab tabstop=4 shiftwidth=4 foldmethod=marker: */
// +----------------------------------------------------------------------+
// | sf-crm 大客户导出程序												  |
// | 规则：	自动运行，每天运行一次										  |
// | 参数：	八位日期(默认执行导出前一天的数据)							  |
// +----------------------------------------------------------------------+
// | Copyright (c) 2008 Baidu.com                                         |
// +----------------------------------------------------------------------+
// | Author: Zhang Xuancheng <zhangxuancheng83@baidu.com>                 |
// +----------------------------------------------------------------------+
//
// $Id: daily_crm_vic_cust.php,v 1.5 2009/11/17 04:52:42 zhuojh Exp $

$bolNeedOutDb = true;
$bolNeedClickDb = true;

require_once('../inc/cfg.inc.php');
include_once("inc/db.inc.php");
include_once("inc/glb.inc.php");
include_once("inc/log.inc.php");
include_once("lib/stat.lib.php");

function printlog($log, $bolDebug=false){
	if ($bolDebug || $_SERVER['argv'][1] == 'debug' || $_SERVER['argv'][2] == 'debug') {
		print "[".date('Y-m-d H:i:s')."] ".$log."\n";
	}
}

function rmLnTab ($strIn) {
	return str_replace(array("\r", "\n", "\t"), " ", $strIn);
}

function crm_file_put_contents ($strFileName, $strCont) {
	if (!$handle = fopen($strFileName, 'w')) {
		return false;
	}
	
	if (!flock($handle, LOCK_EX)) { 	// 锁定
		@fclose($handle);
		return false;
	}

	// 写入
	if (fwrite($handle, $strCont) === FALSE) {
		@fclose($handle);
		return false;
	}

	flock($handle, LOCK_UN); // 释放锁定
	fclose($handle);
	return true;	
}


function p_setUserInfoTmp($intUserId, $arrUserInfo, $strActType = 'update') {
	if (!$intUserId || !is_array($arrUserInfo) && !$arrUserInfo) {
		return false;
	}
	
	$objSql = new CSql();
	$objSql->setMysqlServerVersion('6602');
	$objSql->setSqlTable($GLOBALS['strTblUserInfoTmp']);
	foreach ($arrUserInfo as $key => $val) {
		$objSql->setSqlColumn($key, $val);
	}
	if ($strActType == 'insert') {
		$objSql->getInsertSql($strSql);
	} else {
		$objSql->setSqlWhere('userid', $intUserId);
		$objSql->getUpdateSql($strSql);
	}
	// printlog($strSql);
	
	$result = $GLOBALS['dbhClk']->query($strSql);
	
	return $result;
}

if ($_SERVER['argc'] > 1 && strlen($intDate = $_SERVER['argv'][1]) == 8) {
	$intTime = date2time($intDate);
	$strDateStat = date('Y-m-d', $intTime);
} else {
	$intTime = time() - 86400;
	$strDateStat = $GLOBALS['glb_dateYesterday'];
}

task_start($strDateStat);
task_former($strDateStat);

// 日期
$intDate = date('Ymd', $intTime);
$strDay = date('Y-m-d', $intTime);
$intCurrentDay = date('j', $intTime);

$objLog->setFileConf(array('filename' => $GLOBALS['glb_dirShifenLogs'] . $intDate . '/cron_crm_vic_cust.log'));
$objLog->log('CRON_START');
printlog(basename(__FILE__)."::开始执行", true);

//星期一运行时为true
$bolWeek = date('w', $intTime) == 0 ? true : false;
//每月的第一天运行时为true
$bolMonth = date('d', $intTime)==date('t', $intTime)?true:false;

// 天
$strDayS = $strDay.' 00:00:00';
$strDayE = $strDay.' 23:59:59';
$arrDayTime = array($strDayS, $strDayE);

// 表
$strTblCnsk = $glb_dbCnsk . '.cnsk' . date('Ym', $intTime);
$strTblCnskTmpD = $glb_dbCnsk.'.cnsk_crm_vic_tmp_d';
$strTblCnskTmpW = $glb_dbCnsk.'.cnsk_crm_vic_tmp_w';
$strTblCnskTmpM = $glb_dbCnsk.'.cnsk_crm_vic_tmp_m';
$strTblStatUserTmpD = $glb_dbStat.'.statuser_crm_vic_tmp_d';
$strTblStatUserTmpW = $glb_dbStat.'.statuser_crm_vic_tmp_w';
$strTblStatUserTmpM = $glb_dbStat.'.statuser_crm_vic_tmp_m';
$strTblUserInfoTmp = $glb_dbStat.'.userinfo_crm_vic_tmp';
$strTblHitline = 'SF_Hitline.limit'.date('Ym', $intTime);

/** 文件及目录 ***/
// 目录
if (!is_dir($cfg_strCrmOutportDir)) {
	$objLog->log('CRON_ERROR', array('error' => '未检测到正确的导出路径'));
	$strError = '未检测到正确的导出路径　－　'.$cfg_strCrmOutportDir.' ！';
	printlog(__LINE__ . "\t::" . $strError, true);
	mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
	exit;
}

$strDirCrmImp = $cfg_strCrmOutportDir.'/'.$intDate;
if (!is_dir($strDirCrmImp)) {
	mkdir($strDirCrmImp, 0777);
}
$strDirCrmImpTmp = $cfg_strCrmOutportDir.'/tmp';
if (!is_dir($strDirCrmImpTmp)) {
	mkdir($strDirCrmImpTmp, 0777);
}

// 文件
$strFileCrmImpCltName = 'client_vic_'.$intDate.'.dat';
$strFileCrmImpClt = $strDirCrmImp.'/'.$strFileCrmImpCltName;
$strFileCrmImpCltMd5 = $strDirCrmImp.'/client_vic_'.$intDate.'.dat.md5';

// 临时文件
$strFileCrmImpCltTmp = $strDirCrmImpTmp.'/client_vic_'.$intDate.'.dat';
$strFileCrmImpCltMd5Tmp = $strDirCrmImpTmp.'/client_vic_'.$intDate.'.dat.md5';

/** 数据库 ***/
$objSql = new CSql;
$objSql->setDebug(false);

//取大客户ID
$arrVipClient = array();
$objSql->clear();
$objSql->setSqlTable($GLOBALS['glb_tblUserAcct']);
$objSql->setSqlColumn('userid');
$objSql->setSqlWhere('ulevelid', USER_LEVEL_CLIENT);
//$objSql->setSqlWhere('userid', 30, SQL_WHERE_GT);
$objSql->getSelectSql($strSql);
printlog(__LINE__ . "\t::" . $strSql, true);
$result = $GLOBALS['dbh']->query($strSql);
while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
	$arrVipClient[] = $row['userid'];
}

$arr2VipClient = array_chunk($arrVipClient, 8000);
unset($arrVipClient);
/////////////////////////////////////////////////////////////////

$strSql = "CREATE TEMPORARY TABLE $strTblCnskTmpD (
  userid int(10) unsigned NOT NULL default '0',
  keynum int(10) unsigned NOT NULL default '0',
  KEY (userid)
) TYPE = MYISAM";
$res = $dbhOut->query($strSql);
printlog($strSql);
if (DB::isError($res)) {
	$objLog->log('CRON_ERROR', array('error' => '建立临时表', 'tblname' => $strTblCnskTmpD));
	$strError = '建立临时表' . $strTblCnskTmpD . '失败！';
	printlog(__LINE__ . "\t::" . $strError, true);
	mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
	exit;
}

foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}
	$strVipClient = implode(',', $arrVipClient);
	$strVipClient = '('.$strVipClient.')';
	
	$strSql = "INSERT INTO $strTblCnskTmpD (userid, keynum)
	   SELECT userid, COUNT(DISTINCT keywid)
	   FROM $strTblCnsk
	   WHERE stday='".$intCurrentDay."'
	   AND userid IN ". $strVipClient ."
	   GROUP BY userid";
	$res = $dbhOut->query($strSql);
	printlog($strSql);
	if (DB::isError($res)) {
		$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblCnskTmpD));
		$strError = '插入临时表' . $strTblCnskTmpD . '失败！';
		printlog(__LINE__ . "\t::" . $strError, true);
		mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
		exit;
	}
}
// statuser表
$strSql = "CREATE TEMPORARY TABLE $strTblStatUserTmpD (
  userid int(10) unsigned NOT NULL default '0',
  clks int(10) unsigned NOT NULL default '0',
  charge decimal(8,2) unsigned NOT NULL default '0.00',
  cash decimal(10,4) unsigned NOT NULL default '0.0000',
  key (userid)
) TYPE = MYISAM";
printlog(__LINE__ . "\t::" . $strSql);
$res = $dbhClk->query($strSql);
if (DB::isError($res)) {
	$objLog->log('CRON_ERROR', array('error' => '建立临时表', 'tblname' => $strTblStatUserTmpD));
	$strError = '建立临时表' . $strTblStatUserTmpD . '失败！';
	printlog(__LINE__ . "\t::" . $strError, true);
	mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
	exit;
}

foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}
	$strVipClient = implode(',', $arrVipClient);
	$strVipClient = '('.$strVipClient.')';
	$strSql = "INSERT INTO $strTblStatUserTmpD (userid, clks, charge, cash)
	   SELECT userid, SUM(clks) clks, SUM(charge) charge, SUM(cash) cash
	   FROM " . $GLOBALS['glb_tblStatUser'] . "
	   WHERE stdate='$strDay'
	   AND userid IN ". $strVipClient ."
	   GROUP BY userid";
	$res = $dbhClk->query($strSql);
	if (DB::isError($res)) {
		$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblStatUserTmpD));
		$strError = '插入临时表' . $strTblStatUserTmpD . '失败！';
		printlog(__LINE__ . "\t::" . $strError, true);
		mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
		exit;
	}
}

//userinfo表
$strSql = "CREATE TEMPORARY TABLE $strTblUserInfoTmp (
	userid int(10)  UNSIGNED default 0 not null,
	username char(32)  default '' not null,
	fatuid int(10) UNSIGNED default 0 not null,
	ustatid tinyint(3) UNSIGNED default 0 not null,
	company varchar(100) default ''  not null,
	website varchar(100)  default ''  not null,
	provid smallint(5) UNSIGNED default 0 not null,
	cityid mediumint(8) UNSIGNED default 0 not null,
	realname varchar(50)  default ''  not null,
	phone varchar(100)  default ''  not null,
	fax varchar(100)  default ''  not null,
	email varchar(100)  default ''  not null,
	address varchar(100)  default ''  not null,
	postcode varchar(20)  default ''  not null,
	regtime datetime default '0000-00-00 00:00:00'  not null,
	efftime datetime default '0000-00-00 00:00:00'   not null,
	exptime datetime default '0000-00-00 00:00:00'   not null,
	invest decimal(10,2) UNSIGNED default '0.00' not null,
	balance decimal(10,2) UNSIGNED default '0.00' not null,
	PERDAY_CONSUME int(10)  UNSIGNED default 0 not null,
	EXTEND_REGION varchar(255) default ''  not null,
	cnsknum_d int(10)  UNSIGNED default 0 not null,
	cnsknum_w int(10)  UNSIGNED default 0 not null,
	cnsknum_m int(10)  UNSIGNED default 0 not null,
	knum int(10)  UNSIGNED default 0 not null,
	efftknum int(10)  UNSIGNED default 0 not null,
	clicks_d int(10)  UNSIGNED default 0 not null,
	clicks_w int(10)  UNSIGNED default 0 not null,
	clicks_m int(10)  UNSIGNED default 0 not null,
	cns_d decimal(10,2) UNSIGNED default '0.00' not null,
	cns_w decimal(10,2) UNSIGNED default '0.00' not null,
	cns_m decimal(10,2) UNSIGNED default '0.00' not null,
	cash_d decimal(12,4) UNSIGNED default '0.0000' not null,
	cash_w decimal(12,4) UNSIGNED default '0.0000' not null,
	cash_m decimal(12,4) UNSIGNED default '0.0000' not null,
	opensum decimal(10,2) UNSIGNED default '0.00' not null,
	connnum int(10) default 0   not null,
	connsum decimal(10,2) default '0.00'  not null,
	timeline datetime default '0000-00-00 00:00:00' not null,
	user_leaveword tinyint(1) default 1 not null,
	UNIQUE key (userid)
) TYPE = MYISAM";
printlog($strSql);
$res = $dbhClk->query($strSql);
if (DB::isError($res)) {
	$objLog->log('CRON_ERROR', array('error' => '建立临时表', 'tblname' => $strTblUserInfoTmp));
	$strError = '建立临时表' . $strTblUserInfoTmp . '失败！';
	printlog(__LINE__ . "\t::" . $strError, true);
	mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
	exit;
}
	
if ($bolWeek) {
	// 建临时表
	$strSql = "CREATE TEMPORARY TABLE $strTblCnskTmpW (
	  userid int(10) unsigned NOT NULL default '0',
	  keynum int(10) unsigned NOT NULL default '0',
	  KEY (userid)
	) TYPE = MYISAM";
	$res = $dbhOut->query($strSql);
	printlog($strSql);
	if (DB::isError($res)) {
		$objLog->log('CRON_ERROR', array('error' => '建立临时表', 'tblname' => $strTblCnskTmpW));
		$strError = '建立临时表' . $strTblCnskTmpW . '失败！';
		printlog(__LINE__ . "\t::" . $strError, true);
		mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
		exit;
	}

	$strTblCnsk1 = $glb_dbCnsk . '.cnsk' . date('Ym', $intTime - 6 * 86400);
	$strTblCnsk2 = $glb_dbCnsk . '.cnsk' . date('Ym', $intTime);
	if ($strTblCnsk1 == $strTblCnsk2) { // 如果是同一月份
		foreach ($arr2VipClient AS $arrVipClient) {
			if (empty($arrVipClient)) {
				break;
			}
			$strVipClient = implode(',', $arrVipClient);
			$strVipClient = '('.$strVipClient.')';
			
			$strSql = "INSERT INTO $strTblCnskTmpW (userid, keynum)
			   SELECT userid, COUNT(DISTINCT keywid)
	  		   FROM $strTblCnsk1
			   WHERE stday>='" . date('j', $intTime - 6 * 86400) . "'
				 AND stday<='" . $intCurrentDay . "'
				 AND userid IN ". $strVipClient ."
			   GROUP BY userid";
			$res1 = $dbhOut->query($strSql);
			printlog(__LINE__ . "\t::" . $strSql);
			if (DB::isError($res1)) {
				$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblCnskTmpW));
				$strError = '插入临时表' . $strTblCnskTmpW . '失败！';
				printlog(__LINE__ . "\t::" . $strError, true);
				mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
				exit;
			}
		}
	} else {
		$strTblCnskUnion = $glb_dbCnsk.'.cnsk_tmp_union';
		// 建临时表
		$strSql = "CREATE TEMPORARY TABLE $strTblCnskUnion (
		  userid int(10) unsigned NOT NULL default '0',
		  `keywid` bigint(20) unsigned NOT NULL default '0',
		  KEY (userid)
		) TYPE = MYISAM";
		$res = $dbhOut->query($strSql);
		printlog(__LINE__ . "\t::" . $strSql);
		if (DB::isError($res)) {
			$objLog->log('CRON_ERROR', array('error' => '建立临时表', 'tblname' => $strTblCnskUnion));
			$strError = '建立临时表' . $strTblCnskUnion . '失败！';
			printlog(__LINE__ . "\t::" . $strError, true);
			mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
			exit;
		}
		
		// 插入临时表
		foreach ($arr2VipClient AS $arrVipClient) {
			if (empty($arrVipClient)) {
				break;
			}
			$strVipClient = implode(',', $arrVipClient);
			$strVipClient = '('.$strVipClient.')';
			$strSql = "INSERT INTO $strTblCnskUnion (userid, keywid)
			   SELECT userid,keywid
			   FROM $strTblCnsk1
			   WHERE stday>='".date('j', $intTime - 6 * 86400)."'
			   AND userid IN ". $strVipClient ."
			   GROUP BY userid,keywid";
			$res = $dbhOut->query($strSql);
			printlog(__LINE__ . "\t::" . $strSql);
			if (DB::isError($res)) {
				$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblCnskUnion));
				$strError = '插入临时表' . $strTblCnskUnion . '失败！';
				printlog(__LINE__ . "\t::" . $strError, true);
				mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
				exit;
			}
			
			$strSql = "INSERT INTO $strTblCnskUnion (userid, keywid)
			   SELECT userid,keywid
			   FROM $strTblCnsk2
			   WHERE stday<='" . $intCurrentDay . "'
			   AND userid IN ". $strVipClient ."
			   GROUP BY userid,keywid";
			$res = $dbhOut->query($strSql);
			printlog(__LINE__ . "\t::" . $strSql);
			if (DB::isError($res)) {
				$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblCnskUnion));
				$strError = '插入临时表' . $strTblCnskUnion . '失败！';
				printlog(__LINE__ . "\t::" . $strError, true);
				mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
				exit;
			}
		}
		
		$strSql = "INSERT INTO $strTblCnskTmpW (userid, keynum)
			   SELECT userid, COUNT(DISTINCT keywid)
			   FROM $strTblCnskUnion
			   GROUP BY userid";
		$res2 = $dbhOut->query($strSql);
		printlog(__LINE__ . "\t::" . $strSql);
		if (DB::isError($res2)) {
			$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblCnskTmpW));
			$strError = '插入临时表' . $strTblCnskTmpW . '失败！';
			printlog(__LINE__ . "\t::" . $strError, true);
			mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
			exit;
		}
	}
}

if ($bolMonth) {
	$strSql = "CREATE TEMPORARY TABLE $strTblCnskTmpM (
	  userid int(10) unsigned NOT NULL default '0',
	  keynum int(10) unsigned NOT NULL default '0',
	  KEY (userid)
	) TYPE = MYISAM";
	printlog($strSql);
	$res = $dbhOut->query($strSql);
	if (DB::isError($res)) {
		$objLog->log('CRON_ERROR', array('error' => '建立临时表', 'tblname' => $strTblCnskTmpM));
		$strError = '建立临时表' . $strTblCnskTmpM . '失败！';
		printlog(__LINE__ . "\t::" . $strError, true);
		mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
		exit;
	}
	
	foreach ($arr2VipClient AS $arrVipClient) {
		if (empty($arrVipClient)) {
			break;
		}
		$strVipClient = implode(',', $arrVipClient);
		$strVipClient = '('.$strVipClient.')';
		
		$strSql = "INSERT INTO $strTblCnskTmpM (userid, keynum)
		   SELECT userid,COUNT(DISTINCT keywid)
		   FROM $strTblCnsk
		   WHERE userid IN ". $strVipClient ."
		   GROUP BY userid";
		printlog($strSql);
		$res = $dbhOut->query($strSql);
		if (DB::isError($res)) {
			$objLog->log('CRON_ERROR', array('error' => '插入临时表', 'tblname' => $strTblCnskTmpM));
			$strError = '插入临时表' . $strTblCnskTmpM . '失败！';
			printlog(__LINE__ . "\t::" . $strError, true);
			mail($cfg_strCrmAlarmEmail, 'SF-crm自动运行失败', $strError);
			exit;
		}
	}
}

/** 是否开通留言板 **/
$arrLeaveWord = array();
foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}
	
	$objSql->clear();
	$objSql->setMysqlServerVersion('6602');
	$objSql->setSqlTable($GLOBALS['glb_tblMessOverview']);
	$objSql->setSqlColumn('userid');
	$objSql->setSqlWhere("status", 1);
	$objSql->setSqlWhere('userid', $arrVipClient, SQL_WHERE_IN);
	$objSql->getSelectSql($strSql);
	printlog(__LINE__ . "\t::" . $strSql, true);
	$result = $GLOBALS['dbhOut']->query($strSql);
	while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
	    $arrLeaveWord[$row['userid']] = 1;      // 开通留言板
	}

}

/** 客户父用户ID*/
$arrCltFatId = array();
foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}

	$objSql->clear();
	$objSql->setSqlTable($GLOBALS['glb_tblUserMaps']);
	$objSql->setSqlColumn('sonuid');
	$objSql->setSqlColumn('fatuid');
	$objSql->setSqlWhere('sonuid', $arrVipClient, SQL_WHERE_IN);
	$objSql->getSelectSql($strSql);
	printlog(__LINE__ . "\t::" . $strSql, true);
	$result = $dbh->query($strSql);
	while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
		$arrCltFatId[$row['sonuid']] = $row['fatuid'];
	}
}

/******************** 取大客户信息 ********************/
// 取UserAcct相关数据
$objSql->clear();
$objSql->setSqlTable($GLOBALS['glb_tblUserAcct']);
$objSql->setSqlColumn('userid');
$objSql->setSqlColumn('username');
$objSql->setSqlColumn('ustatid');
//$objSql->setSqlWhere('userid', $GLOBALS['cfg_intInternalUserId'], SQL_WHERE_GT);
$objSql->setSqlWhere('ulevelid', USER_LEVEL_CLIENT);
$objSql->setSqlOrder('userid');
$objSql->getSelectSql($strSql);
printlog(__LINE__ . "\t::" . $strSql, true);
$result = $dbh->query($strSql);

$arrCltId = array();
while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
	$arrCltId[] = $row['userid'];
	$arrTmp = array();
	
	$arrTmp['userid'] = $row['userid'];
	$arrTmp['username'] = rmLnTab($row['username']);
	$arrTmp['fatuid'] = array_key_exists($row['userid'], $arrCltFatId)?$arrCltFatId[$row['userid']]:0;
	$arrTmp['ustatid'] = $row['ustatid'];
	
	// 初始化其它字段数据
	$arrTmp['company'] = '';
	$arrTmp['website'] = '';
	$arrTmp['provid'] = 0;
	$arrTmp['cityid'] = 0;
	$arrTmp['realname'] = '';
	$arrTmp['phone'] = '';
	$arrTmp['fax'] = '';
	$arrTmp['email'] = '';
	$arrTmp['address'] = '';
	$arrTmp['postcode'] = '';
	$arrTmp['regtime'] = '0000-00-00 00:00:00';
	$arrTmp['efftime'] = '0000-00-00 00:00:00';
	$arrTmp['exptime'] = '0000-00-00 00:00:00';
	$arrTmp['invest'] = 0;
	$arrTmp['balance'] = 0;
	$arrTmp['PERDAY_CONSUME'] = 0;
	$arrTmp['EXTEND_REGION'] = '0';
	$arrTmp['cnsknum_d'] = 0;		// 日有消费关键字数
	$arrTmp['cnsknum_w'] = 0;		// 周有消费关键字数
	$arrTmp['cnsknum_m'] = 0;	// 月有消费关键字数
	$arrTmp['knum'] = 0;			// 关键字总数
	$arrTmp['efftknum'] = 0;		// 生效关键字数
	$arrTmp['clicks_d'] = 0;		// 日点击次数
	$arrTmp['clicks_w'] = 0;		// 周点次数
	$arrTmp['clicks_m'] = 0;		// 月点击次数
	$arrTmp['cns_d'] = 0;			// 日点击消费
	$arrTmp['cns_w'] = 0;			// 周点击消费
	$arrTmp['cns_m'] = 0;			// 月点击消费
	$arrTmp['cash_d'] = 0;			// 日点击消费现金
	$arrTmp['cash_w'] = 0;		// 周点击消费现金
	$arrTmp['cash_m'] = 0;		// 月点击消费现金
	$arrTmp['opensum'] = 0;		// 开户金额
	$arrTmp['connnum'] = 0;		// 续费次数
	$arrTmp['connsum'] = 0;		// 续费金额
	$arrTmp['timeline '] = '0000-00-00 00:00:00';   // 下线时间
	if (array_key_exists ($row['userid'], $arrLeaveWord)) {
		$arrTmp['user_leaveword'] = 1;		// 留言板开通状态
	} else {
		$arrTmp['user_leaveword'] = 0;
	}
	
	p_setUserInfoTmp($row['userid'], $arrTmp, 'insert');
}

// 取UserInfo相关数据
foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}
	$objSql->clear();
	$objSql->setSqlTable($GLOBALS['glb_tblUserInfo']);
	$objSql->setSqlColumn('userid');
	$objSql->setSqlColumn('company');
	$objSql->setSqlColumn('website');
	$objSql->setSqlColumn('provid');
	$objSql->setSqlColumn('cityid');
	$objSql->setSqlColumn('realname');
	$objSql->setSqlColumn('phone');
	$objSql->setSqlColumn('fax');
	$objSql->setSqlColumn('email');
	$objSql->setSqlColumn('address');
	$objSql->setSqlColumn('postcode');
	$objSql->setSqlColumn('regtime');
	$objSql->setSqlColumn('efftime');
	$objSql->setSqlColumn('exptime');
	$objSql->setSqlWhere('userid', $arrVipClient, SQL_WHERE_IN);
	$objSql->getSelectSql($strSql);
	printlog(__LINE__ . "\t::" . $strSql, true);
	$result = $dbh->query($strSql);

	while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
		$arrTmp = array();

		$arrTmp['company'] = rmLnTab($row['company']);
		$arrTmp['website'] = rmLnTab($row['website']);
		$arrTmp['provid'] = $row['provid'];
		$arrTmp['cityid'] = $row['cityid'];
		$arrTmp['realname'] = rmLnTab($row['realname']);
		$arrTmp['phone'] = rmLnTab($row['phone']);
		$arrTmp['fax'] = rmLnTab($row['fax']);
		$arrTmp['email'] = rmLnTab($row['email']);
		$arrTmp['address'] = rmLnTab($row['address']);
		$arrTmp['postcode'] = rmLnTab($row['postcode']);
		$arrTmp['regtime'] = $row['regtime'] == null? '0000-00-00 00:00:00' : $row['regtime'] ;
		$arrTmp['efftime'] = $row['efftime'] == null? '0000-00-00 00:00:00' : $row['efftime'] ;
		$arrTmp['exptime'] = $row['exptime'] == null? '0000-00-00 00:00:00' : $row['exptime'] ;

		p_setUserInfoTmp($row['userid'], $arrTmp);
	}
}

// 取UserFund相关数据
foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}
	$objSql->clear();
	$objSql->setSqlTable($GLOBALS['glb_tblUserFund']);
	$objSql->setSqlColumn('userid');
	$objSql->setSqlColumn('(invest+turnin) invest');
	$objSql->setSqlColumn('balance');
	$objSql->setSqlWhere('userid', $arrVipClient, SQL_WHERE_IN);
	$objSql->getSelectSql($strSql);
	printlog(__LINE__ . "\t::" . $strSql, true);
	$result = $dbh->query($strSql);

	while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
		$arrTmp = array();

		$arrTmp['invest'] = $row['invest'];
		$arrTmp['balance'] = $row['balance'];

		p_setUserInfoTmp($row['userid'], $arrTmp);
	}
}

	
$objSql->clear();
$objSql->setMysqlServerVersion('6601');
$objSql->setSqlTable($GLOBALS['strTblStatUserTmpD']);
$objSql->setSqlColumn('userid');
$objSql->setSqlColumn('sum(clks) clks');
$objSql->setSqlColumn('sum(charge) charge');
$objSql->setSqlColumn('sum(cash) cash');
$objSql->setSqlGroup('userid');
$objSql->getSelectSql($strSql);
printlog(__LINE__ . "\t::" . $strSql, true);
$result = $dbhClk->query($strSql);

while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
	$arrTmp = array();
	
	$arrTmp['clicks_d'] = $row['clks'];	// 日点击次数
	$arrTmp['cns_d'] = $row['charge'];	// 日点击消费
	$arrTmp['cash_d'] = $row['cash'];	// 日点击消费现金
	
	p_setUserInfoTmp($row['userid'], $arrTmp);
}



/** 付款额、开户金额 **/
$arrMoney = array();
foreach ($arr2VipClient AS $arrVipClient) {
	if (empty($arrVipClient)) {
		break;
	}
	
	$objSql->clear();
	$objSql->setSqlTable($GLOBALS['glb_tblCltPayRec']);
	$objSql->setSqlColumn('clientid');
	$objSql->setSqlColumn('openacct');
	$objSql->setSqlColumn('COUNT(clientid) cnt');
	$objSql->setSqlColumn('(SUM(invest) - SUM(refund)) sum');
	$objSql->setSqlWhere('paytime', $arrDayTime, SQL_WHERE_BETWEEN);
	$objSql->setSqlWhere('clientid', $arrVipClient, SQL_WHERE_IN);
	$objSql->setSqlGroup('clientid');
	$objSql->setSqlGroup('openacct');
	$objSql->getSelectSql($strSql);
	$result = $GLOBALS['dbh']->query($strSql);
	printlog(__LINE__ . "\t::" . $strSql, true);
	
	while ($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
		if ($row['openacct'] == 0) {
			$arrMoney[$row['clientid']]['connsum'] += $row['sum'];
			$arrMoney[$row['clientid']]['connnum'] += $row['cnt'];
		} else {
			$arrMoney[$row['clientid']]['opensum'] += $row['sum'];
		}
	}
}

foreach ($arrMoney AS $intMoneyUid => $arrMoneyArray) {
	p_setUserInfoTmp($intMoneyUid, $arrMoneyArray);
}
////////////////////////////////////////////////////////////////

if (!$handle = fopen($strFileCrmImpCltTmp, 'w')) {
	$strErrInfo = '不能打开文件 '.$strFileCrmImpCltTmp;
	printlog(__LINE__ . "\t::" . $strErrInfo, true);
	exit;
}
if (!flock($handle, LOCK_EX)) {
	$strErrInfo = '不能锁定文件 '.$strFileCrmImpCltTmp;
	printlog(__LINE__ . "\t::" . $strErrInfo, true);
	@fclose($handle);
	exit;
}

$objSql->clear();
$objSql->setMysqlServerVersion('6601');
$objSql->setSqlTable($GLOBALS['strTblUserInfoTmp']);
$objSql->setSqlColumn('*');
$objSql->getSelectSql($strSql);
printlog(__LINE__ . "\t::" . $strSql, true);
$result = $dbhClk->query($strSql);

$strCont = '';
$i = 0;
while($row = $result->fetchRow(DB_FETCHMODE_ASSOC)) {
	$i ++;
	$strTmp = implode("\t", $row);
	$strCont .= $strTmp."\n";
	
	if ($i % 1000 == 0) {
		if(fwrite($handle, $strCont) === FALSE) {
			$strErrInfo = "不能写入到文件 $strFileImp";
			@fclose($handle);
			printlog(__LINE__ . "\t::" . $strErrInfo, true);
			exit;
		}
		$strCont = '';
		$i = 0;
	}
}

if ($i > 0) {
	if(fwrite($handle, $strCont) === FALSE) {
		$strErrInfo = "不能写入到文件 $strFileImp";
		@fclose($handle);
		printlog(__LINE__ . "\t::" . $strErrInfo, true);
		exit;
	}
}

flock($handle, LOCK_UN); // 释放锁定
fclose($handle);

$strMd5 = md5_file($strFileCrmImpCltTmp);
crm_file_put_contents($strFileCrmImpCltMd5Tmp, $strMd5."  ".$strFileCrmImpCltName);

// 移动
@unlink($strFileCrmImpClt);
@unlink($strFileCrmImpCltMd5);

rename($strFileCrmImpCltTmp, $strFileCrmImpClt);
rename($strFileCrmImpCltMd5Tmp, $strFileCrmImpCltMd5);

$objLog->log('CRON_COMPLETE');
printlog(basename(__FILE__)."::执行结束", true);

task_end($strDateStat);
?>
