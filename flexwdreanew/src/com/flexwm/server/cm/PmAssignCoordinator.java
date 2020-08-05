package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.flexwm.shared.cm.BmoAssignCoordinator;
import com.flexwm.shared.cm.BmoAssignSalesman;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class PmAssignCoordinator extends PmObject {
	BmoAssignCoordinator bmoAssignCoordinator;

	public PmAssignCoordinator(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoAssignCoordinator = new BmoAssignCoordinator();
		setBmObject(bmoAssignCoordinator);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoAssignCoordinator.getUserId(), bmoAssignCoordinator.getBmoUser()),
				new PmJoin(bmoAssignCoordinator.getBmoUser().getAreaId(), bmoAssignCoordinator.getBmoUser().getBmoArea()),
				new PmJoin(bmoAssignCoordinator.getBmoUser().getLocationId(), bmoAssignCoordinator.getBmoUser().getBmoLocation())
				))); 
	}
	
	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de asignaciones de la empresa del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( assc_companyid IN (" 
					+ " SELECT uscp_companyid FROM usercompanies " 
					+ " WHERE uscp_userid = " + loggedUserId + " )" + ") ";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " assc_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoAssignCoordinator bmoAssignCoordinator = (BmoAssignCoordinator) autoPopulate(pmConn, new BmoAssignCoordinator());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoAssignCoordinator.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoAssignCoordinator.setBmoUser(bmoUser);

		return bmoAssignCoordinator;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoAssignCoordinator = (BmoAssignCoordinator)bmObject;

		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();

		// Validar si ya existe
		if (!(bmoAssignCoordinator.getId() > 0)) {
			if (existUserCoordinatorAssigned(pmConn, bmoAssignCoordinator.getCompanyId().toInteger(), bmoAssignCoordinator.getUserId().toInteger()))
				bmUpdateResult.addError(bmoAssignCoordinator.getUserId().getName(), "El " + bmoAssignCoordinator.getUserId().getLabel() + " ya existe");

			// Colocar que esta activo
			bmoUser = (BmoUser)pmUser.get(pmConn, bmoAssignCoordinator.getUserId().toInteger());
			if (bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE))
				bmoAssignCoordinator.getEnable().setValue(1);
			else 
				bmoAssignCoordinator.getEnable().setValue(0);
			
			if (!bmUpdateResult.hasErrors())
				super.save(pmConn, bmoAssignCoordinator, bmUpdateResult);

			// Asignar los vendedores del coordinador
			printDevLog("Crear Vendedores del coordinador | en espera?: "+bmoAssignCoordinator.getQueuedCoordinator().toInteger());
			if (!bmUpdateResult.hasErrors())
				assignSalesmen(pmConn, bmoAssignCoordinator, bmUpdateResult);
		} else {
			if (!bmUpdateResult.hasErrors())
				super.save(pmConn, bmoAssignCoordinator, bmUpdateResult);
		}
		
		// Actualizar asignacion completa de vendedores
		updateComplete(pmConn, bmoAssignCoordinator.getId(), bmUpdateResult);
		// Obtener ultimos cambios
		bmoAssignCoordinator = (BmoAssignCoordinator)bmUpdateResult.getBmObject();

		// Quitar coordinador en espera, ya que solo es para asignarlo a los vendedores
		bmoAssignCoordinator.getQueuedCoordinator().setValue(0);
		
		if (!bmUpdateResult.hasErrors())
			super.save(pmConn, bmoAssignCoordinator, bmUpdateResult);

		return bmUpdateResult;
	}

	// Crear registros de los vendedores del coordinador
	public BmUpdateResult assignSalesmen(PmConn pmConn, BmoAssignCoordinator bmoAssignCoordinator, BmUpdateResult bmUpdateResult) throws SFException {

		BmoAssignSalesman bmoAssignSalesman = new BmoAssignSalesman();
		PmAssignSalesman pmAssignSalesman = new PmAssignSalesman(getSFParams());
		BmoUser bmoUser = new BmoUser();

		// Filtros de jefe inmediato y usuarios activos
		ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
		BmFilter filterParent = new BmFilter();
		BmFilter filterActive = new BmFilter();

		filterParent.setValueFilter(bmoUser.getKind(), bmoUser.getParentId().getName(), bmoAssignCoordinator.getUserId().toInteger());
		filterListD.add(filterParent);
		filterActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus().getName(), "" + BmoUser.STATUS_ACTIVE);
		filterListD.add(filterActive);

		// Listar los usuarios y ordernar por ID
		ArrayList<BmOrder> userOrder = new ArrayList<BmOrder>();
		userOrder.add(new BmOrder(bmoUser.getKind(), bmoUser.getCode(), BmOrder.ASC));
		Iterator<BmObject> listUser = new PmUser(getSFParams()).list(pmConn, filterListD, new ArrayList<BmSearchField>(), "", userOrder, -1, -1).iterator();

		while (listUser.hasNext()) {
			bmoUser = (BmoUser)listUser.next();

			// Crear los vendedores en la tabla
			printDevLog("usuario: "+ bmoUser.getCode().toString()+  " | ID:"+bmoUser.getId() );
			bmoAssignSalesman = new BmoAssignSalesman();
			bmoAssignSalesman.getAssingCoordinatorId().setValue(bmoAssignCoordinator.getId());
			bmoAssignSalesman.getUserId().setValue(bmoUser.getId());
			bmoAssignSalesman.getAssigned().setValue("0");
			bmoAssignSalesman.getCountAssigned().setValue("0");
			// Si el coordinador tiene en espera, heredar a sus vendedores
			if (bmoAssignCoordinator.getQueuedCoordinator().toInteger() > 0)
				bmoAssignSalesman.getQueuedUser().setValue(1);
			
			bmoAssignSalesman.getEnable().setValue(1);
			pmAssignSalesman.saveSimple(pmConn, bmoAssignSalesman, bmUpdateResult);
		}

		return bmUpdateResult;
	}
	
	// Actualiza si esta todos los vendedores asignados en el coordinador
	public BmUpdateResult updateComplete(PmConn pmConn, int assingCoordinatorId, BmUpdateResult bmUpdateResult) throws SFException {
		PmAssignSalesman pmAssignSalesman = new PmAssignSalesman(getSFParams());
		BmoAssignCoordinator bmoAssignCoordinator = new BmoAssignCoordinator();
		PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());
		
		if (!(assingCoordinatorId > 0))
			bmUpdateResult.addError(bmoAssignCoordinator.getIdFieldName(), "El campo NO debe estar vacío.");
		else {
			bmoAssignCoordinator = (BmoAssignCoordinator)pmAssignCoordinator.get(pmConn, assingCoordinatorId);

			if (pmAssignSalesman.completeAssignedSalesman(pmConn, assingCoordinatorId)) {
				printDevLog("Actualiza asignacion completa en el coordinador: "+bmoAssignCoordinator.getBmoUser().getCode());
				bmoAssignCoordinator.getFullAssignment().setValue(1);
				super.save(pmConn, bmoAssignCoordinator, bmUpdateResult);
			} else {
				printDevLog("Actualiza asignacion NO completa en el coordinador: "+bmoAssignCoordinator.getBmoUser().getCode());
				bmoAssignCoordinator.getFullAssignment().setValue(0);
				super.save(pmConn, bmoAssignCoordinator, bmUpdateResult);
			}
		}
		
		return bmUpdateResult;
	}

	// Existe el usuario
	public boolean existUserCoordinatorAssigned(PmConn pmConn, int companyId, int userId) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoAssignCoordinator.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoAssignCoordinator.getKind() 
				+ " WHERE " + bmoAssignCoordinator.getCompanyId().getName() + " = " + companyId
				+ " AND " + bmoAssignCoordinator.getUserId().getName() + " = " + userId;
		pmConn.doFetch(sql);

		int countExist = 0;
		if (pmConn.next()) countExist = pmConn.getInt("countExist");

		printDevLog("countExist:"+countExist);

		if (countExist > 0) return true;
		else return false;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		printDevLog("Ejecutar la Accion");

		int companyId = Integer.parseInt(value);
		
		// Obtiene el ID del modulo de asignacion de leads
		if (action.equals(BmoAssignCoordinator.ACTION_ASSIGNLEAD)) {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			// Inactivar coordinadores y vendedores
			setDisableCoordinador(pmConn, bmUpdateResult);
			setDisableSalesman(pmConn, bmUpdateResult);
			// Actualizar datos antes de buscar disponibilidad
			assignmentUpdate(pmConn, companyId, bmUpdateResult);
			// (Algoritmo lead)Inicar busqueda para retornar el vendedor disponible 
			searchCoordinador(pmConn, companyId, bmUpdateResult);
			pmConn.close();
		}
		return bmUpdateResult;
	}
	
	// INactivar al coordinador y vendedores
	public void setDisableCoordinador(PmConn pmConn, BmUpdateResult bmUpdateResult) throws SFException {
		printDevLog("	Inactivar el coordinador y a sus vendedores");
		String sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
								+ " LEFT JOIN users ON (user_userid = assc_userid) "
								+ " WHERE user_status = '" + BmoUser.STATUS_INACTIVE + "' ";
		pmConn.doFetch(sqlCoordintor);
		while (pmConn.next()) {
			int assingCoordinatorId = pmConn.getInt("assc_assingcoordinatorid");
			// Inactivar coordinador
			sqlCoordintor = "UPDATE assigncoordinators "
					+ " SET assc_enable = 0 "
					+ " WHERE assc_assingcoordinatorid = " + assingCoordinatorId;
			printDevLog("SQL_inactivar al coordinador:"+sqlCoordintor);
			pmConn.doUpdate(sqlCoordintor);
			
			// Inactivar vendedores del coordinador
			sqlCoordintor = "UPDATE assignsalesmen "
					+ " SET assa_enable = 0 "
					+ " WHERE assa_assingcoordinatorid = " + assingCoordinatorId;
			printDevLog("SQL_inactivar los vendedores del coordinador:"+sqlCoordintor);
			pmConn.doUpdate(sqlCoordintor);
			
			updateComplete(pmConn, assingCoordinatorId, bmUpdateResult);
		}
	}
	
	// Inactivar al vendedor
	public void setDisableSalesman(PmConn pmConn, BmUpdateResult bmUpdateResult) throws SFException {
		printDevLog("	Inactivar el vendedor");
		
//		PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());
		PmConn pmConnSalesman = new PmConn(getSFParams()); // No recorre el ciclo con la misma conexion
		pmConnSalesman.open();
		
		String sqlSalesman = "SELECT assa_assignsalesmanid, assa_assingcoordinatorid FROM assignsalesmen "
				+ " LEFT JOIN users ON (user_userid = assa_userid) "
				+ " WHERE user_status = '" + BmoUser.STATUS_INACTIVE + "' ";
		
		pmConnSalesman.doFetch(sqlSalesman);
		while (pmConnSalesman.next()) {
			 BmoAssignSalesman bmoAssignSalesman = new BmoAssignSalesman();
			 PmAssignSalesman pmAssignSalesman = new PmAssignSalesman(getSFParams());
			
				// Desactivar en vendedor

			 bmoAssignSalesman = (BmoAssignSalesman)pmAssignSalesman.get(pmConn, pmConnSalesman.getInt("assa_assignsalesmanid"));
			 printDevLog("USUARIO A INACTIVAR: "+bmoAssignSalesman.getBmoUser().getCode());
			 bmoAssignSalesman.getEnable().setValue(0);
			 pmAssignSalesman.save(pmConn, bmoAssignSalesman, bmUpdateResult);
			 
//			sqlSalesman = "UPDATE assignsalesmen "
//					+ " SET assa_enable = 0 "
//					+ " WHERE assa_assignsalesmanid = " + pmConn.getInt("assa_assignsalesmanid");
//			printDevLog("SQL_inactivar el vendedor:"+sqlSalesman);
//			
//			pmConn.doUpdate(sqlSalesman);
			
			
			// Actualizar si todos los vendedores estan asignados, si es nuevo firmar para posteriores asignaciones
//			pmAssignCoordinator.updateComplete(pmConn,  pmConn.getInt("assa_assingcoordinatorid"), bmUpdateResult);
		}
		pmConnSalesman.close();

	}
	
	public void searchCoordinador(PmConn pmConn, int companyId, BmUpdateResult bmUpdateResult) throws SFException {
		String sqlCoordintor = "";
		BmoAssignCoordinator bmoAssignCoordinator = new BmoAssignCoordinator();
		PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());
		
//		PmConn pmConnCoordinator = new PmConn(getSFParams());
//		pmConnCoordinator.open();
		
		// Buscar PRIMERO coordinadores disponibles y NO este completo
		printDevLog("\n Busqueda: Coordinadores disponibles");
		sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
				+ " LEFT JOIN users ON (user_userid = assc_userid) "
				+ " WHERE assc_assigned = 0 AND assc_fullassignment = 0 "
				+ " AND assc_companyid = " + companyId
				+ " AND assc_enable = 1 "
				+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
		printDevLog("SQL_coord-notIn-queued:"+sqlCoordintor);
		pmConn.doFetch(sqlCoordintor);
		if (pmConn.next()) {
			printDevLog("	Existen Coordinadores disponibles SIN ESPERA");
			// Obtener coordinador
			if (pmConn.getInt("assc_assingcoordinatorid") > 0) {
				bmoAssignCoordinator = (BmoAssignCoordinator)pmAssignCoordinator.get(pmConn, pmConn.getInt("assc_assingcoordinatorid"));
				printDevLog("Coordinador:"+bmoAssignCoordinator.getBmoUser().getCode());
				assignSalesman(pmConn, bmoAssignCoordinator, companyId, bmUpdateResult);
			}
		} else {
			// Buscar PRIMERO coordinadores disponibles y que ademas un usuario ESTA en espera
			printDevLog("\n  Busqueda: Coordinadores disponibles con USUARIO EN ESPERA");
			sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
					+ " LEFT JOIN users ON (user_userid = assc_userid) "
					+ " WHERE assc_assigned = 0 AND assc_fullassignment = 0 "
					+ " AND assc_assingcoordinatorid IN (SELECT assa_assingcoordinatorid "
					+ "		FROM assignsalesmen LEFT JOIN users ON (user_userid = assa_userid) "
					+ "		WHERE assa_queueduser = 1 ORDER BY user_code ASC) "
					+ " AND assc_companyid = " + companyId
					+ " AND assc_enable = 1 "
					+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
			printDevLog("SQL_coord-In-queued:"+sqlCoordintor);
			pmConn.doFetch(sqlCoordintor);
			if (pmConn.next()) {
				printDevLog("	Existen Coordinadores disponibles CON ESPERA	");
				// Obtener coordinador
				if (pmConn.getInt("assc_assingcoordinatorid") > 0) {
					bmoAssignCoordinator = (BmoAssignCoordinator)pmAssignCoordinator.get(pmConn, pmConn.getInt("assc_assingcoordinatorid"));
					printDevLog("Coordinador:"+bmoAssignCoordinator.getBmoUser().getCode());
					assignSalesman(pmConn, bmoAssignCoordinator, companyId, bmUpdateResult);
				}
			} else {
				printDevLog("Si llega aqui, puede faltar un escenario");
			}
		}
		
//		pmConnCoordinator.close();
	}
	
	public void assignSalesman(PmConn pmConn, BmoAssignCoordinator bmoAssignCoordinator, int companyId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoAssignSalesman bmoAssignSalesman = new BmoAssignSalesman();
		PmAssignSalesman pmAssignSalesman = new PmAssignSalesman(getSFParams());
		String sqlSalesman = "";

//		PmConn pmConnSalesman = new PmConn(getSFParams());
//		pmConnSalesman.open();

		// Buscar vendedores disponibles SIN espera
		sqlSalesman = "SELECT * FROM assignsalesmen "
				+ " LEFT JOIN users ON (user_userid = assa_userid) "
				+ " WHERE assa_assigned = 0 AND assa_queueduser = 0 AND assa_enable = 1 "
				+ " AND assa_assingcoordinatorid = " + bmoAssignCoordinator.getId()
				+ " ORDER BY user_code ASC, assa_assignsalesmanid ASC; ";
		printDevLog("SQL-VEND:"+sqlSalesman);

		pmConn.doFetch(sqlSalesman);
		if (pmConn.next()) {
			printDevLog("Existen vendedores disponibles SIN espera");
			if (pmConn.getInt("assa_assignsalesmanid") > 0) 
				bmoAssignSalesman = (BmoAssignSalesman)pmAssignSalesman.get(pmConn, pmConn.getInt("assa_assignsalesmanid"));
			printDevLog("IDRegistro:"+bmoAssignSalesman.getId() + "|Vendedor:"+bmoAssignSalesman.getBmoUser().getCode());
		} else {
			printDevLog("Existen vendedores disponibles CON espera");
			
			// VOLVER A BUSCAR SI EXISTE TODAVIA UN VENDEDOR DISPONIBLE EN OTRO COORDINADOR
			// Buscar coordinadores disponibles y que ademas un usuario no este en espera
			PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());
			printDevLog("\n SEGUNDA VERIFICACION DE Busqueda: Coordinadores disponibles con usuarios disponibles y sin espera");
			String sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
					+ " LEFT JOIN users ON (user_userid = assc_userid) "
					+ " WHERE assc_assigned = 0 AND assc_fullassignment = 0 AND assc_enable = 1 "
					+ " AND assc_assingcoordinatorid NOT IN (SELECT assa_assingcoordinatorid "
					+ "		FROM assignsalesmen "
					+ " 	LEFT JOIN users ON (user_userid = assa_userid) " + 
					"		WHERE assa_queueduser = 1 ORDER BY user_code ASC) "
					+ " AND assc_companyid = " + companyId
					+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
			printDevLog("SEGUNDA SQL_coord-notIn-queued:"+sqlCoordintor);
			pmConn.doFetch(sqlCoordintor);
			if (pmConn.next()) {
				printDevLog("	SEGUNDA VERIFICACION: - Existen Coordinadores disponibles SIN ESPERA");
				// Obtener coordinador
				if (pmConn.getInt("assc_assingcoordinatorid") > 0) {
					bmoAssignCoordinator = (BmoAssignCoordinator)pmAssignCoordinator.get(pmConn, pmConn.getInt("assc_assingcoordinatorid"));
					printDevLog("SEGUNDA VERIFICACION: Coordinador:"+bmoAssignCoordinator.getBmoUser().getCode());
//					assignSalesman(pmConnSalesman, bmoAssignCoordinator, bmUpdateResult);
					// Buscar vendedores disponibles SIN espera
					sqlSalesman = "SELECT * FROM assignsalesmen "
							+ " LEFT JOIN users ON (user_userid = assa_userid) "
							+ " WHERE assa_assigned = 0 AND assa_queueduser = 0 AND assa_enable = 1 "
							+ " AND assa_assingcoordinatorid = " + bmoAssignCoordinator.getId()
							+ " ORDER BY user_code ASC, assa_assignsalesmanid ASC; ";
					printDevLog("SEGUNDA VERIFICACION: SQL-VEND:"+sqlSalesman);

					pmConn.doFetch(sqlSalesman);
					if (pmConn.next()) {
						printDevLog("SEGUNDA VERIFICACION: Existen vendedores disponibles SIN espera");
						if (pmConn.getInt("assa_assignsalesmanid") > 0) 
							bmoAssignSalesman = (BmoAssignSalesman)pmAssignSalesman.get(pmConn, pmConn.getInt("assa_assignsalesmanid"));
						printDevLog("SEGUNDA VERIFICACION: IDRegistro:"+bmoAssignSalesman.getId() + "|Vendedor:"+bmoAssignSalesman.getBmoUser().getCode());
					}
				}
			} else {
				// Buscar vendedores disponibles CON espera
				sqlSalesman = "SELECT * FROM assignsalesmen "
						+ " LEFT JOIN users ON (user_userid = assa_userid) "
						+ " WHERE assa_assigned = 0 AND assa_queueduser = 1 AND assa_enable = 1 "
						+ " AND assa_assingcoordinatorid = " + bmoAssignCoordinator.getId()
						+ " ORDER BY user_code ASC, assa_assignsalesmanid ASC; ";
				printDevLog("SQL-VEND:"+sqlSalesman);
	
				pmConn.doFetch(sqlSalesman);
				if (pmConn.next()) {
					printDevLog("Existen vendedores disponibles CON espera");
					if (pmConn.getInt("assa_assignsalesmanid") > 0) 
						bmoAssignSalesman = (BmoAssignSalesman)pmAssignSalesman.get(pmConn, pmConn.getInt("assa_assignsalesmanid"));
					printDevLog("IDRegistro:"+bmoAssignSalesman.getId() + "|Vendedor:"+bmoAssignSalesman.getBmoUser().getCode());
				}
			}
		}
		// Regresar el objeto del vendedor
		bmUpdateResult.setBmObject(bmoAssignSalesman);
		
//		pmConnSalesman.close();
	}
	
	// Actualizar datos 
	public void assignmentUpdate(PmConn pmConn, int companyId, BmUpdateResult bmUpdateResult) throws SFException {

		printDevLog("--------- Actualizar datos -------------");

		// Busquedas para limpiar firmar en caso de encontrar completados los vendedores
		String sqlCoordintor = "";
		// Buscar coordinadores SIN asignar, NO completos
		sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
				+ " LEFT JOIN users ON (user_userid = assc_userid) "
				+ " WHERE assc_assigned = 0 AND assc_fullassignment = 0 AND assc_enable = 1 "
				+ " AND assc_companyid = " + companyId
				+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
		printDevLog("SQL_existe disponibles:"+sqlCoordintor);
		pmConn.doFetch(sqlCoordintor);
		if (pmConn.next()) {
			printDevLog("SI existen");
			// NO hacer nada
		} else {
			printDevLog("NO existen");
			// Buscar coordinadores asignados y SIN completar
			sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
					+ " LEFT JOIN users ON (user_userid = assc_userid) "
					+ " WHERE assc_assigned = 1 AND assc_fullassignment = 0 AND assc_enable = 1 "
					+ " AND assc_companyid = " + companyId
					+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
			printDevLog("1SQL_asignados-completos-enCola:"+sqlCoordintor);
			pmConn.doFetch(sqlCoordintor);
			
			if (pmConn.next()) {
				// Quitar firmas en coordinadores
				sqlCoordintor = "UPDATE assigncoordinators "
						+ " SET assc_assigned = 0 "
						+ " WHERE assc_fullassignment = 0 "
						+ " AND assc_companyid = " + companyId;
//						+ " ORDER BY assc_assingcoordinatorid ASC; ";
				printDevLog("2SQL_QuitarFirmaCoord:"+sqlCoordintor);
				pmConn.doUpdate(sqlCoordintor);
			} else {
				// Buscar coordinadores asignados y SIN completar
				sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
						+ " LEFT JOIN users ON (user_userid = assc_userid) "
						+ " WHERE assc_assigned = 1 AND assc_fullassignment = 0 AND assc_enable = 1 "
						+ " AND assc_companyid = " + companyId
						+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
				printDevLog("3SQL_coord-Assigned-notFull"+sqlCoordintor);
				pmConn.doFetch(sqlCoordintor);
				
				if (pmConn.next()) {
					// Quitar firmas en coordinadores
					sqlCoordintor = "UPDATE assigncoordinators "
							+ " SET assc_assigned = 0 "
							+ " WHERE assc_fullassignment = 0"
							+ " AND assc_companyid = " + companyId;
					printDevLog("4SQL_QuitarFirmaCoord:"+sqlCoordintor);
					pmConn.doUpdate(sqlCoordintor);
				} else {
					printDevLog("\n"
							+ "		No hay coordinadores/vendedores disponibles, resetear asignaciones de estos");

					// No hay coordinadores/vendedores disponibles, resetear asignaciones de estos
					sqlCoordintor = "SELECT assc_assingcoordinatorid FROM assigncoordinators "
							+ " LEFT JOIN users ON (user_userid = assc_userid) "
							+ " WHERE assc_assigned = 1 AND assc_fullassignment = 1 AND assc_enable = 1 "
							+ " AND assc_companyid = " + companyId
							+ " ORDER BY user_code ASC, assc_assingcoordinatorid ASC; ";
					printDevLog("5SQL_SinDisponibilidad:"+sqlCoordintor);
					pmConn.doFetch(sqlCoordintor);
					
					if (pmConn.next()) {
						printDevLog("Reiniciar asignaciones en coordinadores y vendedores:");

						// Quitar firmas en coordinadores
						sqlCoordintor = "UPDATE assigncoordinators "
								+ " SET assc_assigned = 0, assc_fullassignment = 0 "
								+ " WHERE assc_companyid = " + companyId;
						printDevLog("6SQL_BorrarTodasFirmasCoord:"+sqlCoordintor);
						pmConn.doUpdate(sqlCoordintor);
						
						// Quitar firmas en vendedores
						sqlCoordintor = "UPDATE assignsalesmen "
								+ " SET assa_assigned = 0, assa_queueduser = 0 "
								+ " WHERE assa_assingcoordinatorid "
								+ "			IN (SELECT assc_assingcoordinatorid FROM assigncoordinators WHERE assc_companyid = " + companyId + ") ";
						printDevLog("7SQL_BorrarTodasFirmasVend:"+sqlCoordintor);
						pmConn.doUpdate(sqlCoordintor);
					} else {
						printDevLog("NO HACER NADA");
					}
				}
			}
		}
	}
	

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoAssignCoordinator = (BmoAssignCoordinator)bmObject;

		PmAssignSalesman pmAssignSalesman = new PmAssignSalesman(getSFParams());
		// Existen vendedores en el coordinador?
		if (pmAssignSalesman.existAssingCoordinatorId(pmConn, bmoAssignCoordinator.getId()))
			bmUpdateResult.addError(bmoAssignCoordinator.getUserId().getName(), "Existen Vendedores asignados.");

		if (!bmUpdateResult.hasErrors())
			super.delete(pmConn, bmoAssignCoordinator, bmUpdateResult);

		return bmUpdateResult;
	}
}
