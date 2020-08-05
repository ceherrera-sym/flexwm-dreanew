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
 

<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte Accesos por Grupo";
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	DecimalFormat df = new DecimalFormat("#.##");
	
	String iconTrue = "", iconFalse = "";
	iconTrue = GwtUtil.getProperUrl(sFParams, "/icons/boolean_true.png"); 
	iconFalse = GwtUtil.getProperUrl(sFParams, "/icons/boolean_false.png");

   	String sql = "", sql2 = "", where = "";
   	String startDate = "", endDate = "";   	
   	String filters = "";
   	int profileId = 0;
   	
   	int programId = 0;
    
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("sfca_profileid") != null) profileId = Integer.parseInt(request.getParameter("sfca_profileid"));

   	
   	//Conexiones
   	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
   	
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConnEspecial = new PmConn(sFParams);
	pmConnEspecial.open();
	
	PmConn pmConnList = new PmConn(sFParams);
	pmConnList.open();
		
	// Filtros listados
	if (profileId > 0) {
		  where += " AND sfca_profileid = " + profileId;
		  filters += "<i>Grupo: </i>" + request.getParameter("sfca_profileidLabel") + ", ";
	}
	
	sql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, "sfcomponents") +
			" WHERE sfcm_sfcomponentid IN ( " +
						" SELECT sfca_sfcomponentid FROM " + SQLUtil.formatKind(sFParams, "sfcomponentaccess") +
						" WHERE sfca_sfcomponentaccessid > 0 " +
						where +
					") " +
			" ORDER BY sfcm_name ASC";
	
	//System.out.println(" sql: " + sql);
	pmConnGroup.doFetch(sql);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
%>
<html>

	<script>
		//window.print();
	</script>
	<%  %>
  </body>
</html>