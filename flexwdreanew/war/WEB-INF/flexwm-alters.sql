-- FLEX ALTERS, ORDEN CRONOLOGICO
alter table users add column user_startprogramid INT;
alter table flexconfig add column flxc_opportunityforeignprogramid INT;
alter table flexconfig add column flxc_enablequotes INT;
alter table wflowcategories add column wfca_effectwflowcategoryid int;
ALTER TABLE wflowcategories  ADD FOREIGN KEY (wfca_effectwflowcategoryid) REFERENCES wflowcategories(wfca_wflowcategoryid);
alter table modules add column modu_ispublic int;
alter table flexconfig add column flxc_opportunityforeignprogramid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_opportunityforeignprogramid) REFERENCES modules(modu_programid);
alter table flexconfig add column flxc_enablequotes INT;
alter table wflowcategories add column wfca_effectwflowcategoryid int;
ALTER TABLE wflowcategories  ADD FOREIGN KEY (wfca_effectwflowcategoryid) REFERENCES wflowcategories(wfca_wflowcategoryid);
ALTER TABLE `sfconfig` 
	ADD COLUMN `sfcf_stdtimezone` VARCHAR(10) NULL AFTER `sfcf_clientsecrets`,
	ADD COLUMN `sfcf_stdstart` DATE NULL AFTER `sfcf_stdtimezone`,
	ADD COLUMN `sfcf_daytimezone` VARCHAR(10) NULL AFTER `sfcf_stdstart`,
	ADD COLUMN `sfcf_daystart` DATE NULL AFTER `sfcf_daytimezone`;
ALTER TABLE `bankmovconcepts` CHANGE COLUMN `bkmc_code` `bkmc_code` VARCHAR(10) NULL ;


-- Agregar campo de reply to en los correos
alter table emails add column emai_replyto VARCHAR(255);


-- Creacion de tabla para equipos en cotizaciones
CREATE TABLE quoteequipments (
	qoeq_quoteequipmentid INT NOT NULL AUTO_INCREMENT, 
	qoeq_name VARCHAR(30),
	qoeq_quantity INT NOT NULL,
	qoeq_price DOUBLE,
	qoeq_days FLOAT,
	qoeq_amount DOUBLE,
	qoeq_quoteid INT NOT NULL,
	qoeq_equipmentid INT NOT NULL,
	qoeq_sflog TEXT,
	PRIMARY KEY(qoeq_quoteequipmentid),
	FOREIGN KEY (qoeq_quoteid) REFERENCES quotes(quot_quoteid),
	FOREIGN KEY (qoeq_equipmentid) REFERENCES equipments(equi_equipmentid)
);

-- Agregar bloqueo de usuarios de wflow
ALTER TABLE wflowusers ADD COLUMN wflu_lockstart DATETIME;
ALTER TABLE wflowusers ADD COLUMN wflu_lockend DATETIME;
ALTER TABLE wflowusers ADD COLUMN wflu_islocked INT;

ALTER TABLE equipments ADD COLUMN equi_price DOUBLE;
ALTER TABLE sffiles MODIFY COLUMN file_blobkey VARCHAR(500);
ALTER TABLE projectdetails DROP COLUMN pjde_loadenddate;
ALTER TABLE projectdetails ADD COLUMN pjde_prepdate DATETIME;
ALTER TABLE projectdetails ADD COLUMN pjde_exitdate DATETIME;
ALTER TABLE projectdetails ADD COLUMN pjde_returndate DATETIME;

ALTER TABLE formats ADD COLUMN frmt_publishvalidateclass VARCHAR(50);

ALTER TABLE users ADD COLUMN user_signaturl VARCHAR(100);

ALTER TABLE wflowusers DROP COLUMN wflu_islocked;
ALTER TABLE projectequipments DROP COLUMN pjeq_islocked;
ALTER TABLE wflowusers ADD COLUMN wflu_lockstatus CHAR;
ALTER TABLE projectequipments ADD COLUMN pjeq_lockstatus CHAR;

ALTER TABLE orderitems ADD COLUMN ordi_lockstatus CHAR;
ALTER TABLE orderitems MODIFY COLUMN ordi_days DOUBLE;

ALTER TABLE orders ADD COLUMN orde_lockstart DATETIME;
ALTER TABLE orders ADD COLUMN orde_lockend DATETIME;
ALTER TABLE orders ADD COLUMN orde_lockstatus CHAR;

ALTER TABLE flexconfig ADD COLUMN flxc_enableopportunityeffect INT;

ALTER TABLE paccounts ADD COLUMN pacc_balance FLOAT NULL;
ALTER TABLE raccounts ADD COLUMN racc_balance FLOAT NULL;
ALTER TABLE warehouses ADD COLUMN ware_type CHAR;

-- 20140728
ALTER TABLE requisitions ADD COLUMN reqi_type CHAR;
ALTER TABLE products ADD COLUMN prod_rentalcost FLOAT;
ALTER TABLE requisitionitems ADD COLUMN rqit_name VARCHAR(30);
ALTER TABLE requisitionitems MODIFY COLUMN rqit_productid INT;

ALTER TABLE requisitions MODIFY COLUMN reqi_warehouseid INT;
ALTER TABLE requisitions ADD COLUMN reqi_taxapplies INT;

ALTER TABLE suppliers MODIFY COLUMN supl_description VARCHAR(255);
ALTER TABLE suppliers MODIFY COLUMN supl_legalrep VARCHAR(50);

-- 20140729
CREATE TABLE whsections (
whse_whsectionid INT NOT NULL AUTO_INCREMENT, 
whse_code VARCHAR(10) NOT NULL,
whse_name VARCHAR(50) NOT NULL,
whse_description VARCHAR(255),
whse_warehouseid INT,
whse_sflog TEXT,
PRIMARY KEY (whse_whsectionid),
FOREIGN KEY (whse_warehouseid) REFERENCES warehouses (ware_warehouseid)
 );

ALTER TABLE whmovements DROP FOREIGN KEY whmovements_ibfk_2;
ALTER TABLE whmovements DROP FOREIGN KEY whmovements_ibfk_1;
ALTER TABLE whmovements DROP INDEX whmv_towarehouseid;

ALTER TABLE whmovements CHANGE COLUMN whmv_basewarehouseid whmv_basewhsectionid INT;
ALTER TABLE whmovements CHANGE COLUMN whmv_towarehouseid whmv_towhsectionid INT;

ALTER TABLE whmovements ADD FOREIGN KEY (whmv_basewhsectionid) REFERENCES whsections(whse_whsectionid);
ALTER TABLE whmovements ADD FOREIGN KEY (whmv_towhsectionid) REFERENCES whsections(whse_whsectionid);

-- DROP TABLE whstocks;
CREATE TABLE whstocks (
	whst_whstockid INT NOT NULL AUTO_INCREMENT, 
	whst_whsectionid INT NOT NULL,
	whst_productid INT NOT NULL,
	whst_quantity INT NOT NULL,
	whst_amount DOUBLE,
	whst_sflog TEXT,
	PRIMARY KEY(whst_whstockid),
FOREIGN KEY(whst_whsectionid) REFERENCES whsections(whse_whsectionid),
	FOREIGN KEY (whst_productid) REFERENCES products(prod_productid)
);

ALTER TABLE flexconfig ADD COLUMN flxc_orderwarehouseid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_orderwarehouseid) REFERENCES warehouses(ware_warehouseid);

-- 20140801
ALTER TABLE modules ADD COLUMN modu_audit INT;

CREATE TABLE sflogs (
	sflg_sflogid INT NOT NULL AUTO_INCREMENT, 
	sflg_timestamp DATETIME NOT NULL,
	sflg_header VARCHAR(255),
	sflg_data TEXT NOT NULL, 
	sflg_recordid INT,
	sflg_email VARCHAR(100),
	sflg_userid INT NOT NULL,
	sflg_programid INT, 
	PRIMARY KEY(sflg_sflogid),
 	FOREIGN KEY (sflg_userid) REFERENCES users(user_userid),
 	FOREIGN KEY (sflg_programid) REFERENCES modules(modu_programid)
);

-- 20140820 cargo  y abono
ALTER TABLE paccounts ADD COLUMN pacc_balance DOUBLE NULL;
ALTER TABLE paccounts ADD COLUMN pacc_withdraw DOUBLE NULL, ADD COLUMN pacc_deposit DOUBLE NULL;
ALTER TABLE paccounts CHANGE COLUMN pacc_amount pacc_amount FLOAT NULL ;
ALTER TABLE raccounts ADD COLUMN racc_balance DOUBLE NULL;
ALTER TABLE raccounts ADD COLUMN racc_withdraw DOUBLE NULL, ADD COLUMN racc_deposit DOUBLE NULL;
ALTER TABLE raccounts CHANGE COLUMN racc_amount racc_amount FLOAT NULL ;
ALTER TABLE bankmovements ADD COLUMN bkmv_withdraw DOUBLE NULL, ADD COLUMN bkmv_deposit DOUBLE NULL;
ALTER TABLE bankmovements CHANGE COLUMN bkmv_amount bkmv_amount FLOAT NULL ;
ALTER TABLE bankmovements MODIFY COLUMN bkmv_bankreference VARCHAR(20) NULL DEFAULT NULL;

ALTER TABLE sfconfig ADD COLUMN sfcf_defaultcompanyid INT;
ALTER TABLE sfconfig ADD FOREIGN KEY (sfcf_defaultcompanyid) REFERENCES companies(comp_companyid);

ALTER TABLE orders ADD COLUMN orde_customerid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_customerid) REFERENCES customers(cust_customerid);

ALTER TABLE wflows ADD COLUMN wflw_status CHAR;
ALTER TABLE bankmovconcepts ADD COLUMN bkmc_foreignid INT;

ALTER TABLE bankmovements MODIFY COLUMN bkmv_description VARCHAR(255);

ALTER TABLE flexconfig ADD COLUMN flxc_orderraccounttypeid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_orderraccounttypeid) REFERENCES raccounttypes(ract_raccounttypeid);

ALTER TABLE whsections ADD COLUMN whse_type CHAR;
ALTER TABLE whsections ADD COLUMN whse_orderid INT;
ALTER TABLE whsections ADD FOREIGN KEY (whse_orderid) REFERENCES orders(orde_orderid);

ALTER TABLE flexconfig ADD COLUMN flxc_defaultwhsectionid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultwhsectionid) REFERENCES whsections(whse_whsectionid);

-- Cambios 20140826 almacenes
ALTER TABLE whmovitems DROP COLUMN whmi_whtrackid;
ALTER TABLE whmovitems DROP FOREIGN KEY whmovitems_ibfk_3;

ALTER TABLE whtracks ADD COLUMN whtr_whmovitemid INT;
ALTER TABLE whtracks ADD FOREIGN KEY (whtr_whmovitemid) REFERENCES whmovitems(whmi_whmovitemid);

ALTER TABLE wflowlogs MODIFY wflg_data MEDIUMTEXT;

ALTER TABLE whtracks ADD COLUMN whtr_whsectionid INT;
ALTER TABLE whtracks ADD FOREIGN KEY (whtr_whsectionid) REFERENCES whsections(whse_whsectionid);

ALTER TABLE opportunitydetails MODIFY COLUMN opde_description TEXT;
ALTER TABLE quoteitems MODIFY COLUMN qoit_name VARCHAR(50);
ALTER TABLE customeraddress MODIFY COLUMN cuad_street VARCHAR(100);
ALTER TABLE suppliers MODIFY COLUMN supl_name VARCHAR(50);

ALTER TABLE quoteitems MODIFY COLUMN qoit_name VARCHAR(200);
ALTER TABLE orderitems MODIFY COLUMN ordi_name VARCHAR(200);
ALTER TABLE equipments MODIFY COLUMN equi_name VARCHAR(50);

ALTER TABLE customers MODIFY COLUMN cust_legalname VARCHAR(100);

ALTER TABLE wflowsteptypes CHANGE COLUMN wfst_validateclass wfst_actionclass VARCHAR(100);
ALTER TABLE wflowsteps CHANGE COLUMN wfsp_validateclass wfsp_actionclass VARCHAR(100);
ALTER TABLE wflowsteptypes ADD COLUMN wfst_validateclass VARCHAR(100);
ALTER TABLE wflowsteps ADD COLUMN wfsp_validateclass VARCHAR(100);

-- DROP TABLE projectequipments;
CREATE TABLE `projectequipment` (
  `peqi_projectequipmentid` int(11) NOT NULL AUTO_INCREMENT,
  `peqi_code` varchar(10) DEFAULT NULL,
  `peqi_name` varchar(50) DEFAULT NULL,
  `peqi_description` varchar(1000) DEFAULT NULL,
  `peqi_equipmentid` int(11) DEFAULT NULL,
  `peqi_projectid` int(11) DEFAULT NULL,
  `peqi_enddate` datetime DEFAULT NULL,
  `peqi_startdate` datetime DEFAULT NULL,
  `peqi_usercreateid` int(11) DEFAULT NULL,
  `peqi_usermodifyid` int(11) DEFAULT NULL,
  `peqi_datecreate` datetime DEFAULT NULL,
  `peqi_datemodify` datetime DEFAULT NULL,
  PRIMARY KEY (`peqi_projectequipmentid`),
  KEY `peqi_equipmentid` (`peqi_equipmentid`),
  KEY `peqi_projectid` (`peqi_projectid`),
  FOREIGN KEY (`peqi_equipmentid`) REFERENCES `equipments` (`equi_equipmentid`),
  FOREIGN KEY (`peqi_projectid`) REFERENCES `projects` (`proj_projectid`);
  
CREATE TABLE orderequipments (
	ordq_orderequipmentid INT NOT NULL AUTO_INCREMENT, 
	ordq_name VARCHAR(30),
	ordq_quantity INT NOT NULL,
	ordq_price DOUBLE,
	ordq_days FLOAT,
	ordq_amount DOUBLE,
	ordq_lockstart DATETIME,
	ordq_lockend DATETIME,
	ordq_lockstatus CHAR,
	ordq_hasconflict INT,
	ordq_orderid INT NOT NULL,
	ordq_equipmentid INT NOT NULL,
	ordq_sflog TEXT,
	PRIMARY KEY(ordq_orderequipmentid),
	FOREIGN KEY (ordq_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (ordq_equipmentid) REFERENCES equipments(equi_equipmentid)
);

ALTER TABLE quotes ADD COLUMN quot_showequipmentquantity INT;
ALTER TABLE quotes ADD COLUMN quot_showequipmentprice INT;

CREATE TABLE quotestaff (
	qost_quotestaffid INT NOT NULL AUTO_INCREMENT, 
	qost_description VARCHAR(255),
	qost_quantity INT NOT NULL,
	qost_days FLOAT,
	qost_price DOUBLE,
	qost_amount DOUBLE,
	qost_quoteid INT NOT NULL,
	qost_profileid INT NOT NULL,
	qost_sflog TEXT,
	PRIMARY KEY(qost_quotestaffid),
	FOREIGN KEY (qost_quoteid) REFERENCES quotes(quot_quoteid),
	FOREIGN KEY (qost_profileid) REFERENCES groups(prof_profileid)
);

ALTER TABLE groups ADD COLUMN grup_price DOUBLE;


CREATE TABLE orderstaff (
	ords_orderstaffid INT NOT NULL AUTO_INCREMENT, 
	ords_description VARCHAR(255),
	ords_price DOUBLE,
	ords_days FLOAT,
	ords_amount DOUBLE,
	ords_lockstart DATETIME,
	ords_lockend DATETIME,
	ords_lockstatus CHAR,
	ords_hasconflict INT,
	ords_orderid INT NOT NULL,
	ords_usergroupid INT NOT NULL,
	ords_sflog TEXT,
	PRIMARY KEY(ords_orderstaffid),
	FOREIGN KEY (ords_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (ords_usergroupid) REFERENCES programprofiles(pfus_usergroupid)
);

CREATE TABLE paccountitems (
	pait_paccountitemid INT NOT NULL AUTO_INCREMENT, 
	pait_name VARCHAR(30),
	pait_quantity INT NOT NULL,
	pait_price FLOAT NOT NULL,
	pait_amount FLOAT NOT NULL,
	pait_paccountid INT NOT NULL,
	pait_sflog TEXT,
	PRIMARY KEY(pait_paccountitemid),
	FOREIGN KEY (pait_paccountid) REFERENCES paccounts(pacc_paccountid)
);

ALTER TABLE whboxtracks ADD COLUMN whbt_quantity INT;

ALTER TABLE quoteitems ADD COLUMN qoit_description VARCHAR(500);
ALTER TABLE orderitems ADD COLUMN ordi_description VARCHAR(500);

ALTER TABLE bankmovconcepts MODIFY COLUMN bkmc_code VARCHAR(10);

ALTER TABLE suppliers ADD COLUMN supl_legalname VARCHAR(200);
ALTER TABLE whboxes MODIFY COLUMN whbx_code VARCHAR(30);

CREATE TABLE sfcomponents (
	sfcm_sfcomponentid INT NOT NULL AUTO_INCREMENT, 
	sfcm_code VARCHAR(10), 
	sfcm_name VARCHAR(30), 
	sfcm_description VARCHAR(255),
	sfcm_ispublic INT,
	sfcm_sflog TEXT,
	PRIMARY KEY(sfcm_sfcomponentid) 
);

ALTER TABLE modules DROP FOREIGN KEY modules_ibfk_1;
ALTER TABLE modules DROP COLUMN modu_componentid;
-- DROP TABLE components;
ALTER TABLE modules ADD COLUMN modu_sfcomponentid INT;
ALTER TABLE modules ADD FOREIGN KEY (modu_sfcomponentid) REFERENCES sfcomponents(sfcm_sfcomponentid);

bmoQuoteGroup
CREATE TABLE sfcomponentaccess (
	sfca_sfcomponentaccessid INT NOT NULL AUTO_INCREMENT, 
	sfca_read INT, 
	sfca_write INT, 
	sfca_delete INT, 
	sfca_print INT,
	sfca_disclosure CHAR,
	sfca_profileid INT NOT NULL, 
	sfca_sfcomponentid INT,
	sfca_sflog TEXT, 
	PRIMARY KEY(sfca_sfcomponentaccessid), 
	FOREIGN KEY (sfca_profileid) REFERENCES groups(prof_profileid), 
	FOREIGN KEY (sfca_sfcomponentid) REFERENCES sfcomponents(sfcm_sfcomponentid)
);

CREATE TABLE sfcomponentspecials (
	sfcs_sfcomponentspecialid INT NOT NULL AUTO_INCREMENT, 
	sfcs_code VARCHAR(10) NOT NULL, 
	sfcs_name VARCHAR(30) NOT NULL, 
	sfcs_description VARCHAR(255), 
	sfcs_sfcomponentid INT NOT NULL,
	sfcs_sflog TEXT,
	PRIMARY KEY(sfcs_sfcomponentspecialid),
 	FOREIGN KEY (sfcs_sfcomponentid) REFERENCES sfcomponents(sfcm_sfcomponentid)
);

CREATE TABLE sfcomponentspecialaccess (
	sfce_sfcomponentspecialaccessid INT NOT NULL AUTO_INCREMENT, 
	sfce_sfcomponentaccessid INT NOT NULL, 
	sfce_sfcomponentspecialid INT NOT NULL,
	sfce_sflog TEXT, 
	PRIMARY KEY(sfce_sfcomponentspecialaccessid), 
	FOREIGN KEY (sfce_sfcomponentaccessid) REFERENCES sfcomponentaccess(sfca_sfcomponentaccessid), 
	FOREIGN KEY (sfce_sfcomponentspecialid) REFERENCES sfcomponentspecials(sfcs_sfcomponentspecialid)
);

ALTER TABLE modules DROP COLUMN ispublic;

ALTER TABLE raccounts ADD COLUMN racc_paymentstatus CHAR NULL AFTER racc_deposit;
ALTER TABLE paccounts ADD COLUMN pacc_paymentstatus CHAR NULL AFTER pacc_deposit;



CREATE TABLE projectguidelines (
pjgi_projectguidelineid INT NOT NULL AUTO_INCREMENT,
	pjgi_guidelines TEXT,
	pjgi_projectid INT NOT NULL,
	pjgi_sflog TEXT,
	PRIMARY KEY(pjgi_projectguidelineid),
	FOREIGN KEY (pjgi_projectid) REFERENCES projects(proj_projectid)
);

ALTER TABLE whmovements ADD COLUMN whmv_status CHAR;

ALTER TABLE bankmovements MODIFY COLUMN bkmv_code VARCHAR(10);


CREATE TABLE wflowactions (
	wfac_wflowactionid INT NOT NULL AUTO_INCREMENT, 
	wfac_code VARCHAR(10), 
	wfac_name VARCHAR(30), 
	wfac_description VARCHAR(255), 
	wfac_classname VARCHAR(255), 
	wfac_sflog TEXT,
	PRIMARY KEY(wfac_wflowactionid)
);

CREATE TABLE wflowvalidations (
	wfva_wflowvalidationid INT NOT NULL AUTO_INCREMENT, 
	wfva_code VARCHAR(10), 
	wfva_name VARCHAR(30), 
	wfva_description VARCHAR(255), 
	wfva_classname VARCHAR(255), 
	wfva_sflog TEXT,
	PRIMARY KEY(wfva_wflowvalidationid)
);

ALTER TABLE wflowsteptypes DROP COLUMN wfst_validateclass;
ALTER TABLE wflowsteptypes DROP COLUMN wfst_actionclass;
ALTER TABLE wflowsteps DROP COLUMN wfsp_validateclass;
ALTER TABLE wflowsteps DROP COLUMN wfsp_actionclass;

ALTER TABLE wflowsteptypes ADD COLUMN wfst_wflowvalidationid INT;
ALTER TABLE wflowsteptypes ADD FOREIGN KEY (wfst_wflowvalidationid) REFERENCES wflowvalidations(wfva_wflowvalidationid);
ALTER TABLE wflowsteptypes ADD COLUMN wfst_wflowactionid INT;
ALTER TABLE wflowsteptypes ADD FOREIGN KEY (wfst_wflowactionid) REFERENCES wflowactions(wfac_wflowactionid);

ALTER TABLE wflowsteps ADD COLUMN wfsp_wflowvalidationid INT;
ALTER TABLE wflowsteps ADD FOREIGN KEY (wfsp_wflowvalidationid) REFERENCES wflowvalidations(wfva_wflowvalidationid);
ALTER TABLE wflowsteps ADD COLUMN wfsp_wflowactionid INT;
ALTER TABLE wflowsteps ADD FOREIGN KEY (wfsp_wflowactionid) REFERENCES wflowactions(wfac_wflowactionid);


ALTER TABLE requisitions ADD COLUMN reqi_userid INT NULL AFTER reqi_taxapplies;
ALTER TABLE paccounts ADD COLUMN pacc_userid INT NULL AFTER pacc_paymentstatus;
ALTER TABLE bankmovements ADD COLUMN bkmv_userid INT NULL AFTER bkmv_sflog;


ALTER TABLE equipmentuses ADD COLUMN eqis_whtrackid INT;
ALTER TABLE equipmentuses ADD FOREIGN KEY (eqis_whtrackid) REFERENCES whtracks(whtr_whtrackid);
ALTER TABLE equipmentservices ADD COLUMN eqsv_whtrackid INT;
ALTER TABLE equipmentservices ADD FOREIGN KEY (eqsv_whtrackid) REFERENCES whtracks(whtr_whtrackid);
ALTER TABLE equipmentuses MODIFY COLUMN eqis_equipmentid INT;
ALTER TABLE equipmentservices MODIFY COLUMN eqsv_equipmentid INT;

ALTER TABLE quotestaff MODIFY COLUMN qost_profileid INT;
ALTER TABLE orderstaff MODIFY COLUMN ords_usergroupid INT;
ALTER TABLE orderstaff ADD COLUMN ords_quantity INT;

ALTER TABLE groups ADD COLUMN grup_cost double;
ALTER TABLE wflows ADD COLUMN wflw_userid INT;
ALTER TABLE wflows ADD FOREIGN KEY (wflw_userid) REFERENCES users(user_userid);

ALTER TABLE orderequipments MODIFY COLUMN ordq_equipmentid INT;

ALTER TABLE bankmovtypes ADD COLUMN bkmt_bankmovtypechildid INT;
ALTER TABLE bankmovements ADD COLUMN bkmv_bankaccotransid INT;

ALTER TABLE orderstaff ADD COLUMN ords_name VARCHAR(50);
ALTER TABLE orderequipments ADD COLUMN ordq_description VARCHAR(255);

ALTER TABLE quotes ADD COLUMN quot_taxapplies INT;

CREATE TABLE customerpolls (
	cupo_cupoid INT NOT NULL AUTO_INCREMENT, 
	cupo_projectid INT NOT NULL,	
	cupo_polldate DATE,
	cupo_service INT,
	cupo_image INT,
	cupo_performance INT,
	cupo_quality INT,
	cupo_recommendation INT,
	cupo_observations VARCHAR(1024),
	cupo_sflog TEXT,
	PRIMARY KEY(cupo_cupoid),
	FOREIGN KEY (cupo_projectid) REFERENCES projects(proj_projectid)
);


ALTER TABLE quotestaff ADD COLUMN qost_name VARCHAR(50);

ALTER TABLE quoteequipments ADD COLUMN qoeq_description VARCHAR(255);
ALTER TABLE quoteequipments MODIFY COLUMN qoeq_equipmentid INT;

ALTER TABLE opportunities ADD COLUMN oppo_saledate DATE;
ALTER TABLE opportunities ADD COLUMN oppo_customfield1 VARCHAR(500);
ALTER TABLE opportunities ADD COLUMN oppo_customfield2 VARCHAR(500);
ALTER TABLE opportunities ADD COLUMN oppo_customfield3 VARCHAR(500);
ALTER TABLE opportunities ADD COLUMN oppo_customfield4 VARCHAR(500);

ALTER TABLE customeraddress ADD COLUMN cuad_description VARCHAR(500);

ALTER TABLE requisitionitems ADD COLUMN rqit_days INT;
ALTER TABLE raccounts ADD COLUMN racc_folio VARCHAR(30);

CREATE TABLE ordergroups (
	ordg_ordergroupid INT NOT NULL AUTO_INCREMENT, 
	ordg_code VARCHAR(10),
	ordg_name VARCHAR(100),
	ordg_description VARCHAR(255),
	ordg_amount DOUBLE,
	ordg_iskit INT,
	ordg_showquantity INT,
	ordg_showprice INT,
	ordg_orderid INT NOT NULL,
	ordg_sequence INT,
	ordg_sflog TEXT,
	PRIMARY KEY(ordg_ordergroupid),
	FOREIGN KEY (ordg_orderid) REFERENCES orders(orde_orderid)
);

CREATE TABLE currencies (
  	cure_currencyid INT NOT NULL AUTO_INCREMENT,
  	cure_code VARCHAR(10),
  	cure_name VARCHAR(30),
  	cure_description VARCHAR(255),  
  	cure_parity FLOAT,
  	cure_isbasecurrency INT,
  	cure_sflog TEXT,
  	PRIMARY KEY (cure_currencyid)
);


ALTER TABLE orderitems ADD COLUMN ordi_ordergroupid INT;
ALTER TABLE orderitems ADD FOREIGN KEY (ordi_ordergroupid) REFERENCES ordergroups(ordg_ordergroupid);
ALTER TABLE orderitems DROP FOREIGN KEY orderitems_ibfk_1;
ALTER TABLE orderitems DROP COLUMN ordi_orderid;

ALTER TABLE opportunities ADD COLUMN oppo_currencyid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE quotes ADD COLUMN quot_currencyid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE orders ADD COLUMN orde_currencyid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_currencyid) REFERENCES currencies(cure_currencyid);

-- Agregar alias y comentarios al contacto

ALTER TABLE customercontacts ADD COLUMN cuco_alias VARCHAR(50);
ALTER TABLE customercontacts ADD COLUMN cuco_commentalias VARCHAR(255);

-- tabla para los sitios web --
CREATE TABLE customerwebs (
  cuwb_customerwebid INT NOT NULL AUTO_INCREMENT,
  cuwb_website VARCHAR(100) NOT NULL,
  cuwb_customerid INT NOT NULL,
  cuwb_sflog TEXT NULL,  
  PRIMARY KEY (cuwb_customerwebid),
  FOREIGN KEY (cuwb_customerid) REFERENCES customers(cust_customerid)
);

ALTER TABLE equipmentuses ADD COLUMN eqis_orderid INT;
ALTER TABLE equipmentuses ADD FOREIGN KEY (eqis_orderid) REFERENCES orders(orde_orderid);
ALTER TABLE equipmentuses DROP FOREIGN KEY equipmentuses_ibfk_2;
ALTER TABLE equipmentuses DROP COLUMN eqis_projectid;

ALTER TABLE flexconfig ADD COLUMN flxc_currencyid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE wflowcategories ADD COLUMN wfca_emailreminders INT;

ALTER TABLE bankmovtypes ADD COLUMN bkmt_visible CHAR(1);

ALTER TABLE whmovements ADD COLUMN whmv_autocreateitems INT;

ALTER TABLE projects ADD COLUMN proj_customeraddressid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_customeraddressid) REFERENCES customeraddress(cuad_customeraddressid);

ALTER TABLE sfconfig MODIFY COLUMN sfcf_apptitle VARCHAR(50);

ALTER TABLE users ADD COLUMN user_uitemplate CHAR;

CREATE TABLE currencyrates (
  	cura_currencyrateid INT NOT NULL AUTO_INCREMENT,
  	cura_rate DOUBLE,
  	cura_datetime DATETIME,
	cura_currencyid INT NOT NULL,
  	cura_sflog TEXT,
  	PRIMARY KEY (cura_currencyrateid),
	FOREIGN KEY (cura_currencyid) REFERENCES currencies(cure_currencyid)
);

ALTER TABLE currencies ADD COLUMN cure_updatetime DATETIME;

ALTER TABLE wflowtypes ADD COLUMN wfty_comments TEXT;

ALTER TABLE customers ADD COLUMN cust_currencyid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE paccounts MODIFY COLUMN pacc_description VARCHAR(255);

ALTER TABLE bankaccounts ADD COLUMN bkac_profileid INT;
ALTER TABLE bankaccounts ADD FOREIGN KEY (bkac_profileid) REFERENCES groups(prof_profileid);

ALTER TABLE bankmovements ADD COLUMN bkmv_requisitionid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_requisitionid) REFERENCES requisitions(reqi_requisitionid);

ALTER TABLE sfconfig ADD COLUMN sfcf_logo VARCHAR(255);
ALTER TABLE customers ADD COLUMN cust_logo VARCHAR(255);
ALTER TABLE users ADD COLUMN user_photo VARCHAR(255);

CREATE TABLE tags (
	tags_tagid INT NOT NULL AUTO_INCREMENT, 
	tags_code VARCHAR(10), 
	tags_name VARCHAR(30), 
	tags_description VARCHAR(255),
	tags_sflog TEXT,
	PRIMARY KEY(tags_tagid) 
);

ALTER TABLE users ADD COLUMN user_tags VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN oppo_tags VARCHAR(255);
ALTER TABLE projects ADD COLUMN proj_tags VARCHAR(255);

ALTER TABLE users ADD COLUMN user_password VARCHAR(255);

ALTER TABLE raccounts MODIFY COLUMN racc_folio VARCHAR(60);

CREATE TABLE wflowuserselect (
	wfus_wflowuserselectid INT NOT NULL AUTO_INCREMENT,
	wfus_areaid INT,
	wfus_profileid INT NOT NULL,
	wfus_sflog TEXT,
	PRIMARY KEY(wfus_wflowuserselectid),
	FOREIGN KEY (wfus_areaid) REFERENCES areas(area_areaid),
	FOREIGN KEY (wfus_profileid) REFERENCES groups(prof_profileid)
);

CREATE TABLE requisitionreceipts (
	rerc_requisitionreceiptid INT NOT NULL AUTO_INCREMENT, 
	rerc_code VARCHAR(10), 
	rerc_name VARCHAR(60), 
	rerc_receiptdate DATE NOT NULL,
	rerc_status CHAR,
	rerc_payment CHAR,
	rerc_amount FLOAT, 
	rerc_discount FLOAT,	
	rerc_tax FLOAT,
	rerc_total FLOAT,	
	rerc_sflog TEXT,
	rerc_requisitionid INT NOT NULL,
	rerc_supplierId INT NOT NULL,
	PRIMARY KEY(rerc_requisitionreceiptid) ,
	FOREIGN KEY (rerc_requisitionid) REFERENCES requisitions(reqi_requisitionid),
	FOREIGN KEY (rerc_supplierid) REFERENCES suppliers(supl_supplierid)
);

CREATE TABLE requisitionreceiptitems (
	reit_requisitionreceiptitemid INT NOT NULL AUTO_INCREMENT, 
	reit_name VARCHAR(30),
	reit_quantity INT,
	reit_price FLOAT NOT NULL,
	reit_amount FLOAT NOT NULL,
	reit_requisitionreceiptid INT NOT NULL,
	reit_requisitionitemid INT,
	reit_sflog TEXT,
	PRIMARY KEY(reit_requisitionreceiptitemid),
	FOREIGN KEY (reit_requisitionreceiptid) REFERENCES requisitionreceipts(rerc_requisitionreceiptid)
);

ALTER TABLE paccounts ADD COLUMN pacc_requisitionreceiptid INT;
ALTER TABLE paccounts ADD FOREIGN KEY (pacc_requisitionreceiptid) REFERENCES requisitionreceipts(rerc_requisitionreceiptid);

ALTER TABLE customers ADD COLUMN cust_establishmentdate DATE;

ALTER TABLE paccounts ADD COLUMN pacc_depositId INT;

-- CAMBIOS 2015/02/26

ALTER TABLE opportunitydetails ADD COLUMN opde_showincontract INT NULL;

ALTER TABLE wflowusers ADD COLUMN wflu_geventid VARCHAR(100);

ALTER TABLE paccounts ADD COLUMN pacc_payments FLOAT;
ALTER TABLE raccounts ADD COLUMN racc_payments FLOAT;

ALTER TABLE raccounts ADD COLUMN racc_depositId INT;
ALTER TABLE paccounttypes ADD COLUMN pact_visible CHAR(1);
ALTER TABLE raccounttypes ADD COLUMN ract_visible CHAR(1);

ALTER TABLE requisitions ADD COLUMN reqi_receptionstatus CHAR(1);

ALTER TABLE paccounts ADD COLUMN pacc_amount FLOAT;
ALTER TABLE raccounts ADD COLUMN racc_amount FLOAT;

ALTER TABLE requisitions ADD COLUMN reqi_receptionstatus CHAR(1);
ALTER TABLE bankmovconcepts ADD COLUMN bkmc_total FLOAT;

ALTER TABLE whmovements ADD COLUMN whmv_type CHAR;
ALTER TABLE whmovements DROP FOREIGN KEY whmovements_ibfk_3;
ALTER TABLE whmovements DROP COLUMN whmv_whmovtypeid;
-- DROP TABLE whmovtypes;

ALTER TABLE sflogs ADD COLUMN sflg_sflog TEXT;

ALTER TABLE policies DROP FOREIGN KEY policies_ibfk_4;
ALTER TABLE policies DROP COLUMN poli_productid;

ALTER TABLE requisitionitems ADD COLUMN rqit_quantityreceipt INTEGER;
ALTER TABLE requisitionreceiptitems ADD COLUMN reit_days DOUBLE;
ALTER TABLE requisitionreceiptitems ADD COLUMN reit_quantitybalance INTEGER;

ALTER TABLE whmovements ADD COLUMN whmv_requisitionreceiptid INT;
ALTER TABLE whmovements ADD FOREIGN KEY (whmv_requisitionreceiptid) REFERENCES requisitionreceipts(rerc_requisitionreceiptid);

ALTER TABLE whmovements DROP FOREIGN KEY whmovements_ibfk_4;
ALTER TABLE whmovements DROP COLUMN whmv_requisitionid;

ALTER TABLE whmovitems ADD COLUMN whmi_requisitionreceiptitemid INT;
ALTER TABLE whmovitems ADD FOREIGN KEY (whmi_requisitionreceiptitemid) REFERENCES requisitionreceiptitems(reit_requisitionreceiptitemid);

ALTER TABLE requisitionreceiptitems ADD COLUMN reit_serial VARCHAR(40);

ALTER TABLE requisitionreceipts ADD COLUMN rerc_whsectionid INT;
ALTER TABLE requisitionreceipts ADD FOREIGN KEY (rerc_whsectionid) REFERENCES whsections(whse_whsectionid);

ALTER TABLE requisitionreceipts MODIFY COLUMN rerc_receiptdate DATETIME;

ALTER TABLE whmovements DROP FOREIGN KEY whmovements_ibfk_5;
ALTER TABLE whmovements DROP COLUMN whmv_orderid;

CREATE TABLE orderdeliveries (
	odly_orderdeliveryid INT NOT NULL AUTO_INCREMENT, 
	odly_code VARCHAR(10), 
	odly_name VARCHAR(60), 
	odly_deliverydate DATETIME NOT NULL,
	odly_status CHAR,
	odly_payment CHAR,
	odly_amount FLOAT, 
	odly_discount FLOAT,	
	odly_tax FLOAT,
	odly_total FLOAT,	
	odly_sflog TEXT,
	odly_fromwhsectionid INT,
	odly_towhsectionid INT,
	odly_orderid INT NOT NULL,
	odly_customerid INT NOT NULL,
	PRIMARY KEY(odly_orderdeliveryid),
	FOREIGN KEY (odly_fromwhsectionid) REFERENCES whsections(whse_whsectionid),
	FOREIGN KEY (odly_towhsectionid) REFERENCES whsections(whse_whsectionid),
	FOREIGN KEY (odly_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (odly_customerid) REFERENCES customers(cust_customerid)
);

CREATE TABLE orderdeliveryitems (
	odyi_orderdeliveryitemid INT NOT NULL AUTO_INCREMENT, 
	odyi_name VARCHAR(30),
	odyi_quantity INT,
	odyi_price FLOAT NOT NULL,
	odyi_amount FLOAT NOT NULL,
	odyi_days DOUBLE,
	odyi_quantitybalance INTEGER,
	odyi_serial VARCHAR(40),
	odyi_orderdeliveryid INT NOT NULL,
	odyi_orderitemid INT,
	odyi_productid INT,
	odyi_sflog TEXT,
	PRIMARY KEY(odyi_orderdeliveryitemid),
	FOREIGN KEY (odyi_productid) REFERENCES products(prod_productid),
	FOREIGN KEY (odyi_orderdeliveryid) REFERENCES orderdeliveries(odly_orderdeliveryid),
	FOREIGN KEY (odyi_orderitemid) REFERENCES orderitems(ordi_orderitemid)
);

ALTER TABLE whmovements ADD COLUMN whmv_orderdeliveryid INT;
ALTER TABLE whmovements ADD FOREIGN KEY (whmv_orderdeliveryid) REFERENCES orderdeliveries(odly_orderdeliveryid);

ALTER TABLE requisitions CHANGE reqi_receptionstatus reqi_activestatus CHAR;

ALTER TABLE orders ADD COLUMN orde_activestatus CHAR;
ALTER TABLE orders ADD COLUMN orde_type CHAR;

ALTER TABLE whmovitems ADD COLUMN whmi_orderdeliveryitemid INT;
ALTER TABLE whmovitems ADD FOREIGN KEY (whmi_orderdeliveryitemid) REFERENCES orderdeliveryitems(odyi_orderdeliveryitemid);

ALTER TABLE orderitems ADD COLUMN ordi_quantitydelivered INTEGER;

ALTER TABLE orderdeliveries ADD COLUMN odly_type CHAR;

ALTER TABLE orderdeliveryitems ADD COLUMN odyi_quantityreturned INT;

ALTER TABLE orderitems ADD COLUMN ordi_quantityreturned INT;

ALTER TABLE requisitionreceiptitems ADD COLUMN reit_quantityreturned INT;

ALTER TABLE requisitionitems ADD COLUMN rqit_quantityreturned INT;

ALTER TABLE requisitionreceipts ADD COLUMN rerc_type CHAR;

ALTER TABLE requisitionreceiptitems CHANGE COLUMN reit_reqiitemid reit_requisitionitemid INT;
ALTER TABLE requisitionreceiptitems ADD FOREIGN KEY (reit_requisitionitemid) REFERENCES requisitionitems(rqit_requisitionitemid);

ALTER TABLE requisitionreceipts MODIFY COLUMN rerc_name VARCHAR(60);

ALTER TABLE orderitems ADD COLUMN ordi_quantitybalance INT;

ALTER TABLE requisitionitems ADD COLUMN rqit_quantitybalance INT;

ALTER TABLE sfconfig ADD COLUMN sfcf_wflowcalendaruserid INT;

ALTER TABLE wflowusers ADD COLUMN wflu_publicgeventid VARCHAR(100);

ALTER TABLE wflowusers ADD COLUMN wflu_userpublicgeventid VARCHAR(100);

ALTER TABLE areas ADD COLUMN area_gcalendarid VARCHAR(100);

ALTER TABLE users ADD COLUMN user_publicgcalendarid VARCHAR(100);

-- 11 mar 2015

-- Status de Actividad --
ALTER TABLE paccounts ADD COLUMN pacc_activestatus CHAR;
ALTER TABLE raccounts ADD COLUMN racc_activestatus CHAR;
ALTER TABLE bankmovements ADD COLUMN bkmv_activestatus CHAR;

ALTER TABLE paccountitems ADD COLUMN pait_requisitionreceiptitemid INT;
ALTER TABLE paccountitems ADD FOREIGN KEY (pait_requisitionreceiptitemid) REFERENCES requisitionreceiptitems(reit_requisitionreceiptitemid);

CREATE TABLE raccountitems (
	rait_raccountitemid INT NOT NULL AUTO_INCREMENT,
	rait_name VARCHAR(30),
	rait_quantity INT NOT NULL,
	rait_price FLOAT NOT NULL,
	rait_amount FLOAT NOT NULL,
	rait_raccountid INT NOT NULL,
	rait_orderitemid INT,
	rait_orderequipmentid INT,
	rait_orderstaffid INT,
	rait_sflog TEXT,
	PRIMARY KEY(rait_raccountitemid),
	FOREIGN KEY (rait_raccountid) REFERENCES raccounts(racc_raccountid),
	FOREIGN KEY (rait_orderitemid) REFERENCES orderitems(ordi_orderitemid),
	FOREIGN KEY (rait_orderequipmentid) REFERENCES orderequipments(ordq_orderequipmentid),
	FOREIGN KEY (rait_orderstaffid) REFERENCES orderstaff(ords_orderstaffid)
);

ALTER TABLE customers ADD COLUMN cust_tags VARCHAR(255);

ALTER TABLE tags MODIFY COLUMN tags_code VARCHAR(30);

ALTER TABLE projects ADD COLUMN proj_orderid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_orderid) REFERENCES orders(orde_orderid);

ALTER TABLE orders ADD COLUMN orde_quoteid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_quoteid) REFERENCES quotes(quot_quoteid);

ALTER TABLE requisitions ADD COLUMN reqi_orderid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_orderid) REFERENCES orders(orde_orderid);

ALTER TABLE quotes ADD COLUMN quot_customerid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_customerid) REFERENCES customers(cust_customerid);

ALTER TABLE opportunities ADD COLUMN oppo_quoteid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_quoteid) REFERENCES quotes(quot_quoteid);

ALTER TABLE quotes ADD COLUMN quot_userid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_userid) REFERENCES users(user_userid);

ALTER TABLE quotes ADD COLUMN quot_wflowid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_wflowid) REFERENCES wflows(wflw_wflowid);

ALTER TABLE orders ADD COLUMN orde_wflowid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_wflowid) REFERENCES wflows(wflw_wflowid);

ALTER TABLE quotes ADD COLUMN quot_type CHAR;

ALTER TABLE orders ADD COLUMN orde_userid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_userid) REFERENCES users(user_userid);

ALTER TABLE opportunities ADD COLUMN oppo_type CHAR;
ALTER TABLE flexconfig ADD COLUMN flxc_defaultordertype CHAR;

ALTER TABLE quotes ADD COLUMN quot_startdate DATETIME;
ALTER TABLE quotes ADD COLUMN quot_enddate DATETIME;

ALTER TABLE customercontacts ADD COLUMN cuco_position VARCHAR(50);
ALTER TABLE customers ADD COLUMN cust_position VARCHAR(50);

ALTER TABLE customers MODIFY COLUMN cust_displayname VARCHAR(100);

ALTER TABLE flexconfig ADD COLUMN flxc_defaultreqpaytypeid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultreqpaytypeid) REFERENCES reqpaytypes(rqpt_reqpaytypeid);

ALTER TABLE flexconfig ADD COLUMN flxc_defaultwarehouseid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultwarehouseid) REFERENCES warehouses(ware_warehouseid);

ALTER TABLE orders DROP FOREIGN KEY orders_ibfk_1;
ALTER TABLE orders DROP COLUMN orde_projectid;

ALTER TABLE requisitions DROP FOREIGN KEY requisitions_ibfk_3;
ALTER TABLE requisitions DROP COLUMN reqi_projectid;

ALTER TABLE quotes DROP FOREIGN KEY quotes_ibfk_1;
ALTER TABLE quotes DROP COLUMN quot_opportunityid;

CREATE TABLE areacostcenters (
  	arcc_areacostcenterid INT NOT NULL AUTO_INCREMENT,
  	arcc_code VARCHAR(10),
  	arcc_name VARCHAR(30),
  	arcc_description VARCHAR(255),  
  	arcc_isgroup INT,
	arcc_parentid INT,
  	arcc_sflog TEXT,
  	PRIMARY KEY (arcc_areacostcenterid),
FOREIGN KEY (arcc_parentid) REFERENCES areacostcenters(arcc_areacostcenterid)
);

CREATE TABLE typecostcenters (
  	tycc_typecostcenterid INT NOT NULL AUTO_INCREMENT,
  	tycc_code VARCHAR(10),
  	tycc_name VARCHAR(30),
  	tycc_description VARCHAR(255),  
  	tycc_isgroup INT,
	tycc_parentid INT,
  	tycc_sflog TEXT,
  	PRIMARY KEY (tycc_typecostcenterid),
FOREIGN KEY (tycc_parentid) REFERENCES typecostcenters(tycc_typecostcenterid)
);

CREATE TABLE venturecostcenters (
  	vecc_venturecostcenterid INT NOT NULL AUTO_INCREMENT,
  	vecc_code VARCHAR(10),
  	vecc_name VARCHAR(30),
  	vecc_description VARCHAR(255),  
  	vecc_isgroup INT,
	vecc_parentid INT,
  	vecc_sflog TEXT,
  	PRIMARY KEY (vecc_venturecostcenterid),
FOREIGN KEY (vecc_parentid) REFERENCES venturecostcenters(vecc_venturecostcenterid)
);

ALTER TABLE orders ADD COLUMN orde_venturecostcenterid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_venturecostcenterid) REFERENCES venturecostcenters(vecc_venturecostcenterid);

ALTER TABLE modules DROP COLUMN modu_audit;

ALTER TABLE modules ADD COLUMN modu_enableformats int;
ALTER TABLE modules ADD COLUMN modu_enablefiles int;
ALTER TABLE modules ADD COLUMN modu_enableaudit int;

-- cambios 23 de marzo 2015

ALTER TABLE raccountitems MODIFY COLUMN rait_price DOUBLE;
ALTER TABLE raccountitems MODIFY COLUMN rait_amount DOUBLE;

ALTER TABLE raccounts MODIFY COLUMN racc_amount DOUBLE;
ALTER TABLE raccounts MODIFY COLUMN racc_payments DOUBLE;
ALTER TABLE raccounts MODIFY COLUMN racc_balance DOUBLE;

ALTER TABLE paccountitems MODIFY COLUMN pait_price DOUBLE;
ALTER TABLE paccountitems MODIFY COLUMN pait_amount DOUBLE;

ALTER TABLE paccounts MODIFY COLUMN pacc_amount DOUBLE;
ALTER TABLE paccounts MODIFY COLUMN pacc_payments DOUBLE;
ALTER TABLE paccounts MODIFY COLUMN pacc_balance DOUBLE;

ALTER TABLE requisitionreceiptitems MODIFY COLUMN reit_price DOUBLE;
ALTER TABLE requisitionreceiptitems MODIFY COLUMN reit_amount DOUBLE;

--
UPDATE quotes INNER JOIN opportunities ON (oppo_quoteid = quot_quoteid) SET quot_userid = oppo_userid;

UPDATE orders INNER JOIN projects ON (proj_orderid = orde_orderid) SET orde_userid = proj_userid;

ALTER TABLE requisitions MODIFY COLUMN reqi_amount DOUBLE;
ALTER TABLE requisitions MODIFY COLUMN reqi_discount DOUBLE;
ALTER TABLE requisitions MODIFY COLUMN reqi_tax DOUBLE;
ALTER TABLE requisitions MODIFY COLUMN reqi_total DOUBLE;

ALTER TABLE requisitionitems MODIFY COLUMN rqit_price DOUBLE;
ALTER TABLE requisitionitems MODIFY COLUMN rqit_amount DOUBLE;
ALTER TABLE requisitionitems MODIFY COLUMN rqit_days DOUBLE;

ALTER TABLE requisitionreceipts MODIFY COLUMN rerc_amount DOUBLE;
ALTER TABLE requisitionreceipts MODIFY COLUMN rerc_discount DOUBLE;
ALTER TABLE requisitionreceipts MODIFY COLUMN rerc_tax DOUBLE;
ALTER TABLE requisitionreceipts MODIFY COLUMN rerc_total DOUBLE;

ALTER TABLE quoteitems MODIFY COLUMN qoit_days DOUBLE;
ALTER TABLE quoteequipments MODIFY COLUMN qoeq_days DOUBLE;
ALTER TABLE quotestaff MODIFY COLUMN qost_days DOUBLE;

ALTER TABLE orderequipments MODIFY COLUMN ordq_days DOUBLE;
ALTER TABLE orderstaff MODIFY COLUMN ords_days DOUBLE;

ALTER TABLE orderdeliveryitems MODIFY COLUMN odyi_price DOUBLE;
ALTER TABLE orderdeliveryitems MODIFY COLUMN odyi_amount DOUBLE;

ALTER TABLE orderdeliveries MODIFY COLUMN odly_amount DOUBLE;
ALTER TABLE orderdeliveries MODIFY COLUMN odly_discount DOUBLE;
ALTER TABLE orderdeliveries MODIFY COLUMN odly_tax DOUBLE;
ALTER TABLE orderdeliveries MODIFY COLUMN odly_total DOUBLE;

ALTER TABLE opportunitydetails MODIFY COLUMN opde_deposit DOUBLE;
ALTER TABLE opportunitydetails MODIFY COLUMN opde_extrahour DOUBLE;

ALTER TABLE bankmovements MODIFY COLUMN bkmv_amount DOUBLE;

ALTER TABLE bankmovconcepts MODIFY COLUMN bkmc_amount DOUBLE;
ALTER TABLE bankmovconcepts MODIFY COLUMN bkmc_total DOUBLE;
ALTER TABLE bankaccounts MODIFY COLUMN bkac_balance DOUBLE;
ALTER TABLE bankaccounts MODIFY COLUMN bkac_initialbalance DOUBLE;

ALTER TABLE productkititems MODIFY COLUMN prki_days DOUBLE;
ALTER TABLE productkits MODIFY COLUMN prkt_amount DOUBLE;
ALTER TABLE products MODIFY COLUMN prod_price DOUBLE;
ALTER TABLE products MODIFY COLUMN prod_cost DOUBLE;
ALTER TABLE products MODIFY COLUMN prod_rentalcost DOUBLE;

ALTER TABLE whmovements MODIFY COLUMN whmv_amount DOUBLE;
ALTER TABLE whmovitems MODIFY COLUMN whmi_amount DOUBLE;

UPDATE raccounts SET racc_activestatus = 'O';



ALTER TABLE wflows MODIFY COLUMN wflw_code VARCHAR(10);
ALTER TABLE wflowtypes MODIFY COLUMN wfty_code VARCHAR(10);
ALTER TABLE wflowcategories MODIFY COLUMN wfca_code VARCHAR(10);
ALTER TABLE wflowsteps MODIFY COLUMN wfsp_code VARCHAR(10);
ALTER TABLE wflowsteptypes MODIFY COLUMN wfst_code VARCHAR(10);

CREATE TABLE uicolors (
  	uico_uicolorid INT NOT NULL AUTO_INCREMENT,
  	uico_name VARCHAR(30),
  	uico_css VARCHAR(30),
	uico_color VARCHAR(30),
  	uico_sflog TEXT,
  	PRIMARY KEY (uico_uicolorid)
);

ALTER TABLE tags ADD COLUMN tags_uicolorid INT;
ALTER TABLE tags ADD FOREIGN KEY (tags_uicolorid) REFERENCES uicolors(uico_uicolorid);

ALTER TABLE orderitems ADD COLUMN ordi_lockedquantity INT;
ALTER TABLE orderitems ADD COLUMN ordi_conflictquantity INT;

ALTER TABLE sfcomponents ADD COLUMN sfcm_wflowid INT;
ALTER TABLE sfcomponents ADD FOREIGN KEY (sfcm_wflowid) REFERENCES wflows(wflw_wflowid);

ALTER TABLE sfcomponents ADD COLUMN sfcm_wflowtypeid INT;
ALTER TABLE sfcomponents ADD FOREIGN KEY (sfcm_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);

CREATE TABLE orderlocks (
	orlk_orderlockid INT NOT NULL AUTO_INCREMENT,
	orlk_type CHAR,
	orlk_quantity INT NOT NULL,
	orlk_orderid INT NOT NULL,
	orlk_productid INT NOT NULL,
	orlk_requisitionid INT,
	orlk_whsectionid INT,
	orlk_sflog TEXT,
	PRIMARY KEY(orlk_orderlockid),
	FOREIGN KEY (orlk_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (orlk_productid) REFERENCES products(prod_productid),
	FOREIGN KEY (orlk_requisitionid) REFERENCES requisitions(reqi_requisitionid),
	FOREIGN KEY (orlk_whsectionid) REFERENCES whsections(whse_whsectionid)
);

ALTER TABLE flexconfig ADD COLUMN flxc_enableorderlock INT;

ALTER TABLE wflows ADD COLUMN wflw_googleeventid VARCHAR(100);

ALTER TABLE wflowcategories ADD COLUMN wfca_gcalendarid VARCHAR(100);

ALTER TABLE sfcomponents ADD COLUMN sfcm_startdate DATETIME;
ALTER TABLE sfcomponents ADD COLUMN sfcm_enddate DATETIME;

-- 24 abril 2015

ALTER TABLE orderstaff ADD COLUMN ords_profileid INT;
ALTER TABLE orderstaff ADD FOREIGN KEY (ords_profileid) REFERENCES groups(prof_profileid);

ALTER TABLE properties ADD COLUMN prty_lot VARCHAR(15);
ALTER TABLE properties ADD COLUMN prty_extraprice DOUBLE;

CREATE TABLE quoteproperties (
	qupy_quotepropertyid INT NOT NULL AUTO_INCREMENT, 
	qupy_price DOUBLE,
	qupy_extraland DOUBLE,
	qupy_extraconstruction DOUBLE,
	qupy_extraother DOUBLE,
	qupy_amount DOUBLE,
qupy_quoteid INT NOT NULL,
	qupy_propertyid INT,
	qupy_sflog TEXT,
	PRIMARY KEY(qupy_quotepropertyid),
	FOREIGN KEY (qupy_quoteid) REFERENCES quotes(quot_quoteid),
	FOREIGN KEY (qupy_propertyid) REFERENCES properties(prty_propertyid)
);

CREATE TABLE quotepropertymodelextras (
	qupx_quotepropertymodelextraid INT NOT NULL AUTO_INCREMENT, 
	qupx_price DOUBLE,
	qupx_quantity INT,
	qupx_amount DOUBLE,
	qupx_comments VARCHAR(255),
	qupx_quoteid INT NOT NULL,
	qupx_propertymodelextraid INT,
	qupx_sflog TEXT,
	PRIMARY KEY(qupx_quotepropertymodelextraid),
	FOREIGN KEY (qupx_quoteid) REFERENCES quotes(quot_quoteid),
	FOREIGN KEY (qupx_propertymodelextraid) REFERENCES propertymodelextras(prmx_propertymodelextraid)
);


ALTER TABLE wflowcategories ADD COLUMN wfca_expires INT;
ALTER TABLE wflowcategories ADD COLUMN wfca_expiredays INT;
ALTER TABLE users ADD COLUMN user_code VARCHAR(50);


ALTER TABLE opportunitydetails ADD COLUMN opde_guests INT;
ALTER TABLE opportunitydetails ADD COLUMN opde_venueid INT;
ALTER TABLE opportunitydetails ADD FOREIGN KEY (opde_venueid) REFERENCES venues(venu_venueid);
ALTER TABLE opportunities ADD COLUMN oppo_expiredate DATE;

CREATE TABLE wflowdocumenttypes (
	wfdt_wflowdocumenttypeid INT NOT NULL AUTO_INCREMENT, 
	wfdt_name VARCHAR(30), 
	wfdt_required INT NOT NULL,
	wfdt_wflowtypeid INT NOT NULL,
	wfdt_sflog TEXT,
	PRIMARY KEY(wfdt_wflowdocumenttypeid), 
	FOREIGN KEY (wfdt_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid)
);

CREATE TABLE wflowdocuments (
	wfdo_wflowdocumentid INT NOT NULL AUTO_INCREMENT, 
	wfdo_name VARCHAR(30), 
	wfdo_file VARCHAR(255),
	wfdo_isup INT,
	wfdo_required INT,
	wfdo_wflowid INT NOT NULL,
	wfdo_sflog TEXT,
	PRIMARY KEY(wfdo_wflowdocumentid),
 	FOREIGN KEY (wfdo_wflowid) REFERENCES wflows(wflw_wflowid)
);

ALTER TABLE wflows ADD COLUMN wflw_hasdocuments INT;

CREATE TABLE ordercomplainttypes(
	orct_ordercomplainttypeid INT NOT NULL AUTO_INCREMENT,
	orct_code VARCHAR(10) NOT NULL,
	orct_name VARCHAR(50),
	orct_description VARCHAR(255),
	orct_userid INT, 	
	orct_sflog TEXT,
	PRIMARY KEY (orct_ordercomplainttypeid),
	FOREIGN KEY (orct_userid) 
	REFERENCES users(user_userid)	
);

CREATE TABLE ordercomplaints(
	ordc_ordercomplaintid INT NOT NULL AUTO_INCREMENT,
	ordc_code VARCHAR(10),
	ordc_name VARCHAR(50),
	ordc_description VARCHAR(255),
	ordc_ordercomplainttypeid INT NOT NULL,
	ordc_orderid INT NOT NULL,
	ordc_solution VARCHAR(500),
	ordc_status CHAR(1),
	ordc_active INT(11),
	ordc_registrationdate DATE,
	ordc_committaldate DATE,
	ordc_solutiondate DATE,
	ordc_sflog TEXT,
	PRIMARY KEY (ordc_ordercomplaintid),
	FOREIGN KEY (ordc_orderid) REFERENCES orders(orde_orderid)
);

CREATE TABLE ordercomplaintfollowups(
	orcf_ordercomplaintfollowupid INT NOT NULL AUTO_INCREMENT,
	orcf_ordercomplaintid INT NOT NULL,
	orcf_description VARCHAR(255),
	orcf_userid INT, 	
	orcf_followupdate DATETIME, 	
	orcf_sflog TEXT,
	PRIMARY KEY (orcf_ordercomplaintfollowupid),
	FOREIGN KEY (orcf_ordercomplaintid) REFERENCES ordercomplaints(ordc_ordercomplaintid),
	FOREIGN KEY (orcf_userid) REFERENCES users(user_userid)
);

ALTER TABLE flexconfig ADD COLUMN flxc_enableordersale INT;
ALTER TABLE flexconfig ADD COLUMN flxc_enableorderrental INT;
ALTER TABLE flexconfig ADD COLUMN flxc_enableorderproperty INT;


-- el siguiente es para copiar los datos de la oportunidad al detalle
UPDATE opportunitydetails 
LEFT JOIN opportunities ON (opde_opportunityid = oppo_opportunityid)
SET opde_guests = oppo_guests, opde_venueid = oppo_venueid
WHERE opde_opportunityid = oppo_opportunityid;


UPDATE users 
SET user_code = left(user_email, locate('@', user_email)-1)
WHERE user_userid > 0;

-- IMPORTANTE: MIGRAR LOS DOCUMENTOS DE SFFILES A WFLOWDOCUMENTS, MEDIANTE JSP!!!!!

-- SOLO CUANDO ESTE BIEN REVISADO, BORRAR
ALTER TABLE opportunities DROP COLUMN oppo_guests;
ALTER TABLE opportunities DROP FOREIGN KEY opportunities_ibfk_6;
ALTER TABLE opportunities DROP COLUMN oppo_venueid;

ALTER TABLE quotes ADD COLUMN quot_showstaffquantity INT;
ALTER TABLE quotes ADD COLUMN quot_showstaffprice INT;

ALTER TABLE orders ADD COLUMN orde_showequipmentquantity INT;
ALTER TABLE orders ADD COLUMN orde_showequipmentprice INT;
ALTER TABLE orders ADD COLUMN orde_showstaffquantity INT;
ALTER TABLE orders ADD COLUMN orde_showstaffprice INT;

ALTER TABLE orders MODIFY COLUMN orde_description VARCHAR(500);

ALTER TABLE orders ADD COLUMN orde_wflowtypeid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);
ALTER TABLE orders ADD COLUMN orde_tags VARCHAR(255);

CREATE TABLE ordertypes (
	ortp_ordertypeid INT NOT NULL AUTO_INCREMENT, 
	ortp_code VARCHAR(10) NOT NULL, 
	ortp_name VARCHAR(30), 
	ortp_description VARCHAR(255), 
	ortp_type CHAR,
	ortp_sflog TEXT,
	PRIMARY KEY(ortp_ordertypeid) 
);

ALTER TABLE orders ADD COLUMN orde_ordertypeid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

ALTER TABLE flexconfig DROP COLUMN flxc_defaultordertype;
ALTER TABLE flexconfig DROP COLUMN flxc_enableordersale;
ALTER TABLE flexconfig DROP COLUMN flxc_enableorderrental;
ALTER TABLE flexconfig DROP COLUMN flxc_enableorderproperty;
ALTER TABLE flexconfig ADD COLUMN flxc_defaultordertypeid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

ALTER TABLE quotes ADD COLUMN quot_ordertypeid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

ALTER TABLE opportunities ADD COLUMN oppo_ordertypeid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

ALTER TABLE projects ADD COLUMN proj_ordertypeid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

UPDATE orders SET orde_ordertypeid = 1;
UPDATE opportunities SET oppo_ordertypeid = 1;
UPDATE quotes SET quot_ordertypeid = 1;
UPDATE projects SET proj_ordertypeid = 1;

CREATE TABLE wflowgroups (
	wfgp_wflowgroupid INT NOT NULL AUTO_INCREMENT, 
	wfgp_required INT NOT NULL,
	wfgp_profileid INT NOT NULL,
	wfgp_wflowcategoryid INT NOT NULL,
	wfgp_sflog TEXT,
	PRIMARY KEY(wfgp_wflowgroupid), 
	FOREIGN KEY (wfgp_profileid) REFERENCES groups(prof_profileid),
	FOREIGN KEY (wfgp_wflowcategoryid) REFERENCES wflowcategories(wfca_wflowcategoryid)
);

ALTER TABLE wflowgroups ADD COLUMN wfgp_autodate INT;
ALTER TABLE wflowusers ADD COLUMN wflu_required INT;
ALTER TABLE wflowusers ADD COLUMN wflu_autodate INT;
ALTER TABLE wflowusers ADD COLUMN wflu_profileid INT;
ALTER TABLE wflowusers ADD FOREIGN KEY (wflu_profileid) REFERENCES groups(prof_profileid);

CREATE TABLE  supplierusers (
	spus_supplieruserid INT NOT NULL AUTO_INCREMENT, 
	spus_userid INT NOT NULL,
	spus_supplierid INT NOT NULL,
	spus_sflog TEXT,
	PRIMARY KEY(spus_supplieruserid),
	FOREIGN KEY (spus_userid) REFERENCES users(user_userid),
	FOREIGN KEY (spus_supplierid) REFERENCES suppliers(supl_supplierid)
);


-- SE REQUIERE CREAR PROCESO DE ASIGNAR USUARIO CORRECTO wflu_wflowusers_update.jsp
-- update wflowusers set wflu_profileid = 27;


CREATE TABLE ordercommissions (
	orcm_ordercommissionid INT NOT NULL AUTO_INCREMENT, 
	orcm_code VARCHAR(10),
	orcm_name VARCHAR(100),
	orcm_description VARCHAR(255),
	orcm_ordertypeid INT,
	orcm_reqpaytypeid INT,
	orcm_sflog TEXT,
	PRIMARY KEY(orcm_ordercommissionid),
	FOREIGN KEY (orcm_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
FOREIGN KEY (orcm_reqpaytypeid) REFERENCES reqpaytypes(rqpt_reqpaytypeid)
);

CREATE TABLE ordercommissionamounts (
	orca_ordercommissionamountid INT NOT NULL AUTO_INCREMENT, 
	orca_name VARCHAR(30),
	orca_type CHAR,
	orca_value DOUBLE,
	orca_trigger CHAR,
	orca_userassignrequired INT,
	orca_ispartial INT,
	orca_profileid INT NOT NULL,
	orca_parentid INT,
	orca_ordercommissionid INT NOT NULL,
	orca_sflog TEXT,
	PRIMARY KEY(orca_ordercommissionamountid),
	FOREIGN KEY (orca_ordercommissionid) REFERENCES ordercommissions(orcm_ordercommissionid)
);


ALTER TABLE orders ADD COLUMN orde_ordercommissionid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_ordercommissionid) REFERENCES ordercommissions(orcm_ordercommissionid);

ALTER TABLE requisitions ADD COLUMN reqi_ordercommissionamountid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_ordercommissionamountid) REFERENCES ordercommissionamounts(orca_ordercommissionamountid);

ALTER TABLE ordercommissions ADD COLUMN orcm_reqpaytypeid INT;
ALTER TABLE ordercommissions ADD FOREIGN KEY (orcm_reqpaytypeid) REFERENCES reqpaytypes(rqpt_reqpaytypeid);

ALTER TABLE flexconfig ADD COLUMN flxc_defaultvirtualwhsectionid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultvirtualwhsectionid) REFERENCES whsections(whse_whsectionid);

ALTER TABLE flexconfig ADD COLUMN flxc_defaultvirtualwarehouseid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultvirtualwarehouseid) REFERENCES warehouses(ware_warehouseid);

ALTER TABLE whsections DROP COLUMN whse_type;

ALTER TABLE developmentphases ADD COLUMN dvph_ordercommissionid INT;
ALTER TABLE developmentphases ADD FOREIGN KEY (dvph_ordercommissionid) REFERENCES ordercommissions(orcm_ordercommissionid);

ALTER TABLE orders ADD COLUMN orde_companyid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE requisitions ADD COLUMN reqi_companyid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE developmentphases ADD COLUMN dvph_companyid INT;
ALTER TABLE developmentphases ADD FOREIGN KEY (dvph_companyid) REFERENCES companies(comp_companyid);


-- Asignaciones en CXP
CREATE TABLE paccountassignments (
	pass_paccountassignmentid INT NOT NULL AUTO_INCREMENT, 
	pass_code VARCHAR(10),
	pass_invoiceno VARCHAR(50),
	pass_paccountid INT NOT NULL,
	pass_foreignpaccountid INT,
	pass_amount FLOAT NOT NULL,
	pass_sflog TEXT,
	PRIMARY KEY(pass_paccountassignmentid),
	FOREIGN KEY (pass_paccountid) REFERENCES paccounts(pacc_paccountid)
);

CREATE TABLE raccountassignments (
	rass_raccountassignmentid INT NOT NULL AUTO_INCREMENT, 
	rass_code VARCHAR(10),
	rass_invoiceno VARCHAR(50),
	rass_raccountid INT NOT NULL,
	rass_foreignraccountid INT,
	rass_amount FLOAT NOT NULL,
	rass_sflog TEXT,
	PRIMARY KEY(rass_raccountassignmentid),
	FOREIGN KEY (rass_raccountid) REFERENCES raccounts(racc_raccountid)
);

ALTER TABLE whstocks ADD COLUMN whst_whtrackid INT;
ALTER TABLE whstocks ADD FOREIGN KEY (whst_whtrackid) REFERENCES whtracks(whtr_whtrackid);

ALTER TABLE customers ADD COLUMN cust_curp VARCHAR(25);
ALTER TABLE customers ADD COLUMN cust_nss VARCHAR(25);
ALTER TABLE customers ADD COLUMN cust_maritalstatus CHAR;
ALTER TABLE customers ADD COLUMN cust_income DOUBLE;

ALTER TABLE customers ADD COLUMN cust_parentid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_parentid) REFERENCES customers(cust_customerid);


CREATE TABLE `customerrelatives` (
  `curl_customerrelativeid` int(11) NOT NULL AUTO_INCREMENT,
  `curl_type` char(1) DEFAULT NULL,
  `curl_fullname` varchar(50) DEFAULT NULL,
  `curl_email` varchar(50) DEFAULT NULL,
  `curl_number` varchar(15) DEFAULT NULL,
  `curl_extension` varchar(5) DEFAULT NULL,
  `curl_customerid` int(11) NOT NULL,
  `curl_sflog` text,
  PRIMARY KEY (`curl_customerrelativeid`),
  CONSTRAINT `curlrelatives_ibfk_1` FOREIGN KEY (`curl_customerid`) REFERENCES `customers` (`cust_customerid`)
);

ALTER TABLE propertysales ADD COLUMN prsa_ordertypeid INT; 
ALTER TABLE propertysales ADD FOREIGN KEY (prsa_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

ALTER TABLE flexconfig DROP FOREIGN KEY flexconfig_ibfk_10;
ALTER TABLE flexconfig DROP FOREIGN KEY flexconfig_ibfk_11;
ALTER TABLE flexconfig DROP COLUMN flxc_defaultvirtualwhsectionid;
ALTER TABLE flexconfig DROP COLUMN flxc_defaultvirtualwarehouseid;

ALTER TABLE wflowusers CHANGE COLUMN wflu_geventid wflu_usergeventid VARCHAR(100);
ALTER TABLE wflowusers CHANGE COLUMN wflu_userpublicgeventid wflu_areageventid VARCHAR(100);

ALTER TABLE requisitions MODIFY COLUMN reqi_code VARCHAR(10);

ALTER TABLE developmentphases ADD COLUMN dvph_venturecostcenterid INT; 
ALTER TABLE developmentphases ADD FOREIGN KEY (dvph_venturecostcenterid) REFERENCES venturecostcenters(vecc_venturecostcenterid);

ALTER TABLE paccountassignments ADD COLUMN pass_bankmovconceptid INT;
ALTER TABLE paccountassignments ADD FOREIGN KEY (pass_bankmovconceptid) REFERENCES bankmovconcepts(bkmc_bankmovconceptid);

ALTER TABLE raccountassignments ADD COLUMN rass_bankmovconceptid INT;
ALTER TABLE raccountassignments ADD FOREIGN KEY (rass_bankmovconceptid) REFERENCES bankmovconcepts(bkmc_bankmovconceptid);


ALTER TABLE bankmovconcepts ADD COLUMN bkmc_depositpaccountitemid INT;
ALTER TABLE bankmovconcepts ADD FOREIGN KEY (bkmc_depositpaccountitemid) REFERENCES paccountitems(pait_paccountitemid);

ALTER TABLE bankmovconcepts ADD COLUMN bkmc_depositraccountitemid INT;
ALTER TABLE bankmovconcepts ADD FOREIGN KEY (bkmc_depositraccountitemid) REFERENCES raccountitems(rait_raccountitemid);


ALTER TABLE paccounts ADD COLUMN pacc_code VARCHAR(10);

CREATE TABLE loans(
	 loan_loanid INT NOT NULL AUTO_INCREMENT,
	 loan_code VARCHAR(10),
	 loan_name VARCHAR(50),
	 loan_description VARCHAR(255),
	 loan_amount DOUBLE,
	 loan_rate DOUBLE,
	 loan_downpayment DOUBLE,
	 loan_commission DOUBLE,
	 loan_startdate DATE,
	 loan_enddate DATE,
	 loan_disbursed DOUBLE,
	 loan_progress INT,
	 loan_supplierid INT,
	 loan_developmentphaseid INT,
	 loan_sflog TEXT,
	 PRIMARY KEY (loan_loanid),
	 FOREIGN KEY (loan_supplierid) REFERENCES suppliers (supl_supplierid), 
	 FOREIGN KEY (loan_developmentphaseid) REFERENCES developmentphases (dvph_developmentphaseid)
);

CREATE TABLE loandisbursements(
	lodi_loandisbursementid INT NOT NULL AUTO_INCREMENT,
	lodi_date DATE,
	lodi_amount DOUBLE,
	lodi_progress INT,
	lodi_loanid INT,
	lodi_sflog TEXT,
	PRIMARY KEY (lodi_loandisbursementid),
	FOREIGN KEY (lodi_loanid) REFERENCES loans (loan_loanid)
);

CREATE TABLE loanpayments(
	lopa_loanpaymentid INT NOT NULL AUTO_INCREMENT,
	lopa_date DATE,
	lopa_amount DOUBLE,
	lopa_propertyid INT, 
	lopa_loanid INT,
	lopa_sflog TEXT,
	PRIMARY KEY (lopa_loanpaymentid),
	FOREIGN KEY (lopa_propertyid) REFERENCES properties (prty_propertyid),
	FOREIGN KEY (lopa_loanid) REFERENCES loans (loan_loanid)
);

ALTER TABLE loandisbursements ADD COLUMN lodi_balance DOUBLE;

ALTER TABLE paccountassignments CHANGE COLUMN pass_amount pass_amount DOUBLE;
ALTER TABLE raccountassignments CHANGE COLUMN rass_amount rass_amount DOUBLE;


ALTER TABLE requisitions ADD COLUMN reqi_venturecostcenterid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_venturecostcenterid) REFERENCES venturecostcenters(vecc_venturecostcenterid);

-- REGRESAR ESQUEMA A LO ANTERIOR
-- ALTER TABLE projects ADD COLUMN proj_venturecostcenterid INT;
-- ALTER TABLE projects ADD FOREIGN KEY (proj_venturecostcenterid) REFERENCES venturecostcenters(vecc_venturecostcenterid);

ALTER TABLE bankmovconcepts CHANGE COLUMN bkmc_code bkmc_code VARCHAR(40);

ALTER TABLE equipments ADD FOREIGN KEY (equi_userid) REFERENCES users(user_userid);

ALTER TABLE wflowsteps ADD COLUMN wfsp_emailreminders INT;
ALTER TABLE wflowsteps ADD COLUMN wfsp_file VARCHAR(255);
ALTER TABLE wflowsteptypes ADD COLUMN wfst_emailreminders INT;

ALTER TABLE wflowdocumenttypes ADD COLUMN wfdt_code VARCHAR(10);
ALTER TABLE wflowdocuments ADD COLUMN wfdo_code VARCHAR(10);

ALTER TABLE companies ADD COLUMN comp_deeddate DATE;
ALTER TABLE companies ADD COLUMN comp_deednumber VARCHAR(20);
ALTER TABLE companies ADD COLUMN comp_deednotarynumber VARCHAR(10);
ALTER TABLE companies MODIFY COLUMN comp_code VARCHAR(10);

ALTER TABLE opportunities ADD COLUMN oppo_foreignwflowtypeid INT;

ALTER TABLE propertysales ADD COLUMN prsa_type CHAR;

ALTER TABLE developmentphases ADD COLUMN dvph_electronicfolio VARCHAR(40); -- Número Folio Mercantil Eletrónico	
ALTER TABLE developmentphases ADD COLUMN dvph_fideicomisonumber VARCHAR(40); -- Número de Fideicomiso

-- DROP TABLE modulespecialaccess;
-- DROP TABLE modulespecial;
-- DROP TABLE moduleaccess;

ALTER TABLE flexconfig ADD COLUMN flxc_discountlimit double;

ALTER TABLE bankmovtypes ADD COLUMN bkmt_visible CHAR(1);

CREATE TABLE requisitiontypes (
	rqtp_requisitiontypeid INT NOT NULL AUTO_INCREMENT, 
	rqtp_code VARCHAR(10) NOT NULL, 
	rqtp_name VARCHAR(30), 
	rqtp_description VARCHAR(255), 
	rqtp_type CHAR,
	rqtp_sflog TEXT,
	PRIMARY KEY(rqtp_requisitiontypeid) 
);

ALTER TABLE requisitions ADD COLUMN reqi_requisitiontypeid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_requisitiontypeid) REFERENCES requisitiontypes(rqtp_requisitiontypeid);

ALTER TABLE requisitions ADD COLUMN reqi_contractestimationid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_contractestimationid) REFERENCES contractestimations(coes_contractestimationid);

CREATE TABLE works (
	work_workid INT NOT NULL AUTO_INCREMENT,
	work_code VARCHAR(10),
	work_name VARCHAR(200),
	work_description VARCHAR(255),
	work_developmentphaseid INT,
	work_type CHAR(1) NOT NULL,
	work_ismaster INT,
	work_userid INT NOT NULL,
	work_status CHAR(1),
	work_indirects DOUBLE NOT NULL,
	work_companyid INT,
	work_sflog TEXT,
	work_total DOUBLE, 
	PRIMARY KEY (work_workid),
	FOREIGN KEY (work_companyid) REFERENCES companies (comp_companyid)
);

CREATE TABLE unitprices (
unpr_unitpriceid INT NOT NULL AUTO_INCREMENT,
unpr_code VARCHAR(30) NOT NULL,
unpr_name VARCHAR(768) NOT NULL,
unpr_description VARCHAR(768),
unpr_date DATE,
unpr_type CHAR(1) NOT NULL,
unpr_category CHAR(1) NOT NULL,
unpr_price DOUBLE,
unpr_basefsr DOUBLE,
unpr_supplierid INT,
unpr_materialtypeid INT,
unpr_genrequisition INT,
unpr_deletehistory INT,
unpr_origin CHAR(1),
unpr_subcontract INT,
unpr_quantitybyhouse DOUBLE,
unpr_status CHAR(1),
unpr_workid INT NOT NULL,
unpr_unitid INT NOT NULL,
unpr_currencyid INT,
unpr_sflog TEXT,
PRIMARY KEY (unpr_unitpriceid),
FOREIGN KEY (unpr_workid) REFERENCES works (work_workid),
FOREIGN KEY (unpr_unitid) REFERENCES units (unit_unitid),
FOREIGN KEY (unpr_currencyid) REFERENCES currencies (cure_currencyid),
FOREIGN KEY (unpr_supplierid) REFERENCES suppliers (supl_supplierid) 
);

CREATE TABLE unitpriceitems (
unpi_unitpriceitemid INT NOT NULL AUTO_INCREMENT,
unpi_code VARCHAR(30) NOT NULL,
unpi_quantity DOUBLE NOT NULL,
unpi_price FLOAT NOT NULL,
unpi_amount FLOAT NOT NULL,
unpi_unitpriceid INT NOT NULL,
unpi_unitpriceParentid INT NOT NULL,
unpi_sflog TEXT,
PRIMARY KEY(unpi_unitpriceitemid),
FOREIGN KEY (unpi_unitpriceparentid) REFERENCES unitprices(unpr_unitpriceid)
);

CREATE TABLE conceptgroups (
cpgp_conceptgroupid INT NOT NULL AUTO_INCREMENT,
cpgp_code VARCHAR(10),
cpgp_name VARCHAR(30),
cpgp_description VARCHAR(255),
cpgp_sflog TEXT,
PRIMARY KEY (cpgp_conceptgroupid)
);

CREATE TABLE conceptheadings (
cphd_conceptheadingid INT NOT NULL AUTO_INCREMENT,
cphd_code VARCHAR(20),
cphd_name VARCHAR(30),
cphd_description VARCHAR(255),
cphd_conceptgroupid INT,
cphd_sflog TEXT,
PRIMARY KEY (cphd_conceptheadingid)
);

CREATE TABLE concepts(
cncp_conceptid INT NOT NULL AUTO_INCREMENT,
cncp_code VARCHAR(10) NOT NULL,
cncp_name VARCHAR(30) NOT NULL,
cncp_total DOUBLE,
cncp_workid INT NOT NULL,
cncp_description VARCHAR(200),
cncp_sflog TEXT,
PRIMARY KEY (cncp_conceptid),
FOREIGN KEY (cncp_workid) REFERENCES works (work_workid)
);

CREATE TABLE conceptitems (
cnci_conceptitemid INT NOT NULL AUTO_INCREMENT, 
cnci_code VARCHAR(10) NOT NULL,
cnci_type VARCHAR(30),
cnci_unitpriceid INT NOT NULL,
cnci_amount DOUBLE,
cnci_price DOUBLE,
cnci_conceptheadingid INT NOT NULL,
cnci_quantity DOUBLE NOT NULL,
cnci_conceptid INT NOT NULL,
cnci_sflog TEXT,
PRIMARY KEY(cnci_conceptitemid),
FOREIGN KEY (cnci_conceptid) REFERENCES concepts(cncp_conceptid),
FOREIGN KEY (cnci_unitpriceid) REFERENCES unitprices(unpr_unitpriceid),
FOREIGN KEY (cnci_conceptheadingid) REFERENCES conceptheadings(cphd_conceptheadingid)
);

CREATE TABLE workcontracts(
woco_workcontractid INT NOT NULL AUTO_INCREMENT,
woco_workid INT NOT NULL,
woco_supplierid INT NOT NULL,
woco_code VARCHAR(12),
woco_name VARCHAR(30) NOT NULL,
woco_description VARCHAR(255),
woco_startdate DATE,
woco_enddate DATE,
woco_downpayment DOUBLE,
woco_percentdownpayment DOUBLE,
woco_hastax INT,
woco_tax DOUBLE,
woco_dailysanction DOUBLE,
woco_observations VARCHAR(500),
woco_quantity DOUBLE,
woco_subtotal DOUBLE,
woco_totalconcepts DOUBLE,
woco_total DOUBLE,
woco_companyid INT NOT NULL,
woco_paymentdate DATE,
woco_status CHAR(1),
woco_paymentstatus CHAR(1),
woco_estimationtype CHAR(1),
woco_guaranteefund DOUBLE,
woco_percentguaranteefund DOUBLE,
woco_downpaymentstatus CHAR(1),
woco_comments VARCHAR(500),
woco_datecontract DATE,
woco_auxstatus CHAR(1),
woco_isclosed BOOLEAN DEFAULT false,
woco_history VARCHAR(1000),
woco_descriptionisclosed VARCHAR(250),
woco_worktype INT,
woco_lotdescription VARCHAR(50),
woco_finished INT DEFAULT false,
woco_totalwarranty DOUBLE,
woco_sflog TEXT,
PRIMARY KEY (woco_workcontractid),
FOREIGN KEY (woco_workid)
REFERENCES works (work_workid),
FOREIGN KEY (woco_supplierid) REFERENCES suppliers (supl_supplierid),
FOREIGN KEY (woco_worktype) REFERENCES conceptgroups (cpgp_conceptgroupid) 
);

CREATE TABLE contractconcepts(
coco_contractconceptid INT NOT NULL AUTO_INCREMENT,
coco_workcontractid INT NOT NULL,
coco_conceptid INT NOT NULL,
coco_sflog TEXT,
PRIMARY KEY (coco_contractconceptid),
FOREIGN KEY (coco_workcontractid) REFERENCES workcontracts (woco_workcontractid),
FOREIGN KEY (coco_conceptid) REFERENCES concepts (cncp_conceptid) 
);

CREATE TABLE contractestimations(
coes_contractestimationid INT NOT NULL AUTO_INCREMENT,
coes_workcontractid INT NOT NULL,
coes_consecutive INT NOT NULL,
coes_code VARCHAR(10),
coes_startdate DATE NOT NULL,
coes_enddate DATE NOT NULL,
coes_amount DOUBLE,
coes_tax DOUBLE,
coes_downpayment DOUBLE,
coes_warrantyfund DOUBLE,
coes_total DOUBLE,
coes_status CHAR(1) NOT NULL,
coes_othersexpenses DOUBLE,
coes_paymentstatus CHAR(1),
coes_descriptionotherexpenses VARCHAR(250),
coes_sflog TEXT,
PRIMARY KEY (coes_contractestimationid),
FOREIGN KEY (coes_workcontractid) REFERENCES workcontracts (woco_workcontractid) 
);

CREATE TABLE contractconceptitems (
ccit_contractconceptitemid INT NOT NULL AUTO_INCREMENT, 
ccit_code VARCHAR(10) NOT NULL,
ccit_name VARCHAR(30) NOT NULL,
ccit_conceptid INT NOT NULL,
ccit_amount DOUBLE,
ccit_price DOUBLE,
ccit_quantity DOUBLE NOT NULL,
ccit_workcontractid INT NOT NULL,
ccit_sflog TEXT,
PRIMARY KEY(ccit_contractconceptitemid),
FOREIGN KEY (ccit_workcontractid) REFERENCES workcontracts(woco_workcontractid),
FOREIGN KEY (ccit_conceptid) REFERENCES concepts(cncp_conceptid)	
);

CREATE TABLE materialtype (
mtty_materialtypeid INT NOT NULL AUTO_INCREMENT, 
mtty_code VARCHAR(10) NOT NULL,
mtty_name VARCHAR(30) NOT NULL,
mtty_description VARCHAR(60),
mtty_sflog TEXT,
PRIMARY KEY(mtty_materialtypeid)
);

CREATE TABLE estimationitems(
esti_estimationitemid INT NOT NULL AUTO_INCREMENT,
esti_contractestimationid INT NOT NULL,
esti_contractconceptitemid INT NOT NULL,
esti_quantityreceipt DOUBLE,
esti_quantitytotal DOUBLE,
esti_quantity DOUBLE,
esti_price DOUBLE,
esti_subtotal DOUBLE,
esti_consecutive INT,
esti_sflog TEXT,
PRIMARY KEY (esti_estimationitemid)
);

ALTER TABLE estimationitems ADD FOREIGN KEY (esti_contractestimationid) REFERENCES contractestimations (coes_contractestimationid);
ALTER TABLE estimationitems ADD	FOREIGN KEY (esti_contractconceptitemid) REFERENCES contractconceptitems (ccit_contractconceptitemid);

ALTER TABLE requisitionitems ADD COLUMN rqit_estimationitemid INT;
ALTER TABLE requisitionitems ADD FOREIGN KEY (rqit_estimationitemid) REFERENCES estimationitems(esti_estimationitemid);

CREATE TABLE orderblockdates (
	orbl_orderblockdateid INT NOT NULL AUTO_INCREMENT, 
	orbl_startdate DATETIME, 
	orbl_enddate DATETIME, 
	orbl_comments VARCHAR(255), 
	orbl_sflog TEXT,
	PRIMARY KEY(orbl_orderblockdateid) 
);

-- 20150715

ALTER TABLE products CHANGE COLUMN prod_price prod_rentalprice DOUBLE;
ALTER TABLE products ADD COLUMN prod_saleprice DOUBLE;
ALTER TABLE products ADD COLUMN prod_enabled INT;

ALTER TABLE quoteitems ADD COLUMN qoit_baseprice DOUBLE;
UPDATE quoteitems SET qoit_baseprice = qoit_price;

ALTER TABLE quoteequipments ADD COLUMN qoeq_baseprice DOUBLE;
UPDATE quoteequipments SET qoeq_baseprice = qoeq_price;

ALTER TABLE quotestaff ADD COLUMN qost_baseprice DOUBLE;
UPDATE quotestaff SET qost_baseprice = qost_price;

ALTER TABLE orderitems ADD COLUMN ordi_baseprice DOUBLE;
UPDATE orderitems SET ordi_baseprice = ordi_price;
ALTER TABLE orderitems ADD COLUMN ordi_basecost DOUBLE;

ALTER TABLE orderequipments ADD COLUMN ordq_baseprice DOUBLE;
UPDATE orderequipments SET ordq_baseprice = ordq_price;

ALTER TABLE orderstaff ADD COLUMN ords_baseprice DOUBLE;
UPDATE orderstaff SET ords_baseprice = ords_price;

ALTER TABLE orderstaff ADD COLUMN ords_basecost DOUBLE;

UPDATE products SET prod_enabled = 1;


-- CAMBIOS ALMACENES 20150824

ALTER TABLE whmovitems ADD COLUMN whmi_fromwhsectionid INT;
ALTER TABLE whmovitems ADD FOREIGN KEY (whmi_fromwhsectionid) REFERENCES whsections(whse_whsectionid);

ALTER TABLE orderdeliveryitems ADD COLUMN odyi_fromwhsectionid INT;
ALTER TABLE orderdeliveryitems ADD FOREIGN KEY (odyi_fromwhsectionid) REFERENCES whsections(whse_whsectionid);

ALTER TABLE orderdeliveryitems ADD COLUMN odyi_towhsectionid INT;
ALTER TABLE orderdeliveryitems ADD FOREIGN KEY (odyi_towhsectionid) REFERENCES whsections(whse_whsectionid);

UPDATE whmovements SET whmv_towhsectionid = whmv_basewhsectionid;

ALTER TABLE whmovitems ADD COLUMN whmi_towhsectionid INT;
ALTER TABLE whmovitems ADD FOREIGN KEY (whmi_towhsectionid) REFERENCES whsections(whse_whsectionid);


-- OBRAS

-- Fecha Creación obra
ALTER TABLE works ADD COLUMN work_datecreate DATE;

-- Tipo Obra
CREATE TABLE worktypes (
	wkty_worktypeid INT NOT NULL AUTO_INCREMENT, 
	wkty_code VARCHAR(10) NOT NULL,
	wkty_name VARCHAR(30) NOT NULL,
	wkty_description VARCHAR(60),
	wkty_sflog TEXT,
	PRIMARY KEY(wkty_worktypeid)
);



ALTER TABLE works ADD COLUMN work_worktypeid INT;
ALTER TABLE works ADD FOREIGN KEY (work_worktypeid) REFERENCES worktypes(wkty_worktypeid);


ALTER TABLE unitprices
CHANGE COLUMN unpr_price unpr_subtotal DOUBLE NULL DEFAULT NULL ,
ADD COLUMN unpr_indirects DOUBLE NULL AFTER unpr_sflog,
ADD COLUMN unpr_total DOUBLE NULL AFTER unpr_indirects;

ALTER TABLE unitprices ADD COLUMN unpr_totalindirects DOUBLE;

update unitprices set unpr_total = unpr_subtotal;

ALTER TABLE concepts ADD COLUMN cncp_conceptheadingid INT;
ALTER TABLE concepts ADD FOREIGN KEY (cncp_conceptheadingid) REFERENCES conceptheadings(cphd_conceptheadingid);
-- Eliminar la partida en items de conceptos
ALTER TABLE conceptitems DROP FOREIGN KEY conceptitems_ibfk_3;


ALTER TABLE works ADD COLUMN work_indirectsamount DOUBLE;
ALTER TABLE works ADD COLUMN work_subtotal DOUBLE;


ALTER TABLE worktypes ADD COLUMN wkty_type CHAR;

ALTER TABLE concepts ADD COLUMN cncp_quantity DOUBLE;
ALTER TABLE concepts ADD COLUMN cncp_subtotal DOUBLE;

ALTER TABLE contractconceptitems ADD COLUMN ccit_conceptitemid INT;
ALTER TABLE contractconceptitems ADD FOREIGN KEY (ccit_conceptitemid) REFERENCES conceptitems(cnci_conceptitemid);

ALTER TABLE contractconceptitems
DROP FOREIGN KEY contractconceptitems_ibfk_2;
ALTER TABLE contractconceptitems 
DROP COLUMN ccit_conceptid,
DROP INDEX ccit_conceptid ;

ALTER TABLE unitprices CHANGE COLUMN unpr_description unpr_description VARCHAR(1500) NULL DEFAULT NULL ;

-- Estos los alters de Obra

ALTER TABLE flexconfig ADD COLUMN flxc_enableworkbudgetitem INT;


ALTER TABLE unitprices DROP FOREIGN KEY unitprices_ibfk_1;
ALTER TABLE unitprices CHANGE COLUMN unpr_workid unpr_workid INT(11) NULL ;
ALTER TABLE unitprices ADD CONSTRAINT unitprices_ibfk_1 FOREIGN KEY (unpr_workid) REFERENCES works (work_workid);


-- ALTER TABLE workbudgetitems ADD COLUMN wkbi_amount DOUBLE;

-- update workbudgetitems set wkbi_amount = wkbi_total;


CREATE TABLE budgets (
	budg_budgetid INT NOT NULL AUTO_INCREMENT, 
	budg_code VARCHAR(10) NOT NULL,
	budg_name VARCHAR(60) NOT NULL,
	budg_description VARCHAR(512),
	budg_status CHAR,
	budg_total DOUBLE,
	budg_sflog TEXT,
	PRIMARY KEY(budg_budgetid)
);

CREATE TABLE budgetitems (
	bgit_budgetitemid INT NOT NULL AUTO_INCREMENT, 
	bgit_code VARCHAR(10) NOT NULL,
	bgit_name VARCHAR(30) NOT NULL,
	bgit_description VARCHAR(512),
	bgit_amount DOUBLE,
	bgit_budgetid INT,
	bgit_sflog TEXT,
	PRIMARY KEY(bgit_budgetitemid),
	FOREIGN KEY (bgit_budgetid) REFERENCES budgets(budg_budgetid)
);

ALTER TABLE developmentphases ADD COLUMN dvph_budgetid INT;
ALTER TABLE developmentphases ADD FOREIGN KEY (dvph_budgetid) REFERENCES budgets(budg_budgetid);

-- workBudgetItem en Ordenes de Compra
ALTER TABLE requisitions ADD COLUMN reqi_budgetitemid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

-- workBudgetItem en Obra
ALTER TABLE works ADD COLUMN work_budgetitemid INT;
ALTER TABLE works ADD FOREIGN KEY (work_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

CREATE TABLE budgetitemtypes (
	bdty_budgetitemtypeid INT NOT NULL AUTO_INCREMENT, 
	bdty_code VARCHAR(10) NOT NULL,
	bdty_name VARCHAR(30) NOT NULL,
	bdty_description VARCHAR(512),	
	bdty_sflog TEXT,
	PRIMARY KEY(bdty_budgetitemtypeid)
);

ALTER TABLE budgetitems ADD COLUMN bgit_budgetitemtypeid INT;
ALTER TABLE budgetitems ADD FOREIGN KEY (bgit_budgetitemtypeid) REFERENCES budgetitemtypes(bdty_budgetitemtypeid);

ALTER TABLE flexconfig ADD COLUMN flxc_enableworkbudgetitem INT;

-- CAMBIOS A IMAGENES DE PRODUCTOS

ALTER TABLE products ADD COLUMN prod_image VARCHAR(255);
ALTER TABLE products ADD COLUMN prod_displayname VARCHAR(255);

ALTER TABLE productkits ADD COLUMN prkt_image VARCHAR(255);
ALTER TABLE quotegroups ADD COLUMN qogr_image VARCHAR(255);

ALTER TABLE quotegroups ADD COLUMN qogr_showgroupimage INT;
ALTER TABLE quotegroups ADD COLUMN qogr_showproductimage INT;

UPDATE products SET prod_displayname = concat(prod_name, ' ', prod_brand);

ALTER TABLE products ADD COLUMN prod_typecostcenterid INT;
ALTER TABLE products ADD FOREIGN KEY (prod_typecostcenterid) REFERENCES typecostcenters (tycc_typecostcenterid);


-- BORRADO DE CENTROS DE COSTOS DE PROYECTO
ALTER TABLE orders DROP FOREIGN KEY orders_ibfk_7;
ALTER TABLE orders DROP COLUMN orde_venturecostcenterid;
ALTER TABLE requisitions DROP FOREIGN KEY requisitions_ibfk_9;
ALTER TABLE requisitions DROP COLUMN reqi_venturecostcenterid;
-- ALTER TABLE developmentphases DROP FOREIGN KEY 

-- DROP TABLE venturecostcenters;

ALTER TABLE properties ADD COLUMN prty_habitability INT;

-- ARREGLOS CENTROS DE COSTOS

ALTER TABLE budgetitems ADD COLUMN bgit_typecostcenterid INT;
ALTER TABLE budgetitems ADD FOREIGN KEY (bgit_typecostcenterid) REFERENCES typecostcenters (typecostcenterid);
ALTER TABLE budgetitems DROP COLUMN bgit_budgetitemtypeid;
-- DROP TABLE budgetitemtypes;

ALTER TABLE budgets ADD COLUMN budg_startdate DATE;
ALTER TABLE budgets ADD COLUMN budg_enddate DATE;


ALTER TABLE customers ADD COLUMN cust_recommendedby INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_recommendedby) REFERENCES customers(cust_customerid);


-- Imagenes en hoja de pedido
ALTER TABLE ordergroups ADD COLUMN ordg_image VARCHAR(255);
ALTER TABLE ordergroups ADD COLUMN ordg_showgroupimage INT;
ALTER TABLE ordergroups ADD COLUMN ordg_showproductimage INT;


-- Cambios obra 20151013

CREATE TABLE workitems (
	wkit_workitemid INT NOT NULL AUTO_INCREMENT, 
	wkit_code VARCHAR(10) NOT NULL,
	wkit_type VARCHAR(30),
	wkit_unitpriceid INT NOT NULL,
	wkit_amount DOUBLE,
	wkit_price DOUBLE,
	wkit_quantity DOUBLE NOT NULL,
	wkit_workid INT NOT NULL,
	wkit_sflog TEXT,
	PRIMARY KEY(wkit_workitemid),
	FOREIGN KEY (wkit_workid) REFERENCES works(work_workid),
	FOREIGN KEY (wkit_unitpriceid) REFERENCES unitprices(unpr_unitpriceid)
);

ALTER TABLE contractconceptitems ADD COLUMN ccit_workitemid INT;
ALTER TABLE contractconceptitems ADD FOREIGN KEY (ccit_workitemid) REFERENCES workitems(wkit_workitemid);

ALTER TABLE works ADD COLUMN work_typecostcenterid INT;
ALTER TABLE works ADD FOREIGN KEY (work_typecostcenterid) REFERENCES typecostcenters(tycc_typecostcenterid);

ALTER TABLE opportunities ADD COLUMN oppo_propertymodelid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_propertymodelid) REFERENCES propertymodels(ptym_propertymodelid);

ALTER TABLE propertymodels ADD COLUMN ptym_image VARCHAR(255);

ALTER TABLE wflowsteps MODIFY COLUMN wfsp_comments VARCHAR(1048);

-- Reloj Checador
-- DROP TABLE usertimeclock;

CREATE TABLE usertimeclock (
	ustc_usertimeclockid INT NOT NULL AUTO_INCREMENT, 
	ustc_type CHAR,
	ustc_comments VARCHAR(255),
	ustc_remoteip VARCHAR(50),
	ustc_hostname VARCHAR(50),
	ustc_gpslocation INT,
	ustc_gpslongitude VARCHAR(50),
	ustc_gpslatitude VARCHAR(50),
	ustc_gpsaccuracy DOUBLE,
	ustc_datetime TIMESTAMP,
	ustc_userid INT,
	ustc_sflog TEXT,
	PRIMARY KEY(ustc_usertimeclockid),
	FOREIGN KEY (ustc_userid) REFERENCES users(user_userid)
);

-- DROP TABLE usertimeclock;

ALTER TABLE sfconfig ADD COLUMN sfcf_enableusertimeclock INT;

ALTER TABLE customers MODIFY COLUMN cust_displayname VARCHAR(200);

-- Colindancias en Inmueble
ALTER TABLE properties ADD COLUMN prty_adjoins VARCHAR(1500);

-- Numero de propiedades en fase desarrollo
ALTER TABLE developmentphases ADD COLUMN dvph_numberproperties INT;

-- Cantidad Recibida
ALTER TABLE estimationitems ADD COLUMN esti_quantitylast DOUBLE;

ALTER TABLE contractestimations ADD COLUMN coes_subtotal DOUBLE;
ALTER TABLE contractestimations ADD COLUMN coes_subtotal DOUBLE;

-- Cambio a tareas
ALTER TABLE wflowsteps MODIFY COLUMN wfsp_comments VARCHAR(1024);
ALTER TABLE wflowsteps ADD COLUMN wfsp_commentlog TEXT;

CREATE TABLE meetings (
	meet_meetingid INT NOT NULL AUTO_INCREMENT, 
	meet_code VARCHAR(10),
	meet_name VARCHAR(50),
	meet_description VARCHAR(255),
	meet_startdate DATETIME,
	meet_enddate DATETIME,
	meet_wflowtypeid INT,
	meet_wflowid INT,
	meet_userid INT,
	meet_sflog TEXT,
	PRIMARY KEY(meet_meetingid),
	FOREIGN KEY (meet_userid) REFERENCES users(user_userid),
	FOREIGN KEY (meet_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid),
	FOREIGN KEY (meet_wflowid) REFERENCES wflows(wflw_wflowid)
);


ALTER TABLE wflowlogs MODIFY COLUMN wflg_comments VARCHAR(2048);


ALTER TABLE requisitionreceipts ADD COLUMN rerc_service INT;
ALTER TABLE requisitionreceipts ADD COLUMN rerc_quality INT;
ALTER TABLE requisitionreceipts ADD COLUMN rerc_punctuality INT;


-- CAMBIO DE PRECIOS UNITARIOS A AMOUNT TOTAL EN LUGAR DE PRICE AMOUNT
ALTER TABLE unitpriceitems CHANGE COLUMN unpi_amount unpi_total DOUBLE;
ALTER TABLE unitpriceitems CHANGE COLUMN unpi_price unpi_amount DOUBLE;

ALTER TABLE flexconfig ADD COLUMN flxc_negativebankbalance INT;

ALTER TABLE orders ADD COLUMN orde_opportunityid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_opportunityid) REFERENCES opportunities(oppo_opportunityid);

ALTER TABLE socials MODIFY COLUMN soci_icon VARCHAR(500);

-- static desarrollos
ALTER TABLE developments ADD COLUMN deve_satic VARCHAR(40);

-- DAtos del notario en el proveedor
ALTER TABLE suppliers ADD COLUMN supl_lawyername VARCHAR(40);
ALTER TABLE suppliers ADD COLUMN supl_lawyerdeed VARCHAR(40);
ALTER TABLE suppliers ADD COLUMN supl_lawyerdeeddate VARCHAR(40);
ALTER TABLE suppliers ADD COLUMN supl_citydeedid INT;

-- Manejo de max y min en productos
ALTER TABLE products ADD COLUMN prod_stockmax INT;
ALTER TABLE products ADD COLUMN prod_stockmin INT;

ALTER TABLE equipments ADD COLUMN equi_enabled CHAR;

-- SE AGREGA EMPRESA A OPORTUNIDADES Y A COTIZACIONES
ALTER TABLE opportunities ADD COLUMN oppo_companyid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE quotes ADD COLUMN quot_companyid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE developments ADD COLUMN deve_companyid INT;
ALTER TABLE developments ADD FOREIGN KEY (deve_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE companies ADD COLUMN comp_deednotaryname VARCHAR(100);

ALTER TABLE projects ADD COLUMN proj_companyid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_companyid) REFERENCES companies (comp_companyid);

-- 27 NOV 2015
ALTER TABLE locations ADD COLUMN loct_gpslongitude VARCHAR(100);
ALTER TABLE locations ADD COLUMN loct_gpslatitude VARCHAR(100);

ALTER TABLE requisitiontypes ADD COLUMN rqtp_createreceipt INT;

-- ajustes de prespuestos
ALTER TABLE budgets ADD COLUMN budg_payments DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_balance DOUBLE;

ALTER TABLE budgetitems ADD COLUMN bgit_payments DOUBLE;
ALTER TABLE budgetitems ADD COLUMN bgit_balance DOUBLE;

ALTER TABLE flexconfig ADD COLUMN flxc_ensureprocesscxc INT;

ALTER TABLE raccounts ADD COLUMN racc_autocreate INT;

ALTER TABLE requisitions ADD COLUMN reqi_holdback DOUBLE;

ALTER TABLE wflows MODIFY COLUMN wflw_description VARCHAR(500);

ALTER TABLE propertymodelextras ADD COLUMN prmx_fixedprice INT;

UPDATE propertymodelextras SET prmx_fixedprice = 1;

-- Cambios 2 febrero 2016

ALTER TABLE wflowcategories ADD COLUMN wfca_gcalendarsync INT;

ALTER TABLE wflowsteps ADD COLUMN wfsp_pending INT;

-- 15 de febrero 2016, arreglo bancos
ALTER TABLE workcontracts ADD COLUMN woco_hassanction INT default 1; 
ALTER TABLE opportunities ADD COLUMN oppo_datecreate DATETIME;

ALTER TABLE requisitions ADD COLUMN reqi_payments double;
ALTER TABLE requisitions ADD COLUMN reqi_balance double;

ALTER TABLE areas ADD COLUMN area_userid INT;
ALTER TABLE areas ADD FOREIGN KEY (area_userid) REFERENCES users(user_userid);

ALTER TABLE properties MODIFY COLUMN prty_lot INTEGER;

ALTER TABLE sflogs CHANGE COLUMN sflg_email sflg_username VARCHAR(40);
ALTER TABLE sflogs ADD COLUMN sflg_action CHAR;

ALTER TABLE users ADD COLUMN user_companyid INT;
ALTER TABLE users ADD FOREIGN KEY (user_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE requisitionreceipts ADD COLUMN rerc_handling INT;
ALTER TABLE requisitionreceipts ADD COLUMN rerc_reliability INT;

-- ELIMINAR WORKBUDGETS
ALTER TABLE developmentphases DROP FOREIGN KEY developmentphases_ibfk_8;
ALTER TABLE developmentphases DROP COLUMN dvph_workbudgetid;
ALTER TABLE requisitions DROP FOREIGN KEY requisitions_ibfk_12;
ALTER TABLE requisitions DROP COLUMN reqi_workbudgetid;
ALTER TABLE works DROP FOREIGN KEY works_ibfk_3;
ALTER TABLE works DROP COLUMN work_workbudgetitemid;
DELETE FROM workbudgetitems;
-- DROP TABLE workbudgetitems;
DELETE FROM workbudgets;
-- DROP TABLE workbudgets;

-- ARREGLOS BUDGET EN DISTINTAS TABLAS
ALTER TABLE requisitionreceipts ADD COLUMN rerc_budgetitemid INT;
ALTER TABLE requisitionreceipts ADD FOREIGN KEY (rerc_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

ALTER TABLE paccounts ADD COLUMN pacc_budgetitemid INT;
ALTER TABLE paccounts ADD FOREIGN KEY (pacc_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

ALTER TABLE bankmovements ADD COLUMN bkmv_budgetitemid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

-- MENUS DINAMICOS
CREATE TABLE menus (
	menu_menuid INT NOT NULL AUTO_INCREMENT, 
	menu_code VARCHAR(10),
	menu_name VARCHAR(50),
	menu_description VARCHAR(255),
	menu_index INT,
	menu_parentid INT,
	menu_usercreateid INT,
	menu_usermodifyid INT,
	menu_datecreate DATETIME,
	menu_datemodify DATETIME,
	PRIMARY KEY(menu_menuid),
	FOREIGN KEY (menu_parentid) REFERENCES menus(menu_menuid),
	FOREIGN KEY (menu_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (menu_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE modules ADD COLUMN modu_menuid INT;
ALTER TABLE modules ADD FOREIGN KEY (modu_menuid) REFERENCES menus(menu_menuid);
ALTER TABLE modules ADD COLUMN modu_index INT;
ALTER TABLE sfcomponentaccess ADD COLUMN sfca_menu INT;
UPDATE sfcomponentaccess SET sfca_menu = 1;
  
-- SISTEMA DE SESSIONES / CURSOS
CREATE TABLE sessiondisciplines (
	sedi_sessiondisciplineid INT NOT NULL AUTO_INCREMENT, 
	sedi_code VARCHAR(10),
	sedi_name VARCHAR(50),
	sedi_description VARCHAR(255),
	sedi_usercreateid INT,
	sedi_usermodifyid INT,
	sedi_datecreate DATETIME,
	sedi_datemodify DATETIME,
	PRIMARY KEY(sedi_sessiondisciplineid),
	FOREIGN KEY (sedi_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (sedi_usermodifyid) REFERENCES users(user_userid)
);

CREATE TABLE sessiontypes (
	sety_sessiontypeid INT NOT NULL AUTO_INCREMENT, 
	sety_code VARCHAR(10),
	sety_name VARCHAR(50),
	sety_description VARCHAR(255),
	sety_duration INT,
	sety_capacity INT,
	sety_sessiondisciplineid INT,
	sety_usercreateid INT,
	sety_usermodifyid INT,
	sety_datecreate DATETIME,
	sety_datemodify DATETIME,
	PRIMARY KEY(sety_sessiontypeid),
	FOREIGN KEY (sety_sessiondisciplineid) REFERENCES sessiondisciplines(sedi_sessiondisciplineid),
	FOREIGN KEY (sety_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (sety_usermodifyid) REFERENCES users(user_userid)
);
 
-- ELIMINA TABLAS CREADAS QUE DEBEN MODIFICARSE
-- DROP TABLE usersessions;
-- DROP TABLE customersessions;
-- DROP TABLE sessions;

CREATE TABLE sessions (
	sess_sessionid INT NOT NULL AUTO_INCREMENT, 
	sess_code VARCHAR(10),
	sess_name VARCHAR(50),
	sess_description VARCHAR(255),
	sess_startdate DATETIME,
	sess_enddate DATETIME,
	sess_reservations INT,
	sess_userid INT,
	sess_sessiontypeid INT,
	sess_usercreateid INT,
	sess_usermodifyid INT,
	sess_datecreate DATETIME,
	sess_datemodify DATETIME,
	PRIMARY KEY(sess_sessionid),
	FOREIGN KEY (sess_sessiontypeid) REFERENCES sessiontypes(sety_sessiontypeid),
	FOREIGN KEY (sess_userid) REFERENCES users(user_userid),
	FOREIGN KEY (sess_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (sess_usermodifyid) REFERENCES users(user_userid)
);

-- drop table ordersessions;
CREATE TABLE ordersessions (
	orss_ordersessionid INT NOT NULL AUTO_INCREMENT, 
	orss_attended INT,
	orss_sessionid INT,
	orss_orderid INT,
	orss_usercreateid INT,
	orss_usermodifyid INT,
	orss_datecreate DATETIME,
	orss_datemodify DATETIME,
	PRIMARY KEY(orss_ordersessionid),
	FOREIGN KEY (orss_sessionid) REFERENCES sessions(sess_sessionid),
	FOREIGN KEY (orss_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (orss_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (orss_usermodifyid) REFERENCES users(user_userid)
);

-- DROP TABLE sessionsales;

CREATE TABLE sessionsales (
	sesa_sessionsaleid INT NOT NULL AUTO_INCREMENT, 
	sesa_status CHAR NOT NULL,
	sesa_type CHAR,
	sesa_code VARCHAR(10),
	sesa_name VARCHAR(30),
	sesa_description VARCHAR(500),
	sesa_startdate DATETIME,
	sesa_enddate DATETIME,
	sesa_progress INT,
	sesa_tags VARCHAR(255),
	sesa_orderid INT,
	sesa_customerid INT NOT NULL,
	sesa_salesuserid INT,
	sesa_wflowtypeid INT,
	sesa_wflowid INT,
	sesa_opportunityid INT,
	sesa_ordertypeid INT,
	sesa_sessiontypepackageid INT,
	sesa_usercreateid INT,
	sesa_usermodifyid INT,
	sesa_datecreate DATETIME,
	sesa_datemodify DATETIME,
	PRIMARY KEY (sesa_sessionsaleid),
	FOREIGN KEY (sesa_customerid) REFERENCES customers(cust_customerid),
	FOREIGN KEY (sesa_salesuserid) REFERENCES users(user_userid),
	FOREIGN KEY (sesa_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (sesa_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
	FOREIGN KEY (sesa_wflowid) REFERENCES wflows(wflw_wflowid),
	FOREIGN KEY (sesa_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid),
	FOREIGN KEY (sesa_sessiontypepackageid) REFERENCES sessiontypepackages(setp_sessiontypepackageid),
	FOREIGN KEY (sesa_opportunityid) REFERENCES opportunities(oppo_opportunityid),
	FOREIGN KEY (sesa_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (sesa_usermodifyid) REFERENCES users(user_userid)
);

CREATE TABLE sessiontypepackages (
	setp_sessiontypepackageid INT NOT NULL AUTO_INCREMENT, 
	setp_code VARCHAR(10),
	setp_name VARCHAR(50),
	setp_description VARCHAR(255),
	setp_type CHAR,
	setp_sessions INT,
	setp_startdate DATETIME,
	setp_enddate DATETIME,
	setp_saleprice DOUBLE,
	setp_sessiontypeid INT,
	setp_usercreateid INT,
	setp_usermodifyid INT,
	setp_datecreate DATE,
	setp_datemodify DATE,
	PRIMARY KEY(setp_sessiontypepackageid),
	FOREIGN KEY (setp_sessiontypeid) REFERENCES sessiontypes(sety_sessiontypeid),
	FOREIGN KEY (setp_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (setp_usermodifyid) REFERENCES users(user_userid)
);

-- drop table ordersessiontypepackages;

CREATE TABLE ordersessiontypepackages (
	orsp_ordersessiontypepackageid INT NOT NULL AUTO_INCREMENT, 
	orsp_name VARCHAR(50),
	orsp_description VARCHAR(255),
	orsp_quantity INT,
	orsp_baseprice DOUBLE,
	orsp_price DOUBLE,
	orsp_amount DOUBLE,
	orsp_sessiontypepackageid INT,
	orsp_orderid INT,
	orsp_usercreateid INT,
	orsp_usermodifyid INT,
	orsp_datecreate DATETIME,
	orsp_datemodify DATETIME,
	PRIMARY KEY(orsp_ordersessiontypepackageid),
	FOREIGN KEY (orsp_sessiontypepackageid) REFERENCES sessiontypepackages(setp_sessiontypepackageid),
	FOREIGN KEY (orsp_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (orsp_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (orsp_usermodifyid) REFERENCES users(user_userid)
);
  
-- alters 20 abril 2016 
ALTER TABLE propertysales ADD COLUMN prsa_losemotiveid INT;
ALTER TABLE propertysales ADD COLUMN prsa_losecomments VARCHAR(255);
ALTER TABLE propertysales ADD COLUMN prsa_cancelldate DATETIME;

ALTER TABLE bankmovconcepts ADD COLUMN bkmc_budgetitemid INT;
ALTER TABLE bankmovconcepts ADD FOREIGN KEY (bkmc_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

ALTER TABLE sessiontypepackages MODIFY COLUMN setp_startdate DATE;
ALTER TABLE sessiontypepackages MODIFY COLUMN setp_enddate DATE;

ALTER TABLE sessions ADD COLUMN sess_available INT;

ALTER TABLE flexconfig ADD COLUMN flxc_negativebudget INT;

ALTER TABLE products ADD COLUMN prod_currencyid INT;

CREATE TABLE industries (
indu_industryid INT NOT NULL AUTO_INCREMENT,
indu_code VARCHAR(10),
indu_name VARCHAR(50),
indu_description VARCHAR (512),
indu_usercreateid INT,
indu_usermodifyid INT,
indu_datecreate DATETIME,
indu_datemodify DATETIME,
PRIMARY KEY (indu_industryid),
FOREIGN KEY (indu_usercreateid) REFERENCES users (user_userid),
FOREIGN KEY (indu_usermodifyid) REFERENCES users (user_userid)
);

ALTER TABLE customers ADD COLUMN cust_industryid INT;
ALTER TABLE customers ADD COLUMN cust_phone VARCHAR(20);
ALTER TABLE customers ADD COLUMN cust_email VARCHAR(50);

-- 16 de mayo 2016, manejo de monedas
ALTER TABLE bankaccounts ADD COLUMN bkac_currencyid INT;
ALTER TABLE bankaccounts ADD FOREIGN KEY (bkac_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE requisitions ADD COLUMN reqi_currencyid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE requisitions ADD COLUMN reqi_currencyparity VARCHAR(10);

ALTER TABLE requisitionreceipts ADD COLUMN rerc_currencyid INT;
ALTER TABLE requisitionreceipts ADD FOREIGN KEY (rerc_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE requisitionreceipts ADD COLUMN rerc_currencyparity VARCHAR(10);

ALTER TABLE paccounts ADD COLUMN pacc_currencyid INT;
ALTER TABLE paccounts ADD FOREIGN KEY (pacc_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE paccounts ADD COLUMN pacc_currencyparity DOUBLE;

ALTER TABLE raccounts ADD COLUMN racc_currencyid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE raccounts ADD COLUMN racc_currencyparity DOUBLE;

ALTER TABLE bankmovements ADD COLUMN bkmv_currencyid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE bankmovements ADD COLUMN bkmv_currencyparity DOUBLE;

ALTER TABLE orderdeliveries ADD COLUMN odly_currencyid INT;
ALTER TABLE orderdeliveries ADD FOREIGN KEY (odly_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE orderdeliveries ADD COLUMN odly_currencyparity DOUBLE;

ALTER TABLE bankmovconcepts ADD COLUMN bkmc_currencyparity DOUBLE;
ALTER TABLE bankmovconcepts ADD COLUMN bkmc_amountconverted DOUBLE;

ALTER TABLE flexconfig ADD COLUMN flxc_customerprotection INT;

ALTER TABLE opportunitydetails ADD COLUMN opde_downpayment DOUBLE;

ALTER TABLE typecostcenters ADD COLUMN tycc_type CHAR;

ALTER TABLE bankmovements ADD COLUMN bkmv_budgetitemid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);


ALTER TABLE loans ADD COLUMN loan_companyid INT;
ALTER TABLE loans ADD FOREIGN KEY (loan_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE requisitions ADD COLUMN reqi_loanid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_loanid) REFERENCES loans(loan_loanid);

ALTER TABLE bankmovements ADD COLUMN bkmv_loanid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_loanid) REFERENCES loans(loan_loanid);

ALTER TABLE loanpayments ADD COLUMN lopa_bankmovconceptid INT;
ALTER TABLE loanpayments ADD FOREIGN KEY (lopa_bankmovconceptid) REFERENCES bankmovconcepts(bkmc_bankmovconceptid);

ALTER TABLE loandisbursements ADD COLUMN lodi_bankmovconceptid INT;
ALTER TABLE loandisbursements ADD FOREIGN KEY (lodi_bankmovconceptid) REFERENCES bankmovconcepts(bkmc_bankmovconceptid);

ALTER TABLE flexconfig ADD COLUMN flxc_comissionbudgetitemid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_comissionbudgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

-- 2016 06 22
ALTER TABLE requisitionreceiptitems MODIFY COLUMN reit_serial VARCHAR(40);
ALTER TABLE loans ADD COLUMN loan_disbursedamount DOUBLE;
ALTER TABLE loans ADD COLUMN loan_capitalpayment DOUBLE;
ALTER TABLE loans ADD COLUMN loan_capitalbalance DOUBLE;

ALTER TABLE budgets ADD COLUMN budg_userid INT;
ALTER TABLE budgets ADD FOREIGN KEY (budg_userid) REFERENCES users(user_userid);

ALTER TABLE flexconfig ADD COLUMN flxc_typecostcenterid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_typecostcenterid) REFERENCES typecostcenters(tycc_typecostcenterid);

ALTER TABLE budgets ADD COLUMN budg_companyid INT;
ALTER TABLE budgets ADD FOREIGN KEY (budg_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE raccounts ADD COLUMN racc_budgetitemid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

ALTER TABLE flexconfig ADD COLUMN flxc_comissionbudgetitemid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_comissionbudgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

-- CAMBIO NOMBRE TABLA TIPOS DE PARTIDAS PRESUPUESTALES, SOLO PARA MYSQL 5.6
ALTER TABLE typecostcenters CHANGE COLUMN tycc_typecostcenterid bgty_budgetitemtypeid INT NOT NULL AUTO_INCREMENT;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_code bgty_code VARCHAR(10);
ALTER TABLE typecostcenters CHANGE COLUMN tycc_name bgty_name VARCHAR(30);
ALTER TABLE typecostcenters CHANGE COLUMN tycc_description bgty_description VARCHAR(255);
ALTER TABLE typecostcenters CHANGE COLUMN tycc_isgroup bgty_isgroup INT;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_parentid bgty_parentid INT;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_type bgty_type CHAR;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_usercreateid bgty_usercreateid INT;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_usermodifyid bgty_usermodifyid INT;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_datecreate bgty_datecreate DATETIME;
ALTER TABLE typecostcenters CHANGE COLUMN tycc_datemodify bgty_datemodify DATETIME;
ALTER TABLE budgetitems DROP FOREIGN KEY budgetitems_ibfk_2;
ALTER TABLE budgetitems DROP COLUMN bgit_budgetitemtypeid;
ALTER TABLE flexconfig DROP FOREIGN KEY flexconfig_ibfk_12;
-- DROP TABLE budgetitemtypes;
ALTER TABLE typecostcenters RENAME budgetitemtypes;
ALTER TABLE budgetitems CHANGE COLUMN bgit_typecostcenterid bgit_budgetitemtypeid INT;
ALTER TABLE budgetitems DROP COLUMN bgit_code;
ALTER TABLE budgetitems DROP COLUMN bgit_name;
ALTER TABLE budgetitems DROP COLUMN bgit_description;
ALTER TABLE budgetitems ADD COLUMN bgit_comments VARCHAR(255);
ALTER TABLE flexconfig CHANGE COLUMN flxc_typecostcenterid flxc_depositbudgetitemtypeid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_depositbudgetitemtypeid) REFERENCES budgetitemtypes(bgty_budgetitemtypeid);
ALTER TABLE budgetitemtypes ADD COLUMN bgty_type CHAR;
ALTER TABLE budgetitems ADD COLUMN bgit_provisioned DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_provisioned DOUBLE;

ALTER TABLE bankmovements ADD COLUMN bkmv_nocheck VARCHAR(20);

-- MOVIMIENTOS SESIONES CAPACITACION, 20160701
ALTER TABLE sessions ADD COLUMN sess_isseries INT;
ALTER TABLE sessions ADD COLUMN sess_seriesstart DATE;
ALTER TABLE sessions ADD COLUMN sess_seriesend DATE;
ALTER TABLE sessions ADD COLUMN sess_seriesapplyall INT;
ALTER TABLE sessions ADD COLUMN sess_seriesparentid INT;
ALTER TABLE sessions ADD COLUMN sess_seriesmonday INT;
ALTER TABLE sessions ADD COLUMN sess_seriestuesday INT;
ALTER TABLE sessions ADD COLUMN sess_serieswednesday INT;
ALTER TABLE sessions ADD COLUMN sess_seriesthursday INT;
ALTER TABLE sessions ADD COLUMN sess_seriesfriday INT;
ALTER TABLE sessions ADD COLUMN sess_seriessaturday INT;
ALTER TABLE sessions ADD COLUMN sess_seriessunday INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriesapplyall INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriesmonday INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriestuesday INT;
ALTER TABLE ordersessions ADD COLUMN orss_serieswednesday INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriesthursday INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriesfriday INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriessaturday INT;
ALTER TABLE ordersessions ADD COLUMN orss_seriessunday INT;


-- 20160705
ALTER TABLE loans ADD COLUMN loan_revolving INT;

CREATE TABLE invoices (
 	invo_invoiceid INT NOT NULL AUTO_INCREMENT,
 	invo_code VARCHAR(10),
 	invo_name VARCHAR(30),
 	invo_description VARCHAR(255),  
 	invo_duedate DATE,
invo_stampdatetime DATETIME,
invo_amount DOUBLE,
invo_tax DOUBLE,
invo_total DOUBLE,
invo_userid INT,
invo_customerid INT,
invo_currencyid INT,
invo_folio VARCHAR(100),
invo_certstring VARCHAR(255),
invo_cfdiseal VARCHAR(255),
invo_satseal VARCHAR(255),
invo_orderid INT,
invo_status CHAR,
invo_paymentstatus CHAR,
invo_usercreateid INT,
invo_usermodifyid INT,
invo_datecreate DATETIME,
invo_datemodify DATETIME,
 	PRIMARY KEY (invo_invoiceid),
FOREIGN KEY (invo_orderid) REFERENCES orders(orde_orderid),
FOREIGN KEY (invo_userid) REFERENCES users(user_userid),
FOREIGN KEY (invo_customerid) REFERENCES customers(cust_customerid),
FOREIGN KEY (invo_currencyid) REFERENCES currencies(cure_currencyid)
);


CREATE TABLE invoiceorderdeliveries (
inod_invoiceorderdeliveryid INT NOT NULL AUTO_INCREMENT,
inod_code VARCHAR(10),
inod_invoiceid INT NOT NULL,
inod_orderdeliveryid INT NOT NULL,
inod_amount DOUBLE,
inod_usercreateid INT,
inod_usermodifyid INT,
inod_datecreate DATETIME,
inod_datemodify DATETIME,
PRIMARY KEY (inod_invoiceorderdeliveryid),
FOREIGN KEY (inod_invoiceid) REFERENCES invoices(invo_invoiceid),
FOREIGN KEY (inod_orderdeliveryid) REFERENCES orderdeliveries(odly_orderdeliveryid)	
);

ALTER TABLE ordergroups ADD COLUMN ordg_showamount INT;
ALTER TABLE quotegroups ADD COLUMN qogr_showamount INT;

ALTER TABLE equipmentservices ADD COLUMN eqsv_status CHAR(1);
ALTER TABLE equipmentuses ADD COLUMN eqis_meterin FLOAT;
ALTER TABLE equipmentuses ADD COLUMN eqis_fuelin FLOAT;
ALTER TABLE equipmentuses ADD COLUMN eqis_fuelprice DOUBLE;

ALTER TABLE socials MODIFY COLUMN soci_icon VARCHAR(500);

-- 20160722
ALTER TABLE sfconfig ADD COLUMN sfcf_listtype CHAR;

ALTER TABLE projects ADD COLUMN proj_comments VARCHAR(255);
ALTER TABLE requisitions MODIFY COLUMN reqi_description VARCHAR(1000);
ALTER TABLE projects ADD COLUMN proj_datecreateproject DATETIME;

ALTER TABLE ordercomplaints ADD COLUMN ordc_userid INT;
ALTER TABLE ordercomplaints ADD FOREIGN KEY (ordc_userid) REFERENCES users(user_userid);

ALTER TABLE budgets ADD COLUMN budg_provisionedwithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_paymentwithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_totalwithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_balancewithdraw DOUBLE;


ALTER TABLE budgets ADD COLUMN budg_provisioneddeposit DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_paymentdeposit DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_totaldeposit DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_balancedeposit DOUBLE;

-- 20160811
ALTER TABLE works DROP FOREIGN KEY works_ibfk_3;
ALTER TABLE works DROP COLUMN work_workbudgetitemid;
ALTER TABLE requisitions DROP FOREIGN KEY requisitions_ibfk_12;
ALTER TABLE requisitions DROP COLUMN reqi_workbudgetid;
ALTER TABLE developmentphases DROP FOREIGN KEY developmentphases_ibfk_8;
ALTER TABLE developmentphases DROP COLUMN dvph_workbudgetid;

-- DROP TABLE eosconfig;
-- DROP TABLE workbudgetitems;
-- DROP TABLE workbudgetitemtypes;
-- DROP TABLE workbudgets;

ALTER TABLE customers ADD COLUMN cust_mobile VARCHAR(15);

CREATE TABLE filetypes (
 	fity_filetypeid INT NOT NULL AUTO_INCREMENT,
 	fity_code VARCHAR(10),
 	fity_name VARCHAR(30),
 	fity_description VARCHAR(255),  
	fity_usercreateid INT,
	fity_usermodifyid INT,
	fity_datecreate DATETIME,
	fity_datemodify DATETIME,
 	PRIMARY KEY (fity_filetypeid)
);

ALTER TABLE wflowdocumenttypes ADD COLUMN wfdt_filetypeid INT;
ALTER TABLE wflowdocumenttypes ADD FOREIGN KEY (wfdt_filetypeid) REFERENCES filetypes(fity_filetypeid);

ALTER TABLE wflowdocuments ADD COLUMN wfdo_filetypeid INT;
ALTER TABLE wflowdocuments ADD FOREIGN KEY (wfdo_filetypeid) REFERENCES filetypes(fity_filetypeid);

CREATE TABLE usercompanies (
  uscp_usercompanyid INT NOT NULL AUTO_INCREMENT,
  uscp_userid INT NOT NULL,
  uscp_companyid INT NOT NULL,
	uscp_usercreateid INT,
	uscp_usermodifyid INT,
	uscp_datecreate DATETIME,
	uscp_datemodify DATETIME,
  PRIMARY KEY (uscp_usercompanyid),
  FOREIGN KEY (uscp_userid) REFERENCES users(user_userid),
  FOREIGN KEY (uscp_companyid) REFERENCES companies(comp_companyid)
);

CREATE TABLE territories (
 	terr_territoryid INT NOT NULL AUTO_INCREMENT,
 	terr_code VARCHAR(10),
 	terr_name VARCHAR(30),
 	terr_description VARCHAR(255),  
	terr_usercreateid INT,
	terr_usermodifyid INT,
	terr_datecreate DATETIME,
	terr_datemodify DATETIME,
 	PRIMARY KEY (terr_territoryid)
);

ALTER TABLE customers ADD COLUMN cust_territoryid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_territoryid) REFERENCES territories(terr_territoryid);

ALTER TABLE flexconfig ADD COLUMN flxc_defaulttypecustomer CHAR;

ALTER TABLE budgets ADD COLUMN budg_provisionedwithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_paymentwithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_totalwithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_balancewithdraw DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_provisioneddeposit DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_paymentdeposit DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_totaldeposit DOUBLE;
ALTER TABLE budgets ADD COLUMN budg_balancedeposit DOUBLE;

ALTER TABLE sfcaptions ADD COLUMN sfcp_enabled INT;
UPDATE sfcaptions SET sfcp_enabled = 1;

ALTER TABLE socials MODIFY COLUMN soci_icon VARCHAR(200);

-- CAMBIOS DE AGREGAR SINC GOOGLE SESIONES
ALTER TABLE sessiontypes ADD COLUMN sety_gcalendarsync INT;
ALTER TABLE sessiontypes ADD COLUMN sety_gcalendarid VARCHAR(100);
ALTER TABLE sessions ADD COLUMN sess_googleeventid VARCHAR(100);
ALTER TABLE sessions ADD COLUMN sess_usergoogleeventid VARCHAR(100);

ALTER TABLE customerrelatives ADD COLUMN curl_responsible INT;

-- CAMBIOS 20160827
ALTER TABLE ordertypes ADD COLUMN ortp_autorenew INT;

UPDATE customers SET cust_status = 'A' WHERE cust_status = 'C';

ALTER TABLE loans CHANGE COLUMN loan_downpayment loan_commissionpayment DOUBLE NULL DEFAULT NULL ;

ALTER TABLE loans CHANGE COLUMN loan_downpayment loan_commissionpayment DOUBLE NULL DEFAULT NULL ;

-- 20160909
-- MicroCreditos
CREATE TABLE terms (
term_termid INT NOT NULL AUTO_INCREMENT,
term_code VARCHAR(10) NOT NULL,
term_name VARCHAR(30) NOT NULL,
term_description VARCHAR(255),  
       term_deadline INT NOT NULL,
       term_interest DOUBLE NOT NULL,
       term_type CHAR NOT NULL,
term_usercreateid INT,
term_usermodifyid INT,
term_datecreate DATETIME,
term_datemodify DATETIME,
PRIMARY KEY (term_termid)
);

CREATE TABLE ordercredits (
orcr_ordercreditid INT NOT NULL AUTO_INCREMENT, 
orcr_name VARCHAR(50),
orcr_description VARCHAR(255),
orcr_quantity INT,
orcr_baseprice DOUBLE,
orcr_price DOUBLE,
       orcr_interest DOUBLE,
orcr_amount DOUBLE,	
orcr_orderid INT,
orcr_usercreateid INT,
orcr_usermodifyid INT,
orcr_datecreate DATETIME,
orcr_datemodify DATETIME,
PRIMARY KEY(orcr_ordercreditid),	
FOREIGN KEY (orcr_orderid) REFERENCES orders(orde_orderid),
FOREIGN KEY (orcr_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (orcr_usermodifyid) REFERENCES users(user_userid)
);

CREATE TABLE credits (
cred_creditid INT NOT NULL AUTO_INCREMENT, 
       cred_termid INT NOT NULL,	
cred_customerid INT NOT NULL,
       cred_orderid INT,
       cred_ordertypeid INT,
       cred_salesuserid INT,
cred_code VARCHAR(10),
cred_name VARCHAR(30),
cred_comments VARCHAR(500),
       cred_status CHAR NOT NULL,
       cred_firstpayment DATE,
cred_startdate DATETIME,
cred_enddate DATETIME,
       cred_amount DOUBLE NOT NULL,
cred_progress INT,
cred_tags VARCHAR(255),
       cred_wflowtypeid INT,
cred_wflowid INT,
cred_usercreateid INT,
cred_usermodifyid INT,
cred_datecreate DATETIME,
cred_datemodify DATETIME,
PRIMARY KEY (cred_creditid),
FOREIGN KEY (cred_customerid) REFERENCES customers(cust_customerid),
FOREIGN KEY (cred_salesuserid) REFERENCES users(user_userid),
FOREIGN KEY (cred_orderid) REFERENCES orders(orde_orderid),
FOREIGN KEY (cred_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
FOREIGN KEY (cred_wflowid) REFERENCES wflows(wflw_wflowid),
FOREIGN KEY (cred_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid),	
FOREIGN KEY (cred_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (cred_orderid) REFERENCES orders(orde_orderid),
FOREIGN KEY (cred_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
FOREIGN KEY (cred_wflowid) REFERENCES wflows(wflw_wflowid),
FOREIGN KEY (cred_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid),	
FOREIGN KEY (cred_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (cred_usermodifyid) REFERENCES users(user_userid)
);


ALTER TABLE bankmovconcepts ADD COLUMN bkmc_orderdeliveryid INT;
ALTER TABLE bankmovconcepts ADD FOREIGN KEY (bkmc_orderdeliveryid) REFERENCES orderdeliveries(odly_orderdeliveryid);

ALTER TABLE developmentblocks CHANGE COLUMN dvbl_processporcent dvbl_processpercentage INT;
ALTER TABLE bankmovconcepts ADD COLUMN bkmc_orderdeliveryid INT;
ALTER TABLE bankmovconcepts ADD FOREIGN KEY (bkmc_orderdeliveryid) REFERENCES orderdeliveries(odly_orderdeliveryid);


ALTER TABLE orderdeliveryitems ADD COLUMN odyi_ordercreditid INT;
ALTER TABLE orderdeliveryitems ADD FOREIGN KEY (odyi_ordercreditid) REFERENCES ordercredits(orcr_ordercreditid);

ALTER TABLE companies ADD COLUMN comp_logo VARCHAR(255);

ALTER TABLE requisitiontypes ADD COLUMN rqtp_stock CHAR;
ALTER TABLE wflowdocuments ADD COLUMN wfdo_filelink VARCHAR(100);
ALTER TABLE useremails MODIFY COLUMN usem_email VARCHAR(100);

ALTER TABLE usersocials MODIFY COLUMN usso_account VARCHAR(50);

ALTER TABLE modules ADD COLUMN modu_enablemobile int;

ALTER TABLE developmentphases ADD COLUMN dvph_feeduedate DATE;

-- 20160922

ALTER TABLE workcontracts ADD COLUMN woco_totalreal DOUBLE;

CREATE TABLE consultingservices (
cose_consultingserviceid INT NOT NULL AUTO_INCREMENT, 
cose_code VARCHAR(10) NOT NULL,
cose_name VARCHAR(60) NOT NULL,
cose_description VARCHAR(255),
cose_usercreateid INT,
cose_usermodifyid INT,
cose_datecreate DATETIME,
cose_datemodify DATETIME,
PRIMARY KEY (cose_consultingserviceid),
FOREIGN KEY (cose_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (cose_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE customers ADD COLUMN cust_consultingserviceid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_consultingserviceid) REFERENCES consultingservices (cose_consultingserviceid);

ALTER TABLE flexconfig ADD COLUMN flxc_enableautofill INT;

ALTER TABLE paccounts ADD COLUMN pacc_reqicode VARCHAR(10);

ALTER TABLE wflowdocuments MODIFY COLUMN wfdo_filelink VARCHAR(200);

CREATE TABLE customercompanies (
	cucp_customercompanyid INT NOT NULL AUTO_INCREMENT,
	cucp_customerid INT NOT NULL,
	cucp_companyid INT NOT NULL,
	cucp_usercreateid INT,
	cucp_usermodifyid INT,
	cucp_datecreate DATETIME,
	cucp_datemodify DATETIME,
	PRIMARY KEY (cucp_customercompanyid),
	FOREIGN KEY (cucp_customerid) REFERENCES customers(cust_customerid),
	FOREIGN KEY (cucp_companyid) REFERENCES companies(comp_companyid)
);


ALTER TABLE territories MODIFY COLUMN terr_name VARCHAR(60);

CREATE TABLE regions (
	regi_regionid INT NOT NULL AUTO_INCREMENT, 
	regi_code VARCHAR(10) NOT NULL,
	regi_name VARCHAR(60) NOT NULL,
	regi_description VARCHAR(255),
	regi_usercreateid INT,
	regi_usermodifyid INT,
	regi_datecreate DATETIME,
	regi_datemodify DATETIME,
	PRIMARY KEY (regi_regionid),
	FOREIGN KEY (regi_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (regi_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE customers ADD COLUMN cust_regionid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_regionid) REFERENCES regions(regi_regionid);

ALTER TABLE flexconfig ADD COLUMN flxc_systemcurrencyid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_systemcurrencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE customeraddress change column cuad_neighborhood cuad_neighborhood varchar(60);
ALTER TABLE customeraddress change column cuad_number cuad_number varchar(25);
ALTER TABLE industries CHANGE COLUMN indu_name indu_name VARCHAR(100) NULL DEFAULT NULL ;

ALTER TABLE quotes ADD COLUMN quot_authnum DOUBLE;
ALTER TABLE customers ADD COLUMN cust_rating CHAR(1);


ALTER TABLE suppliers ADD COLUMN supl_currencyid INT;
ALTER TABLE suppliers ADD FOREIGN KEY (supl_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE suppliers ADD COLUMN supl_paymenttype CHAR(1);
ALTER TABLE customers ADD COLUMN cust_paymenttype CHAR(1);

CREATE TABLE supplierbankaccounts (
suba_supplierbankaccountid INT NOT NULL AUTO_INCREMENT,
suba_supplierid INT NOT NULL,
suba_bank VARCHAR(70),
suba_clabe VARCHAR(20),
   suba_accountnumber VARCHAR(30),
suba_usercreateid INT,
suba_usermodifyid INT,
suba_datecreate DATETIME,
suba_datemodify DATETIME,
PRIMARY KEY (suba_supplierbankaccountid),
FOREIGN KEY (suba_supplierid) REFERENCES suppliers(supl_supplierid)
);

CREATE TABLE customerbankaccounts (
cuba_customerbankaccountid INT NOT NULL AUTO_INCREMENT,
cuba_customerid INT NOT NULL,
   cuba_bank VARCHAR(70),
cuba_clabe VARCHAR(20),
   cuba_accountnumber VARCHAR(30),
cuba_usercreateid INT,
cuba_usermodifyid INT,
cuba_datecreate DATETIME,
cuba_datemodify DATETIME,
PRIMARY KEY (cuba_customerbankaccountid),
FOREIGN KEY (cuba_customerid) REFERENCES customers(cust_customerid)
);

-- 20161017
CREATE TABLE suppliercompanies (
sucp_suppliercompanyid INT NOT NULL AUTO_INCREMENT,
sucp_supplierid INT NOT NULL,
sucp_companyid INT NOT NULL,
sucp_usercreateid INT,
sucp_usermodifyid INT,
sucp_datecreate DATETIME,
sucp_datemodify DATETIME,
PRIMARY KEY (sucp_suppliercompanyid),
FOREIGN KEY (sucp_supplierid) REFERENCES suppliers(supl_supplierid),
FOREIGN KEY (sucp_companyid) REFERENCES companies(comp_companyid),
   FOREIGN KEY (sucp_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (sucp_usermodifyid) REFERENCES users(user_userid)
);


CREATE TABLE supplieremails (
suem_supplieremailid INT NOT NULL AUTO_INCREMENT, 
suem_type CHAR(1) NOT NULL,
suem_email VARCHAR(100),
suem_supplierid INT NOT NULL,
suem_usercreateid INT,
suem_usermodifyid INT,
suem_datecreate DATETIME,
suem_datemodify DATETIME,
PRIMARY KEY (suem_supplieremailid),
FOREIGN KEY (suem_supplierid) REFERENCES suppliers(supl_supplierid),
FOREIGN KEY (suem_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (suem_usermodifyid) REFERENCES users(user_userid)
);

CREATE TABLE supplierphones (
suph_supplierphoneid INT NOT NULL AUTO_INCREMENT, 
suph_type CHAR(1) NOT NULL,
   suph_number VARCHAR(15),
   suph_extension VARCHAR(5),
suph_fax VARCHAR(15),
suph_supplierid INT NOT NULL,
suph_usercreateid INT,
suph_usermodifyid INT,
suph_datecreate DATETIME,
suph_datemodify DATETIME,
PRIMARY KEY (suph_supplierphoneid),
FOREIGN KEY (suph_supplierid) REFERENCES suppliers(supl_supplierid),
FOREIGN KEY (suph_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (suph_usermodifyid) REFERENCES users(user_userid)
);


CREATE TABLE supplieraddress (
suad_supplieraddressid INT NOT NULL AUTO_INCREMENT, 
suad_type CHAR(1) NOT NULL,
suad_street VARCHAR(100),
suad_number VARCHAR(60),
suad_description VARCHAR(500),
suad_neighborhood VARCHAR(60),
suad_zip VARCHAR(5),
suad_cityid INT, 
suad_supplierid INT NOT NULL,
suad_usercreateid INT,
suad_usermodifyid INT,
suad_datecreate DATETIME,
suad_datemodify DATETIME,
PRIMARY KEY (suad_supplieraddressid),
FOREIGN KEY (suad_supplierid) REFERENCES suppliers(supl_supplierid),
FOREIGN KEY (suad_cityid) REFERENCES cities(city_cityid),
FOREIGN KEY (suad_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (suad_usermodifyid) REFERENCES users(user_userid)
);

CREATE TABLE suppliercontacts (
suco_suppliercontactid INT NOT NULL AUTO_INCREMENT, 
suco_firstname VARCHAR(50) NOT NULL,
suco_fatherlastname VARCHAR(50),
suco_motherlastname VARCHAR(50),
suco_position VARCHAR(50),
   suco_email VARCHAR(50),
suco_number VARCHAR(15),
suco_cellphone VARCHAR(15), 
suco_extension VARCHAR(5), 
suco_alias VARCHAR(255),
suco_comments VARCHAR(255),
suco_supplierid INT NOT NULL,
suco_usercreateid INT,
suco_usermodifyid INT,
suco_datecreate DATETIME,
suco_datemodify DATETIME,
PRIMARY KEY (suco_suppliercontactid),
FOREIGN KEY (suco_supplierid) REFERENCES suppliers(supl_supplierid),
FOREIGN KEY (suco_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (suco_usermodifyid) REFERENCES users(user_userid)
);


ALTER TABLE opportunities MODIFY COLUMN oppo_name VARCHAR(100);
ALTER TABLE quotes MODIFY COLUMN quot_name VARCHAR(100);
ALTER TABLE projects MODIFY COLUMN proj_name VARCHAR(100);
ALTER TABLE orders MODIFY COLUMN orde_name VARCHAR(100);
ALTER TABLE wflows MODIFY COLUMN wflw_name VARCHAR(100);


ALTER TABLE wflowsteps ADD COLUMN wfsp_userid INT;
ALTER TABLE wflowsteps ADD FOREIGN KEY (wfsp_userid) REFERENCES users(user_userid);

ALTER TABLE wflowusers ADD COLUMN wflu_assignsteps INT;

ALTER TABLE users ADD COLUMN user_startdate DATE;
ALTER TABLE users ADD COLUMN user_enddate DATE;
ALTER TABLE users ADD COLUMN user_bloodtype CHAR;
ALTER TABLE users ADD COLUMN user_description VARCHAR(1000);
ALTER TABLE users ADD COLUMN user_employeenumber VARCHAR(50);
ALTER TABLE users ADD COLUMN user_rfc VARCHAR(12);
ALTER TABLE users ADD COLUMN user_curp VARCHAR(18);
ALTER TABLE users ADD COLUMN user_socialnumber VARCHAR(11);

-- DROP TABLE terms;

CREATE TABLE credittypes (
crty_credittypeid INT NOT NULL AUTO_INCREMENT,
crty_code VARCHAR(10) NOT NULL,
crty_name VARCHAR(30) NOT NULL,
crty_description VARCHAR(255),  
crty_deadline INT NOT NULL,
crty_interest DOUBLE NOT NULL,
crty_type CHAR NOT NULL,
crty_creditlimit DOUBLE NOT NULL,
crty_guarantees INT(11),
crty_usercreateid INT,
crty_usermodifyid INT,
crty_datecreate DATETIME,
crty_datemodify DATETIME,
PRIMARY KEY (crty_credittypeid)
);

ALTER TABLE credits CHANGE COLUMN cred_termid cred_credittypeid INT(11) NOT NULL;
ALTER TABLE credits ADD FOREIGN KEY (cred_credittypeid) REFERENCES credittypes(crty_credittypeid);
ALTER TABLE customers ADD COLUMN cust_creditlimit DOUBLE;


CREATE TABLE usercreditlimits (
uscl_usercreditlimitid INT NOT NULL AUTO_INCREMENT, 
uscl_userid INT(11) NOT NULL,
uscl_creditlimit DOUBLE NOT NULL,
uscl_usercreateid INT,
uscl_usermodifyid INT,
uscl_datecreate DATETIME,
uscl_datemodify DATETIME,
PRIMARY KEY (uscl_usercreditlimitid),
FOREIGN KEY (uscl_userid) REFERENCES users(user_userid)
);

CREATE TABLE creditguarantees (
crgu_creditguaranteesid INT NOT NULL AUTO_INCREMENT,
crgu_customerid INT NOT NULL,
crgu_creditid INT NOT NULL,
crgu_usercreateid INT,
crgu_usermodifyid INT,
crgu_datecreate DATETIME,
crgu_datemodify DATETIME,
PRIMARY KEY (crgu_creditguaranteesid),
FOREIGN KEY (crgu_customerid) REFERENCES customers(cust_customerid),
FOREIGN KEY (crgu_creditid) REFERENCES credits(cred_creditid)
);

ALTER TABLE customerdates ADD COLUMN cuda_emailreminder INT;
ALTER TABLE customerdates ADD COLUMN cuda_reminddate INT;

-- 20161022
ALTER TABLE products ADD COLUMN prod_reneworder INT; 
ALTER TABLE products ADD COLUMN prod_ordertypeid INT;
ALTER TABLE products ADD FOREIGN KEY (prod_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid);

ALTER TABLE orders ADD COLUMN orde_reneworderid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_reneworderid) REFERENCES orders(orde_orderid);

ALTER TABLE raccounts ADD COLUMN racc_failure INT(11);
ALTER TABLE raccounts ADD COLUMN racc_related VARCHAR(30);

ALTER TABLE developmentphases ADD COLUMN dvph_maintenancecost DOUBLE;

ALTER TABLE users MODIFY COLUMN user_rfc VARCHAR(13);
UPDATE products SET prod_reneworder = 0;

ALTER TABLE ordercomplaints ADD COLUMN ordc_file VARCHAR(255);
ALTER TABLE ordercomplaints MODIFY ordc_orderid INT;

CREATE TABLE customerpaymenttypes (
cupt_customerpaymenttypeid INT NOT NULL AUTO_INCREMENT, 
cupt_paymenttype CHAR(1) NOT NULL,
cupt_currencyid INT,
cupt_customerid INT NOT NULL,
cupt_usercreateid INT,
cupt_usermodifyid INT,
cupt_datecreate DATETIME,
cupt_datemodify DATETIME,
PRIMARY KEY (cupt_customerpaymenttypeid),
FOREIGN KEY (cupt_currencyid) REFERENCES currencies(cure_currencyid),
FOREIGN KEY (cupt_customerid) REFERENCES customers(cust_customerid),
FOREIGN KEY (cupt_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (cupt_usermodifyid) REFERENCES users(user_userid)
);

CREATE TABLE supplierpaymenttypes (
supt_supplierpaymenttypeid INT NOT NULL AUTO_INCREMENT, 
supt_paymenttype CHAR(1) NOT NULL,
supt_currencyid INT,
supt_supplierid INT NOT NULL,
supt_usercreateid INT,
supt_usermodifyid INT,
supt_datecreate DATETIME,
supt_datemodify DATETIME,
PRIMARY KEY (supt_supplierpaymenttypeid),
FOREIGN KEY (supt_currencyid) REFERENCES currencies(cure_currencyid),
FOREIGN KEY (supt_supplierid) REFERENCES suppliers(supl_supplierid),
FOREIGN KEY (supt_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (supt_usermodifyid) REFERENCES users(user_userid)
);

-- 20161031

CREATE TABLE productcompanies (
prcp_productcompanyid INT NOT NULL AUTO_INCREMENT, 
prcp_companyid INT NOT NULL,
prcp_productid INT NOT NULL,
prcp_usercreateid INT,
prcp_usermodifyid INT,
prcp_datecreate DATETIME,
prcp_datemodify DATETIME,
PRIMARY KEY (prcp_productcompanyid),
FOREIGN KEY (prcp_companyid) REFERENCES companies(comp_companyid),
FOREIGN KEY (prcp_productid) REFERENCES products(prod_productid),
FOREIGN KEY (prcp_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (prcp_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE users ADD COLUMN user_phone VARCHAR(20);
ALTER TABLE users ADD COLUMN user_mobile VARCHAR(12);

ALTER TABLE sfconfig ADD COLUMN sfcf_mailserveruser VARCHAR(50);
ALTER TABLE sfconfig ADD COLUMN sfcf_mailserverpassword VARCHAR(255);

ALTER TABLE products ADD COLUMN prod_wflowtypeid INT;
ALTER TABLE products ADD FOREIGN KEY (prod_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);
ALTER TABLE flexconfig ADD COLUMN flxc_orderrenewdays INT(11);

ALTER TABLE companies MODIFY COLUMN comp_number VARCHAR(20);

ALTER TABLE customerbankaccounts ADD COLUMN cuba_currencyid INT;
ALTER TABLE customerbankaccounts ADD FOREIGN KEY (cuba_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE supplierbankaccounts ADD COLUMN suba_currencyid INT;
ALTER TABLE supplierbankaccounts ADD FOREIGN KEY (suba_currencyid) REFERENCES currencies(cure_currencyid);

-- 20161115

ALTER TABLE sessiontypes ADD COLUMN sety_currencyid INT;
ALTER TABLE sessiontypes ADD FOREIGN KEY (sety_currencyid) REFERENCES currencies(cure_currencyid);

ALTER TABLE sessionsales ADD COLUMN sesa_sessiondemo INT;

CREATE TABLE propertysaledetails (
	prsd_propertysaledetailid INT NOT NULL AUTO_INCREMENT, 
	prsd_propertysaleid INT NOT NULL,
	prsd_notary INT,
	prsd_writingnumber VARCHAR(20),
	prsd_usercreateid INT,
	prsd_usermodifyid INT,
	prsd_datecreate DATETIME,
	prsd_datemodify DATETIME,
	PRIMARY KEY (prsd_propertysaledetailid),
	FOREIGN KEY (prsd_propertysaleid) REFERENCES propertysales(prsa_propertysaleid),
	FOREIGN KEY (prsd_notary) REFERENCES suppliers(supl_supplierid),
	FOREIGN KEY (prsd_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (prsd_usermodifyid) REFERENCES users(user_userid)
);


CREATE TABLE customernotes (
cuno_customernoteid INT NOT NULL AUTO_INCREMENT,
cuno_customerid INT NOT NULL,
cuno_type CHAR(1) NOT NULL,
cuno_notes VARCHAR(500) NOT NULL,
cuno_file VARCHAR(255),
cuno_usercreateid INT,
cuno_usermodifyid INT,
cuno_datecreate DATETIME,
cuno_datemodify DATETIME,
PRIMARY KEY (cuno_customernoteid),
FOREIGN KEY (cuno_customerid) REFERENCES customers(cust_customerid),
FOREIGN KEY (cuno_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (cuno_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE developments ADD COLUMN deve_municipalagency INT;
ALTER TABLE developments ADD FOREIGN KEY (deve_municipalagency) REFERENCES suppliers(supl_supplierid);
ALTER TABLE developments ADD COLUMN deve_municipalagencywater INT;
ALTER TABLE developments ADD FOREIGN KEY (deve_municipalagencywater) REFERENCES suppliers(supl_supplierid);

ALTER TABLE raccounts ADD COLUMN racc_relatedraccountid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_relatedraccountid) REFERENCES raccounts(racc_raccountid);
ALTER TABLE raccounts DROP COLUMN racc_related;

ALTER TABLE raccounts ADD COLUMN racc_taxapplies INT;
ALTER TABLE raccounts ADD COLUMN racc_tax DOUBLE;

-- 20161130
ALTER TABLE raccounts ADD COLUMN racc_taxapplies INT;
ALTER TABLE raccounts ADD COLUMN racc_tax DOUBLE;

ALTER TABLE raccounts CHANGE COLUMN racc_amount racc_total DOUBLE NULL DEFAULT NULL ;
ALTER TABLE raccounts ADD COLUMN racc_amount DOUBLE;

ALTER TABLE credits ADD COLUMN cred_bond VARCHAR(20);

-- 20161214
ALTER TABLE properties ADD COLUMN prty_facade VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_map VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_location VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_latitude DOUBLE;
ALTER TABLE properties ADD COLUMN prty_longitude DOUBLE;
ALTER TABLE developmentblocks ADD COLUMN dvbl_map VARCHAR(255);

-- 20170110
ALTER TABLE sessions ADD COLUMN sess_autossign INT;

ALTER TABLE sessiontypepackages ADD COLUMN setp_enabled INT;
UPDATE sessiontypepackages SET setp_enabled = 1;

ALTER TABLE sessiontypepackages ADD COLUMN setp_profileid INT;
ALTER TABLE sessiontypepackages ADD FOREIGN KEY (setp_profileid) REFERENCES groups(group_profileidcure_currencyid);

ALTER TABLE properties DROP COLUMN prty_latitude;
ALTER TABLE properties DROP COLUMN prty_longitude;
ALTER TABLE properties ADD COLUMN prty_coordinates VARCHAR(50);

ALTER TABLE locations ADD COLUMN loct_coordinates VARCHAR(50);

ALTER TABLE sessionsales ADD COLUMN sesa_signletter INT;
ALTER TABLE sessionsales ADD COLUMN sesa_takephoto INT;

-- 20170127
ALTER TABLE sessionreviews MODIFY COLUMN serw_comments VARCHAR(500);
ALTER TABLE sessionreviews ADD COLUMN serw_programmini INT;
ALTER TABLE sessionreviews ADD COLUMN serw_integration INT;
ALTER TABLE sessionreviews ADD COLUMN serw_interaction INT;
ALTER TABLE sessionreviews ADD COLUMN serw_coordination INT;
ALTER TABLE sessionreviews ADD COLUMN serw_programjunior INT;
ALTER TABLE sessionreviews ADD COLUMN serw_recognition INT;
ALTER TABLE sessionreviews ADD COLUMN serw_crawl INT;
ALTER TABLE sessionreviews ADD COLUMN serw_back INT;
ALTER TABLE sessionreviews ADD COLUMN serw_chest INT;
ALTER TABLE sessionreviews ADD COLUMN serw_butterflyswim INT;
ALTER TABLE sessionreviews DROP COLUMN serw_breathing1;
ALTER TABLE sessionreviews DROP COLUMN serw_kick1;
ALTER TABLE sessionreviews DROP COLUMN serw_arrows1;
ALTER TABLE sessionreviews DROP COLUMN serw_coordination1;
ALTER TABLE sessionreviews DROP COLUMN serw_independence1;
ALTER TABLE sessionreviews DROP COLUMN serw_breathing2;
ALTER TABLE sessionreviews DROP COLUMN serw_kick2;
ALTER TABLE sessionreviews DROP COLUMN serw_arrows2;
ALTER TABLE sessionreviews DROP COLUMN serw_coordination2;
ALTER TABLE sessionreviews DROP COLUMN serw_independence2;
ALTER TABLE sessionreviews DROP COLUMN serw_breathing3;
ALTER TABLE sessionreviews DROP COLUMN serw_kick3;
ALTER TABLE sessionreviews DROP COLUMN serw_arrows3;
ALTER TABLE sessionreviews DROP COLUMN serw_coordination3;
ALTER TABLE sessionreviews DROP COLUMN serw_independence3;

ALTER TABLE propertysaledetails ADD COLUMN prsd_creditmodality VARCHAR(50);


ALTER TABLE raccounts ADD COLUMN racc_intransit INT;


ALTER TABLE flexconfig ADD COLUMN flxc_defaultbankaccountid INT;
ALTER TABLE credits ADD COLUMN cred_collectoruserid INT;

ALTER TABLE customers ADD COLUMN cust_reqpaytypeid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_reqpaytypeid) REFERENCES reqpaytypes(rqpt_reqpaytypeid);

ALTER TABLE customers ADD COLUMN cust_reqpaytypeid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_reqpaytypeid) REFERENCES reqpaytypes(rqpt_reqpaytypeid);

ALTER TABLE properties ADD COLUMN prty_garage VARCHAR(512);
ALTER TABLE properties ADD COLUMN prty_roofgarden VARCHAR(512);

ALTER TABLE bankaccounts ADD COLUMN bkac_responsibleid INT;
ALTER TABLE bankaccounts ADD FOREIGN KEY (bkac_responsibleid) REFERENCES users(user_userid);

ALTER TABLE whmovements ADD COLUMN whmv_companyid INT;
ALTER TABLE whmovements ADD FOREIGN KEY (whmv_companyid) REFERENCES companies(comp_companyid);
ALTER TABLE orderdeliveries ADD COLUMN odly_companyid INT;
ALTER TABLE orderdeliveries ADD FOREIGN KEY (odly_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE bankaccounts ADD COLUMN bkac_clabe VARCHAR(20);
ALTER TABLE developmentphases ADD COLUMN dvph_companyid INT;
ALTER TABLE developmentphases ADD FOREIGN KEY (dvph_companyid) REFERENCES companies(comp_companyid);
ALTER TABLE developmentphases ADD COLUMN dvph_bankaccountid INT;
ALTER TABLE developmentphases ADD FOREIGN KEY (dvph_bankaccountid) REFERENCES bankaccounts(bkac_bankaccountid);
ALTER TABLE propertymodels ADD COLUMN ptym_garage VARCHAR(512);
ALTER TABLE propertymodels ADD COLUMN ptym_roofgarden VARCHAR(512);

-- 20170425
ALTER TABLE orders ADD COLUMN orde_comments VARCHAR(1000);
ALTER TABLE orders ADD COLUMN orde_authorizeduser INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_authorizeduser) REFERENCES users (user_userid);
ALTER TABLE orders ADD COLUMN orde_authorizeddate DATETIME;
ALTER TABLE orders ADD COLUMN orde_cancellationdate DATETIME;
ALTER TABLE orders ADD COLUMN orde_customercontactid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_customercontactid) REFERENCES customercontacts (cuco_customercontactid);

ALTER TABLE flexconfig ADD COLUMN flxc_defaultformatorder INT;
ALTER TABLE orders ADD COLUMN orde_currencyparity DOUBLE;

-- 20170517
CREATE TABLE equipmentcompanies (
eqcp_equipmentcompanyid INT NOT NULL AUTO_INCREMENT, 
eqcp_companyid INT NOT NULL,
eqcp_equipmentid INT NOT NULL,
eqcp_usercreateid INT,
eqcp_usermodifyid INT,
eqcp_datecreate DATETIME,
eqcp_datemodify DATETIME,
PRIMARY KEY (eqcp_equipmentcompanyid),
FOREIGN KEY (eqcp_companyid) REFERENCES companies(comp_companyid),
FOREIGN KEY (eqcp_equipmentid) REFERENCES equipments(equi_equipmentid),
FOREIGN KEY (eqcp_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (eqcp_usermodifyid) REFERENCES users(user_userid)
);

--  Se cambia nombre a la tabla Tipos de Atención a Clientes

RENAME TABLE ordercomplainttypes TO customerservicetypes;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_ordercomplainttypeid csty_customerservicetypeid INT NOT NULL AUTO_INCREMENT;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_code csty_code VARCHAR(10) NOT NULL;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_name csty_name VARCHAR(50) NOT NULL;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_description csty_description VARCHAR(255);
ALTER TABLE customerservicetypes CHANGE COLUMN orct_userid csty_userid INT;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_sflog csty_sflog TEXT;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_usercreateid csty_usercreateid INT;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_usermodifyid csty_usermodifyid INT;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_datecreate csty_datecreate DATETIME;
ALTER TABLE customerservicetypes CHANGE COLUMN orct_datemodify csty_datemodify DATETIME;


--  Se cambia nombre a la tabla Atención a Clientes
RENAME TABLE ordercomplaints TO customerservices;
ALTER TABLE customerservices CHANGE COLUMN ordc_ordercomplaintid cuse_customerserviceid INT NOT NULL AUTO_INCREMENT;
ALTER TABLE customerservices CHANGE COLUMN ordc_code cuse_code VARCHAR(10);
ALTER TABLE customerservices CHANGE COLUMN ordc_name cuse_name VARCHAR(50) NOT NULL;
ALTER TABLE customerservices CHANGE COLUMN ordc_description cuse_description VARCHAR(255);
ALTER TABLE customerservices CHANGE COLUMN ordc_ordercomplainttypeid cuse_customerservicetypeid INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_orderid cuse_orderid INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_solution cuse_solution VARCHAR(500);
ALTER TABLE customerservices CHANGE COLUMN ordc_status cuse_status CHAR(1);
ALTER TABLE customerservices CHANGE COLUMN ordc_active cuse_active INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_registrationdate cuse_registrationdate DATE;
ALTER TABLE customerservices CHANGE COLUMN ordc_committaldate cuse_committaldate DATE;
ALTER TABLE customerservices CHANGE COLUMN ordc_solutiondate cuse_solutiondate DATE;
ALTER TABLE customerservices CHANGE COLUMN ordc_userid cuse_userid INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_file cuse_file VARCHAR(500);
ALTER TABLE customerservices CHANGE COLUMN ordc_companyid cuse_companyid INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_sflog cuse_sflog TEXT;
ALTER TABLE customerservices CHANGE COLUMN ordc_usercreateid cuse_usercreateid INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_usermodifyid cuse_usermodifyid INT;
ALTER TABLE customerservices CHANGE COLUMN ordc_datecreate cuse_datecreate DATETIME;
ALTER TABLE customerservices CHANGE COLUMN ordc_datemodify cuse_datemodify DATETIME;


​--  Se cambia nombre a la tabla Seguimiento de Atención a Clientes​

​RENAME TABLE ordercomplaintfollowups TO customerservicefollowups;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_ordercomplaintfollowupid csfo_customerservicefollowupid INT NOT NULL AUTO_INCREMENT;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_ordercomplaintid csfo_customerserviceid INT;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_description csfo_description VARCHAR(255);
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_userid csfo_userid INT;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_followupdate csfo_followupdate DATETIME;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_sflog csfo_sflog TEXT;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_usercreateid csfo_usercreateid INT;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_usermodifyid csfo_usermodifyid INT;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_datecreate csfo_datecreate DATETIME;
ALTER TABLE customerservicefollowups CHANGE COLUMN orcf_datemodify csfo_datemodify DATETIME;​

ALTER TABLE wflowsteptypes ADD COLUMN wfst_funnel INT;
ALTER TABLE wflowsteps ADD COLUMN wfsp_funnel INT;
ALTER TABLE wflows ADD COLUMN wflw_funnel INT;

-- 20170612

ALTER TABLE paccountassignments ADD COLUMN pass_currencyparity DOUBLE;
ALTER TABLE paccountassignments ADD COLUMN pass_amountconverted DOUBLE;

ALTER TABLE raccountassignments ADD COLUMN rass_currencyparity DOUBLE;
ALTER TABLE raccountassignments ADD COLUMN rass_amountconverted DOUBLE;
ALTER TABLE paccounts ADD COLUMN pacc_taxapplies INT;
ALTER TABLE paccounts ADD COLUMN pacc_tax DOUBLE;
ALTER TABLE paccounts ADD COLUMN pacc_total DOUBLE;

ALTER TABLE sessionsales ADD COLUMN sesa_inscriptiondate DATE;

ALTER TABLE paccountassignments ADD COLUMN pass_currencyparity DOUBLE;
ALTER TABLE paccountassignments ADD COLUMN pass_amountconverted DOUBLE;

ALTER TABLE raccountassignments ADD COLUMN rass_currencyparity DOUBLE;
ALTER TABLE raccountassignments ADD COLUMN rass_amountconverted DOUBLE;

ALTER TABLE paccounts ADD COLUMN pacc_taxapplies INT;
ALTER TABLE paccounts ADD COLUMN pacc_tax DOUBLE;
ALTER TABLE paccounts ADD COLUMN pacc_total DOUBLE;

-- MDM
ALTER TABLE propertymodelextras ADD COLUMN prmx_file VARCHAR(255);

ALTER TABLE flexconfig ADD COLUMN flxc_automaticpaymentcxc INT;

ALTER TABLE wflowlogs ADD COLUMN wflg_link VARCHAR(200);

ALTER TABLE ordertypes ADD COLUMN ortp_emailreminders INT;
ALTER TABLE ordertypes ADD COLUMN ortp_reminddaysbeforeendorder INT;
ALTER TABLE ordertypes ADD COLUMN ortp_profileid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_profileid) REFERENCES groups(prof_profileid);

-- 20170710

-- Config. Flex
ALTER TABLE flexconfig ADD COLUMN flxc_defaultformatopportunity INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultformatopportunity) REFERENCES formats (frmt_formatid);
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultformatorder) REFERENCES formats (frmt_formatid);

-- Oportunidades
ALTER TABLE opportunities ADD COLUMN oppo_currencyparity DOUBLE;
ALTER TABLE opportunities ADD COLUMN oppo_customerrequisition VARCHAR(20);

-- Cotizaciones
ALTER TABLE quotes ADD COLUMN quot_currencyparity DOUBLE;
ALTER TABLE quotes ADD COLUMN quot_coverageparity INT;
ALTER TABLE quotes ADD COLUMN quot_comments VARCHAR(1000);
ALTER TABLE quotes ADD COLUMN quot_customerrequisition VARCHAR(20);
ALTER TABLE quotes ADD COLUMN quot_customercontactid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_customercontactid) REFERENCES customercontacts (cuco_customercontactid);

ALTER TABLE quotes ADD COLUMN quot_authorizeddate DATETIME;
ALTER TABLE quotes ADD COLUMN quot_authorizeduser INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_authorizeduser) REFERENCES users (user_userid);
ALTER TABLE quotes ADD COLUMN quot_cancelleddate DATETIME;
ALTER TABLE quotes ADD COLUMN quot_cancelleduser INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_cancelleduser) REFERENCES users (user_userid);

-- Pedidos
ALTER TABLE orders ADD COLUMN  orde_coverageparity INT;
ALTER TABLE orders ADD COLUMN orde_customerrequisition VARCHAR(20);
ALTER TABLE orders CHANGE COLUMN orde_cancellationdate orde_cancelleddate DATETIME;
ALTER TABLE orders CHANGE COLUMN orde_cancellationuser orde_cancelleduser INT;

ALTER TABLE orders ADD COLUMN orde_cancelleduser INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_cancelleduser) REFERENCES users (user_userid);
ALTER TABLE orders ADD COLUMN orde_finisheddate DATETIME;
ALTER TABLE orders ADD COLUMN orde_finisheduser INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_finisheduser) REFERENCES users (user_userid);


-- 20170719
ALTER TABLE ordergroups ADD COLUMN ordg_createraccount INT;


ALTER TABLE raccounts ADD COLUMN racc_ordergroupid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_ordergroupid) REFERENCES ordergroups(ordg_ordergroupid);

ALTER TABLE ordergroups ADD COLUMN ordg_createraccount INT;


ALTER TABLE raccounts ADD COLUMN racc_ordergroupid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_ordergroupid) REFERENCES ordergroups(ordg_ordergroupid);

-- 20170904
ALTER TABLE credits ADD COLUMN cred_companyid INT;
ALTER TABLE credits ADD FOREIGN KEY (cred_companyid) REFERENCES companies(comp_companyid);
ALTER TABLE credits ADD COLUMN cred_currencyid INT;
ALTER TABLE credits ADD FOREIGN KEY (cred_currencyid) REFERENCES currencies(cure_currencyid);
ALTER TABLE credits ADD COLUMN cred_currencyparity DOUBLE;
-- Quitar hora de los créditos y luego cambiar la columna el tipo de datos a Fecha solamente
UPDATE credits SET cred_startdate = SUBSTRING(cred_startdate, 1, 10);
ALTER TABLE credits MODIFY COLUMN cred_startdate DATE;
ALTER TABLE credits MODIFY COLUMN cred_enddate DATE;

ALTER TABLE wflows ADD COLUMN wflw_customerid INT;
ALTER TABLE wflows ADD FOREIGN KEY (wflw_customerid) REFERENCES customers(cust_customerid);
ALTER TABLE wflowdocuments MODIFY COLUMN wfdo_filelink VARCHAR(500);
ALTER TABLE wflowlogs MODIFY COLUMN wflg_link VARCHAR(500);

ALTER TABLE modules ADD COLUMN modu_enablehelp INT;
ALTER TABLE modules ADD COLUMN modu_helptext VARCHAR(10000);
ALTER TABLE modules ADD COLUMN modu_helplink VARCHAR(255);

-- 20170925
ALTER TABLE raccounts ADD COLUMN racc_linked INT;


CREATE TABLE raccountlinks (
	ralk_raccountlinkid INT NOT NULL AUTO_INCREMENT, 
	ralk_raccountid INT NOT NULL,
	ralk_foreignid INT NOT NULL,
	ralk_usercreateid INT,
	ralk_usermodifyid INT,
	ralk_datecreate DATETIME,
	ralk_datemodify DATETIME,
	PRIMARY KEY (ralk_raccountlinkid),
	FOREIGN KEY (ralk_foreignid) REFERENCES raccounts(racc_raccountid)
);
CREATE TABLE sessiontypeextras (
setx_sessiontypeextraid INT NOT NULL AUTO_INCREMENT, 
setx_name VARCHAR(50),
setx_description VARCHAR(500),
setx_price DOUBLE,
setx_fixedprice INT,
setx_startdate DATE,
setx_enddate DATE,
setx_sessiontypeid INT,
setx_usercreateid INT,
setx_usermodifyid INT,
setx_datecreate DATETIME,
setx_datemodify DATETIME,
PRIMARY KEY (setx_sessiontypeextraid),
FOREIGN KEY (setx_sessiontypeid) REFERENCES sessiontypes(sety_sessiontypeid),
FOREIGN KEY (setx_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (setx_usermodifyid) REFERENCES users(user_userid));

CREATE TABLE ordersessionextras (
orsx_ordersessionextraid INT NOT NULL AUTO_INCREMENT, 
orsx_quantity INT,
orsx_price DOUBLE,	
orsx_amount DOUBLE,
orsx_comments VARCHAR(255),
orsx_orderid INT NOT NULL,
orsx_sessiontypeextraid INT,
orsx_usercreateid INT,
orsx_usermodifyid INT,
orsx_datecreate DATETIME,    
   orsx_datemodify DATETIME,
PRIMARY KEY(orsx_ordersessionextraid),
FOREIGN KEY (orsx_orderid) REFERENCES orders(orde_orderid),
FOREIGN KEY (orsx_sessiontypeextraid) REFERENCES sessiontypeextras(setx_sessiontypeextraid),
   FOREIGN KEY (orsx_usercreateid) REFERENCES users(user_userid),
   FOREIGN KEY (orsx_usermodifyid) REFERENCES users(user_userid));

-- Autorización OC, CxP, CxC    
ALTER TABLE requisitions ADD COLUMN reqi_authorizeduser INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_authorizeduser) REFERENCES users (user_userid);
ALTER TABLE requisitions ADD COLUMN reqi_authorizeddate DATETIME;

ALTER TABLE paccounts ADD COLUMN pacc_authorizeduser INT;
ALTER TABLE paccounts ADD FOREIGN KEY (pacc_authorizeduser) REFERENCES users (user_userid);
ALTER TABLE paccounts ADD COLUMN pacc_authorizeddate DATETIME;

ALTER TABLE raccounts ADD COLUMN racc_authorizeduser INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_authorizeduser) REFERENCES users (user_userid);
ALTER TABLE raccounts ADD COLUMN racc_authorizeddate DATETIME;

-- Envio de Correo al conciliar el MB
ALTER TABLE flexconfig ADD COLUMN flxc_sendemailreconciled INT;

ALTER TABLE bankmovements MODIFY COLUMN bkmv_bankreference VARCHAR(30);

ALTER TABLE propertymodels ADD COLUMN ptym_bonusrg DOUBLE;

ALTER TABLE flexconfig ADD COLUMN flxc_sendemailreconciled INT;

-- Autorizar el Envio de Correo al proveedor
ALTER TABLE suppliers ADD COLUMN supl_sendemail INT;

-- Conciliacion en Bancos
ALTER TABLE bankmovements ADD COLUMN bkmv_reconcileduserid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_reconcileduserid) REFERENCES users (user_userid);
ALTER TABLE bankmovements ADD COLUMN bkmv_reconcileddate DATETIME;

ALTER TABLE bankmovements ADD COLUMN bkmv_authorizeuserid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_authorizeuserid) REFERENCES users (user_userid);
ALTER TABLE bankmovements ADD COLUMN bkmv_authorizedate DATETIME;

CREATE TABLE wflowtimetracks (
	wftt_wflowtimetrackid INT NOT NULL AUTO_INCREMENT, 
	wftt_name VARCHAR(30),
	wftt_description VARCHAR(255),	
	wftt_startdate DATETIME,  
	wftt_enddate DATETIME,  
	wftt_minutes INT,
	wftt_userid INT NOT NULL,
    wftt_wflowid INT,
	wftt_wflowstepid INT,
	wftt_usercreateid INT,
	wftt_usermodifyid INT,
	wftt_datecreate DATETIME,    
	wftt_datemodify DATETIME,
	PRIMARY KEY(wftt_wflowtimetrackid),
	FOREIGN KEY (wftt_userid) REFERENCES users(user_userid),
	FOREIGN KEY (wftt_wflowid) REFERENCES wflows(wflw_wflowid),
	FOREIGN KEY (wftt_wflowstepid) REFERENCES wflowsteps(wfsp_wflowstepid),
	FOREIGN KEY (wftt_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (wftt_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE wflowtimetracks ADD COLUMN wftt_wflowid INT;

ALTER TABLE ordertypes ADD COLUMN ortp_emailchangestatusoppo INT;
ALTER TABLE sfcaptions ADD COLUMN sfcp_required INT;

-- 20171011
ALTER TABLE wflowsteps ADD COLUMN wfsp_hours DOUBLE;
ALTER TABLE wflowsteps ADD COLUMN wfsp_billable INT;
ALTER TABLE wflowsteps ADD COLUMN wfsp_rate DOUBLE;
ALTER TABLE wflowusers ADD COLUMN wflu_hours DOUBLE;
ALTER TABLE wflowusers ADD COLUMN wflu_billable INT;
ALTER TABLE wflowusers ADD COLUMN wflu_rate DOUBLE;
ALTER TABLE wflows ADD COLUMN wflw_hours DOUBLE;
ALTER TABLE wflows ADD COLUMN wflw_billable INT;
ALTER TABLE wflows ADD COLUMN wflu_billable INT;
ALTER TABLE wflows ADD COLUMN wfty_billable INT;
ALTER TABLE users ADD COLUMN user_billable INT;
ALTER TABLE users ADD COLUMN user_rate DOUBLE;
ALTER TABLE wflowsteptypes ADD COLUMN wfst_billable INT;
ALTER TABLE wflowsteptypes ADD COLUMN wfst_hours INT;
ALTER TABLE wflowsteptypes ADD COLUMN wfst_rate DOUBLE;

-- Comercial
ALTER TABLE credittypes DROP COLUMN crty_code;
ALTER TABLE losemotives DROP COLUMN lomt_code;
ALTER TABLE socials DROP COLUMN soci_code;
ALTER TABLE referrals DROP COLUMN refe_code;
ALTER TABLE industries DROP COLUMN indu_code;
ALTER TABLE territories DROP COLUMN terr_code;
ALTER TABLE sessiondisciplines DROP COLUMN sedi_code;
ALTER TABLE sessiontypes DROP COLUMN sety_code;
ALTER TABLE sessiontypepackages DROP COLUMN setp_code;

-- Obra
ALTER TABLE conceptgroups DROP COLUMN cpgp_code;
ALTER TABLE conceptheadings DROP COLUMN cphd_code;
ALTER TABLE worktypes DROP COLUMN wkty_code;

-- Operaciones
ALTER TABLE consultingservices DROP COLUMN cose_code;
ALTER TABLE warehouses DROP COLUMN ware_code;
ALTER TABLE whsections DROP COLUMN whse_code;
ALTER TABLE suppliercategories DROP COLUMN spca_code;
ALTER TABLE reqpaytypes DROP COLUMN rqpt_code;
ALTER TABLE requisitiontypes DROP COLUMN rqtp_code;
ALTER TABLE productgroups DROP COLUMN prgp_code;
ALTER TABLE productfamilies DROP COLUMN prfm_code;
ALTER TABLE equipmenttypes DROP COLUMN eqty_code;
ALTER TABLE ordertypes DROP COLUMN ortp_code;
ALTER TABLE ordercommissions DROP COLUMN orcm_code;
ALTER TABLE customerservicetypes DROP COLUMN csty_code;

-- Finanzas
ALTER TABLE paccounttypes DROP COLUMN pact_code;
ALTER TABLE raccounttypes DROP COLUMN ract_code;
ALTER TABLE bankmovtypes DROP COLUMN bkmt_code;
ALTER TABLE bankaccounts DROP COLUMN bkac_code;
ALTER TABLE paymenttypes CHANGE COLUMN payt_code payt_codesat VARCHAR(5) NULL;
ALTER TABLE companies DROP COLUMN comp_code;

-- 20171025
ALTER TABLE ordertypes ADD COLUMN ortp_emailchangestatusoppo INT;

ALTER TABLE ordertypes ADD COLUMN ortp_dispersiongroupid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_dispersiongroupid) REFERENCES groups (prof_profileid);

ALTER TABLE raccountlinks ADD COLUMN ralk_ordercode VARCHAR(10);

ALTER TABLE suppliers ADD COLUMN supl_isenabled INT;
UPDATE suppliers SET supl_isenabled = 1;

ALTER TABLE ordertypes ADD COLUMN ortp_schedulestart INT;
ALTER TABLE ordertypes ADD COLUMN ortp_scheduleend INT;

ALTER TABLE customers ADD COLUMN cust_sendemailreconciled INT;
ALTER TABLE flexconfig ADD COLUMN flxc_emailcustomerbirthday INT;
ALTER TABLE flexconfig ADD COLUMN flxc_authorizedbankmov INT;
ALTER TABLE flexconfig ADD COLUMN flxc_requestauthreqi INT;
ALTER TABLE flexconfig ADD COLUMN flxc_requestauthreqiamount INT;

ALTER TABLE wflowsteps MODIFY COLUMN wfsp_file VARCHAR(512);

ALTER TABLE companies ADD COLUMN comp_deedfile VARCHAR(512);
ALTER TABLE menus ADD COLUMN menu_visible INT;
UPDATE menus SET menu_visible = 1;

ALTER TABLE whsections MODIFY COLUMN whse_name VARCHAR(100);

ALTER TABLE wflowusers ADD COLUMN wflu_commission INT;
ALTER TABLE products ADD COLUMN prod_commission INT;
ALTER TABLE quotes ADD COLUMN quot_commission INT;
ALTER TABLE quoteitems ADD COLUMN qoit_commission INT;
ALTER TABLE orderitems ADD COLUMN ordi_commission INT;

ALTER TABLE users ADD COLUMN user_passwordconf VARCHAR(256);

ALTER TABLE customercontacts ADD COLUMN cuco_contactmain INT;

ALTER TABLE ordertypes ADD COLUMN ortp_enablereqiorderfinish INT;

ALTER TABLE ordertypes ADD COLUMN ortp_enabledeliverysend INT;

-- 20171205

ALTER TABLE projects ADD COLUMN proj_currencyid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_currencyid) REFERENCES currencies (cure_currencyid);
ALTER TABLE projects ADD COLUMN proj_currencyparity DOUBLE;

-- flotis
-- Programas
-- DROP TABLE programsessions;
CREATE TABLE programsessions (
prgs_programsessionid INT NOT NULL AUTO_INCREMENT,
prgs_name VARCHAR(100) NOT NULL,
prgs_description VARCHAR(500),
prgs_usercreateid INT,
prgs_usermodifyid INT,
prgs_datecreate DATETIME,
prgs_datemodify DATETIME,
PRIMARY KEY (prgs_programsessionid),
FOREIGN KEY (prgs_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (prgs_usermodifyid) REFERENCES users(user_userid)
);

-- Niveles de Programas
-- DROP TABLE programsessionlevels;
CREATE TABLE programsessionlevels (
pgsl_programsessionlevelid INT NOT NULL AUTO_INCREMENT,
pgsl_name VARCHAR(100) NOT NULL,
pgsl_description VARCHAR(500),
pgsl_sequence INT NOT NULL,
pgsl_programsessionid INT NOT NULL,
pgsl_usercreateid INT,
pgsl_usermodifyid INT,
pgsl_datecreate DATETIME,
pgsl_datemodify DATETIME,
PRIMARY KEY (pgsl_programsessionlevelid),
FOREIGN KEY (pgsl_programsessionid) REFERENCES programsessions(prgs_programsessionid),
FOREIGN KEY (pgsl_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (pgsl_usermodifyid) REFERENCES users(user_userid)
);

-- Tipos Sub. Nivel de Programas
-- DROP TABLE programsessionsubleveltypes;
CREATE TABLE programsessionsubleveltypes (
pslt_programsessionsubleveltypeid INT NOT NULL AUTO_INCREMENT,
pslt_name VARCHAR(100) NOT NULL,
pslt_description VARCHAR(500),
pslt_sequence INT NOT NULL,
pslt_programsessionlevelid INT NOT NULL,
pslt_usercreateid INT,
pslt_usermodifyid INT,
pslt_datecreate DATETIME,
pslt_datemodify DATETIME,
PRIMARY KEY (pslt_programsessionsubleveltypeid),
FOREIGN KEY (pslt_programsessionlevelid) REFERENCES programsessionlevels(pgsl_programsessionlevelid),
FOREIGN KEY (pslt_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (pslt_usermodifyid) REFERENCES users(user_userid)
);

-- Sub. Nivel de Programas
-- DROP TABLE programsessionsublevels;
CREATE TABLE programsessionsublevels (
pssl_programsessionsublevelid INT NOT NULL AUTO_INCREMENT,
pssl_name VARCHAR(100) NOT NULL,
pssl_description VARCHAR(500),
pssl_sequence INT NOT NULL,
pssl_observation VARCHAR(500),
pssl_progress INT,
pssl_sessionreviewid INT NOT NULL,
pssl_programsessionlevelid INT NOT NULL,
pssl_usercreateid INT,
pssl_usermodifyid INT,
pssl_datecreate DATETIME,
pssl_datemodify DATETIME,
PRIMARY KEY (pssl_programsessionsublevelid),
FOREIGN KEY (pssl_sessionreviewid) REFERENCES sessionreviews(serw_sessionreviewid),
FOREIGN KEY (pssl_programsessionlevelid) REFERENCES programsessionlevels(pgsl_programsessionlevelid),
FOREIGN KEY (pssl_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (pssl_usermodifyid) REFERENCES users(user_userid)
);

-- Evaluación
-- DROP TABLE sessionreviews;
CREATE TABLE sessionreviews (
serw_sessionreviewid INT NOT NULL AUTO_INCREMENT,
serw_datereview DATE,
serw_comments VARCHAR(500),
serw_userid INT NOT NULL,
serw_sessionsaleid INT NOT NULL,
serw_programsessionid INT NOT NULL,
serw_programsessionlevelid INT NOT NULL,
serw_usercreateid INT,
serw_usermodifyid INT,
serw_datecreate DATETIME,
serw_datemodify DATETIME,
PRIMARY KEY (serw_sessionreviewid),
FOREIGN KEY (serw_userid) REFERENCES users(user_userid),
FOREIGN KEY (serw_programsessionid) REFERENCES programsessions(prgs_programsessionid),
FOREIGN KEY (serw_programsessionlevelid) REFERENCES programsessionlevels(pgsl_programsessionlevelid),
FOREIGN KEY (serw_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (serw_usermodifyid) REFERENCES users(user_userid)
);

-- wflowfunnel
CREATE TABLE wflowfunnels (
wflf_wflowfunnelid INT NOT NULL AUTO_INCREMENT,
wflf_name VARCHAR(100) NOT NULL,
wflf_description VARCHAR(500),
wflf_usercreateid INT,
wflf_usermodifyid INT,
wflf_datecreate DATETIME,
wflf_datemodify DATETIME,
PRIMARY KEY (wflf_wflowfunnelid),
FOREIGN KEY (wflf_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (wflf_usermodifyid) REFERENCES users(user_userid)
);
ALTER TABLE wflows ADD COLUMN wflw_wflowfunnelid INT;
ALTER TABLE wflows ADD FOREIGN KEY (wflw_wflowfunnelid) REFERENCES wflowfunnels (wflf_wflowfunnelid);

ALTER TABLE wflowsteps ADD COLUMN wfsp_wflowfunnelid INT;
ALTER TABLE wflowsteps ADD FOREIGN KEY (wfsp_wflowfunnelid) REFERENCES wflowfunnels (wflf_wflowfunnelid);

ALTER TABLE wflowsteptypes ADD COLUMN wfst_wflowfunnelid INT;
ALTER TABLE wflowsteptypes ADD FOREIGN KEY (wfst_wflowfunnelid) REFERENCES wflowfunnels (wflf_wflowfunnelid);

ALTER TABLE sessionreviews ADD FOREIGN KEY (serw_sessionsaleid) REFERENCES sessionsales (sesa_sessionsaleid);



CREATE TABLE cronjobs (
crjb_cronjobid INT NOT NULL AUTO_INCREMENT,
crjb_name VARCHAR(100) NOT NULL,
crjb_description VARCHAR(500),
crjb_classname VARCHAR(256),
crjb_usercreateid INT,
crjb_usermodifyid INT,
crjb_datecreate DATETIME,
crjb_datemodify DATETIME,
PRIMARY KEY (crjb_cronjobid)
);

ALTER TABLE products ADD COLUMN prod_renewrate DOUBLE;

ALTER TABLE orders ADD COLUMN orde_originreneworderid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_originreneworderid) REFERENCES orders(orde_orderid);

ALTER TABLE raccounts CHANGE COLUMN racc_folio racc_reference VARCHAR(30);
ALTER TABLE raccounts ADD COLUMN racc_serie VARCHAR(30);
ALTER TABLE raccounts ADD COLUMN racc_folio INT;

ALTER TABLE programprofiles ADD COLUMN pfus_defaultuser INT;

ALTER TABLE raccounts ADD COLUMN racc_comments VARCHAR(1024);
ALTER TABLE raccounts ADD COLUMN racc_commentlog TEXT;
ALTER TABLE bankmovements ADD COLUMN bkmv_cancelleddate DATETIME;
ALTER TABLE bankmovements ADD COLUMN bkmv_cancelleduserid INT;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_cancelleduserid) REFERENCES users (user_userid);

CREATE TABLE cronlogs (
	crlg_cronlogid INT NOT NULL AUTO_INCREMENT,
	crlg_startdate DATETIME,
	crlg_enddate DATETIME,
	crlg_log VARCHAR(10000),
	crlg_success INT,
	crlg_usercreateid INT,
	crlg_usermodifyid INT,
	crlg_datecreate DATETIME,
	crlg_datemodify DATETIME,
	PRIMARY KEY (crlg_cronlogid)
);

-- 20180207

ALTER TABLE customeraddress MODIFY COLUMN cuad_zip VARCHAR(7);
ALTER TABLE supplieraddress MODIFY COLUMN suad_zip VARCHAR(7);
ALTER TABLE developments MODIFY COLUMN deve_zip VARCHAR(7);

ALTER TABLE properties ADD COLUMN prty_companyid INT;
ALTER TABLE properties ADD FOREIGN KEY (prty_companyid) REFERENCES companies(comp_companyid);

-- 2018feb13
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultordertypeid) REFERENCES ordertypes(ortp_ordertypeid);
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultbankaccountid) REFERENCES bankaccounts(bkac_bankaccountid);
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_salesgroupid) REFERENCES groups(prof_profileid);
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_defaultsalesman) REFERENCES users(user_userid);

ALTER TABLE flexconfig ADD COLUMN flxc_collectgroupid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_collectgroupid) REFERENCES groups(prof_profileid);


CREATE TABLE markets (
	mrkt_marketid INT NOT NULL AUTO_INCREMENT,
	mrkt_name VARCHAR(50),
	mrkt_description VARCHAR(255),
	mrkt_usercreateid INT,
	mrkt_usermodifyid INT,
	mrkt_datecreate DATETIME,
	mrkt_datemodify DATETIME,
	PRIMARY KEY (mrkt_marketid)
);



CREATE TABLE productprices (
	prpc_productpriceid INT NOT NULL AUTO_INCREMENT, 
	prpc_price DOUBLE,
	prpc_startdate DATE,
	prpc_productid INT NOT NULL,
	prpc_currencyid INT NOT NULL,
	prpc_ordertypeid INT,
	prpc_marketid INT,
	prpc_companyid INT,
	prpc_usercreateid INT,
	prpc_usermodifyid INT,
	prpc_datecreate DATETIME,
	prpc_datemodify DATETIME,
	PRIMARY KEY (prpc_productpriceid),
	FOREIGN KEY (prpc_productid) REFERENCES products(prod_productid),
	FOREIGN KEY (prpc_currencyid) REFERENCES currencies(cure_currencyid),
	FOREIGN KEY (prpc_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
	FOREIGN KEY (prpc_marketid) REFERENCES markets(mrkt_marketid),
	FOREIGN KEY (prpc_companyid) REFERENCES companies(comp_companyid),
	FOREIGN KEY (prpc_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (prpc_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE opportunities ADD COLUMN oppo_marketid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_marketid) REFERENCES markets(mrkt_marketid);

ALTER TABLE quotes ADD COLUMN quot_marketid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_marketid) REFERENCES markets(mrkt_marketid);

ALTER TABLE orders ADD COLUMN orde_marketid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_marketid) REFERENCES markets(mrkt_marketid);

ALTER TABLE ordertypes ADD COLUMN ortp_hastaxes INT;

ALTER TABLE orders ADD COLUMN orde_willrenew INT;

ALTER TABLE raccounts ADD COLUMN racc_reminddate DATE;

ALTER TABLE ordertypes DROP COLUMN ortp_code;

ALTER TABLE raccountlinks ADD ralk_orderid INT;
ALTER TABLE raccountlinks ADD FOREIGN KEY (ralk_orderid) REFERENCES orders(orde_orderid);

ALTER TABLE raccountassignments ADD FOREIGN KEY (rass_foreignraccountid) REFERENCES raccounts(racc_raccountid);

ALTER TABLE raccounts ADD COLUMN racc_collectuserid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_collectuserid) REFERENCES users(user_userid);

ALTER TABLE orders ADD COLUMN orde_totalraccounts DOUBLE;
ALTER TABLE orders ADD COLUMN orde_totalcreditnotes DOUBLE;
ALTER TABLE orders ADD COLUMN orde_payments DOUBLE;
ALTER TABLE orders ADD COLUMN orde_balance DOUBLE;

update orders set orde_totalcreditnotes = 0;
update orders set orde_totalraccounts = 0;
update orders set orde_payments = 0;
update orders set orde_balance = 0;

ALTER TABLE products MODIFY COLUMN prod_name VARCHAR(50);
ALTER TABLE productkits MODIFY COLUMN prkt_name VARCHAR(50);
ALTER TABLE productkits MODIFY COLUMN prkt_description VARCHAR(512);

-- una vez que se actualice a quitar estatus de pago parciales:
UPDATE raccounts SET racc_paymentstatus = 'P' WHERE racc_paymentstatus = 'R';
UPDATE paccounts SET pacc_paymentstatus = 'P' WHERE pacc_paymentstatus = 'R';
UPDATE orders SET orde_paymentstatus = 'P' WHERE orde_paymentstatus = 'R';
UPDATE requisitions SET reqi_paymentstatus = 'P' WHERE reqi_paymentstatus = 'R';
UPDATE contractestimations SET coes_paymentstatus = 'P' WHERE coes_paymentstatus = 'W';

ALTER TABLE products MODIFY COLUMN prod_name VARCHAR(60);
ALTER TABLE whboxes MODIFY COLUMN whbx_name VARCHAR(100);


ALTER TABLE ordertypes ADD COLUMN ortp_defaultbudgetitemid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_defaultbudgetitemid) REFERENCES budgetitems(bgit_budgetitemid);

ALTER TABLE projects ADD COLUMN proj_marketid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_marketid) REFERENCES markets(mrkt_marketid);

-- actualiacion subida al archivo 15 mayo 2018
ALTER TABLE ordertypes ADD COLUMN ortp_requiredlosecomments INT;


ALTER TABLE opportunities ADD COLUMN oppo_venueid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_venueid) REFERENCES venues(venu_venueid);

ALTER TABLE opportunities ADD COLUMN oppo_customeraddressid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_customeraddressid) REFERENCES customeraddress(cuad_customeraddressid);


ALTER TABLE raccounts ADD COLUMN racc_duedatestart DATE;
ALTER TABLE raccounts ADD COLUMN racc_reqpaytypeid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_reqpaytypeid) REFERENCES reqpaytypes(rqpt_reqpaytypeid);


-- 2018-05-10
ALTER TABLE paccounts ADD COLUMN pacc_areaid INT;
ALTER TABLE paccounts ADD FOREIGN KEY (pacc_areaid) REFERENCES areas (area_areaid);
ALTER TABLE paccountitems ADD COLUMN pait_budgetitemid INT;
ALTER TABLE paccountitems ADD FOREIGN KEY (pait_budgetitemid) REFERENCES budgetitems (bgit_budgetitemid);
ALTER TABLE paccountitems ADD COLUMN pait_areaid INT;
ALTER TABLE paccountitems ADD FOREIGN KEY (pait_areaid) REFERENCES areas (area_areaid);

ALTER TABLE raccounts ADD COLUMN racc_areaid INT;
ALTER TABLE raccounts ADD FOREIGN KEY (racc_areaid) REFERENCES areas (area_areaid);
ALTER TABLE raccountitems ADD COLUMN rait_budgetitemid INT;
ALTER TABLE raccountitems ADD FOREIGN KEY (rait_budgetitemid) REFERENCES budgetitems (bgit_budgetitemid);
ALTER TABLE raccountitems ADD COLUMN rait_areaid INT;
ALTER TABLE raccountitems ADD FOREIGN KEY (rait_areaid) REFERENCES areas (area_areaid);

-- 2018-05-18
ALTER TABLE requisitiontypes ADD COLUMN rqtp_enablesend INT;


-- 2018-05-28
CREATE TABLE arearates (
	arrt_arearateid INT NOT NULL AUTO_INCREMENT, 
	arrt_parentid INT NOT NULL,
	arrt_childid INT NOT NULL,
	arrt_rate DOUBLE,
	arrt_usercreateid INT,
	arrt_usermodifyid INT,
	arrt_datecreate DATETIME,
	arrt_datemodify DATETIME,
	PRIMARY KEY (arrt_arearateid),
	FOREIGN KEY (arrt_parentid) REFERENCES areas(area_areaid),
	FOREIGN KEY (arrt_childid) REFERENCES areas(area_areaid)
);

ALTER TABLE areas ADD COLUMN area_type char(1);

CREATE TABLE `activities` (
  `acti_activityid` int(11) NOT NULL AUTO_INCREMENT,
  `acti_code` varchar(10) DEFAULT NULL,
  `acti_name` varchar(50) DEFAULT NULL,
  `acti_description` varchar(255) DEFAULT NULL,
  `acti_startdate` datetime DEFAULT NULL,
  `acti_enddate` datetime DEFAULT NULL,
  `acti_status` char(1) DEFAULT NULL,
  `acti_wflowtypeid` int(11) DEFAULT NULL,
  `acti_wflowid` int(11) DEFAULT NULL,
  `acti_userid` int(11) DEFAULT NULL,
  `acti_usercreateid` int(11) DEFAULT NULL,
  `acti_usermodifyid` int(11) DEFAULT NULL,
  `acti_datecreate` datetime DEFAULT NULL,
  `acti_datemodify` datetime DEFAULT NULL,
  PRIMARY KEY (`acti_activityid`),
  KEY `acti_userid` (`acti_userid`),
  KEY `acti_wflowtypeid` (`acti_wflowtypeid`),
  KEY `acti_wflowid` (`acti_wflowid`),
  KEY `acti_usercreateid` (`acti_usercreateid`),
  KEY `acti_usermodifyid` (`acti_usermodifyid`),
  CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`acti_userid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `activities_ibfk_2` FOREIGN KEY (`acti_wflowtypeid`) REFERENCES `wflowtypes` (`wfty_wflowtypeid`),
  CONSTRAINT `activities_ibfk_3` FOREIGN KEY (`acti_wflowid`) REFERENCES `wflows` (`wflw_wflowid`),
  CONSTRAINT `activities_ibfk_4` FOREIGN KEY (`acti_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `activities_ibfk_5` FOREIGN KEY (`acti_usermodifyid`) REFERENCES `users` (`user_userid`)
);


CREATE TABLE orderdetails (
	ordt_orderdetailid INT NOT NULL AUTO_INCREMENT, 
	ordt_status char(1),
	ordt_closedate DATE,
	ordt_orderdate DATE,
	ordt_desiredate DATE,
	ordt_startdate DATE,
	ordt_deliverydate DATE,
	ordt_leaderuserid INT,
	ordt_assigneduserid INT,
	ordt_areaid INT,
	ordt_orderid INT NOT NULL,
	ordt_usercreateid INT,
	ordt_usermodifyid INT,
	ordt_datecreate DATETIME,
	ordt_datemodify DATETIME,
	PRIMARY KEY (ordt_orderdetailid),
	FOREIGN KEY (ordt_leaderuserid) REFERENCES users(user_userid),
	FOREIGN KEY (ordt_assigneduserid) REFERENCES users(user_userid),
	FOREIGN KEY (ordt_orderid) REFERENCES orders(orde_orderid),
	FOREIGN KEY (ordt_areaid) REFERENCES areas(area_areaid)
);


ALTER TABLE budgets ADD COLUMN budg_currencyid INT;
ALTER TABLE budgets ADD FOREIGN KEY (budg_currencyid) REFERENCES currencies(cure_currencyid);
ALTER TABLE budgets ADD COLUMN budg_currencyparity INT;

ALTER TABLE budgetitems ADD COLUMN bgit_currencyid INT;
ALTER TABLE budgetitems ADD FOREIGN KEY (bgit_currencyid) REFERENCES currencies(cure_currencyid);
ALTER TABLE budgetitems ADD COLUMN bgit_currencyparity VARCHAR(10);

ALTER TABLE budgets MODIFY COLUMN budg_code VARCHAR(10) null;



-- 2018-05-28
ALTER TABLE budgets ADD COLUMN budg_currencyid INT;
ALTER TABLE budgets ADD FOREIGN KEY (budg_currencyid) REFERENCES currencies(cure_currencyid);
ALTER TABLE budgets ADD COLUMN budg_currencyparity DOUBLE;

ALTER TABLE budgetitems ADD COLUMN bgit_currencyid INT;
ALTER TABLE budgetitems ADD FOREIGN KEY (bgit_currencyid) REFERENCES currencies(cure_currencyid);
ALTER TABLE budgetitems ADD COLUMN bgit_currencyparity DOUBLE;

-- 2018-05-31
ALTER TABLE ordertypes ADD COLUMN ortp_defaultareaid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_defaultareaid) REFERENCES areas(area_areaid);

ALTER TABLE orders ADD COLUMN orde_defaultbudgetitemid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_defaultbudgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE orders ADD COLUMN orde_defaultareaid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_defaultareaid) REFERENCES areas(area_areaid);


ALTER TABLE wflowsteps ADD COLUMN wfsp_filelink VARCHAR(500);

-- 2018-06-04
ALTER TABLE requisitionitems ADD COLUMN  rqit_budgetitemid INT;
ALTER TABLE requisitionitems ADD FOREIGN KEY (rqit_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE requisitionitems ADD COLUMN rqit_areaid INT;
ALTER TABLE requisitionitems ADD FOREIGN KEY (rqit_areaid) REFERENCES areas(area_areaid);

-- 2018-06-07
ALTER TABLE requisitionreceipts ADD COLUMN rerc_areaid INT;
ALTER TABLE requisitionreceipts ADD FOREIGN KEY (rerc_areaid) REFERENCES areas(area_areaid);

ALTER TABLE requisitionreceiptitems ADD COLUMN  reit_budgetitemid INT;
ALTER TABLE requisitionreceiptitems ADD FOREIGN KEY (reit_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE requisitionreceiptitems ADD COLUMN reit_areaid INT;
ALTER TABLE requisitionreceiptitems ADD FOREIGN KEY (reit_areaid) REFERENCES areas(area_areaid);

-- 2018-06-12
ALTER TABLE requisitionreceiptitems MODIFY COLUMN reit_days DOUBLE;
-- 2018-06-15
ALTER TABLE products ADD COLUMN  prod_budgetitemid INT;
ALTER TABLE products ADD FOREIGN KEY (prod_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE products ADD COLUMN prod_areaid INT;
ALTER TABLE products ADD FOREIGN KEY (prod_areaid) REFERENCES areas(area_areaid);

ALTER TABLE opportunities ADD COLUMN  oppo_budgetitemid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE opportunities ADD COLUMN oppo_areaid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_areaid) REFERENCES areas(area_areaid);

ALTER TABLE quotes ADD COLUMN  quot_budgetitemid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE quotes ADD COLUMN quot_areaid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_areaid) REFERENCES areas(area_areaid);

ALTER TABLE quoteitems ADD COLUMN  qoit_budgetitemid INT;
ALTER TABLE quoteitems ADD FOREIGN KEY (qoit_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE quoteitems ADD COLUMN qoit_areaid INT;
ALTER TABLE quoteitems ADD FOREIGN KEY (qoit_areaid) REFERENCES areas(area_areaid);

ALTER TABLE orderitems ADD COLUMN  ordi_budgetitemid INT;
ALTER TABLE orderitems ADD FOREIGN KEY (ordi_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE orderitems ADD COLUMN ordi_areaid INT;
ALTER TABLE orderitems ADD FOREIGN KEY (ordi_areaid) REFERENCES areas(area_areaid);

-- 2018-06-22
ALTER TABLE orderitems ADD COLUMN ordi_orderitemcomposedid INT;
ALTER TABLE orderitems ADD FOREIGN KEY (ordi_orderitemcomposedid) REFERENCES orderitems(ordi_orderitemid);

CREATE TABLE productlinks (
prlk_productlinkid INT NOT NULL AUTO_INCREMENT,
prlk_productlinkedid INT NOT NULL,
prlk_productid INT NOT NULL,
prlk_usercreateid INT,
prlk_usermodifyid INT,
prlk_datecreate DATETIME,
prlk_datemodify DATETIME,
PRIMARY KEY (prlk_productlinkid),
FOREIGN KEY (prlk_productid) REFERENCES products(prod_productid),
FOREIGN KEY (prlk_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (prlk_usermodifyid) REFERENCES users(user_userid)
);

-- 2018-06-22
ALTER TABLE ordertypes ADD COLUMN ortp_wflowtypeid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);
ALTER TABLE ordertypes ADD COLUMN ortp_enableextraorder INT;

ALTER TABLE sessionsales ADD COLUMN sesa_maxsessions INT;

ALTER TABLE ordersessions ADD COLUMN orss_type CHAR;

-- 2018-07-04
ALTER TABLE productcompanies ADD COLUMN  prcp_budgetitemid INT;
ALTER TABLE productcompanies ADD FOREIGN KEY (prcp_budgetitemid) REFERENCES budgetitems(bgit_budgetitemid);
ALTER TABLE productcompanies ADD COLUMN prcp_areaid INT;
ALTER TABLE productcompanies ADD FOREIGN KEY (prcp_areaid) REFERENCES areas(area_areaid);

-- 2018-07-10
CREATE TABLE ordertypegroups (
ortg_ordertypegroupid INT NOT NULL AUTO_INCREMENT,
ortg_ordertypeid INT NOT NULL,
ortg_profileid INT NOT NULL,
ortg_usercreateid INT,
ortg_usermodifyid INT,
ortg_datecreate DATETIME,
ortg_datemodify DATETIME,
PRIMARY KEY (ortg_ordertypegroupid),
FOREIGN KEY (ortg_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
FOREIGN KEY (ortg_profileid) REFERENCES groups(prof_profileid),
FOREIGN KEY (ortg_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (ortg_usermodifyid) REFERENCES users(user_userid)
);

-- 2018-07-25
ALTER TABLE propertytypes ADD COLUMN ptyp_ordertypeid INT;
ALTER TABLE properties modify prty_developmentblockid INT NULL;
ALTER TABLE properties add column prty_neighborhood varchar(60);
ALTER TABLE properties add COLUMN prty_zip VARCHAR(7);
ALTER TABLE properties add column prty_cityid INT;
ALTER TABLE properties ADD FOREIGN KEY (prty_cityid) REFERENCES cities(city_cityid);
ALTER TABLE properties add column prty_betweenstreets varchar(200);

ALTER TABLE properties ADD COLUMN prty_contract VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_propertyreceipt VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_propertytitle VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_constitutiveact VARCHAR(255);


ALTER TABLE orders ADD COLUMN orde_propertyid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_propertyid) REFERENCES properties(prty_propertyid);
ALTER TABLE orders ADD COLUMN orde_deadline INT;

-- Creacion de tabla predial PRTX
CREATE TABLE propertytax (
	prtx_propertytaxid INT NOT NULL AUTO_INCREMENT, 
	prtx_accountno VARCHAR(30) NOT NULL,
	prtx_description varchar(500),
    prtx_propertyid INT NOT NULL,
    prtx_usercreateid INT,
	prtx_usermodifyid INT,
	prtx_datecreate DATETIME,
	prtx_datemodify DATETIME,
    PRIMARY KEY(prtx_propertytaxid),
    FOREIGN KEY (prtx_propertyid) REFERENCES properties(prty_propertyid),
    FOREIGN KEY (prtx_usercreateid) REFERENCES users(user_userid),
    FOREIGN KEY (prtx_usermodifyid) REFERENCES users(user_userid));
    
-- Creacion de tabla arrendamiento pedido ORPT
CREATE TABLE orderpropertiestax (
	orpt_orderpropertytaxid INT NOT NULL AUTO_INCREMENT, 
	orpt_price DOUBLE,
	orpt_amount DOUBLE,
    orpt_propertyid INT NOT NULL,
    orpt_orderid INT NOT NULL,
    orpt_usercreateid INT,
	orpt_usermodifyid INT,
	orpt_datecreate DATETIME,
	orpt_datemodify DATETIME,
    PRIMARY KEY(orpt_orderpropertytaxid),
    FOREIGN KEY (orpt_propertyid) REFERENCES properties(prty_propertyid),
    FOREIGN KEY (orpt_orderid) REFERENCES orders(orde_orderid),
    FOREIGN KEY (orpt_usercreateid) REFERENCES users(user_userid),
    FOREIGN KEY (orpt_usermodifyid) REFERENCES users(user_userid)
);



-- 30 jul 18

-- Creacion de tabla Plazo de Contrato
CREATE TABLE contractterms (
	crtr_contracttermid INT NOT NULL AUTO_INCREMENT, 
    crtr_name varchar(30),
	crtr_startdate DATETIME,
	crtr_enddate DATETIME,
    crtr_deadline INT,
    crtr_usercreateid INT,
	crtr_usermodifyid INT,
	crtr_datecreate DATETIME,
	crtr_datemodify DATETIME,
    PRIMARY KEY(crtr_contracttermid),
    FOREIGN KEY (crtr_usercreateid) REFERENCES users(user_userid),
    FOREIGN KEY (crtr_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE orders DROP COLUMN orde_deadline;
ALTER TABLE orders ADD COLUMN orde_contracttermid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_contracttermid) REFERENCES contractterms (crtr_contracttermid);
 
ALTER TABLE ordersessions add column orss_description VARCHAR(512);

-- 13/Ago/2018
ALTER TABLE properties ADD COLUMN prty_reneworder INT; 
ALTER TABLE properties ADD COLUMN prty_renewrate DOUBLE;

-- 16/Ago/2018
ALTER TABLE activities ADD COLUMN acti_emailreminders INT;
ALTER TABLE activities ADD COLUMN acti_reminddate DATE;

-- 17/Ago/2018
ALTER TABLE properties ADD COLUMN prty_customerid INT;
ALTER TABLE properties ADD FOREIGN KEY (prty_customerid) REFERENCES customers (cust_customerid);

-- 20/Ago/2018
ALTER TABLE customers ADD COLUMN cust_customercategory CHAR;

ALTER TABLE orders DROP FOREIGN KEY orders_ibfk_26;
ALTER TABLE orders DROP COLUMN orde_contracttermid;
ALTER TABLE orders ADD COLUMN orde_contractterm INT;

-- 21/Ago/2018
ALTER TABLE orders ADD COLUMN orde_initialIconme DOUBLE;
ALTER TABLE orders ADD COLUMN orde_currentIncome DOUBLE;

-- 22/ago/18
ALTER TABLE orders ADD COLUMN orde_rentalscheduledate DATE;

-- 27/Ago/2018

--
-- Table structure for table `propertiesrent`
--


CREATE TABLE propertiesrent (
  `prrt_propertiesrentid` int(11) NOT NULL AUTO_INCREMENT,
  `prrt_status` char(1) NOT NULL,
  `prrt_code` varchar(10) DEFAULT NULL,
  `prrt_name` varchar(100) DEFAULT NULL,
  `prrt_description` varchar(500) DEFAULT NULL,
  `prrt_startdate` datetime DEFAULT NULL,
  `prrt_enddate` datetime DEFAULT NULL,
  `prrt_ordertypeid` INT DEFAULT NULL,
  `prrt_orderid` INT DEFAULT NULL,
  `prrt_companyid` int(11) DEFAULT NULL,
  `prrt_marketid` int(11) DEFAULT NULL,
  `prrt_currencyid` int(11) DEFAULT NULL,
  `prrt_currencyparity` double DEFAULT NULL,
  `prrt_tags` VARCHAR(255),
  `prrt_userid` int(11) DEFAULT NULL,
  `prrt_wflowid` int(11) DEFAULT NULL,
  `prrt_customercontactid` INT,
   prrt_propertyid INT,
   prrt_contractterm INT,
   prrt_initialIconme DOUBLE,
   prrt_currentIncome DOUBLE,
   prrt_rentalscheduledate DATE,
  `prrt_wflowtypeid` int(11) DEFAULT NULL,
  `prrt_customerid` int(11) NOT NULL,
  `prrt_usercreateid` int(11) DEFAULT NULL,
  `prrt_usermodifyid` int(11) DEFAULT NULL,
  `prrt_datecreate` datetime DEFAULT NULL,
  `prrt_datemodify` datetime DEFAULT NULL,
  
   PRIMARY KEY (`prrt_propertiesrentid`),
   FOREIGN KEY (prrt_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
   FOREIGN KEY (prrt_orderid) REFERENCES orders(orde_orderid),
   FOREIGN KEY (prrt_customercontactid) REFERENCES customercontacts (cuco_customercontactid),
   FOREIGN KEY (prrt_propertyid) REFERENCES properties(prty_propertyid),
   FOREIGN KEY (`prrt_customerid`) REFERENCES `customers` (`cust_customerid`),
   FOREIGN KEY (`prrt_companyid`) REFERENCES `companies` (`comp_companyid`),
   FOREIGN KEY (`prrt_usercreateid`) REFERENCES `users` (`user_userid`),
   FOREIGN KEY (`prrt_usermodifyid`) REFERENCES `users` (`user_userid`),
   FOREIGN KEY (`prrt_currencyid`) REFERENCES `currencies` (`cure_currencyid`),
   FOREIGN KEY (`prrt_marketid`) REFERENCES `markets` (`mrkt_marketid`),
   FOREIGN KEY (`prrt_userid`) REFERENCES `users` (`user_userid`),
   FOREIGN KEY (`prrt_wflowid`) REFERENCES `wflows` (`wflw_wflowid`),
   FOREIGN KEY (`prrt_wflowtypeid`) REFERENCES `wflowtypes` (`wfty_wflowtypeid`),
   FOREIGN KEY (`prrt_orderid`) REFERENCES `orders` (`orde_orderid`)
); 


ALTER TABLE customers ADD COLUMN cust_paymentdate DATE;
ALTER TABLE propertiesrent MODIFY prrt_startdate DATE;
ALTER TABLE propertiesrent MODIFY prrt_enddate DATE;

-- 28/Ago/2018
ALTER TABLE propertiesrent ADD COLUMN prrt_rentincrease DATE;

-- 03/Sep/2018
ALTER TABLE requisitions ADD COLUMN reqi_propertyid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_propertyid) REFERENCES properties(prty_propertyid);

-- 06/sep/2018
ALTER TABLE bankaccounts ADD COLUMN bkac_bankrfc VARCHAR(13);

-- 13/sep/2018
CREATE TABLE ratetypes (
	raty_ratetypeid INT NOT NULL AUTO_INCREMENT, 
    raty_name VARCHAR(30) NOT NULL,
	raty_description VARCHAR(512),
    raty_usercreateid INT,
	raty_usermodifyid INT,
	raty_datecreate DATETIME,
	raty_datemodify DATETIME,
    PRIMARY KEY(raty_ratetypeid),
    FOREIGN KEY (raty_usercreateid) REFERENCES users(user_userid),
    FOREIGN KEY (raty_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE customers ADD COLUMN cust_ratetypeid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_ratetypeid) REFERENCES ratetypes(raty_ratetypeid);
ALTER TABLE customers ADD COLUMN cust_rate DOUBLE;

-- 19/sep/2018
CREATE TABLE externalsales(exts_externalsalesid INT(11) NOT NULL AUTO_INCREMENT,
  exts_code varchar(10) DEFAULT NULL,
  exts_customerid INT(11) NULL,
  exts_extsreference VARCHAR(30) NULL,
  exts_date DATE NULL,
  exts_quantity DOUBLE NULL,
  exts_price DOUBLE NULL,
  exts_total DOUBLE NULL,
  exts_productid INT(11) NULL,
  exts_usercreateid INT,
  exts_usermodifyid INT,
  exts_datecreate DATETIME,
  exts_datemodify DATETIME,
  PRIMARY KEY (exts_externalsalesid),
  FOREIGN KEY (exts_customerid) REFERENCES customers(cust_customerid),
  FOREIGN KEY (exts_usercreateid) REFERENCES users(user_userid),
  FOREIGN KEY (exts_usermodifyid) REFERENCES users(user_userid));
 
-- 21/sep/2018
ALTER TABLE customers ADD COLUMN cust_wflowtypeid INT(11) NULL;
ALTER TABLE customers ADD FOREIGN KEY (cust_wflowtypeid) REFERENCES wflowtypes (wfty_wflowtypeid);
ALTER TABLE customers ADD COLUMN cust_wflowid INT(11) NULL;
ALTER TABLE customers ADD FOREIGN KEY (cust_wflowid) REFERENCES wflows (wflw_wflowid);

-- 26/sep/2018
CREATE TABLE customerstatus (
	csta_customerstatusid INT NOT NULL AUTO_INCREMENT, 
    csta_customerid INT NOT NULL,
    csta_status CHAR,
    csta_companyid INT, 
    csta_usercreateid INT,
	csta_usermodifyid INT,
	csta_datecreate DATETIME,
	csta_datemodify DATETIME,
    PRIMARY KEY(csta_customerstatusid),
    FOREIGN KEY (csta_customerid) REFERENCES customers(cust_customerid),
    FOREIGN KEY (csta_companyid) REFERENCES companies(comp_companyid)
    );

ALTER TABLE sessions ADD COLUMN sess_companyid INT;
ALTER TABLE sessions ADD FOREIGN KEY (sess_companyid) REFERENCES companies(comp_companyid);

ALTER TABLE bankmovements ADD COLUMN bkmv_paymenttypeid int;
ALTER TABLE bankmovements ADD FOREIGN KEY (bkmv_paymenttypeid) REFERENCES paymenttypes(payt_paymenttypeid);

-- 09/oct/2018
ALTER TABLE wflowtypes ADD COLUMN wfty_status CHAR; 
ALTER TABLE wflowtypes MODIFY COLUMN wfty_name VARCHAR(50);
ALTER TABLE wflowcategories MODIFY COLUMN wfca_name VARCHAR(50);
ALTER TABLE wflowsteps MODIFY COLUMN wfsp_name VARCHAR(50);

-- 10/oct/2018
ALTER TABLE wflowcategories ADD COLUMN wfca_status CHAR; 

ALTER TABLE wflows ADD COLUMN wflw_companyid INT;
ALTER TABLE wflows ADD FOREIGN KEY (wflw_companyid) REFERENCES companies(comp_companyid);
ALTER TABLE wflowtypes ADD COLUMN wfty_companyid INT;
ALTER TABLE wflowtypes ADD FOREIGN KEY (wfty_companyid) REFERENCES companies(comp_companyid);

-- Pasar empresa del flujo correspondiente
UPDATE wflows
LEFT JOIN opportunities ON (oppo_wflowid = wflw_wflowid)
SET wflw_companyid = oppo_companyid 
WHERE wflw_callercode = 'OPPO'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN orders ON (orde_wflowid = wflw_wflowid)
SET wflw_companyid =  orde_companyid 
WHERE wflw_callercode = 'ORDE'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN propertysales ON (prsa_wflowid = wflw_wflowid)
SET wflw_companyid = prsa_companyid 
WHERE wflw_callercode = 'PRSA'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN projects ON (proj_wflowid = wflw_wflowid)
SET wflw_companyid = proj_companyid 
WHERE wflw_callercode = 'PROJ'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN sessionsales ON (sesa_wflowid = wflw_wflowid)
SET wflw_companyid = sesa_companyid 
WHERE wflw_callercode = 'SESA'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN credits ON (cred_wflowid = wflw_wflowid)
SET wflw_companyid = cred_companyid 
WHERE wflw_callercode = 'CRED'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN propertiesrent ON (prrt_wflowid = wflw_wflowid)
SET wflw_companyid = prrt_companyid 
WHERE wflw_callercode = 'PRRT'
AND wflw_companyid IS NULL;

UPDATE wflows
LEFT JOIN developmentphases ON (dvph_wflowid = wflw_wflowid)
SET wflw_companyid  = dvph_companyid
WHERE wflw_callercode = 'DVPH'
AND wflw_companyid IS NULL;

-- 17/oct/2018
ALTER TABLE propertiesrent ADD COLUMN prrt_ownerproperty VARCHAR(200); 

-- 18/oct/2018
ALTER TABLE customers
ADD COLUMN cust_user VARCHAR(10) NULL ,
ADD COLUMN cust_passw VARCHAR(10) NULL;

-- 18/oct/2018
ALTER TABLE customers
CHANGE COLUMN `cust_passw` `cust_passw` VARCHAR(255) NULL DEFAULT NULL ;

-- 22/oct/2018
ALTER TABLE ordertypes ADD COLUMN ortp_filteronscrum INT;


ALTER TABLE sfconfig ADD COLUMN sfcf_maxrecords INT;

ALTER TABLE companies ADD COLUMN comp_fiscalstartmonth INT;
ALTER TABLE companies ADD COLUMN comp_fiscalperiodtype CHAR;


-- 23/oct/2018
ALTER TABLE properties ADD COLUMN prty_certifiedwriting VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_demarcation VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_notaryquotation VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_appraise VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_debtcertificate VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_taxcertificate VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_waterbill VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_electricitybill VARCHAR(255);
ALTER TABLE properties ADD COLUMN prty_otherdocuments VARCHAR(255);

-- 6/nov/2018
ALTER TABLE ordertypes ADD COLUMN ortp_reminddaysbeforeendordertwo INT;
ALTER TABLE ordertypes ADD COLUMN ortp_reminddaysbeforerentincrease INT;
ALTER TABLE ordertypes ADD COLUMN ortp_reminddaysbeforerentincreasetwo INT;

--7/Nov/2018
ALTER TABLE bankmovements
ADD COLUMN bkmv_comments VARCHAR(1024) NULL ;
ALTER TABLE bankmovements 
ADD COLUMN bkmv_commentlog TEXT NULL ;

-- 07/nov/2018
ALTER TABLE opportunities ADD COLUMN oppo_fiscalyear INT;
ALTER TABLE opportunities ADD COLUMN oppo_fiscalperiod VARCHAR(2);

-- 16/nov/2018
ALTER TABLE wflowlogs ADD COLUMN wflg_customerid INT;
ALTER TABLE wflowlogs ADD FOREIGN KEY (wflg_customerid) REFERENCES customers (cust_customerid);

-- 22/nov/2018
ALTER TABLE raccounts ADD COLUMN racc_file INT(11) NULL;
ALTER TABLE paccounts ADD COLUMN pacc_file INT(11) NULL;
ALTER TABLE requisitions ADD COLUMN reqi_file INT(11) NULL;

-- 23/nov/2018
ALTER TABLE orderpropertiestax ADD COLUMN orpt_quantity INT;

-- 29/nov/19
ALTER TABLE bankmovements MODIFY COLUMN bkmv_description VARCHAR(500);

-- 5/dic/2018
ALTER TABLE customers DROP COLUMN cust_wflowid;
ALTER TABLE customers DROP COLUMN cust_wflowtypeid;
ALTER TABLE wflowlogs DROP COLUMN wflg_customerid;
ALTER TABLE wflows ADD FOREIGN KEY (wflw_customerid) REFERENCES customers(cust_customerid);

-- Colocar cliente el flujos
UPDATE wflows
LEFT JOIN opportunities ON(oppo_wflowid = wflw_wflowid)
SET wflw_customerid = oppo_customerid
WHERE wflw_callercode = 'OPPO';
 
UPDATE wflows
LEFT JOIN orders on (orde_wflowid = wflw_wflowid)
SET wflw_customerid = orde_customerid
WHERE wflw_callercode = 'ORDE';

UPDATE wflows 
LEFT JOIN projects on (proj_wflowid = wflw_wflowid)
SET wflw_customerid = proj_customerid
WHERE wflw_callercode = 'PROJ';

UPDATE wflows 
LEFT JOIN propertysales on (prsa_wflowid = wflw_wflowid)
SET wflw_customerid = prsa_customerid
WHERE wflw_callercode = 'PRSA';

UPDATE wflows 
LEFT JOIN sessionsales on (sesa_wflowid = wflw_wflowid)
SET wflw_customerid = sesa_customerid
WHERE wflw_callercode = 'SESA';

UPDATE wflows
LEFT JOIN credits on (cred_wflowid = wflw_wflowid)
SET wflw_customerid = cred_customerid
WHERE wflw_callercode = 'CRED';

UPDATE wflows 
LEFT JOIN propertiesrent on (prrt_wflowid = wflw_wflowid)
SET wflw_customerid = prrt_customerid
WHERE wflw_callercode = 'PRRT';

-- 6/dic/2018 poner fecha de el día 
ALTER TABLE wflowsteps ADD COLUMN wfsp_status VARCHAR(50);

update wflowsteps set wfsp_status = 'T' where (wfsp_reminddate > '2018-12-11' or wfsp_reminddate is null) AND wfsp_status IS NULL;
update wflowsteps set wfsp_status = 'E' where (wfsp_reminddate <= '2018-12-11') AND wfsp_status IS NULL;
update wflowsteps set wfsp_status = 'F' where (wfsp_progress = 100) AND wfsp_status IS NULL;
-- 7/dic/2018
ALTER TABLE ordertypes ADD COLUMN ortp_datafiscal INT;

-- 13 /dic /2018

ALTER TABLE requisitiontypes
ADD COLUMN rqtp_createpaccount INT(11) NULL ;

update requisitiontypes set rqtp_createpaccount = 0;

-- 18/dic/2018
ALTER TABLE orders ADD COLUMN orde_statusdetail CHAR;
UPDATE orders LEFT JOIN orderdetails ON (ordt_orderid = orde_orderid) SET orde_statusdetail = ordt_status;


-- 26/dic/2018
ALTER TABLE orderdeliveries ADD COLUMN odly_projectid INT;
ALTER TABLE orderdeliveries ADD FOREIGN KEY (odly_projectid) REFERENCES projects(proj_projectid);

-- 27/dic/2018

ALTER TABLE ordertypes
ADD COLUMN ortp_reminddaysbeforeenddate INT(11), 
ADD COLUMN ortp_reminddaysbeforeenddatetwo INT(11);

ALTER TABLE ordertypes
ADD COLUMN ortp_emailremindercontractend INT(11) ;

-- 03/ene/2019
ALTER TABLE orderdeliveryitems MODIFY COLUMN odyi_name VARCHAR(200);

-- 04/ene/2019
ALTER TABLE projects ADD COLUMN proj_warehousemanagerid INT;
ALTER TABLE projects ADD FOREIGN KEY (proj_warehousemanagerid) REFERENCES users(user_userid);

-- 04/ene/2019

ALTER TABLE `whboxtracks` DROP FOREIGN KEY `whboxtracks_ibfk_2`;
ALTER TABLE `whboxtracks` CHANGE COLUMN `whbt_whtrackid` `whbt_whtrackid` INT(11) NULL ;
ALTER TABLE `whboxtracks` ADD CONSTRAINT `whboxtracks_ibfk_2`  FOREIGN KEY (`whbt_whtrackid`)  REFERENCES `whtracks` (`whtr_whtrackid`);

-- 15/ene/2019
ALTER TABLE venues ADD COLUMN venu_homeaddress INT;
ALTER TABLE `venues` DROP FOREIGN KEY `venues_ibfk_1`;
ALTER TABLE `venues` CHANGE COLUMN `venu_cityid` `venu_cityid` INT(11) NULL ;
ALTER TABLE `venues` ADD CONSTRAINT `venues_ibfk_1`  FOREIGN KEY (`venu_cityid`) REFERENCES `cities` (`city_cityid`);

ALTER TABLE projects ADD COLUMN proj_homeaddress VARCHAR(500);

-- 16/ene/2019
ALTER TABLE customers ADD COLUMN cust_passwconf VARCHAR(255) ;
ALTER TABLE venues ADD COLUMN venu_logo VARCHAR(255) ;
ALTER TABLE orderdeliveries ADD COLUMN odly_notes VARCHAR(1024);
ALTER TABLE flexconfig ADD COLUMN flxc_renewproducts INT(11);

-- 18/ene/2019
CREATE TABLE ordertypewflowcategories (
	ortw_ordertypewflowcategoryid INT NOT NULL AUTO_INCREMENT, 
    ortw_ordertypeid INT NOT NULL,
    ortw_wflowcategoryid INT, 
    ortw_usercreateid INT,
	ortw_usermodifyid INT,
	ortw_datecreate DATETIME,
	ortw_datemodify DATETIME,
    PRIMARY KEY(ortw_ordertypewflowcategoryid),
    FOREIGN KEY (ortw_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid),
    FOREIGN KEY (ortw_wflowcategoryid) REFERENCES wflowcategories(wfca_wflowcategoryid),
    FOREIGN KEY (ortw_usercreateid) REFERENCES users(user_userid),
  	FOREIGN KEY (ortw_usermodifyid) REFERENCES users(user_userid)
    );

ALTER TABLE quotegroups ADD COLUMN qogr_index INT;
ALTER TABLE quoteitems ADD COLUMN qoit_index INT;
ALTER TABLE ordergroups ADD COLUMN ordg_index INT;
ALTER TABLE orderitems ADD COLUMN ordi_index INT;


-- 21 ene 2019
CREATE TABLE wflowuserblockdates (
	wfub_wflowuserblockdateid INT NOT NULL AUTO_INCREMENT, 
	wfub_startdate DATETIME, 
	wfub_enddate DATETIME, 
	wfub_comments VARCHAR(255),
	wfub_userid INT NOT NULL,
    wfub_usercreateid INT,
	wfub_usermodifyid INT,
	wfub_datecreate DATETIME,
	wfub_datemodify DATETIME,
	PRIMARY KEY(wfub_wflowuserblockdateid),
    FOREIGN KEY (wfub_userid) REFERENCES users(user_userid),
 	FOREIGN KEY (wfub_usercreateid) REFERENCES users(user_userid),
  	FOREIGN KEY (wfub_usermodifyid) REFERENCES users(user_userid)
);


-- 22/Ene/2019

ALTER TABLE orders
ADD COLUMN orde_notes VARCHAR(1024) ;

ALTER TABLE orderdeliveries
ADD COLUMN odly_usercreate  VARCHAR(60);

-- 23/Ene/2018

CREATE TABLE orderflowusergroup (
  `ofug_orderflowusergroupid` INT(11) NOT NULL AUTO_INCREMENT,
  `ofug_profileid` INT(11) ,
  `ofug_usercreateid` INT(11),
  `ofug_usermodifyid` INT(11),
  `ofug_datecreate` DATETIME ,
  `ofug_datemodify` DATETIME,
  PRIMARY KEY (`ofug_orderflowusergroupid`),
  FOREIGN  KEY (`ofug_profileid`) REFERENCES groups(prof_profileid),
  FOREIGN  KEY (`ofug_usercreateid`) REFERENCES users(user_userid),
  FOREIGN  KEY (`ofug_usermodifyid`) REFERENCES users(user_userid));

-- 28/ene/2019
ALTER TABLE ordertypes ADD COLUMN ortp_atmccrevision INT;

ALTER TABLE bankmovconcepts ADD COLUMN bkmc_requisitionid INT;
ALTER TABLE bankmovconcepts ADD FOREIGN KEY (bkmc_requisitionid) REFERENCES requisitions(reqi_requisitionid);

-- 29/Ene/2019
ALTER TABLE products ADD COLUMN prod_consumable INT(11) ;
ALTER TABLE equipmentservices ADD COLUMN eqsv_liberatedate DATE;

-- 30/ene/2019
ALTER TABLE flexconfig 
ADD COLUMN flxc_statusrevision INT(11),
ADD COLUMN flxc_statusauthorized INT(11),
ADD COLUMN flxc_statusreconciled INT(11),
ADD COLUMN flxc_statuscancelled INT(11);

update flexconfig set flxc_statusrevision = 1,flxc_statusauthorized = 1,flxc_statusreconciled = 1,flxc_statuscancelled = 1;

-- 31/enero/2019
CREATE TABLE projectequipment (
peqi_projectequipmentid INT NOT NULL AUTO_INCREMENT PRIMARY KEY
);
ALTER TABLE projectequipment
ADD COLUMN peqi_code VARCHAR(30),
ADD COLUMN peqi_name VARCHAR(30),
ADD COLUMN peqi_description VARCHAR(30),
ADD COLUMN peqi_equipmentid INT,
ADD COLUMN peqi_projectid INT,
ADD COLUMN peqi_enddate datetime,
ADD COLUMN peqi_startdate datetime,
ADD COLUMN peqi_usercreateid varchar(20) ,
ADD COLUMN peqi_usermodifyid varchar(20) ,
ADD COLUMN peqi_datecreate varchar(20),
ADD COLUMN peqi_datemodify varchar(20);
ALTER TABLE projectequipment ADD FOREIGN KEY (peqi_equipmentid) REFERENCES equipments(equi_equipmentid);
ALTER TABLE projectequipment ADD FOREIGN KEY (peqi_projectid) REFERENCES projects(proj_projectid);
ALTER TABLE projectequipment ADD FOREIGN KEY (peqi_usercreateid) REFERENCES users(user_userid);
ALTER TABLE projectequipment ADD FOREIGN KEY (peqi_usermodifyid) REFERENCES users(user_userid);

-- 01/feb/19
ALTER TABLE requisitiontypes ADD COLUMN rqtp_paymentbankmovtypeid INT;
ALTER TABLE requisitiontypes ADD FOREIGN KEY (rqtp_paymentbankmovtypeid) REFERENCES bankmovtypes(bkmt_bankmovtypeid);

ALTER TABLE requisitiontypes ADD COLUMN rqtp_devolutionbankmovtypeid INT;
ALTER TABLE requisitiontypes ADD FOREIGN KEY (rqtp_devolutionbankmovtypeid) REFERENCES bankmovtypes(bkmt_bankmovtypeid);

ALTER TABLE requisitions ADD COLUMN reqi_wflowid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_wflowid) REFERENCES wflows(wflw_wflowid);

ALTER TABLE requisitions ADD COLUMN reqi_wflowtypeid INT;
ALTER TABLE requisitions ADD FOREIGN KEY (reqi_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);

-- 6/feb/2019
ALTER TABLE opportunities CHANGE COLUMN `oppo_losecomments` `oppo_losecomments` VARCHAR(500) NULL ;

-- 11/feb/2019
ALTER TABLE ordertypes ADD COLUMN ortp_statusdefaultdetail CHAR(1) NULL, ADD COLUMN ortp_areadefaultdetail INT(11) NULL;
ALTER TABLE sfconfig ADD COLUMN sfcf_mailserverapi VARCHAR(255);

-- 12/feb/2019
ALTER TABLE properties ADD COLUMN prty_tags VARCHAR(255) NULL;

-- 13/feb/2019

ALTER TABLE propertytypes ADD COLUMN ptyp_copytags INT(11) NULL ;


-- 20190218
ALTER TABLE sfconfig ADD COLUMN sfcf_maxusers INT;
ALTER TABLE sfconfig ADD COLUMN sfcf_enablegpstimeclock INT;

-- 19/feb/2019
ALTER TABLE customers ADD COLUMN cust_lessormasterid INT(11) NULL;

-- 20/feb/2019
CREATE TABLE fiscalperiods (
  `fipe_fiscalperiodid` INT(11) NOT NULL AUTO_INCREMENT,
  `fipe_name` VARCHAR(100) NOT NULL,
  `fipe_description` VARCHAR(500),
  `fipe_startdate` DATE NOT NULL,
  `fipe_enddate` DATE NOT NULL,
  `fipe_status` CHAR(1) NOT NULL,
  `fipe_companyid` INT(11) NULL,
  `fipe_usercreateid` INT(11),
  `fipe_usermodifyid` INT(11),
  `fipe_datecreate` DATETIME ,
  `fipe_datemodify` DATETIME,
  PRIMARY KEY (`fipe_fiscalperiodid`),
  FOREIGN  KEY (`fipe_companyid`) REFERENCES companies(comp_companyid),
  FOREIGN  KEY (`fipe_usercreateid`) REFERENCES users(user_userid),
  FOREIGN  KEY (`fipe_usermodifyid`) REFERENCES users(user_userid));

-- 25/feb/2019
ALTER TABLE ordertypes ADD COLUMN ortp_reminddaysbeforeenddatethree INT(11) NULL ;

-- 20190225
ALTER TABLE sfconfig ADD COLUMN sfcf_maxcompanies INT;

-- 01/mar/2019
ALTER TABLE workcontracts ADD COLUMN woco_wflowid INT;
ALTER TABLE workcontracts ADD FOREIGN KEY (woco_wflowid) REFERENCES wflows(wflw_wflowid);

ALTER TABLE workcontracts ADD COLUMN woco_wflowtypeid INT;
ALTER TABLE workcontracts ADD FOREIGN KEY (woco_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);

-- 20190302
ALTER TABLE cronjobs ADD COLUMN crjb_status CHAR;

-- 201903007

CREATE table paymethod(
pamt_paymethodid int primary key auto_increment,
pamt_codesat varchar (20),
pamt_description varchar (80),
pamt_usercreateid INT(11),
pamt_usermodifyid INT(11),
pamt_datecreate  DATETIME ,
pamt_datemodify DATETIME,
  FOREIGN  KEY (pamt_usercreateid) REFERENCES users(user_userid),
  FOREIGN  KEY (pamt_usermodifyid) REFERENCES users(user_userid));
  

  CREATE table cfdi(
cfdi_cfdiid int primary key auto_increment,
cfdi_codesat varchar (20),
cfdi_description varchar (80),
cfdi_usercreateid INT(11),
cfdi_usermodifyid INT(11),
cfdi_datecreate  DATETIME ,
cfdi_datemodify DATETIME,
  FOREIGN  KEY (cfdi_usercreateid) REFERENCES users(user_userid),
  FOREIGN  KEY (cfdi_usermodifyid) REFERENCES users(user_userid));
  
ALTER TABLE customers add COLUMN cust_cfdiid  int ;
ALTER TABLE customers ADD FOREIGN KEY (cust_cfdiid) REFERENCES cfdi(cfdi_cfdiid);

ALTER TABLE customers add COLUMN cust_paymethodid int ;
ALTER TABLE customers ADD FOREIGN KEY (cust_paymethodid) REFERENCES paymethod(pamt_paymethodid);
  

-- 20190311
ALTER TABLE requisitiontypes add COLUMN rqtp_viewformat BOOLEAN;

-- 11/mar/2019
ALTER TABLE flexconfig ADD COLUMN flxc_requiredperiodfiscal INT;

-- 13/mar/2019
ALTER TABLE flexconfig ADD COLUMN flxc_oppostatusganada int(11);
ALTER TABLE flexconfig ADD COLUMN flxc_oppostatusperdida int(11);
ALTER TABLE flexconfig ADD COLUMN flxc_oppostatusexpirada int(11);
ALTER TABLE flexconfig ADD column flxc_oppostatushold int(11);
ALTER TABLE flexconfig ADD COLUMN flxc_oppostatusrevision int(11);

-- 19/Marzo/2019

CREATE TABLE wflowprogramprofiles (
  `wfug_wflowusergroupid` INT(11) NOT NULL AUTO_INCREMENT,
  `wfug_profileid` INT(11) ,
  `wfug_wflowcategoryid` INT(11) ,
  `wfug_usercreateid` INT(11),
  `wfug_usermodifyid` INT(11),
  `wfug_datecreate` DATETIME ,
  `wfug_datemodify` DATETIME,
  PRIMARY KEY (`wfug_wflowusergroupid`),
  FOREIGN  KEY (`wfug_profileid`) REFERENCES groups(prof_profileid),
  FOREIGN  KEY (`wfug_usercreateid`) REFERENCES users(user_userid),
  FOREIGN  KEY (`wfug_usermodifyid`) REFERENCES users(user_userid));
  
  -- 22/Marzo/2019
  
ALTER TABLE contractconceptitems ADD COLUMN `ccit_description` VARCHAR(1500) ;

-- 3 marzo 2019

ALTER TABLE `programs` 
DROP FOREIGN KEY `programs_ibfk_1`,
DROP FOREIGN KEY `programs_ibfk_2`,
DROP FOREIGN KEY `programs_ibfk_3`;
ALTER TABLE `programs` 
CHANGE COLUMN `prog_programid` `cpro_programid` INT(11) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `prog_name` `cpro_name` VARCHAR(100) NOT NULL ,
CHANGE COLUMN `prog_description` `cpro_description` VARCHAR(500) NULL DEFAULT NULL ,
CHANGE COLUMN `prog_areaid` `cpro_areaid` INT(11) NOT NULL ,
CHANGE COLUMN `prog_sflog` `cpro_sflog` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `prog_usercreateid` `cpro_usercreateid` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `prog_usermodifyid` `cpro_usermodifyid` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `prog_datecreate` `cpro_datecreate` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `prog_datemodify` `cpro_datemodify` DATETIME NULL DEFAULT NULL , RENAME TO `courseprograms` ;

ALTER TABLE `courseprograms` 
ADD CONSTRAINT `courseprograms_ibfk_1`
  FOREIGN KEY (`cpro_areaid`)
  REFERENCES `areas` (`area_areaid`),
ADD CONSTRAINT `courseprograms_ibfk_2`
  FOREIGN KEY (`cpro_usercreateid`)
  REFERENCES `users` (`user_userid`),
ADD CONSTRAINT `courseprograms_ibfk_3`
  FOREIGN KEY (`cpro_usermodifyid`)
  REFERENCES `users` (`user_userid`);


-- 4 abri 2019
ALTER TABLE requisitionitems CHANGE rqit_quantity rqit_quantity DOUBLE;
ALTER TABLE requisitionreceiptitems CHANGE reit_quantity reit_quantity DOUBLE;
ALTER TABLE paccountitems CHANGE pait_quantity pait_quantity DOUBLE;

-- 29 Abril 2019
alter table opportunities add column oppo_customercontactid int ;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_customercontactid) REFERENCES customercontacts(cuco_customercontactid);	


UPDATE opportunities left join quotes on (oppo_quoteid = quot_quoteid)
SET oppo_customercontactid = quot_customercontactid WHERE quot_customercontactid >0  ;

-- 06/ MAYO / 2019    Catalogo tipos de cuentas 

create table bankaccounttype(
bact_bankaccounttypeid int primary key auto_increment,
bact_name varchar(50),
bact_description varchar(500),
bact_usercreateid INT,
bact_usermodifyid INT,
bact_datecreate DATETIME,
bact_datemodify DATETIME
);
ALTER TABLE bankaccounts ADD COLUMN bkac_bankaccounttypeid int;
ALTER TABLE bankaccounts ADD FOREIGN KEY (bkac_bankaccounttypeid) REFERENCES bankaccounttype(bact_bankaccounttypeid);	

ALTER TABLE bankaccounts modify COLUMN bkac_type char;

INSERT INTO bankaccounttype (bact_name,bact_description ) VALUES ('Maestra','');
INSERT INTO bankaccounttype (bact_name,bact_description ) VALUES ('Cheques','');
INSERT INTO bankaccounttype (bact_name,bact_description ) VALUES ('Ahorro','');
INSERT INTO bankaccounttype (bact_name,bact_description ) VALUES ('Efectivo','');


UPDATE bankaccounts SET bkac_bankaccounttypeid = (SELECT bact_bankaccounttypeid FROM bankaccounttype WHERE bact_name LIKE 'Maestra') WHERE bkac_type  LIKE 'M';
UPDATE bankaccounts SET bkac_bankaccounttypeid = (SELECT bact_bankaccounttypeid FROM bankaccounttype WHERE bact_name LIKE 'Cheques') WHERE bkac_type  LIKE 'C';
UPDATE bankaccounts SET bkac_bankaccounttypeid = (SELECT bact_bankaccounttypeid FROM bankaccounttype WHERE bact_name LIKE 'Ahorro') WHERE bkac_type  LIKE 'S';
UPDATE bankaccounts SET bkac_bankaccounttypeid = (SELECT bact_bankaccounttypeid FROM bankaccounttype WHERE bact_name LIKE 'Efectivo') WHERE bkac_type  LIKE 'A';


-- estatus de mov banco 06/ MAYO / 2019
ALTER TABLE bankaccounts ADD COLUMN  bkac_status char;  
UPDATE  bankaccounts SET bkac_status = 'O' WHERE bkac_status is null;

-- 7/mayo/2019
ALTER TABLE `bankmovements` CHANGE COLUMN `bkmv_inputdate` `bkmv_inputdate` DATE NULL ;


-- 7 mayo 2019
alter table raccounts add column racc_statuscharge varchar(1024);


-- 4 junio 2019

ALTER TABLE opportunities
ADD COLUMN oppo_amountservice DOUBLE NULL ,
ADD COLUMN oppo_amountlicense DOUBLE NULL ;
update opportunities set oppo_amountservice = 0, oppo_amountlicense = 0 where oppo_opportunityid > 0;



-- miercoles 5 de junio 2019
alter table flexconfig add column flxc_statusdefaultcompany int;




create table competition (
cmpt_competitionid int primary key not null auto_increment,
cmpt_name varchar(100),
cmpt_usercreateid int,
cmpt_usermodifyid int,
cmpt_datecreate DATETIME,
cmpt_datemodify DATETIME

);

create table  opportunitycompetition(
opcm_compid int primary key auto_increment not null,
opcm_competitionid int,
opcm_opportunityid int
);


ALTER TABLE opportunitycompetition ADD COLUMN   opcm_usercreateid int;
ALTER TABLE opportunitycompetition ADD COLUMN   opcm_usermodifyid int;
ALTER TABLE opportunitycompetition ADD COLUMN   opcm_datecreate DATETIME;
ALTER TABLE opportunitycompetition ADD COLUMN   opcm_datemodify DATETIME;  

ALTER TABLE opportunities ADD COLUMN oppo_compid int;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_compid) REFERENCES opportunitycompetition(opcm_compid);
ALTER TABLE opportunitycompetition ADD FOREIGN KEY (opcm_opportunityid) REFERENCES opportunities(oppo_opportunityid);


ALTER TABLE opportunities ADD COLUMN oppo_leaddate date;


update customers set cust_rating  = 'A' where cust_rating = '1';
update customers set cust_rating  = 'B' where cust_rating = '2';	
update customers set cust_rating  = 'C' where cust_rating = '3';

-- 

-- 
ALTER TABLE opportunities
ADD COLUMN oppo_category VARCHAR(1) NULL ;

ALTER TABLE opportunities
ADD COLUMN oppo_categoryforecast VARCHAR(1) NULL ;


--6/6/2019

ALTER TABLE opportunities ADD COLUMN oppo_compvspos varchar(512);

-- 10/6/2019
ALTER TABLE flexconfig ADD COLUMN flxc_ordestatusauthorized   int(11);
ALTER TABLE flexconfig ADD COLUMN flxc_ordestatusfinished   int(11);
ALTER TABLE flexconfig ADD COLUMN flxc_ordestatuscancelled   int(11);
ALTER TABLE flexconfig ADD COLUMN flxc_ordestatusrevision  int(11);

-- 17 junio 2019
UPDATE  flexconfig 
SET flxc_ordestatusrevision=1,
flxc_ordestatusauthorized = 1,
flxc_ordestatusfinished=1,
flxc_ordestatuscancelled=1 
where flxc_flexconfigid >0;

-- 26/jun/2019

ALTER TABLE propertiesrent
ADD COLUMN prrt_originrenewcontractid INT(11) ;


ALTER TABLE sfconfig ADD COLUMN sfcf_modalform INT;

-- 27/jun/2019
ALTER TABLE `regions` CHANGE COLUMN `regi_code` `regi_code` VARCHAR(60) NULL ;

ALTER TABLE `orderdeliveries` DROP FOREIGN KEY `orderdeliveries_ibfk_4`;
ALTER TABLE `orderdeliveries` CHANGE COLUMN `odly_customerid` `odly_customerid` INT(11) NULL ;
ALTER TABLE `orderdeliveries` ADD CONSTRAINT `orderdeliveries_ibfk_4` FOREIGN KEY (`odly_customerid`)  REFERENCES`customers` (`cust_customerid`);

-- 5/jul/2019 (Decimales en almacen)
ALTER TABLE `units` ADD COLUMN `unit_fraction` INT;
UPDATE units SET unit_fraction = 0;

-- Almacenes
ALTER TABLE `whmovitems` CHANGE COLUMN `whmi_quantity` `whmi_quantity` DOUBLE NOT NULL;
ALTER TABLE `whstocks` CHANGE COLUMN `whst_quantity` `whst_quantity` DOUBLE NOT NULL;
ALTER TABLE `whtracks` CHANGE COLUMN `whtr_inquantity` `whtr_inquantity` DOUBLE NULL DEFAULT NULL;
ALTER TABLE `whtracks` CHANGE COLUMN `whtr_outquantity` `whtr_outquantity` DOUBLE NULL DEFAULT NULL;
ALTER TABLE `whtracks` CHANGE COLUMN `whtr_outquantity` `whtr_outquantity` DOUBLE NULL DEFAULT NULL;
ALTER TABLE `whboxtracks` CHANGE COLUMN `whbt_quantity` `whbt_quantity` DOUBLE NULL DEFAULT NULL;
-- Items Cotizacion
ALTER TABLE `quoteitems` CHANGE COLUMN `qoit_quantity` `qoit_quantity` DOUBLE NOT NULL;
-- Items Pedido
ALTER TABLE `orderitems` CHANGE COLUMN `ordi_quantity` `ordi_quantity` DOUBLE NOT NULL;
ALTER TABLE `orderitems` CHANGE COLUMN `ordi_quantitydelivered` `ordi_quantitydelivered` DOUBLE NULL;
ALTER TABLE `orderitems` CHANGE COLUMN `ordi_quantityreturned` `ordi_quantityreturned` DOUBLE NULL;
ALTER TABLE `orderitems` CHANGE COLUMN `ordi_quantitybalance` `ordi_quantitybalance` DOUBLE NULL;
ALTER TABLE `orderitems` CHANGE COLUMN `ordi_lockedquantity` `ordi_lockedquantity` DOUBLE NULL;
ALTER TABLE `orderitems` CHANGE COLUMN `ordi_conflictquantity` `ordi_conflictquantity` DOUBLE NULL;
-- Items Envio Pedido
ALTER TABLE `orderdeliveryitems` CHANGE COLUMN `odyi_quantity` `odyi_quantity` DOUBLE NULL;
ALTER TABLE `orderdeliveryitems` CHANGE COLUMN `odyi_quantitybalance` `odyi_quantitybalance` DOUBLE NULL;
ALTER TABLE `orderdeliveryitems` CHANGE COLUMN `odyi_quantityreturned` `odyi_quantityreturned` DOUBLE NULL;
-- Item de Kits 
ALTER TABLE `productkititems` CHANGE COLUMN `prki_quantity` `prki_quantity` DOUBLE NOT NULL;
-- OC
ALTER TABLE `requisitionitems` CHANGE COLUMN `rqit_quantityreceipt` `rqit_quantityreceipt` DOUBLE NULL;
ALTER TABLE `requisitionitems` CHANGE COLUMN `rqit_quantityreturned` `rqit_quantityreturned` DOUBLE NULL;
ALTER TABLE `requisitionitems` CHANGE COLUMN `rqit_quantitybalance` `rqit_quantitybalance` DOUBLE NULL; 
-- ROC
ALTER TABLE `requisitionreceiptitems` CHANGE COLUMN `reit_quantitybalance` `reit_quantitybalance` DOUBLE NOT NULL;
ALTER TABLE `requisitionreceiptitems` CHANGE COLUMN `reit_quantityreturned`	`reit_quantityreturned` DOUBLE NOT NULL;

-- 2 julio 2019
create table payconditions(
paco_payconditionid int primary key auto_increment not null,
paco_code varchar(20),
paco_description varchar(512),
paco_usercreateid INT,
paco_usermodifyid INT,
paco_datecreate DATETIME,
paco_datemodify DATETIME,
FOREIGN  KEY (`paco_usercreateid`) REFERENCES users(user_userid),
FOREIGN  KEY (`paco_usermodifyid`) REFERENCES users(user_userid));


-- viernes 12
create table rfqu(
rfqu_rfquid int primary key auto_increment not null,
rfqu_code varchar(12),
rfqu_customerid int(11),
rfqu_userid int(11),
rfqu_customercontactId int(11),
rfqu_affair varchar(200),
rfqu_date datetime,
rfqu_ordertypeid int(11),
rfqu_wflowtypeid int(11),
rfqu_objective varchar(1024),
rfqu_status char(1),
rfqu_usercreateid int(11),
rfqu_usermodifyid int(11),
rfqu_datecreate datetime,
rfqu_datemodify datetime,
rfqu_wflowid int(11),
rfqu_companyid int(11),
rfqu_currencyid int(11),
rfqu_foreignwflowtypeid int(11),
rfqu_budgetitemid int(11),
rfqu_estimationid int(11),
foreign key (rfqu_customerid) references customers (cust_customerid),
foreign key (rfqu_ordertypeid) references ordertypes (ortp_ordertypeid),
foreign key (rfqu_wflowtypeid) references wflowtypes (wfty_wflowtypeid),
foreign key (rfqu_customercontactId) references customercontacts (cuco_customercontactid),
foreign key (rfqu_wflowid) references wflows (wflw_wflowid),
FOREIGN  KEY (rfqu_usercreateid) REFERENCES users(user_userid),
 FOREIGN  KEY (rfqu_usermodifyid) REFERENCES users(user_userid));

alter table rfqu add foreign key (rfqu_customerid) references customers (cust_customerid);
alter table rfqu add foreign key (rfqu_ordertypeid) references ordertypes (ortp_ordertypeid);
alter table rfqu add foreign key (rfqu_wflowtypeid) references wflowtypes (wfty_wflowtypeid);
alter table rfqu add foreign key (rfqu_customercontactId) references customercontacts (cuco_customercontactid);
alter table rfqu add foreign key (rfqu_wflowid) references wflows (wflw_wflowid);

  -- 
CREATE TABLE `estimations` (
  `ests_estimationid` int(11) NOT NULL AUTO_INCREMENT,
  `ests_code` varchar(10) DEFAULT NULL,
  `ests_name` varchar(100) DEFAULT NULL,
  `ests_description` varchar(1000) DEFAULT NULL,
  `ests_status` char(1) DEFAULT NULL,
  `ests_amount` double DEFAULT NULL,
  `ests_discount` double DEFAULT NULL,
  `ests_tax` double DEFAULT NULL,
  `ests_total` double DEFAULT NULL,
  `ests_sflog` text,
  `ests_downpayment` float DEFAULT NULL,
  `ests_showequipmentquantity` int(11) DEFAULT NULL,
  `ests_showequipmentprice` int(11) DEFAULT NULL,
  `ests_taxapplies` int(11) DEFAULT NULL,
  `ests_currencyid` int(11) DEFAULT NULL,
  `ests_customerid` int(11) DEFAULT NULL,
  `ests_userid` int(11) DEFAULT NULL,
  `ests_wflowid` int(11) DEFAULT NULL,
  `ests_type` char(1) DEFAULT NULL,
  `ests_startdate` datetime DEFAULT NULL,
  `ests_enddate` datetime DEFAULT NULL,
  `ests_showstaffquantity` int(11) DEFAULT NULL,
  `ests_showstaffprice` int(11) DEFAULT NULL,
  `ests_ordertypeid` int(11) DEFAULT NULL,
  `ests_companyid` int(11) DEFAULT NULL,
  `ests_usercreateid` int(11) DEFAULT NULL,
  `ests_usermodifyid` int(11) DEFAULT NULL,
  `ests_datecreate` datetime DEFAULT NULL,
  `ests_datemodify` datetime DEFAULT NULL,
  `ests_authnum` double DEFAULT NULL,
  `ests_currencyparity` double DEFAULT NULL,
  `ests_coverageparity` int(11) DEFAULT NULL,
  `ests_comments` varchar(1000) DEFAULT NULL,
  `ests_customerrequisition` varchar(20) DEFAULT NULL,
  `ests_customercontactid` int(11) DEFAULT NULL,
  `ests_authorizeddate` datetime DEFAULT NULL,
  `ests_authorizeduser` int(11) DEFAULT NULL,
  `ests_cancelleddate` datetime DEFAULT NULL,
  `ests_cancelleduser` int(11) DEFAULT NULL,
  `ests_marketid` int(11) DEFAULT NULL,
  `ests_budgetitemid` int(11) DEFAULT NULL,
  `ests_areaid` int(11) DEFAULT NULL,
  PRIMARY KEY (`ests_estimationid`),
  KEY `ests_currencyid` (`ests_currencyid`),
  KEY `ests_customerid` (`ests_customerid`),
  KEY `ests_userid` (`ests_userid`),
  KEY `ests_wflowid` (`ests_wflowid`),
  KEY `ests_ordertypeid` (`ests_ordertypeid`),
  KEY `ests_companyid` (`ests_companyid`),
  KEY `ests_usercreateid` (`ests_usercreateid`),
  KEY `ests_usermodifyid` (`ests_usermodifyid`),
  KEY `ests_customercontactid` (`ests_customercontactid`),
  KEY `ests_authorizeduser` (`ests_authorizeduser`),
  KEY `ests_cancelleduser` (`ests_cancelleduser`),
  KEY `ests_marketid` (`ests_marketid`),
  KEY `ests_budgetitemid` (`ests_budgetitemid`),
  KEY `ests_areaid` (`ests_areaid`),
  CONSTRAINT `estimations_ibfk_10` FOREIGN KEY (`ests_customercontactid`) REFERENCES `customercontacts` (`cuco_customercontactid`),
  CONSTRAINT `estimations_ibfk_11` FOREIGN KEY (`ests_authorizeduser`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `estimations_ibfk_12` FOREIGN KEY (`ests_cancelleduser`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `estimations_ibfk_13` FOREIGN KEY (`ests_marketid`) REFERENCES `markets` (`mrkt_marketid`),
  CONSTRAINT `estimations_ibfk_14` FOREIGN KEY (`ests_budgetitemid`) REFERENCES `budgetitems` (`bgit_budgetitemid`),
  CONSTRAINT `estimations_ibfk_15` FOREIGN KEY (`ests_areaid`) REFERENCES `areas` (`area_areaid`),
  CONSTRAINT `estimations_ibfk_2` FOREIGN KEY (`ests_currencyid`) REFERENCES `currencies` (`cure_currencyid`),
  CONSTRAINT `estimations_ibfk_3` FOREIGN KEY (`ests_customerid`) REFERENCES `customers` (`cust_customerid`),
  CONSTRAINT `estimations_ibfk_4` FOREIGN KEY (`ests_userid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `estimations_ibfk_5` FOREIGN KEY (`ests_wflowid`) REFERENCES `wflows` (`wflw_wflowid`),
  CONSTRAINT `estimations_ibfk_6` FOREIGN KEY (`ests_ordertypeid`) REFERENCES `ordertypes` (`ortp_ordertypeid`),
  CONSTRAINT `estimations_ibfk_7` FOREIGN KEY (`ests_companyid`) REFERENCES `companies` (`comp_companyid`),
  CONSTRAINT `estimations_ibfk_8` FOREIGN KEY (`ests_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `estimations_ibfk_9` FOREIGN KEY (`ests_usermodifyid`) REFERENCES `users` (`user_userid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- 
CREATE TABLE `estimationgroups` (
  `esgp_estimationgroupid` int(11) NOT NULL AUTO_INCREMENT,
  `esgp_code` varchar(10) DEFAULT NULL,
  `esgp_name` varchar(100) DEFAULT NULL,
  `esgp_description` varchar(255) DEFAULT NULL,
  `esgp_amount` double DEFAULT NULL,
  `esgp_estimationid` int(11) NOT NULL,
  `esgp_sequence` int(11) DEFAULT NULL,
  `esgp_sflog` text,
  `esgp_iskit` int(11) DEFAULT NULL,
  `esgp_showprice` int(11) DEFAULT NULL,
  `esgp_showquantity` int(11) DEFAULT NULL,
  `esgp_usercreateid` int(11) DEFAULT NULL,
  `esgp_usermodifyid` int(11) DEFAULT NULL,
  `esgp_datecreate` datetime DEFAULT NULL,
  `esgp_datemodify` datetime DEFAULT NULL,
  `esgp_showamount` int(11) DEFAULT NULL,
  `esgp_image` varchar(255) DEFAULT NULL,
  `esgp_showgroupimage` int(11) DEFAULT NULL,
  `esgp_showproductimage` int(11) DEFAULT NULL,
  `esgp_index` int(11) DEFAULT NULL,
  PRIMARY KEY (`esgp_estimationgroupid`),
  KEY `esgp_estimationid` (`esgp_estimationid`),
  KEY `esgp_usercreateid` (`esgp_usercreateid`),
  KEY `esgp_usermodifyid` (`esgp_usermodifyid`),
   FOREIGN KEY (`esgp_estimationid`) REFERENCES `estimations` (`ests_estimationid`),
   FOREIGN KEY (`esgp_usercreateid`) REFERENCES `users` (`user_userid`),
   FOREIGN KEY (`esgp_usermodifyid`) REFERENCES `users` (`user_userid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  alter table estimationgroups add foreign key (esgp_estimationid) references estimations(ests_estimationid);
  
-- 
  CREATE TABLE `estimationrfqitems` (
  `esrf_estimationrfqitemid` int(11) NOT NULL AUTO_INCREMENT,
  `esrf_quantity` int(11) NOT NULL,
  `esrf_price` double DEFAULT NULL,
  `esrf_days` double DEFAULT NULL,
  `esrf_amount` double DEFAULT NULL,
  `esrf_sequence` int(11) DEFAULT NULL,
  `esrf_productid` int(11) DEFAULT NULL,
  `esrf_estimationgroupid` int(11) NOT NULL,
  `esrf_sflog` text,
  `esrf_name` varchar(200) DEFAULT NULL,
  `esrf_description` varchar(500) DEFAULT NULL,
  `esrf_usercreateid` int(11) DEFAULT NULL,
  `esrf_usermodifyid` int(11) DEFAULT NULL,
  `esrf_datecreate` datetime DEFAULT NULL,
  `esrf_datemodify` datetime DEFAULT NULL,
  `esrf_baseprice` double DEFAULT NULL,
  `esrf_commission` int(11) DEFAULT NULL,
  `esrf_budgetitemid` int(11) DEFAULT NULL,
  `esrf_areaid` int(11) DEFAULT NULL,
  `esrf_index` int(11) DEFAULT NULL,
  PRIMARY KEY (`esrf_estimationrfqitemid`),
  KEY `esrf_estimationgroupid` (`esrf_estimationgroupid`),
  KEY `esrf_productid` (`esrf_productid`),
  KEY `esrf_usercreateid` (`esrf_usercreateid`),
  KEY `esrf_usermodifyid` (`esrf_usermodifyid`),
  KEY `esrf_budgetitemid` (`esrf_budgetitemid`),
  KEY `esrf_areaid` (`esrf_areaid`),
   FOREIGN KEY (`esrf_estimationgroupid`) REFERENCES `estimationgroups` (`esgp_estimationgroupid`),
   FOREIGN KEY (`esrf_productid`) REFERENCES `products` (`prod_productid`),
   FOREIGN KEY (`esrf_usercreateid`) REFERENCES `users` (`user_userid`),
   FOREIGN KEY (`esrf_usermodifyid`) REFERENCES `users` (`user_userid`),
   FOREIGN KEY (`esrf_budgetitemid`) REFERENCES `budgetitems` (`bgit_budgetitemid`),
   FOREIGN KEY (`esrf_areaid`) REFERENCES `areas` (`area_areaid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 15/Julio/2019
ALTER TABLE `ordertypes` ADD COLUMN `ortp_createproject` INT(11) NULL ;

-- 18/07/2019
ALTER TABLE quotes ADD COLUMN quot_estimationid int;
ALTER TABLE quotes ADD FOREIGN KEY(quot_estimationid) REFERENCES estimations(ests_estimationid);
 ALTER TABLE estimations ADD COLUMN ests_rfquid int(11);
-- ALTER TABLE estimations  ADD FOREIGN KEY (ests_rfquid) REFERENCES rfqu(rfqu_rfquid);
ALTER TABLE opportunities ADD COLUMN oppo_rfquid int(11);
ALTER TABLE opportunities ADD COLUMN oppo_estimationid int(11);
ALTER TABLE opportunities  ADD FOREIGN KEY (oppo_rfquid) REFERENCES rfqu(rfqu_rfquid);

-- 8/Julio/2019 proyectos Visual
CREATE TABLE `projectsstep` (
  `spro_projectstepid` INT(11) NOT NULL AUTO_INCREMENT,
  `spro_code` VARCHAR(10) NULL,
  `spro_name` VARCHAR(100) NULL,
  `spro_ordertypeId` INT(11) NULL,
  `spro_description` VARCHAR(1000) NULL,
  `spro_usercreateid` INT(11) NULL,
  `spro_usermodifyid` INT(11) NULL,
  `spro_datecreate` DATETIME NULL,
  `spro_datemodify` DATETIME NULL,
  `spro_customerid` INT(11) NULL,
  `spro_userid` INT(11) NULL,
  `spro_lockstart` DATETIME NULL,
  `spro_lockend` DATETIME NULL,
  `spro_companyid` INT(11) NULL,
  `spro_defaultbudgetitemid` INT(11) NULL,
  `spro_defaultareaid` INT(11) NULL,
  `spro_currencyid` INT(11) NULL,
  `spro_currencyparity` DOUBLE NULL,
  `spro_status` CHAR(1) NULL,
  `spro_orderid` INT(11) NULL,
  `spro_wflowtypeid` INT(11) NULL ,
  `spro_wflowid` INT(11) ,
  PRIMARY KEY (`spro_projectstepid`),
  FOREIGN  KEY (`spro_usercreateid`) REFERENCES users(user_userid),
  FOREIGN  KEY (`spro_usermodifyid`) REFERENCES users(user_userid));

CREATE TABLE `projectactivities` (
  `prac_projectactivitiesid` INT(11) NOT NULL  AUTO_INCREMENT ,
  `prac_number` INT(11) NULL,
  `prac_profileid` INT(11) NULL,
  `prac_userid` INT(11) NULL,
  `prac_name` VARCHAR(50) NULL,
  `prac_startdate` DATETIME NULL,
  `prac_enddate` DATETIME NULL,
  `prac_estimatedhours` INT(11) NULL,
  `prac_realhours` INT(11) NULL,
  `prac_progress` INT(11) NULL,
  `prac_stepprojectid` INT(11) NULL,
  `prac_usercreateid` INT(11) NULL,
  `prac_usermodifyid` INT(11) NULL,
  `prac_datecreate` DATETIME NULL,
  `prac_datemodify` DATETIME NULL,
  `prac_wflowtypeid` INT(11) NULL ,
  `prac_wflowid` INT(11) ,
  PRIMARY KEY (`prac_projectactivitiesid`),
  FOREIGN  KEY (`prac_usercreateid`) REFERENCES users(user_userid),
  FOREIGN  KEY (`prac_usermodifyid`) REFERENCES users(user_userid));

CREATE TABLE `projectactivitiesdeps` (
  `psdp_projectstepdepid` INT(11) NOT NULL AUTO_INCREMENT,
  `psdp_projectactivitieid` INT(11) NULL,
  `psdp_usercreateid` INT(11) NULL,
  `psdp_usermodifyid` INT(11) NULL,
  `psdp_datecreate` DATETIME NULL,
  `psdp_datemodify` DATETIME NULL,
  `psdp_childprojectactivityid` INT(11) NULL,
  PRIMARY KEY (`psdp_projectstepdepid`),
  FOREIGN  KEY (`psdp_usercreateid`) REFERENCES users(user_userid),
  FOREIGN  KEY (`psdp_usermodifyid`) REFERENCES users(user_userid));
  
  ALTER TABLE ordergroups  ADD COLUMN ordg_createproject INT(11) ;

  ALTER TABLE orderitems  ADD COLUMN ordi_createproject INT(11) ;
  
-- 5/jul/2019
CREATE TABLE serviceorders (
	`sror_serviceorderid` INT(11) NOT NULL AUTO_INCREMENT,
	`sror_code` VARCHAR(10) NULL,
	`sror_activity` VARCHAR(500),
	`sror_type` CHAR(1) NOT NULL,
	`sror_startdate` DATETIME NOT NULL,
	`sror_enddate` DATETIME NOT NULL,
  	`sror_estimatetime` DOUBLE NULL,
	`sror_realtime` DOUBLE NULL,
	`sror_costperhour` DOUBLE NOT NULL,
	`sror_projectactivitiesid` INT NULL,
	`sror_userid` INT NOT NULL,
	`sror_rfquid` INT NULL,
	`sror_hasreporttime` INT NULL,
	`sror_usercreateid` INT(11),
	`sror_usermodifyid` INT(11),
	`sror_datecreate` DATETIME ,
	`sror_datemodify` DATETIME,
PRIMARY KEY (`sror_serviceorderid`),
FOREIGN  KEY (`sror_projectactivitiesid`) REFERENCES projectactivities (prac_projectactivitiesid),
FOREIGN  KEY (`sror_rfquid`) REFERENCES rfqu (rfqu_rfquid),
FOREIGN  KEY (`sror_userid`) REFERENCES users(user_userid),
FOREIGN  KEY (`sror_usercreateid`) REFERENCES users(user_userid),
FOREIGN  KEY (`sror_usermodifyid`) REFERENCES users(user_userid));

CREATE TABLE serviceorderreporttimes (
	`srrt_serviceorderreporttimeid` INT(11) NOT NULL AUTO_INCREMENT,
    `srrt_type` CHAR(1) NOT NULL,
	`srrt_serviceorderid` INT NOT NULL,
	`srrt_comments` VARCHAR(500) NOT NULL,
	`srrt_dateandtime` DATETIME NULL,
	`srrt_realtime` DOUBLE NULL,
    `srrt_childid` INT NULL,
	`srrt_usercreateid` INT(11),
	`srrt_usermodifyid` INT(11),
	`srrt_datecreate` DATETIME ,
	`srrt_datemodify` DATETIME,
PRIMARY KEY (`srrt_serviceorderreporttimeid`),
FOREIGN  KEY (`srrt_serviceorderid`) REFERENCES serviceorders (`sror_serviceorderid`),
FOREIGN  KEY (`srrt_childid`) REFERENCES serviceorderreporttimes (`srrt_serviceorderreporttimeid`),
FOREIGN  KEY (`srrt_usercreateid`) REFERENCES users(user_userid),
FOREIGN  KEY (`srrt_usermodifyid`) REFERENCES users(user_userid));

-- 08/jul/2019
ALTER TABLE opportunities ADD COLUMN oppo_commercialterms TEXT;
UPDATE opportunities SET oppo_saleprobability = 25 WHERE oppo_saleprobability = 0;

ALTER TABLE customers ADD COLUMN cust_payconditionid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_payconditionid) REFERENCES payconditions(paco_payconditionid);

ALTER TABLE opportunities ADD COLUMN oppo_payconditionid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_payconditionid) REFERENCES payconditions(paco_payconditionid);

ALTER TABLE quotes ADD COLUMN quot_payconditionid INT;
ALTER TABLE quotes ADD FOREIGN KEY (quot_payconditionid) REFERENCES payconditions(paco_payconditionid);

ALTER TABLE quotegroups ADD COLUMN qogr_payconditionid INT;
ALTER TABLE quotegroups ADD FOREIGN KEY (qogr_payconditionid) REFERENCES payconditions(paco_payconditionid);

ALTER TABLE orders ADD COLUMN orde_payconditionid INT;
ALTER TABLE orders ADD FOREIGN KEY (orde_payconditionid) REFERENCES payconditions(paco_payconditionid);

ALTER TABLE ordergroups ADD COLUMN ordg_payconditionid INT;
ALTER TABLE ordergroups ADD FOREIGN KEY (ordg_payconditionid) REFERENCES payconditions(paco_payconditionid);

-- 11/jul/2019
ALTER TABLE projectactivities ADD COLUMN prac_dependencies TEXT;
ALTER TABLE projectactivities ADD COLUMN prac_status CHAR(1);

-- 22/Julio/2019
ALTER TABLE propertiesrent ADD COLUMN `prrt_enabled` CHAR(1) NULL;
UPDATE propertiesrent SET prrt_enabled = 'E' ;
UPDATE propertiesrent SET prrt_enabled = 'D' WHERE prrt_status = 'F';

-- 24/Julio/2019
ALTER TABLE propertysaledetails 
ADD COLUMN prsd_description TEXT NULL,
ADD COLUMN prsd_descriptionlog TEXT NULL ;

-- 1/Agosto/2019
CREATE TABLE `companynomenclatures` (
  `cono_companynomenclaturesid` INT NOT NULL AUTO_INCREMENT,
  `cono_acronym` VARCHAR(5) NULL,
  `cono_companyid` INT(11) NULL,
  `cono_programid` INT(11) NULL,
  `cono_consecutive` INT(11),
  `cono_codeformatdigits` INT(11),
  `cono_usercreateid` INT(11) NULL,
  `cono_usermodifyid` INT(11) NULL,
  `cono_datecreate` DATETIME NULL,
  `cono_datemodify` DATETIME NULL,
  PRIMARY KEY (`cono_companynomenclaturesid`),
  FOREIGN KEY (cono_programid) REFERENCES programs (prog_programid),
  FOREIGN KEY (cono_companyid) REFERENCES companies (comp_companyid));

-- 1/oct/19
CREATE TABLE consultancies (
	`cons_consultancyid` INT(11) NOT NULL AUTO_INCREMENT,   
    `cons_code` VARCHAR(10) NULL,
    `cons_name` VARCHAR(100) NOT NULL,
    `cons_description` VARCHAR(500),
    `cons_ordertypeid` INT NOT NULL,
    `cons_wflowtypeid` INT,
    `cons_wflowid` INT,
    `cons_customerid` INT NOT NULL,
    `cons_userid` INT NOT NULL,
    `cons_companyid` INT NOT NULL,
    `cons_startdate` DATETIME,
    `cons_enddate` DATETIME,
    `cons_status` CHAR(1) NOT NULL,
    `cons_marketid` INT,
    `cons_currencyid` INT NOT NULL,
    `cons_currencyparity` DOUBLE,
    `cons_amount` DOUBLE,
    `cons_tax` DOUBLE,
    `cons_total` DOUBLE,
    `cons_payments` DOUBLE,
    `cons_balance` DOUBLE,
	`cons_orderid` INT,
    `cons_opportunityid` INT,
    `cons_budgetitemid` INT,
    `cons_areaid` INT,
	`cons_tags` VARCHAR(255), -- 26
    /* orderDetail */
    `cons_statusscrum` CHAR(1),
    `cons_closedate` DATE,
    `cons_orderdate` DATE,
	`cons_desiredate` DATE,
    `cons_startdatescrum` DATE,
    `cons_deliverydate` DATE,
    `cons_leaderuserid` INT,
    `cons_assigneduserid` INT,
    `cons_areaidscrum` INT,

	`cons_usercreateid` INT(11),
	`cons_usermodifyid` INT(11),
	`cons_datecreate` DATETIME ,
	`cons_datemodify` DATETIME,
 PRIMARY KEY (`cons_consultancyid`),
FOREIGN  KEY (`cons_ordertypeid`) REFERENCES ordertypes (`ortp_ordertypeid`),
FOREIGN  KEY (`cons_wflowtypeid`) REFERENCES wflowtypes (`wfty_wflowtypeid`),
FOREIGN  KEY (`cons_wflowid`) REFERENCES wflows (`wflw_wflowid`),
FOREIGN  KEY (`cons_customerid`) REFERENCES customers (`cust_customerid`),
FOREIGN  KEY (`cons_userid`) REFERENCES users (`user_userid`),
FOREIGN  KEY (`cons_companyid`) REFERENCES companies (`comp_companyid`),
FOREIGN  KEY (`cons_marketid`) REFERENCES markets (`mrkt_marketid`),
FOREIGN  KEY (`cons_currencyid`) REFERENCES currencies (`cure_currencyid`),
FOREIGN  KEY (`cons_orderid`) REFERENCES orders (`orde_orderid`),
FOREIGN  KEY (`cons_opportunityid`) REFERENCES opportunities (`oppo_opportunityid`),
FOREIGN  KEY (`cons_budgetitemid`) REFERENCES budgetitems (`bgit_budgetitemid`),
FOREIGN  KEY (`cons_areaid`) REFERENCES areas (`area_areaid`),
FOREIGN  KEY (`cons_leaderuserid`) REFERENCES users (`user_userid`),
FOREIGN  KEY (`cons_assigneduserid`) REFERENCES users (`user_userid`),
FOREIGN  KEY (`cons_areaidscrum`) REFERENCES areas(`area_areaid`),

FOREIGN  KEY (`cons_usercreateid`) REFERENCES users (`user_userid`),
FOREIGN  KEY (`cons_usermodifyid`) REFERENCES users (`user_userid`));


ALTER TABLE customers ADD COLUMN cust_extension VARCHAR(5);
ALTER TABLE customers ADD COLUMN cust_accountownerid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_accountownerid) REFERENCES users (user_userid);

ALTER TABLE customercontacts ADD COLUMN cuco_titleid INT;
ALTER TABLE customercontacts ADD FOREIGN KEY (cuco_titleid) REFERENCES titles (titl_titleid);


-- 26/diciembre/2019
ALTER TABLE customers ADD COLUMN cust_developmentrate DOUBLE;
UPDATE customers SET cust_developmentrate = cust_rate;
ALTER TABLE customers ADD COLUMN cust_formalCustomer INT;

ALTER TABLE customers ADD COLUMN  cust_potentialcustomer INT;
ALTER TABLE customers ADD COLUMN  cust_remaindpaymentraccount INT;
ALTER TABLE flexconfig ADD COLUMN flxc_daybeforeremindraccount INT ;
ALTER TABLE flexconfig ADD COLUMN flxc_remaindraccountincustomer INT ;


-- 7/01/2020
 ALTER TABLE `raccounts` CHANGE COLUMN `racc_folio` `racc_folio` VARCHAR(30) NULL DEFAULT NULL ;

-- 8 Enero 2020
ALTER TABLE propertysales ADD COLUMN prsa_hooking DOUBLE ;
ALTER TABLE propertysales ADD COLUMN prsa_deadlinepayment INT ;
ALTER TABLE propertysales ADD COLUMN prsa_payconditionid INT ;

-- 9 enero 2020
CREATE TABLE nationalities(
naty_nationalityid INT auto_increment NOT NULL,
naty_name VARCHAR(60),
naty_description VARCHAR(225),
naty_usercreateid INT ,	
naty_usermodifyid INT,
naty_datemodify datetime,
naty_datecreate datetime,
PRIMARY KEY (naty_nationalityid),
FOREIGN KEY (naty_usercreateid) REFERENCES users(user_userid),
FOREIGN KEY (naty_usermodifyid) REFERENCES users(user_userid));
  
ALTER TABLE customers ADD COLUMN cust_nationalityid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_nationalityid) REFERENCES nationalities (naty_nationalityid);
ALTER TABLE customers ADD COLUMN cust_oficialidentify VARCHAR(60);  
  
-- 13/ene/2020  
ALTER TABLE ordertypes ADD COLUMN ortp_requiredpropertymodel INT;
ALTER TABLE ordertypes ADD COLUMN ortp_defaultwflowtypeid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_defaultwflowtypeid) REFERENCES wflowtypes (wfty_wflowtypeid);
ALTER TABLE wflowsteps ADD COLUMN wfsp_emailremindercomments INT;
ALTER TABLE flexconfig ADD COLUMN flxc_enableemailremindercomments INT;
ALTER TABLE customers CHANGE COLUMN cust_potentialcustomer cust_lead INT;

-- 16/ene/2020 
ALTER TABLE projects ADD COLUMN proj_datecontract DATE;
CREATE TABLE delegations (
	dele_delegationid INT NOT NULL AUTO_INCREMENT, 
    dele_code VARCHAR(10),
    dele_name VARCHAR(50) NOT NULL,
	dele_description VARCHAR(512),
	dele_cityid INT,
    dele_usercreateid INT,
	dele_usermodifyid INT,
	dele_datecreate DATETIME,
	dele_datemodify DATETIME,
    PRIMARY KEY (dele_delegationid),
	FOREIGN KEY (dele_cityid) REFERENCES cities(city_cityid),
    FOREIGN KEY (dele_usercreateid) REFERENCES users(user_userid),
    FOREIGN KEY (dele_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE venues ADD COLUMN venu_delegationid INT;
ALTER TABLE venues ADD FOREIGN KEY (venu_delegationid) REFERENCES delegations(dele_delegationid);

-- 17/ene/2020
ALTER TABLE bankmovements ADD COLUMN bkmv_file INT;

-- 31/ene/2020
ALTER TABLE quotegroups MODIFY COLUMN qogr_description VARCHAR(512);
ALTER TABLE ordergroups MODIFY COLUMN ordg_description VARCHAR(512);

-- 4/feb/2020
ALTER TABLE products MODIFY COLUMN prod_description VARCHAR(500);


-- 4/feb/2020
ALTER TABLE consultancies ADD COLUMN cons_customerrequisition VARCHAR(20);
UPDATE consultancies LEFT JOIN orders ON (orde_orderid = cons_orderid) SET cons_customerrequisition = orde_customerrequisition;
ALTER TABLE consultancies ADD COLUMN cons_customercontactid VARCHAR(20);
UPDATE consultancies LEFT JOIN orders ON (orde_orderid = cons_orderid) SET cons_customercontactid = orde_customercontactid;

 -- 7/feb/2020
 
ALTER TABLE `propertytax` 
ADD COLUMN `prtx_status` VARCHAR(1);

-- 11/feb/2020
CREATE TABLE categoryforecasts (
	cafo_categoryforecastid INT NOT NULL AUTO_INCREMENT, 
    cafo_name VARCHAR(50) NOT NULL,
	cafo_description VARCHAR(512),
    cafo_usercreateid INT,
	cafo_usermodifyid INT,
	cafo_datecreate DATETIME,
	cafo_datemodify DATETIME,
    PRIMARY KEY (cafo_categoryforecastid),
    FOREIGN KEY (cafo_usercreateid) REFERENCES users(user_userid),
    FOREIGN KEY (cafo_usermodifyid) REFERENCES users(user_userid)
);

ALTER TABLE opportunities ADD COLUMN oppo_categoryforecastid INT;
ALTER TABLE opportunities ADD FOREIGN KEY (oppo_categoryforecastid) REFERENCES categoryforecasts(cafo_categoryforecastid);

-- 12/feb/2020
ALTER TABLE flexconfig ADD COLUMN flxc_showorderincustomer INT;

-- 13/feb/2020
ALTER TABLE propertysales ADD COLUMN prsa_datecontract DATE;
ALTER TABLE propertysales ADD COLUMN prsa_datekeep DATE;

-- 19- feb - 2020
ALTER TABLE flexconfig ADD COLUMN flxc_sendemailauthorizedmb INT;
ALTER TABLE flexconfig ADD COLUMN flxc_daybeforeremindraccounttwo INT;

-- 21/feb/2020
ALTER TABLE flexconfig ADD COLUMN flxc_emailfailcron VARCHAR(50);

-- 26/feb/2020
ALTER TABLE categoryforecasts ADD COLUMN cafo_statusopportunity CHAR(1);

-- 28/feb/2020
ALTER TABLE raccounttypes ADD COLUMN ract_foliozeros INT;

-- 28/feb/2020
CREATE TABLE maritalstatus (
 mast_maritalstatusid INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
 mast_name VARCHAR(80) ,
 mast_description VARCHAR(512),
 mast_usercreateid INT,
 mast_usermodifyid INT,
 mast_datecreate DATETIME,
 mast_datemodify DATETIME,
 FOREIGN KEY (mast_usermodifyid) REFERENCES users(user_userid),    
 FOREIGN KEY (mast_usercreateid) REFERENCES users(user_userid)
);


ALTER TABLE customers ADD COLUMN  cust_maritalstatusid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_maritalstatusid) REFERENCES maritalstatus(mast_maritalstatusid);
ALTER TABLE maritalstatus ADD FOREIGN KEY (mast_usercreateid) REFERENCES users(user_userid);
ALTER TABLE maritalstatus ADD FOREIGN KEY (mast_usermodifyid) REFERENCES users(user_userid);


ALTER TABLE flexconfig ADD COLUMN  flxc_showowncustomer INT;

-- inicio Aplicar solo una vez
INSERT INTO maritalstatus (mast_name, mast_description) value ('Soltero', 'Soltero');
INSERT INTO maritalstatus (mast_name, mast_description) value ('Casado', 'Casado');
INSERT INTO maritalstatus (mast_name, mast_description) value ('Unión Libre', 'Unión Libre');

update customers left join maritalstatus on (mast_maritalstatusid = cust_maritalstatusid)
set cust_maritalstatusid  = 1 WHERE  cust_maritalstatus = 'S';
update customers left join maritalstatus on (mast_maritalstatusid = cust_maritalstatusid)
set cust_maritalstatusid  = 2 WHERE  cust_maritalstatus = 'M';
update customers left join maritalstatus on (mast_maritalstatusid = cust_maritalstatusid)
set cust_maritalstatusid  = 3 WHERE  cust_maritalstatus = 'U';
-- Fin aplicar solo una vez

-- 04/mar/2020
ALTER TABLE flexconfig ADD COLUMN flxc_duplicateaddress INT(11);
ALTER TABLE flexconfig ADD COLUMN flxc_duplicateaddressnumber INT(11);

-- 11/mar/2020
CREATE TABLE companysalesmen (
	cosa_companysalesmanid INT NOT NULL AUTO_INCREMENT, 
    cosa_usercreateid INT,
	cosa_companyid INT,
    cosa_profileid INT,	
	cosa_flexconfigid INT,
	cosa_usermodifyid INT,
	cosa_datecreate DATETIME,
	cosa_datemodify DATETIME,
    PRIMARY KEY (cosa_companysalesmanid),
    FOREIGN KEY (cosa_usercreateid) REFERENCES `users` (user_userid),
    FOREIGN KEY (cosa_usermodifyid) REFERENCES `users` (user_userid),
    FOREIGN KEY (cosa_companyid) REFERENCES `companies`(comp_companyid),
    FOREIGN KEY (cosa_profileid) REFERENCES `profiles` (prof_profileid),
    FOREIGN KEY (cosa_flexconfigid) REFERENCES `flexconfig` (flxc_flexconfigid)
);

CREATE TABLE companycollectionprofiles (
	cocp_companycollectionprofileid INT NOT NULL AUTO_INCREMENT, 
	cocp_companyid INT,
	cocp_flexconfigid INT,
	cocp_collectprofileid INT,
	cocp_sendemailauthorizedmb INT, 
	cocp_remaindraccountincustomer INT,
	cocp_daybeforeremindraccount INT,
	cocp_daybeforeremindraccounttwo INT,
    cocp_usercreateid INT,
	cocp_usermodifyid INT,
	cocp_datecreate DATETIME,
	cocp_datemodify DATETIME,
    PRIMARY KEY (cocp_companycollectionprofileid),
    FOREIGN KEY (cocp_usercreateid) REFERENCES `users` (user_userid),
    FOREIGN KEY (cocp_usermodifyid) REFERENCES `users` (user_userid),
    FOREIGN KEY (cocp_companyid) REFERENCES `companies` (comp_companyid),
    FOREIGN KEY (cocp_collectprofileid) REFERENCES `profiles` (prof_profileid),
    FOREIGN KEY (cocp_flexconfigid) REFERENCES `flexconfig` (flxc_flexconfigid)
);

-- 17/mar/2020
ALTER TABLE flexconfig add COLUMN  flxc_multicompany INT(11);

-- 17/marzo/2020
CREATE TABLE `wflowformats` (
  `wfft_wflowformatid` INT NOT NULL AUTO_INCREMENT,
  `wfft_code` VARCHAR(10) NULL,
  `wfft_name` VARCHAR(30) NULL,
  `wfft_description` VARCHAR(255) NULL,
  `wfft_link` VARCHAR(50) NULL,
  `wfft_hasconsecutive` INT(5) NULL,
  `wfft_multipleprint` INT(5) NULL,
  `wfft_publishvalidateclass` VARCHAR(50) NULL,
  `wfft_sequence` INT(5) NULL,
  `wfft_wflowtypeid` INT(10) NULL,
  `wfft_usercreateid` INT(10) NULL,
  `wfft_usermodifyid` INT(10) NULL,
  `wfft_datecreate` DATETIME NULL,
  `wfft_datemodify` DATETIME NULL,
  PRIMARY KEY (`wfft_wflowformatid`),
  FOREIGN KEY (`wfft_usercreateid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wfft_usermodifyid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wfft_wflowtypeid`) REFERENCES wflowtypes(wfty_wflowtypeid));

CREATE TABLE `wflowformatcompanies` (
  `wffc_wflowformatcompanyid` INT(10) NOT NULL AUTO_INCREMENT,
  `wffc_wflowformatid` INT(10) NULL,
  `wffc_companyid` INT(10) NULL,
  `wffc_usercreateid` INT(10) NULL,
  `wffc_usermodifyid` INT(10) NULL,
  `wffc_datecreate` DATETIME NULL,
  `wffc_datemodify` DATETIME NULL,
  PRIMARY KEY (`wffc_wflowformatcompanyid`),
  FOREIGN KEY (`wffc_wflowformatid`) REFERENCES wflowformats(wfft_wflowformatid),
  FOREIGN KEY (`wffc_usercreateid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wffc_usermodifyid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wffc_companyid`) REFERENCES companies(comp_companyid));
  

CREATE TABLE `wflowtypecompanies` (
  `wftc_wflowtypecompanyid` INT(10) NOT NULL AUTO_INCREMENT,
  `wftc_wflowtypeid` INT(10) NULL,
  `wftc_companyid` INT(10) NULL,
  `wftc_usercreateid` INT(10) NULL,
  `wftc_usermodifyid` INT(10) NULL,
  `wftc_datecreate` DATETIME NULL,
  `wftc_datemodify` DATETIME NULL,
  PRIMARY KEY (`wftc_wflowtypecompanyid`),
  FOREIGN KEY (`wftc_wflowtypeid`) REFERENCES wflowtypes(wfty_wflowtypeid),
  FOREIGN KEY (`wftc_usercreateid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wftc_usermodifyid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wftc_companyid`) REFERENCES companies(comp_companyid));
 


CREATE TABLE `wflowcategorycompanies` (
  `wfcc_wflowcategorycompanyid` INT(10) NOT NULL AUTO_INCREMENT,
  `wfcc_wflowcategoryid` INT(10) NULL,
  `wfcc_companyid` INT(10) NULL,
  `wfcc_usercreateid` INT(10) NULL,
  `wfcc_usermodifyid` INT(10) NULL,
  `wfcc_datecreate` DATETIME NULL,
  `wfcc_datemodify` DATETIME NULL,
  PRIMARY KEY (`wfcc_wflowcategorycompanyid`),
   FOREIGN KEY (`wfcc_wflowcategoryid`) REFERENCES wflowcategories(wfca_wflowcategoryid),
  FOREIGN KEY (`wfcc_usercreateid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wfcc_usermodifyid`) REFERENCES users(user_userid),
  FOREIGN KEY (`wfcc_companyid`) REFERENCES companies(comp_companyid));

  
  ALTER TABLE wflowcategories ADD column wfca_companyid INT(11);
  ALTER TABLE wflowcategories FOREIGN KEY (wfca_companyid) REFERENCES companies(comp_companyid);

  ALTER TABLE wflowformats ADD COLUMN wfft_companyid INT(11);
   ALTER TABLE wflowcategories add FOREIGN KEY (wfca_companyid) REFERENCES companies(comp_companyid);

  
   -- 27 marzo 2020 se borraran las siguientes llaves. Al crear un tipo,categoria o formato se quedaba trabajando
  --  y al final exedia tiempo y creaba el registro sin empresa 
ALTER TABLE `wflowformatcompanies` 
	DROP FOREIGN KEY `wflowformatcompanies_ibfk_4`;
	ALTER TABLE `wflowformatcompanies` 
	DROP INDEX `wffc_wflowformatid` ;

ALTER TABLE `wflowtypecompanies` 
	DROP FOREIGN KEY `wflowtypecompanies_ibfk_4`;
	ALTER TABLE `wflowtypecompanies` 
	DROP INDEX `wftc_wflowtypeid` ;

ALTER TABLE `wflowcategorycompanies` 
	DROP FOREIGN KEY `wflowcategorycompanies_ibfk_4`;
	ALTER TABLE `wflowcategorycompanies` 
	DROP INDEX `wfcc_wflowcategoryid` ;
	
-- 30 de Marzo 2020
ALTER TABLE `paccounts` 
	ADD COLUMN `pacc_discount` DOUBLE NULL ;
	
ALTER TABLE `paccountitems` 
	ADD COLUMN `pait_discount` DOUBLE NULL ;
	
-- 16 abril 2020
ALTER TABLE flexconfig ADD COLUMN flxc_mobilormailcust INT(1);

-- 17/abr/2020
ALTER TABLE companysalesmen ADD COLUMN cosa_coordinatorprofileid INT;
ALTER TABLE companysalesmen ADD FOREIGN KEY (cosa_coordinatorprofileid) REFERENCES `profiles`(prof_profileid);

CREATE TABLE assigncoordinators (
	`assc_assingcoordinatorid` INT(11) NOT NULL AUTO_INCREMENT,
    `assc_companyid` INT NOT NULL,
	`assc_userid` INT NOT NULL,
    `assc_assigned` INT NULL,
    `assc_fullassignment` INT NULL,
	`assc_countassigned` INT NULL,
	`assc_queuedcoordinator` INT NULL,
	`assc_enable` INT NULL,
	`assc_usercreateid` INT(11),
	`assc_usermodifyid` INT(11),
	`assc_datecreate` DATETIME ,
	`assc_datemodify` DATETIME,
PRIMARY KEY (`assc_assingcoordinatorid`),
FOREIGN KEY (`assc_userid`) REFERENCES users (user_userid),
FOREIGN KEY (`assc_companyid`) REFERENCES companies (comp_companyid),
FOREIGN KEY (`assc_usercreateid`) REFERENCES users (user_userid),
FOREIGN KEY (`assc_usermodifyid`) REFERENCES users (user_userid));

CREATE TABLE assignsalesmen (
	`assa_assignsalesmanid` INT(11) NOT NULL AUTO_INCREMENT,
	`assa_assingcoordinatorid` INT NOT NULL,
	`assa_userid` INT NOT NULL,
    `assa_assigned` INT NULL,
	`assa_countassigned` INT NULL,
   	`assa_queueduser` INT NULL,
	`assa_enable` INT NULL,
	`assa_usercreateid` INT(11),
	`assa_usermodifyid` INT(11),
	`assa_datecreate` DATETIME ,
	`assa_datemodify` DATETIME,
PRIMARY KEY (`assa_assignsalesmanid`),
FOREIGN KEY (`assa_userid`) REFERENCES users (user_userid),
FOREIGN KEY (`assa_assingcoordinatorid`) REFERENCES assigncoordinators (assc_assingcoordinatorid),
FOREIGN KEY (`assa_usercreateid`) REFERENCES users (user_userid),
FOREIGN KEY (`assa_usermodifyid`) REFERENCES users (user_userid));

-- 27/abr/2020
ALTER TABLE productprices ADD COLUMN prpc_wflowtypeid INT;
ALTER TABLE productprices ADD FOREIGN KEY (prpc_wflowtypeid) REFERENCES wflowtypes(wfty_wflowtypeid);

ALTER TABLE customers ADD COLUMN cust_marketid INT;
ALTER TABLE customers ADD FOREIGN KEY (cust_marketid) REFERENCES markets(mrkt_marketid);

ALTER TABLE wflowcategories ADD FOREIGN KEY (wfca_companyid) REFERENCES companies(comp_companyid);

-- 28 Abril 2020
ALTER TABLE products ADD COLUMN prod_weight double;
ALTER TABLE products ADD COLUMN prod_cubicmeter double;
ALTER TABLE products ADD COLUMN prod_dimensionheight double;
ALTER TABLE products ADD COLUMN prod_dimensionwidth double;
ALTER TABLE products ADD COLUMN prod_dimensionlength double;
ALTER TABLE products ADD COLUMN prod_amperage double;
ALTER TABLE products ADD COLUMN prod_usecase INT(1);
ALTER TABLE products ADD COLUMN prod_quantityforcase INT(11);
ALTER TABLE products ADD COLUMN prod_weightcase double;
ALTER TABLE products ADD COLUMN prod_caselength double;
ALTER TABLE products ADD COLUMN prod_caseheight double;
ALTER TABLE products ADD COLUMN prod_casewidth double;
ALTER TABLE products ADD COLUMN prod_casecubicmeter double;

ALTER TABLE paccounts ADD COLUMN pacc_isxml INT(1);
ALTER TABLE paccountitems ADD pait_tax DOUBLE;

CREATE TABLE factortypes (
	faty_factortypeid  int(11) NOT NULL AUTO_INCREMENT,
    faty_code VARCHAR(6),
	faty_usercreateid int(11) ,
	faty_usermodifyid int(11),
	faty_datecreate datetime ,
	faty_datemodify datetime ,
    PRIMARY KEY (faty_factortypeid),
    FOREIGN KEY (faty_usermodifyid) REFERENCES users(user_userid),    
	FOREIGN KEY (faty_usercreateid) REFERENCES users(user_userid)
 );
 

 
  CREATE TABLE taxs (
	taxs_taxid  int(11) NOT NULL AUTO_INCREMENT,
    taxs_code VARCHAR(3),
    taxs_descrition VARCHAR(10),
	taxs_usercreateid int(11) ,
	taxs_usermodifyid int(11),
	taxs_datecreate datetime ,
	taxs_datemodify datetime ,
    PRIMARY KEY (taxs_taxid),
    FOREIGN KEY (taxs_usermodifyid) REFERENCES users(user_userid),    
	FOREIGN KEY (taxs_usercreateid) REFERENCES users(user_userid)
 );
 
   CREATE TABLE rateorfees (
	rafe_rateorfeesid  int(11) NOT NULL AUTO_INCREMENT,
    rafe_code VARCHAR(10),
    rafe_maxvalue double,
    rafe_taxid INT(10),
    rafe_factortypeid int(10),
    rafe_companyid int(10),
	rafe_usercreateid int(11) ,
	rafe_usermodifyid int(11),
	rafe_datecreate datetime ,
	rafe_datemodify datetime ,
    PRIMARY KEY (rafe_rateorfeesid),
    FOREIGN KEY (rafe_usermodifyid) REFERENCES users(user_userid),    
	FOREIGN KEY (rafe_usercreateid) REFERENCES users(user_userid),
	FOREIGN KEY (rafe_taxid) REFERENCES taxs(taxs_taxid),
	FOREIGN KEY (rafe_factortypeid) REFERENCES factortypes(faty_factortypeid),
    FOREIGN KEY (rafe_companyid) REFERENCES companies(comp_companyid) 
 );
 
 ALTER TABLE rateorfees ADD COLUMN rafe_transfer INT(1);
ALTER TABLE rateorfees ADD COLUMN rafe_retention INT(1);
ALTER TABLE paccountitems ADD COLUMN pait_sumretention double;
ALTER TABLE paccounts ADD COLUMN pacc_sumretention double;

-- 27 de mayo 2020
ALTER TABLE products CHANGE COLUMN prod_amperage prod_amperage110  DOUBLE;

ALTER TABLE products ADD prod_amperage220 DOUBLE;

-- 9 Junio 2020
ALTER TABLE venues add venu_blueprint VARCHAR(400) ;
 -- 23 de junio 2020
 ALTER TABLE venues CHANGE COLUMN venu_street venu_street VARCHAR(80)  ;
 ALTER TABLE venues CHANGE COLUMN venu_name venu_name VARCHAR(50)  ;

 -- 8 julio 2020
ALTER TABLE wflowcategories ADD wfca_daysremindexpired INT(5);

ALTER TABLE ordergroups ADD ordg_showinfkit INT(8);
ALTER TABLE quotegroups ADD qogr_showinfkit INT(8);

ALTER TABLE requisitions ADD reqi_oportunityid INT(8);

-- 16/julio/2020
ALTER TABLE ordergroups CHANGE COLUMN ordg_showinfkit ordg_showitems INT (8) ;
ALTER TABLE quotegroups CHANGE COLUMN qogr_showinfkit qogr_showitems INT (8) ;

--17/julio/2020
ALTER TABLE projects ADD proj_total double; 
UPDATE projects LEFT JOIN orders ON (orde_orderid = proj_orderid) SET proj_total = orde_total;

-- 5/agosto/2020
	ALTER TABLE quotegroups ADD qogr_days double;
	ALTER TABLE quotegroups ADD qogr_total double;
	
	ALTER TABLE ordergroups ADD ordg_days double;
	ALTER TABLE ordergroups ADD ordg_total double;
	
	ALTER TABLE whsections ADD whse_status CHAR(1);
	-- Actualizar secciones con nuevo estatus
	UPDATE whsections 
	LEFT JOIN orders on (whse_orderid = orde_orderid) 
	LEFT JOIN projects ON (orde_orderid = proj_orderid) 
	SET whse_status = 'N' WHERE whse_orderid > 0 AND (proj_status = 'F' OR proj_status = 'C');
	
	UPDATE whsections 
	LEFT JOIN orders on (whse_orderid = orde_orderid) 
	LEFT JOIN projects ON (orde_orderid = proj_orderid) 
	SET whse_status = 'A' WHERE whse_orderid > 0 AND (proj_status <> 'F' OR proj_status <> 'C');
		
	UPDATE whsections 
	LEFT JOIN orders on (whse_orderid = orde_orderid) 
	LEFT JOIN projects ON (orde_originreneworderid = proj_orderid) 
	SET whse_status = 'A' WHERE whse_orderid > 0 AND (proj_status <> 'F' OR proj_status <> 'C');
	
	UPDATE whsections 
	LEFT JOIN orders on (whse_orderid = orde_orderid) 
	LEFT JOIN projects ON (orde_originreneworderid = proj_orderid) 
	SET whse_status = 'N' WHERE whse_orderid > 0 AND (proj_status = 'F' OR proj_status = 'C');

	UPDATE whsections LEFT JOIN warehouses ON (whse_warehouseid = ware_warehouseid) SET whse_status = 'A' WHERE ware_type = 'N';
	
	CREATE TABLE extraorderprofiles (
		eopr_extraorderprofileid INT NOT NULL AUTO_INCREMENT, 
		eopr_usercreateid INT,		
		eopr_usermodifyid INT,
		eopr_datecreate DATETIME,
		eopr_datemodify DATETIME,
		eopr_profileid INT,	
	    eopr_ordertypeid INT,
		PRIMARY KEY(eopr_extraorderprofileid),
		FOREIGN KEY (eopr_usercreateid) REFERENCES users(user_userid),
		FOREIGN KEY (eopr_usermodifyid) REFERENCES users(user_userid),
	    FOREIGN KEY (eopr_profileid) REFERENCES profiles(prof_profileid),
	    FOREIGN KEY (eopr_ordertypeid) REFERENCES ordertypes(ortp_ordertypeid)
	);
