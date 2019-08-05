package com.ljw.gateway.business.apollo.template;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;

/**
 * @ClassName: ITemplateService
 * @Description: Apollo监听配置变化模板
 * @Author: ljw
 * @Date: 2019/7/30 17:31
 **/
public interface ITemplateService {
    /**
     * @Author ljw
     * @Description 操作类型
     * @Date 2019/7/30
     * @Param []
     * @return void
     **/
    void doCheck(ConfigChangeEvent changeEvent);

    /**
     * @Author ljw
     * @Description 监听具体配置变化后handle
     * @Date 2019/7/30
     * @Param []
     * @return void
     **/
    void doChangeHandler(ConfigChangeEvent changeEvent);

    /**
     * 模板方法
     *
     */
    void doTemplate(ConfigChangeEvent changeEvent);
}
