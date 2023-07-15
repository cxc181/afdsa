package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.entity.query.AccessPartyOrderQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.InvPayInfoVo;
import com.yuqian.itax.order.entity.vo.OrderVO;
import com.yuqian.itax.order.entity.vo.RegOrderVO;
import com.yuqian.itax.order.entity.vo.RegisterOrderOfAccessPartyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工商注册订单dao
 * 
 * @Date: 2019年12月07日 20:06:37 
 * @author 蒋匿
 */
@Mapper
public interface RegisterOrderMapper extends BaseMapper<RegisterOrderEntity> {

    /**
     * @Description 根据订单号查询订单
     * @param  orderNo
     * @Return RegisterOrderEntity
     */
    RegisterOrderEntity queryByOrderNo(String orderNo);

    /**
     * @Description 查询待通知的工商注册订单列表
     * @Author  Kaven
     * @Date   2019/12/9 15:57
     * @Param  userId
     * @Return List
     */
    List<OrderVO> queryNoticeRegOrderList(Long userId);

    /**
     * @Description 更新签名或视频地址
     * @Author  Kaven
     * @Date   2019/12/11 14:32
     * @Param  params
    */
    void updateSignOrVideoAddr(Map<String, Object> params);

    /**
     * @Description 企业注册订单查询-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 14:40
     * @Param   TZBOrderQuery
     * @Return  List
     * @Exception
     */
    List<RegOrderVO> queryRegistOrderList(TZBOrderQuery query);

    /**
     * @Description 根据ID更新订单数据（因字号更新的特殊性，不能使用editByIdSelective方法）
     * @Author  Kaven
     * @Date   2020/6/11 4:32 下午
     * @Param
     * @Return
     * @Exception
    */
    void updateOrderById(RegisterOrderEntity entity);

    /**
     * @Description 查询待身份验证的订单列表
     * @Author  yejian
     * @Date   2020/6/11 18:22
     * @Param   memberId
     * @Param   oemCode
     * @Return  List<RegisterOrderEntity>
     */
    List<RegisterOrderEntity> queryTobeAuthRegOrder(@Param("memberId")Long memberId, @Param("oemCode")String oemCode);

    /**
     * @Description 根据订单号更新微信授权标识
     * @Author  Kaven
     * @Date   2020/6/12 11:59 上午
     * @Param   RegisterOrderEntity
     * @Return
     * @Exception
    */
    void updateWechatAuthFlagByOrderNo(RegisterOrderEntity entity);

    InvPayInfoVo queryPayInfoByOrderNo(String orderNo);

    /**
     * 根据用户id查询注册订单列表
     * @param query
     * @return
     */
    List<RegisterOrderOfAccessPartyVO> listByMemberId(AccessPartyOrderQuery query);

    /**
     * 根据园区id获取最后一个使用的经营地址
     * @return
     */
    String getBusinessAddress(@Param("parkId") Long parkId);

    /**
     * 根据经营范围和园区id查询数据
     * @param content
     * @param parkId
     * @return
     */
    List<RegisterOrderEntity> getRegisterOrderByBusinessContent(@Param("content") String content,@Param("parkId") Long parkId);
}

