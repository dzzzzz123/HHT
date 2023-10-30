package ext.sap.supply;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SupplyEntity {
	@JsonProperty("MATNR")
	private String MATNR;
	@JsonProperty("BALANCE")
	private String BALANCE;
	@JsonProperty("VERPR")
	private String VERPR;
	@JsonProperty("MENGE")
	private String MENGE;
	@JsonProperty("STOCK")
	private String STOCK;
	@JsonProperty("PRSL")
	private String PRSL;
	@JsonProperty("WQPOSL")
	private String WQPOSL;
	@JsonProperty("ZPRM3")
	private String ZPRM3;
	@JsonProperty("ZPRM2")
	private String ZPRM2;
	@JsonProperty("ZPRM1")
	private String ZPRM1;
	@JsonProperty("VMISL")
	private String VMISL;
	@JsonProperty("IT_MRP2")
	private MRPItem[] IT_MRP2;

	class MRPItem {
		@JsonProperty("MATNR")
		private String MATNR;
		@JsonProperty("MAKTX")
		private String MAKTX;
		@JsonProperty("WERKS")
		private String WERKS;
		@JsonProperty("BALANCE")
		private String BALANCE;
		@JsonProperty("ZPRM2")
		private String ZPRM2;
		@JsonProperty("ZPRM3")
		private String ZPRM3;
		@JsonProperty("ZPRM1")
		private String ZPRM1;
		@JsonProperty("STOCK")
		private String STOCK;
		@JsonProperty("PURMRK")
		private String PURMRK;
		@JsonProperty("PURRQS")
		private String PURRQS;
		@JsonProperty("PLDORD")
		private String PLDORD;
		@JsonProperty("PLDMRK")
		private String PLDMRK;
		@JsonProperty("POITEM")
		private String POITEM;
		@JsonProperty("PRDORD")
		private String PRDORD;
		@JsonProperty("INDREQ")
		private String INDREQ;
		@JsonProperty("DEPREQ")
		private String DEPREQ;
		@JsonProperty("ORDRES")
		private String ORDRES;
		@JsonProperty("MTLRES")
		private String MTLRES;
		@JsonProperty("SAFEST")
		private String SAFEST;
		@JsonProperty("PRQREL")
		private String PRQREL;
		@JsonProperty("ORDDS")
		private String ORDDS;
		@JsonProperty("SOITEM")
		private String SOITEM;
		@JsonProperty("GRNUM")
		private String GRNUM;
	}
}
