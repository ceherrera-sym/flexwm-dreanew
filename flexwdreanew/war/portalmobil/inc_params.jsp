<!--  
/**
 * @author C�sar Herrera Hern�ndez
 */ -->
<%@page import="com.symgae.shared.LoginInfo"%>
<%@page import="com.symgae.shared.SFParams"%>
<%
	SFParams sFParams = new SFParams();
	LoginInfo loginInfo = new LoginInfo();
	loginInfo = (LoginInfo)session.getAttribute("loginInf");
	sFParams =  (SFParams)session.getAttribute("sfparamPortal");
	
%>