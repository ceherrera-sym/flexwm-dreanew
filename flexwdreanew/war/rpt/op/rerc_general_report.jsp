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

<%@page import="com.flexwm.shared.op.BmoRequisitionReceipt"%>
<%@page import="com.flexwm.server.op.PmRequisitionReceipt"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes de Recibos de Ordenes de Compra";
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
    BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
    PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	
    String filters = "", sql = "", where = "" , whereSupl = "", sqlCurrency = "",
    		type = "", receiptDateStart = "", receiptDateEnd = "",  status = "", payment = "";
    			   
    int programId = 0, whSectionId = 0, supplierId = 0, quality = 0, punctuality = 0, service = 0, handling = 0, reliability = 0, companyId = 0, 
    		currencyId = 0, activeBudgets = 0;
    double  nowParity = 0, defaultParity = 0;
    boolean enableBudgets = false;
   	
   	// se agrega 2 columnas para presupuestos, para manejo de colspans
   	if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
   		enableBudgets = true;
   		activeBudgets = 2;
   	}
   	
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("rerc_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("rerc_supplierid"));
    if (request.getParameter("rerc_type") != null) type = request.getParameter("rerc_type");
    if (request.getParameter("rerc_receiptdate") != null) receiptDateStart = request.getParameter("rerc_receiptdate");
    if (request.getParameter("receiptDateEnd") != null) receiptDateEnd = request.getParameter("receiptDateEnd");
    if (request.getParameter("rerc_whsectionid") != null) whSectionId = Integer.parseInt(request.getParameter("rerc_whsectionid"));
    if (request.getParameter("rerc_quality") != null) quality = Integer.parseInt(request.getParameter("rerc_quality"));
    if (request.getParameter("rerc_punctuality") != null) punctuality = Integer.parseInt(request.getParameter("rerc_punctuality"));
    if (request.getParameter("rerc_service") != null) service = Integer.parseInt(request.getParameter("rerc_service"));
    if (request.getParameter("rerc_handling") != null) handling = Integer.parseInt(request.getParameter("rerc_handling"));
    if (request.getParameter("rerc_reliability") != null) reliability = Integer.parseInt(request.getParameter("rerc_reliability"));
    if (request.getParameter("rerc_status") != null) status = request.getParameter("rerc_status");    
    if (request.getParameter("rerc_payment") != null) payment = request.getParameter("rerc_payment");
    if (request.getParameter("reqi_companyid") != null) companyId = Integer.parseInt(request.getParameter("reqi_companyid"));    
    if (request.getParameter("rerc_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("rerc_currencyid"));
    
    // Filtros listados
    if (!type.equals("")) {
        where += " AND rerc_type = '" + type + "'";
        filters += "<i>Tipo Recibo: </i>" + request.getParameter("rerc_typeLabel") + ", ";
    }    
    
    if (supplierId > 0) {
    	whereSupl += " AND supl_supplierid = " + supplierId;
        filters += "<i>Proveedor: </i>" + request.getParameter("rerc_supplieridLabel") + ", ";
    }
    
    if (whSectionId > 0) {
    	where += " AND rerc_whsectionid = " + whSectionId;
    	filters += "<i>Secci&oacute;n Almacen: </i>" + request.getParameter("rerc_whsectionidLabel") + ", ";
    }
    
    if (!receiptDateStart.equals("")) {
        where += " AND rerc_receiptdate >= '" + receiptDateStart + " 00:00:00'";
        filters += "<i>F. Recibo: </i>" + receiptDateStart + ", ";
    }
    
    if (!receiptDateEnd.equals("")) {
        where += " AND rerc_receiptdate <= '" + receiptDateEnd + " 23:59:59'";
        filters += "<i>F. Recibo: </i>" + receiptDateEnd + ", ";
    }
    
    if (quality > 0) {
        where += " AND rerc_quality = " + quality ;
        filters += "<i>Calidad: </i>" + request.getParameter("rerc_qualityLabel") + ", ";
    }
    
    if (punctuality > 0) {
        where += " AND rerc_punctuality = " + punctuality ;
        filters += "<i>Puntualidad: </i>" + request.getParameter("rerc_punctualityLabel") + ", ";
    }
    
    if (service > 0) {
        where += " AND rerc_service = " + service ;
        filters += "<i>Servicio: </i>" + request.getParameter("rerc_serviceLabel") + ", ";
    }
    
    if (reliability > 0) {
        where += " AND rerc_reliability = " + reliability ;
        filters += "<i>Veracidad: </i>" + request.getParameter("rerc_reliabilityLabel") + ", ";
    }
    
    if (handling > 0) {
        where += " AND rerc_handling = " + handling ;
        filters += "<i>Manejo: </i>" + request.getParameter("rerc_handlingLabel") + ", ";
    }
    
    if (!status.equals("")) {
        where += SFServerUtil.parseFiltersToSql("rerc_status", status);
        filters += "<i>Estatus: </i>" + request.getParameter("rerc_statusLabel") + ", ";
    }
    
    if (!payment.equals("")) {
        where += SFServerUtil.parseFiltersToSql("rerc_payment", payment);
        filters += "<i>Pago: </i>" + request.getParameter("rerc_paymentLabel") + ", ";
    }   
    
    if (companyId > 0) {
        where += " AND reqi_companyid = " + companyId ;
        filters += "<i>Empresa: </i>" + request.getParameter("reqi_companyidLabel") + ", ";
    }
    
   	if (currencyId > 0) {
   		bmoCurrency = (BmoCurrency)pmCurrency.get(currencyId);   		
   		defaultParity = bmoCurrency.getParity().toDouble();
   		filters += "<i>Moneda: </i>" + request.getParameter("rerc_currencyidLabel") + " | <i>Tipo de Cambio: </i>" + defaultParity;
   	} else {
   		//Obtener la paridad de la moneda del sistema
//   		bmoCurrency = (BmoCurrency)pmCurrency.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getCurrencyId().toInteger());
//   		nowParity = bmoCurrency.getParity().toDouble();
//   		filters += "<i>Moneda: </i>" + bmoCurrency.getCode().toString() + " | " + bmoCurrency.getName().toString() + ", ";
   		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = "SELECT cure_currencyid, cure_code, cure_name FROM " + SQLUtil.formatKind(sFParams, " currencies ");
   	}
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmRequisitionReceipt(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
   	
   	PmConn pmRequisitionReceiptSupl = new PmConn(sFParams);
   	pmRequisitionReceiptSupl.open();
   
   	PmConn pmRequisitionReceipt = new PmConn(sFParams);
   	pmRequisitionReceipt.open();
    
   	PmConn pmCurrencyWhile =new PmConn(sFParams);
    pmCurrencyWhile.open();
    
    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
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

<table breqir="0" cellspacing="0" cellpading="0" width="100%">
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
			<b>Agrupado por:</b> <%= ((!(currencyId > 0)) ? "Moneda, Proveedor" : "Proveedor")%>
			<b>Valoracion Prov:</b>
            (1=P&eacute;simo&nbsp;&nbsp;
            2=Malo&nbsp;&nbsp;
            3=Regular&nbsp;&nbsp;
            4=Bueno&nbsp;&nbsp;
            5=Excelente)
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
        </td>
    </tr>
</table>
<br>
<table class="report">
	<%

		sql = " SELECT COUNT(rerc_requisitionreceiptid) as contador FROM " + SQLUtil.formatKind(sFParams, " requisitionreceipts  ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON(reqi_requisitionid = rerc_requisitionid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (rerc_currencyid = cure_currencyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid = reqi_companyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = rerc_supplierid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON(whse_whsectionid = rerc_whsectionid) " +
				" WHERE rerc_requisitionreceiptid > 0 " +
				where +
				" ORDER by rerc_supplierid ASC, rerc_receiptdate ASC, rerc_requisitionid ASC";
		int count =0;
		//ejecuto el sql DEL CONTADOR
		pmConnCount.doFetch(sql);
		if(pmConnCount.next())
			count=pmConnCount.getInt("contador");
		
		System.out.println("contador DE REGISTROS --> "+count);
	//if que muestra el mensajede error
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
			%>
			
					<%=messageTooLargeList %>
			<% 
		}else{
	if (!(currencyId > 0)) {
        int currencyIdWhile = 0, n = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			double amountSum = 0, totalSum = 0, ivaSum = 0,
					amountTotal = 0, totalTotal = 0, ivaTotal = 0,
					sumCalidad = 0, sumPuntualidad = 0, sumServicio = 0, sumVeracidad = 0, sumManejo = 0,
					promCalidad = 0, promPuntualidad = 0, promServicio = 0, promVeracidad = 0, promManejo = 0,
					sumTotalCalidad = 0, sumTotalPuntualidad = 0, sumTotalServicio = 0, sumTotalVeracidad = 0, sumTotalManejo = 0,
					promTotalCalidad = 0, promTotalPuntualidad = 0, promTotalServicio = 0, promTotalVeracidad = 0, promTotalManejo = 0,
					sumFila = 0, sumFilaPv = 0 , promFila = 0, promFilaPv = 0,
					sumTotalFilaPv = 0, sumPromTotal = 0, sumPromTotalGen = 0,
					sumProm = 0, sumPromPv = 0, sumPromGen = 0, sumTotalPromGen = 0;
			
			int i = 1, y = 0, suplId = 0, countSupl = 0;
		 	
			sql = " SELECT rerc_code, reqi_code, reqi_name, comp_name, rerc_receiptdate, rerc_quality, rerc_punctuality, rerc_service, rerc_reliability, rerc_handling, "+
					" rerc_type, rerc_status, rerc_payment, rerc_currencyid, rerc_currencyparity, rerc_amount, rerc_tax, rerc_total, " +
					" supl_supplierid, supl_code, supl_name, whse_name, cure_code" +
					" FROM " + SQLUtil.formatKind(sFParams, " requisitionreceipts  ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON(reqi_requisitionid = rerc_requisitionid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid = reqi_companyid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = rerc_supplierid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON(whse_whsectionid = rerc_whsectionid) " +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (rerc_currencyid = cure_currencyid) " +
					" WHERE rerc_requisitionreceiptid > 0 " +
					" AND rerc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
					whereSupl +
					where +
					" ORDER by rerc_supplierid ASC, rerc_receiptdate ASC, rerc_requisitionid ASC";
			
			
			pmRequisitionReceipt.doFetch(sql);
			while(pmRequisitionReceipt.next()) {
				
				if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
            		currencyIdWhile =  pmCurrencyWhile.getInt("currencies", "cure_currencyid");
            		y = 0;
            		%>
            		<tr>
	            		<td class="reportHeaderCellCenter" colspan="21">
	            			<%= HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name")) %>
	            		</td>
            		</tr>
            		<%
            	}
				
				bmoRequisitionReceipt.getType().setValue(pmRequisitionReceipt.getString("requisitionreceipts", "rerc_type"));
				bmoRequisitionReceipt.getStatus().setValue(pmRequisitionReceipt.getString("requisitionreceipts", "rerc_status"));
				bmoRequisitionReceipt.getPayment().setValue(pmRequisitionReceipt.getString("requisitionreceipts", "rerc_payment"));
				
				y++; i++;
				
				if (pmRequisitionReceipt.getInt("suppliers", "supl_supplierid") != suplId) {
					suplId = pmRequisitionReceipt.getInt("suppliers", "supl_supplierid");
		
					sql = " SELECT COUNT(rerc_requisitionreceiptid) as countSupl FROM " + SQLUtil.formatKind(sFParams, " requisitionreceipts  ") +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON(reqi_requisitionid = rerc_requisitionid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (rerc_currencyid = cure_currencyid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid = reqi_companyid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = rerc_supplierid) " +
							" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON(whse_whsectionid = rerc_whsectionid) " +
							" WHERE rerc_requisitionreceiptid > 0 " +
							" AND supl_supplierid = " + suplId +
							" AND rerc_currencyid =  " + pmCurrencyWhile.getInt("cure_currencyid") +
							where +
							" ORDER by rerc_supplierid ASC, rerc_receiptdate ASC, rerc_requisitionid ASC";
		
					pmRequisitionReceiptSupl.doFetch(sql);
					if (pmRequisitionReceiptSupl.next()) {
						countSupl = pmRequisitionReceiptSupl.getInt("countSupl");
					}
					%>
					<tr>
						<td colspan="21" class="reportGroupCell">
							<%= HtmlUtil.stringToHtml(pmRequisitionReceipt.getString("suppliers", "supl_code") + " " + pmRequisitionReceipt.getString("suppliers", "supl_name"))%>
						</td>
					</tr>
					<tr class="">
						<td class="reportHeaderCellCenter">#</td>
						<td class="reportHeaderCell">Clave ROC</td>
						<td class="reportHeaderCell">O.C.</td>
						<td class="reportHeaderCell">Empresa</td>
						<td class="reportHeaderCell">Tipo</td>
						<td class="reportHeaderCell">Fecha Recibo</td>
						<td class="reportHeaderCell">Secci&oacute;n Alm.</td>
						<td class="reportHeaderCell">Calidad</td>
						<td class="reportHeaderCell">Puntualidad</td>
						<td class="reportHeaderCell">Servicio</td>
						<td class="reportHeaderCell">Veracidad<br>Documentos</td>
						<td class="reportHeaderCell">Manejo y<br>Descargas</td>
						<td class="reportHeaderCell">Sumas</td>
						<td class="reportHeaderCell">Promedio</td>
						<td class="reportHeaderCell">Estatus</td>
						<td class="reportHeaderCell">Pago</td>
						<td class="reportHeaderCell">Moneda</td>
						<td class="reportHeaderCellCenter">Tipo de Cambio</td>
						<td class="reportHeaderCellRight">Monto</td>
						<td class="reportHeaderCellRight">IVA</td>
						<td class="reportHeaderCellRight">Total</td>
					</tr>
					<%
					i=1; 
					
					amountSum = 0; ivaSum = 0; totalSum = 0;
					sumCalidad = 0; sumPuntualidad = 0; sumServicio = 0; sumVeracidad = 0; sumManejo = 0; sumFilaPv = 0;
				}
				
				sumCalidad += pmRequisitionReceipt.getDouble("rerc_quality");
				sumPuntualidad += pmRequisitionReceipt.getDouble("rerc_punctuality");
				sumServicio += pmRequisitionReceipt.getDouble("rerc_service");
				sumVeracidad += pmRequisitionReceipt.getDouble("rerc_reliability");
				sumManejo += pmRequisitionReceipt.getDouble("rerc_handling");
				sumFila = pmRequisitionReceipt.getDouble("rerc_quality") + 
						pmRequisitionReceipt.getDouble("rerc_punctuality") +
						pmRequisitionReceipt.getDouble("rerc_service") +
						pmRequisitionReceipt.getDouble("rerc_reliability") +
						pmRequisitionReceipt.getDouble("rerc_handling");
				
				sumFilaPv += sumFila;
				
				promCalidad += pmRequisitionReceipt.getDouble("rerc_quality");
				promPuntualidad += pmRequisitionReceipt.getDouble("rerc_punctuality");
				promServicio += pmRequisitionReceipt.getDouble("rerc_service");
				promVeracidad += pmRequisitionReceipt.getDouble("rerc_reliability");
				promManejo += pmRequisitionReceipt.getDouble("rerc_handling");
				
				%>      
				<tr class="reportCellEven">
					<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_code"), BmFieldType.STRING) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitions", "reqi_code") + " " +pmRequisitionReceipt.getString("requisitions", "reqi_name"), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("companies", "comp_name"), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoRequisitionReceipt.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.STRING)) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_receiptdate"), BmFieldType.DATETIME) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("whsections", "whse_name"), BmFieldType.STRING)) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_quality"), BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_punctuality"), BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_service"), BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_reliability"), BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_handling"), BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumFila, BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + (sumFila/5), BmFieldType.NUMBER) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoRequisitionReceipt.getStatus().getSelectedOption().getLabeltoHtml(), BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoRequisitionReceipt.getPayment().getSelectedOption().getLabeltoHtml(), BmFieldType.STRING)) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("cure_code"), BmFieldType.CODE) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getDouble("rerc_currencyparity"), BmFieldType.NUMBER) %>
					<%
					double amount = pmRequisitionReceipt.getDouble("rerc_amount");
		          	double tax = pmRequisitionReceipt.getDouble("rerc_tax");
		          	double total = pmRequisitionReceipt.getDouble("rerc_total");
		      				          	
		          	amountSum += amount;
		    		ivaSum += tax;
		    		totalSum += total;
					%>
					<%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
					<%=HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
					<%=HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
				</tr>
				<%
				if (countSupl == i) {
					sumTotalCalidad += sumCalidad;
					sumTotalPuntualidad += sumPuntualidad;
					sumTotalServicio += sumServicio;
					sumTotalVeracidad += sumVeracidad;
					sumTotalManejo += sumManejo;
					sumProm = 0;
					sumPromPv = 0;
					sumPromPv = (sumFilaPv/5);
					sumTotalFilaPv += sumFilaPv;
					sumTotalPromGen += sumPromPv;
					
					%>
					<!-- ---------------Sumas-------------- -->
					<tr class="reportCellEven reportCellNumber">
						<td colspan="7" align="right">&nbsp;<b>Sumas:&nbsp;&nbsp;</b></td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumCalidad) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumPuntualidad) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumServicio) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumVeracidad) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumManejo) %></b>
						</td>
						<td class="reportCellNumber">
							<b><%= SFServerUtil.roundCurrencyDecimals(sumFilaPv) %></b>
						</td>
						<td class="reportCellNumber">
							<b><%= SFServerUtil.roundCurrencyDecimals(sumPromPv) %></b>				 	   
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<!-- ---------------Promedio-------------- -->
					<%
					sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumCalidad/countSupl));
					sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumPuntualidad/countSupl));
					sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumServicio/countSupl));
					sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumVeracidad/countSupl));
					sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumManejo/countSupl));
		
					sumPromGen += sumProm;
		
					%>
					<tr class="reportCellEven reportCellCode">
						<td colspan="7" align="right">&nbsp;<b>Promedio:&nbsp;&nbsp;</b></td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumCalidad/countSupl) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumPuntualidad/countSupl) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumServicio/countSupl) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumVeracidad/countSupl) %></b>
						</td>
						<td class="reportCellNumber"> 
							<b><%= SFServerUtil.roundCurrencyDecimals(sumManejo/countSupl) %></b>
						</td>
						<td class="reportCellNumber">
							<b><%= SFServerUtil.roundCurrencyDecimals(sumProm)%></b>
						</td>
						<td class="reportCellNumber">
							<b>
								<%= SFServerUtil.roundCurrencyDecimals(  ((sumCalidad/countSupl) + (sumPuntualidad/countSupl) + (sumServicio/countSupl) + (sumVeracidad/countSupl) + (sumManejo/countSupl)) / 5 )%>
							</b>
						</td>
						<td class="reportCellNumber">&nbsp;</td>
						<td class="reportCellNumber">&nbsp;</td>
						<td class="reportCellNumber">&nbsp;</td>
						<td class="reportCellNumber">&nbsp;</td>
						<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + ivaSum, BmFieldType.CURRENCY) %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
					</tr>
					<%  
					amountTotal += amountSum;
					ivaTotal += ivaSum;
					totalTotal += totalSum;
				}
			} //pmRequisitionReceipt
		    %>   
		    <tr><td colspan="21">&nbsp;</td></tr>
		    
		 	<!-- ---------------Sumas-------------- -->
		 	<%	if (y > 0) { %>
				    <tr class="reportCellEven reportCellCode">
				        <td>&nbsp;</td>
				        <td colspan="6" align="right">&nbsp;<b>&nbsp;&nbsp;Total Sumas:</b></td>
				        <td class="reportCellNumber">
				        	<b>
				    	<% 		if (y==0) { %>
				        			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				        	<% 	} else { %>
					        		<%= SFServerUtil.roundCurrencyDecimals(sumTotalCalidad) %></b>
				        	<% 	}%>
				        	</b>
				    	</td>
				        <td class="reportCellNumber"> 
				        	<b>
							<% 	if (y==0) { %>
									<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
							<% 	} else { %>
						    		<%= SFServerUtil.roundCurrencyDecimals(sumTotalPuntualidad) %></b>
							<% 	}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber">
				        	<b>
				    	<% 		if (y==0) { %>
				        			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 		} else { %>
				        			<%= SFServerUtil.roundCurrencyDecimals(sumTotalServicio) %></b>
				    	<% 		}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber"> 
				        	<b>
					    	<% 	if (y==0) { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					    	<% 	} else { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(sumTotalVeracidad) %></b>
					    	<% 	}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber"> 
				        	<b>
					    	<% 	if (y==0) { %>
						        		<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					    	<% 	} else {%>
						        		<%= SFServerUtil.roundCurrencyDecimals(sumTotalManejo) %></b>
					    	<% 	}%>
				    		</b>
				        </td>
				        <td class="reportCellNumber">
						 	<b>
					 			<%= SFServerUtil.roundCurrencyDecimals(sumTotalFilaPv)%>
						 	</b>
					 	</td>
					 	<td class="reportCellNumber">
						 	<b>
				 				<%= SFServerUtil.roundCurrencyDecimals(sumTotalPromGen)%>
						 	</b>
					 	</td>
				        <td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
				
				    </tr>
				    <!-- ---------------Promedio-------------- -->
				    <%
				        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promCalidad/y));
				        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promPuntualidad/y));
				        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promServicio/y));
				        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promVeracidad/y));
				        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promManejo/y));
				        
				        sumPromTotalGen += sumPromTotal;
				    %>
				    <tr class="reportCellEven reportCellCode">
				    	<%=HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
				    	
				        <td colspan="6" align="right">&nbsp;<b>&nbsp;&nbsp;Total Promedio:</b></td>
				        <td class="reportCellNumber">
				        	<b>
					    	<% 	if (y==0) { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
						<% 		} else { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(promCalidad/y) %></b>
					    	<% 	} %>
				        	</b>
				        	</td>
				        <td class="reportCellNumber"> 
				        	<b>
					    	<% 	if (y==0) { %>
									<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					    	<% 	} else { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(promPuntualidad/y) %></b>
					    	<% 	}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber"> 
				        	<b>
					    	<% 	if (y==0) { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					    	<% 	} else {%>
					        		<%= SFServerUtil.roundCurrencyDecimals(promServicio/y) %></b>
					    	<% 	}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber"> 
				        	<b>
					    	<% 	if (y==0) { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					    	<% 	} else {%>
					    			<%= SFServerUtil.roundCurrencyDecimals(promVeracidad/y) %></b>
					    	<% 	}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber"> 
				        	<b>
					    	<% 	if (y==0) { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					    	<% 	} else { %>
					    			<%= SFServerUtil.roundCurrencyDecimals(promManejo/y) %></b>
					    	<% 	}%>
				        	</b>
				        </td>
				        <td class="reportCellNumber">
						 	<b>
					<%			if (sumPromTotalGen > 0) { %>
				 					<%= SFServerUtil.roundCurrencyDecimals(sumPromTotalGen) %>
				 		<% 		} else { %>
					 				<%= SFServerUtil.roundCurrencyDecimals(0.0)%>
					 		<%	}%>
						 	</b>
					 	</td>
					 	<td class="reportCellNumber">
						 	<b>
					 			<% double  promToltSum = 0;
						 		promToltSum = ((promCalidad/y) + (promPuntualidad/y) + (promServicio/y) + (promVeracidad/y) + (promManejo/y) ) / 5 ;
						 		%>
					 		<% 	if (promToltSum > 0) { %>
					 				<%= SFServerUtil.roundCurrencyDecimals(promToltSum) %>
					 		<% 	} else { %>
					 				<%= SFServerUtil.roundCurrencyDecimals(0.0)%>
					 		<%	} %>
					 		</b>
					 	</td>
					 	
				        <td class="reportCellNumber">&nbsp;</td>
					 	<td class="reportCellNumber">&nbsp;</td>
				        <td class="reportCellNumber">&nbsp;</td>
				        <td class="reportCellNumber">&nbsp;</td>
				        <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
				        <%= HtmlUtil.formatReportCell(sFParams, "" + ivaTotal, BmFieldType.CURRENCY) %>
				        <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
			        </tr>
	        <%	}%>
			<tr><td colspan="21">&nbsp;</td></tr>
        <%
		} // Fin pmCurrencyWhile
	} // Fin de no existe moneda
	// Existe moneda destino
	else {
		double amountSum = 0, totalSum = 0, ivaSum = 0,
				amountTotal = 0, totalTotal = 0, ivaTotal = 0,
				sumCalidad = 0, sumPuntualidad = 0, sumServicio = 0, sumVeracidad = 0, sumManejo = 0,
				promCalidad = 0, promPuntualidad = 0, promServicio = 0, promVeracidad = 0, promManejo = 0,
				sumTotalCalidad = 0, sumTotalPuntualidad = 0, sumTotalServicio = 0, sumTotalVeracidad = 0, sumTotalManejo = 0,
				promTotalCalidad = 0, promTotalPuntualidad = 0, promTotalServicio = 0, promTotalVeracidad = 0, promTotalManejo = 0,
				sumFila = 0, sumFilaPv = 0 , promFila = 0, promFilaPv = 0,
				sumTotalFilaPv = 0, sumPromTotal = 0, sumPromTotalGen = 0,
				sumProm = 0, sumPromPv = 0, sumPromGen = 0, sumTotalPromGen = 0;
		
		int i = 1, y = 0, suplId = 0, countSupl = 0;
	 	
		sql = " SELECT rerc_code, reqi_code, reqi_name, comp_name, rerc_receiptdate, rerc_quality, rerc_punctuality, rerc_service, rerc_reliability, rerc_handling, "+
				" rerc_type, rerc_status, rerc_payment, rerc_currencyid, rerc_currencyparity, rerc_amount, rerc_tax, rerc_total, " +
				" supl_supplierid, supl_code, supl_name, whse_name, cure_code" +
				" FROM " + SQLUtil.formatKind(sFParams, " requisitionreceipts  ") +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON(reqi_requisitionid = rerc_requisitionid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid = reqi_companyid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = rerc_supplierid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON(whse_whsectionid = rerc_whsectionid) " +
				" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (rerc_currencyid = cure_currencyid) " +
				" WHERE rerc_requisitionreceiptid > 0 " +
				where + whereSupl +
				" ORDER by rerc_supplierid ASC, rerc_receiptdate ASC, rerc_requisitionid ASC";
		
		
		pmRequisitionReceipt.doFetch(sql);
		while(pmRequisitionReceipt.next()) {
			bmoRequisitionReceipt.getType().setValue(pmRequisitionReceipt.getString("requisitionreceipts", "rerc_type"));
			bmoRequisitionReceipt.getStatus().setValue(pmRequisitionReceipt.getString("requisitionreceipts", "rerc_status"));
			bmoRequisitionReceipt.getPayment().setValue(pmRequisitionReceipt.getString("requisitionreceipts", "rerc_payment"));
			
			y++; i++;
			
			if (pmRequisitionReceipt.getInt("suppliers", "supl_supplierid") != suplId) {
				suplId = pmRequisitionReceipt.getInt("suppliers", "supl_supplierid");
	
				sql = " SELECT COUNT(rerc_requisitionreceiptid) as countSupl FROM " + SQLUtil.formatKind(sFParams, " requisitionreceipts  ") +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitions")+" ON(reqi_requisitionid = rerc_requisitionid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on (rerc_currencyid = cure_currencyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON(comp_companyid = reqi_companyid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON(supl_supplierid = rerc_supplierid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " whsections")+" ON(whse_whsectionid = rerc_whsectionid) " +
						" WHERE rerc_requisitionreceiptid > 0 " +
						" AND supl_supplierid = " + suplId +
						where +
						" ORDER by rerc_supplierid ASC, rerc_receiptdate ASC, rerc_requisitionid ASC";
	
				pmRequisitionReceiptSupl.doFetch(sql);
				if (pmRequisitionReceiptSupl.next()) {
					countSupl = pmRequisitionReceiptSupl.getInt("countSupl");
				}
				%>
				<tr>
					<td colspan="21" class="reportGroupCell">
						<%= HtmlUtil.stringToHtml(pmRequisitionReceipt.getString("suppliers", "supl_code") + " " + pmRequisitionReceipt.getString("suppliers", "supl_name"))%>
					</td>
				</tr>
				<tr class="">
					<td class="reportHeaderCellCenter">#</td>
					<td class="reportHeaderCell">Clave ROC</td>
					<td class="reportHeaderCell">O.C.</td>
					<td class="reportHeaderCell">Empresa</td>
					<td class="reportHeaderCell">Tipo</td>
					<td class="reportHeaderCell">Fecha Recibo</td>
					<td class="reportHeaderCell">Secci&oacute;n Alm.</td>
					<td class="reportHeaderCell">Calidad</td>
					<td class="reportHeaderCell">Puntualidad</td>
					<td class="reportHeaderCell">Servicio</td>
					<td class="reportHeaderCell">Veracidad<br>Documentos</td>
					<td class="reportHeaderCell">Manejo y<br>Descargas</td>
					<td class="reportHeaderCell">Sumas</td>
					<td class="reportHeaderCell">Promedio</td>
					<td class="reportHeaderCell">Estatus</td>
					<td class="reportHeaderCell">Pago</td>
					<td class="reportHeaderCell">Moneda</td>
					<td class="reportHeaderCellCenter">Tipo de Cambio</td>
					<td class="reportHeaderCellRight">Monto</td>
					<td class="reportHeaderCellRight">IVA</td>
					<td class="reportHeaderCellRight">Total</td>
				</tr>
				<%
				i=1; 
				
				amountSum = 0; ivaSum = 0; totalSum = 0;
				sumCalidad = 0; sumPuntualidad = 0; sumServicio = 0; sumVeracidad = 0; sumManejo = 0; sumFilaPv = 0;
			}
			
			sumCalidad += pmRequisitionReceipt.getDouble("rerc_quality");
			sumPuntualidad += pmRequisitionReceipt.getDouble("rerc_punctuality");
			sumServicio += pmRequisitionReceipt.getDouble("rerc_service");
			sumVeracidad += pmRequisitionReceipt.getDouble("rerc_reliability");
			sumManejo += pmRequisitionReceipt.getDouble("rerc_handling");
			sumFila = pmRequisitionReceipt.getDouble("rerc_quality") + 
					pmRequisitionReceipt.getDouble("rerc_punctuality") +
					pmRequisitionReceipt.getDouble("rerc_service") +
					pmRequisitionReceipt.getDouble("rerc_reliability") +
					pmRequisitionReceipt.getDouble("rerc_handling");
			
			sumFilaPv += sumFila;
			
			promCalidad += pmRequisitionReceipt.getDouble("rerc_quality");
			promPuntualidad += pmRequisitionReceipt.getDouble("rerc_punctuality");
			promServicio += pmRequisitionReceipt.getDouble("rerc_service");
			promVeracidad += pmRequisitionReceipt.getDouble("rerc_reliability");
			promManejo += pmRequisitionReceipt.getDouble("rerc_handling");
			
			%>      
			<tr class="reportCellEven">
				<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_code"), BmFieldType.STRING) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitions", "reqi_code") + " " +pmRequisitionReceipt.getString("requisitions", "reqi_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("companies", "comp_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoRequisitionReceipt.getType().getSelectedOption().getLabeltoHtml(), BmFieldType.STRING)) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_receiptdate"), BmFieldType.DATETIME) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("whsections", "whse_name"), BmFieldType.STRING)) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_quality"), BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_punctuality"), BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_service"), BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_reliability"), BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("requisitionreceipts", "rerc_handling"), BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + sumFila, BmFieldType.NUMBER) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + (sumFila/5), BmFieldType.NUMBER) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoRequisitionReceipt.getStatus().getSelectedOption().getLabeltoHtml(), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + bmoRequisitionReceipt.getPayment().getSelectedOption().getLabeltoHtml(), BmFieldType.STRING)) %>
				<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getString("cure_code"), BmFieldType.CODE)) %>
				<%= HtmlUtil.formatReportCell(sFParams, "" + pmRequisitionReceipt.getDouble("rerc_currencyparity"), BmFieldType.NUMBER) %>
				<%
				double amount = pmRequisitionReceipt.getDouble("rerc_amount");
	          	double tax = pmRequisitionReceipt.getDouble("rerc_tax");
	          	double total = pmRequisitionReceipt.getDouble("rerc_total");
	      		
	          	//Conversion a la moneda destino(seleccion del filtro)
	          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	          	double parityOrigin = 0, parityDestiny = 0;
	          	currencyIdOrigin = pmRequisitionReceipt.getInt("rerc_currencyid");
	          	parityOrigin = pmRequisitionReceipt.getDouble("rerc_currencyparity");
	          	currencyIdDestiny = currencyId;
	          	parityDestiny = defaultParity;
		        
	          	amount = pmCurrencyExchange.currencyExchange(amount, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	tax = pmCurrencyExchange.currencyExchange(tax, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	total = pmCurrencyExchange.currencyExchange(total, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	
	          	amountSum += amount;
	    		ivaSum += tax;
	    		totalSum += total;
				%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + amount, BmFieldType.CURRENCY) %>
				<%=HtmlUtil.formatReportCell(sFParams, "" + tax, BmFieldType.CURRENCY) %>
				<%=HtmlUtil.formatReportCell(sFParams, "" + total, BmFieldType.CURRENCY) %>
			</tr>
			<%
			if (countSupl == i) {
				sumTotalCalidad += sumCalidad;
				sumTotalPuntualidad += sumPuntualidad;
				sumTotalServicio += sumServicio;
				sumTotalVeracidad += sumVeracidad;
				sumTotalManejo += sumManejo;
				sumProm = 0;
				sumPromPv = 0;
				sumPromPv = (sumFilaPv/5);
				sumTotalFilaPv += sumFilaPv;
				sumTotalPromGen += sumPromPv;
				
				%>
				<!-- ---------------Sumas-------------- -->
				<tr class="reportCellEven reportCellNumber">
					<td colspan="7" align="right">&nbsp;<b>Sumas:&nbsp;&nbsp;</b></td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumCalidad) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumPuntualidad) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumServicio) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumVeracidad) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumManejo) %></b>
					</td>
					<td class="reportCellNumber">
						<b><%= SFServerUtil.roundCurrencyDecimals(sumFilaPv) %></b>
					</td>
					<td class="reportCellNumber">
						<b><%= SFServerUtil.roundCurrencyDecimals(sumPromPv) %></b>				 	   
					</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<!-- ---------------Promedio-------------- -->
				<%
				sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumCalidad/countSupl));
				sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumPuntualidad/countSupl));
				sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumServicio/countSupl));
				sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumVeracidad/countSupl));
				sumProm += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(sumManejo/countSupl));
	
				sumPromGen += sumProm;
	
				%>
				<tr class="reportCellEven reportCellCode">
					<td colspan="7" align="right">&nbsp;<b>Promedio:&nbsp;&nbsp;</b></td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumCalidad/countSupl) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumPuntualidad/countSupl) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumServicio/countSupl) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumVeracidad/countSupl) %></b>
					</td>
					<td class="reportCellNumber"> 
						<b><%= SFServerUtil.roundCurrencyDecimals(sumManejo/countSupl) %></b>
					</td>
					<td class="reportCellNumber">
						<b><%= SFServerUtil.roundCurrencyDecimals(sumProm)%></b>
					</td>
					<td class="reportCellNumber">
						<b>
							<%= SFServerUtil.roundCurrencyDecimals(  ((sumCalidad/countSupl) + (sumPuntualidad/countSupl) + (sumServicio/countSupl) + (sumVeracidad/countSupl) + (sumManejo/countSupl)) / 5 )%>
						</b>
					</td>
					<td class="reportCellNumber">&nbsp;</td>
					<td class="reportCellNumber">&nbsp;</td>
					<td class="reportCellNumber">&nbsp;</td>
					<td class="reportCellNumber">&nbsp;</td>
					<%= HtmlUtil.formatReportCell(sFParams, "" + amountSum, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + ivaSum, BmFieldType.CURRENCY) %>
					<%= HtmlUtil.formatReportCell(sFParams, "" + totalSum, BmFieldType.CURRENCY) %>
				</tr>
				<%  
				amountTotal += amountSum;
				ivaTotal += ivaSum;
				totalTotal += totalSum;
			}
		} //pmRequisitionReceipt
	    %>   
	    <tr><td colspan="21">&nbsp;</td></tr>
	    
	 	<!-- ---------------Sumas-------------- -->
	 	<%	if (y > 0) { %>
			    <tr class="reportCellEven reportCellCode">
			        <td>&nbsp;</td>
			        <td colspan="6" align="right">&nbsp;<b>&nbsp;&nbsp;Total Sumas:</b></td>
			        <td class="reportCellNumber">
			        	<b>
			    	<% 		if (y==0) { %>
			        			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
			        	<% 	} else { %>
				        		<%= SFServerUtil.roundCurrencyDecimals(sumTotalCalidad) %></b>
			        	<% 	}%>
			        	</b>
			    	</td>
			        <td class="reportCellNumber"> 
			        	<b>
						<% 	if (y==0) { %>
								<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
						<% 	} else { %>
					    		<%= SFServerUtil.roundCurrencyDecimals(sumTotalPuntualidad) %></b>
						<% 	}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber">
			        	<b>
			    	<% 		if (y==0) { %>
			        			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
			    	<% 		} else { %>
			        			<%= SFServerUtil.roundCurrencyDecimals(sumTotalServicio) %></b>
			    	<% 		}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber"> 
			        	<b>
				    	<% 	if (y==0) { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 	} else { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(sumTotalVeracidad) %></b>
				    	<% 	}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber"> 
			        	<b>
				    	<% 	if (y==0) { %>
					        		<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 	} else {%>
					        		<%= SFServerUtil.roundCurrencyDecimals(sumTotalManejo) %></b>
				    	<% 	}%>
			    		</b>
			        </td>
			        <td class="reportCellNumber">
					 	<b>
				 			<%= SFServerUtil.roundCurrencyDecimals(sumTotalFilaPv)%>
					 	</b>
				 	</td>
				 	<td class="reportCellNumber">
					 	<b>
			 				<%= SFServerUtil.roundCurrencyDecimals(sumTotalPromGen)%>
					 	</b>
				 	</td>
			        <td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
			
			    </tr>
			    <!-- ---------------Promedio-------------- -->
			    <%
			        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promCalidad/y));
			        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promPuntualidad/y));
			        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promServicio/y));
			        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promVeracidad/y));
			        sumPromTotal += SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(promManejo/y));
			        
			        sumPromTotalGen += sumPromTotal;
			    %>
			    <tr class="reportCellEven reportCellCode">
			    	<%=HtmlUtil.formatReportCell(sFParams, "" + y, BmFieldType.NUMBER) %>
			    	
			        <td colspan="6" align="right">&nbsp;<b>&nbsp;&nbsp;Total Promedio:</b></td>
			        <td class="reportCellNumber">
			        	<b>
				    	<% 	if (y==0) { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
					<% 		} else { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(promCalidad/y) %></b>
				    	<% 	} %>
			        	</b>
			        	</td>
			        <td class="reportCellNumber"> 
			        	<b>
				    	<% 	if (y==0) { %>
								<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 	} else { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(promPuntualidad/y) %></b>
				    	<% 	}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber"> 
			        	<b>
				    	<% 	if (y==0) { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 	} else {%>
				        		<%= SFServerUtil.roundCurrencyDecimals(promServicio/y) %></b>
				    	<% 	}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber"> 
			        	<b>
				    	<% 	if (y==0) { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 	} else {%>
				    			<%= SFServerUtil.roundCurrencyDecimals(promVeracidad/y) %></b>
				    	<% 	}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber"> 
			        	<b>
				    	<% 	if (y==0) { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(0.0) %></b>
				    	<% 	} else { %>
				    			<%= SFServerUtil.roundCurrencyDecimals(promManejo/y) %></b>
				    	<% 	}%>
			        	</b>
			        </td>
			        <td class="reportCellNumber">
					 	<b>
				<%			if (sumPromTotalGen > 0) { %>
			 					<%= SFServerUtil.roundCurrencyDecimals(sumPromTotalGen) %>
			 		<% 		} else { %>
				 				<%= SFServerUtil.roundCurrencyDecimals(0.0)%>
				 		<%	}%>
					 	</b>
				 	</td>
				 	<td class="reportCellNumber">
					 	<b>
				 			<% double  promToltSum = 0;
					 		promToltSum = ((promCalidad/y) + (promPuntualidad/y) + (promServicio/y) + (promVeracidad/y) + (promManejo/y) ) / 5 ;
					 		%>
				 		<% 	if (promToltSum > 0) { %>
				 				<%= SFServerUtil.roundCurrencyDecimals(promToltSum) %>
				 		<% 	} else { %>
				 				<%= SFServerUtil.roundCurrencyDecimals(0.0)%>
				 		<%	} %>
				 		</b>
				 	</td>
				 	
			        <td class="reportCellNumber">&nbsp;</td>
				 	<td class="reportCellNumber">&nbsp;</td>
			        <td class="reportCellNumber">&nbsp;</td>
			        <td class="reportCellNumber">&nbsp;</td>
			        <%= HtmlUtil.formatReportCell(sFParams, "" + amountTotal, BmFieldType.CURRENCY) %>
			        <%= HtmlUtil.formatReportCell(sFParams, "" + ivaTotal, BmFieldType.CURRENCY) %>
			        <%= HtmlUtil.formatReportCell(sFParams, "" + totalTotal, BmFieldType.CURRENCY) %>
			    </tr>
    <%	}	%>
<% 	} %>
</table>
<%
		}// FIN DEL CONTADOR
	}// Fin de if(no carga datos)
	pmConnCount.close();
    pmCurrencyWhile.close();
	pmRequisitionReceipt.close();
	pmRequisitionReceiptSupl.close();

	%>
	<% if (print.equals("1")) { %>
		<script>
			//window.print();
		</script>
	<% } %>
	</body>
</html>
