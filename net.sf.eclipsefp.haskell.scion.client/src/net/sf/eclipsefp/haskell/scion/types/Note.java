package net.sf.eclipsefp.haskell.scion.types;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.json.JSONException;
import org.json.JSONObject;

public class Note {

	public enum Kind { ERROR, WARNING, INFO, OTHER };
	
	private Kind kind; // error, warning, info or other
	private Location location;
	private String message;
	private String additionalInfo;
	
	public Note(JSONObject json) throws JSONException {
		String kind = json.getString("kind");
		if (kind.equals("error"))
			this.kind = Kind.ERROR;
		else if (kind.equals("warning"))
			this.kind = Kind.WARNING;
		else if (kind.equals("info"))
			this.kind = Kind.INFO;
		else
			this.kind = Kind.OTHER;
		
		this.location = new Location(json.getJSONObject("location"));
		
		this.message = json.getString("message");
		
		this.additionalInfo = null;
	}
	
	public Note(Kind kind, Location location, String message, String additionalInfo) {
		this.kind = kind;
		this.location = location;
		this.message = message;
		this.additionalInfo = additionalInfo;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	
	public void applyAsMarker(IResource resource) throws CoreException {
		applyAsMarker(resource,Integer.MAX_VALUE);
	}
	
	public void applyAsMarker(IResource resource,int maxLines) throws CoreException {
		if (resource != null && resource.isAccessible()) {
			if (!resource.getWorkspace().isTreeLocked()){
				resource.refreshLocal(0, new NullProgressMonitor());
			}
			
	        int severity;
	        switch (kind) {
	          case ERROR: severity = IMarker.SEVERITY_ERROR; break;
	          case WARNING: severity = IMarker.SEVERITY_WARNING; break;
	          case INFO: severity = IMarker.SEVERITY_INFO; break;
	          default: severity = IMarker.SEVERITY_INFO; break;
	        }
	        int line= Math.min(location.getStartLine(),maxLines);
	     // we may not have any character to show
	        int start=location.getStartColumn();
	        int end=location.getEndColumn();
	        	        
	        String msg= message + (additionalInfo != null ? "\n" + additionalInfo : "");
	        
	        addMarker(resource, severity, line, start, end, msg);
		  
		}
	}

	private void addMarker(IResource resource, int severity, int line,
			int start, int end, String msg) throws CoreException {
		// duplicate
		for (IMarker m:resource.findMarkers(IMarker.PROBLEM, false, 0)){
			if (m.getAttribute(IMarker.SEVERITY, -1)==severity
					&&  m.getAttribute(IMarker.LINE_NUMBER, -1)==line
					&&  m.getAttribute(IMarker.CHAR_START, -1)==start
					&&  m.getAttribute(IMarker.MESSAGE, "").equals(msg)
					)
				return;
		}

		Map<Object,Object> attributes=new HashMap<Object,Object>();
		MarkerUtilities.setLineNumber(attributes, line);
		MarkerUtilities.setCharStart(attributes, start);
		// if we have startColumn==endColumn we could take end+1
		// BUT if end goes over the document size, the marker is not shown on the document
		// so it's better to just show the line without more info 
		if (end>start){
			// exclusive
			MarkerUtilities.setCharEnd(attributes, end-1);
		} 
		attributes.put(IMarker.SEVERITY, severity);
		attributes.put(IMarker.MESSAGE,msg);
		MarkerUtilities.createMarker(resource, attributes, IMarker.PROBLEM);
	}
	

	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Note)) {
			return false;
		}
		Note other = (Note)obj;
		return
			kind.equals(other.kind) &&
			location.equals(other.location) &&
			message.equals(other.message) &&
			((additionalInfo==null && other.additionalInfo==null) || (additionalInfo!=null && additionalInfo.equals(other.additionalInfo)));
	}
	
	@Override
	public int hashCode() {
		return kind.hashCode()+location.hashCode();
	}
	
	
	@Override
	public String toString() {
		return String.format("%s:%s: %s", kind.toString(), location.toString(), message);
	}
	
}
