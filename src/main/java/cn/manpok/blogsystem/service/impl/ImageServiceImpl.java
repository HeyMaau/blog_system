package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.*;
import cn.manpok.blogsystem.pojo.*;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IImageService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
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

    @Value("${blog.system.image.dir-path-nginx}")
    private String imagePath4Nginx;

    @Value("${blog.system.image.redirect-base-url}")
    private String imageRedirectBaseUrl;

    @Value("${blog.system.image.watermark.font-size}")
    private int watermarkFontSize;

    @Value("${blog.system.image.watermark.text}")
    private String watermarkText;

    @Value("${blog.system.image.watermark.originFilePath}")
    private String originWatermarkPath;

    @Value("${blog.system.image.watermark.tempFilePath}")
    private String tempWaterMarkPath;

    @Value("${blog.system.image.watermark.tempWatermarkFileName}")
    private String tempWatermarkFileName;

    @Value("${blog.system.image.watermark.tempPNGFileName}")
    private String tempPNGFileName;

    @Value("${blog.system.image.max-width-height}")
    private int maxWithHeight;

    @Value("${blog.system.image.webp.shell-name}")
    private String shellName;

    @Value("${blog.system.image.webp.shell-param}")
    private String shellParam;

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
    public ResponseResult uploadImage(MultipartFile imageFile) {
        //判断文件是否存在
        if (imageFile == null) {
            return ResponseResult.FAIL("图片为空");
        }
        //限制图片类型为jpg、png、gif、jpeg、webp
        String contentType = checkImageContentType(imageFile.getContentType());
        if (contentType == null) {
            return ResponseResult.FAIL(ResponseState.IMAGE_TYPE_NOT_SUPPORT);
        }
        //如果图片大于6M不能上传
        long size = imageFile.getSize();
        if (size > maxImageSize) {
            return ResponseResult.FAIL("图片大小超过6MB");
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
        File file4Nginx = new File(imagePath4Nginx, fileName);
        //创建目录
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file4Nginx.getParentFile().exists()) {
            file4Nginx.getParentFile().mkdirs();
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
        imageDao.save(image);
        //写入
        try {
            imageFile.transferTo(file);
            FileUtils.copyFile(file, file4Nginx);
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
    public ResponseResult uploadImageWithWatermark(MultipartFile imageFile) {
        //判断文件是否存在
        if (imageFile == null) {
            return ResponseResult.FAIL("图片为空");
        }
        //限制图片类型为jpg、png、gif、jpeg
        String contentType = checkImageContentType(imageFile.getContentType());
        if (contentType == null) {
            return ResponseResult.FAIL(ResponseState.IMAGE_TYPE_NOT_SUPPORT);
        }
        //如果图片大于6M不能上传
        long size = imageFile.getSize();
        if (size > maxImageSize) {
            return ResponseResult.FAIL("图片大小超过6MB");
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
        contentType = "webp";
        String imageFilePath = imagePath + File.separator + dateFormatStr + File.separator + contentType;
        String fileName = id + "." + contentType;
        File file = new File(imageFilePath, fileName);
        File file4Nginx = new File(imagePath4Nginx, fileName);
        //创建目录
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file4Nginx.getParentFile().exists()) {
            file4Nginx.getParentFile().mkdirs();
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
        imageDao.save(image);
        //写入
        try {
            addWaterMark(imageFile.getInputStream(), file, id);
            FileUtils.copyFile(file, file4Nginx);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.FAIL(ResponseState.IMAGE_UPLOAD_FAILED);
        }
        //返回数据给前端，ID、原始文件名
        result.put("image_id", id);
        result.put("image_name", originalFilename);
        log.info("上传加水印图片 ----> " + id);
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
        String url = queryImage.getUrl();
        String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
        try {
            response.sendRedirect(imageRedirectBaseUrl + fileName);
        } catch (IOException e) {
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
        HttpStatusCode statusCode = responseEntity.getStatusCode();
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

            //删除为nginx准备的文件夹下的图片
            String url = image.getUrl();
            String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
            File file4Nginx = new File(imagePath4Nginx, fileName);
            if (file4Nginx.exists()) {
                boolean delete = file4Nginx.delete();
                if (delete) {
                    log.info("删除Nginx本地图片成功 ----> " + image.getId());
                } else {
                    log.error("删除Nginx本地图片失败 ----> " + image.getId());
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
            case Constants.Image.TYPE_WEBP_WITH_PREFIX -> Constants.Image.TYPE_WEBP;
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

    /**
     * 添加文字水印
     *
     * @param inputStream
     * @param format
     * @param textColor
     * @param fontSize
     * @param text
     * @param destFile
     */
    private void addTextWaterMark(InputStream inputStream, String format, Color textColor, int fontSize, String text, int style, File destFile) {
        try {
            BufferedImage targetImg = ImageIO.read(inputStream);
            int width = targetImg.getWidth(); //图片宽
            int height = targetImg.getHeight(); //图片高
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(targetImg, 0, 0, width, height, null);
            g.setColor(textColor); //水印颜色
            g.setFont(new Font("微软雅黑", style, fontSize));
            // 水印内容放置在右下角
            int x = width - (text.length() + 1) * fontSize;
            int y = height - fontSize * 2;
            g.drawString(text, x, y);
            FileOutputStream outImgStream = new FileOutputStream(destFile);
            ImageIO.write(bufferedImage, format, outImgStream);
            outImgStream.flush();
            outImgStream.close();
            g.dispose();
        } catch (Exception e) {
            log.error("上传图片生成文字水印失败");
        }
    }

    /**
     * 添加图片水印
     *
     * @param inputStream
     * @param destFile
     * @param imageID
     */
    private void addWaterMark(InputStream inputStream, File destFile, String imageID) {
        try {
            File watermarkFile = new File(originWatermarkPath);
            BufferedImage targetImg = ImageIO.read(inputStream);
            File tempWatermarkFile = new File(tempWaterMarkPath, tempWatermarkFileName + imageID + ".png");
            File tempPNGFile = new File(tempWaterMarkPath, tempPNGFileName + imageID + ".png");
            int scale = (int) Math.ceil((double) Math.max(targetImg.getHeight(), targetImg.getWidth()) / maxWithHeight);
            Thumbnails.of(watermarkFile).width(targetImg.getWidth() / 4 / scale).outputQuality(1f).toFile(tempWatermarkFile);
            BufferedImage waterImg = ImageIO.read(tempWatermarkFile);
            Thumbnails.of(targetImg)
                    .size(maxWithHeight, maxWithHeight) // 大小
                    .watermark(Positions.BOTTOM_RIGHT, waterImg, 1f)  // 0.5f表示透明度，最大值为1
                    .outputQuality(1f)   // 图片质量，最大值为1
                    .outputFormat("png")
                    .toFile(tempPNGFile);
            List<String> command = new ArrayList<>();
            command.add(shellName);
            command.add(shellParam);
            String cwebpCommand = "cwebp -q 75 " + tempPNGFile.getAbsolutePath() + " -o " + destFile.getAbsolutePath();
            command.add(cwebpCommand);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            log.info("转换webp图像的exitCode: " + exitCode);
            tempWatermarkFile.delete();
            tempPNGFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传图片生成图片水印失败");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("上传图片生成webp图片失败");
        }
    }

}
