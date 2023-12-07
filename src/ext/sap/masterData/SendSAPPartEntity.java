package ext.sap.masterData;

public class SendSAPPartEntity {

	private String PartType;
	private String HHT_Classification;
	private String Number;
	private String Name;
	private String revision;
	private String Unit;
	private String HHT_Bonded;
	private String NonbondedNumber;
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
	private String LargeScreenSize;
	private String HHT_FinishedSeries;
	private String HHT_Industry;
	private String HHT_ProductDevelopmentType;
	private String HHT_CustomizedProductIdentifier;
	private String HHT_SupplierSku;
	private String HHT_SerialNumber;
	private String HHT_Factory;
	private String HHT_Price;
	private String HHT_PriceUnit;
	private String HHT_INValue;
	private String ClassDescription;
	private String ClassPartDescription;

	public SendSAPPartEntity() {
	}

	public SendSAPPartEntity(String partType, String hHT_Classification, String number, String name, String revision,
			String unit, String hHT_Bonded, String nonbondedNumber, String hHT_GrossWeight, String hHT_NetWeight,
			String hHT_WeightUnit, String hHT_Traffic, String hHT_VolumeUnit, String hHT_Length, String hHT_Width,
			String hHT_Height, String hHT_SizeUnits, String state, String hHT_ClassificationCode,
			String hHT_ClassificationName, String hHT_ProductLineNumber, String hHT_ProductLineName,
			String hHT_ProductNumber, String hHT_Productdescription, String hHT_ModelSpecification,
			String hHT_CommodityName, String hHT_Brand, String hHT_Year, String largeScreenSize,
			String hHT_FinishedSeries, String hHT_Industry, String hHT_ProductDevelopmentType,
			String hHT_CustomizedProductIdentifier, String hHT_SupplierSku, String hHT_SerialNumber, String hHT_Factory,
			String hHT_Price, String hHT_PriceUnit, String hHT_INValue, String classDescription,
			String classPartDescription) {
		super();
		PartType = partType;
		HHT_Classification = hHT_Classification;
		Number = number;
		Name = name;
		this.revision = revision;
		Unit = unit;
		HHT_Bonded = hHT_Bonded;
		NonbondedNumber = nonbondedNumber;
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
		LargeScreenSize = largeScreenSize;
		HHT_FinishedSeries = hHT_FinishedSeries;
		HHT_Industry = hHT_Industry;
		HHT_ProductDevelopmentType = hHT_ProductDevelopmentType;
		HHT_CustomizedProductIdentifier = hHT_CustomizedProductIdentifier;
		HHT_SupplierSku = hHT_SupplierSku;
		HHT_SerialNumber = hHT_SerialNumber;
		HHT_Factory = hHT_Factory;
		HHT_Price = hHT_Price;
		HHT_PriceUnit = hHT_PriceUnit;
		HHT_INValue = hHT_INValue;
		ClassDescription = classDescription;
		ClassPartDescription = classPartDescription;
	}

	public String getPartType() {
		return PartType;
	}

	public void setPartType(String partType) {
		PartType = partType;
	}

	public String getHHT_Classification() {
		return HHT_Classification;
	}

	public void setHHT_Classification(String hHT_Classification) {
		HHT_Classification = hHT_Classification;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getHHT_Bonded() {
		return HHT_Bonded;
	}

	public void setHHT_Bonded(String hHT_Bonded) {
		HHT_Bonded = hHT_Bonded;
	}

	public String getNonbondedNumber() {
		return NonbondedNumber;
	}

	public void setNonbondedNumber(String nonbondedNumber) {
		NonbondedNumber = nonbondedNumber;
	}

	public String getHHT_GrossWeight() {
		return HHT_GrossWeight;
	}

	public void setHHT_GrossWeight(String hHT_GrossWeight) {
		HHT_GrossWeight = hHT_GrossWeight;
	}

	public String getHHT_NetWeight() {
		return HHT_NetWeight;
	}

	public void setHHT_NetWeight(String hHT_NetWeight) {
		HHT_NetWeight = hHT_NetWeight;
	}

	public String getHHT_WeightUnit() {
		return HHT_WeightUnit;
	}

	public void setHHT_WeightUnit(String hHT_WeightUnit) {
		HHT_WeightUnit = hHT_WeightUnit;
	}

	public String getHHT_Traffic() {
		return HHT_Traffic;
	}

	public void setHHT_Traffic(String hHT_Traffic) {
		HHT_Traffic = hHT_Traffic;
	}

	public String getHHT_VolumeUnit() {
		return HHT_VolumeUnit;
	}

	public void setHHT_VolumeUnit(String hHT_VolumeUnit) {
		HHT_VolumeUnit = hHT_VolumeUnit;
	}

	public String getHHT_Length() {
		return HHT_Length;
	}

	public void setHHT_Length(String hHT_Length) {
		HHT_Length = hHT_Length;
	}

	public String getHHT_Width() {
		return HHT_Width;
	}

	public void setHHT_Width(String hHT_Width) {
		HHT_Width = hHT_Width;
	}

	public String getHHT_Height() {
		return HHT_Height;
	}

	public void setHHT_Height(String hHT_Height) {
		HHT_Height = hHT_Height;
	}

	public String getHHT_SizeUnits() {
		return HHT_SizeUnits;
	}

	public void setHHT_SizeUnits(String hHT_SizeUnits) {
		HHT_SizeUnits = hHT_SizeUnits;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getHHT_ClassificationCode() {
		return HHT_ClassificationCode;
	}

	public void setHHT_ClassificationCode(String hHT_ClassificationCode) {
		HHT_ClassificationCode = hHT_ClassificationCode;
	}

	public String getHHT_ClassificationName() {
		return HHT_ClassificationName;
	}

	public void setHHT_ClassificationName(String hHT_ClassificationName) {
		HHT_ClassificationName = hHT_ClassificationName;
	}

	public String getHHT_ProductLineNumber() {
		return HHT_ProductLineNumber;
	}

	public void setHHT_ProductLineNumber(String hHT_ProductLineNumber) {
		HHT_ProductLineNumber = hHT_ProductLineNumber;
	}

	public String getHHT_ProductLineName() {
		return HHT_ProductLineName;
	}

	public void setHHT_ProductLineName(String hHT_ProductLineName) {
		HHT_ProductLineName = hHT_ProductLineName;
	}

	public String getHHT_ProductNumber() {
		return HHT_ProductNumber;
	}

	public void setHHT_ProductNumber(String hHT_ProductNumber) {
		HHT_ProductNumber = hHT_ProductNumber;
	}

	public String getHHT_Productdescription() {
		return HHT_Productdescription;
	}

	public void setHHT_Productdescription(String hHT_Productdescription) {
		HHT_Productdescription = hHT_Productdescription;
	}

	public String getHHT_ModelSpecification() {
		return HHT_ModelSpecification;
	}

	public void setHHT_ModelSpecification(String hHT_ModelSpecification) {
		HHT_ModelSpecification = hHT_ModelSpecification;
	}

	public String getHHT_CommodityName() {
		return HHT_CommodityName;
	}

	public void setHHT_CommodityName(String hHT_CommodityName) {
		HHT_CommodityName = hHT_CommodityName;
	}

	public String getHHT_Brand() {
		return HHT_Brand;
	}

	public void setHHT_Brand(String hHT_Brand) {
		HHT_Brand = hHT_Brand;
	}

	public String getHHT_Year() {
		return HHT_Year;
	}

	public void setHHT_Year(String hHT_Year) {
		HHT_Year = hHT_Year;
	}

	public String getLargeScreenSize() {
		return LargeScreenSize;
	}

	public void setLargeScreenSize(String largeScreenSize) {
		LargeScreenSize = largeScreenSize;
	}

	public String getHHT_FinishedSeries() {
		return HHT_FinishedSeries;
	}

	public void setHHT_FinishedSeries(String hHT_FinishedSeries) {
		HHT_FinishedSeries = hHT_FinishedSeries;
	}

	public String getHHT_Industry() {
		return HHT_Industry;
	}

	public void setHHT_Industry(String hHT_Industry) {
		HHT_Industry = hHT_Industry;
	}

	public String getHHT_ProductDevelopmentType() {
		return HHT_ProductDevelopmentType;
	}

	public void setHHT_ProductDevelopmentType(String hHT_ProductDevelopmentType) {
		HHT_ProductDevelopmentType = hHT_ProductDevelopmentType;
	}

	public String getHHT_CustomizedProductIdentifier() {
		return HHT_CustomizedProductIdentifier;
	}

	public void setHHT_CustomizedProductIdentifier(String hHT_CustomizedProductIdentifier) {
		HHT_CustomizedProductIdentifier = hHT_CustomizedProductIdentifier;
	}

	public String getHHT_SupplierSku() {
		return HHT_SupplierSku;
	}

	public void setHHT_SupplierSku(String hHT_SupplierSku) {
		HHT_SupplierSku = hHT_SupplierSku;
	}

	public String getHHT_SerialNumber() {
		return HHT_SerialNumber;
	}

	public void setHHT_SerialNumber(String hHT_SerialNumber) {
		HHT_SerialNumber = hHT_SerialNumber;
	}

	public String getHHT_Factory() {
		return HHT_Factory;
	}

	public void setHHT_Factory(String hHT_Factory) {
		HHT_Factory = hHT_Factory;
	}

	public String getHHT_Price() {
		return HHT_Price;
	}

	public void setHHT_Price(String hHT_Price) {
		HHT_Price = hHT_Price;
	}

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

	public String getClassDescription() {
		return ClassDescription;
	}

	public void setClassDescription(String classDescription) {
		ClassDescription = classDescription;
	}

	public String getClassPartDescription() {
		return ClassPartDescription;
	}

	public void setClassPartDescription(String classPartDescription) {
		ClassPartDescription = classPartDescription;
	}

	@Override
	public String toString() {
		return "SendSAPPartEntity [PartType=" + PartType + ", HHT_Classification=" + HHT_Classification + ", Number="
				+ Number + ", Name=" + Name + ", revision=" + revision + ", Unit=" + Unit + ", HHT_Bonded=" + HHT_Bonded
				+ ", NonbondedNumber=" + NonbondedNumber + ", HHT_GrossWeight=" + HHT_GrossWeight + ", HHT_NetWeight="
				+ HHT_NetWeight + ", HHT_WeightUnit=" + HHT_WeightUnit + ", HHT_Traffic=" + HHT_Traffic
				+ ", HHT_VolumeUnit=" + HHT_VolumeUnit + ", HHT_Length=" + HHT_Length + ", HHT_Width=" + HHT_Width
				+ ", HHT_Height=" + HHT_Height + ", HHT_SizeUnits=" + HHT_SizeUnits + ", state=" + state
				+ ", HHT_ClassificationCode=" + HHT_ClassificationCode + ", HHT_ClassificationName="
				+ HHT_ClassificationName + ", HHT_ProductLineNumber=" + HHT_ProductLineNumber + ", HHT_ProductLineName="
				+ HHT_ProductLineName + ", HHT_ProductNumber=" + HHT_ProductNumber + ", HHT_Productdescription="
				+ HHT_Productdescription + ", HHT_ModelSpecification=" + HHT_ModelSpecification + ", HHT_CommodityName="
				+ HHT_CommodityName + ", HHT_Brand=" + HHT_Brand + ", HHT_Year=" + HHT_Year + ", LargeScreenSize="
				+ LargeScreenSize + ", HHT_FinishedSeries=" + HHT_FinishedSeries + ", HHT_Industry=" + HHT_Industry
				+ ", HHT_ProductDevelopmentType=" + HHT_ProductDevelopmentType + ", HHT_CustomizedProductIdentifier="
				+ HHT_CustomizedProductIdentifier + ", HHT_SupplierSku=" + HHT_SupplierSku + ", HHT_SerialNumber="
				+ HHT_SerialNumber + ", HHT_Factory=" + HHT_Factory + ", HHT_Price=" + HHT_Price + ", HHT_PriceUnit="
				+ HHT_PriceUnit + ", HHT_INValue=" + HHT_INValue + ", ClassDescription=" + ClassDescription
				+ ", ClassPartDescription=" + ClassPartDescription + "]";
	}

}
