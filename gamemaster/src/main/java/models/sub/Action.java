package models.sub;

import java.util.ArrayList;
import java.util.List;

public class Action {
	private String url;
	private String method;
	private List<String> parameters = new ArrayList<String>();

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

	public void addParameter(String param) {
		parameters.add(param);
	}

	public List<String> getParameters() {
		return parameters;
	}
}
