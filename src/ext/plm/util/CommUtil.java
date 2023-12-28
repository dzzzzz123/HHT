package ext.plm.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

public class CommUtil {
	
	public static WTPartUsageLink getLinkByPart( WTPart fatherPart , WTPart sonPart ) {
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters( fatherPart );
			while ( qr.hasMoreElements() ) {
				WTPartUsageLink link = ( WTPartUsageLink )qr.nextElement();
				WTPart part = getBomPartBylink( link );
				if (part.getNumber().equals( sonPart.getNumber() )) {
					return link;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到link对应的bom子部件
	 * @param WTPart
	 *            
	 * @return WTPart
	 * @throws Exception
	 */
	public static WTPart getBomPartBylink( WTPartUsageLink link ) {
		try {
			QueryResult qr = VersionControlHelper.service.allVersionsOf( link.getUses() );
			if (qr.hasMoreElements()) {
				WTPart part = ( WTPart )qr.nextElement();
				return part;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 根据part得到其包含子部件 的所有部件(包括其本身)
	 * 
	 * @param WTPart
	 * @param Set
	 *            <WTPart>
	 * @return Set<WTPart>
	 */
	public static Set<WTPart> getHasBomPartsByPart( WTPart productPart , Set<WTPart> set ) {
		WTPart sPart = null;
		QueryResult qr2 = null;
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters( productPart );
			while ( qr.hasMoreElements() ) {
				set.add( productPart );
				WTPartUsageLink usageLink = ( WTPartUsageLink )qr.nextElement();
				qr2 = VersionControlHelper.service.allVersionsOf( usageLink.getUses() );
				if (qr2.hasMoreElements()) {
					sPart = ( WTPart )qr2.nextElement();
					getHasBomPartsByPart( sPart , set );
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * @param WTPart
	 *            fatherPart 根据父部件获取 link
	 * @return WTPart
	 * @throws Exception
	 */
	public static List<WTPartUsageLink> getLinksByPart( WTPart fatherPart ) {
		List<WTPartUsageLink> list = new ArrayList<WTPartUsageLink>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters( fatherPart );
			while ( qr.hasMoreElements() ) {
				WTPartUsageLink link = ( WTPartUsageLink )qr.nextElement();
				list.add( link );
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
	public static WTPart getWTPartByNumber( String number ) {
		WTPart result = null;
		QueryResult qr = null;
		try {
			QuerySpec qs = new QuerySpec( WTPart.class );
			SearchCondition scnumber = new SearchCondition( WTPart.class , wt.part.WTPart.NUMBER , SearchCondition.EQUAL , number.toUpperCase() );
			qs.appendSearchCondition( scnumber );
			qs.appendAnd();
			SearchCondition sclatest = VersionControlHelper.getSearchCondition( wt.part.WTPart.class , true );
			qs.appendSearchCondition( sclatest );
			qr = PersistenceHelper.manager.find( qs );
			LatestConfigSpec cfg = new LatestConfigSpec();
			qr = cfg.process( qr );
			if (qr != null && qr.hasMoreElements()) {
				result = ( WTPart )qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<WTPart> getBomByPart( WTPart productPart) {
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters( productPart );
			while ( qr.hasMoreElements() ) {
				WTPartUsageLink usageLink = ( WTPartUsageLink )qr.nextElement();
				QueryResult qr2 = VersionControlHelper.service.allVersionsOf( usageLink.getUses() );
				if (qr2.hasMoreElements()) {
					WTPart sPart = ( WTPart )qr2.nextElement();
					list.add( sPart );
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private static final String DATE_STYLE = "yyyy-MM-dd";
	public static String getFormatDate(Timestamp time,String formateStr){
		String currentTime = "";
		if(time == null){
			return currentTime;
		}
		if(formateStr==null || "".equals( formateStr.trim() )){
			formateStr = DATE_STYLE;
		}
		try{
			DateFormat dateFormat = new SimpleDateFormat(formateStr);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			currentTime = (time!=null?dateFormat.format(time):"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentTime;
	}
	public static String formatDate(Date date,String formatStr) {		 
		String currentTime = "";
		try {		
			 TimeZone zone = TimeZone.getTimeZone("GMT+8:00");  
		        SimpleDateFormat sdf = new SimpleDateFormat(formatStr,Locale.ENGLISH);  
		        sdf.setTimeZone(zone);  
		        currentTime = sdf.format(date); 		        
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return currentTime;
	}
	
	public static List<ApplicationData> getContents(ContentHolder contentholder , ContentRoleType roleType ) {
		List<ApplicationData> contents = new ArrayList<ApplicationData>();
		try {
			contentholder = ContentHelper.service.getContents( contentholder );
			QueryResult qr = ContentHelper.service.getContentsByRole( contentholder , roleType );
			while ( qr.hasMoreElements() ) {
				contents.add( ( ApplicationData )qr.nextElement() );
			}
		} catch (WTException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public static void updateContent(ContentHolder contentholder ,ContentRoleType roleType, String filePath ) {
		try {
			ContentHolder contentHolder = ContentHelper.service.getContents( contentholder );
			ApplicationData applicationData = ApplicationData.newApplicationData( contentHolder );
			applicationData.setRole(roleType);
			applicationData = ContentServerHelper.service.updateContent( contentHolder , applicationData , filePath );
			applicationData = ( ApplicationData )PersistenceHelper.manager.save( applicationData );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
