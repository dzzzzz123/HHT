package ext.HHT.CIS;

public class CISEntity {
	private String number;
	private String name;
	private String HHT_LongtDescription;
	private String Part_Type;
	private String Schematic_Part;
	private String PCB_Footprint;
	private String HHT_Classification;

	public CISEntity() {
		super();
	}

	public CISEntity(String number, String name, String hHT_LongtDescription, String part_Type, String schematic_Part,
			String pCB_Footprint, String hHT_Classification) {
		super();
		this.number = number;
		this.name = name;
		HHT_LongtDescription = hHT_LongtDescription;
		Part_Type = part_Type;
		Schematic_Part = schematic_Part;
		PCB_Footprint = pCB_Footprint;
		HHT_Classification = hHT_Classification;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHHT_LongtDescription() {
		return HHT_LongtDescription;
	}

	public void setHHT_LongtDescription(String hHT_LongtDescription) {
		HHT_LongtDescription = hHT_LongtDescription;
	}

	public String getPart_Type() {
		return Part_Type;
	}

	public void setPart_Type(String part_Type) {
		Part_Type = part_Type;
	}

	public String getSchematic_Part() {
		return Schematic_Part;
	}

	public void setSchematic_Part(String schematic_Part) {
		Schematic_Part = schematic_Part;
	}

	public String getPCB_Footprint() {
		return PCB_Footprint;
	}

	public void setPCB_Footprint(String pCB_Footprint) {
		PCB_Footprint = pCB_Footprint;
	}

	public String getHHT_Classification() {
		return HHT_Classification;
	}

	public void setHHT_Classification(String hHT_Classification) {
		HHT_Classification = hHT_Classification;
	}

	@Override
	public String toString() {
		return "CISEntity [number=" + number + ", name=" + name + ", HHT_LongtDescription=" + HHT_LongtDescription
				+ ", Part_Type=" + Part_Type + ", Schematic_Part=" + Schematic_Part + ", PCB_Footprint=" + PCB_Footprint
				+ ", HHT_Classification=" + HHT_Classification + "]";
	}
}
