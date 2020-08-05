/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoBankMovConcept;

import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiBankMovConcept extends UiFormList {
	BmoBankMovConcept bmoBankMovConcept;
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	TextBox codeTextBox = new TextBox();
	UiListBox paccListBox;
	UiListBox raccListBox;
	TextBox amountTextBox = new TextBox();
	TextBox totalBkmcTextBox = new TextBox();
	int bankMovementId;
	BmoBankMovement bmoBankMovement;
	private int totalConceptsRpcAttempt = 0;
	private int balanceRpcAttempt = 0;

	public UiBankMovConcept(UiParams uiParams, Panel defaultPanel, BmoBankMovement bmoBankMovement1) {
		super(uiParams, defaultPanel, new BmoBankMovConcept());
		bmoBankMovConcept = (BmoBankMovConcept)getBmObject();
		bmoBankMovement = (BmoBankMovement)bmoBankMovement1;
		bankMovementId = bmoBankMovement.getId();

		// Lista solo los conceptos ligados al movimiento bancario
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoBankMovConcept.getKind(), 
				bmoBankMovConcept.getBankMovementId().getName(), 
				bmoBankMovConcept.getBankMovementId().getLabel(), 
				BmFilter.EQUALS, 
				bmoBankMovement.getId(),
				bmoBankMovement.getIdFieldName());

		// Preparar filtro para mostrar las cxp
		BmoPaccount bmoPaccount = new BmoPaccount();
		BmFilter bmFilter = new BmFilter();
		if (bmoBankMovement.getSupplierId().toInteger() > 0) {
			bmFilter.setValueFilter(bmoPaccount.getKind(), 
					bmoPaccount.getSupplierId(), 
					bmoBankMovement.getSupplierId().toInteger()				
					);
		}

		BmFilter bmFPAccWithDraw = new BmFilter();

		if (bmoBankMovement.getSupplierId().toInteger() > 0 ) {
			if(bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
				bmFPAccWithDraw.setValueFilter(bmoPaccount.getKind(), 
						bmoPaccount.getBmoPaccountType().getType(), 
						"" + BmoPaccountType.TYPE_DEPOSIT);			
			} else  {		
				bmFPAccWithDraw.setValueFilter(bmoPaccount.getKind(), 
						bmoPaccount.getBmoPaccountType().getType(), 
						"" + BmoPaccountType.TYPE_WITHDRAW);
			}
		}

		BmFilter bmFPaccAut = new BmFilter();
		bmFPaccAut.setValueFilter(bmoPaccount.getKind(),bmoPaccount.getStatus().getName(),	"" + BmoPaccount.STATUS_AUTHORIZED);

		BmFilter bmFPaccPP = new BmFilter();
		if (bmoBankMovement.getSupplierId().toInteger() > 0 ) {
			if(bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
				bmFPaccPP.setValueFilter(bmoPaccount.getKind(),bmoPaccount.getPaymentStatus().getName(), "" + BmoPaccount.PAYMENTSTATUS_TOTAL);
			} else {
				bmFPaccPP.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getPaymentStatus().getName(), "" + BmoPaccount.PAYMENTSTATUS_PENDING);
			}
		}	

		paccListBox = new UiListBox(getUiParams(), bmoPaccount);		
		paccListBox.addBmFilter(bmFilter);
		paccListBox.addBmFilter(bmFPAccWithDraw);
		paccListBox.addBmFilter(bmFPaccAut);
		paccListBox.addBmFilter(bmFPaccPP);

		// Preparar filtro para mostrar las cxc
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilterCXC = new BmFilter();
		bmFilterCXC.setValueFilter(bmoRaccount.getKind(), 
				bmoRaccount.getCustomerId(), 
				bmoBankMovement.getCustomerId().toInteger()
				);
		BmFilter bmFRAccWithDraw = new BmFilter();

		if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {			
			bmFRAccWithDraw.setValueFilter(bmoRaccount.getKind(), 
					bmoRaccount.getBmoRaccountType().getType(), 
					"" + BmoRaccountType.TYPE_WITHDRAW);			
		} else  {		
			bmFRAccWithDraw.setValueFilter(bmoRaccount.getKind(), 
					bmoRaccount.getBmoRaccountType().getType(), 
					"" + BmoRaccountType.TYPE_DEPOSIT);
		}


		BmFilter bmFRaccAut = new BmFilter();
		bmFRaccAut.setValueFilter(bmoRaccount.getKind(),bmoRaccount.getStatus().getName(), 
				"" + BmoRaccount.STATUS_AUTHORIZED);

		BmFilter bmFRaccPP = new BmFilter();

		if(bmoBankMovement.getBmoBankMovType().getType().toChar() == BmoBankMovType.TYPE_DEPOSIT) {			
			bmFRaccPP.setValueFilter(bmoRaccount.getKind(),bmoRaccount.getPaymentStatus().getName(), "" + BmoRaccount.PAYMENTSTATUS_PENDING);
		} else {
			bmFRaccPP.setValueFilter(bmoRaccount.getKind(),bmoRaccount.getPaymentStatus().getName(), "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
		}




		raccListBox = new UiListBox(getUiParams(), bmoRaccount);
		raccListBox.addBmFilter(bmFilterCXC);
		raccListBox.addBmFilter(bmFRAccWithDraw);
		raccListBox.addBmFilter(bmFRaccAut);
		raccListBox.addBmFilter(bmFRaccPP);		
	}

	@Override
	public void populateFields() {
		bmoBankMovConcept = (BmoBankMovConcept)getBmObject();
		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {				
			formFlexTable.addField(0, 0, paccListBox, bmoBankMovConcept.getPaccountId());	
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXC) {
			formFlexTable.addField(0, 2, raccListBox, bmoBankMovConcept.getRaccountId());		
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_DEPOSITFREE) {
			formFlexTable.addField(0, 4, codeTextBox, bmoBankMovement.getCode());
		}

		formFlexTable.addField(0, 6, amountTextBox, bmoBankMovConcept.getAmount());

		formFlexTable.addField(1, 6, totalBkmcTextBox, bmoBankMovConcept.getTotal());

		getTotalConcepts();

		typeEffect();
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoBankMovConcept.setId(id);
		bmoBankMovConcept.getCode().setValue(codeTextBox.getText());
		bmoBankMovConcept.getAmount().setValue(amountTextBox.getText());
		bmoBankMovConcept.getTotal().setValue(totalBkmcTextBox.getText());

		if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXP) {
			bmoBankMovConcept.getPaccountId().setValue(paccListBox.getSelectedId());
			bmoBankMovConcept.getRaccountId().setValue(0);
		}

		if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXC) {
			bmoBankMovConcept.getRaccountId().setValue(raccListBox.getSelectedId());
			bmoBankMovConcept.getPaccountId().setValue(0);
		}

		bmoBankMovConcept.getBankMovementId().setValue(bankMovementId);		

		return bmoBankMovConcept;
	}

	@Override
	public void formListChange(ChangeEvent event) {
		//Obtener el saldo de una CXP o CXC		
		if(bmoBankMovement.getBmoBankMovType().getType().toChar() != BmoBankMovType.TYPE_DEPOSIT) {
			getBalance();
		} else {
			getBalance();
		}
	}

	private void typeEffect() {		
		raccListBox.setEnabled(false);
		paccListBox.setEnabled(false);		
		amountTextBox.setEnabled(false);
		codeTextBox.setEnabled(false);
		totalBkmcTextBox.setEnabled(false);


		if (bmoBankMovement.getBmoBankMovType() != null) {
			// Si es de tipo Ordenes de compra
			if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXC) {
				raccListBox.setEnabled(true);
			}  
			// Si es de tipo Comisiones
			if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXP) {
				paccListBox.setEnabled(true);				
				if (bmoBankMovement.getBmoBankMovType().getType().toChar() == BmoBankMovType.TYPE_DEPOSIT) {
					amountTextBox.setEnabled(true);
				}
			}

			if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_DEPOSITFREE) {
				codeTextBox.setEnabled(true);
				amountTextBox.setEnabled(true);
			}									
		}

		// Si esta conciliado, eliminar visualizacion de botones
		if (bmoBankMovement.getStatus().toChar() == BmoBankMovement.STATUS_RECONCILED) {
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
		} else {
			if (bmoBankMovConcept.getId() > 0 ) {
				saveButton.setVisible(false);
			} else {
				saveButton.setVisible(true);
			}
		}

	}
	// Primer intento
	public void getBalance() {
		getBalance(0);
	}

	public void getBalance(int balanceRpcAttempt) {
		if (balanceRpcAttempt < 5) {
			setBalanceRpcAttempt(balanceRpcAttempt + 1);

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					if (getBalanceRpcAttempt() < 5)
						getBalance(getBalanceRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-getBalance() ERROR: " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					setBalanceRpcAttempt(0);
					amountTextBox.setValue(result.getMsg());
					amountTextBox.setEnabled(true);
				}
			};
			try {			
				String type = "";
				String id = "0";
				if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXP)	 {
					type = "" + BmoBankMovType.CATEGORY_CXP;
					id = paccListBox.getSelectedId();
				}
	
				if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_CXC) {
					type = "" + BmoBankMovType.CATEGORY_CXC;
					id = raccListBox.getSelectedId();
				}
				getUiParams().getBmObjectServiceAsync().action(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, type,  id, callback);
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-getBalance() ERROR: " + e.toString());
			}
		}
	}

	public void setAmount(BmoBankMovConcept bmoBankMovConcept) {
		numberFormat = NumberFormat.getCurrencyFormat();		
		totalBkmcTextBox.setText(numberFormat.format(bmoBankMovConcept.getTotal().toDouble()));
	}
	
	//Obtener la cantidad en almacen
	public void getTotalConcepts() {
		getTotalConcepts(0);
	}

	//Obtener la cantidad en almacen
	public void getTotalConcepts(int totalConceptsRpcAttempt) {
		if (totalConceptsRpcAttempt < 5) {
			setTotalConceptsRpcAttempt(totalConceptsRpcAttempt + 1);

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					//stopLoading();
					if (getTotalConceptsRpcAttempt() < 5)
						getTotalConcepts(getTotalConceptsRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-getTotalConcepts() ERROR: " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					//stopLoading();				
					//totalBkmcTextBox.setText(result.getMsg());
					setTotalConceptsRpcAttempt(0);
					try {
						bmoBankMovConcept.getTotal().setValue(result.getMsg());
						setAmount(bmoBankMovConcept);
					} catch (BmException e) {					
						e.printStackTrace();
					}
				}
			};
	
			try {	
				totalBkmcTextBox.setText("");
				//startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, "" + BmoBankMovConcept.GETTOTAL, "" + bankMovementId, callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getTotalConcepts() ERROR: " + e.toString());
			}
		}
	} 

	// Variables para llamadas RPC
	public int getTotalConceptsRpcAttempt() {
		return totalConceptsRpcAttempt;
	}

	public void setTotalConceptsRpcAttempt(int totalConceptsRpcAttempt) {
		this.totalConceptsRpcAttempt = totalConceptsRpcAttempt;
	}

	public int getBalanceRpcAttempt() {
		return balanceRpcAttempt;
	}

	public void setBalanceRpcAttempt(int balanceRpcAttempt) {
		this.balanceRpcAttempt = balanceRpcAttempt;
	}

}
