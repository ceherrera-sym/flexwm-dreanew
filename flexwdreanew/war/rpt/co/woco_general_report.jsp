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
	    		sql += " WHERE woco_workcontractid > 0 " +
	    		where + 
	    		" ORDER BY dvph_code ASC, ";
	    		 if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
	    			sql += "bgty_name ASC,";
	    			 
	    		 }
	    		sql +=" supl_code ASC, work_code ASC ";
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
    		sql += " WHERE woco_workcontractid > 0 " +
    		where + 
    		" ORDER BY dvph_code ASC, ";
    		 if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
    			sql += "bgty_name ASC,";
    			 
    		 }
    		sql +=" supl_code ASC, work_code ASC ";
    
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
				<td class="reportHeaderCell">Estatus<br>Contrato</td>
				<td class="reportHeaderCell">Estatus Pago<br>Contrato</td>
				
				<td class="reportHeaderCell">Obra</td>
				<td class="reportHeaderCell">Estatus<br>Obra</td>
				<td class="reportHeaderCell">Empresa</td>
				<td class="reportHeaderCell">Contratista</td>
				
				<td class="reportHeaderCell">Fecha Inicio</td>
				<td class="reportHeaderCell">Fecha T&eacute;rmino</td>
				<td class="reportHeaderCell">Fecha Pago<br>Anticipo</td>
				<td class="reportHeaderCell">Fecha de<br>Contrato</td>
				
				<td class="reportHeaderCell">Cantidad<br>Paquetes</td>
				<td class="reportHeaderCell">&#191Aplica<br>IVA&#63;</td>
				<td class="reportHeaderCell">&#191Aplica<br>Sanci&oacute;n&#63;</td>
				
				<td class="reportHeaderCellRight">&#37;Anticipo</td>
				<td class="reportHeaderCellRight">Monto<br>Anticipo</td>
				
				<td class="reportHeaderCellRight">&#37;Fondo de<br>Garant&iacute;a</td>
				<td class="reportHeaderCellRight">Fondo de<br>Garant&iacute;a</td>
				<td class="reportHeaderCellRight">Sanci&oacute;n<br>Diaria</td>

				
				<td class="reportHeaderCellRight">Sub-Total</td>
				<td class="reportHeaderCellRight">Monto IVA</td>
				<td class="reportHeaderCellRight">Total</td>
				<td class="reportHeaderCellRight">Provisionado</td>
				<td class="reportHeaderCellRight">Pagado</td>
				<td class="reportHeaderCellRight">Saldo</td>

			</tr>
			<%
				int i = 1;
				double  downPaymentSum = 0, guaranteeFundSum = 0, dailySanctionSum = 0, subTotalSum = 0, taxSum = 0, totaRealSum = 0,
						provisionadoSum = 0, pagosSum = 0, saldoSum = 0;
				pmWorkContracts.doFetch(sql);
				while (pmWorkContracts.next()) {
					bmoWork.getStatus().setValue(pmWorkContracts.getString("works", "work_status"));
					bmoWorkContract.getStatus().setValue(pmWorkContracts.getString("workcontracts", "woco_status"));
					bmoWorkContract.getPaymentStatus().setValue(pmWorkContracts.getString("workcontracts", "woco_paymentstatus"));
					
					downPaymentSum += pmWorkContracts.getDouble("woco_downpayment");
					guaranteeFundSum += pmWorkContracts.getDouble("woco_guaranteefund");
					dailySanctionSum += pmWorkContracts.getDouble("woco_dailysanction");
					subTotalSum += pmWorkContracts.getDouble("woco_subtotal");
					taxSum += pmWorkContracts.getDouble("woco_tax");
					totaRealSum += pmWorkContracts.getDouble("woco_totalreal");

			%>
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
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoWorkContract.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoWorkContract.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("works", "work_code") + " | " +pmWorkContracts.getString("works", "work_name"), BmFieldType.STRING)) %>
			        	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoWork.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
			        	
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("companies", "comp_code"), BmFieldType.CODE) %>
		                <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("suppliers", "supl_code") + " | " + pmWorkContracts.getString("suppliers", "supl_name"), BmFieldType.STRING)) %>
		                
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_startdate"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_enddate"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_paymentdate"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_datecontract"), BmFieldType.DATE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_quantity"), BmFieldType.NUMBER) %>
		                <%= HtmlUtil.formatReportCell(sFParams, ((pmWorkContracts.getBoolean("work_hastax")) ? "Si" : "No"), BmFieldType.BOOLEAN) %>
		                <%= HtmlUtil.formatReportCell(sFParams, ((pmWorkContracts.getBoolean("work_hassanction")) ? "Si" : "No"), BmFieldType.BOOLEAN) %>
		                
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_percentdownpayment"), BmFieldType.PERCENTAGE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_downpayment"), BmFieldType.CURRENCY) %>

		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_percentguaranteefund"), BmFieldType.PERCENTAGE) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_guaranteefund"), BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_dailysanction"), BmFieldType.CURRENCY) %>

		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_subtotal"), BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_tax"), BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, pmWorkContracts.getString("workcontracts", "woco_totalreal"), BmFieldType.CURRENCY) %>
		                
		                <%
	                		//System.out.println("sqlMB: "+pmWorkContracts.getString("workcontracts", "woco_workcontractid") +
	                			//" " +pmWorkContracts.getString("workcontracts", "woco_name"));

		                	double provisionado = 0;
		                	String sqlOC = "SELECT SUM(reqi_total) as provisionado FROM " + SQLUtil.formatKind(sFParams, " requisitions ") + 
				                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " contractestimations")+" ON(coes_contractestimationid = reqi_contractestimationid) " +
				                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " workcontracts")+" ON(woco_workcontractid = coes_workcontractid) " +
			                		 		" WHERE reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED  + "' " +
			                		 		" AND woco_workcontractid = " + pmWorkContracts.getInt("workcontracts", "woco_workcontractid");
		                	
		                	//System.out.println("sqlOC: "+sqlOC);

			                pmConn.doFetch(sqlOC);
			                if (pmConn.next()) provisionado = pmConn.getDouble("provisionado");
			                
			                provisionadoSum += provisionado;
		                %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + provisionado, BmFieldType.CURRENCY) %>
		                <%
		                	double pagos = 0;
			                sqlOC = "SELECT SUM(reqi_payments) as pagos FROM " + SQLUtil.formatKind(sFParams, " requisitions ") + 
		                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " contractestimations")+" ON(coes_contractestimationid = reqi_contractestimationid) " +
		                			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " workcontracts")+" ON(woco_workcontractid = coes_workcontractid) " +
	                		 		" WHERE reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED  + "' " +
									" AND reqi_paymentstatus = '" + BmoRequisition.PAYMENTSTATUS_TOTAL + "'  " +
	                		 		" AND woco_workcontractid = " + pmWorkContracts.getInt("workcontracts", "woco_workcontractid");
	            	
			            	//System.out.println("sqlOC: "+sqlOC);
			
			                pmConn.doFetch(sqlOC);
			                if (pmConn.next()) pagos = pmConn.getDouble("pagos");
			                
			                pagosSum += pagos;
			                
			                
			                saldoSum += pmWorkContracts.getDouble("woco_totalreal") - pagos;
		                %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + pagos, BmFieldType.CURRENCY) %>
		                <%= HtmlUtil.formatReportCell(sFParams, "" + (pmWorkContracts.getDouble("woco_totalreal") - pagos), BmFieldType.CURRENCY) %>
		                
			        </tr>
	    <%
	       i++;
	    } // fin pmWorkContracts		
				
				
		%>
		<tr><td colspan="31">&nbsp;</td></tr>

		<tr class="reportCellEven reportCellCode">
			<% if(((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) { %>
				<td class="reportCellEven" colspan="21">&nbsp;</td>
			<% } else { %>
				<td class="reportCellEven" colspan="19">&nbsp;</td>
			<% } %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + downPaymentSum, BmFieldType.CURRENCY) %>
            <td class="reportCellEven" align="center">-</td>            
            <%= HtmlUtil.formatReportCell(sFParams, "" + guaranteeFundSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + dailySanctionSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + subTotalSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + taxSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + totaRealSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + provisionadoSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + pagosSum, BmFieldType.CURRENCY) %>
            <%= HtmlUtil.formatReportCell(sFParams, "" + saldoSum, BmFieldType.CURRENCY) %>
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
	pmConn.close();
	pmConnCount.close();
	%>
  </body>
</html>