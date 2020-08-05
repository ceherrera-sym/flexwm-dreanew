/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client;

import com.symgae.client.ui.Ui;

public class UiMainTreeMenu extends Ui {

//	public UiMainTreeMenu(SFParams sfParams, Panel defaultPanel) {
//		super(sfParams, defaultPanel);
//	}
//
//	public StackLayoutPanel getMenu(){
//		// Menu base
//		Tree baseTree = new Tree();
//		
//		TreeItem treeItem = new TreeItem();
//		treeItem.setText("Comercial");
//		treeItem.addItem(new HTML("Ventas"));
//		treeItem.addItem(new HTML("Atencion"));
//
//		// Menu arbol principal
//		TreeItem fileItem = new TreeItem();
//		fileItem.setText("Principal");
//		baseTree.addItem(fileItem);
//		fileItem.addItem(new HTML("Tablero"));
//		
////		// Menu CRM
////		setTreeBranch(baseTree, "CRM", UiProgramFactory.getCRMItems(getSFParams(), defaultPanel));
////
////		// Menu sistema
////		setTreeBranch(baseTree, "Sistema", UiProgramFactory.getSystemItems(getSFParams(), defaultPanel));
//
//		// Stack Menu
//		StackLayoutPanel leftMenu = new StackLayoutPanel(Unit.EM);
//		leftMenu.add(baseTree, new HTML("Menu"), 2.5);
//		leftMenu.add(new HTML("Comercial"), new HTML(getSFParams().getLoginInfo().getEmailAddress()), 2.5);
//
//		// Experimento menu dinamico
//		baseTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
//		@Override
//		public void onSelection(SelectionEvent<TreeItem> event) {
//			UiTreeItem u = (UiTreeItem)event.getSelectedItem();
//			u.getUi().show();
//		}
//		});
//		
//		return leftMenu;
//	}
	
//	public void setTreeBranch(Tree baseTree, String title, ArrayList<UiPair> items) {
//		// Menu arbol de sistema
//		TreeItem newTree = new TreeItem();
//		newTree.setText(title);
//		baseTree.addItem(newTree);
//		
//		// Experimento de menu dinamico
//
//		Iterator<UiPair> it = items.iterator();
//		while (it.hasNext()){
//			UiPair u = (UiPair)it.next();
//			newTree.addItem(new UiTreeItem(u.getUi(), u.getBmObject()));
//		}
//	}
}
