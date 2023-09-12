package ext.sap.BOM;

import java.util.List;

public class BOMBodyEntity {
	private String Number;
	private String Name;
	private String Version;
	private String Quantity;
	private String Unit;
	private String ReferenceDesignatorRange;
	private List<SubstituteEntity> Substitute;

	public BOMBodyEntity() {

	}

	public BOMBodyEntity(String number, String name, String version, String quantity, String unit,
			String referenceDesignatorRange, List<SubstituteEntity> substitute) {
		Number = number;
		Name = name;
		Version = version;
		Quantity = quantity;
		Unit = unit;
		ReferenceDesignatorRange = referenceDesignatorRange;
		Substitute = substitute;
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

	public String getQuantity() {
		return Quantity;
	}

	public void setQuantity(String quantity) {
		Quantity = quantity;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getReferenceDesignatorRange() {
		return ReferenceDesignatorRange;
	}

	public void setReferenceDesignatorRange(String referenceDesignatorRange) {
		ReferenceDesignatorRange = referenceDesignatorRange;
	}

	public List<SubstituteEntity> getSubstitute() {
		return Substitute;
	}

	public void setSubstitute(List<SubstituteEntity> substitute) {
		Substitute = substitute;
	}

	@Override
	public String toString() {
		return "BOMBodyEntity [Number=" + Number + ", Name=" + Name + ", Version=" + Version + ", Quantity=" + Quantity
				+ ", Unit=" + Unit + ", ReferenceDesignatorRange=" + ReferenceDesignatorRange + ", Substitute="
				+ Substitute.toString() + "]";
	}

}
