package smadja.homeAutomation.utils;

import java.util.HashSet;
import java.util.Set;

import smadja.homeAutomation.model.HomeElement;

public class PluginHelper {

	public static Set<HomeElement> filterElementSet(Set<HomeElement> eltSet, Class<?> clazz) {
		if (eltSet == null || eltSet.isEmpty()) {
			return new HashSet<HomeElement>();
		}
		Set<HomeElement> returnSet = new HashSet<HomeElement>();
		if (clazz == null) {
			returnSet.addAll(eltSet);
		} else {
			for (HomeElement elt : eltSet) {
				if (clazz.isAssignableFrom(elt.getClass())) {
					returnSet.add(elt);
				}
			}
		}
		return returnSet;
	}

}
