package ext.plm.change;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.plm.util.CommUtil;

public class ChangeUtil {
	
	/**
	 * ECR流程调用：将ECA更改单附件 挂到ECR附件
	 * @param pbo
	 * @return
	 */
	public static String createECRFile(WTObject pbo){
		String msg = "";
		try {
			if (pbo instanceof WTChangeRequest2) {
				WTChangeRequest2 ecr = ( WTChangeRequest2 )pbo;
				System.out.println("------createECRFile ecr:"+ecr.getNumber());
				QueryResult qr = ChangeHelper2.service.getChangeOrders( ecr );
				while ( qr.hasMoreElements() ) {
					Object obj = qr.nextElement();
					if (obj instanceof WTChangeOrder2) {
						WTChangeOrder2 co = ( WTChangeOrder2 )obj;
						QueryResult changeActivities = ChangeHelper2.service.getChangeActivities( co );
						while ( changeActivities.hasMoreElements() ) {
							Object nextElement = changeActivities.nextElement();
							if (nextElement instanceof WTChangeActivity2) {
								WTChangeActivity2 eca = ( WTChangeActivity2 )nextElement;
								System.out.println("------createECRFile copy eca:"+eca.getNumber());
								copyECAFile2ECR(eca,ecr);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * 将eca最新的更改单  附件 挂到ECR
	 * @param eca
	 * @param ecr
	 */
	public static void copyECAFile2ECR(WTChangeActivity2 eca,WTChangeRequest2 ecr){
		try {
			List<ApplicationData> list = CommUtil.getContents(eca, ContentRoleType.SECONDARY);
			if(list==null  || list.size()==0){
			}else{
				ApplicationData copy_app = null;
				long time = 0;
				for(ApplicationData app : list){
					if(app.getFileName().contains("变更通知单_V")){
						long app_time = app.getModifyTimestamp()==null?0:app.getModifyTimestamp().getTime();
						if(app_time>=time){
							time = app_time;
							copy_app = app;
						}
					}
				}
				if(copy_app!=null){
					//删除旧版本附件
					List<ApplicationData> ecr_list = CommUtil.getContents(ecr, ContentRoleType.SECONDARY);
					if(ecr_list!=null){
						for(ApplicationData app : ecr_list){
							if(app.getFileName().startsWith(eca.getNumber()+"_变更通知单")){
								PersistenceHelper.manager.delete(app);
							}
						}
					}
					
					ApplicationData ap = ApplicationData.newApplicationData( ecr );
					ap.setRole( ContentRoleType.SECONDARY );
					ap.setFileName( copy_app.getFileName() );
					ap.setFileSize( copy_app.getFileSize() );
					InputStream inputStream = ContentServerHelper.service.findContentStream( copy_app );
					ContentServerHelper.service.updateContent(ecr, ap, inputStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ECA流程调用：生成更改通知单EXCEL，并挂到ECA附件
	 * @param pbo
	 * @return
	 */
	public static String createECAFile(WTObject pbo){
		String msg = "";
		try {
			if (pbo instanceof WTChangeActivity2) {
				WTChangeActivity2 eca = ( WTChangeActivity2 )pbo;
				System.out.println("--------createECAFile eca:"+eca.getNumber());
				List<WTPart> part_list = getBeforeParts(eca);
				Map<String,WTPart> after_map = getAfterParts(eca);
				System.out.println("--------before part:"+part_list.size()+"--after parts:"+after_map.size());
				//BOM变更信息
				List<CompareMessage> all_list = new ArrayList<CompareMessage>();
				for(WTPart old_part : part_list){
					//比较视图相同的相同物料 2024-2-26 
					String number_view = old_part.getNumber()+"_"+old_part.getViewName();
					WTPart new_part = after_map.get(number_view);
					List<CompareMessage> messList = BomCompareHepler.comparePart(old_part, new_part);
					if(messList!=null && messList.size()>0){
						all_list.addAll(messList);
					}
				}
				System.out.println("---------mess list:"+all_list.size());
				QueryResult qr = ChangeHelper2.service.getChangeOrder(eca);
				WTChangeOrder2 ecn = null;
				if(qr.size()>0){
					ecn = (WTChangeOrder2)qr.nextElement();
				}
				//变更单流水号
				String version = getECAFileFlow(eca)+"";
				//生成Excel
				String fileName = eca.getNumber()+"_变更通知单_V"+version+".xlsx";
				String outFilePath = ECRExcelUtil.writeInfo2Excel(ecn, eca.getNumber(), fileName,all_list);
				//上传ECA附件
				CommUtil.updateContent(eca,ContentRoleType.SECONDARY,outFilePath);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return msg;
	}
	
	/**
	 * 获取ECA附件
	 * @param eca
	 * @return
	 */
	private static int getECAFileFlow(WTChangeActivity2 eca){
		int flowNum = 1;
		List<ApplicationData> list = CommUtil.getContents(eca, ContentRoleType.SECONDARY);
		if(list==null  || list.size()==0){
		}else{
			for(ApplicationData app : list){
				if(app.getFileName().contains("变更通知单_V")){
					flowNum++;
				}
			}
		}
		return flowNum;
	}
	
	
	public static List<WTPart> getBeforeParts(WTChangeActivity2 eca){
		List<WTPart> changePartList = new ArrayList<WTPart>();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore( eca );// 受影响对象列表
			while ( qr.hasMoreElements() ) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = ( WTPart )obj;
					changePartList.add( part );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return changePartList;
	}
	
	public static Map<String,WTPart> getAfterParts(WTChangeActivity2 eca){
		Map<String,WTPart> map = new HashMap<String,WTPart>();
		try {
			QueryResult changeablesAfter = ChangeHelper2.service.getChangeablesAfter( eca );//产生的对象
			while ( changeablesAfter.hasMoreElements() ) {
				Object object = ( Object )changeablesAfter.nextElement();
				if (object instanceof WTPart) {
					WTPart part = ( WTPart )object;
					String view1 = part.getViewName();
					map.put(part.getNumber()+"_"+view1, part );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static WTChangeRequest2 getECRByNumber( String number ){
		try {
			QuerySpec qs = new QuerySpec( WTChangeRequest2.class );
			SearchCondition scnumber = new SearchCondition( WTChangeRequest2.class , WTChangeRequest2.NUMBER , SearchCondition.EQUAL , number );
			qs.appendSearchCondition( scnumber );
			qs.appendAnd();
			SearchCondition sclatest = VersionControlHelper.getSearchCondition( WTChangeRequest2.class , true );
			qs.appendSearchCondition( sclatest );
			QueryResult qr = PersistenceHelper.manager.find( qs );
			LatestConfigSpec cfg = new LatestConfigSpec();
			qr = cfg.process( qr );
			if (qr != null && qr.hasMoreElements()) {
				return ( WTChangeRequest2 )qr.nextElement();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static WTChangeOrder2 getECNByNumber( String number ){
		try {
			QuerySpec qs = new QuerySpec( WTChangeOrder2.class );
			SearchCondition scnumber = new SearchCondition( WTChangeOrder2.class , WTChangeOrder2.NUMBER , SearchCondition.EQUAL , number );
			qs.appendSearchCondition( scnumber );
			qs.appendAnd();
			SearchCondition sclatest = VersionControlHelper.getSearchCondition( WTChangeOrder2.class , true );
			qs.appendSearchCondition( sclatest );
			QueryResult qr = PersistenceHelper.manager.find( qs );
			LatestConfigSpec cfg = new LatestConfigSpec();
			qr = cfg.process( qr );
			if (qr != null && qr.hasMoreElements()) {
				return ( WTChangeOrder2 )qr.nextElement();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static WTChangeActivity2 getECAByNumber( String number ){
		try {
			QuerySpec qs = new QuerySpec( WTChangeActivity2.class );
			SearchCondition scnumber = new SearchCondition( WTChangeActivity2.class , WTChangeActivity2.NUMBER , SearchCondition.EQUAL , number );
			qs.appendSearchCondition( scnumber );
			qs.appendAnd();
			SearchCondition sclatest = VersionControlHelper.getSearchCondition( WTChangeActivity2.class , true );
			qs.appendSearchCondition( sclatest );
			QueryResult qr = PersistenceHelper.manager.find( qs );
			LatestConfigSpec cfg = new LatestConfigSpec();
			qr = cfg.process( qr );
			if (qr != null && qr.hasMoreElements()) {
				return ( WTChangeActivity2 )qr.nextElement();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ApplicationData getEcaAppData(WTChangeActivity2 eca){
		ApplicationData copy_app = null;
		try {
			List<ApplicationData> list = CommUtil.getContents(eca, ContentRoleType.SECONDARY);
			if(list==null  || list.size()==0){
			}else{
				long time = 0;
				for(ApplicationData app : list){
					if(app.getFileName().contains("变更通知单_V")){
						long app_time = app.getModifyTimestamp()==null?0:app.getModifyTimestamp().getTime();
						if(app_time>=time){
							time = app_time;
							copy_app = app;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return copy_app;
	}
	
	/**
	 * ECA任务保存更改单内容
	 * @param nmCommandBean
	 * @param eca
	 * @throws Exception 
	 */
	public static void saveECAMess(NmCommandBean nmCommandBean, WTChangeActivity2 eca) throws WTException{
		try {
			//填写页面才有ecaKeyStr值
			String ecaKeyStr = nmCommandBean.getRequest().getParameter( "ecaKeyStr" );
//			System.out.println("--------ecaKeyStr:"+ecaKeyStr);
			//获取页面填写数据
			List<CompareMessage> all_list = new ArrayList<CompareMessage>();
			if(StringUtils.isNotBlank(ecaKeyStr)){
				String[] keys = ecaKeyStr.split(",");
				String ecaMessCount = nmCommandBean.getRequest().getParameter( "ecaMessCount" );
				int count = Integer.parseInt(ecaMessCount);
				Map<String, Method> setths = getClassSetMthds(CompareMessage.class);
				for(int i=1;i<count;i++){
					CompareMessage item = new CompareMessage();
					boolean isHasVal = false;//该行是否有值
					for(String key : keys){
						String val = nmCommandBean.getRequest().getParameter( key+"_"+i );
//						System.out.println(key+"_"+i+"-----val:"+val);
						if(StringUtils.isBlank(val)){
							continue;
						}
						isHasVal = true;
						String fld = key;
						setths.get( fld.toLowerCase() ).invoke( item , new Object[] { val } );
					}
					if(isHasVal){
						all_list.add(item);
					}
				}
			}else{//审核页面 无需更新
				return;
			}
			//生成Excel
			QueryResult qr = ChangeHelper2.service.getChangeOrder(eca);
			WTChangeOrder2 ecn = null;
			if(qr.size()>0){
				ecn = (WTChangeOrder2)qr.nextElement();
			}
			//更新ECA附件
			ApplicationData app = getEcaAppData(eca);
			String fileName = eca.getNumber()+"_变更通知单_V1.xlsx";
			//第一份更改单
			if(app==null){
				app = ApplicationData.newApplicationData( eca );
				app.setRole( ContentRoleType.SECONDARY );
				app.setFileName( fileName );
			}else{
				fileName = app.getFileName();
			}
			System.out.println("----update--fileName:"+fileName);
			String outFilePath = ECRExcelUtil.writeInfo2Excel(ecn, eca.getNumber(), fileName,all_list);
			app = ContentServerHelper.service.updateContent(eca, app, outFilePath);
			app = (ApplicationData) PersistenceHelper.manager.save(app);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException("保存更改单错误："+e.getLocalizedMessage());
		}
	}
	
	private static Map<String, Method> getClassSetMthds(Class<?> theClass) throws Exception {
		Map<String, Method> setths = new HashMap<String, Method>();
		BeanInfo beaninfo = Introspector.getBeanInfo(theClass);
		PropertyDescriptor[] propertyDescriptors = beaninfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : propertyDescriptors) {
			String pname = pd.getName().toLowerCase();
			if (!pname.equals("class")) {
				Method writMethod = pd.getWriteMethod();
				setths.put(pname, writMethod);
			}
		}
		return setths;
	}
}
