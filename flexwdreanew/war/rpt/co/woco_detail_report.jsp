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
 
<%@page import="com.flexwm.server.FlexUtil"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<% 
	// Inicializar variables
 	String title = "Reportes de Contratos de Obras";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoWorkContract bmoWorkContract = new BmoWorkContract();
	BmoWork bmoWork = new BmoWork();

   	String sql = "", where = "", filters = "", 
   			startDate = "", endDate = "", startDatePayment = "", endDatePayment= "", startDateContract = "", endDateContract = "",
   			status = "", statusPayment = "";
   	int programId = 0, workId = 0, supplierId = 0, budgetItemId = 0, budgetId = 0, companyId = 0, developmentPhaseId = 0;
   
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("woco_workid") != null) workId = Integer.parseInt(request.getParameter("woco_workid"));
   	if (request.getParameter("woco_companyid") != null) companyId = Integer.parseInt(request.getParameter("woco_companyid"));
   	if (request.getParameter("woco_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("woco_supplierid"));
   	if (request.getParameter("woco_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("woco_budgetitemid"));
   	if (request.getParameter("woco_startdate") != null) startDate = request.getParameter("woco_startdate");
   	if (request.getParameter("woco_enddate") != null) endDate = request.getParameter("woco_enddate");
   	if (request.getParameter("woco_paymentdate") != null) startDatePayment = request.getParameter("woco_paymentdate");
   	if (request.getParameter("endDatePayment") != null) endDatePayment = request.getParameter("endDatePayment");
   	if (request.getParameter("woco_datecontract") != null) startDateContract = request.getParameter("woco_datecontract");
   	if (request.getParameter("endDateContract") != null) endDateContract = request.getParameter("endDateContract");
   	if (request.getParameter("woco_status") != null) status = request.getParameter("woco_status");
   	if (request.getParameter("woco_paymentstatus") != null) statusPayment = request.getParameter("woco_paymentstatus");
   	if (request.getParameter("work_developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("work_developmentphaseid"));
   	if (request.getParameter("budgetId") != null) budgetId = Integer.parseInt(request.getParameter("budgetId"));

	// Construir filtros 
    if (workId > 0) {
		where += " AND woco_workid = " + workId;
   		filters += "<i>Obra: </i>" + request.getParameter("woco_workidLabel") + ", ";
   	}
    
    if (companyId > 0) {
		where += " AND woco_companyid = " + companyId;
   		filters += "<i>Empresa: </i>" + request.getParameter("woco_companyidLabel") + ", ";
   	}
    
    if (supplierId > 0) {
		where += " AND woco_supplierid = " + supplierId;
   		filters += "<i>Contratista: </i>" + request.getParameter("woco_supplieridLabel") + ", ";
   	}
    
    if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
    	
	    if (budgetId > 0) {
			where += " AND budg_budgetid = " + budgetId;
	   		filters += "<i>Presupuesto: </i>" + request.getParameter("budgetIdLabel") + ", ";
	   	}
	    
	    if (budgetItemId > 0) {
			where += " AND woco_budgetitemid = " + budgetItemId;
	   		filters += "<i>Item Pres.: </i>" + request.getParameter("woco_budgetitemidLabel") + ", ";
	   	}
    }
    
    if (!startDate.equals("")) {
   		where += " AND woco_startdate >= '" + startDate + "'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	if (!endDate.equals("")) {
   		where += " AND woco_startdate <= '" + endDate + "'";
   		filters += "<i>Fecha T&eacute;rmino: </i>" + endDate + ", ";
   	}
   	
   	if (!startDatePayment.equals("")) {
   		where += " AND woco_paymentdate >= '" + startDatePayment + "'";
   		filters += "<i>F. Pago Anticipo: </i>" + startDatePayment + ", ";
   	}
   	
   	if (!endDatePayment.equals("")) {
   		where += " AND woco_paymentdate <= '" + endDatePayment + "'";
   		filters += "<i>F. Pago Anticipo Fin: </i>" + endDatePayment + ", ";
   	}
   	
   	if (!startDateContract.equals("")) {
   		where += " AND woco_datecontract >= '" + startDateContract + "'";
   		filters += "<i>Fecha de Contrato: </i>" + startDateContract + ", ";
   	}
   	
   	if (!endDateContract.equals("")) {
   		where += " AND woco_datecontract <= '" + endDateContract + "'";
   		filters += "<i>Fecha de Contrato Fin: </i>" + endDateContract + ", ";
   	}

    if (!status.equals("")) {
        where += SFServerUtil.parseFiltersToSql("woco_status", status);        
   		filters += "<i>Estatus: </i>" + request.getParameter("woco_statusLabel") + ", ";
   	}
    
    if (!statusPayment.equals("")) {
        where += SFServerUtil.parseFiltersToSql("woco_paymentstatus", statusPayment);        
   		filters += "<i>Estatus Pago: </i>" + request.getParameter("woco_paymentstatusLabel") + ", ";
   	}
    
    if (developmentPhaseId > 0) {
		where += " AND work_developmentphaseid = " + developmentPhaseId;
   		filters += "<i>Etapa Desarrollo: </i>" + request.getParameter("work_developmentphaseidLabel") + ", ";
   	}
  //abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
    sql = " SELECT COUNT(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " workcontracts ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " works")+" ON(work_workid = woco_workid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = work_developmentphaseid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid= woco_companyid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = woco_supplierid) ";
    	    if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
	    		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON(bgit_budgetitemid = woco_budgetitemid) " +
			    	   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON(budg_budgetid = bgit_budgetid) " +
					   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) ";
    	    }
    		sql += " WHERE woco_workcontractid > 0 " + where + 
    		" ORDER BY supl_code ASC, dvph_code ASC,";
    		 if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
    			 sql += "bgty_name ASC," ;
    		 }
    		sql += " work_code ASC";
			int count =0;
			//ejecuto el sql DEL CONTADOR
			pmConnCount.doFetch(sql);
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			
			System.out.println("contador DE REGISTROS --> "+count);
    		
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " workcontracts ") +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " works")+" ON(work_workid = woco_workid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = work_developmentphaseid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid= woco_companyid) " +
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = woco_supplierid) ";
    	    if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
	    		sql += " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON(bgit_budgetitemid = woco_budgetitemid) " +
			    	   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON(budg_budgetid = bgit_budgetid) " +
					   " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) ";
    	    }
    		sql += " WHERE woco_workcontractid > 0 " + where + 
    		" ORDER BY supl_code ASC, dvph_code ASC,";
    		 if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
    			 sql += "bgty_name ASC," ;
    		 }
    		sql += " work_code ASC";
    
    //System.out.println("sql: "+sql);
    PmConn pmWorkContracts = new PmConn(sFParams);
    pmWorkContracts.open();
    
    PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
    
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
			<!--<b>Agrupado Por:</b> Vendedor, <b>Ordenado por:</b> Clave -->
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
%>
<p>
<table class="report">
			<tr class="">		
				<td class="reportHeaderCell">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Nombre</td>
				<td class="reportHeaderCell">Descripci&oacute;n</td>
				<td class="reportHeaderCell">Etapa<br>Desarrollo</td>
			    <% if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) { %>
			    	<td class="reportHeaderCell">Presupuesto</td>
			    	<td class="reportHeaderCell">Item<br>Presupuesto</td>
			    <% } %>
			    <td class="reportHeaderCell">Contratista</td>			    
				<td class="reportHeaderCell">Monto<br>Contratado</td>
				<td class="reportHeaderCell">Anticipo</td>
				<td class="reportHeaderCell">Amortizado</td>
				<td class="reportHeaderCell">Por Amortizar</td>
				<td class="reportHeaderCell">Estimado</td>
				<td class="reportHeaderCell">Total a<br>Pagar</td>
				<td class="reportHeaderCell">Iva</td>
				<td class="reportHeaderCell">Por<br>Estimar</td>
				<td class="reportHeaderCell">Otros<br>Cargos</td>
				<td class="reportHeaderCell">Fondo<br>Garantia</td>
				<td class="reportHeaderCell">% Acumulado</td>
				<td class="reportHeaderCell">Comentarios</td>
				<td class="reportHeaderCell">Estatus</td>
			</tr>
			<%
				int i = 1;
			
				//Totales
				double wocoTotal = 0, downPaymentTotal = 0, amortizedTotal = 0, toAmortizedTotal = 0;
				double estimatedTotal = 0, amountPayTotal = 0, taxesTotal = 0, toEstimatedTotal = 0;
				double otherTotal = 0, warrantyFundTotal = 0;
				
				double wocoTotalSupl = 0, downPaymentTotalSupl = 0, amortizedTotalSupl = 0, toAmortizedTotalSupl = 0;
				double estimatedTotalSupl = 0, amountPayTotalSupl = 0, taxesTotalSupl = 0, toEstimatedTotalSupl = 0;
				double otherTotalSupl = 0, warrantyFundTotalSupl = 0;
				
				int suplId = 0;
				pmWorkContracts.doFetch(sql);
				while (pmWorkContracts.next()) {	
											
						bmoWork.getStatus().setValue(pmWorkContracts.getString("works", "work_status"));
						bmoWorkContract.getStatus().setValue(pmWorkContracts.getString("workcontracts", "woco_status"));
						bmoWorkContract.getPaymentStatus().setValue(pmWorkContracts.getString("workcontracts", "woco_paymentstatus"));
			%>
					
				<% if (i != 1 && suplId != pmWorkContracts.getInt("supl_supplierid")) { %>
					
					<tr class="reportCellEven reportCellCode">
						<% if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) { %>
							<td class="reportCellEven" colspan="8">&nbsp;</td>
						<% } else { %>
							<td class="reportCellEven" colspan="7">&nbsp;</td>
						<% } %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + wocoTotalSupl, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + downPaymentTotalSupl, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amortizedTotalSupl, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + toAmortizedTotalSupl, BmFieldType.CURRENCY) %>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + estimatedTotalSupl, BmFieldType.CURRENCY) %>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + amountPayTotalSupl, BmFieldType.CURRENCY) %>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + taxesTotalSupl, BmFieldType.CURRENCY) %>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + toEstimatedTotalSupl, BmFieldType.CURRENCY) %>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + otherTotalSupl, BmFieldType.CURRENCY) %>
			            <%= HtmlUtil.formatReportCell(sFParams, "" + warrantyFundTotalSupl, BmFieldType.CURRENCY) %>
			            <td class="reportCellEven" colspan="3">&nbsp;</td>
					</tr>
					<%
						//Borrar los valores
						wocoTotalSupl = 0;
				    	downPaymentTotalSupl = 0;
				    	amortizedTotalSupl = 0;
				    	toAmortizedTotalSupl = 0;
				    	estimatedTotalSupl = 0;
				    	amountPayTotalSupl = 0;
				    	taxesTotalSupl = 0;
				    	toEstimatedTotalSupl = 0;
				    	otherTotalSupl = 0;
				    	warrantyFundTotalSupl = 0;
					%>
				<% } %>	
			        <tr class="reportCellEven">
	                	<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>                                    
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_code"), BmFieldType.CODE) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_name"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_description"), BmFieldType.STRING)) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("developmentphases","dvph_code"), BmFieldType.CODE) %>
		                
					    <% if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) { %>
					    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("budgets", "budg_name") + " | " + pmWorkContracts.getString("budgets", "budg_name"), BmFieldType.STRING)) %>
					    	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("budgetitemtypes", "bgty_name") + " | " + pmWorkContracts.getString("budgetitemtypes", "bgty_name"), BmFieldType.STRING)) %>
					    <% } %>
					    <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("suppliers", "supl_code") + " | " + pmWorkContracts.getString("suppliers", "supl_name"), BmFieldType.STRING)) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + pmWorkContracts.getDouble("woco_total"), BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + pmWorkContracts.getDouble("woco_downpayment"), BmFieldType.CURRENCY) %>
					    <%
					    	//Amortizado, Estimado
					    	double downpayment = 0, amortized = 0, estimated  = 0, warrantyFund = 0, taxes = 0, others = 0, totalPay = 0;
					    	downpayment = pmWorkContracts.getDouble("woco_downpayment");
					    	
					    	sql= " SELECT SUM(coes_downpayment) AS downpayment, SUM(coes_amount) AS	estimated, " +
					    	     " SUM(coes_warrantyfund) AS warrantyFund, SUM(coes_tax) AS taxes, SUM(coes_othersexpenses) AS others, " +
					    		 " SUM(coes_total) AS totalPay FROM " + SQLUtil.formatKind(sFParams, " contractestimations ") + 
					    	     " WHERE coes_workcontractid = " + pmWorkContracts.getInt("woco_workcontractid");
					    	pmConn.doFetch(sql);
					    	if (pmConn.next()) {
					    		amortized = pmConn.getDouble("downpayment");
					    		estimated = pmConn.getDouble("estimated");
					    		warrantyFund = pmConn.getDouble("warrantyFund");
					    		taxes = pmConn.getDouble("taxes");
					    		others = pmConn.getDouble("others");
					    		totalPay = pmConn.getDouble("totalPay");
					    	}
					    	
					    	
					    	//Por Amortizar
					    	double toAmortized = downpayment - amortized;
					    	double toEstimated = pmWorkContracts.getDouble("woco_total") - (estimated + taxes);
					    	
					    	//Totales por proveedor
					    	wocoTotalSupl += pmWorkContracts.getDouble("woco_total");
					    	downPaymentTotalSupl += downpayment;
					    	amortizedTotalSupl += amortized;
					    	toAmortizedTotalSupl += toAmortized;
					    	estimatedTotalSupl += estimated;
					    	amountPayTotalSupl += totalPay;
					    	taxesTotalSupl += taxes;
					    	toEstimatedTotalSupl += toEstimated;
					    	otherTotalSupl += others;
					    	warrantyFundTotalSupl  += warrantyFund;
					    						    	
					    	//Totales
					    	wocoTotal += pmWorkContracts.getDouble("woco_total");
					    	downPaymentTotal += downpayment;
					    	amortizedTotal += amortized;
					    	toAmortizedTotal += toAmortized;
					    	estimatedTotal += estimated;
					    	amountPayTotal += totalPay;
					    	taxesTotal += taxes;
					    	toEstimatedTotal += toEstimated;
					    	otherTotal += others;
					    	warrantyFundTotal += warrantyFund;
					    %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + amortized, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + toAmortized, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + estimated, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + totalPay, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + taxes, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + toEstimated, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + others, BmFieldType.CURRENCY) %>
					    <%= HtmlUtil.formatReportCell(sFParams, "" + warrantyFund, BmFieldType.CURRENCY) %>
					    <%	
					    	// % Acumlado
					    	double percAmount = 0;
					    	sql = " SELECT SUM(coes_amount) AS amount, SUM(coes_tax) AS taxes FROM " + SQLUtil.formatKind(sFParams, " contractestimations ") +
					              " WHERE coes_workcontractId = " + pmWorkContracts.getInt("woco_workcontractid");
					    	pmConn.doFetch(sql);
					    	if (pmConn.next()) {
					    		percAmount = FlexUtil.roundDouble(((pmConn.getDouble("amount") + pmConn.getDouble("taxes")) / pmWorkContracts.getDouble("woco_total")) * 100,2);
					    	}
					    %>
					    <%= HtmlUtil.formatReportCell(sFParams, percAmount + "%", BmFieldType.NUMBER) %>
					    <%	
					    	// Comentarios
					    	String comments = "";
					    	sql = " SELECT coes_descriptionotherexpenses AS comments FROM " + SQLUtil.formatKind(sFParams, " contractestimations ") +
					              " WHERE coes_workcontractId = " + pmWorkContracts.getInt("woco_workcontractid");
					    	pmConn.doFetch(sql);
					    	while (pmConn.next()) {
					    		comments += " " + pmConn.getString("contractestimations" , "comments");
					    	}
					    %>
					    <%= HtmlUtil.formatReportCell(sFParams, comments, BmFieldType.STRING) %>
					    <%
					    	//Estatus de la Obra
					    	bmoWorkContract.getStatus().setValue(pmWorkContracts.getString("workcontracts", "woco_status"));
					    %>
					    <%= HtmlUtil.formatReportCell(sFParams, bmoWorkContract.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING) %> 					    
			        </tr>
	    <%
	       suplId = pmWorkContracts.getInt("supl_supplierid");	
	       i++;
	    } // fin pmWorkContracts		
				
				
		%>
		<tr><td colspan="31">&nbsp;</td></tr>

		<tr class="reportCellEven reportCellCode">
			<% if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) { %>
				<td class="reportCellEven" colspan="8">&nbsp;</td>
			<% } else { %>
				<td class="reportCellEven" colspan="7">&nbsp;</td>
			<% } %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + wocoTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + downPaymentTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + amortizedTotal, BmFieldType.CURRENCY) %>
			<%= HtmlUtil.formatReportCell(sFParams, "" + toAmortizedTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + estimatedTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + amountPayTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + taxesTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + toEstimatedTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + otherTotal, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + warrantyFundTotal, BmFieldType.CURRENCY) %>
            <td class="reportCellEven" colspan="3">&nbsp;</td>
		</tr>
		<%
	}// FIN DEL CONTADOR

	%>

</table>    
     

	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% }
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	}// Fin de if(no carga datos)
	pmWorkContracts.close();
	pmConnCount.close();
	pmConn.close();
	%>
  </body>
</html>