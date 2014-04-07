package admin.console.converters;

import java.io.File;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class FileConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String str) {
		if (str == null || "".equals(str.trim())) {
			return null;
		}
		return new File(str);
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent comp, Object obj) {
		if(obj == null){
			return null;
		}
		if( obj instanceof File){
			return ((File)obj).getAbsolutePath();
		}
		return null ;
	}

}
