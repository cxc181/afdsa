package com.yuqian.itax.admin.shiro;

/**
 * @ClassName AuthcFilter
 * @Description
 * @Author jiangni
 * @Date 2019/9/5
 * @Version 1.0
 */

import com.alibaba.fastjson.JSONObject;

import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.exception.NotLoginException;
import com.yuqian.itax.util.util.SpringContextUtil;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Set;

/**
 * 重写用户filter
 * <p>
 * shiro 默认 {@link org.apache.shiro.web.filter.authc.UserFilter}
 *
 * @author seer
 * @date 2018/6/17 22:30
 */
public class AuthcFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
         HttpServletRequest httpRequest = WebUtils.toHttp(request);
         HttpServletResponse httpResponse = WebUtils.toHttp(response);
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setContentType("application/json; charset=UTF-8");
            httpResponse.setCharacterEncoding("utf-8");
            httpResponse.setHeader("Access-control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers",
                    "Origin,Accept,x-requested-with,Content-Type,X-AUTH-SESSION,Content-Security-Policy,X-Content-Type-Options,X-XSS-Protection,token,workToken,oemCode,sourceType");
            httpResponse.setHeader("Access-Control-Expose-Headers",
                    "X-AUTH-SESSION,Content-Security-Policy,X-Content-Type-Options,X-XSS-Protection");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Content-Security-Policy",
                    "script-src 'self'; object-src 'none'; style-src cdn.example.org third-party.org; child-src https:");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            return true;
        }
        String token = httpRequest.getHeader("token");
        try {
            //SysApiService sysApiService = (SysApiService) SpringContextUtil.getBean("sysApiService");
            //Set<String> permissionSets = sysApiService.findUserPermissionByToken(token);
           // String url = httpRequest.getServletPath();
           // if (permissionSets.contains(url)) {
            //    return true;
            //}
        }catch (Exception e){
            httpResponse.setContentType("application/json; charset=UTF-8");
            httpResponse.setCharacterEncoding("utf-8");
            OutputStream os = response.getOutputStream();
            ResultVo vo = null;
            try{
                if(e instanceof NotLoginException) {
                     vo = new ResultVo(ExceptionEnum.NO_LOGIN.getCode(),ExceptionEnum.NO_LOGIN.getMsg());
                }else {
                    vo = new ResultVo(ExceptionEnum.PERMISSIONS_ERROR.getCode(),ExceptionEnum.PERMISSIONS_ERROR.getMsg());
                }
                os.write(JSONObject.toJSONString(vo).getBytes());
            }catch (Exception ex){}
            finally {
                os.close();
            }
            throw new Exception(e);
        }
        return false;
    }

    /**
     * 不要做任何处理跳转，直接return，进行下一个filter
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }
}