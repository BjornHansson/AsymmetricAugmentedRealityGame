package models.sub;

import java.util.ArrayList;
import java.util.List;

public class Action {
	private String url;
	private String method;
	private List<Parameter> parameters = new ArrayList<Parameter>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void addParameter(Parameter param) {
		parameters.add(param);
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
}
