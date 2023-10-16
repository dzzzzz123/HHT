package ext.epm;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.FormatContentHolder;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Identified;
import wt.fc.PersistenceManagerEvent;
import wt.fc.PersistenceServerHelper;
import wt.inf.container.WTContainer;
import wt.method.MethodContext;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

/**
 * 监听EPMDocument的POST_STORE事件，更新CADName为number.CATPart或number.CATProd
 * 
 * @author samuel
 *
 */

public class EPMDocumentFileNameListener extends StandardManager
		implements EPMDocumentFileNameListenerIntf, Serializable {
	private static Properties CONFIG = new Properties();

	static {
		try {
			InputStream is = EPMDocumentFileNameListener.class.getResourceAsStream("/ext/epm/config.properties");
			CONFIG.load(is);
			is.close();
			System.out.println("Load change CAD Name config.properties :" + CONFIG);
		} catch (Exception e) {
			System.out.println("## WARNING: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7674613111164596422L;
	private static final String CLASSNAME = EPMDocumentFileNameListener.class.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public static EPMDocumentFileNameListener newEPMDocumentFileNameListener() throws WTException {
		EPMDocumentFileNameListener instance = new EPMDocumentFileNameListener();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() throws ManagerException {
		this.listener = new WCListenerEventListener(getConceptualClassname());
		getManagerService().addEventListener(this.listener,
				PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_STORE));
		// getManagerService().addEventListener(this.listener,
		// PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_MODIFY));
	}

	class WCListenerEventListener extends ServiceEventListenerAdapter {

		public WCListenerEventListener(String manager_name) {
			super(manager_name);
		}

		@SuppressWarnings("deprecation")
		public void notifyVetoableEvent(Object eve) throws WTException {
			if (!(eve instanceof KeyedEvent)) {
				return;
			}
			KeyedEvent event = (KeyedEvent) eve;
			Object target = event.getEventTarget();
			if (target instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) target;
				if (WorkInProgressHelper.isCheckedOut(epm)) {
					return;
				}
				Transaction transaction = new Transaction();
				try {
					System.out.println("001-Start rename EPMDocument:" + epm.getNumber());
					WTContainer container = epm.getContainer();
					if (!(container instanceof PDMLinkProduct)) {// 只有产品库的才刷新文件名
						System.out.println("001-The EPMDocument is not in container PDMLinkProduct,but "
								+ container.getContainerName() + ",so return.");
						return;
					}
					String version = epm.getVersionIdentifier().getValue();
					if (!StringUtils.equalsIgnoreCase(version, "A")) {
						System.out.println("001-The EPMDocument is revised to version :" + version + ",so return.");
						return;
					}
					EPMDocumentType docType = epm.getDocType();
					System.out.println("001-The DocType of EPMDocument:" + docType);
					String toolName = epm.getAuthoringApplication().toString();
					System.out.println("001-The Authoring Application(Tool) of EPMDocument:" + toolName);
					if (!("CADCOMPONENT".equalsIgnoreCase(docType.toString())
							|| "CADASSEMBLY".equalsIgnoreCase(docType.toString())
							|| "CADDRAWING".equalsIgnoreCase(docType.toString()))) {
						System.out.println(
								"001-The Type of EPMDocument is not CADCOMPONENT or CADASSEMBLY or CADDRAWING ,so return.");
						return;// 只有零件图或装配图才更新文件名称
					}
					System.out.println("001-Getting Application(CAD File) of EPMDocument...");
					ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) epm);
					ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
					ApplicationData applicationdata = (ApplicationData) contentitem;
					if (applicationdata != null) {
						String fileName = applicationdata.getFileName();
						System.out.println("001-EPMDocument old filename :" + fileName);
						String number = epm.getNumber();
						String extension = CONFIG.getProperty(toolName + "." + docType);
						if (StringUtils.isBlank(extension)) {
							System.out.println("001-extension of filename is not supported, so return.");
							return;// 如果编号和cadname一致则忽略
						}
						System.out.println("001-New filename extension:" + extension);
						if (number.equalsIgnoreCase(StringUtils.substringBefore(fileName, extension))) {
							System.out.println("001-New filename is the same of number+" + extension + ", so return.");
							return;// 如果编号和cadname一致则忽略
						}
						String newCADName = epm.getNumber() + extension;
						// if proe ,use lowercase
						if (toolName.equalsIgnoreCase("PROE")) {
							newCADName = newCADName.toLowerCase();
						}
//							EPMDocumentHelper.service.changeCADName((EPMDocumentMaster) epm.getMaster(), newCADName);
						if (!StringUtils.equalsIgnoreCase(fileName, "{$CAD_NAME}")) {
							System.out.println("001-Filename will be setted by new name:" + newCADName);
							applicationdata.setFileName(newCADName);
							PersistenceServerHelper.manager.update(applicationdata);// 更新文件名
						}
						Identified identified = (Identified) epm.getMaster();
						MethodContext methodContext = MethodContext.getContext();
						WTConnection connection = (WTConnection) methodContext.getConnection();
						PreparedStatement prepareStatement = connection
								.prepareStatement("update EPMDocumentMaster set CADName=? where ida2a2="
										+ identified.getPersistInfo().getObjectIdentifier().getId());
						prepareStatement.setString(1, newCADName);
						boolean execute = prepareStatement.execute();// 更新CADName
						transaction.commit();
						System.out.println("001-Filename has been setted by new name:" + newCADName + ",success!!!");
					} else {
						System.out.println("001-The Application of EPMDocument is null, so return.");
					}
				} catch (Exception e) {

					e.printStackTrace();
					System.out.println(
							"001-Error happends when rename EPMDocument:" + epm.getNumber() + ", so rollback.");
					transaction.rollback();
				}
			}

		}
	}

}