package ext.listener.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.PersistenceUtil;
import ext.listener.Config;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;

public class WTDocumentService {

	public static void process_POST_STORE(WTDocument document) throws WTException {
		if (!PersistenceUtil.isCheckOut(document)
				&& Config.getDocType().contains(PersistenceUtil.getSubTypeInternal(document))) {
			List<WTPart> partList = findAllWTPartByNumber(Config.getHHT_PartNumber(document));
			if (partList.isEmpty()) {
				throw new WTException("部件编号填写错误，请编辑检出该文档，重写输入正确的部件编号!");
			} else {
				for (WTPart wtPart : partList) {
					if ((wtPart.getViewName()).equals("Design")) {
						WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(wtPart, document);
						PersistenceServerHelper.manager.insert(link);
						PersistenceHelper.manager.refresh(link);
					}
				}
			}
		}
	}

	/**
	 * 根据编号或者名称查询部件
	 * 
	 * @param partNumber
	 * @return
	 */
	public static List<WTPart> findAllWTPartByNumber(String partNumber) {
		QueryResult qr = null;
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			QuerySpec querySpec = new QuerySpec(WTPart.class);
			if (StringUtils.isNotBlank(partNumber)) {
				WhereExpression where = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL,
						partNumber);
				querySpec.appendWhere(where);
				qr = PersistenceHelper.manager.find((StatementSpec) querySpec);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
					list.add(part);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

}
