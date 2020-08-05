
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
 
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBudget"%>
<%@page import="com.flexwm.server.fi.PmBudget"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp" %>
<%
	// Inicializar variables
 	String title = "Reporte Presupuestos por Proveedor";
	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);

   	String sql = "", where = "", whereDueDate = "";
   	String receiveDate = "", dueDateStart = "", dueDateEnd = "";
   	int budgetId = 0, budgetItemId = 0, supplierId = 0, developmentPhaseId = 0;
   	String filters = "";
   	
   	int programId = 0;
    
   	// Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));   	
   	if (request.getParameter("bgit_budgetid") != null) budgetId = Integer.parseInt(request.getParameter("bgit_budgetid"));
   	if (request.getParameter("bgit_budgetitemid") != null) budgetItemId = Integer.parseInt(request.getParameter("bgit_budgetitemid"));
   	if (request.getParameter("supplierid") != null) supplierId = Integer.parseInt(request.getParameter("supplierid"));
   	if (request.getParameter("developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("developmentphaseid"));
   	if (request.getParameter("duedatestart") != null) dueDateStart = request.getParameter("duedatestart");
   	if (request.getParameter("duedateend") != null) dueDateEnd = request.getParameter("duedateend");
   	
	
   	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	PmConn pmConn3 = new PmConn(sFParams);
	PmConn pmConn4 = new PmConn(sFParams);
	PmConn pmConn5 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
	pmConn3.open();
	pmConn4.open();
	pmConn5.open();
	
	if (budgetId > 0) {
		where += " AND budg_budgetid = " + budgetId;
        filters += "<i>Presupuesto: </i>" + request.getParameter("bgit_budgetidLabel") + ", ";
	}
	
	if (budgetItemId > 0) {
		where += " AND bgit_budgetitemid = " + budgetItemId;
        filters += "<i>Partida: </i>" + request.getParameter("bgit_budgetitemidLabel") + ", ";
	}
	
	if (supplierId > 0) {
		where += " AND bkmv_supplierid = " + supplierId;
        filters += "<i>Proveedor: </i>" + request.getParameter("supplieridLabel") + ", ";
	}
	
	if (developmentPhaseId > 0) {
		where += " AND dvph_developmentphaseid = " + developmentPhaseId;
        filters += "<i>Etapa Desarrollo: </i>" + request.getParameter("developmentphaseidLabel") + ", ";
	}
	
	if(!dueDateStart.equals("")){
		whereDueDate += " AND bkmv_duedate >= '" + dueDateStart + "' ";
        filters += "<i>Fecha Inicio: </i>" + request.getParameter("duedatestart") + ", ";
	}
	
	if(!dueDateEnd.equals("")){
		whereDueDate += " AND bkmv_duedate <= '" + dueDateEnd + "' ";
        filters += "<i>Fecha Fin: </i>" + request.getParameter("duedateend") + ", ";
	}
	
	if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	sFParams.getBmoSelectedCompany().getName().toString() + 
	   	" | " + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
	
	// Obtener disclosure de datos
    String disclosureFilters = new PmBudget(sFParams).getDisclosureFilters();   
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
	
	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " bankmovconcepts ") +
	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovements")+" ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitems")+" ON (bkmc_budgetitemid = bgit_budgetitemid) " +
	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgets")+" ON (bgit_budgetid = budg_budgetid) " +
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +			  		  
	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON (budg_budgetid = dvph_budgetid) " +
	      " WHERE budg_budgetid > 0 " + where + whereDueDate +
	      " GROUP BY budg_budgetid " +
	      " ORDER BY budg_name ";
	pmConn.doFetch(sql);
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
if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
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
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
	<tr>
		<td align="left" width="5%" rowspan="6" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td class="reportTitle" colspan="2">
			Control Presupuestal
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
<table border="0" class="report" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<%
		double sumTotalConcepts = 0, totalConcepts = 0;
		//Listado de Presupuestos
		while(pmConn.next()) {
			//Se cuenta los proveedores de todo el presupuesto para asignarsela al rowspan
			sumTotalConcepts = 0;
			int countBudgetItem = 0;
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " budgetitems ") +
					" LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +			  		  
					" WHERE bgit_budgetid = " + pmConn.getInt("budg_budgetid");
			pmConn2.doFetch(sql);
			//if (pmConn2.next()) countBudgetItem = pmConn2.getInt("countBudgetItem");
			while(pmConn2.next()) {
				sql = " SELECT COUNT(distinct(supl_supplierid)) AS countSupplier FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
				          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (reqi_supplierid = supl_supplierid) " +										          
					  " WHERE reqi_budgetitemid = " + pmConn2.getInt("bgit_budgetitemid");
				pmConn3.doFetch(sql);
				if (pmConn3.next()){
					countBudgetItem += pmConn3.getInt("countSupplier");
					//Si una partida no tiene proveedores, suma 1 renglon para que no se descuadre
					if(pmConn3.getInt("countSupplier") == 0){
						countBudgetItem = countBudgetItem+1;
					}
				}
			}
			
	%>
			<TR>
				<th  class="reportHeaderCell">
					Presupuesto
				</th>
				<th  class="reportHeaderCell">
					Partida Presupuestal
				</th>
				<th  class="reportHeaderCell">
					Proveedores
				</th>
				<th  class="reportHeaderCellRight">
					Montos
				</th>
			</TR>
			<TR class="reportCellEven">
				<%// Le sumo 1 para incluir el total de cada presupuesto%>
				<TD class="reportCellText" rowspan="<%= countBudgetItem +1 %>" valign="middle">
					<b><%= HtmlUtil.stringToHtml(pmConn.getString("budgets", "budg_name")) %></b>
				</TD>
				<%
					//Partidas por Presupuesto
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " budgetitems ") +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " budgetitemtypes")+" ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
							  " WHERE bgit_budgetid = " + pmConn.getInt("budg_budgetid");
					pmConn2.doFetch(sql);
					int countRowSuplControl=0; //Controla cuando se inicia un nuevo renglon
					while(pmConn2.next()) {
						int countSupplier = 0;
						//Se cuenta los proveedores de la partida para asignarsela al rowspan
						sql = " SELECT COUNT(distinct(supl_supplierid)) AS countSupplier FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
						          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (reqi_supplierid = supl_supplierid) " +										          
							  " WHERE reqi_budgetitemid = " + pmConn2.getInt("bgit_budgetitemid");
						pmConn3.doFetch(sql);
						if (pmConn3.next()) countSupplier = pmConn3.getInt("countSupplier");
						%>
						<TD  class="reportCellText" valign="middle" rowspan="<%= countSupplier %>">
							<b><%= pmConn2.getString("budgetitemtypes", "bgty_name") %></b>
							- <%= HtmlUtil.stringToHtml(pmConn2.getString("budgetitemtypes", "bgty_name")) %>
						</TD>
						<%
						sql = " SELECT distinct(supl_supplierid), supl_code, supl_name FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
					          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (reqi_supplierid = supl_supplierid) " +										          
						      " WHERE reqi_budgetitemid = " + pmConn2.getInt("bgit_budgetitemid");
						pmConn4.doFetch(sql);
						int countRowSupl = 0; //Contador para controlar cuando se inicia un nuevo renglon
						// si no hay proveedores en la partida agregar columnas vacias
						if(countSupplier != 0){
							while(pmConn4.next()) {
								countRowSuplControl += countRowSupl+1;
					%>			
									<TD class="reportCellText">
										<b><%= pmConn4.getString("suppliers", "supl_code") %></b> - <%= HtmlUtil.stringToHtml(pmConn4.getString("suppliers", "supl_name")) %>
									</TD>
									
									<%
										//Obtener los conceptos de banco
										double sumAmountConcepts = 0;
										sql = " SELECT SUM(bkmc_amount) AS sumAmountConcepts FROM " + SQLUtil.formatKind(sFParams, " bankmovconcepts ") +
										      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " bankmovements")+" ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
										      " WHERE bkmc_budgetitemid = " + pmConn2.getInt("bgit_budgetitemid") + 
										      " AND bkmv_supplierid = " + pmConn4.getInt("supl_supplierid") + 
										      " AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "' " +
										      whereDueDate;
										pmConn5.doFetch(sql);
										if (pmConn5.next()) sumAmountConcepts = pmConn5.getDouble("sumAmountConcepts");
										
										sumTotalConcepts += sumAmountConcepts;
									%>
									<%= HtmlUtil.formatReportCell(sFParams, "" + sumAmountConcepts, BmFieldType.CURRENCY) %>
					    		</TR>
					    		<% //iniciar nuevo renglon siempre que NO sea igual al total de renglones contados de prov. 
					    		if(countRowSuplControl != countBudgetItem){%>
									<TR class="reportCellEven">
								<% } %>
					<% 			
							} //Fin Proveedores 
							
						}else{
							countRowSuplControl += countRowSupl+1;
					%>
				    			<%= HtmlUtil.formatReportCell(sFParams, "", BmFieldType.STRING) %>
				    			<%= HtmlUtil.formatReportCell(sFParams, "" + 0, BmFieldType.CURRENCY) %>
							</TR>
							<% if(countRowSuplControl != countBudgetItem){%>
								<TR class="reportCellEven">
							<% } %>
						<% }%>	
					<%
					} //Fin budgetItems
					%>
					<% if(countRowSuplControl != countBudgetItem){%>
								<!-- <TD class="reportCellEven" colspan="3">&nbsp;</TD> -->
						</TR>
					<%}%>
			<TR class="reportCellEven">
				<TD colspan="3" class="reportCellCurrency">
					<b><%= formatCurrency.format(sumTotalConcepts) %></b>	
				</TD>
			</TR>
		<% 
			totalConcepts += sumTotalConcepts;
		} //Listado Presupuestos 
		%>
		<TR class="reportCellEven">
			<TD colspan="4" class="reportCellCurrency">
				&nbsp;
			</TD>
		</TR>
		<TR class="reportCellEven">
			<TD colspan="4" class="reportCellCurrency">
				<b><%= formatCurrency.format(totalConcepts) %></b>	
			</TD>
		</TR>
</table>


<%
	pmConn5.close();
	pmConn4.close();
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
%>
<% } %>
	<% if (print.equals("1")) { %>
	<script>
		//window.print();
	</script>
	<% } %>
  </body>
</html>