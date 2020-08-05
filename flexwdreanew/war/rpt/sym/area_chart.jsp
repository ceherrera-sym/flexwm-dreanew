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
   	
   	sql = " SELECT area_name AS Departamento, count(user_userid) AS Usuarios "
			+ "	FROM " + SQLUtil.formatKind(sFParams, " users ")
			+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "areas") + " ON (user_areaid = area_areaid) "
			+ " WHERE user_userid > 0 "
			+ where  
			+ " GROUP BY area_name";
			
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
          	['Departamento', 'Usuarios'],
          	<% 
          		int total = 0;
       			while (pmConn.next()) {
       				total += pmConn.getInt("Usuarios");
       		%>
	        		['<%= pmConn.getString("Departamento") %>', <%= pmConn.getInt("Usuarios") %>],
        	<%
       			}
        	%>
        	]);

        var options = {
          chart: {
            title: 'Usuarios',
            subtitle: 'Por Departamento, total: <%= total %>',
          },
          legend: {position: 'none'},
          bars: 'vertical'
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