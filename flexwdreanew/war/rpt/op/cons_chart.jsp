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
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	BmoConsultancy bmoConsultancy = new BmoConsultancy();
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
   	String sql = "", where = "";
   	int userId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("user_userid") != null) userId = Integer.parseInt(request.getParameter("user_userid"));
   	   	
   	// Asigna usuario seleccionado
   	if (userId > 0) {
   		
		//if (sFParams.restrictData(bmoOpportunity.getProgramCode())) {

			where += " AND ( cons_userid in (" +
						" SELECT user_userid FROM " + SQLUtil.formatKind(sFParams, " users ") +
						" WHERE " + 
						" user_userid = " + userId +
						" or user_userid in ( " +
						" 	select u2.user_userid FROM " + SQLUtil.formatKind(sFParams, " users")+" u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
						" where u1.user_userid = " + userId +
						" ) " +
						" or user_userid in ( " +
						" select u3.user_userid FROM " + SQLUtil.formatKind(sFParams, " users")+" u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 on (u3.user_parentid = u2.user_userid) " +
						" where u1.user_userid = " + userId +
						" ) " +
						" or user_userid in ( " +
						" select u4.user_userid FROM " + SQLUtil.formatKind(sFParams, " users")+" u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 on (u3.user_parentid = u2.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 on (u4.user_parentid = u3.user_userid) " +
						" where u1.user_userid = " + userId +
						" ) " +
						" or user_userid in ( " +
						" select u5.user_userid FROM " + SQLUtil.formatKind(sFParams, " users")+" u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 on (u3.user_parentid = u2.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 on (u4.user_parentid = u3.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u5 on (u5.user_parentid = u4.user_userid) " +
						" where u1.user_userid = " + userId +
						" ) " + 
						" ) " +
						" OR " +
						" ( " +
						" cons_orderid IN ( " +
						" SELECT wflw_callerid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
						" WHERE wflu_userid = " + userId +
						" AND wflw_callercode = '" + bmoConsultancy.getProgramCode() + "' " + 
						"   ) " +
						" ) " +
						" ) ";
		//}
   	}
   	
	// Filtro de ventas de empresas del usuario
	if (sFParams.restrictData(new BmoCompany().getProgramCode())) {
		where += " AND ( cons_companyid in (" +
				" select uscp_companyid FROM " + SQLUtil.formatKind(sFParams, " usercompanies ") +
				" where " + 
				" uscp_userid = " + ((userId > 0) ? "" + userId : "" + sFParams.getLoginInfo().getUserId()) + " )" +
				" ) ";			
	}

	// Filtro de empresa seleccionada
	if (sFParams.getSelectedCompanyId() > 0) {
		where += " AND cons_companyid = " + sFParams.getSelectedCompanyId();
	}
   	
   	sql = " SELECT wfty_name AS Tipo, count(cons_consultancyid) AS Ventas "
			+ "	FROM " + SQLUtil.formatKind(sFParams, "consultancies ")
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflows") + " ON (cons_wflowid = wflw_wflowid) "
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") + " ON (wflw_wflowtypeid = wfty_wflowtypeid) "
			+ "	LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (cons_ordertypeid = ortp_ordertypeid) "
			+ " WHERE cons_consultancyid > 0 "
			+ " AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' "
			+ where  
			+ " GROUP BY wfty_name";
			
	//System.out.println("PED: "+sql);
			 
   	//Conexion a Base de Datos
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);
%>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
			
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['bar']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          	['Tipo', 'Ventas'],
          	<% 
          		int total = 0;
       			while (pmConn.next()) {
       				total += pmConn.getInt("Ventas");
       		%>
	        		['<%= pmConn.getString("Tipo") %>', <%= pmConn.getInt("Ventas") %>],
        	<%
       			}
        	%>
        	]);

        var options = {
          chart: {
            title: 'Ventas Activas',
            subtitle: 'Por Tipo de Venta, total: <%= total %>',
          },
          legend: {position: 'none'},
          bars: 'horizontal'
        };

        var chart = new google.charts.Bar(document.getElementById('chart'));
        chart.draw(data, options);
      }
    </script>
  </head>
  <body>
    <div id="chart" style="width: 400px; height: 300px;"></div>
<%
	pmConn.close();
%> 
	</body>
</html>