package admin.console.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.log4j.Logger;

public class ClassConverter implements Converter {
	private static Logger logger = Logger.getLogger(ClassConverter.class);

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String str) {
		if (str == null || "".equals(str.trim())) {
			return null;
		}
		try {
			return Class.forName(str);
		} catch (ClassNotFoundException e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent comp, Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Class) {
			return ((Class<?>) obj).getName();
		}
		return obj.toString();
	}

}
