package ext.HHT.part.duplicateCheck;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.ContainerUtil;
import ext.ait.util.PropertiesUtil;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.part.WTPart;

public class FinishClassificationHelper {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static String process(WTPart part) {
		String result = "";
		if (part == null) {
			return result;
		}
		String source = part.getSource().toString();
		String classification = ClassificationUtil.getClassificationInternal(part,
				pUtil.getValueByKey("iba.internal.HHT_Classification"));
		if (StringUtils.isNotBlank(classification) && source.equals(pUtil.getValueByKey("source.buy"))
				&& classification.startsWith("5")) {
			pUtil.setValueByKey(part, "iba.internal.HHT_MaterialGroup", "6" + classification.substring(1));
			Set<String> folderSet = ContainerUtil.getContainerFolders(pUtil.getValueByKey("finish.library.name"));
			boolean flag = true;
			for (String folder : folderSet) {
				if (folder.startsWith(classification)) {
					folder = "6" + folder.substring(1);
					WTContainer container = ContainerUtil.getContainer(pUtil.getValueByKey("finish.library.name"));
					Folder toFolder = ContainerUtil.getFolder(folder, container);
					ContainerUtil.moveObj2Folder(part, toFolder);
					flag = false;
					break;
				}
			}
			if (flag) {
				String folderName = ClassificationUtil.getClassificationdDisPlayName(classification);
				folderName = "6" + folderName.substring(1);
				WTContainer container = ContainerUtil.getContainer(pUtil.getValueByKey("finish.library.name"));
				Folder toFolder = ContainerUtil.getFolder(folderName, container);
				ContainerUtil.moveObj2Folder(part, toFolder);
			}

		}
		return result;
	}

}
