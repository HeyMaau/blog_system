package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogThinking;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IThinkingService {

    ResponseResult addThinking(BlogThinking thinking);

    ResponseResult updateThinking(BlogThinking thinking);
}
