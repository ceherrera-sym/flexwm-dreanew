<%@page import="com.flexwm.server.cr.PmCredit"%>
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.op.BmoConsultancy"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoProductPrice"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProductPrice"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>
<%
	// Variables
	String programTitle = "Importacion de Creditos";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) 
		populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) 
		action = request.getParameter("action");
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<% if (action.equalsIgnoreCase("")) { %>
	<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
	    <tr class="reportSubTitle">
	        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
	          <li>Se recomienda elaborar preparaci&oacute;n una hoja de calculo, seleccionar y pegar en el recuadro siguiente.</li>
	          <li><b>Los campos con '*' son oblitarorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (| = tab): 
	          Tipo Pedido* | Tipo de Flujo* | Ubicacion* | Tipo crédito* | Monto* |	Cliente* | Clave Promotor* | Fecha de Inicio | 1er Aval | 2do Aval | Empresa*
	        </td>
	    </tr>    
	    <TR>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="POST" name="listFilter">
				    <input type="hidden" name="action" value="revision">
	             	<tr class="" height="35">
					    <td colspan="10" align="center" width="100%">
							<textarea name="populateData" width="100%" cols="100" rows="30"></textarea>
				        </td>
				    </tr>
	                <tr class="">
	                    <td align="center" colspan="4" height="35" valign="middle">
	                        <input class="formSaveButton" type="submit" value="SIGUIENTE">
	                    </td>
	                </tr>			    
	                </FORM>								
	            </TABLE>
	        </TD>
	    </TR>
	</TABLE>
<% 
	} else if (action.equalsIgnoreCase("revision")) {
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {
			String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	          //Tipo Pedido* | Tipo de Flujo* | Tipo crédito* | Monto* | Moneda* | Nombre Cliente* | Nombre Promotor* | Fecha de Inicio | 1er Aval | 2do Aval | Empresa*

			String cred_orderTypeName = "";
			String cred_wFlowTypeName = "";
			String cred_locationName = "";
			String cred_creditTypeName = "";
			String cred_amount = "";
			String cred_currencyCode = "";
			String cred_customerName = "";
			String cred_userName = "";
			String cred_startDate = "";
			String cred_guaranteeOne = "";
			String cred_guaranteeTwo = "";
			String cred_companyName = "";
			String errors = "";
			int countGuarantees = 0;
			
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Tipo Pedido</td>
		                    <td class="reportHeaderCell">Tipo de Flujo</td>
		                    <td class="reportHeaderCell">Ubicación</td>
		                    <td class="reportHeaderCell">Tipo crédito</td>
   		                    <td class="reportHeaderCell">Monto</td>
   		                   	<td class="reportHeaderCell">Moneda</td>
		                    <td class="reportHeaderCell">Nombre Cliente</td>
							<td class="reportHeaderCell">Clave Promotor</td>
		                    <td class="reportHeaderCell">Fecha de Inicio</td>
		                    <td class="reportHeaderCell">1er Aval</td>
		                    <td class="reportHeaderCell">2do Aval</td>
		                    <td class="reportHeaderCell">Empresa</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%  int countError = 0;
		        		while (inputData.hasMoreTokens() && i < 2000) {

		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			

		        			// Recuperar valores
		        			cred_orderTypeName = tabs.nextToken();
							cred_wFlowTypeName = tabs.nextToken();
							cred_locationName = tabs.nextToken();
							cred_creditTypeName = tabs.nextToken();
							cred_amount = tabs.nextToken();
							cred_currencyCode = tabs.nextToken();
							cred_customerName = tabs.nextToken();
							cred_userName = tabs.nextToken();
							cred_startDate = tabs.nextToken();
							cred_guaranteeOne = tabs.nextToken();
							cred_guaranteeTwo = tabs.nextToken();
							cred_companyName = tabs.nextToken();
							
							// Formatear; quitar espacios
							cred_orderTypeName = cred_orderTypeName.trim(); 
							cred_wFlowTypeName = cred_wFlowTypeName.trim();
							cred_locationName = cred_locationName.trim();
							cred_creditTypeName =cred_creditTypeName.trim();
							cred_amount = cred_amount.trim();
							cred_currencyCode = cred_currencyCode.trim();
							cred_customerName = cred_customerName.trim();
							cred_userName = cred_userName.trim();
							cred_startDate = cred_startDate.trim();
							cred_guaranteeOne = cred_guaranteeOne.trim();
							cred_guaranteeTwo = cred_guaranteeTwo.trim();
							cred_companyName = cred_companyName.trim();
							int cred_locationid = 0;

							System.out.println("--- DATOS A GUARDAR ---");
							System.out.println("Tipo pedido:"+cred_orderTypeName);
							System.out.println("Tipo de Flujo:"+cred_wFlowTypeName);
							System.out.println("ubicacion:"+cred_locationName);
							System.out.println("Tipo crédito:"+cred_creditTypeName);
							System.out.println("Monto:"+cred_amount);
							System.out.println("Moneda:"+cred_currencyCode);
							System.out.println("Cliente:"+cred_customerName);
							System.out.println("Promotor:"+cred_userName);
							System.out.println("Fecha:"+cred_startDate);
							System.out.println("1er Aval:"+cred_guaranteeOne);
							System.out.println("2do Aval:"+cred_guaranteeTwo);
							System.out.println("Empresa:"+cred_companyName);


		        			// Validar tipo de pedido*
		        			if (!cred_orderTypeName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT ortp_name FROM ordertypes " +
			          			      " WHERE ortp_name = '" + cred_orderTypeName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Tipo de Pedido no Existe: " + cred_orderTypeName+ " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "El Tipo de Pedido es requerido.";
		        				countError++;
		        			}

		        			// Validar Tipo de FLujo*
		        			if (!cred_wFlowTypeName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM wflowtypes " +
			          			      " WHERE wfty_name = '" + cred_wFlowTypeName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Tipo de Flujo no Existe: " + cred_wFlowTypeName+ " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "El Tipo de flujo es requerido. ";
		        				countError++;
		        			}
		        			
		        			// Validar Ubicacion*
		        			if (!cred_locationName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM locations " +
			          			      " WHERE loct_name = '" + cred_locationName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Ubicacion no Existe: " + cred_locationName + ".";
			        				countError++;
			        			} else {
			        				cred_locationid = pmConn2.getInt("loct_locationid");
			        			}
		        			} else {
		        				errors = "La Ubicacion  es requerida. ";
		        				countError++;
		        			}
		        			
		        			// Validar Tipo de credito*
		        			if (!cred_creditTypeName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM credittypes " +
			          			      " WHERE crty_name = '" + cred_creditTypeName + "' AND crty_locationid = " + cred_locationid;
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Tipo de crédito no Existe: " + cred_creditTypeName + ".";
			        				countError++;
			        			} else {
			        				countGuarantees = pmConn2.getInt("crty_guarantees");
			        			}
		        			} else {
		        				errors = "El Tipo de crédito es requerido. ";
		        				countError++;
		        			}
		        			
		        			// Validar monto*
		        			if (cred_amount.equalsIgnoreCase("empty")) {
		        				errors = "El Monto del crédito debe ser mayor a cero. ";
		        				countError++;
		        			}
		        			
		        			// Validar Moneda*
		        			if (!cred_currencyCode.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM currencies " +
			          			      " WHERE cure_code = '" + cred_currencyCode + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Moneda no Existe: " + cred_currencyCode + " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "La Moneda es requerida. ";
		        				countError++;
		        			}
		        			
		        			
		        			// Validar cliente*
		        			if (!cred_customerName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM customers " +	
			          			      " WHERE cust_displayname = '" + cred_customerName + "' AND cust_locationid = " + cred_locationid;
								System.out.println("clienteSQL:"+sql);

			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors += "El Cliente no Existe: " + cred_customerName + " - ";;
			        			}
		        			} else {
		        				errors = "El Cliente es requerido. ";
		        				countError++;
		        			}
		        			// Validar vendedor*
		        			if (!cred_userName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM users " +	
			          			      " WHERE user_code = '" + cred_userName + "' AND user_locationid = " + cred_locationid;
			        		
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors += "El Vendedor no Existe: " + cred_userName + " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "El Vendedor es requerido. ";
		        				countError++;
		        			}
		        			
		        			// Validar fecha, nada que validar
		        			
		        			// Validar aval1/2*
		        			if (countGuarantees == 1 || countGuarantees == 2) {
			        			if (!cred_guaranteeOne.equalsIgnoreCase("empty")) {
				        			sql = " SELECT * FROM customers " +	
				          			      " WHERE cust_displayname = '" + cred_guaranteeOne + "' AND cust_locationid = " + cred_locationid;
				        		
				        			pmConn2.doFetch(sql);
				        			if (!pmConn2.next()) {
				        				errors += "El Aval 1 no Existe: " + cred_guaranteeOne + " - ";
				        				countError++;
				        			}
			        			}
			        			
			        			if (countGuarantees == 2) {
				        			if (!cred_guaranteeTwo.equalsIgnoreCase("empty")) {
					        			sql = " SELECT * FROM customers " +	
					          			      " WHERE cust_displayname = '" + cred_guaranteeTwo + "' AND cust_locationid = " + cred_locationid;
					        			
										System.out.println("AVAL 2_SQL:"+sql);

					        			pmConn2.doFetch(sql);
					        			if (!pmConn2.next()) {
					        				errors += "El Aval 2 no Existe: " + cred_guaranteeTwo + " - ";
					        				countError++;
					        			}
				        			}
			        			}
		        			} else {
		        				if (countGuarantees != 0) {
			        				errors = "Debe seleccionar " + countGuarantees + " Aval(es) correspondiente(s) este tipo de credito(máximo son 2 avales).";
			        				countError++;
		        				}
		        			}
		        			// Validar Empresa*
		        			if (!cred_companyName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM companies " +
			          			      " WHERE comp_name = '" + cred_companyName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Empresa no Existe: " + cred_companyName + " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "La Empresa es requerida. ";
		        				countError++;
		        			}
		        			

		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_orderTypeName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_wFlowTypeName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_locationName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_creditTypeName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_amount, BmFieldType.CURRENCY)%>
								<%=HtmlUtil.formatReportCell(sFParams, cred_currencyCode, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, cred_customerName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_userName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_startDate, BmFieldType.DATE)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_guaranteeOne, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_guaranteeTwo, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cred_companyName, BmFieldType.STRING)%>
		    					
		    					
	    				    	<td>
	    				    		<font color="red">
		    				    		<%= errors %>
		    				  	  		<%= ((countError > 0 ? "(Verifique que los datos cargados sean de la ubicaci&oacute;n correcta.)" : "")) %>
		    				  	  	</font>
	    				  	 	</td>
		    				</TR>
		    		<%
		    			i++;        		
		        	}
		            %>
			</TABLE>
		
			<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
				<FORM action="?" method="POST" name="listFilter">	
				<input type="hidden" name="action" value="populate">
				<input type="hidden" name="populateData" value="<%= populateData %>">			
				<tr class="">
				    <td align="center" colspan="4" height="35" valign="middle">
				    <% 	if (!(countError > 0)) { %>
				       		<input type="submit" class="formSaveButton" value="GUARDAR">
				    <% 	} %>    
				    </td>
				</tr>			    
				</FORM>								
			</TABLE>
		
		<% 
		} catch (Exception e) {
			throw new SFException(e.toString());
		} finally {
			pmConn2.close();
		}
		
	} else if (action.equals("populate")) {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {
			pmConn.disableAutoCommit();
			String s = "";
			
			String cred_orderTypeName = "";
			String cred_wFlowTypeName = "";
			String cred_locationName = "";
			String cred_creditTypeName = "";
			String cred_amount = "";
			String cred_currencyCode = "";
			String cred_customerName = "";
			String cred_userName = "";
			String cred_startDate = "";
			String cred_guaranteeOne = "";
			String cred_guaranteeTwo = "";
			String cred_companyName = "";

			// Llaves foraneas
			int cred_ordertypeid = 0;
			int cred_wflowtypeid = 0;
			int cred_locationid = 0;
			int cred_credittypeid = 0;
			int cred_currencyid = 0;
			int cred_customerid = 0;
			int cred_userid = 0;
			int cred_guaranteeOneId = 0;
			int cred_guaranteeTwoId = 0;
			int cred_companyid = 0;
			int countGuarantees = 0;

			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
				BmoCredit bmoCredit = new BmoCredit();
				PmCredit pmCredit = new PmCredit(sFParams);
				
				cred_orderTypeName = "";
				cred_wFlowTypeName = "";
				cred_locationName = "";
				cred_creditTypeName = "";
				cred_amount = "";
				cred_currencyCode = "";
				cred_customerName = "";
				cred_userName = "";
				cred_startDate = "";
				cred_guaranteeOne = "";
				cred_guaranteeTwo = "";
				cred_companyName = "";
				countGuarantees = 0;
				
				// Llaves foraneas
				cred_ordertypeid = 0;
				cred_wflowtypeid = 0;
				cred_locationid = 0;
				cred_credittypeid = 0;
				cred_currencyid = 0;
				cred_customerid = 0;
				cred_userid = 0;
				cred_guaranteeOneId = 0;
				cred_guaranteeTwoId = 0;
				cred_companyid = 0;

				// Recuperar valores
    			cred_orderTypeName = tabs.nextToken();
				cred_wFlowTypeName = tabs.nextToken();
				cred_locationName = tabs.nextToken();
				cred_creditTypeName = tabs.nextToken();
				cred_amount = tabs.nextToken();
				cred_currencyCode = tabs.nextToken();
				cred_customerName = tabs.nextToken();
				cred_userName = tabs.nextToken();
				cred_startDate = tabs.nextToken();
				cred_guaranteeOne = tabs.nextToken();
				cred_guaranteeTwo = tabs.nextToken();
				cred_companyName = tabs.nextToken();
				
				// Formatear; quitar espacios
				cred_orderTypeName = cred_orderTypeName.trim(); 
				cred_wFlowTypeName = cred_wFlowTypeName.trim();
				cred_locationName = cred_locationName.trim();
				cred_creditTypeName =cred_creditTypeName.trim();
				cred_amount = cred_amount.trim();
				cred_currencyCode = cred_currencyCode.trim();
				cred_customerName = cred_customerName.trim();
				cred_userName = cred_userName.trim();
				cred_startDate = cred_startDate.trim();
				cred_guaranteeOne = cred_guaranteeOne.trim();
				cred_guaranteeTwo = cred_guaranteeTwo.trim();
				cred_companyName = cred_companyName.trim();
				
				System.out.println("--- DATOS A GUARDAR(populate) ---");
				System.out.println("Tipo pedido:"+cred_orderTypeName);
				System.out.println("Tipo de Flujo:"+cred_wFlowTypeName);
				System.out.println("Ubicacion:"+cred_locationName);
				System.out.println("Tipo crédito:"+cred_creditTypeName);
				System.out.println("Monto:"+cred_amount);
				System.out.println("Moneda:"+cred_currencyCode);
				System.out.println("Cliente:"+cred_customerName);
				System.out.println("Promotor:"+cred_userName);
				System.out.println("Fecha:"+cred_startDate);
				System.out.println("1er Aval:"+cred_guaranteeOne);
				System.out.println("2do Aval:"+cred_guaranteeTwo);
				System.out.println("Empresa:"+cred_companyName);

				// Validar tipo de pedido*
    			if (!cred_orderTypeName.equalsIgnoreCase("empty")) {
        			sql = " SELECT ortp_ordertypeid FROM ordertypes " +
          			      " WHERE ortp_name = '" + cred_orderTypeName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_ordertypeid = pmConn2.getInt("ortp_ordertypeid");
        			}
    			}

    			// Validar Tipo de FLujo*
    			if (!cred_wFlowTypeName.equalsIgnoreCase("empty")) {
        			sql = " SELECT wfty_wflowtypeid FROM wflowtypes " +
          			      " WHERE wfty_name = '" + cred_wFlowTypeName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_wflowtypeid = pmConn2.getInt("wfty_wflowtypeid");
        			}
    			}
    			
    			// Validar Ubicacion*
    			if (!cred_locationName.equalsIgnoreCase("empty")) {
        			sql = " SELECT loct_locationid FROM locations " +
          			      " WHERE loct_name = '" + cred_locationName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_locationid = pmConn2.getInt("loct_locationid");
        			}
    			}
    			
    			// Validar Tipo de credito*
    			if (!cred_creditTypeName.equalsIgnoreCase("empty")) {
        			sql = " SELECT crty_credittypeid, crty_guarantees FROM credittypes " +
          			      " WHERE crty_name = '" + cred_creditTypeName + "' AND crty_locationid = " + cred_locationid;
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_credittypeid = pmConn2.getInt("crty_credittypeid");
        				countGuarantees = pmConn2.getInt("crty_guarantees");
        			}
    			}
    			
    			// Validar Moneda*
    			if (!cred_currencyCode.equalsIgnoreCase("empty")) {
        			sql = " SELECT cure_currencyid FROM currencies " +
          			      " WHERE cure_code = '" + cred_currencyCode + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_currencyid = pmConn2.getInt("cure_currencyid");
        			}
    			}
    			
    			// Validar cliente*
    			if (!cred_customerName.equalsIgnoreCase("empty")) {
        			sql = " SELECT cust_customerid FROM customers " +	
          			      " WHERE cust_displayname = '" + cred_customerName + "' AND cust_locationid = " + cred_locationid;
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_customerid = pmConn2.getInt("cust_customerid");
        			}
    			}
    			
    			// Validar vendedor*
    			if (!cred_userName.equalsIgnoreCase("empty")) {
        			sql = " SELECT user_userid FROM users " +	
          			      " WHERE user_code = '" + cred_userName + "' AND user_locationid = " + cred_locationid;
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_userid = pmConn2.getInt("user_userid");
        			}
    			}
    			
    			// Validar aval1/2*
    			if (countGuarantees == 1 || countGuarantees == 2) {
        			if (!cred_guaranteeOne.equalsIgnoreCase("empty")) {
	        			sql = " SELECT cust_customerid FROM customers " +	
	          			      " WHERE cust_displayname = '" + cred_guaranteeOne + "' AND cust_locationid = " + cred_locationid;
	        		
	        			pmConn2.doFetch(sql);
	        			if (pmConn2.next()) {
	        				cred_guaranteeOneId = pmConn2.getInt("cust_customerid");
	        			}
        			}
        			
        			if (countGuarantees == 2) {
	        			if (!cred_guaranteeTwo.equalsIgnoreCase("empty")) {
		        			sql = " SELECT cust_customerid FROM customers " +	
		          			      " WHERE cust_displayname = '" + cred_guaranteeTwo + "' AND cust_locationid = " + cred_locationid;
		        		
		        			pmConn2.doFetch(sql);
		        			if (pmConn2.next()) {
		        				cred_guaranteeTwoId = pmConn2.getInt("cust_customerid");
		        			}
	        			}
        			}
    			}
    			
    			// Validar Empresa*
    			if (!cred_companyName.equalsIgnoreCase("empty")) {
        			sql = " SELECT comp_companyid FROM companies " +
          			      " WHERE comp_name = '" + cred_companyName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cred_companyid = pmConn2.getInt("comp_companyid");
        			}
    			}
    			
    			bmoCredit.getOrderTypeId().setValue(cred_ordertypeid);
    			bmoCredit.getWFlowTypeId().setValue(cred_wflowtypeid);
    			bmoCredit.getLocationId().setValue(cred_locationid);
    			bmoCredit.getCreditTypeId().setValue(cred_credittypeid);
    			bmoCredit.getAmount().setValue(cred_amount);
    			bmoCredit.getCurrencyId().setValue(cred_currencyid);
    			bmoCredit.getCustomerId().setValue(cred_customerid);
    			bmoCredit.getSalesUserId().setValue(cred_userid);
    			bmoCredit.getStartDate().setValue(cred_startDate);
    			bmoCredit.getGuaranteeOneId().setValue(cred_guaranteeOneId);
    			bmoCredit.getGuaranteeTwoId().setValue(cred_guaranteeTwoId);
    			bmoCredit.getCompanyId().setValue(cred_companyid);

				System.out.println("Datos a mandar al server:");
				System.out.println("Tipo pedido:"+bmoCredit.getOrderTypeId().toInteger());
				System.out.println("Tipo de Flujo:"+bmoCredit.getWFlowTypeId().toInteger());
				System.out.println("Ubicacion:"+bmoCredit.getLocationId().toInteger());
				System.out.println("Tipo crédito:"+bmoCredit.getCreditTypeId().toInteger());

				System.out.println("Monto:"+bmoCredit.getAmount().toDouble());
				System.out.println("Moneda:"+bmoCredit.getCurrencyId().toInteger());
				System.out.println("Cliente:"+bmoCredit.getCustomerId().toInteger());
				System.out.println("Promotor:"+bmoCredit.getSalesUserId().toInteger());
				System.out.println("Fecha:"+bmoCredit.getStartDate().toString());
				System.out.println("1er Aval:"+bmoCredit.getGuaranteeOneId().toInteger());
				System.out.println("2do Aval:"+bmoCredit.getGuaranteeTwoId().toInteger());
				System.out.println("Empresa:"+bmoCredit.getCompanyId().toInteger());
				
    			pmCredit.save(pmConn, bmoCredit, bmUpdateResult);
			}
			
			if (!bmUpdateResult.hasErrors())
				pmConn.commit();
		
		} catch (Exception e) {
			pmConn.rollback();
// 			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		
		response.sendRedirect("cred_dacredito_batch.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
			
	}  else if (action.equals("complete")) { %>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>		
				<td colspan="4" align="center" class="reportTitle">
				    &nbsp;
				    <% if (errorSave.equals("")) { %>
						Datos Cargados OK.
					<% } else { %>
						Existen Errores de Carga: <%= errorSave %>
					<% } %>
					<br>
					<a href="?">Reiniciar</a>
				</td>
			</tr>
			</table>
	<% } %>
</body>
</html>