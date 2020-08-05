-- Permisos a usuario de BD
/*
// Permisos para ver tablas(solo select)
GRANT [permiso] ON [nombre de bases de datos].[nombre de tabla] TO ‘[nombre de usuario]’@'localhost’;
GRANT SELECT ON `flexwm-visual`.`Oportunidades_Revision/Perdidas` TO `flexwm-visual-report`@`%`;
GRANT SELECT ON `flexwm-visual`.`Oportunidades_Ganadas` TO 'flexwm-visual-report'@'%';
GRANT SELECT ON `flexwm-visual`.`Pipeline_Comercial` TO 'flexwm-visual-report'@'%';
GRANT SELECT ON `flexwm-visual`.`Pedidos` TO 'flexwm-visual-report'@'%';
-- ACTUALIZACION 18dic19:
	* Pide un identificacion. Lin: https://stackoverflow.com/questions/17597144/cant-find-any-matching-row-in-the-user-table/54072858#54072858
	GRANT SELECT ON `flexwm-visual`.`Pipeline_Comercial` TO 'flexwm-visual-report2'@'%' IDENTIFIED BY 'Ak1m@r1$2';


// Permisos para ver vistas
GRANT SHOW VIEW ON `flexwm-visual`.`Oportunidades_Revision/Perdidas` TO `flexwm-visual-report`@`%`;
GRANT SHOW VIEW ON `flexwm-visual`.`Oportunidades_Ganadas` TO `flexwm-visual-report`@`%`;
GRANT SHOW VIEW ON `flexwm-visual`.`Pipeline_Comercial` TO `flexwm-visual-report`@`%`;
GRANT SHOW VIEW ON `flexwm-visual`.`Pedidos` TO `flexwm-visual-report`@`%`;
*/

-- Ver vistas en la BD
show full tables where table_type = 'VIEW'; 

-- Borrar vistas
DROP VIEW `Pipeline_Comercial`;

-- Funcion para obtener el ultimo tipo de cambo

DELIMITER //
CREATE FUNCTION `nowParity`(codeCurrency VARCHAR(3)) RETURNS DOUBLE
BEGIN 
	DECLARE parity DOUBLE;
	SET parity = (SELECT ROUND(cure_parity, 4) FROM currencies WHERE cure_code = codeCurrency);
	RETURN parity;
END;
//
DELIMITER;


-- SELECT nowParity('usd'); -- 19.2597

/* ************************************************* Oportunidades Revision/Perdida ************************************************* */

CREATE OR REPLACE VIEW `Oportunidades_Revision/Perdidas` AS
SELECT user_code AS 'Vendedor',
comp_name AS 'Empresa',
oppo_fiscalperiod AS 'Trimestre', 
oppo_fiscalyear AS 'Año', 
SUBSTRING(wflf_name, 1, 2) AS 'Etapa',
cust_code AS 'Clave Cliente',
cust_legalname AS 'Cliente',
oppo_code AS 'Oportunidad',
orde_code AS 'Pedido',
prod_code AS 'Clave Producto',
CASE
	WHEN qoit_productid > 0 THEN prod_name
    ELSE 
		CASE  
			WHEN qoit_description IS NULL THEN qoit_name 
            ELSE qoit_description 
        END
END AS 'Producto',
prgp_name AS 'Grupo Producto',
prfm_name AS 'Familia Producto',
qoit_quantity AS 'Cantidad',
unit_code AS 'Unidad', 
CASE 
	WHEN cure_code = 'MXN' THEN ROUND((qoit_price / nowParity('USD')), 4)
    ELSE ROUND(qoit_price, 4)
END AS 'Precio Unitario',
CASE
	WHEN oppo_status = 'R' THEN 'En Revisión'
    WHEN oppo_status = 'W' THEN 'Ganada'
    WHEN oppo_status = 'L' THEN 'Perdida'
    WHEN oppo_status = 'E' THEN 'Expirada'
    WHEN oppo_status = 'H' THEN 'Detenido'
    ELSE 'Indefinido'
END AS 'Estatus', 
CASE
	WHEN prgp_name LIKE 'INFOR Licenses'
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(qoit_quantity * (qoit_price / nowParity('USD')) , 4)
				ELSE ROUND((qoit_quantity * qoit_price), 4)
			END
    ELSE '0'
END AS 'Venta Licencias INFOR',
CASE
	WHEN (prgp_name LIKE 'Cimatic Licenses' OR prgp_name LIKE 'VES Licenses' OR prgp_name LIKE 'CTECH Licenses') 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(qoit_quantity * (qoit_price / nowParity('USD')) , 4)
				ELSE ROUND((qoit_quantity * qoit_price), 4)
			END
    ELSE '0'
END AS 'Venta Licencias VES',
CASE
	WHEN prgp_name LIKE 'Services' 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(qoit_quantity * (qoit_price / nowParity('USD')) , 4)
				ELSE ROUND((qoit_quantity * qoit_price), 4)
			END
    ELSE '0'
END AS 'Venta Servicios VES',
CASE
	WHEN prgp_name LIKE 'Development' 
    THEN ROUND(qoit_amount, 4)
    ELSE '0'
END AS 'Venta Desarrollo VES',
CASE
	WHEN prgp_name LIKE 'VTS' 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(qoit_quantity * (qoit_price / nowParity('USD')) , 4)
				ELSE ROUND((qoit_quantity * qoit_price), 4)
			END
    ELSE '0'
END AS 'Venta VTS'
FROM quoteitems
LEFT JOIN products ON (prod_productid = qoit_productid)
LEFT JOIN productfamilies ON (prfm_productfamilyid = prod_productfamilyid)
LEFT JOIN productgroups ON (prgp_productgroupid = prod_productgroupid)
LEFT JOIN units ON (unit_unitid = prod_unitid)
LEFT JOIN quotegroups ON (qogr_quotegroupid = qoit_quotegroupid)
LEFT JOIN quotes ON (quot_quoteid = qogr_quoteid)
LEFT JOIN currencies ON (cure_currencyid = quot_currencyid)
LEFT JOIN opportunities ON (oppo_quoteid = quot_quoteid)
LEFT JOIN customers ON (cust_customerid = oppo_customerid)
LEFT JOIN users ON (user_userid = oppo_userid)
LEFT JOIN companies ON (comp_companyid = oppo_companyid)
LEFT JOIN wflows ON (wflw_wflowid = oppo_wflowid)
LEFT JOIN wflowfunnels ON (wflf_wflowfunnelid = wflw_wflowfunnelid)
LEFT JOIN orders ON (orde_opportunityid = oppo_opportunityid)
WHERE (oppo_status = 'R' OR oppo_status = 'L')
ORDER BY oppo_opportunityid ASC;


/* ************************************************* Oportunidades Ganadas ************************************************* */

 CREATE OR REPLACE VIEW `Oportunidades_Ganadas` AS
SELECT user_code AS 'Vendedor',
comp_name AS 'Empresa',
oppo_fiscalperiod AS 'Trimestre', 
oppo_fiscalyear AS 'Año', 
SUBSTRING(wflf_name, 1, 2) AS 'Etapa',
cust_code AS 'Clave Cliente',
cust_legalname AS 'Cliente',
oppo_code AS 'Oportunidad',
orde_code AS 'Pedido',
prod_code AS 'Clave Producto',
CASE
	WHEN ordi_productid > 0 THEN prod_name
    ELSE 
		CASE  
			WHEN ordi_description IS NULL THEN ordi_name 
            ELSE ordi_description 
        END
END AS 'Producto',
prgp_name AS 'Grupo Producto',
prfm_name AS 'Familia Producto',
ordi_quantity AS 'Cantidad',
unit_code AS 'Unidad', 
CASE 
	WHEN cure_code = 'MXN' THEN ROUND((ordi_price / nowParity('USD')), 4)
    ELSE ROUND(ordi_price, 4)
END AS 'Precio Unitario',
CASE
	WHEN oppo_status = 'R' THEN 'En Revisión'
    WHEN oppo_status = 'W' THEN 'Ganada'
    WHEN oppo_status = 'L' THEN 'Perdida'
    WHEN oppo_status = 'E' THEN 'Expirada'
    WHEN oppo_status = 'H' THEN 'Detenido'
    ELSE 'Indefinido'
END AS 'Estatus', 
CASE
	WHEN prgp_name LIKE 'INFOR Licenses'
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta Licencias INFOR',
CASE
	WHEN (prgp_name LIKE 'Cimatic Licenses' OR prgp_name LIKE 'VES Licenses' OR prgp_name LIKE 'CTECH Licenses') 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta Licencias VES',
CASE
	WHEN prgp_name LIKE 'Services' 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta Servicios VES',
CASE
	WHEN prgp_name LIKE 'Development' 
    THEN ROUND(ordi_amount, 4)
    ELSE '0'
END AS 'Venta Desarrollo VES',
CASE
	WHEN prgp_name LIKE 'VTS' 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta VTS'
FROM orderitems
LEFT JOIN products ON (prod_productid = ordi_productid)
LEFT JOIN productfamilies ON (prfm_productfamilyid = prod_productfamilyid)
LEFT JOIN productgroups ON (prgp_productgroupid = prod_productgroupid)
LEFT JOIN units ON (unit_unitid = prod_unitid)
LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid)
LEFT JOIN orders ON (orde_orderid = ordg_orderid)
LEFT JOIN currencies ON (cure_currencyid = orde_currencyid)

LEFT JOIN opportunities ON (oppo_opportunityid = orde_opportunityid)
LEFT JOIN wflows ON (wflw_wflowid = oppo_wflowid)
LEFT JOIN wflowfunnels ON (wflf_wflowfunnelid = wflw_wflowfunnelid)
LEFT JOIN customers ON (cust_customerid = oppo_customerid)
LEFT JOIN users ON (user_userid = oppo_userid)
LEFT JOIN companies ON (comp_companyid = oppo_companyid)
WHERE oppo_status = 'W'
ORDER BY oppo_opportunityid ASC;



/* ********************ejemplos********************* */

select Oportunidad, `Valor Est. Licencias`, 
`Valor Est. Servicios`, `Valor Cotizado`,  `Valor Est. Servicios(decimal)` , 
`Valor Est. Servicios(varchar)`
from `Pipeline_Comercial`  where Oportunidad = 'OP-0484';

/* ************************************************* Pipeline_Comercial(oppo) ************************************************* */

CREATE OR REPLACE VIEW `Pipeline_Comercial` AS
SELECT 
oppo_code AS 'Oportunidad',
oppo_name AS 'Nombre',
wfty_description AS 'Desc. Tipo WorkFlow',
cust_displayname AS 'Cliente',
indu_name AS 'SIC',
refe_name AS 'Fuente de Lead',
cust_rating AS 'Rating',
regi_name AS 'Región',
CASE
	WHEN oppo_amountlicense IS NULL THEN 0
    ELSE oppo_amountlicense
END  AS 'Valor Est. Licencias',
CASE
	WHEN oppo_amountservice IS NULL THEN 0
    ELSE oppo_amountservice
END  AS 'Valor Est. Servicios',
comp_name AS 'Empresa',
oppo_fiscalperiod AS 'Periodo Fiscal', 
oppo_fiscalyear AS 'Año Fiscal', 
SUBSTRING(wflf_name, 1, 2) AS 'Funnel',
cafo_name AS 'Categoría de Forecast',
oppo_customfield1 AS 'Oport. INFOR',
oppo_customfield2 AS 'INFOR PR',
user_code AS 'Vendedor',
oppo_leaddate AS 'Fecha de Lead',
oppo_saledate AS 'Fecha Cierre',
REPLACE(REPLACE(GROUP_CONCAT(DISTINCT(cmpt_name)), '\r', ''), '\n', '') AS 'Competencias', 
REPLACE(REPLACE(oppo_compvspos, '\r', ''), '\n', '') AS 'Posición vs. Competencia',
REPLACE(REPLACE(GROUP_CONCAT(DISTINCT(cuno_notes)), '\r', ''), '\n', '')  AS 'Notas',
CASE
	 WHEN wflg_type = 'O' THEN  REPLACE(REPLACE(wflg_comments, '\r', ''), '\n', '') 
     ELSE NULL
END AS 'Última Comunicación',
CASE
	WHEN oppo_status = 'R' THEN 'En Revisión'
    WHEN oppo_status = 'W' THEN 'Ganada'
    WHEN oppo_status = 'L' THEN 'Perdida'
    WHEN oppo_status = 'E' THEN 'Expirada'
	WHEN oppo_status = 'H' THEN 'Detenido'
    ELSE 'Indefinido'
END AS 'Estatus',
CASE
	WHEN oppo_amount IS NULL THEN 0
    ELSE oppo_amount
END AS 'Valor Cotizado'
FROM opportunities
LEFT JOIN currencies ON (cure_currencyid = oppo_currencyid)
LEFT JOIN opportunitycompetition ON (opcm_opportunityid = oppo_opportunityid)
LEFT JOIN categoryforecasts ON (cafo_categoryforecastid = oppo_categoryforecastid)
LEFT JOIN competition ON (cmpt_competitionid = opcm_competitionid)
LEFT JOIN customers ON (cust_customerid = oppo_customerid)
LEFT JOIN customernotes ON (cuno_customerid = cust_customerid)
LEFT JOIN industries ON (indu_industryid = cust_industryid )
LEFT JOIN referrals ON (refe_referralid = cust_referralid)
LEFT JOIN regions ON (regi_regionid = cust_regionid)
LEFT JOIN users ON (user_userid = oppo_userid)
LEFT JOIN companies ON (comp_companyid = oppo_companyid)
LEFT JOIN wflows ON (wflw_wflowid = oppo_wflowid)
LEFT JOIN wflowtypes ON (wfty_wflowtypeid = wflw_wflowtypeid)
LEFT JOIN wflowfunnels ON (wflf_wflowfunnelid = wflw_wflowfunnelid)
LEFT JOIN wflowlogs  ON (wflg_wflowid = oppo_wflowid)

WHERE oppo_opportunityid > 0
AND  (
	wflg_type = 'O' 
    -- Traer registro si no existe el registro(puede no haber bitacora de tipo Otros)
	OR (NOT EXISTS (SELECT a.wflg_wflowlogid FROM wflowlogs a
						WHERE a.wflg_type = 'O'
                        AND a.wflg_wflowid = wflw_wflowid  
                        ORDER BY a.wflg_logdate DESC, a.wflg_wflowlogid DESC)
		)
)

AND (
		-- Traerl el utlimo registro de la bitacora de tipo Otros
		( wflg_wflowlogid IN (SELECT MAX(b.wflg_wflowlogid) FROM wflowlogs b
							WHERE b.wflg_type = 'O'
							AND b.wflg_wflowid = wflw_wflowid  
							ORDER BY b.wflg_logdate DESC, b.wflg_wflowlogid DESC)
		)
		-- FORZAR: Traer registro si no existe el registro(puede no haber bitacora de tipo Otros)
		OR  (
			NOT EXISTS (SELECT c.wflg_wflowlogid FROM wflowlogs c
						WHERE c.wflg_type = 'O'
                        AND c.wflg_wflowid = wflw_wflowid  
                        ORDER BY c.wflg_logdate DESC, c.wflg_wflowlogid DESC)
			)
)
GROUP BY wflw_wflowid
ORDER BY oppo_opportunityid ASC;

/* ************************************************* Pedidos ************************************************* */

 CREATE OR REPLACE VIEW `Pedidos` AS
SELECT user_code AS 'Vendedor',
cust_code AS 'Clave Cliente',
cust_legalname AS 'Cliente',
comp_name AS 'Empresa',
orde_code AS 'Pedido',
oppo_code AS 'Oportunidad',
prod_code AS 'Clave Producto',
CASE
	WHEN ordi_productid > 0 THEN prod_name
    ELSE 
		CASE  
			WHEN ordi_description IS NULL THEN ordi_name 
            ELSE ordi_description 
        END
END AS 'Producto',
prgp_name AS 'Grupo Producto',
prfm_name AS 'Familia Producto',
ordi_quantity AS 'Cantidad',
unit_code AS 'Unidad', 
CASE 
	WHEN cure_code = 'MXN' THEN ROUND((ordi_price / nowParity('USD')), 4)
    ELSE ROUND(ordi_price, 4)
END AS 'Precio Unitario',
CASE
	WHEN orde_status = 'R' THEN 'En Revisión'
    WHEN orde_status = 'A' THEN 'Autorizada'
    WHEN orde_status = 'F' THEN 'Terminada'
	WHEN orde_status = 'C' THEN 'Cancelada'
    ELSE 'Indefinido'
END AS 'Estatus', 
CASE
	WHEN prgp_name LIKE 'INFOR Licenses'
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta Licencias INFOR',
CASE
	WHEN (prgp_name LIKE 'Cimatic Licenses' OR prgp_name LIKE 'VES Licenses' OR prgp_name LIKE 'CTECH Licenses') 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta Licencias VES',
CASE
	WHEN prgp_name LIKE 'Services' 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta Servicios VES',
CASE
	WHEN prgp_name LIKE 'Development' 
    THEN ROUND(ordi_amount, 4)
    ELSE '0'
END AS 'Venta Desarrollo VES',
CASE
	WHEN prgp_name LIKE 'VTS' 
    THEN 	CASE 
				WHEN cure_code = 'MXN' THEN ROUND(ordi_quantity * (ordi_price / nowParity('USD')) , 4)
				ELSE ROUND((ordi_quantity * ordi_price), 4)
			END
    ELSE '0'
END AS 'Venta VTS'
FROM orderitems
LEFT JOIN products ON (prod_productid = ordi_productid)
LEFT JOIN productfamilies ON (prfm_productfamilyid = prod_productfamilyid)
LEFT JOIN productgroups ON (prgp_productgroupid = prod_productgroupid)
LEFT JOIN units ON (unit_unitid = prod_unitid)
LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid)
LEFT JOIN orders ON (orde_orderid = ordg_orderid)
LEFT JOIN currencies ON (cure_currencyid = orde_currencyid)
LEFT JOIN customers ON (cust_customerid = orde_customerid)
LEFT JOIN users ON (user_userid = orde_userid)
LEFT JOIN companies ON (comp_companyid = orde_companyid)
LEFT JOIN wflows ON (wflw_wflowid = orde_wflowid)
LEFT JOIN wflowfunnels ON (wflf_wflowfunnelid = wflw_wflowfunnelid)
LEFT JOIN opportunities ON (oppo_opportunityid = orde_opportunityid)
ORDER BY orde_orderid ASC;




