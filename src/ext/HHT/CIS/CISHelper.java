package ext.HHT.CIS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ext.ait.util.PropertiesUtil;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;

public class CISHelper {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	/**
	 * 处理内容主方法，这里返回的是一个错误信息列表
	 * 
	 * @param ref
	 * @return List<String>
	 */
	public static List<String> process(WTObject ref) {
		List<WTPart> parts = getPartList(ref);
		List<String> result = new ArrayList<>();
		parts.forEach(part -> {
			CISEntity entity = null;
			try {
				entity = getEntity(part);
				result.add(sendToDB(entity));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return result;
	}

	/**
	 * 从工作流中获取物料列表
	 * 
	 * @param obj
	 * @return List<WTPart>
	 */
	private static List<WTPart> getPartList(WTObject obj) {
		List<WTPart> list = new ArrayList<>();
		try {
			if (obj instanceof WTPart) {
				list.add((WTPart) obj);
			} else if (obj instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) obj;
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof WTPart) {
						list.add((WTPart) object);
					}
				}
			} else if (obj instanceof WTChangeOrder2) {
				WTChangeOrder2 co = (WTChangeOrder2) obj;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(co);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (object instanceof WTPart) {
						list.add((WTPart) object);
					}
				}
			} else {
				System.out.println("不是部件，无法修改其名称/编号/描述");
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		System.out.println("list" + list);
		System.out.println("list.toString()" + list.toString());
		return list;
	}

	/**
	 * 从部件中获取所需要的属性并将其转换为实体类
	 * 
	 * @param part
	 * @return CISEntity
	 * @throws WTException
	 */
	private static CISEntity getEntity(WTPart part) throws WTException {
		CISEntity entity = new CISEntity();
		entity.setName(part.getName());
		entity.setNumber(part.getNumber());
		entity.setHHT_Classification(pUtil.getValueByKey(part, "iba.internal.HHT_Classification"));
		entity.setHHT_LongtDescription(pUtil.getValueByKey(part, "iba.internal.HHT_LongtDescription"));
		entity.setPart_Type(pUtil.getValueByKey(part, "iba.internal.Part_Type"));
		entity.setPCB_Footprint(pUtil.getValueByKey(part, "iba.internal.Schematic_Part"));
		entity.setSchematic_Part(pUtil.getValueByKey(part, "iba.internal.PCB_Footprint"));

		return entity;
	}

	/**
	 * 将实体类中的属性值发送到数据库中去
	 * 
	 * @param entity
	 * @return String
	 * @throws Exception
	 */
	private static String sendToDB(CISEntity entity) throws Exception {
		Connection connection = DatabaseConnector.getConnection();
		String HHT_Classification = entity.getHHT_Classification();
		String Classification = HHT_Classification.substring(0, 4);
		String nameSql = "select name from sysobjects where xtype='U'";
		Statement Statement = connection.createStatement();
		ResultSet resultSet = Statement.executeQuery(nameSql);
		String name = "";
		while (resultSet.next()) {
			name = resultSet.getString("name");
			if (name.subSequence(0, 4).equals(Classification)) {
				break;
			}
		}

		String sql = "INSERT INTO dbo.[" + name
				+ "]( Part_Number, Description, Spec, Part_Type, Schematic_Part, PCB_Footprint, Classification ) VALUES (?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, entity.getNumber());
		ps.setString(2, entity.getName());
		ps.setString(3, entity.getHHT_LongtDescription());
		ps.setString(4, entity.getPart_Type());
		ps.setString(5, entity.getSchematic_Part());
		ps.setString(6, entity.getPCB_Footprint());
		ps.setString(7, entity.getHHT_Classification());

		ps.addBatch();

		ps.executeBatch(); // insert remaining records
		ps.close();
		connection.close();

		return "";

	}
}
