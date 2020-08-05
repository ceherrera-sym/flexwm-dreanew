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
   	
  
	
sql =" SELECT bkac_name as Tipo, SUM(bkmv_deposit) AS deposit, SUM(bkmv_withdraw) AS withdraw "
		+ "	FROM " + SQLUtil.formatKind(sFParams, " bankaccounts")
		+ " LEFT JOIN  bankmovements  ON ( bkmv_bankaccountid =  bkAC_bankaccountid) ";
		sql += " WHERE bkac_bankaccountid > 0  ";
		sql +=  " ";

	sql += where;
	sql += " group by bkac_name";
	sql += "";
	System.out.println("++++++++++++++++++++ "+sql );

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
          ['Cuenta', ('Saldo')],
	      	<% 
	  		
				while (pmConn.next()) {
					double total = 0, total1 = 0, totalsaldo=0;
					total = pmConn.getInt("deposit");
					total1 = pmConn.getInt("withdraw");
					totalsaldo = total-total1;
			%>
	    		['<%= HtmlUtil.stringToHtml(pmConn.getString("Tipo")) %>', <%= totalsaldo %>],
			<%
					}
			%>
        ]);

        var options = {
                chart: {
                  title: 'Cuentas de banco con saldo',
                  subtitle: 'Por Cuenta De Banco',
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