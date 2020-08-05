/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;


import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowCategoryCompany;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiWFlowType extends UiList {
	BmoWFlowType bmoWFlowType;

	public UiWFlowType(UiParams uiParams) {
		super(uiParams, new BmoWFlowType());
		bmoWFlowType = (BmoWFlowType)getBmObject();
	}

	@Override
	public void create() {
		UiWFlowTypeForm uiWFlowTypeForm = new UiWFlowTypeForm(getUiParams(), 0);
		uiWFlowTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowTypeDetail uiWFlowTypeDetail = new UiWFlowTypeDetail(getUiParams(), bmObject.getId());
		uiWFlowTypeDetail.show();		
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWFlowTypeForm uiWFlowTypeForm = new UiWFlowTypeForm(getUiParams(), bmObject.getId());
		uiWFlowTypeForm.show();	
	}

	@Override
	public void postShow() {
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory()), bmoWFlowType.getBmoWFlowCategory());
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowType.getStatus()), bmoWFlowType,bmoWFlowType.getStatus());
	}

	public class UiWFlowTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextArea commentsTextArea = new TextArea();	
		UiListBox wFlowCategoryListBox = new UiListBox(getUiParams(), new BmoWFlowCategory());
		BmoWFlowType bmoWFlowType;
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox statusListBox = new UiListBox(getUiParams());

		boolean copyProcessing = false;

		Button copyButton = new Button("Copiar");
		TextBox newCodeTextBox = new TextBox();


		public UiWFlowTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowType(), id);

			// Botón de imprimir
			copyButton.setStyleName("formCloseButton");
			copyButton.setVisible(true);
			copyButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareCopy();
				}
			});
		}

		@Override
		public void populateFields(){
			bmoWFlowType = (BmoWFlowType)getBmObject();
			if(getSFParams().getSelectedCompanyId() > 0 && newRecord 
					&& ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
				try {
					bmoWFlowType.getCompanyId().setValue(getSFParams().getSelectedCompanyId());
//					filterByCompany(bmoWFlowType.getCompanyId().toInteger());
					
					BmoWFlowCategoryCompany bmoWFlowCategoryCompany = new BmoWFlowCategoryCompany();
					BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
					BmFilter bmFilter = new BmFilter();
					bmFilter.setInFilter(bmoWFlowCategoryCompany.getKind(), bmoWFlowCategory.getIdFieldName(), 
							bmoWFlowCategoryCompany.getWflowCategoryId().getName(), 
							bmoWFlowCategoryCompany.getCompanyId().getName(),
							""+getSFParams().getSelectedCompanyId());
					wFlowCategoryListBox = new UiListBox(getUiParams(),new BmoWFlowCategory(),bmFilter);
					
				} catch (BmException e) {

					showErrorMessage("populateFields() - No se pudo asignar la empresa : " + e.getMessage() );
				}
			}
			if(newRecord && ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())
				formFlexTable.addField(0, 0, companyListBox, bmoWFlowType.getCompanyId());
			formFlexTable.addField(1, 0, wFlowCategoryListBox, bmoWFlowType.getWFlowCategoryId());
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlowType.getName());
			if(!newRecord && ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())
				formFlexTable.addField(3, 0, new UiWFlowTypeCompanyLabelList(getUiParams(), bmoWFlowType));
			formFlexTable.addField(4, 0, descriptionTextArea, bmoWFlowType.getDescription());
			formFlexTable.addField(5, 0, commentsTextArea, bmoWFlowType.getComments());
			if( !((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())
				formFlexTable.addField(6, 0, companyListBox, bmoWFlowType.getCompanyId());
			formFlexTable.addField(7, 0, statusListBox, bmoWFlowType.getStatus());

			if (!newRecord) {
				formFlexTable.addSectionLabel(8, 0, "Copiar Tipo de Flujo", 4);
				formFlexTable.addField(9, 0, newCodeTextBox, bmoWFlowType.getName());
				formFlexTable.addButtonCell(10, 0, copyButton);
				formFlexTable.hideSection("Copiar Tipo de Flujo");
			}
			if(getSFParams().getSelectedCompanyId() > 0 && newRecord 
					&& ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) 
				companyListBox.setEnabled(false);
			
//			statusEffect();
		}

//		private void statusEffect() {
//			wFlowCategoryListBox.setEnabled(false);
//			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() && newRecord) {
//				if (companyListBox.getSelectedBmObject() != null) {
//					wFlowCategoryListBox.setEnabled(true);
//				} else {
//					
//					wFlowCategoryListBox.setEnabled(false);
//				}
//			} else {
//				
//				wFlowCategoryListBox.setEnabled(true);
//			}
//
//
//		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowType.setId(id);
			bmoWFlowType.getName().setValue(nameTextBox.getText());
			bmoWFlowType.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowType.getComments().setValue(commentsTextArea.getText());
			bmoWFlowType.getWFlowCategoryId().setValue(wFlowCategoryListBox.getSelectedId());
			if(newRecord || (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) )
				bmoWFlowType.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoWFlowType.getStatus().setValue(statusListBox.getSelectedCode());

			return bmoWFlowType;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if(event.getSource() ==  companyListBox) {
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {
					if (companyListBox.getSelectedBmObject() != null) {
						filterByCompany(companyListBox.getSelectedBmObject().getId());
					}else {
						filterByCompany(0);
					}
				}
			}
		}		
		public void filterByCompany(int companyId) {

			if(companyId > 0) {

				BmoWFlowCategoryCompany bmoWFlowCategoryCompany = new BmoWFlowCategoryCompany();
				BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setInFilter(bmoWFlowCategoryCompany.getKind(), bmoWFlowCategory.getIdFieldName(), 
						bmoWFlowCategoryCompany.getWflowCategoryId().getName(), 
						bmoWFlowCategoryCompany.getCompanyId().getName(),
						""+companyId);
				wFlowCategoryListBox.clearFilters();
				wFlowCategoryListBox.addFilter(bmFilter);			

			} else {

				wFlowCategoryListBox.clearFilters();
			}
			wFlowCategoryListBox.populate(bmoWFlowType.getWFlowCategoryId());
//			statusEffect();
		}

		@Override
		public void saveNext() {
			if (!newRecord) { 
				showFormMsg("Cambios almacenados exitosamente.", "Cambios almacenados exitosamente.");
			} else {
				UiWFlowTypeDetail uiWFlowTypeDetail = new UiWFlowTypeDetail(getUiParams(), getBmObject().getId());
				uiWFlowTypeDetail.show();
			}		
		}

		private void prepareCopy(){
			copyAction(newCodeTextBox.getText());
		}

		private void copyAction(String newCode) {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					copyProcessing = false;
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-copyAction() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					copyProcessing = false;
					if (result.hasErrors()) showFormMsg("Error al copiar Tipo de Flujo.", "El Tipo de Flujo no se pudo Copiar: " + result.errorsToString());
					else {
						showFormMsg("Tipo de Flujo Copiado Exitosamente.", "Tipo de Flujo Copiado Exitosamente");
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!copyProcessing) {
					copyProcessing = true;
					getUiParams().getBmObjectServiceAsync().action(bmoWFlowType.getPmClass(), bmoWFlowType, BmoWFlowType.ACTION_COPY, newCode, callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-copyAction() ERROR: " + e.toString());
			}
		}
	}
}