package smadja.homeAutomation.plugins.ics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;

import org.apache.log4j.Logger;

public class ICSCache {

	private static Logger logger = Logger.getLogger(ICSCache.class);
	private static Map<String, List<VEvent>> eventMap = new HashMap<String, List<VEvent>>();

	public void clear(String cacheKey) {
		eventMap.remove(cacheKey);
	}

	@SuppressWarnings("unchecked")
	public synchronized void add(String cacheKey, DateTime start, DateTime end, VEvent event) {
		logger.debug(event.getSummary().getValue() + ": " + start.toString()+ ": " + end.toString()+ ": " + (event.getDescription() == null ? null : event.getDescription().getValue()));
		List<VEvent> eventList = eventMap.get(cacheKey);
		if(eventList == null){
			eventList = new ArrayList<VEvent>();
			eventMap.put(cacheKey, eventList);
		}
		VEvent cloneEvent = new VEvent();
		cloneEvent.getProperties().addAll(event.getProperties());
		cloneEvent.getProperties().add(new DtStart(new net.fortuna.ical4j.model.Date(start.getTime())));
		cloneEvent.getProperties().add(new DtEnd(new net.fortuna.ical4j.model.Date(end.getTime())));
		eventList.add(cloneEvent);
	}
	
	public synchronized List<VEvent> get(Date startDate, Date endDate, boolean isStart){
		List<VEvent> returnList = new ArrayList<VEvent>();
		DateRange askRange = new DateRange(startDate, endDate);
		for(List<VEvent> eventList : eventMap.values()){
			for(VEvent event : eventList){
				Date date = isStart?event.getStartDate().getDate():event.getEndDate().getDate();				
				if(askRange.includes(date, DateRange.INCLUSIVE_START) || askRange.includes(date,DateRange.INCLUSIVE_END)){
					returnList.add(event);
				}
			}
		}
		return returnList;
	}
}
