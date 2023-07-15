package com.yuqian.itax.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.CustomerWorker;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.orgs.service.OrgService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserRelaEntity;
import com.yuqian.itax.user.service.UserRelaService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.exception.NotLoginException;
import com.yuqian.itax.util.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseController
 * @Description 基础信息controller
 * @Author jiangni
 * @Date 2019/8/12
 * @Version 1.0
 */
@Slf4j
public class BaseController {

    @Autowired
    protected RedisService redisService;

    @Autowired
    protected OrgService orgService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRelaService userRelaService;

    @Autowired
    protected DictionaryService sysDictionaryService;
    /**
     * 获取httpservletrequest请求
     *
     * @return
     */
    protected HttpServletRequest getRequest() {
        RequestAttributes reqAttrs = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)reqAttrs;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取HttpServletResponse请求
     *
     * @return
     */
    protected HttpServletResponse getResponse() {
        RequestAttributes reqAttrs = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)reqAttrs;
        return servletRequestAttributes.getResponse();
    }

    /**
     * 从httpservlet中根据请求参数名称获取参数值
     *
     * @param paramName
     * @return
     */
    protected String getRequestParams(String paramName) {
        return getRequest().getParameter(paramName);
    }
    /**
     * 从httpservlet中根据请求参数名称获取参数值
     *
     * @param paramName
     * @return
     */
    protected String getRequestHeadParams(String paramName) {
        return getRequest().getHeader(paramName);
    }

    /**
     * 获取当前用户信息
     *
     * @author LiuXianTing
     * @return
     */
    protected CurrUser getCurrUser() {
        String token = getRequestHeadParams("token");
        String oemCode = getRequestHeadParams("oemCode");

        String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
        if(StringUtils.isBlank(currUserJson)) {
            throw new NotLoginException(ExceptionEnum.NO_LOGIN);
        }
        //重新设置redis的过期时间
        DictionaryEntity entity = sysDictionaryService.getByCode("redis_token_outtime");
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_"+ token,currUserJson,Integer.parseInt(entity.getDictValue()));
        return JSON.parseObject(currUserJson, CurrUser.class);
    }

    /**
     * 获取当前工号用户信息
     *
     * @author LiuXianTing
     * @return
     */

    protected CustomerWorker getCustomerWorker() {
        String token = getRequestHeadParams("workToken");
        String oemCode = getRequestHeadParams("oemCode");

        String customerWorkerJson = redisService.get(RedisKey.WORKER_LOGIN_TOKEN_KEY+"_"+oemCode+"_"+ token);
        if(StringUtils.isBlank(customerWorkerJson)) {
            throw new NotLoginException(ExceptionEnum.NO_WORK_LOGIN);
        }
        return JSON.parseObject(customerWorkerJson, CustomerWorker.class);
    }

    /**
     * 获取当前用户id
     *
     * @author jiangni
     * @param
     * @return
     */
    protected Long getCurrUserId() {
        return getCurrUser().getUserId();
    }

    /**
     * 获取当前用户账号
     *
     * @author jiangni
     * @param
     * @return
     */
    protected String getCurrUseraccount() {
        return getCurrUser().getUseraccount();
    }

    /**
     * 获取当前账户团队id
     * @return
     */
    protected Integer getCurrUserGroupId() {
        return getCurrUser().getGroupId();
    }
    /**
     * 允许未登录的调用
     * @param notLogin
     * @return
     */
    protected CurrUser getCurrUserGroupIdNotLogin(boolean notLogin) {
        if(notLogin) {
            try {
                return getCurrUser();
            } catch (NotLoginException e) {
                return null;
            }
        }
        return getCurrUser();
    }

    /**
     * excel导出
     * @param fileName
     * @param sheetName
     * @param Classes
     * @param dataSet
     * @throws Exception
     */
    public void exportExcel(String fileName, String sheetName, Class<?> Classes, Collection<?> dataSet) throws Exception {
        ExportParams exportParams = new ExportParams();
        exportParams.setSheetName(sheetName);
        fileName = fileName + DateUtil.formatDefaultDate(new Date());
        exportExcel(fileName, exportParams, Classes, dataSet);
    }

    public void exportExcel(String fileName, List<Map<String, Object>> list) throws Exception{
        Workbook workbook =ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        HttpServletResponse response = getResponse();
        response.setContentType("application/vnd.ms-excel");
        String codedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
        OutputStream output = response.getOutputStream();
        BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
        workbook.write(bufferedOutPut);
        bufferedOutPut.flush();
        bufferedOutPut.close();
        output.close();
    }
    /**
     * excel导出
     * @param fileName
     * @param entity
     * @param Classes
     * @param dataSet
     * @throws Exception
     */
    public void exportExcel(String fileName, ExportParams entity, Class<?> Classes, Collection<?> dataSet) throws Exception {
        Workbook workbook = ExcelExportUtil.exportExcel(entity, Classes, dataSet);
        HttpServletResponse response = getResponse();
        response.setContentType("application/vnd.ms-excel");
        String codedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
//        String agent = request.getHeader("USER-AGENT").toLowerCase();
//        if (agent.contains("firefox")) {
//            response.setCharacterEncoding("utf-8");
//            response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1") + ".xls" );
//        } else {
//            response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
//        }
        response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
        OutputStream output = response.getOutputStream();
        BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
        workbook.write(bufferedOutPut);
        bufferedOutPut.flush();
        bufferedOutPut.close();
        output.close();
    }

    /**
     * 格式化参数
     * @param data 请求的json串
     * @param paramName 请求json串中的参数key
     * @return
     * @throws BusinessException
     */
    protected String getParameter(String data, String paramName) {
        return getParameter(data, paramName, String.class);
    }


    /**
     * 格式化参数
     * @param data 请求的json串
     * @param paramName 请求json串中的参数key
     * @param classes paramName对应的类
     * @return
     * @throws BusinessException
     */
    protected <T> T getParameter(String data, String paramName, Class<T> classes) {
        if(data == null || StringUtils.isBlank(data)){
            return null;
        }
        try{
            JSONObject jsonObj = JSONObject.parseObject(data);
            T tmp = jsonObj.getObject(paramName, classes);
            if(tmp == null){
                return null;
            }
            return tmp;
        }catch(Exception ex){
            log.error("格式化请求参数异常：" + ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 获取用户组织树
     * @return
     */
    protected String getOrgTree() throws BusinessException {
        return getUserRelaEntity().getUserTree();
    }

    /**
     * 获取用户组织树
     * @return
     */
    protected UserRelaEntity getUserRelaEntity() throws BusinessException {
        //查询当前登陆账号所在组织
        OrgEntity orgEntity = orgService.queryOrgEntityByUserId(getCurrUserId());
        if (orgEntity == null) {
            throw new BusinessException("当前登录用户组织不存在");
        }
        //获取组织的管理员账号
        UserEntity userEntity = userService.getOrgAdminAccount(getCurrUserId());
        if (userEntity == null) {
            throw new BusinessException("管理员账号不存在");
        }
        //获取用户关系表
        Integer userClass = orgEntity.getOrgType();
        if(userClass == 2||userClass == 3){
            userClass = 2;
        }else if (userClass == 4) {
            userClass = 3;
        }else  if (userClass == 5) {
            userClass = 4;
        }
        UserRelaEntity userRelaEntity = userRelaService.queryUserRelaEntityByUserId(userEntity.getId(), userClass);
        if (userRelaEntity == null) {
            throw new BusinessException("当前登录用户关系不存在");
        }
        return userRelaEntity;
    }

    /**
     * 校验前端用户归属
     * @param userId 用户id
     * @param userTree 用户树
     * @param userClass 用户层级
     * @return true 前端小程序用户不归属于当前登录后台用户，false 属于
     */
    protected boolean belongAdmin(Long userId, String userTree, Integer userClass) {
        UserRelaEntity userRelaEntity = userRelaService.queryUserRelaEntity(userId, userTree, userClass);
        return null == userRelaEntity;
    }

    /**
     * 校验系统用户归属
     * @param userId 用户id
     * @param userTree 用户树
     * @return true 系统用户不归属于当前登录后台用户，false 属于
     */
    protected boolean belongSystem(Long userId, String userTree) {
        UserRelaEntity userRelaEntity = userRelaService.querySystemUserRelaEntity(userId, userTree);
        return null == userRelaEntity;
    }
}
