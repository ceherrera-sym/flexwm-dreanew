package com.flexwm.server.co;

import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cm.PmOpportunity;
import com.flexwm.server.wf.IWFlowValidate;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class WFlowPropertySalesCustVal implements IWFlowValidate {

	@Override
	public void validate(SFParams sFParams, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Se obtiene la venta
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		BmoCustomer bmoCustomer = new BmoCustomer();		
		
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(bmoWFlowStep.getBmoWFlow().getCallerId().toInteger(),
				bmoOpportunity.getIdFieldName());
		if(!(bmoOpportunity.getId()>0)){
		//obtener el cliente de la oportunidad
		bmoCustomer = (BmoCustomer)pmCustomer.getBy(bmoWFlowStep.getBmoWFlow().getCustomerId().toInteger(),
		bmoCustomer.getIdFieldName());
		
		//Validar apellido materno del cliente.
		if(bmoCustomer.getMotherlastname().toString().isEmpty()){
//			System.out.println("Error de motherlastname");
			bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita un Apellido Materno.");
		}
		//Validar fecha de nacimiento del cliente.
		if(bmoCustomer.getBirthdate().toString().isEmpty()){
			bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita una fecha de nacimiento.");
//			System.out.println("Error de birthdat");
		}
		//Validar estado civil del cliente.
		if(bmoCustomer.getMaritalStatusId().toString().isEmpty()){
			bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita una estado civil.");
//			System.out.println("Error de maritalstatus");
		}
		//Validar INE del cliente.
		if(bmoCustomer.getNss().toString().isEmpty()){
			bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita ID.");
//			System.out.println("Error de maritalstatus");
		}
		}
		else{
			//obtener el cliente de la oportunidad
			bmoCustomer = (BmoCustomer)pmCustomer.getBy(bmoWFlowStep.getBmoWFlow().getCustomerId().toInteger(),
			bmoCustomer.getIdFieldName());
			
			//Validar apellido materno del cliente.
			if(bmoCustomer.getMotherlastname().toString().isEmpty()){
//				System.out.println("Error de motherlastname");
				bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita un Apellido Materno.");
			}
			//Validar fecha de nacimiento del cliente.
			if(bmoCustomer.getBirthdate().toString().isEmpty()){
				bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita una fecha de nacimiento.");
//				System.out.println("Error de birthdat");
			}
			//Validar estado civil del cliente.
			if(bmoCustomer.getMaritalStatusId().toString().isEmpty()){
				bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita una estado civil.");
//				System.out.println("Error de maritalstatus");
			}
			//Validar INE del cliente.
			if(bmoCustomer.getNss().toString().isEmpty()){
				bmUpdateResult.addError(bmoOpportunity.getCustomerId().getName(), "El cliente necesita ID.");
//				System.out.println("Error de maritalstatus");
			}
		}
	}
}
