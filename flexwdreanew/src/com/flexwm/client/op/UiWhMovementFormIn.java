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
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiWhMovementFormIn extends UiFormDialog {	
	private FlowPanel whMovItemPanel = new FlowPanel();
	private CellTable<BmObject> whMovItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	protected DialogBox whMovItemDialogBox;
	private UiSuggestBox toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());

	BmoWhSection bmoWhSection = new BmoWhSection();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	BmoWhMovement bmoWhMovement;

	String itemsSection = "Items";
	String statusSection = "Estatus";

	public UiWhMovementFormIn(UiParams uiParams, int id) {
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
		formFlexTable.addLabelField(3, 0, bmoWhMovement.getBmoRequisitionReceipt().getCode());
		formFlexTable.addLabelField(4, 0, bmoWhMovement.getBmoRequisitionReceipt().getName());
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
		Column<BmObject, String> quantityColumn;
		quantityColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getQuantity().toString();
			}
		};
		whMovItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		whMovItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Clave
			Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoProduct().getCode().toString();
				}
			};
			whMovItemGrid.setColumnWidth(codeColumn, 70, Unit.PX);
			whMovItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));	
		}
		// Nombre
		Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getName().toString(50);
			}
		};
		whMovItemGrid.setColumnWidth(prodColumn, 150, Unit.PX);
		whMovItemGrid.addColumn(prodColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));	

		if (!isMobile()) {
			// Modelo
			Column<BmObject, String> modelColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoProduct().getModel().toString();
				}
			};
			whMovItemGrid.addColumn(modelColumn, SafeHtmlUtils.fromSafeConstant("Modelo"));
			whMovItemGrid.setColumnWidth(modelColumn, 100, Unit.PX);
		}
		// Tipo producto
		Column<BmObject, String> trackColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getTrack().getSelectedOption().getLabel();
			}
		};
		whMovItemGrid.addColumn(trackColumn, SafeHtmlUtils.fromSafeConstant("Rastreo"));
		whMovItemGrid.setColumnWidth(trackColumn, 70, Unit.PX);

		// Número de Serie
		Column<BmObject, String> serialColumn;
		serialColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getSerial().toString();
			}
		};
		whMovItemGrid.addColumn(serialColumn, SafeHtmlUtils.fromSafeConstant("#Serie/Lote"));
		whMovItemGrid.setColumnWidth(serialColumn, 100, Unit.PX);
		serialColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	
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
			UiWhMovementFormIn uiWhMovementForm = new UiWhMovementFormIn(getUiParams(), getBmObject().getId());
			uiWhMovementForm.show();
		} else {
			UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
			uiWhMovementList.show();
		}		
	}

	public void reset() {
		data.list();
		whMovItemGrid.redraw();
	}

}
