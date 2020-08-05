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
 
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.symgae.server.SFServerUtil"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 
	// Imprimir
	String print = "0";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgae_report.xls");
    }
%>

<%
	//Inicializar variables
	String title = "Utilidad por Proyectos";

	String sql = "", where = "";
	String startDate = "", endDate = "";
	String status = "";
	int wflowTypeId = 0;
   	int venueId = 0;
   	int userId = 0;
   	int customerId = 0;
   	int referralId = 0;
   	int wflowPhaseId = 0;   	
	String groupFilter = "";
	String filters = "";
	int programId = 0;

	// Obtener parametros
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");	
	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid")); 
   	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
    if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
 
    // Filtro 
   	if (customerId > 0) {
		where += " and proj_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
	}

	// Filtros listados
	if (wflowTypeId > 0) {
		where += " and wfty_wflowtypeid = " + wflowTypeId;
		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
	}
	
	if (wflowPhaseId > 0) {
		where += " and wflw_wflowphaseid = " + wflowPhaseId;
		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
	}
	
	if (venueId > 0) {
		where += " and proj_venueid = " + venueId;
		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
	}
	
	if (!status.equals("")) {
		where += " and proj_status like '" + status + "'";
		filters += "<i>Status: </i>" + request.getParameter("proj_statusLabel") + ", ";
	}
	
	if (userId > 0) {
		where += " and user_userid = " + userId;
			filters += "<i>Vendedor: </i>" + request.getParameter("proj_useridLabel") + ", ";
	}
	
	if (referralId > 0) {
	    where += " and cust_referralid = " + referralId;
	    filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
	}
	
	if (!startDate.equals("")) {
		where += " and proj_startdate >= '" + startDate + " 00:00'";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}
	
	if (!endDate.equals("")) {
		where += " and proj_enddate <= '" + endDate + " 23:59'";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}
	
	
   	
	
    BmoCompany bmoCompany = new BmoCompany();
    PmCompany pmCompany = new PmCompany(sFParams);
   	
   	PmConn pmConn = new PmConn(sFParams);
   	pmConn.open();
   	
   	PmConn pmConn2 = new PmConn(sFParams);
   	pmConn2.open();
	 //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();

sql = "  SELECT COUNT(*) as contador FROM  " + SQLUtil.formatKind(sFParams, "projects ") +
	  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" on (proj_customerid = cust_customerid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " venues")+" on (venu_venueid = proj_venueid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" on (city_cityid = venu_cityid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflw_wflowid = proj_wflowid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" on (wfty_wflowtypeid = proj_wflowtypeid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) " +	     
" right join  " + SQLUtil.formatKind(sFParams, "orders")+" on (proj_orderid = orde_orderid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (proj_userid = user_userid) " +
" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" on (wfph_wflowcategoryid = wfca_wflowcategoryid) " +
	  " where proj_projectid > 0 " + 
where + 
	  " ORDER BY proj_startdate ASC";
	  
	  int count =0;
		//ejecuto el sql
		pmConnCount.doFetch(sql);
		if(pmConnCount.next())
			count=pmConnCount.getInt("contador");
		System.out.println("contador de reportes -->  "+count);

   	pmConn.doFetch(sql);
   	
   	double totalOrdeCost = 0;
   	double totalOrdeEquip = 0;
   	double totalOrdeStaff = 0;
	double totalOrdePrice = 0;
	double totalReqiCost = 0;
	double totalReqiPrice = 0;
	
   		  	
%>

<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">	
</head>

<body class="default"  style="padding-right: 10px">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td class="reportTitle" align="left">
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


<table class="report" border="0">
	<TR class="">      					
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Proyecto</td>
		<td class="reportHeaderCell">Fecha</td>
		<td class="reportHeaderCell">Tipo</td>
		<td class="reportHeaderCellRight">Total</td>
		<td class="reportHeaderCellRight">Costo Producto</td>
		<td class="reportHeaderCellRight">Costo Recursos</td>
		<td class="reportHeaderCellRight">Costo Personal</td>
		<td class="reportHeaderCellRight">Costo O.C.</td>
		<td class="reportHeaderCellRight">Utilidad</td>
		<td class="reportHeaderCellRight">Rentabilidad</td>

	</TR>
	<%//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
<%=messageTooLargeList %>
		<% 
	}else{
		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "projects ") +
		   		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" on (proj_customerid = cust_customerid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " venues")+" on (venu_venueid = proj_venueid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" on (city_cityid = venu_cityid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflw_wflowid = proj_wflowid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" on (wfty_wflowtypeid = proj_wflowtypeid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) " +	     
			      " right join  " + SQLUtil.formatKind(sFParams, "orders")+" on (proj_orderid = orde_orderid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on (proj_userid = user_userid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" on (wfph_wflowcategoryid = wfca_wflowcategoryid) " +
		   		  " where proj_projectid > 0 " + 
				  where + 
		   		  " ORDER BY proj_startdate ASC";
		   	pmConn.doFetch(sql);
		DecimalFormat f = new DecimalFormat("####.##");
	    double sumOrdeItemCost = 0, sumEquimentCost = 0, sumStaffCost = 0, sumReqiItemCost = 0;
	    double sumOrdeItemPrice = 0, sumEquimentPrice = 0, sumStaffPrice = 0, sumReqiItemPrice = 0;
	    double utilidad = 0, rentabilidad = 0,  rentabilidadTotal = 0;
		int i = 0; 
		while(pmConn.next()) {
			//Obtener el costos de los items ligados a un producto
			sql = " SELECT SUM(ordi_quantity * prod_cost * ordi_days) as sumitemcost, " +
				  " SUM(ordi_quantity * ordi_price * ordi_days) as sumitemprice  FROM  " + SQLUtil.formatKind(sFParams, "orderitems ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_ordergroupid = ordi_ordergroupid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +			      
				  " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
				  " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
				  " AND NOT ordi_productid IS NULL";
			pmConn2.doFetch(sql);
			if(pmConn2.next()) {
				sumOrdeItemCost = pmConn2.getDouble("sumitemcost");
				sumOrdeItemPrice = pmConn2.getDouble("sumitemprice");
			}
			
			//Obtener el costos de los items que no estan ligados a un producto
			sql = " SELECT SUM(ordi_quantity * ordi_price * ordi_days) as sumitems FROM " + SQLUtil.formatKind(sFParams, "orderitems ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) " +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_ordergroupid = ordi_ordergroupid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordg_orderid) " +			      
			      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
			      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
				  " AND ordi_productid IS NULL";
			pmConn2.doFetch(sql);
			if(pmConn2.next()) {
				sumOrdeItemCost += pmConn2.getDouble("sumitems");
				sumOrdeItemPrice += pmConn2.getDouble("sumitems");
			}
			
			//Obtener el costos de los recursos ligados a un equipo
			sql = " SELECT SUM(ordq_quantity * equi_price * ordq_days) as sumequitcost, " +
				  "	SUM(ordq_quantity * ordq_price * ordq_days) as sumequitprice FROM " + SQLUtil.formatKind(sFParams, "orderequipments ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON (equi_equipmentid = ordq_orderequipmentid) " +			      
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordq_orderid) " +			     
			      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
			      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
				  " AND NOT ordq_equipmentid IS NULL";
			pmConn2.doFetch(sql);
			if(pmConn2.next()) {
				sumEquimentCost = pmConn2.getDouble("sumequitcost");
				sumEquimentPrice = pmConn2.getDouble("sumequitprice");
			}
			
			//Obtener el costos de los recusos no ligados a un equipo
			sql = " SELECT SUM(ordq_quantity * ordq_price * ordq_days) as sumequiments FROM " + SQLUtil.formatKind(sFParams, "orderequipments ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " equipments")+" ON (equi_equipmentid = ordq_orderequipmentid) " +			      
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ordq_orderid) " +			      
			      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
			      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
				  " AND ordq_equipmentid IS NULL";
			pmConn2.doFetch(sql);
			if(pmConn2.next()){ 
				sumEquimentCost += pmConn2.getDouble("sumequiments");
				sumEquimentPrice += pmConn2.getDouble("sumequiments");
			}
			
			//Obtener el costos de los staff
			sql = " SELECT SUM(ords_quantity * ords_price * ords_days) as sumitstaff FROM  " + SQLUtil.formatKind(sFParams, "orderstaff ") +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (orde_orderid = ords_orderid) " +			      
			      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
			      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'";
			pmConn2.doFetch(sql);
			if(pmConn2.next()) sumStaffCost = pmConn2.getDouble("sumitstaff");
			
			//Obtener el costos de las ordenes de compra ligadas a un producto
			sql = " SELECT SUM(rqit_quantity * prod_rentalprice * rqit_days) as sumrqitcost, " +
				  " SUM(rqit_quantity * rqit_price * rqit_days) as sumrqitprice  FROM  " + SQLUtil.formatKind(sFParams, "requisitionitems ") +			
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = rqit_productid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +			      
				  " WHERE reqi_orderid = " + pmConn.getInt("proj_orderid") +
				  " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
				  " AND NOT rqit_productid IS NULL";
			pmConn2.doFetch(sql);
			if(pmConn2.next()) {
				sumReqiItemCost = pmConn2.getDouble("sumrqitcost");
				sumReqiItemPrice = pmConn2.getDouble("sumrqitprice");				
			}
			
			//Obtener el costos de las ordenes de compra no ligadas a un producto
			sql = " SELECT SUM(rqit_quantity * rqit_price * rqit_days) as sumreqiitems FROM " + SQLUtil.formatKind(sFParams, " requisitionitems ") +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = rqit_productid) " +
				  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON (reqi_requisitionid = rqit_requisitionid) " +			      
				  " WHERE reqi_orderid = " + pmConn.getInt("proj_orderid") +
				  " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
				  " AND rqit_productid IS NULL";
			pmConn2.doFetch(sql);
			if(pmConn2.next()) {
				sumReqiItemCost += pmConn2.getDouble("sumreqiitems");
				sumReqiItemPrice += pmConn2.getDouble("sumreqiitems");
			}
			
			//Sumar los costos y el precios
			double ordeCost = sumOrdeItemCost;
			double ordePrice = sumOrdeItemPrice;
			double ordeEquip = + sumEquimentPrice;
			double ordeStaff = + sumStaffCost;
			double reqiCost =  sumReqiItemCost;
			double reqiPrice = sumReqiItemPrice;
						
			totalOrdeCost += ordeCost;
			totalOrdeEquip += ordeEquip;
			totalOrdeStaff += ordeStaff;
			totalOrdePrice += ordePrice + reqiPrice;
			totalReqiCost += reqiCost;
			totalReqiPrice += reqiPrice;
			
			rentabilidad = 0;
			utilidad = ((ordePrice + sumReqiItemPrice) - (ordeCost + reqiCost + ordeEquip + ordeStaff));
			if((ordePrice + sumReqiItemPrice) > 0)
				rentabilidad = (utilidad/(ordePrice + sumReqiItemPrice)) * 100;
	%>
	
			<TR class="reportCellEven">
				<td align="left" width="40">
		  			&nbsp;<%= i + 1%>
		  		</td> 
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects","proj_code")+" "+pmConn.getString("projects","proj_name") , BmFieldType.STRING)) %>
				<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects","proj_startdate"), BmFieldType.DATETIME) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("wflowtypes","wfty_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + (ordePrice + sumReqiItemPrice), BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + ordeCost, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + ordeEquip, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + ordeStaff, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + reqiCost, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + utilidad, BmFieldType.CURRENCY) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + SFServerUtil.roundCurrencyDecimals(rentabilidad), BmFieldType.PERCENTAGE) %>
			</TR>
	<% i++;
	   } 
		
		rentabilidadTotal = (((totalOrdePrice) - (totalOrdeCost + totalReqiCost + totalOrdeEquip + totalOrdeStaff)) / totalOrdePrice) * 100;		
		
		%>
	   <TR class="reportCellEven" valign="bottom">
			<td align="left" width="40" colspan="4">
	 			&nbsp;<b>TOTALES</b>
	 		</td>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalOrdePrice, BmFieldType.CURRENCY) %>			
			<%= HtmlUtil.formatReportCell(sFParams, "" +  totalOrdeCost, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" +  totalOrdeEquip, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" +  totalOrdeStaff, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + totalReqiCost, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + ((totalOrdePrice) - (totalOrdeCost + totalReqiCost + totalOrdeEquip + totalOrdeStaff)), BmFieldType.CURRENCY) %>				
			<%= HtmlUtil.formatReportCell(sFParams, "" + SFServerUtil.roundCurrencyDecimals(rentabilidadTotal), BmFieldType.PERCENTAGE) %>
		</TR>   

</TABLE>

            
  
<%
	
%>  

	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% }
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	}//Fin de validacion de 5 mil registros
	pmConn2.close();
    pmConn.close();
    pmConnCount.close();
	%>
  </body>
</html>