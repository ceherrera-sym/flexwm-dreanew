<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>
    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Migracion de Perfiles</title>
</head>
<body>
	<table>
	<%
	String sql ="",sql2 = "";
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn.open();
	pmConn2.open();
	sql = "SELECT * FROM sfcomponentspecialaccess "
		 + " left join sfcomponentaccess on (sfce_sfcomponentaccessid = sfca_sfcomponentaccessid) "
		 + " left join sfcomponentspecials on (sfcs_sfcomponentspecialid = sfce_sfcomponentspecialid) "
		 + " left join groups on (sfca_groupid = grup_groupid)";
	try{
	pmConn.doFetch(sql);
	int i =0;
	while(pmConn.next()){
		int progId = 0,programspecialId = 0,programprofilesId = 0 ;
		sql2 = "SELECT pgsp_programspecialid,pgsp_programid FROM programspecials where pgsp_code = '"+pmConn.getString("sfcs_code")+"'";
		
		if(!sFParams.isProduction()){
			System.out.println("CONSULA programspecials ::: " + sql2);
		}
		pmConn2.doFetch(sql2);
		
		if(pmConn2.next()){
			progId = pmConn2.getInt("pgsp_programid");
		    programspecialId = pmConn2.getInt("pgsp_programspecialid");
		}
		sql2 = "SELECT pgpf_programprofileid FROM programprofiles left join profiles on (pgpf_profileid = prof_profileid) "
				+"where (prof_name = '"+pmConn.getString("grup_name")+"' AND pgpf_programid = "+progId+")";
		
		if(!sFParams.isProduction()){
			System.out.println("CONSULA programprofiles ::: " + sql2);
		}
		pmConn2.doFetch(sql2);
	
		if(pmConn2.next()){
			programprofilesId = pmConn2.getInt("pgpf_programprofileid");		
		}
		if(programprofilesId > 0 && programspecialId > 0 ){
			sql2 = "INSERT INTO programprofilespecials (pgps_programprofileid,pgps_programspecialid) "
				+"VALUES ("+programprofilesId+","+programspecialId+")";
			if(!sFParams.isProduction()){
				System.out.println("CONSULA INSERT ::: " + sql2);
			}
			pmConn2.doUpdate(sql2);
			i++;
		}

	}%>
	<tr>
		<td>Registros migrados :::  <%=i %></td>
    </tr>
	<%System.out.println("Registros migrados ::: " +i);

	}catch(Exception e){
		%>
		<tr>
			<td>Errores al procesar ::: <%=e.toString() %></td>
		</tr>
		
		<%System.out.println("ERROR ::: " +e.toString());
	}finally{
		pmConn.close();
		pmConn2.close();
	}

%>
	</table>
</body>
</html>