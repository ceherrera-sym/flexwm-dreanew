/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;


public class UiProductPrice extends UiList {
	BmoProductPrice bmoProductPrice;
	BmoProduct bmoProduct;

	public UiProductPrice(UiParams uiParams, Panel defaultPanel, BmoProduct bmoProduct) {
		super(uiParams, defaultPanel, new BmoProductPrice());
		bmoProductPrice = (BmoProductPrice)getBmObject();
		this.bmoProduct = bmoProduct;
	}

	@Override
	public void create() {
		UiProductPriceForm uiProductPriceForm = new UiProductPriceForm(getUiParams(), 0);
		uiProductPriceForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoProductPrice = (BmoProductPrice)bmObject;
		UiProductPriceForm uiProductPriceForm = new UiProductPriceForm(getUiParams(), bmoProductPrice.getId());
		uiProductPriceForm.show(); 
	}

	private class UiProductPriceForm extends UiFormDialog {
		UiDateBox startDateBox = new UiDateBox();
		TextBox priceTextBox = new TextBox();
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		BmoOrderType bmoOrderType = new BmoOrderType();
		private int bmoOrderTypeRpcAttempt = 0;
		
		public UiProductPriceForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProductPrice(), id);
			
			// Filtro Sin registros
			if (newRecord) {
				// Filtro de tipos de flujo en categorias del modulo Proyectos
				BmFilter bmFilterByProgramId = new BmFilter();
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				bmFilterByProgramId.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), "-1");
				wFlowTypeListBox.addFilter(bmFilterByProgramId);
			}
		}

		@Override
		public void populateFields() {
			bmoProductPrice = (BmoProductPrice)getBmObject();
			if (newRecord) {
				populateFieldsProcess();
			} else {
	 			if (bmoProductPrice.getOrderTypeId().toInteger() > 0) {
	 				// Obtener el tipo del tipo de pedido
	 				getBmoOrderType();
	 				
	 				// Como hay un asincrono de por medio, si no se quita la botonera queda arriba
	 				formFlexTable.remove(buttonPanel);
	 			} else {
	 				populateFieldsProcess();
	 			}
			}
		}
		
		public void populateFieldsProcess() {
    		formFlexTable.addField(1, 0, startDateBox, bmoProductPrice.getStartDate());
 			formFlexTable.addField(2, 0, currencyListBox, bmoProductPrice.getCurrencyId());
 			formFlexTable.addField(3, 0, orderTypeListBox, bmoProductPrice.getOrderTypeId());
 			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoProductPrice.getWFlowTypeId());
 			formFlexTable.addField(5, 0, marketListBox, bmoProductPrice.getMarketId());
 			formFlexTable.addField(6, 0, companyListBox, bmoProductPrice.getCompanyId());
 			formFlexTable.addField(7, 0, priceTextBox, bmoProductPrice.getPrice());

 			if (!newRecord) {
				if (bmoProductPrice.getOrderTypeId().toInteger() > 0) {
					// Se añade botonera, solo cuando se haya removido
		 			formFlexTable.addButtonPanel(buttonPanel);
				}
			}
 			
 			statusEffect();
		}
		
		private void statusEffect() {
			if (!newRecord) {
				if (!(bmoProductPrice.getOrderTypeId().toInteger() > 0)) {
					wFlowTypeListBox.clear();
					wFlowTypeListBox.clearFilters();
				}
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProductPrice = new BmoProductPrice();
			bmoProductPrice.setId(id);
			bmoProductPrice.getPrice().setValue(priceTextBox.getText());				
			bmoProductPrice.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoProductPrice.getProductId().setValue(bmoProduct.getId());
			bmoProductPrice.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoProductPrice.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoProductPrice.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoProductPrice.getMarketId().setValue(marketListBox.getSelectedId());
			bmoProductPrice.getCompanyId().setValue(companyListBox.getSelectedId());

			return bmoProductPrice;
		}

		@Override
		public void formListChange(ChangeEvent event) {
			// Si el movimiento es en el lista de tipos de flujo de la oportunidad, modificar el de los efectos
			if (event.getSource() == orderTypeListBox) {
				BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
				if (bmoOrderType != null) {
					changeOrderType(bmoOrderType);
				} else {
					wFlowTypeListBox.clear();
					wFlowTypeListBox.clearFilters();
				}
			}
		}
		
		// Obtener el programa referencia del tipo de pedido
		private int getProgramIdByOrderType(BmoOrderType bmoOrderType) {
			int programId = -1;
			
			try {
				if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_CONSULTANCY)) {
					programId = getSFParams().getProgramId(new BmoConsultancy().getProgramCode());
				} else if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
					programId = getSFParams().getProgramId(new BmoProject().getProgramCode());
				} else if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SALE)) {
					programId = getSFParams().getProgramId(new BmoOrder().getProgramCode());
				} else { 
					showSystemMessage("No se pudo obtener el Programa através del Tipo de Pedido, verifique que este configurado.");
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + " - getProgramIdByOrderType() - Error: " + e.toString());
			}
			
			return programId;
		}

		// Filtrar tipos de flujo por empresa
		private void setWFlowTypeListBoxFilters(BmoOrderType bmoOrderType) {
			
			int bmProjectProgramId = -1;
			if (bmoOrderType.getId() > 0) {
				bmProjectProgramId = getProgramIdByOrderType(bmoOrderType);
			}

			// Filtro de tipos de flujo en categorias del modulo Proyectos
			BmFilter bmFilterByProgramId = new BmFilter();
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			bmFilterByProgramId.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), "" + bmProjectProgramId);

			// Filtros de tipos de flujos activos
			BmFilter bmFilterByStatus = new BmFilter();
			bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);

			// Filtro de flujos en categoria agregada al tipo de pedido
			BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			BmFilter bmFilterByWFlowCategories = new BmFilter();
			bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
					bmoWFlowCategory.getIdFieldName(), 
					bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
					bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
					"" + bmoOrderType.getId());
			
			// Agregar filtros al tipo de flujo
			wFlowTypeListBox.addFilter(bmFilterByProgramId); 
			wFlowTypeListBox.addFilter(bmFilterByStatus);
			wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);

		}

		// Cambia el tipo de pedido y modifica combo de Tipos de Flujo
		private void changeOrderType(BmoOrderType bmoOrderType) {
			wFlowTypeListBox.clear();
			wFlowTypeListBox.clearFilters();
			if (bmoOrderType.getId() > 0) {
				setWFlowTypeListBoxFilters(bmoOrderType);
			}
			wFlowTypeListBox.populate(bmoProductPrice.getWFlowTypeId().toString(), false);
		}
		
		private void getBmoOrderType() {
			getBmoOrderType(0);
		}

		private void getBmoOrderType(int bmoQuoteRpcAttempt) {
			if (bmoQuoteRpcAttempt < 5) {
				setBmoOrderTypeRpcAttempt(bmoQuoteRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getBmoOrderTypeRpcAttempt() < 5)
							getBmoOrderType(getBmoOrderTypeRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getBmoOrderType() ERROR: " + caught.toString());
					}

					public void onSuccess(BmObject result) {
						stopLoading();
						setBmoOrderTypeRpcAttempt(0);
						bmoOrderType = ((BmoOrderType)result);
						setWFlowTypeListBoxFilters(bmoOrderType);
						populateFieldsProcess();
					}
				};
				try {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoOrderType.getPmClass(), bmoProductPrice.getOrderTypeId().toInteger(), callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getBmoOrderType() ERROR: " + e.toString());
				}
			}
		}

		@Override
		public void close() {
			list();
		}

		// Variables para llamadas RPC
		public int getBmoOrderTypeRpcAttempt() {
			return bmoOrderTypeRpcAttempt;
		}

		public void setBmoOrderTypeRpcAttempt(int bmoOrderTypeRpcAttempt) {
			this.bmoOrderTypeRpcAttempt = bmoOrderTypeRpcAttempt;
		}

	}
}
