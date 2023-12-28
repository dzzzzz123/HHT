package ext.sap.SupplierMasterData;

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
import ext.sap.CostCenter.CostCenterServlet;
import wt.pom.WTConnection;

public class SupplierMasterDataServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SupplierEntity> SupplierEntitiesList = CommonUtil.getEntitiesFromRequest(request, SupplierEntity.class,
				"data");
		// 写入接收到的数据到properties文件中
//		writeToProperties(SupplierEntitiesList);
		SupplierEntitiesList.forEach(SupplierMasterDataServlet::insertORUpdate);

		response.setCharacterEncoding("utf-8");
		response.setContentType("appliction/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(Result.success().toString());
		out.close();
		return null;
	}

	public static List<SupplierEntity> getAllSupplier() {
		List<SupplierEntity> supplierList = new ArrayList<>();
		String sql = "SELECT * FROM CUS_SUPPLIER";
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				SupplierEntity entity = new SupplierEntity();
				entity.setInternalName(resultSet.getString("INTERNALNAME"));
				entity.setDisplayName(resultSet.getString("DISPLAYNAME"));
				entity.setCreateTime(resultSet.getString("CREATETIME"));
				supplierList.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CostCenterServlet.closeResources(statement, resultSet);
		}
		return supplierList;
	}

	public static List<SupplierEntity> getSuppliers(String displayName) {
		if (StringUtils.isBlank(displayName)) {
			return getAllSupplier();
		}
		List<SupplierEntity> supplierList = new ArrayList<>();
		String sql = "SELECT * FROM CUS_SUPPLIER WHERE DISPLAYNAME LIKE ?";
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			displayName = "%" + displayName + "%";
			statement.setString(1, displayName);
			String fullSql = sql;
			fullSql = fullSql.replaceFirst("\\?", "'" + displayName + "'");
			System.out.println("--------当前执行查询操作的SQL语句为--------");
			System.out.println(fullSql);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				SupplierEntity entity = new SupplierEntity();
				entity.setInternalName(resultSet.getString("INTERNALNAME"));
				entity.setDisplayName(resultSet.getString("DISPLAYNAME"));
				entity.setCreateTime(resultSet.getString("CREATETIME"));
				supplierList.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CostCenterServlet.closeResources(statement, resultSet);
		}
		return supplierList;
	}

	public static void insertORUpdate(SupplierEntity entity) {
		SupplierEntity tempEntity = selectSupplier(entity.getInternalName());
		int i = StringUtils.isNotBlank(tempEntity.getInternalName()) ? updateSupplier(entity) : insertSupplier(entity);
		System.out.println("当前插入/更新的供应商数据条数为" + i + "条！");
	}

	public static SupplierEntity selectSupplier(String INTERNALNAME) {
		SupplierEntity entity = new SupplierEntity();
		String sql = "SELECT * FROM CUS_SUPPLIER WHERE INTERNALNAME = ?";
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
				entity.setInternalName(resultSet.getString("InternalName"));
				entity.setDisplayName(resultSet.getString("DisplayName"));
				entity.setCreateTime(resultSet.getString("CreateTime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CostCenterServlet.closeResources(statement, resultSet);
		}

		return entity;
	}

	public static int insertSupplier(SupplierEntity entity) {
		String sql = "INSERT INTO CUS_SUPPLIER (INTERNALNAME, DISPLAYNAME, CREATETIME) VALUES ( ? , ? , ?) ";
		String INTERNALNAME = entity.getInternalName();
		String DISPLAYNAME = entity.getDisplayName();
		String CREATETIME = entity.getCreateTime();
		int affectedRows = 0;
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, INTERNALNAME);
			statement.setString(2, DISPLAYNAME);
			statement.setString(3, CREATETIME);

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			fullSql = fullSql.replaceFirst("\\?", "\"" + INTERNALNAME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + DISPLAYNAME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + CREATETIME + "\"");

			System.out.println("--------当前执行插入操作的SQL语句为--------");
			System.out.println(fullSql);
			affectedRows = statement.executeUpdate();
			return affectedRows;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CostCenterServlet.closeResources(statement, resultSet);
		}
		return affectedRows;
	}

	public static int updateSupplier(SupplierEntity entity) {
		String sql = "UPDATE CUS_SUPPLIER SET DISPLAYNAME = ? , CREATETIME = ? WHERE INTERNALNAME = ? ";
		String INTERNALNAME = entity.getInternalName();
		String DISPLAYNAME = entity.getDisplayName();
		String CREATETIME = entity.getCreateTime();
		int affectedRows = 0;
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, DISPLAYNAME);
			statement.setString(2, CREATETIME);
			statement.setString(3, INTERNALNAME);

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			fullSql = fullSql.replaceFirst("\\?", "\"" + DISPLAYNAME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + CREATETIME + "\"");
			fullSql = fullSql.replaceFirst("\\?", "\"" + INTERNALNAME + "\"");

			System.out.println("--------当前执行插入操作的SQL语句为--------");
			System.out.println(fullSql);
			affectedRows = statement.executeUpdate();
//			statement.close();
			return affectedRows;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CostCenterServlet.closeResources(statement, resultSet);
		}
		return affectedRows;
	}

}
