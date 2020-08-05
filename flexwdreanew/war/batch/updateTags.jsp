<%@page import="com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.symgae.shared.GwtUtil"%>
<%@include file="../inc/imports.jsp"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp"%>

<%
	String programTitle = "Actualizar Tags de pipe(|) a dos puntos(:)";
//Obtener variables url
	int idStart =0, idEnd = 0;
	if (request.getParameter("idStart") != null) idStart = Integer.parseInt(request.getParameter("idStart"));
	if (request.getParameter("idEnd") != null) idEnd = Integer.parseInt(request.getParameter("idEnd"));
	
	String css = GwtUtil.getProperUrl(sFParams, "/css/" + defaultCss);
%>

<html>
<head>
<title>:::<%= programTitle %>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= css %>">
<meta charset=utf-8>
</head>
<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top"><img
				border="0" height="" src="<%= sFParams.getMainImageUrl() %>">
			</td>
			<td colspan="" class="reportTitle">&nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>

	<% 

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	PmConn pmConnPrsa = new PmConn(sFParams);
	pmConnPrsa.open();
	
	try {	
			String tagsFinal = "";
			BmUpdateResult bmUpdateResult = new BmUpdateResult();
			BmoPropertySale bmoPropertySale = new BmoPropertySale();
			PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
			
			String sql = "SELECT prsa_propertysaleid, prsa_code, prsa_tags  FROM propertysales " 
						+ " WHERE prsa_tags IS NOT NULL "
						+ " AND prsa_propertysaleid = 3540";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				//System.out.println("venta: "+pmConn.getString("prsa_code"));

				String tags = pmConn.getString("prsa_tags");
				//System.out.println("tagsCompletos: "+tags);

					String tagListHtml = "";
					if (!tags.toString().equals("")) {
						String[] split = tags.split("\\" + "|");
						
						for (int i = 0; i < split.length; i++) {
							String tagId = split[i];
							//System.out.println("tagId1:"+tagId);
							if (!tagId.equals("")) {
								tagsFinal += SFParams.TAG_SEPARATOR + tagId + SFParams.TAG_SEPARATOR;
							}
						}
					}
					
					pmConnPrsa.doUpdate("UPDATE propertysales "
										+ " SET prsa_tags = '" + tagsFinal + "' " 
										+ " WHERE prsa_propertysaleid = " + pmConn.getInt("prsa_propertysaleid"));
				
			} // fin de while
			//System.out.println("tagsFinal: "+tagsFinal);

	%>
		
		<%= bmUpdateResult.errorsToString() %>
		<%
		} catch (Exception e) {
			%>
				Error: <%= e.toString() %>
			<% 
		} finally {
			pmConn.close();	
		}
	%>

	</table>
</body>
</html>
