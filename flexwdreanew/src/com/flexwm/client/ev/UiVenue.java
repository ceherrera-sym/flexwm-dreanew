/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ev;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCity;
import com.flexwm.shared.cm.BmoDelegation;
import com.flexwm.shared.ev.BmoVenue;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiVenue extends UiList {
	private UiVenueForm uiVenueForm;
	BmoVenue bmoVenue;
	boolean slave = false;

	public UiVenue(UiParams uiParams) {
		super(uiParams, new BmoVenue());
		bmoVenue = (BmoVenue)getBmObject();
	}

	@Override
	public void create() {
		UiVenueForm uiVenueForm = new UiVenueForm(getUiParams(), 0);
		uiVenueForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiVenueForm uiVenueForm = new UiVenueForm(getUiParams(), bmObject.getId());
		uiVenueForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiVenueForm uiVenueForm = new UiVenueForm(getUiParams(), bmObject.getId());
		uiVenueForm.show();
	}

	public UiVenueForm getUiVenueForm() {
		uiVenueForm = new UiVenueForm(getUiParams(), 0);
		slave = true;
		return uiVenueForm;
	}

	public class UiVenueForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox streetTextBox = new TextBox();
		TextBox numberTextBox = new TextBox();
		TextBox neighborhoodTextBox = new TextBox();
		TextBox zipTextBox = new TextBox();
		TextBox cityIdTextBox = new TextBox();
		TextBox contactNameTextBox = new TextBox();
		TextBox contactPhoneTextBox = new TextBox();
		TextBox contactPhoneExtTextBox = new TextBox();
		TextBox contactPhone2TextBox = new TextBox();
		TextBox contactPhoneExt2TextBox = new TextBox();
		TextBox contactEmailTextBox = new TextBox();
		TextBox wwwTextBox = new TextBox();
		UiSuggestBox delegationSuggestBox = new UiSuggestBox(new BmoDelegation());
		UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());
		UiFileUploadBox logoFileUpload = new UiFileUploadBox(getUiParams());
		CheckBox	 homeAddressCheckBox = new CheckBox();
		TextBox bluePrinttTextBox = new TextBox();

		BmoVenue bmoVenue;

		String generalSection = "Datos Generales";
		String addressSection = "Dirección";

		final SimplePanel mapPanel = new SimplePanel();
//		final DialogBox dialogBox = new DialogBox(true);

		public UiVenueForm(UiParams uiParams, int id) {
			super(uiParams, new BmoVenue(), id);

//			dialogBox.hide();
			//			dblClick = new DblClickHandler() {
			//				@Override
			//				public void handle(MouseEvent event) {				
			//					latitudeTextBox.setText("" + event.getLatLng().lat());
			//					longitudeTextBox.setText("" + event.getLatLng().lng());
			//					dialogBox.hide();
			//				}
			//			};
			//			uiEosGMaps.setDblClickHandler(dblClick);

			//			markerDblClick = new Marker.DblClickHandler() {
			//				@Override
			//				public void handle(MouseEvent event) {				
			//					latitudeTextBox.setText("" + event.getLatLng().lat());
			//					longitudeTextBox.setText("" + event.getLatLng().lng());
			//					dialogBox.hide();
			//				}
			//			};

//			dialogBox.add(mapPanel);

//			dialogBox.setGlassEnabled(true);
			//			dialogBox.setAnimationEnabled(true);
//			dialogBox.setPixelSize(620, 420);
//			dialogBox.setText("SYMGF-Maps");
//			mapPanel.setSize("600px", "400px");
		}

		@Override
		public void populateFields(){
			bmoVenue = (BmoVenue)getBmObject();

			//			Button findButton = new Button("Buscar");
			//			findButton.setStyleName("formCloseButton");
			//			findButton.addClickHandler(new ClickHandler() {
			//				public void onClick(ClickEvent event) {
			//					prepareMap();
			//				}
			//			});

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoVenue.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoVenue.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoVenue.getDescription());
			formFlexTable.addField(5, 0, contactNameTextBox, bmoVenue.getContactName());
			formFlexTable.addField(6, 0, contactEmailTextBox, bmoVenue.getContactEmail());
			formFlexTable.addField(7, 0, contactPhoneTextBox, bmoVenue.getContactPhone());
			formFlexTable.addField(8, 0, contactPhoneExtTextBox, bmoVenue.getContactPhoneExt());
			formFlexTable.addField(9, 0, contactPhone2TextBox, bmoVenue.getContactPhone2());
			formFlexTable.addField(10, 0, contactPhoneExt2TextBox, bmoVenue.getContactPhoneExt2());
			formFlexTable.addField(11, 0, wwwTextBox, bmoVenue.getWww());
			formFlexTable.addField(12, 0, logoFileUpload,bmoVenue.getLogo());
			formFlexTable.addField(13, 0, bluePrinttTextBox,bmoVenue.getBluePrint());

			formFlexTable.addSectionLabel(14, 0, addressSection, 2);
			formFlexTable.addField(15, 0, homeAddressCheckBox, bmoVenue.getHomeAddress());
			formFlexTable.addField(16, 0, streetTextBox, bmoVenue.getStreet());
			formFlexTable.addField(17, 0, numberTextBox, bmoVenue.getNumber());
			formFlexTable.addField(18, 0, neighborhoodTextBox, bmoVenue.getNeighborhood());
			formFlexTable.addField(19, 0, zipTextBox, bmoVenue.getZip());
			formFlexTable.addField(20, 0, delegationSuggestBox, bmoVenue.getDelegationId());
			formFlexTable.addField(21, 0, citySuggestBox, bmoVenue.getCityId());

			formFlexTable.hideSection(addressSection);
			existhomeAddress(bmoVenue);
			
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoVenue.setId(id);
			bmoVenue.getCode().setValue(codeTextBox.getText());
			bmoVenue.getName().setValue(nameTextBox.getText());
			bmoVenue.getDescription().setValue(descriptionTextArea.getText());
			bmoVenue.getStreet().setValue(streetTextBox.getText());
			bmoVenue.getNumber().setValue(numberTextBox.getText());
			bmoVenue.getNeighborhood().setValue(neighborhoodTextBox.getText());
			bmoVenue.getZip().setValue(zipTextBox.getText());
			bmoVenue.getCityId().setValue(citySuggestBox.getSelectedId());
			bmoVenue.getWww().setValue(wwwTextBox.getText());
			bmoVenue.getContactName().setValue(contactNameTextBox.getText());
			bmoVenue.getContactEmail().setValue(contactEmailTextBox.getText());
			bmoVenue.getContactPhone().setValue(contactPhoneTextBox.getText());
			bmoVenue.getContactPhoneExt().setValue(contactPhoneExtTextBox.getText());
			bmoVenue.getContactPhone2().setValue(contactPhone2TextBox.getText());
			bmoVenue.getContactPhoneExt2().setValue(contactPhoneExt2TextBox.getText());
			bmoVenue.getLogo().setValue(logoFileUpload.getBlobKey());
			bmoVenue.getHomeAddress().setValue(homeAddressCheckBox.getValue());
			bmoVenue.getDelegationId().setValue(delegationSuggestBox.getSelectedId());
			bmoVenue.getBluePrint().setValue(bluePrinttTextBox.getText());			
			
			
			return bmoVenue;
		}
		public void existhomeAddress(BmoVenue bmoVenue ) {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-existhomeAddress() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.getId() > 0)
						homeAddressCheckBox.setEnabled(false);
					statusEffect();
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoVenue.getPmClass(),bmoVenue,
							BmoVenue.ACTION_EXITSHOMEADDRESS,"",callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + e.toString());
			}
		}

		@Override
		public void formBooleanChange(ValueChangeEvent<Boolean> event) {
			if (event.getSource() == homeAddressCheckBox)
				statusEffect();
		}

		private void statusEffect() {

			if (homeAddressCheckBox.getValue()) {
				streetTextBox.setEnabled(false);
				numberTextBox.setEnabled(false);
				neighborhoodTextBox.setEnabled(false);
				zipTextBox.setEnabled(false);
				citySuggestBox.setEnabled(false);
			} else {
				streetTextBox.setEnabled(true);
				numberTextBox.setEnabled(true);
				neighborhoodTextBox.setEnabled(true);
				zipTextBox.setEnabled(true);
				citySuggestBox.setEnabled(true);
			}
		}

		@Override
		public boolean validate(BmObject bObject) throws BmException {
			//throw new BoException("error de objeto de negocio.");
			// Validaciones especiales, en caso de error enviar una excepcion de objecto de negocio
			return true;
		}

		@Override
		public void close() {
			if (!slave) {
				UiVenue uiVenueList = new UiVenue(getUiParams());
				uiVenueList.show();
			}
		}

		//		public void prepareMap() {
		//			boolean showMap = false;
		//			if (latitudeTextBox.getText().equals("") || longitudeTextBox.getText().equals("")) {
		//				if (!streetTextBox.getText().equals("") 
		//						&& !numberTextBox.getText().equals("") 
		//						&& !neighborhoodTextBox.getText().equals("")
		//						&& !citySuggestBox.getText().equals("")
		//						) {
		//					uiEosGMaps.find(streetTextBox.getText(), 
		//							numberTextBox.getText(), 
		//							neighborhoodTextBox.getText(),
		//							bmoVenue.getBmoCity().getName().toString(), 
		//							bmoVenue.getBmoCity().getBmoState().getName().toString(),
		//							bmoVenue.getBmoCity().getBmoState().getBmoCountry().getName().toString());
		//					showMap = true;
		//				} else {
		//					showFormMsg("Error al buscar la dirección.", "Error al buscar la dirección. Establecer los datos de Coordenadas y/o de Dirección completos.");
		//				}
		//			} else {
		//				uiEosGMaps.find(latitudeTextBox.getText(), longitudeTextBox.getText());
		//				showMap = true;
		//			}
		//
		//			if (showMap) {
		//
		//				dialogBox.center();
		//				dialogBox.show();
		//
		//				uiEosGMaps.show();
		//			}
		//		}

//		public void hideDialogBox() {
//			dialogBox.hide();
//		}
	}
}