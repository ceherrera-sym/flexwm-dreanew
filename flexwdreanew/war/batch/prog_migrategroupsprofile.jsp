<%@include file="../inc/login.jsp"%>
<%@include file="../inc/imports.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<%=sFParams.getAppURL()%>css/<%=defaultCss%>">
</head>
<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<%
			String sql = "", sqlGroup = "", groupName = "";
			int profileId = 0;
			boolean existTable = true;
			PmConn pmConn = new PmConn(sFParams);
			PmConn pmConnGroup = new PmConn(sFParams);
			pmConn.open();
			pmConnGroup.open();

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			sql = "SELECT qost_groupid FROM quotestaff GROUP BY qost_groupid";
			int i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("qost_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("qost_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE quotestaff SET qost_profileid = " + profileId + " WHERE qost_groupid = "
								+ pmConn.getInt("qost_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}

				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en quotestaff ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				quotestaff ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT bkac_groupid FROM bankaccounts GROUP BY bkac_groupid";
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				i = 0;
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("bkac_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("bkac_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE bankaccounts SET bkac_profileid = " + profileId
								+ " WHERE bkac_groupid = " + pmConn.getInt("bkac_groupid");

						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}

				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en bankaccounts ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				bankaccounts ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT comi_groupid FROM commissions GROUP bY comi_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("comi_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("comi_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE commissions SET comi_profileid = " + profileId
								+ " WHERE comi_groupid = " + pmConn.getInt("comi_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en commissions ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				commissions ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT orca_groupid FROM ordercommissionamounts GROUP BY orca_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("orca_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("orca_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE ordercommissionamounts SET orca_profileid = " + profileId
								+ " WHERE orca_groupid = "
								+ pmConn.getInt("orca_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en ordercommissionamounts ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				ordercommissionamounts ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT ords_groupid FROM orderstaff GROUP BY ords_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("ords_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("ords_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE orderstaff SET ords_profileid = " + profileId + " WHERE ords_groupid = "
								+ pmConn.getInt("ords_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en orderstaff ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				orderstaff ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT ortp_groupid FROM ordertypes GROUP BY ortp_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("ortp_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("ortp_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE ordertypes SET ortp_profileid = " + profileId + " WHERE ortp_groupid = "
								+ pmConn.getInt("ortp_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en ordertypes 'Perfil Notf. Fin Pedido' ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en ordertypes 'Perfil Notf. Fin Pedido' ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT ortp_dispersiongroupid FROM ordertypes GROUP BY ortp_dispersiongroupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					
					if (pmConn.getInt("ortp_dispersiongroupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("ortp_dispersiongroupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE ordertypes SET ortp_dispersionprofileid = " + profileId + " WHERE ortp_dispersiongroupid = "
								+ pmConn.getInt("ortp_dispersiongroupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en ordertypes 'Perfil Dispersión Crédito'::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en ordertypes 'Perfil Dispersión Crédito'::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT wfst_groupid FROM wflowsteptypes GROUP BY wfst_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("wfst_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("wfst_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE wflowsteptypes SET wfst_profileid = " + profileId
								+ " WHERE wfst_groupid = " + pmConn.getInt("wfst_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en wflowsteptypes ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				wflowsteptypes ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT wflu_groupid FROM wflowusers GROUP BY wflu_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("wflu_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("wflu_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE wflowusers SET wflu_profileid = " + profileId + " WHERE wflu_groupid = "
								+ pmConn.getInt("wflu_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en wflowusers ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				wflowusers ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT wfus_groupid FROM wflowuserselect GROUP BY wfus_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("wfus_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("wfus_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE wflowuserselect SET wfus_profileid = " + profileId
								+ " WHERE wfus_groupid = " + pmConn.getInt("wfus_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en wflowuserselect ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				wflowuserselect ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT setp_groupid FROM sessiontypepackages GROUP BY setp_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("setp_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("setp_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE sessiontypepackages SET setp_profileid = " + profileId
								+ " WHERE setp_groupid = " + pmConn.getInt("setp_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en sessiontypepackages ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				sessiontypepackages ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT wfgp_groupid FROM wflowgroups GROUP BY wfgp_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("wfgp_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("wfgp_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE wflowgroups SET wfgp_profileid = " + profileId
								+ " WHERE wfgp_groupid = " + pmConn.getInt("wfgp_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en wflowgroups ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				wflowgroups ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT wfsp_groupid FROM wflowsteps group by wfsp_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("wfsp_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("wfsp_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE wflowsteps SET wfsp_profileid = " + profileId + " WHERE wfsp_groupid = "
								+ pmConn.getInt("wfsp_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en wflowsteps ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				wflowsteps ::: <%=i%></td>
		</tr>
		<%
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql = "SELECT sdrp_groupid FROM sendreports GROUP BY sdrp_groupid";
			i = 0;
			try {
				pmConn.doFetch(sql);
				existTable = true;
			} catch (Exception e) {
				existTable = false;
			}
			if (existTable) {
				while (pmConn.next()) {
					groupName = "";
					if (pmConn.getInt("sdrp_groupid") > 0) {
						sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("sdrp_groupid");

						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							groupName = pmConnGroup.getString("grup_name");
						}
						sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
						pmConnGroup.doFetch(sqlGroup);
						if (pmConnGroup.next()) {
							profileId = pmConnGroup.getInt("prof_profileid");
						}

						sqlGroup = "UPDATE sendreports SET sdrp_profileid = " + profileId
								+ " WHERE sdrp_groupid = " + pmConn.getInt("sdrp_groupid");
						pmConnGroup.doUpdate(sqlGroup);
						i++;
					}
				}
			}
			if (!sFParams.isProduction()) {
				System.out.println("Registros procesados en sendreports ::: " + i);
			}
		%>
		<tr>
			<td align="center" class="reportTitle">Registros procesados en
				sendreports ::: <%=i %></td>
		</tr>
		<%
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		sql = "SELECT flxc_salesgroupid, flxc_collectgroupid FROM flexconfig ";
		i = 0;
		try {
			pmConn.doFetch(sql);
			existTable = true;
		} catch (Exception e) {
			existTable = false;
		}
		if (existTable) {
			while (pmConn.next()) {
				groupName = "";
				if (pmConn.getInt("flxc_salesgroupid") > 0) {
					sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("flxc_salesgroupid");

					pmConnGroup.doFetch(sqlGroup);
					if (pmConnGroup.next()) {
						groupName = pmConnGroup.getString("grup_name");
					}
					sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
					pmConnGroup.doFetch(sqlGroup);
					if (pmConnGroup.next()) {
						profileId = pmConnGroup.getInt("prof_profileid");
					}

					sqlGroup = "UPDATE flexconfig "
							+ " SET flxc_salesprofileid = " + profileId
							+ " WHERE flxc_salesgroupid = " + pmConn.getInt("flxc_salesgroupid");
					pmConnGroup.doUpdate(sqlGroup);
					
				}
				
				groupName = "";
				if (pmConn.getInt("flxc_collectgroupid") > 0) {
					sqlGroup = "SELECT grup_name FROM groups WHERE grup_groupid = " + pmConn.getInt("flxc_collectgroupid");

					pmConnGroup.doFetch(sqlGroup);
					if (pmConnGroup.next()) {
						groupName = pmConnGroup.getString("grup_name");
					}
					sqlGroup = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + groupName + "'";
					pmConnGroup.doFetch(sqlGroup);
					if (pmConnGroup.next()) {
						profileId = pmConnGroup.getInt("prof_profileid");
					}

					sqlGroup = "UPDATE flexconfig "
							+ " SET flxc_collectprofileid = " + profileId
							+ " WHERE flxc_collectgroupid = " + pmConn.getInt("flxc_collectgroupid");
					pmConnGroup.doUpdate(sqlGroup);
					
				}
				i++;
			}
		}
		if (!sFParams.isProduction()) {
			System.out.println("Registros procesados en flexconfig ::: " + i);
		}
	%>
	<tr>
		<td align="center" class="reportTitle">Registros procesados en
			flexconfig ::: <%=i %></td>
	</tr>
	<%
pmConn.close();
pmConnGroup.close();

%>
	</table>
</body>
</html>