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
<%@page import="com.symgae.shared.sf.BmoUser"%>
<%@page import="com.symgae.shared.sf.BmoUserEmail"%>
<%@page import="com.symgae.shared.sf.BmoUserPhone"%>
<%@page import="com.symgae.shared.sf.BmoUserDate"%>
<%@page import="com.symgae.shared.sf.BmoUserAddress"%>
<%@page import="com.symgae.shared.sf.BmoUserSocial"%>
<%@page import="com.symgae.shared.sf.BmoUserRelative"%>
<%@page import="com.symgae.shared.sf.BmoUserContact"%>
<%@page import="com.symgae.shared.sf.BmoProfileUser"%>
<%@page import="com.symgae.shared.sf.BmoUserCompany"%>
<%@page import="com.symgae.shared.sf.BmoTag"%>
<%@page import="com.symgae.server.sf.PmTag"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte de Usuarios Detallado";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	DecimalFormat df = new DecimalFormat("#.##");

   	String sql = "", where = "", filters = "";
   	String status = "", birthdate = "", birthdateEnd = "", areaId = "", locationdId = "";   	
   	int programId = 0, companyId = 0, parentId = 0, startprogramId = 0, birthdateByMonth = 0;
   	
	String iconTrue = "", iconFalse = "";
	iconTrue = GwtUtil.getProperUrl(sFParams, "/icons/boolean_true.png"); 
	iconFalse = GwtUtil.getProperUrl(sFParams, "/icons/boolean_false.png");

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
		filters += "<i>Jefe inmediato: </i>" + request.getParameter("user_parentidLabel") + ", ";
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
    
	sql = "SELECT COUNT(*) as contador FROM " + SQLUtil.formatKind(sFParams, " users")+"" +
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
			System.out.println("contador de reportes -"+count);
	

		    System.out.println("sql.: "+sql);
	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);
	
   	PmConn pmConnAdic = new PmConn(sFParams);
   	pmConnAdic.open();
	

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
			<title>:::<%= title %>:::</title>
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
					" FROM " + SQLUtil.formatKind(sFParams, " users")+"" +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" on(area_areaid = user_areaid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" on(loct_locationid = user_locationid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" on(comp_companyid = user_companyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " programs")+" on(prog_programid = user_startprogramid) " +
				    " WHERE user_userid > 0 " +
					where +
					" ORDER BY user_firstname, user_fatherlastname, user_motherlastname, user_code, user_birthdate";
					pmConn.doFetch(sql);
		int i = 1;
		bmoUser = new BmoUser();
		BmoTag bmoTag = new BmoTag();
		PmTag pmTag = new PmTag(sFParams);
		
		BmoUserEmail bmoUserEmail = new BmoUserEmail();
		BmoUserPhone bmoUserPhone = new BmoUserPhone();
		BmoUserDate bmoUserDate = new BmoUserDate();
		BmoUserAddress bmoUserAddress = new BmoUserAddress();
		BmoUserSocial bmoUserSocial = new BmoUserSocial();
		BmoUserRelative bmoUserRelative = new BmoUserRelative();
		BmoUserContact bmoUserContact = new BmoUserContact();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmoUserCompany bmoUserCompany = new BmoUserCompany();



		while(pmConn.next()) {
			bmoUser.getStatus().setValue(pmConn.getString("users", "user_status"));
			bmoUser.getUiTemplate().setValue(pmConn.getString("users", "user_uitemplate"));
			bmoUser.getBloodType().setValue(pmConn.getString("users", "user_bloodtype"));
			bmoUser.getPhoto().setValue(pmConn.getString("users", "user_photo"));
			
			
			String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoUser.getPhoto());
			String blobKeyParseError = GwtUtil.getProperUrl(sFParams, "/icons/" + bmoProgram.getCode().toString().toLowerCase() + ".png");

// 			String blobKeyParse = "";
// 			if (pmConn.getString("users", "user_photo").length() > 0) {
// 				blobKeyParse = pmConn.getString("users", "user_photo");
// 				if (pmConn.getString("users", "user_photo").indexOf(".") > 0)
// 					blobKeyParse = pmConn.getString("users", "user_photo").substring(0, pmConn.getString("users", "user_photo").indexOf("."));
// 			}

		%>  
			<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
				<tr>
					<td align="left" width="10" rowspan="16" valign="top">
						<img border="0" width="100" height="100" src="<%= blobKeyParse %>"  onerror="this.src='<%= blobKeyParseError%>'">
					</td>
					<td colspan="4" class="reportHeaderCell">
						#<%= i%>-
						<%= HtmlUtil.stringToHtml(pmConn.getString("users", "user_firstname"))%>
						<%= HtmlUtil.stringToHtml(pmConn.getString("users", "user_fatherlastname"))%>
						<%= HtmlUtil.stringToHtml(pmConn.getString("users", "user_motherlastname"))%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left" colspan="4">Datos Generales</th>
				</tr>
				<tr class="reportCellEven">
				    <th class="reportCellText">
				        Clave:
				    </th>
			    	<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_code"), BmFieldType.STRING) %>	     
				    <th class="reportCellText">
				        Email:
				    </th>
			    	<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_email"), BmFieldType.EMAIL) %>
				</tr>
				<tr class="reportCellEven">	     
			    	<th class="reportCellText">
				        Nacimiento:
				    </th>
				    <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_birthdate"), BmFieldType.DATE) %>	     
				    <th class="reportCellText">
				        Tipo de Sangre:
				    </th>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUser.getBloodType().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>	     
				</tr>
				<tr>
					<th class="reportCellEven" align="left" colspan="4">Datos Laborales</th>
				</tr>
				<tr class="reportCellEven">	     
				    <th class="reportCellText">
				        Empresa:
				    </th>
						<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("companies", "comp_name") + " - " + pmConn.getString("companies", "comp_name"), BmFieldType.STRING)) %>	     
				    <th class="reportCellText">
				        Departamento:
				    </th>
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("areas", "area_name"), BmFieldType.STRING)) %>	     
				</tr>
				<tr class="reportCellEven">	     
				    <th class="reportCellText">
				        Ubicaci&oacute;n:
				    </th>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("locations", "loct_name"), BmFieldType.STRING)) %>	     
				    <th class="reportCellText">
				        Superior:
				    </th>
				    <%
				    // Jefe inmediato
				    String jefe = "";
					String jefeSql = "SELECT user_code AS jefe FROM " + SQLUtil.formatKind(sFParams, " users")+" WHERE user_userid = " + pmConn.getInt("users", "user_parentid");
					pmConnAdic.doFetch(jefeSql);
					if (pmConnAdic.next()) jefe = pmConnAdic.getString("jefe");
				    %>
				    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, jefe, BmFieldType.STRING)) %>				
			    </tr>
				<tr>
				<tr class="reportCellEven">	     
				    <th class="reportCellText">
				        Fecha Ingreso:
				    </td>
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_startdate"), BmFieldType.DATE) %>	     
				    <th class="reportCellText">
				        Fecha Baja:
				    </th>
				    <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_enddate"), BmFieldType.DATE) %>				
			    </tr>
			    <tr class="reportCellEven">	     
				    <th class="reportCellText">
				        # Empleado:
				    </th>
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_employeenumber"), BmFieldType.STRING) %>	     
				    <th class="reportCellText">
				        # IMSS:
				    </th>
				    <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_socialnumber"), BmFieldType.STRING) %>				
			    </tr>
			    <tr class="reportCellEven">	     
				    <th class="reportCellText">
				        RFC:
				    </th>
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_rfc"), BmFieldType.STRING) %>	     
				    <th class="reportCellText">
				        CURP:
				    </th>
				    <%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_curp"), BmFieldType.STRING) %>				
			    </tr>
				<tr class="reportCellEven">	     
				    <th class="reportCellText">
				        Notas:
				    </th>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_description"), BmFieldType.STRING)) %>				
				    <th class="reportCellText">
			        	Estatus:
				    </th>
		                <%= HtmlUtil.formatReportCell(sFParams, ((pmConn.getString("user_status").equals("A")) ? "<img src=\"" + iconTrue + "\"> Activo" : "<img src=\"" + iconFalse + "\"> Inactivo"), BmFieldType.STRING) %> 
		                <%//= HtmlUtil.formatReportCell(sFParams, bmoUser.getStatus().getSelectedOption().getLabeltoHtml(), BmFieldType.BOOLEAN) %>	 
			    </tr>
			    <tr>
					<th class="reportCellEven" align="left" colspan="4">Personalizaci&oacute;n del Sistema</th>
				</tr>
			    <tr class="reportCellEven">
				    <th class="reportCellText">
			        	Interfaz:
				    </th>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUser.getUiTemplate().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
				    <th class="reportCellText">
				        M&oacute;dulo Inicio:
				    </th>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("programs", "prog_code") + " - " + pmConn.getString("programs", "prog_name"), BmFieldType.STRING)) %>	      
				</tr>
				<tr class="reportCellEven">	     
				    <th class="reportCellText">
				        <%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoUser.getCustomField1()))%>:
				    </th>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_customfield1"), BmFieldType.STRING)) %>	     
				    <th class="reportCellText">
			        	<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoUser.getCustomField2()))%>:
				    </th>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_customfield2"), BmFieldType.STRING)) %>	     
				</tr>
				<tr class="reportCellEven">	     
					<th class="reportCellText">
				        ID Cal. Google:
				    </th>
					<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("users", "user_publicgcalendarid"), BmFieldType.STRING) %>	     
				    <th class="reportCellText">
			        Tags:
				    </th>
			        <td class="reportCellText">
				        <%
					    	String tagList = pmConn.getString("users", "user_tags");
					    	if (!pmConn.getString("users", "user_tags").equals("")){
								String[] split = tagList.split("\\:");
								for (int y = 0; y < split.length; y++) {
									String tagId = split[y];
									if (!tagId.equals("")) {
										bmoTag = (BmoTag)pmTag.get(Integer.parseInt(tagId));
										%>
										<%= HtmlUtil.stringToHtml(bmoTag.getCode().toString())%>|
										<%
									}
								}
							}
				    	%>
				    </td>
				</tr>
			</table>
			<br>
			<!-- 
				// Tabs adicionales
			-->
			<table class="" border="0" align="left" width="100%" style="font-size: 12px">
				<tr>
					<td>
						<table class="" border="0" align="left" width="33%" style="font-size: 12px">
							<tr>
								<th class="reportHeaderCell" colspan="2">
									Emails
								</th>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Tipo</th>
								<th class="reportCellEven" align="left">Email</th>
							</tr>
							
								<%
								// Emails
								String emails = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " useremails")+" WHERE usem_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(emails);
								while (pmConnAdic.next()) {
									bmoUserEmail.getType().setValue(pmConnAdic.getString("useremails", "usem_type"));
								%>
									<tr class="reportCellEven">
							    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUserEmail.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
							    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("useremails", "usem_email"), BmFieldType.EMAIL)) %>
						    		</tr>
								<% } %>
						</table>
						<table class="" border="0" align="left"  width="33%" style="font-size: 12px">
							<tr>
								<th class="reportHeaderCell" colspan="3">
									Tel&eacute;fonos
								</th>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Tipo</th>
								<th class="reportCellEven" align="left">N&uacute;mero</th>
								<th class="reportCellEven" align="left">Ext.</th>
							</tr>
							
							<%
								// Telefonos
								String phones = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " userphones")+" WHERE usph_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(phones);
								while (pmConnAdic.next()) {
									bmoUserPhone.getType().setValue(pmConnAdic.getString("userphones", "usph_type"));
								%>
								<tr class="reportCellEven">
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUserPhone.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userphones", "usph_number"), BmFieldType.PHONE) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userphones", "usph_extension"), BmFieldType.STRING) %>
					    		</tr>
							<% } %>
						</table>
						<table class="" border="0" align="left" width="33%" style="font-size: 12px">
							<tr>
								<th class="reportHeaderCell" colspan="3">
									Fechas
								</th>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Tipo</th>
								<th class="reportCellEven" align="left">Fecha</th>
								<th class="reportCellEven" align="left">Comentarios</th>
							</tr>
							
							<%
								// Fechas
								String dates = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " userdates")+" WHERE usda_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(dates);
								while (pmConnAdic.next()) {
									bmoUserDate.getType().setValue(pmConnAdic.getString("userdates", "usda_type"));
								%>
								<tr class="reportCellEven">
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUserDate.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userdates", "usda_relevantdate"), BmFieldType.DATE) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userdates", "usda_description"), BmFieldType.STRING)) %>
								</tr>
							<% } %>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<table class="" border="0" align="left" width="100%" style="font-size: 12px">
							<tr>
								<td class="reportHeaderCell" colspan="6">
									Direcciones
								</td>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Tipo</th>
								<th class="reportCellEven" align="left">Calle</th>
								<th class="reportCellEven" align="left">No.</th>
								<th class="reportCellEven" align="left">Colonia</th>
								<th class="reportCellEven" align="left">C.P.</th>
								<th class="reportCellEven" align="left">Ciudad</th>
							</tr>
							
							<%
								// Direcciones
								String address = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " useraddress ") +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" ON (city_cityid = usad_cityid)" +
												"WHERE usad_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(address);
								while (pmConnAdic.next()) {
									bmoUserAddress.getType().setValue(pmConnAdic.getString("useraddress", "usad_type"));
								%>
								<tr class="reportCellEven">
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUserAddress.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("useraddress", "usad_street"), BmFieldType.STRING)) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("useraddress", "usad_number"), BmFieldType.STRING) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("useraddress", "usad_neighborhood"), BmFieldType.STRING)) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("useraddress", "usad_zip"), BmFieldType.STRING) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("cities", "city_name"), BmFieldType.STRING)) %>
								</tr>
							<% } %>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<table class="" border="0" align="left"  width="100%" style="font-size: 12px">
							<tr>
								<td class="reportHeaderCell" colspan="6">
									Familiares
								</td>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Tipo</th>
								<th class="reportCellEven" align="left">Nombre</th>
								<th class="reportCellEven" align="left">Email</th>
								<th class="reportCellEven" align="left">Tel&eacute;fono</th>
								<th class="reportCellEven">Ext.</th>

							</tr>
							
							<%
								// Familiares
								String relatives = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " userrelatives ") +
													"WHERE usrl_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(relatives);
								while (pmConnAdic.next()) {
									bmoUserRelative.getType().setValue(pmConnAdic.getString("userrelatives", "usrl_type"));

								%>
								<tr class="reportCellEven">
					    			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoUserRelative.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS)) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userrelatives", "usrl_fullname"), BmFieldType.STRING)) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userrelatives", "usrl_email"), BmFieldType.STRING)) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userrelatives", "usrl_number"), BmFieldType.STRING) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("userrelatives", "usrl_extension"), BmFieldType.STRING) %>
								</tr>
							<% } %>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<table class="" border="0" align="left" width="100%" style="font-size: 12px">
							<tr>
								<td class="reportHeaderCell" colspan="6">
									Contactos
								</td>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Nombre</th>
								<th class="reportCellEven" align="left">Email</th>
								<th class="reportCellEven" align="left">Tel&eacute;fono</th>
								<th class="reportCellEven" >Ext.</th>
							</tr>
							
							<%
								// Contactos
								String contacts = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " usercontacts ") +
													"WHERE usco_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(contacts);
								while (pmConnAdic.next()) {
								%>
								<tr class="reportCellEven">
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("usercontacts", "usco_fullname"), BmFieldType.STRING)) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("usercontacts", "usco_email"), BmFieldType.EMAIL)) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("usercontacts", "usco_number"), BmFieldType.PHONE) %>
						    		<%= HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("usercontacts", "usco_extension"), BmFieldType.STRING) %>
								</tr>
							<% } %>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<table class="" border="0" align="left" width="33%" style="font-size: 12px">
							<tr>
								<td class="reportHeaderCell" colspan="6">
									Redes Sociales
								</td>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Tipo</th>
								<th class="reportCellEven" align="left">Cuenta</th>
							</tr>
							
							<%
								// Redes sociales
								String socials = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " usersocials ") +
													" LEFT JOIN " + SQLUtil.formatKind(sFParams, " socials")+" ON (soci_socialid = usso_socialid)" +
													"WHERE usso_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(socials);
								while (pmConnAdic.next()) {
								%>
								<tr class="reportCellEven">
					    			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("socials", "soci_name"), BmFieldType.STRING)) %>
						    		<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("usersocials", "usso_account"), BmFieldType.STRING)) %>
								</tr>
							<% } %>
						</table>
						<table class="" border="0" align="left" width="33%"  style="font-size: 12px">
							<tr>
								<td class="reportHeaderCell" colspan="6">
									Perfiles
								</td>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Nombre</th>
							</tr>
							
							<%
								// Permisos
								String programProfilesSql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " profileusers ") +
													" LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (prof_profileid = pfus_profileid)" +
													"WHERE pfus_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(programProfilesSql);
								while (pmConnAdic.next()) {
								%>
								<tr class="reportCellEven">
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("profiles", "prof_name"), BmFieldType.STRING)) %>
								</tr>
							<% } %>
						</table>
						<table class="" border="0" align="left" width="33%" style="font-size: 12px">
							<tr>
								<td class="reportHeaderCell" colspan="6">
									Empresas
								</td>
							</tr>
							<tr>								
								<th class="reportCellEven" align="left">Nombre</th>
							</tr>
							
							<%
								// Empresas
								String companies = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " usercompanies ") +
													" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = uscp_companyid)" +
													"WHERE uscp_userid = " + pmConn.getInt("users", "user_userid");
								pmConnAdic.doFetch(companies);
								while (pmConnAdic.next()) {
								%>
								<tr class="reportCellEven">
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConnAdic.getString("companies", "comp_name"), BmFieldType.STRING)) %>
								</tr>
							<% } %>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
			</table>
			<p style="page-break-after: always">&nbsp;</p>

		<% 	
			i++;
		}// Fin while 	   
		}
	}// Fin de if(no carga datos)
	pmConnCount.close();
  	pmConn.close();
   	pmConnAdic.close();
%>  
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } %>
  </body>
</html>