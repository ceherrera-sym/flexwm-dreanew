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
   		
   		where += " AND oppo_userid = " + userId + " ";
		//}
   	}	
   	
	int currentYear = SFServerUtil.currentYear();
	int currentLastYear = currentYear-1;
	
	
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
			sql += " AND oppo_status = '" + BmoOpportunity.STATUS_REVISION + "'  "+
				where + 
				" GROUP BY wfph_name";

			 System.out.println("***---          "+sql);
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
	    		['<%= HtmlUtil.removeAccents(pmConn.getString("Fase")) %>', <%= pmConn.getInt("Oportunidades") %>],
			<%
					}
			%>
        ]);

        var options = {
                chart: {
                  title: 'Oportunidades Activas',
                  subtitle: 'Por Fase de Flujo, total: <%= total %>',
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