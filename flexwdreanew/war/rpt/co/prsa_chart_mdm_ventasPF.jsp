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
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
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
//Inicializar variables

	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	BmoOrder bmoOrder = new BmoOrder();
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	PmConn pmConn = new PmConn(sFParams);
   	String sql = "", where = "";
   	int userId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("user_userid") != null) userId = Integer.parseInt(request.getParameter("user_userid"));
   	
//   	if (sFParams.getSelectedCompanyId() > 0)
//    	where += " AND oppo_companyid = " + sFParams.getSelectedCompanyId();
   	
   	// Asigna usuario seleccionado
   	if (userId > 0) {
   		
	

			where += "  ( prsa_salesuserid in (" +
					" select user_userid from users " +
					" where " + 
					" user_userid = " + userId +
					" or user_userid in ( " +
					" select u2.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + userId +
					" ) " + 
					" ) " +
					" OR " +
					" ( " +
					" prsa_wflowid IN ( " +
					" SELECT wflw_wflowid FROM wflowusers  " +
					" LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + userId +
					" AND (wflw_callercode = '" + bmoPropertySale.getProgramCode().toString() +  
					"' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "') " + 
					"   ) " +
					" ) " +
					" ) ";
		//}
   	}	
   	
	// Filtro de oportunidades de empresas del usuario
	if (sFParams.restrictData(new BmoCompany().getProgramCode())) {
		where += " AND ( prsa_companyid in (" +
				" select uscp_companyid from " + SQLUtil.formatKind(sFParams, "usercompanies ") +
				" where " + 
				" uscp_userid = " + ((userId > 0) ? "" + userId : "" + sFParams.getLoginInfo().getUserId()) + " )"
				+ ") ";			
	}


	// Filtro de empresa seleccionada
	if (sFParams.getSelectedCompanyId() > 0) {
		where += " AND prsa_companyid = " + sFParams.getSelectedCompanyId();
	}
   	
	
	int currentYear = SFServerUtil.currentYear();
	int currentLastYear = currentYear-1;
	// Filtro fecha inicio

		where += " AND prsa_startdate >= '" + currentYear + "-00-00'";
	


		where += " AND prsa_startdate <= '" + currentYear + "-12-31'";


	
	 sql =" SELECT " +
			" CASE WHEN MONTH(prsa_startdate) = 1 THEN 'enero'" +
			" WHEN MONTH(prsa_startdate) = 2 THEN 'febrero'" +
			" WHEN MONTH(prsa_startdate) = 3 THEN 'marzo'" +
			" WHEN MONTH(prsa_startdate) = 4 THEN 'abril'" +
			" WHEN MONTH(prsa_startdate) = 5 THEN 'mayo'" +
			" WHEN MONTH(prsa_startdate) = 6 THEN 'junio'" +
			" WHEN MONTH(prsa_startdate) = 7 THEN 'julio'" +
			" WHEN MONTH(prsa_startdate) = 8 THEN 'agosto'" +
			" WHEN MONTH(prsa_startdate) = 9 THEN 'septiembre'" +
			" WHEN MONTH(prsa_startdate) = 10 THEN 'octubre'" +
			" WHEN MONTH(prsa_startdate) = 11 THEN 'noviembre'" +
			" WHEN MONTH(prsa_startdate) = 12 THEN 'diciembre'" +
			" ELSE 'Mes indefinido' END AS Mes, " +
			" COUNT(prsa_propertysaleid) AS Pedidos " +
			" FROM propertysales WHERE ";
	
	sql += where;
	sql += " GROUP BY Mes ";
	sql += " ORDER BY Mes DESC ";
	
	System.out.println("----------        1"+sql);
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
          ['Mes', 'Pedidos'],
	      	<% 
	  		int total = 0;
				while (pmConn.next()) {
					total += pmConn.getInt("Pedidos");
			%>
	    		['<%= HtmlUtil.stringToHtml(pmConn.getString("Mes")) %>', <%= pmConn.getInt("Pedidos") %>],
			<%
					}
			%>
        ]);

        var options = {
                chart: {
                  title: 'Ventas Activas Por Mes',
                  subtitle: ' Total: <%= total %>',
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