-- SCRIPT PARA CAMBIAR DE MODULOS A PROGRAMAS, 

-- borrar campos de modulos
ALTER TABLE sfcaptions DROP FOREIGN KEY sfcaptions_ibfk_1;
ALTER TABLE `sfcaptions` CHANGE COLUMN `sfcp_moduleid` `sfcp_moduleid` INT(11) NULL ;

-- RENAME TABLE programs TO courseprograms;
CREATE TABLE `programs` (
  `prog_programid` int(11) NOT NULL AUTO_INCREMENT,
  `prog_code` varchar(6) DEFAULT NULL,
  `prog_name` varchar(30) DEFAULT NULL,
  `prog_description` varchar(255) DEFAULT NULL,
  `prog_type` char DEFAULT NULL,
  `prog_index` varchar(50) DEFAULT NULL,
  `prog_uientryclass` varchar(50) DEFAULT NULL,
  `prog_image` varchar(50) DEFAULT NULL,
  `prog_listtitle` varchar(30) DEFAULT NULL,
  `prog_formtitle` varchar(30) DEFAULT NULL,
  `prog_ispublic` int(11) DEFAULT NULL,
  `prog_parentid` int(11) DEFAULT NULL,
  `prog_enableformats` int(11) DEFAULT NULL,
  `prog_enablefiles` int(11) DEFAULT NULL,
  `prog_enableaudit` int(11) DEFAULT NULL,
  `prog_usercreateid` int(11) DEFAULT NULL,
  `prog_usermodifyid` int(11) DEFAULT NULL,
  `prog_datecreate` datetime DEFAULT NULL,
  `prog_datemodify` datetime DEFAULT NULL,
  `prog_menuid` int(11) DEFAULT NULL,
  `prog_menuindex` int(11) DEFAULT NULL,
  `prog_enablemobile` int(11) DEFAULT NULL,
  `prog_enablehelp` int(11) DEFAULT NULL,
  `prog_helptext` varchar(10000) DEFAULT NULL,
  `prog_helplink` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`prog_programid`),
  KEY `prog_parentid` (`prog_programid`),
  KEY `prog_usercreateid` (`prog_usercreateid`),
  KEY `prog_usermodifyid` (`prog_usermodifyid`),
  KEY `prog_menuid` (`prog_menuid`),
  CONSTRAINT `programs_ibfk_1` FOREIGN KEY (`prog_parentid`) REFERENCES `programs` (`prog_programid`),
  CONSTRAINT `programs_ibfk_2` FOREIGN KEY (`prog_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `programs_ibfk_3` FOREIGN KEY (`prog_usermodifyid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `programs_ibfk_4` FOREIGN KEY (`prog_menuid`) REFERENCES `menus` (`menu_menuid`)
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=utf8;


CREATE TABLE `profiles` (
  `prof_profileid` int(11) NOT NULL AUTO_INCREMENT,
  `prof_name` varchar(30) DEFAULT NULL,
  `prof_description` varchar(255) DEFAULT NULL,
  `prof_price` double DEFAULT NULL,
  `prof_cost` double DEFAULT NULL,
  `prof_usercreateid` int(11) DEFAULT NULL,
  `prof_usermodifyid` int(11) DEFAULT NULL,
  `prof_datecreate` datetime DEFAULT NULL,
  `prof_datemodify` datetime DEFAULT NULL,
  PRIMARY KEY (`prof_profileid`),
  KEY `prof_usercreateid` (`prof_usercreateid`),
  KEY `prof_usermodifyid` (`prof_usermodifyid`),
  CONSTRAINT `profiles_ibfk_1` FOREIGN KEY (`prof_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `profiles_ibfk_2` FOREIGN KEY (`prof_usermodifyid`) REFERENCES `users` (`user_userid`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;


CREATE TABLE `programprofiles` (
  `pgpf_programprofileid` int(11) NOT NULL AUTO_INCREMENT,
  `pgpf_read` int(11) DEFAULT NULL,
  `pgpf_write` int(11) DEFAULT NULL,
  `pgpf_delete` int(11) DEFAULT NULL,
  `pgpf_print` int(11) DEFAULT NULL,
  `pgpf_alldata` int(11) DEFAULT NULL,
  `pgpf_profileid` int(11) NOT NULL,
  `pgpf_programid` int(11) DEFAULT NULL,
  `pgpf_usercreateid` int(11) DEFAULT NULL,
  `pgpf_usermodifyid` int(11) DEFAULT NULL,
  `pgpf_datecreate` datetime DEFAULT NULL,
  `pgpf_datemodify` datetime DEFAULT NULL,
  `pgpf_menu` int(11) DEFAULT NULL,
  PRIMARY KEY (`pgpf_programprofileid`),
  KEY `pgpf_profileid` (`pgpf_profileid`),
  KEY `pgpf_programid` (`pgpf_programid`),
  KEY `pgpf_usercreateid` (`pgpf_usercreateid`),
  KEY `pgpf_usermodifyid` (`pgpf_usermodifyid`),
  CONSTRAINT `programprofiles_ibfk_1` FOREIGN KEY (`pgpf_profileid`) REFERENCES `profiles` (`prof_profileid`),
  CONSTRAINT `programprofiles_ibfk_2` FOREIGN KEY (`pgpf_programid`) REFERENCES `programs` (`prog_programid`),
  CONSTRAINT `programprofiles_ibfk_3` FOREIGN KEY (`pgpf_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `programprofiles_ibfk_4` FOREIGN KEY (`pgpf_usermodifyid`) REFERENCES `users` (`user_userid`)
) ENGINE=InnoDB AUTO_INCREMENT=1070 DEFAULT CHARSET=utf8;

CREATE TABLE `profileusers` (
  `pfus_profileuserid` int(11) NOT NULL AUTO_INCREMENT,
  `pfus_profileid` int(11) NOT NULL,
  `pfus_userid` int(11) NOT NULL,
  `pfus_usercreateid` int(11) DEFAULT NULL,
  `pfus_usermodifyid` int(11) DEFAULT NULL,
  `pfus_datecreate` datetime DEFAULT NULL,
  `pfus_datemodify` datetime DEFAULT NULL,
  `pfus_defaultuser` int(11) DEFAULT NULL,
  PRIMARY KEY (`pfus_profileuserid`),
  KEY `pfus_profileid` (`pfus_profileid`),
  KEY `pfus_userid` (`pfus_userid`),
  KEY `pfus_usercreateid` (`pfus_usercreateid`),
  KEY `pfus_usermodifyid` (`pfus_usermodifyid`),
  CONSTRAINT `profileusers_ibfk_1` FOREIGN KEY (`pfus_profileid`) REFERENCES `profiles` (`prof_profileid`),
  CONSTRAINT `profileusers_ibfk_2` FOREIGN KEY (`pfus_userid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `profileusers_ibfk_3` FOREIGN KEY (`pfus_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `profileusers_ibfk_4` FOREIGN KEY (`pfus_usermodifyid`) REFERENCES `users` (`user_userid`)
) ENGINE=InnoDB AUTO_INCREMENT=1371 DEFAULT CHARSET=utf8;


CREATE TABLE `programspecials` (
  `pgsp_programspecialid` int(11) NOT NULL AUTO_INCREMENT,
  `pgsp_code` varchar(10) NOT NULL,
  `pgsp_name` varchar(30) NOT NULL,
  `pgsp_description` varchar(255) DEFAULT NULL,
  `pgsp_programid` int(11) NOT NULL,
  `pgsp_usercreateid` int(11) DEFAULT NULL,
  `pgsp_usermodifyid` int(11) DEFAULT NULL,
  `pgsp_datecreate` datetime DEFAULT NULL,
  `pgsp_datemodify` datetime DEFAULT NULL,
  PRIMARY KEY (`pgsp_programspecialid`),
  KEY `pgsp_programid` (`pgsp_programid`),
  KEY `pgsp_usercreateid` (`pgsp_usercreateid`),
  KEY `pgsp_usermodifyid` (`pgsp_usermodifyid`),
  CONSTRAINT `programspecials_ibfk_1` FOREIGN KEY (`pgsp_programid`) REFERENCES `programs` (`prog_programid`),
  CONSTRAINT `programspecials_ibfk_2` FOREIGN KEY (`pgsp_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `programspecials_ibfk_3` FOREIGN KEY (`pgsp_usermodifyid`) REFERENCES `users` (`user_userid`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;


CREATE TABLE `programprofilespecials` (
  `pgps_programprofilespecialid` int(11) NOT NULL AUTO_INCREMENT,
  `pgps_programprofileid` int(11) NOT NULL,
  `pgps_programspecialid` int(11) NOT NULL,
  `pgps_usercreateid` int(11) DEFAULT NULL,
  `pgps_usermodifyid` int(11) DEFAULT NULL,
  `pgps_datecreate` datetime DEFAULT NULL,
  `pgps_datemodify` datetime DEFAULT NULL,
  PRIMARY KEY (`pgps_programprofilespecialid`),
  KEY `pgps_programprofileid` (`pgps_programprofileid`),
  KEY `pgps_programspecialid` (`pgps_programspecialid`),
  KEY `pgps_usercreateid` (`pgps_usercreateid`),
  KEY `pgps_usermodifyid` (`pgps_usermodifyid`),
  CONSTRAINT `programprofilespecials_ibfk_1` FOREIGN KEY (`pgps_programprofileid`) REFERENCES `programprofiles` (`pgpf_programprofileid`),
  CONSTRAINT `programprofilespecials_ibfk_2` FOREIGN KEY (`pgps_programspecialid`) REFERENCES `programspecials` (`pgsp_programspecialid`),
  CONSTRAINT `programprofilespecials_ibfk_3` FOREIGN KEY (`pgps_usercreateid`) REFERENCES `users` (`user_userid`),
  CONSTRAINT `programprofilespecials_ibfk_4` FOREIGN KEY (`pgps_usermodifyid`) REFERENCES `users` (`user_userid`)
) ENGINE=InnoDB AUTO_INCREMENT=354 DEFAULT CHARSET=utf8;

-- crear campos necesarios
-- Agregar campo de programa inicial
ALTER TABLE sfconfig ADD COLUMN sfcf_startprogramid INT;
ALTER TABLE sfconfig ADD FOREIGN KEY (sfcf_startprogramid) REFERENCES programs(prog_programid);

ALTER TABLE sfcaptions ADD COLUMN sfcp_programid INT;
ALTER TABLE sfcaptions ADD FOREIGN KEY (sfcp_programid) REFERENCES programs(prog_programid);

ALTER TABLE users ADD COLUMN user_startprogramid INT;
ALTER TABLE users ADD FOREIGN KEY (user_startprogramid) REFERENCES programs(prog_programid);

ALTER TABLE formats ADD COLUMN frmt_programid INT;
ALTER TABLE formats ADD FOREIGN KEY (frmt_programid) REFERENCES programs(prog_programid);

ALTER TABLE sflogs ADD COLUMN sflg_programid INT;
ALTER TABLE sflogs ADD FOREIGN KEY (sflg_programid) REFERENCES programs(prog_programid);


DELETE FROM programs WHERE prog_programid > 0;
ALTER TABLE programs AUTO_INCREMENT = 1;

ALTER TABLE flexconfig ADD COLUMN flxc_salesprofileid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_salesprofileid) REFERENCES profiles(prof_profileid);

ALTER TABLE flexconfig ADD COLUMN flxc_collectprofileid INT;
ALTER TABLE flexconfig ADD FOREIGN KEY (flxc_collectprofileid) REFERENCES profiles(prof_profileid);

/* 
 * En LOBA es:
ALTER TABLE `flexconfig` DROP FOREIGN KEY `flexconfig_ibfk_25`;
ALTER TABLE `flexconfig` DROP INDEX `flxc_salesgroupid` ;
*/

ALTER TABLE `flexconfig` DROP FOREIGN KEY `flexconfig_ibfk_21`,
DROP FOREIGN KEY `flexconfig_ibfk_18`,
DROP FOREIGN KEY `flexconfig_ibfk_17`;

ALTER TABLE `flexconfig` DROP INDEX `flxc_salesgroupid`;
ALTER TABLE `flexconfig` DROP INDEX `flxc_collectgroupid` ;

INSERT INTO programs (prog_code, prog_name) VALUES ('PROG', 'Programas'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('PROF', 'Perfiles'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('PGPF', 'Perfiles Programa'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('PGSP', 'Accesos Especiales'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('PGPS', 'Acceso Especial Perf.'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('PFUS', 'Usuarios de Perfil'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('CAFI', 'Catálogos Finanzas'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('CACO', 'Catálogos Comerciales'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('CADV', 'Catálogos Obra'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('FXLC', 'FlexWM Config.'); 
INSERT INTO programs (prog_code, prog_name) VALUES ('SFCF', 'SYMGF Config.'); 

-- EJECUTAR /batch/prog_moduleimport.jsp, primero haciendo login.jsp standalone, 
-- asegurar que este el password asignado al usuario de login

-- SE DEBE AGREGAR EL PERMISO DE PROGRAMAS
INSERT INTO profileusers (pfus_profileid, pfus_userid) VALUES (71, 1);

-- PERMISO PARA PROGRAMAS (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (1, 71, 1, 1, 1, 1);

-- PERMISO PARA PERFILES (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (2, 71, 1, 1, 1, 1);

-- PERMISO PARA PERFILES DE PROGRAMA (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (3, 71, 1, 1, 1, 1);

-- PERMISO PARA USUARIOS DE PERFIL (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (4, 71, 1, 1, 1, 1);

-- PERMISO PARA ACCESOS ESPECIALES (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (5, 71, 1, 1, 1, 1);

-- PERMISO PARA ACCESOS ESPECIALES PERFIL (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (6, 71, 1, 1, 1, 1);

INSERT INTO programs (prog_code, prog_name) VALUES ('WFPU', 'Perfiles WFlows'); 

-- PERMISO PARA ACCESOS ESPECIALES PERFIL (REVISAR IDS REALES)
INSERT INTO programprofiles (pgpf_programid, pgpf_profileid, pgpf_read, pgpf_write, pgpf_delete, pgpf_menu) 
VALUES (7, 71, 1, 1, 1, 1);

-- EJECUTAR batch/flex_migrationprograms.jsp

-- REVISAR QUE TODO ESTÉ FUNCIONANDO AL 100%

-- REVISAR DEPENDENCIAS DE LAS SIGUIENTES TABLAS

-- WFLOWCATEGORIES
-- Cambiar los IDs ligados a los modulos, hacia los programas
ALTER TABLE wflowcategories ADD COLUMN wfca_programid INT;
ALTER TABLE wflowcategories ADD FOREIGN KEY (wfca_programid) REFERENCES programs(prog_programid);


-- Cambiar sffiles a program id
ALTER TABLE sffiles ADD COLUMN file_programid INT;
ALTER TABLE sffiles ADD FOREIGN KEY (file_programid) REFERENCES programs(prog_programid);

ALTER TABLE `sffiles` DROP FOREIGN KEY `sffiles_ibfk_1`, DROP FOREIGN KEY `sffiles_ibfk_4`;
ALTER TABLE `sffiles` CHANGE COLUMN `file_moduleid` `file_moduleid` INT(11) NULL;
ALTER TABLE `sffiles` CHANGE COLUMN `file_programid` `file_programid` INT(11) NOT NULL ;
ALTER TABLE `sffiles` ADD CONSTRAINT `sffiles_ibfk_1`  FOREIGN KEY (`file_moduleid`)  REFERENCES `modules` (`modu_moduleid`);
ALTER TABLE `sffiles` ADD CONSTRAINT `sffiles_ibfk_4`  FOREIGN KEY (`file_programid`)  REFERENCES `programs` (`prog_programid`);

-- Cambiar emails a program id
ALTER TABLE emails ADD COLUMN emai_programid INT;
ALTER TABLE emails ADD FOREIGN KEY (emai_programid) REFERENCES programs(prog_programid);

-- CREA CAMPOS PARA CAMBIAR GRUPOS A PERFILES

ALTER TABLE quotestaff ADD COLUMN qost_profileid INT;
ALTER TABLE quotestaff ADD FOREIGN KEY (qost_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE bankaccounts ADD COLUMN bkac_profileid INT;
ALTER TABLE bankaccounts ADD FOREIGN KEY (bkac_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE commissions ADD COLUMN comi_profileid INT;
ALTER TABLE commissions ADD FOREIGN KEY (comi_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE ordercommissionamounts ADD COLUMN orca_profileid INT;
ALTER TABLE ordercommissionamounts ADD FOREIGN KEY (orca_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE orderstaff ADD COLUMN ords_profileid INT;
ALTER TABLE orderstaff ADD FOREIGN KEY (ords_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE ordertypes ADD COLUMN ortp_profileid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE ordertypes ADD COLUMN ortp_dispersionprofileid INT;
ALTER TABLE ordertypes ADD FOREIGN KEY (ortp_dispersionprofileid) REFERENCES profiles(prof_profileid);

ALTER TABLE wflowsteps ADD COLUMN wfsp_profileid INT;
ALTER TABLE wflowsteps ADD FOREIGN KEY (wfsp_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE wflowsteptypes ADD COLUMN wfst_profileid INT;
ALTER TABLE wflowsteptypes ADD FOREIGN KEY (wfst_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE wflowusers ADD COLUMN wflu_profileid INT;
ALTER TABLE wflowusers ADD FOREIGN KEY (wflu_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE wflowuserselect ADD COLUMN wfus_profileid INT;
ALTER TABLE wflowuserselect ADD FOREIGN KEY (wfus_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE wflowgroups ADD COLUMN wfgp_profileid INT;
ALTER TABLE wflowgroups ADD FOREIGN KEY (wfgp_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE sendreports ADD COLUMN sdrp_profileid INT;
ALTER TABLE sendreports ADD FOREIGN KEY (sdrp_profileid) REFERENCES profiles(prof_profileid);

ALTER TABLE sessiontypepackages ADD COLUMN setp_profileid INT;
ALTER TABLE sessiontypepackages ADD FOREIGN KEY (setp_profileid) REFERENCES profiles(prof_profileid);

-- MIGRAR GROUPS MEDIANTE JSP prog_migrategroupsprofile.jsp

-- 09/abr/2019
ALTER TABLE `wflowgroups` 
DROP FOREIGN KEY `wflowgroups_ibfk_5`,
DROP FOREIGN KEY `wflowgroups_ibfk_4`,
DROP FOREIGN KEY `wflowgroups_ibfk_3`,
DROP FOREIGN KEY `wflowgroups_ibfk_2`,
DROP FOREIGN KEY `wflowgroups_ibfk_1`;

ALTER TABLE `wflowgroups` 
DROP INDEX `wfgp_profileid` ,
DROP INDEX `wfgp_usermodifyid` ,
DROP INDEX `wfgp_usercreateid` ,
DROP INDEX `wfgp_wflowcategoryid` ,
DROP INDEX `wfgp_groupid` ;

ALTER TABLE `wflowgroups` 
CHANGE COLUMN `wfgp_wflowgroupid` `wfcp_wflowcategoryprofileid` INT(11) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `wfgp_required` `wfcp_required` INT(11) NOT NULL ,
CHANGE COLUMN `wfgp_groupid` `wfcp_groupid` INT(11) NOT NULL ,
CHANGE COLUMN `wfgp_wflowcategoryid` `wfcp_wflowcategoryid` INT(11) NOT NULL ,
CHANGE COLUMN `wfgp_sflog` `wfcp_sflog` TEXT NULL DEFAULT NULL ,
CHANGE COLUMN `wfgp_autodate` `wfcp_autodate` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `wfgp_usercreateid` `wfcp_usercreateid` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `wfgp_usermodifyid` `wfcp_usermodifyid` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `wfgp_datecreate` `wfcp_datecreate` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `wfgp_datemodify` `wfcp_datemodify` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `wfgp_profileid` `wfcp_profileid` INT(11) NULL DEFAULT NULL , 
RENAME TO `wflowcategoryprofiles` ;

ALTER TABLE `wflowcategoryprofiles` ADD FOREIGN KEY (`wfcp_groupid`) REFERENCES `groups` (`grup_groupid`);
ALTER TABLE `wflowcategoryprofiles` ADD FOREIGN KEY (`wfcp_wflowcategoryid`)  REFERENCES `wflowcategories` (`wfca_wflowcategoryid`);
ALTER TABLE `wflowcategoryprofiles` ADD FOREIGN KEY (`wfcp_usercreateid`)  REFERENCES `users` (`user_userid`);
ALTER TABLE `wflowcategoryprofiles` ADD FOREIGN KEY (`wfcp_usermodifyid`)  REFERENCES `users` (`user_userid`);
ALTER TABLE `wflowcategoryprofiles` ADD FOREIGN KEY (`wfcp_profileid`)  REFERENCES `profiles` (`prof_profileid`);

ALTER TABLE wflowcategoryprofiles ADD COLUMN wfcp_autoprofile INT;
UPDATE wflowcategoryprofiles SET wfcp_autoprofile = 1;

-- Asegurar que se haya ejecutado el JSP prog_migrategroupsprofile.jsp para aplicar esto
ALTER TABLE `wflowcategoryprofiles` CHANGE COLUMN `wfcp_groupid` `wfcp_groupid` INT(11) NULL ;
ALTER TABLE `wflowcategoryprofiles` CHANGE COLUMN `wfcp_profileid` `wfcp_profileid` INT(11) NOT NULL ;
ALTER TABLE `wflowsteps` DROP FOREIGN KEY `wflowsteps_ibfk_1`;
ALTER TABLE `wflowsteps` CHANGE COLUMN `wfsp_groupid` `wfsp_groupid` INT(11) NULL ;
ALTER TABLE `wflowsteps` ADD CONSTRAINT `wflowsteps_ibfk_1`  FOREIGN KEY (`wfsp_groupid`)  REFERENCES `groups` (`grup_groupid`)  ON DELETE RESTRICT  ON UPDATE RESTRICT;

ALTER TABLE `wflowsteptypes` DROP FOREIGN KEY `wflowsteptypes_ibfk_1`;
ALTER TABLE `wflowsteptypes` CHANGE COLUMN `wfst_groupid` `wfst_groupid` INT(11) NULL ;
ALTER TABLE `wflowsteptypes` ADD CONSTRAINT `wflowsteptypes_ibfk_1`  FOREIGN KEY (`wfst_groupid`)  REFERENCES `groups` (`grup_groupid`);


-- CONFIGURAR PERMISOS ESPECIALES
-- ejecutar /batch/pgsp_migrationprogramspecials.jsp
-- ejecutar /batch/pgps_migrationprofilespecials.jsp

-- ejecutar /batch/prog_copywflowgroups.jsp
-- ejecutar /batch/prog_migrationmodule.jsp


 -- QUITAR DE MENU MODULOS/COMPONENTES/GRUPOS 
UPDATE programs SET prog_menuid = null WHERE prog_code = 'MODU';
UPDATE programs SET prog_menuid = null WHERE prog_code = 'SFCM';
UPDATE programs SET prog_menuid = null WHERE prog_code = 'GRUP';

-- ACTUALIZAR ANTIGUOS ACCESSOS DE MODULOS A PROGRAMAS
UPDATE programprofiles
LEFT JOIN programs ON (prog_programid = pgpf_programid)
SET pgpf_programid = (SELECT prog_programid FROM programs WHERE prog_code = 'PROG')
WHERE prog_code = 'MODU';

UPDATE programprofiles
LEFT JOIN programs ON (prog_programid = pgpf_programid)
SET pgpf_programid = (SELECT prog_programid FROM programs WHERE prog_code = 'PROF')
WHERE prog_code = 'GRUP';

UPDATE programprofiles
LEFT JOIN programs ON (prog_programid = pgpf_programid)
SET pgpf_programid = (SELECT prog_programid FROM programs WHERE prog_code = 'PFUS')
WHERE prog_code = 'USGP';


ALTER TABLE `wflowcategories` DROP FOREIGN KEY `wflowcategories_ibfk_1`;
ALTER TABLE `wflowcategories` CHANGE COLUMN `wfca_moduleid` `wfca_moduleid` INT(11) NULL ;
ALTER TABLE `wflowcategories` ADD CONSTRAINT `wflowcategories_ibfk_1`  FOREIGN KEY (`wfca_moduleid`)  REFERENCES `modules` (`modu_moduleid`);


-- ELIMINAR LOS PROGRAMAS QUE HACEN REFERENCIA A MODULOS: COMPONENTES, COMPONENTACCESS, MODULES, ETC.
-- ELIMINAR LAS TABLAS ANTERIORES, SOLO DESPUÉS DE HABER PROBADO TODO CORRECTAMENTE
ALTER TABLE sfcaptions DROP COLUMN sfcp_moduleid;

DROP TABLE sfcomponentspecialaccess;
DROP TABLE sfcomponentspecials;
DROP TABLE sfcomponentaccess;
DROP TABLE moduledetails;

DROP TABLE modules;
DROP TABLE groups;
DROP TABLE modules;

