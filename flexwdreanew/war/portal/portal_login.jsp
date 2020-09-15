<!--  
/**
 * @author César Herrera Hernández
 */ -->
<%@page import="com.symgae.shared.GwtUtil"%>
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

<%
	String cust_id = (String)session.getAttribute("Id");
	if(cust_id == null)cust_id = "0";
	//Si no hay usario logeado mana a login
	if(Integer.parseInt(cust_id) <= 0){	
		String appTitle = "Portal Clientes SYMGF";
		String defaultCss = "login.css"; 
		boolean isMobile = false;
		// session.setAttribute(SFParams.SFPARAMSNAME, null);
		String msg = request.getParameter("msg");
		String user = request.getParameter("user");
		SFParams sFParams = new SFParams();
		
		
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setLoggedIn(true);
		loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
		SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
		
		if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
				isMobile = true;
		}
%>
		<html>
		<head>
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		
		<% if (isMobile) { %>
			<meta name="viewport" content="width=320, initial-scale=1.0, maximum-scale=1">
		<% } %>
		
		<title>:::<%= appTitle %>:::</title>
			
				<link rel="stylesheet" type="text/css" href="./css/<%= defaultCss %>"> 
		        <script type="text/javascript"></script>
		</head>
		<body  background="img/body.jpg">
			<div class="alignLog">		
				<form method="post"	action="portal_login_post.jsp?customer=<%=request.getParameter("user") %>?cRFC=<%=request.getParameter("passw") %>">
					<div align="center">
						<img src="img/logo.png" width="500px" height="100px" style="margin-bottom: 1%">
					</div>
					<div align="center">
						<input class="loginTextBox" placeholder="USUARIO" maxlength="15"  type="text" name="user">
					</div>
					<div align="center">
						<input class="loginTextBox" placeholder="CONTRASEÑA" maxlength="15" type="password" name="passw">
					</div>
					<% if (msg != null && msg.equals("2")) { %>
						<div class="loginError" align="center">Usuario y/o contraseña
							incorrectos
						</div>
					<% } %>
					<% if (msg != null && msg.equals("1")) { %>
						<div class="loginError" align="center">
							El usuario <b><%= user %></b> no tiene permisos para acceder al
							portal.
						</div>
		
					<% } %>
					<br>
					<div align="center">
						<input type="submit" class="loginButton" value="Entrar">
					</div>
				</form>
			</div>
		</body>
		</html>
<%	}else{
		response.sendRedirect("portal_start.jsp") ;
	} 
%>
