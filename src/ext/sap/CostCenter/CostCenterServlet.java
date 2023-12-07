package ext.sap.CostCenter;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import ext.ait.util.Result;
import wt.pom.WTConnection;

public class CostCenterServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CostCenterEntity> costCenterEntityList = CommonUtil.getEntitiesFromRequest(request, CostCenterEntity.class,
				"data");
		// 写入接收到的数据到properties文件中
//		writeToProperties(costCenterEntityList);
		costCenterEntityList.forEach(CostCenterServlet::insertORUpdate);

		response.setCharacterEncoding("utf-8");
		response.setContentType("appliction/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(Result.success().toString());
		out.close();
		return null;
	}

	public static List<CostCenterEntity> getAllCostCenter() {
		List<CostCenterEntity> costCenterList = new ArrayList<>();
		String sql = "SELECT * FROM CUS_COSTCENTER";
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				CostCenterEntity entity = new CostCenterEntity();
				entity.setInternalName(resultSet.getString("INTERNALNAME"));
				entity.setDisplayName(resultSet.getString("DISPLAYNAME"));
				entity.setFactoryCode(resultSet.getString("FACTORYCODE"));
				costCenterList.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(statement, resultSet);
		}
		return costCenterList;
	}

	public static void insertORUpdate(CostCenterEntity entity) {
		CostCenterEntity tempEntity = selectCostCenter(entity.getInternalName());
		int i = StringUtils.isNotBlank(tempEntity.getInternalName()) ? updateCostCenter(entity)
				: insertCostCenter(entity);
		System.out.println("当前插入/更新的供应商数据条数为" + i + "条！");
	}

	public static CostCenterEntity selectCostCenter(String INTERNALNAME) {
		CostCenterEntity entity = new CostCenterEntity();
		String sql = "SELECT * FROM CUS_COSTCENTER WHERE INTERNALNAME = ?";
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, INTERNALNAME);

			String fullSql = sql;
			fullSql = fullSql.replaceFirst("\\?", "'" + INTERNALNAME + "'");
			System.out.println("--------当前执行查询操作的SQL语句为--------");
			System.out.println(fullSql);

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				entity.setInternalName(resultSet.getString("INTERNALNAME"));
				entity.setDisplayName(resultSet.getString("DISPLAYNAME"));
				entity.setFactoryCode(resultSet.getString("FACTORYCODE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(statement, resultSet);
		}
		return entity;
	}

	public static int insertCostCenter(CostCenterEntity entity) {
		String sql = "INSERT INTO CUS_COSTCENTER (INTERNALNAME, DISPLAYNAME, FACTORYCODE) VALUES ( ? , ? , ?) ";
		String INTERNALNAME = entity.getInternalName();
		String DISPLAYNAME = entity.getDisplayName();
		String FACTORYCODE = entity.getFactoryCode();
		int affectedRows = 0;
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, INTERNALNAME);
			statement.setString(2, DISPLAYNAME);
			statement.setString(3, FACTORYCODE);

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			fullSql = fullSql.replaceFirst("\\?", "\"" + INTERNALNAME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + DISPLAYNAME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + FACTORYCODE + "\"");

			System.out.println("--------当前执行插入操作的SQL语句为--------");
			System.out.println(fullSql);
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources(statement, resultSet);
		}
		return affectedRows;
	}

	public static int updateCostCenter(CostCenterEntity entity) {
		String sql = "UPDATE CUS_COSTCENTER SET DISPLAYNAME = ? , FACTORYCODE = ? WHERE INTERNALNAME = ? ";
		String INTERNALNAME = entity.getInternalName();
		String DISPLAYNAME = entity.getDisplayName();
		String FACTORYCODE = entity.getFactoryCode();
		int affectedRows = 0;
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, DISPLAYNAME);
			statement.setString(2, FACTORYCODE);
			statement.setString(3, INTERNALNAME);

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			fullSql = fullSql.replaceFirst("\\?", "\"" + DISPLAYNAME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + FACTORYCODE + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + INTERNALNAME + "\"");

			System.out.println("--------当前执行更新操作的SQL语句为--------");
			System.out.println(fullSql);
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources(statement, resultSet);
		}
		return affectedRows;
	}

	// 关闭资源的方法
	public static void closeResources(PreparedStatement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
