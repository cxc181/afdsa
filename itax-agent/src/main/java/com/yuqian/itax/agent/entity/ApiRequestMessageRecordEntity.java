package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * api接口请求报文记录
 * 
 * @Date: 2020年07月20日 17:44:57 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_api_request_message_record")
public class ApiRequestMessageRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 接口地址
	 */
	private String urlAddress;
	
	/**
	 * 版本号
	 */
	private String version;
	
	/**
	 * 请求参数
	 */
	private String requestParams;
	
	/**
	 * 签名内容
	 */
	private String signContent;
	
	/**
	 * 返回结果
	 */
	private String resultMsg;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	
	
}
