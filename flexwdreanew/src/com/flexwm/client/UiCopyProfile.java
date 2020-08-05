package com.flexwm.client;



import java.sql.Types;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoProfile;

public class UiCopyProfile extends UiList {
	BmoProfile bmoProfile = new BmoProfile();
	private Image copy = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/copy.png"));
	private DialogBox copyDialogBox;
	private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
	private UiTextBox nameTextBox = new UiTextBox();
    private Button copyButton = new Button("Copiar");
    private Button closeButton = new Button("Cancelar");
    
	public UiCopyProfile(UiParams uiParams) {
		super(uiParams, new BmoProfile());
		
		copy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("Copiar Perfiles?")) {
					copyprofiles();
				}
			}
		});
		
		closeButton.setStyleName("formCloseButton");
		closeButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				copyDialogBox.hide();
			}
		});
		
		
	}

	@Override
	public void postShow() {
		titleLabel.setText("Copiar Perfil");
		printImage.setVisible(false);
		securityImage.setVisible(false);
		newImage.setVisible(false);
		copy.setStyleName("listSearchImage");
		copy.setTitle("Copiar Perfil");
		actionItems.add(copy);
		deleteImage.setVisible(false);


	}
	@Override
	protected void open(BmObject bmObject) {
		if (Window.confirm("Copiar Perfil?")) {
			openCopyDialogBox(bmObject);
		}else {
			list();
		}
	}
	public void openCopyDialogBox(BmObject bmObject) {
		copyDialogBox = new DialogBox(true);
		copyDialogBox.setGlassEnabled(true);
		copyDialogBox.setText("Copiar Pelfil");
		copyDialogBox.setSize("350px", "150px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("350px", "150px");
		copyDialogBox.setWidget(vp);
		BmField nameField = new BmField("namefield", "", "Nombre nuevo Perfil ", 30, Types.VARCHAR, false,
				BmFieldType.STRING, false);

		copyButton.setStyleName("formSaveButton");
		copyButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				copyprofile(bmObject);
				
			}
		});
		
		formTable.addField(1, 0, nameTextBox, nameField);
		

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(closeButton);
		buttonPanel.add(copyButton);

		vp.add(formTable);
		vp.add(buttonPanel);

		copyDialogBox.center();
		copyDialogBox.show();
	}
	
	
	private void copyprofile(BmObject bmObject) {
		if(nameTextBox.getText().matches("[a-z].*") || nameTextBox.getText().matches("[A-Z].*") || 
				nameTextBox.getText().matches("[0-9]*") ){	
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(""+ caught.getMessage());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					showList();
					copyDialogBox.hide();
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action("com.flexwm.server.PmProfileCopy", bmObject, "", nameTextBox.getText(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}	
		}else {
			showErrorMessage("Nombre invalido");
		}
	}
	private void copyprofiles() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				
				stopLoading();
				showErrorMessage(""+ caught.getMessage());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				showList();
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().actionBatch("com.flexwm.server.PmProfileCopy", listFlexTable.getListCheckBoxSelectedBmObjectList(), "", new BmField(), "", callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
		}	
	}

}
