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
 
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	BmoOrder bmoOrder = new BmoOrder();
	 
 	String title = "Reporte Avales";
	
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
   	String sql = "", sqlOrderTypes = "" ,where = "", filters = "", groupFilter = "", birthdate = "", birthdateEnd = "",userCollaborator = "",creditTypeId = "";
   	String orderStatus = "", creditStatus = "", startDate = "", endDate = "", whereGroup = "", collaboratorGroup = "";
   	int programId = 0, customerId = 0, salesmanId = 0, regionId = 0, profileId = 0, birthdateByMonth = 0,
   			industryId = 0 ;
   	
   	// Obtener parametros
//    	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
//     if (request.getParameter("cust_customerid") != null) customerId = Integer.parseInt(request.getParameter("cust_customerid"));
//     if (request.getParameter("cust_customertype") != null) customerType = request.getParameter("cust_customertype");
//     if (request.getParameter("cust_salesmanid") != null) salesman = Integer.parseInt(request.getParameter("cust_salesmanid"));
//     if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
//     if (request.getParameter("cust_territoryid") != null) territoryId = Integer.parseInt(request.getParameter("cust_territoryid"));
//     if (request.getParameter("cust_maritalstatus") != null) maritalStatus = request.getParameter("cust_maritalstatus");
//     if (request.getParameter("cust_status") != null) status = request.getParameter("cust_status");
//     if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));
//     if (request.getParameter("cust_regionid") != null) regionId = Integer.parseInt(request.getParameter("cust_regionid"));
//     if (request.getParameter("birthdateByMonth") != null) birthdateByMonth = Integer.parseInt(request.getParameter("birthdateByMonth"));
// 	if (request.getParameter("cust_birthdate") != null) birthdate = request.getParameter("cust_birthdate");
// 	if (request.getParameter("birthdateEnd") != null) birthdateEnd = request.getParameter("birthdateEnd");
    if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
    if (request.getParameter("cred_credittypeid") != null) creditTypeId = request.getParameter("cred_credittypeid");
   	if (request.getParameter("cred_customerid") != null) customerId = Integer.parseInt(request.getParameter("cred_customerid"));
   	if (request.getParameter("salesUserId") != null) salesmanId = Integer.parseInt(request.getParameter("salesUserId"));
   	if (request.getParameter("cust_regionid") != null) regionId = Integer.parseInt(request.getParameter("cust_regionid"));
   	if (request.getParameter("cred_startdate") != null) startDate = request.getParameter("cred_startdate");
   	if (request.getParameter("cred_enddate") != null) endDate = request.getParameter("cred_enddate");   		
   	if (request.getParameter("cred_status") != null) creditStatus = request.getParameter("cred_status");
   	if (request.getParameter("profileid") != null) profileId = Integer.parseInt(request.getParameter("profileid"));
   	if (request.getParameter("orde_status") != null) orderStatus = request.getParameter("orde_status");
   	
    bmoProgram = (BmoProgram)pmProgram.get(programId);
	// Construir filtros 
//     if (creditTypeId > 0){
//     	where += " AND cred_credittypeid = " + creditTypeId;
//     	filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
//     }
	if(!creditTypeId.equals("")){
   		where += SFServerUtil.parseFiltersToSql("cred_credittypeid", creditTypeId);
   	   filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
   	}
	if (customerId > 0) {
//         where += " AND (cred_customerid = " + customerId + " OR crgu_customerid = " + customerId + ")";
		where += "AND (crgu_customerid = " + customerId + " OR cred_customerid = " + customerId + ")";	
        filters += "<i>Cliente: </i>" + request.getParameter("cred_customeridLabel") + ", ";
    }
	 
    if (salesmanId > 0) {
    	where += " AND ( " +
					" cred_salesuserid = " + salesmanId +
					" OR cred_wflowid IN ( " +
						 	" SELECT wflw_wflowid FROM "+ SQLUtil.formatKind(sFParams,"wflowusers  ") +
						 	" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"wflows")+" on (wflu_wflowid = wflw_wflowid) " +
						 	" LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credits")+" on (cred_wflowid = wflw_wflowid) " +
			   				" WHERE wflu_userid = " + salesmanId + 
			   			 	" AND cred_creditid > 0 " +
						 " AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + 
					 " ) " + 
				 " )";
        filters += "<i>Usuario: </i>" + request.getParameter("salesUserIdLabel") + ", ";
    }
    if (regionId > 0) {
 		where += " AND cust_regionid = " + regionId;
		filters += "<i>Zona: </i>" + request.getParameter("cust_regionidLabel") + ", ";
	}
//     if (profileId > 0) {
// 		whereGroup += " AND prof_profileid = " + profileId;
// 		filters += "<i>Grupo :</i>" + request.getParameter("profileidLabel") + ", ";
// 		collaboratorGroup = request.getParameter("profileidLabel");
// 		// Saca nombre del grupo
//     	String[] collaboratorGroupShort = collaboratorGroup.split(" | ");
//     	collaboratorGroup = collaboratorGroupShort[0];
// 	}
    if (!startDate.equals("")) {
        where += " AND cred_startdate >= '" + startDate + " 00:00' ";
        filters += "<i>Fecha Inicial: </i>" + startDate + ", ";
    }
    if (!endDate.equals("")) {
        where += " AND cred_startdate <= '" + endDate + " 23:59' ";
        filters += "<i>Fecha Final: </i>" + endDate + ", ";
    }
    
    if (!creditStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql("cred_status", creditStatus);
        filters += "<i>Estatus Cr&eacute;dito: </i>" + request.getParameter("cred_statusLabel") + ", ";
    }
    
    if (!orderStatus.equals("")) {
        where += SFServerUtil.parseFiltersToSql(" orde_status", orderStatus);
        filters += "<i>Estatus Pedido: </i>" + request.getParameter("orde_statusLabel") + ", ";
    }
    
  
	
	PmConn pmConnGroup = new PmConn(sFParams);
	pmConnGroup.open();
	
	PmConn pmConnExtra = new PmConn(sFParams);
	pmConnExtra.open();
	
	//abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    
	sql = " SELECT COUNT(*) as contador FROM  " + SQLUtil.formatKind(sFParams, " customers ") +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " creditguarantees")+
			" ON customers.cust_customerid = creditguarantees.crgu_customerid " +
			" LEFT JOIN credits ON creditguarantees.crgu_creditid = credits.cred_creditid "+
			" LEFT JOIN orders ON credits.cred_orderid = orders.orde_orderid " +
			" LEFT JOIN users ON credits.cred_salesuserid = users.user_userid " +
			" WHERE crgu_customerid IS NOT NULL " +
			where + 
			" ORDER BY cust_customerid";
	
	int count =0;
	//ejecuto el sql
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	System.out.println("contador de reportes -> "+count);
	
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
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))){ %>
		<head>
			<title>:::<%= title %>:::</title>
			<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
		</head>
		
		<body class="default" <%= permissionPrint %> style="padding-right: 10px">
		
		<table border="0" cellspacing="0" cellpading="0" width="100%">
			<tr>
				<td align="left" rowspan="2" valign="top">	
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
		<%
		//if que muestra el mensajede error
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
			%>
			
<%=messageTooLargeList %>
			<% 
		}else{
			
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customers ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " creditguarantees")+
					" ON customers.cust_customerid = creditguarantees.crgu_customerid " +
					" LEFT JOIN credits ON creditguarantees.crgu_creditid = credits.cred_creditid "+
					" LEFT JOIN orders ON credits.cred_orderid = orders.orde_orderid " +
					" LEFT JOIN users ON credits.cred_salesuserid = users.user_userid " +
					" WHERE crgu_customerid IS NOT NULL " +
					where + 
					" ORDER BY cust_customerid";
			
			System.out.println("sql_general: "+sql);
			
			pmConnCount.doFetch(sql);

		%>
		<br>
		<table class="report" width="100%">
			<tr>
				<td class="reportHeaderCellCenter">#</td>
		    	<td class="reportHeaderCell" >Clave</td>
		    	<td class="reportHeaderCell" >Nombre</td>
		        <td class="reportHeaderCell" >Usuario</td>
		       	<td class="reportHeaderCell" >Crédito</td>   		      
		       	<td class="reportHeaderCell" >Cliente Crédito</td>  
		       	<td class="reportHeaderCell" >Nombre</td> 
		       	<td class="reportHeaderCell" >Estatus Crédito</td>
		       	<td class="reportHeaderCell" >Estatus Pedido</td>
		       	
		       
		   

		    <% 		 
		    pmConnGroup.doFetch(sql);
			int i = 1;
	        while(pmConnGroup.next()) {	     
	        	sql = "SELECT cust_code,cust_displayname FROM customers WHERE cust_customerid = " + pmConnGroup.getString("cred_customerid");
	        	pmConnExtra.doFetch(sql);
	        	String credCustomer = "",credCode = "";
	        	while(pmConnExtra.next()){
	        		credCustomer = pmConnExtra.getString("cust_displayname");
	        		credCode = pmConnExtra.getString("cust_code");
	        		
	        	}
				%>
				<tr class="reportCellEven">
				
					<%=HtmlUtil.formatReportCell(sFParams,""+i,BmFieldType.NUMBER) %>
					<%=HtmlUtil.formatReportCell(sFParams,pmConnGroup.getString("cust_code"),BmFieldType.CODE) %>		
					<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,pmConnGroup.getString("cust_displayname"),BmFieldType.STRING)) %>	
					<%=HtmlUtil.formatReportCell(sFParams,pmConnGroup.getString("user_firstname") + " " + 
							pmConnGroup.getString("user_fatherlastname") + " " + 
							pmConnGroup.getString("user_motherlastname")
							,BmFieldType.STRING) %>		
					<%=HtmlUtil.formatReportCell(sFParams,pmConnGroup.getString("cred_code"),BmFieldType.STRING) %>
								
					<%=HtmlUtil.formatReportCell(sFParams,credCode,BmFieldType.CODE) %>
					<%=HtmlUtil.formatReportCell(sFParams,credCustomer,BmFieldType.STRING) %>
<!-- 					Estatus de Credito -->
					<%if (pmConnGroup.getString("cred_status").equals(""+BmoCredit.STATUS_REVISION)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"En Revisión",BmFieldType.STRING) %>	
					<%} %>
					<%if (pmConnGroup.getString("cred_status").equals(""+BmoCredit.STATUS_AUTHORIZED)){ %>
					<%=HtmlUtil.formatReportCell(sFParams,"Autorizado",BmFieldType.STRING) %>
					<%} %>
					<%if (pmConnGroup.getString("cred_status").equals(""+BmoCredit.STATUS_FINISHED)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"Finalizado",BmFieldType.STRING) %>
					<%} %>
					<%if (pmConnGroup.getString("cred_status").equals(""+BmoCredit.STATUS_CANCELLED)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"Cancelada",BmFieldType.STRING) %>
					<%} %>
					
<!-- 					Estatus de orden -->
					<%if (pmConnGroup.getString("orde_status").equals(""+BmoOrder.STATUS_REVISION)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"En Revisión",BmFieldType.STRING) %>
					<%} %>
					<%if (pmConnGroup.getString("orde_status").equals(""+BmoOrder.STATUS_AUTHORIZED)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"Autorizada",BmFieldType.STRING) %>
					<%} %>
					<%if (pmConnGroup.getString("orde_status").equals(""+BmoOrder.STATUS_FINISHED)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"Terminado",BmFieldType.STRING) %>
					<%} %>
					<%if (pmConnGroup.getString("orde_status").equals(""+BmoOrder.STATUS_CANCELLED)) {%>
					<%=HtmlUtil.formatReportCell(sFParams,"Cancelada",BmFieldType.STRING) %>
					<%} %>
				</tr>
				
	   			<%	
   				i++;
	        } // pmConnGroup
			          %>
			
		</table> 
	<% 
		
	}//Fin de validacion de 5 mil registros	
	

%> 
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	}// Fin de if(no cargar datos)
	pmConnExtra.close();
	pmConnGroup.close();
	pmConnCount.close();
	
	System.out.println("\n Filtros Reporte: "+ filters);
	System.out.println("\n Fin reporte - Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
	%>
  </body>
</html>