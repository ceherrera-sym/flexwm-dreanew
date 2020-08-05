/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoCustomerServiceType;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


/**
 * @author smuniz
 *
 */

public class PmCustomerService extends PmObject{
	BmoCustomerService bmoCustomerService;

	public PmCustomerService(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoCustomerService = new BmoCustomerService();
		setBmObject(bmoCustomerService); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerService.getOrderId(), bmoCustomerService.getBmoOrder()),
				new PmJoin(bmoCustomerService.getBmoOrder().getOrderTypeId(), bmoCustomerService.getBmoOrder().getBmoOrderType()),
				new PmJoin(bmoCustomerService.getBmoOrder().getWFlowId(), bmoCustomerService.getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoCustomerService.getBmoOrder().getBmoWFlow().getWFlowTypeId(), bmoCustomerService.getBmoOrder().getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoCustomerService.getBmoOrder().getBmoWFlowType().getWFlowCategoryId(), bmoCustomerService.getBmoOrder().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoCustomerService.getBmoOrder().getBmoWFlow().getWFlowPhaseId(), bmoCustomerService.getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoCustomerService.getBmoOrder().getCurrencyId(), bmoCustomerService.getBmoOrder().getBmoCurrency()),
				new PmJoin(bmoCustomerService.getBmoOrder().getCustomerId(), bmoCustomerService.getBmoOrder().getBmoCustomer()),
				new PmJoin(bmoCustomerService.getBmoOrder().getBmoCustomer().getTerritoryId(), bmoCustomerService.getBmoOrder().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoCustomerService.getBmoOrder().getBmoCustomer().getReqPayTypeId(), bmoCustomerService.getBmoOrder().getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoCustomerService.getCustomerServiceTypeId(), bmoCustomerService.getBmoCustomerServiceType()),
				new PmJoin(bmoCustomerService.getBmoCustomerServiceType().getUserId(), bmoCustomerService.getBmoCustomerServiceType().getBmoUser()),
				new PmJoin(bmoCustomerService.getBmoCustomerServiceType().getBmoUser().getAreaId(), bmoCustomerService.getBmoCustomerServiceType().getBmoUser().getBmoArea()),
				new PmJoin(bmoCustomerService.getBmoCustomerServiceType().getBmoUser().getLocationId(), bmoCustomerService.getBmoCustomerServiceType().getBmoUser().getBmoLocation()),
				new PmJoin(bmoCustomerService.getBmoOrder().getBmoWFlow().getWFlowFunnelId(), bmoCustomerService.getBmoOrder().getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		//return autoPopulate(pmConn, new bmoCustomerService());
		bmoCustomerService = (BmoCustomerService)autoPopulate(pmConn, new BmoCustomerService());	

		// BmoUser
		BmoOrder bmoOrder = new BmoOrder();
		int orderId = (int)pmConn.getInt(bmoOrder.getIdFieldName());
		if (orderId > 0) bmoCustomerService.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else bmoCustomerService.setBmoOrder(bmoOrder);

		// bmoCustomerServiceType
		BmoCustomerServiceType bmoCustomerServiceType = new BmoCustomerServiceType();
		int orderComplaintTypeId = (int)pmConn.getInt(bmoCustomerServiceType.getIdFieldName());
		if (orderComplaintTypeId > 0) bmoCustomerService.setBmoCustomerServiceType((BmoCustomerServiceType) new PmCustomerServiceType(getSFParams()).populate(pmConn));
		else bmoCustomerService.setBmoCustomerServiceType(bmoCustomerServiceType);

		return bmoCustomerService;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();


		// Filtro de antecionCliente de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( cuse_companyid in (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )" +
					") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " cuse_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerService bmoCustomerService = (BmoCustomerService)bmObject;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoCustomerService.getId() > 0)) {
			super.save(pmConn, bmoCustomerService, bmUpdateResult);
			bmoCustomerService.setId(bmUpdateResult.getId());	
			bmoCustomerService.getCode().setValue(bmoCustomerService.getCodeFormat());
		}

		if (bmoCustomerService.getStatus().equals(BmoCustomerService.STATUS_OPENED))
			bmoCustomerService.getActive().setValue(true);
		else 
			bmoCustomerService.getActive().setValue(false);

		if (bmoCustomerService.getStatus().equals(BmoCustomerService.STATUS_CLOSED))
			if (bmoCustomerService.getSolutionDate().toString().equals(""))
				bmUpdateResult.addError(bmoCustomerService.getSolutionDate().getName(), "No se ha capturado la Fecha de Solución");

		super.save(pmConn, bmoCustomerService, bmUpdateResult);

		return bmUpdateResult;
	}
}

