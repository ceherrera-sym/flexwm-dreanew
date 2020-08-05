/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server;

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.op.BmoOrder;

public class PmFlexConfig extends PmObject {
	BmoFlexConfig bmoFlexConfig = new BmoFlexConfig();

	public PmFlexConfig(SFParams sfParams) throws SFException {
		super(sfParams);
		setBmObject(bmoFlexConfig);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoFlexConfig());
	}

	char statusRevision = ' ';
	char statusExpired = ' ';
	char statusWon = ' ';
	char statusHold = ' ';
	char statusLost = ' ';

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoFlexConfig bmoFlexConfig = (BmoFlexConfig)bmObject;
		if(!(bmoFlexConfig.getOppoStatusRevision().toBoolean())) {
			statusRevision = BmoOpportunity.STATUS_REVISION;
		}
		if(!(bmoFlexConfig.getOppoStatusExpirada().toBoolean())) {
			statusExpired = BmoOpportunity.STATUS_EXPIRED;
		}
		if(!(bmoFlexConfig.getOppoStatusGanada().toBoolean())) {
			statusWon = BmoOpportunity.STATUS_WON;
		}
		if(!(bmoFlexConfig.getOppoStatusHold().toBoolean())) {
			statusHold = BmoOpportunity.STATUS_HOLD;
		}
		if(!(bmoFlexConfig.getOppoStatusPerdida()).toBoolean()) {
			statusLost = BmoOpportunity.STATUS_LOST;
		}
		//int contador = 0;
		String sql = " SELECT * FROM opportunities " +
				" WHERE oppo_status like '"+ statusRevision+"' OR oppo_status like '"+ statusRevision+"' OR oppo_status like '"+ statusWon+"'"
				+ " OR oppo_status like '"+ statusLost+"' OR oppo_status like '"+ statusExpired+"' OR oppo_status like '"+ statusHold+"';";			
		pmConn.doFetch(sql);
		printDevLog("sql " + sql);
		if (pmConn.next()) {
			bmUpdateResult.addError(bmoFlexConfig.getOppoStatusRevision().getName(), "No se puede desactivar ya que hay oportunidades con este estatus.");
		}

		// Validar siel pedido tiene pedidos cone 	este estatus
		char statusOrdeRevision = ' ';
		char statusAuthoized = ' ';
		char statusFinished = ' ';
		char statusCancelled = ' ';

		if(!(bmoFlexConfig.getOrdeStatusRevision().toBoolean())) {
			statusOrdeRevision = BmoOrder.STATUS_REVISION;
		}
		if(!(bmoFlexConfig.getOrdeStatusAuthorized().toBoolean())) {
			statusAuthoized = BmoOrder.STATUS_AUTHORIZED;
		}
		if(!(bmoFlexConfig.getOrdeStatusFinished().toBoolean())) {
			statusFinished = BmoOrder.STATUS_FINISHED;
		}
		if(!(bmoFlexConfig.getOrdeStatusCancelled().toBoolean())) {
			statusCancelled = BmoOrder.STATUS_CANCELLED;
		}

		String ordeSql = "SELECT * FROM orders "+
				" WHERE orde_status LIKE '"+statusOrdeRevision+"'"+
				" OR orde_status LIKE '"+statusAuthoized+"'"+
				" OR orde_status LIKE '"+statusFinished+"' "+
				" OR orde_status LIKE '"+statusCancelled+"';";
//		System.out.println("sql2*****************************   "+ordeSql);
		pmConn.doFetch(ordeSql);
		if(pmConn.next())
			bmUpdateResult.addError("", "No se puede desactivar ya que hay pedidos con este estatus.");

		if(!(bmUpdateResult.hasErrors()))
			super.save(pmConn, bmoFlexConfig, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		//bmUpdateResult.addError(bmoEosConfig.getName().getName(), "Error predefinido");
		//bmUpdateResult.addMsg("Validacion no satisfactoria.");
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		if (action.equals(BmoFlexConfig.ACTION_SEARCHPROFILESALESMAN)) {
			int profileId = 0;
			int companyId = Integer.parseInt(value);
			profileId = getSalesmanProfileId(companyId);
			if (profileId > 0)
				bmUpdateResult.setMsg("" + profileId);
			
			printDevLog("Perfil vendedor por empresa: " + profileId + " | Errores: " + bmUpdateResult.errorsToString()+"|");
		} else if (action.equals(BmoFlexConfig.ACTION_SEARCHCOLLECTPROFILE)) {
			int profileId = 0;
			int companyId = Integer.parseInt(value);
			profileId = getCollectProfileId(companyId);
			if (profileId > 0)
				bmUpdateResult.setMsg("" + profileId);
			
			printDevLog("Perfil vendedor por empresa: " + profileId + " | Errores: " + bmUpdateResult.errorsToString()+"|");
		} else if (action.equals(BmoFlexConfig.ACTION_SEARCHPROFILECOORDINATOR)) {
			int profileId = 0;
			int companyId = Integer.parseInt(value);
			profileId = getCoordinatorProfileId(companyId);
			if (profileId > 0)
				bmUpdateResult.setMsg("" + profileId);
			
			printDevLog("Perfil Coordinador por empresa: " + profileId + " | Errores: " + bmUpdateResult.errorsToString()+"|");
		}
			
		return bmUpdateResult;
	}
	
	// Obtener el perfil de vendedores por empresa
	public int getSalesmanProfileId(int companyId) throws SFException {
		int profileId = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		profileId = getSalesmanProfileId(pmConn, companyId);
		pmConn.close();
		return profileId;
	}
	
	// Obtener el perfil de vendedores por empresa
	public int getSalesmanProfileId(PmConn pmConn, int companyId) throws SFException {
		int profileId = 0;
		String sql = "SELECT cosa_profileid FROM companysalesmen WHERE cosa_companyid = " + companyId;
		pmConn.doFetch(sql);
		if (pmConn.next()) profileId = pmConn.getInt("cosa_profileid"); 
		printDevLog("Perfil vendedor por empresa: " + profileId);
		return profileId;
	}
	
	// Obtener el perfil de cobranza por empresa
	public int getCollectProfileId(int companyId) throws SFException {
		int profileId = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		profileId = getCollectProfileId(pmConn, companyId);
		pmConn.close();
		return profileId;
	}
	
	// Obtener el perfil de cobranza por empresa
	public int getCollectProfileId(PmConn pmConn, int companyId) throws SFException {
		int profileId = 0;
		String sql = "SELECT cocp_collectprofileid FROM companycollectionprofiles WHERE cocp_companyid = " + companyId;
		pmConn.doFetch(sql);
		if (pmConn.next()) profileId = pmConn.getInt("cocp_collectprofileid"); 
		
		printDevLog("Perfil cobranza por empresa: " + profileId);
		return profileId;
	}
	
	// Obtener el ID de perfil de cobranza por empresa
	public int getCompanyCollectionProfileId(PmConn pmConn, int companyId) throws SFException {
		int companyCollectionProfileId = 0;
		String sql = "SELECT cocp_companycollectionprofileid FROM companycollectionprofiles WHERE cocp_companyid = " + companyId;
		pmConn.doFetch(sql);
		if (pmConn.next()) companyCollectionProfileId = pmConn.getInt("cocp_companycollectionprofileid"); 
		
		printDevLog("ID Registro: " + companyCollectionProfileId);
		return companyCollectionProfileId;
	}
	
	// Obtener el perfil de vendedores por empresa
	public int getCoordinatorProfileId(int companyId) throws SFException {
		int profileId = 0;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		profileId = getCoordinatorProfileId(pmConn, companyId);
		pmConn.close();
		return profileId;
	}
	
	// Obtener el perfil de vendedores por empresa
	public int getCoordinatorProfileId(PmConn pmConn, int companyId) throws SFException {
		int profileId = 0;
		String sql = "SELECT cosa_coordinatorprofileid FROM companysalesmen WHERE cosa_companyid = " + companyId;
		pmConn.doFetch(sql);
		if (pmConn.next()) profileId = pmConn.getInt("cosa_coordinatorprofileid"); 
		printDevLog("Perfil coordinador por empresa: " + profileId);
		return profileId;
	}

}
