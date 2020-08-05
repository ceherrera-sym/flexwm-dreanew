
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Fallas de los Creditos";
	String programDescription = programTitle;
	
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
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

int idStart = 0, idEnd = 0;
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));

String sql = "", where = "" ;

if (idStart > 0)
	where += " AND racc_orderid >= " + idStart;

if (idEnd > 0)
	where += " AND racc_orderid <= " + idEnd;

PmConn pmConn = new PmConn(sFParams);
pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

BmUpdateResult bmUpdateResult = new BmUpdateResult();

PmOrder pmOrder = new PmOrder(sFParams);
BmoOrder bmoOrder = new BmoOrder();

PmRaccount pmRaccount = new PmRaccount(sFParams);

try {		

	//Crear la penalización si no tiene
	sql = " SELECT * FROM raccounts " +
	      " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
		  " LEFT JOIN orders ON (racc_orderid = orde_orderid) " +	
	      //" WHERE racc_linked = 1 " +	
		  " WHERE racc_duedate <= '2017-10-22'" +
	      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
		  " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +
	      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + where +
	      " order by racc_receivedate ";  
	pmConn.doFetch(sql);
	while(pmConn.next()) {
		bmoOrder = (BmoOrder)pmOrder.get(pmConn.getInt("racc_orderid"));
		if (!pmRaccount.hasPenalty(pmConn2, bmoOrder, bmUpdateResult)) {
			pmRaccount.createPenalty(pmConn2, bmoOrder, pmConn.getInt("racc_raccountid"), bmUpdateResult);
		} else {
			BmoCredit bmoCredit = new BmoCredit();
			PmCredit pmCredit = new PmCredit(sFParams);
			bmoCredit = (BmoCredit) pmCredit.getBy(bmoOrder.getId(), bmoCredit.getOrderId().getName());

			bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);

			pmCredit.saveSimple(bmoCredit, bmUpdateResult);

			// Ligar la Penalizacion ya creada a la CxC que fallo
			// esta semana
			pmRaccount.updatePenaltyFromNewFail(pmConn2, bmoOrder, pmConn.getInt("racc_raccountid"), bmUpdateResult);
		}
	}
	
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {
	throw new SFException(e.toString());
} finally {
	pmConn2.close();
	pmConn.close();
}
	


%>

</table>
</body>
</html>


