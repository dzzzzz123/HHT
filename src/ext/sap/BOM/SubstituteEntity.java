package ext.sap.BOM;

public class SubstituteEntity {
	private String Number;
	private String Quantity;
	private String Unit;
	private String HHT_Priority;
	private String HHT_Strategies;
	private String HHT_UsagePossibility;
	private String HHT_MatchGroup;

	public SubstituteEntity() {
	}

	public SubstituteEntity(String number, String quantity, String unit, String hHT_Priority, String hHT_Strategies,
			String hHT_UsagePossibility, String hHT_MatchGroup) {
		super();
		Number = number;
		Quantity = quantity;
		Unit = unit;
		HHT_Priority = hHT_Priority;
		HHT_Strategies = hHT_Strategies;
		HHT_UsagePossibility = hHT_UsagePossibility;
		HHT_MatchGroup = hHT_MatchGroup;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
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

	public String getHHT_Priority() {
		return HHT_Priority;
	}

	public void setHHT_Priority(String hHT_Priority) {
		HHT_Priority = hHT_Priority;
	}

	public String getHHT_Strategies() {
		return HHT_Strategies;
	}

	public void setHHT_Strategies(String hHT_Strategies) {
		HHT_Strategies = hHT_Strategies;
	}

	public String getHHT_UsagePossibility() {
		return HHT_UsagePossibility;
	}

	public void setHHT_UsagePossibility(String hHT_UsagePossibility) {
		HHT_UsagePossibility = hHT_UsagePossibility;
	}

	public String getHHT_MatchGroup() {
		return HHT_MatchGroup;
	}

	public void setHHT_MatchGroup(String hHT_MatchGroup) {
		HHT_MatchGroup = hHT_MatchGroup;
	}

	@Override
	public String toString() {
		return "SubstituteEntity [Number=" + Number + ", Quantity=" + Quantity + ", Unit=" + Unit + ", HHT_Priority="
				+ HHT_Priority + ", HHT_Strategies=" + HHT_Strategies + ", HHT_UsagePossibility=" + HHT_UsagePossibility
				+ ", HHT_MatchGroup=" + HHT_MatchGroup + "]";
	}

}
