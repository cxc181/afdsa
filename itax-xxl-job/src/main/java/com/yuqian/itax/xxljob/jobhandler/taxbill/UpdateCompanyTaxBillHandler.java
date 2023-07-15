package com.yuqian.itax.xxljob.jobhandler.taxbill;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.CompanyTaxBillChangeService;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 *  @Author: LiuMenghao
 *  @Date: 2022/03/18
 *  @Description: 修改企业税单状态
 *  超过确认成本截止日税期内未开票企业税单状态变为申报中
 */
@Slf4j
@JobHandler(value="updateCompanyTaxBillHandler")
@Component
public class UpdateCompanyTaxBillHandler extends IJobHandler {

	@Autowired
	private CompanyTaxBillService companyTaxBillService;
	@Autowired
	private MemberCompanyService memberCompanyService;
	@Autowired
	private CompanyTaxBillChangeService companyTaxBillChangeService;


	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("修改企业税单状态调度任务启动");
		int type=0;
		if(StringUtil.isNotBlank(param)){
			JSONObject jsonObject=JSONObject.parseObject(param);
			type=jsonObject.getIntValue("type");
		}
		PendingTaxBillQuery query = new PendingTaxBillQuery();
		query.setStatusRange(1);
		int year = DateUtil.getYear(new Date());
		int quarter = Integer.parseInt(DateUtil.getQuarter());
		if (type == 0) {
			if (quarter == 1) {
				quarter = 4;
				year -= 1;
			} else {
				quarter -= 1;
			}
		}
		query.setTaxBillYear(year);
		query.setTaxBillSeasonal(quarter);
		List<PendingTaxBillVO> vos = companyTaxBillService.pendingTaxBill(query);
		vos = vos.stream().filter(x -> TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(x.getTaxBillStatus()) && Objects.equals(0L, x.getInvoiceMoney())).collect(Collectors.toList());
		if (CollectionUtil.isEmpty(vos)) {
			XxlJobLogger.log("没有需要修改企业税单状态的税单，调度任务结束");
			return SUCCESS;
		}
		Date date = new Date();
		for (PendingTaxBillVO vo : vos) {
			// 查询企业信息
			MemberCompanyEntity company = memberCompanyService.findById(vo.getCompanyId());
			if (null == company) {
				XxlJobLogger.log("未查询到企业信息，企业id为：" + vo.getCompanyId());
				continue;
			}
			// 已冻结企业不修改状态
			if (MemberCompanyStatusEnum.PROHIBIT.getValue().equals(company.getStatus())) {
				continue;
			}
			// 查询是否有历史未填报成本的税单
			query.setEin(company.getEin());
			query.setCompanyId(vo.getCompanyId());
			query.setRange(2);
			query.setStatusRange(1);
			List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(query);
			// 过滤掉上季度税单
			int finalYear = year;
			int finalQuarter = quarter;
			pendingTaxBillVOS = pendingTaxBillVOS.stream().filter(x -> x.getTaxBillYear() != finalYear || x.getTaxBillSeasonal() != finalQuarter).collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(pendingTaxBillVOS)) {
				continue;
			}
			// 税单自动变成“待申报”，签名取企业表
			// 查询企业税单
			CompanyTaxBillEntity companyTaxBill = companyTaxBillService.findById(vo.getCompanyTaxBillId());
			if (null == companyTaxBill) {
				XxlJobLogger.log("未查询到的企业税单id:" + vo.getCompanyTaxBillId());
				continue;
			}
			// 未开票但有应缴增值税或应缴附加税的税单不自动更新税单状态
			if (companyTaxBill.getVatShouldTaxMoney() > 0L || companyTaxBill.getAdditionalShouldTaxMoney() > 0L) {
				continue;
			}
			companyTaxBill.setTaxBillStatus(TaxBillStatusEnum.TO_BE_DECLARE.getValue());
			companyTaxBill.setSignImg(company.getSignImg());
			companyTaxBill.setUpdateTime(date);
			companyTaxBill.setUpdateUser("xxl-job");
			companyTaxBill.setQuarterCostAmount(0L);
			companyTaxBill.setQuarterIncomeAmount(0L);
			if (quarter == 1) {
				companyTaxBill.setYearCostAmount(0L);
				companyTaxBill.setYearIncomeAmount(0L);
			} else {
				// 查询上季度税单
				Example example = new Example(CompanyTaxBillEntity.class);
				example.createCriteria().andEqualTo("companyId", company.getId()).andEqualTo("taxBillYear", year)
						.andEqualTo("taxBillSeasonal", quarter - 1).andNotEqualTo("taxBillStatus", 9);
				List<CompanyTaxBillEntity> companyTaxBillEntities = companyTaxBillService.selectByExample(example);
				if (CollectionUtil.isNotEmpty(companyTaxBillEntities) && null != companyTaxBillEntities.get(0).getYearCostAmount()) {
					companyTaxBill.setYearCostAmount(companyTaxBillEntities.get(0).getYearCostAmount());
					companyTaxBill.setYearIncomeAmount(companyTaxBillEntities.get(0).getYearIncomeAmount());
				} else {
					companyTaxBill.setYearCostAmount(0L);
					companyTaxBill.setYearIncomeAmount(0L);
				}
			}
			companyTaxBillService.editByIdSelective(companyTaxBill);
			// 企业税单历史记录
			CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
			BeanUtils.copyProperties(companyTaxBill,companyTaxBillChangeEntity);
			companyTaxBillChangeEntity.setId(null);
			companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBill.getId());
			companyTaxBillChangeEntity.setDescrip("企业状态自动改为待申报");
			companyTaxBillChangeEntity.setAddTime(date);
			companyTaxBillChangeEntity.setAddUser("xxl-job");
			companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
		}

		XxlJobLogger.log("修改企业税单状态调度任务结束");
		return SUCCESS;
	}

}
