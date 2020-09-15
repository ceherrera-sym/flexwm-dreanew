<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
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
	String programTitle = "Importacion de Clientes";
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
<table border="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<% 	if (action.equalsIgnoreCase("")) { %>
	<table border="0"   class="detailStart">	
   		<tr class="reportSubTitle">
        	<td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
          		<li>Se recomienda elaborar preparaci&oacute;n una hoja de calculo, seleccionar y pegar en el recuadro siguiente.</li>
	          	<li><b>Los campos con '*' son oblitarorios. Colocar la palabra "empty" para los campos vacíos.</b></li> 
	          	<li>
	          		Formato (| = tab): Ubicación* | Tipo de Cliente* | Genero | Nombre* | Apellido P.* | Apellido M. | Email | Movil | Vendedor* (promotor) | CURP | INE* | Fecha de Nacimiento  | Tipo direccion | Calle | No. | Colonia | CP. | Ciudad | Tipo | Descripción
				</li>
	        </td>
	    </tr>    
	    <TR>
	        <td colspan="3">  
	            <TABLE >
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

			String cust_type = "";
			String cust_genere = "";
			String cust_name = "";
			String cust_fatherlastname = "";
			String cust_motherlastname = "";
			String cust_email = "";
			String cust_mobile = "";
			String cust_salesUser = "";
			String cust_location = "";
			String cust_curp  = "";
			String cust_nss = "";
			String cust_birthdate = "";
			
			String cuad_type = "";
			String cuad_street = "";
			String cuad_number = "";
			String cuad_neighborhbood = "";
			String cuad_zip = "";
			String cuad_city = "";
			String cuad_description = "";

			String errors = "";
      		// Ubicación* | Tipo de Cliente* | Genero | Nombre* | Apellido P.* | Apellido M. | Email | Movil | Vendedor* (promotor) | CURP | INE* | Fecha de Nacimiento  
      		// | Tipo direccion | calle | No | Colonia | CP. | Ciudad | Descripción

		%>
		<table width="80%" border="0" align="center"  class="">
	   		<TR>
	       		<td colspan="4">  
	            	<TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
	            		<tr valign="middle" align="center" class="">
	            	 		<td class="reportHeaderCell">&nbsp;#</td>
 		   		            <td class="reportHeaderCell">Ubicaci&oacute;n</td>
	            	 		<td class="reportHeaderCell">Tipo</td>
	            	 		<td class="reportHeaderCell">Genero</td>
	            	 		<td class="reportHeaderCell">Nombre</td>
		                    <td class="reportHeaderCell">Paterno</td>
		                    <td class="reportHeaderCell">Materno</td>
   		                    <td class="reportHeaderCell">Email</td>
   		                    <td class="reportHeaderCell">Movil</td>
   		                    <td class="reportHeaderCell">Vendedor</td>
   		                    <td class="reportHeaderCell">CURP</td>
   		                    <td class="reportHeaderCell">INE</td>
   		                    <td class="reportHeaderCell">Nacimiento</td>
   		                    
		                    <td class="reportHeaderCell">Tipo</td>
		                    <td class="reportHeaderCell">Calle</td>
		                    <td class="reportHeaderCell">No.</td>
		                    <td class="reportHeaderCell">Colonia</td>
		                    <td class="reportHeaderCell">C.P.</td>
		                    <td class="reportHeaderCell">Ciudad</td>
		                    <td class="reportHeaderCell">Descripcion</td>
		                    <td class="reportHeaderCell">Errors</td>
		                </TR>
		            <%  int countError = 0;
		        		while (inputData.hasMoreTokens() && i < 2000) {

		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			

							int cust_locationid = 0;

		        			// Recuperar valores
		        			cust_location = tabs.nextToken();
		        			cust_type = tabs.nextToken();
							cust_genere = tabs.nextToken();
							cust_name = tabs.nextToken();
							cust_fatherlastname = tabs.nextToken();
							cust_motherlastname = tabs.nextToken();
							cust_email = tabs.nextToken();
							cust_mobile = tabs.nextToken();
							cust_salesUser = tabs.nextToken();
							cust_curp  = tabs.nextToken();
							cust_nss = tabs.nextToken();
							cust_birthdate = tabs.nextToken();
							
							cuad_type = tabs.nextToken();
							cuad_street = tabs.nextToken();
							cuad_number = tabs.nextToken();
							cuad_neighborhbood = tabs.nextToken();
							cuad_zip = tabs.nextToken();
							cuad_city = tabs.nextToken();
							cuad_description = tabs.nextToken();
							
							// Formatear; quitar espacios
							cust_location = cust_location.trim();
							cust_type = cust_type.trim();
							cust_genere = cust_genere.trim();
							cust_name = cust_name.trim();
							cust_fatherlastname = cust_fatherlastname.trim();
							cust_motherlastname = cust_motherlastname.trim();
							cust_email = cust_email.trim();
							cust_mobile = cust_mobile.trim();
							cust_salesUser = cust_salesUser.trim();
							cust_curp = cust_curp.trim();
							cust_nss = cust_nss.trim();
							cust_birthdate = cust_birthdate.trim();
							
							cuad_type = cuad_type.trim();
							cuad_street = cuad_street.trim();
							cuad_number = cuad_number.trim();
							cuad_neighborhbood = cuad_neighborhbood.trim();
							cuad_zip = cuad_zip.trim();
							cuad_city = cuad_city.trim();
							cuad_description = cuad_description.trim();
							
							System.out.println("--- DATOS A GUARDAR ---");
							System.out.println("Ubicación:"+cust_location);
							System.out.println("Tipo Cliente:"+cust_type);
							System.out.println("Genero:"+cust_genere);
							System.out.println("Nombre:"+cust_name);
							System.out.println("Apellido P:"+cust_fatherlastname);
							System.out.println("Apellido M:"+cust_motherlastname);
							System.out.println("Email:"+cust_email);
							System.out.println("Movil:"+cust_mobile);
							System.out.println("Vendedor:"+cust_salesUser);
							System.out.println("CURP:"+cust_curp);
							System.out.println("INE:"+cust_nss);
							System.out.println("Fecha de Nacimiento:"+cust_birthdate);
							System.out.println("Tipo direccion:"+cuad_type);
							System.out.println("calle:"+cuad_street);
							System.out.println("No:"+cuad_number);
							System.out.println("Colonia:"+cuad_neighborhbood);
							System.out.println("CP:"+cuad_zip);
							System.out.println("Ciudad:"+cuad_city);
							System.out.println("Descripción:"+cuad_description);

							// Validar Ubicacion*
		        			if (!cust_location.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM locations " +
			          			      " WHERE loct_name = '" + cust_location + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Ubicacion no Existe: " + cust_location + ".";
			        				countError++;
			        			} else {
			        				cust_locationid = pmConn2.getInt("loct_locationid");
			        			}
		        			} else {
		        				errors = "La Ubicacion  es requerida. ";
		        				countError++;
		        			}
							
		        			// Validar genero(titulo)*
		        			if (!cust_genere.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM titles " +
			          			      " WHERE titl_name = '" + cust_genere + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Genero no Existe: " + cust_genere + ".";
			        				countError++;
			        			}
		        			}
		        			
		        			// Validar vendedor*
		        			if (!cust_salesUser.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM users " +	
			          			      " WHERE user_code = '" + cust_salesUser + "' AND user_locationid = " + cust_locationid;
			        		
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors += "El Vendedor no Existe: " + cust_salesUser + " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "El Vendedor es requerido. ";
		        				countError++;
		        			}
		        			
		        			// Validar ciudad*
		        			if (!cuad_city.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM cities " +	
			          			      " WHERE city_name LIKE '" + cuad_city + "'";
			        			System.out.println("sql_city: "+cuad_city);

			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors += "La ciudad no Existe: " + cuad_city + " - ";
			        				countError++;
			        			}
		        			} else {
		        				errors = "La ciudad es requerido. ";
		        				countError++;
		        			}
		        			
		        			System.out.println("--- DATOS A GUARDAR ---");
							System.out.println("Ubicación:"+cust_location);
							System.out.println("Tipo Cliente:"+cust_type);
							System.out.println("Genero:"+cust_genere);
							System.out.println("Nombre:"+cust_name);
							System.out.println("Apellido P:"+cust_fatherlastname);
							System.out.println("Apellido M:"+cust_motherlastname);
							System.out.println("Email:"+cust_email);
							System.out.println("Movil:"+cust_mobile);
							System.out.println("Vendedor:"+cust_salesUser);
							System.out.println("CURP:"+cust_curp);
							System.out.println("INE:"+cust_nss);
							System.out.println("Fecha de Nacimiento:"+cust_birthdate);
							System.out.println("Tipo direccion:"+cuad_type);
							System.out.println("calle:"+cuad_street);
							System.out.println("No:"+cuad_number);
							System.out.println("Colonia:"+cuad_neighborhbood);
							System.out.println("CP:"+cuad_zip);
							System.out.println("Ciudad:"+cuad_city);
							System.out.println("Descripción:"+cuad_description);
		    		%>
		    				<TR class="reportCellEven">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_location, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_type, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_genere, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_fatherlastname, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, cust_motherlastname, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, cust_email, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_mobile, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_salesUser, BmFieldType.DATE)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_curp, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_nss, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_birthdate, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_type, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_street, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_number, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_neighborhbood, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_zip, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_city, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuad_description, BmFieldType.STRING)%>
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

			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
				BmoCustomer bmoCustomer = new BmoCustomer();
				PmCustomer pmCustomer = new PmCustomer(sFParams);
				
				BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
				PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
				
				String cust_location = "";
				String cust_type = "";
				String cust_genere = "";
				String cust_name = "";
				String cust_fatherlastname = "";
				String cust_motherlastname = "";
				String cust_email = "";
				String cust_mobile = "";
				String cust_salesUser = "";
				String cust_curp  = "";
				String cust_nss = "";
				String cust_birthdate = "";
				
				String cuad_type = "";
				String cuad_street = "";
				String cuad_number = "";
				String cuad_neighborhbood = "";
				String cuad_zip = "";
				String cuad_city = "";
				String cuad_description = "";
				
				// Llaves foraneas
				int cust_locationId = 0;
				int cust_titleId = 0;
				int cust_salesUserId = 0;
				int cuad_cityId = 0;

				// Recuperar valores
    			cust_location = tabs.nextToken();
    			cust_type = tabs.nextToken();
				cust_genere = tabs.nextToken();
				cust_name = tabs.nextToken();
				cust_fatherlastname = tabs.nextToken();
				cust_motherlastname = tabs.nextToken();
				cust_email = tabs.nextToken();
				cust_mobile = tabs.nextToken();
				cust_salesUser = tabs.nextToken();
				cust_curp  = tabs.nextToken();
				cust_nss = tabs.nextToken();
				cust_birthdate = tabs.nextToken();
				
				cuad_type = tabs.nextToken();
				cuad_street = tabs.nextToken();
				cuad_number = tabs.nextToken();
				cuad_neighborhbood = tabs.nextToken();
				cuad_zip = tabs.nextToken();
				cuad_city = tabs.nextToken();
				cuad_description = tabs.nextToken();
				
				// Formatear; quitar espacios
				cust_location = cust_location.trim();
				cust_type = cust_type.trim();
				cust_genere = cust_genere.trim();
				cust_name = cust_name.trim();
				cust_fatherlastname = cust_fatherlastname.trim();
				cust_motherlastname = cust_motherlastname.trim();
				cust_email = cust_email.trim();
				cust_mobile = cust_mobile.trim();
				cust_salesUser = cust_salesUser.trim();
				cust_curp = cust_curp.trim();
				cust_nss = cust_nss.trim();
				cust_birthdate = cust_birthdate.trim();
				
				cuad_type = cuad_type.trim();
				cuad_street = cuad_street.trim();
				cuad_number = cuad_number.trim();
				cuad_neighborhbood = cuad_neighborhbood.trim();
				cuad_zip = cuad_zip.trim();
				cuad_city = cuad_city.trim();
				cuad_description = cuad_description.trim();
				
				System.out.println("--- DATOS A GUARDAR ---");
				System.out.println("Ubicación:"+cust_location);
				System.out.println("Tipo Cliente:"+cust_type);
				System.out.println("Genero:"+cust_genere);
				System.out.println("Nombre:"+cust_name);
				System.out.println("Apellido P:"+cust_fatherlastname);
				System.out.println("Apellido M:"+cust_motherlastname);
				System.out.println("Email:"+cust_email);
				System.out.println("Movil:"+cust_mobile);
				System.out.println("Vendedor:"+cust_salesUser);
				System.out.println("CURP:"+cust_curp);
				System.out.println("INE:"+cust_nss);
				System.out.println("Fecha de Nacimiento:"+cust_birthdate);
				System.out.println("Tipo direccion:"+cuad_type);
				System.out.println("calle:"+cuad_street);
				System.out.println("No:"+cuad_number);
				System.out.println("Colonia:"+cuad_neighborhbood);
				System.out.println("CP:"+cuad_zip);
				System.out.println("Ciudad:"+cuad_city);
				System.out.println("Descripción:"+cuad_description);

				// Validar Ubicacion*
    			if (!cust_location.equalsIgnoreCase("empty")) {
        			sql = " SELECT loct_locationid FROM locations " +
          			      " WHERE loct_name = '" + cust_location + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cust_locationId = pmConn2.getInt("loct_locationid");
        			}
    			}
				
    			// Validar genero(titulo)*
    			if (!cust_genere.equalsIgnoreCase("empty")) {
        			sql = " SELECT titl_titleid FROM titles " +
          			      " WHERE titl_name = '" + cust_genere + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cust_titleId = pmConn2.getInt("titl_titleid");
        			}
    			}
				
    			// Validar vendedor*
    			if (!cust_salesUser.equalsIgnoreCase("empty")) {
        			sql = " SELECT user_userid FROM users " +	
          			      " WHERE user_code = '" + cust_salesUser + "' AND user_locationid = " + cust_locationId;
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cust_salesUserId = pmConn2.getInt("user_userid");
        			}
    			}
    			
    			if (!cuad_type.equalsIgnoreCase("empty")) {
	    			// Validar ciudad*
	    			if (!cuad_city.equalsIgnoreCase("empty")) {
	        			sql = " SELECT city_cityid FROM cities " +	
	          			      " WHERE city_name = '" + cuad_city + "'";
	        			pmConn2.doFetch(sql);
	        			if (pmConn2.next()) {
	        				cuad_cityId = pmConn2.getInt("city_cityid");
	        			}
	    			}
    			}
    			
    			System.out.println("\n\nDatos a mandar al server:");
    			bmoCustomer.getLocationId().setValue(cust_locationId);
    			bmoCustomer.getCustomertype().setValue(cust_type);
    			bmoCustomer.getTitleId().setValue(cust_titleId);
    			bmoCustomer.getFirstname().setValue(cust_name);
    			bmoCustomer.getFatherlastname().setValue(cust_fatherlastname);
    			bmoCustomer.getMotherlastname().setValue(cust_motherlastname);
    			bmoCustomer.getEmail().setValue(cust_email);
    			bmoCustomer.getMobile().setValue(cust_mobile);
    			bmoCustomer.getSalesmanId().setValue(cust_salesUserId);
    			bmoCustomer.getCurp().setValue(cust_curp);
    			bmoCustomer.getNss().setValue(cust_nss);
    			bmoCustomer.getBirthdate().setValue(cust_birthdate);
    			
				System.out.println("Ubicación:"+bmoCustomer.getLocationId().toInteger());
				System.out.println("Tipo Cliente:"+bmoCustomer.getCustomertype().toString());
				System.out.println("Genero:"+bmoCustomer.getTitleId().toInteger());
				System.out.println("Nombre:"+bmoCustomer.getFirstname().toString());
				System.out.println("Apellido P:"+bmoCustomer.getFatherlastname().toString());
				System.out.println("Apellido M:"+bmoCustomer.getMotherlastname().toString());
				System.out.println("Email:"+bmoCustomer.getEmail().toString());
				System.out.println("Movil:"+bmoCustomer.getMobile().toString());
				System.out.println("Vendedor:"+bmoCustomer.getSalesmanId().toInteger());
				System.out.println("CURP:"+bmoCustomer.getCurp().toString());
				System.out.println("INE:"+bmoCustomer.getNss().toString());
				System.out.println("Fecha de Nacimiento:"+bmoCustomer.getBirthdate().toString());
    			pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult); // guardar para que retorne id del cliente
    			bmoCustomer.setId(bmUpdateResult.getId());
    			System.out.println("ID_CLIENTE:"+bmoCustomer.getId());

    			// Direccion del cliente
    			if (!cuad_type.equalsIgnoreCase("empty")) {
	    			bmoCustomerAddress.getCustomerId().setValue(bmoCustomer.getId());
	    			bmoCustomerAddress.getType().setValue(cuad_type);
	    			bmoCustomerAddress.getStreet().setValue(cuad_street);
	    			bmoCustomerAddress.getNumber().setValue(cuad_number);
	    			bmoCustomerAddress.getNeighborhood().setValue(cuad_neighborhbood);
	    			bmoCustomerAddress.getZip().setValue(cuad_zip);
	    			bmoCustomerAddress.getCityId().setValue(cuad_cityId);
	    			bmoCustomerAddress.getDescription().setValue(cuad_description);
	    			System.out.println("Tipo direccion:"+bmoCustomerAddress.getType().toString());
					System.out.println("calle:"+bmoCustomerAddress.getStreet().toString());
					System.out.println("No:"+bmoCustomerAddress.getNumber().toString());
					System.out.println("Colonia:"+bmoCustomerAddress.getNeighborhood().toString());
					System.out.println("CP:"+bmoCustomerAddress.getZip().toString());
					System.out.println("Ciudad:"+bmoCustomerAddress.getCityId().toInteger());
					System.out.println("Descripción:"+bmoCustomerAddress.getDescription().toString());
	    			pmCustomerAddress.save(pmConn, bmoCustomerAddress, bmUpdateResult);
    			}
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
		
		response.sendRedirect("cust_dacredito_batch.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
			
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