package ext.HHT.part.duplicateCheck.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ext.HHT.Config;
import ext.ait.util.ClassificationUtil;
import ext.ait.util.ContainerUtil;
import wt.part.WTPart;

public class DistrbutePartService {

	/**
	 * 电子/结构/外购成品部件创建时放置到指定的部件库中
	 * 
	 * @param part
	 * @return
	 */
	public static String process(WTPart part) {
		if (part == null) {
			return "";
		}
		String source = part.getSource().toString();
		String classification = Config.getHHT_Classification(part);
		String buy = Config.getBuy();
		if (StringUtils.isNotBlank(classification) && source.equals(buy) && classification.startsWith("5")) {
			distrbuteFinished(part, classification);
		} else if (StringUtils.isNotBlank(classification)) {
			if (classification.startsWith("1")) {
				distrbuteParts(part, classification, "electrical");
			} else if (classification.startsWith("2") && Config.getStructureNodes().contains(classification)) {
				distrbuteParts(part, classification, "structure");
			} else if (classification.startsWith("3")) {
				distrbuteParts(part, classification, "packaging");
			}
		}
		return "";
	}

	/**
	 * 创建外购成品时修改物料组属性，然后放置到指定产品库 物料组规则 50111->60111
	 * 
	 * @param part
	 * @param classification
	 */
	private static void distrbuteFinished(WTPart part, String classification) {
		String finishLibrary = Config.getFinishLibrary();
		Config.setHHT_MaterialGroup(part, "6" + classification.substring(1));
		Set<String> folderSet = ContainerUtil.getContainerFolders(finishLibrary);
		boolean flag = true;
		for (String folder : folderSet) {
			if (folder.startsWith(classification)) {
				folder = "6" + folder.substring(1);
				ContainerUtil.moveObj2FolderWithContainer(part, finishLibrary, "6" + folder.substring(1));
				flag = false;
				break;
			}
		}
		if (flag) {
			String folderName = ClassificationUtil.getClassificationdDisPlayName(classification);
			ContainerUtil.moveObj2FolderWithContainer(part, finishLibrary, "6" + folderName.substring(1));
		}
	}

	/**
	 * 当创建电子/结构部件时放置到其对应指定的库中
	 * 
	 * @param part
	 * @param classification
	 * @param sign
	 */
	private static void distrbuteParts(WTPart part, String classification, String sign) {
		String library = "";
		switch (sign) {
		case "electrical":
			library = Config.getElectricalLibrary();
			break;
		case "structure":
			library = Config.getStructureLibrary();
			break;
		case "packaging":
			library = Config.getPackagingLibrary();
			break;
		default:
			library = "";
		}
		Set<String> folderSet = ContainerUtil.getContainerFolders(library);
		boolean flag = true;
		for (String folder : folderSet) {
			if (folder.startsWith(classification)) {
				ContainerUtil.moveObj2FolderWithContainer(part, library, folder);
				flag = false;
				break;
			}
		}
		if (flag) {
			String folderName = ClassificationUtil.getClassificationdDisPlayName(classification);
			ContainerUtil.moveObj2FolderWithContainer(part, library, folderName);
		}
	}
}
