<%@include file="../inc/login.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

</head>
<body>
	<table>
		<%
			BmoProfile bmoProfile = new BmoProfile();
			PmProfile pmProfile = new PmProfile(sFParams);
			PmConn pmConn = new PmConn(sFParams);
			PmConn pmConn2 = new PmConn(sFParams);

			pmConn.open();
			pmConn2.open();

			String sql = "", sql2 = "";
			try {
				sql = "SELECT wfug_wflowcategoryid,wfug_groupid,grup_name FROM wflowusergroups "
						+ "LEFT JOIN groups ON (wfug_groupid = grup_groupid)";
				int i = 0;
				pmConn.doFetch(sql);
				while (pmConn.next()) {
					sql2 = "SELECT * FROM wflowcategoryprofiles WHERE wfcp_wflowcategoryid = "
							+ pmConn.getInt("wfug_wflowcategoryid") + " AND wfcp_groupid = "
							+ pmConn.getInt("wfug_groupid");
					pmConn2.doFetch(sql2);
					if (!pmConn2.next()) {
						bmoProfile = (BmoProfile) pmProfile.getBy(pmConn.getString("grup_name"),
								bmoProfile.getName().getName());

						sql2 = "INSERT INTO wflowcategoryprofiles (wfcp_required,wfcp_wflowcategoryid,wfcp_groupid,wfcp_profileid,wfcp_autoprofile,wfcp_autodate) "
								+ " VALUES (0," + pmConn.getInt("wfug_wflowcategoryid") + ","
								+ pmConn.getInt("wfug_groupid") + "," + bmoProfile.getId() + ",0,1)";
						pmConn2.doUpdate(sql2);
						i++;
					}

				}
				%>
				<tr>
					<td>Registros copiados ::: <%=i%></td>
				</tr>
				<%
			} catch (Exception e) {
		%>
		<tr>
			<td>Se encontro un error ::: <%=e%></td>
		</tr>
		<%
			}

			pmConn.close();
			pmConn2.close();
		%>
	</table>
</body>
</html>