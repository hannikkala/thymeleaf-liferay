/**
 * 
 */
package com.hannikkala.thymeleaf.liferay;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLFactoryUtil;

import static com.liferay.portal.kernel.portlet.LiferayPortletMode.*;
import static com.liferay.portal.kernel.portlet.LiferayWindowState.*;

/**
 * Util class to create Liferay portlet URLs.
 * 
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 */
public class LiferayURLUtil {

	protected final Logger log = LoggerFactory.getLogger(getClass());

    protected static final PortletMode[] PORTLET_MODES = {
        VIEW, EDIT, HELP, ABOUT, CONFIG, EDIT_DEFAULTS, EDIT_GUEST, PREVIEW, PRINT
    };
    protected static final WindowState[] WINDOW_STATES = {
        NORMAL, MAXIMIZED, MINIMIZED, EXCLUSIVE, POP_UP
    };
	
	private String prefix;
	
	/**
	 * Default constructor
	 * @param prefix Attribute prefix in Thymeleafed HTML.
	 */
	public LiferayURLUtil(String prefix) {
		this.prefix = prefix != null ? prefix + ":" : "";
	}
	
	public LiferayPortletURL createUrl(Map<String, Object> params, HttpServletRequest request) {
		long plid = getPlid(params.get("plid"), request);
		String portletName = getPortletName((String) params.get("portletname"), request);
		String lifecycle = getLifecycle((String) params.get("lifecycle"));
		
		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(request,
				portletName, plid, lifecycle);
		
		try {
			portletURL.setWindowState(getWindowState((String) params.get("windowState")));
		} catch (WindowStateException e) {
		}
		
		try {
			portletURL.setPortletMode(getPortletMode((String) params.get("portletMode")));
		} catch (PortletModeException e) {
		}

        if (params.containsKey("action")) {
            String value = params.get("action") != null ? params.get("action").toString() : "";
            portletURL.setParameter("javax.portlet.action", value, false);
            portletURL.setLifecycle(PortletRequest.ACTION_PHASE);
        }
		
		params.remove("plid");
		params.remove("portletname");
		params.remove("lifecycle");
		params.remove("windowState");
		params.remove("portletMode");
		params.remove("action");
		
		addParameters(params, portletURL);
		
		return portletURL;
	}
	
	protected WindowState getWindowState(String windowState) {
        if (windowState != null) {
            windowState = windowState.toLowerCase();

            for (WindowState state : WINDOW_STATES) {
                if (windowState.equals(state.toString())) {
                    return state;
                }
            }
        }
		return WindowState.NORMAL;
	}

	protected PortletMode getPortletMode(String portletMode) {
        if (portletMode != null) {
            portletMode = portletMode.toLowerCase();

            for (PortletMode mode : PORTLET_MODES) {
                if (portletMode.equals(mode.toString())) {
                    return mode;
                }
            }
        }
		return PortletMode.VIEW;
	}
	
	protected String getAttributeValue(Element element, String attributeName) {
		Attribute attribute = element.getAttributeFromNormalizedName(this.prefix + attributeName);
		if(attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	protected String getLifecycle(String lifecycle) {
		if (lifecycle != null) {
			return lifecycle;
		}
		return PortletRequest.RENDER_PHASE;
	}

	protected String getPortletName(String portletName, HttpServletRequest request) {
		if (portletName != null) {
			return portletName;
		}
		return (String) request.getAttribute(WebKeys.PORTLET_ID);
	}

	protected long getPlid(Object plid, HttpServletRequest request) {
		if (plid != null) {
			try {
				return Long.parseLong(plid.toString());
			} catch (NumberFormatException e) {
				log.error("Couldn't parse plid value '"
						+ plid
						+ "' to long. Returning default.");
			}
		}
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(com.liferay.portal.kernel.util.WebKeys.THEME_DISPLAY);
		Layout layout = themeDisplay.getLayout();
		return layout.getPlid();
	}
	
	protected void addParameters(Map<String, Object> params, LiferayPortletURL portletURL) {
		for(Entry<String, Object> entry : params.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue() != null ? entry.getValue().toString() : "";
			portletURL.setParameter(name, value, true);
		}
	}

	protected Map<String, Object> parseParams(Arguments arguments, String attributeValue) {
		final AssignationSequence assignations = 
			    StandardExpressionProcessor.parseAssignationSequence(
			            arguments, attributeValue, false /* no parameters without value */);
			if (assignations == null) {
			    throw new TemplateProcessingException(
			            "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
			}

			final Map<String,Object> newLocalVariables = new HashMap<String,Object>(assignations.size() + 1, 1.0f);
			for (final Assignation assignation : assignations) {
			    
			    final String varName = assignation.getLeft().getValue();
			    final Expression expression = assignation.getRight();
			    final Object varValue = StandardExpressionProcessor.executeExpression(arguments, expression);
			    
			    newLocalVariables.put(varName, varValue);
			    
			}
	    return newLocalVariables;  
	}

}
