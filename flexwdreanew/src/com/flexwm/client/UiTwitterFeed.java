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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class UiTwitterFeed {
	FlowPanel twitterPanel = new FlowPanel();

	public UiTwitterFeed() {
		drawTwitter();
	}

	private void drawTwitter() {

		String s = "<a class=\"twitter-timeline\" width=\"100%\" height=\"300\" href=\"https://twitter.com/Planem4U\" data-widget-id=\"429632765491826688\">Tweets por @Planem4U</a>";
		HTML h = new HTML(s);
		twitterPanel.add(h);

		// Agregar metodo que utiliza youtube
		Document doc = Document.get();
		ScriptElement script = doc.createScriptElement();
		script.setText("!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");");
		script.setType("text/javascript");
		script.setLang("javascript");

		doc.getBody().appendChild(script);
	}

	public FlowPanel getTwitterPanel() {
		return twitterPanel;
	}

	public void setTwitterPanel(FlowPanel twitterPanel) {
		this.twitterPanel = twitterPanel;
	}
}
