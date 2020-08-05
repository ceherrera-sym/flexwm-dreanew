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
 

<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.symgae.shared.sf.BmoUser"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte de Usuarios";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	DecimalFormat df = new DecimalFormat("#.##");
	bmoUser = new BmoUser();

   	String sql = "", where = "", filters = "";
   	String status = "", birthdate = "", birthdateEnd = "", areaId = "", locationdId = "";   	
   	int programId = 0, companyId = 0, parentId = 0, startprogramId = 0, birthdateByMonth = 0;

   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("user_areaid") != null) areaId = request.getParameter("user_areaid");
   	if (request.getParameter("user_locationid") != null) locationdId = request.getParameter("user_locationid");
   	if (request.getParameter("user_companyid") != null) companyId = Integer.parseInt(request.getParameter("user_companyid"));
   	if (request.getParameter("user_parentid") != null) parentId = Integer.parseInt(request.getParameter("user_parentid"));
   	if (request.getParameter("user_startprogramid") != null) startprogramId = Integer.parseInt(request.getParameter("user_startprogramid"));
   	if (request.getParameter("user_status") != null) status = request.getParameter("user_status");
   	if (request.getParameter("user_birthdate") != null) birthdate = request.getParameter("user_birthdate");
   	if (request.getParameter("birthdateEnd") != null) birthdateEnd = request.getParameter("birthdateEnd");
   	if (request.getParameter("birthdateByMonth") != null) birthdateByMonth = Integer.parseInt(request.getParameter("birthdateByMonth"));

   	
	// Filtros listados
	if (!areaId.equals("")) {
		where += SFServerUtil.parseFiltersToSql("user_areaid", areaId);
		filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
	
	if (!locationdId.equals("")) {
        where += SFServerUtil.parseFiltersToSql("user_locationid", locationdId);
		filters += "<i>Ubicaci&oacute;n: </i>" + request.getParameter("user_locationidLabel") + ", ";
	}
	
	if (companyId > 0) {
		where += " AND user_companyid = " + companyId;
		filters += "<i>Empresa: </i>" + request.getParameter("user_companyidLabel") + ", ";
	}
	
	if (parentId > 0) {
		where += " AND user_parentid = " + parentId;
		filters += "<i>Superior: </i>" + request.getParameter("user_parentidLabel") + ", ";
	}
	
	if (startprogramId > 0) {
		where += " AND user_startprogramid = " + startprogramId;
		filters += "<i>Modulo Inicio: </i>" + request.getParameter("user_startprogramidLabel") + ", ";
	}
	
	if (!status.equals("")) {
		where += SFServerUtil.parseFiltersToSql("user_status", status);
		filters += "<i>Estatus: </i>" + request.getParameter("user_statusLabel") + ", ";
   	}
	
	// Para usuarios por mes de fecha de nacimiento
	if (birthdateByMonth == 1){
		if (!birthdate.equals("")) {
			where += " AND DATE_FORMAT(user_birthdate, '%m-%d') >= '" + birthdate.substring(5, 10) + "'";
			filters += "<i>F. Nacimiento: </i>" + request.getParameter("user_birthdate") + ", ";
	    }
	    
	    if (!birthdateEnd.equals("")) {
	    	where += " AND DATE_FORMAT(user_birthdate, '%m-%d') <= '" + birthdateEnd.substring(5, 10) + "'";
	    	filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
	    }
	}else{
		if (!birthdate.equals("")) {
			where += " AND user_birthdate >= '" + birthdate + "'";
			filters += "<i>F. Nacimiento: </i>" + request.getParameter("user_birthdate") + ", ";
	    }
	    
	    if (!birthdateEnd.equals("")) {
	    	where += " AND user_birthdate <= '" + birthdateEnd + "'";
	    	filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
	    }
    }
	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmUser(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    	
    //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    
    sql = "SELECT COUNT(*) as contador FROM " + SQLUtil.formatKind(sFParams, " users ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on(area_areaid = user_areaid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" on(loct_locationid = user_locationid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on(comp_companyid = user_companyid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " programs")+" on(prog_programid = user_startprogramid) " +
		    " WHERE user_userid > 0 " +
			where +
			" ORDER BY user_firstname, user_fatherlastname, user_motherlastname, user_code, user_birthdate";

		    int count =0;
			//ejecuto el sql
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador------->  "+count);
			
	

	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);
	
	PmConn pmConnParent = new PmConn(sFParams);
	pmConnParent.open();

	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
%>
		
	<html>
		
		<% 
		// Imprimir
		String print = "0", permissionPrint = "";
		if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
		
		// Exportar a Excel
		String exportExcel = "0";
		if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
		if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
		}
		
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))){ %>
		<style> 
			body{
				user-select: none;
				-moz-user-select: none; 
				-o-user-select: none; 
				-webkit-user-select: none; 
				-ie-user-select: none; 
				-khtml-user-select:none; 
				-ms-user-select:none; 
				-webkit-touch-callout:none
			}
		</style>
		<style type="text/css" media="print">
		    * { display: none; }
		</style>
		<%
		permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if(print.equals("1") || exportExcel.equals("1")) { %>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<% }
		}
			
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))){ %>
		<head>
			<title>:::<%= appTitle %>:::</title>
			<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
		</head>
		
		<body class="default" <%= permissionPrint %> style="padding-right: 10px">
		
		<table border="0" cellspacing="0" cellpading="0" width="100%">
			<tr>
				<td align="left" width="80" rowspan="2" valign="top">	
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
				</td>
				<td class="reportTitle" align="left" colspan="2">
					<%= title %>
				</td>
			</tr>
			<tr>
				<td class="reportSubTitle">
					<b>Filtros:</b> <%= filters %>
					<br>			
				</td>
			<td class="reportDate" align="right">
					Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
				</td>
			</tr>
		</table>
		<br>
		<%
		//if que muestra el mensajede error
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
			%>
			
<%=messageTooLargeList %>
			<% 
		}else{
			sql = "SELECT * " +
					" FROM " + SQLUtil.formatKind(sFParams, " users ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on(area_areaid = user_areaid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" on(loct_locationid = user_locationid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on(comp_companyid = user_companyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " programs")+" on(prog_programid = user_startprogramid) " +
				    " WHERE user_userid > 0 " +
					where +
					" ORDER BY user_firstname, user_fatherlastname, user_motherlastname, user_code, user_birthdate";
					pmConn.doFetch(sql);

		%>
		<TABLE class="report" border="0">
		    <tr class="">
		       <td class="reportHeaderCell">#</td>
		       <td class="reportHeaderCell">Nombre</td>
		       <td class="reportHeaderCell">Apellido Paterno</td>
		       <td class="reportHeaderCell">Apellido Materno</td>
		       <td class="reportHeaderCell">Clave</td>
		       <td class="reportHeaderCell">Email</td>
		       <td class="reportHeaderCell">Nacimiento</td>
		       <td class="reportHeaderCell">Departamento</td>
		       <td class="reportHeaderCell">Ubicaci&oacute;n</td>
		       <td class="reportHeaderCell">Empresa</td>
		       <td class="reportHeaderCell">Superior</td>
		       <td class="reportHeaderCell">Modulo Inicio</td>
		       <td class="reportHeaderCell"><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoUser.getCustomField1())%></td>
		       <td class="reportHeaderCell"><%= sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoUser.getCustomField2())%></td>
		       <td class="reportHeaderCell">Estatus</td>
		    </tr>
		<%
			int i = 1;
			while(pmConn.next()) {  
				bmoUser.getStatus().setValue(pmConn.getString("users", "user_status"));
			%>  
			    <tr class="reportCellEven">
			    	<%= HtmlUtil.formatReportCell(sFParams, "" + i , BmFieldType.NUMBER) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_firstname"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_fatherlastname"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_motherlastname"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_code"), BmFieldType.CODE)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_email"), BmFieldType.EMAIL)) %>
			    	<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_birthdate"), BmFieldType.DATE) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("areas", "area_name"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("locations", "loct_name"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("companies", "comp_name"), BmFieldType.STRING)) %>
			    	<%
			    	String parentCode = "";
			    	sql = "SELECT user_code FROM " + SQLUtil.formatKind(sFParams, " users")+" WHERE user_userid = " + pmConn.getInt("users", "user_parentid");
			    	pmConnParent.doFetch(sql);
			    	if (pmConnParent.next()) parentCode = pmConnParent.getString("user_code");
			    		
			    	%>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + parentCode, BmFieldType.CODE)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("programs", "prog_name"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_customfield1"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_customfield2"), BmFieldType.STRING)) %>
			    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUser.getStatus().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
				<tr>   
			<% 	
				i++;
			} %>	   
			   
		      
		</TABLE>  
<%

	}// Fin de if(no carga datos)
	}
   pmConn.close();
   pmConnParent.close();
   pmConnCount.close();
%>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
		System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
						+ " Reporte: "+title
						+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
  </body>
</html>