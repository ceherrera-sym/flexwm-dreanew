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
import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoContractConcept;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoEstimationItem;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;

/**
 * @author smuniz
 *
 */
public class UiEstimationItem extends UiList {
	BmoEstimationItem bmoEstimationItem;

	public UiEstimationItem(UiParams uiParams) {
		super(uiParams, new BmoEstimationItem());
		bmoEstimationItem = (BmoEstimationItem)getBmObject();
	}
	
	@Override
	public void postShow(){
		//Falta "Descripcion de cierre de contrato" con el boton de "Cerrar Contrato" y otro boton de "Autorizar"
	}
	
	@Override
	public void create() {
		UiEstimationItemForm uiEstimationItemForm = new UiEstimationItemForm(getUiParams(), 0);
		uiEstimationItemForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		bmoEstimationItem = (BmoEstimationItem)bmObject;
		UiEstimationItemForm uiEstimationItemForm = new UiEstimationItemForm(getUiParams(), bmoEstimationItem.getId());
		uiEstimationItemForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {
		UiEstimationItemForm uiEstimationItemForm = new UiEstimationItemForm(getUiParams(), bmObject.getId());
		uiEstimationItemForm.show();
	}
	
	public class UiEstimationItemForm extends UiFormDialog {
		BmoEstimationItem bmoEstimationItem;
		//BmoEstimationItem bmoEstimationItem;

		UiSuggestBox contractEstimationIdUiSuggestBox = new UiSuggestBox(new BmoContractEstimation());
		UiSuggestBox contractConceptItemIdUiSuggestBox = new UiSuggestBox(new BmoContractConcept());
		TextBox consecutiveTextBox = new TextBox();

		TextBox codeUnitPriceTextBox = new TextBox();
		TextBox nameUnitPriceTextBox = new TextBox();
		TextBox codeConceptGroupTextBox = new TextBox();
		TextBox nameConceptHeadingTextBox = new TextBox();

		TextBox quantityWorkContractTextBox = new TextBox();
		TextBox quantityConceptTextBox = new TextBox();
		TextBox acum1TextBox = new TextBox();
		TextBox acum2TextBox = new TextBox();
		TextBox packagesTextBox = new TextBox();
		TextBox amountTextBox = new TextBox();

		public UiEstimationItemForm(UiParams uiParams, int id) {
			super(uiParams, new BmoEstimationItem(), id); 
		}



		@Override
		public void populateFields(){
			bmoEstimationItem = (BmoEstimationItem)getBmObject();
			formFlexTable.addField(1, 0, contractEstimationIdUiSuggestBox, bmoEstimationItem.getContractEstimationId());
			formFlexTable.addField(2, 0, contractConceptItemIdUiSuggestBox, bmoEstimationItem.getContractConceptItemId());
			formFlexTable.addField(3, 0, consecutiveTextBox, bmoEstimationItem.getConsecutive());

			/*formFlexTable.addFieldReadOnly(2, 2, codeUnitPriceTextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getBmoUnitPrice().getCode());
			formFlexTable.addFieldReadOnly(3, 0, codeConceptGroupTextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getBmoConceptHeading().getBmoConceptGroup().getCode());
			formFlexTable.addFieldReadOnly(3, 2, nameUnitPriceTextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getBmoUnitPrice().getName());
			formFlexTable.addFieldReadOnly(4, 0, nameConceptHeadingTextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getBmoConceptHeading().getName());
			formFlexTable.addFieldEmpty(4, 2);
			formFlexTable.addFieldEmpty(5,0);
			formFlexTable.addFieldEmpty(5, 2);


			formFlexTable.addLabelField(6, 0, "Estimación por Paquetes");
			formFlexTable.addLabelField(6, 2, "Estimación por Conceptos");

			formFlexTable.addFieldReadOnly(7, 0, quantityWorkContractTextBox, bmoEstimationItem.getBmoContractEstimation().getBmoWorkContract().getQuantity());
			formFlexTable.addFieldReadOnly(7, 2, quantityConceptTextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getQuantity()); //falta multiplicar por quantityWorkContractTextBox
			formFlexTable.addLabelField(8, 0, "Acumulado / Estimación #".concat(bmoEstimationItem.getBmoContractEstimation().getConsecutive().toString()));
			formFlexTable.addFieldReadOnly(8, 2, acum1TextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getQuantity()); //le falta, lleva funcion y otra caja de texto
			formFlexTable.addLabelField(9, 0, "Acumulado / Estimación #".concat(bmoEstimationItem.getBmoContractEstimation().getConsecutive().toString()));
			formFlexTable.addFieldReadOnly(9,2, acum2TextBox, bmoEstimationItem.getBmoContractConcept().getBmoConcept().getQuantity());//le falta, lleva funcion y otra caja de texto
			formFlexTable.addField(10, 0, packagesTextBox, bmoEstimationItem.getAmount()); // No va getAmount, sino manda a llamar la funcion javascript:changeAmount(), 
			formFlexTable.addField(10, 2, amountTextBox, bmoEstimationItem.getAmount()); //manda a llamar la funcion javascript:changePackages()*/
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoEstimationItem.setId(id);
			bmoEstimationItem.getContractEstimationId().setValue(contractEstimationIdUiSuggestBox.getSelectedId());
			bmoEstimationItem.getContractConceptItemId().setValue(contractConceptItemIdUiSuggestBox.getSelectedId());
			bmoEstimationItem.getConsecutive().setValue(consecutiveTextBox.getText());
			bmoEstimationItem.getQuantity().setValue(amountTextBox.getText());

			return bmoEstimationItem;
		}

		@Override
		public void close() {
			list();
		}
	}
}
