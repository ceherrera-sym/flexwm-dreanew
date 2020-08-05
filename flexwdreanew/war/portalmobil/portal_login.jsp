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
}%>


<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport"
	content="width=320, initial-scale=1.0, maximum-scale=1">
<title>:::<%= appTitle %>:::
</title>

<link rel="stylesheet" type="text/css" href="./css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css"
	href=".<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
<script type="text/javascript"></script>
</head>
<body background="img/body.jpg">
	<div class="alignLog">
		
		<form method="post" action="portal_login_post.jsp">

			<div align="center">
				<img style=" max-width: 100%; height: auto;" src="img/login_logo.png" >
			

			</div>

			<div align="center">
				<input class="loginTextBox" placeholder="Empresa"
					maxlength="15"  type="text" name="company">
			</div>
			<% if (msg != null && msg.equals("3")) { %>

			<div class="loginError" align="center">
			Escriba el nombre de la empresa
			</div>

			<% } %>
			
			<div align="center">
				<input class="loginTextBox" placeholder="Usuario"
					maxlength="15"  type="text" name="user">
			</div>

			<div align="center">
				<input class="loginTextBox" placeholder="Contraseña" maxlength="15"
					 type="password" name="passw">
			</div>
			<% if (msg != null && msg.equals("2")) { %>
			<div class="loginError" align="center">Usuario y/o contraseña
				incorrectos</div>

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
<%}else{
	response.sendRedirect("portal_start_mobile.jsp?program=prrt&viewHelp=0") ;
} %>


