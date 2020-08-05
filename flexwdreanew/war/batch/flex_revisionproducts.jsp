
<%@page import="com.sun.xml.internal.ws.api.pipe.NextAction"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>
<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Productos";
	String programDescription = programTitle;
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) action = request.getParameter("action");
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset="UTF-8">
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

<% 

PmConn pmConn = new PmConn(sFParams);
pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

try { 

if (action.equals("revision")) { %>
	<table width="80%" border="0" align="center"  class="">

<%
	String msg = "";
	String sql = "";
	
	String s = "";
	int i = 1, nameLength = 0;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		
	String code = "";
	String name = "";
	String type = "";
	String description = "";
	String brand = "";
    String model = "";    
    String displayName = "";   
    String prodFamilyCode = "";
    String productGroup = "";
    String enabled = "";
    String cureCode = "";
    String suplCode = "";
    String track = "";
    String unitCode = "";
    String rentalPrice = ""; 
    String cost = ""; 
    String rentalCost = ""; 
    String salePrice = "";
	String errors = "";
	String weightProduct = "";
	double cubicmeter = 0,cubicmeterCase = 0;
	String dimensionLength = "",dimensionHeight= "",dimensionWidth = "",amperage110 = "",amperage220 = ""
			,useCaseString= "",quantityForCase = "",weightCase = "",caseLength = "",
			caseHeight = "",caseWidth = "";
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Clave de Producto</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Tipo</td>
                    <td class="reportHeaderCell">Descripcion</td>
                    <td class="reportHeaderCell">Marca</td>
                    <td class="reportHeaderCell">Modelo</td>                    
                    <td class="reportHeaderCell">N.Comercial</td>
                    <td class="reportHeaderCell">Activo</td>
                    <td class="reportHeaderCell">Familia Prod</td>        
                    <td class="reportHeaderCell">Grupo Prod</td>            
                    <td class="reportHeaderCell">Moneda</td>
                    <td class="reportHeaderCell">Precio Venta</td>
                    <td class="reportHeaderCell">Costo Compra</td>
                    <td class="reportHeaderCell">Precio Renta</td>
                    <td class="reportHeaderCell">Costo Renta</td>
                    <td class="reportHeaderCell">Proveedor</td>
                    <td class="reportHeaderCell">Rastreo</td>
                    <td class="reportHeaderCell">Unidad</td>                   
                    <td class="reportHeaderCell">Peso(Kg)</td>
                   
                    <td class="reportHeaderCell">Largo</td>
                    <td class="reportHeaderCell">Alto</td>
                    <td class="reportHeaderCell">Ancho</td>
                    <td class="reportHeaderCell">M³</td>
                    <td class="reportHeaderCell">Amperaje 110v</td>
                    <td class="reportHeaderCell">Amperaje 220v</td>
                   <%  if (sFParams.isFieldEnabled(new BmoProduct().getUseCase())){%>
                    	<td class="reportHeaderCell">Case?</td>
                    	<td class="reportHeaderCell">Peso Case</td>
                    	<td class="reportHeaderCell">Largo Case</td>
                    	<td class="reportHeaderCell">Alto Case</td>
                    	<td class="reportHeaderCell">Ancho Case</td>
                    	<td class="reportHeaderCell">M³ Case</td>                     	
                    <%} %>
                    <td class="reportHeaderCell">Errors</td>
                    <td class="reportHeaderCell">Tamanio</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores 
        			code = tabs.nextToken();
        			name = tabs.nextToken();

        			nameLength = name.length();
        			System.out.println("tamNombre: "+code);
        			name = name.trim();
        			type = tabs.nextToken();
        			description = (tabs.nextToken()).trim();
        			brand = (tabs.nextToken()).trim();
        		    model = (tabs.nextToken()).trim();    
        		    displayName = (tabs.nextToken());
        		    prodFamilyCode = (tabs.nextToken());      
        		    productGroup = (tabs.nextToken());
        		    enabled = tabs.nextToken();
        		    cureCode = tabs.nextToken();
        		    
        		    salePrice = tabs.nextToken();
        		    cost = tabs.nextToken();
        		    rentalPrice = tabs.nextToken();
        		    rentalCost = tabs.nextToken();
        		    
        		    suplCode = tabs.nextToken();
        		    track = tabs.nextToken();
        		    unitCode = tabs.nextToken();
        		    weightProduct = tabs.nextToken();
        		    dimensionLength  = tabs.nextToken();
        		    dimensionHeight = tabs.nextToken();
        		    dimensionWidth = tabs.nextToken();
        		    amperage110 = tabs.nextToken();
        		    amperage220 = tabs.nextToken();
        		    if(weightProduct.equals("empty"))weightProduct = "0";
           		    if(dimensionLength.equals("empty"))dimensionLength = "0";
           		 	if(dimensionHeight.equals("empty"))dimensionHeight = "0";
           		 	if(dimensionWidth.equals("empty"))dimensionWidth = "0";
           		 	if(amperage110.equals("empty"))amperage110 = "0";
           			if(amperage220.equals("empty"))amperage220 = "0";
        		    // Metros cubicos de productoPower Distribution
        		    cubicmeter = (Double.parseDouble(dimensionLength) * Double.parseDouble(dimensionHeight) * Double.parseDouble(dimensionWidth));
        		    cubicmeter = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(cubicmeter));
        		   	if (sFParams.isFieldEnabled(new BmoProduct().getUseCase())){
        		    	useCaseString = tabs.nextToken();        		
        		    	quantityForCase  = tabs.nextToken();
        		    	weightCase = tabs.nextToken();
        		    	caseLength = tabs.nextToken();
        		    	caseHeight  = tabs.nextToken();
        		   		caseWidth = tabs.nextToken();        	
        		   		
        		   		if(useCaseString.equals("empty"))useCaseString = "0";
               		 	if(quantityForCase.equals("empty"))quantityForCase = "0";
               		 	if(weightCase.equals("empty"))weightCase = "0";
               		 	if(caseLength.equals("empty"))caseLength = "0";
               		 	if(caseHeight.equals("empty"))caseHeight = "0";
               		 	if(caseWidth.equals("empty"))caseWidth = "0";
        		    	// Metros cubicos de case
        		    	cubicmeterCase = (Double.parseDouble(caseLength) * Double.parseDouble(caseHeight) * Double.parseDouble(caseWidth));
        		    	cubicmeterCase = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(cubicmeterCase));
        		   	}
        			//Tipo

        			if (!type.equals("empty")) {
        				if (!type.equals("P") && !type.equals("S") && !type.equals("E") && !type.equals("C") 
        						&& !type.equals("M") && !type.equals("B") && !type.equals("Y") && !type.equals("Y") 
        						&& !type.equals("Q")) {
        					errors = "Producto = P, S.Interno = S, S.Externo = E, Clase = C ";
        				}
        			}
        			
        			//Activo
        			if (!enabled.equals("empty")) {
        				if (!enabled.equals("1") && !enabled.equals("0")) {
        					errors = "Activo = 1, No Activo = 0";
        				}
        			}
        			
        			//Rastreo
        			if(!track.equals("empty")) {
        				if (!track.equals("N") && !track.equals("S") && !track.equals("B")) {
        					errors = "sin rastreo = n, no.serie = s,  lote = b ";
        				}
        			}
        			
        			int prodFamilyId = -1;
        			if (!prodFamilyCode.equals("empty")) {
	        			//Validar la familia
	        			sql = " SELECT prfm_productfamilyid FROM productfamilies " +
	          			      " WHERE prfm_name = '" + prodFamilyCode.trim() + "'";
	        			pmConn2.doFetch(sql);
	        			if (pmConn2.next()) {
	        				prodFamilyId = pmConn2.getInt("prfm_productfamilyid");
	        			} else
	        				errors = "La Familia no Existe " + prodFamilyCode;
        			}	
        			int prodGroupId = 0;
					if (!productGroup.equals("empty")){
						sql = " SELECT prgp_productgroupid FROM productgroups " +
		          			      " WHERE prgp_name = '" + productGroup + "'";
						pmConn2.doFetch(sql);
						if (pmConn2.next()) {
							prodGroupId = pmConn2.getInt("prgp_productgroupid");
	        			} else
	        				errors = "El Grupo no Existe " + productGroup;
        			}
        			
        			if (!unitCode.equals("empty")) {
	        			//Validad la unidad
	        			sql = " SELECT * FROM units WHERE unit_code like '" + unitCode.trim() + "'";
	          			pmConn2.doFetch(sql);
	          			if (!pmConn2.next()) {
	          				errors = "La Unidad no Existe " + unitCode;
	          			}
        			}	
          			
          			if (!suplCode.equals("empty")) {
	          			//Validad la proveedor
	        			sql = " SELECT * FROM suppliers " +
	            			  " WHERE supl_name = '" + suplCode.trim() + "'";
	          			pmConn2.doFetch(sql);
	          			if (!pmConn2.next()) {
	          				errors = "El Proveedor no Existe " + suplCode;
	          			}
          			}	
          			if (cureCode.equalsIgnoreCase("empty")){
          				errors = "Falta la Moneda";
          			} else {
          			//Validad la moneda
	          			sql = " SELECT * FROM currencies " +
	              			  " WHERE cure_code = '" + cureCode.trim() + "'";
	            			pmConn2.doFetch(sql);
	            			if (!pmConn2.next()) {
	            				errors = "La Moneda no Existe " + cureCode;
	            			}	
          			}
            			
           			if (salePrice.equals("empty"))
           		    	salePrice = "0";
           		    
           		    if (cost.equals("empty"))
           		    	cost = "0";
           		    
           		    if (rentalPrice.equals("empty"))
           		    	rentalPrice = "0";
           		    
           		    if (rentalCost.equals("empty"))
           		    	rentalCost = "0";
           		    
           		    
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, brand, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, model, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, displayName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, enabled, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, prodFamilyCode, BmFieldType.STRING)%>   
    					<%=HtmlUtil.formatReportCell(sFParams, productGroup, BmFieldType.STRING)%> 					
    					<%=HtmlUtil.formatReportCell(sFParams, cureCode, BmFieldType.STRING)%>
    					
    					<%=HtmlUtil.formatReportCell(sFParams, salePrice, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, cost, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, rentalPrice, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, rentalCost, BmFieldType.STRING)%>
    					
    					<%=HtmlUtil.formatReportCell(sFParams, suplCode, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, track, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, unitCode, BmFieldType.STRING)%>
    					
    					<%=HtmlUtil.formatReportCell(sFParams, weightProduct, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, dimensionLength, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, dimensionHeight, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, dimensionWidth, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, ""+cubicmeter, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, amperage110, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, amperage220, BmFieldType.STRING)%>
    					<%if(sFParams.isFieldEnabled(new BmoProduct().getUseCase())){ %>
    						<%=HtmlUtil.formatReportCell(sFParams, useCaseString, BmFieldType.STRING)%>
    						<%=HtmlUtil.formatReportCell(sFParams, quantityForCase, BmFieldType.STRING)%>
    						<%=HtmlUtil.formatReportCell(sFParams, weightCase, BmFieldType.STRING)%>
    						<%=HtmlUtil.formatReportCell(sFParams, caseLength, BmFieldType.STRING)%>
    						<%=HtmlUtil.formatReportCell(sFParams, caseHeight, BmFieldType.STRING)%>
    						<%=HtmlUtil.formatReportCell(sFParams, caseWidth, BmFieldType.STRING)%>
    					<%} %>
    				    <td><font color="red"><%= errors %></font></td>
    				    <%=HtmlUtil.formatReportCell(sFParams, ""+nameLength, BmFieldType.STRING)%>
    				    
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionproducts.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Productos">
		    <% } else { %>
		    	<%= errors.toString()%>
		    <% }%>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) {
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	try {		
		
		pmConn.disableAutoCommit();
	
		String s = "";		
		String code = "";
		String name = "";
		String type = "";
		String description = "";
		String brand = "";
	    String model = "";    
	    String displayName = "";   
	    String prodFamilyCode = "";
	    String productGroup = "";
	    String enabled = "";
	    String cureCode = "";
	    
	    String rentalPrice = ""; 
	    String cost = ""; 
	    String rentalCost = ""; 
	    String salePrice = "";
	    
	    String suplCode = "";
	    String track = "";
	    String unitCode = "";
	    String weightProduct = "";
		double cubicmeter = 0,cubicmeterCase = 0;
		String dimensionLength = "",dimensionHeight= "",dimensionWidth = "",amperage110 = "",amperage220 = ""
				,useCaseString= "",quantityForCase = "",weightCase = "",caseLength = "",
				caseHeight = "",caseWidth = "";
			
		
		PmProduct pmProduct = new PmProduct(sFParams);
		BmoProduct bmoProduct = new BmoProduct();

				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		int countFromNomenclature = 0;
		while (inputData.hasMoreTokens() && i < 20000) { 
			countFromNomenclature ++;
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");

			//Recuperar valores   
			code = (tabs.nextToken()).trim();
			name = (tabs.nextToken()).trim();
			type = tabs.nextToken();
			description = (tabs.nextToken()).trim();
			brand = (tabs.nextToken()).trim();
		    model = (tabs.nextToken()).trim();    
		    displayName = (tabs.nextToken());
		    prodFamilyCode = (tabs.nextToken()).trim();		    
		    productGroup = (tabs.nextToken()).trim();		
		    enabled = tabs.nextToken();
		    cureCode = tabs.nextToken();
		    
		    salePrice = tabs.nextToken();
		    cost = tabs.nextToken();
		    rentalPrice = tabs.nextToken();
		    rentalCost = tabs.nextToken();
		    
		    suplCode = tabs.nextToken();
		    track = tabs.nextToken();
		    unitCode = tabs.nextToken();
		    
		    weightProduct = tabs.nextToken();
		    dimensionLength  = tabs.nextToken();
		    dimensionHeight = tabs.nextToken();
		    dimensionWidth = tabs.nextToken();
		    amperage110 = (tabs.nextToken()).trim();			   
		    amperage220 = (tabs.nextToken()).trim();	
		    if(weightProduct.equals("empty"))weightProduct = "0";
   		    if(dimensionLength.equals("empty"))dimensionLength = "0";
   		 	if(dimensionHeight.equals("empty"))dimensionHeight = "0";
   		 	if(dimensionWidth.equals("empty"))dimensionWidth = "0";
   		 	if(amperage110.equals("empty"))amperage110 = "0";
   		 	if(amperage220.equals("empty"))amperage220 = "0";
   		 	if(amperage110.equals("empty"))amperage110 = "0";
		  	if(amperage220.equals("empty"))amperage220 = "0";
   		 	
		    cubicmeter = (Double.parseDouble(dimensionLength) * Double.parseDouble(dimensionHeight) * Double.parseDouble(dimensionWidth));
		    cubicmeter = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(cubicmeter));
		    if(sFParams.isFieldEnabled(bmoProduct.getUseCase())){
		    	useCaseString = tabs.nextToken();
		    	quantityForCase  = tabs.nextToken();
		    	weightCase = tabs.nextToken();
		   		caseLength = tabs.nextToken();
		    	caseHeight  = tabs.nextToken();
		    	caseWidth = tabs.nextToken();		
		    	if(useCaseString.equals("empty"))useCaseString = "0";
       		 	if(quantityForCase.equals("empty"))quantityForCase = "0";
       		 	if(weightCase.equals("empty"))weightCase = "0";
       		 	if(caseLength.equals("empty"))caseLength = "0";
       		 	if(caseHeight.equals("empty"))caseHeight = "0";
       		 	if(caseWidth.equals("empty"))caseWidth = "0";
		    	cubicmeterCase = (Double.parseDouble(caseLength) * Double.parseDouble(caseHeight) * Double.parseDouble(caseWidth));
		    	cubicmeterCase = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(cubicmeterCase));
		    }
		  
		    // Metros cubicos de case
		   
			
			int prodFamilyId = -1;
			
			//Obtener la familia
			if (!prodFamilyCode.equals("empty")) {
				sql = " SELECT prfm_productfamilyid FROM productfamilies " +
	    			  " WHERE prfm_name like '" + prodFamilyCode + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) {
					prodFamilyId = pmConn2.getInt("prfm_productfamilyid");
				}
			}	
			//Obtener la grupo
			int prodGroupId = 0;
			if (!productGroup.equals("empty")){
				sql = " SELECT prgp_productgroupid FROM productgroups " +
          			      " WHERE prgp_name = '" + productGroup + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) {
					prodGroupId = pmConn2.getInt("prgp_productgroupid");
    			} 
			}
			
			int currencyId = 0;
			if (!cureCode.equals("empty")) {
				//Obtener la familia
				sql = " SELECT cure_currencyid FROM currencies " +
	    			  " WHERE cure_code = '" + cureCode.trim() + "'";			
				pmConn2.doFetch(sql);
				if (pmConn2.next()) {
					currencyId = pmConn2.getInt("cure_currencyid");
				}
			}	
			
			
			int supplierId = 0;
			if (!suplCode.equals("empty")) {
				//Obtener el proveedor
				sql = " SELECT supl_supplierid FROM suppliers " +
	    			  " WHERE supl_name = '" + suplCode.trim() + "'";			
				pmConn2.doFetch(sql);
				if (pmConn2.next()) {
					supplierId = pmConn2.getInt("supl_supplierid");
				}
			}	
			
			int unitId = 0;			
			//Obtener el proveedor
			sql = " SELECT unit_unitid FROM units " +
    			  " WHERE unit_code = '" + unitCode.trim() + "'";			
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				unitId = pmConn2.getInt("unit_unitid");
			}
			
			 
			bmoProduct = new BmoProduct();	
			if (name.length() > 60) name = name.substring(0, 59);
			bmoProduct.getName().setValue(name);
			bmoProduct.getType().setValue(type);
			bmoProduct.getProductFamilyId().setValue(prodFamilyId);
			if (prodGroupId > 0)bmoProduct.getProductGroupId().setValue(prodGroupId);
			
			if (!description.equals("empty")) bmoProduct.getDescription().setValue(description);
			if (!model.equals("empty")) bmoProduct.getModel().setValue(model);
			if (!brand.equals("empty")) bmoProduct.getBrand().setValue(brand);
			if (!displayName.equals("empty")) bmoProduct.getDisplayName().setValue(displayName);
			else  bmoProduct.getDisplayName().setValue(name);
			if (!enabled.equals("empty")) bmoProduct.getEnabled().setValue(Integer.parseInt(enabled));
		    			
			if(!cost.equals("empty"))bmoProduct.getCost().setValue(cost);
			else bmoProduct.getCost().setValue(0);
			if(!rentalCost.equals("empty"))bmoProduct.getRentalCost().setValue(rentalCost);
			else bmoProduct.getRentalCost().setValue(0);
			
			if (!suplCode.equals("empty")) bmoProduct.getSupplierId().setValue(supplierId);			
			bmoProduct.getTrack().setValue(track);			
			bmoProduct.getUnitId().setValue(unitId);
			
			bmoProduct.getCode().setValue(code);
			bmoProduct.getWeight().setValue(weightProduct);
			bmoProduct.getDimensionLength().setValue(dimensionLength);
			bmoProduct.getDimensionHeight().setValue(dimensionHeight);
			bmoProduct.getDimensionWidth().setValue(dimensionWidth);
			bmoProduct.getAmperage110().setValue(amperage110);
			bmoProduct.getAmperage220().setValue(amperage220);
			bmoProduct.getCubicMeter().setValue(cubicmeter);
			if(sFParams.isFieldEnabled(bmoProduct.getUseCase())){
				bmoProduct.getUseCase().setValue(useCaseString);
				bmoProduct.getQuantityForCase().setValue(quantityForCase);
				bmoProduct.getWeightCase().setValue(weightCase);
				bmoProduct.getCaseLength().setValue(caseLength);
				bmoProduct.getCaseHeight().setValue(caseHeight);
				bmoProduct.getCaseWidth().setValue(caseWidth);				
				bmoProduct.getCaseCubicMeter().setValue(cubicmeterCase);
			}
			
			
			pmProduct.saveSimple(pmConn, bmoProduct, bmUpdateResult);
		}
		
		if (!bmUpdateResult.hasErrors()) {
			pmConn.commit();
		
// 			// Actualizar consecutivo de la nomenclatura (DREA)
			StringTokenizer tabs = new StringTokenizer(code, "-");		
			String codeReflix = "";
			int consecutive = 0;
			while (tabs.hasMoreTokens()) {				
				codeReflix = tabs.nextToken();
				consecutive = Integer.parseInt(tabs.nextToken());
			}
			String sql = "UPDATE companynomenclatures LEFT JOIN  programs ON (prog_programid = cono_programid) " +
					" LEFT JOIN companies ON (cono_companyid = comp_companyid) " +
					" SET cono_consecutive = " + consecutive +
					" WHERE prog_code = 'PROD' AND comp_name = 'DREA Guadalajara'";
			pmConn.doUpdate(sql);
			pmConn.commit();
		} else {
			System.out.println("Error: "+bmUpdateResult.errorsToString());
		}
			
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect(sFParams.getAppURL() + "/batch/flex_revisionproducts.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
}  else if (action.equals("complete")) { %>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>		
		<td colspan="4" align="center" class="reportTitle">
		<a href="flex_populateproducts.jsp">Reiniciar</a>
		<br>
		    &nbsp;La Carga esta completa
		    <%= errorSave %>
		</td>
	</tr>
	</table>

<% } %>


<% 	} catch (Exception e) { 
	errorLabel = "Error ";
	errorText = "Error";
	errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

<%
} finally {
	pmConn.close();
	pmConn2.close();
	
}
%>
</body>
</html>


