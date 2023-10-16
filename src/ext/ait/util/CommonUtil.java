package ext.ait.util;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.ptc.core.lwc.common.view.EnumerationDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.server.LWCLocalizablePropertyValue;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;

import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderingInfo;
import wt.folder.SubFolder;
import wt.folder.SubFolderReference;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class CommonUtil implements RemoteAccess {

	private static Logger LOGGER = LogR.getLogger(CommonUtil.class.getName());

	/**
	 * 转换中文格式，避免中文乱码
	 * 
	 * @param value
	 * @return
	 * @throws WTException
	 */
	public static String formatString(String value) throws WTException {
		try {
			if (value != null && value.trim().length() > 0) {
				byte[] tembyte = value.getBytes("gb2312");
				return new String(tembyte);
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 将一个实体类中的所有字段转换为Map
	 * @param entity 实体类
	 * @return 输出的Map
	 */
	public static <T> Map<String, Object> entityToMap(T entity) {
		Map<String, Object> resultMap = new HashMap<>();

		// 使用反射获取类的所有字段
		Field[] fields = entity.getClass().getDeclaredFields();

		try {
			for (Field field : fields) {
				// 设置字段为可访问，以便获取私有字段的值
				field.setAccessible(true);

				// 将字段名和字段值添加到Map中
				resultMap.put(field.getName(), field.get(entity));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 获取对象的文件夹路径
	 * 
	 * @param obj
	 * @return
	 */
	public static String getPath(RevisionControlled obj) {
		StringBuffer path = new StringBuffer();
		SubFolderReference ref = obj.getParentFolder();
		if (ref != null && ref.getObject() instanceof SubFolder) {
			SubFolder subFolder = (SubFolder) ref.getObject();
			getPath(path, subFolder);
		} else {
			path = new StringBuffer("/Default");
		}
		return path.toString();
	}

	/**
	 * 获取对象存储位置
	 * 
	 * @param fInfo
	 * @return
	 */
	public static String getFolderStr(FolderingInfo fInfo) {
		StringBuffer path = new StringBuffer();
		SubFolderReference ref = fInfo.getParentFolder();
		if (ref != null && ref.getObject() instanceof SubFolder) {
			SubFolder subFolder = (SubFolder) ref.getObject();
			getPath(path, subFolder);
		} else {
			path = new StringBuffer("/Default");
		}
		return path.toString();
	}

	/**
	 * 用来递归获取文件夹完整路径的方法
	 * 
	 * @param path
	 * @param subFolder
	 */
	private static void getPath(StringBuffer path, SubFolder subFolder) {
		path.insert(0, subFolder.getName()).insert(0, "/");
		SubFolderReference ref = subFolder.getParentFolder();
		if (ref != null && ref.getObject() instanceof SubFolder) {
			SubFolder sub = (SubFolder) ref.getObject();
			getPath(path, sub);
		} else {
			path.insert(0, "/Default");
		}
	}

	/**
	 * 根据用户id获取WTUser对象
	 * 
	 * @param id
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getUserById(String id) throws WTException {
		WTUser user = null;
		try {
			if (id != null && id.trim().length() > 0) {
				QuerySpec qs = new QuerySpec(WTUser.class);
				SearchCondition sc1 = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, id);
				SearchCondition sc2 = new SearchCondition(WTUser.class, WTUser.FULL_NAME, SearchCondition.EQUAL, id);
				qs.appendWhere(sc1);
				qs.appendOr();
				qs.appendWhere(sc2);
				LOGGER.debug("searchUsers sql where --->" + qs.getWhere());
				QueryResult qr = new QueryResult();
				qr = PersistenceHelper.manager.find(qs);
				while (qr.hasMoreElements()) {
					user = (WTUser) qr.nextElement();
				}
			}
			return user;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 通过类型的Key获取国际化的名称
	 * 
	 * @param key
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static String getTypeDisplayName(String key) throws WTException {
		String typeDisplayName = "";
		try {
			LWCTypeDefinition lwcType = null;
			QuerySpec lwcSpec = new QuerySpec(LWCTypeDefinition.class);
			lwcSpec.appendSearchCondition(
					new SearchCondition(LWCTypeDefinition.class, LWCTypeDefinition.NAME, SearchCondition.EQUAL, key));
			QueryResult qr = PersistenceHelper.manager.find(lwcSpec);
			while (qr.hasMoreElements()) {
				lwcType = (LWCTypeDefinition) qr.nextElement();
			}
			if (lwcType != null) {
				/**
				 * LWCLocalizablePropertyValue记录所有的国际化字段
				 */
				QuerySpec valueSpec = new QuerySpec(LWCLocalizablePropertyValue.class);
				valueSpec.appendSearchCondition(
						new SearchCondition(LWCLocalizablePropertyValue.class, "contextReference.key.id",
								SearchCondition.EQUAL, lwcType.getPersistInfo().getObjectIdentifier().getId()));
				valueSpec.appendAnd();
				valueSpec.appendSearchCondition(
						new SearchCondition(LWCLocalizablePropertyValue.class, "holderReference.key.id",
								SearchCondition.EQUAL, lwcType.getPersistInfo().getObjectIdentifier().getId()));
				QueryResult vqr = PersistenceHelper.manager.find(valueSpec);
				while (vqr.hasMoreElements()) {
					LWCLocalizablePropertyValue value = (LWCLocalizablePropertyValue) vqr.nextElement();
					typeDisplayName = value.getValue(Locale.CHINA);
					if (typeDisplayName == null || typeDisplayName.trim().length() == 0) {
						typeDisplayName = value.getValue();
					}
				}
			}
			return typeDisplayName;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 得到指定文件夹的对象，如果没有则创建该文件夹（尚不明晰，看上去并不那么好用）
	 * 
	 * @param strFolder
	 * @param wtContainer
	 * @return
	 * @throws WTException
	 */
	public static Folder getFolder(String strFolder, WTContainer wtContainer) throws WTException {
		WTPrincipal curUser = SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setAdministrator();
		Folder folder = null;
		String subPath = "Default/" + strFolder;
		WTContainerRef ref = WTContainerRef.newWTContainerRef(wtContainer);
		try {
			folder = FolderHelper.service.getFolder(subPath, ref);
		} catch (WTException e) {
			folder = FolderHelper.service.createSubFolder(subPath, ref);
		} finally {
			SessionHelper.manager.setPrincipal(curUser.getName());
		}
		return folder;
	}

	/**
	 * 根据容器的名称获取容器对象
	 * 
	 * @param containerName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static WTContainer getContainer(String containerName) throws Exception {
		QuerySpec qs = new QuerySpec(WTContainer.class);
		SearchCondition sc = new SearchCondition(WTContainer.class, WTContainer.NAME, "=", containerName);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			WTContainer container = (WTContainer) qr.nextElement();
			return container;
		}
		return null;
	}

	/**
	 * 根据组织名称获取组织对象
	 * 
	 * @param orgName
	 * @return
	 */
	public static OrgContainer getOrgContainer(String orgName) {
		try {
			QuerySpec queryspec = new QuerySpec(OrgContainer.class);
			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			while (qr.hasMoreElements()) {
				OrgContainer org = (OrgContainer) qr.nextElement();
				if (StringUtils.equalsIgnoreCase(orgName, org.getName())) {
					return org;
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据用户名称/用户全名获取用户对象
	 * 
	 * @param String
	 * @param boolean
	 * @return WTUser
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getUserByName(String name, boolean IsFull) {
		String parm = IsFull ? WTUser.FULL_NAME : WTUser.NAME;
		if (StringUtils.isBlank(name)) {
			return null;
		}
		try {
			Enumeration enumUser = OrganizationServicesHelper.manager.findUser(parm, name.trim());
			while (enumUser.hasMoreElements()) {
				return (WTUser) enumUser.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据组名获取组对象
	 * 
	 * @param String
	 * @return WTGroup
	 */
	public static WTGroup queryGroupByName(String groupName) {
		try {
			ChangeSession.administratorSession();
			if (StringUtils.isBlank(groupName)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL,
					groupName.trim());
			int[] index = { 0 };
			qs.appendWhere(sc, index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				WTGroup group = (WTGroup) qr.nextElement();
				return group;
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			ChangeSession.goPreviousSession();
		}
		return null;
	}

	/**
	 * 根据全局枚举的内部名称获取其中所有的子枚举的内部名称与外部名称
	 * 
	 * @param String
	 * @return Map<String, String>
	 */
	public static Map<String, String> getEnumTypeByInternalName(String internalName) {
		Map<String, String> map = new HashMap<>();
		try {
			EnumerationDefinitionReadView edr = TypeDefinitionServiceHelper.service.getEnumDefView(internalName);
			if (edr != null) {
				Map<String, EnumerationEntryReadView> views = edr.getAllEnumerationEntries();
				Set<String> keysOfView = views.keySet();
				for (String key : keysOfView) {
					EnumerationEntryReadView view = views.get(key);
					String enumKey = view.getName();
					String enumName = view.getPropertyValueByName("displayName").getValue().toString();
					// 此方法判断枚举值是否在可用列表
					if (view.getPropertyValueByName("selectable").getValue().equals(true)) {
						map.put(enumKey, enumName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 连接Windchill数据库来运行自定义sql
	 * 
	 * @return WTConnection 数据库连接
	 * @throws Exception
	 */
	public static WTConnection getWTConnection() throws Exception {
		MethodContext methodcontext = MethodContext.getContext();
		WTConnection wtconnection = (WTConnection) methodcontext.getConnection();
		return wtconnection;
	}

	/**
	 * 执行查询的SQL语句并返回结果
	 * SQL示例：SELECT WTPARTNUMBER FROM WTPARTMASTER WHERE WTPARTNUMBER LIKE ?
	 * @param sql SQL语句
	 * @param params 参数集
	 * @return ResultSet 返回结果集
	 */
	public static ResultSet excuteSelect(String sql, String... params) {
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setString(i + 1, params[i]);
			}

			// 输出当前执行查询操作的SQL语句
			String fullSql = sql;
			for (String param : params) {
				fullSql = fullSql.replaceFirst("\\?", "'" + param + "'");
			}
			System.out.println("--------当前执行查询操作的SQL语句为--------");
			System.out.println(fullSql);

			ResultSet resultSet = statement.executeQuery();
			return resultSet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 执行更新的SQL语句并返回被更新的条数
	 * SQL示例：UPDATE WTPARTMASTER SET WTPARTNUMBER = ? WHERE IDA2A2 = ?
	 * @param sql SQL语句
	 * @param params 参数集
	 * @return int 数据库表被影响的行数
	 */
	public static int excuteUpdate(String sql, String... params) {
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setString(i + 1, params[i]);
			}

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			for (String param : params) {
				fullSql = fullSql.replaceFirst("\\?", "'" + param + "'");
			}
			System.out.println("--------当前执行更新操作的SQL语句为--------");
			System.out.println(fullSql);

			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 执行插入的SQL语句
	 * @param sql 被执行的SQL语句
	 * @param params SQL语句中的参数
	 * @return 是否执行成功
	 */
	public static int excuteInsert(String sql, String... params) {
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setString(i + 1, params[i]);
			}

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			for (String param : params) {
				fullSql = fullSql.replaceFirst("\\?", "\"" + param + "\"");
			}
			System.out.println("--------当前执行插入操作的SQL语句为--------");
			System.out.println(fullSql);
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 携带信息并用POST请求外部系统（如SAP，OA）中的某个接口
	 * 存在账户和密码时则设置验证否则不设置
	 * @param url 外部系统对应的地址
	 * @param json 需要传输的信息
	 * @return 返回信息
	 */
	public static String requestInterface(String url, String username, String password, String json, String method,
			HashMap<String, String> map) {

		// 自定义请求头
		RestTemplate restTemplate = new RestTemplate();
		if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(username)) {
			restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
		}
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("utf-8")));
		HttpHeaders headers = new HttpHeaders();
		for (String set : map) {

		}
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAcceptCharset(Collections.singletonList(Charset.forName("utf-8")));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// 参数
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		ResponseEntity<String> responseEntity = method.equals("GET")
				? restTemplate.exchange(url, HttpMethod.GET, entity, String.class)
				: restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		if (responseEntity == null) {
			return null;
		}

		return responseEntity.getBody().toString();
	}

}
