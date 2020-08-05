package com.flexwm.client.cm;

import java.util.Iterator;
import com.flexwm.client.cm.UiQuoteForm.QuoteUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteProperty;
import com.flexwm.shared.co.BmoProperty;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiQuotePropertyGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// QuoteProperty
	private BmoQuoteProperty bmoQuoteProperty = new BmoQuoteProperty();
	private FlowPanel quotePropertyPanel = new FlowPanel();
	private CellTable<BmObject> quotePropertyGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> quotePropertyData;
	BmFilter quotePropertyFilter;
	private String propertyLabel = "";
	private Button addQuotePropertyButton = new Button("+INMUEBLE" );
	protected DialogBox quotePropertyDialogBox;

	// Arbol
	public final MultiSelectionModel<BmObject> selectionQuotePropertyModel = new MultiSelectionModel<BmObject>();
//	private UiQuotePropertyTreeModel uiQuotePropertyTreeModel = new UiQuotePropertyTreeModel(selectionQuotePropertyModel);
	private UiQuotePropertyTreeModel uiQuotePropertyTreeModel;

	private Button treeShowButton = new Button("NAVEGADOR");
	private Button treeCloseButton = new Button("CERRAR");
	CellTree cellPropertyTree;
	Label treePropertyLabel = new Label("Agregar Inmueble: ");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoQuote bmoQuote;
	private QuoteUpdater quoteUpdater;

	public UiQuotePropertyGrid(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, QuoteUpdater quoteUpdater){
		super(uiParams, defaultPanel);
		this.quoteUpdater = quoteUpdater;
		this.bmoQuote = bmoQuote;
		
		uiQuotePropertyTreeModel = new UiQuotePropertyTreeModel(selectionQuotePropertyModel, getUiParams().getSFParams(), bmoQuote);

		// Cambiar etiqueta por configuracion del campo
		propertyLabel = getSFParams().getFieldFormTitle(bmoQuoteProperty.getPropertyId());
		treePropertyLabel.setText("Agregar " + propertyLabel + ":");
		addQuotePropertyButton.setText("+ " + propertyLabel.toUpperCase());
				
		// Arbol de seleccion
		selectionQuotePropertyModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Iterator<BmObject> i = selectionQuotePropertyModel.getSelectedSet().iterator();
				BmObject bmObject;
				if (i.hasNext()) {
					bmObject = (BmObject)i.next();

					// Se esta agregando un producto directo
					if (bmObject instanceof BmoProperty) {
						addProperty((BmoProperty)bmObject);
					}
				}
			}
		});
		cellPropertyTree = new CellTree(uiQuotePropertyTreeModel, selectionQuotePropertyModel);
		cellPropertyTree.setAnimationEnabled(true);
		cellPropertyTree.addStyleName("quoteProductTree");
		treePropertyLabel.setStyleName("detailLabelTitle");

		treeShowButton.setStyleName("formCloseButton");
		treeShowButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showTree();
			}
		});
		treeCloseButton.setStyleName("formCloseButton");
		treeCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hideTree();
			}
		});

		// Inicializar grid de Inmueble
		quotePropertyGrid = new CellTable<BmObject>();
		quotePropertyGrid.setWidth("100%");
		quotePropertyPanel.clear();
		quotePropertyPanel.setWidth("100%");
		setQuotePropertyColumns();
		quotePropertyFilter = new BmFilter();
		quotePropertyFilter.setValueFilter(bmoQuoteProperty.getKind(), bmoQuoteProperty.getQuoteId().getName(), bmoQuote.getId());
		quotePropertyGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		quotePropertyData = new UiListDataProvider<BmObject>(new BmoQuoteProperty(), quotePropertyFilter);
		quotePropertyData.addDataDisplay(quotePropertyGrid);
		quotePropertyPanel.add(quotePropertyGrid);

		// Panel de botones
		addQuotePropertyButton.setStyleName("formSaveButton");
		addQuotePropertyButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addProperty();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addQuotePropertyButton);
		buttonPanel.add(treeShowButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, quotePropertyPanel, 2);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		quotePropertyData.list();
		quotePropertyGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED || bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED) {
			addQuotePropertyButton.setVisible(false);
			treeShowButton.setVisible(false);
		} else {
			addQuotePropertyButton.setVisible(true);
			treeShowButton.setVisible(true);
		}
	}

	private void showTree() {
		getUiParams().getUiTemplate().showEastPanel();
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(treePropertyLabel);
		getUiParams().getUiTemplate().getEastPanel().add(cellPropertyTree);
		getUiParams().getUiTemplate().getEastPanel().add(treeCloseButton);
	}

	private void hideTree() {
		getUiParams().getUiTemplate().hideEastPanel();
	}

	public void reset(){
		quoteUpdater.changeQuoteProperty();
	}

	public void changeHeight() {
		quotePropertyGrid.setPageSize(quotePropertyData.getList().size());
		quotePropertyGrid.setVisibleRange(0, quotePropertyData.getList().size());
	}

	// Columnas grid de personal
	public void setQuotePropertyColumns() {
		// Clave
		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoQuoteProperty)bmObject).getBmoProperty().getCode().toString();
			}
		};
		quotePropertyGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		quotePropertyGrid.setColumnWidth(codeColumn, 150, Unit.PX);

		if (!isMobile()) {
			// Manzana
			Column<BmObject, String> blockColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteProperty)bmObject).getBmoProperty().getBmoDevelopmentBlock().getCode().toString();
				}
			};
			quotePropertyGrid.addColumn(blockColumn, SafeHtmlUtils.fromSafeConstant("Mz"));
			quotePropertyGrid.setColumnWidth(blockColumn, 50, Unit.PX);

			// Lote
			Column<BmObject, String> lotColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteProperty)bmObject).getBmoProperty().getLot().toString();
				}
			};
			quotePropertyGrid.addColumn(lotColumn, SafeHtmlUtils.fromSafeConstant("Lote"));
			quotePropertyGrid.setColumnWidth(lotColumn, 50, Unit.PX);

			// Calle y numero
			Column<BmObject, String> streetColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteProperty)bmObject).getBmoProperty().getStreet().toString() + " "
							+ " #" + ((BmoQuoteProperty)bmObject).getBmoProperty().getNumber().toString();
				}
			};
			quotePropertyGrid.addColumn(streetColumn, SafeHtmlUtils.fromSafeConstant("Calle y Número"));
			quotePropertyGrid.setColumnWidth(streetColumn, 200, Unit.PX);

			// Precio
			Column<BmObject, String> priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoQuoteProperty)bmObject).getPrice().toDouble());
					return (formatted);
				}
			};
			priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			quotePropertyGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));

			// Tierra excedente
			Column<BmObject, String> extraLandColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoQuoteProperty)bmObject).getExtraLand().toDouble());
					return (formatted);
				}
			};
			extraLandColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			quotePropertyGrid.addColumn(extraLandColumn, SafeHtmlUtils.fromSafeConstant("$ T. Ex."));

			// Construccion Excedente
			Column<BmObject, String> extraConstructionColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoQuoteProperty)bmObject).getExtraConstruction().toDouble());
					return (formatted);
				}
			};
			extraConstructionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			quotePropertyGrid.addColumn(extraConstructionColumn, SafeHtmlUtils.fromSafeConstant("$ C. Ex."));

			// Otros Extras
			Column<BmObject, String> extraOtherColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoQuoteProperty)bmObject).getExtraOther().toDouble());
					return (formatted);
				}
			};
			extraOtherColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			quotePropertyGrid.addColumn(extraOtherColumn, SafeHtmlUtils.fromSafeConstant("$ Otros"));
		}
		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoQuoteProperty)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		quotePropertyGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));


		// Eliminar
		Column<BmObject, String> deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "-";
			}
		};
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		quotePropertyGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				deleteProperty((BmoQuoteProperty)bmObject);
			}
		});
	}

	public void addProperty(){
		addProperty(new BmoProperty());
	}

	public void addProperty(BmoProperty bmoProperty) {
		quotePropertyDialogBox = new DialogBox(true);
		quotePropertyDialogBox.setGlassEnabled(true);
		String propertyLabel = getSFParams().getFieldFormTitle(bmoQuoteProperty.getPropertyId());

		quotePropertyDialogBox.setText(propertyLabel + " de la Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "90px");

		quotePropertyDialogBox.setWidget(vp);

		UiQuotePropertyForm quotePropertyForm = new UiQuotePropertyForm(getUiParams(), vp, bmoQuote, bmoProperty);

		quotePropertyForm.show();

		quotePropertyDialogBox.center();
		quotePropertyDialogBox.show();
	}

	public void deleteProperty(BmoQuoteProperty bmoQuoteProperty) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			this.bmoQuoteProperty = bmoQuoteProperty;
			deleteProperty();
		} else {
			String status= "";
			if(bmoQuote.getStatus().toChar()== BmoQuote.STATUS_AUTHORIZED)
				status= "Autorizada";
			else if(bmoQuote.getStatus().toChar()== BmoQuote.STATUS_CANCELLED)
				status= "Cancelada";
			showSystemMessage("La cotización no se puede modificar - está " + status);
		}
	}

	public void deleteProperty() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteProperty(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processPropertyDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoQuoteProperty.getPmClass(), bmoQuoteProperty, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteProperty(): ERROR " + e.toString());
		}
	}

	public void processPropertyDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processPropertyDelete() ERROR: " + result.errorsToString());
		this.reset();
	}	

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiQuotePropertyForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private UiSuggestBox propertySuggestBox = new UiSuggestBox(new BmoProperty());

		private BmoQuoteProperty bmoQuoteProperty;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoQuote bmoQuote;
		private BmoProperty bmoProperty = new BmoProperty();


		public UiQuotePropertyForm(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, BmoProperty bmoProperty) {
			super(uiParams, defaultPanel);
			this.bmoQuoteProperty = new BmoQuoteProperty();
			this.bmoQuote = bmoQuote;
			this.bmoProperty = bmoProperty;

			try {
				bmoQuoteProperty.getPropertyId().setValue(bmoProperty.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
			}

			ChangeHandler listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChange(event);
				}
			};
			formTable.setListChangeHandler(listChangeHandler);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoQuoteProperty.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);

			BmFilter filterCanSell = new BmFilter();
			filterCanSell.setValueFilter(bmoProperty.getKind(), bmoProperty.getCansell().getName(), "1");
			propertySuggestBox.addFilter(filterCanSell);
			
			// MultiEmpresa: g100
			if ( ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
				BmFilter filterByCompany = new BmFilter();
				filterByCompany.setValueFilter(bmoProperty.getKind(), bmoProperty.getCompanyId(), bmoQuote.getCompanyId().toInteger());
				propertySuggestBox.addFilter(filterByCompany);
			}
		}

		public void show(){
			formTable.addField(1, 0, propertySuggestBox, bmoProperty.getIdField());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formListChange(ChangeEvent event) {

			statusEffect();
		}

		private void statusEffect() {

		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
			else {
				quotePropertyDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteProperty = new BmoQuoteProperty();
				bmoQuoteProperty.getPropertyId().setValue(propertySuggestBox.getSelectedId());
				bmoQuoteProperty.getQuoteId().setValue(bmoQuote.getId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteProperty.getPmClass(), bmoQuoteProperty, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}
}
