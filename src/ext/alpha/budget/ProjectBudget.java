package ext.alpha.budget;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.folder.CabinetReference;
import wt.folder.Foldered;
import wt.inf.container.WTContained;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, WTContained.class, Ownable.class,
		Foldered.class }, properties = { @GeneratedProperty(name = "project_budget", type = String.class),
				@GeneratedProperty(name = "buget_number", type = String.class, columnProperties = @ColumnProperties(index = true), constraints = @PropertyConstraints(required = true, upperLimit = 60)),
				@GeneratedProperty(name = "name", type = String.class),
				@GeneratedProperty(name = "buget_amount", type = String.class),
				@GeneratedProperty(name = "buget_category", type = String.class),
				@GeneratedProperty(name = "buget_subcategory", type = String.class),
				@GeneratedProperty(name = "buget_responsible", type = String.class),
				@GeneratedProperty(name = "state", type = String.class),
				@GeneratedProperty(name = "stage_of_project", type = String.class),
				@GeneratedProperty(name = "estimated_usage_time", type = String.class),
				@GeneratedProperty(name = "actual_usage_time", type = String.class) })
public class ProjectBudget extends _ProjectBudget {
	static final long serialVersionUID = 1;

	public static ProjectBudget newHProjectBudget() throws WTException {
		final ProjectBudget instance = new ProjectBudget();
		instance.initialize();
		return instance;
	}

	@Override
	public CabinetReference getCabinetReference() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFolderPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
}
