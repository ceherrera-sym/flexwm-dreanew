<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ -->
 
<%@include file="/inc/login.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 
	String sql = "", where = "", whereGroup = "", chartSql = "";

	try {
	// Inicializar variables
 	String title = "Oportunidades Perdidas por Motivo";
 	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
 	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	
   	String startDate = "", startDateEnd = "", endDate = "", endDateEnd = "";
   	String status = "", referralId = "";
   	int wflowTypeId = 0;
   	int venueId = 0;
   	int userId = 0, profileId = 0;
   	int customerId = 0;
   	int loseMotiveId = 0;
   	String groupFilter = "";
   	String filters = "";
   	int programId = 0;
   	int propertyModelId = 0;
   	int developmentId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("oppo_userid") != null) userId = Integer.parseInt(request.getParameter("oppo_userid"));
   	if (request.getParameter("oppo_customerid") != null) customerId = Integer.parseInt(request.getParameter("oppo_customerid"));
   	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
   	if (request.getParameter("oppo_losemotiveid") != null) loseMotiveId = Integer.parseInt(request.getParameter("oppo_losemotiveid"));
   	if (request.getParameter("oppo_startdate") != null) startDate = request.getParameter("oppo_startdate");
   	//if (request.getParameter("startDateEnd") != null) startDateEnd = request.getParameter("startDateEnd");
   	if (request.getParameter("oppo_enddate") != null) endDate = request.getParameter("oppo_enddate");
   	//if (request.getParameter("endDateEnd") != null) endDateEnd = request.getParameter("endDateEnd");
    if (request.getParameter("oppo_status") != null) status = request.getParameter("oppo_status");
    if (request.getParameter("cust_referralid") != null) referralId = request.getParameter("cust_referralid");
    if (request.getParameter("oppo_propertymodelid") != null) propertyModelId = Integer.parseInt(request.getParameter("oppo_propertymodelid"));
   	if (request.getParameter("development") != null) developmentId = Integer.parseInt(request.getParameter("development"));
   	if (request.getParameter("profileid") != null) profileId = Integer.parseInt(request.getParameter("profileid"));

	// Construir filtros 
	// Filtro agrupacion
	if (loseMotiveId > 0) {
   		groupFilter += " AND lomt_losemotiveid = " + loseMotiveId;
   		filters += "<i>Motivo de Perder: </i>" + request.getParameter("oppo_losemotiveidLabel") + ", ";
   	}
	
	if (wflowTypeId > 0) {
		where += " AND oppo_wflowtypeid = " + wflowTypeId;
   		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
   	}
	   	
   	if (customerId > 0) {
   		where += " AND cust_customerid = " + customerId;
   		filters += "<i>Cliente: </i>" + request.getParameter("oppo_customeridLabel") + ", ";
   	}
   	
   	if (!referralId.equals("")) {
        //where += " AND cust_referralid = " + referralId;
        where += SFServerUtil.parseFiltersToSql("cust_referralid", referralId);
        filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
    }
   	   	
   	if (!status.equals("")) {
   		where += " AND oppo_status like '" + status + "'";
   	}
   	
   	if (propertyModelId > 0) {
        where += " AND oppo_propertymodelid = " + propertyModelId;
        filters += "<i>Modelo: </i>" + request.getParameter("oppo_propertymodelidLabel") + ", ";
    }
   	
   	if (developmentId > 0) {
        where += " AND deve_developmentid = " + developmentId;
        filters += "<i>Desarrollo: </i>" + request.getParameter("developmentLabel") + ", ";
    }
   	
   	if (!startDate.equals("")) {
   		where += " AND oppo_startdate >= '" + startDate + " 00:00' ";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	
//   	if (!startDateEnd.equals("")) {
//   		where += " AND oppo_startdate <= '" + startDateEnd + " 23:59' ";
//   		filters += "<i>Fecha Inicio Termino: </i>" + startDateEnd + ", ";
//   	}
   	
   	if (!endDate.equals("")) {
   		where += " AND oppo_startdate <= '" + endDate + " 23:59' ";
   		//where += " AND oppo_enddate >= '" + endDate + " 00:00' ";
   		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
   	}
   	
//   	if (!endDateEnd.equals("")) {
//   		where += " AND oppo_enddate <= '" + endDateEnd + " 23:59' ";
//   		filters += "<i>Fecha Fin Termino: </i>" + endDateEnd + ", ";
//   	}
   	
   	if (profileId > 0) {
		whereGroup += " AND prof_profileid = " + profileId;
	}
   	
	if (userId > 0) {
		//where += " AND u1.user_userid = " + userId;
		if (sFParams.restrictData(bmoOpportunity.getProgramCode())) {
			where += " AND u1.user_userid = " + userId;
		} else {
			where += " AND ( " +
						" u1.user_userid = " + userId +
						" OR oppo_wflowid IN ( " +
							 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ") +
							 " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" on (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + userId + 
							 " AND wflw_callercode = '" + bmoOpportunity.getProgramCode().toString() + "' " + 
						 " ) " + 
					 " )";
		}
   		filters += "<i>Vendedor: </i>" + request.getParameter("oppo_useridLabel") + ", ";
   	}
	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
	/*
   	if (sFParams.restrictData(bmoOpportunity.getProgramCode()) || (userId > 0)) {
   		if (!(userId > 0)) {
   			userId = sFParams.getLoginInfo().getUserId();
   		}
   		
   		filters += "<i>Vendedor: </i>" + request.getParameter("oppo_useridLabel") + ", ";

		// Filtro por asignacion de Oportunidads
		where += " AND ( oppo_userid in (" +
				" SELECT user_userid FROM " + SQLUtil.formatKind(sFParams, " users " +
				" WHERE " + 
				" user_userid = " + userId +
				" or user_userid in ( " +
				" select u2.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u2 on (u2.user_parentid = u1.user_userid) " +
				" where u1.user_userid = " + userId +
				" ) " +
				" or user_userid in ( " +
				" select u3.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u2 on (u2.user_parentid = u1.user_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u3 on (u3.user_parentid = u2.user_userid) " +
				" where u1.user_userid = " + userId +
				" ) " +
				" or user_userid in ( " +
				" select u4.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u2 on (u2.user_parentid = u1.user_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u3 on (u3.user_parentid = u2.user_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u4 on (u4.user_parentid = u3.user_userid) " +
				" where u1.user_userid = " + userId +
				" ) " +
				" or user_userid in ( " +
				" select u5.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u2 on (u2.user_parentid = u1.user_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u3 on (u3.user_parentid = u2.user_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u4 on (u4.user_parentid = u3.user_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users u5 on (u5.user_parentid = u4.user_userid) " +
				" where u1.user_userid = " + userId +
				" ) " + 
				" ) " +
				" OR " +
				" ( " +
				" oppo_opportunityid IN ( " +
				" SELECT wflw_callerid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows on (wflu_wflowid = wflw_wflowid) " +
				" WHERE wflu_userid = " + userId +
				" AND wflw_callercode = 'OPPO' " + 
				"   ) " +
				" ) " +
				" ) ";
	}
	*/
	
	// Obtener disclosure de datos
    String disclosureFilters = new PmOpportunity(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
   	
   	chartSql = " SELECT lomt_name, oppo_startdate, oppo_code, count(oppo_opportunityid) as c, sum(oppo_amount) as s, sum(quot_total) as q " + 
   					" FROM " + SQLUtil.formatKind(sFParams, " opportunities ") +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," losemotives")+" on (lomt_losemotiveid = oppo_losemotiveid) " +	     
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," customers")+" on (cust_customerid = oppo_customerid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," referrals")+" on (refe_referralid = cust_referralid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quotes")+" ON (quot_quoteid = oppo_quoteid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quoteproperties")+" ON (qupy_quoteid = quot_quoteid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON (prty_propertyid = qupy_propertyid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," propertymodels")+" ON (ptym_propertymodelid = oppo_propertymodelid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (deve_developmentid = ptym_developmentid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u1 ON(u1.user_userid = oppo_userid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" on (wflw_wflowid = oppo_wflowid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" on (wfty_wflowtypeid = oppo_wflowtypeid) " +
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) " +	
		   			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowcategories")+" on (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
		   			" WHERE wfca_programid = " + programId + 
		   			where + groupFilter +
		   			" GROUP BY lomt_name " + 
		   			" ORDER BY oppo_startdate ASC, oppo_code ASC";

   	//System.out.println("chartSql "+chartSql);
	
	//Conexion a Base de Datos
	PmConn pmConnChart = new PmConn(sFParams);
	pmConnChart.open();
	pmConnChart.doFetch(chartSql);
	
	PmConn pmPhaseGestor = new PmConn(sFParams);
    pmPhaseGestor.open();
    
    
	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
	
	PmConn pmBitacora = new PmConn(sFParams);
	pmBitacora.open();
	
	PmConn pmConnList = new PmConn(sFParams);
	pmConnList.open();
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	sql = " SELECT COUNT(*) as contador  " + 
			" FROM " + SQLUtil.formatKind(sFParams, " opportunities ") +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," losemotives")+" on (lomt_losemotiveid = oppo_losemotiveid) " +	      
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," customers")+" on (cust_customerid = oppo_customerid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," referrals")+" on (refe_referralid = cust_referralid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quotes")+" ON (quot_quoteid = oppo_quoteid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quoteproperties")+" ON (qupy_quoteid = quot_quoteid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON (prty_propertyid = qupy_propertyid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," propertymodels")+" ON (ptym_propertymodelid = oppo_propertymodelid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (deve_developmentid = ptym_developmentid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u1 ON(u1.user_userid = oppo_userid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" on (wflw_wflowid = oppo_wflowid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" on (wfty_wflowtypeid = oppo_wflowtypeid) " +
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) " +	
			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowcategories")+" on (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
			" WHERE lomt_losemotiveid > 0 " + 
			" AND wfca_programid = " + programId + 
			where + groupFilter +
			"  " + 
			" ORDER BY lomt_name ASC";
			
			int count =0;
			//ejecuto el sql
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador de reportes --> "+count);
			//if que muestra el mensajede error
			if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
				%>
				
<%=messageTooLargeList %>
				<% 
			}else{
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
	<title>:::<%= title %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
	
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Fase', 'Oportunidades'],
		<% 
       		while(pmConnChart.next()) {
       			String type = pmConnChart.getString("lomt_name");
       			if (!(type.length() > 0)) type = "Indefinido";
       %>
       		<%= "['" + type + "', " + pmConnChart.getInt("c") + "]," %>          
       <%
       		}
       %>
        ]);

        var options = {
          title: 'Oportunidades Perdidas por Motivo',
          vAxis: {minValue: 0},
          animation: {duration: 10000, easing: "inAndOut"},
          legend: { position: "none"},
          colors:['steelblue']
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>

	<% pmConnChart.beforeFirst(); %>
	
	 <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Fase', 'Valor'],
		<% 
       		while(pmConnChart.next()) {
       			String type = pmConnChart.getString("lomt_name");
       			if (!(type.length() > 0)) type = "Indefinido";
       %>
       		<%//= "['" + type + "', " + pmConnChart.getInt("s") + "]," %>       
       		<%= "['" + type + "', " + pmConnChart.getInt("q") + "]," %>          
       <%
       		}
       %>
        ]);

        var options = {
          title: 'Valor Total de Oportunidades Perdidas por Motivo',
          vAxis: {minValue: 0},
          animation: {duration: 10000, easing: "inAndOut"},
          legend: { position: "none"},
          colors:['lightsalmon']
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div2'));
        chart.draw(data, options);
      }
    </script>
    
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

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
			<b>Agrupado Por:</b> Motivo de Perder, <b>Ordenado por:</b> Fecha
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>

<table width="100%">
	<tr>
		<td width="50%"> <div id="chart_div" style="width: 100%; height: 400px;"></div> </td>
		<td width="50%"> <div id="chart_div2" style="width: 100%; height: 400px;"></div> </td>
	</tr>
</table>

<table class="report">
    
    <% 
	double sumTotal = 0, sumQuotTotal = 0, sumTotalQuotTotal = 0;

	// Para este filtro de tipos de flujo considerar el id del modulo oportunidad

	//sql =" SELECT * FROM " + SQLUtil.formatKind(sFParams, " losemotives WHERE lomt_losemotiveid > 0 " +  groupFilter + "";
			
	sql = " SELECT lomt_name, oppo_startdate, oppo_losemotiveid, lomt_losemotiveid " + 
				" FROM " + SQLUtil.formatKind(sFParams, " opportunities ") +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," losemotives")+" on (lomt_losemotiveid = oppo_losemotiveid) " +	      
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," customers")+" on (cust_customerid = oppo_customerid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," referrals")+" on (refe_referralid = cust_referralid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quotes")+" ON (quot_quoteid = oppo_quoteid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quoteproperties")+" ON (qupy_quoteid = quot_quoteid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON (prty_propertyid = qupy_propertyid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," propertymodels")+" ON (ptym_propertymodelid = oppo_propertymodelid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (deve_developmentid = ptym_developmentid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u1 ON(u1.user_userid = oppo_userid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" on (wflw_wflowid = oppo_wflowid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" on (wfty_wflowtypeid = oppo_wflowtypeid) " +
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) " +	
				" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowcategories")+" on (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
				" WHERE lomt_losemotiveid > 0 " + 
				" AND wfca_programid = " + programId + 
				where + groupFilter +
				" GROUP BY oppo_losemotiveid " + 
				" ORDER BY lomt_name ASC";
	
	
		pmConnGroup.doFetch(sql);
		System.out.println("pmConnGroup "+sql);
		
		int  i = 0, y = 0;
        while(pmConnGroup.next()) {
			int groupMotiveId = pmConnGroup.getInt("oppo_losemotiveid");	

			
	    	sql = " SELECT *, " +
		    			" u1.user_code AS promo, u2.user_code AS coord, u3.user_code AS gerente, u4.user_code AS gerentecom, u5.user_code AS direc, " +
		    			" credito.wfty_name as wftyName " +
		    			" FROM " + SQLUtil.formatKind(sFParams, " opportunities ") +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," losemotives")+" on (lomt_losemotiveid = oppo_losemotiveid) " +	     
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," customers")+" on (cust_customerid = oppo_customerid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," referrals")+" on (cust_referralid = refe_referralid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quotes")+" ON (quot_quoteid = oppo_quoteid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," quoteproperties")+" ON (qupy_quoteid = quot_quoteid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON (prty_propertyid = qupy_propertyid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," propertymodels")+" ON (ptym_propertymodelid = oppo_propertymodelid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON (deve_developmentid = ptym_developmentid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u1 ON(u1.user_userid = oppo_userid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" on (wflw_wflowid = oppo_wflowid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" wflowtypes ON (wflowtypes.wfty_wflowtypeid = oppo_wflowtypeid) " +  
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" on (wfph_wflowphaseid = wflw_wflowphaseid) " +	    
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowcategories")+" on (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
		    			" LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowtypes")+" credito ON (credito.wfty_wflowtypeid = oppo_foreignwflowtypeid) " + 
		    			" WHERE lomt_losemotiveid = " + groupMotiveId +
		    			where + 
		    			" ORDER BY oppo_startdate ASC";         
			pmConnList.doFetch(sql);
			System.out.println("sql "+sql);
	          double total = 0, subTotalSum = 0, descuentoSum = 0, ivaSum = 0;
	          int losemotiveid = 0;
	          while(pmConnList.next()) {
	        	  	i++; y++;
	          		//total += pmConnList.getDouble("oppo_amount"); 
	          		//sumTotal += pmConnList.getDouble("oppo_amount");
	          		//subTotalSum += pmConnList.getDouble("quot_amount");
	          		//descuentoSum += pmConnList.getDouble("quot_discount");
	          		//ivaSum += pmConnList.getDouble("quot_tax");
	          		
	          		bmoOpportunity.getStatus().setValue(pmConnList.getString("opportunities", "oppo_status"));

	          		if((pmConnGroup.getInt("losemotives", "lomt_losemotiveid") != losemotiveid)){
	          			losemotiveid = pmConnGroup.getInt("losemotives", "lomt_losemotiveid");
	          			total = 0;
	          			sumQuotTotal = 0;
	          			i = 1;
	    %>
	    
		    	<tr>
					<td colspan="25" class="reportGroupCell">
						<%= HtmlUtil.stringToHtml(HtmlUtil.stringToHtml(pmConnGroup.getString("lomt_name"))) %>
					</td>
				</tr>
				<tr class="">
				    <td class="reportHeaderCell">#</td>
				    <td class="reportHeaderCell">Clave</td>
				    <td class="reportHeaderCell">Nombre</td>
				    <td class="reportHeaderCell">Cliente</td>
			        <td class="reportHeaderCell">Fecha Reg. CL.</td>
			        <td class="reportHeaderCell">Motivo</td>
			        <td class="reportHeaderCell">Comentario</td>
			        <td class="reportHeaderCell">Estatus</td>
			        <td class="reportHeaderCell">Modelo Inm.</td>
			        <td class="reportHeaderCell">Desarrollo</td>
			        <td class="reportHeaderCell">Cr&eacute;dito</td>
			        <td class="reportHeaderCell">Tipo</td>
			        <td class="reportHeaderCell">Fase</td>
			        <td class="reportHeaderCell">Gestor</td>
			        <td class="reportHeaderCell">Doc. Comp.</td>
			        <td class="reportHeaderCell">Comentarios Tareas</td>
			        <td class="reportHeaderCell">Avance</td>
			        <td class="reportHeaderCell">Promotor</td>
			        <td class="reportHeaderCell">Coordinador</td>
			        <td class="reportHeaderCell">Gerente</td>
			        <td class="reportHeaderCell">Referencia</td>
			        <td class="reportHeaderCell">Fecha Inicio</td>
			        <td class="reportHeaderCell">Fecha Fin</td>
			        <td class="reportHeaderCellRight">Valor Estimado</td>
			        <td class="reportHeaderCellRight">Valor Total</td>
				</tr>
				<% } %>
	          <tr class="reportCellEven">
		          <%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
		      	
		          <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_code"), BmFieldType.CODE) %>
	
		          <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_name"), BmFieldType.STRING)) %>
	
		          <% 
		          String customer = "";
		          if (pmConnList.getString("customers","cust_customertype").equals("C")) {
		        	  customer = pmConnList.getString("customers","cust_code") + " " +
		        			  pmConnList.getString("customers","cust_legalname");
		          } else {
		        	  customer = pmConnList.getString("customers","cust_code") + " " +
		        			  pmConnList.getString("customers","cust_displayname");
		          }
		          %>
		          <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
	
	        	  <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("customers", "cust_datecreate"), BmFieldType.DATETIME) %>
	        	  
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("losemotives", "lomt_name"), BmFieldType.STRING)) %>
	        	  
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_losecomments"), BmFieldType.STRING)) %>

	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoOpportunity.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %> 
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("propertymodels","ptym_name"), BmFieldType.STRING)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("developments", "deve_name"), BmFieldType.STRING)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("credito", "wftyName"), BmFieldType.STRING)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflowphases", "wfph_name"), BmFieldType.STRING)) %>
	        	  
	        	  <%
	        	  //Obtener Gestor buscando por el Grupo
	        	  String userGestor = "";

	        	  if(profileId > 0){
	        		  sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
	        				  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," profiles")+" ON (wflu_profileid = prof_profileid) " +	
	        				  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," users")+" ON (wflu_userid = user_userid) " +						  
	        				  " WHERE wflu_wflowid = " + pmConnList.getInt("wflw_wflowid") +
	        				  whereGroup;
	        		  pmPhaseGestor.doFetch(sql);
	        		  while(pmPhaseGestor.next()){
	        			  userGestor += pmPhaseGestor.getString("users", "user_code") + "<br>";
	        		  }
	        	  }
	        	  %>
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, userGestor, BmFieldType.CODE)) %>

	        	  <%= HtmlUtil.formatReportCell(sFParams, ((pmConnList.getBoolean("wflw_hasdocuments") ? "Si": "No")), BmFieldType.CODE) %>

	        	  <% 
	        	  String bitacora = "";
	        	  if(profileId > 0){
	        		  String bitacoraGrupo = "SELECT wfsp_commentlog FROM " + SQLUtil.formatKind(sFParams, " wflowsteps ") +
	        				  " WHERE wfsp_wflowid = " + pmConnList.getInt("opportunities","oppo_wflowid") +
	        				  " AND wfsp_profileid = " + profileId +
	        				  " ORDER BY wfsp_wflowstepid ASC ";
	        		  System.out.println("BITACORA: "+ bitacoraGrupo);
	        		  pmBitacora.doFetch(bitacoraGrupo);
	        		  while(pmBitacora.next()){
	        			  bitacora += pmBitacora.getString("wfsp_commentlog");
	        		  }

	        	  }
	        	  %>
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bitacora, BmFieldType.STRING)) %>
	        	  
	        	  <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("wflows", "wflw_progress"), BmFieldType.PERCENTAGE) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("u1", "promo"), BmFieldType.CODE)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("u2", "coord"), BmFieldType.CODE)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("u3", "gerente"), BmFieldType.CODE)) %>
	
	        	  <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnList.getString("referrals", "refe_name"), BmFieldType.STRING)) %>
	
	        	  <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_startdate"), BmFieldType.DATE) %>
	
	        	  <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_enddate"), BmFieldType.DATE) %>
	
	        	  <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("opportunities", "oppo_amount"), BmFieldType.CURRENCY) %>
	
	        	  <%= HtmlUtil.formatReportCell(sFParams, pmConnList.getString("quotes", "quot_total"), BmFieldType.CURRENCY) %>
	          </tr>
	    <%
		    total += pmConnList.getDouble("oppo_amount");
	  		sumTotal +=  pmConnList.getDouble("oppo_amount");
	  		
	  		sumQuotTotal += pmConnList.getDouble("quot_total");
	  		sumTotalQuotTotal += pmConnList.getDouble("quot_total");
	       }

	          
	       
	       
	       if(total > 0 || sumQuotTotal > 0){
	     %>
	     <tr class="reportCellEven reportCellCode">
		     <td colspan="23"> 
		     	&nbsp;
		     </td >
		     <%= HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
		     <%= HtmlUtil.formatReportCell(sFParams, "" + sumQuotTotal, BmFieldType.CURRENCY) %>
	     </tr>
	     <%
	       }
	}
	%>
	<tr><td colspan="25">&nbsp;</td></tr>
	<tr class="reportCellEven reportCellCode">
		<%= HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
		<td colspan="22" > 
			&nbsp;
		</td >
		<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotal, BmFieldType.CURRENCY) %>
		<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalQuotTotal, BmFieldType.CURRENCY) %>
	</tr>

</table>    
     
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
	}//fin de count%>
	
<% 	
	pmConnList.close();
	pmBitacora.close();
	pmConnGroup.close();
	pmConnChart.close();
	pmPhaseGestor.close();
	pmConnCount.close();
	} catch (Exception e) {
	String errorLabel = "Error de Reporte";
	String errorText = "El reporte no pudo ser generado satisfactoriamente. ";
	String errorException = "Chart SQL:\n " + chartSql + 
							"\n\nList SQL:\n " + sql + 
							"\n\nException:\n" + e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}
	
%>
  </body>
</html>