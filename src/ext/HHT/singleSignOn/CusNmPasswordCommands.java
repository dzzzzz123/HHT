package ext.HHT.singleSignOn;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.infoengine.util.IEException;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.user.NmPasswordCommands;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.CommonUtil;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class CusNmPasswordCommands extends NmPasswordCommands {
	public static FormResult changePassword(NmCommandBean paramNmCommandBean)
			throws IEException, WTPropertyVetoException, WTException {
		FormResult formResult = new FormResult();
		// 获取用户名
		NmOid nmOid = paramNmCommandBean.getPageOid();
		System.out.println("J015_nmOid:" + nmOid);
		ReferenceFactory rf = new ReferenceFactory();
		WTReference reference = rf.getReference(nmOid.toString());
		System.out.println("J015_reference:" + reference.toString());
		WTUser user = (WTUser) reference.getObject();
		System.out.println("J015_user:" + user.getName());
		String username = user.getName();

		String usql = "SELECT IDA2A2 FROM WTUSER WHERE NAME =?";
		ResultSet uresultSet = CommonUtil.excuteSelect(usql, username);
		String IDA2A2 = "";
		try {
			if (uresultSet.next()) {
				IDA2A2 = uresultSet.getString("IDA2A2");
				System.out.println("IDA2A2:" + IDA2A2);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// 获取修改后的密码
		String newPassword = NmPasswordCommands.getNewPassword(paramNmCommandBean);
		System.out.println("J01_newPassword:" + newPassword);
		try {
			// 将密码进行加密
			newPassword = AESUtil.Encryption(newPassword);
			System.out.println("J01_newPassword:" + newPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 从TCUSERACCESS表中根据用户名查询数据
		String sql = "SELECT * FROM TCUSERACCESS WHERE USERID = ?";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, username);

		try {
			if (resultSet.next()) {

				// 修改密码
				String sqls = "UPDATE TCUSERACCESS SET PASSWORD=? where USERID=?";
				int number = CommonUtil.excuteUpdate(sqls, newPassword, username);
				System.out.println("J01_number:" + number);

			} else {
				String insertSql = "INSERT INTO TCUSERACCESS(ID,USERID,PASSWORD) VALUES (?,?,?)";
				int num = CommonUtil.excuteInsert(insertSql, IDA2A2, username, newPassword);
				System.out.println("J01_num:" + num);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 执行原有的编辑代码方法
		formResult = NmPasswordCommands.changePassword(paramNmCommandBean);
		return formResult;
	}

}
