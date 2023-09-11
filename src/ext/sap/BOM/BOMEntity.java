package ext.sap.BOM;

import java.util.List;

public class BOMEntity {
    private String Number;
    private String Name;
    private String Version;
    private String Factory;
    private List<BOMBodyEntity> body;

    public BOMEntity() {
    }

    public BOMEntity(String number, String name, String version, String factory, List<BOMBodyEntity> body) {
        Number = number;
        Name = name;
        Version = version;
        Factory = factory;
        this.body = body;
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

    public List<BOMBodyEntity> getBody() {
        return body;
    }

    public void setBody(List<BOMBodyEntity> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "BOMEntity [Number=" + Number + ", Name=" + Name + ", Version=" + Version + ", Factory=" + Factory
                + ", body=" + body + "]";
    }

}
