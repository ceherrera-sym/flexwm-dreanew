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
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
   	String sql = "", where = "";
   	int userId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("user_userid") != null) userId = Integer.parseInt(request.getParameter("user_userid"));
   	
//   	if (sFParams.getSelectedCompanyId() > 0)
//    	where += " AND oppo_companyid = " + sFParams.getSelectedCompanyId();
   	
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
		//}
   	}	
   	
	// Filtro de oportunidades de empresas del usuario
	if (sFParams.restrictData(new BmoCompany().getProgramCode())) {
		where += " AND ( oppo_companyid in (" +
				" select uscp_companyid from " + SQLUtil.formatKind(sFParams, "usercompanies ") +
				" where " + 
				" uscp_userid = " + ((userId > 0) ? "" + userId : "" + sFParams.getLoginInfo().getUserId()) + " )"
				+ ") ";			
	}


	// Filtro de empresa seleccionada
	if (sFParams.getSelectedCompanyId() > 0) {
		where += " AND oppo_companyid = " + sFParams.getSelectedCompanyId();
	}
	int currentYear = SFServerUtil.currentYear();
sql =" SELECT wfph_name	 AS Fase, count(oppo_opportunityid) AS Oportunidades "
		+ " FROM " + SQLUtil.formatKind(sFParams, " opportunities ") + 
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"ordertypes")+" on (ortp_ordertypeid = oppo_ordertypeid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cust_customerid = oppo_customerid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"referrals")+" ON (cust_referralid = refe_referralid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (user_userid = oppo_userid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"quotes")+" ON (oppo_quoteid = quot_quoteid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"venues")+" on (venu_venueid = oppo_venueid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"currencies")+" on (cure_currencyid = quot_currencyid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"wflows")+" ON (wflw_wflowid = oppo_wflowid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"wflowfunnels")+" ON (wflf_wflowfunnelid = wflw_wflowfunnelid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"wflowtypes")+" ON (wfty_wflowtypeid = oppo_wflowtypeid) " +
			" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"wflowphases")+" ON (wfph_wflowphaseid = wflw_wflowphaseid) " ;
		sql += " WHERE oppo_opportunityid > 0  ";
		sql +=  " AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' AND oppo_startdate >'2019-0-0'";
		
	sql += where;
	sql += " GROUP BY Fase ";
	sql += " ORDER BY Fase DESC ";


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
          ['Fase', 'Oportunidades'],
	      	<% 
	  		int total = 0;
				while (pmConn.next()) {
					total += pmConn.getInt("Oportunidades");
			%>
	    		['<%= HtmlUtil.stringToHtml(pmConn.getString("Fase")) %>', <%= pmConn.getInt("Oportunidades") %>],
			<%
					}
			%>
        ]);

        var options = {
                chart: {
                  title: 'Oportunidades Activas Por Fase',
                  subtitle: 'Por Tipo, total: <%= total %>',
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