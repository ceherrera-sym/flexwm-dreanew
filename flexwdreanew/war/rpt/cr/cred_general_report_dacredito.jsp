<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito. *
 * @version 2013-10
 */ -->
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@page import="com.flexwm.server.cr.PmCredit"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%
	// Inicializar variables
 	String title = "Reporte Microcredito General";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress());

	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
   	String sql = "", where = "", groupFilter = "", whereGroup = "";
   	String startDate = "", endDate = "", userCollaborator = "";
   	String creditStatus = "", orderStatus = "";
   	String filters = "", collaboratorProfile = "",creditTypeId = "", amountStart = "", amountEnd = "";
   	int  customerId = 0, salesmanId = 0, locationId = 0;   	
   	int programId = 0, profileId = 0;
   	
    BmoCredit bmoCredit = new BmoCredit();
    PmCredit pmCredit = new PmCredit(sFParams);
    BmoOrder bmoOrder = new BmoOrder();
   	
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));  	
   	if (request.getParameter("cred_credittypeid") != null) creditTypeId = request.getParameter("cred_credittypeid");
   	if (request.getParameter("cred_customerid") != null) customerId = Integer.parseInt(request.getParameter("cred_customerid"));
   	if (request.getParameter("salesUserId") != null) salesmanId = Integer.parseInt(request.getParameter("salesUserId"));
   	if (request.getParameter("cred_locationid") != null) locationId = Integer.parseInt(request.getParameter("cred_locationid"));
   	if (request.getParameter("cred_startdate") != null) startDate = request.getParameter("cred_startdate");
   	if (request.getParameter("cred_enddate") != null) endDate = request.getParameter("cred_enddate");   		
   	if (request.getParameter("cred_status") != null) creditStatus = request.getParameter("cred_status");
   	if (request.getParameter("profileid") != null) profileId = Integer.parseInt(request.getParameter("profileid"));
   	if (request.getParameter("orde_status") != null) orderStatus = request.getParameter("orde_status");
   	if (request.getParameter("cred_amountstart") != null) amountStart = request.getParameter("cred_amountstart");
   	if (request.getParameter("cred_amountend") != null) amountEnd = request.getParameter("cred_amountend");

   	//Filtros
//    	if (creditTypeId > 0) {
//         where += " AND cred_credittypeid = " + creditTypeId;
//         filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
//     }
   	
   	if(!creditTypeId.equals("")){
   		where += SFServerUtil.parseFiltersToSql("cred_credittypeid", creditTypeId);
   	   filters += "<i>Tipo Cr&eacute;dito </i>" + request.getParameter("cred_credittypeidLabel") + ", ";
   	}
   	if (customerId > 0) {
        where += " AND cred_customerid = " + customerId;
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
    
	if (locationId > 0) {
 		where += " AND cred_locationid = " + locationId;
		filters += "<i>Ubicaci&oacute;n: </i>" + request.getParameter("cred_locationidLabel") + ", ";
	}
	
	if (profileId > 0) {
		whereGroup += " AND prof_profileid = " + profileId;
		filters += "<i>Grupo :</i>" + request.getParameter("profileidLabel") + ", ";
		collaboratorProfile = request.getParameter("profileidLabel");
		// Saca nombre del grupo
    	String[] collaboratorProfileShort = collaboratorProfile.split(" | ");
    	collaboratorProfile = collaboratorProfileShort[0];
	}
	
	if (!amountStart.equals("") || !amountEnd.equals("")) {
		double amountStartTmp = 0, amountEndTmp = 0;

		try {
			amountStartTmp = Double.parseDouble(amountStart);
		} catch (Exception e) {
			amountStartTmp = -1;
		}
		
		try {
			amountEndTmp = Double.parseDouble(amountEnd);
		} catch (Exception e) {
			amountEndTmp = -1;
		}
		
		if (amountStartTmp > 0 ) {
			where += " AND cred_amount >= " + amountStartTmp;
	        filters += "<i>R. Monto Inicial: >= </i>" + amountStartTmp + ", ";
		}
		
		if (amountEndTmp > 0) {
			where += " AND cred_amount <= " + amountEndTmp + "";
	        filters += "<i>R. Monto Final: <= </i>" + amountEndTmp + ", ";
		} 
	}
       	
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
        where += SFServerUtil.parseFiltersToSql("orde_status", orderStatus);
        filters += "<i>Estatus Pedido: </i>" + request.getParameter("orde_statusLabel") + ", ";
    }
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
 	// Obtener disclosure de datos
    String disclosureFilters = new PmCredit(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;

    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
		
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
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { %>
    
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
<table class="report" border="0">
	<tr>
		<td class="reportHeaderCell">#</td>
		<td class="reportHeaderCell">Fecha Pr&eacute;stamo</td>	
		<td class="reportHeaderCell">Cliente</td>
		<td class="reportHeaderCell">Promotor</td>
		<% if ((collaboratorProfile.length() > 0)) { %>
			<td class="reportHeaderCell"><%= collaboratorProfile%></td>
		<% } %>
		<td class="reportHeaderCell">Ubicaci&oacute;n</td>
		<td class="reportHeaderCell">Tipo Cr&eacute;dito</td>
		<td class="reportHeaderCellRight">Pr&eacute;stamo</td>
		<td class="reportHeaderCellRight">Cantidad por Pagar</td>
		<td class="reportHeaderCellRight">Cantidad por Sem.</td>
	</tr>
		 
<%		
	double ordeSumFinalAmount = 0;
	double paymentSumFinal = 0;
	double ordeSumFinalTotal = 0;
	
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	
	int i = 0;
	double credSumAmount = 0;
	double ordeSumTotal = 0;
	double paymentSum = 0;
	//abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();

	sql = " SELECT COUNT(*) as contador FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
			  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (loct_locationid = cred_locationid) " +
		      " WHERE cred_creditid > 0 " +  
		      where +
			  " ORDER BY loct_locationid, cred_startdate, cred_salesuserid, cred_creditid" ;
		      int count =0;
				//ejecuto el sql
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
				System.out.println("contador de registros es---->   "+count);
				//if que muestra el mensajede error
				if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
					%>
					
	<%=messageTooLargeList %>
					<% 
				}else{
	
	sql = " SELECT * FROM "+ SQLUtil.formatKind(sFParams,"credits ") +
		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"credittypes")+" ON (cred_credittypeid = crty_credittypeid) " +
		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (cred_salesuserid = user_userid) " +
		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"orders")+" ON (cred_orderid = orde_orderid) " +
		  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"customers")+" ON (cred_customerid = cust_customerid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " locations")+" ON (loct_locationid = cred_locationid) " +
	      " WHERE cred_creditid > 0 " +  
	      where +
		  " ORDER BY loct_locationid, cred_startdate, cred_salesuserid, cred_creditid" ;
	      
	      System.out.println("sql_general: "+sql);
	pmConn.doFetch(sql);       
	while (pmConn.next()) {
%>	
		<tr class="reportCellEven">
			<%=HtmlUtil.formatReportCell(sFParams, "" + (i+1), BmFieldType.STRING)%>		
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("credits", "cred_startdate").substring(0, 10), BmFieldType.STRING) %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("customers", "cust_code") + " " + pmConn.getString("customers", "cust_displayname"), BmFieldType.STRING)) %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("users", "user_code"), BmFieldType.CODE)) %>
	    	<%
				userCollaborator = "";

				if ((collaboratorProfile.length() > 0)) {

					//Obtener colaborador buscando por el Grupo
	
					if (profileId > 0) {
						sql = " SELECT user_code FROM "+ SQLUtil.formatKind(sFParams,"wflowusers ") +
							  " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"profiles")+" ON (wflu_profileid = prof_profileid) " +	
						      " LEFT JOIN "+ SQLUtil.formatKind(sFParams,"users")+" ON (wflu_userid = user_userid) " +						  
						      " WHERE wflu_wflowid = " + pmConn.getInt("cred_wflowid") +
						      whereGroup;
						pmConn2.doFetch(sql);
						int moreUsers = 1;
						while(pmConn2.next()) { 
							if(moreUsers > 1 ) userCollaborator += "| ";
							userCollaborator += pmConn2.getString("users", "user_code");
							moreUsers++;
						}
					}
			%>
	    			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + userCollaborator, BmFieldType.CODE)) %>
	    	<% 	} %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("locations", "loct_name"), BmFieldType.STRING)) %>
	    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("credittypes", "crty_name"), BmFieldType.STRING)) %>
	    	
	    	<%
		    	credSumAmount += pmConn.getDouble("cred_amount");
		    	ordeSumTotal += pmConn.getDouble("orde_total");
		    	paymentSum += (pmConn.getDouble("orde_total") / pmConn.getDouble("crty_deadline"));
	    	%>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getDouble("cred_amount"), BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getDouble("orde_total"), BmFieldType.CURRENCY) %>
	    	<%= HtmlUtil.formatReportCell(sFParams, "" + (pmConn.getDouble("orde_total") / pmConn.getDouble("crty_deadline")), BmFieldType.CURRENCY) %>

	    	
    	</tr>
<%
		i++;
	} // fin pmConn
%>		
	<tr><td colspan="<%= ((profileId > 0) ? "10": "9")%>">&nbsp;</td></tr>

	<tr class="reportCellCode">
	<td colspan="<%= ((profileId > 0) ? "7": "6")%>">
			&nbsp;
		</td>
		<%= HtmlUtil.formatReportCell(sFParams, "" + credSumAmount, BmFieldType.CURRENCY) %>
		<%= HtmlUtil.formatReportCell(sFParams, "" + ordeSumTotal, BmFieldType.CURRENCY) %>
		<%= HtmlUtil.formatReportCell(sFParams, "" + paymentSum, BmFieldType.CURRENCY) %>
	</tr>

		 
 </table>
  <%
  
}// Fin de if(no carga datos)
		pmConnCount.close();

	}
	pmConn2.close();
	pmConn.close();
  %>  

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } 
	System.out.println("\n Filtros Reporte: "+ filters);
	System.out.println("\n Fin reporte - Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
					+ " Reporte: "+title
					+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); %>
  </body>
</html>
