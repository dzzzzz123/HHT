package ext.sap.masterData;

import com.fasterxml.jackson.annotation.JsonGetter;

public class SendSAPPartEntity {

	private String PartType;
	private String HHT_Classification;
	private String Number;
	private String Name;
	private String revision;
	private String Unit;
	private String HHT_Bonded;
	private String HHT_GrossWeight;
	private String HHT_NetWeight;
	private String HHT_WeightUnit;
	private String HHT_Traffic;
	private String HHT_VolumeUnit;
	private String HHT_Length;
	private String HHT_Width;
	private String HHT_Height;
	private String HHT_SizeUnits;
	private String state;
	private String HHT_ClassificationCode;
	private String HHT_ClassificationName;
	private String HHT_ProductLineNumber;
	private String HHT_ProductLineName;
	private String HHT_ProductNumber;
	private String HHT_Productdescription;
	private String HHT_ModelSpecification;
	private String HHT_CommodityName;
	private String HHT_Brand;
	private String HHT_Year;
	private String HHT_Size;
	private String HHT_FinishedSeries;
	private String HHT_Industry;
	private String HHT_ProductDevelopmentType;
	private String HHT_CustomizedProductIdentifier;
	private String HHT_SupplierSku;
	private String DefaultTraceCode;
	private String HHT_Factory;
	private String HHT_Price;
	private String HHT_PriceUnit;
	private String HHT_INValue;

	public SendSAPPartEntity() {
	}

	public SendSAPPartEntity(String partType, String hHT_Classification, String number, String name, String revision,
			String unit, String hHT_Bonded, String hHT_GrossWeight, String hHT_NetWeight, String hHT_WeightUnit,
			String hHT_Traffic, String hHT_VolumeUnit, String hHT_Length, String hHT_Width, String hHT_Height,
			String hHT_SizeUnits, String state, String hHT_ClassificationCode, String hHT_ClassificationName,
			String hHT_ProductLineNumber, String hHT_ProductLineName, String hHT_ProductNumber,
			String hHT_Productdescription, String hHT_ModelSpecification, String hHT_CommodityName, String hHT_Brand,
			String hHT_Year, String hHT_Size, String hHT_FinishedSeries, String hHT_Industry,
			String hHT_ProductDevelopmentType, String hHT_CustomizedProductIdentifier, String hHT_SupplierSku,
			String defaultTraceCode, String hHT_Factory, String hHT_Price, String hHT_PriceUnit, String hHT_INValue) {
		PartType = partType;
		HHT_Classification = hHT_Classification;
		Number = number;
		Name = name;
		this.revision = revision;
		Unit = unit;
		HHT_Bonded = hHT_Bonded;
		HHT_GrossWeight = hHT_GrossWeight;
		HHT_NetWeight = hHT_NetWeight;
		HHT_WeightUnit = hHT_WeightUnit;
		HHT_Traffic = hHT_Traffic;
		HHT_VolumeUnit = hHT_VolumeUnit;
		HHT_Length = hHT_Length;
		HHT_Width = hHT_Width;
		HHT_Height = hHT_Height;
		HHT_SizeUnits = hHT_SizeUnits;
		this.state = state;
		HHT_ClassificationCode = hHT_ClassificationCode;
		HHT_ClassificationName = hHT_ClassificationName;
		HHT_ProductLineNumber = hHT_ProductLineNumber;
		HHT_ProductLineName = hHT_ProductLineName;
		HHT_ProductNumber = hHT_ProductNumber;
		HHT_Productdescription = hHT_Productdescription;
		HHT_ModelSpecification = hHT_ModelSpecification;
		HHT_CommodityName = hHT_CommodityName;
		HHT_Brand = hHT_Brand;
		HHT_Year = hHT_Year;
		HHT_Size = hHT_Size;
		HHT_FinishedSeries = hHT_FinishedSeries;
		HHT_Industry = hHT_Industry;
		HHT_ProductDevelopmentType = hHT_ProductDevelopmentType;
		HHT_CustomizedProductIdentifier = hHT_CustomizedProductIdentifier;
		HHT_SupplierSku = hHT_SupplierSku;
		DefaultTraceCode = defaultTraceCode;
		HHT_Factory = hHT_Factory;
		HHT_Price = hHT_Price;
		HHT_PriceUnit = hHT_PriceUnit;
		HHT_INValue = hHT_INValue;
	}

	@JsonGetter("MTART")
	public String getPartType() {
		return PartType;
	}

	public void setPartType(String partType) {
		PartType = partType;
	}

	@JsonGetter("MATKL")
	public String getHHT_Classification() {
		return HHT_Classification;
	}

	public void setHHT_Classification(String hHT_Classification) {
		HHT_Classification = hHT_Classification;
	}

	@JsonGetter("MATNR")
	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
	}

	@JsonGetter("MAKTX")
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	@JsonGetter("ZEIVR")
	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	@JsonGetter("MEINS")
	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	@JsonGetter("ZZJHBS")
	public String getHHT_Bonded() {
		return HHT_Bonded;
	}

	public void setHHT_Bonded(String hHT_Bonded) {
		HHT_Bonded = hHT_Bonded;
	}

	@JsonGetter("BRGEW")
	public String getHHT_GrossWeight() {
		return HHT_GrossWeight;
	}

	public void setHHT_GrossWeight(String hHT_GrossWeight) {
		HHT_GrossWeight = hHT_GrossWeight;
	}

	@JsonGetter("NTGEW")
	public String getHHT_NetWeight() {
		return HHT_NetWeight;
	}

	public void setHHT_NetWeight(String hHT_NetWeight) {
		HHT_NetWeight = hHT_NetWeight;
	}

	@JsonGetter("GEWEI")
	public String getHHT_WeightUnit() {
		return HHT_WeightUnit;
	}

	public void setHHT_WeightUnit(String hHT_WeightUnit) {
		HHT_WeightUnit = hHT_WeightUnit;
	}

	@JsonGetter("VOLUM")
	public String getHHT_Traffic() {
		return HHT_Traffic;
	}

	public void setHHT_Traffic(String hHT_Traffic) {
		HHT_Traffic = hHT_Traffic;
	}

	@JsonGetter("VOLEH")
	public String getHHT_VolumeUnit() {
		return HHT_VolumeUnit;
	}

	public void setHHT_VolumeUnit(String hHT_VolumeUnit) {
		HHT_VolumeUnit = hHT_VolumeUnit;
	}

	@JsonGetter("LAENG")
	public String getHHT_Length() {
		return HHT_Length;
	}

	public void setHHT_Length(String hHT_Length) {
		HHT_Length = hHT_Length;
	}

	@JsonGetter("BREIT")
	public String getHHT_Width() {
		return HHT_Width;
	}

	public void setHHT_Width(String hHT_Width) {
		HHT_Width = hHT_Width;
	}

	@JsonGetter("HOEHE")
	public String getHHT_Height() {
		return HHT_Height;
	}

	public void setHHT_Height(String hHT_Height) {
		HHT_Height = hHT_Height;
	}

	@JsonGetter("MEABM")
	public String getHHT_SizeUnits() {
		return HHT_SizeUnits;
	}

	public void setHHT_SizeUnits(String hHT_SizeUnits) {
		HHT_SizeUnits = hHT_SizeUnits;
	}

	@JsonGetter("MSTAE")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@JsonGetter("ZZWLFLBM")
	public String getHHT_ClassificationCode() {
		return HHT_ClassificationCode;
	}

	public void setHHT_ClassificationCode(String hHT_ClassificationCode) {
		HHT_ClassificationCode = hHT_ClassificationCode;
	}

	@JsonGetter("ZZWLFLMC")
	public String getHHT_ClassificationName() {
		return HHT_ClassificationName;
	}

	public void setHHT_ClassificationName(String hHT_ClassificationName) {
		HHT_ClassificationName = hHT_ClassificationName;
	}

	@JsonGetter("ZZCPXBM")
	public String getHHT_ProductLineNumber() {
		return HHT_ProductLineNumber;
	}

	public void setHHT_ProductLineNumber(String hHT_ProductLineNumber) {
		HHT_ProductLineNumber = hHT_ProductLineNumber;
	}

	@JsonGetter("ZZCPXMC")
	public String getHHT_ProductLineName() {
		return HHT_ProductLineName;
	}

	public void setHHT_ProductLineName(String hHT_ProductLineName) {
		HHT_ProductLineName = hHT_ProductLineName;
	}

	@JsonGetter("ZZCPBM")
	public String getHHT_ProductNumber() {
		return HHT_ProductNumber;
	}

	public void setHHT_ProductNumber(String hHT_ProductNumber) {
		HHT_ProductNumber = hHT_ProductNumber;
	}

	@JsonGetter("ZZCPMS")
	public String getHHT_Productdescription() {
		return HHT_Productdescription;
	}

	public void setHHT_Productdescription(String hHT_Productdescription) {
		HHT_Productdescription = hHT_Productdescription;
	}

	@JsonGetter("ZZCPXH")
	public String getHHT_ModelSpecification() {
		return HHT_ModelSpecification;
	}

	public void setHHT_ModelSpecification(String hHT_ModelSpecification) {
		HHT_ModelSpecification = hHT_ModelSpecification;
	}

	@JsonGetter("ZZHPMC")
	public String getHHT_CommodityName() {
		return HHT_CommodityName;
	}

	public void setHHT_CommodityName(String hHT_CommodityName) {
		HHT_CommodityName = hHT_CommodityName;
	}

	@JsonGetter("ZZPP")
	public String getHHT_Brand() {
		return HHT_Brand;
	}

	public void setHHT_Brand(String hHT_Brand) {
		HHT_Brand = hHT_Brand;
	}

	@JsonGetter("ZZNF")
	public String getHHT_Year() {
		return HHT_Year;
	}

	public void setHHT_Year(String hHT_Year) {
		HHT_Year = hHT_Year;
	}

	@JsonGetter("ZZCC")
	public String getHHT_Size() {
		return HHT_Size;
	}

	public void setHHT_Size(String hHT_Size) {
		HHT_Size = hHT_Size;
	}

	@JsonGetter("ZZCPXL")
	public String getHHT_FinishedSeries() {
		return HHT_FinishedSeries;
	}

	public void setHHT_FinishedSeries(String hHT_FinishedSeries) {
		HHT_FinishedSeries = hHT_FinishedSeries;
	}

	@JsonGetter("ZZHY")
	public String getHHT_Industry() {
		return HHT_Industry;
	}

	public void setHHT_Industry(String hHT_Industry) {
		HHT_Industry = hHT_Industry;
	}

	@JsonGetter("ZZCPKFLX")
	public String getHHT_ProductDevelopmentType() {
		return HHT_ProductDevelopmentType;
	}

	public void setHHT_ProductDevelopmentType(String hHT_ProductDevelopmentType) {
		HHT_ProductDevelopmentType = hHT_ProductDevelopmentType;
	}

	@JsonGetter("ZZDZCPBS")
	public String getHHT_CustomizedProductIdentifier() {
		return HHT_CustomizedProductIdentifier;
	}

	public void setHHT_CustomizedProductIdentifier(String hHT_CustomizedProductIdentifier) {
		HHT_CustomizedProductIdentifier = hHT_CustomizedProductIdentifier;
	}

	@JsonGetter("ZZGYSHH")
	public String getHHT_SupplierSku() {
		return HHT_SupplierSku;
	}

	public void setHHT_SupplierSku(String hHT_SupplierSku) {
		HHT_SupplierSku = hHT_SupplierSku;
	}

	@JsonGetter("SERNP")
	public String getDefaultTraceCode() {
		return DefaultTraceCode;
	}

	public void setDefaultTraceCode(String defaultTraceCode) {
		DefaultTraceCode = defaultTraceCode;
	}

	@JsonGetter("ZZCD")
	public String getHHT_Factory() {
		return HHT_Factory;
	}

	public void setHHT_Factory(String hHT_Factory) {
		HHT_Factory = hHT_Factory;
	}

	@JsonGetter("ZZJG")
	public String getHHT_Price() {
		return HHT_Price;
	}

	public void setHHT_Price(String hHT_Price) {
		HHT_Price = hHT_Price;
	}

	@JsonGetter("PEINH")
	public String getHHT_PriceUnit() {
		return HHT_PriceUnit;
	}

	public void setHHT_PriceUnit(String hHT_PriceUnit) {
		HHT_PriceUnit = hHT_PriceUnit;
	}

	public String getHHT_INValue() {
		return HHT_INValue;
	}

	public void setHHT_INValue(String hHT_INValue) {
		HHT_INValue = hHT_INValue;
	}

	@Override
	public String toString() {
		return "SendSAPPartEntity [PartType=" + PartType + ", HHT_Classification=" + HHT_Classification + ", Number="
				+ Number + ", Name=" + Name + ", revision=" + revision + ", Unit=" + Unit + ", HHT_Bonded=" + HHT_Bonded
				+ ", HHT_GrossWeight=" + HHT_GrossWeight + ", HHT_NetWeight=" + HHT_NetWeight + ", HHT_WeightUnit="
				+ HHT_WeightUnit + ", HHT_Traffic=" + HHT_Traffic + ", HHT_VolumeUnit=" + HHT_VolumeUnit
				+ ", HHT_Length=" + HHT_Length + ", HHT_Width=" + HHT_Width + ", HHT_Height=" + HHT_Height
				+ ", HHT_SizeUnits=" + HHT_SizeUnits + ", state=" + state + ", HHT_ClassificationCode="
				+ HHT_ClassificationCode + ", HHT_ClassificationName=" + HHT_ClassificationName
				+ ", HHT_ProductLineNumber=" + HHT_ProductLineNumber + ", HHT_ProductLineName=" + HHT_ProductLineName
				+ ", HHT_ProductNumber=" + HHT_ProductNumber + ", HHT_Productdescription=" + HHT_Productdescription
				+ ", HHT_ModelSpecification=" + HHT_ModelSpecification + ", HHT_CommodityName=" + HHT_CommodityName
				+ ", HHT_Brand=" + HHT_Brand + ", HHT_Year=" + HHT_Year + ", HHT_Size=" + HHT_Size
				+ ", HHT_FinishedSeries=" + HHT_FinishedSeries + ", HHT_Industry=" + HHT_Industry
				+ ", HHT_ProductDevelopmentType=" + HHT_ProductDevelopmentType + ", HHT_CustomizedProductIdentifier="
				+ HHT_CustomizedProductIdentifier + ", HHT_SupplierSku=" + HHT_SupplierSku + ", DefaultTraceCode="
				+ DefaultTraceCode + ", HHT_Factory=" + HHT_Factory + ", HHT_Price=" + HHT_Price + ", HHT_PriceUnit="
				+ HHT_PriceUnit + ", HHT_INValue=" + HHT_INValue + "]";
	}

}
