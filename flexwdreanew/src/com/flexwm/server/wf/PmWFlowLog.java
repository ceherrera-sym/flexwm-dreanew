/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.wf.BmoWFEmail;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;


public class PmWFlowLog extends PmObject {
	BmoWFlowLog bmoWFlowLog;
	PmWFlow pmWFlow = new PmWFlow(getSFParams());

	public PmWFlowLog(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowLog = new BmoWFlowLog();
		setBmObject(bmoWFlowLog);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowLog.getUserId(), bmoWFlowLog.getBmoUser()),
				new PmJoin(bmoWFlowLog.getBmoUser().getLocationId(), bmoWFlowLog.getBmoUser().getBmoLocation()),
				new PmJoin(bmoWFlowLog.getBmoUser().getAreaId(), bmoWFlowLog.getBmoUser().getBmoArea()),
				new PmJoin(bmoWFlowLog.getWFlowId(), bmoWFlowLog.getBmoWFlow()),
				new PmJoin(bmoWFlowLog.getBmoWFlow().getWFlowTypeId(), bmoWFlowLog.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWFlowLog.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowLog.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowLog.getBmoWFlow().getWFlowPhaseId(), bmoWFlowLog.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWFlowLog.getBmoWFlow().getWFlowFunnelId(), bmoWFlowLog.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public String getDisclosureFilters() {


		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Estos Filtros vienen de WFlow
		// Filtro de buscar los flujos donde tengas permisos de lectura
		/** Explicación primera parte:
		 * 		Añadir Flujos de Pedidos(orde) donde tengas permisos de lectura,
		 *  	porque es la base de los cascarones y este genera el id del Flujo,
		 *  	además puede que en los workFlows no exista el programa Pedidos.
		 * 	Explicación segunda parte (OR):
		 * 		Regresar los flujos que concuerden, 
		 * 		despues traer los programas de los workFlows y se quitan los repetidos,
		 * 		buscar que tengas permisos de lectura en los programas encontrados.
		 **/
		if (filters.length() > 0) filters += " AND ";
		
		filters += " (wflw_callercode IN ( "
				+ "			SELECT prog_code FROM " + formatKind("users") 
				+ " 		LEFT JOIN " + formatKind("profileusers") + " ON (pfus_userid = user_userid) "
				+ " 		LEFT JOIN " + formatKind("profiles") + " ON (prof_profileid = pfus_profileid) "
				+ " 		LEFT JOIN " + formatKind("programprofiles") + " ON (pgpf_profileid = prof_profileid) "
				+ " 		LEFT JOIN " + formatKind("programs") + " ON (prog_programid = pgpf_programid) "
				+ " 		WHERE user_userid = " + loggedUserId
				+ " 		AND pgpf_read = 1"
				+ " 		AND prog_code = '" + new BmoOrder().getProgramCode()+ "' "
				+ " 	) "
		
				+ " 	OR wflw_callercode IN ( "
				+ " 		SELECT DISTINCT(prog_code) FROM " + formatKind("wflowcategories")
				+ " 		LEFT JOIN " + formatKind("programs") + " ON (prog_programid = wfca_programid) "
				+ " 		WHERE prog_code IN ( "
				+ "				SELECT prog_code FROM " + formatKind("users") 
				+ " 			LEFT JOIN " + formatKind("profileusers") + " ON (pfus_userid = user_userid) "
				+ " 			LEFT JOIN " + formatKind("profiles") + " ON (prof_profileid = pfus_profileid) "
				+ " 			LEFT JOIN " + formatKind("programprofiles") + " ON (pgpf_profileid = prof_profileid) "
				+ " 			LEFT JOIN " + formatKind("programs") + " ON (prog_programid = pgpf_programid) "
				+ " 			WHERE user_userid = " + loggedUserId
				+ " 			AND pgpf_read = 1"
				+ " 		) "
				+ " 	)"
				+ " )";

		// Filtro de pedidos de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( wflw_companyid IN ( " 
					+ " SELECT uscp_companyid FROM " + formatKind("usercompanies") 
					+ " WHERE uscp_userid = " + loggedUserId + " ) "
					+ " OR wflw_companyid IS NULL " // Para el flujo de clientes(ya que este no pasa la empresa)
					+ " ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " wflw_companyid = " + getSFParams().getSelectedCompanyId()
					+ " OR wflw_companyid IS NULL "; // Para el flujo de clientes(ya que este no pasa la empresa)
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowLog = (BmoWFlowLog)autoPopulate(pmConn, new BmoWFlowLog());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoWFlowLog.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoWFlowLog.setBmoUser(bmoUser);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		if ((int)pmConn.getInt(bmoWFlow.getIdFieldName()) > 0) bmoWFlowLog.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoWFlowLog.setBmoWFlow(bmoWFlow);

		return bmoWFlowLog;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoWFlowLog = (BmoWFlowLog)bmObject;

		// Ultimo guardado
		super.save(pmConn, bmoWFlowLog, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult addLog(PmConn pmConn, BmUpdateResult bmUpdateResult, int wFlowId, char type, String comments) throws SFException {
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		bmoWFlowLog.getWFlowId().setValue(wFlowId);
		bmoWFlowLog.getComments().setValue(comments);
		bmoWFlowLog.getType().setValue(type);

		bmoWFlowLog.getLogdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

		super.save(pmConn, bmoWFlowLog, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult addLog(BmUpdateResult bmUpdateResult, int wFlowId, char type, String comments) throws SFException {
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		bmoWFlowLog.getWFlowId().setValue(wFlowId);
		bmoWFlowLog.getComments().setValue(comments);
		bmoWFlowLog.getType().setValue(type);

		bmoWFlowLog.getLogdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

		super.save(bmoWFlowLog, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult addDataLog(PmConn pmConn, BmUpdateResult bmUpdateResult, int wFlowId, char type, String comments, String data) throws SFException {
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		bmoWFlowLog.getWFlowId().setValue(wFlowId);
		bmoWFlowLog.getComments().setValue(comments);
		bmoWFlowLog.getType().setValue(type);
		bmoWFlowLog.getData().setValue(data);

		bmoWFlowLog.getLogdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

		super.save(pmConn, bmoWFlowLog, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult addDataLog(BmUpdateResult bmUpdateResult, int wFlowId, char type, String comments, String data) throws SFException {
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		bmoWFlowLog.getWFlowId().setValue(wFlowId);
		bmoWFlowLog.getComments().setValue(comments);
		bmoWFlowLog.getType().setValue(type);
		bmoWFlowLog.getData().setValue(data);

		bmoWFlowLog.getLogdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

		super.save(bmoWFlowLog, bmUpdateResult);

		return bmUpdateResult;
	}

	public BmUpdateResult addLinkLog(PmConn pmConn, BmUpdateResult bmUpdateResult, int wFlowId, char type, String comments, String link) throws SFException {
		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		bmoWFlowLog.getWFlowId().setValue(wFlowId);
		bmoWFlowLog.getComments().setValue(comments);
		bmoWFlowLog.getType().setValue(type);
		bmoWFlowLog.getLink().setValue(link);

		bmoWFlowLog.getLogdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

		super.save(pmConn, bmoWFlowLog, bmUpdateResult);

		return bmUpdateResult;
	}

	public void addEmailLog(PmConn pmConn, BmoWFEmail bmoWFEmail, BmUpdateResult bmUpdateResult) throws SFException {
		String comments = " Para: " + bmoWFEmail.getTo().toString() + "\n" + 
				" Asunto: " + bmoWFEmail.getSubject().toString();

		String data = " Para: " + bmoWFEmail.getTo().toString() + "\n" + 
				" Cp: " + bmoWFEmail.getCp().toString() + "\n" + 
				" De: " + bmoWFEmail.getFrom().toString() + "\n" + 
				" Asunto: " + bmoWFEmail.getSubject().toString() + "\n" + 
				" Cuerpo: " + bmoWFEmail.getBody().toString() + "\n" + 
				" Texto Fijo: " + bmoWFEmail.getFixedBody().toString();

		addDataLog(pmConn, bmUpdateResult, bmoWFEmail.getWFlowId().toInteger(), BmoWFlowLog.TYPE_EMAIL, comments, data);
	}
}
