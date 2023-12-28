package ext.algorithm.part;

import java.text.SimpleDateFormat;
import java.util.Date;

import wt.inf.container.WTContainerRef;
import wt.rule.algorithm.RuleAlgorithm;
import wt.util.WTException;

public class PartRuleAlgorithm implements RuleAlgorithm {

	@Override
	public Object calculate(Object[] arg, WTContainerRef ref) throws WTException {
		StringBuilder prefix = new StringBuilder();
		// 获取当前时间
		Date date = new Date();

		// 使用SimpleDateFormat格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String formattedDate = sdf.format(date);
		System.out.println("formattedDate" + formattedDate);
		prefix.append(formattedDate);
		System.out.println("编号前缀 ：" + prefix);

		return DBUtil.generateNumber(prefix.toString());
	}

}
