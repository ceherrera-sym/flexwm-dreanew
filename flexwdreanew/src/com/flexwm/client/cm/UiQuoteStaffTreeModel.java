package com.flexwm.client.cm;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;


public class UiQuoteStaffTreeModel implements TreeViewModel {
	private SelectionModel<BmObject> selectionModel;
	private final DefaultSelectionEventManager<BmObject> selectionManager = DefaultSelectionEventManager.createDefaultManager();
	
	private static String GROUPS = "Grupos";
	
	public UiQuoteStaffTreeModel(SelectionModel<BmObject> selectionModel){
		this.selectionModel = selectionModel;
	}

	Cell<BmObject> cell = new AbstractCell<BmObject>() {
		@Override
		public void render(Context context, BmObject value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.listBoxFieldsToString());
			}
		}
	};

	public <T> NodeInfo<?> getNodeInfo(T parentBmObject) {
		// Mostrar Grupos
		if (parentBmObject instanceof String && ((String)parentBmObject).equals(GROUPS)) {
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProfile()), cell, selectionModel, selectionManager, null);
		} 
		
		// Panel raiz
		else {
			ListDataProvider<String> dataProvider = new ListDataProvider<String>();
			dataProvider.getList().add(GROUPS);
			return new DefaultNodeInfo<String>(dataProvider, new TextCell());
		}		
	}

	public boolean isLeaf(Object value) {
		boolean leaf = false;
		if (value instanceof BmoProfile) leaf = true;
		return leaf;
	}
}
