/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoOrderSession;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionType;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiActionHandler;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiSessionSelectorList extends UiList {

	BmoSession bmoSession;
	BmoOrderSession bmoOrderSession = new BmoOrderSession();
	BmoSessionType bmoSessionType;
	String orderId;
	UiActionHandler uiActionHandler;

	public UiSessionSelectorList(UiParams uiParams, Panel defaultPanel, BmoSessionType bmoSessionType, UiActionHandler uiActionHandler) {
		super(uiParams, defaultPanel, new BmoSession());
		this.bmoSession = (BmoSession)getBmObject();
		this.bmoSessionType = bmoSessionType;
		this.uiActionHandler = uiActionHandler;

		orderId = getUiParams().getUiProgramParams(bmoOrderSession.getProgramCode()).getForceFilter().getValue();
	}

	@Override
	public void postShow() {
		// Filtros suggestbox sesiones para no mostrar y duplicar sesiones en un pedido
		BmFilter filterByOrder = new BmFilter();
		filterByOrder.setNotInFilter(bmoOrderSession.getKind(), bmoSession.getIdFieldName(), 
				bmoOrderSession.getSessionId().getName(), bmoOrderSession.getOrderId().getName(), orderId);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterByOrder);

		// Filtrar sesiones del tipo de venta
		BmFilter filterBySessionType = new BmFilter();
		filterBySessionType.setValueFilter(bmoSession.getKind(), bmoSession.getSessionTypeId(), bmoSessionType.getId());
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterBySessionType);

		// Filtrar sesiones disponibles
		BmFilter filterByAvailability = new BmFilter();
		filterByAvailability.setValueFilter(bmoSession.getKind(), bmoSession.getAvailable(), 1);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterByAvailability);

		// Filtrar por vendedores
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + salesGroupId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoUser(), filterSalesmen), new BmoUser());
	}

	@Override
	public void create() {
		UiSession uiSessionForm = new UiSession(getUiParams());
		uiSessionForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSession uiSessionForm = new UiSession(getUiParams());
		uiSessionForm.open(bmoSession);
	}
}