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

import java.util.ArrayList;
import java.util.Iterator;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListCheckBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiOrderList extends UiList {
	BmoOrder bmoOrder;
	Image extOrderImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/type_deposit.png"));	
	public UiOrderList(UiParams uiParams) {
		super(uiParams, new BmoOrder());
		bmoOrder = (BmoOrder)getBmObject();
		initialize();
	}

	public UiOrderList(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder) {
		super(uiParams, defaultPanel, new BmoOrder());
		this.bmoOrder = bmoOrder;
		newImage.setVisible(false);
		initialize();
	}

	public UiOrderList(UiParams uiParams,BmoOrder bmoOrder) {
		super(uiParams, new BmoOrder());
		this.bmoOrder = bmoOrder;
		extOrderImage.setTitle("Agregar Pedido Extra");
		extOrderImage.setStyleName("listSearchImage");
		extOrderImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					if (Window.confirm("Desea Crear un Pedido Extra?"))
					{	
						addExtraOrder();
					}
				}
			}
		});
		initialize();
	}

	public void initialize () {
		// Resetear valores para no guardar los filtros del Tablero de Finanzas. REVISAR como dejar los filtros SOLO en el listado del tablero
		// Resetar Filtros
		getUiParams().resetUiProgramParams(getBmObject().getProgramCode());
		// Resetear Ordenamiento
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setOrderList(getBmObject().getOrderFields());
		// Resetar Busqueda de texto
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setSearchText("");
	}

	@Override
	public void postShow() {
		if (isMaster() || isSlave()) {
			if (getUiParams().getCallerProgramCode() == new BmoProject().getProgramCode()) {			
				if(bmoOrder.getStatus().getValue().charAt(0) == BmoOrder.STATUS_AUTHORIZED || 
						bmoOrder.getStatus().getValue().charAt(0) == BmoOrder.STATUS_FINISHED) {
					newImage.setVisible(false);
					extOrderImage.setVisible(true);
					localButtonPanel.add(extOrderImage);
				}
				else {
					newImage.setVisible(false);
					extOrderImage.setVisible(false);
				}

			}
			if(getUiParams().getCallerProgramCode() == new BmoPropertyRental().getProgramCode()) {	
				newImage.setVisible(false);
				deleteImage.setVisible(false);
			}
			addFilterListBox(new UiListBox(getUiParams(), new BmoOrderType()), new BmoOrderType());
			addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoOrder.getCustomerId());

			if (!isMobile()) {
				// Filtrar por vendedores
				BmoUser bmoUser = new BmoUser();
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterSalesmen = new BmFilter();
				int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + salesGroupId);		
				addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoOrder.getUserId());

				addDateRangeFilterListBox(bmoOrder.getLockStart());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoOrder.getPaymentStatus()), bmoOrder, bmoOrder.getPaymentStatus());
			}
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoOrder.getStatus()), bmoOrder, bmoOrder.getStatus());

			if (getSFParams().isFieldEnabled(bmoOrder.getStatusDetail()))
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoOrder.getStatusDetail()), bmoOrder, bmoOrder.getStatusDetail());

			if (!isMobile()) 
				addTagFilterListBox(bmoOrder.getTags());

			if (!isMobile()) {
				if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUS)) {
					addActionBatchListBox(new UiListBox(getUiParams(), new BmoOrder().getStatus()), bmoOrder);
				}
			}
		}
	}
	private void prepareColumnHeader() {
		ArrayList<BmField> fields;

		if (isMobile()) 
			fields = getBmObject().getMobileFieldList();
		else 
			fields = getBmObject().getDisplayFieldList();

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

				// No ordena por imagen
				if (!(titleBmField.getFieldType() == BmFieldType.IMAGE) && !(titleBmField.getFieldType() == BmFieldType.FILEUPLOAD)) {
					titleLabel.addClickHandler(new UiClickHandler(titleBmField) {
						@Override
						public void onClick(ClickEvent event) {
//							addOrderField(bmField);
						}
					});
				}		
			}
		}
	}
	// Tipo de desplegado en columnas
		public void displayColumnList() {	
			listActionCheckBox.setValue(false);
			listFlexTable.resetListCheckBoxList();

			// Prepara encabezado de la lista
			prepareColumnHeader();

			int row = 1, col = 0, firstCol = 0;
			while (iterator.hasNext()) {
				BmObject cellBmObject = (BmObject)iterator.next(); 
				ArrayList<BmField> fields;

				if (isMobile()) 
					fields = cellBmObject.getMobileFieldList();
				else 
					fields = cellBmObject.getDisplayFieldList();

				col = 0;
				if (enableActionBar) {
					CheckBox checkBox = new CheckBox();
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							toggleActionPanel();
						}
					});
					UiListCheckBox uiListCheckBox = new UiListCheckBox(cellBmObject, checkBox);
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
							listFlexTable.addListCell(row, col++, cellBmObject, cellBmField);	
						}

						if (totalCols < col) totalCols = col;
					}
				}

				listFlexTable.formatRow(row);
				row++;
			}
			if (bmoOrder.getId() > 0 && !isMaster())
				existExtra();
		}
	@Override
	public void create() {
		UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), 0);
		uiOrderForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoOrder = (BmoOrder)bmObject;
		UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), bmoOrder.getId());
		uiOrderDetail.show();
	}

	public void existExtra() {
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {

			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-hasActiveOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.getId() > 0) {
					extOrderImage.setVisible(false);
				}
				else {
					extOrderImage.setVisible(true);
				}
			
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(),bmoOrder,
						BmoOrder.ACTION_GETEXTRAORDER,"",callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-hasActiveOrder() ERROR: " + e.toString());
		}
	}
	public void hasActiveOrder() {

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {

			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-hasActiveOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.getMsg().equals("0")) {
					extOrderImage.setVisible(true);	
				}
				else {
					extOrderImage.setVisible(false);
				}
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(),bmoOrder,
						BmoOrder.ACTION_EXTRAORDER,"",callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-hasActiveOrder() ERROR: " + e.toString());
		}
	}

	public void addExtraOrder() {

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-addExtraOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();

				if (!result.hasErrors()) {	
					UiOrderDetail uiOrderForm = new UiOrderDetail(getUiParams(),((BmoOrder)result.getBmObject()).getId());
					uiOrderForm.show();
				} 
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(),bmoOrder,
						BmoOrder.ACTION_EXTRAORDER,"",callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-addExtraOrder() ERROR: " + e.toString());
		}
	}

}
