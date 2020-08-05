<%@include file="/inc/login_opt.jsp"%>
<!DOCTYPE html>
<html>
<body>
	<table>
		<%
			String sql = "", sql2 = "",moduleCode = "";		
			int programId = 0;
			PmConn pmConn = new PmConn(sFParams);
			PmConn pmConn2 = new PmConn(sFParams);
			pmConn.open();
			pmConn2.open();
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT emai_moduleid FROM emails GROUP BY emai_moduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("emai_moduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE emails SET  emai_programid = " + programId + " WHERE emai_moduleid = " + pmConn.getInt("emai_moduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en emails </td>
				</tr>
				<%	
			} catch (Exception e) {
			%>
		<tr>
			<td>Ocurrio un error en emails ::: <%=e %></td>
		</tr>
		<%
			} 
		%>
		<%
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT wfca_moduleid FROM wflowcategories GROUP BY wfca_moduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("wfca_moduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE wflowcategories SET  wfca_programid = " + programId + " WHERE wfca_moduleid = " + pmConn.getInt("wfca_moduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en wflowcategories </td>
				</tr>
				<%	
			} catch (Exception e) {%>
		<tr>
			<td>Ocurrio un error en wflowcategories ::: <%=e %></td>
		</tr>
		<%
			} 
		%>
		<%
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT user_startmoduleid FROM users GROUP BY user_startmoduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("user_startmoduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE users SET  user_startprogramid = " + programId + " WHERE user_startmoduleid = " + pmConn.getInt("user_startmoduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en users </td>
				</tr>
				<%	
			} catch (Exception e) {%>
		<tr>
			<td>Ocurrio un error en users ::: <%=e %></td>
		</tr>
		<%
			} 
		%>
		<%
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT sfcf_startmoduleid FROM sfconfig GROUP BY sfcf_startmoduleid;";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("sfcf_startmoduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE sfconfig SET  sfcf_startprogramid = " + programId + " WHERE sfcf_startmoduleid = " + pmConn.getInt("sfcf_startmoduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en sfconfig </td>
				</tr>
				<%	
			} catch (Exception e) {%>
		<tr>
			<td>Ocurrio un error en sfconfig ::: <%=e %></td>
		</tr>
		<%
			} 
		%>
		<%
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT sflg_moduleid FROM sflogs GROUP BY sflg_moduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("sflg_moduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE sflogs SET  sflg_programid = " + programId + " WHERE sflg_moduleid = " + pmConn.getInt("sflg_moduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en sflogs </td>
				</tr>
				<%	
			} catch (Exception e) {%>
		<tr>
			<td>Ocurrio un error en sflogs ::: <%=e %></td>
		</tr>
		<%
			} 
		%>
		<%
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT file_moduleid FROM sffiles GROUP BY file_moduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("file_moduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE sffiles SET  file_programid = " + programId + " WHERE file_moduleid = " + pmConn.getInt("file_moduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en sffiles </td>
				</tr>
				<%	
			} catch (Exception e) {%>
		<tr>
			<td>Ocurrio un error en sffiles ::: <%=e %></td>
		</tr>
		<%
			} 
		%>
		<%
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT frmt_moduleid FROM formats GROUP BY frmt_moduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("frmt_moduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE formats SET  frmt_programid = " + programId + " WHERE frmt_moduleid = " + pmConn.getInt("frmt_moduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en formats </td>
				</tr>
				<%	
			} catch (Exception e) {%>
		<tr>
			<td>Ocurrio un error en formats ::: <%=e %></td>
		</tr>
		<%
			} 
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try {

				sql = "SELECT sfcp_moduleid FROM sfcaptions GROUP BY sfcp_moduleid";
				pmConn.doFetch(sql);

				while (pmConn.next()) {
					sql2 = "SELECT modu_code FROM modules WHERE modu_moduleid = " + pmConn.getInt("sfcp_moduleid");
					pmConn2.doFetch(sql2);
					if(pmConn2.next()){
						moduleCode = pmConn2.getString("modu_code");
					}
					if(moduleCode.length() > 0){
						sql2 = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+moduleCode+"'";
						pmConn2.doFetch(sql2);
						if(pmConn2.next()){							
							programId = pmConn2.getInt("prog_programid");
						}
						if(programId > 0){
							sql2 = "UPDATE sfcaptions SET sfcp_programid = " + programId + " WHERE sfcp_moduleid = " + pmConn.getInt("sfcp_moduleid");
							pmConn2.doUpdate(sql2);
							
						}
					}										
				}
				%>
				<tr>
					<td>Migraci&oacuten terminada en detalle de modulos </td>
				</tr>
				<%	
			} catch (Exception e) {%>
			<tr>
				<td>Ocurrio un error en detalle de modulos ::: <%=e %></td>
			</tr>
			<%
				} 
				pmConn2.open();
				pmConn.close();
			
		%>
	</table>
</body>
</html>