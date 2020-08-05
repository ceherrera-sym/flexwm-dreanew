







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
<%@page import="com.symgae.shared.sf.*"%>
<%@page import="com.symgae.server.sf.*"%>
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
   
   	// Obtener parametros
   	if (request.getParameter("user_userid") != null) userId = Integer.parseInt(request.getParameter("user_userid"));
   	
   	sql = " SELECT grup_name AS Grupo, count(pfus_usergroupid) AS Usuarios "
			+ "	FROM " + SQLUtil.formatKind(sFParams, "programprofiles")
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "profiles") + " ON (pfus_profileid = prof_profileid) "
			+ " WHERE pfus_userid > 0 "
			+ where  
			+ " GROUP BY grup_name";
			
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
	          ['Year', 'Sales', 'Expenses', 'Profit'],
	          ['2014', 1000, 400, 200],
	          ['2015', 1170, 460, 250],
	          ['2016', 660, 1120, 300],
	          ['2017', 1030, 540, 350]
	        ]);
	
	        var options = {
	          chart: {
	            title: 'Company Performance',
	            subtitle: 'Sales, Expenses, and Profit: 2014-2017',
	          }
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
