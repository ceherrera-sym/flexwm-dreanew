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

<%@include file="/inc/login.jsp"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.symgae.shared.sf.*"%>
<%@page import="com.symgae.server.sf.*"%>
<%@page import="com.symgae.server.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil"%>

<%
	// Inicializar variables
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
   	String sql = "", where = "";
   	int userId = 0;
   	BmoOpportunity bmoOpportunity = new BmoOpportunity();
   	
   	//Conexion a Base de Datos
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
   	//2nda Conexion a Base de Datos
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
	
   	//3era Conexion a Base de Datos
	PmConn pmConn3 = new PmConn(sFParams);
	pmConn3.open();
	
   	// Obtener parametros
   	if (request.getParameter("user_userid") != null) userId = Integer.parseInt(request.getParameter("user_userid"));
   	
   	
 	// Asigna usuario seleccionado
   	if (userId > 0) {
		where += " AND ( oppo_userid in (" +
					" SELECT user_userid FROM " + SQLUtil.formatKind(sFParams, "users ") +
					" WHERE " + 
					" user_userid = " + userId +
					" or user_userid in ( " +
					" 	select u2.user_userid from " + SQLUtil.formatKind(sFParams, "users")+" u1 " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from " + SQLUtil.formatKind(sFParams, "users")+" u1 " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from " + SQLUtil.formatKind(sFParams, "users")+" u1 " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 on (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from " + SQLUtil.formatKind(sFParams, "users")+" u1 " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 on (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 on (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " + 
					" ) " +
					" OR " +
					" ( " +
					" oppo_opportunityid IN ( " +
					" SELECT wflw_callerid FROM " + SQLUtil.formatKind(sFParams, "wflowusers  ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + userId +
					" AND wflw_callercode = '" + bmoOpportunity.getProgramCode().toString() + " ' " + 
					"   ) " +
					" ) " +
					" ) ";
   	}	
   	
   	
   	int countOpportunities = 0;
	int currentYear = SFServerUtil.currentYear();
   	// Organizar funnels
   	int countFunnels = 0;
   	sql = "SELECT COUNT(*) as c FROM wflowfunnels";
   	pmConn.doFetch(sql);
   	if (pmConn.next()) {
   		countFunnels = pmConn.getInt("c");	
   	}
   	
   	int countWFlowTypes = 0;
   	sql = "SELECT COUNT(*) as c FROM wflowtypes "
			+ " WHERE wfty_wflowtypeid IN (SELECT wflw_wflowtypeid FROM opportunities "
			+ " LEFT JOIN wflows ON oppo_wflowid = wflw_wflowid ) "
			+ " AND wfty_status = 'A' "  
			+ " ORDER BY wfty_name ";
   	pmConn.doFetch(sql);
   	if (pmConn.next()) {
   		countWFlowTypes = pmConn.getInt("c");	
   	}
   	
   	// Genera matriz
   	int[][] funnelWFlowTypes = new int[countFunnels][countWFlowTypes];
   	
   	// Obtiene lista de funnels
   	String wFlowFunnelSql = "SELECT * FROM wflowfunnels ORDER BY wflf_name";
   	pmConn.doFetch(wFlowFunnelSql);

	// Obtiene lista de tipos de flujos
	// TODO: cambiar el estatus a no hardcode
	String wFlowTypesSql = "SELECT * FROM wflowtypes "
			+ " WHERE wfty_wflowtypeid IN (SELECT wflw_wflowtypeid FROM opportunities "
										+ " LEFT JOIN wflows ON oppo_wflowid = wflw_wflowid ) "
			+ " AND wfty_status = 'A' "  
			+ " ORDER BY wfty_name ";
	pmConn2.doFetch(sql);
   	
   	int currentFunnel = 0;
   	while (pmConn.next()) {
   		int currentWFlowType = 0;
   		int currentWFlowFunnelId = pmConn.getInt("wflf_wflowfunnelid");

   		pmConn2.doFetch(wFlowTypesSql);
   		while (pmConn2.next()) {
   			int currentWFlowTypeId = pmConn2.getInt("wfty_wflowtypeid");
   			sql = "SELECT COUNT(*) AS c FROM opportunities "
   					+ " LEFT JOIN wflows ON oppo_wflowid = wflw_wflowid " 
   					+ " LEFT JOIN wflowtypes ON wflw_wflowtypeid = wfty_wflowtypeid " 
   					+ " LEFT JOIN wflowfunnels ON wflw_wflowfunnelid = wflf_wflowfunnelid "
   					+ " WHERE wflw_wflowtypeid = " + currentWFlowTypeId + " "
   					+ " AND wflw_wflowfunnelid = " + currentWFlowFunnelId + " "
   					+ where
   					;
   			pmConn3.doFetch(sql);
   			if (pmConn3.next()) {
   				funnelWFlowTypes[currentFunnel][currentWFlowType] = pmConn3.getInt("c");
   				countOpportunities += pmConn3.getInt("c");
   			} else{
   				funnelWFlowTypes[currentFunnel][currentWFlowType] = 0;
   			}
   			currentWFlowType++;
   		}
   		currentFunnel++;
   	}
%>
<head>
<title>:::<%= appTitle %>:::
</title>
<link rel="stylesheet" type="text/css"
	href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">

<script type="text/javascript"
	src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
      google.charts.load('current', {'packages':['bar']});
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          	['Funnel' 
			<% 
         		pmConn.doFetch(wFlowTypesSql);
          		while (pmConn.next()) {
        	  %><%= ", '" + pmConn.getString("wfty_name") + "'" %><% } %>],
        	<% 
        	 	currentFunnel = 0; 
        		pmConn.doFetch(wFlowFunnelSql);
          		while (pmConn.next()) {
          	%>
          			<%= "['" + pmConn.getString("wflf_name") + "'" %>
          			<%
          				for (int i = 0; i < countWFlowTypes; i++) {
          			%><%= ", " + funnelWFlowTypes[currentFunnel][i] %>
          			<%
          				}
          			%><%= "]" %><% if (currentFunnel < countFunnels) { %><%= "," %><% } %>
          	<%
          		currentFunnel++;
          		}
          	%>	
        	]);

        var options = {
          chart: {
            title: 'Oportunidades Activas',
            subtitle: 'Por Funnel y por Tipo, total <%= countOpportunities %>',
          },
          legend: { position: 'none'},
          bars: 'horizontal',
          isStacked: true
        };

        var chart = new google.charts.Bar(document.getElementById('barchart_material'));

        chart.draw(data, google.charts.Bar.convertOptions(options));
      }
    </script>
</head>
<body>
	<div id="barchart_material" style="width: 400px; height: 300px;"></div>
	<%
	pmConn.close();
	pmConn2.close();
	pmConn3.close();
%>
</body>
</html>
