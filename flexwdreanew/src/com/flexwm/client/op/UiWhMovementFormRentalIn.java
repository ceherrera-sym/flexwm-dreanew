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

import com.flexwm.client.op.UiWhMovement;
import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiWhMovementFormRentalIn extends UiFormDialog {	
	private FlowPanel whMovItemPanel = new FlowPanel();
	private CellTable<BmObject> whMovItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	protected DialogBox whMovItemDialogBox;
	protected DialogBox whBoxDialogBox;
	private UiSuggestBox toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());

	String itemsSection = "Items";
	String statusSection = "Estatus";

	BmoWhSection bmoWhSection = new BmoWhSection();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	BmoWhMovement bmoWhMovement;

	public UiWhMovementFormRentalIn(UiParams uiParams, int id) {
		super(uiParams, new BmoWhMovement(), id);

		// Movimiento de altura del grid
		whMovItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 

		// Grid de items
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWhMovItem.getKind(), bmoWhMovItem.getWhMovementId().getName(), id);
		data = new UiListDataProvider<BmObject>(new BmoWhMovItem(), bmFilter);

		whMovItemGrid.setWidth("100%");
		whMovItemPanel.setWidth("100%");
		setColumns();

		data.addDataDisplay(whMovItemGrid);
		whMovItemPanel.add(whMovItemGrid);
	}

	@Override
	public void populateFields() {
		bmoWhMovement = (BmoWhMovement)getBmObject();

		formFlexTable.addLabelField(1, 0, bmoWhMovement.getCode());
		formFlexTable.addLabelField(2, 0, bmoWhMovement.getType());
		formFlexTable.addLabelField(3, 0, bmoWhMovement.getBmoOrderDelivery().getCode());
		formFlexTable.addLabelField(4, 0, bmoWhMovement.getBmoOrderDelivery().getName());
		formFlexTable.addLabelField(5, 0, bmoWhMovement.getDatemov());
		formFlexTable.addLabelField(6, 0, bmoWhMovement.getDescription());
		formFlexTable.addLabelField(7, 0, bmoWhMovement.getCompanyId().getLabel(), bmoWhMovement.getBmoCompany().getName().toString());
		formFlexTable.addField(8, 0, toWhSectionSuggestBox, bmoWhMovement.getToWhSectionId());		

		formFlexTable.addSectionLabel(9, 0, itemsSection, 2);
		formFlexTable.addPanel(10, 0, whMovItemPanel, 2);

		formFlexTable.addSectionLabel(11, 0, statusSection, 2);
		formFlexTable.addLabelField(12, 0, bmoWhMovement.getStatus());
	}

	@Override
	public void postShow() {
		saveButton.setVisible(false);
		deleteButton.setVisible(false);
		toWhSectionSuggestBox.setEnabled(false);
	}

	public void changeHeight() {
		whMovItemGrid.setVisibleRange(0, data.getList().size());
	}

	public void setColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getQuantity().toString();
			}
		};
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		whMovItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant"));

		if (!isMobile()) {
			// Clave
			Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoProduct().getCode().toString();
				}
			};
			whMovItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
			//		whMovItemGrid.setColumnWidth(codeColumn, 100, Unit.PX);
		}

		// Nombre
		Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getName().toString();
			}
		};
		whMovItemGrid.addColumn(prodColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		//		whMovItemGrid.setColumnWidth(prodColumn, 100, Unit.PX);

		if (!isMobile()) {
			// Modelos
			Column<BmObject, String> modelColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoProduct().getModel().toString();
				}
			};
			whMovItemGrid.addColumn(modelColumn, SafeHtmlUtils.fromSafeConstant("Modelo"));
			//		whMovItemGrid.setColumnWidth(prodColumn, 100, Unit.PX);
		}

		// Tipo producto
		Column<BmObject, String> trackColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getTrack().getSelectedOption().getLabel();
			}
		};
		whMovItemGrid.addColumn(trackColumn, SafeHtmlUtils.fromSafeConstant("Rastreo"));

		// #Serie
		Column<BmObject, String> serialColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getSerial().toString();
			}
		};
		serialColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		whMovItemGrid.addColumn(serialColumn, SafeHtmlUtils.fromSafeConstant("# Serie"));
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWhMovement = (BmoWhMovement)getBmObject();
		bmoWhMovement.getAmount().setValue(0);	
		return bmoWhMovement;
	}

	@Override
	public void close() {
		UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
		uiWhMovementList.show();
	}

	@Override
	public void saveNext() {
		if (newRecord) { 
			UiWhMovementFormRentalIn uiWhMovementForm = new UiWhMovementFormRentalIn(getUiParams(), getBmObject().getId());
			uiWhMovementForm.show();
		} else {
			UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
			uiWhMovementList.show();
		}		
	}

	public void addWhBox() {
		addWhBox(new BmoWhBox());
	}

	public void addWhBox(BmoWhBox bmoWhBox) {
		whBoxDialogBox = new DialogBox(true);
		whBoxDialogBox.setGlassEnabled(true);
		whBoxDialogBox.setText("Caja de Productos");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "100px");

		whBoxDialogBox.setWidget(vp);

		UiWhBoxForm whBoxForm = new UiWhBoxForm(getUiParams(), vp, bmoWhMovement, bmoWhBox);

		whBoxForm.show();

		whBoxDialogBox.center();
		whBoxDialogBox.show();
	}

	public void reset() {
		data.list();
		whMovItemGrid.redraw();
	}

	// Agrega un item de un producto a la orden de compra
	private class UiWhBoxForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private UiSuggestBox whBoxSuggestBox = new UiSuggestBox(new BmoWhBox());

		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoWhBox bmoWhBox;
		private BmoWhMovement bmoWhMovement;

		public UiWhBoxForm(UiParams uiParams, Panel defaultPanel, BmoWhMovement bmoWhMovement, BmoWhBox bmoWhBox) {
			super(uiParams, defaultPanel);
			this.bmoWhMovement = bmoWhMovement;
			this.bmoWhBox = bmoWhBox;

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoWhMovement.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, whBoxSuggestBox, bmoWhBox.getIdField());
			formTable.addButtonPanel(buttonPanel);
		}

		public void prepareSave() {
			try {
				bmoWhBox = new BmoWhBox();
				bmoWhBox.setId(whBoxSuggestBox.getSelectedId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
			}
		}

		//Obtener la cantidad en almacen
		public void save() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);			
				}
			};

			try {	
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoWhMovement.getPmClass(), bmoWhMovement, BmoWhMovement.ACTION_WHBOX, "" +  bmoWhBox.getId(), callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + e.toString());
			}
		} 

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				whBoxDialogBox.hide();
				reset();
			}
		}
	}
}
