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

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListCheckBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;

public class UiProjectActiveList extends UiList {
	BmoProject bmoProject;
	UiSuggestBox userSuggestBox;
	BmoUser bmoUser = new BmoUser();
	protected BmFilter userFilter = new BmFilter();

	public UiProjectActiveList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoProject());
		bmoProject = (BmoProject)getBmObject();	

		// Filtrar por proyectos no terminadas al 100%
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoProject.getKind(), 
				bmoProject.getStatus().getName(),
				"" + BmoProject.STATUS_REVISION);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(bmFilter);
	}

	public UiProjectActiveList(UiParams uiParams, Panel defaultPanel, BmoUser bmoUser) {
		super(uiParams, defaultPanel, new BmoProject());
		bmoProject = (BmoProject)getBmObject();	

		this.bmoUser = bmoUser;	

		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activos");

		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();
	}

	@Override
	public void postShow() {		
		// Filtro de Proyectos En Revision
		BmFilter revisionFilter = new BmFilter();
		revisionFilter.setValueFilter(bmoProject.getKind(), 
				bmoProject.getStatus(), 
				"" + BmoProject.STATUS_REVISION);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(revisionFilter);


		//		// Preparar filtro default de usuario loggeado
		//		userSuggestBox = new UiSuggestBox(new BmoUser());
		//
		//		BmoUser bmoUser = new BmoUser();
		//		BmFilter bmFilter = new BmFilter();
		//		bmFilter.setValueLabelFilter(bmoProject.getKind(), 
		//				bmoProject.getUserId().getName(), 
		//				bmoProject.getUserId().getLabel(),
		//				"=",
		//				"" + getSFParams().getLoginInfo().getUserId(),
		//				getSFParams().getLoginInfo().getEmailAddress());
		//		getUiParams().getUiProgramParams(bmoProject.getProgramCode()).addFilter(bmFilter);
		//		
		//		// Filtrar por vendedores
		//		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		//		BmFilter filterSalesmen = new BmFilter();
		//		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		//		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
		//								bmoUser.getIdFieldName(),
		//								bmoProfileUser.getUserId().getName(),
		//								bmoProfileUser.getProfileId().getName(),
		//								"" + salesGroupId);		
		//		
		//		userSuggestBox.addFilter(filterSalesmen);
		//
		//		// Filtrar por vendedores activos
		//		BmFilter filterSalesmenActive = new BmFilter();
		//		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		//		userSuggestBox.addFilter(filterSalesmenActive);
		//		
		//		addFilterSuggestBox(userSuggestBox, bmoUser, bmoProject.getUserId());


		if (bmoUser.getId() > 0) {
			userFilter.setValueLabelFilter(bmoProject.getKind(), 
					bmoProject.getUserId().getName(), 
					bmoProject.getUserId().getLabel(), 
					BmFilter.EQUALS,
					bmoUser.getIdField().toString(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		}
	}

	@Override
	public void create() {
		UiProject uiProjectForm = new UiProject(getUiParams());
		getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MASTER);
		uiProjectForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoProject = (BmoProject)bmObject;
		getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MASTER);
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoProject.getWFlowTypeId().toInteger() > 0) {
			UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), bmoProject.getId());
			uiProjectDetail.show();
		} else {
			UiProject uiProjectForm = new UiProject(getUiParams());
			uiProjectForm.edit(bmoProject);
		}
	}

	@Override
	public void displayColumnList() {
		int row = 1, col = 0, firstCol = 0;
		listActionCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				listFlexTable.listCheckBoxTooggle(event.getValue());
				toggleActionPanelActive();
			}
		});
		listActionCheckBox.setValue(false);
		listFlexTable.resetListCheckBoxList();
		prepareColumnHeaderActive();
		while (iterator.hasNext()) {
			BmoProject nextBmoProject = (BmoProject)iterator.next();
			ArrayList<BmField> fields = new ArrayList<BmField>();
			if (isMobile()) 
				fields = nextBmoProject.getMobileFieldList();
			else {
				fields.add(nextBmoProject.getCode());
				fields.add(nextBmoProject.getName());
				fields.add(nextBmoProject.getBmoVenue().getName());
				
				fields.add(nextBmoProject.getStartDate());
				fields.add(nextBmoProject.getBmoCustomer().getCode());
				fields.add(nextBmoProject.getBmoCustomer().getDisplayName());
				fields.add(nextBmoProject.getBmoWFlow().getProgress());
				fields.add(nextBmoProject.getBmoWFlow().getBmoWFlowPhase().getName());
				fields.add(nextBmoProject.getTotal());
			}
			col = 0;
			if (enableActionBar) {
				CheckBox checkBox = new CheckBox();
				checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						toggleActionPanelActive();
					}
				});
				UiListCheckBox uiListCheckBox = new UiListCheckBox(nextBmoProject, checkBox);
				listFlexTable.addListCheckBox(uiListCheckBox);
				listFlexTable.setWidget(row, 0, uiListCheckBox.getCheckBox());
				listFlexTable.setWidget(row, 0, uiListCheckBox.getCheckBox());
				listFlexTable.getCellFormatter().addStyleName(row, 0, "listFirstColumn");
				col++;
			}
			firstCol = col;
			Iterator<BmField> itf = fields.iterator();
			while (itf.hasNext()) {
				BmField cellBmField = (BmField)itf.next();
				if (!cellBmField.isInternal()) {

					if (col == firstCol) {
						String linkValue = cellBmField.getValue();
						if (linkValue == null || linkValue.equalsIgnoreCase(""))
							linkValue = "Editar";
						Label linkLabel = new Label(linkValue);
						linkLabel.addClickHandler(rowClickHandler);
						linkLabel.setStyleName("listCellLink");
						listFlexTable.setWidget(row, col++, linkLabel);
					} else {
						listFlexTable.addListCell(row, col++, nextBmoProject, cellBmField);	
					}

					if (totalCols < col) totalCols = col;
				}
			}
			listFlexTable.formatRow(row);
			row++;
		}
		getSumTotal(row);
	}
	// Muestra los botones de accion 
	public void toggleActionPanelActive() {
		// Lista de tipo columnas
		if (listFlexTable.hasSelectedListCheckBox()) {
			listFlexTable.removeRow(0);
			listFlexTable.insertRow(0);
			prepareRowHeaderActive();
			actionPanel.setVisible(true);
		} else {
			listFlexTable.removeRow(0);
			listFlexTable.insertRow(0);
			prepareColumnHeaderActive();
		}
	}

	// Agrega el header de columnas 
	private void prepareRowHeaderActive() {
		listFlexTable.setWidget(0, 0, listActionCheckBox);
		listFlexTable.getCellFormatter().addStyleName(0, 0, "listHeaderFirstColumn");

		listFlexTable.addListTitleCell(0, 1, getSFParams().getProgramTitle(getBmObject()));

		listFlexTable.addListTitleCell(0, 2, " ");
		listFlexTable.getCellFormatter().addStyleName(0, 2, "listLastColumn");
		listFlexTable.getFlexCellFormatter().setColSpan(0, 2, totalCols);
		listFlexTable.setWidget(0, 2, actionPanel);
	}
	private void prepareColumnHeaderActive() {
		ArrayList<BmField> fields = new ArrayList<BmField>();

		fields.add(bmoProject.getCode());
		fields.add(bmoProject.getName());
		fields.add(bmoProject.getBmoVenue().getName());
		bmoProject.getStartDate().setLabel("Día del Evento");
		fields.add(bmoProject.getStartDate());
		fields.add(bmoProject.getBmoCustomer().getCode());
		fields.add(bmoProject.getBmoCustomer().getDisplayName());
		fields.add(bmoProject.getBmoWFlow().getProgress());
		fields.add(bmoProject.getBmoWFlow().getBmoWFlowPhase().getName());
		fields.add(bmoProject.getTotal());
		Iterator<BmField> it = fields.iterator();
		int col = 0;
		if (enableActionBar) {
			listFlexTable.setWidget(0, 0, listActionCheckBox);
			listFlexTable.getCellFormatter().addStyleName(0, 0, "listHeaderFirstColumn");
			listFlexTable.getCellFormatter().addStyleName(0, col++, "listHeaderFirstColumn");
		}
		while (it.hasNext()) {
			BmField titleBmField = (BmField)it.next();
			if (!titleBmField.isInternal()) {
				boolean existingOrder = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).hasOrder(getBmObject().getKind(), titleBmField.getName());
				String suffix = "";

				if (existingOrder) {
					BmOrder currentOrder = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).searchOrder(getBmObject().getKind(), titleBmField.getName());
					if (currentOrder.getOrder().equalsIgnoreCase(BmOrder.ASC)) {
						suffix = " >";
					} else {
						suffix = " <";
					}
				}

				Label titleLabel = new Label();
				listFlexTable.addListTitleCell(0, col++, getBmObject().getProgramCode(), titleBmField, titleLabel, existingOrder);

				titleLabel.setText(titleLabel.getText() + suffix);

				if (!(titleBmField.getFieldType() == BmFieldType.IMAGE) && !(titleBmField.getFieldType() == BmFieldType.FILEUPLOAD)) {
					titleLabel.addClickHandler(new UiClickHandler(titleBmField) {
						@Override
						public void onClick(ClickEvent event) {
							addOrderField(bmField);
						}
					});
				}		
			}
		}
	}
	//Asigna campos de ordenamiento
	private void addOrderField(BmField bmField) {
		ArrayList<BmOrder> orderList = new ArrayList<BmOrder>();

		// Revisa si ya esta listado por ese, y cambia el ordenamiento
		BmOrder currentOrder = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).searchOrder(getBmObject().getKind(), bmField.getName());

		if (currentOrder == null || currentOrder.getKind() == null) {
			BmOrder bmOrder = new BmOrder(getBmObject().getKind(), bmField.getName(), bmField.getLabel(), BmOrder.ASC);
			orderList.add(bmOrder);
		} else {
			if (currentOrder.getOrder().equalsIgnoreCase(BmOrder.ASC)) {
				currentOrder.setOrder(BmOrder.DESC);
			} else {
				currentOrder.setOrder(BmOrder.ASC);
			}
			orderList.add(currentOrder);
		}

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setOrderList(orderList);
		list();
	}
	private void getSumTotal(int row) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getSumTotal() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				listFlexTable.addListCell(row,8, "Sum. Totales");
				listFlexTable.getCellFormatter().addStyleName(row, 8, "listCellCode");
				BmField sumTotal = new BmField("sumtotal", result.getMsg(), "Suma Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
				listFlexTable.addListCell(row, 9, new BmoOpportunity(), sumTotal);	
				listFlexTable.getCellFormatter().addStyleName(row, 9, "listCellCurrency");
				
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoProject.getPmClass(), bmoProject, BmoProject.ACTION_GETSUMTOTAL, ""+bmoUser.getId() , callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-sendPollAction() ERROR: " + e.toString());
		}
	}
	//	@Override
	//	public void displayList() {
	//		int col = 0;
	//		
	//		listFlexTable.addListTitleCell(0, col++, "Abrir");
	//		listFlexTable.addListTitleCell(0, col++, "Clave");
	//		listFlexTable.addListTitleCell(0, col++, "Nombre");
	//		listFlexTable.addListTitleCell(0, col++, "Cliente");
	//		listFlexTable.addListTitleCell(0, col++, "Fase");
	//		listFlexTable.addListTitleCell(0, col++, "Avance");
	//		
	//		int row = 1;
	//		while (iterator.hasNext()) {
	//
	//		    BmoProject cellBmObject = (BmoProject)iterator.next(); 
	//		    
	//			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
	//			clickImage.setTitle("Abrir registro.");
	//			clickImage.addClickHandler(rowClickHandler);
	//			listFlexTable.setWidget(row, 0, clickImage);
	//			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
	//		    
	//		    col = 1;
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getName());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoCustomer().getDisplayName());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoWFlow().getBmoWFlowPhase().getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoWFlow().getProgress());
	//			listFlexTable.formatRow(row);
	//			row++;
	//		}
	//	}
}


