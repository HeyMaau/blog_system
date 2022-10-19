package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IImageDao;
import cn.manpok.blogsystem.pojo.BlogImage;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IImageService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ImageServiceImpl implements IImageService {

    @Value("${blog.system.image.dir.path}")
    private String imagePath;

    @Value("${blog.system.image.max.size}")
    private long maxImageSize;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IUserService userService;

    @Autowired
    private IImageDao imageDao;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.Image.DATE_FORMAT);

    @Override
    public ResponseResult uploadImage(MultipartFile imageFile) {
        //判断文件是否存在
        if (imageFile == null) {
            return ResponseResult.FAIL("图片为空");
        }
        //限制图片类型为jpg、png、gif、jpeg
        String type = checkImageContentType(imageFile.getContentType());
        if (type == null) {
            return ResponseResult.FAIL(ResponseState.IMAGE_TYPE_NOT_SUPPORT);
        }
        //如果图片大于2M不能上传
        long size = imageFile.getSize();
        if (size > maxImageSize) {
            return ResponseResult.FAIL("图片大小超过2MB");
        }
        //把文件写到磁盘上
        //先查询MD5，如果有重复，直接返回结果
        String MD5 = null;
        try {
            MD5 = DigestUtils.md5DigestAsHex(imageFile.getBytes());
        } catch (IOException e) {
            log.error("图片MD5获取失败");
            e.printStackTrace();
        }
        //返回给前端的结果
        Map<String, String> result = new HashMap<>(2);
        if (MD5 != null) {
            BlogImage queryImageByMD5 = imageDao.findImageByMD5(MD5);
            if (queryImageByMD5 != null) {
                result.put("image_id", queryImageByMD5.getId());
                result.put("image_name", queryImageByMD5.getName());
                log.info("图片已存在，触发秒传 ----> " + queryImageByMD5.getId());
                return ResponseResult.SUCCESS("图片上传成功").setData(result);
            }
        }
        //命名规则：基本路径+日期+图片类型+文件名，文件名用ID+后缀名
        //日期
        Date currentDate = new Date();
        String dateFormatStr = this.dateFormat.format(currentDate);
        //id
        String id = String.valueOf(snowflake.nextId());
        //最终文件路径、文件名
        String imageFilePath = imagePath + File.separator + dateFormatStr + File.separator + type;
        String fileName = id + "." + type;
        File file = new File(imageFilePath, fileName);
        //创建目录
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        //写入数据库
        BlogImage image = new BlogImage();
        String originalFilename = imageFile.getOriginalFilename();
        image.setName(originalFilename);
        image.setId(id);
        image.setUrl(dateFormatStr + File.separator + type);
        image.setState(Constants.DEFAULT_STATE);
        image.setCreateTime(currentDate);
        image.setUpdateTime(currentDate);
        BlogUser user = userService.checkUserToken();
        image.setUserId(user.getId());
        image.setMD5(MD5);
        imageDao.save(image);
        //写入
        try {
            imageFile.transferTo(file);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.FAIL(ResponseState.IMAGE_UPLOAD_FAILED);
        }
        //返回数据给前端，ID、原始文件名
        result.put("image_id", id);
        result.put("image_name", originalFilename);
        return ResponseResult.SUCCESS("图片上传成功").setData(result);
    }

    /**
     * 检查图片类型
     *
     * @return
     */
    private String checkImageContentType(String contentType) {
        return switch (contentType) {
            case Constants.Image.TYPE_JPG_WITH_PREFIX -> Constants.Image.TYPE_JPG;
            case Constants.Image.TYPE_GIF_WITH_PREFIX -> Constants.Image.TYPE_GIF;
            case Constants.Image.TYPE_PNG_WITH_PREFIX -> Constants.Image.TYPE_PNG;
            case Constants.Image.TYPE_JPEG_WITH_PREFIX -> Constants.Image.TYPE_JPEG;
            default -> null;
        };
    }
}
