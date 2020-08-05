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
 
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
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
	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	BmoOrder bmoOrder = new BmoOrder();
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
   		
	

			where += " AND ( prsa_salesuserid in (" +
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
	
	  sql =" SELECT deve_name as Desarrollo, count(prsa_propertysaleid) as Ventas FROM " +
	  		  " "+SQLUtil.formatKind(sFParams," propertysales")+
	  		  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflows")+" ON (wflw_wflowid = prsa_wflowid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," wflowphases")+" ON (wflw_wflowphaseid = wfph_wflowphaseid) " +		    		  	
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," properties")+" ON(prty_propertyid = prsa_propertyid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developmentregistry")+" ON(dvrg_developmentregistryid = prty_developmentregistryid) " +
			  " LEFT JOIN"+ SQLUtil.formatKind(sFParams," developments")+" ON(deve_developmentid = ptym_developmentid) " +
    		  " WHERE prsa_propertysaleid > 0 " ;
	
	sql += where;
	sql += " GROUP BY Desarrollo ";
			
	
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
          ['Desarrollo', 'Ventas'],
	      	<% 
	  		int total = 0;
				while (pmConn.next()) {
					total += pmConn.getInt("Ventas");
			%>
	    		['<%= HtmlUtil.stringToHtml(pmConn.getString("Desarrollo")) %>', <%= pmConn.getInt("Ventas") %>],
			<%
					}
			%>
        ]);

        var options = {
                chart: {
                  title: 'Ventas Activas Por Desarrollo',
                  subtitle: 'Total: <%= total %>',
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