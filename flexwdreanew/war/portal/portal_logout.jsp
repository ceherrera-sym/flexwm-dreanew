<!--  
/**
 * @author César Herrera Hernández
 */ -->
<%
	session.setAttribute("Id", null);
	session.setAttribute("sfparamPortal", null);
	response.sendRedirect("portal_login.jsp");
%>