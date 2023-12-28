package ext.sap.Custing;

public class CustingEntity {
	private String Number;
	private String Name;
	private String Version;
	private Double Amount;
	private Double Price;
	private boolean Master = false;
	private String Parent;
	private String status;
	private String unit;

	public CustingEntity() {
	}

	public CustingEntity(String number, String name, String version,Double amount) {
		super();
		Number = number;
		Name = name;
		Version = version;
		Amount = amount;
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

	public Double getAmount() {
		return Amount;
	}

	public void setAmount(Double amount) {
		Amount = amount;
	}

	public Double getPrice() {
		return Price;
	}

	public void setPrice(Double price) {
		Price = price;
	}

	public boolean isMaster() {
		return Master;
	}

	public void setMaster(boolean master) {
		Master = master;
	}

	public String getParent() {
		return Parent;
	}

	public void setParent(String parent) {
		Parent = parent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
