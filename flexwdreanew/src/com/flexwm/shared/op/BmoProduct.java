/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoProduct extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, description, displayName, brand, model, track, type, 
	cost, rentalCost, enabled, image,
	renewOrder, renewRate, orderTypeId, wFlowTypeId,
	reorder, inventory, productFamilyId, productGroupId, unitId, supplierId, 
	stockMax, stockMin, commision, areaId, budgetItemId,consumable,weight,cubicMeter,dimensionLength,
	dimensionHeight,dimensionWidth,amperage110,amperage220,useCase,quantityForCase,weightCase,caseLength,caseHeight,caseWidth,caseCubicMeter;

	BmoProductFamily bmoProductFamily = new BmoProductFamily();
	BmoProductGroup bmoProductGroup = new BmoProductGroup();
	BmoUnit bmoUnit = new BmoUnit();

	public static char TRACK_NONE = 'N';
	public static char TRACK_SERIAL = 'S';
	public static char TRACK_BATCH = 'B';

	public static char TYPE_PRODUCT = 'P';
	public static char TYPE_COMPOSED = 'M';
	public static char TYPE_SUBPRODUCT = 'B';
	public static char TYPE_SERVICE = 'S';
	public static char TYPE_EXTERNAL = 'E';
	public static char TYPE_CLASS = 'C';
	public static char TYPE_COMPLEMENTARY = 'Y';
	public static char TYPE_COMPLEMENTARY_QUOTABLE = 'Q';

	public static String ACTION_GETPRODUCT = "GETPRODUCT";

	public static String CODE_PREFIX = "PR-";

	public BmoProduct() {
		super("com.flexwm.server.op.PmProduct", "products", "productid", "PROD", "Productos");

		code = setField("code", "", "Clave Prod.", 10, Types.VARCHAR, true, BmFieldType.CODE, true);
		name = setField("name", "", "Nombre Prod.", 60, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);		
		displayName = setField("displayname", "", "N. Comercial", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		brand = setField("brand", "", "Marca", 50, Types.VARCHAR, true, BmFieldType.STRING, false);		
		model = setField("model", "", "Modelo", 50, Types.VARCHAR, true, BmFieldType.STRING, false);		
		track = setField("track", "", "Rastreo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		track.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TRACK_NONE, "S/Rastreo", "./icons/prod_track_none.png"),
				new BmFieldOption(TRACK_SERIAL, "#Serie", "./icons/prod_track_serial.png"),
				new BmFieldOption(TRACK_BATCH, "Lote", "./icons/prod_track_batch.png")
				)));
		type = setField("type", "" + TYPE_PRODUCT, "Tipo", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PRODUCT, "Producto", "./icons/prod_type_product.png"),
				new BmFieldOption(TYPE_COMPOSED, "Compuesto", "./icons/prod_type_composed.png"),
				new BmFieldOption(TYPE_SUBPRODUCT, "Sub-Producto", "./icons/prod_type_subproduct.png"),
//				new BmFieldOption(TYPE_SERVICE, "Servicio Interno", "./icons/prod_type_service.png"),
//				new BmFieldOption(TYPE_EXTERNAL, "Servicio Externo", "./icons/prod_type_external.png"),
//				new BmFieldOption(TYPE_CLASS, "Clase", "./icons/prod_type_class.png"),
				new BmFieldOption(TYPE_COMPLEMENTARY, "Complementario/Auxiliar", "./icons/prod_type_complementary.png"),
				new BmFieldOption(TYPE_COMPLEMENTARY_QUOTABLE, "Complementario Cotizable", "./icons/prod_type_complementary_quotable.png")
				)));

		rentalCost = setField("rentalcost", "0", "Costo Renta", 20, Types.FLOAT, false, BmFieldType.CURRENCY, false);
		cost = setField("cost", "0", "Costo Compra", 20, Types.FLOAT, false, BmFieldType.CURRENCY, false);

		reorder = setField("reorder", "0", "Punto Reorden", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		inventory = setField("inventory", "0", "A. Inventario", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		image = setField("image", "", "Imagen", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
		enabled = setField("enabled", "1", "Activo?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		unitId = setField("unitid", "", "Unidad", 8, Types.INTEGER, false, BmFieldType.ID, false);
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
		productFamilyId = setField("productfamilyid", "", "Familia Producto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		productGroupId = setField("productgroupid", "", "Grupo Producto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		stockMax = setField("stockmax", "0", "Stock Max", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		stockMin = setField("stockmin", "0", "Stock Min", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		renewOrder = setField("reneworder", "0", "Renovar Pedido", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		renewRate = setField("renewrate", "", "% Incremento", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		orderTypeId = setField("ordertypeid", "", "Tipo Pedido", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo WFlow", 8, Types.INTEGER, true, BmFieldType.ID, false);
		commision = setField("commission", "", "Comisión ?", 8, Types.BOOLEAN, true, BmFieldType.BOOLEAN, false);
		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = setField("budgetitemid", "", "Partida Presup.", 11, Types.INTEGER, true, BmFieldType.ID, false);
		consumable = setField("consumable", "0", "Consumible?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		weight = setField("weight", "", "Peso (Kg)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		cubicMeter = setField("cubicmeter", "0", " Mts³", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		dimensionLength = setField("dimensionlength", "0", "Largo (Mts)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		dimensionHeight = setField("dimensionheight", "0", "Alto (Mts)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		dimensionWidth = setField("dimensionwidth", "0", "Ancho (Mts)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		amperage110 = setField("amperage110", "", "Amperaje 110v", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		amperage220 = setField("amperage220", "", "Amperaje 220v", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		useCase = setField("usecase", "", "Case", 1, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		quantityForCase = setField("quantityforcase", "", "Cantidad por Case", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		weightCase = setField("weightcase", "", "Peso Case (kg)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		caseLength = setField("caselength", "0", "Largo Case (Mts)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		caseHeight = setField("caseheight", "0", "Alto Case (Mts)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		caseWidth = setField("casewidth", "0", "Ancho case (Mts)", 11, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		caseCubicMeter = setField("casecubicmeter", "0", "Case Mts³", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
	}
	//useCase,quantityForCase,weightCase,caseLength,caseHeight,caseseWidth,caseCubicMeter;
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getDescription(),
				getBrand(),
				getModel(),
				getBmoProductFamily().getName(),
				getBmoProductGroup().getName(),
				getBmoUnit().getCode(), 
				getBmoUnit().getFraction(),
				getTrack(),
				getType(),
				getEnabled(),
				getInventory(), 
				getCommision(),
				getConsumable()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getName()),
				new BmSearchField(getBrand()),
				new BmSearchField(getModel()),
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(),
				getDescription(),
				getBrand(),
				getModel()
				));
	}

	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getTrack(),
				getEnabled()
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getProductFamilyId() {
		return productFamilyId;
	}

	public void setProductFamilyId(BmField productFamilyId) {
		this.productFamilyId = productFamilyId;
	}

	public BmoProductFamily getBmoProductFamily() {
		return bmoProductFamily;
	}

	public void setBmoProductFamily(BmoProductFamily bmoProductFamily) {
		this.bmoProductFamily = bmoProductFamily;
	}

	public BmField getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(BmField productGroupId) {
		this.productGroupId = productGroupId;
	}

	public BmoProductGroup getBmoProductGroup() {
		return bmoProductGroup;
	}

	public void setBmoProductGroup(BmoProductGroup bmoProductGroup) {
		this.bmoProductGroup = bmoProductGroup;
	}

	public BmField getCost() {
		return cost;
	}

	public void setCost(BmField cost) {
		this.cost = cost;
	}

	public BmField getTrack() {
		return track;
	}

	public void setTrack(BmField track) {
		this.track = track;
	}

	public BmField getReorder() {
		return reorder;
	}

	public void setReorder(BmField reorder) {
		this.reorder = reorder;
	}

	public BmField getInventory() {
		return inventory;
	}

	public void setInventory(BmField inventory) {
		this.inventory = inventory;
	}

	public BmField getBrand() {
		return brand;
	}

	public void setBrand(BmField brand) {
		this.brand = brand;
	}

	public BmField getModel() {
		return model;
	}

	public void setModel(BmField model) {
		this.model = model;
	}

	public BmField getUnitId() {
		return unitId;
	}

	public void setUnitId(BmField unitId) {
		this.unitId = unitId;
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

	public BmField getRentalCost() {
		return rentalCost;
	}

	public void setRentalCost(BmField rentalCost) {
		this.rentalCost = rentalCost;
	}

	public BmField getEnabled() {
		return enabled;
	}

	public void setEnabled(BmField enabled) {
		this.enabled = enabled;
	}

	public BmField getImage() {
		return image;
	}

	public void setImage(BmField image) {
		this.image = image;
	}

	public BmField getDisplayName() {
		return displayName;
	}

	public void setDisplayName(BmField displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the stockMax
	 */
	public BmField getStockMax() {
		return stockMax;
	}

	/**
	 * @param stockMax the stockMax to set
	 */
	public void setStockMax(BmField stockMax) {
		this.stockMax = stockMax;
	}

	/**
	 * @return the stockMin
	 */
	public BmField getStockMin() {
		return stockMin;
	}

	/**
	 * @param stockMin the stockMin to set
	 */
	public void setStockMin(BmField stockMin) {
		this.stockMin = stockMin;
	}

	public BmField getRenewOrder() {
		return renewOrder;
	}

	public void setRenewOrder(BmField renewOrder) {
		this.renewOrder = renewOrder;
	}

	public BmField getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmField getCommision() {
		return commision;
	}

	public void setCommision(BmField commision) {
		this.commision = commision;
	}

	public BmField getRenewRate() {
		return renewRate;
	}

	public void setRenewRate(BmField renewRate) {
		this.renewRate = renewRate;
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getBudgetItemId() {
		return budgetItemId;
	}

	public void setBudgetItemId(BmField budgetItemId) {
		this.budgetItemId = budgetItemId;
	}

	public BmoUnit getBmoUnit() {
		return bmoUnit;
	}

	public void setBmoUnit(BmoUnit bmoUnit) {
		this.bmoUnit = bmoUnit;
	}

	public BmField getConsumable() {
		return consumable;
	}

	public void setConsumable(BmField consumable) {
		this.consumable = consumable;
	}
	public BmField getWeight() {
		return weight;
	}
	public void setWeight(BmField weight) {
		this.weight = weight;
	}
	public BmField getCubicMeter() {
		return cubicMeter;
	}
	public void setCubicMeter(BmField cubicMeter) {
		this.cubicMeter = cubicMeter;
	}
	public BmField getDimensionLength() {
		return dimensionLength;
	}
	public void setDimensionLength(BmField dimensionLength) {
		this.dimensionLength = dimensionLength;
	}
	public BmField getDimensionHeight() {
		return dimensionHeight;
	}
	public void setDimensionHeight(BmField dimensionHeight) {
		this.dimensionHeight = dimensionHeight;
	}
	public BmField getDimensionWidth() {
		return dimensionWidth;
	}
	public void setDimensionWidth(BmField dimensionWidth) {
		this.dimensionWidth = dimensionWidth;
	}	
	public BmField getAmperage110() {
		return amperage110;
	}
	public void setAmperage110(BmField amperage110) {
		this.amperage110 = amperage110;
	}
	public BmField getAmperage220() {
		return amperage220;
	}
	public void setAmperage220(BmField amperage220) {
		this.amperage220 = amperage220;
	}
	public BmField getUseCase() {
		return useCase;
	}
	public void setUseCase(BmField useCase) {
		this.useCase = useCase;
	}
	public BmField getQuantityForCase() {
		return quantityForCase;
	}
	public void setQuantityForCase(BmField quantityForCase) {
		this.quantityForCase = quantityForCase;
	}
	public BmField getWeightCase() {
		return weightCase;
	}
	public void setWeightCase(BmField weightCase) {
		this.weightCase = weightCase;
	}
	public BmField getCaseLength() {
		return caseLength;
	}
	public void setCaseLength(BmField caseLength) {
		this.caseLength = caseLength;
	}
	public BmField getCaseHeight() {
		return caseHeight;
	}
	public void setCaseHeight(BmField caseHeight) {
		this.caseHeight = caseHeight;
	}
	public BmField getCaseWidth() {
		return caseWidth;
	}
	public void setCaseWidth(BmField caseseWidth) {
		this.caseWidth = caseseWidth;
	}
	public BmField getCaseCubicMeter() {
		return caseCubicMeter;
	}
	public void setCaseCubicMeter(BmField caseCubicMeter) {
		this.caseCubicMeter = caseCubicMeter;
	}
	
}
