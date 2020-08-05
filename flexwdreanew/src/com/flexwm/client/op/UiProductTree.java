package com.flexwm.client.op;

import java.util.Iterator;

import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoProductKitItem;
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

public class UiProductTree implements TreeViewModel {
	private SelectionModel<BmObject> selectionModel;
	private final DefaultSelectionEventManager<BmObject> selectionManager = DefaultSelectionEventManager.createDefaultManager();
	
	private static String PRODUCTFAMILIES = "Familias";
	private static String PRODUCTGROUPS = "Grupos";
	private static String PRODUCTKITS = "Kits";
	
	public UiProductTree(SelectionModel<BmObject> selectionModel){
		this.selectionModel = selectionModel;
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
		// Mostrar Familias de productos
		if (parentBmObject instanceof String && ((String)parentBmObject).equals(PRODUCTFAMILIES)) {
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProductFamily()), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar grupos de productos
		else if (parentBmObject instanceof String && ((String)parentBmObject).equals(PRODUCTGROUPS)) {
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProductGroup()), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar kits de productos
		else if (parentBmObject instanceof String && ((String)parentBmObject).equals(PRODUCTKITS)) {
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProductKit()), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar productos segun la familia
		else if (parentBmObject instanceof BmoProductFamily) {
			BmoProduct bmoProduct = new BmoProduct();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoProduct.getKind(), bmoProduct.getProductFamilyId().getName(), ((BmObject)parentBmObject).getId());
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProduct(), bmFilter), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar productos segun el grupo
		else if (parentBmObject instanceof BmoProductGroup) {
			BmoProduct bmoProduct = new BmoProduct();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoProduct.getKind(), bmoProduct.getProductFamilyId().getName(), ((BmObject)parentBmObject).getId());
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProduct(), bmFilter), cell, selectionModel, selectionManager, null);
		} 
		
		// Mostrar items de productos segun el kit
		else if (parentBmObject instanceof BmoProductKit) {
			BmoProductKitItem bmoProductKitItem = new BmoProductKitItem();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoProductKitItem.getKind(), bmoProductKitItem.getProductKitId().getName(), ((BmObject)parentBmObject).getId());
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoProductKitItem(), bmFilter), cell, selectionModel, selectionManager, null);
		} 
		
		// Panel raiz
		else {
			ListDataProvider<String> dataProvider = new ListDataProvider<String>();
			dataProvider.getList().add(PRODUCTFAMILIES);
			dataProvider.getList().add(PRODUCTGROUPS);
			dataProvider.getList().add(PRODUCTKITS);
			return new DefaultNodeInfo<String>(dataProvider, new TextCell());
		}		
	}

	public boolean isLeaf(Object value) {
		boolean leaf = false;
		if (value instanceof BmoProduct || value instanceof BmoProductKit) leaf = true;
		return leaf;
	}
}
