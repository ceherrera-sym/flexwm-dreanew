package com.flexwm.client.wf;

import com.flexwm.shared.wf.BmoWFlowFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiFormatActionHandler;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;

import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.symgae.client.sf.UiEmailForm;
import com.symgae.client.ui.UiBmFieldClickHandler;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoEmail;


public class UiWFlowFormatDisplayList extends UiList {
	int foreignId;
	UiFormatActionHandler uiFormatAction;
	boolean publishValidateProcessing = false;
	BmoWFlowFormat bmoWFlowFormat;
	BmObject foreignBmObject = new BmObject();
	String defaultTo = "", defaultToName = "";
	int companyId;
	public UiWFlowFormatDisplayList(UiParams uiParams, Panel defaultPanel, BmObject foreignBmObject, int foreignId) {
		super(uiParams, defaultPanel, new BmoWFlowFormat());
		this.foreignBmObject = foreignBmObject;
		this.foreignId = foreignId;
		//		this.companyId = companyId;
	}
	// Muestra la lista
	public void show() {
		clearDP();

		// Determina el tamaño de la pagina
		if (isMinimalist())
			listPageSize = minimalistListPageSize;
		else
			listPageSize = getSFParams().getListPageSize();

		// Obtiene la pagina de los parametros si es Master
		if (isMaster()) {
			currentPage = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).getPage();
			if (currentPage == 0) 
				currentPage = 1;
		} else {
			currentPage = 1;
		}

		if (getSFParams().hasRead(getBmObject().getProgramCode())) {
			// Modo Maestro
			if (isMaster()) {	
				clearMT();
				getUiParams().getUiTemplate().hideWestPanel();
				getUiParams().getUiTemplate().hideEastPanel();
				getUiParams().getUiTemplate().hideProgramButtonPanel();
				getUiParams().getUiTemplate().hideProgramExtrasPanel();
				getUiParams().getUiTemplate().hideSouthPanel();

				// Se resetean filtros forzados cuando es modo MASTER, para tener solo parametros de la lista
				getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();

				// Si es mobil, quita titulo
				if (isMobile()) titleLabel.setText("");

				renderButtons();

				// Panel titulo
				HorizontalPanel hp = new HorizontalPanel();
				hp.setWidth("100%");
				hp.setStyleName("programTitlePanel");
				hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

				// Titulo
				HorizontalPanel titlePanel = new HorizontalPanel();
				titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				Image image = new Image("./icons/" + getBmObject().getProgramCode().toLowerCase() + ".png");
				image.setStyleName("programImage");
				titlePanel.add(image);
				titlePanel.add(new HTML("<pre> </pre>"));
				titlePanel.add(titleLabel);
				titlePanel.add(new HTML("<pre> </pre>"));
				titlePanel.add(new HTML("<pre> </pre>")); 
				hp.add(titlePanel);

				// Panel botones
				VerticalPanel buttonPanel = new VerticalPanel();
				buttonPanel.setStyleName("programLocalButtonPanel");
				buttonPanel.setWidth("100%");
				buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

				localButtonPanel.setStyleName("programLocalButtonPanel");
				localButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				localButtonPanel.add(newImage);
				localButtonPanel.add(searchTextBox);
				localButtonPanel.add(searchImage);
				localButtonPanel.add(allImage);
				localButtonPanel.add(printImage);
				if (getBmObjectBmoProgram().getEnableHelp().toBoolean())
					localButtonPanel.add(helpImage);
				if (getUiParams().getSFParams().hasWrite(getBmObjectBmoProgram().getProgramCode()))
					localButtonPanel.add(securityImage);
				localButtonPanel.add(new HTML("&nbsp;"));
				buttonPanel.add(localButtonPanel);
				hp.add(buttonPanel);

				getUiParams().getUiTemplate().getProgramTitlePanel().add(hp);

				addToDP(extrasPanel);
				addToDP(listFlexTable);
				addToDP(bottomPanel);

				getUiParams().getUiTemplate().getActionPopupPanel().show();
			} 
			// Modo esclavo
			else if (isSlave()) {

				// Se resetean filtros y busquedas cuando es modo SLAVE, para tener solo los nuevos parametros del MASTER
				getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setSearchText("");
				getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setPage(1);
				getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setFilterList(new ArrayList<BmFilter>());

				getUiParams().getUiTemplate().hideProgramButtonPanel();
				getUiParams().getUiTemplate().hideProgramExtrasPanel();

				renderButtons();

				getDP().setSize("100%", "100%");

				programSubTitlePanel.setSize("100%", "40px");
				programSubTitlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				programSubTitlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				programSubTitlePanel.setStyleName("programSubtitle");

				// Panel titulo
				HorizontalPanel hp = new HorizontalPanel();
				hp.setWidth("100%");
				hp.setStyleName("programTitlePanel");
				hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

				// Titulo
				HorizontalPanel titlePanel = new HorizontalPanel();
				titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				Image image = new Image("./icons/" + getBmObject().getProgramCode().toLowerCase() + ".png");
				image.setStyleName("programImage");
				titlePanel.add(image);
				titlePanel.add(new HTML("<pre> </pre>"));
				titlePanel.add(titleLabel);
				titlePanel.add(new HTML("<pre> </pre>"));
				titlePanel.add(new HTML("<pre> </pre>")); 
				hp.add(titlePanel);

				// Panel botones
				VerticalPanel buttonPanel = new VerticalPanel();
				buttonPanel.setStyleName("programLocalButtonPanel");
				buttonPanel.setWidth("100%");
				buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

				localButtonPanel.setStyleName("programLocalButtonPanel");
				localButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				localButtonPanel.add(newImage);
				localButtonPanel.add(searchTextBox);
				localButtonPanel.add(searchImage);
				localButtonPanel.add(allImage);
				localButtonPanel.add(printImage);
				if (getBmObjectBmoProgram().getEnableHelp().toBoolean())
					localButtonPanel.add(helpImage);
				if (getUiParams().getSFParams().hasWrite(getBmObjectBmoProgram().getProgramCode()))
					localButtonPanel.add(securityImage);
				localButtonPanel.add(new HTML("&nbsp;"));
				buttonPanel.add(localButtonPanel);
				hp.add(buttonPanel);

				programSubTitlePanel.add(hp);

				addToDP(programSubTitlePanel);
				addToDP(extrasPanel);
				addToDP(listFlexTable);
				addToDP(bottomPanel);
				addToDP(new HTML("<pre> </pre>"));

			} 
			// Modo minimalista
			else if (isMinimalist()) {
				// Se resetean filtros cuando es modo MINIMALIST, para tener solo los nuevos parametros del MASTER
				getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setSearchText("");
				getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setPage(1);
				//					getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setFilterList(new ArrayList<BmFilter>());

				//getDP().setSize("100%", "100%");

				programSubTitlePanel.setSize("100%", "40px");
				programSubTitlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				programSubTitlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				programSubTitlePanel.setStyleName("programSubtitle");

				titleLabel.setStyleName("programMinimalistSubtitle");
				titleLabel.addClickHandler(new UiBmFieldClickHandler() {
					public void onClick(ClickEvent event) {
						//							toggleProgram();
					}
				});

				// Panel titulo
				HorizontalPanel hp = new HorizontalPanel();
				hp.setWidth("100%");
				hp.setStyleName("programTitlePanel");
				hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

				// Titulo
				HorizontalPanel titlePanel = new HorizontalPanel();
				titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				Image image = new Image("./icons/" + getBmObject().getProgramCode().toLowerCase() + ".png");
				image.setStyleName("programImage");
				titlePanel.add(image);
				titlePanel.add(titleLabel);
				titlePanel.add(toggleImage);
				titlePanel.add(new HTML("<pre> </pre>")); 
				hp.add(titlePanel);

				// Panel botones
				VerticalPanel buttonPanel = new VerticalPanel();
				buttonPanel.setStyleName("programLocalButtonPanel");
				buttonPanel.setWidth("100%");
				buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

				localButtonPanel.setStyleName("programLocalButtonPanel");
				localButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				localButtonPanel.add(newImage);
				localButtonPanel.add(searchTextBox);
				localButtonPanel.add(searchImage);
				localButtonPanel.add(allImage);
				if (getBmObjectBmoProgram().getEnableHelp().toBoolean())
					localButtonPanel.add(helpImage);
				if (getUiParams().getSFParams().hasWrite(getBmObjectBmoProgram().getProgramCode()))
					localButtonPanel.add(securityImage);
				localButtonPanel.add(extrasPanel);
				localButtonPanel.add(new HTML("&nbsp;"));
				buttonPanel.add(localButtonPanel);
				hp.add(buttonPanel);

				programSubTitlePanel.add(hp);

				minimalistPanel.setSize("100%", "100%");
				minimalistPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				minimalistPanel.setStyleName("programMinimalistPanel");
				minimalistPanel.add(programSubTitlePanel);
				minimalistPanel.add(listFlexTable);
				minimalistPanel.add(bottomPanel);
			} 

			showList();
		} else {
			showErrorMessage("No tiene permisos para el modulo " + getBmObject().getProgramCode());
		}
	}
	public UiWFlowFormatDisplayList(UiParams uiParams, Panel defaultPanel, BmObject foreignBmObject, int foreignId, UiFormatActionHandler uiFormatAction,int companyId) {
		super(uiParams, defaultPanel, new BmoWFlowFormat());
		this.foreignBmObject = foreignBmObject;
		this.foreignId = foreignId;
		this.uiFormatAction = uiFormatAction;
		this.companyId = companyId;
	}



	@Override
	public void postShow() {
		newImage.setVisible(false);
		allImage.setVisible(false);
	}


	@Override
	public void displayList() {
		BmoWFlowFormat bmoWFlowFormat = new BmoWFlowFormat();

		int col = 0;
		listFlexTable.addListTitleCell(0, col++, bmoWFlowFormat.getCode().getLabel());
		listFlexTable.addListTitleCell(0, col++, bmoWFlowFormat.getName().getLabel());		
		listFlexTable.addListTitleCell(0, col++, bmoWFlowFormat.getDescription().getLabel());	
		listFlexTable.addListTitleCell(0, col++, "Visualizar");

		int row = 1;
		while (iterator.hasNext()) {
			bmoWFlowFormat = (BmoWFlowFormat)iterator.next();
			setData(row,bmoWFlowFormat,false);
			row++;

		}
	}

	public void setData(int row, BmoWFlowFormat bmoWFlowFormat,boolean extra) {
		int col = 0;
		listFlexTable.addListCell(row, col++, bmoWFlowFormat, bmoWFlowFormat.getCode());
		listFlexTable.addListCell(row, col++, bmoWFlowFormat, bmoWFlowFormat.getName());	
		listFlexTable.addListCell(row, col++, bmoWFlowFormat, bmoWFlowFormat.getDescription());
		// Actualiza info del link para asignar el foreignId
		BmField linkField = new BmField(bmoWFlowFormat.getLink());
		String linkString = bmoWFlowFormat.getLink().toString();

		try {
			linkField.setValue(linkString + "?foreignId=" + foreignId);
		} catch (BmException e) {
			Window.alert("Error: " + e.toString());
		}

		HorizontalPanel hp = new HorizontalPanel();
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		Image icon = new Image("./icons/format.png");
		icon.setTitle("Visualizar Formato.");

		if (uiFormatAction != null) {
			// Esta configurada una accion, activarla
			icon.addClickHandler(new UiBmFieldClickHandler(bmoWFlowFormat, bmoWFlowFormat.getLink()) {
				@Override
				public void onClick(ClickEvent event) {
					//						uiFormatAction.view(bmObject);
					canPrintAction((BmoWFlowFormat)bmObject);

				}
			});


		} else {

			// Si no hay accion establecida, abrir formato en nuevo jsp
			icon.addClickHandler(new UiBmFieldClickHandler(bmoWFlowFormat, linkField) {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(GwtUtil.getProperUrl(getSFParams(), bmField.toString()), "_blank", "");
				}
			});
		}
		hp.add(icon);

		Image email = new Image("./icons/send.png");
		email.setTitle("Enviar Formato por Email.");
		if (uiFormatAction != null) {
			email.addClickHandler(new UiBmFieldClickHandler(bmoWFlowFormat, bmoWFlowFormat.getLink()) {
				@Override
				public void onClick(ClickEvent event) {
					canSendAction((BmoWFlowFormat)bmObject);
				}
			});
			hp.add(email);
		} else {
			email.addClickHandler(new UiBmFieldClickHandler(bmoWFlowFormat, bmoWFlowFormat.getLink()) {
				@Override
				public void onClick(ClickEvent event) {
					defaultSendEmail((BmoWFlowFormat)bmObject);
				}
			});
			hp.add(email);
		}

		listFlexTable.setWidget(row, col, hp);
		listFlexTable.getCellFormatter().setWidth(row,  col,  "50px");
		listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellLink");

		listFlexTable.formatRow(row);


	}
	public void defaultSendEmail(BmoWFlowFormat bmoWFlowFormat) {
		BmoEmail bmoEmail = new BmoEmail();

		try {
			bmoEmail.getTo().setValue(defaultTo);
			bmoEmail.getToName().setValue(defaultToName);
			bmoEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoEmail.getSubject().setValue(getUiParams().getSFParams().getMainAppTitle() + ": #" + getUiParams().getSFParams().getAppCode() + " -  " + bmoWFlowFormat.getName().toString());
			bmoEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoEmail.getBody().setValue(""
					+ "	"
					+ " Se envía el Documento [" + bmoWFlowFormat.getName().toString() + "] "
					+ " de " + foreignBmObject.getProgramLabel() + ": " + foreignBmObject.listBoxFieldsToString()
					);
			bmoEmail.getFixedBody().setValue("<br><br>Para revisar el documento, haz click <a href=\"" + getUiParams().getSFParams().getAppURL() + bmoWFlowFormat.getLink().toString() + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(foreignId) + "&resource=bo" + (new Date().getTime() * 456) +"\">Aqui</a>");
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}

		UiEmailForm uiEmailForm = new UiEmailForm(getUiParams(), 0, bmoEmail);
		getUiParams().setUiType(new BmoEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiEmailForm.show();
	}

	public void canSendAction(BmoWFlowFormat bmoFormatSelected) {
		this.bmoWFlowFormat = bmoFormatSelected;

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-canSendAction() ERROR: " + caught.toString());
				publishValidateProcessing = false;
			}

			public void onSuccess(BmUpdateResult result) {
				publishValidateProcessing = false;
				if (result.hasErrors()) {
					showErrorMessage("No se puede Enviar el Formato: " + result.errorsToString());
				} else {
					uiFormatAction.send(bmoWFlowFormat);
				}

			}
		};

		// Llamada al servicio RPC
		try {
			publishValidateProcessing = true;
			getUiParams().getBmObjectServiceAsync().action(bmoWFlowFormat.getPmClass(), bmoWFlowFormat, BmoWFlowFormat.ACTION_PUBLISHVALIDATE, "" + foreignId, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-canSendAction() ERROR: " + e.toString());
		}
	}

	public void canPrintAction(BmoWFlowFormat bmoFormatSelected) {
		this.bmoWFlowFormat = bmoFormatSelected;

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-canPrintAction() ERROR: " + caught.toString());
				publishValidateProcessing = false;
			}

			public void onSuccess(BmUpdateResult result) {
				publishValidateProcessing = false;
				if (result.hasErrors()) {
					showErrorMessage("No se puede Publicar el Formato: " + result.errorsToString());
				} else {
					uiFormatAction.view(bmoWFlowFormat);
				}

			}
		};

		// Llamada al servicio RPC
		try {
			publishValidateProcessing = true;
			getUiParams().getBmObjectServiceAsync().action(bmoWFlowFormat.getPmClass(), bmoWFlowFormat, BmoWFlowFormat.ACTION_PUBLISHVALIDATE, "" + foreignId, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-canPrintAction() ERROR: " + e.toString());
		}
	}

}
