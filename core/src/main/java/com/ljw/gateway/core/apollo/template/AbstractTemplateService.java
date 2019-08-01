package com.ljw.gateway.core.apollo.template;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ITemplateService
 * @Description: 抽象模板方法
 * @Author: ljw
 * @Date: 2019/7/30 17:31
 **/
@Component
public abstract class AbstractTemplateService implements ITemplateService, ApplicationContextAware {

	public ApplicationContext applicationContext;

    @Override
    public void doTemplate(ConfigChangeEvent changeEvent) {
		// 注入监听
		doCheck(changeEvent);
		// 处理变化
		doChangeHandler(changeEvent);
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
