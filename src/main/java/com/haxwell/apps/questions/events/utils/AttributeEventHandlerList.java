package com.haxwell.apps.questions.events.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.haxwell.apps.questions.events.handlers.IAttributeEventHandler;

public class AttributeEventHandlerList {

	HashMap<String, List<AttributeEventHandlerBean>> /*attr to list of handlers*/ attrToRegisteredAEHLBeanMap = new HashMap<String, List<AttributeEventHandlerBean>>();
	HashMap<String, List<IAttributeEventHandler>> /*event to list of handlers*/ eventNameToActiveIEventHandlerMap = new HashMap<String, List<IAttributeEventHandler>>();

	Logger log = Logger.getLogger(AttributeEventHandlerList.class.getName());
	
	public AttributeEventHandlerList() { }

	public void setAttributeEventHandlerItem(AttributeEventHandlerBean item) {
		addItemToMap(attrToRegisteredAEHLBeanMap, item.attr, item);
		
		log.log(Level.FINER, "Added: " + item.toString());
	}
	
	public void setAttributeEventHandlerList(List<AttributeEventHandlerBean> list)
	{
		for (AttributeEventHandlerBean bean : list)
			setAttributeEventHandlerItem(bean);
	}

	public void activateHandlers(String attribute) {
		List<AttributeEventHandlerBean> aehlBeanlist = attrToRegisteredAEHLBeanMap.get(attribute);

		if (aehlBeanlist != null) {
			log.log(Level.FINER, "Activating '" + attribute + "'....");
			
			for (AttributeEventHandlerBean bean : aehlBeanlist) {
				List<IAttributeEventHandler> list = eventNameToActiveIEventHandlerMap.get(bean.event);
				boolean listChanged = false;
				
				if (list == null) {
					list = new ArrayList<IAttributeEventHandler>();
					listChanged = true;
					log.log(Level.FINER, "The event '"+ bean.event+"' has no active handlers.");
				}
				
				if (!list.contains(bean.handler)) {
					list.add(bean.handler);
					listChanged = true;
					log.log(Level.FINER, "Activated handler for the event (" + bean.event + ") and attr '" + attribute + "' (type: '" + bean.handler.getClass().toString() + "')");
				}
				else
					log.log(Level.FINER, "The list of handlers for the event " + bean.event + " already has a handler associated with the attribute '" + attribute + "' (" + bean.handler.toString() + ")");
					
				if (listChanged)
					eventNameToActiveIEventHandlerMap.put(bean.event, list);
			}
		}
		else {
			log.log(Level.FINER, "No beans registered for '" + attribute + "'. Nothing to activate!");
		}
	}
	
	public List<IAttributeEventHandler> getEventHandlerList(String event) {
		List<IAttributeEventHandler> rtn = eventNameToActiveIEventHandlerMap.get(event);
		
		// We null the list so that we only return handlers which have been registered since the last use,
		//  and we don't build up a list in between events.. When an event happens, all the active listeners are called,
		//  and then cleared.
		eventNameToActiveIEventHandlerMap.put(event, null);
		
		return rtn;
	}
	
	private void addItemToMap(HashMap<String, List<AttributeEventHandlerBean>> map, String key, AttributeEventHandlerBean item) {
		List<AttributeEventHandlerBean> list = map.get(key);
		
		if (list == null)
			list = new ArrayList<AttributeEventHandlerBean>();
		
		// TODO add some checking so duplicates aren't added.. 
		list.add(item);
		
		map.put(key, list);
	}
}
