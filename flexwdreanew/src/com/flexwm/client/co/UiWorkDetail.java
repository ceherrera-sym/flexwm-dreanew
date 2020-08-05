/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkItem;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;


public class UiWorkDetail extends UiDetail {
	protected BmoWork bmoWork;	

	public UiWorkDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoWork(), id);
		bmoWork = (BmoWork)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoWork = (BmoWork)getBmObject();
		addTitleLabel(bmoWork.getCode());
		addLabel(bmoWork.getName());
		addLabel(bmoWork.getStartDate());
		addLabel(bmoWork.getDescription());
		addLabel(bmoWork.getIsMaster());
		addLabel(bmoWork.getBmoDevelopmentPhase().getCode());
		addLabel(bmoWork.getBmoCompany().getName());
		addLabel(bmoWork.getStatus());
		addLabel(bmoWork.getTotal());

		addActionLabel("Inicio", bmoWork.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar oportunidad
		addActionLabel("Editar", "edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showWorkForm();
			}
		});

		// Precios Unitarios
		addActionLabel(new BmoUnitPrice(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showUnitPrices();
			}
		});

		// Items
		addActionLabel(new BmoWorkItem(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showWorkItems();
			}
		});


		addActionLabel("Ir a Listado", "close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				close();
			}
		});

		// Panel default inicial
		showStart();
	}

	@Override
	public void close() {
		UiWork uiWorkList = new UiWork(getUiParams());
		setUiType(UiParams.MASTER);
		uiWorkList.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWorkStart uiWorkStart = new UiWorkStart(getUiParams(), bmoWork);		
		uiWorkStart.show();
	}

	public void showWorkForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWork uiWorkForm = new UiWork(getUiParams());
		uiWorkForm.edit(bmoWork);
	}

	public void showUnitPrices() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiUnitPrice uiUnitPriceList = new UiUnitPrice(getUiParams());
		BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoUnitPrice.getKind(), bmoUnitPrice.getWorkId().getName(), bmoWork.getId());
		getUiParams().getUiProgramParams(bmoUnitPrice.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoUnitPrice().getProgramCode(), UiParams.SLAVE);
		uiUnitPriceList.show();
	}

	public void showWorkItems() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWorkShowItem uiWorkShowItem = new UiWorkShowItem(getUiParams(), bmoWork.getId());
		setUiType(UiParams.DETAILFORM);
		uiWorkShowItem.show();
	}

	public void reset() {
		UiWorkDetail uiWorkDetail = new UiWorkDetail(getUiParams(), bmoWork.getId());
		uiWorkDetail.show();
	}

}
