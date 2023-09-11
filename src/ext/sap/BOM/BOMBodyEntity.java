package ext.sap.BOM;

import java.util.List;

public class BOMBodyEntity {
    private String Number;
    private String Name;
    private String Version;
    private String LineNumber;
    private String Quantity;
    private String Unit;
    private String ReferenceDesignatorRange;
    private List<AlternateEntity> Alternates;

    public BOMBodyEntity() {

    }

    public BOMBodyEntity(String number, String name, String version, String lineNumber, String quantity, String unit,
            String referenceDesignatorRange, List<AlternateEntity> alternates) {
        Number = number;
        Name = name;
        Version = version;
        LineNumber = lineNumber;
        Quantity = quantity;
        Unit = unit;
        ReferenceDesignatorRange = referenceDesignatorRange;
        Alternates = alternates;
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

    public String getLineNumber() {
        return LineNumber;
    }

    public void setLineNumber(String lineNumber) {
        LineNumber = lineNumber;
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

    public List<AlternateEntity> getAlternates() {
        return Alternates;
    }

    public void setAlternates(List<AlternateEntity> alternates) {
        Alternates = alternates;
    }

    @Override
    public String toString() {
        return "BOMBodyEntity [Number=" + Number + ", Name=" + Name + ", Version=" + Version + ", LineNumber="
                + LineNumber + ", Quantity=" + Quantity + ", Unit=" + Unit + ", ReferenceDesignatorRange="
                + ReferenceDesignatorRange + ", Alternates=" + Alternates + "]";
    }

}
