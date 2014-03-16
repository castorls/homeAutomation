package smadja.homeAutomation.utils;

import java.util.ArrayList;
import java.util.List;

import smadja.homeAutomation.model.HomeElement;

public class PluginHelper {

	public static List<HomeElement> filterElementList(List<HomeElement> eltList, Class<?> clazz) {
		if (eltList == null || eltList.isEmpty()) {
			return new ArrayList<HomeElement>();
		}
		List<HomeElement> returnList = new ArrayList<HomeElement>();
		if (clazz == null) {
			returnList.addAll(eltList);
		} else {
			for (HomeElement elt : eltList) {
				if (clazz.isAssignableFrom(elt.getClass())) {
					returnList.add(elt);
				}
			}
		}
		return returnList;
	}

}
