package ext.ait.util;

import org.apache.logging.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

public class VersionUtil implements RemoteAccess {

	private static Logger LOGGER = LogR.getLogger(CommonUtil.class.getName());

	/**
	 * 获取对象的版本，如A.1
	 * 
	 * @param RevisionControlled
	 * @return String
	 */
	public static String getVersion(RevisionControlled revisionControlled) {
		return revisionControlled.getVersionInfo().getIdentifier().getValue() + "."
				+ revisionControlled.getIterationInfo().getIdentifier().getValue();
	}

	/**
	 * 校验对象是否为最新版
	 * 
	 * @param Iterated
	 * @return boolean
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static boolean isLatestIterated(Iterated interated) throws WTException {

		Iterated localIterated = null;
		boolean bool = false;
		LatestConfigSpec localLatestConfigSpec = new LatestConfigSpec();

		QueryResult localQueryResult = ConfigHelper.service.filteredIterationsOf(interated.getMaster(),
				localLatestConfigSpec);
		if ((localQueryResult != null) && (localQueryResult.size() <= 0)) {
			ConfigSpec localConfigSpec = ConfigHelper.service.getDefaultConfigSpecFor(WTPartMaster.class);
			localQueryResult = ConfigHelper.service.filteredIterationsOf(interated.getMaster(), localConfigSpec);
		}

		while ((localQueryResult.hasMoreElements()) && (!bool)) {
			localIterated = (Iterated) localQueryResult.nextElement();
			bool = localIterated.isLatestIteration();
		}
		LOGGER.debug("    the latest iteration=" + localIterated.getIdentity());
		if (localIterated.equals(interated)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取最新版本的对象
	 * 
	 * @param Mastered
	 * @return Iterated
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static Iterated getLatestInterated(Mastered master) throws WTException {

		Iterated localIterated = null;
		LatestConfigSpec localLatestConfigSpec = new LatestConfigSpec();

		QueryResult localQueryResult = ConfigHelper.service.filteredIterationsOf(master, localLatestConfigSpec);
		if ((localQueryResult != null) && (localQueryResult.size() <= 0)) {
			ConfigSpec localConfigSpec = ConfigHelper.service.getDefaultConfigSpecFor(WTPartMaster.class);
			localQueryResult = ConfigHelper.service.filteredIterationsOf(master, localConfigSpec);
		}

		while ((localQueryResult.hasMoreElements())) {
			Iterated localIterated1 = (Iterated) localQueryResult.nextElement();
			if (localIterated1.isLatestIteration()) {
				localIterated = localIterated1;
			}
		}
		return localIterated;
	}

	/**
	 * 获取最新版本的对象
	 * 
	 * @param Mastered
	 * @return RevisionControlled
	 * @throws WTException
	 */
	public static RevisionControlled getLatestObjectByMaster(Mastered master) throws WTException {
		if (master == null) {
			return null;
		} else {
			QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
			return queryResult.hasMoreElements() ? (RevisionControlled) queryResult.nextElement() : null;
		}
	}

	/**
	 * 获取上一个大版本的最新小版本，如果没有上一个大版本，则返回当前版本的最新小版本
	 * 
	 * @param RevisionControlled
	 * @return RevisionControlled
	 */
	public static RevisionControlled getLasterBigOne(RevisionControlled revisionControlled) {
		RevisionControlled last = null;
		try {
			if ("A".equals(revisionControlled.getVersionInfo().getIdentifier().getValue())) {
				return (RevisionControlled) getLatestInterated(revisionControlled.getMaster());
			}
			last = (RevisionControlled) VersionControlHelper.service.predecessorOf(revisionControlled);
			if (!last.getVersionInfo().getIdentifier().getValue()
					.equals(revisionControlled.getVersionInfo().getIdentifier().getValue())) {
				return last;
			} else {
				last = getLasterBigOne(last);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return last;
	}

	/**
	 * 获取上一个大版本的最新小版本，如果没有上一个大版本，则返回当前版本的最新小版本
	 * 
	 * @param RevisionControlled
	 * @return RevisionControlled
	 */
	public static RevisionControlled getLastBigOne(RevisionControlled revisionControlled) {
		RevisionControlled last = null;
		try {
			last = (RevisionControlled) VersionControlHelper.service.predecessorOf(revisionControlled);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return last;
	}
}
