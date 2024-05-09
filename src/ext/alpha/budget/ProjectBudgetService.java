package ext.alpha.budget;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface ProjectBudgetService {
	ProjectBudget createSimpleExampleByName(final String name) throws WTException;
}
