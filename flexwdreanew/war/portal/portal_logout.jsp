<!--  
/**
 * @author C�sar Herrera Hern�ndez
 */ -->
<%
	session.setAttribute("Id", null);
	session.setAttribute("sfparamPortal", null);
	response.sendRedirect("portal_login.jsp");
%>