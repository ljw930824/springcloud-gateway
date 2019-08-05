package com.ljw.gateway.business.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChange;

import java.util.Properties;
import java.util.Set;

/**
 * @ClassName: ApolloConfigurationChange
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/7/30 16:20
 **/
public class ApolloConfigurationChange {
    public static void monitorApolloConfigurationChange(Properties properties, Config config){
        config.addChangeListener(configChangeEvent -> {
            Set<String> keys = configChangeEvent.changedKeys();
            for(String key : keys){
                ConfigChange configChange = configChangeEvent.getChange(key);
                //覆盖旧值
//                PropertiesUtils.properties.setProperty(key,configChange.getNewValue());
                System.out.println(configChange.getPropertyName()+" 的值改变了，原值："+
                        configChange.getOldValue()+",新值："+configChange.getNewValue());
            }
        });
    }
}
