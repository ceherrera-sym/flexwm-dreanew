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
import java.util.Iterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.server.sf.PmProfileUser;
import com.flexwm.server.wf.PmWFlowDocument;
import com.flexwm.server.wf.PmWFlowDocumentType;
import com.flexwm.server.wf.PmWFlowCategoryProfile;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowPhase;
import com.flexwm.server.wf.PmWFlowStep;
import com.flexwm.server.wf.PmWFlowStepDep;
import com.flexwm.server.wf.PmWFlowStepType;
import com.flexwm.server.wf.PmWFlowStepTypeDep;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.server.wf.PmWFlowUser;
import com.symgae.server.PmConn;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowDocumentType;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowCategoryProfile;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowStepDep;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowStepTypeDep;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowUser;


public class PmWFlow extends PmObject {
	BmoWFlow bmoWFlow;

	public PmWFlow(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlow = new BmoWFlow();
		setBmObject(bmoWFlow);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlow.getWFlowTypeId(), bmoWFlow.getBmoWFlowType()),
				new PmJoin(bmoWFlow.getBmoWFlowType().getWFlowCategoryId(), bmoWFlow.getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlow.getWFlowPhaseId(), bmoWFlow.getBmoWFlowPhase()),
				new PmJoin(bmoWFlow.getWFlowFunnelId(), bmoWFlow.getBmoWFlowFunnel())
				)));
	}
	
	@Override
	public String getDisclosureFilters() {

		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de usuario 
		if (getSFParams().restrictData(bmoWFlow.getProgramCode())) {

			filters = "( wflw_userid IN (" +
					" SELECT user_userid FROM " + formatKind("users") +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid IN ( " +
					" select u2.user_userid FROM " + formatKind("users") + " u1 " +
					" LEFT JOIN " + formatKind("users") + " u2 ON (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" select u3.user_userid FROM " + formatKind("users") + " u1 " +
					" LEFT JOIN " + formatKind("users") + " u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN " + formatKind("users") + " u3 ON (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" select u4.user_userid FROM " + formatKind("users") + " u1 " +
					" LEFT JOIN " + formatKind("users") + " u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN " + formatKind("users") + " u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN " + formatKind("users") + " u4 ON (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" select u5.user_userid FROM " + formatKind("users") + " u1 " +
					" LEFT JOIN " + formatKind("users") + " u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN " + formatKind("users") + " u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN " + formatKind("users") + " u4 ON (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN " + formatKind("users") + " u5 ON (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" OR " +
					" ( " +
					" wflw_wflowid IN ( " +
					" SELECT wflw_wflowid FROM " + formatKind("wflowusers") + "  " +
					" LEFT JOIN " + formatKind("wflows") + " ON (wflu_wflowid = wflw_wflowid) " +
					" WHERE wflu_userid = " + loggedUserId +
					"   ) " +
					" ) " +
					" ) ";
		}

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

		// Se resuelve con el lo de arriba, si no funciona lo de arriba, habilitar esto, y agregar cada flujo(cascaron) nuevo aqui
		//		// Descartar si NO tiene permiso de ver Módulo Oportunidades
		//		if (!getSFParams().hasRead(bmoOpportunity.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoOpportunity.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Pedidos
		//		if (!getSFParams().hasRead(bmoOrder.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoOrder.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Minuta
		//		if (!getSFParams().hasRead(bmoMeeting.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoMeeting.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Proyectos
		//		if (!getSFParams().hasRead(bmoProject.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoProject.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Vta. Inmuebles
		//		if (!getSFParams().hasRead(bmoPropertySale.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoPropertySale.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Fases de Desarrollo
		//		if (!getSFParams().hasRead(bmoDevelopmentPhase.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoDevelopmentPhase.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Vtas. de Sesion
		//		if (!getSFParams().hasRead(bmoSessionSale.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoSessionSale.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Creditos
		//		if (!getSFParams().hasRead(bmoCredit.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoCredit.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Actividades
		//		if (!getSFParams().hasRead(bmoActivity.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoActivity.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Arrendamientos
		//		if (!getSFParams().hasRead(bmoPropertyRental.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoPropertyRental.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Clientes
		//		if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoCustomer.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Ordes de compra
		//		if (!getSFParams().hasRead(bmoRequisition.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoRequisition.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo Proyectos (Visual)
		//		if (!getSFParams().hasRead(bmoProjectStep.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoProjectStep.getProgramCode() + "'";
		//		}
		//
		//		// Descartar si NO tiene permiso de ver Módulo RFQ
		//		if (!getSFParams().hasRead(bmoRFQU.getProgramCode())) {
		//			if (filters.length() > 0) filters += " AND ";
		//			filters += " wflw_callercode <> '" + bmoRFQU.getProgramCode() + "'";
		//		}

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
		BmoWFlow bmoWFlow = (BmoWFlow)autoPopulate(pmConn, new BmoWFlow());

		// BmoWFlowType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int wflowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (wflowTypeId > 0) bmoWFlow.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoWFlow.setBmoWFlowType(bmoWFlowType);

		// BmoWFlowPhase
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		int wflowPhaseId = (int)pmConn.getInt(bmoWFlowPhase.getIdFieldName());
		if (wflowPhaseId > 0) bmoWFlow.setBmoWFlowPhase((BmoWFlowPhase) new PmWFlowPhase(getSFParams()).populate(pmConn));
		else bmoWFlow.setBmoWFlowPhase(bmoWFlowPhase);

		// BmoWFlowFunnel
		BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();
		int wFlowFunnelId = (int)pmConn.getInt(bmoWFlowFunnel.getIdFieldName());
		if (wFlowFunnelId > 0) bmoWFlow.setBmoWFlowFunnel((BmoWFlowFunnel) new PmWFlowFunnel(getSFParams()).populate(pmConn));
		else bmoWFlow.setBmoWFlowFunnel(bmoWFlowFunnel);

		return bmoWFlow;
	}

	// Crea un nuevo wflow o actualiza el actual, a partir de datos de un objeto foraneo
	public BmoWFlow updateWFlow(PmConn pmConn, int wFlowTypeId, int wFlowId, String programCode, int foreignId, int userId, int companyId, int customerId, String code, String name, 
			String description, String startDate, String endDate, char wFlowStatus, BmUpdateResult bmUpdateResult) throws SFException {

		BmoCustomer bmoCustomer = new BmoCustomer();
		BmoRequisition bmoRequisition = new BmoRequisition();

		// Esta asignado el tipo de wflow
		if (wFlowTypeId > 0) {

			// Es nuevo registro
			if (!(wFlowId > 0)) {
				// Crea un nuevo wFlow
				bmoWFlow = new BmoWFlow();
				bmoWFlow.getStatus().setValue(wFlowStatus);
				bmoWFlow.getCode().setValue(code);
				bmoWFlow.getName().setValue(name);
				bmoWFlow.getStartDate().setValue(startDate);
				bmoWFlow.getEndDate().setValue(endDate);
				bmoWFlow.getProgress().setValue(0);
				bmoWFlow.getCallerCode().setValue(programCode);
				bmoWFlow.getCallerId().setValue(foreignId);
				bmoWFlow.getUserId().setValue(userId);
				bmoWFlow.getCustomerId().setValue(customerId);
				if (companyId > 0) bmoWFlow.getCompanyId().setValue(companyId);
				bmoWFlow.getDescription().setValue(description);
				bmoWFlow.getWFlowTypeId().setValue(wFlowTypeId);

				// Determina cual tipo y categoría de flujo es
				PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
				BmoWFlowType bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, wFlowTypeId);
				bmoWFlow.setBmoWFlowType(bmoWFlowType);
				bmoWFlow.getHours().setValue(bmoWFlowType.getHours().toDouble());
				bmoWFlow.getBillable().setValue(bmoWFlowType.getBillable().toInteger());

				// Determina cual es la primer fase
				PmWFlowPhase pmWFlowPhase = new PmWFlowPhase(getSFParams());
				bmoWFlow.getWFlowPhaseId().setValue(pmWFlowPhase.getFirstPhaseId(pmConn, bmoWFlowType.getWFlowCategoryId().toInteger()));

				// Guardar el nuevo WFlow
				this.save(pmConn, bmoWFlow, bmUpdateResult);

				bmoWFlow.setId(bmUpdateResult.getId());

				// No crear datos adicionales para flujo del modulo cliente/oc
				if (!bmoCustomer.getProgramCode().toString().equals(programCode) 
						&& !bmoRequisition.getProgramCode().toString().equals(programCode)) {

					// Crear los pasos del tipo de proyecto
					this.createSteps(pmConn, bmoWFlow, bmUpdateResult);

					// Crear los usuarios de la categoria del flujo
					this.createWFlowUsers(pmConn, bmoWFlow, userId, programCode, bmUpdateResult);

					// Crear los documentos del tipo de proyecto
					this.createDocuments(pmConn, bmoWFlow, bmUpdateResult);

					// Actualiza estatus documentos
					this.checkRequiredDocuments(pmConn, bmoWFlow, bmUpdateResult);
				}

			} else {
				// Ya existe un WFlow, Actualiza los datos del WFlow
				bmoWFlow = (BmoWFlow)this.get(pmConn, wFlowId);
				bmoWFlow.getCode().setValue(code);
				bmoWFlow.getName().setValue(name);
				bmoWFlow.getDescription().setValue(description);
				// Actualizar fechas para los flujos diferentes del modulo de Cliente
				if (!bmoCustomer.getProgramCode().toString().equals(programCode)) {
					bmoWFlow.getStartDate().setValue(startDate);
					bmoWFlow.getEndDate().setValue(endDate);
				}
				bmoWFlow.getStatus().setValue(wFlowStatus);
				bmoWFlow.getUserId().setValue(userId);
				bmoWFlow.getCustomerId().setValue(customerId);
				if (companyId > 0) bmoWFlow.getCompanyId().setValue(companyId);

				// Guardar el la modificacion al WFlow
				super.save(pmConn, bmoWFlow, bmUpdateResult);

				// Actualiza las fechas
				this.changeDate(pmConn, bmoWFlow, bmUpdateResult);

				// Actualiza estatus documentos
				this.checkRequiredDocuments(pmConn, bmoWFlow, bmUpdateResult);
			}

			// Calendario google, si no hay errores y esta habilitado en la categoria
			if (!bmUpdateResult.hasErrors() &&
					bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarSync().toBoolean()) {

				// Revisa que sea autentificacion google
				/*
				if (getSFParams().isGoogleAuth()) {
					updateGoogleCalendar(pmConn, bmoWFlow, bmUpdateResult);	

					// Actualiza los calendarios de los usuarios del flujo
					updateWFlowUsersGoogleCalendar(pmConn, bmoWFlow, bmUpdateResult);
				}	
				*/
			}
		}

		return bmoWFlow;
	}
	
	// Cambia(Renueva) todo el WorkFlow de un Flujo
	public void changeWorkWFlowType(PmConn pmConn, BmoWFlow bmoWFlow, int wFlowTypeId, BmUpdateResult bmUpdateResult) throws SFException {
		// Borra dependencias de los pasos
		pmConn.doUpdate("DELETE FROM wflowstepdeps "
				+ " WHERE wfsz_wflowstepid IN "
				+ "		(SELECT wfsp_wflowstepid FROM wflowsteps WHERE wfsp_wflowid = " + bmoWFlow.getId() + ")");

		// Borra documentos
		pmConn.doUpdate("DELETE FROM wflowdocuments WHERE wfdo_wflowid = " + bmoWFlow.getId());

		// Borra pasos
		pmConn.doUpdate("DELETE FROM wflowsteps WHERE wfsp_wflowid = " + bmoWFlow.getId());

		// Borra bitacora
		pmConn.doUpdate("DELETE FROM wflowlogs WHERE wflg_wflowid = " + bmoWFlow.getId());

		// Borra usuarios
		pmConn.doUpdate("DELETE FROM wflowusers WHERE wflu_wflowid = " + bmoWFlow.getId());

		// Cambia el tipo de flujo
		bmoWFlow.getWFlowTypeId().setValue(wFlowTypeId);
		super.save(pmConn, bmoWFlow, bmUpdateResult);	
		
		// Crear los pasos del tipo de proyecto
		this.createSteps(pmConn, bmoWFlow, bmUpdateResult);

		// Crear los usuarios de la categoria del flujo
		this.createWFlowUsers(pmConn, bmoWFlow, bmoWFlow.getUserId().toInteger(), bmoWFlow.getCallerCode().toString(), bmUpdateResult);

		// Crear los documentos del tipo de proyecto
		this.createDocuments(pmConn, bmoWFlow, bmUpdateResult);

		// Actualiza estatus documentos
		this.checkRequiredDocuments(pmConn, bmoWFlow, bmUpdateResult);
	}

	// Cambia el Tipo de Flujo de un Flujo
	public void changeWFlowType(PmConn pmConn, BmoWFlow bmoWFlow, int wFlowTypeId, BmUpdateResult bmUpdateResult) throws SFException {
		// Borra dependencias de los pasos
		pmConn.doUpdate("DELETE FROM wflowstepdeps "
				+ " WHERE wfsz_wflowstepid IN "
				+ "		(SELECT wfsp_wflowstepid FROM wflowsteps WHERE wfsp_wflowid = " + bmoWFlow.getId() + ")");

		// Eliminar tareas ligaas
		pmConn.doUpdate("DELETE FROM wflowsteps WHERE wfsp_wflowid = " + bmoWFlow.getId());

		// Cambia el tipo de flujo
		bmoWFlow.getWFlowTypeId().setValue(wFlowTypeId);
		super.save(pmConn, bmoWFlow, bmUpdateResult);

		// Crear nuevas tareas
		this.createSteps(pmConn, bmoWFlow, bmUpdateResult);

		// Crear documentos que no existian, dejar los que ya existen
		this.createDocuments(pmConn, bmoWFlow, bmUpdateResult);
	}

	public void updateGoogleCalendar(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		// Actualiza calendario google si esta habilitado la sincronizacion de calendarios
		if (bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarSync().toBoolean()) {
			try {
				SFGCalendar gCalendar = new SFGCalendar(getSFParams(), getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
				bmoWFlow.getGoogleEventId().setValue(
						gCalendar.updateEvent(bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarId().toString(), 
								bmoWFlow.getGoogleEventId().toString(), 
								bmoWFlow.getCode().toString(), 
								bmoWFlow.getName().toString(), 
								new PmWFlowUser(getSFParams()).wFlowUsersToString(pmConn, bmoWFlow),
								bmoWFlow.getStartDate().toString(), 
								bmoWFlow.getEndDate().toString()));

				// Guardar el la modificacion al WFlow
				super.save(pmConn, bmoWFlow, bmUpdateResult);			

			} catch (SFException e) {
				bmUpdateResult.addMsg(this.getClass().getName() + "-updateGoogleCalendar(): Revisar que este asignado el Usuario de Calendario Google a nivel Sistema en Configuración SYMGF: " + e.toString());
			}
		}
		*/
	}

	private void createSteps(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {

		// Revisa si existen tareas ya creadas, en cuyo caso se detiene la creacion automatica
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());
		BmFilter filterWFlow = new BmFilter();
		filterWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoWFlow.getId());
		ArrayList<BmObject> wFlowStepList = pmWFlowStep.list(pmConn, filterWFlow);

		if (!(wFlowStepList.size() > 0)) {
			// Obten lista de steps segun tipo de flujo
			PmWFlowStepType pmWFlowStepType = new PmWFlowStepType(getSFParams());
			BmoWFlowStepType bmoWFlowStepType = new BmoWFlowStepType();
			BmFilter filterWFlowType = new BmFilter();
			filterWFlowType.setValueFilter(bmoWFlowStepType.getKind(), bmoWFlowStepType.getWFlowTypeId(), bmoWFlow.getWFlowTypeId().toInteger());
			Iterator<BmObject> stepTypeList = pmWFlowStepType.list(pmConn, filterWFlowType).iterator();

			// Por cada step type, genera un step asociado a este flujo
			while (stepTypeList.hasNext()) {
				bmoWFlowStepType = (BmoWFlowStepType)stepTypeList.next();
				bmoWFlowStep = new BmoWFlowStep();
				bmoWFlowStep.getName().setValue(bmoWFlowStepType.getName().toString());
				bmoWFlowStep.getDescription().setValue(bmoWFlowStepType.getDescription().toString());
				bmoWFlowStep.getType().setValue(BmoWFlowStep.TYPE_WFLOW);
				bmoWFlowStep.getWFlowValidationId().setValue(bmoWFlowStepType.getWFlowValidationId().toString());
				bmoWFlowStep.getWFlowActionId().setValue(bmoWFlowStepType.getWFlowActionId().toString());
				bmoWFlowStep.getDaysRemind().setValue(bmoWFlowStepType.getDaysRemind().toString());
				bmoWFlowStep.getEmailReminders().setValue(bmoWFlowStepType.getEmailReminders().toBoolean());
				bmoWFlowStep.getSequence().setValue(bmoWFlowStepType.getSequence().toString());
				bmoWFlowStep.getWFlowFunnelId().setValue(bmoWFlowStepType.getWFlowFunnelId().toInteger());
				bmoWFlowStep.getHours().setValue(bmoWFlowStepType.getHours().toDouble());
				bmoWFlowStep.getBillable().setValue(bmoWFlowStepType.getBillable().toInteger());
				bmoWFlowStep.getRate().setValue(bmoWFlowStepType.getRate().toDouble());
				bmoWFlowStep.getWFlowPhaseId().setValue(bmoWFlowStepType.getWFlowPhaseId().toString());
				bmoWFlowStep.getProgress().setValue(0);
				bmoWFlowStep.getEnabled().setValue(false);
				bmoWFlowStep.getProfileId().setValue(bmoWFlowStepType.getProfileId().toString());
				bmoWFlowStep.getProgress().setValue(0);
				bmoWFlowStep.getWFlowId().setValue(bmoWFlow.getId());

				pmWFlowStep.saveCheckDates(pmConn, bmoWFlow, bmoWFlowStep, bmUpdateResult);
			}

			// Obten lista de dependencias de este tipo de flujo
			PmWFlowStepTypeDep pmWFlowStepTypeDep = new PmWFlowStepTypeDep(getSFParams());
			BmoWFlowStepTypeDep bmoWFlowStepTypeDep = new BmoWFlowStepTypeDep();
			BmFilter filterWFlowStepTypeDeps = new BmFilter();
			filterWFlowStepTypeDeps.setValueFilter(bmoWFlowStepTypeDep.getBmoWFlowStepType().getKind(), 
					bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowTypeId(), 
					bmoWFlow.getWFlowTypeId().toInteger());

			Iterator<BmObject> stepTypeDepList = pmWFlowStepTypeDep.list(pmConn, filterWFlowStepTypeDeps).iterator();

			// Para cada tipo de dependencia, crear una dependencia
			while (stepTypeDepList.hasNext()) {
				bmoWFlowStepTypeDep = (BmoWFlowStepTypeDep)stepTypeDepList.next();
				PmWFlowStepDep pmWFlowStepDep = new PmWFlowStepDep(getSFParams());

				// Obten el ID del paso raiz
				int parentId = pmWFlowStepDep.getIdByStepType(pmConn, 
						bmoWFlow.getId(),
						bmoWFlow.getWFlowTypeId().toInteger(),
						bmoWFlowStepTypeDep.getBmoWFlowStepType().getWFlowPhaseId().toInteger(),
						bmoWFlowStepTypeDep.getBmoWFlowStepType().getName().toString()
						);

				BmoWFlowStepType bmoChildStepType = new BmoWFlowStepType();
				PmWFlowStepType pmChild = new PmWFlowStepType(getSFParams());
				bmoChildStepType = (BmoWFlowStepType)pmChild.get(pmConn, bmoWFlowStepTypeDep.getChildStepTypeId().toInteger());

				int childId = pmWFlowStepDep.getIdByStepType(pmConn, 
						bmoWFlow.getId(),
						bmoWFlow.getWFlowTypeId().toInteger(),
						bmoChildStepType.getWFlowPhaseId().toInteger(),
						bmoChildStepType.getName().toString()
						);

				BmoWFlowStepDep bmoWFlowStepDep = new BmoWFlowStepDep();
				bmoWFlowStepDep.getWFlowStepId().setValue(parentId);
				bmoWFlowStepDep.getChildStepId().setValue(childId);

				pmWFlowStepDep.save(pmConn, bmoWFlowStepDep, bmUpdateResult);
			}

			// Registrar bitacora
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW, "Creación de Tareas Finalizada.");

			// No hubo errores, guarda todo
			if (!bmUpdateResult.hasErrors()) 
				calculate(pmConn, bmoWFlow, bmUpdateResult);

		} else {
			throw new SFException("PmWFlow-createSteps() El WFlow ya tiene Tareas creadas.");
		}

	}

	// Crea los usuarios asociados al wflow
	private void createWFlowUsers(PmConn pmConn, BmoWFlow bmoWFlow, int defaultUserId, String programCode, BmUpdateResult bmUpdateResult) throws SFException {

		// Obten lista de useros segun tipo de flujo
		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();

		ArrayList<BmFilter> filterWFlowUserProfile = new ArrayList<BmFilter>();

		PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());
		PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());
		PmWFlowCategoryProfile pmWFlowCategoryProfile = new PmWFlowCategoryProfile(getSFParams());
		BmoWFlowCategoryProfile bmoWFlowCategoryProfile = new BmoWFlowCategoryProfile();
		
		// Obtener tipo de flujo
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, bmoWFlow.getWFlowTypeId().toInteger());
		
		// Filtro por categoria
		BmFilter filterWFlowType = new BmFilter();
		// (NO UTILIZAR bmoWFlow.getBmoWFlowType().getWFlowCategoryId() ya que no obtiene el id correcto)
		filterWFlowType.setValueFilter(bmoWFlowCategoryProfile.getKind(), bmoWFlowCategoryProfile.getWFlowCategoryId(), bmoWFlowType.getWFlowCategoryId().toInteger());
		filterWFlowUserProfile.add(filterWFlowType);
		
		// Filtro por creacion automatico
		BmFilter filterAutoProfile = new BmFilter();
		filterAutoProfile.setValueFilter(bmoWFlowCategoryProfile.getKind(), bmoWFlowCategoryProfile.getAutoProfile(), "" + 1);
		filterWFlowUserProfile.add(filterAutoProfile);
		Iterator<BmObject> userTypeList = pmWFlowCategoryProfile.list(pmConn, filterWFlowUserProfile).iterator();
		
		// Por cada user type, genera un usuario asociado a este flujo
		while (userTypeList.hasNext()) {
			bmoWFlowCategoryProfile = (BmoWFlowCategoryProfile)userTypeList.next();
			bmoWFlowUser = new BmoWFlowUser();
			bmoWFlowUser.getAutoDate().setValue(bmoWFlowCategoryProfile.getAutoDate().toString());
			bmoWFlowUser.getRequired().setValue(bmoWFlowCategoryProfile.getRequired().toString());
			bmoWFlowUser.getLockStart().setValue(bmoWFlow.getStartDate().toString());
			bmoWFlowUser.getLockEnd().setValue(bmoWFlow.getEndDate().toString());
			bmoWFlowUser.getProfileId().setValue(bmoWFlowCategoryProfile.getProfileId().toInteger());
			bmoWFlowUser.getAssignSteps().setValue(true);
			bmoWFlowUser.getWFlowId().setValue(bmoWFlow.getId());

			// Revisa si el usuario inicial pertenece al grupo actual
			if (pmProfileUser.userInProfile(pmConn, bmoWFlowCategoryProfile.getProfileId().toInteger(), defaultUserId))
				bmoWFlowUser.getUserId().setValue(defaultUserId);
			else {
				// No pertenece; si solo hay 1 usuario, asignalo
				int onlyUserId = pmProfileUser.onlyUserInProfile(pmConn, bmoWFlowCategoryProfile.getProfileId().toInteger());
				if (onlyUserId > 0)
					bmoWFlowUser.getUserId().setValue(onlyUserId);
			}

			pmWFlowUser.saveSimple(pmConn, bmoWFlowUser, bmUpdateResult);

			// Si se asigno usuario, obtiene valores del usuario
			if (bmoWFlowUser.getUserId().toInteger() > 0) {
				BmoUser bmoUser = new BmoUser();
				PmUser pmUser = new PmUser(getSFParams());
				bmoUser = (BmoUser)pmUser.get(pmConn, bmoWFlowUser.getUserId().toInteger());
				bmoWFlowUser.getBillable().setValue(bmoUser.getBillable().toBoolean());
				bmoWFlowUser.getRate().setValue(bmoUser.getRate().toDouble());
				pmWFlowUser.saveSimple(pmConn, bmoWFlowUser, bmUpdateResult);
			} else {
				// Si no hay usuario en el grupo, colocar usuario predeterminado de Usuarios de Grupo del Grupo
				if (!(bmoWFlowUser.getUserId().toInteger() > 0)) {

					BmoProfileUser bmoProfileUser = new BmoProfileUser();
					ArrayList<BmFilter> filterListUserGroup = new ArrayList<BmFilter>();

					// Filtrar por grupo
					BmFilter filterUserGroupByGroup  = new BmFilter();
					filterUserGroupByGroup.setValueFilter(bmoProfileUser.getKind(), bmoProfileUser.getProfileId(), bmoWFlowCategoryProfile.getProfileId().toInteger());
					filterListUserGroup.add(filterUserGroupByGroup);

					//Filtrar por usuario predeterminado
					BmFilter filterUserGroupDefault = new BmFilter();
					filterUserGroupDefault.setValueFilter(bmoProfileUser.getKind(), bmoProfileUser.getDefaultUser(), 1);
					filterListUserGroup.add(filterUserGroupDefault);

					Iterator<BmObject> userGroupList = pmProfileUser.list(pmConn, filterListUserGroup).iterator();

					if (userGroupList.hasNext())
						bmoWFlowUser.getUserId().setValue(((BmoProfileUser)userGroupList.next()).getUserId().toInteger());

					// Si se asigno usuario, obtiene valores del usuario
					if (bmoWFlowUser.getUserId().toInteger() > 0) {
						BmoUser bmoUser = new BmoUser();
						PmUser pmUser = new PmUser(getSFParams());
						bmoUser = (BmoUser)pmUser.get(pmConn, bmoWFlowUser.getUserId().toInteger());
						bmoWFlowUser.getBillable().setValue(bmoUser.getBillable().toBoolean());
						bmoWFlowUser.getRate().setValue(bmoUser.getRate().toDouble());
						pmWFlowUser.saveSimple(pmConn, bmoWFlowUser, bmUpdateResult);
					}
				}
			}

			// Asignar usuarios a tareas
			pmWFlowStep.updateWFlowStepUsers(pmConn, bmoWFlow, bmoWFlowCategoryProfile.getProfileId().toInteger(), bmoWFlowUser.getUserId().toInteger(), bmUpdateResult);
		}
		if (!(programCode.equals(new BmoWorkContract().getProgramCode()))) {
			// Ya fueron creados todos los wflow users, ahora a asignarles usuarios
			BmFilter filterByWFlow = new BmFilter();
			filterByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWFlow.getId());
			Iterator<BmObject> wFlowUserList = pmWFlowUser.list(pmConn, filterByWFlow).iterator();
			while (wFlowUserList.hasNext()) {
				bmoWFlowUser = (BmoWFlowUser)wFlowUserList.next();

				// Determina usuarios superiores y si tiene relevancia en los colaboradores del flujo
				if (bmoWFlowUser.getUserId().toInteger() > 0) {
					// Si se asigno un usuario, buscar superiores
					PmUser pmUser = new PmUser(getSFParams());
					BmoUser bmoUser = (BmoUser)pmUser.get(pmConn, bmoWFlowUser.getUserId().toInteger());

					if (bmoUser.getParentId().toInteger() > 0) {
						BmoUser parentUser = (BmoUser)pmUser.get(pmConn, bmoWFlowUser.getBmoUser().getParentId().toInteger());
						pmWFlowUser.updateUnassignedWithUser(pmConn, bmoWFlow.getId(), parentUser.getId(), bmUpdateResult);

						// Determina usuario superior del padre
						if (parentUser.getParentId().toInteger() > 0) {
							BmoUser parentParentUser = (BmoUser)pmUser.get(pmConn, parentUser.getParentId().toInteger());
							pmWFlowUser.updateUnassignedWithUser(pmConn, bmoWFlow.getId(), parentParentUser.getId(), bmUpdateResult);

							// Determina usuario superior del padre del padre
							if (parentParentUser.getParentId().toInteger() > 0) {
								BmoUser parentParentParentUser = (BmoUser)pmUser.get(pmConn, parentParentUser.getParentId().toInteger());
								pmWFlowUser.updateUnassignedWithUser(pmConn, bmoWFlow.getId(), parentParentParentUser.getId(), bmUpdateResult);

								// Determina usuario superior del padre del padre
								if (parentParentParentUser.getParentId().toInteger() > 0) {
									BmoUser parentParentParentParentUser = (BmoUser)pmUser.get(pmConn, parentParentParentUser.getParentId().toInteger());
									pmWFlowUser.updateUnassignedWithUser(pmConn, bmoWFlow.getId(), parentParentParentParentUser.getId(), bmUpdateResult);
								}
							}
						}
					}
				}	
			}

			// Registrar bitacora
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW, "Creación de Colaboradores Finalizada.");
		}
	}

	// Actualiza el calendario de todos los usuarios de un flujo
	public void updateWFlowUsersGoogleCalendar(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {
		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();

		BmFilter filterByWFlow = new BmFilter();
		filterByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWFlow.getId());
		Iterator<BmObject> wFlowUserList = pmWFlowUser.list(pmConn, filterByWFlow).iterator();
		while (wFlowUserList.hasNext()) {
			bmoWFlowUser = (BmoWFlowUser)wFlowUserList.next();		

			// Si esta asignado el userid, actualizar calendario
			if (bmoWFlowUser.getUserId().toInteger() > 0)
				pmWFlowUser.updateGoogleCalendar(pmConn, bmoWFlow, bmoWFlowUser, bmUpdateResult);
		}
	}

	// Crea los documentos asociados al wflow
	private void createDocuments(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {

		// Obten lista de documentos segun tipo de flujo
		PmWFlowDocumentType pmWFlowDocumentType = new PmWFlowDocumentType(getSFParams());
		BmoWFlowDocumentType bmoWFlowDocumentType = new BmoWFlowDocumentType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowDocumentType.getKind(), bmoWFlowDocumentType.getWFlowTypeId(), bmoWFlow.getWFlowTypeId().toInteger());
		Iterator<BmObject> documentTypeList = pmWFlowDocumentType.list(filterWFlowType).iterator();

		// Por cada document type, genera un documento asociado a este flujo
		while (documentTypeList.hasNext()) {
			bmoWFlowDocumentType = (BmoWFlowDocumentType)documentTypeList.next();

			// Si no existe ya el documento, lo crea
			if (!wFlowDocumentExists(pmConn, bmoWFlow.getId(), bmoWFlowDocumentType.getCode().toString())) {
				BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
				bmoWFlowDocument.getCode().setValue(bmoWFlowDocumentType.getCode().toString());
				bmoWFlowDocument.getName().setValue(bmoWFlowDocumentType.getName().toString());
				bmoWFlowDocument.getRequired().setValue(bmoWFlowDocumentType.getRequired().toString());
				bmoWFlowDocument.getFileTypeId().setValue(bmoWFlowDocumentType.getFileTypeId().toString());
				bmoWFlowDocument.getWFlowId().setValue(bmoWFlow.getId());
				PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(getSFParams());
				pmWFlowDocument.saveSimple(pmConn, bmoWFlowDocument, bmUpdateResult);
			}
		}

		// Registrar bitacora
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW, "Creación de Documentos Finalizada.");
	}

	// Revisa si ya existe documento por clave
	// Revisar que no exista ya un pedido renovado de este, del tipo esperado
	private boolean wFlowDocumentExists(PmConn pmConn, int wFlowId, String code) throws SFPmException {
		String sql = "SELECT wfdo_wflowdocumentid FROM wflowdocuments "
				+ "	WHERE wfdo_wflowid = " + wFlowId + ""
				+ " AND wfdo_code LIKE '" + code + "'";

		pmConn.doFetch(sql);
		return pmConn.next();
	}

	// Cambiar la fecha y grabar cambios
	public void changeDate(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {
		// Cambiar las fechas de los usuarios ligados
		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		BmFilter filterByWFlow = new BmFilter();
		filterByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWFlow.getId());
		Iterator<BmObject> wFlowUserList = pmWFlowUser.list(pmConn, filterByWFlow).iterator();

		while (wFlowUserList.hasNext()) {
			BmoWFlowUser nextBmoWFlowUser = (BmoWFlowUser)wFlowUserList.next();

			// Si la asignacion es con fechas automáticas
			if (nextBmoWFlowUser.getAutoDate().toBoolean()) {

				nextBmoWFlowUser.getLockStart().setValue(bmoWFlow.getStartDate().toString());
				nextBmoWFlowUser.getLockEnd().setValue(bmoWFlow.getEndDate().toString());

				pmWFlowUser.save(pmConn, nextBmoWFlowUser, bmUpdateResult);
			}
		}
	}

	public void calculate(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {

		// Obtener tareas creadas
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());

		// Filtrar por flujo, eliminando disclose del usuario
		pmWFlowStep.disableDiscloseSetting();
		BmFilter filterWFlow = new BmFilter();
		filterWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoWFlow.getId());

		ArrayList<BmObject> wFlowStepList = pmWFlowStep.list(pmConn, filterWFlow);
		Iterator<BmObject> wFlowStepListIterator = wFlowStepList.iterator();

		int wFlowPhaseId = 0, lastWFlowPhaseId = 0, funnelId = 0, lastFunnelId = 0, countSteps = wFlowStepList.size(), finished = 0;

		while (wFlowStepListIterator.hasNext()) {
			bmoWFlowStep = (BmoWFlowStep)wFlowStepListIterator.next();
			boolean wasEnabled = bmoWFlowStep.getEnabled().toBoolean();

			if (bmoWFlowStep.getProgress().toInteger() == 100) finished++;

			// Si el paso se puede habilitar, asignarlo como habilitado
			if (pmWFlowStep.shallEnable(pmConn, bmoWFlow.getId(), bmoWFlowStep.getId())) {

				bmoWFlowStep.getEnabled().setValue(true);

				// Si no esta asignada la fase, asignarla
				if (bmoWFlowStep.getProgress().toInteger() < 100 && wFlowPhaseId == 0)
					wFlowPhaseId = bmoWFlowStep.getWFlowPhaseId().toInteger();

				// Asignar la ultima fase encontrada 
				lastWFlowPhaseId = bmoWFlowStep.getWFlowPhaseId().toInteger();

				// Si no esta asignado funnel, asignarlo
				if (bmoWFlowStep.getProgress().toInteger() < 100 && funnelId == 0)
					funnelId = bmoWFlowStep.getWFlowFunnelId().toInteger();

				// Asignar el ultimo funnel encontrado 
				lastFunnelId = bmoWFlowStep.getWFlowFunnelId().toInteger();

				// Acciones de avisar al responsable, no estaba activo y ahora si
				if (!wasEnabled) {
					pmWFlowStep.sendMailActivation(pmConn, bmoWFlowStep);

					// Si la fecha de hoy es anterior a la fecha del flujo, pone la fecha del flujo
					if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), 
							getSFParams().getTimeZone(), 
							SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()), 
							bmoWFlow.getStartDate().toString()))
						bmoWFlowStep.getStartdate().setValue(bmoWFlow.getStartDate().toString());
					else
						bmoWFlowStep.getStartdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

					// Establecer la fecha de recordatorio
					bmoWFlowStep.getRemindDate().setValue(SFServerUtil.addDays(getSFParams().getDateFormat(), 
							bmoWFlowStep.getStartdate().toString(), bmoWFlowStep.getDaysRemind().toInteger()));
				}
			} else {
				// Deshabilitar si el paso no debe ser habilitado
				bmoWFlowStep.getEnabled().setValue(false);
				bmoWFlowStep.getRemindDate().setValue("");
				bmoWFlowStep.getStatus().setValue("" + BmoWFlowStep.STATUS_TOEXPIRED);
			}

			pmWFlowStep.saveCheckDates(pmConn, bmoWFlow, bmoWFlowStep, bmUpdateResult);
		}

		// Actualiza fase vigente
		if (wFlowPhaseId == 0) 
			wFlowPhaseId = lastWFlowPhaseId;
		if (wFlowPhaseId > 0)
			bmoWFlow.getWFlowPhaseId().setValue(wFlowPhaseId);

		// Actualiza funnel vigente
		if (funnelId == 0) 
			funnelId = lastFunnelId;
		if (funnelId > 0)
			bmoWFlow.getWFlowFunnelId().setValue(funnelId);

		// Actualiza avances del wflow
		int progress = 0;
		if (countSteps > 0) progress = ((finished * 100 / countSteps));
		else progress = 100;
		bmoWFlow.getProgress().setValue(progress);

		super.save(pmConn, bmoWFlow, bmUpdateResult);
	}	

	// Actualiza estatus de documentos completos del flujo
	public void checkRequiredDocuments(PmConn pmConn, BmoWFlow bmoWFlow, BmUpdateResult bmUpdateResult) throws SFException {
		boolean completeDocuments = true;

		// Obtener documentos creados
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(getSFParams());
		BmFilter filterWFlow = new BmFilter();
		filterWFlow.setValueFilter(bmoWFlowDocument.getKind(), bmoWFlowDocument.getWFlowId(), bmoWFlow.getId());
		Iterator<BmObject> wFlowDocumentListIterator = pmWFlowDocument.list(pmConn, filterWFlow).iterator();

		while (wFlowDocumentListIterator.hasNext()) {
			BmoWFlowDocument nextWFlowDocument = (BmoWFlowDocument)wFlowDocumentListIterator.next();
			if (nextWFlowDocument.getRequired().toBoolean() && 
					!nextWFlowDocument.getIsUp().toBoolean())
				completeDocuments = false;
		}

		bmoWFlow.getHasDocuments().setValue(completeDocuments);

		super.saveSimple(pmConn, bmoWFlow, bmUpdateResult);

	}	

	private void deleteGoogleCalendar(PmConn pmConn, BmoWFlow bmoWFlow) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		if (bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarSync().toBoolean()
				&& getSFParams().isGoogleAuth()) {
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
			gCalendar.deleteEvent(bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarId().toString(), 
					bmoWFlow.getGoogleEventId().toString());
		}
		*/
	}
	
	// Regresar el objeto del flujo
//	public BmObject returnBmObjectFromWFlow(int wFlowId) throws SFException  {
//		PmConn pmConn = new PmConn(getSFParams());
//		try {
//			pmConn.open();
//
//			return this.returnBmObjectFromWFlow(pmConn, wFlowId);
//
//		} catch (SFPmException e) {
//			throw new SFException("" + e.toString());
//		} finally {
//			pmConn.close();
//		}
//	}
//
//	// Regresar el objeto del flujo
//	public BmObject returnBmObjectFromWFlow(PmConn pmConn, int wFlowId) throws SFException  {
//		BmObject bmObject = new BmObject();
//
//		PmWFlow pmWFlow = new PmWFlow(getSFParams());
//		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, wFlowId);
//
//		//		if (bmoWFlow.getCallerCode().toString().equals("" + new BmoSFComponent().getProgramCode()))
//		//			bmObject = (BmoSFComponent)new PmOpportunity(getSFParams()).getBy(bmoWFlow.getId(), new BmoSFComponent().getWFlowId().getName());
//
//		// Flujos con clientes
////		if (bmoWFlow.getCallerCode().toString().equals("" + new BmoCustomer().getProgramCode()))
////			bmObject = (BmoCustomer)new PmCustomer(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoCustomer().getWFlowId().getName());
//
//		if (bmoWFlow.getCallerCode().toString().equals("" + new BmoOpportunity().getProgramCode()))
//			bmObject = (BmoOpportunity)new PmOpportunity(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoOpportunity().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoOrder().getProgramCode()))
//			bmObject = (BmoOrder)new PmOrder(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoOrder().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoPropertySale().getProgramCode()))
//			bmObject = (BmoPropertySale)new PmPropertySale(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoPropertySale().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoProject().getProgramCode()))
//			bmObject = (BmoProject)new PmProject(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoProject().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoCredit().getProgramCode()))
//			bmObject = (BmoCredit)new PmCredit(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoCredit().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoSessionSale().getProgramCode()))
//			bmObject = (BmoSessionSale)new PmSessionSale(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoSessionSale().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoPropertyRental().getProgramCode()))
//			bmObject = (BmoPropertyRental)new PmPropertyRental(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoPropertyRental().getWFlowId().getName());
//
//		// Flujos sin clientes
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoDevelopmentPhase().getProgramCode()))
//			bmObject = (BmoDevelopmentPhase)new PmDevelopmentPhase(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoDevelopmentPhase().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoMeeting().getProgramCode()))
//			bmObject = (BmoMeeting)new PmMeeting(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoMeeting().getWFlowId().getName());
//
//		else if (bmoWFlow.getCallerCode().toString().equals("" + new BmoActivity().getProgramCode()))
//			bmObject = (BmoActivity)new PmActivity(getSFParams()).getBy(pmConn, bmoWFlow.getId(), new BmoActivity().getWFlowId().getName());
//
//		//printDevLog("ok: " + bmObject.getIdFieldName() + ", aa: "+bmObject.getPmClass());
//
//		return bmObject;
//	}
//
//	// Existe cliente en modulo, regresarlo
//	public int getCustomerId(BmObject bmObject) {
//		int customerId = 0;
//		if (bmObject instanceof BmoCustomer) 
//			customerId = ((BmoCustomer)bmObject).getId();
//		else if (bmObject instanceof BmoOpportunity) 
//			customerId = ((BmoOpportunity)bmObject).getCustomerId().toInteger();
//		else if (bmObject instanceof BmoOrder) 
//			customerId = ((BmoOrder)bmObject).getCustomerId().toInteger();
//		else if (bmObject instanceof BmoPropertySale) 
//			customerId = ((BmoPropertySale)bmObject).getCustomerId().toInteger();
//		else if (bmObject instanceof BmoProject) 
//			customerId = ((BmoProject)bmObject).getCustomerId().toInteger();
//		else if (bmObject instanceof BmoCredit) 
//			customerId = ((BmoCredit)bmObject).getCustomerId().toInteger();
//		else if (bmObject instanceof BmoSessionSale) 
//			customerId = ((BmoSessionSale)bmObject).getCustomerId().toInteger();
//		else if (bmObject instanceof BmoPropertyRental) 
//			customerId = ((BmoPropertyRental)bmObject).getCustomerId().toInteger();	
//
//		return customerId;
//	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlow = (BmoWFlow)bmObject;

		// Determina cual tipo y categoría de flujo es
		PmWFlowType pmWFlowType = new PmWFlowType(getSFParams());
		BmoWFlowType bmoWFlowType = (BmoWFlowType)pmWFlowType.get(pmConn, bmoWFlow.getWFlowTypeId().toInteger());
		bmoWFlow.setBmoWFlowType(bmoWFlowType);

		// Borra dependencias de los pasos
		pmConn.doUpdate("DELETE FROM wflowstepdeps "
				+ " WHERE wfsz_wflowstepid IN "
				+ "		(SELECT wfsp_wflowstepid FROM wflowsteps WHERE wfsp_wflowid = " + bmoWFlow.getId() + ")");

		// Borra documentos
		pmConn.doUpdate("DELETE FROM wflowdocuments WHERE wfdo_wflowid = " + bmoWFlow.getId());

		// Borra pasos
		pmConn.doUpdate("DELETE FROM wflowsteps WHERE wfsp_wflowid = " + bmoWFlow.getId());

		// Borra bitacora
		pmConn.doUpdate("DELETE FROM wflowlogs WHERE wflg_wflowid = " + bmoWFlow.getId());

		// Borra usuarios
		pmConn.doUpdate("DELETE FROM wflowusers WHERE wflu_wflowid = " + bmoWFlow.getId());

		// Borra flujo
		super.delete(pmConn, bmoWFlow, bmUpdateResult);

		// Elimina calendario
		if (!bmUpdateResult.hasErrors()) {
			deleteGoogleCalendar(pmConn, bmoWFlow);
		}

		return bmUpdateResult;
	}
}
