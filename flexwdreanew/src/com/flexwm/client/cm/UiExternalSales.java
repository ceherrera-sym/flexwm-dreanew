/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoExternalSales;
import com.flexwm.shared.op.BmoProduct;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiExternalSales extends UiList {
	private UiExternalSalesForm uiExternalSalesForm;
	BmoExternalSales bmoExternalSales;
	
	Image batchProductPricesImage;

	public UiExternalSales(UiParams uiParams) {
		super(uiParams, new BmoExternalSales());
		bmoExternalSales = (BmoExternalSales)getBmObject();
		
		batchProductPricesImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/batch.png"));
		batchProductPricesImage.setTitle("Carga Masiva de Ventas Externas...");
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

	@Override
	public void create() {
		uiExternalSalesForm = new UiExternalSalesForm(getUiParams(), 0);
		uiExternalSalesForm.show();
	}
	public void showBatch() {
		String url = "/batch/exts_batch.jsp";
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
	}
	public void postShow() {
		if (getSFParams().hasWrite(getBmObject().getProgramCode()))
			localButtonPanel.add(batchProductPricesImage);
	}

	@Override
	public void open(BmObject bmObject) {
		UiExternalSalesForm uiExternalSalesForm = new UiExternalSalesForm(getUiParams(), bmObject.getId());
		uiExternalSalesForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		uiExternalSalesForm = new UiExternalSalesForm(getUiParams(), bmObject.getId());
		uiExternalSalesForm.show();
	}

	public UiExternalSalesForm getUiExternalSalesForm() {
		uiExternalSalesForm = new UiExternalSalesForm(getUiParams(), 0);
		return uiExternalSalesForm;
	}

	public class UiExternalSalesForm extends UiFormDialog {
		TextBox claveTextBox = new TextBox();
		UiSuggestBox customerIdSuggestBox = new UiSuggestBox(new BmoCustomer());
		TextArea referenceTextArea = new TextArea();
		UiDateBox dateBox = new UiDateBox();
		TextBox quantityTextBox = new TextBox();
		TextBox priceTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		UiSuggestBox productIdSuggestBox = new UiSuggestBox(new BmoProduct());
		 
		UiSuggestBox externalsalesIdSuggestbox = new UiSuggestBox(new BmoExternalSales());
		
		
		BmoExternalSales bmoExternalSales;

		String generalSection = "Datos Generales";
		

		public UiExternalSalesForm(UiParams uiParams, int id) {
			super(uiParams, new BmoExternalSales(), id);
			bmoExternalSales = (BmoExternalSales)getBmObject();
			
			}



		@Override
		public void populateFields(){
			bmoExternalSales = (BmoExternalSales)getBmObject();
			totalTextBox.setEnabled(false);
			
			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(2, 0, claveTextBox,bmoExternalSales.getCode());
			claveTextBox.setEnabled(false);
			formFlexTable.addField(3, 0, customerIdSuggestBox,bmoExternalSales.getCustomerId());
			formFlexTable.addField(4, 0, referenceTextArea, bmoExternalSales.getReference());
			formFlexTable.addField(5, 0, dateBox, bmoExternalSales.getDate());
			formFlexTable.addField(6, 0, quantityTextBox, bmoExternalSales.getQuantity());
            formFlexTable.addField(7, 0, priceTextBox,bmoExternalSales.getPrice());
            formFlexTable.addField(8, 0, totalTextBox,bmoExternalSales.getTotal());
            formFlexTable.addField(9, 0, productIdSuggestBox,bmoExternalSales.getProductId());

		
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoExternalSales.setId(id);
			bmoExternalSales.getCustomerId().setValue(customerIdSuggestBox.getSelectedId());
			bmoExternalSales.getReference().setValue(referenceTextArea.getText());
			bmoExternalSales.getDate().setValue(dateBox.getTextBox().getText());
			bmoExternalSales.getQuantity().setValue(quantityTextBox.getText());			 
			bmoExternalSales.getPrice().setValue(priceTextBox.getText());	
		    bmoExternalSales.getTotal().setValue(String.valueOf(Double.parseDouble(quantityTextBox.getText()) * Double.parseDouble(priceTextBox.getText())));
			bmoExternalSales.getProductId().setValue(productIdSuggestBox.getSelectedId());
	
			return bmoExternalSales;
			
		}

		@Override
		public void close() {
			list();
		}
		public void open(BmObject bmObject) {
			
		}

		@Override
		public void saveNext() {
			if (isMaster()) {
				if (newRecord ) {					
					edit(bmoExternalSales);
				} else {				
					showList();
					
				}
			}
		}	
		
	}
}


