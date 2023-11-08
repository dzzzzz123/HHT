package ext.HHT.part.duplicateCheck;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.ContainerUtil;
import ext.ait.util.PropertiesUtil;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.part.WTPart;

public class DistrbutePartHelper {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

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
		String classification = pUtil.getValueByKey(part, "iba.internal.HHT_Classification");
		String buy = pUtil.getValueByKey("source.buy");
		if (StringUtils.isNotBlank(classification) && source.equals(buy) && classification.startsWith("5")) {
			distrbuteFinished(part, classification);
		} else if (StringUtils.isNotBlank(classification)) {
			if (classification.startsWith("1")) {
				distrbuteParts(part, classification, "electrical");
			} else if (classification.startsWith("2")) {
				distrbuteParts(part, classification, "structure");
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
		String finishLibrary = pUtil.getValueByKey("finish.library.name");
		pUtil.setValueByKey(part, "iba.internal.HHT_MaterialGroup", "6" + classification.substring(1));
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
			library = pUtil.getValueByKey("electrical.library.name");
			break;
		case "structure":
			library = pUtil.getValueByKey("structure.library.name");
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

	/**
	 * 将新创建的物料移动到指定库下的文件夹中
	 * 
	 * @param part
	 * @param library
	 * @param folderName
	 */
	private static void distributePartToFolder(WTPart part, String library, String folderName) {
		WTContainer container = ContainerUtil.getContainer(library);
		Folder toFolder = ContainerUtil.getFolder(folderName, container);
		ContainerUtil.moveObj2Folder(part, toFolder);
	}

}
