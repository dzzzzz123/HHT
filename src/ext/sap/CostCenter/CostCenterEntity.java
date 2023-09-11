package ext.sap.CostCenter;

public class CostCenterEntity {
    private String InternalName;
    private String DisplayName;
    private String FactoryCode;

    public CostCenterEntity() {

    }

    public CostCenterEntity(String internalName, String displayName, String factoryCode) {
        InternalName = internalName;
        DisplayName = displayName;
        FactoryCode = factoryCode;
    }

    public String getInternalName() {
        return InternalName;
    }

    public void setInternalName(String internalName) {
        InternalName = internalName;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getFactoryCode() {
        return FactoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        FactoryCode = factoryCode;
    }

    @Override
    public String toString() {
        return "CostCenterEntity [InternalName=" + InternalName + ", DisplayName=" + DisplayName + ", FactoryCode="
                + FactoryCode + "]";
    }

}