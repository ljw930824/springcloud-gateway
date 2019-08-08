//package com.ljw.gateway.core.filter.pre;
//
//import com.variflight.b.gateway.common.Conf;
//import com.variflight.b.gateway.entity.GatewayReq;
//import com.variflight.b.gateway.entity.GatewayRes;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//
///**
// * @ClassName: ParamPreFilter
// * @Description: 参数校验过滤器
// * @Author: ljw
// * @Date: 2019/7/26 14:07
// **/
//@Component
//@Order(-120)
//public class ParamPreFilter implements GlobalFilter {
//
//    private HashSet<String> params = new HashSet<String>();
//
//    @Override
//    public void init() throws Exception {
//        refresh();
//    }
//
//    @Override
//    public void refresh() throws Exception {
//        params.clear();
//
//        String paramsStr = Conf.getInstance().getParams();
//        if (paramsStr != null) {
//            if (paramsStr.length() > 0) {
//                String[] paramArray = paramsStr.split(";");
//                for (String pm : paramArray) {
//                    pm = (pm.indexOf("{")) > 0 ? pm.substring(0, pm.indexOf("{")) : pm;
//                    params.add(pm);
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean check(GatewayReq gatewayReq, GatewayRes gatewayRes, Object... args) throws Exception {
//        return true;
//    }
//
//    @Override
//    public GatewayRes run(GatewayReq gatewayReq, GatewayRes gatewayRes, Object... args) throws Exception {
//        if (!params.isEmpty()) {
//            for (String param : params) {
//                if (!gatewayReq.getRequestParams().containsKey(param)) {
//                    gatewayRes = new GatewayRes();
//                    gatewayRes.fail(-1, String.format("请求参数'%s'不能为空", param));
//                    return gatewayRes;
//                }
//            }
//        }
//
//        return null;
//    }
//}
