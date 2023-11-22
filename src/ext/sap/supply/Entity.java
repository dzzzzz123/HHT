package ext.sap.supply;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entity implements Serializable {
	@JsonProperty("MATNR")
	private String PartNumber;
	@JsonProperty("BALANCE")
	private String MRPBalanceQuantity;
	@JsonProperty("VERPR")
	private String UnitPrice;
	@JsonProperty("MENGE")
	private String RequiredQuantity;
	@JsonProperty("STOCK")
	private String STOCK;
	@JsonProperty("PRSL")
	private String PRQuantity;
	@JsonProperty("WQPOSL")
	private String OpenPOQuantity;
	@JsonProperty("ZPRM3")
	private String RedundantInventory;
	@JsonProperty("ZPRM2")
	private String RedundantPO;
	@JsonProperty("ZPRM1")
	private String RedundantPR;
	@JsonProperty("VMISL")
	private String VMIInventory;
	@JsonProperty("IT_MRP2")
	private List<IT_MRP2> IT_MRP2;

	public Entity() {
		super();
	}

	public Entity(String partNumber, String mRPBalanceQuantity, String unitPrice, String requiredQuantity, String sTOCK,
			String pRQuantity, String openPOQuantity, String redundantInventory, String redundantPO, String redundantPR,
			String vMIInventory, List<Entity.IT_MRP2> iT_MRP2) {
		super();
		PartNumber = partNumber;
		MRPBalanceQuantity = mRPBalanceQuantity;
		UnitPrice = unitPrice;
		RequiredQuantity = requiredQuantity;
		STOCK = sTOCK;
		PRQuantity = pRQuantity;
		OpenPOQuantity = openPOQuantity;
		RedundantInventory = redundantInventory;
		RedundantPO = redundantPO;
		RedundantPR = redundantPR;
		VMIInventory = vMIInventory;
		IT_MRP2 = iT_MRP2;
	}

	public String getPartNumber() {
		return PartNumber;
	}

	public void setPartNumber(String partNumber) {
		PartNumber = partNumber;
	}

	public String getMRPBalanceQuantity() {
		return MRPBalanceQuantity;
	}

	public void setMRPBalanceQuantity(String mRPBalanceQuantity) {
		MRPBalanceQuantity = mRPBalanceQuantity;
	}

	public String getUnitPrice() {
		return UnitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		UnitPrice = unitPrice;
	}

	public String getRequiredQuantity() {
		return RequiredQuantity;
	}

	public void setRequiredQuantity(String requiredQuantity) {
		RequiredQuantity = requiredQuantity;
	}

	public String getSTOCK() {
		return STOCK;
	}

	public void setSTOCK(String sTOCK) {
		STOCK = sTOCK;
	}

	public String getPRQuantity() {
		return PRQuantity;
	}

	public void setPRQuantity(String pRQuantity) {
		PRQuantity = pRQuantity;
	}

	public String getOpenPOQuantity() {
		return OpenPOQuantity;
	}

	public void setOpenPOQuantity(String openPOQuantity) {
		OpenPOQuantity = openPOQuantity;
	}

	public String getRedundantInventory() {
		return RedundantInventory;
	}

	public void setRedundantInventory(String redundantInventory) {
		RedundantInventory = redundantInventory;
	}

	public String getRedundantPO() {
		return RedundantPO;
	}

	public void setRedundantPO(String redundantPO) {
		RedundantPO = redundantPO;
	}

	public String getRedundantPR() {
		return RedundantPR;
	}

	public void setRedundantPR(String redundantPR) {
		RedundantPR = redundantPR;
	}

	public String getVMIInventory() {
		return VMIInventory;
	}

	public void setVMIInventory(String vMIInventory) {
		VMIInventory = vMIInventory;
	}

	public List<IT_MRP2> getIT_MRP2() {
		return IT_MRP2;
	}

	public void setIT_MRP2(List<IT_MRP2> iT_MRP2) {
		IT_MRP2 = iT_MRP2;
	}

	@Override
	public String toString() {
		return "Entity [PartNumber=" + PartNumber + ", MRPBalanceQuantity=" + MRPBalanceQuantity + ", UnitPrice="
				+ UnitPrice + ", RequiredQuantity=" + RequiredQuantity + ", STOCK=" + STOCK + ", PRQuantity="
				+ PRQuantity + ", OpenPOQuantity=" + OpenPOQuantity + ", RedundantInventory=" + RedundantInventory
				+ ", RedundantPO=" + RedundantPO + ", RedundantPR=" + RedundantPR + ", VMIInventory=" + VMIInventory
				+ ", IT_MRP2=" + IT_MRP2 + "]";
	}

	public static class IT_MRP2 implements Serializable {
		@JsonProperty("MATNR")
		private String PartNumber;
		@JsonProperty("MAKTX")
		private String PartDescription;
		@JsonProperty("WERKS")
		private String Factory;
		@JsonProperty("BALANCE")
		private String MRPBalancingQuantity;
		@JsonProperty("ZPRM2")
		private String RedundantPO;
		@JsonProperty("ZPRM3")
		private String RedundantInventory;
		@JsonProperty("ZPRM1")
		private String RedundantPR;
		@JsonProperty("STOCK")
		private String STOCK;
		@JsonProperty("PURMRK")
		private String FixedPurchaseRequisition;
		@JsonProperty("PURRQS")
		private String PurchaseRequisition;
		@JsonProperty("PLDORD")
		private String PlannedOrder;
		@JsonProperty("PLDMRK")
		private String FixedPlannedOrder;
		@JsonProperty("POITEM")
		private String PurchaseOrder;
		@JsonProperty("PRDORD")
		private String ProductionOrder;
		@JsonProperty("INDREQ")
		private String PlanIndependentReq;
		@JsonProperty("DEPREQ")
		private String RelatedRequirement;
		@JsonProperty("ORDRES")
		private String OrderReservation;
		@JsonProperty("MTLRES")
		private String Reservation;
		@JsonProperty("SAFEST")
		private String SafetyStock;
		@JsonProperty("PRQREL")
		private String TransPurchaseRequisition;
		@JsonProperty("ORDDS")
		private String TransOrderRequirement;
		@JsonProperty("SOITEM")
		private String SalesOrderRequirement;
		@JsonProperty("GRNUM")
		private String OutDeliveryRequirement;

		public IT_MRP2() {
			super();
		}

		public IT_MRP2(String partNumber, String partDescription, String factory, String mRPBalancingQuantity,
				String redundantPO, String redundantInventory, String redundantPR, String sTOCK,
				String fixedPurchaseRequisition, String purchaseRequisition, String plannedOrder,
				String fixedPlannedOrder, String purchaseOrder, String productionOrder, String planIndependentReq,
				String relatedRequirement, String orderReservation, String reservation, String safetyStock,
				String transPurchaseRequisition, String transOrderRequirement, String salesOrderRequirement,
				String outDeliveryRequirement) {
			super();
			PartNumber = partNumber;
			PartDescription = partDescription;
			Factory = factory;
			MRPBalancingQuantity = mRPBalancingQuantity;
			RedundantPO = redundantPO;
			RedundantInventory = redundantInventory;
			RedundantPR = redundantPR;
			STOCK = sTOCK;
			FixedPurchaseRequisition = fixedPurchaseRequisition;
			PurchaseRequisition = purchaseRequisition;
			PlannedOrder = plannedOrder;
			FixedPlannedOrder = fixedPlannedOrder;
			PurchaseOrder = purchaseOrder;
			ProductionOrder = productionOrder;
			PlanIndependentReq = planIndependentReq;
			RelatedRequirement = relatedRequirement;
			OrderReservation = orderReservation;
			Reservation = reservation;
			SafetyStock = safetyStock;
			TransPurchaseRequisition = transPurchaseRequisition;
			TransOrderRequirement = transOrderRequirement;
			SalesOrderRequirement = salesOrderRequirement;
			OutDeliveryRequirement = outDeliveryRequirement;
		}

		public String getPartNumber() {
			return PartNumber;
		}

		public void setPartNumber(String partNumber) {
			PartNumber = partNumber;
		}

		public String getPartDescription() {
			return PartDescription;
		}

		public void setPartDescription(String partDescription) {
			PartDescription = partDescription;
		}

		public String getFactory() {
			return Factory;
		}

		public void setFactory(String factory) {
			Factory = factory;
		}

		public String getMRPBalancingQuantity() {
			return MRPBalancingQuantity;
		}

		public void setMRPBalancingQuantity(String mRPBalancingQuantity) {
			MRPBalancingQuantity = mRPBalancingQuantity;
		}

		public String getRedundantPO() {
			return RedundantPO;
		}

		public void setRedundantPO(String redundantPO) {
			RedundantPO = redundantPO;
		}

		public String getRedundantInventory() {
			return RedundantInventory;
		}

		public void setRedundantInventory(String redundantInventory) {
			RedundantInventory = redundantInventory;
		}

		public String getRedundantPR() {
			return RedundantPR;
		}

		public void setRedundantPR(String redundantPR) {
			RedundantPR = redundantPR;
		}

		public String getSTOCK() {
			return STOCK;
		}

		public void setSTOCK(String sTOCK) {
			STOCK = sTOCK;
		}

		public String getFixedPurchaseRequisition() {
			return FixedPurchaseRequisition;
		}

		public void setFixedPurchaseRequisition(String fixedPurchaseRequisition) {
			FixedPurchaseRequisition = fixedPurchaseRequisition;
		}

		public String getPurchaseRequisition() {
			return PurchaseRequisition;
		}

		public void setPurchaseRequisition(String purchaseRequisition) {
			PurchaseRequisition = purchaseRequisition;
		}

		public String getPlannedOrder() {
			return PlannedOrder;
		}

		public void setPlannedOrder(String plannedOrder) {
			PlannedOrder = plannedOrder;
		}

		public String getFixedPlannedOrder() {
			return FixedPlannedOrder;
		}

		public void setFixedPlannedOrder(String fixedPlannedOrder) {
			FixedPlannedOrder = fixedPlannedOrder;
		}

		public String getPurchaseOrder() {
			return PurchaseOrder;
		}

		public void setPurchaseOrder(String purchaseOrder) {
			PurchaseOrder = purchaseOrder;
		}

		public String getProductionOrder() {
			return ProductionOrder;
		}

		public void setProductionOrder(String productionOrder) {
			ProductionOrder = productionOrder;
		}

		public String getPlanIndependentReq() {
			return PlanIndependentReq;
		}

		public void setPlanIndependentReq(String planIndependentReq) {
			PlanIndependentReq = planIndependentReq;
		}

		public String getRelatedRequirement() {
			return RelatedRequirement;
		}

		public void setRelatedRequirement(String relatedRequirement) {
			RelatedRequirement = relatedRequirement;
		}

		public String getOrderReservation() {
			return OrderReservation;
		}

		public void setOrderReservation(String orderReservation) {
			OrderReservation = orderReservation;
		}

		public String getReservation() {
			return Reservation;
		}

		public void setReservation(String reservation) {
			Reservation = reservation;
		}

		public String getSafetyStock() {
			return SafetyStock;
		}

		public void setSafetyStock(String safetyStock) {
			SafetyStock = safetyStock;
		}

		public String getTransPurchaseRequisition() {
			return TransPurchaseRequisition;
		}

		public void setTransPurchaseRequisition(String transPurchaseRequisition) {
			TransPurchaseRequisition = transPurchaseRequisition;
		}

		public String getTransOrderRequirement() {
			return TransOrderRequirement;
		}

		public void setTransOrderRequirement(String transOrderRequirement) {
			TransOrderRequirement = transOrderRequirement;
		}

		public String getSalesOrderRequirement() {
			return SalesOrderRequirement;
		}

		public void setSalesOrderRequirement(String salesOrderRequirement) {
			SalesOrderRequirement = salesOrderRequirement;
		}

		public String getOutDeliveryRequirement() {
			return OutDeliveryRequirement;
		}

		public void setOutDeliveryRequirement(String outDeliveryRequirement) {
			OutDeliveryRequirement = outDeliveryRequirement;
		}

		@Override
		public String toString() {
			return "IT_MRP2 [PartNumber=" + PartNumber + ", PartDescription=" + PartDescription + ", Factory=" + Factory
					+ ", MRPBalancingQuantity=" + MRPBalancingQuantity + ", RedundantPO=" + RedundantPO
					+ ", RedundantInventory=" + RedundantInventory + ", RedundantPR=" + RedundantPR + ", STOCK=" + STOCK
					+ ", FixedPurchaseRequisition=" + FixedPurchaseRequisition + ", PurchaseRequisition="
					+ PurchaseRequisition + ", PlannedOrder=" + PlannedOrder + ", FixedPlannedOrder="
					+ FixedPlannedOrder + ", PurchaseOrder=" + PurchaseOrder + ", ProductionOrder=" + ProductionOrder
					+ ", PlanIndependentReq=" + PlanIndependentReq + ", RelatedRequirement=" + RelatedRequirement
					+ ", OrderReservation=" + OrderReservation + ", Reservation=" + Reservation + ", SafetyStock="
					+ SafetyStock + ", TransPurchaseRequisition=" + TransPurchaseRequisition
					+ ", TransOrderRequirement=" + TransOrderRequirement + ", SalesOrderRequirement="
					+ SalesOrderRequirement + ", OutDeliveryRequirement=" + OutDeliveryRequirement + "]";
		}

	}
}
