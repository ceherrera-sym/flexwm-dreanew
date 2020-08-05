
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
	String title = "Utilidad por Pedido";
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
	
	
   	String where = "",filters = "",startDate = "", endDate = "";
   	String sqlCurrency = "",sql = "";
	int programId = 0,orderTypeId = 0,orderId = 0,userId = 0,customerId = 0,industryId = 0;
	int currencyId = 0;
	double defaultParity = 0;
	boolean rental = true;

	// Obtener parametros
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("orde_ordertypeid") != null) orderTypeId = Integer.parseInt(request.getParameter("orde_ordertypeid"));
   	if (request.getParameter("orde_orderid") != null) orderId = Integer.parseInt(request.getParameter("orde_orderid"));
   	if (request.getParameter("orde_userid") != null) userId = Integer.parseInt(request.getParameter("orde_userid"));
   	if (request.getParameter("orde_customerid") != null) customerId = Integer.parseInt(request.getParameter("orde_customerid"));
   	if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));
   	if (request.getParameter("orde_lockstart") != null) startDate = request.getParameter("orde_lockstart");
   	if (request.getParameter("orde_lockend") != null) endDate = request.getParameter("orde_lockend");
   	if (request.getParameter("orde_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
   
   	if (orderTypeId > 0) {
		where += " AND orde_ordertypeid = " + orderTypeId;
		filters += "<i>Tipo de Pedido: </i>" + request.getParameter("orde_ordertypeidLabel") + ", ";
	}
	
   	if (orderId > 0) {
   		where += " AND orde_orderid = " + orderId;
   		filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
   	}
   	
	if (userId > 0) {
		where += " AND orde_userid = " + userId;
		filters += "<i>Usuario: </i>" + request.getParameter("orde_useridLabel") + ", ";
	}
	
	if (customerId > 0) {
		where += " AND orde_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
	}
	
	if (industryId > 0) {
   		where += " AND cust_industryid = " + industryId;
   		filters += "<i>Giro: </i>" + request.getParameter("cust_industryidLabel") + ", ";
   	}
	
	if (!startDate.equals("")) {
		where += " AND orde_lockstart >= '" + startDate + " 00:00'";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}
	
	if (!endDate.equals("")) {
		where += " AND orde_lockstart <= '" + endDate + " 23:59'";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}
	
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
     if (currencyId > 0) {
        	bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
   	    	defaultParity = bmoCurrency.getParity().toDouble();
    	   	filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
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
    	    		+ " WHERE orde_orderid > 0 " 
    	   		 	+ where 
    	   			+ " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
    	   	System.out.println("SQL CURRENCY " +sqlCurrency );
   	} 	
   	
   	// Obtener disclosure de datos
    
	
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
	<link rel="stylesheet" type="text/css" href="/css/<%= defaultCss %>">	
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
<table>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
</table>


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
     		  " WHERE orde_orderid > 0 " + 
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
	            		<td class="reportHeaderCellCenter" colspan="22">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
	        		</tr>
	        		
	        	<%} %>
		<%
		sql = " SELECT * FROM projects " +
				 " LEFT JOIN orders ON (proj_orderid = orde_orderid) " +
	        	  " LEFT JOIN currencies ON (orde_currencyid = cure_currencyid) " +
		   		  " LEFT JOIN customers ON (proj_customerid = cust_customerid) " +
				  " LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
				  " LEFT JOIN cities ON (city_cityid = venu_cityid) " +
				  " LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
				  " LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
				  " LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +    
			      " LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) " +
				  " LEFT JOIN users ON (proj_userid = user_userid) " +
				  " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
		   		  " WHERE orde_orderid > 0 " + " AND proj_currencyid = " + currencyId +
				  where + 
		   		  " ORDER BY orde_orderid ASC";
		   	pmConn.doFetch(sql);%>

		<table class="report" border="0">
			<TR class="">      					
				<td class="reportHeaderCell">#</td>
				<td class="reportHeaderCell">Clave Pedido</td>				
				<td class="reportHeaderCell">Nomb. Pedido</td>
				<td class="reportHeaderCell">Fecha</td>
				<td class="reportHeaderCellCenter">Tipo</td>
				<td class="reportHeaderCellCenter">Moneda</td>
				<td class="reportHeaderCellCenter">Tipo de Cambio</td>
				<td class="reportHeaderCellCenter">Subtotal</td>
				<td class="reportHeaderCellCenter">Desc</td>
<!-- 				<td class="reportHeaderCellCenter">Nota C.</td> -->
				<td class="reportHeaderCellRight">Total sin Iva</td>
				<td class="reportHeaderCellCenter">Iva</td>
				<td class="reportHeaderCellRight">Costo Producto</td>
<!-- 				<td class="reportHeaderCellRight">Costo Recursos</td> -->
<!-- 				<td class="reportHeaderCellRight">Costo Personal</td> -->
				<td class="reportHeaderCellRight">Costo O.C.</td>
				<td class="reportHeaderCellRight">Utilidad</td>
				<td class="reportHeaderCellRight">Rentabilidad</td>
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

					
					//COSTO EQUIPOS
//		 			String sqlEqui = " SELECT SUM(ordq_quantity * ordq_basecost * ordq_days) as sumEquipmentsExtra FROM orderequipments " +
//		 				      " LEFT JOIN equipments ON (equi_equipmentid = ordq_orderequipmentid) " +			      
//		 					  " LEFT JOIN orders ON (orde_orderid = ordq_orderid) " +			      
//		 				      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
//		 				      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
					String sqlEqui = " SELECT ordq_quantity,ordq_basecost,ordq_days,orde_currencyid,orde_currencyParity FROM orderequipments " +
						      " LEFT JOIN equipments ON (equi_equipmentid = ordq_orderequipmentid) " +			      
							  " LEFT JOIN orders ON (orde_orderid = ordq_orderid) " +			      
						      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
						      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
					pmConn2.doFetch(sqlEqui);
					while(pmConn2.next()){
						double quantity = pmConn2.getDouble("ordq_quantity");
						double basecost = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("ordq_basecost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
								currencyId, defaultParity);
						double days =  pmConn2.getDouble("ordq_days");
						double total = quantity * basecost * days;
						sumEqui = total;
//		 				sumEqui = pmConn2.getDouble("sumEquipmentsExtra");
					}
					//System.out.println("sqlEqui22: "+sqlEqui);
					
					boolean staff = false;
					int countGroup = 0;
					//COSTOS DE STAFF
					String sqlStaff = " SELECT ords_quantity, ords_basecost ,ords_days,orde_currencyid,orde_currencyParity " + 								
										" FROM orderstaff " +
										" LEFT JOIN orders ON (orde_orderid = ords_orderid) " +		
										" LEFT JOIN profiles ON (prof_profileid = ords_profileid) " +			      
										" WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
										" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
					
					pmConn2.doFetch(sqlStaff);
					while(pmConn2.next()){
						double quantity = pmConn2.getDouble("ords_quantity");
						double basecost = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("ords_basecost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
								currencyId, defaultParity);
						double days =  pmConn2.getDouble("ords_days");
						double total = quantity * basecost * days;
						
						sumStaff = total;
//		 				sumStaff = pmConn2.getDouble("sumItemStaffExtra");
						
							staff = true;
					}
					//System.out.println("staff: "+staff + "  contador: " +countGroup);

					if(!staff){
						String sqlWFlowUsersGroupCost = " SELECT prof_cost, orde_currencyid, orde_currencyParity" + 
								  " FROM wflowusers " +
								  " LEFT JOIN profiles ON (prof_profileid = wflu_profileid) " +	
								  " LEFT JOIN orders ON (orde_wflowid = wflu_wflowid) " +		
							      " WHERE wflu_wflowid = " + pmConn.getInt("proj_wflowid") +
							      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
						//System.out.println("sqlWFlowUsersGroupCost: "+sqlWFlowUsersGroupCost);
						pmConn2.doFetch(sqlWFlowUsersGroupCost);
						while(pmConn2.next()){
							sumStaff = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("prof_cost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
									currencyId, defaultParity);
//		 					sumStaff = pmConn2.getDouble("sumWFlowUsersGroupCost");
						}
					}
					
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
					
					
					//CXC DE TIPO NOTA DE CRÉDITO
					String sqlCXCCreditNote = " SELECT racc_total,racc_currencyid,racc_currencyParity " + 
									  " FROM raccounts " +
									  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
									  " WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
									  " AND ract_category = '" + BmoRaccountType.CATEGORY_CREDITNOTE  + "'" +
								      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
					
					//System.out.println("sqlCXCCreditNote: "+sqlCXCCreditNote);
					pmConn2.doFetch(sqlCXCCreditNote);
					while(pmConn2.next()){
						sumCXC = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("racc_total")	, pmConn2.getInt("racc_currencyid"), pmConn2.getDouble("racc_currencyParity"), 
								currencyId, defaultParity);
//		 				sumCXC = pmConn2.getDouble("racc_total");
					}
					
					
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
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_code"), BmFieldType.CODE)) %>
						<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_name"), BmFieldType.STRING) %>
					    <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockstart"), BmFieldType.DATETIME) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("ortp_name"), BmFieldType.STRING)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE)) %>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, ""+defaultParity, BmFieldType.NUMBER)) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + subTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + extras, BmFieldType.CURRENCY) %>
<%-- 						<%= HtmlUtil.formatReportCell(sFParams, "" + sumCXC, BmFieldType.CURRENCY) %> --%>
						<%= HtmlUtil.formatReportCell(sFParams, "" + ordeTotal, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + iva, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumProd, BmFieldType.CURRENCY) %>
<%-- 						<%= HtmlUtil.formatReportCell(sFParams, "" + sumEqui, BmFieldType.CURRENCY) %> --%>
<%-- 						<%= HtmlUtil.formatReportCell(sFParams, "" + sumStaff, BmFieldType.CURRENCY) %> --%>
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
			   
					<td align="left" width="40" colspan="6">
			 			&nbsp;
			 		</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumSubTotal, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumExtras, BmFieldType.CURRENCY) %>
<%-- 					<%= HtmlUtil.formatReportCell(sFParams, "" + sumCXCTotal, BmFieldType.CURRENCY) %>			 --%>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumOrdeTotal, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumIva, BmFieldType.CURRENCY) %>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumProdTotal, BmFieldType.CURRENCY) %>
<%-- 					<%= HtmlUtil.formatReportCell(sFParams, "" + sumEquiTotal, BmFieldType.CURRENCY) %> --%>
<%-- 					<%= HtmlUtil.formatReportCell(sFParams, "" + sumStaffTotal, BmFieldType.CURRENCY) %> --%>
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
   		  " WHERE orde_orderid > 0 " + 
		  where + 
   		  " ORDER BY orde_orderid ASC";
   	pmConn.doFetch(sql);%>

<table class="report" border="0">
	<TR class="">      					
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Clave Pedido</td>
		<td class="reportHeaderCell">Nomb. Pedido</td>
		<td class="reportHeaderCell">Fecha</td>
		<td class="reportHeaderCellCenter">Tipo</td>
		<td class="reportHeaderCellCenter">Moneda</td>
		<td class="reportHeaderCellCenter">Tipo de Cambio</td>
		<td class="reportHeaderCellCenter">Subtotal</td>
		<td class="reportHeaderCellCenter">Desc</td>
<!-- 		<td class="reportHeaderCellCenter">Nota C.</td> -->
		<td class="reportHeaderCellRight">Total sin Iva</td>
		<td class="reportHeaderCellCenter">Iva</td>
		<td class="reportHeaderCellRight">Costo Producto</td>
<!-- 		<td class="reportHeaderCellRight">Costo Recursos</td> -->
<!-- 		<td class="reportHeaderCellRight">Costo Personal</td> -->
		<td class="reportHeaderCellRight">Costo O.C.</td>
		<td class="reportHeaderCellRight">Utilidad</td>
		<td class="reportHeaderCellRight">Rentabilidad</td>
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

			
			//COSTO EQUIPOS
// 			String sqlEqui = " SELECT SUM(ordq_quantity * ordq_basecost * ordq_days) as sumEquipmentsExtra FROM orderequipments " +
// 				      " LEFT JOIN equipments ON (equi_equipmentid = ordq_orderequipmentid) " +			      
// 					  " LEFT JOIN orders ON (orde_orderid = ordq_orderid) " +			      
// 				      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
// 				      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
			String sqlEqui = " SELECT ordq_quantity,ordq_basecost,ordq_days,orde_currencyid,orde_currencyParity FROM orderequipments " +
				      " LEFT JOIN equipments ON (equi_equipmentid = ordq_orderequipmentid) " +			      
					  " LEFT JOIN orders ON (orde_orderid = ordq_orderid) " +			      
				      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
				      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
			pmConn2.doFetch(sqlEqui);
			while(pmConn2.next()){
				double quantity = pmConn2.getDouble("ordq_quantity");
				double basecost = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("ordq_basecost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
						currencyId, defaultParity);
				double days =  pmConn2.getDouble("ordq_days");
				double total = quantity * basecost * days;
				sumEqui = total;
// 				sumEqui = pmConn2.getDouble("sumEquipmentsExtra");
			}
			//System.out.println("sqlEqui22: "+sqlEqui);
			
			boolean staff = false;
			int countGroup = 0;
			//COSTOS DE STAFF
			String sqlStaff = " SELECT ords_quantity, ords_basecost ,ords_days,orde_currencyid,orde_currencyParity " + 								
								" FROM orderstaff " +
								" LEFT JOIN orders ON (orde_orderid = ords_orderid) " +		
								" LEFT JOIN profiles ON (prof_profileid = ords_profileid) " +			      
								" WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
								" AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
			
			pmConn2.doFetch(sqlStaff);
			while(pmConn2.next()){
				double quantity = pmConn2.getDouble("ords_quantity");
				double basecost = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("ords_basecost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
						currencyId, defaultParity);
				double days =  pmConn2.getDouble("ords_days");
				double total = quantity * basecost * days;
				
				sumStaff = total;
// 				sumStaff = pmConn2.getDouble("sumItemStaffExtra");
				
					staff = true;
			}
			//System.out.println("staff: "+staff + "  contador: " +countGroup);

			if(!staff){
				String sqlWFlowUsersGroupCost = " SELECT prof_cost, orde_currencyid, orde_currencyParity" + 
						  " FROM wflowusers " +
						  " LEFT JOIN profiles ON (prof_profileid = wflu_profileid) " +	
						  " LEFT JOIN orders ON (orde_wflowid = wflu_wflowid) " +		
					      " WHERE wflu_wflowid = " + pmConn.getInt("proj_wflowid") +
					      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
				//System.out.println("sqlWFlowUsersGroupCost: "+sqlWFlowUsersGroupCost);
				pmConn2.doFetch(sqlWFlowUsersGroupCost);
				while(pmConn2.next()){
					sumStaff = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("prof_cost"), pmConn2.getInt("orde_currencyid"), pmConn2.getDouble("orde_currencyParity"), 
							currencyId, defaultParity);
// 					sumStaff = pmConn2.getDouble("sumWFlowUsersGroupCost");
				}
			}
			
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
			
			
			//CXC DE TIPO NOTA DE CRÉDITO
			String sqlCXCCreditNote = " SELECT racc_total,racc_currencyid,racc_currencyParity " + 
							  " FROM raccounts " +
							  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
							  " WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
							  " AND ract_category = '" + BmoRaccountType.CATEGORY_CREDITNOTE  + "'" +
						      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
			
			//System.out.println("sqlCXCCreditNote: "+sqlCXCCreditNote);
			pmConn2.doFetch(sqlCXCCreditNote);
			while(pmConn2.next()){
				sumCXC = pmCurrencyExchange.currencyExchange(pmConn2.getDouble("racc_total")	, pmConn2.getInt("racc_currencyid"), pmConn2.getDouble("racc_currencyParity"), 
						currencyId, defaultParity);
// 				sumCXC = pmConn2.getDouble("racc_total");
			}
			
			
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
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_code"), BmFieldType.CODE)) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_name"), BmFieldType.STRING) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("orde_lockstart"), BmFieldType.DATETIME) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("ortp_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCurrency.getCode().toString(), BmFieldType.CODE)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, ""+defaultParity, BmFieldType.NUMBER)) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + subTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + extras, BmFieldType.CURRENCY) %>
<%-- 				<%= HtmlUtil.formatReportCell(sFParams, "" + sumCXC, BmFieldType.CURRENCY) %> --%>
				<%= HtmlUtil.formatReportCell(sFParams, "" + ordeTotal, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + iva, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumProd, BmFieldType.CURRENCY) %>
<%-- 				<%= HtmlUtil.formatReportCell(sFParams, "" + sumEqui, BmFieldType.CURRENCY) %> --%>
<%-- 				<%= HtmlUtil.formatReportCell(sFParams, "" + sumStaff, BmFieldType.CURRENCY) %> --%>
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
	   
			<td align="left" width="40" colspan="6">
	 			&nbsp;
	 		</td>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumSubTotal, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumExtras, BmFieldType.CURRENCY) %>
<%-- 			<%= HtmlUtil.formatReportCell(sFParams, "" + sumCXCTotal, BmFieldType.CURRENCY) %>			 --%>
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumOrdeTotal, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumIva, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumProdTotal, BmFieldType.CURRENCY) %>
<%-- 			<%= HtmlUtil.formatReportCell(sFParams, "" + sumEquiTotal, BmFieldType.CURRENCY) %> --%>
<%-- 			<%= HtmlUtil.formatReportCell(sFParams, "" + sumStaffTotal, BmFieldType.CURRENCY) %> --%>
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
