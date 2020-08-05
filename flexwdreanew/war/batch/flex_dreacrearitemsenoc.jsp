<%@page import="java.util.Iterator"%>

<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>

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

<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionItem"%>
<%@page import="com.flexwm.server.op.PmRequisitionItem"%>

<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>


<%@page import="java.util.Iterator"%>

<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>

<%@include file="../inc/login.jsp" %>



<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
</head>

<body class="default">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img src="<%= sFParams.getMainImageUrl() %>">
		</td>
		<td class="reportTitle" align="left">
			&nbsp;
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
	<tr>
	<%
	
	//Actualizar el depositItem en conceptos de banco
	PmConn pmConn = new PmConn(sFParams);
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {
		pmConn.open();
		pmConn.disableAutoCommit();
		
		String sql = "select * from requisitions where not reqi_requisitionid in (select rqit_requisitionid from requisitionitems)";
		pmConn.doFetch(sql);
		int i = 0;
		while (pmConn.next()) { %>
			
			<%= i + 1%>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			<%= pmConn.getInt("reqi_requisitionid") %>
			&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			<br>
			<%
			PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(sFParams);
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			
			bmoRequisitionItemNew.getRequisitionId().setValue(pmConn.getInt("reqi_requisitionid"));
			bmoRequisitionItemNew.getName().setValue("Ajuste Automatico");
			bmoRequisitionItemNew.getQuantity().setValue(1);
			bmoRequisitionItemNew.getPrice().setValue(pmConn.getDouble("reqi_amount"));
			bmoRequisitionItemNew.getAmount().setValue(pmConn.getDouble("reqi_amount"));

			pmRequisitionItem.saveSimple(pmConn,bmoRequisitionItemNew, bmUpdateResult);
			
		 i++;	
		}	
				
		
		pmConn.commit();		
	
}catch (Exception e) {
	pmConn.rollback();
	throw new SFException("Proceso Item en conceptos: "+e.toString());
}finally {
	pmConn.close();
}
%>
		
%>
<b>Proceso Terminado</b>
		</td>
	</tr>
</table>

  </body>
</html>