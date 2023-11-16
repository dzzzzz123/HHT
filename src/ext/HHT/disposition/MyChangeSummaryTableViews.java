package ext.HHT.disposition;

import java.util.List;
import java.util.Locale;

import com.ptc.core.htmlcomp.createtableview.Attribute;
import com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute;
import com.ptc.windchill.enterprise.change2.tableViews.ChangeSummaryTableViews;

public class MyChangeSummaryTableViews extends ChangeSummaryTableViews {
	public List<?> getSpecialTableColumnsAttrDefinition(Locale locale) {
		List<Attribute.TextAttribute> results = (List<TextAttribute>) super.getSpecialTableColumnsAttrDefinition(
				locale);
		results.add(new Attribute.TextAttribute(MyDispositionHandler.MY_DISPOSITION_COMPID,
				"My Disposition" /* Should be localized label */, locale));
		return results;
	}
}