prod_cdc_prodline_sys_cash_day
CREATE TABLE 百度商业产品线收入表
  (
    st_date      VARCHAR2 ,
    alb_cust_id  NUMBER ,
    acct_id      NUMBER ,
    prod_line_id VARCHAR2 ,
    cash         NUMBER ,
    linkid UNKNOWN
    --  ERROR: Datatype UNKNOWN is not allowed
    NOT NULL
  ) ;
COMMENT ON COLUMN 百度商业产品线收入表.st_date
IS
  '发生的业务日期' ;
  COMMENT ON COLUMN 百度商业产品线收入表.alb_cust_id
IS
  '客户id，alb中的passportid，品专、品牌起跑线的广告主id' ;
  COMMENT ON COLUMN 百度商业产品线收入表.acct_id
IS
  '走uc账户体系的账户id，主要是fc、bd、zx、qs的一部分' ;
  COMMENT ON COLUMN 百度商业产品线收入表.prod_line_id
IS
  '流量分类的id' ;
  COMMENT ON COLUMN 百度商业产品线收入表.cash
IS
  '总现金' ;
  CREATE INDEX 百度商业产品线收入表__IDX ON 百度商业产品线收入表
    ( linkid ASC
    ) ;
  ALTER TABLE 百度商业产品线收入表 ADD CONSTRAINT 百度商业产品线收入表_PK PRIMARY KEY
  (
    linkid
  )
  ;
