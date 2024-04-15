package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IAudioDao;
import cn.manpok.blogsystem.pojo.BlogAudio;
import cn.manpok.blogsystem.pojo.BlogPaging;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAudioService;
import cn.manpok.blogsystem.utils.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AudioServiceImpl implements IAudioService {

    @Autowired
    private IAudioDao audioDao;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${blog.system.audio.temp-dir-path}")
    private String tempAudioFilePath;

    @Override
    public ResponseResult addAudio(BlogAudio blogAudio) {
        if (!checkAudioParams(blogAudio)) {
            return ResponseResult.FAIL("音频参数有误");
        }
        blogAudio.setId(String.valueOf(snowflake.nextId()));
        Date date = new Date();
        blogAudio.setCreateTime(date);
        blogAudio.setUpdateTime(date);
        audioDao.save(blogAudio);
        redisUtil.del(Constants.Audio.KEY_AUDIO_LIST_CACHE);
        return ResponseResult.SUCCESS("添加音频成功");
    }

    @Transactional
    @Override
    public ResponseResult updateAudio(BlogAudio blogAudio) {
        if (!checkAudioParams(blogAudio)) {
            return ResponseResult.FAIL("音频参数有误");
        }
        BlogAudio queryBlogAudio = audioDao.findBlogAudioById(blogAudio.getId());
        if (queryBlogAudio == null) {
            return ResponseResult.FAIL("音频不存在");
        }
        queryBlogAudio.setName(blogAudio.getName());
        queryBlogAudio.setArtist(blogAudio.getArtist());
        queryBlogAudio.setAlbum(blogAudio.getAlbum());
        queryBlogAudio.setAudioOrder(blogAudio.getAudioOrder());
        queryBlogAudio.setAudioUrl(blogAudio.getAudioUrl());
        queryBlogAudio.setCoverUrl(blogAudio.getCoverUrl());
        queryBlogAudio.setUpdateTime(new Date());
        redisUtil.del(Constants.Audio.KEY_AUDIO_LIST_CACHE);
        return ResponseResult.SUCCESS("修改音频成功");
    }

    @Override
    public ResponseResult getAudioList(int page, int size) {
        String audioListCacheJson = (String) redisUtil.get(Constants.Audio.KEY_AUDIO_LIST_CACHE);
        if (!TextUtil.isEmpty(audioListCacheJson)) {
            BlogPaging<List<BlogAudio>> audioListCache = gson.fromJson(audioListCacheJson, new TypeToken<BlogPaging<List<BlogAudio>>>() {
            }.getType());
            if (audioListCache != null) {
                log.info("前端从redis获取音频列表");
                return ResponseResult.SUCCESS("获取音频列表成功").setData(audioListCache);
            }
        }
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Sort sort = Sort.by(Sort.Direction.ASC, "audioOrder");
        PageRequest pageRequest = PageRequest.of(pageInfo.page - 1, pageInfo.size, sort);
        Page<BlogAudio> all = audioDao.findAll(pageRequest);
        BlogPaging<List<BlogAudio>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, all.getTotalElements(), all.getContent());
        String audioListJson = gson.toJson(paging);
        redisUtil.set(Constants.Audio.KEY_AUDIO_LIST_CACHE, audioListJson, Constants.TimeValue.HOUR);
        log.info("音频列表已缓存到redis");
        return ResponseResult.SUCCESS("获取音频列表成功").setData(paging);
    }

    @Transactional
    @Override
    public ResponseResult deleteAudio(String id) {
        if (TextUtil.isEmpty(id)) {
            return ResponseResult.FAIL("音频不存在");
        }
        int deleteCount = audioDao.deleteBlogAudioById(id);
        if (deleteCount == 0) {
            log.info("管理平台删除音频失败 ----> " + id);
            return ResponseResult.SUCCESS("删除音频失败");
        }
        log.info("管理平台删除音频 ----> " + id);
        redisUtil.del(Constants.Audio.KEY_AUDIO_LIST_CACHE);
        return ResponseResult.SUCCESS("删除音频成功");
    }

    private boolean checkAudioParams(BlogAudio blogAudio) {
        return !TextUtil.isEmpty(blogAudio.getName())
                && !TextUtil.isEmpty(blogAudio.getArtist())
                && !TextUtil.isEmpty(blogAudio.getAlbum())
                && !TextUtil.isEmpty(blogAudio.getAudioUrl())
                && !TextUtil.isEmpty(blogAudio.getCoverUrl());
    }

    @Override
    @Async("asyncTaskServiceExecutor")
//    @Scheduled(cron = "0 0 23 * * ?")
    public void downloadAudioFile() {
        File tempAudioFileFolder = new File(tempAudioFilePath);
        if (!tempAudioFileFolder.exists()) {
            tempAudioFileFolder.mkdirs();
        }
        List<BlogAudio> audioList = audioDao.findAll();
        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        for (BlogAudio audio : audioList) {
            String audioUrl = audio.getAudioUrl();
            if (!TextUtil.isEmpty(audioUrl)) {
                try {
                    String suffix = audioUrl.substring(audioUrl.lastIndexOf("."));
                    String targetPath = tempAudioFilePath + audio.getName() + suffix;
                    URI uri = new URI(audioUrl);
                    restTemplate.execute(uri, HttpMethod.GET, requestCallback, response -> {
                        log.info("开始下载音频文件：" + audio.getName());
                        Files.copy(response.getBody(), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
                        return null;
                    });
                    log.info("下载音频文件完成：" + audio.getName());
                } catch (Exception e) {
                    log.error("下载音频文件异常：" + e);
                }
            }
        }
    }
}
