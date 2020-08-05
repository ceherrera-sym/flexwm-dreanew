package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.cm.BmoAssignSalesman;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;


public class PmAssignSalesman extends PmObject {
	BmoAssignSalesman bmoAssignSalesman;

	public PmAssignSalesman(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoAssignSalesman = new BmoAssignSalesman();
		setBmObject(bmoAssignSalesman);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoAssignSalesman.getUserId(), bmoAssignSalesman.getBmoUser()),
				new PmJoin(bmoAssignSalesman.getBmoUser().getAreaId(), bmoAssignSalesman.getBmoUser().getBmoArea()),
				new PmJoin(bmoAssignSalesman.getBmoUser().getLocationId(), bmoAssignSalesman.getBmoUser().getBmoLocation())
				))); 
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoAssignSalesman bmoAssignSalesman = (BmoAssignSalesman) autoPopulate(pmConn, new BmoAssignSalesman());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoAssignSalesman.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoAssignSalesman.setBmoUser(bmoUser);

		return bmoAssignSalesman;
	}


	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoAssignSalesman = (BmoAssignSalesman)bmObject;
		PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());
		
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();
		
//		boolean newRecord = false;
		if (!(bmoAssignSalesman.getAssingCoordinatorId().toInteger() > 0))
			bmUpdateResult.addError(bmoAssignSalesman.getAssingCoordinatorId().getName(), "El campo de debe estar vacío.");
		
		// Validar si ya existe
		if (!(bmoAssignSalesman.getId() > 0)) {
//			newRecord = true;
			if (existSalesman(pmConn, bmoAssignSalesman.getUserId().toInteger()))
				bmUpdateResult.addError(bmoAssignSalesman.getUserId().getName(), "El " + bmoAssignSalesman.getUserId().getLabel() + " ya existe");
			
			// Poner usuario en espera(cola)
			bmoAssignSalesman.getQueuedUser().setValue(1);
			
			// Colocar que esta activo
			bmoUser = (BmoUser)pmUser.get(pmConn, bmoAssignSalesman.getUserId().toInteger());
			if (bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE))
				bmoAssignSalesman.getEnable().setValue(1);
			else 
				bmoAssignSalesman.getEnable().setValue(0);
		}
		
		// Guardar cambios previos
		if (!bmUpdateResult.hasErrors()) {
			super.save(pmConn, bmoAssignSalesman, bmUpdateResult);
		
			// Actualizar si todos los vendedores estan asignados, si es nuevo firmar para posteriores asignaciones
			pmAssignCoordinator.updateComplete(pmConn, bmoAssignSalesman.getAssingCoordinatorId().toInteger(), bmUpdateResult);
		}
		
		if (!bmUpdateResult.hasErrors())
			super.save(pmConn, bmoAssignSalesman, bmUpdateResult);

		return bmUpdateResult;
	}
	
	// Esta completa la lisa de vedendores?
	public boolean completeAssignedSalesman(PmConn pmConn, int assingCoordinatorId) throws SFException {
		double countAssigned = 0;
		boolean complete = false;
		String sql = "SELECT AVG(assa_assigned) countAssigned "
				+ " FROM assignsalesmen WHERE assa_enable = 1 "
				+ " AND assa_assingcoordinatorid = " + assingCoordinatorId;
		
		printDevLog("sql_completeAssignedSalesman:" + sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			countAssigned = pmConn.getDouble("countAssigned");
		}
		if (countAssigned == 1) complete = true;
		printDevLog("AVG countAssigned:"+ countAssigned);
		
		return complete;
	}

	// Existe el usuario
	public boolean existSalesman(PmConn pmConn, int userId) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoAssignSalesman.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoAssignSalesman.getKind() 
				+ " WHERE " + bmoAssignSalesman.getUserId().getName() + " = " + userId;
		pmConn.doFetch(sql);

		int countExist = 0;
		if (pmConn.next()) countExist = pmConn.getInt("countExist");

		printDevLog("countExist:"+countExist);

		if (countExist > 0) return true;
		else return false;
	}
	
	// Existe el usuario y esta activo y sin asignar
	public boolean existSalesmanEnable(PmConn pmConn, int userId) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoAssignSalesman.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoAssignSalesman.getKind() 
				+ " WHERE " + bmoAssignSalesman.getUserId().getName() + " = " + userId
				+ " AND " + bmoAssignSalesman.getEnable().getName() + " = 1";
//				+ " AND " + bmoAssignSalesman.getAssigned().getName() + " = 1";

		pmConn.doFetch(sql);

		int countExist = 0;
		if (pmConn.next()) countExist = pmConn.getInt("countExist");

		printDevLog("countExist:"+countExist);

		if (countExist > 0) return true;
		else return false;
	}
	
	// Existen vendedores en el coordinador?
	public boolean existAssingCoordinatorId(PmConn pmConn, int assignCoordinatorId) throws SFPmException {
		String sql = "SELECT COUNT(" + bmoAssignSalesman.getIdFieldName() + ") AS countExist " 
				+ " FROM " + bmoAssignSalesman.getKind() 
				+ " WHERE " + bmoAssignSalesman.getAssingCoordinatorId().getName() + " = " + assignCoordinatorId;
		pmConn.doFetch(sql);

		int countExist = 0;
		if (pmConn.next()) countExist = pmConn.getInt("countExist");

		printDevLog("countExist:"+countExist);

		if (countExist > 0) return true;
		else return false;
	}
	
	
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoAssignSalesman = (BmoAssignSalesman)bmObject;
		PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());

		super.delete(pmConn, bmoAssignSalesman, bmUpdateResult);
		
		// Actualizar si todos los vendedores estan asignados
		pmAssignCoordinator.updateComplete(pmConn, bmoAssignSalesman.getAssingCoordinatorId().toInteger(), bmUpdateResult);
		
		
		return bmUpdateResult;
	}
}
