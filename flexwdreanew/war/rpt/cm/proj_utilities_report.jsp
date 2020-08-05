<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Javier Alberto Hernandez
 * @version 2013-10
 */ -->
 
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import= "com.flexwm.shared.cm.BmoProject"%>
<%@page import= "com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>

<%
//Inicializar variables
	String title = "Utilidad por Proyectos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();
	PmRaccount pmRaccount = new PmRaccount(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	PmCurrency pmCurrencyExchange = new PmCurrency(sFParams);
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
	String sql = "", where = "", filters = "", sqlCurrency = "";
	String startDate = "", endDate = "", startDateCreate = "", endDateCreate = "";
	String status = "", paymentStatus = "";
	int wflowTypeId = 0, projectId = 0, areaId = 0;
   	int wflowCategoryId = 0;
   	int venueId = 0;
   	int userId = 0;
   	int customerId = 0;
   	int referralId = 0;
   	int wflowPhaseId = 0;   	
	int programId = 0, currencyId = 0;
	double defaultParity = 0;
	boolean rental = true;

	// Obtener parametros
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");	
	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid")); 
   	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
    if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
   	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
    if (request.getParameter("paymentStatus") != null) paymentStatus = request.getParameter("paymentStatus");
    if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));
    if (request.getParameter("proj_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("proj_currencyid"));

    // Filtro 
   	if (customerId > 0) {
		where += " AND proj_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
	}

	// Filtros listados
   	if (wflowCategoryId > 0) {
   		where += " AND wfty_wflowcategoryid = " + wflowCategoryId;
   		filters += "<i>Categor&iacute;a de Flujo: </i>" + request.getParameter("wfty_wflowcategoryidLabel") + ", ";
   	}
   	
	if (wflowTypeId > 0) {
		where += " AND wfty_wflowtypeid = " + wflowTypeId;
		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
	}
	
	if (wflowPhaseId > 0) {
		where += " AND wflw_wflowphaseid = " + wflowPhaseId;
		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
	}
	
	if (projectId > 0) {
   		where += " AND proj_projectid = " + projectId;
   		filters += "<i>Proyecto: </i>" + request.getParameter("proj_projectidLabel") + ", ";
   	}
	
	if (venueId > 0) {
		where += " AND proj_venueid = " + venueId;
		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
	}
	
	if (!status.equals("")) {
   		//where += " AND proj_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("proj_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("proj_statusLabel") + ", ";
   	}
	
	if (!paymentStatus.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
   		filters += "<i>Estatus Pago: </i>" + request.getParameter("paymentStatusLabel") + ", ";
   	}
	
	if (userId > 0) {
		where += " AND proj_userid = " + userId;
		/*
		if (sFParams.restrictData(bmoProject.getProgramCode())) {
   			where += " AND proj_userid = " + userId;
		} else {
			where += " AND ( " +
							" proj_userid = " + userId +
							" OR proj_wflowid IN ( " +
								 " SELECT wflw_wflowid FROM wflowusers  " +
								 " LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
								 " WHERE wflu_userid = " + userId + 
								 " AND (wflw_callercode = '" + bmoProject.getProgramCode().toString() + "' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " + 
							 " ) " + 
					 " )";
		}*/
		filters += "<i>Vendedor: </i>" + request.getParameter("proj_useridLabel") + ", ";
	}
	
	if (referralId > 0) {
	    where += " AND cust_referralid = " + referralId;
	    filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
	}
	
	if (areaId > 0) {
   		where += " AND user_areaid = " + areaId;
		filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
	
	if (!startDate.equals("")) {
		where += " AND proj_startdate >= '" + startDate + " 00:00'";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}
	
	if (!endDate.equals("")) {
		where += " AND proj_startdate <= '" + endDate + " 23:59'";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}
	
	if (!startDateCreate.equals("")) {
   		where += " AND proj_datecreateproject >= '" + startDateCreate + " 00:00'";
   		filters += "<i>Fecha Inicio Sis.: </i>" + startDateCreate + ", ";
   	}
   	
   	if (!endDateCreate.equals("")) {
   		where += " AND proj_datecreateproject <= '" + endDateCreate + " 23:59'";
   		filters += "<i>Fecha Fin Sis.: </i>" + endDateCreate + ", ";
   	}
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
     if (currencyId > 0) {
        	bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
   	    	defaultParity = bmoCurrency.getParity().toDouble();
    	   	filters += "<i>Moneda: </i>" + request.getParameter("proj_currencyidLabel")
    	   				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
   	 } else {
    	   	filters += "<i>Moneda: </i> Todas ";
    	   	sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM projects "
    	   			+ " LEFT JOIN customers ON (cust_customerid = proj_customerid) "
    	   			+ " LEFT JOIN referrals ON (refe_referralid = cust_referralid) "
        			+ " LEFT JOIN venues ON (venu_venueid = proj_venueid) "
   	    			+ " LEFT JOIN cities ON (city_cityid = venu_cityid) "
    	    		+ " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) "
    	    		+ " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) "
    	   	    	+ " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) "
    	    		+ " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) "
    	    		+ " LEFT JOIN orders ON (proj_orderid = orde_orderid) "
    	    		+ " LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) "
    	    		+ " LEFT JOIN users ON (user_userid = proj_userid) " 
    	    		+ " WHERE wfca_programid = " + programId
    	   		 	+ where 
    	   			+ " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
   	} 	
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
	
   	PmConn pmConn = new PmConn(sFParams);
   	pmConn.open();
   	
   	PmConn pmConn2 = new PmConn(sFParams);
   	pmConn2.open();
   	
   	PmConn PmConnOrdeGroup = new PmConn(sFParams);
    PmConnOrdeGroup.open();
    
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
   	
   	
   	
   	//System.out.println("sql: "+sql);
   	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
%>

<html>

	<% 
	// Imprimir
	String print = "0", permissionPrint = "";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
	
	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
	}
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))){ %>
		<style> 
			body{
				user-select: none;
				-moz-user-select: none; 
				-o-user-select: none; 
				-webkit-user-select: none; 
				-ie-user-select: none; 
				-khtml-user-select:none; 
				-ms-user-select:none; 
				-webkit-touch-callout:none
			}
		</style>
		<style type="text/css" media="print">
		    * { display: none; }
		</style>
		<%
		permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if(print.equals("1") || exportExcel.equals("1")) { %>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<% }
		}
			
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))){ %>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">	
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td class="reportTitle" align="left" colspan="2">
			<%= title %>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle">
			<b>Filtros:</b> <%= filters %>
			<br>			
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<%sql = " SELECT COUNT(*) AS contador FROM projects " +
     		  " LEFT JOIN customers ON (proj_customerid = cust_customerid) " +
  		  " LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
  		  " LEFT JOIN cities ON (city_cityid = venu_cityid) " +
  		  " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
  		  " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
  		  " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	     
  	      " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
  	      " LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) " +
  		  " LEFT JOIN users ON (proj_userid = user_userid) " +
  		  " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
     		  " WHERE proj_projectid > 0 " + 
  		  where + 
     		  "  ";
  			int count =0;
  			//ejecuto el sql DEL CONTADOR
  			pmConnCount.doFetch(sql);
  			if(pmConnCount.next())
  				count=pmConnCount.getInt("contador");
  			System.out.println("contador DE REGISTROS --> "+count);
  			//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
						<%=messageTooLargeList %>
				<% 
			}else{
				
	if(!(currencyId >0)){
		int currencyIdWhile = 0;
		pmCurrencyWhile.open();
		pmCurrencyWhile.doFetch(sqlCurrency);
		while(pmCurrencyWhile.next()){%>
			<table class="report" style="width: 100%;">
			<%
				if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
	        		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
					currencyId = currencyIdWhile;
			    	defaultParity = pmCurrencyWhile.getInt("cure_parity");
			    	bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);
	        		%>
	        		<tr>
	            		<td class="reportHeaderCellCenter" colspan="24">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
	        		
	        	<%} %>
		<%	sql = " SELECT * FROM projects " +
	        	  " LEFT JOIN currencies ON (proj_currencyid = cure_currencyid) " +
		   		  " LEFT JOIN customers ON (proj_customerid = cust_customerid) " +
				  " LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
				  " LEFT JOIN cities ON (city_cityid = venu_cityid) " +
				  " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
				  " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
				  " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	     
			      " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
			      " LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) " +
				  " LEFT JOIN users ON (proj_userid = user_userid) " +
				  " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
		   		  " WHERE proj_projectid > 0 " + " AND proj_currencyid = " + currencyId +
				  where + 
		   		  " ORDER BY proj_startdate ASC";
		   	pmConn.doFetch(sql);%>

		<table class="report" border="0">
			<TR class="">      					
				<td class="reportHeaderCell">#</td>
				<td class="reportHeaderCell">Proyecto</td>
				<td class="reportHeaderCell">Fecha</td>
				<td class="reportHeaderCell">Tipo</td>
				<td class="reportHeaderCell">Moneda</td>
				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
				<td class="reportHeaderCellRight">Subtotal</td>
				<td class="reportHeaderCellRight">Desc</td>
				<!-- <td class="reportHeaderCellCenter">Nota C.</td>-->
				<td class="reportHeaderCellRight">Total sin Iva</td>
				<td class="reportHeaderCellRight">Iva</td>
				<td class="reportHeaderCellRight">Costo Producto</td>
				<!-- 
				<td class="reportHeaderCellRight">Costo Recursos</td>
				<td class="reportHeaderCellRight">Costo Personal</td>
				 -->
				<td class="reportHeaderCellRight">Costo O.C.</td>
				<td class="reportHeaderCellRight">Utilidad</td>
				<td class="reportHeaderCellCenter">Rentabilidad</td>
				<td class="reportHeaderCellRight">Saldo</td>

			</TR>
			<%
			    double utilidad = 0, utilidadTotal = 0, rentabilidad = 0,  rentabilidadTotal = 0;
			    double ordeTotal = 0, sumOrdeTotal = 0,
			    		sumProd = 0, sumProdTotal = 0,
			    		sumEqui = 0, sumEquiTotal = 0,
			    		sumStaff = 0, sumStaffTotal = 0,
			    		sumOC = 0, sumOCTotal = 0,
						sumCXC = 0, sumCXCTotal = 0,
			    		subTotal = 0, sumSubTotal = 0,
			    		extras = 0, sumExtras = 0,
			    		iva = 0, sumIva = 0,
			    		balance = 0, balanceTotal = 0;
			    
			    int  i = 1, y = 0;

				while(pmConn.next()) {
//		 			subTotal = pmConn.getDouble("orde_amount");
		            subTotal = pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_amount")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
							currencyId, defaultParity);
//		 			extras = pmConn.getDouble("orde_discount");
					extras = pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_discount")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
							currencyId, defaultParity);
//		 			iva = pmConn.getDouble("orde_tax");
					iva = pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_tax")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
							currencyId, defaultParity);
					ordeTotal = (pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_total")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
							currencyId, defaultParity)) - iva;
//		 			ordeTotal = pmConn.getDouble("orde_total") - iva;
					sumOrdeTotal += ordeTotal;
					
					sumSubTotal += subTotal;
					sumExtras += extras;
					sumIva += iva;
					
					//Costo Productos
					String sqlOrdeGroup = " SELECT * FROM ordergroups WHERE ordg_orderid = " + pmConn.getInt("proj_orderid");
					PmConnOrdeGroup.doFetch(sqlOrdeGroup);
					sumProd = 0;
					//System.out.println("sqlOrdeGroup: "+sqlOrdeGroup);

					while(PmConnOrdeGroup.next()){
						
						//COSTO PRODUCTOS
//		 				String sqlProd = " SELECT SUM(ordi_quantity * ordi_basecost * ordi_days) as sumItemsCostExtra FROM orderitems " +
//		 					      " LEFT JOIN products ON (prod_productid = ordi_productid) " +
//		 					      " LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
//		 						  " LEFT JOIN orders ON (orde_orderid = ordg_orderid) " +			      
//		 					      " WHERE ordi_ordergroupid = " + PmConnOrdeGroup.getInt("ordg_ordergroupid") +
//		 					      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
						String sqlProd = " SELECT ordi_quantity, ordi_basecost,orde_currencyid,ordi_days,orde_currencyid,orde_currencyParity FROM orderitems " +
							      " LEFT JOIN products ON (prod_productid = ordi_productid) " +
							      " LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
								  " LEFT JOIN orders ON (orde_orderid = ordg_orderid) " +			      
							      " WHERE ordi_ordergroupid = " + PmConnOrdeGroup.getInt("ordg_ordergroupid") +
							      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
						//System.out.println(" sqlProd2: "+sqlProd);
						pmConn2.doFetch(sqlProd);
						while(pmConn2.next()) {
							double quantity = pmConn2.getDouble("ordi_quantity");
							double basecost = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("ordi_basecost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
									currencyId, defaultParity);
							double days =  pmConn2.getDouble("ordi_days");
							double total = quantity * basecost * days;
							
							sumProd += total;
						}
						
					} //Fin de PmConnOrdeGroup
					
					//ORDENES DE COMPRA
					//Obtener costos de OC ligados a un producto
					String sqlOC = "";
					if(pmConn.getString("ortp_type").equals(""+BmoOrderType.TYPE_RENTAL)){
						 rental = true;
						sqlOC = " SELECT rqit_quantity ,prod_rentalcost,rqit_days,reqi_currencyid,reqi_currencyparity ";
					
					}else{
						 rental = false;
						sqlOC = " SELECT rqit_quantity, prod_cost, rqit_days,reqi_currencyid,reqi_currencyparity";
					}
						sqlOC += "FROM requisitionitems " +
								      " LEFT JOIN requisitions ON (reqi_requisitionid = rqit_requisitionid) " +
								      " LEFT JOIN products ON (prod_productid = rqit_productid) " +
									  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +			     
								      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
								      " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
									  " AND NOT rqit_productid IS NULL";
// 					System.out.println("sqlOC: "+sqlOC);      
					pmConn2.doFetch(sqlOC);
					while(pmConn2.next()) {
						double cost = 0;
						double quantity = pmConn2.getDouble("rqit_quantity");
						if(rental){						
							cost =  pmCurrencyExchange.currencyExchange(pmConn2.getDouble("prod_rentalcost"), pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
								currencyId, defaultParity);
						}else{
							 cost =  pmCurrencyExchange.currencyExchange(pmConn2.getDouble("prod_cost"), pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
									currencyId, defaultParity);	
						}
						double days = pmConn2.getDouble("rqit_days");
						double total = quantity * cost * days;
						sumOC += total;
//		 				sumOC = pmConn2.getDouble("sumReqiItemCost");
					}
					
					//Obtener  costos de OC no ligados a un producto
//		 			sqlOC = " SELECT SUM(rqit_amount) as sumReqiItemOCExtra " +
					//sqlOC = " SELECT SUM(rqit_quantity * rqit_price * rqit_days) as sumReqiItemOCExtra " +
							sqlOC=  "SELECT rqit_amount,reqi_currencyid,reqi_currencyparity	FROM requisitionitems " +
						     	 " LEFT JOIN requisitions ON (reqi_requisitionid = rqit_requisitionid) " +	
						     	 " LEFT JOIN products ON (prod_productid = rqit_productid) " +
							  	 " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +			     
						      	 " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
						     	 " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
							 	 " AND rqit_productid IS NULL";
					//System.out.println("sqlOC22: "+sqlOC);      
			        pmConn2.doFetch(sqlOC);
			        
					while(pmConn2.next()) {
						
						sumOC += pmCurrencyExchange.currencyExchange(pmConn2.getDouble("rqit_amount")	, pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
								currencyId, defaultParity);
//		 				sumOC += pmConn2.getDouble("sumReqiItemOCExtra");
					}
					//System.out.println("sqlOC22: "+sqlOC);
					
					//Obtener descuentos de OC
					sqlOC = " SELECT reqi_discount,reqi_currencyid,reqi_currencyparity " +
							  "	FROM requisitions " +
							  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +			     
						      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
						      " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";
			        pmConn2.doFetch(sqlOC);
					while(pmConn2.next()) {
						sumOC -= pmCurrencyExchange.currencyExchange(pmConn2.getDouble("reqi_discount")	, pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
								currencyId, defaultParity);
//		 				sumOC -= pmConn2.getDouble("sumReqiDiscount");
					}
					//System.out.println("sqlOCComDiscount: "+sqlOC);
					
					
					//CXC DE TIPO NOTA DE CREDITO
// 					String sqlCXCCreditNote = " SELECT racc_total,racc_currencyid,racc_currencyParity " + 
// 									  " FROM raccounts " +
// 									  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
// 									  " WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
// 									  " AND ract_category = '" + BmoRaccountType.CATEGORY_CREDITNOTE  + "'" +
// 								      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
					
// 					//System.out.println("sqlCXCCreditNote: "+sqlCXCCreditNote);
// 					pmConn2.doFetch(sqlCXCCreditNote);
// 					while(pmConn2.next()){
// 						sumCXC = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("racc_total")	, pmConn2.getInt("racc_currencyid"), pmConn2.getDouble("racc_currencyParity"), 
// 								currencyId, defaultParity);
// 					}
					
					
					// OBTENER EL SALDO DE CXC DEL PROYECTO
					//double balanceCxC =  pmRaccount.getOrderBalance(pmConn.getInt("proj_orderid"), bmUpdateResult)
					double withDraw = 0, deposit = 0;
					withDraw = Math.round(pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_balance"), pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyparity"), 
							currencyId, defaultParity));
//		 			withDraw = Math.round(pmConn.getDouble("orde_total"));			

					//Obtener el total de Abonos ligados
// 					sql =  " SELECT ROUND(racc_total, 2) FROM raccounts " +
// 							" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid)" +
// 							" WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
// 							" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' " +
// 							" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'";
					
// 					while(pmConn2.next()){ 
// 						deposit =pmCurrencyExchange.currencyExchange(pmConn2.getDouble("racc_total"), pmConn2.getInt("racc_currencyid"), pmConn2.getDouble("racc_currencyParity"), 
// 								currencyId, defaultParity);
// 					}		

					balance = withDraw - deposit;
					
					//Totales
					sumProdTotal += sumProd;
					sumEquiTotal += sumEqui;
					sumStaffTotal += sumStaff;
					sumOCTotal += sumOC;
					sumCXCTotal += sumCXC;
					ordeTotal = ordeTotal - sumCXC;
					balanceTotal += balance;
					
					utilidad = (ordeTotal - (sumProd + sumEqui + sumStaff + sumOC)); 
					if(ordeTotal > 0)
						rentabilidad = ((utilidad/ordeTotal) * 100);
					else rentabilidad = 0;
					
					rentabilidad = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(rentabilidad));
					
					
			%>	
					<TR class="reportCellEven">
		        		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects", "proj_code") + "&nbsp;&nbsp;" + pmConn.getString("projects", "proj_name"), BmFieldType.STRING)) %>
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects", "proj_startdate"), BmFieldType.DATETIME) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, ""+defaultParity, BmFieldType.NUMBER)) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + subTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + extras, BmFieldType.CURRENCY) %>
						<%//= HtmlUtil.formatReportCell(sFParams, "" + sumCXC, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + ordeTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + iva, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumProd, BmFieldType.CURRENCY) %>
						<%//= HtmlUtil.formatReportCell(sFParams, "" + sumEqui, BmFieldType.CURRENCY) %>
						<%//= HtmlUtil.formatReportCell(sFParams, "" + sumStaff, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumOC, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + utilidad, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + rentabilidad, BmFieldType.PERCENTAGE) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
					</TR>
			<% 	
					sumCXC = 0;
					sumOC=0;
					i++;
					y++;
			   }
				
				sumOrdeTotal = sumOrdeTotal - sumCXCTotal;
				
				utilidadTotal = (sumOrdeTotal - (sumProdTotal + sumEquiTotal + sumStaffTotal + sumOCTotal)); 
				if(sumOrdeTotal > 0)
					rentabilidadTotal = ((utilidadTotal/sumOrdeTotal) * 100);
				else rentabilidadTotal = 0;
				
				rentabilidadTotal = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(rentabilidadTotal));
			%>
				<tr><td colspan="16">&nbsp;</td></tr>
			   <TR class="reportCellEven reportCellCode">
			   		<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
			   
					<td align="left" width="40" colspan="5">
			 			&nbsp;
			 		</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumSubTotal, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumExtras, BmFieldType.CURRENCY) %>
					<%//= HtmlUtil.formatReportCell(sFParams, "" + sumCXCTotal, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumOrdeTotal, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumIva, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumProdTotal, BmFieldType.CURRENCY) %>
					<%//= HtmlUtil.formatReportCell(sFParams, "" + sumEquiTotal, BmFieldType.CURRENCY) %>
					<%//= HtmlUtil.formatReportCell(sFParams, "" + sumStaffTotal, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumOCTotal, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + utilidadTotal, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + rentabilidadTotal, BmFieldType.PERCENTAGE) %>	
					<%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
				</TR>   

		</TABLE>

		<%
			
		}	pmCurrencyWhile.close();//fin CurrencyWhile
				
	}else{
   	sql = " SELECT * FROM projects " +
   		  " LEFT JOIN customers ON (proj_customerid = cust_customerid) " +
		  " LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
		  " LEFT JOIN cities ON (city_cityid = venu_cityid) " +
		  " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
		  " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
		  " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	     
	      " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
	      " LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) " +
		  " LEFT JOIN users ON (proj_userid = user_userid) " +
		  " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
   		  " WHERE proj_projectid > 0 " + 
		  where + 
   		  " ORDER BY proj_startdate ASC";
   	pmConn.doFetch(sql);%>

<table class="report" border="0">
	<TR class="">      					
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Proyecto</td>
		<td class="reportHeaderCell">Fecha</td>
		<td class="reportHeaderCell">Tipo</td>
		<td class="reportHeaderCell">Moneda</td>
		<td class="reportHeaderCellCenter">Tipo de Cambio</td>
		<td class="reportHeaderCellCenter">Subtotal</td>
		<td class="reportHeaderCellCenter">Desc</td>
		<!-- <td class="reportHeaderCellCenter">Nota C.</td>-->
		<td class="reportHeaderCellRight">Total sin Iva</td>
		<td class="reportHeaderCellRight">Iva</td>
		<td class="reportHeaderCellRight">Costo Producto</td>
		<!-- 
		<td class="reportHeaderCellRight">Costo Recursos</td>
		<td class="reportHeaderCellRight">Costo Personal</td>
		-->
		<td class="reportHeaderCellRight">Costo O.C.</td>
		<td class="reportHeaderCellRight">Utilidad</td>
		<td class="reportHeaderCellCenter">Rentabilidad</td>
		<td class="reportHeaderCellRight">Saldo</td>

	</TR>
	<%
	    double utilidad = 0, utilidadTotal = 0, rentabilidad = 0,  rentabilidadTotal = 0;
	    double ordeTotal = 0, sumOrdeTotal = 0,
	    		sumProd = 0, sumProdTotal = 0,
	    		sumEqui = 0, sumEquiTotal = 0,
	    		sumStaff = 0, sumStaffTotal = 0,
	    		sumOC = 0, sumOCTotal = 0,
				sumCXC = 0, sumCXCTotal = 0,
	    		subTotal = 0, sumSubTotal = 0,
	    		extras = 0, sumExtras = 0,
	    		iva = 0, sumIva = 0,
	    		balance = 0, balanceTotal = 0;
	    
	    int  i = 1, y = 0;

		while(pmConn.next()) {
// 			subTotal = pmConn.getDouble("orde_amount");
            subTotal = pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_amount")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
					currencyId, defaultParity);
// 			extras = pmConn.getDouble("orde_discount");
			extras = pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_discount")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
					currencyId, defaultParity);
// 			iva = pmConn.getDouble("orde_tax");
			iva = pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_tax")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
					currencyId, defaultParity);
			ordeTotal = (pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_total")	, pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyParity"), 
					currencyId, defaultParity)) - iva;
// 			ordeTotal = pmConn.getDouble("orde_total") - iva;
			sumOrdeTotal += ordeTotal;
			
			sumSubTotal += subTotal;
			sumExtras += extras;
			sumIva += iva;
			
			//Costo Productos
			String sqlOrdeGroup = " SELECT * FROM ordergroups WHERE ordg_orderid = " + pmConn.getInt("proj_orderid");
			PmConnOrdeGroup.doFetch(sqlOrdeGroup);
			sumProd = 0;
			//System.out.println("sqlOrdeGroup: "+sqlOrdeGroup);

			while(PmConnOrdeGroup.next()){
				
				//COSTO PRODUCTOS
// 				String sqlProd = " SELECT SUM(ordi_quantity * ordi_basecost * ordi_days) as sumItemsCostExtra FROM orderitems " +
// 					      " LEFT JOIN products ON (prod_productid = ordi_productid) " +
// 					      " LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
// 						  " LEFT JOIN orders ON (orde_orderid = ordg_orderid) " +			      
// 					      " WHERE ordi_ordergroupid = " + PmConnOrdeGroup.getInt("ordg_ordergroupid") +
// 					      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
				String sqlProd = " SELECT ordi_quantity, ordi_basecost,orde_currencyid,ordi_days,orde_currencyid,orde_currencyParity FROM orderitems " +
					      " LEFT JOIN products ON (prod_productid = ordi_productid) " +
					      " LEFT JOIN ordergroups ON (ordg_ordergroupid = ordi_ordergroupid) " +
						  " LEFT JOIN orders ON (orde_orderid = ordg_orderid) " +			      
					      " WHERE ordi_ordergroupid = " + PmConnOrdeGroup.getInt("ordg_ordergroupid") +
					      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
				//System.out.println(" sqlProd2: "+sqlProd);
				pmConn2.doFetch(sqlProd);
				while(pmConn2.next()) {
					double quantity = pmConn2.getDouble("ordi_quantity");
					double basecost = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("ordi_basecost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
							currencyId, defaultParity);
					double days =  pmConn2.getDouble("ordi_days");
					double total = quantity * basecost * days;
					
					sumProd += total;
				}
				
			} //Fin de PmConnOrdeGroup

			
			//ORDENES DE COMPRA
			//Obtener costos de OC ligados a un producto
			String sqlOC = "";
			if(pmConn.getString("ortp_type").equals(""+BmoOrderType.TYPE_RENTAL)){
				 rental = true;
				sqlOC = " SELECT rqit_quantity ,prod_rentalcost,rqit_days,reqi_currencyid,reqi_currencyparity ";
			
			}else{
				 rental = false;
				sqlOC = " SELECT rqit_quantity, prod_cost, rqit_days,reqi_currencyid,reqi_currencyparity";
			}
				sqlOC += "FROM requisitionitems " +
						      " LEFT JOIN requisitions ON (reqi_requisitionid = rqit_requisitionid) " +
						      " LEFT JOIN products ON (prod_productid = rqit_productid) " +
							  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +			     
						      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
						      " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
							  " AND NOT rqit_productid IS NULL";
			System.out.println("sqlOC: "+sqlOC);      
			pmConn2.doFetch(sqlOC);
			while(pmConn2.next()) {
				double cost = 0;
				double quantity = pmConn2.getDouble("rqit_quantity");
				if(rental){						
					cost =  pmCurrencyExchange.currencyExchange(pmConn2.getDouble("prod_rentalcost"), pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
						currencyId, defaultParity);
				}else{
					 cost =  pmCurrencyExchange.currencyExchange(pmConn2.getDouble("prod_cost"), pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
							currencyId, defaultParity);	
				}
				double days = pmConn2.getDouble("rqit_days");
				double total = quantity * cost * days;
				sumOC += total;
// 				sumOC = pmConn2.getDouble("sumReqiItemCost");
			}
			
			//Obtener  costos de OC no ligados a un producto
// 			sqlOC = " SELECT SUM(rqit_amount) as sumReqiItemOCExtra " +
			//sqlOC = " SELECT SUM(rqit_quantity * rqit_price * rqit_days) as sumReqiItemOCExtra " +
					sqlOC=  "SELECT rqit_amount,reqi_currencyid,reqi_currencyparity	FROM requisitionitems " +
				     	 " LEFT JOIN requisitions ON (reqi_requisitionid = rqit_requisitionid) " +	
				     	 " LEFT JOIN products ON (prod_productid = rqit_productid) " +
					  	 " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +			     
				      	 " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
				     	 " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
					 	 " AND rqit_productid IS NULL";
			//System.out.println("sqlOC22: "+sqlOC);      
	        pmConn2.doFetch(sqlOC);
	        
			while(pmConn2.next()) {
				
				sumOC += pmCurrencyExchange.currencyExchange(pmConn2.getDouble("rqit_amount")	, pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
						currencyId, defaultParity);
// 				sumOC += pmConn2.getDouble("sumReqiItemOCExtra");
			}
			//System.out.println("sqlOC22: "+sqlOC);
			
			//Obtener descuentos de OC
			sqlOC = " SELECT reqi_discount,reqi_currencyid,reqi_currencyparity " +
					  "	FROM requisitions " +
					  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +			     
				      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
				      " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'";
	        pmConn2.doFetch(sqlOC);
			while(pmConn2.next()) {
				sumOC -= pmCurrencyExchange.currencyExchange(pmConn2.getDouble("reqi_discount")	, pmConn2.getInt("reqi_currencyid"), pmConn2.getDouble("reqi_currencyparity"), 
						currencyId, defaultParity);
// 				sumOC -= pmConn2.getDouble("sumReqiDiscount");
			}
			//System.out.println("sqlOCComDiscount: "+sqlOC);
			
			
			//CXC DE TIPO NOTA DE CREDITO
// 			String sqlCXCCreditNote = " SELECT racc_total,racc_currencyid,racc_currencyParity " + 
// 							  " FROM raccounts " +
// 							  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
// 							  " WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
// 							  " AND ract_category = '" + BmoRaccountType.CATEGORY_CREDITNOTE  + "'" +
// 						      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
			
// 			//System.out.println("sqlCXCCreditNote: "+sqlCXCCreditNote);
// 			pmConn2.doFetch(sqlCXCCreditNote);
// 			while(pmConn2.next()) {
// 				sumCXC = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("racc_total"), pmConn2.getInt("racc_currencyid"), pmConn2.getDouble("racc_currencyParity"), 
// 						currencyId, defaultParity);
// 			}
			
			
			// OBTENER EL SALDO DE CXC DEL PROYECTO
			//double balanceCxC =  pmRaccount.getOrderBalance(pmConn.getInt("proj_orderid"), bmUpdateResult)
			double withDraw = 0, deposit = 0;
			withDraw = Math.round(pmCurrencyExchange.currencyExchange(pmConn.getDouble("orde_balance"), pmConn.getInt("orde_currencyid"), pmConn.getDouble("orde_currencyparity"), 
					currencyId, defaultParity));
// 			withDraw = Math.round(pmConn.getDouble("orde_total"));			

			//Obtener el total de Abonos ligados (ESQUEMA ANTERIOR, BORRAR ESTO)
// 			sql =  " SELECT ROUND(racc_total, 2) FROM raccounts " +
// 					" LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid)" +
// 					" WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
// 					" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' " +
// 					" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'";
			
// 			while(pmConn2.next()) { 
// 				deposit =pmCurrencyExchange.currencyExchange(pmConn2.getDouble("racc_total"), pmConn2.getInt("racc_currencyid"), pmConn2.getDouble("racc_currencyParity"), 
// 						currencyId, defaultParity);
// 			}		

			balance = withDraw - deposit;
			
			//Totales
			sumProdTotal += sumProd;
			sumEquiTotal += sumEqui;
			sumStaffTotal += sumStaff;
			sumOCTotal += sumOC;
			sumCXCTotal += sumCXC;
			ordeTotal = ordeTotal - sumCXC;
			balanceTotal += balance;
			
			utilidad = (ordeTotal - (sumProd + sumEqui + sumStaff + sumOC)); 
			if(ordeTotal > 0)
				rentabilidad = ((utilidad/ordeTotal) * 100);
			else rentabilidad = 0;
			
			rentabilidad = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(rentabilidad));
			
			
	%>	
			<TR class="reportCellEven">
        		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects", "proj_code") + "&nbsp;&nbsp;" + pmConn.getString("projects", "proj_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects", "proj_startdate"), BmFieldType.DATETIME) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, ""+defaultParity, BmFieldType.NUMBER)) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + subTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + extras, BmFieldType.CURRENCY) %>
				<%//= HtmlUtil.formatReportCell(sFParams, "" + sumCXC, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + ordeTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + iva, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumProd, BmFieldType.CURRENCY) %>
				<%//= HtmlUtil.formatReportCell(sFParams, "" + sumEqui, BmFieldType.CURRENCY) %>
				<%//= HtmlUtil.formatReportCell(sFParams, "" + sumStaff, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumOC, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + utilidad, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + rentabilidad, BmFieldType.PERCENTAGE) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>
			</TR>
	<% 	
			sumCXC = 0;
			sumOC=0;
			i++;
			y++;
	   }
		
		sumOrdeTotal = sumOrdeTotal - sumCXCTotal;
		
		utilidadTotal = (sumOrdeTotal - (sumProdTotal + sumEquiTotal + sumStaffTotal + sumOCTotal)); 
		if(sumOrdeTotal > 0)
			rentabilidadTotal = ((utilidadTotal/sumOrdeTotal) * 100);
		else rentabilidadTotal = 0;
		
		rentabilidadTotal = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(rentabilidadTotal));
	%>
		<tr><td colspan="16">&nbsp;</td></tr>
	   <TR class="reportCellEven reportCellCode">
	   		<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
	   
			<td align="left" width="40" colspan="5">
	 			&nbsp;
	 		</td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumSubTotal, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumExtras, BmFieldType.CURRENCY) %>
			<%//= HtmlUtil.formatReportCell(sFParams, "" + sumCXCTotal, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumOrdeTotal, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumIva, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumProdTotal, BmFieldType.CURRENCY) %>
			<%//= HtmlUtil.formatReportCell(sFParams, "" + sumEquiTotal, BmFieldType.CURRENCY) %>
			<%//= HtmlUtil.formatReportCell(sFParams, "" + sumStaffTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumOCTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + utilidadTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + rentabilidadTotal, BmFieldType.PERCENTAGE) %>	
			<%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
		</TR>   

</TABLE>

<%
	
	}// Fin de if(no carga datos)
	
     
%>  

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
			}//fin else moneda
	}// FIN DEL CONTADOR
	pmConnCount.close();
	PmConnOrdeGroup.close();
	pmConn2.close();
    pmConn.close();%>
  </body>
</html>