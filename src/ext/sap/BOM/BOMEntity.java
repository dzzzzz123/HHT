package ext.sap.BOM;

import java.util.List;

public class BOMEntity {

	private String Number;
	private String Name;
	private String HHT_BasicQuantity;
	private String Unit;
	private String Version;
	private String Factory;
	private String ECNNumber;
	private String Stlan;
	private List<BOMBodyEntity> BOMBody;

	public BOMEntity() {
	}

	public BOMEntity(String number, String name, String hHT_BasicQuantity, String unit, String version, String factory,
			String eCNNumber, String stlan, List<BOMBodyEntity> bOMBody) {
		super();
		Number = number;
		Name = name;
		HHT_BasicQuantity = hHT_BasicQuantity;
		Unit = unit;
		Version = version;
		Factory = factory;
		ECNNumber = eCNNumber;
		Stlan = stlan;
		BOMBody = bOMBody;
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

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getFactory() {
		return Factory;
	}

	public void setFactory(String factory) {
		Factory = factory;
	}

	public String getStlan() {
		return Stlan;
	}

	public void setStlan(String stlan) {
		Stlan = stlan;
	}

	public String getECNNumber() {
		return ECNNumber;
	}

	public void setECNNumber(String eCNNumber) {
		ECNNumber = eCNNumber;
	}

	public List<BOMBodyEntity> getBOMBody() {
		return BOMBody;
	}

	public void setBOMBody(List<BOMBodyEntity> bOMBody) {
		BOMBody = bOMBody;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getHHT_BasicQuantity() {
		return HHT_BasicQuantity;
	}

	public void setHHT_BasicQuantity(String hHT_BasicQuantity) {
		HHT_BasicQuantity = hHT_BasicQuantity;
	}

	@Override
	public String toString() {
		return "BOMEntity [Number=" + Number + ", Name=" + Name + ", HHT_BasicQuantity=" + HHT_BasicQuantity + ", Unit="
				+ Unit + ", Version=" + Version + ", Factory=" + Factory + ", ECNNumber=" + ECNNumber + ", Stlan="
				+ Stlan + ", BOMBody=" + BOMBody + "]";
	}
}
