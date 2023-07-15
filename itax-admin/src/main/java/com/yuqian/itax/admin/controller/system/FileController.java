package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 文件Controller
 * @author：pengwei
 * @Date：2019/12/14 14:52
 * @version：1.0
 */
@Slf4j
@RestController
@RequestMapping("file")
public class FileController extends BaseController {

	@Autowired
	private DictionaryService dictionaryService;

	/**
	 * excel文件下载
	 * @param fileName
	 * @return
	 */
	@PostMapping("down/excel")
	public ResultVo downLoadFile(@JsonParam String fileName){
		String useraccount = getCurrUseraccount();
		DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
		if (dicEntity == null) {
			return ResultVo.Fail("字典数据未配置");
		}
		HttpServletResponse response = getResponse();
		response.setContentType("application/vnd.ms-excel");
		fileName = dicEntity.getDictValue() + "/" + useraccount + "/" + fileName;
		File file;
		try{
			file = new File(fileName);
			if (!file.exists()) {
				return ResultVo.Fail("文件不存在");
			}
			Workbook workbook = ExcelUtils.readExcel(fileName);
			response.setHeader("content-disposition", "attachment;filename=response.xls");
			OutputStream output = response.getOutputStream();
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
			workbook.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
			workbook.close();
			output.close();
		}catch (Exception e){
			log.error("文件下载异常:" + e.getMessage(), e);
			return ResultVo.Fail("文件下载失败");
		}

		if (file.isFile()){
			if (!file.delete()){
				throw new BusinessException("文件删除失败");
			}
		}
		return ResultVo.Success();
	}
}
