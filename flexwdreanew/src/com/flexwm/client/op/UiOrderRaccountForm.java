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

import com.flexwm.client.fi.UiRaccountOrder;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;


public class UiOrderRaccountForm extends UiForm {
	BmoOrder bmoOrder = new BmoOrder();
	TextBox amountTextBox = new TextBox();
	TextBox discountTextBox = new TextBox();
	TextBox taxTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();

	public UiOrderRaccountForm(UiParams uiParams, int id) {
		super(uiParams, new BmoOrder(), id);
		bmoOrder = (BmoOrder)getBmObject();
	}

	@Override
	public void populateFields(){
		bmoOrder = (BmoOrder)getBmObject();

		if (!newRecord) {
			formFlexTable.addLabelField(1, 0, bmoOrder.getCode());
			formFlexTable.addLabelField(1, 2, bmoOrder.getName());

			formFlexTable.addLabelField(2, 0, bmoOrder.getStatus());
			formFlexTable.addFieldReadOnly(2, 2, amountTextBox, bmoOrder.getAmount());			

			formFlexTable.addLabelField(3, 0, bmoOrder.getLockStatus());
			formFlexTable.addFieldReadOnly(3, 2, discountTextBox, bmoOrder.getDiscount());				

			formFlexTable.addLabelField(4, 0, bmoOrder.getDeliveryStatus());
			formFlexTable.addFieldReadOnly(4, 2, taxTextBox, bmoOrder.getTax());

			formFlexTable.addLabelField(5, 0, bmoOrder.getPaymentStatus());
			formFlexTable.addFieldReadOnly(5, 2, totalTextBox, bmoOrder.getTotal());
			
			if (!newRecord) {
				TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
				tabPanel.setSize("100%", "400px");

				FlowPanel raccountPanel = new FlowPanel();
				raccountPanel.setSize("100%", "100%");
				ScrollPanel raccountScrollPanel = new ScrollPanel();
				raccountScrollPanel.setSize("98%", "350px");
				raccountScrollPanel.add(raccountPanel);
				UiRaccountOrder uiRaccountOrder = new UiRaccountOrder(getUiParams(), raccountPanel, bmoOrder);
				uiRaccountOrder.show();
				tabPanel.add(raccountScrollPanel, "Cuentas x Cobrar");

				formFlexTable.addPanel(6, 0, tabPanel, 4);
			}
		}
	}
	
	@Override
	public void postShow() { 
		saveButton.setVisible(false);
	}
}
