package sayservice;

import java.util.ArrayList;
import java.util.List;

public class UrbanTnAnnotaterModel {
	private List<String> ignoreServiceIdPattern = new ArrayList<>();
	private String outputPattern;
	private String funiviaRouteId;

	public List<String> getIgnoreServiceIdPattern() {
		return ignoreServiceIdPattern;
	}

	public void setIgnoreServiceIdPattern(List<String> ignoreServiceIdPattern) {
		this.ignoreServiceIdPattern = ignoreServiceIdPattern;
	}

	public String getOutputPattern() {
		return outputPattern;
	}

	public void setOutputPattern(String outputPattern) {
		this.outputPattern = outputPattern;
	}

	public String getFuniviaRouteId() {
		return funiviaRouteId;
	}

	public void setFuniviaRouteId(String funiviaRouteId) {
		this.funiviaRouteId = funiviaRouteId;
	}

	public UrbanTnAnnotaterModel(List<String> ignoreServiceIdPattern, String outputPattern, String funiviaRouteId) {
		super();
		this.ignoreServiceIdPattern = ignoreServiceIdPattern;
		this.outputPattern = outputPattern;
		this.funiviaRouteId = funiviaRouteId;
	}

	public UrbanTnAnnotaterModel(List<String> ignoreServiceIdPattern, String outputPattern) {
		super();
		this.ignoreServiceIdPattern = ignoreServiceIdPattern;
		this.outputPattern = outputPattern;
	}

	public UrbanTnAnnotaterModel() {
	}

}
