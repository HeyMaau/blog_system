package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogAudio;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IAudioService {

    ResponseResult addAudio(BlogAudio blogAudio);

    ResponseResult updateAudio(BlogAudio blogAudio);

    ResponseResult getAudioList(int page, int size);

    ResponseResult deleteAudio(String id);
}
