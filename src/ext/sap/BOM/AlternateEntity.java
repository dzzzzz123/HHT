package ext.sap.BOM;

public class AlternateEntity {
    private String name;
    private String HHT_ReplaceGroup;

    public AlternateEntity() {
    }

    public AlternateEntity(String name, String hHT_ReplaceGroup) {
        this.name = name;
        HHT_ReplaceGroup = hHT_ReplaceGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHHT_ReplaceGroup() {
        return HHT_ReplaceGroup;
    }

    public void setHHT_ReplaceGroup(String hHT_ReplaceGroup) {
        HHT_ReplaceGroup = hHT_ReplaceGroup;
    }

    @Override
    public String toString() {
        return "AlternateEntity [name=" + name + ", HHT_ReplaceGroup=" + HHT_ReplaceGroup + "]";
    }

}
