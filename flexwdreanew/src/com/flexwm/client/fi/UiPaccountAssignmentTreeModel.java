package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoPaccount;
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

public class UiPaccountAssignmentTreeModel implements TreeViewModel {
	private SelectionModel<BmObject> selectionModel;
	private final DefaultSelectionEventManager<BmObject> selectionManager = DefaultSelectionEventManager.createDefaultManager();
	
	private static String PACCOUNTS = "Cuentas x Pagar";
	
	public UiPaccountAssignmentTreeModel(SelectionModel<BmObject> selectionModel){
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
		// Mostrar Familias de productos
		if (parentBmObject instanceof String && ((String)parentBmObject).equals(PACCOUNTS)) {
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoPaccount()), cell, selectionModel, selectionManager, null);
		} 
		
		// Panel raiz
		else {
			ListDataProvider<String> dataProvider = new ListDataProvider<String>();
			dataProvider.getList().add(PACCOUNTS);
			return new DefaultNodeInfo<String>(dataProvider, new TextCell());
		}		
	}

	public boolean isLeaf(Object value) {
		boolean leaf = false;
		if (value instanceof BmoPaccount) leaf = true;
		return leaf;
	}
}
