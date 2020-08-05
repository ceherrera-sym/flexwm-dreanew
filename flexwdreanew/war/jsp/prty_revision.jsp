
<%@page import="com.symgae.shared.GwtUtil"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.flexwm.server.co.PmDevelopmentBlock"%>
<%@page import="com.flexwm.server.co.PmDevelopmentRegistry"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentRegistry"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModel"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Viviendas";
	String programDescription = "Importacion de Viviendas";
	String populateData = "", action = "";
	String errorSave = "";
	if (request.getParameter("populateData") != null) populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) action = request.getParameter("action");
	if (request.getParameter("errorsave") != null) errorSave = request.getParameter("errorsave");
%>

<html>
<head>
<title>:::Importacion de Viviendas:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<%  
PmConn pmConn = new PmConn(sFParams);
pmConn.open();
try { 
if (action.equals("revision")) { %>

<%
	String msg = "", sql = "";
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	String cons = "";	
	String devePhase  = "";
    String bloc_code = "";    
    String packageDevelopment = "";
    String propertyModel = "";
    String inm = "";
    String description = "";
    String street = "";
    String lot = "";	
	String number = "";
	String codeProperty = "";
	String landSize = "";
	String publicLandSize = "";
	String constructionSize = "";
	String colindancias = "";
	String orientation = "";
	String index = "";
	String all = "";
	String cadenaError="";
	String revision = "";
	String company ="";
	String inmAbierto ="";
	String habilable ="";
	String city ="";
	
%>
    
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Etapa</td>
                    <td class="reportHeaderCell">Modelo</td>
                    <td class="reportHeaderCell">Paq</td>
                    <td class="reportHeaderCell">Mz</td>
                    <td class="reportHeaderCell">Descripci&oacute;n</td>
                    <td class="reportHeaderCell">Lote</td>
                    <td class="reportHeaderCell">Calle</td>                    
                    <td class="reportHeaderCell">N&uacute;mero</td>
                    <td class="reportHeaderCell">M2.Terreno</td>
                    <td class="reportHeaderCell">M2.T.Comunal</td>
                    <td class="reportHeaderCell">M2.T.Construcci&oacute;n</td>
                    <td class="reportHeaderCell">Revision</td>
                    <td class="reportHeaderCell">Empresa</td>
                    <td class="reportHeaderCell">Inm. Abierto</td>
                    <td class="reportHeaderCell">Habitable</td>
                    <td class="reportHeaderCell">Ciudad*</td>                   
                    <td class="reportHeaderCell">Error</td>
                </TR>
            <%
            	boolean errors = false;
        		while (inputData.hasMoreTokens() && i < 5000) {
        			
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores
        			devePhase = tabs.nextToken();
    				propertyModel = tabs.nextToken();
    				packageDevelopment = tabs.nextToken();
    				bloc_code = tabs.nextToken();
    				description = tabs.nextToken();
    				lot = tabs.nextToken();
    				street = tabs.nextToken();    				    				
    				number = tabs.nextToken();    				    				
    				landSize = tabs.nextToken();
    				publicLandSize = tabs.nextToken();
    				constructionSize = tabs.nextToken();    				
    				revision = tabs.nextToken();
    				company = tabs.nextToken();
    				inmAbierto = tabs.nextToken();
    				habilable = tabs.nextToken();
    				city = tabs.nextToken();
    				cadenaError = "";
    				
    				int dvphCode = 0;
        			//Revisar la etapa exista
        			sql = " SELECT count(dvph_code) as dvphCode FROM developmentphases " +
        			      " WHERE dvph_code like '" + devePhase + "'";        			
        			pmConn.doFetch(sql);
        			if (pmConn.next()) dvphCode = pmConn.getInt("dvphCode");
        			
        			if (!(dvphCode > 0)) { 
        				errors = true;
        				cadenaError = "La Etapa No Existe | ";
        			}
        			
        			
    				int bloccodeS = 0;
        			//Revisar la manzana que no exista
        			sql = " SELECT count(dvbl_code) as bloccode FROM developmentblocks " +
        				  " LEFT JOIN developmentphases ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
        			      " WHERE dvbl_code like '" + bloc_code + "'" + 
        				  " AND dvph_code like '" + devePhase + "'";        			
        			pmConn.doFetch(sql);
        			if (pmConn.next()) bloccodeS = pmConn.getInt("bloccode");
        			
        			if (!(bloccodeS > 0) && !errors) {
        				errors = true;
        				cadenaError += "La Manzana no existe en la Etapa " + devePhase+ " | ";
        			}
        			
        			int dvrgcodeS = 0;
        			//Revisar el Paquete
        			if (!packageDevelopment.equals("empty")) {
	        			sql = " SELECT count(dvrg_code) as dvrgcode FROM developmentregistry " +
	        				  " LEFT JOIN developmentphases ON (dvrg_developmentphaseid = dvph_developmentphaseid) " +
	        				  " WHERE dvrg_code = '" + packageDevelopment + "'" +
	        				  " AND dvph_code = '" + devePhase + "'";
	        			System.out.println("sql " + sql);
	        			pmConn.doFetch(sql);
	        			if (pmConn.next()) dvrgcodeS = pmConn.getInt("dvrgcode");
	        			
	        			if (!(dvrgcodeS > 0)) { 
	        				errors = true;
	        				cadenaError = "No Existe el paquete | ";
	        			}
        			}
        			int ptymcodeS = 0;
        			//Revisar el proveedor
        			sql = "SELECT count(ptym_code) as ptymcode FROM propertymodels WHERE ptym_code like '" + propertyModel + "'";        			
        			pmConn.doFetch(sql);
        			if (pmConn.next()) ptymcodeS = pmConn.getInt("ptymcode");
        			
        			if (!(ptymcodeS > 0)) { 
        				errors = true;
        				cadenaError = "No Existe el modelo | ";
        			}
        			
        			if (lot.equals("empty"))
        				lot = "0";
        			if (landSize.equals("empty"))
        				landSize = "0";
        			if (publicLandSize.equals("empty"))
        				publicLandSize = "0";
        			if (constructionSize.equals("empty"))
        				constructionSize = "0";
        			
        			try {
        				Double.parseDouble(landSize); 
        			} catch(NumberFormatException e) {
        				errors = true;
        				cadenaError = "M2.Terreno: El formato no es un n�mero.";
        			} catch(NullPointerException e) {
        				errors = true;
        				cadenaError = "M2.Terreno: Debe asignar un valor.";
        			}
        			
        			try {
        				Double.parseDouble(publicLandSize); 
        			} catch(NumberFormatException e) {
        				errors = true;
        				cadenaError = "M2.T.Comunal: El formato no es un n�mero.";
        			} catch(NullPointerException e) {
        				errors = true;
        				cadenaError = "M2.T.Comunal: Debe asignar un valor.";
        			}
        			
        			try {
        				Double.parseDouble(constructionSize); 
        			} catch(NumberFormatException e) {
        				errors = true;
        				cadenaError = "M2.T.Construcci&oacute;n: El formato no es un n�mero.";
        			} catch(NullPointerException e) {
        				errors = true;
        				cadenaError = "M2.T.Construcci&oacute;n: Debe asignar un valor.";
        			}
        			
        			
    				%>
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%> 
    					<%=HtmlUtil.formatReportCell(sFParams, devePhase, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, propertyModel, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, packageDevelopment, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, bloc_code, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, lot, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, street, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, number, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, landSize, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, publicLandSize, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, constructionSize, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, revision, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, company, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, inmAbierto, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, habilable, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, city, BmFieldType.STRING)%>    					
    					<td><font color="red"><%= cadenaError %></font></td>
    					
    				</TR>
    				<%
    			i++;	
    			
        		}
                pmConn.close();
            %>
	</TABLE>

	
	<TABLE  border="0"  width="100%">
		<FORM action="prty_revision.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">		
		<tr>
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (!errors) {%>
		        <input type="submit" value="Cargar Viviendas">
		        <%	} %>
		    </td>
		</tr>
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) {
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	try {		
	
		pmConn.disableAutoCommit();
			
		PmProperty pmProperty = new PmProperty(sFParams);
		String cadenaError = "";
		PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
		PmDevelopmentRegistry pmDevelopmentRegistry = new PmDevelopmentRegistry(sFParams);		
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		BmoDevelopmentRegistry bmoDevelopmentRegistry = new BmoDevelopmentRegistry();
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		
		String s = "", sql = "";
		int i = 1;		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		
		while (inputData.hasMoreTokens() && i < 5000) {
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			//Recuperar valores						
			String devePhase = tabs.nextToken();
			String propertyModel = tabs.nextToken();			
			String packageDevelopment = tabs.nextToken();
			String bloc_code = tabs.nextToken();
			String description = tabs.nextToken();
			String lot = tabs.nextToken();
			String street = tabs.nextToken();			    				
			String number = tabs.nextToken();    				    				
			String landSize = tabs.nextToken();
			String publicLandSize = tabs.nextToken();
			String constructionSize = tabs.nextToken();    				
			String revision = tabs.nextToken();
			String company = tabs.nextToken();
			String inmAbierto = tabs.nextToken();
			String habilable = tabs.nextToken();
			String city = tabs.nextToken();
			
			//Obtener la manzana 
			int developmentBlockId = 0;
			sql = " SELECT dvbl_developmentblockid FROM developmentblocks " +
			      " LEFT JOIN developmentphases ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
  			      " WHERE dvbl_code like '" + bloc_code + "'" + 
  				  " AND dvph_code = '" + devePhase + "'";        			
  			pmConn.doFetch(sql);
  			if (pmConn.next()) developmentBlockId = pmConn.getInt("dvbl_developmentblockid");
			
			bmoDevelopmentBlock = (BmoDevelopmentBlock) pmDevelopmentBlock.get(pmConn, developmentBlockId);						
			
			
			//Obtener el Paquete
			int developmentRegistryId = 0;
			if (!packageDevelopment.equals("empty")) {
				sql = " SELECT dvrg_developmentregistryid FROM developmentregistry " +
					  " LEFT JOIN developmentphases ON (dvrg_developmentphaseid = dvph_developmentphaseid) " +
					  " WHERE dvrg_code like '" + packageDevelopment + "'" +
					  " AND dvph_code like '" + devePhase + "'";	
				pmConn.doFetch(sql);
				if (pmConn.next()) developmentRegistryId = pmConn.getInt("dvrg_developmentregistryid");
				

				bmoDevelopmentRegistry = (BmoDevelopmentRegistry) pmDevelopmentRegistry.get(pmConn, developmentRegistryId);
			}
			
			int cityId = 0;
			if(!city.equals("empty")){
				System.out.println("*********************************");
				sql = " SELECT city_cityid  FROM cities WHERE city_name like '"+city+"'";
				pmConn.doFetch(sql);
				System.out.println(sql);
				if (pmConn.next()){
					cityId = pmConn.getInt("city_cityid");
					System.out.println(cityId);
				}
			}
			int habitableValue = 0;
				if(habilable.equals("SI")){
					habitableValue= 1;
				}else{
					habitableValue= 0;
				}
				int inmAbiertoValue = 0;
				if(inmAbierto.equals("SI")){
					inmAbiertoValue= 1;
				}else{
					inmAbiertoValue= 0;
				}
			bmoPropertyModel = (BmoPropertyModel) pmPropertyModel.getBy(pmConn, propertyModel, bmoPropertyModel.getCode().getName());
			//Crear el registro de la vivienda
			BmoProperty bmoProperty = new BmoProperty();
			if (!packageDevelopment.equals("empty")) 
				bmoProperty.getDescription().setValue(description);
			bmoProperty.getLot().setValue(lot);
			bmoProperty.getStreet().setValue(street);
			bmoProperty.getNumber().setValue(number);
			bmoProperty.getLandSize().setValue(Double.parseDouble(landSize));
			bmoProperty.getPublicLandSize().setValue(Double.parseDouble(publicLandSize));
			bmoProperty.getConstructionSize().setValue(constructionSize);
			bmoProperty.getPropertyModelId().setValue(bmoPropertyModel.getId());
			bmoProperty.getDevelopmentBlockId().setValue(bmoDevelopmentBlock.getId());
			if (!packageDevelopment.equals("empty")) 
				bmoProperty.getDevelopmentRegistryId().setValue(bmoDevelopmentRegistry.getId());
			
			bmoProperty.getAvailable().setValue(inmAbiertoValue);
			bmoProperty.getCansell().setValue("1");
			bmoProperty.getHabitability().setValue(habitableValue);
			bmoProperty.getCompanyId().setValue(bmoDevelopmentBlock.getBmoDevelopmentPhase().getCompanyId().toInteger());
			bmoProperty.getCityId().setValue(cityId	);
			pmProperty.save(pmConn, bmoProperty, bmUpdateResult);
		}	
		
		pmConn.commit();
		
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
		response.sendRedirect(GwtUtil.getProperUrl(sFParams, "/jsp/prty_revision.jsp?action=complete&errorsave="
				+ bmUpdateResult.errorsToString()));
		
}  else if (action.equals("complete")) { %>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>		
			<td colspan="" class="reportTitle">
			   	<br><br> &nbsp;La Carga esta completa
			   	<br>
			    <%= errorSave %>
			    
			    <br><br><a href="<%= GwtUtil.getProperUrl(sFParams, "/jsp/prty_populate.jsp") %>">Importar m&aacute;s viviendas.</a>
			</td>
		</tr>
	</table>

<% } %>




<% 	} catch (Exception e) { 
	errorLabel = "Error ";
	errorText = "Error al guardar registros";
	errorException = e.toString();
	
    response.sendRedirect(GwtUtil.getProperUrl(sFParams, "/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException));
} finally {
	pmConn.close();
}
%>
</body>
</html>