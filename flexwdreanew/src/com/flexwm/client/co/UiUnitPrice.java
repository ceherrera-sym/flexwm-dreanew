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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoUnit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceItem;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.fi.BmoCurrency;


public class UiUnitPrice extends UiList {
	BmoUnitPrice bmoUnitPrice;
	Image batchProductPricesImage;

	public UiUnitPrice(UiParams uiParams) {
		super(uiParams, new BmoUnitPrice());
		bmoUnitPrice = (BmoUnitPrice)getBmObject();
		
		batchProductPricesImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/batch.png"));
		batchProductPricesImage.setTitle("Carga Precios Unitarios");
		batchProductPricesImage.setStyleName("listSearchImage");
		batchProductPricesImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					showBatch();
				}
			}
		});
	}
	public void showBatch() {
		String url = "/batch/unpr_batch.jsp";
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
	}

	@Override
	public void postShow() {
		if (isSlave()) {
			getUiParams().getUiTemplate().hideEastPanel();
		} else {			
			addFilterSuggestBox(new UiSuggestBox(new BmoUnit()), new BmoUnit(), bmoUnitPrice.getUnitId());			
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoUnitPrice.getCategory()), bmoUnitPrice, bmoUnitPrice.getCategory());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoUnitPrice.getType()), bmoUnitPrice, bmoUnitPrice.getType());
		}
		if (getSFParams().hasWrite(getBmObject().getProgramCode()))
			localButtonPanel.add(batchProductPricesImage);
	}

	@Override
	public void create() {
		UiUnitPriceForm uiUnitPriceForm = new UiUnitPriceForm(getUiParams(), 0);
		uiUnitPriceForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiUnitPriceForm uiUnitPriceForm = new UiUnitPriceForm(getUiParams(), bmObject.getId());
		uiUnitPriceForm.show();
	}

	public class UiUnitPriceForm extends UiFormDialog {

		BmoUnitPrice bmoUnitPrice;
		BmoWork bmoWork = new BmoWork();

		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiDateBox dateDateBox = new UiDateBox();
		TextBox subTotalTextBox = new TextBox();
		TextBox indirectsTextBox = new TextBox();
		TextBox indirectsTotalTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		UiListBox typeListBox = new UiListBox(getUiParams());    
		UiListBox categoryListBox = new UiListBox(getUiParams());
		UiSuggestBox unitSuggestBox = new UiSuggestBox(new BmoUnit());
		UiListBox originListBox = new UiListBox(getUiParams());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

		// Items de UnitPrice
		private UiUnitPriceItemGrid uiUnitPriceItemGrid;

		UnitPriceUpdater unitPriceUpdater = new UnitPriceUpdater();

		String generalSection = "Datos Generales";

		public UiUnitPriceForm(UiParams uiParams, int id) {
			super(uiParams, new BmoUnitPrice(), id);			

			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					indirectChangePrice();
				}
			};
			indirectsTextBox.addValueChangeHandler(textChangeHandler);
		}

		public void indirectChangePrice() {
			try {
				bmoUnitPrice.getIndirects().setValue(indirectsTextBox.getText());
				saveIndirectChangePrice();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-othersExpensesChange() ERROR: " + e.toString());
			}
		}

		public void saveIndirectChangePrice() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveDiscount(): ERROR " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUnitPriceUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoUnitPrice.getPmClass(), bmoUnitPrice, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveOtherExpensesChange(): ERROR " + e.toString());
			}
		}

		public void processUnitPriceUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Errores al actualizar los indirectos.", "Errores al actualizar los indirectos: " + result.errorsToString());
			else updateAmount(id);
		}

		public void reset() {
			updateAmount(id);
			uiUnitPriceItemGrid.show();		
		}

		public void setAmount(BmoUnitPrice bmoUnitPrice) {
			numberFormat = NumberFormat.getCurrencyFormat();
			subTotalTextBox.setText(numberFormat.format(bmoUnitPrice.getSubTotal().toDouble()));
			indirectsTotalTextBox.setText(numberFormat.format(bmoUnitPrice.getTotalIndirects().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoUnitPrice.getTotal().toDouble()));
			setAmount("" + bmoUnitPrice.getSubTotal().toDouble(), "" + bmoUnitPrice.getTotalIndirects().toDouble() ,"" + bmoUnitPrice.getTotal().toDouble());
		}

		private void setAmount(String subTotal, String totalIndirects, String total ) {		
			double a = Double.parseDouble(subTotal);
			subTotalTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(totalIndirects);
			indirectsTotalTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(total);
			totalTextBox.setText(numberFormat.format(a));
		}

		@Override
		public void populateFields() {
			bmoUnitPrice = (BmoUnitPrice)getBmObject();

			if (newRecord) {
				try {
					bmoUnitPrice.getDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoUnitPrice.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toString());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}			
			}

			if (isSlave()) {
				// Es derivado de una obra, no es necesario mostrarlo
				int workId = Integer.parseInt(getUiParams().getUiProgramParams(bmoUnitPrice.getProgramCode()).getForceFilter().getValue());			
				getBmoWork(workId);
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 4);
			formFlexTable.addField(2, 0, codeTextBox, bmoUnitPrice.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoUnitPrice.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoUnitPrice.getDescription());
			formFlexTable.addField(5, 0, dateDateBox, bmoUnitPrice.getDate());
			formFlexTable.addField(6, 0, typeListBox, bmoUnitPrice.getType());
			formFlexTable.addField(7, 0, categoryListBox, bmoUnitPrice.getCategory());		
			formFlexTable.addField(8, 0, unitSuggestBox, bmoUnitPrice.getUnitId());
			formFlexTable.addField(9, 0, currencyListBox, bmoUnitPrice.getCurrencyId());

			if (!newRecord && bmoUnitPrice.getCategory().toChar() != BmoUnitPrice.CATEGORY_SUPPLIES) {		
				formFlexTable.addSectionLabel(10, 0, "Items", 4);
				BmoUnitPriceItem bmoUnitPriceItem = new BmoUnitPriceItem();
				FlowPanel uiUnitPriceItemPanel = new FlowPanel();
				BmFilter filterUnitPriceItem = new BmFilter();
				filterUnitPriceItem.setValueFilter(bmoUnitPriceItem.getKind(), bmoUnitPriceItem.getUnitPriceId(), bmoUnitPrice.getId());
				getUiParams().setForceFilter(bmoUnitPriceItem.getProgramCode(), filterUnitPriceItem);
				UiUnitPriceItemGrid uiUnitPriceItemGrid = new UiUnitPriceItemGrid(getUiParams(), uiUnitPriceItemPanel, bmoUnitPrice, unitPriceUpdater);
				setUiType(bmoUnitPriceItem.getProgramCode(), UiParams.MINIMALIST);
				uiUnitPriceItemGrid.show();
				formFlexTable.addPanel(11, 0, uiUnitPriceItemPanel, 4);

				formFlexTable.addSectionLabel(12, 0, "Totales", 4);
				formFlexTable.addField(13, 0, subTotalTextBox, bmoUnitPrice.getSubTotal());
				formFlexTable.addField(14, 0, indirectsTextBox, bmoUnitPrice.getIndirects());
				formFlexTable.addField(15, 0, indirectsTotalTextBox, bmoUnitPrice.getTotalIndirects());
				formFlexTable.addField(16, 0, totalTextBox, bmoUnitPrice.getTotal());
			} else {
				formFlexTable.addField(10, 0, totalTextBox, bmoUnitPrice.getTotal());
			}

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {		
			bmoUnitPrice.setId(id);		
			if (isSlave()) {
				bmoUnitPrice.getWorkId().setValue(bmoWork.getId());
			} else {
				bmoUnitPrice.getWorkId().setValue(bmoUnitPrice.getWorkId().toInteger());
			}
			bmoUnitPrice.getCode().setValue(codeTextBox.getText());
			bmoUnitPrice.getName().setValue(nameTextBox.getText());
			bmoUnitPrice.getDescription().setValue(descriptionTextArea.getText());
			bmoUnitPrice.getDate().setValue(dateDateBox.getTextBox().getText());		
			bmoUnitPrice.getType().setValue(typeListBox.getSelectedCode());
			bmoUnitPrice.getCategory().setValue(categoryListBox.getSelectedCode());		
			bmoUnitPrice.getUnitId().setValue(unitSuggestBox.getSelectedId());		
			bmoUnitPrice.getCurrencyId().setValue(currencyListBox.getSelectedId());		
			bmoUnitPrice.getIndirects().setValue(indirectsTextBox.getText());
			bmoUnitPrice.getSubTotal().setValue(subTotalTextBox.getText());
			bmoUnitPrice.getTotal().setValue(totalTextBox.getText());		

			return bmoUnitPrice;
		}

		private void statusEffect() {		
			subTotalTextBox.setEnabled(false);

			if (bmoUnitPrice.getCategory().equals(BmoUnitPrice.CATEGORY_SUPPLIES)) {
				totalTextBox.setEnabled(true);			
			} else {
				subTotalTextBox.setEnabled(false);
				indirectsTotalTextBox.setEnabled(false);
				totalTextBox.setEnabled(false);			
			}

//			if (bmoWork.getStatus().equals(BmoWork.STATUS_AUTHORIZED)) {
//				buttonPanel.setVisible(false);
//			}

		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == categoryListBox) {
				try {
					bmoUnitPrice.getCategory().setValue(categoryListBox.getSelectedCode());				

				} catch (BmException e) {				
					e.printStackTrace();
				}			
			}

			statusEffect();
		}

		//@Override
		public void close() {
			list();
		}

		public void getBmoWork(int workId) {

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoWork((BmoWork)result);
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoWork.getPmClass(), workId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + e.toString());
			}
		}

		private void setBmoWork(BmoWork bmoWork) {
			this.bmoWork = bmoWork;
		}

		public void updateAmount(int id) {
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmObject result) {
					stopLoading();
					setAmount((BmoUnitPrice)result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoUnitPrice.getPmClass(), id, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
			}
		}


		protected class UnitPriceUpdater {
			public void changeUnitPrice() {
				stopLoading();
				reset();
			}		
		}
	}
}
