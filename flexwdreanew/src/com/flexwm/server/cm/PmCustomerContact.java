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

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerContact;


public class PmCustomerContact extends PmObject {
	BmoCustomerContact bmoCustomerContact;

	public PmCustomerContact(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomerContact = new BmoCustomerContact();
		setBmObject(bmoCustomerContact);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomerContact.getCustomerId(),bmoCustomerContact.getBmoCustomer()),
				new PmJoin(bmoCustomerContact.getBmoCustomer().getSalesmanId(),bmoCustomerContact.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoCustomerContact.getBmoCustomer().getTerritoryId(),bmoCustomerContact.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoCustomerContact.getBmoCustomer().getReqPayTypeId(),bmoCustomerContact.getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoCustomerContact.getBmoCustomer().getBmoUser().getAreaId(),bmoCustomerContact.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoCustomerContact.getBmoCustomer().getBmoUser().getLocationId(), bmoCustomerContact.getBmoCustomer().getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoCustomerContact = (BmoCustomerContact) autoPopulate(pmConn, new BmoCustomerContact());

		// BmoOrderType
		BmoCustomer bmoCustomer = new BmoCustomer();
		if (pmConn.getInt(bmoCustomer.getIdFieldName()) > 0)
			bmoCustomerContact.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else
			bmoCustomerContact.setBmoCustomer(bmoCustomer);

		return bmoCustomerContact;
	}

	// Validaciones del contacto
	public void validateCustomerContact(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoCustomerContact bmoCustomerContact = (BmoCustomerContact)bmObject;
		PmConn pmConn;
		try {
			// Validar que el contacto no este registrado, valida por nombre y apellidos
			pmConn = new PmConn(getSFParams());
			pmConn.open();
			int customerContactId = 0;
			customerContactId = customerContactByName(pmConn, bmoCustomerContact.getFullName().toString(),
					bmoCustomerContact.getFatherLastName().toString(), 
					bmoCustomerContact.getMotherLastName().toString(), 
					bmoCustomerContact.getCustomerId().toInteger());
			if (!(customerContactId > 0)) customerContactId = 0;
			pmConn.close();

			// Estas modificando el registro
			if (bmoCustomerContact.getId() > 0) {
				// Encontro un contacto con los mismos valores
				if (customerContactId > 0) {
					// El modificado es igual a otro contacto, mandar que ya existe uno similar
					if (customerContactId != bmoCustomerContact.getId()) {
						bmUpdateResult.addError(bmoCustomerContact.getFullName().getName(), 
								"Ya existe un contacto con el mismo nombre y apellidos.");
					}
				}
			} else {
				// Registro nuevo, revisar si existe
				if (customerContactId > 0)
					bmUpdateResult.addError(bmoCustomerContact.getFullName().getName(), "Ya existe un contacto con el mismo nombre y apellidos.");
			}

		} catch (SFPmException e) {
			bmUpdateResult.addError(this.getClass().getName(), " - validate(): " + e.toString());
		}
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoCustomerContact bmoCustomerContact = (BmoCustomerContact)bmObject;
		BmoCustomerContact bmoCustomerContactPrev = (BmoCustomerContact)bmObject;
		PmCustomerContact pmCustomerContactPrev = new PmCustomerContact(getSFParams());
		boolean newRecord = false;

		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(getSFParams());

		// Validaciones
		validateCustomerContact(bmoCustomerContact, bmUpdateResult);

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoCustomerContact.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoCustomerContact, bmUpdateResult);
		} else {
			bmoCustomerContactPrev = (BmoCustomerContact)pmCustomerContactPrev.get(bmoCustomerContact.getId());
		}

		// Revisar si ya existe Contacto Principal
		if (bmoCustomerContact.getContactMain().toBoolean()) {

			BmoCustomerContact bmoCustomerContactMain = new BmoCustomerContact();
			PmCustomerContact pmCustomerContactMain = new PmCustomerContact(getSFParams());
			int existContactMain = getCustomerContactMain(bmoCustomerContact.getCustomerId().toInteger());

			// Validar contacto principal duplicado
			if (existContactMain > 0) {
				bmoCustomerContactMain = (BmoCustomerContact)pmCustomerContactMain.get(existContactMain);

				// No mandar mensaje si es el mismo contacto principal el que se esta modificando
				if (bmoCustomerContact.getId() != bmoCustomerContactMain.getId()) {
					bmUpdateResult.addError(bmoCustomerContact.getContactMain().getName(), 
							"El Cliente ya tiene asignado a " + 
									bmoCustomerContactMain.getFullName().toString() + 
									" " + bmoCustomerContactMain.getFatherLastName().toString() +
							" como Contacto Principal");
				}
			}
			// Actualizar datos si el contacto es principal en el formulario del cliente
			bmoCustomer = (BmoCustomer)pmCustomer.get(bmoCustomerContact.getCustomerId().toInteger());
			bmoCustomer.getTitleId().setValue(bmoCustomerContact.getTitleId().toInteger());
			bmoCustomer.getFirstname().setValue(bmoCustomerContact.getFullName().toString());
			bmoCustomer.getFatherlastname().setValue(bmoCustomerContact.getFatherLastName().toString());
			bmoCustomer.getMotherlastname().setValue(bmoCustomerContact.getMotherLastName().toString());
			bmoCustomer.getPhone().setValue(bmoCustomerContact.getNumber().toString());
			bmoCustomer.getExtension().setValue(bmoCustomerContact.getExtension().toString());
			bmoCustomer.getMobile().setValue(bmoCustomerContact.getCellPhone().toString());
			bmoCustomer.getEmail().setValue(bmoCustomerContact.getEmail().toString());
			bmoCustomer.getPosition().setValue(bmoCustomerContact.getPosition().toString());
			pmCustomer.saveSimple(pmConn, bmoCustomer, bmUpdateResult);
		} else {

			// Actualizar datos en el cliente si el contacto que estas modificando 
			// es el mismo que el contacto del formulario del cliente
			int customerId = pmCustomer.getCustomerByNameContact(pmConn,
					bmoCustomerContactPrev.getFullName().toString(),
					bmoCustomerContactPrev.getFatherLastName().toString(),
					bmoCustomerContactPrev.getMotherLastName().toString(),
					bmoCustomerContact.getCustomerId().toInteger());

			if (!newRecord && customerId > 0) {
				bmoCustomer = (BmoCustomer)pmCustomer.get(customerId);
				bmoCustomer.getTitleId().setValue(bmoCustomerContact.getTitleId().toInteger());
				bmoCustomer.getFirstname().setValue(bmoCustomerContact.getFullName().toString());
				bmoCustomer.getFatherlastname().setValue(bmoCustomerContact.getFatherLastName().toString());
				bmoCustomer.getMotherlastname().setValue(bmoCustomerContact.getMotherLastName().toString());
				bmoCustomer.getPhone().setValue(bmoCustomerContact.getNumber().toString());
				bmoCustomer.getExtension().setValue(bmoCustomerContact.getExtension().toString());
				bmoCustomer.getMobile().setValue(bmoCustomerContact.getCellPhone().toString());
				bmoCustomer.getEmail().setValue(bmoCustomerContact.getEmail().toString());
				bmoCustomer.getPosition().setValue(bmoCustomerContact.getPosition().toString());
				pmCustomer.saveSimple(pmConn, bmoCustomer, bmUpdateResult);
			}
		}
		if (!bmUpdateResult.hasErrors())
			super.save(pmConn, bmoCustomerContact, bmUpdateResult);
		else printDevLog(this.getClass().getName() + " - save() " + bmUpdateResult.errorsToString() );
		return bmUpdateResult;
	}

	// Regresar contacto principal
	public int getCustomerContactMain(int customerId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		int result = getCustomerContactMain(pmConn, customerId);		
		pmConn.close();
		return result;
	}

	// Regresar primer contacto principal
	private int getCustomerContactMain(PmConn pmConn, int customerId) throws SFPmException {
		int contactMain = -1;

		String sql = "SELECT cuco_customercontactid AS contactMain FROM customercontacts " +
				" WHERE cuco_customerid = " + customerId  +
				" AND cuco_contactmain = 1" + 
				" ORDER BY cuco_customercontactid ASC";
		pmConn.doFetch(sql);

		if (pmConn.next())
			contactMain = pmConn.getInt("contactMain");

		return contactMain;
	}

	public boolean customerContactExists(PmConn pmConn, String name, String fatherLastName, String motherLastName, int customerId) throws SFPmException {
		pmConn.doFetch("SELECT cuco_customercontactid FROM customercontacts " +
				" WHERE cuco_fullname = '" + name + "' " + 
				" AND cuco_fatherlastname = '" + fatherLastName + "' " +
				((motherLastName.equals("")) ? "" : 
					" AND cuco_motherlastname =  '" + motherLastName + "' ") +
				" AND cuco_customerid = " + customerId );
		return pmConn.next();
	}

	public int customerContactByName(PmConn pmConn, String name, String fatherLastName, String motherLastName, int customerId) throws SFPmException {
		int contact = -1;
		pmConn.doFetch("SELECT cuco_customercontactid FROM customercontacts " +
				" WHERE cuco_fullname = '" + name + "' " + 
				" AND cuco_fatherlastname = '" + fatherLastName + "' " +
				((motherLastName.equals("")) ? "" : 
					" AND cuco_motherlastname =  '" + motherLastName + "' ") +
				" AND cuco_customerid = " + customerId);

		if (pmConn.next())
			contact = pmConn.getInt("cuco_customercontactid");

		return contact;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCustomerContact = (BmoCustomerContact)bmObject;

		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomerContact bmoCustomerContactPrev = (BmoCustomerContact)this.get(bmoCustomerContact.getId());
		int customerId = pmCustomer.getCustomerByNameContact(pmConn,
				bmoCustomerContactPrev.getFullName().toString(),
				bmoCustomerContactPrev.getFatherLastName().toString(),
				bmoCustomerContactPrev.getMotherLastName().toString(),
				bmoCustomerContact.getCustomerId().toInteger());

		if (bmoCustomerContact.getContactMain().toInteger() > 0)
			bmUpdateResult.addError(bmoCustomerContact.getContactMain().getName(), 
					"No se puede eliminar, debe asignar otro Contacto como principal.");
		else {
			if (customerId > 0) {
				// Verificar que ya exista un contacto principal, ya que debe haber 1
				int existContactMain = getCustomerContactMain(bmoCustomerContact.getCustomerId().toInteger());
				if (!(existContactMain > 0))
					bmUpdateResult.addError(bmoCustomerContact.getContactMain().getName(), 
							"No se puede eliminar, debe asignar un Contacto como principal.");
			}
		}
		if (!bmUpdateResult.hasErrors())
			super.delete(pmConn, bmoCustomerContact, bmUpdateResult);
		
		if (bmUpdateResult.hasErrors())			
			printDevLog(this.getClass().getName() + " - delete() " + bmUpdateResult.errorsToString() );

		return bmUpdateResult;
	}
}
