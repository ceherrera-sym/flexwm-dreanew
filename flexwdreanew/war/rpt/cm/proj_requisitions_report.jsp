<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla
 * @version 2018-06
 */ -->
 
<%@include file="/inc/login.jsp" %>

<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import= "com.flexwm.shared.cm.BmoProject"%>
<%@page import= "com.flexwm.shared.op.BmoOrder"%>
<%@page import= "com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import= "com.flexwm.shared.op.BmoRequisition"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.symgae.server.SFServerUtil"%>


<%
	// Inicializar variables
 	String title = "Ord.Compra por Proyectos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();	
	BmoRequisition bmoRequisition = new BmoRequisition();
	
	BmoBankMovement bmoBankMovement = new BmoBankMovement();
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
   	String sql = "", where = "", groupFilter = "",filters = "",sqlCurrency = "";
   	String startDate = "", endDate = "", startDateCreate = "", endDateCreate = "";
   	String status = "", paymentStatusOrde = "";
   	int wflowTypeId = 0, projectId = 0, areaId = 0;
   	int wflowCategoryId = 0;
   	int venueId = 0;
   	int userId = 0;
   	int customerId = 0;
   	int referralId = 0;
   	int wflowPhaseId = 0;
   	int programId = 0;
   	int currencyId = 0;
   
   	double sumTotal = 0,defaultParity = 0;
   	
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
   	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
    if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
   	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid"));
   	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
   	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");
   	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
   	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
    if (request.getParameter("paymentStatus") != null) paymentStatusOrde = request.getParameter("paymentStatus");
    if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));
    if (request.getParameter("proj_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("proj_currencyid"));

	// Construir filtros 
	
	// Filtro listados	
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
   	
   	if (customerId > 0) {
   		where += " AND proj_customerid = " + customerId;
   		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
   	}
   	
   	if (areaId > 0) {
   		where += " AND user_areaid = " + areaId;
		filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
   	
   	if (referralId > 0) {
        where += " AND cust_referralid = " + referralId;
        filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
    }
   	
   	if (venueId > 0) {
   		where += " AND venu_venueid = " + venueId;
   		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
   	}
   	
   	if (!status.equals("")) {
   		//where += " AND proj_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("proj_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("proj_statusLabel") + ", ";
   	}
   	
   	if (!paymentStatusOrde.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatusOrde);
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
   	
   	if (currencyId > 0) {
		bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
		defaultParity = bmoCurrency.getParity().toDouble();

		filters += "<i>Moneda: </i>" + request.getParameter("proj_currencyidLabel")
		+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
	} else {
		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM projects "
				+ " LEFT JOIN customers ON (proj_customerid = cust_customerid) "
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
				+ groupFilter 
				+ where 
				+ " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
	}
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;

  //Conexion a Base de Datos
  	PmConn pmCurrencyWhile =new PmConn(sFParams);
      pmCurrencyWhile.open();
  	
  	
  	PmConn pmConnListProject = new PmConn(sFParams); //Conexion para obtener todos los proyectos con OC
  	pmConnListProject.open();
  	
  	PmConn pmConnListOrder = new PmConn(sFParams); //Conexion para obtener todos los pedidos y sus OC de un proyecto
  	pmConnListOrder.open();
  	
  	PmConn pmConnListCountOC = new PmConn(sFParams); //Conexion para contar cuantas OC tiene el pedido 
  	pmConnListCountOC.open();
  	
  	PmConn	pmBankMovConcep = new PmConn(sFParams); 
  	pmBankMovConcep.open();
  	
  	PmConn	pmBankMov = new PmConn(sFParams); 
  	pmBankMov.open();
  			
  	PmConn pmConnListOrderExtra = new PmConn(sFParams); //Conexion para obtener todos los pedido extrar de un pedido
  	pmConnListOrderExtra.open();
  	
	
    //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();

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
	
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>

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
			<b>Agrupado Por:</b> Proyecto, <b>Ordenado por:</b> Fecha de Evento
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<br>
<table width="100%">
	<tr>
		<!--<td width="50%"> <div id="chart_div" style="width: 100%; height: 400px;"></div> </td>
		<td width="50%"> <div id="chart_div2" style="width: 100%; height: 400px;"></div> </td>-->
	</tr>
</table>

<table class="report">   
    <%	
	sql = " SELECT COUNT(*) AS contador FROM requisitions "+
			  " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
			  " LEFT JOIN  areas on (area_areaid = reqi_areaid)" +
			  " LEFT JOIN companies on (comp_companyid = reqi_companyid) " +	
			  " LEFT JOIN  currencies on (reqi_currencyid = cure_currencyid) " +
			  " LEFT JOIN suppliers on (supl_supplierid = reqi_supplierid) "+
			  " LEFT JOIN  bankmovements  on (bkmv_requisitionid = reqi_requisitionid) "+
			  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) "+
			  " LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
			  " LEFT JOIN projects ON(orde_orderid= proj_orderid) " +
			  " LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
		      " LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
		      " LEFT JOIN cities ON(city_cityid = venu_cityid) " +
		      " LEFT JOIN users ON(user_userid = orde_userid) " +				
		      " LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
		      " LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
		      " LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
		      " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
			  "	WHERE reqi_orderid IN (SELECT orde_orderid "+
				                        " FROM orders " +
				                        " WHERE orde_originreneworderid "+
														" IN( SELECT orde_orderid FROM orders "+
																" LEFT JOIN projects ON (orde_orderid = proj_orderid) "+
																" WHERE proj_projectid  >0 )) "+
				" OR  reqi_orderid IN (SELECT orde_orderid "+
				                        " FROM orders WHERE orde_orderid " +
														"IN( SELECT orde_orderid FROM orders "+
																" LEFT JOIN projects ON (orde_orderid = proj_orderid)" +
																"WHERE proj_projectid  > 0))" +
				
				" AND wfca_programid >0" +
				  where ;		
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
    if (!(currencyId > 0)) {
    	int currencyIdWhile = 0;
 		pmCurrencyWhile.doFetch(sqlCurrency);
 		while (pmCurrencyWhile.next()) {
 			currencyId =  pmCurrencyWhile.getInt("cure_currencyid");
	        defaultParity = pmCurrencyWhile.getDouble("cure_parity");
	        
	        
 	        //Consulta para obtener todos los proyectos con OC
 	        pmConnListProject.doFetch("SELECT distinct(proj_projectid),proj_code,proj_name,proj_currencyid  " +
 	    			" FROM requisitions " +
 	    			" LEFT JOIN orders ON(reqi_orderid = orde_orderid) " +
 	    			" LEFT JOIN projects ON(orde_orderid = proj_orderid) " +
 	    			" LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "+
 	    			" WHERE proj_orderid IN ( " +
 	    			" SELECT reqi_orderid FROM projects " +
 	    					" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
 	    					" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
 	    					" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
 	    					" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
 	    					" LEFT JOIN users ON(user_userid = proj_userid) " +
 	    					" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
 	    					" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
 	    					" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
 	    					" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
 	    					" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
 	    				    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
 	    					" WHERE wfca_programid = " + programId +
 	    					where +
 	    					" ORDER BY proj_startdate ASC )" + 
 	    					" AND proj_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
 	    			" ORDER BY proj_projectid");
 	        
 	    	int n = 0, countPr = 0;
 	    	
 	    	 int  i = 0, y =1;
 	         double total = 0, totalProyect = 0;
 	         int  projectId2 = 0;
 	         int orderId = 0, orderId2 = 0, countReqi = 0;
 	         
 	         double amountTotal1 = 0, discountTotal1 = 0, taxTotal1 = 0, holdbackTotal1 = 0, 
 						totalTotal1 = 0, paymentsTotal1 = 0, balanceTotal1 = 0;
 	         
 	         
 	    	double sumTotalPV = 0;
 	            while(pmConnListProject.next()) {				
					
 	            	countPr++;
 	    			int proj_projectid = pmConnListProject.getInt("projects", "proj_projectid");	
 	    			
 	    			sql = "SELECT * FROM requisitions "+
 	    				  " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
 	    				  " LEFT JOIN  areas on (area_areaid = reqi_areaid)" +
 	    				  " LEFT JOIN companies on (comp_companyid = reqi_companyid) " +	
 	    				  " LEFT JOIN  currencies on (reqi_currencyid = cure_currencyid) " +
 	    				  " LEFT JOIN suppliers on (supl_supplierid = reqi_supplierid) "+
 	    				  " LEFT JOIN  bankmovements  on (bkmv_requisitionid = reqi_requisitionid) "+
 	    				  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) "+
 	    				  " LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
 	    				  " LEFT JOIN projects ON(orde_orderid= proj_orderid) " +
 	    				  " LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
 	    			      " LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
 	    			      " LEFT JOIN cities ON(city_cityid = venu_cityid) " +
 	    			      " LEFT JOIN users ON(user_userid = orde_userid) " +				
 	    			      " LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
 	    			      " LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
 	    			      " LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
 	    			      " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
 	    				  "	WHERE reqi_orderid IN (SELECT orde_orderid "+
 	    					                        " FROM orders " +
 	    					                        " WHERE orde_originreneworderid "+
 	    															" IN( SELECT orde_orderid FROM orders "+
 	    																	" LEFT JOIN projects ON (orde_orderid = proj_orderid) "+
 	    																	" WHERE proj_projectid = " + proj_projectid +")) "+
 	    					" OR  reqi_orderid IN (SELECT orde_orderid "+
 	    					                        " FROM orders WHERE orde_orderid " +
 	    															"IN( SELECT orde_orderid FROM orders "+
 	    																	" LEFT JOIN projects ON (orde_orderid = proj_orderid)" +
 	    																	"WHERE proj_projectid = " + proj_projectid +"))" +
 	    					
 	    					" AND wfca_programid = " + programId +
 	    					  where ;
 	    			System.out.println("pmConnListOrder: "+sql);
 	    			pmConnListOrder.doFetch(sql);
 	    			double amountTotal = 0, discountTotal = 0, taxTotal = 0, holdbackTotal = 0, 
 	    					totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
 	    			int countOC = 0;
 	    			
 	    	          while(pmConnListOrder.next()) {
 	    	        	  countOC ++;
 	    	        	//Estatus
 	    					bmoRequisition.getStatus().setValue(pmConnListOrder.getString("requisitions", "reqi_status"));
 	    					bmoRequisition.getDeliveryStatus().setValue(pmConnListOrder.getString("requisitions", "reqi_deliverystatus"));
 	    					bmoRequisition.getPaymentStatus().setValue(pmConnListOrder.getString("requisitions", "reqi_paymentstatus"));
 	    					bmoRequisition.getBmoRequisitionType().getType().setValue(pmConnListOrder.getString("requisitiontypes", "rqtp_type"));
 	    					   	  
 	    	        	    String reqiStatus = "", deliveryStatus = "", paymentStatus = "", activeStatus = "", projStatuts = "";
 	    	        	   
 	    	        	    if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
 	    	          		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
 	    	          		y = 0;
 	    	          		%>
 	    	          		<tr>
 	    	              		<td class="reportHeaderCellCenter" colspan="25">
 	    	              			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
 	    	              		</td>
 	    	          		</tr>
 	    	          		<%
 	    	          	}
 	    				//si el projecto de iteracion es diferente al que ya esta cargado pinta de nuevo la cabecera 
 	    	          		if((proj_projectid != projectId2)){
 	    	          			projectId2 = proj_projectid;
 	    	          			
 	    	    %>
 	    				    	<tr>
 	    							<td colspan="20" class="reportGroupCell">
 	    							    <%= pmConnListProject.getString("proj_code") %> - <%= pmConnListProject.getString("proj_name") %>
 	    							</td>
 	    						</tr>
 	    						
 	    						<tr>
 	    							<td class="reportHeaderCellCenter">#</td>
 	    							<td class="reportHeaderCell">Clave</td>
 	    							<td class="reportHeaderCell">Nombre</td>
 	    							<td class="reportHeaderCell">Tipo O.C</td>
 	    							<td class="reportHeaderCell">Proveedor</td>
 	    							<td class="reportHeaderCell">Solicita</td>
 	    							<td class="reportHeaderCell">Departamento</td>
 	    <%-- 						<% 	if (enableBudgets) { %> --%>
 	    <!-- 								<td class="reportHeaderCell">Presup.</td> -->
 	    <!-- 								<td class="reportHeaderCell">Item Presup.</td>	 -->
 	    <%-- 						<% 	} %> --%>
 	    							<td class="reportHeaderCell">Empresa</td>
 	    							<td class="reportHeaderCell">Pedido</td>
 	    							<td class="reportHeaderCell">F. Solicitud</td>
 	    							<td class="reportHeaderCell">F. Entrega</td>
 	    							<td class="reportHeaderCell">F. Pago</td>
 	    							<td class="reportHeaderCell">Estatus</td>
 	    							<td class="reportHeaderCell">Estatus MB</td>
 	    							<td class="reportHeaderCell">Entrega</td>  
 	    							<td class="reportHeaderCell">Pago</td>
 	    							<td class="reportHeaderCell">Moneda</td>
 	    							<td class="reportHeaderCellCenter">Tipo de Cambio</td>
 	    							<td class="reportHeaderCellRight">Monto</td>
 	    							<td class="reportHeaderCellRight">Descuento</td>
 	    							<td class="reportHeaderCellRight">IVA</td>
 	    							<td class="reportHeaderCellRight">Retenciones</td>
 	    							<td class="reportHeaderCellRight">Total</td>
 	    							<td class="reportHeaderCellRight">Pagos</td>
 	    							<td class="reportHeaderCellRight">Saldo</td>
 	    						</tr>    						
 	    			    <% } %>
 	    			    <% 
 	    			    	orderId = pmConnListOrder.getInt("orde_orderid");
 	    			    	totalProyect = 0;
 	    			    	y = 1;
 	    			    	i++; n++;
 	              			%>
 	    			          <tr class="reportCellEven">
 	    			          		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
 	    							<%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_code"),BmFieldType.CODE) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_name"),BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitiontypes", "rqtp_name"),BmFieldType.STRING)) %>
 	    					        <%//= HtmlUtil.formatReportCell(sFParams, bmoRequisition.getBmoRequisitionType().getType().getSelectedOption().getLabel(),BmFieldType.STRING) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("suppliers", "supl_code") + " " + pmConnListOrder.getString("suppliers", "supl_name"),BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("users", "user_code"),BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("areas", "area_name"),BmFieldType.STRING)) %>
 	    <%-- 					        <%	if (enableBudgets) { %> --%>
 	    <%-- 							        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("budg_name"),BmFieldType.CODE)) %> --%>
 	    <%-- 							        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("bgty_name"),BmFieldType.CODE)) %> --%>
 	    <%-- 					        <% 	} %> --%>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("companies", "comp_name"),BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("orders", "orde_code") + " " + pmConnListOrder.getString("orders", "orde_name"), BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_requestdate"),BmFieldType.DATETIME) %>
 	    					        <%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_deliverydate"),BmFieldType.DATETIME) %>
 	    					        <%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_paymentdate"),BmFieldType.DATETIME) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
 	    					       <td class="reportCellText">
 	    							<%			       
 	    					       		//Movimientos de banco pago proveedor
 	    					       String sqlbankMovConcep = "SELECT bkmv_code, bkmv_status FROM bankmovconcepts"+ 
 	    					    		 "  left join bankmovements on bkmc_bankmovementid = bkmv_bankmovementid " +
 	    					    		 "  left join paccounts on bkmc_paccountid = pacc_paccountid" +
 	    					    		 "  left join bankmovtypes on bkmt_bankmovtypeid = bkmv_bankmovtypeid" +
 	    					    		 "  where pacc_requisitionid = "+ pmConnListOrder.getString("requisitions", "reqi_requisitionid") +
 	    					    		 "  and bkmt_type = '"+ BmoBankMovType.TYPE_WITHDRAW+"'" +
 	    					    		 "  and bkmt_category = '"+ BmoBankMovType.CATEGORY_CXP + "'" ;
 	    					       
 	    					       pmBankMovConcep.doFetch(sqlbankMovConcep);
 	    					       String mb = "";
 	    					       while (pmBankMovConcep.next()){
 	    					    	   bmoBankMovement.getStatus().setValue(pmBankMovConcep.getString("bkmv_status"));
 	    								
 	    					    	   mb = pmBankMovConcep.getString("bkmv_code");
 	    					    	   mb = mb +": "+ bmoBankMovement.getStatus().getSelectedOption().getLabel();
 	    					    	   %>
 	    								<%= HtmlUtil.stringToHtml(mb)%> |
 	    							<%		
 	    					       }
 	    					      //Movimientos de banco Anticipo
 	    					       String sqlbankMov = "SELECT  bkmv_code, bkmv_status FROM bankmovements WHERE bkmv_requisitionid ="+ pmConnListOrder.getString("requisitions", "reqi_requisitionid");
 	    					       pmBankMov.doFetch(sqlbankMov);
 	    					       while (pmBankMov.next()){
 	    					    	   bmoBankMovement.getStatus().setValue(pmBankMov.getString("bkmv_status"));
 	    								
 	    					    	   mb = pmBankMov.getString("bkmv_code");
 	    					    	   mb = mb+": "+  bmoBankMovement.getStatus().getSelectedOption().getLabel();
 	    					    	   
 	    					    	   %>
 	    					    	   <%= HtmlUtil.stringToHtml(mb)%> |
 	    					      <%
 	    					       }
 	    					      
 	    					      %>
 	    					      	</td>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getPaymentStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
 	    					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("cure_code"),BmFieldType.CODE)) %>				
 	    					        <%= HtmlUtil.formatReportCell(sFParams, "" + pmConnListOrder.getDouble("reqi_currencyparity"),BmFieldType.NUMBER) %>
 	    					        <%
 	    					        
 	    				          	double amount = pmConnListOrder.getDouble("reqi_amount");
 	    				          	double discount = pmConnListOrder.getDouble("reqi_discount");
 	    				          	double tax = pmConnListOrder.getDouble("reqi_tax");
 	    				          	double holdback = pmConnListOrder.getDouble("reqi_holdback");
 	    				          	double total1 = pmConnListOrder.getDouble("reqi_total");
 	    				          	double payments = pmConnListOrder.getDouble("reqi_payments");					           		
 	    				          	double balance = pmConnListOrder.getDouble("reqi_balance");
 	    				          	
 	    				        	//Conversion a la moneda destino(seleccion del filtro)
 	    				        	if(pmConnListOrder.getInt("reqi_currencyid") !=  pmConnListProject.getInt("proj_currencyid")){   				        	
	 	    				          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	 	    				          	double parityOrigin = 0, parityDestiny = 0;
	 	    				          	currencyIdOrigin = pmConnListOrder.getInt("reqi_currencyid");
	 	    				          	parityOrigin = pmConnListOrder.getDouble("reqi_currencyparity");
	 	    				          	currencyIdDestiny =  currencyId;
	 	    				          	parityDestiny = defaultParity;
 	    				        	
	 	    				          	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	 	    				          	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	 	    				          	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	 	    				          	holdback = pmCurrencyExchange.currencyExchange(holdback, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	 	    				          	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	 	    				          	payments = pmCurrencyExchange.currencyExchange(payments, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	 	    				          	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 	    				        	}
 	    				          	
 	    				          	amountTotal += amount;
 	    				          	discountTotal += discount;
 	    				            taxTotal += tax;
 	    				          	holdbackTotal += holdback;
 	    				          	totalTotal += total1;
 	    				          	paymentsTotal += payments;
 	    				          	balanceTotal += balance;
 	    				          	    				          	
 	    				          	
 	    					        %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + discount,BmFieldType.CURRENCY) %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + holdback, BmFieldType.CURRENCY) %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + payments, BmFieldType.CURRENCY) %>
 	    					        <%=HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>

 	    					</tr>
 	    							    
 	    							    <% 
 	    							//    totalProyect += pmConnListOrder.getDouble("reqi_total");
 	    							    
 	    							    
 	    														    %>
 	    		<tr>		    
 	    	    <% 
 	    	       } //pmConnListOrder
 	    	       	amountTotal1   += amountTotal ;
 		          	discountTotal1 += discountTotal ;
 		            taxTotal1      += taxTotal ;
 		          	holdbackTotal1 += holdbackTotal ;
 		          	totalTotal1    += totalTotal ;
 		          	paymentsTotal1 += paymentsTotal ;
 		          	balanceTotal1  += balanceTotal ;
 	    	       %> 
 	    	       <tr><td colspan="25 ">&nbsp;</td></tr>
 	    		    <tr class="reportCellEven reportCellCode">
 	    		    <%= HtmlUtil.formatReportCell(sFParams, "" + countOC, BmFieldType.NUMBER) %>
 	    			    <td colspan="17">&nbsp;</td>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + holdbackTotal, BmFieldType.CURRENCY) %>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
 	    			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
 	    		    </tr>  
 	    	       <%   
 	    	} //pmConnListProject %>
 	            <tr><td colspan="25 ">&nbsp;</td></tr>
 			    <tr class="reportCellEven reportCellCode"> 			    
 				    <td colspan="18">&nbsp;</td>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal1, BmFieldType.CURRENCY) %>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal1, BmFieldType.CURRENCY) %>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal1, BmFieldType.CURRENCY) %>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + holdbackTotal1, BmFieldType.CURRENCY) %>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal1, BmFieldType.CURRENCY) %>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal1, BmFieldType.CURRENCY) %>
 				    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal1, BmFieldType.CURRENCY) %>
 			    </tr>  
 	      <% 
 	      
 	    	%>
 	<% 
    	}//pmCurrencyWhile
 	}// Fin de no existe moneda
	// Existe moneda destino
    else{
    	//Consulta para obtener todos los proyectos con OC
        pmConnListProject.doFetch("SELECT distinct(proj_projectid),proj_code,proj_name  " +
    			" FROM requisitions " +
    			" LEFT JOIN orders ON(reqi_orderid = orde_orderid) " +
    			" LEFT JOIN projects ON(orde_orderid = proj_orderid) " +
    			" LEFT JOIN currencies ON (cure_currencyid = orde_currencyid) "+
    			" WHERE proj_orderid IN ( " +
    			" SELECT reqi_orderid FROM projects " +
    					" LEFT JOIN customers ON(cust_customerid = proj_customerid) " +
    					" LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
    					" LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
    					" LEFT JOIN cities ON(city_cityid = venu_cityid) " +
    					" LEFT JOIN users ON(user_userid = proj_userid) " +
    					" LEFT JOIN orders ON(orde_orderid = proj_orderid) " +
    					" LEFT JOIN requisitions ON(reqi_orderid = orde_orderid) " +
    					" LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
    					" LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
    					" LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
    				    " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
    					" WHERE wfca_programid = " + programId +
    					where +
    					" ORDER BY proj_startdate ASC )" + 
    					
    			" ORDER BY proj_projectid");
        
    	int n = 0, countPr = 0;
    	
    	 int  i = 0;
         double total = 0, totalProyect = 0;
         int  projectId2 = 0;
         int orderId = 0, orderId2 = 0, countReqi = 0;
         
         double amountTotal1 = 0, discountTotal1 = 0, taxTotal1 = 0, holdbackTotal1 = 0, 
					totalTotal1 = 0, paymentsTotal1 = 0, balanceTotal1 = 0;
         
         while(pmConnListProject.next()) {
         	countPr++;
 			int proj_projectid = pmConnListProject.getInt("projects", "proj_projectid");	
 			
 			sql = "SELECT * FROM requisitions "+
 				  " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
 				  " LEFT JOIN  areas on (area_areaid = reqi_areaid)" +
 				  " LEFT JOIN companies on (comp_companyid = reqi_companyid) " +	
 				  " LEFT JOIN  currencies on (reqi_currencyid = cure_currencyid) " +
 				  " LEFT JOIN suppliers on (supl_supplierid = reqi_supplierid) "+
 				  " LEFT JOIN  bankmovements  on (bkmv_requisitionid = reqi_requisitionid) "+
 				  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) "+
 				  " LEFT JOIN customers ON(cust_customerid = orde_customerid) " +
 				  " LEFT JOIN projects ON(orde_orderid= proj_orderid) " +
 				  " LEFT JOIN referrals ON(refe_referralid = cust_referralid) " +
 			      " LEFT JOIN venues ON(venu_venueid = proj_venueid) " +
 			      " LEFT JOIN cities ON(city_cityid = venu_cityid) " +
 			      " LEFT JOIN users ON(user_userid = orde_userid) " +				
 			      " LEFT JOIN wflows ON(wflw_wflowid = proj_wflowid) " +
 			      " LEFT JOIN wflowtypes ON(wfty_wflowtypeid = proj_wflowtypeid) " +
 			      " LEFT JOIN wflowphases ON(wfph_wflowphaseid = wflw_wflowphaseid) " +
 			      " LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
 				  "	WHERE reqi_orderid IN (SELECT orde_orderid "+
 					                        " FROM orders " +
 					                        " WHERE orde_originreneworderid "+
 															" IN( SELECT orde_orderid FROM orders "+
 																	" LEFT JOIN projects ON (orde_orderid = proj_orderid) "+
 																	" WHERE proj_projectid = " + proj_projectid +")) "+
 					" OR  reqi_orderid IN (SELECT orde_orderid "+
 					                        " FROM orders WHERE orde_orderid " +
 															"IN( SELECT orde_orderid FROM orders "+
 																	" LEFT JOIN projects ON (orde_orderid = proj_orderid)" +
 																	"WHERE proj_projectid = " + proj_projectid +"))" +
 					
 					" AND wfca_programid = " + programId +
 					  where ;
//  			System.out.println("pmConnListOrder: "+sql);
 			pmConnListOrder.doFetch(sql);
 			double amountTotal = 0, discountTotal = 0, taxTotal = 0, holdbackTotal = 0, 
 					totalTotal = 0, paymentsTotal = 0, balanceTotal = 0;
 			int countOC = 0;
 			
 	          while(pmConnListOrder.next()) {
 	        	  countOC ++;
 	        	//Estatus
 					bmoRequisition.getStatus().setValue(pmConnListOrder.getString("requisitions", "reqi_status"));
 					bmoRequisition.getDeliveryStatus().setValue(pmConnListOrder.getString("requisitions", "reqi_deliverystatus"));
 					bmoRequisition.getPaymentStatus().setValue(pmConnListOrder.getString("requisitions", "reqi_paymentstatus"));
 					bmoRequisition.getBmoRequisitionType().getType().setValue(pmConnListOrder.getString("requisitiontypes", "rqtp_type"));
 					   	  
 	        	    String reqiStatus = "", deliveryStatus = "", paymentStatus = "", activeStatus = "", projStatuts = "";

 				//si el projecto de iteracion es diferente al que ya esta cargado pinta de nuevo la cabecera 
 	          		if((proj_projectid != projectId2)){
 	          			projectId2 = proj_projectid;
 	          			
 	    %>
 				    	<tr>
 							<td colspan="20" class="reportGroupCell">
 							    <%= pmConnListProject.getString("proj_code") %> - <%= pmConnListProject.getString("proj_name") %>
 							</td>
 						</tr>
 						
 						<tr>
 							<td class="reportHeaderCellCenter">#</td>
 							<td class="reportHeaderCell">Clave</td>
 							<td class="reportHeaderCell">Nombre</td>
 							<td class="reportHeaderCell">Tipo O.C</td>
 							<td class="reportHeaderCell">Proveedor</td>
 							<td class="reportHeaderCell">Solicita</td>
 							<td class="reportHeaderCell">Departamento</td>
 <%-- 						<% 	if (enableBudgets) { %> --%>
 <!-- 								<td class="reportHeaderCell">Presup.</td> -->
 <!-- 								<td class="reportHeaderCell">Item Presup.</td>	 -->
 <%-- 						<% 	} %> --%>
 							<td class="reportHeaderCell">Empresa</td>
 							<td class="reportHeaderCell">Pedido</td>
 							<td class="reportHeaderCell">F. Solicitud</td>
 							<td class="reportHeaderCell">F. Entrega</td>
 							<td class="reportHeaderCell">F. Pago</td>
 							<td class="reportHeaderCell">Estatus</td>
 							<td class="reportHeaderCell">Estatus MB</td>
 							<td class="reportHeaderCell">Entrega</td>  
 							<td class="reportHeaderCell">Pago</td>
 							<td class="reportHeaderCell">Moneda</td>
 							<td class="reportHeaderCellCenter">Tipo de Cambio</td>
 							<td class="reportHeaderCellRight">Monto</td>
 							<td class="reportHeaderCellRight">Descuento</td>
 							<td class="reportHeaderCellRight">IVA</td>
 							<td class="reportHeaderCellRight">Retenciones</td>
 							<td class="reportHeaderCellRight">Total</td>
 							<td class="reportHeaderCellRight">Pagos</td>
 							<td class="reportHeaderCellRight">Saldo</td>
 						</tr>    						
 			    <% } %>
 			    <% orderId = pmConnListOrder.getInt("orde_orderid");
 			    	totalProyect = 0;
 			    	
 			    	i++; n++;
           			%>
 			          <tr class="reportCellEven">
 			          		<%= HtmlUtil.formatReportCell(sFParams, "" + countOC, BmFieldType.NUMBER) %>
 							<%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_code"),BmFieldType.CODE) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_name"),BmFieldType.STRING)) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitiontypes", "rqtp_name"),BmFieldType.STRING)) %>
 					        <%//= HtmlUtil.formatReportCell(sFParams, bmoRequisition.getBmoRequisitionType().getType().getSelectedOption().getLabel(),BmFieldType.STRING) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("suppliers", "supl_code") + " " + pmConnListOrder.getString("suppliers", "supl_name"),BmFieldType.STRING)) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("users", "user_code"),BmFieldType.STRING)) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("areas", "area_name"),BmFieldType.STRING)) %>
 <%-- 					        <%	if (enableBudgets) { %> --%>
 <%-- 							        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("budg_name"),BmFieldType.CODE)) %> --%>
 <%-- 							        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmRequisition.getString("bgty_name"),BmFieldType.CODE)) %> --%>
 <%-- 					        <% 	} %> --%>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("companies", "comp_name"),BmFieldType.STRING)) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("orders", "orde_code") + " " + pmConnListOrder.getString("orders", "orde_name"), BmFieldType.STRING)) %>
 					        <%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_requestdate"),BmFieldType.DATETIME) %>
 					        <%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_deliverydate"),BmFieldType.DATETIME) %>
 					        <%= HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("requisitions", "reqi_paymentdate"),BmFieldType.DATETIME) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
 					       <td class="reportCellText">
 							<%			       
 					       		//Movimientos de banco pago proveedor
 					       String sqlbankMovConcep = "SELECT bkmv_code, bkmv_status FROM bankmovconcepts"+ 
 					    		 "  left join bankmovements on bkmc_bankmovementid = bkmv_bankmovementid " +
 					    		 "  left join paccounts on bkmc_paccountid = pacc_paccountid" +
 					    		 "  left join bankmovtypes on bkmt_bankmovtypeid = bkmv_bankmovtypeid" +
 					    		 "  where pacc_requisitionid = "+ pmConnListOrder.getString("requisitions", "reqi_requisitionid") +
 					    		 "  and bkmt_type = '"+ BmoBankMovType.TYPE_WITHDRAW+"'" +
 					    		 "  and bkmt_category = '"+ BmoBankMovType.CATEGORY_CXP + "'" ;
 					       
 					       pmBankMovConcep.doFetch(sqlbankMovConcep);
 					       String mb = "";
 					       while (pmBankMovConcep.next()){
 					    	   bmoBankMovement.getStatus().setValue(pmBankMovConcep.getString("bkmv_status"));
 								
 					    	   mb = pmBankMovConcep.getString("bkmv_code");
 					    	   mb = mb +": "+ bmoBankMovement.getStatus().getSelectedOption().getLabel();
 					    	   %>
 								<%= HtmlUtil.stringToHtml(mb)%> |
 							<%		
 					       }
 					      //Movimientos de banco Anticipo
 					       String sqlbankMov = "SELECT  bkmv_code, bkmv_status FROM bankmovements WHERE bkmv_requisitionid ="+ pmConnListOrder.getString("requisitions", "reqi_requisitionid");
 					       pmBankMov.doFetch(sqlbankMov);
 					       while (pmBankMov.next()){
 					    	   bmoBankMovement.getStatus().setValue(pmBankMov.getString("bkmv_status"));
 								
 					    	   mb = pmBankMov.getString("bkmv_code");
 					    	   mb = mb+": "+  bmoBankMovement.getStatus().getSelectedOption().getLabel();
 					    	   
 					    	   %>
 					    	   <%= HtmlUtil.stringToHtml(mb)%> |
 					      <%
 					       }
 					      
 					      %>
 					      	</td>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRequisition.getPaymentStatus().getSelectedOption().getLabel(),BmFieldType.STRING)) %>
 					        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnListOrder.getString("cure_code"),BmFieldType.CODE)) %>				
 					        <%= HtmlUtil.formatReportCell(sFParams, "" + pmConnListOrder.getDouble("reqi_currencyparity"),BmFieldType.NUMBER) %>
 					        <%
 					        
 				          	double amount = pmConnListOrder.getDouble("reqi_amount");
 				          	double discount = pmConnListOrder.getDouble("reqi_discount");
 				          	double tax = pmConnListOrder.getDouble("reqi_tax");
 				          	double holdback = pmConnListOrder.getDouble("reqi_holdback");
 				          	double total1 = pmConnListOrder.getDouble("reqi_total");
 				          	double payments = pmConnListOrder.getDouble("reqi_payments");					           		
 				          	double balance = pmConnListOrder.getDouble("reqi_balance");
 				          	
 				        	//Conversion a la moneda destino(seleccion del filtro)
 				          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
 				          	double parityOrigin = 0, parityDestiny = 0;
 				          	currencyIdOrigin = pmConnListOrder.getInt("reqi_currencyid");
 				          	parityOrigin = pmConnListOrder.getDouble("reqi_currencyparity");
 				          	currencyIdDestiny = currencyId;
 				          	parityDestiny = defaultParity;
 				
 				          	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				          	discount = pmCurrencyExchange.currencyExchange(discount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				          	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				          	holdback = pmCurrencyExchange.currencyExchange(holdback, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				          	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				          	payments = pmCurrencyExchange.currencyExchange(payments, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				          	balance = pmCurrencyExchange.currencyExchange(balance, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
 				
 				          	
 				          	amountTotal += amount;
 				          	discountTotal += discount;
 				            taxTotal += tax;
 				          	holdbackTotal += holdback;
 				          	totalTotal += total1;
 				          	paymentsTotal += payments;
 				          	balanceTotal += balance;
 				          	    				          	
 				          	
 					        %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + discount,BmFieldType.CURRENCY) %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + holdback, BmFieldType.CURRENCY) %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + payments, BmFieldType.CURRENCY) %>
 					        <%=HtmlUtil.formatReportCell(sFParams, "" + balance, BmFieldType.CURRENCY) %>

 					</tr>
 							    
 							    <% 
 							//    totalProyect += pmConnListOrder.getDouble("reqi_total");
 							    
 							    
 														    %>
 		<tr>		    
 	    <% 
 	       //i++;
 	       } //pmConnListOrder
 	       	amountTotal1   += amountTotal ;
	          	discountTotal1 += discountTotal ;
	            taxTotal1      += taxTotal ;
	          	holdbackTotal1 += holdbackTotal ;
	          	totalTotal1    += totalTotal ;
	          	paymentsTotal1 += paymentsTotal ;
	          	balanceTotal1  += balanceTotal ;
 	       %> 
 	       <tr><td colspan="25 ">&nbsp;</td></tr>
 		    <tr class="reportCellEven reportCellCode">
 		    <%= HtmlUtil.formatReportCell(sFParams, "" + countOC, BmFieldType.NUMBER) %>
 			    <td colspan="17">&nbsp;</td>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal, BmFieldType.CURRENCY) %>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal, BmFieldType.CURRENCY) %>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + holdbackTotal, BmFieldType.CURRENCY) %>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal, BmFieldType.CURRENCY) %>
 			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal, BmFieldType.CURRENCY) %>
 		    </tr>  
 	       <%   
 	} //pmConnListProject %>
         <tr><td colspan="25 ">&nbsp;</td></tr>
		    <tr class="reportCellEven reportCellCode">
			    <td colspan="18">&nbsp;</td>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal1, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + discountTotal1, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + taxTotal1, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + holdbackTotal1, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal1, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + paymentsTotal1, BmFieldType.CURRENCY) %>
			    <%= HtmlUtil.formatReportCell(sFParams, "" + balanceTotal1, BmFieldType.CURRENCY) %>
		    </tr>  
  			<%  } %>
    </table>
  			<% }// FIN DEL CONTADOR
  			
  					
    %>
    <% 	if (print.equals("1")) { %>
    	<script>
        	//window.print();
    	</script>
<% 	}
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
    }// Fin de if(no carga datos
    pmConnListProject.close();
	pmConnListOrder.close();
	pmConnListCountOC.close();
	pmConnCount.close();  		
    		
    %>
  </body>
</html>