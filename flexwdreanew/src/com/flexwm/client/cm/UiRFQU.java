//package com.flexwm.client.cm;
//
//
//import com.flexwm.shared.cm.BmoRFQU;
//import com.symgae.client.ui.UiList;
//import com.symgae.client.ui.UiParams;
//import com.symgae.shared.BmObject;
//
//
//
//public class UiRFQU extends UiList {
//	BmoRFQU bmoRFQU ;
//
//	public UiRFQU(UiParams uiParams) {
//		super(uiParams, new BmoRFQU());
//		bmoRFQU= (BmoRFQU)getBmObject();
//	}
//
//	@Override
//	public void create() {
//		UiRFQUForm uiRFQUForm = new UiRFQUForm(getUiParams(), 0);
//		uiRFQUForm.show();
//	}
//
//	@Override
//	public void open(BmObject bmObject) {
//		bmoRFQU= (BmoRFQU)getBmObject();
//		UiRFQDetail uiRFQDetail = new UiRFQDetail(getUiParams(), bmObject.getId());
//		uiRFQDetail.show();
//	}
//
//	public void edit(BmObject bmObject) {
//		bmoRFQU= (BmoRFQU)getBmObject();
//		UiRFQUForm uiRFQUForm = new UiRFQUForm(getUiParams(), bmObject.getId());
//		uiRFQUForm.show();
//	}
//
//	
//	
//	
//}
