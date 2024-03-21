package ext.plm.change;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

public class BomCompareHepler {
	
	public static List<CompareMessage> comparePart(WTPart oldPart,WTPart newPart) {
		//比较信息
		List<CompareMessage> listMess =  getCompareMessage(oldPart,newPart);
		return listMess;
	}
	
	private static List<CompareMessage> getCompareMessage( WTPart parentPart1 , WTPart parentPart2 ) {
		// 存放比较信息
		List<CompareMessage> list = new ArrayList<CompareMessage>();
		if(parentPart1==null || parentPart2==null || parentPart1.equals( parentPart2 )){
			return null;
		}
		
		Map<String,Object[]> sonMap1 = getSingleBomByParentPart( parentPart1 );
		Map<String,Object[]> sonMap2 = getSingleBomByParentPart( parentPart2 );
		String view1 = " ("+parentPart1.getViewName()+")";
		String view2 = " ("+parentPart2.getViewName()+")";
		String version1 = parentPart1.getVersionIdentifier().getValue() + "." + parentPart1.getIterationIdentifier().getValue()+view1;
		String version2 = parentPart2.getVersionIdentifier().getValue() + "." + parentPart2.getIterationIdentifier().getValue()+view2;
		//0、更改物料
		if (!version1.equals(version2)) {
			CompareMessage mess = new CompareMessage();
			mess.setChangeType("修改物料");
			mess.setSonPartNumber_old(parentPart2.getNumber());
			mess.setSonPartDesc_old(parentPart2.getName());
			mess.setSonPartNumber_new(parentPart2.getNumber());
			mess.setSonPartDesc_new(parentPart2.getName());
			mess.setFatherPartVer_old(version1);
			mess.setFatherPartVer_new(version2);
			list.add(mess);
		}
		
		//对编号相同的子部件做比较
		for (String sonPartNum : sonMap1.keySet()) {
			Object[] part1 = sonMap1.get(sonPartNum);
			Object[] part2 = sonMap2.get( sonPartNum );
			WTPart old_son_part = (WTPart)part1[0];
			//1、子料被删除
			if(part2 == null){
				CompareMessage mess = new CompareMessage();
				mess.setFatherPartNumber(parentPart2.getNumber());
				mess.setFatherPartDesc(parentPart2.getName());
				mess.setFatherPartVer_old(version1);
				mess.setChangeType( "移除用料" );
				mess.setSonPartNumber_old( sonPartNum );
				mess.setSonPartDesc_old(old_son_part.getName());
				String unit1 = (String)part1[2];
				String quantiy1 = ""+part1[1];
				mess.setUnit_old(unit1);
				mess.setQuantity_old(quantiy1);
				list.add( mess );
			}else{
				//2、子料修改
				CompareMessage mess = compareSameSonPart(part1,part2);
				if(mess!=null){
					mess.setFatherPartNumber(parentPart2.getNumber());
					mess.setFatherPartDesc(parentPart2.getName());
					mess.setFatherPartVer_old(version1);
					mess.setFatherPartVer_new(version2);
					list.add( mess );
				}
			}
		}
		
		//3、新增子料
		for (String sonPartNum : sonMap2.keySet()) {
			Object[] part1 = sonMap1.get( sonPartNum );
			if(part1 == null){
				CompareMessage mess = new CompareMessage();
				mess.setFatherPartNumber(parentPart2.getNumber());
				mess.setFatherPartDesc(parentPart2.getName());
				mess.setFatherPartVer_new(version2);
				mess.setChangeType( "增加用料" );
				mess.setSonPartNumber_new( sonPartNum );
				Object[] part2 = sonMap2.get(sonPartNum);
				WTPart new_son_part = (WTPart)part2[0];
				String quantiy2 = ""+part2[1];
				String unit2 = (String)part2[2];
				mess.setSonPartDesc_new(new_son_part.getName());
				mess.setUnit_new(unit2);
				mess.setQuantity_new(quantiy2);
				String version = new_son_part.getVersionIdentifier().getValue() + "." + new_son_part.getIterationIdentifier().getValue();
				version += " ("+new_son_part.getViewName()+")";
				mess.setVersion( version );
				list.add( mess );
			}
		}
		
		return list;
	}
	
	
	private static Map<String,Object[]> getSingleBomByParentPart( WTPart parentPart) {
		Map<String,Object[]> sonMap = new HashMap<String,Object[]>();//Object[]: sonpart,quantity,unit
		try {
			WTPart sonPart = null;
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters( parentPart );
			while ( qr.hasMoreElements() ) {
				WTPartUsageLink usageLink = ( WTPartUsageLink )qr.nextElement();
				QueryResult qr2 = VersionControlHelper.service.allVersionsOf( usageLink.getUses() );
				if (qr2.hasMoreElements()) {
					sonPart = ( WTPart )qr2.nextElement();
					Object[] hadPartAtt = sonMap.get( sonPart.getNumber() );
					if(hadPartAtt != null) {
						hadPartAtt[ 1 ] = ( Double )hadPartAtt[ 1 ] + usageLink.getQuantity().getAmount();
					}else{
						Object[] obj = new Object[] { sonPart, usageLink.getQuantity().getAmount(), usageLink.getQuantity().getUnit().getDisplay(),usageLink };
						sonMap.put( sonPart.getNumber(), obj);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return sonMap;
	}
	
	/**
	 * 比较相同子料
	 * @param parentPart1
	 * @param sonP1
	 * @param parentPart2
	 * @param sonP2
	 * @return
	 */
	private static CompareMessage compareSameSonPart(Object[] sonP1,Object[] sonP2 ) {
		try {
			WTPart part1 = (WTPart)sonP1[0];
			String quantiy1 = ""+sonP1[1];
			String unit1 = (String)sonP1[2];
			
			String quantiy2 = ""+sonP2[1];
			String unit2 = (String)sonP2[2];
			boolean isUpdate = false;
			if(!quantiy1.equals( quantiy2 )){
				isUpdate = true;
			}
			if(isUpdate){
				CompareMessage mess = new CompareMessage();
				mess.setSonPartNumber_old(part1.getNumber());
				mess.setSonPartDesc_old(part1.getName());
				mess.setSonPartNumber_new(part1.getNumber());
				mess.setSonPartDesc_new(part1.getName());
				mess.setChangeType( "更改数量" );
				
				String version = part1.getVersionIdentifier().getValue() + "." + part1.getIterationIdentifier().getValue();
				version += " ("+part1.getViewName()+")";
				mess.setVersion( version );
				mess.setUnit_old(unit1);
				mess.setUnit_new(unit2);
				mess.setQuantity_old(quantiy1);
				mess.setQuantity_new(quantiy2);
				return mess;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
