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

import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoCompany;


public class UiWhMovement extends UiList {
	BmoWhMovement bmoWhMovement;

	public UiWhMovement(UiParams uiParams) {
		super(uiParams, new BmoWhMovement());
		bmoWhMovement = (BmoWhMovement)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterListBox(new UiListBox(getUiParams(), new BmoWarehouse()), bmoWhMovement.getBmoToWhSection().getBmoWarehouse());
		addFilterSuggestBox(new UiSuggestBox(new BmoWhSection()), new BmoWhSection(), bmoWhMovement.getToWhSectionId());
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoWhMovement.getType()), bmoWhMovement, bmoWhMovement.getType());
	}

	@Override
	public void create() {
		UiWhMovementForm uiWhMovementForm = new UiWhMovementForm(getUiParams(), 0);
		uiWhMovementForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWhMovementForm uiWhMovementForm = new UiWhMovementForm(getUiParams(), bmObject.getId());
		uiWhMovementForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		BmoWhMovement bmoWhMovement = (BmoWhMovement)bmObject;
		// Entrada
		if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN) {
			UiWhMovementFormIn uiWhMovementFormIN = new UiWhMovementFormIn(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormIN.show();
		} 
		// Entrada devolución
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_DEV) {
			UiWhMovementFormInDev uiWhMovementFormInDev = new UiWhMovementFormInDev(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormInDev.show();
		} 
		// Ajuste Entrada
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_ADJUST) {
			UiWhMovementFormInAdjust uiWhMovementFormInAdjust = new UiWhMovementFormInAdjust(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormInAdjust.show();
		} 
		// Salida
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT) {
			UiWhMovementFormOut uiWhMovementFormOut = new UiWhMovementFormOut(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormOut.show();
		} 
		// Ajuste de Salida
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_ADJUST) {
			UiWhMovementFormOutAdjust uiWhMovementFormOutAdjust = new UiWhMovementFormOutAdjust(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormOutAdjust.show();
		} 
		// Salida por devolucion de OC
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_DEV) {
			UiWhMovementFormOutDev uiWhMovementFormOutDev = new UiWhMovementFormOutDev(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormOutDev.show();
		} 
		// Salida por Renta
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_OUT) {
			UiWhMovementFormRentalOut uiWhMovementFormRentalOut = new UiWhMovementFormRentalOut(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormRentalOut.show();
		} 
		// Entrada por Renta
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_IN) {
			UiWhMovementFormRentalIn uiWhMovementFormRentalIn = new UiWhMovementFormRentalIn(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormRentalIn.show();
		} 
		// Transferencia
		else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_TRANSFER) {
			UiWhMovementFormTransfer uiWhMovementFormTransfer = new UiWhMovementFormTransfer(getUiParams(), bmoWhMovement.getId());
			uiWhMovementFormTransfer.show();
		} 
		else {
			showSystemMessage("No se ha configurado la forma para el tipo de movimiento: " + bmoWhMovement.getType().getSelectedOption().getLabel());
		}
	}

	public class UiWhMovementForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox dateMovDateTimeBox = new UiDateTimeBox();
		UiListBox toWhSectionListBox = new UiListBox(getUiParams(), new BmoWhSection());
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiSuggestBox requisitionReceiptSuggestBox = new UiSuggestBox(new BmoRequisitionReceipt());
		UiSuggestBox orderDeliverySuggestBox = new UiSuggestBox(new BmoOrderDelivery());
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox companyListBox;

		BmoWhSection bmoWhSection = new BmoWhSection();
		BmoWhMovement bmoWhMovement;

		public UiWhMovementForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWhMovement(), id);

			// Filtrar requisiciones autorizadas
			BmoRequisition bmoRequisition = new BmoRequisition();
			BmFilter bmFilterAuthorizedRequisitions = new BmFilter();
			bmFilterAuthorizedRequisitions.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getStatus(), 
					"" + BmoRequisition.STATUS_AUTHORIZED);
			requisitionReceiptSuggestBox.addFilter(bmFilterAuthorizedRequisitions);

			// Filtrar requisiciones no entregadas totalmente
			BmFilter bmFilterDeliveryRequisitions = new BmFilter();
			bmFilterDeliveryRequisitions.setValueOperatorFilter(bmoRequisition.getKind(), 
					bmoRequisition.getDeliveryStatus(), BmFilter.NOTEQUALS,
					"" + BmoRequisition.DELIVERYSTATUS_TOTAL);
			requisitionReceiptSuggestBox.addFilter(bmFilterDeliveryRequisitions);

			// Filtrar pedidos autorizados
			BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
			BmFilter bmFilterAuthorizedOrders = new BmFilter();
			bmFilterAuthorizedOrders.setValueFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getStatus(), 
					"" + BmoOrderDelivery.STATUS_AUTHORIZED);
			orderDeliverySuggestBox.addFilter(bmFilterAuthorizedOrders);
		}

		@Override
		public void populateFields() {
			bmoWhMovement = (BmoWhMovement)getBmObject();
			companyListBox = new UiListBox(getUiParams(), new BmoCompany());

			if (newRecord) {
				try {
					bmoWhMovement.getDatemov().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
					if (getSFParams().getSelectedCompanyId() > 0) {
						bmoWhMovement.getCompanyId().setValue(getSFParams().getSelectedCompanyId());
						companyListBox.setEnabled(false);
					}
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
				}
			}

			formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoWhMovement.getCode());
			formFlexTable.addField(2, 0, typeListBox, bmoWhMovement.getType());
			formFlexTable.addLabelField(3, 0, bmoWhMovement.getDatemov());
			formFlexTable.addField(4, 0, requisitionReceiptSuggestBox, bmoWhMovement.getRequisitionReceiptId());
			formFlexTable.addField(5, 0, orderDeliverySuggestBox, bmoWhMovement.getOrderDeliveryId());
			formFlexTable.addField(6, 0, toWhSectionListBox, bmoWhMovement.getToWhSectionId());
			formFlexTable.addField(7, 0, descriptionTextArea, bmoWhMovement.getDescription());	
			formFlexTable.addField(8, 0, companyListBox, bmoWhMovement.getCompanyId());
			formFlexTable.addField(9, 0, statusListBox, bmoWhMovement.getStatus());

			statusEffect();
			populateTypeListBox();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWhMovement = (BmoWhMovement)getBmObject();
			bmoWhMovement.setId(id);
			bmoWhMovement.getDescription().setValue(descriptionTextArea.getText());
			bmoWhMovement.getType().setValue(typeListBox.getSelectedCode());
			bmoWhMovement.getToWhSectionId().setValue(toWhSectionListBox.getSelectedId());
			bmoWhMovement.getRequisitionReceiptId().setValue(requisitionReceiptSuggestBox.getSelectedId());
			bmoWhMovement.getOrderDeliveryId().setValue(orderDeliverySuggestBox.getSelectedId());
			bmoWhMovement.getStatus().setValue(statusListBox.getSelectedCode());
			bmoWhMovement.getAutoCreateItems().setValue(true);
			bmoWhMovement.getAmount().setValue(0);		
			bmoWhMovement.getCompanyId().setValue(companyListBox.getSelectedId());
			return bmoWhMovement;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				BmoWhMovement bmoWhMovement = (BmoWhMovement)getBmObject();
				// Entrada (Recibo O. C.)
				if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN) {
					UiWhMovementFormIn uiWhMovementFormIN = new UiWhMovementFormIn(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormIN.show();
				} 
				// Entrada devolución (Devolución Rec. O.C.)
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_DEV) {
					UiWhMovementFormInDev uiWhMovementFormInDev = new UiWhMovementFormInDev(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormInDev.show();
				} 
				// Ajuste Entrada
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_ADJUST) {
					UiWhMovementFormInAdjust uiWhMovementFormInAdjust = new UiWhMovementFormInAdjust(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormInAdjust.show();
				} 
				// Salida
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT) {
					UiWhMovementFormOut uiWhMovementFormOut = new UiWhMovementFormOut(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormOut.show();
				} 
				// Ajuste de Salida
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_ADJUST) {
					UiWhMovementFormOutAdjust uiWhMovementFormOutAdjust = new UiWhMovementFormOutAdjust(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormOutAdjust.show();
				} 
				// Devolucion Salida
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_DEV) {
					UiWhMovementFormOutDev uiWhMovementFormOutDev = new UiWhMovementFormOutDev(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormOutDev.show();
				} 
				// Salida por Renta
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_OUT) {
					UiWhMovementFormRentalOut uiWhMovementFormRentalOut = new UiWhMovementFormRentalOut(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormRentalOut.show();
				} 
				// Entrada por Renta
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_IN) {
					UiWhMovementFormRentalIn uiWhMovementFormRentalIn = new UiWhMovementFormRentalIn(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormRentalIn.show();
				} 
				// Transferencia
				else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_TRANSFER) {
					UiWhMovementFormTransfer uiWhMovementFormTransfer = new UiWhMovementFormTransfer(getUiParams(), bmoWhMovement.getId());
					uiWhMovementFormTransfer.show();
				} 
				else {
					showSystemMessage("No se ha configurado la forma para el tipo de movimiento: " + bmoWhMovement.getType().getSelectedOption().getLabel());
				}
			} else {
				UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
				uiWhMovementList.show();
			}		
		}

		public void populateTypeListBox() {
			typeListBox.clear();

			typeListBox.addItem("< Tipo >", "");
			typeListBox.addItem("Transferencia", "" + BmoWhMovement.TYPE_TRANSFER);
			typeListBox.addItem("Entrada Ajuste", "" + BmoWhMovement.TYPE_IN_ADJUST);
			typeListBox.addItem("Salida Ajuste", "" + BmoWhMovement.TYPE_OUT_ADJUST);
		}

		private void statusEffect() {
			statusListBox.setEnabled(false);
			requisitionReceiptSuggestBox.setEnabled(false);
			orderDeliverySuggestBox.setEnabled(false);
			toWhSectionListBox.setEnabled(false);
			companyListBox.setEnabled(false);
			
			if (newRecord) {
				formFlexTable.hideField(bmoWhMovement.getRequisitionReceiptId());
				formFlexTable.hideField(bmoWhMovement.getOrderDeliveryId());
				formFlexTable.hideField(bmoWhMovement.getToWhSectionId());
			}
		}
		
		private void typeEffect() {
			// Cambios segun el tipo de movimiento

			if (typeListBox.getSelectedCode() != null) {
				char type = typeListBox.getSelectedCode().charAt(0);

				// Recibo de OC: Entrada
				if (type == BmoWhMovement.TYPE_IN) {
					// Mostrar campos de entrada
					requisitionReceiptSuggestBox.setEnabled(true);
					toWhSectionListBox.setEnabled(false);

					// Filtrar secciones de almacen normales
					setWhSectionListBoxFilters();

					// Si no esta asignado el valor, asignarlo
					if (toWhSectionListBox.getSelectedId().equals("") || toWhSectionListBox.getSelectedId() == null) {
						try {
							bmoWhMovement.getToWhSectionId().setValue(((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getDefaultWhSectionId().toString());
						} catch (BmException e) {
							showFormMsg("Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel(), "Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel());
						}
					}

					// Mostrar en la forma e
					formFlexTable.showField(bmoWhMovement.getRequisitionReceiptId());
					formFlexTable.showField(bmoWhMovement.getToWhSectionId());

				} 
				// Entrada por ajuste
				else if (type == BmoWhMovement.TYPE_IN_ADJUST) {
					// Filtrar secciones de almacen normales
					populateCompany(-1);
					setWhSectionListBoxFilters();

					// Mostrar en la forma e
					toWhSectionListBox.setEnabled(true);
					formFlexTable.showField(bmoWhMovement.getToWhSectionId());
				}
				// Salida Venta
				else if (type == BmoWhMovement.TYPE_OUT) {
					// Mostrar en la forma e
					orderDeliverySuggestBox.setEnabled(true);
					formFlexTable.showField(bmoWhMovement.getOrderDeliveryId());

				} 
				// Devolucion de salida
				else if (type == BmoWhMovement.TYPE_OUT_DEV) {
					orderDeliverySuggestBox.setEnabled(true);

					toWhSectionListBox.setEnabled(true);
				} 
				// Ajuste salida
				else if (type == BmoWhMovement.TYPE_OUT_ADJUST) {
					// Limpiar/Añadir combo de empresa con todas las empresas
					populateCompany(-1);
					companyListBox.setEnabled(true);

					// Se limpia filtro de secc. almacen
					setWhSectionListBoxFilters();

					try {
						if (!(getSFParams().getSelectedCompanyId() > 0))
							bmoWhMovement.getCompanyId().setValue(getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger());
						bmoWhMovement.getToWhSectionId().setValue(-1);
					} catch (BmException e) {
						showFormMsg("Error al asignar valor al campo " + bmoWhMovement.getCompanyId().getLabel(), "Error al asignar valor al campo " + bmoWhMovement.getCompanyId().getLabel());
					} 
					formFlexTable.hideField(bmoWhMovement.getToWhSectionId());
					formFlexTable.hideField(bmoWhMovement.getRequisitionReceiptId());
					formFlexTable.hideField(bmoWhMovement.getOrderDeliveryId());
					formFlexTable.showField(bmoWhMovement.getCompanyId());

				} 
				// Transferencia
				else if (type == BmoWhMovement.TYPE_TRANSFER) {
					// limpiar filtro empresas
					populateCompany(-1);
					// Filtrar secciones de almacen normales
					setWhSectionListBoxFilters();
					// Mostrar en la forma
					toWhSectionListBox.setEnabled(true);
					formFlexTable.hideField(bmoWhMovement.getRequisitionReceiptId());
					formFlexTable.hideField(bmoWhMovement.getOrderDeliveryId());
					formFlexTable.showField(bmoWhMovement.getToWhSectionId());
				} 
				// Salida por Renta
				else if (type == BmoWhMovement.TYPE_RENTAL_OUT) {
					// Filtrar secciones de almacen normales
					setWhSectionListBoxFilters();

					// Si no esta asignado el valor, asignarlo
					if (toWhSectionListBox.getSelectedId().equals("") || toWhSectionListBox.getSelectedId() == null) {
						try {
							bmoWhMovement.getToWhSectionId().setValue(((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getDefaultWhSectionId().toString());
						} catch (BmException e) {
							showFormMsg("Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel(), "Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel());
						}
					}

					// Mostrar en la forma e
					toWhSectionListBox.setEnabled(true);
					orderDeliverySuggestBox.setEnabled(true);

					formFlexTable.showField(bmoWhMovement.getOrderDeliveryId());
					formFlexTable.showField(bmoWhMovement.getToWhSectionId());
				} 
				// Entrada de renta
				else if (type == BmoWhMovement.TYPE_RENTAL_IN) {
					// Filtrar secciones de almacen normales
					setWhSectionListBoxFilters();

					// Si no esta asignado el valor, asignarlo
					if (toWhSectionListBox.getSelectedId().equals("") || toWhSectionListBox.getSelectedId() == null) {
						try {
							bmoWhMovement.getToWhSectionId().setValue(((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getDefaultWhSectionId().toString());
						} catch (BmException e) {
							showFormMsg("Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel(), "Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel());
						}
					}

					// Mostrar en la forma e
					toWhSectionListBox.setEnabled(true);
					orderDeliverySuggestBox.setEnabled(true);
					formFlexTable.showField(bmoWhMovement.getOrderDeliveryId());
					formFlexTable.showField(bmoWhMovement.getToWhSectionId());
				} else {
					formFlexTable.hideField(bmoWhMovement.getRequisitionReceiptId());
					formFlexTable.hideField(bmoWhMovement.getOrderDeliveryId());
				}
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == typeListBox) 
				typeEffect();
			if (event.getSource() == toWhSectionListBox) {
				BmoWhSection bmoWhSection = (BmoWhSection)toWhSectionListBox.getSelectedBmObject();		
				if (bmoWhSection != null) 
					populateCompany(bmoWhSection.getBmoWarehouse().getCompanyId().toInteger());
				else
					populateCompany(-1);
			}
		}

		// Actualiza listado de empresas
		private void populateCompany(int companyId) {
			companyListBox.clear();
			companyListBox.clearFilters();
			setCompanyListBoxFilters(companyId);
			companyListBox.populate("" + companyId);
		}

		// Asigna filtros de listado de empresas
		private void setCompanyListBoxFilters(int companyId) {
			BmoCompany bmoCompany = new BmoCompany();

			if (companyId > 0) {
				BmFilter bmFilterByCompanyId = new BmFilter();
				bmFilterByCompanyId.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyListBox.addBmFilter(bmFilterByCompanyId);

			}  else {
				// Agregar empresa
				BmFilter bmFilterByCompanyId = new BmFilter();
				// Agregar empresa al filtro si esta seleccionado filtro de empresa maestra
				if (getSFParams().getSelectedCompanyId() > 0) {
					bmFilterByCompanyId.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), getSFParams().getSelectedCompanyId());
					companyListBox.addBmFilter(bmFilterByCompanyId);
				} else 	{
					//bmFilterByCompanyId.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
				}
				companyListBox.setEnabled(false);
			}
		}

		// Asigna filtros de listado de empresas
		private void setWhSectionListBoxFilters() {
			toWhSectionListBox.clear();
			toWhSectionListBox.clearFilters();
			// Filtrar secciones de almacen normales
			BmFilter bmFilterWhSections = new BmFilter();
			bmFilterWhSections.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getBmoWarehouse().getType(), 
					"" + BmoWarehouse.TYPE_NORMAL);
			toWhSectionListBox.addFilter(bmFilterWhSections);
			toWhSectionListBox.populate(bmoWhMovement.getToWhSectionId());
		}
	}
}
