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
 
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.symgae.shared.sf.*"%>
<%@page import="com.symgae.server.sf.*"%>
<%@page import="com.symgae.server.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
   	String sql = "", where = "";
   	int userId = 0;
   	BmoOrder bmoOrder = new BmoOrder();
   	
   	//Conexion a Base de Datos
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
   
   	// Obtener parametros
   	if (request.getParameter("user_userid") != null) userId = Integer.parseInt(request.getParameter("user_userid"));
   	
 	// Asigna usuario seleccionado
   	if (userId > 0) {
   		where += " AND ( racc_userid in (" + " SELECT user_userid FROM users " + " WHERE " + " user_userid = "
				+ userId + " or user_userid in ( " + " select u2.user_userid from users u1 "
				+ " left join users u2 on (u2.user_parentid = u1.user_userid) " + " where u1.user_userid = "
				+ userId + " ) " + " or user_userid in ( " + " select u3.user_userid from users u1 "
				+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
				+ " left join users u3 on (u3.user_parentid = u2.user_userid) " + " where u1.user_userid = "
				+ userId + " ) " + " or user_userid in ( " + " select u4.user_userid from users u1 "
				+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
				+ " left join users u3 on (u3.user_parentid = u2.user_userid) "
				+ " left join users u4 on (u4.user_parentid = u3.user_userid) " + " where u1.user_userid = "
				+ userId + " ) " + " or user_userid in ( " + " select u5.user_userid from users u1 "
				+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
				+ " left join users u3 on (u3.user_parentid = u2.user_userid) "
				+ " left join users u4 on (u4.user_parentid = u3.user_userid) "
				+ " left join users u5 on (u5.user_parentid = u4.user_userid) " + " where u1.user_userid = "
				+ userId + " ) " + " ) " + " ) ";
	}


   	
   	
   	// Obtener primer año del estudio
   	int currentYear = SFServerUtil.currentYear();
   	int lastYear = currentYear - 1;
   	

   	
   	double[] lastYearSales = new double[13];
   	double[] currentYearSales = new double[13];
   	
   	// Llena los arreglos de ventas
   	for (int i = 1; i <= 12; i++) {
   		// Suma ventas del año pasado
   		sql = "SELECT SUM(racc_total) AS total FROM " + SQLUtil.formatKind(sFParams, "raccounts")
   			+ " WHERE racc_receivedate BETWEEN CAST('" + currentYear + "-" + i + "-01' AS DATE) "
   			+ " AND CAST('" + (currentYear) + "-" + i + "-30' AS DATE) "
   			+ where;  

   		pmConn.doFetch(sql);
   		pmConn.next();
   		lastYearSales[i] = pmConn.getDouble("total");
   		
   		// Suma ventas del año actual
   		// Suma ventas del año pasado
   		sql = "SELECT SUM(racc_total) AS total FROM " + SQLUtil.formatKind(sFParams, "raccounts")
   			+ " WHERE racc_receivedate BETWEEN CAST('" + lastYear + "-" + i + "-01' AS DATE) "
   			+ " AND CAST('" + (lastYear) + "-" + i + "-30' AS DATE) "
   		   	+ where;  

   		pmConn.doFetch(sql);
   		pmConn.next();
   		currentYearSales[i] = pmConn.getDouble("total");
   	}
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
				['Mes', '<%= lastYear %>', '<%= currentYear %>'],
				['01', <%= lastYearSales[1] %>, <%= currentYearSales[1] %>],
				['02', <%= lastYearSales[2] %>, <%= currentYearSales[2] %>],
				['03', <%= lastYearSales[3] %>, <%= currentYearSales[3] %>],
				['04', <%= lastYearSales[4] %>, <%= currentYearSales[4] %>],
				['05', <%= lastYearSales[5] %>, <%= currentYearSales[5] %>],
				['06', <%= lastYearSales[6] %>, <%= currentYearSales[6] %>],
				['07', <%= lastYearSales[7] %>, <%= currentYearSales[7] %>],
				['08', <%= lastYearSales[8] %>, <%= currentYearSales[8] %>],
				['09', <%= lastYearSales[9] %>, <%= currentYearSales[9] %>],
				['10', <%= lastYearSales[10] %>, <%= currentYearSales[10] %>],
				['11', <%= lastYearSales[11] %>, <%= currentYearSales[11] %>],
				['12', <%= lastYearSales[12] %>, <%= currentYearSales[12] %>]
	        ]);
	
	        var options = {
	          chart: {
	            title: 'Facturas por Mes Y Año',
	            subtitle: 'Cuentas por pagar: <%= lastYear %>-<%= currentYear %>',
	          },
	          legend: {position: 'none'},
	          bars: 'vertical'
	        };
	
	        var chart = new google.charts.Bar(document.getElementById('columnchart_material'));
	
	        chart.draw(data, google.charts.Bar.convertOptions(options));
	      }
	    </script>
  </head>
  <body>
	<div id="columnchart_material" style="width: 400px; height: 300px;"></div>
<%
	pmConn.close();
%> 
	</body>
</html>
