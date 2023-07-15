package com.yuqian.itax.admin.controller.user;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.po.CustomerServiceWorkPO;
import com.yuqian.itax.user.entity.query.CustomerServiceWorkQuery;
import com.yuqian.itax.user.entity.vo.CustomerServiceWorkVO;
import com.yuqian.itax.user.service.CustomerServiceWorkNumberService;
import com.yuqian.itax.util.util.Md5Util;
import com.yuqian.itax.util.util.MemberPsdUtil;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.service.WorkOrderService;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/customer")
public class CustomerServiceWorkController extends BaseController {

    @Autowired
    private CustomerServiceWorkNumberService customerServiceWorkNumberService;

    @Autowired
    private WorkOrderService workOrderService;

    /***
     * 工号列表（分页）
     * @author  HZ
     * @Date 2019/12/21
     */
    @PostMapping("/customerServiceWorkPageInfo")
    public ResultVo customerServiceWorkPageInfo(@RequestBody CustomerServiceWorkQuery customerServiceWorkQuery){
        //带登陆验证
        getCurrUser();
        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            customerServiceWorkQuery.setOemCode(oemCode);
        }
        PageInfo<CustomerServiceWorkVO> userPageInfo=customerServiceWorkNumberService.customerServiceWorkPageInfo(customerServiceWorkQuery);

        return ResultVo.Success(userPageInfo);
    }

    /***
     * 新增工号
     * @author  HZ
     * @Date 2019/12/21
     */
    @PostMapping("/addCustomerServiceWork")
    public ResultVo addCustomerServiceWork(@RequestBody CustomerServiceWorkPO customerServiceWorkPO){
        //带登陆验证
        getCurrUser();
        UserEntity userEntity = userService.findById(customerServiceWorkPO.getUserId());
       /* if (StringUtils.isNotBlank(userEntity.getOemCode())) {
            return ResultVo.Fail("当前坐席不属于平台");
        }*/
        if (!Objects.equals(userEntity.getAccountType(), 2)) {
            return ResultVo.Fail("请选择正确的坐席客服");
        }
        Example example = new Example(CustomerServiceWorkNumberEntity.class);
        example.createCriteria().andEqualTo("userId", customerServiceWorkPO.getUserId())
                .andEqualTo("workNumber", customerServiceWorkPO.getWorkNumber())
                .andNotEqualTo("status", 2);// 非注销状态
        List<CustomerServiceWorkNumberEntity> list = customerServiceWorkNumberService.selectByExample(example);
        if (CollectionUtil.isNotEmpty(list)) {
            return ResultVo.Fail("当前选择坐席已有相同的客服工号");
        }
      /*  if(userEntity.getAccountType()!=2){
            return ResultVo.Fail("您不是客服坐席，不能新增工号。");
        }*/
        CustomerServiceWorkNumberEntity customerServiceWorkNumberEntity=customerServiceWorkNumberService.addCustomerServiceWorkNumberEntity(customerServiceWorkPO,getCurrUserId());

        return ResultVo.Success();
    }
    /***
     * 工号状态变更
     * @author  HZ
     * @Date 2019/12/21
     */
    @PostMapping("/updateCustomerServiceWorkStatus")
    public ResultVo updateCustomerServiceWorkStatus(@JsonParam Long id,@JsonParam Integer status){
        //带登陆验证
        getCurrUser();
        if(null == id || null == status){
            return  ResultVo.Fail("参数不正确，非法请求!");
        }
        CustomerServiceWorkNumberEntity customerServiceWorkNumberEntity=customerServiceWorkNumberService.findById(id);
       if(null == customerServiceWorkNumberEntity){
           return  ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
       }

       if(customerServiceWorkNumberEntity.getStatus()==2){
           return  ResultVo.Fail("用户已经注销不允许修改状态。");
       }

        customerServiceWorkNumberEntity.setStatus(status);
        customerServiceWorkNumberService.editByIdSelective(customerServiceWorkNumberEntity);
        return ResultVo.Success();
    }

    /***
     * 工号密码重置
     * @author  HZ
     * @Date 2019/12/21
     */
    @PostMapping("/resetCustomerServiceWork")
    public ResultVo resetCustomerServiceWork(@JsonParam Long id){
        //带登陆验证
        getCurrUser();
        if(null == id){
            return  ResultVo.Fail("参数不正确，非法请求!");
        }
        CustomerServiceWorkNumberEntity customerServiceWorkNumberEntity=customerServiceWorkNumberService.findById(id);
        String pwd= Md5Util.MD5("123456").toLowerCase();
        pwd= MemberPsdUtil.encrypt(pwd,customerServiceWorkNumberEntity.getWorkNumber(),customerServiceWorkNumberEntity.getSlat());
        customerServiceWorkNumberEntity.setWorkNumberPwd(pwd);
        customerServiceWorkNumberService.editByIdSelective(customerServiceWorkNumberEntity);
        return ResultVo.Success();
    }

    @Resource
    OemService oemService;
    /**
     * 客服坐席列表
     * @author  HZ
     * @Date 2019/12/21
     */
    @PostMapping("queryCustomerServiceByOemCode")
    public ResultVo queryCustomerServiceByOemCode(@JsonParam String workOrderNo,@JsonParam String oemCode){
        //带登陆验证
        getCurrUser();
        Long customerServiceId=null;
        if(StringUtils.isNotBlank(workOrderNo)){
            if(StringUtils.isBlank(workOrderNo)){
                return ResultVo.Fail("请选择需要转派的工单");
            }
            //获取需要过滤的坐席客服账号
            WorkOrderEntity workOrderEntity = new WorkOrderEntity();
            workOrderEntity.setWorkOrderNo(workOrderNo);
            WorkOrderEntity entity= workOrderService.selectOne(workOrderEntity);
            if(entity == null ){
                return ResultVo.Fail("工单不存在!");
            }
            oemCode=userService.findById(getCurrUserId()).getOemCode();
            OemEntity oemEntity=oemService.getOem(entity.getOemCode());
            Integer workAuditWay=oemEntity.getWorkAuditWay();
            if(workAuditWay==1 && oemCode!=null){
                return ResultVo.Success();
            }
            if(workAuditWay==2 &&oemCode==null){
                return ResultVo.Success();
            }
            customerServiceId=entity.getCustomerServiceId();
        }


        List<CustomerServiceWorkVO> customerServiceWorkVOS= userService.getCustomerServiceByOemCode(oemCode,customerServiceId);
        return ResultVo.Success(customerServiceWorkVOS);
    }
}
