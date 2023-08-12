package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.*;
import cn.manpok.blogsystem.pojo.*;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IImageService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
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
    private IImageDao imageDao;

    @Autowired
    private IArticleAdminDao articleAdminDao;

    @Autowired
    private ICategoryDao categoryDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IThinkingDao thinkingDao;

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
    @Transactional
    public ResponseResult uploadImage(MultipartFile imageFile, String type, String oldID) {
        //判断文件是否存在
        if (imageFile == null) {
            return ResponseResult.FAIL("图片为空");
        }
        //限制图片类型为jpg、png、gif、jpeg
        String contentType = checkImageContentType(imageFile.getContentType());
        if (contentType == null) {
            return ResponseResult.FAIL(ResponseState.IMAGE_TYPE_NOT_SUPPORT);
        }
        //如果图片大于2M不能上传
        long size = imageFile.getSize();
        if (size > maxImageSize) {
            return ResponseResult.FAIL("图片大小超过2MB");
        }
        //删除旧的图片
        if (!TextUtil.isEmpty(oldID)) {
            deleteImage(oldID);
        }
        //把文件写到磁盘上
        //返回给前端的结果
        Map<String, String> result = new HashMap<>(2);
        //命名规则：基本路径+日期+图片类型+文件名，文件名用ID+后缀名
        //日期
        Date currentDate = new Date();
        String dateFormatStr = this.dateFormat.format(currentDate);
        //id
        String id = String.valueOf(snowflake.nextId());
        //最终文件路径、文件名
        String imageFilePath = imagePath + File.separator + dateFormatStr + File.separator + contentType;
        String fileName = id + "." + contentType;
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
        image.setUrl(dateFormatStr + File.separator + contentType + File.separator + fileName);
        image.setState(Constants.STATE_NORMAL);
        image.setCreateTime(currentDate);
        image.setUpdateTime(currentDate);
        image.setType(type);
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
        log.info("上传图片 ----> " + id);
        return ResponseResult.SUCCESS("图片上传成功").setData(result);
    }

    @Override
    public void getImage(String imageID) {
        //根据图片ID查询数据库记录
        BlogImage queryImage = imageDao.findImageById(imageID);
        if (queryImage == null) {
            handleImageNotFound();
            log.error("图片不存在 ----> " + imageID);
            return;
        }
        //获取图片相对路径
        String imageUrl = queryImage.getUrl();
        //根据主机存放路径，组成图片的全路径
        String imageFilePath = imagePath + File.separator + imageUrl;
        File file = new File(imageFilePath);
        if (!file.exists()) {
            handleImageNotFound();
            log.error("图片本地文件不存在 ----> " + imageID);
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
        return ResponseResult.FAIL(ResponseState.PERMISSION_DENIED);
    }

    @Override
    @Transactional
    public ResponseResult deleteImage(String imageID) {
        BlogImage queryImage = imageDao.findImageById(imageID);
        if (queryImage == null) {
            log.info("删除图片不存在 ----> " + imageID);
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

    @Override
    @Transactional
    @Async("asyncTaskServiceExecutor")
    @Scheduled(cron = "0 0 2 ? * 1")
    public void removeUnusedImages() {
        Set<String> imageSet = new HashSet<>();
        //找出文章中所有的图片
        List<BlogArticle> articleList = articleAdminDao.findAll();
        for (BlogArticle article : articleList) {
            Document document = Jsoup.parse(article.getContent());
            Elements imgs = document.getElementsByTag("img");
            for (Element img : imgs) {
                String src = img.attr("src");
                int index = src.lastIndexOf("/");
                String id = src.substring(index + 1);
                imageSet.add(id);
            }
            imageSet.add(article.getCover());
        }
        //找出分类中所有的图片
        List<BlogCategory> categoryList = categoryDao.findAll();
        for (BlogCategory category : categoryList) {
            imageSet.add(category.getCover());
        }
        //找出用户中所有的图片
        List<BlogUser> userList = userDao.findAll();
        for (BlogUser user : userList) {
            imageSet.add(user.getAvatar());
        }
        //找出想法中所有的图片
        List<BlogThinking> thinkingList = thinkingDao.findAll();
        for (BlogThinking thinking : thinkingList) {
            String thinkingImages = thinking.getImages();
            if (thinkingImages != null) {
                String[] images = thinking.getImages().split("-");
                imageSet.addAll(Arrays.asList(images));
            }
        }
        List<BlogImage> imageList = imageDao.findAll();
        for (BlogImage image : imageList) {
            if (!imageSet.contains(image.getId())) {
                image.setState(Constants.STATE_FORBIDDEN);
                log.info("标记文章图片为删除状态 ----> " + image.getId());
            }
        }
    }

    @Override
    @Transactional
    @Async("asyncTaskServiceExecutor")
    @Scheduled(cron = "0 0 4 ? * 1")
    public void deleteImagePhysically() {
        List<BlogImage> imageList = imageDao.findAllByState(Constants.STATE_FORBIDDEN);
        for (BlogImage image : imageList) {
            File file = new File(imagePath + File.separator + image.getUrl());
            if (file.exists()) {
                File parentFile = file.getParentFile();
                File grandParent = parentFile.getParentFile();
                boolean delete = file.delete();
                if (delete) {
                    log.info("删除本地图片成功 ----> " + image.getId());
                } else {
                    log.error("删除本地图片失败 ----> " + image.getId());
                }
                if (parentFile.list() == null || parentFile.list().length == 0) {
                    parentFile.delete();
                }
                if (grandParent.list() == null || grandParent.list().length == 0) {
                    grandParent.delete();
                }
            }
        }
        int count = imageDao.deleteAllByState(Constants.STATE_FORBIDDEN);
        if (count == 0) {
            log.info("没有本地图片需要清理");
        } else {
            log.info("已清除" + count + "张本地图片");
        }
    }

    @Override
    @Transactional
    public ResponseResult deleteImages(String[] imageIDs) {
        List<String> imageIDList = Arrays.asList(imageIDs);
        List<BlogImage> queryImages = imageDao.findAllById(imageIDList);
        for (BlogImage image : queryImages) {
            image.setState(Constants.STATE_FORBIDDEN);
            log.info("标记文章图片为删除状态 ----> " + image.getId());
        }
        return ResponseResult.SUCCESS("批量删除图片成功");
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

    private void handleImageNotFound() {
        response.setStatus(HttpStatus.NOT_FOUND.value());
    }
}
