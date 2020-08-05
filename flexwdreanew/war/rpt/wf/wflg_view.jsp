<%@page import="com.flexwm.shared.wf.BmoWFlowLog"%>
<%@page import="com.flexwm.server.wf.PmWFlowLog"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>

<%

    // Obtener parametros
    int wFlowLogId = Integer.parseInt(request.getParameter("wflg_wflowlogid"));    
	
	BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
	PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);
	bmoWFlowLog = (BmoWFlowLog)pmWFlowLog.get(wFlowLogId);
	
%>

<b>Fecha y hora de Bit&aacute;cora:</b>  <%=  bmoWFlowLog.getLogdate().toString() %>, <b>Comentarios:</b> <%=  bmoWFlowLog.getComments().toString() %>

<br><br>

<%= bmoWFlowLog.getData().toString() %>