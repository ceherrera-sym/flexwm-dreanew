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
      google.charts.load('current', {
        'packages':['geochart'],
        // Note: you will need to get a mapsApiKey for your project.
        // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
        'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY'
      });
      google.charts.setOnLoadCallback(drawRegionsMap);

      function drawRegionsMap() {
        var data = google.visualization.arrayToDataTable([
          ['Estado', 'Popularidad'],
          ['Jalisco', 200],
          ['Guanajuato', 300],
          ['Nuevo Leon', 400],
          ['Michoacan', 500],
          ['Coahuila', 600],
          ['Nayarit', 700]
        ]);

        var options = {
                region: 'MX',
                displayMode: 'markers',
                colorAxis: {colors: ['green', 'blue']}
              }

        var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));

        chart.draw(data, options);
      }
    </script>
  </head>
  <body>
	<div id="regions_div" style="width: 400px; height: 300px;"></div>
<%
	pmConn.close();
%> 
	</body>
</html>
