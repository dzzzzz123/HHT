package ext.HHT.CIS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import wt.fc.WTObject;
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
		List<WTPart> parts = CommonUtil.getListFromPBO(ref, WTPart.class);
		List<String> result = new ArrayList<>();
		parts.forEach(part -> {
			CISEntity entity = null;
			try {
				entity = getEntity(part);
			} catch (WTException e) {
				e.printStackTrace();
			}
			try {
				result.add(sendToDB(entity));
			} catch (Exception e) {
				String Msg = e.getMessage();
				result.add(Msg);
			}

		});
		return result;
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
		String ClassificationName = HHT_Classification.substring(0, 2);
		if (ClassificationName.equals("11")) {
			String Classification = HHT_Classification.substring(0, 4);
			System.out.println("Classification==========" + Classification);
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
			if (name.subSequence(0, 4).equals(Classification)) {
				String Numbersql = "SELECT * FROM dbo.[" + name + "] WHERE Part_Number = '" + entity.getNumber() + "'";

				System.out.println("entity.getNumber()=====" + entity.getNumber());
				System.out.println("Numbersql=====" + Numbersql);
				ResultSet rt = Statement.executeQuery(Numbersql);
				int rowCount = 0;
				while (rt.next()) {
					rowCount++;

				}
				System.out.println("rt=======" + rt);
				System.out.println("rowCount=======" + rowCount);
				if (rowCount == 1) {

					String sql = "UPDATE dbo.[" + name
							+ "] SET  Description=?, Spec=?, Part_Type=?, Schematic_Part=?, PCB_Footprint=?, Classification=?  WHERE Part_Number= '"
							+ entity.getNumber() + "'";

					PreparedStatement ps = connection.prepareStatement(sql);

					ps.setString(1, entity.getName());
					ps.setString(2, entity.getHHT_LongtDescription());
					ps.setString(3, entity.getPart_Type());
					ps.setString(4, entity.getSchematic_Part());
					ps.setString(5, entity.getPCB_Footprint());
					ps.setString(6, entity.getHHT_Classification());

					ps.addBatch();

					ps.executeUpdate();
					ps.close();
					connection.close();
					System.out.println("sql=====" + sql);
					return "数据已更新";
				} else {
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
			} else

			{
				return "数据库没有这个表名，请创建该表" + Classification;
			}
		} else {
			return "该电子数据不应该发送到CIS";
		}
	}
}