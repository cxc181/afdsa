package com.yuqian.itax.admin.controller.system;

import com.google.common.collect.Maps;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.enums.OSSPrefixEnum;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/oss")
@Slf4j
public class OSSController extends BaseController {
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    OssService ossService;

    /**
     * OSS文件上传
     */
    @PostMapping("/uploadOss")
    //@OperatorLog(module="系统模块",operDes="OSS文件上传",oprType=4)
    @SuppressWarnings("all")
    public ResultVo upload( String fileBase64,  String fileName){
        String oemCode=getRequestHeadParams("oem_code");

        if(StringUtils.isBlank(fileBase64) || StringUtils.isBlank(fileName) || StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("参数不正确！");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd/");
        String filePath=sdf.format(new Date())+oemCode+"/";

        fileName=getCurrUserId()+"_"+fileName;
        //获取系统参数
        String bucketName=dictionaryService.getByCode("oss_bucket_name").getDictValue();

        boolean flag= ossService.upload( filePath+fileName, Base64.decodeBase64(fileBase64));
        Map<String,Object> map=new HashMap<>();
        if(flag){
            String url="http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+filePath+fileName;
            map.put("imgUrl",url);
        }else{
            return  ResultVo.Fail("上传图片OSS失败！");
        }
        return  ResultVo.Success(map);
    }

    /**
     * 图片上传私有域
     * @return
     */
    @PostMapping("upload/private")
    public ResultVo OSSUploadImage(HttpServletRequest request) {
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ArrayList<Map<String, Object>> results = Lists.newArrayList();
        Map<String, Object> map = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        long maxSize = Integer.valueOf(dictionaryService.getByCode("oss_maxFileSize").getDictValue()).longValue() * 1024L * 1024L;
        String originalFileName = null;
        while(fileNames.hasNext()) {
            try {
                map = Maps.newHashMap();
                MultipartFile file = multipartRequest.getFile(fileNames.next());
                String name = file.getName();
                originalFileName = file.getOriginalFilename();
                map.put("url", "");
                map.put("originalFileName", originalFileName);
                if (!StringUtils.endsWithIgnoreCase(originalFileName, ".JPG")
                        && !StringUtils.endsWithIgnoreCase(originalFileName, ".PNG")
                        && !StringUtils.endsWithIgnoreCase(originalFileName, ".JPEG")
                        && !StringUtils.endsWithIgnoreCase(originalFileName, ".PDF")) {
                    map.put("failReason", "文件格式上传有误，请上传图片或者pdf文件，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;
                }
                if (file.getSize() > maxSize) {
                    map.put("failReason", "文件大小超过10M，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;
                }
                name = UUID.randomUUID().toString();

//                if (name.lastIndexOf("/") != -1) {
//                    name = name.substring(name.lastIndexOf("/") + 1);
//                    if (StringUtils.isBlank(name)) {
//                        name = UUID.randomUUID().toString() + ".jpg";
//                    }
//                }
                if (StringUtils.endsWithIgnoreCase(originalFileName, ".PDF")) {
                    if (!name.toUpperCase().endsWith(".PDF")) {
                        name += ".pdf";
                    }
                } else {
                    if (!name.toUpperCase().endsWith(".JPG") || !name.toUpperCase().endsWith(".PNG")) {
                        name += ".jpg";
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String dir = sdf.format(new Date()) + "/";
                if (StringUtils.isNotBlank(userEntity.getOemCode())) {
                    dir = userEntity.getOemCode() + "/" + dir;
                } else {
                    dir = OSSPrefixEnum.SYSTEM_ADMIN.getValue() + "/" + dir;
                }
                name = dir + name;
                //oss上传
                ossService.upload(name, file.getBytes());
                map.put("failReason", "");
                map.put("fileName", name);
                map.put("url", ossService.getPrivateVideoUrl(name));
                results.add(map);
            } catch (Exception e) {
                map.put("failReason", "文件上传失败，上传的文件名为：" + originalFileName);
                results.add(map);
                continue;
            }
        }
        return ResultVo.Success(results);
    }

    /**
     * 文件上传私有域
     * @return
     */
    @PostMapping("upload/private/file")
    public ResultVo OSSUploadPublicFile(HttpServletRequest request) {
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ArrayList<Map<String, Object>> results = Lists.newArrayList();
        Map<String, Object> map = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        long maxSize = Integer.valueOf(dictionaryService.getByCode("oss_maxFileSize").getDictValue()).longValue() * 1024L * 1024L;
        String originalFileName = null;
        while(fileNames.hasNext()) {
            try {
                map = Maps.newHashMap();
                MultipartFile file = multipartRequest.getFile(fileNames.next());
                //String name = file.getName();
                String name =UUID.randomUUID().toString();
                originalFileName = file.getOriginalFilename();
                name=name+"."+originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                map.put("url", "");
                map.put("originalFileName", originalFileName);
                if (file.getSize() > maxSize) {
                    return ResultVo.Fail("文件大小超过10M，上传的文件名为：" + originalFileName);
                    /* map.put("failReason", "文件大小超过10M，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;*/
                }

                if (!originalFileName.toUpperCase().endsWith(".XLSX") &&!originalFileName.toUpperCase().endsWith(".XLS") &&!originalFileName.toUpperCase().endsWith(".ZIP") ) {
                    return ResultVo.Fail("文件格式上传有误，请上传xlsx、xls、ZIP的格式，上传的文件名为：" + originalFileName);
                    /* map.put("failReason", "文件格式上传有误，请上传xlsx、xls的格式，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;*/
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String dir = "file/"+sdf.format(new Date()) + "/";
                if (StringUtils.isNotBlank(userEntity.getOemCode())) {
                    dir = userEntity.getOemCode() + "/" + dir;
                } else {
                    dir = OSSPrefixEnum.SYSTEM_ADMIN.getValue() + "/" + dir;
                }
                name = dir + name;
                //oss上传
                ossService.upload(name, file.getBytes());
                map.put("failReason", "");
                map.put("fileName", name);
                map.put("url",ossService.getPrivateImgUrl(name));
                results.add(map);
            } catch (Exception e) {
               /* map.put("failReason", "文件上传失败，上传的文件名为：" + originalFileName);
                results.add(map);
                continue;*/
                return ResultVo.Fail("文件上传失败，上传的文件名为：" + originalFileName);
            }
        }
        return ResultVo.Success(results);
    }


    /**
     * 文件上传私有域
     * @return
     */
    @PostMapping("upload/private/file/all")
    public ResultVo OSSUploadPublicFileAll(HttpServletRequest request) {
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ArrayList<Map<String, Object>> results = Lists.newArrayList();
        Map<String, Object> map = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        String ossMaxFileSize = dictionaryService.getByCode("oss_maxFileSize").getDictValue();
        long maxSize = Integer.valueOf(ossMaxFileSize).longValue() * 1024L * 1024L;
        String originalFileName = null;
        while(fileNames.hasNext()) {
            try {
                map = Maps.newHashMap();
                MultipartFile file = multipartRequest.getFile(fileNames.next());
                //String name = file.getName();
                String name =UUID.randomUUID().toString();
                originalFileName = file.getOriginalFilename();
                name=name+"."+originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                map.put("url", "");
                map.put("originalFileName", originalFileName);
                if (file.getSize() > maxSize) {
                    return ResultVo.Fail("文件大小超过"+ossMaxFileSize+"M，上传的文件名为：" + originalFileName);
                    /* map.put("failReason", "文件大小超过10M，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;*/
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String dir = "file/"+sdf.format(new Date()) + "/";
                if (StringUtils.isNotBlank(userEntity.getOemCode())) {
                    dir = userEntity.getOemCode() + "/" + dir;
                } else {
                    dir = OSSPrefixEnum.SYSTEM_ADMIN.getValue() + "/" + dir;
                }
                name = dir + name;
                //oss上传
                ossService.upload(name, file.getBytes());
                map.put("failReason", "");
                map.put("fileName", name);
                map.put("url",ossService.getPrivateImgUrl(name));
                results.add(map);
            } catch (Exception e) {
               /* map.put("failReason", "文件上传失败，上传的文件名为：" + originalFileName);
                results.add(map);
                continue;*/
                return ResultVo.Fail("文件上传失败，上传的文件名为：" + originalFileName);
            }
        }
        return ResultVo.Success(results);
    }
    /**
     * 图片上传公有域
     * @return
     */
    @PostMapping("upload/public")
    public ResultVo OSSUploadPublic(HttpServletRequest request) {
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ArrayList<Map<String, Object>> results = Lists.newArrayList();
        Map<String, Object> map = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        long maxSize = Integer.valueOf(dictionaryService.getByCode("oss_maxFileSize").getDictValue()).longValue() * 1024L * 1024L;
        String originalFileName = null;
        String http = dictionaryService.getByCode("oss_req_head").getDictValue();
        String host = http + dictionaryService.getByCode("oss_access_public_host").getDictValue();
        while(fileNames.hasNext()) {
            try {
                map = Maps.newHashMap();
                MultipartFile file = multipartRequest.getFile(fileNames.next());
                //String name = file.getName();
                String name = UUID.randomUUID().toString();
                originalFileName = file.getOriginalFilename();
                map.put("url", "");
                map.put("originalFileName", originalFileName);
                if (!file.getContentType().contains("image") || (!StringUtils.endsWithIgnoreCase(originalFileName, ".JPG")
                        && !StringUtils.endsWithIgnoreCase(originalFileName, ".PNG")
                        && !StringUtils.endsWithIgnoreCase(originalFileName, ".JPEG"))) {
                    map.put("failReason", "文件格式上传有误，请上传png或者jpg的图片，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;
                }
                if (file.getSize() > maxSize) {
                    map.put("failReason", "文件大小超过10M，上传的文件名为：" + originalFileName);
                    results.add(map);
                    continue;
                }
                name = UUID.randomUUID().toString() + ".jpg";
//                if (StringUtils.isBlank(name)) {
//                }
//
//                if (name.lastIndexOf("/") != -1) {
//                    map.put("failReason", "name不能带/等特殊字符，上传的文件名为：" + originalFileName);
//                    results.add(map);
//                    name = name.substring(name.lastIndexOf("/") + 1);
//                    if (StringUtils.isBlank(name)) {
//                        name = UUID.randomUUID().toString() + ".jpg";
//                    }
//                }
//                if (!name.toUpperCase().endsWith(".JPG") || !name.toUpperCase().endsWith(".PNG")) {
//                    name += ".jpg";
//                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String dir = sdf.format(new Date()) + "/";
                if (StringUtils.isNotBlank(userEntity.getOemCode())) {
                    dir = userEntity.getOemCode() + "/" + dir;
                } else {
                    dir = OSSPrefixEnum.SYSTEM_ADMIN.getValue() + "/" + dir;
                }
                name = dir + name;
                //oss上传
                ossService.uploadPublic(name, file.getBytes());
                map.put("failReason", "");
                map.put("fileName", host + name);
                map.put("url", host + name);
                results.add(map);
            } catch (Exception e) {
                map.put("failReason", "文件上传失败，上传的文件名为：" + originalFileName);
                results.add(map);
                continue;
            }
        }
        return ResultVo.Success(results);
    }
}
