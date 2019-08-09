package com.ljw.gateway.core.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <b>项目所属: </b>demo<br/>
 * <b>类 名：</b>DynamicRouteServiceImpl<br/>
 * <b>类描述：</b>动态路由实现<br/>
 * <b>创建人：</b>jiaweiluo<br/>
 * <b>创建时间：</b>2019/7/28<br/>
 * <b>修改人：</b>ljw<br/>
 * <b>修改时间：</b><br/>
 * <b>修改备注：</b><br/>
 *
 * @version 1.0<br />
 */
@Component
public class DynamicRouteResolver implements ApplicationEventPublisherAware {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    private void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * @return java.lang.String
     * @Author ljw
     * @Description 增加路由
     * @Date 2019/8/8
     * @Param [definitions]
     **/
    public String add(List<RouteDefinition> definitions) {
        for (RouteDefinition routeDefinition :
                definitions) {
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        }
        this.notifyChanged();
        return "success";
    }

    /**
     * @return java.lang.String
     * @Author ljw
     * @Description 更新路由
     * @Date 2019/8/8
     * @Param [definition]
     **/
    public String update(RouteDefinition definition) {
        //移除原有路由配置
        try {
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail, not find route routeId: " + definition.getId();
        }
        //保存新的路由配置
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.notifyChanged();
            return "success";
        } catch (Exception e) {
            return "update route fail";
        }
    }

    /**
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity < java.lang.Object>>
     * @Author ljw
     * @Description 删除路由
     * @Date 2019/8/8
     * @Param [id]
     **/
    public Mono<ResponseEntity<Object>> delete(String id) {
        return this.routeDefinitionWriter.delete(Mono.just(id))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok().build())))
                .onErrorResume(t -> t instanceof NotFoundException, t -> Mono.just(ResponseEntity.notFound().build()));
    }
}
