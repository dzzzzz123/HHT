package ext.listener.service;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ext.listener.Config;
import ext.listener.Listener.ListenService;
import wt.doc.WTDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManagerEvent;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class StandardListenService extends StandardManager implements ListenService, Serializable {
	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = StandardListenService.class.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public StandardListenService() {
	}

	public static StandardListenService newStandardListenService() throws WTException {
		StandardListenService instance = new StandardListenService();
		instance.initialize();
		return instance;
	}

	@Override
	protected void performStartupProcess() throws ManagerException {
		this.listener = new WCListenerEventListener(getConceptualClassname());
		getManagerService().addEventListener(listener,
				PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_STORE));
	}

	class WCListenerEventListener extends ServiceEventListenerAdapter {
		public WCListenerEventListener(String manager_name) {
			super(manager_name);
		}

		@Override
		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent)) {
				return;
			}
			Object eventTarget = (Object) ((KeyedEvent) event).getEventTarget();
			System.out.println("Listen hears notifyVetoableEvent on:" + event.toString());
			if (eventTarget instanceof WTDocument) {
				WTDocument document = (WTDocument) eventTarget;
				System.out.println("document:" + document.getName());
				String partNumber = Config.getHHT_PartNumber(document);
				System.out.println("partNumber:" + partNumber);
				if (partNumber.isEmpty()) {
					return;
				} else {
					List<WTPart> part = findAllWTPartByNumber(partNumber);
					if (part.isEmpty()) {
						throw new WTException("部件编号填写错误，请编辑检出该文档，重写输入正确的部件编号!");
					} else {
						System.out.println("part:" + part.toString());
						for (WTPart wtPart : part) {
							System.out.println("view:" + wtPart.getViewName());
							if ((wtPart.getViewName()).equals("Design")) {
								WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(wtPart, document);
								System.out.println("link:" + link.toString());
								PersistenceServerHelper.manager.insert(link);
								PersistenceHelper.manager.refresh(link);
							}
						}
					}
				}
			}
			if (eventTarget instanceof WTPart) {
				WTPart part = (WTPart) eventTarget;
				System.out.println("part:" + part.getName());
				if (Config.getHHT_Classification(part).startsWith("5")) {
					System.out.println("part.getSource().toString():" + part.getSource().toString());
					if (part.getSource().toString().equals(Config.getBuy())) {
						System.out.println("part:" + part.getNumber());
						String result4 = process(part);
						System.out.println("---result4----" + result4);
						if (StringUtils.isNotBlank(result4)) {
							throw new WTException(result4);
						}
					}
				}
			}
		}
	}

	public String process(WTPart part) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			OrgContainer orgContainer = (OrgContainer) rf.getReference(Config.getORGID()).getObject();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(orgContainer);
			LifeCycleTemplate template = LifeCycleHelper.service.getLifeCycleTemplate(Config.getLFName(),
					wtContainerRef);
			System.out.println("template:" + template.getName());
			System.out.println("template:" + template.getLifeCycleTemplateReference());
			TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(Config.getPESType());
			System.out.println("tdr:" + tdr.toString());
			part.setTypeDefinitionReference(tdr);
			LifeCycleHelper.service.reassign(part, template.getLifeCycleTemplateReference());
			// PersistenceServerHelper.manager.update(part);
//			LifeCycleHelper.setLifeCycle(part, template.getLifeCycleTemplateReference());
		} catch (LifeCycleException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据编号或者名称查询部件
	 * 
	 * @param partNumber
	 * @return
	 */
	public static List<WTPart> findAllWTPartByNumber(String partNumber) {
		QueryResult qr = null;
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			QuerySpec querySpec = new QuerySpec(WTPart.class);
			if (StringUtils.isNotBlank(partNumber)) {
				WhereExpression where = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL,
						partNumber);
				querySpec.appendWhere(where);
				qr = PersistenceHelper.manager.find((StatementSpec) querySpec);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
					list.add(part);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
}
