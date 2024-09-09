package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogThinking;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IThinkingService {

    ResponseResult addThinking(BlogThinking thinking);

    ResponseResult updateThinking(BlogThinking thinking);

    ResponseResult deleteThinking(String thinkingID);

    ResponseResult deleteThinkingPhysically(String thinkingID);

    ResponseResult getNormalThinkings(int page, int size);

    ResponseResult getAllThinkings(int page, int size, String keyword, String state);

    ResponseResult getThinking(String id);
}
