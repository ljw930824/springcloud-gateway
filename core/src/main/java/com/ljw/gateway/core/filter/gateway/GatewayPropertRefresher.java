package com.ljw.gateway.core.filter.gateway;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <b>项目所属: </b>demo<br/>
 * <b>类 名：</b>GatewayPropertRefresher<br/>
 * <b>类描述：</b>apollo路由更改监听刷新<br/>
 * <b>创建人：</b>jiaweiluo<br/>
 * <b>创建时间：</b>2019/7/28<br/>
 * <b>修改人：</b><br/>
 * <b>修改时间：</b><br/>
 * <b>修改备注：</b><br/>
 *
 * @version 1.0<br   />
 */
public class GatewayPropertRefresher implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(GatewayPropertRefresher.class);

    private ApplicationContext applicationContext;

    private GatewayProperties gatewayProperties;
    private DynamicRouteServiceImpl dynamicRouteService;

    public GatewayPropertRefresher(DynamicRouteServiceImpl dynamicRouteService, GatewayProperties gatewayProperties) {
        this.dynamicRouteService = dynamicRouteService;
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 监听路由修改
     *
     * @param changeEvent
     */
    @ApolloConfigChangeListener(value = "TEST1.zuul-config")
    public void onChange(ConfigChangeEvent changeEvent) {
        boolean gatewayPropertiesChanged = false;
        for (String changedKey : changeEvent.changedKeys()) {
            if (changedKey.startsWith("spring.cloud.gateway")) {
                gatewayPropertiesChanged = true;
                break;
            }
        }
        if (gatewayPropertiesChanged) {
            GatewayPropertRefresher(changeEvent);
        }
    }

    /**
     * 刷新路由信息
     *
     * @param changeEvent
     */
    private void GatewayPropertRefresher(ConfigChangeEvent changeEvent) {
        logger.info("Refreshing gateway properties!");
        //更新配置
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        //更新路由
        gatewayProperties.getRoutes().forEach(dynamicRouteService::update);
        logger.info("gateway properties refreshed!");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
