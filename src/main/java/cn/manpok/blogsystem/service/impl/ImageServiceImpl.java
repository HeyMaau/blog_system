package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IImageDao;
import cn.manpok.blogsystem.pojo.BlogImage;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IImageService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ImageServiceImpl implements IImageService {

    @Value("${blog.system.image.dir-path}")
    private String imagePath;

    @Value("${blog.system.image.max-size}")
    private long maxImageSize;

    @Value("${blog.system.image.input-stream.buffer}")
    private int bufferSize;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IUserService userService;

    @Autowired
    private IImageDao imageDao;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${blog.system.image.multi-avatar-url}")
    private String multiAvatarUrl;

    @Value("${blog.system.image.multi-avatar-api-key}")
    private String multiAvatarApiKey;

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
                //引用次数+1

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
        image.setUrl(dateFormatStr + File.separator + type + File.separator + fileName);
        image.setState(Constants.STATE_NORMAL);
        image.setCreateTime(currentDate);
        image.setUpdateTime(currentDate);
        BlogUser user = userService.checkUserToken();
        image.setUserId(user.getId());
        image.setMD5(MD5);
        image.setRefCount(1);
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

    @Override
    public void getImage(String imageID) {
        //根据图片ID查询数据库记录
        BlogImage queryImage = imageDao.findImageById(imageID);
        if (queryImage == null) {
            log.error("图片不存在 ----> " + imageID);
            return;
        }
        //获取图片相对路径
        String imageUrl = queryImage.getUrl();
        //根据主机存放路径，组成图片的全路径
        String imageFilePath = imagePath + File.separator + imageUrl;
        File file = new File(imageFilePath);
        if (!file.exists()) {
            return;
        }
        //获取文件后缀名
        int index = imageUrl.lastIndexOf(".");
        String type = imageUrl.substring(index + 1);
        //根据后缀名设置contentType
        String contentType = getContentType(type);
        response.setContentType(contentType);
        //输出流写出
        try (FileInputStream inputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error("输出图片失败");
            e.printStackTrace();
        }
    }

    @Override
    public ResponseResult getImages(int page, int size) {
        //检查分页参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        //获取用户token
        BlogUser user = userService.checkUserToken();
        //构建分页
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.ASC, "createTime");
        //查询条件：1、属于当前用户；2、状态正常
        Page<BlogImage> queryImages = imageDao.findImagesByUserIdAndState(user.getId(), Constants.STATE_NORMAL, pageable);
        return ResponseResult.SUCCESS("获取所有图片成功").setData(queryImages);
    }

    @Override
    public ResponseResult deleteImage(String imageID) {
        BlogImage queryImage = imageDao.findImageById(imageID);
        if (queryImage == null) {
            return ResponseResult.FAIL("图片不存在");
        }
        queryImage.setState(Constants.STATE_FORBIDDEN);
        return ResponseResult.SUCCESS("删除图片成功");
    }

    @Override
    public void getCommentAvatar(String key) {
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(multiAvatarUrl, byte[].class, key, multiAvatarApiKey);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode.is2xxSuccessful()) {
            byte[] body = responseEntity.getBody();
            if (body != null) {
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    outputStream.write(body);
                } catch (Exception e) {
                    log.error("从MultiAvatar获取图片失败");
                    log.error(e.toString());
                }
            } else {
                log.info("MultiAvatar获取图片为空");
            }
        }
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

    /**
     * 获取图片类型对应的contentType
     *
     * @param type
     * @return
     */
    private String getContentType(String type) {
        return switch (type) {
            case Constants.Image.TYPE_JPG -> Constants.Image.TYPE_JPG_WITH_PREFIX;
            case Constants.Image.TYPE_GIF -> Constants.Image.TYPE_GIF_WITH_PREFIX;
            case Constants.Image.TYPE_PNG -> Constants.Image.TYPE_PNG_WITH_PREFIX;
            case Constants.Image.TYPE_JPEG -> Constants.Image.TYPE_JPEG_WITH_PREFIX;
            default -> null;
        };
    }
}
