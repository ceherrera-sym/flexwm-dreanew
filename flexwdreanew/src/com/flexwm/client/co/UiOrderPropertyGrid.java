package com.flexwm.client.co;

import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.co.BmoOrderProperty;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiOrderPropertyGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderProperty
	private BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
	private FlowPanel orderPropertyPanel = new FlowPanel();
	private CellTable<BmObject> orderPropertyGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderPropertyData;
	BmFilter orderPropertyFilter;
//	private String propertyLabel = "";
	
	// Otros
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private OrderUpdater orderUpdater;

	public UiOrderPropertyGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderUpdater orderUpdater){
		super(uiParams, defaultPanel);
		this.orderUpdater = orderUpdater;

		// Cambiar etiqueta por configuracion del campo
//		propertyLabel = getSFParams().getFieldFormTitle(bmoOrderProperty.getPropertyId());
		
		// Inicializar grid de Inmueble
		orderPropertyGrid = new CellTable<BmObject>();
		orderPropertyGrid.setWidth("100%");
		orderPropertyPanel.clear();
		orderPropertyPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setOrderPropertyColumns();
		orderPropertyFilter = new BmFilter();
		orderPropertyFilter.setValueFilter(bmoOrderProperty.getKind(), bmoOrderProperty.getOrderId().getName(), bmoOrder.getId());
		orderPropertyGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderPropertyData = new UiListDataProvider<BmObject>(new BmoOrderProperty(), orderPropertyFilter);
		orderPropertyData.addDataDisplay(orderPropertyGrid);
		orderPropertyPanel.add(orderPropertyGrid);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
//		formFlexTable.addSectionLabel(1, 0, propertyLabel, 2);
		//		formTable.addField(2, 0, showQuantityCheckBox, bmoOrder.get());
		//		formTable.addField(2, 0, showPriceCheckBox, bmoOrderProperty.getShowPrice());
		//		formFlexTable.addLabelField(2, 2, bmoOrderProperty.getAmount(), amountLabel);
		formFlexTable.addPanel(3, 0, orderPropertyPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderPropertyData.list();
		orderPropertyGrid.redraw();
	}

	public void reset(){
		orderUpdater.changeOrderProperty();
	}

	public void changeHeight() {
		orderPropertyGrid.setPageSize(orderPropertyData.getList().size());
		orderPropertyGrid.setVisibleRange(0, orderPropertyData.getList().size());
	}

	// Columnas grid de personal
	public void setOrderPropertyColumns() {
		// Clave
		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderProperty)bmObject).getBmoProperty().getCode().toString();
			}
		};
		orderPropertyGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		orderPropertyGrid.setColumnWidth(codeColumn, 150, Unit.PX);

		if (!isMobile()) {
			// Manzana
			Column<BmObject, String> blockColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderProperty)bmObject).getBmoProperty().getBmoDevelopmentBlock().getCode().toString();
				}
			};
			orderPropertyGrid.addColumn(blockColumn, SafeHtmlUtils.fromSafeConstant("Mz"));

			// Lote
			Column<BmObject, String> lotColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderProperty)bmObject).getBmoProperty().getLot().toString();
				}
			};
			orderPropertyGrid.addColumn(lotColumn, SafeHtmlUtils.fromSafeConstant("Lote"));
		}

		// Calle y numero
		Column<BmObject, String> streetColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderProperty)bmObject).getBmoProperty().getStreet().toString() + " "
						+ " #" + ((BmoOrderProperty)bmObject).getBmoProperty().getNumber().toString();
			}
		};
		orderPropertyGrid.addColumn(streetColumn, SafeHtmlUtils.fromSafeConstant("Calle y Número"));
		orderPropertyGrid.setColumnWidth(streetColumn, 200, Unit.PX);

		if (!isMobile()) {
			// Precio
			Column<BmObject, String> priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoOrderProperty)bmObject).getPrice().toDouble());
					return (formatted);
				}
			};
			priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			orderPropertyGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));

			// Tierra excedente
			Column<BmObject, String> extraLandColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoOrderProperty)bmObject).getExtraLand().toDouble());
					return (formatted);
				}
			};
			extraLandColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			orderPropertyGrid.addColumn(extraLandColumn, SafeHtmlUtils.fromSafeConstant("$ T. Ex."));

			// Construccion Excedente
			Column<BmObject, String> extraConstructionColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoOrderProperty)bmObject).getExtraConstruction().toDouble());
					return (formatted);
				}
			};
			extraConstructionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			orderPropertyGrid.addColumn(extraConstructionColumn, SafeHtmlUtils.fromSafeConstant("$ C. Ex."));

			// Otros Extras
			Column<BmObject, String> extraOtherColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoOrderProperty)bmObject).getExtraOther().toDouble());
					return (formatted);
				}
			};
			extraOtherColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			orderPropertyGrid.addColumn(extraOtherColumn, SafeHtmlUtils.fromSafeConstant("$ Otros"));
		}

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderProperty)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderPropertyGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
	}
}
