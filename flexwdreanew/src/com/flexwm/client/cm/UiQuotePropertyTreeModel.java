package com.flexwm.client.cm;

import java.util.ArrayList;
import java.util.Iterator;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFParams;

public class UiQuotePropertyTreeModel implements TreeViewModel {
	private SelectionModel<BmObject> selectionModel;
	private final DefaultSelectionEventManager<BmObject> selectionManager = DefaultSelectionEventManager.createDefaultManager();
	private SFParams sfParams;
	BmoQuote bmoQuote;
	private static String DEVELOPMENTPHASES = "Etapas";
	
	public UiQuotePropertyTreeModel(SelectionModel<BmObject> selectionModel, SFParams sfParams, BmoQuote bmoQuote){
		this.selectionModel = selectionModel;
		this.sfParams = sfParams;
		this.bmoQuote = bmoQuote;
	}

	Cell<BmObject> cell = new AbstractCell<BmObject>() {
		@Override
		public void render(Context context, BmObject value, SafeHtmlBuilder sb) {
			if (value != null) {
				Iterator<BmField> fieldIterator = value.getDisplayFieldList().iterator();
				BmField bmField = (BmField)fieldIterator.next();
				String toolTip = bmField.getLabel() + ": " + bmField.toString();
				while (fieldIterator.hasNext()) { 
					bmField = (BmField)fieldIterator.next();
					toolTip += "\n" + bmField.getLabel() + ": " + bmField.toString();
				} 
				
				sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" + value.listBoxFieldsToString() + "</span>");
			}
		}
	};

	public <T> NodeInfo<?> getNodeInfo(T parentBmObject) {
		// Mostrar etapas de los desarrollos
		if (parentBmObject instanceof String && ((String)parentBmObject).equals(DEVELOPMENTPHASES)) {
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

			BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
			BmFilter filterActive = new BmFilter();
			filterActive.setValueFilter(bmoDevelopmentPhase.getKind(), bmoDevelopmentPhase.getIsActive().getName(), "1");
			filterList.add(filterActive);
			
			// MultiEmpresa: g100
			if ( ((BmoFlexConfig)sfParams.getBmoAppConfig()).getMultiCompany().toBoolean() ) {
				BmFilter filterByCompany = new BmFilter();
				filterByCompany.setValueFilter(bmoDevelopmentPhase.getKind(), bmoDevelopmentPhase.getCompanyId(), bmoQuote.getCompanyId().toInteger());
				filterList.add(filterByCompany);
			}
			
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoDevelopmentPhase(), filterList), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar manzanas segun la la fase
		else if (parentBmObject instanceof BmoDevelopmentPhase) {
			BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
			BmFilter filterByPhase = new BmFilter();
			filterByPhase.setValueFilter(bmoDevelopmentBlock.getKind(), bmoDevelopmentBlock.getDevelopmentPhaseId().getName(), ((BmObject)parentBmObject).getId());
			BmFilter filterOpen = new BmFilter();
			filterOpen.setValueFilter(bmoDevelopmentBlock.getKind(), bmoDevelopmentBlock.getIsOpen().getName(), "1");
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			filterList.add(filterByPhase);
			filterList.add(filterOpen);
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoDevelopmentBlock(), filterList), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar productos segun la familia
		else if (parentBmObject instanceof BmoDevelopmentBlock) {
			BmoProperty bmoProperty = new BmoProperty();
			BmFilter filterByDevelopmentBlock = new BmFilter();
			filterByDevelopmentBlock.setValueFilter(bmoProperty.getKind(), bmoProperty.getDevelopmentBlockId().getName(), ((BmObject)parentBmObject).getId());
			BmFilter filterAvailable = new BmFilter();
			filterAvailable.setValueFilter(bmoProperty.getKind(), bmoProperty.getAvailable().getName(), "1");
			BmFilter filterCanSell = new BmFilter();
			filterCanSell.setValueFilter(bmoProperty.getKind(), bmoProperty.getCansell().getName(), "1");
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			filterList.add(filterByDevelopmentBlock);
			filterList.add(filterAvailable);
			filterList.add(filterCanSell);
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProperty(), filterList), cell, selectionModel, selectionManager, null);
		} 
		
		// Panel raiz
		else {
			ListDataProvider<String> dataProvider = new ListDataProvider<String>();
			dataProvider.getList().add(DEVELOPMENTPHASES);
			return new DefaultNodeInfo<String>(dataProvider, new TextCell());
		}		
	}

	public boolean isLeaf(Object value) {
		boolean leaf = false;
		if (value instanceof BmoProperty) leaf = true;
		return leaf;
	}
}
