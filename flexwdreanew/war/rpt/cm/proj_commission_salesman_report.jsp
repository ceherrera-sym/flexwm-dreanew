<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Javier Alberto Hernandez
 * @version 2013-10
 */ -->
<%@page import="java.sql.Types"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionType"%>
<%@page import="com.flexwm.shared.op.BmoSupplier"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import= "com.flexwm.server.cm.PmProject"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="/inc/login.jsp" %>

<%

//Inicializar variables
	String title = "Comision Ejecutivos";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
	
	BmoProject bmoProject = new BmoProject();
		
	String sql = "", sqlRacc = "", where = "",groupFilter = "", filters = "";
	String startDate = "", endDate = "", startDateCreate = "", endDateCreate = "", whereBkmvDueDateStart = "", whereBkmvDueDateEnd = "", startDueDate = "", endDueDate = "";
	String status = "", paymentStatus = "";
	int wflowTypeId = 0, projectId = 0, areaId = 0;
   	int wflowCategoryId = 0;
   	int venueId = 0;
   	int userId = 0;
   	int customerId = 0;
   	int referralId = 0;
   	int wflowPhaseId = 0;   	
	int programId = 0;
	int countProject = 0;
	
	// Obtener parametros
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wfty_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfty_wflowcategoryid"));
	if (request.getParameter("wflw_wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflw_wflowtypeid"));
	if (request.getParameter("wflw_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
	if (request.getParameter("proj_venueid") != null) venueId = Integer.parseInt(request.getParameter("proj_venueid"));   
	if (request.getParameter("proj_startdate") != null) startDate = request.getParameter("proj_startdate");	
	if (request.getParameter("proj_enddate") != null) endDate = request.getParameter("proj_enddate");
	if (request.getParameter("proj_userid") != null) userId = Integer.parseInt(request.getParameter("proj_userid")); 
   	if (request.getParameter("proj_customerid") != null) customerId = Integer.parseInt(request.getParameter("proj_customerid"));
    if (request.getParameter("proj_status") != null) status = request.getParameter("proj_status");
    if (request.getParameter("cust_referralid") != null) referralId = Integer.parseInt(request.getParameter("cust_referralid"));
    if (request.getParameter("startdatecreateproject") != null) startDateCreate = request.getParameter("startdatecreateproject");
   	if (request.getParameter("enddatecreateproject") != null) endDateCreate = request.getParameter("enddatecreateproject");
   	if (request.getParameter("startduedate") != null) startDueDate = request.getParameter("startduedate");
   	if (request.getParameter("endduedate") != null) endDueDate = request.getParameter("endduedate");
    if (request.getParameter("paymentStatus") != null) paymentStatus = request.getParameter("paymentStatus");
    if (request.getParameter("user_areaid") != null) areaId = Integer.parseInt(request.getParameter("user_areaid"));
    if (request.getParameter("proj_projectid") != null) projectId = Integer.parseInt(request.getParameter("proj_projectid"));
 
	// Filtros listados
   	if (wflowCategoryId > 0) {
   		where += " AND wfty_wflowcategoryid = " + wflowCategoryId;
   		filters += "<i>Categor&iacute;a de Flujo: </i>" + request.getParameter("wfty_wflowcategoryidLabel") + ", ";
   	}
   	
	if (wflowTypeId > 0) {
		where += " AND wfty_wflowtypeid = " + wflowTypeId;
		filters += "<i>Tipo de Flujo: </i>" + request.getParameter("wflw_wflowtypeidLabel") + ", ";
	}
	
	if (wflowPhaseId > 0) {
		where += " AND wflw_wflowphaseid = " + wflowPhaseId;
		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
	}
	
	if (projectId > 0) {
   		where += " AND proj_projectid = " + projectId;
   		filters += "<i>Proyecto: </i>" + request.getParameter("proj_projectidLabel") + ", ";
   	}
	
    // Filtro 
   	if (customerId > 0) {
		where += " AND proj_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("proj_customeridLabel") + ", ";
	}
   	
   	if (areaId > 0) {
   		where += " AND user_areaid = " + areaId;
		filters += "<i>Departamento: </i>" + request.getParameter("user_areaidLabel") + ", ";
	}
   	
   	if (referralId > 0) {
	    where += " AND cust_referralid = " + referralId;
	    filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
	}
	
	if (venueId > 0) {
		where += " AND proj_venueid = " + venueId;
		filters += "<i>Lugar: </i>" + request.getParameter("proj_venueidLabel") + ", ";
	}
	
	if (!status.equals("")) {
   		//where += " AND proj_status like '" + status + "'";
        where += SFServerUtil.parseFiltersToSql("proj_status", status);
   		filters += "<i>Estatus: </i>" + request.getParameter("proj_statusLabel") + ", ";
   	}
	
	if (!paymentStatus.equals("")) {
   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
   		filters += "<i>Estatus Pago: </i>" + request.getParameter("paymentStatusLabel") + ", ";
   	}
	
	if (userId > 0) {
		where += " AND proj_userid = " + userId;
		filters += "<i>Vendedor: </i>" + request.getParameter("proj_useridLabel") + ", ";
	}
	
	if (!startDate.equals("")) {
		where += " AND proj_startdate >= '" + startDate + " 00:00'";
		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
	}
	
	if (!endDate.equals("")) {
		where += " AND proj_startdate <= '" + endDate + " 23:59'";
		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
	}
	
	if (!startDateCreate.equals("")) {
   		where += " AND proj_datecreateproject >= '" + startDateCreate + " 00:00'";
   		filters += "<i>Fecha Inicio Sis.: </i>" + startDateCreate + ", ";
   	}
   	
   	if (!endDateCreate.equals("")) {
   		where += " AND proj_datecreateproject <= '" + endDateCreate + " 23:59'";
   		filters += "<i>Fecha Fin Sis.: </i>" + endDateCreate + ", ";
   	}
   	
   	if (!startDueDate.equals("")) {
   		whereBkmvDueDateStart += " AND bkmv_duedate >= '" + startDueDate + "' ";
   		filters += "<i>Fecha Pago: </i>" + startDueDate + ", ";
   	}
   	
   	if (!endDueDate.equals("")) {
   		whereBkmvDueDateEnd += " AND bkmv_duedate <= '" + endDueDate + "' ";
   		filters += "<i>Fecha Pago Fin: </i>" + endDueDate + ", ";
   	}
   	
   	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
   	
   	
   	// Obtener disclosure de datos
    String disclosureFilters = new PmProject(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
   	
	
   	PmConn pmConn = new PmConn(sFParams);
   	pmConn.open();
   	
   	PmConn pmConn2 = new PmConn(sFParams);
   	pmConn2.open();
   	
   	PmConn pmConnByUser = new PmConn(sFParams);
   	pmConnByUser.open();
   	
   	PmConn pmConnRaccCount = new PmConn(sFParams);
   	pmConnRaccCount.open();
   	
   	PmConn pmConnRaccCountTemp = new PmConn(sFParams);
   	pmConnRaccCountTemp.open();
   	
   	PmConn pmConnUser = new PmConn(sFParams);
    pmConnUser.open();
   	
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
			<link rel="stylesheet" type="text/css" href="/css/<%= defaultCss %>">			
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
					<b>Ordenado por:</b> Usuario, Fecha Sistema
				</td>
			<td class="reportDate" align="right">
					Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
				</td>
			</tr>
		</table>
		<table>
			<tr>
				<td colspan="14" align="rigth">&nbsp;</td>
			</tr>
		</table>
		
		
		<table class="report" border="0">
			
			<%
			    double subTotal = 0, subTotalSum = 0, subTotalSumTotal = 0,
			    		extras = 0, extrasSum = 0, extrasSumTotal = 0,
			    		ordeTotal = 0, ordeTotalSum = 0, ordeTotalSumTotal = 0,
			    		comision = 0, comisionSum = 0, comisionSumTotal = 0,
			    	    comisionRpt = 0, comisionRptSum = 0, comisionRptSumTotal = 0,
			    		comisionTerceros = 0, comisionTercerosSum = 0, comisionTercerosSumTotal = 0,
			    		precioTotal = 0, precioTotalSum = 0, precioTotalSumTotal = 0,
			    		percentage = 0, percentage5 = 5,
			    		percentage7 = 7, percentage10 = 10,
			    		pagos = 0, pagosSum = 0, pagosSumTotal = 0,
			    		saldos = 0, saldosSum = 0, saldosSumTotal = 0,
			    		bono = 0, bonoSum = 0, bonoSumTotal = 0,
						cincoMil = 0, cincoMilSum = 0, cincoMilSumTotal = 0
			    		;
			
				String concatODC = "", concatODCTerceros = "", percentageWord="";
		
			    int i = 0, idUser = 0; 
			    
			    boolean primerVenta = true;
		
			    // Traigo los proyectos en los que haya al menos un pago en el periodo de fecha(mes)
			    sql =	" SELECT * FROM projects " +
							" LEFT JOIN customers ON (cust_customerid = proj_customerid) " +
							" LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
							" LEFT JOIN cities ON (city_cityid = venu_cityid) " +
							" LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
							" LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
							" LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	
							" LEFT JOIN orders ON (orde_orderid = proj_orderid) " +
							" LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) " +
							" LEFT JOIN users ON (user_userid = proj_userid) " +
							" LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
							
							" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) " +
							" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid)  " +
							" LEFT JOIN bankmovconcepts ON (bkmc_foreignid = racc_raccountid) " +
							" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid)   " +
							
							" WHERE proj_projectid > 0 " + 
							where + 
							
							whereBkmvDueDateStart + whereBkmvDueDateEnd +
				    		" AND bkmc_raccountid > 0 "  + 
				    		" AND racc_payments > 0 " + 
				    		" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "' " + 
				    		" AND ract_category <> '" + BmoRaccountType.CATEGORY_CREDITNOTE + "' " + 
				    		" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "' " + 
				    		" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'  " + 
			    		
							" GROUP BY proj_projectid " +
							" ORDER BY proj_userid ASC, proj_datecreateproject ASC ";
			     	pmConn.doFetch(sql);
			     	//System.out.println("sql1er: "+sql);   	
		     	
		     	while(pmConn.next()) {
					
					int raccCount = 0;
					// Hay que descartar los pagos hecho antes del periodo
					//Contador para saber si ha hecho pagos antes, si tiene ya NO hay que mostrar el proyecto porque ya se había dado un pago
					sqlRacc = " SELECT COUNT(racc_raccountid) as raccCount FROM raccounts " +
								" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
								" LEFT JOIN bankmovconcepts ON (bkmc_foreignid = racc_raccountid) " +
								" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
								" WHERE racc_raccountid > 0 " +
								" AND racc_orderid = " + pmConn.getInt("orders", "orde_orderid") +
								" AND bkmc_raccountid > 0 "  + 
					    		" AND racc_payments > 0 " + 
					    		" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "' " + 
					    		" AND ract_category <> '" + BmoRaccountType.CATEGORY_CREDITNOTE + "' " + 
					    		" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "' " + 
					    		" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' " + 
								" AND bkmv_duedate < '" + startDueDate + "'";
					
					pmConnRaccCount.doFetch(sqlRacc);
					if(pmConnRaccCount.next())
						raccCount = pmConnRaccCount.getInt("raccCount");
					
					//System.out.println("tiene pagos anteriores: "+raccCount);
					
					if(!(raccCount > 0)){
							if(pmConn.getInt("user_userid") != idUser){
								idUser = pmConn.getInt("user_userid");
								countProject = 0;
								//System.out.println("usuario: " + pmConn.getString("user_code"));
								// Contador de registros de proyectos, para mostrar el total en cada vendedor
								// Traigo los proyectos en los que haya al menos un pago en el periodo de fecha(mes)
							    sql =	" SELECT * FROM projects " +
											" LEFT JOIN customers ON (cust_customerid = proj_customerid) " +
											" LEFT JOIN venues ON (venu_venueid = proj_venueid) " +
											" LEFT JOIN cities ON (city_cityid = venu_cityid) " +
											" LEFT JOIN wflows ON (wflw_wflowid = proj_wflowid) " +
											" LEFT JOIN wflowtypes ON (wfty_wflowtypeid = proj_wflowtypeid) " +
											" LEFT JOIN wflowphases ON (wfph_wflowphaseid = wflw_wflowphaseid) " +	
											" LEFT JOIN orders ON (orde_orderid = proj_orderid) " +
											" LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) " +
											" LEFT JOIN users ON (user_userid = proj_userid) " +
											" LEFT JOIN wflowcategories ON (wfca_wflowcategoryid = wfty_wflowcategoryid) " +
											
											" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) " +
											" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid)  " +
											" LEFT JOIN bankmovconcepts ON (bkmc_foreignid = racc_raccountid) " +
											" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid)   " +	
											" WHERE proj_projectid > 0 " + 
								    	
											where + 
											
											whereBkmvDueDateStart + whereBkmvDueDateEnd +
											" AND user_userid = " + idUser +
								    		" AND bkmc_raccountid > 0 "  + 
								    		" AND racc_payments > 0 " + 
								    		" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "' " + 
								    		" AND ract_category <> '" + BmoRaccountType.CATEGORY_CREDITNOTE+ "' " + 
								    		" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "' " + 
								    		" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'  " + 
							    		
											" GROUP BY proj_projectid " +
											" ORDER BY proj_userid ASC, proj_datecreateproject ASC ";
							    	pmConnByUser.doFetch(sql);
							     	//System.out.println("sql2doParaContarProj: "+sql);
		
							     	//Este while es solo para contar los registros que van a salir, y para controlar la salida  de los totales de cada vendedor
								     	while(pmConnByUser.next()) {
								     		int raccCountByUser = 0;
											// Hay que descartar los pagos hecho antes del periodo
											//Contador para saber si ha hecho pagos antes
											String sqlRaccByUser = " SELECT COUNT(racc_raccountid) as raccCountByUser FROM raccounts " +
														" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
														" LEFT JOIN bankmovconcepts ON (bkmc_foreignid = racc_raccountid) " +
														" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
														" WHERE racc_raccountid > 0 " +
														" AND racc_orderid = " + pmConnByUser.getInt("orders", "orde_orderid") +
														" AND bkmc_raccountid > 0 "  + 
											    		" AND racc_payments > 0 " + 
											    		" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "' " + 
											    		" AND ract_category <> '" + BmoRaccountType.CATEGORY_CREDITNOTE+ "' " + 
											    		" AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "' " + 
											    		" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' " + 
														" AND bkmv_duedate < '" + startDueDate + "'";
											
											pmConnRaccCountTemp.doFetch(sqlRaccByUser);
											if(pmConnRaccCountTemp.next())
												raccCountByUser = pmConnRaccCountTemp.getInt("raccCountByUser");
											
											//System.out.println("raccCONTADOR-PREV-POR-USUARIO: "+raccCountByUser);
											
											if(!(raccCountByUser > 0)){
												countProject ++;
											}
								     	}
								     	
								     	//System.out.println("countProjectByUser: "+countProject);
								
						       	
						     	primerVenta = true;
						     	i=0;
						     	percentageWord = "";
								percentage = 0;
						     	%>
						     	<tr>
						     		<td colspan="13" class="reportGroupCell">
						     			<b>
							     			&nbsp;<%= HtmlUtil.stringToHtml(pmConn.getString("users","user_firstname") + " " + 
							     			pmConn.getString("users","user_fatherlastname") + " " + 
							     			pmConn.getString("users","user_motherlastname"))%>
						     			</b>
						     		</td>
						     	</tr>
						     	<TR class="">      					
									<td class="reportHeaderCellCenter">#</td>
									<td class="reportHeaderCell">Proyecto</td>
									<td class="reportHeaderCell">Fecha Evento</td>
									<td class="reportHeaderCell">Fecha Sistema</td>
									<td class="reportHeaderCell">Lugar</td>
									<td class="reportHeaderCellRight">Precio Evento</td>
									<td class="reportHeaderCellRight">Comisiones<br>a Terceros</td>
									<!-- <td class="reportHeaderCellRight">ODC<br>Terceros</td>-->
									<td class="reportHeaderCellRight">Precio Total</td>
									<td class="reportHeaderCellRight">Pagos<br>Cliente</td>
									<td class="reportHeaderCellRight">Saldo<br>Cliente</td>
									<td class="reportHeaderCellRight">% Comisi&oacute;n</td>
									<!-- <td class="reportHeaderCellRight">Comisi&oacute;n<br>Eje-Com.</td>-->
									<td class="reportHeaderCellRight">Comisi&oacute;n<br>Ejecutivo</td>
									<td class="reportHeaderCellRight">ODC</td>
									
								</TR>
				
						     	<%
				
								}// Fin de (... != Iduser)
							     	
								i++;
								subTotal = 0; 
								extras = 0;
					    		ordeTotal = 0;
					    		comision = 0;
					    	    comisionRpt = 0;
					    		comisionTerceros = 0;
					    		precioTotal = 0;
					    		pagos = 0;
					    		saldos = 0;
					    		bono = 0;
					    		cincoMil = 0;
						
						
								subTotal = pmConn.getDouble("orde_amount");
								extras = pmConn.getDouble("orde_discount");
								ordeTotal = pmConn.getDouble("orde_total");
								ordeTotalSum += ordeTotal;
								
							
								//Obtener Comision del Ejecutivo, Nota: el proveedor debe estar ligado al usuario
								String sqlOCCom = " SELECT SUM(reqi_total) as comision, GROUP_CONCAT(reqi_code SEPARATOR '<br>') as odc" +
										  "	FROM requisitions " +
									      " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
										  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +
										  " LEFT JOIN suppliers ON (supl_supplierid = reqi_supplierid) " +
										  " LEFT JOIN supplierusers ON (spus_supplierid = reqi_supplierid) " +
									      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
									      " AND rqtp_type = '" + BmoRequisitionType.TYPE_COMMISION + "'" +
									      " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
									      " AND supl_type = '" + BmoSupplier.TYPE_BROKER + "'" +
									      " AND spus_userid = "+ pmConn.getInt("user_userid") +
									      " ORDER BY reqi_requisitionid ASC";
						        pmConn2.doFetch(sqlOCCom);
								if(pmConn2.next()) {
									concatODC = pmConn2.getString("odc");
									comision = pmConn2.getDouble("comision");
								}
								//System.out.println("Comision: "+sqlOCCom);
								
								
								//Obtener Comision a Terceros
								String sqlOCComTer = " SELECT SUM(reqi_total) as comisionTerceros, GROUP_CONCAT(reqi_code SEPARATOR '<br>') as odc" +
										  "	FROM requisitions " +
									      " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
										  " LEFT JOIN orders ON (orde_orderid = reqi_orderid) " +
										  " LEFT JOIN suppliers ON (supl_supplierid = reqi_supplierid) " +
									      " WHERE orde_orderid = " + pmConn.getInt("proj_orderid") +
									      " AND supl_type <> '" + BmoSupplier.TYPE_BROKER + "'" +
									      " AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'" +
									      " ORDER BY reqi_requisitionid ASC";
						        pmConn2.doFetch(sqlOCComTer);
								if(pmConn2.next()){
									//concatODCTerceros = pmConn2.getString("odc");
									comisionTerceros = pmConn2.getDouble("comisionTerceros");
									comisionTercerosSum += comisionTerceros;
								}
								//System.out.println("ComisionTerceros: "+sqlOCComTer);
								
								
								//CXC Pagadas
								String sqlCXCPagadas = " SELECT SUM(racc_total) AS pagosSum " + 
												  " FROM raccounts " +
												  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
												  " WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
												  " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT  + "'" +
											      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
								
								//System.out.println("sqlCXCPagadas: "+sqlCXCPagadas);
								pmConn2.doFetch(sqlCXCPagadas);
								if(pmConn2.next()){
									pagos = pmConn2.getDouble("pagosSum");
								}
								
								//CXC Saldo Cliente
								String sqlCXCSaldo = " SELECT SUM(racc_total) AS saldoSum " + 
												  " FROM raccounts " +
												  " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
												  " WHERE racc_orderid = " + pmConn.getInt("proj_orderid") +
												  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW  + "'" +
												  " AND (racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "') ";
								
								//System.out.println("sqlCXCSaldo: "+sqlCXCSaldo);
								pmConn2.doFetch(sqlCXCSaldo);
								if(pmConn2.next()){
									saldos = pmConn.getDouble("orde_total") - pmConn2.getDouble("saldoSum");
								}
								
								
								//Precio Total
								precioTotal = ordeTotal - comisionTerceros;
								precioTotalSum += precioTotal;
								
								//Suma Pagos
								pagosSum += pagos;
								
								//Suma Saldos Cliente
								saldosSum += saldos;
										
								
						%>	
								<TR class="reportCellEven"> 
							  		<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>	
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects","proj_code") + "&nbsp;&nbsp;" + pmConn.getString("projects", "proj_name"), BmFieldType.STRING)) %>
									<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects","proj_startdate"), BmFieldType.DATETIME) %>
									<%= HtmlUtil.formatReportCell(sFParams, pmConn.getString("projects","proj_datecreateproject"), BmFieldType.DATETIME) %>
									<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("venues","venu_code") + " " + pmConn.getString("venues","venu_name"), BmFieldType.STRING)) %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + ordeTotal, BmFieldType.CURRENCY) %>	
									<%= HtmlUtil.formatReportCell(sFParams, "" + comisionTerceros, BmFieldType.CURRENCY) %>
									<%//= HtmlUtil.formatReportCell(sFParams, "" + concatODCTerceros, BmFieldType.STRING) %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + precioTotal, BmFieldType.CURRENCY) %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + pagos, BmFieldType.CURRENCY) %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + saldos, BmFieldType.CURRENCY) %>
									<%= HtmlUtil.formatReportCell(sFParams, "" + percentageWord, BmFieldType.PERCENTAGE, "reportCellCurrency") %>	
									<%= HtmlUtil.formatReportCell(sFParams, "" + comision, BmFieldType.CURRENCY) %>	
									<%= HtmlUtil.formatReportCell(sFParams, "" + concatODC, BmFieldType.STRING) %>	
								</TR>
								
								<% 
									if(i==countProject){
										if(precioTotalSum >= 0 && precioTotalSum < 50000 ){
											percentageWord = String.valueOf(0);
											percentage = (0/100);
										}
										
										
										
										//Si el precio total  esta  entre 50000 y 50999, se le dan solo 5000
										if(precioTotalSum >= 50000 && precioTotalSum < 51000 ){
											precioTotalSum = 5000;	
										}
										
										
										//Si esta o rebasa los 51 mil
										if(precioTotalSum >= 51000){
											//Si rebasa los 400mil se le da bono de 10mil
											if(precioTotalSum >= 400000){	
												bono = 10000;
											}
											//Se le quitan los 50,000 en caso de que rebase los 51,000 pero se le dan 5000
											precioTotalSum = precioTotalSum - 50000;
											cincoMil = 5000;
										}
										
										if(precioTotalSum >= 51000 && precioTotalSum < 151000 ){
											percentageWord = String.valueOf(percentage5);
											percentage = (percentage5/100);
						
										}else if(precioTotalSum >= 151000 && precioTotalSum < 251000 ){
											percentageWord = String.valueOf(percentage7);
											percentage = (percentage7/100);
						
										}else if(precioTotalSum >= 251000){
											percentageWord = String.valueOf(percentage10);
											percentage = (percentage10/100);
						
										}
										
										//System.out.println("precioTotal: "+precioTotalSum+" percentage:"+percentage+" cincoMil:"+cincoMil+ " bono:"+bono);
										comisionRpt = (precioTotalSum * percentage) + cincoMil + bono;
										comisionRptSum += comisionRpt;
										
										%>
										<tr class="reportCellEven reportCellCurrency" height="30">
									        <td colspan="5" >&nbsp;</td>
									        <td> 
												&nbsp;<b><%= formatCurrency.format(ordeTotalSum) %></b>
											</td>
											<td> 
												&nbsp;<b><%= formatCurrency.format(comisionTercerosSum) %></b>
											</td>
											<td> 
												&nbsp;<b><%= formatCurrency.format(precioTotalSum) %></b>
											</td>
											<td> 
												&nbsp;<b><%= formatCurrency.format(pagosSum) %></b>
											</td>
											<td> 
												&nbsp;<b><%= formatCurrency.format(saldosSum) %></b>
											</td>
											<%= HtmlUtil.formatReportCell(sFParams, "" + percentageWord, BmFieldType.PERCENTAGE, "reportCellCurrency") %>	
											<td> 
												&nbsp;<b><%= formatCurrency.format(comisionRpt) %></b>
											</td>
											<td class="reportCellText"> 
												-
											</td>
										</tr>
										<tr class="reportCellEven"><td colspan="13">&nbsp;</td><tr>
										
							<% 			
										ordeTotalSumTotal += ordeTotalSum;
										comisionTercerosSumTotal += comisionTercerosSum;
										precioTotalSumTotal += precioTotalSum;
										comisionRptSumTotal += comisionRptSum;
										pagosSumTotal += pagosSum;
										saldosSumTotal += saldosSum;
										ordeTotalSum = 0;
										comisionTercerosSum = 0;
										precioTotalSum = 0;
										comisionRptSum = 0;
										pagosSum = 0;
										saldosSum = 0;
									}// Fin if(i==countProject)
					}// Fin de contador para descartar
			   } // Fin de while
		
			%>
			<tr class=""><td colspan="13">&nbsp;</td><tr>
		
			<tr class="reportCellEven reportCellCode">
			    <td colspan="5" align="left">&nbsp;<b>Totales:&nbsp;&nbsp;</b></td>
			    <td align="right"> 
					&nbsp;<b><%= formatCurrency.format(ordeTotalSumTotal) %></b>
				</td>
				<td align="right"> 
					&nbsp;<b><%= formatCurrency.format(comisionTercerosSumTotal) %></b>
				</td>
				<!--<td align="left"> 
					&nbsp;&nbsp;-&nbsp;
				</td>-->
				<td align="right"> 
					&nbsp;<b><%= formatCurrency.format(precioTotalSumTotal) %></b>
				</td>
				<td align="right"> 
					&nbsp;<b><%= formatCurrency.format(pagosSumTotal) %></b>
				</td>
				<td align="right"> 
					&nbsp;<b><%= formatCurrency.format(saldosSumTotal) %></b>
				</td>
				<td align="right"> 
					&nbsp;-&nbsp;
				</td>
				<td align="right"> 
					&nbsp;<b><%= formatCurrency.format(comisionRptSumTotal) %></b>
				</td>
				<td align="left"> 
					&nbsp;&nbsp;-&nbsp;
				</td>
			</tr>
				
		</TABLE>
		
		<%
			

	}// Fin de if(no carga datos)
	pmConnUser.close();
	pmConn2.close();
    pmConn.close();
    pmConnRaccCount.close();
   	pmConnRaccCountTemp.close();
   	pmConnByUser.close();
%>  
	System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
						+ " Reporte: "+title
						+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	<% if (print.equals("1")) { %>
	<script>
		/*window.print();*/
	</script>
	<% } %>
	
	
  </body>
</html>