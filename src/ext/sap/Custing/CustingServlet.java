package ext.sap.Custing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.commons.lang.util.StringUtils;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.pom.WTConnection;

public class CustingServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		ReferenceFactory rf = new ReferenceFactory();

		// 查询当前组件信息
		WTPart part = (WTPart) rf.getReference(oid).getObject();

		// 构建树形结构
		Tree tree = new Tree();
		doopAllWtpart(part, tree, true);
		if (tree == null || tree.getData() == null || tree.getData().size() == 0) {
			return null;
		}

		// 遍历所有的物料号
		List<String> xlhList = new ArrayList<>();
		loopWlh(tree, xlhList);
		// 拼接sap参数
		String param = CustingSAPService.formatSapParam(xlhList);
		// 掉sap获取响应
		String resultSap = CustingSAPService.getCustingFromSap(param);
		ObjectMapper objectMapper = new ObjectMapper();
		Map map = objectMapper.readValue(resultSap, Map.class);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = (List<Map<String, String>>) map.get("ET_ITEM");
		List<CustingEntity> finalList = new ArrayList<>();
		WTConnection connection = CommonUtil.getWTConnection();
		loopAutoSapInfo(tree, list, finalList, connection);
		if (connection != null) {
			connection.disconnect();
		}
		List<CustingEntity> tempList = new ArrayList<>();
		for (int i = 1; i < finalList.size(); i++) {
			CustingEntity entity = finalList.get(i);
			if (!entity.isMaster()) {
				double sum = finalList.stream().filter(obj -> entity.getParent().equals(obj.getNumber()))
						.mapToDouble(obj -> formatDouble(obj.getPrice()) * formatDouble(obj.getAmount())).sum();
				if (sum > 0) {
					entity.setPrice(null);
				} else {
					entity.setPrice(formatDouble(formatDouble(entity.getPrice()) * formatDouble(entity.getAmount())));
				}
			} else if (entity.isMaster() && entity.getPrice() != null && entity.getPrice() > 0) {
				entity.setPrice(formatDouble(formatDouble(entity.getPrice()) * formatDouble(entity.getAmount())));
			} else {
				entity.setPrice(null);
			}

			tempList.add(entity);
		}

		double sum = tempList.stream().mapToDouble(obj -> {
			double zjMtr = obj.getPrice() == null ? 0 : obj.getPrice();
			return zjMtr;
		}).sum();
		tempList.add(autoTableRow(0, formatDouble(sum)));
		String result = CommonUtil.getJsonFromObject(tempList);
		response.getWriter().write(result.toString());
		return null;
	}

	public static Double formatDouble(Double number) {
		if (number == null) {
			return 0.0;
		}
		try {
			String str = new DecimalFormat("#.####").format(number);
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0.0;
		}
	}

	public static List<WTPart> sortList(List<WTPart> list) {
		Collections.sort(list, (obj1, obj2) -> {
			// 处理数据为null的情况
			String str1 = obj1.getNumber();
			String str2 = obj2.getNumber();

			if (str1 == null && str2 == null) {
				return 0;
			}
			if (str1 == null) {
				return -1;
			}
			if (str2 == null) {
				return 1;
			}
			// 循环次数
			int forSize = Math.min(str1.length(), str2.length());
			// 逐字比较返回结果
			for (int i = 0; i < forSize; i++) {
				if (str1.charAt(i) != str2.charAt(i)) {
					return str1.charAt(i) - str2.charAt(i);
				}
			}
			return str1.length() - str2.length();
		});
		return list;
	}

	@SuppressWarnings("unlikely-arg-type")
	private static void loopAutoSapInfo(Tree tree, List<Map<String, String>> list, List<CustingEntity> returnList,
			WTConnection connection) {
		String number = tree.getNumber();
		Double cb = 0.0;
		List<Double> cbList = list.stream().filter(obj -> obj.get("MATNR").equals(number))
				.map(obj -> Double.parseDouble(obj.get("VERPR"))).collect(Collectors.toList());
		if (cbList != null && !cbList.isEmpty()) {
			cb = cbList.get(0);
		}

		WTPart parent = StringUtils.isBlank(tree.getParent()) ? tree.getWtpart()
				: PartUtil.getWTPartByNumber(tree.getParent());
		CustingEntity entity = CustingSAPService.getBOMEntity(parent, tree.getWtpart(), connection);
		List<Tree> sonList = tree.getData();
		if (CollectionUtils.isNotEmpty(sonList)) {
			entity.setMaster(true);
			entity.setParent(tree.getParent());
			entity.setPrice(cb);
			returnList.add(entity);
			for (int i = 0; i < sonList.size(); i++) {
				loopAutoSapInfo(sonList.get(i), list, returnList, connection);
			}
		} else {
			entity.setMaster(false);
			entity.setParent(tree.getParent());
			entity.setPrice(cb);
			returnList.add(entity);
		}
	}

	private static void loopWlh(Tree tree, List<String> list) {
		List<Tree> treeList = tree.getData();
		list.add(tree.getNumber());
		if (treeList.size() > 0) {
			for (int i = 0; i < treeList.size(); i++) {
				loopWlh(treeList.get(i), list);
			}
		}
	}

	static class Tree {
		private String parent;
		private String name;
		private String number;
		private WTPart wtpart;
		private List<Tree> data;

		public List<Tree> getData() {
			return data;
		}

		public void setData(List<Tree> data) {
			this.data = data;
		}

		public WTPart getWtpart() {
			return wtpart;
		}

		public void setWtpart(WTPart wtpart) {
			this.wtpart = wtpart;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		Tree(String name, String parent, WTPart wtpart, List<Tree> data, String number) {
			this.name = name;
			this.parent = parent;
			this.wtpart = wtpart;
			this.data = data;
			this.number = number;
		}

		Tree() {

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

	}

	private static Tree autoTree(WTPart wtpart, WTPart self) {
		return new Tree(self.getName(), wtpart.getNumber(), self, new ArrayList<Tree>(), self.getNumber());
	}

	private static void doopAllWtpart(WTPart wtPart, Tree result, boolean root) {
		if (root) {
			result.setName(wtPart.getName());
			result.setNumber(wtPart.getNumber());
			result.setWtpart(wtPart);
			List<WTPart> sonList = PartUtil.getBomByPart(wtPart);
			if (CollectionUtils.isNotEmpty(sonList)) {
				sortList(sonList);
				result.setData(new ArrayList<Tree>());
				for (int i = 0; i < sonList.size(); i++) {
					List<Tree> nowList = result.getData();
					Tree newTree = autoTree(wtPart, sonList.get(i));
					nowList.add(newTree);
					result.setData(nowList);
					doopAllWtpart(sonList.get(i), newTree, false);
				}
			}
		} else {
			List<WTPart> sonList = PartUtil.getBomByPart(wtPart);
			if (CollectionUtils.isNotEmpty(sonList)) {
				sortList(sonList);
				for (int i = 0; i < sonList.size(); i++) {
					List<Tree> nowList = result.getData();
					Tree newTree = autoTree(wtPart, sonList.get(i));
					nowList.add(newTree);
					result.setData(nowList);
					doopAllWtpart(sonList.get(i), newTree, false);
				}
			}
		}
	}

	private CustingEntity autoTableRow(int i, double zj) {
		CustingEntity entity = new CustingEntity();
		entity.setPrice(zj);
		return entity;
	}

}
