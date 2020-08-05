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
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%@include file="/inc/login.jsp" %>

<% 
	// Imprimir
	String print = "0";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgae_report.xls");
    }
%>

<%
//try{
	// Inicializar variables
 	String title = "Lista de Relaciones P&uacute;blicas";
	
   	String sql = "", sql2 = "", sql3 = "", sql4 = "", where = "", whereCust = "", whereCuda = "", whereProj = "", whereRacc = "";
   	String startDate = "", endDate = "";
   	String filters = "";
   	int customerid = 0, orderId = 0, cols= 0, paccountTypeId = 0, companyId = 0 ;
   	String showTotal  = "N";
   	
   	// Obtener parametros   	
   	if (request.getParameter("racc_customerid") != null) customerid = Integer.parseInt(request.getParameter("racc_customerid"));
   	if (request.getParameter("startdate") != null) startDate = request.getParameter("startdate");    
    if (request.getParameter("enddate") != null) endDate = request.getParameter("enddate");

	// Filtros listados
	if (customerid > 0) {
		  where += " and cust_customerid = " + customerid;
		  whereProj += " and proj_customerid = "+ customerid;
		  whereRacc += " and racc_customerid = " + customerid;
		  filters += "<i>Cliente: </i>" + request.getParameter("racc_customeridLabel") + ", ";
	}
	

   	if (!startDate.equals("")) {
   		String month="", day="", birthdate="";
   		int year=0,mes=0, dia=0;
   		Calendar cal = SFServerUtil.stringToCalendar("yyyy-MM-dd", startDate);
   		year = cal.get(cal.YEAR);
   		mes = (cal.get(cal.MONTH))+1;
   		dia = cal.get(cal.DAY_OF_MONTH);
   		System.out.println("fechjaaaa: "+ dia+"**"+mes+"**"+year);
		
   		if (mes < 10) month =  "0" + mes;
        else month=mes+"";
		if (dia < 10) day =  "0" + dia;
		else day=dia+"";
   		
		birthdate = month+"-"+day;
   		whereCust += " and date_format(cust_birthdate, '%m-%d') >= '"+birthdate+"'";
   		//whereCust += " and cust_birthdate >= '" + startDate + "'";
   		whereCuda += " and date_format(cuda_relevantdate, '%m-%d') >= '"+birthdate+"'";
   		//whereCuda += " and cuda_relevantdate >= '" + startDate + "'";
   		whereProj += " and proj_startdate >= '" + startDate + " 00:00:00'";
   		whereRacc += " and racc_receivedate >= '" + startDate + "'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	
	
   	if (!endDate.equals("")) {
   		String month="", day="", importartDate="";
   		int year=0,mes=0, dia=0;
   		Calendar cal = SFServerUtil.stringToCalendar("yyyy-MM-dd", endDate);
   		year = cal.get(cal.YEAR);
   		mes = (cal.get(cal.MONTH))+1;
   		dia = cal.get(cal.DAY_OF_MONTH);
   		System.out.println("fechaENDDDDD: "+ dia+"**"+mes+"**"+year);
   		
   		if (mes < 10) month =  "0" + mes;
        else month=mes+"";
		if (dia < 10) day =  "0" + dia;
		else day=dia+"";
   		
		importartDate = month+"-"+day;
   		whereCust += " and date_format(cust_birthdate, '%m-%d') <= '"+importartDate+"'";
   		//whereCust += " and cust_birthdate <= '" + endDate + "'";
   		whereCuda += " and date_format(cuda_relevantdate, '%m-%d') <= '"+importartDate+"'";
   		//whereCuda += " and cuda_relevantdate <= '" + endDate + "'";
   		whereProj += " and proj_enddate <= '" + endDate + " 23:59:59'";
   		whereRacc += " and racc_duedate <= '" + endDate + "'";
        filters += "<i>Fecha Fin: </i>" + endDate + ", ";
    }


   	
   	String sqlCount = " SELECT count(cust_customerid) as custCount " + 
   			" FROM " + SQLUtil.formatKind(sFParams, "customers ") +
   			" WHERE cust_customerid > 0 " +
   			where + whereCust;
   	System.out.println("sqlCount: "+ sqlCount);

  
   	PmConn pmCustomers = new PmConn(sFParams);
   	pmCustomers.open();

   	PmConn pmCustomerDates = new PmConn(sFParams);
   	pmCustomerDates.open();
   	
   	PmConn pmCustomerEmails = new PmConn(sFParams);
   	pmCustomerEmails.open();
   	
   	PmConn pmProjects = new PmConn(sFParams);
   	pmProjects.open();

   	PmConn pmRaccounts = new PmConn(sFParams);
   	pmRaccounts.open();
   	
   	boolean s = true;
%>

<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
</head>

<body class="default" style="padding-right: 10px">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td class="reportTitle" align="left">
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
<table class="report" border="0">
		<TR>
			<td class="reportHeaderCell" colspan="12">
				CLIENTES
			</td>
		<TR>
		<TR class="">
			<td class="reportHeaderCell" width="3%">#</td>
			<td class="reportHeaderCell" width="10%">C&oacute;digo</td>
			<td class="reportHeaderCell" width="30%" colspan="3">Nombre</td>
			<td class="reportHeaderCell" width="10%" colspan="2">Tipo de Cliente</td>
			<td class="reportHeaderCell" width="30%" colspan="2">Fechas Importantes</td>                                                                           
			<td class="reportHeaderCell" width="27%" colspan="2">Correos</td>   
			<td class="reportHeaderCell" width="17%">Estatus</td>  
		</TR>
                	<%
					int i = 1, custId= 0, custCount=0;
                	String custType = "", fechasImport="", custEmails="", nacimiento="", emails="", status="";

                   	sql = " SELECT * " + 
                   			" FROM " + SQLUtil.formatKind(sFParams, "customers ") +
                   			" WHERE cust_customerid > 0 " +
                   			where + whereCust +
                   			" ORDER BY cust_customerid ASC, cust_birthdate ASC";
                   	System.out.println("sql: "+ sql);
                		pmCustomers.doFetch(sql);
                		while (pmCustomers.next()) {
                			custType = ""; fechasImport=""; custEmails=""; nacimiento=""; emails=""; status="";
                			System.out.println("pmCustomerssssss: ");
	                    	custId = pmCustomers.getInt("customers","cust_customerid");
	                    	if(pmCustomers.getString("customers","cust_birthdate").equals("")){
	                    		nacimiento+= "Nacimiento:\nN/A";
	                    	}else{ nacimiento+= "Nacimiento: " +pmCustomers.getString("customers","cust_birthdate"); }
	                    	
	                    	emails="SELECT * FROM" + SQLUtil.formatKind(sFParams, " customeremails")+" where cuem_customerid = "+ pmCustomers.getInt("cust_customerid");
	                    	
	                    	pmCustomerEmails.doFetch(emails);
	                    	//System.out.println("pmCustomerEmails: "+custId);
	                    	while (pmCustomerEmails.next()) {
	                    		custEmails+=pmCustomerEmails.getString("cuem_email")+"\n";                 		
	                    	}
	                    	
	                    	%>
	                    	
	                    	
				            
				            <TR class=""> 
				            	<td><%= i%></td>
				            	<%= HtmlUtil.formatReportCell(sFParams, pmCustomers.getString("customers","cust_code"), BmFieldType.STRING) %>
				            	<td colspan="3"><%= HtmlUtil.stringToHtml(pmCustomers.getString("customers","cust_displayname"))%></td>
		                        <%if (pmCustomers.getString("customers","cust_customertype").equals("P")) {
		                    	    custType="Persona"; }else{ custType="Empresa"; }%>
		                    	<td colspan="2"><%= custType%></td>
		                    	<td colspan="2"><%= nacimiento%></td>
		                    	<td colspan="2"><%= HtmlUtil.stringToHtml(custEmails)%></td>
		                        <% 	if (pmCustomers.getString("customers","cust_status").equals("C")) status = "Cliente";  
		                        	else if(pmCustomers.getString("customers","cust_status").equals("P")) status = "Prospecto"; 
		                        	else if(pmCustomers.getString("customers","cust_status").equals("I")) status = "Inactivo"; 
		                        %>
		                        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, status, BmFieldType.STRING)) %>                                    
	
	                        </TR>
		            	<% 
		            		i++;
                    	} //End while pmCustomers

                		
                		//i = 1;
                		sql2 = " SELECT * " +
                	  			 " FROM " + SQLUtil.formatKind(sFParams, "customers ") +
                				 " INNER JOIN " + SQLUtil.formatKind(sFParams, "customerdates")+" ON(cuda_customerid=cust_customerid) " +
                				 " WHERE cuda_customerid > 0 " +
                				 where + whereCuda +
                				 " ORDER BY cust_customerid ASC, cuda_relevantdate ASC";
                		System.out.println("sql2222: "+ sql2);
                		pmCustomerDates.doFetch(sql2);
                		while (pmCustomerDates.next()) {
                			System.out.println("pmCustomerDates: ");
                			custType = ""; fechasImport=""; custEmails=""; status="";

	                    	emails="SELECT * FROM " + SQLUtil.formatKind(sFParams, "customeremails")+" where cuem_customerid = "+ pmCustomerDates.getInt("cust_customerid");
	                    	
	                    	pmCustomerEmails.doFetch(emails);
	                    	while (pmCustomerEmails.next()) {
	                    		custEmails+=pmCustomerEmails.getString("cuem_email")+" ";
	                    	}
	                    	%>
				            <TR class=""> 
				            	<td><%= i%></td>
		                        <%= HtmlUtil.formatReportCell(sFParams, pmCustomerDates.getString("customers","cust_code"), BmFieldType.CODE) %>
		                        <td colspan="3"><%= HtmlUtil.stringToHtml(pmCustomerDates.getString("customers","cust_displayname"))%></td>
		                        <%if (pmCustomerDates.getString("customers","cust_customertype").equals("P")) {
		                    	    custType="Persona"; }else{ custType="Empresa"; }%>
			                    <td colspan="2"><%= HtmlUtil.stringToHtml(custType)%></td>
			                    <td colspan="2"><%= HtmlUtil.stringToHtml(pmCustomerDates.getString("cuda_description") + " " +pmCustomerDates.getString("cuda_relevantdate"))+"\n" %></td>
			                    <td colspan="2"><%= HtmlUtil.stringToHtml(custEmails)%></td>

		                        <% 	if (pmCustomerDates.getString("customers","cust_status").equals("C")) status = "Cliente";  
	                        	else if(pmCustomerDates.getString("customers","cust_status").equals("P")) status = "Prospecto"; 
	                        	else if(pmCustomerDates.getString("customers","cust_status").equals("I")) status = "Inactivo"; 
	                        %>
	                        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, status, BmFieldType.STRING)) %>                                    
	
	                        </TR>
		            	<% 
		            		i++;	
                    	} //End while pmCustomerDates
                           %> 
       	                <TR> 
		                	<td colpan="12">&nbsp;</td>
		                </TR>
			            <TR>
			            	<td class="reportHeaderCell" colspan="12">
			            		PROYECTOS
			            	</td>
			            <TR>
			            <TR class="">
			            	<td class="reportHeaderCell">#</td>
			               <td class="reportHeaderCell">C&oacute;digo</td>
			               <td class="reportHeaderCell" colspan="2">Proyecto</td>
			               <td class="reportHeaderCell" colspan="2">Cliente</td>                                                                         
			               <td class="reportHeaderCell" colspan="2">Lugar</td>  
			               <td class="reportHeaderCell" colspan="2">Fecha Inicio</td> 
			               <td class="reportHeaderCell">Fecha Fin</td> 
			               <td class="reportHeaderCell" align="right">Avance</td> 
			            </TR>
                    	<%
                    			i = 1;
	                    		sql3 = " SELECT * " +
		                       			" FROM " + SQLUtil.formatKind(sFParams, "flex.projects ") +
		                       			" WHERE proj_projectid > 0 " +
		                       			//" AND proj_status <> '" + BmoProject.STATUS_FINISHED + "'" +
		                       			//" AND proj_status <> '" + BmoProject.STATUS_CANCEL + "'" +
		                       			whereProj +
		                       			" ORDER BY proj_startdate ASC, proj_enddate ASC, proj_code ASC";
		                    	System.out.println("sql333: "+ sql3);
		                    	
		                    	BmoProject bmoProject = new BmoProject();
		                	    PmProject pmProject = new PmProject(sFParams);
		                	    
	                    		pmProjects.doFetch(sql3);
	                    		while(pmProjects.next()) {
	                    				bmoProject = (BmoProject)pmProject.get(pmProjects.getInt("proj_projectid"));   
%>		            	
		                    	<TR>
		                    		<td><%= i %></td>
         			            	<%= HtmlUtil.formatReportCell(sFParams, bmoProject.getCode().toString(), BmFieldType.CODE) %>
			                    	<td colspan="2"><%= HtmlUtil.stringToHtml(bmoProject.getName().toString())%></td>
				                    <td colspan="2"><%= bmoProject.getBmoCustomer().getCode().toString()%> | <%= HtmlUtil.stringToHtml(bmoProject.getBmoCustomer().getDisplayName().toString())%></td>
				                    <td colspan="2"><%= HtmlUtil.stringToHtml(bmoProject.getBmoVenue().getName().toString())%></td>
				                    <td colspan="2"><%= bmoProject.getStartDate().toString()%></td>
	         			            <%= HtmlUtil.formatReportCell(sFParams, bmoProject.getEndDate().toString(), BmFieldType.STRING) %>
				                    <td colspan="2" align="right"><%= bmoProject.getBmoWFlow().getProgress().toString()%>%</td>
					            </TR>
			           <% 		i++;
	                    		} //while pmProjects
	                   %>
	                   
	                <TR> 
	                	<td colpan="12">&nbsp;</td>
	                </TR>
	                <TR>
		            	<td class="reportHeaderCell" colspan="12">
		            		CUENTAS POR COBRAR
		            	</td>
		            <TR>
		            <TR class="">
			               <td class="reportHeaderCell">#</td>
			               <td class="reportHeaderCell">Clave</td>
			               <td class="reportHeaderCell">Factura</td>                                          
			               <td class="reportHeaderCell">Cliente</td>  
			               <td class="reportHeaderCell">Folio</td> 
			               <td class="reportHeaderCell" width="5%">Fecha</td>
			               <td class="reportHeaderCell">Programaci&oacute;n</td>
			               <td class="reportHeaderCell">Estatus</td> 
			               <td class="reportHeaderCell">Pago</td> 
			               <td class="reportHeaderCell" align="right">Cargo</td> 
			               <td class="reportHeaderCell" align="right">Abono</td>
			               <td class="reportHeaderCell" align="right">Saldo</td>
		            </TR>
               	<%
               	BmoRaccount bmoRaccount = new BmoRaccount();
                PmRaccount pmRaccount = new PmRaccount(sFParams);
                i = 1;
               	sql4 = " SELECT * " +
               			" FROM " + SQLUtil.formatKind(sFParams, "raccounts ") +
               			" INNER JOIN " + SQLUtil.formatKind(sFParams, "raccounttypes")+" ON(ract_raccounttypeid=racc_raccounttypeid) " +
               			" INNER JOIN " + SQLUtil.formatKind(sFParams, "customers")+" ON(cust_customerid=racc_customerid) " +
               			" WHERE racc_raccountid > 0 " +
               			whereRacc +
               			" ORDER BY racc_receivedate ASC, racc_duedate ASC, racc_customerid ASC";
		            	System.out.println("sql444: "+ sql4);

		            	pmRaccounts.doFetch(sql4);
                   		while(pmRaccounts.next()) {
                   			bmoRaccount = (BmoRaccount)pmRaccount.get(pmRaccounts.getInt("racc_raccountid"));   

	                       	System.out.println("dentro de racc");

	                    	%>
	                    	<TR>
	                    		<td><%= i %></td>
                            	<%= HtmlUtil.formatReportCell(sFParams, bmoRaccount.getCode().toString(), BmFieldType.STRING) %>
                            	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getInvoiceno().toString(), BmFieldType.STRING)) %>
                            	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getBmoCustomer().getCode().toString()+" | "+bmoRaccount.getBmoCustomer().getDisplayName().toString(), BmFieldType.STRING)) %>
                            	<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getFolio().toString(), BmFieldType.STRING)) %>
                            	<%= HtmlUtil.formatReportCell(sFParams, bmoRaccount.getReceiveDate().toString(), BmFieldType.DATE) %>
                            	<%= HtmlUtil.formatReportCell(sFParams, " "+bmoRaccount.getDueDate().toString(), BmFieldType.DATE) %>
	                            <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	                            <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoRaccount.getPaymentStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %>
	                            <%= HtmlUtil.formatReportCell(sFParams, bmoRaccount.getAmount().toString(), BmFieldType.CURRENCY) %>
	                            <%= HtmlUtil.formatReportCell(sFParams, bmoRaccount.getBalance().toString(), BmFieldType.CURRENCY) %>
				            </TR>
		           <% 		i++;
                   		} //while pmRaccounts
                  %>

                    <TR> 
                    	<td colpan="12">&nbsp;</td>
                    </TR>
                    <%
                    	i++; 
                     %>

                           
</TABLE>  


<%
	pmCustomerEmails.close();
	pmCustomers.close(); 
	pmCustomerDates.close(); 
	pmProjects.close();
	pmRaccounts.close();
	//pmProjectsCount.close();
	//pmCustomersCount.close();
%>  
	<% if (print.equals("1")) { %>
	<script>
		window.print();
	</script>
	<% } %>

  </body>
</html>