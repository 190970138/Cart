package main.java.controller;

import main.java.common.ExceptionEnum;
import main.java.common.HttpUtils;
import main.java.common.JsonUtil;
import main.java.model.Cart;
import main.java.service.ICartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Cart对外服务接口
 * Created by shike on 16/3/26.
 */
@Controller
@RequestMapping(value = "/cart")
public class CartController extends AbstractCtl {
    private static Logger logger = Logger.getLogger(CartController.class);
    @Resource
    private ICartService cartService;

    /**
     * 查询购物车
     * @param request
     * @param response
     */
    @RequestMapping(value = "/query")                                       //推荐使用一个注解：@ResponseBody，可以直接返回Object,不用每次都写成response.getOutputStream().write(resultStr.getBytes());
    public void getCart(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Cart result = new Cart();
        String resultStr = "";
        try {
            //获取请求的IP,记录日志
            String remoteIp = HttpUtils.getRemoteIp(request);
            logger.info("CartController.getCart() | remoteIp=" + remoteIp);     //这种普通的查询日志，和下面异常中的日志最好不要放一起
                                                                                //不然查询那些异常的特别慢
            //获取前端请求参数并校验
            Map<String, String> paramMap = HttpUtils.getParameterMap(request);
            logger.info("CartController.getCart() | paramMap=" + paramMap);
            if (paramMap == null || paramMap.size() == 0) {
                throw new IllegalArgumentException("query param is null");      //你这抛出异常有人处理么，你自己要处理controller层的异常
                                                                                //我习惯定义接口的返回数据如下：
                                                                                //retCode	整数	响应码	200：操作成功
                                                                                                            500：系统异常
                                                                                                            401：请求参数错误，请稍后重试	是
                                                                                //retDesc	字符串	响应描述		是
                                                                                //retData	Map	响应数据		

            }
            result = cartService.getCart(paramMap.get("cartId"));               //此处最好要把参数取出来，做检查，如判空、是否整型等
            resultStr = JsonUtil.toJson(result);
            logger.info("CartController.getCart() | result:" + resultStr);
            response.getOutputStream().write(resultStr.getBytes());
        } catch (Exception e) {
            logger.error("CartController.getCart() | Exception:" + e.getMessage());
        }
    }
}
