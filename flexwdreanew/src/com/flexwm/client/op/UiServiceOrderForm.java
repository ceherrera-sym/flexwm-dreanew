package com.flexwm.client.op;
//package com.flexwm.client.cm;
//
//import java.util.Date;
//
//import com.flexwm.shared.cm.BmoServiceOrder;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.symgae.client.sf.UiFileLabelList;
//import com.symgae.client.ui.UiDateTimeBox;
//import com.symgae.client.ui.UiForm;
//import com.symgae.client.ui.UiFormDialog;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.GwtUtil;
//import com.symgae.shared.sf.BmoFile;
//import com.symgae.shared.sf.BmoUser;
//
//
//public class UiServiceOrderForm extends UiFormDialog {
//
//	TextBox codeTextBox = new TextBox();
//	UiSuggestBox userUiSuggestBox = new UiSuggestBox(new BmoUser());
//	TextArea activityTextArea = new TextArea();
//	TextArea commitsTextArea = new TextArea();
//	UiListBox typeListBox = new UiListBox(getUiParams());
//	UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
//	UiDateTimeBox endDateTimeBox = new UiDateTimeBox();
//	TextBox estimateTimeTextBox = new TextBox();
//	TextBox realTimeTextBox = new TextBox();
//	TextBox costPerHourTextBox = new TextBox();
//
//	BmoServiceOrder bmoServiceOrder;
//	private UiFileLabelList uiFileLabelList;
//	protected HorizontalPanel programEnabledPanelFiles = new HorizontalPanel();
//	private boolean enableFiles = true;
//
//	public UiServiceOrderForm(UiParams uiParams, int id) {
//		super(uiParams, new BmoServiceOrder(), id);
//	}
//
//	@Override
//	public void populateFields() {
//		bmoServiceOrder = (BmoServiceOrder)getBmObject();
//		try {
//			if (newRecord) {
//				bmoServiceOrder.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
//				bmoServiceOrder.getEndDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
//			}
//		} catch (BmException e) {
//			showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
//		}
//
//		formFlexTable.addField(1, 0, codeTextBox, bmoServiceOrder.getCode());
//		formFlexTable.addField(2, 0, userUiSuggestBox, bmoServiceOrder.getUserId());
//		formFlexTable.addField(3, 0, activityTextArea, bmoServiceOrder.getActivity());
//		formFlexTable.addField(4, 0, typeListBox, bmoServiceOrder.getType());
//		formFlexTable.addField(5, 0, estimateTimeTextBox, bmoServiceOrder.getEstimateTime());
//		formFlexTable.addField(6, 0, realTimeTextBox, bmoServiceOrder.getRealTime());
//		formFlexTable.addField(7, 0, costPerHourTextBox, bmoServiceOrder.getCostPerHour());
//		formFlexTable.addField(8, 0, startDateTimeBox, bmoServiceOrder.getStartDate());
//		formFlexTable.addField(9, 0, endDateTimeBox, bmoServiceOrder.getEndDate());
//		statusEffect();
//	}
//
//	private void statusEffect() {
//		codeTextBox.setEnabled(false);
//		realTimeTextBox.setEnabled(false);
//		startDateTimeBox.setEnabled(false);
//		endDateTimeBox.setEnabled(false);
//		setProgramEnabled();
//	}
//	
//	@Override
//	public void postShow() {
//		if (!newRecord) closeButton.setVisible(false);
//	}
//	
//	// Revisar si estan habilitados el mostrar formatos y/o archivos
//		private void setProgramEnabled() {
////			BmoProgram bmoProgramObject = new BmoProgram();
////			try {
//				// Si es registro creado, mostrar formatos y archivos
//				if (id > 0) {
////					bmoProgramObject = getUiParams().getSFParams().getBmoProgram(getBmObject().getProgramCode());
//
//					int row = formFlexTable.getRowCount();
//					int col = 0;
//					String sectionName = "";
//					
////					if (bmoProgramObject.getEnableFiles().toBoolean() &&
////							bmoProgramObject.getEnableFormats().toBoolean())
////						sectionName = "Archivos y Formatos";
////					else if (bmoProgramObject.getEnableFiles().toBoolean())
//						sectionName = getSFParams().getProgramTitle(new BmoFile().getProgramCode());
////					else if (bmoProgramObject.getEnableFormats().toBoolean())
////						sectionName = "Formatos";
//					
//					// Si hay archivos o formatos habilitados, mostrar seccion
//					if (!sectionName.equals(""))
//						formFlexTable.addSectionLabel(row++, col, sectionName, 2);
//
////					if (bmoProgramObject.getEnableFiles().toBoolean())
//						setProgramFiles(row++, col);
//					
////					if (bmoProgramObject.getEnableFormats().toBoolean())
////						setProgramFormats(row, col);
//
//					// Ocultar seccion si hay archivos o formatos habilitados, 
//					if (!sectionName.equals(""))
//						formFlexTable.hideSection(sectionName);
//					
//				} else {
//					programEnabledPanelFiles.setVisible(false);
//				}
////			} catch (SFException e) {
////				getUiParams().getUiTemplate().showSystemMessage(this.getClass().getName() + "-setProgramEnabled(): No se pudo obtener el Modulo: " + getBmObject().getProgramCode());
////			}
//		}
//	
//	private void setProgramFiles(int row, int col) {
//		uiFileLabelList = new UiFileLabelList(getUiParams(), getBmObjectBmoProgram().getId(), getBmObject(), enableFiles);
//		formFlexTable.addField(row, col, uiFileLabelList);
//	}
//	
//	// Agrega formatos a FlowPanel
////	private void setProgramFormats(int row, int col) {
////		formFlexTable.addField(row, col, new UiFormatDisplayLabelList(getUiParams(), getBmObjectBmoProgram().getId(), getBmObject()));
////	}
//	
//	// Habilita archivos
//	public void enableFiles() {
//		uiFileLabelList.enable();
//	}
//	
//	// Deshabilita archivos
//	public void disableFiles() {
//		uiFileLabelList.disable();
//	}
//
//	@Override
//	public BmObject populateBObject() throws BmException {
//		bmoServiceOrder.setId(id);
//		bmoServiceOrder.getCode().setValue(codeTextBox.getText());
//		bmoServiceOrder.getUserId().setValue(userUiSuggestBox.getSelectedId());
//		bmoServiceOrder.getActivity().setValue(activityTextArea.getText());
//		bmoServiceOrder.getType().setValue(typeListBox.getSelectedCode());
//		bmoServiceOrder.getStartDate().setValue(startDateTimeBox.getDateTime());
//		bmoServiceOrder.getEndDate().setValue(endDateTimeBox.getDateTime());
//		bmoServiceOrder.getEstimateTime().setValue(estimateTimeTextBox.getText());
//		bmoServiceOrder.getRealTime().setValue(realTimeTextBox.getText());
//		bmoServiceOrder.getCostPerHour().setValue(costPerHourTextBox.getText());
//
//		return bmoServiceOrder;
//	}
//	
//	@Override
//	public void saveNext() {
//		if (newRecord) {
//			UiServiceOrderDetail uiServiceOrderDetail = new UiServiceOrderDetail(getUiParams(), getBmObject().getId());
//			uiServiceOrderDetail.show();
//		} else {
//			UiServiceOrderForm uiServiceOrderForm = new UiServiceOrderForm(getUiParams(), getBmObject().getId());
//			uiServiceOrderForm.show();
//		}
//	}
//
//	@Override
//	public void close() {
//		UiServiceOrderList uiServiceOrderList = new UiServiceOrderList(getUiParams());
//		uiServiceOrderList.show();
//	}
//}
//
