package ext.plm.supplier;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.doc.WTDocument;
import wt.util.WTProperties;
import ext.sap.SupplierMasterData.SupplierEntity;
import ext.sap.SupplierMasterData.SupplierMasterDataServlet;

public class SupplierHelper {
	
	public static String SUPP_IBA_KEY = "HHT_Supplier";//TODO 供应商IBA属性key
	private static String FILE_PATH ;
	private static long LAST_MODIFIED = 0;
	private static String CONFIG_FILENAME = "supplier_doc.xlsx";
	public static List<String[]> DOC_LIST = new ArrayList<String[]>();
	static{
		try{
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String wt_home = wtproperties.getProperty("wt.home");
			String sep = wtproperties.getProperty("dir.sep");
			StringBuffer tempBuf = new StringBuffer(wt_home);
			tempBuf.append(sep).append("codebase").append(sep).append("ext").append(sep).append("plm").append(sep).append("supplier").append(sep);
			FILE_PATH = tempBuf.toString();
		}catch (Exception e) {
			System.out.println("------Error:supplier doc Config file not found !!!!!!!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取供应商列表
	 * @return
	 */
	public static List<String[]> getAllSupplier(){
		List<String[]> list = new ArrayList<String[]>();
		List<SupplierEntity> entityList = SupplierMasterDataServlet.getAllSupplier();
		//TODO 如果数据库返回数据为空，虚构测试数据
		if(entityList==null || entityList.isEmpty()){
			for(int i=1;i<200;i++){
				String[] data = new String[2];
				data[0] = i+"_interName";
				data[1] = i+"--供应商";
				list.add(data);
			}
		}else{
			for (SupplierEntity entity : entityList) {
				String[] data = new String[2];
				data[0] = entity.getInternalName() ;
				data[1] = entity.getDisplayName();
				list.add(data);
			}
		}
		
		sortListByName(list);
		return list;
	}
	
	
	//升序排序
	public static void sortListByName( List<String[]> dataList ) {
		if(dataList==null){
			return;
		}
		Collections.sort( dataList , new Comparator<String[]>() {
			public int compare( String[] data1 , String[] data2 ) {
				if (data1 == null || data2 == null) {
					return 0;
				}
				String number1 = data1[1].toString();
				String number2 = data2[1].toString();
				return number1.compareTo( number2 );
			}
		} );
	}
	
	
	/**
	 * 获取配置了供应商属性的文档
	 * @return
	 */
	public static List<String[]> getSupplierDoc(){
		File file = new File(FILE_PATH+CONFIG_FILENAME);
		long lastModified = file.lastModified();
		try{
			if( lastModified > LAST_MODIFIED){
				DOC_LIST.clear();
				LAST_MODIFIED = lastModified;
				FileInputStream fs = new FileInputStream(file);
				XSSFWorkbook workbook = new XSSFWorkbook( fs );
				List<Object[]> dataList = ext.plm.change.ECRExcelUtil.getDataFromExcel(workbook, 0, 1, 3);
				for(Object[] data : dataList){
					if(data[0]!=null && StringUtils.isNotBlank(data[0].toString())){
						String[] item = new String[2];
						item[0] = data[0].toString().trim();
						item[1] = data[2].toString().trim();
						DOC_LIST.add(item);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return DOC_LIST;
	}
	
	
	/**
	 * 判断文档是否配置了[供应商]属性
	 * @param wtDoc
	 * @return
	 */
	public static boolean isSupplierDoc(WTDocument wtDoc){
		boolean flag = false;
		try {
			String typeName = ext.plm.util.CommUtil.getDocTypeName( wtDoc );
			List<String[]> docList = getSupplierDoc();
			for(String[] data : docList){
				if(typeName.equalsIgnoreCase(data[0])){
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
}
