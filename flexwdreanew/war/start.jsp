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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ page import="com.symgae.server.*"%>
<%@ page import="com.symgae.server.sf.*"%>
<%@ page import="com.symgae.shared.SFParams"%>
<%@ page import="com.symgae.shared.BmFilter"%>
<%@ page import="com.symgae.shared.BmSearchField"%>
<%@ page import="com.symgae.shared.BmOrder"%>
<%@ page import="com.symgae.shared.BmObject"%>
<%@ page import="com.symgae.shared.BmField"%>
<%@ page import="com.symgae.shared.BmFieldType"%>
<%@ page import="com.symgae.shared.sf.*"%>
<%@ page import="com.symgae.shared.LoginInfo"%>
<%@ page import="java.lang.reflect.Constructor"%>


<%
	String appTitle = "SYMGF";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	boolean isMobile = false;
	
	SFParams sFParams;
	LoginInfo loginInfo = new LoginInfo();
	
	try {
		// Obtener informacion de SFParams
		sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
		if (sFParams == null) {
			response.sendRedirect("login.jsp");
		} else {
			appTitle = sFParams.getMainAppTitle();
			defaultCss = sFParams.getDefaultCss();
			loginInfo = sFParams.getLoginInfo();
			
			// Revisa si esta loggeado
			if (!sFParams.getLoginInfo().isLoggedIn()) {	
				response.sendRedirect("login.jsp");
			}
		}
		
		// Determina si es mobil o 
		if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
			isMobile = true;
		}
	} catch (Exception e) {
%>
	<!-- SYMGF Init Error: <%= e.toString() %> -->
<%
	}
%>

<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">
<link type="text/css" rel="stylesheet" href="./css/<%= gwtCss %>">
<link type="text/css" rel="stylesheet" href="./css/tags.jsp">
<link type="text/css" rel="stylesheet" href="./css/symgaeUtils.css">


<% if (isMobile) { %>
<meta name="viewport"
	content="width=320, initial-scale=1.0, maximum-scale=1">
<% } %>

<title>:::<%= appTitle %>:::
</title>

<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<script type="text/javascript" language="javascript"
	src="flexwm/flexwm.nocache.js"></script>
</head>

<body>

	Iniciando...

	<!-- OPTIONAL: include this if you want history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>

	<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>

</body>
</html>
