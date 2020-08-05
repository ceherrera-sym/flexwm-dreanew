/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoProjectGuideline;
import com.google.gwt.user.client.ui.RichTextArea;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


public class UiProjectGuidelinesForm extends UiFormDialog {
	RichTextArea guidelinesEditor = new RichTextArea();
	BmoProjectGuideline bmoProjectGuideline;
	BmoProject bmoProject;
	String projectId;

	public UiProjectGuidelinesForm(UiParams uiParams, int id, BmoProject bmoProject) {
		super(uiParams, new BmoProjectGuideline(), id);
		bmoProjectGuideline = (BmoProjectGuideline)getBmObject();
		this.bmoProject = bmoProject;
		super.foreignId = bmoProject.getId();
		this.projectId = "" + foreignId;
		super.foreignField = bmoProjectGuideline.getProjectId().getName();
	}

	@Override
	public void populateFields() {
		bmoProjectGuideline = (BmoProjectGuideline)getBmObject();
		formFlexTable.addField(1, 0, guidelinesEditor, bmoProjectGuideline.getGuidelines());
	}

	@Override
	public void postShow() {
		if (bmoProject.getStatus().toChar() != BmoProject.STATUS_REVISION) {
			saveButton.setVisible(false);
		}
		deleteButton.setVisible(false);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoProjectGuideline.setId(id);
		bmoProjectGuideline.getProjectId().setValue(projectId);
		bmoProjectGuideline.getGuidelines().setValue(guidelinesEditor.getHTML());
		return bmoProjectGuideline;
	}

	@Override
	public void close() {
		dialogClose();
	}
}