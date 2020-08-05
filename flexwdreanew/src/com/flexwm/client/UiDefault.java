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

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;

public class UiDefault extends Ui {

	Image defaultImage = new Image("/img/default.jpg");

	public UiDefault(UiParams uiParams) {
		super(uiParams);
	}

	@Override
	public void show() {
		clearDP();
		VerticalPanel vp = new VerticalPanel();
		vp.add(defaultImage);
		addToDP(vp);
	}

}
