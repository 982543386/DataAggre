package org.ku8eye.ctrl;

import org.apache.log4j.Logger;
import org.ku8eye.service.WarService;
import org.ku8eye.util.AjaxReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WarlController {
	private Logger log = Logger.getLogger(this.toString());

	@Autowired
	private WarService WarService;
/*	@Autowired
	private YamlRegistry yamlRegistry;*/

	/**
	 * 新增war包
	 * 
	 * @param request 
	 * @return
	 */
	@RequestMapping(value = "/war/uploadwar")
	public AjaxReponse createWar(@RequestParam("warlocation") String warlocation){
            int res = WarService.insertContent(warlocation);
			
			if (res == -1)
			{
				log.error("warService Content failed to insert, SQL returned " + res);
				return new AjaxReponse(res, "CONTENT INSERT FAILED");
			}
			else
			{
				if(log.isInfoEnabled())
					log.info("Content inserted");
				return new AjaxReponse(res, "warService 上传成功");
			}
		
	}
	
}
