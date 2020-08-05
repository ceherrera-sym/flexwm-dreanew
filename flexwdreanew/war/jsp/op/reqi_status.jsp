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
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>

<%@include file="/inc/login.jsp"%>

<%
	try {
		String msg = "";	
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram  = new PmProgram(sFParams);
		bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoRequisition.getProgramCode());
		BmUpdateResult bmUpdateResult = new BmUpdateResult();

	    // Obtener parametros
	    int requisitionId = 0;
	    if (isExternal) 
	    		requisitionId = decryptId;
	    else if (request.getParameter("foreignId") != null) 
	    		requisitionId = Integer.parseInt(request.getParameter("foreignId"));
	    String newStatus = request.getParameter("v");
	    
	    // Obtiene orden de compra
	    PmRequisition pmRequisition = new PmRequisition(sFParams);
	    bmoRequisition = (BmoRequisition)pmRequisition.get(requisitionId);
	    BmFieldOption prevStatusOption = bmoRequisition.getStatus().getFieldOptionByCode(bmoRequisition.getStatus().toChar());
	    BmFieldOption newStatusOption = bmoRequisition.getStatus().getFieldOptionByCode(newStatus.charAt(0));
	    
	    // Revisa permisos de cambio de estatus
	    if (sFParams.hasSpecialAccess(BmoRequisition.ACCESS_CHANGESTATUS)) {
		    // Si hay cambio de estatus, proceder 
		    if (bmoRequisition.getStatus().toString().equalsIgnoreCase(newStatus)) {
		    		msg = "No hay cambio de Estatus, se mantiene en: " + prevStatusOption.getLabel();
		    } else {
		    	
		    		// Si el cambio es a Autorizada, revisar que tenga permisos
		    		if (newStatus.equalsIgnoreCase("" + BmoRequisition.STATUS_AUTHORIZED)) {
		    			if (sFParams.hasSpecialAccess(BmoRequisition.ACCESS_AUTHORIZE)) {
				    		bmoRequisition.getStatus().setValue(newStatus);
			    			bmUpdateResult = pmRequisition.save(bmoRequisition, bmUpdateResult);
		    			} else {
		    				msg = "No cuenta con Permisos para Autorizar la Órden de Compra.";
		    			}
		    		} 
		    		// Cambiar estatus
		    		else {
			    		bmoRequisition.getStatus().setValue(newStatus);
			    		bmUpdateResult = pmRequisition.save(bmoRequisition, bmUpdateResult);
		    		}
		    		
		    		if (bmUpdateResult.hasErrors()) {
		    			msg = bmUpdateResult.errorsToString();
		    		} else {
		    			msg = "Se cambió el Estatus de: " + prevStatusOption.getLabel() + " a: " + newStatusOption.getLabel();
		    		}
		    	}
	    } else {
	    		msg = "No cuenta con Permisos para cambiar el Estatus.";
	    }
	    
	    BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoRequisition.getCompanyId().toInteger());		
	    
	 	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	 	String logoURL ="";
	 	if (!bmoCompany.getLogo().toString().equals(""))
	 		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	 	else 
	 		logoURL = sFParams.getMainImageUrl();
	%>
<html>
<head>
<title>:::Orden de Compra:::</title>
<link rel="stylesheet" type="text/css" href="/css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="/css/flexwm.css">
</head>
<body class="default">
<TABLE bgcolor="#EDEDED" width="100%" height="100%">
	<TR>
		<TD width="100%" heigh="100%" valign="top">
			<TABLE width="500px" style="font-family:verdana; font-size:10pt;" align="center" border="0" bordercolor="silver" cellpading="0" cellspacing="0">  
				<TR>
					<TD width="75" align="left" height="75" colspan="2" valign="bottom">  
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>"> 
					</TD>
				</TR>
				<TR>
					<TD bgcolor="black"  align="center" colspan="3" height="35px" style="font-size:14px; color:white">  
 					Órden de Compra: <b><%= bmoRequisition.getCode().toString() %></b>
 					<%= bmoRequisition.getName().toString() %>
					</TD>
				</TR>
				<TR>
					<TD bgcolor="white" align="center" width="50px">  
							&nbsp;
					 </TD> 
					<TD bgcolor="white" align="justify" width="400px" valign="top">
						<br>
						<%= msg %>
					</TD>
					<TD bgcolor="white" align="center" width="50px">  
							&nbsp;
					 </TD>  
				</TR>
				<TR>
					<TD bgcolor="white" colspan="3" align="center" height="40px"> 
						 &nbsp;	<br><br><br><br>
					</TD>
				</TR>
				<TR>
					<TD bgcolor="#EDEDED" colspan="3" align="center" height="30px"> 
						 <br><a href="http://www.symetria.com.mx" title="FlexWM Web-Based Management" style="font-size:11px"><img src="<%= GwtUtil.getProperUrl(sFParams, "/img/main.png")%>" height="25"></a>
					</TD>
				</TR>
				<TR bgcolor="#EDEDED" > 
					<TD align="center" width="100px" height="50px">  
							&nbsp;
					 </TD> 
					<TD align="center" style="font-size:9px"> 
						 &nbsp;© <%=SFServerUtil.nowToString(sFParams, "YYYY") %>  FlexWM, FlexWM Web-Based Management y el logo FlexWM
						 son marcas registradas de Ctech Consulting S.A. de C.V. y Symetria Tecnológica S.A. de C.V.
						  <br>
						 Todos los Derechos Reservados.
					</TD>
					<TD align="center" >  
							&nbsp;
					 </TD> 
				</TR> 
				<TR bgcolor="#EDEDED" >
					<TD colspan="3" align="center" style="font-size:8px" height="50px"> 
						 &nbsp;
					</TD>
				</TR>
			</TABLE>
		</TD>
	</TR>
</TABLE>
</BODY>  
</HTML>
	<br>
	<%  
		} catch (Exception e) { 
		    String errorLabel = "Error de Estatus Orden de Compra";
		    String errorText = "No se pudo cambiar el estatus a la Orden de Compra";
		    String errorException = e.toString();
		    
		    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	    }	
	%>