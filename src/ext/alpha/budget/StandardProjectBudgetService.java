package ext.alpha.budget;

import wt.fc.PersistenceHelper;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class StandardProjectBudgetService extends StandardManager implements ProjectBudgetService {

	private static final long serialVersionUID = 1L;

	public static StandardProjectBudgetService newStandardProjectBudgetService() throws WTException {
		final StandardProjectBudgetService instance = new StandardProjectBudgetService();
		instance.initialize();
		return instance;
	}

	@Override
	public ProjectBudget createSimpleExampleByName(String name) throws WTException {
		final ProjectBudget projectBudget = ProjectBudget.newHProjectBudget();
		try {
			projectBudget.setName(name);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return (ProjectBudget) PersistenceHelper.manager.store(projectBudget);
	}
}
