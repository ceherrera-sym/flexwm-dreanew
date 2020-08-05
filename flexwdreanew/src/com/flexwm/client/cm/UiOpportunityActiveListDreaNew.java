package com.flexwm.client.cm;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import com.flexwm.shared.cm.BmoOpportunity;
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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;

public class UiOpportunityActiveListDreaNew  extends UiList {
	BmoOpportunity bmoOpportunity;
	UiSuggestBox userSuggestBox;
	protected BmFilter userFilter = new BmFilter();

	BmoUser bmoUser = new BmoUser();
	public UiOpportunityActiveListDreaNew(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoOpportunity());
		bmoOpportunity = (BmoOpportunity)getBmObject();	

		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activas");
	}
	public UiOpportunityActiveListDreaNew(UiParams uiParams, Panel defaultPanel, BmoUser bmoUser) {
		super(uiParams, defaultPanel, new BmoOpportunity());
		bmoOpportunity = (BmoOpportunity)getBmObject();	

		this.bmoUser = bmoUser;	

		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activas");

		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();
	}
	@Override
	public void postShow() {
		// Filtro de Ventas En Revision
		BmFilter revisionFilter = new BmFilter();
		revisionFilter.setValueFilter(bmoOpportunity.getKind(), 
				bmoOpportunity.getStatus(), 
				"" + BmoOpportunity.STATUS_REVISION);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(revisionFilter);

		if (bmoUser.getId() > 0) {
			userFilter.setValueLabelFilter(bmoOpportunity.getKind(), 
					bmoOpportunity.getUserId().getName(), 
					bmoOpportunity.getUserId().getLabel(), 
					BmFilter.EQUALS,
					bmoUser.getIdField().toString(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		}
	}
	@Override
	public void create() {
		UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
		getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
		uiOpportunity.create();
	}
	@Override
	public void open(BmObject bmObject) {
		bmoOpportunity = (BmoOpportunity)bmObject;
		getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
			UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoOpportunity.getId());
			uiOpportunityDetail.show();
		} else {
			UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
			uiOpportunity.edit(bmoOpportunity);
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
			BmoOpportunity nextBmoOpportunity = (BmoOpportunity)iterator.next(); 
			ArrayList<BmField> fields = new ArrayList<BmField>();
			if (isMobile()) 
				fields = nextBmoOpportunity.getMobileFieldList();
			else {
				fields.add(nextBmoOpportunity.getCode());
				fields.add(nextBmoOpportunity.getName());
				fields.add(nextBmoOpportunity.getBmoVenue().getName());
				fields.add(nextBmoOpportunity.getStartDate());
				fields.add(nextBmoOpportunity.getBmoCustomer().getCode());
				fields.add(nextBmoOpportunity.getBmoCustomer().getDisplayName());
				fields.add(nextBmoOpportunity.getBmoWFlow().getProgress());
				fields.add(nextBmoOpportunity.getExpireDate());
				fields.add(nextBmoOpportunity.getBmoWFlow().getBmoWFlowPhase().getName());
				fields.add(nextBmoOpportunity.getAmount());

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
				UiListCheckBox uiListCheckBox = new UiListCheckBox(nextBmoOpportunity, checkBox);
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
						listFlexTable.addListCell(row, col++, nextBmoOpportunity, cellBmField);	
					}

					if (totalCols < col) totalCols = col;
				}
			}
			listFlexTable.formatRow(row);
			row++;
		}
		getSumTotal(row);
		
//		listFlexTable.getFlexCellFormatter().setColSpan(row, 8, col);
	}
	// leer el total de el listado de oportunidades
	private void getSumTotal(int row) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getSumTotal() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				listFlexTable.addListCell(row,9, "Sum. Totales");
				listFlexTable.getCellFormatter().addStyleName(row, 9, "listCellCode");
				BmField sumTotal = new BmField("sumtotal", result.getMsg(), "Suma Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
				listFlexTable.addListCell(row, 10, new BmoOpportunity(), sumTotal);	
				listFlexTable.getCellFormatter().addStyleName(row, 10, "listCellCurrency");
				
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOpportunity.getPmClass(), bmoOpportunity, BmoOpportunity.ACTION_GETSUMTOTAL, ""+bmoUser.getId() , callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-sendPollAction() ERROR: " + e.toString());
		}
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

		fields.add(bmoOpportunity.getCode());
		fields.add(bmoOpportunity.getName());
		fields.add(bmoOpportunity.getBmoVenue().getName());
		bmoOpportunity.getStartDate().setLabel("Creación Op");
		fields.add(bmoOpportunity.getStartDate());
		fields.add(bmoOpportunity.getBmoCustomer().getCode());
		fields.add(bmoOpportunity.getBmoCustomer().getDisplayName());
		fields.add(bmoOpportunity.getBmoWFlow().getProgress());
		fields.add(bmoOpportunity.getExpireDate());
		fields.add(bmoOpportunity.getBmoWFlow().getBmoWFlowPhase().getName());
		fields.add(bmoOpportunity.getAmount());

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
	// Asigna campos de ordenamiento
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

}
