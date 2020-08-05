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
 
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoCustomerService"%>
<%@page import="com.flexwm.shared.op.BmoCustomerServiceType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes General de Atencion a Clientes";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
    
    String sql = "", where = "", filters = "", groupFilter = "", 
    		registrationDate = "", registrationDateEnd = "", committalDate = "", committalDateEnd = "",
			solutionDate = "", solutionDateEnd = "", status = "", lockStartDate = "", lockEndDate = "";
    
    int i = 1, cols= 0, programId = 0,  customerServiceTypeId = 0, orderId = 0, customerId = 0, developmentId = 0, userId = 0;
    
    
    // Obtener parametros       
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("cuse_customerservicetypeid") != null) customerServiceTypeId = Integer.parseInt(request.getParameter("cuse_customerservicetypeid"));    
    if (request.getParameter("cuse_orderid") != null) orderId = Integer.parseInt(request.getParameter("cuse_orderid"));    
    if (request.getParameter("cuse_registrationdate") != null) registrationDate = request.getParameter("cuse_registrationdate");
    if (request.getParameter("registrationdateend") != null) registrationDateEnd = request.getParameter("registrationdateend");
    if (request.getParameter("cuse_committaldate") != null) committalDate = request.getParameter("cuse_committaldate");
    if (request.getParameter("committaldateend") != null) committalDateEnd = request.getParameter("committaldateend");
    if (request.getParameter("cuse_solutiondate") != null) solutionDate = request.getParameter("cuse_solutiondate");
    if (request.getParameter("solutiondateend") != null) solutionDateEnd = request.getParameter("solutiondateend");
    if (request.getParameter("cuse_status") != null) status = request.getParameter("cuse_status");
    if (request.getParameter("orde_customerid") != null) customerId = Integer.parseInt(request.getParameter("orde_customerid"));    
    if (request.getParameter("orde_lockstart") != null) lockStartDate = request.getParameter("orde_lockstart");
    if (request.getParameter("orde_lockend") != null) lockEndDate = request.getParameter("orde_lockend");
    if (request.getParameter("developmentid") != null) developmentId = Integer.parseInt(request.getParameter("developmentid"));
    if (request.getParameter("cuse_userid") != null) userId = Integer.parseInt(request.getParameter("cuse_userid"));    

    
 // Filtros
    if (customerServiceTypeId > 0) {
    	where += " AND cuse_customerservicetypeid = " + customerServiceTypeId;
        filters += "<i>Tipo Atn. Cliente: </i>" + request.getParameter("cuse_customerservicetypeidLabel") + ", ";
    }
    
    if (orderId > 0) {
    	where += " AND cuse_orderid = " + orderId;
    	filters += "<i>Pedido: </i>" + request.getParameter("cuse_orderidLabel") + ", ";
    }
    
    if (!registrationDate.equals("")) {
        where += " AND cuse_registrationdate >= '" + registrationDate + "'";
        filters += "<i>F. Registro: </i>" + request.getParameter("cuse_registrationdate") + ", ";
    }
    
    if (!registrationDateEnd.equals("")) {
        where += " AND cuse_registrationdate <= '" + registrationDateEnd + "'";
        filters += "<i>F. Registro Fin: </i>" + request.getParameter("registrationdateend") + ", ";
    }
    
    if (!committalDate.equals("")) {
        where += " AND cuse_committaldate >= '" + committalDate + "'";
        filters += "<i>F. Compromiso: </i>" + request.getParameter("cuse_committaldate") + ", ";
    }
    
    if (!committalDateEnd.equals("")) {
        where += " AND cuse_committaldate <= '" + committalDateEnd + "'";
        filters += "<i>F. Compromiso Fin: </i>" + request.getParameter("committaldateend") + ", ";
    }
    
    if (!solutionDate.equals("")) {
        where += " AND cuse_solutiondate >= '" + solutionDate + "'";
        filters += "<i>F. Soluci&oacute;n: </i>" + request.getParameter("cuse_solutiondate") + ", ";
    }
    
    if (!solutionDateEnd.equals("")) {
        where += " AND cuse_solutiondate <= '" + solutionDateEnd + "'";
        filters += "<i>F. Soluci&oacute;n Fin: </i>" + request.getParameter("solutiondateend") + ", ";
    }
    
    if (!status.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cuse_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("cuse_statusLabel") + ", ";
   	}
    
    if (customerId > 0) {
    	where += " AND cust_customerid = " + customerId;
    	filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
    }
    
    if (userId > 0) {
    	where += " AND cuse_userid = " + userId;
    	filters += "<i>Registrá: </i>" + request.getParameter("cuse_useridLabel") + ", ";
    }
    
    if (!lockStartDate.equals("")) {
        where += " AND orde_lockstart >= '" + lockStartDate + " 00:00'";
    	filters += "<i>Apartado Inicio: </i>" + request.getParameter("orde_lockstart") + " 00:00, ";
    }
    
    if (!lockEndDate.equals("")) {
        where += " AND orde_lockstart <= '" + lockEndDate + " 23:59'";
    	filters += "<i>Apartado Fin: </i>" + request.getParameter("orde_lockend") + " 23:59, ";
    }
    
    if (developmentId > 0) {
    	where += " AND deve_developmentid = " + developmentId;
    	filters += "<i>Desarrollo: </i>" + request.getParameter("developmentidLabel") + ", ";
    }
    
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;

  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    
    sql = " SELECT COUNT(*) as contador FROM " + SQLUtil.formatKind(sFParams, " customerservices ") +     
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customerservicetypes")+" ON(csty_customerservicetypeid = cuse_customerservicetypeid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = cuse_orderid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = cuse_userid) " +
	    	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderproperties")+" ON(orpy_orderid = orde_orderid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = orpy_propertyid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = dvph_developmentid) " +
		    " WHERE cuse_customerserviceid > 0 " + 
		    where +
		    " ORDER BY deve_code ASC, dvph_code ASC, cuse_registrationdate ASC ";
		    int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador DE REGISTROS --> "+count);
	//if que muestra el mensajede error
			
    
    
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerservices ") +     
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customerservicetypes")+" ON(csty_customerservicetypeid = cuse_customerservicetypeid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = cuse_orderid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = cuse_userid) " +
	    	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderproperties")+" ON(orpy_orderid = orde_orderid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = orpy_propertyid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
		    " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = dvph_developmentid) " +
		    " WHERE cuse_customerserviceid > 0 " + 
		    where +
		    " ORDER BY deve_code ASC, dvph_code ASC, cuse_registrationdate ASC ";
    
    BmoCustomerService bmoCustomerService = new BmoCustomerService();
    PmConn pmCustomerService = new PmConn(sFParams);
    pmCustomerService.open();
    pmCustomerService.doFetch(sql);
    
//    PmConn pmOrderComplaintFollowups = new PmConn(sFParams);
//    pmOrderComplaintFollowups.open();
    
    PmConn pmConnCustomer= new PmConn(sFParams);
	pmConnCustomer.open();
   
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
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
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
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { %>
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
		<%if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
			%>
			
			<%=messageTooLargeList %>
	<% 
}else{ %>
		<table class="report">
        	<tr class="">
	            <th class="reportHeaderCellCenter" width="1%">#</td>
	            <th class="reportHeaderCell" width="4%">Clave</td>
	            <!--<th class="reportHeaderCell">Queja</td>-->
	            <th class="reportHeaderCell">Descripci&oacute;n</td>
	            <th class="reportHeaderCell">Captur&oacute;</td>
	            <th class="reportHeaderCell" width="5%">Tipo<br>Atn. Cliente</td>
            	<!--<th class="reportHeaderCell" width="7%">Fecha<br>Venta</td>-->
            	<th class="reportHeaderCell" width="7%">Fecha<br>Entrega</td>
            	<th class="reportHeaderCellCenter" width="7%">D&iacute;as<br>Soluci&oacute;n</td>
	            <th class="reportHeaderCellCenter" width="7%">D&iacute;as<br>en Espera</td>
	            <th class="reportHeaderCell">Cliente</td>
	            <th class="reportHeaderCell">Tel&eacute;fono</td>
        		<th class="reportHeaderCell">Inmueble</td>
            	<th class="reportHeaderCell">Domicilio<br>Inmueble</td>
	            <th class="reportHeaderCell">Desarrollo</td>
	            <th class="reportHeaderCell">Etapa Des.</td>
	            <th class="reportHeaderCell">Estatus<br>Atn. Cliente</td>
	            <th class="reportHeaderCell">Activo</td>
	            <th class="reportHeaderCell">Fecha<br>Registro</td>
	            <!--<th class="reportHeaderCell">Fecha<br>Compromiso</td>-->
	            <th class="reportHeaderCell">Fecha<br>Soluci&oacute;n</td>
	            <!--<th class="reportHeaderCell">Seguimiento</td>-->
	        </tr>
		    
		        <%
		        while(pmCustomerService.next()){
		        	bmoCustomerService.getStatus().setValue(pmCustomerService.getString("customerservices", "cuse_status"));
		        %>
			        <tr class="reportCellEven">
		                 <%= HtmlUtil.formatReportCell(sFParams, "" + (i), BmFieldType.NUMBER) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservices", "cuse_code"), BmFieldType.CODE) %>
		                 <%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservices", "cuse_name"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservices", "cuse_description"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("users", "user_code"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservicetypes", "csty_name"), BmFieldType.STRING)) %>
		                 <%//= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("orders", "orde_lockstart"), BmFieldType.DATETIME) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("orders", "orde_lockend"), BmFieldType.DATETIME) %>
		                 <%
		                 String fechaRegistro = "", fechaSolucion = "", hoy = "";
		                 fechaRegistro = pmCustomerService.getString("customerservices", "cuse_registrationdate");
		                 fechaSolucion = pmCustomerService.getString("customerservices", "cuse_solutiondate");
		                 hoy = SFServerUtil.nowToString(sFParams, "yyyy-MM-dd");
		                 int diasSolucion = 0, diasEspera = 0;
		                 if (pmCustomerService.getString("customerservices", "cuse_status").equals("" + BmoCustomerService.STATUS_OPENED)
		                		 || pmCustomerService.getString("customerservices", "cuse_status").equals("" + BmoCustomerService.STATUS_FROZEN))
		                	 diasEspera = FlexUtil.daysDiff(sFParams, fechaRegistro, hoy);
		                 else
		                	 diasSolucion = FlexUtil.daysDiff(sFParams, fechaRegistro, fechaSolucion);

		                 %>
		                 <%= HtmlUtil.formatReportCell(sFParams, "" + diasSolucion, BmFieldType.NUMBER) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, "" + diasEspera, BmFieldType.NUMBER) %>
			                 
		                 <% 
		                 String customer = "";
		                 if (pmCustomerService.getString("customers","cust_customertype").equals("C")) {
		                	 customer = pmCustomerService.getString("customers","cust_code") + " " +
		                			 pmCustomerService.getString("customers","cust_legalname");
		                 } else {
		                	 customer = pmCustomerService.getString("customers","cust_code") + " " +
		                			 pmCustomerService.getString("customers","cust_displayname");
		                 }
		                 %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
		     			
		                 <%
			               //Telefonos del Cliente
			         		String phone = "";
			         		if(pmCustomerService.getString("customers", "cust_phone").equals("")){
				         		String sqlCustPhones = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerphones")+" WHERE cuph_customerid = " + pmCustomerService.getInt("orders", "orde_customerid") +" ORDER BY cuph_customerphoneid";
				         		pmConnCustomer.doFetch(sqlCustPhones);
				         		//System.out.println("sqlPh: "+sqlCustPhones);
				         		if(pmConnCustomer.next()) phone = pmConnCustomer.getString("customerphones", "cuph_number");

			         		}else{
			         			phone = pmCustomerService.getString("customers", "cust_phone");
			         		}
		                 
		                 %>
		                 <%= HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.PHONE) %>
		                 
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("properties", "prty_code"), BmFieldType.CODE) %>
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("properties", "prty_street") + "&nbsp;&nbsp;&nbsp;#" + pmCustomerService.getString("properties", "prty_number"), BmFieldType.STRING)) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("developments", "deve_code"), BmFieldType.CODE) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("developmentphases", "dvph_code"), BmFieldType.CODE) %>
		                 
		                 <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoCustomerService.getStatus().getSelectedOption().getLabel(), BmFieldType.OPTIONS)) %> 
		                 <%= HtmlUtil.formatReportCell(sFParams, ((pmCustomerService.getBoolean("cuse_active")) ? "Si" : "No"), BmFieldType.BOOLEAN) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservices", "cuse_registrationdate"), BmFieldType.DATE) %>
		                 <%//= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservices", "cuse_committaldate"), BmFieldType.DATE) %>
		                 <%= HtmlUtil.formatReportCell(sFParams, pmCustomerService.getString("customerservices", "cuse_solutiondate"), BmFieldType.DATE) %>
		                 <%
//		                 	String customerServiceFollowups = "";
//		                 	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerservicefollowups " +
//		                 			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users ON(user_userid = csfo_userid) " +
//		                 			" WHERE orcf_ordercomplaintid = " + pmCustomerService.getInt("customerservices", "cuse_ordercomplaintid");
//		                 	pmCustomerServiceFollowups.doFetch(sql);
//		                 	while(pmCustomerServiceFollowups.next()){
//		                 		customerServiceFollowups += "<li>"+
//		                 									//pmCustomerServiceFollowups.getString("user_code") + "&nbsp;|&nbsp;" +
//		                 									//pmCustomerServiceFollowups.getString("orcf_followupdate") + "&nbsp;|&nbsp;" +
//	                 										pmCustomerServiceFollowups.getString("orcf_description")+
//             										  "</li>";
//		                 	}
		                 %>
		                 
		                 <%//= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + customerServiceFollowups, BmFieldType.STRING)) %>


		             </tr>
		        <%
		           	i++;
		        
		        }
		        %>
		        <tr class="reportCellEven"><td colspan="15">&nbsp;</td></tr>  
		        
		</table>
		<%

	}// Fin de if(no carga datos)
//	pmCustomerServiceFollowups.close();
	pmCustomerService.close();
	pmConnCustomer.close();
	pmConnCount.close();
%>  
	<% if (print.equals("1")) { %>
	    <script>
	        //window.print();
	    </script>
    <% 
	}// FIN DEL CONTADOR
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	} %>
  </body>
</html>
