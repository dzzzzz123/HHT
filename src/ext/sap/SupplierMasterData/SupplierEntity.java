package ext.sap.SupplierMasterData;

public class SupplierEntity {
    private String InternalName;
    private String DisplayName;
    private String CreateTime;

    public SupplierEntity() {
    }

    public SupplierEntity(String internalName, String displayName, String createTime) {
        InternalName = internalName;
        DisplayName = displayName;
        CreateTime = createTime;
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

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    @Override
    public String toString() {
        return "SupplierEntity [InternalName=" + InternalName + ", DisplayName=" + DisplayName + ", CreateTime="
                + CreateTime + "]";
    }

}
