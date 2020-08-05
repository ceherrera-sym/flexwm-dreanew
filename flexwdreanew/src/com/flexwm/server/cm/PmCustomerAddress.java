/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.sf.PmCity;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCity;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomerAddress;


public class PmCustomerAddress extends PmObject {
	BmoCustomerAddress bmoCustomerAddress;

	public PmCustomerAddress(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerAddress = new BmoCustomerAddress();
		setBmObject(bmoCustomerAddress);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerAddress.getCityId(), bmoCustomerAddress.getBmoCity()),
				new PmJoin(bmoCustomerAddress.getBmoCity().getStateId(), bmoCustomerAddress.getBmoCity().getBmoState()),
				new PmJoin(bmoCustomerAddress.getBmoCity().getBmoState().getCountryId(), bmoCustomerAddress.getBmoCity().getBmoState().getBmoCountry())
				)));
	}
	
	// Validaciones
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoCustomerAddress bmoCustomerAddress = (BmoCustomerAddress)bmObject;
		
		PmConn pmConn;
		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();
			String sql = "";
			int countStreet = 0, countNumber = 0, duplicateAddressNumber = 0;
			// Si esta activado en configuracion
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDuplicateAddress().toBoolean() ) {

				// Obtener el numero
				duplicateAddressNumber = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDuplicateAddressNumber().toInteger();
				// Si no hay valor o es menor a cero, poner por defecto 1
				if (!(duplicateAddressNumber > 0)) duplicateAddressNumber = 1;
				System.out.println("numeros CONF:"+duplicateAddressNumber);

				// Contar las calles iguales
				 sql = "SELECT COUNT(*) FROM " + bmoCustomerAddress.getKind() 
								+ " WHERE " + bmoCustomerAddress.getStreet().getName() + " LIKE '" + bmoCustomerAddress.getStreet() + "'";
			
				pmConn.doFetch(sql);
				while (pmConn.next()) countStreet = pmConn.getInt(1);
				System.out.println("calles:"+countStreet);
				
				// Contar los numeros de calle iguales
				 sql = "SELECT COUNT(*) FROM " + bmoCustomerAddress.getKind() 
								+ " WHERE " + bmoCustomerAddress.getNumber().getName() + " LIKE '" + bmoCustomerAddress.getNumber() + "'";
			
				pmConn.doFetch(sql);
				while (pmConn.next()) countNumber = pmConn.getInt(1);
				System.out.println("numeros:"+countNumber);

				// Validar si esta duplicada la calle y numero, solo si es nuevo
				if (!(bmoCustomerAddress.getId() > 0)) {
					if (countStreet >= duplicateAddressNumber && countNumber >= duplicateAddressNumber) {
						bmUpdateResult.addError(bmoCustomerAddress.getStreet().getName(),
							"La " + getSFParams().getFieldFormTitle(bmoCustomerAddress.getStreet()) +
							" y el " + getSFParams().getFieldFormTitle(bmoCustomerAddress.getNumber()) + 
							" ya existe (" + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDuplicateAddressNumber().toInteger() + ") veces.");
					}
				} else {
					printDevLog("NO ESTA DUPLICADO");
//					if (countStreet >= duplicateAddressNumber && countNumber >= duplicateAddressNumber) {
//						bmUpdateResult.addError(bmoCustomerAddress.getStreet().getName(),
//								"La " + getSFParams().getFieldFormTitle(bmoCustomerAddress.getStreet()) +
//								" y el " + getSFParams().getFieldFormTitle(bmoCustomerAddress.getNumber()) + 
//								" ya existe (" + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDuplicateAddressNumber().toInteger() + ") veces.");
//				
//					}
				}
			}	
			
			pmConn.close();

		} catch (SFPmException e) {
			bmUpdateResult.addError(bmoCustomerAddress.getStreet().getName(), "Error al obtener los datos.");
		}
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCustomerAddress = (BmoCustomerAddress) autoPopulate(pmConn, new BmoCustomerAddress());

		// BmoCity
		BmoCity bmoCity = new BmoCity();
		int cityId = (int)pmConn.getInt(bmoCity.getIdFieldName());
		if (cityId > 0) bmoCustomerAddress.setBmoCity((BmoCity) new PmCity(getSFParams()).populate(pmConn));
		else bmoCustomerAddress.setBmoCity(bmoCity);

		return bmoCustomerAddress;
	}
}
