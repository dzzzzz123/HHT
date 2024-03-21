package ext.plm.change;


public class CompareMessage {
	
	private String fatherPartNumber;// 父编码
	private String fatherPartDesc;
	private String fatherPartVer_old;// 父旧版本
	private String fatherPartVer_new;// 父新版本
	
	private String sonPartNumber_old;// 子部件编号
	private String sonPartDesc_old;//子物料描述
	private String sonPartNumber_new;// 子部件编号
	private String sonPartDesc_new;//子物料描述
	private String version;//子部件版本
	private String changeType;// 更改类型
	private String quantity_old;// 数量
	private String quantity_new;// 数量
	private String placeNum_old;// 位号
	private String placeNum_new;// 位号
	private String unit_old;//BOM单位
	private String unit_new;
	private String att_old;//属性
	private String att_new;
	
	private String group;//替换组
	private String zzView;//在制处理意见
	private String ztView;//在途处理意见
	private String kcView;//库存处理意见
	private String remark;//备注
	private String xh;
	
	public String getFatherPartNumber() {
		return fatherPartNumber;
	}
	public void setFatherPartNumber(String fatherPartNumber) {
		this.fatherPartNumber = fatherPartNumber;
	}
	public String getFatherPartDesc() {
		return fatherPartDesc;
	}
	public void setFatherPartDesc(String fatherPartDesc) {
		this.fatherPartDesc = fatherPartDesc;
	}
	public String getFatherPartVer_old() {
		return fatherPartVer_old;
	}
	public void setFatherPartVer_old(String fatherPartVer_old) {
		this.fatherPartVer_old = fatherPartVer_old;
	}
	public String getFatherPartVer_new() {
		return fatherPartVer_new;
	}
	public void setFatherPartVer_new(String fatherPartVer_new) {
		this.fatherPartVer_new = fatherPartVer_new;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public String getQuantity_old() {
		return quantity_old;
	}
	public void setQuantity_old(String quantity_old) {
		this.quantity_old = quantity_old;
	}
	public String getQuantity_new() {
		return quantity_new;
	}
	public void setQuantity_new(String quantity_new) {
		this.quantity_new = quantity_new;
	}
	public String getPlaceNum_old() {
		return placeNum_old;
	}
	public void setPlaceNum_old(String placeNum_old) {
		this.placeNum_old = placeNum_old;
	}
	public String getPlaceNum_new() {
		return placeNum_new;
	}
	public void setPlaceNum_new(String placeNum_new) {
		this.placeNum_new = placeNum_new;
	}
	public String getUnit_old() {
		return unit_old;
	}
	public void setUnit_old(String unit_old) {
		this.unit_old = unit_old;
	}
	public String getUnit_new() {
		return unit_new;
	}
	public void setUnit_new(String unit_new) {
		this.unit_new = unit_new;
	}
	public String getAtt_old() {
		return att_old;
	}
	public void setAtt_old(String att_old) {
		this.att_old = att_old;
	}
	public String getAtt_new() {
		return att_new;
	}
	public void setAtt_new(String att_new) {
		this.att_new = att_new;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getZzView() {
		return zzView;
	}
	public void setZzView(String zzView) {
		this.zzView = zzView;
	}
	public String getZtView() {
		return ztView;
	}
	public void setZtView(String ztView) {
		this.ztView = ztView;
	}
	public String getKcView() {
		return kcView;
	}
	public void setKcView(String kcView) {
		this.kcView = kcView;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSonPartNumber_old() {
		return sonPartNumber_old;
	}
	public void setSonPartNumber_old(String sonPartNumber_old) {
		this.sonPartNumber_old = sonPartNumber_old;
	}
	public String getSonPartDesc_old() {
		return sonPartDesc_old;
	}
	public void setSonPartDesc_old(String sonPartDesc_old) {
		this.sonPartDesc_old = sonPartDesc_old;
	}
	public String getSonPartNumber_new() {
		return sonPartNumber_new;
	}
	public void setSonPartNumber_new(String sonPartNumber_new) {
		this.sonPartNumber_new = sonPartNumber_new;
	}
	public String getSonPartDesc_new() {
		return sonPartDesc_new;
	}
	public void setSonPartDesc_new(String sonPartDesc_new) {
		this.sonPartDesc_new = sonPartDesc_new;
	}
	public String getXh() {
		return xh;
	}
	public void setXh(String xh) {
		this.xh = xh;
	}
	
	
	
}