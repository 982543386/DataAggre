/**
 * 
 */
package org.ku8eye.service.dcos;

import java.util.List;

import org.ku8eye.domain.MonitorScheduleRule;
import org.ku8eye.mapping.MonitorScheduleRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sean 
 * 上午10:43:31
 */
@Service
public class MonitorScheduleRuleService {
	
	@Autowired
	private MonitorScheduleRuleMapper monitorScheduleRuleMapper;
	
	public MonitorScheduleRule getMonitorScheduleRuleById(int id) {
		return monitorScheduleRuleMapper.selectByPrimaryKey(id);
	}
	
	public void deleteMonitorScheduleRuleById(int id) {
		monitorScheduleRuleMapper.deleteByPrimaryKey(id);
	}
	
	public MonitorScheduleRule addMonitorScheduleRule(MonitorScheduleRule rule) {
		monitorScheduleRuleMapper.insert(rule);
		return rule;
	}
	
	public MonitorScheduleRule updateMonitorScheduleRule(MonitorScheduleRule rule) {
		monitorScheduleRuleMapper.updateByPrimaryKey(rule);
		return rule;
	}
	
	public List<MonitorScheduleRule> listMonitorScheduleRule() {
		return monitorScheduleRuleMapper.selectAll();
	}
	
}
