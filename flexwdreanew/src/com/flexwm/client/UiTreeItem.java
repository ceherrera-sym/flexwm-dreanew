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

import com.google.gwt.user.client.ui.TreeItem;
import com.symgae.client.ui.Ui;
import com.symgae.shared.BmObject;

public class UiTreeItem extends TreeItem {
	private final Ui ui;
	private final BmObject bmObject;
	
	
	public UiTreeItem(Ui ui, BmObject bmObject){
		this.ui = ui;
		this.bmObject = bmObject;
		setText(bmObject.getProgramLabel());
	}


	public Ui getUi() {
		return ui;
	}


	public BmObject getBmObject() {
		return bmObject;
	}

	
}
