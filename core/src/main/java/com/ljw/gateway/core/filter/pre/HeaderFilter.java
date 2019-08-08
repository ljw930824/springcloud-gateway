//package com.ljw.gateway.core.filter.pre;
//
//import com.alibaba.fastjson.JSONObject;
//import com.variflight.b.gateway.business.param.ParamAttribute;
//import com.variflight.b.gateway.business.param.ParamModuler;
//import com.variflight.b.gateway.common.Conf;
//import com.variflight.b.gateway.common.Constants;
//import com.variflight.b.gateway.entity.GatewayReq;
//import com.variflight.b.gateway.entity.GatewayRes;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.annotation.Order;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.regex.Matcher;
//
///**
// * @ClassName: HeaderFilter
// * @Description: 请求头参数校验
// * @Author: ljw
// * @Date: 2019/7/26 14:07
// **/
//@Component
//@Order(-100)
//public class HeaderFilter implements GlobalFilter {
//
//    private HashSet<ParamAttribute> headerParams = new HashSet<ParamAttribute>();
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String token = exchange.getRequest().getQueryParams().getFirst("authToken");
//        //返回401状态码和提示信息
//        if (StringUtils.isBlank(token)) {
//            ServerHttpResponse response = exchange.getResponse();
//            JSONObject message = new JSONObject();
//            message.put("status", -1);
//            message.put("data", "鉴权失败");
//            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bits);
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            //指定编码，否则在浏览器中会中文乱码
//            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
//            return response.writeWith(Mono.just(buffer));
//        }
//        return chain.filter(exchange);
//    }
//
//
//    @Override
//    public void refresh() throws Exception {
//        headerParams.clear();
//        String headerstr = Conf.getInstance().getHeaders();
//        if (headerstr != null) {
//            if (headerstr.length() > 0) {
//                String[] paramArray = headerstr.split(Constants.PARAM_SEQ);
//                for (String pm : paramArray) {
//                    int fno = pm.indexOf("{");
//
//                    ParamAttribute paramAttribute = new ParamAttribute();
//                    paramAttribute.setParamKey((fno > 0) ? pm.substring(0, fno) : pm);
//
//                    Matcher m = Constants.PARAM_PATTERN.matcher(pm);
//                    if (m.find()) {
//                        String paramstr = m.group(1).replace(",", "&");
//                        Map<String, String> attributeMap =
//                            ParamModuler.getParamsMap(paramstr, Constants.DEFAULT_ENCODEY);
//                        String length = attributeMap.get(Constants.PARAM_LENGTH_KEY);
//                        if (length != null) {
//                            if (length.length() > 0) {
//                                //TODO 校验类型
//                                paramAttribute.setLength(Integer.parseInt(length));
//                            }
//                        }
//
//                        String type = attributeMap.get(Constants.PARAM_TYPE_KEY);
//                        if (type != null) {
//                            if (type.length() > 0) {
//                                paramAttribute.setType(type);
//                            }
//                        }
//                    }
//
//                    headerParams.add(paramAttribute);
//                }
//            }
//        }
//    }
//
//    @Override
//    public GatewayRes run(GatewayReq gatewayReq, GatewayRes gatewayRes, Object... args) throws Exception {
//        if (!headerParams.isEmpty()) {
//            //遍历需要验证的参数
//            for (ParamAttribute attribute : headerParams) {
//                String headerVal = gatewayReq.getRequestHeaders().get(attribute.getParamKey());
//                //第一步：参数有无的校验
//                if (headerVal == null || headerVal.length() < 1) {
//                    gatewayRes = new GatewayRes();
//                    gatewayRes.fail(-1, String.format("缺少请求头参数'%s'", attribute.getParamKey()));
//                    // 参数不存在
//                    return gatewayRes;
//                } else {
//                    //第二步：需要校验长度
//                    if (attribute.getLength() > 0) {
//                        if (attribute.getLength() != headerVal.length()) {
//                            gatewayRes = new GatewayRes();
//                            gatewayRes.fail(-1,
//                                String.format("请求头参数'%s'的值长度必须为%s", attribute.getParamKey(), attribute.getLength()));
//                            // 长度不相等
//                            return gatewayRes;
//                        }
//                    }
//                    //第三步：需要校验类型
//                    if (!(attribute.getType() == null || attribute.getType().length() < 1)) {
//                        if (!ParamModuler.checkDataType(attribute.getType(), headerVal)) {
//                            gatewayRes = new GatewayRes();
//                            gatewayRes.fail(-1,
//                                String.format("请求头参数'%s'的值必须为'%s'类型", attribute.getParamKey(), attribute.getType()));
//                            //校验类型失败
//                            return gatewayRes;
//                        }
//                    }
//                }
//            }
//        }
//
//        return null;
//    }
//
//}
