package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface ILabelService {

    /**
     * 增加数据库中标签的计数
     * 仅用于发布文章时，修改文章不适用
     *
     * @param labelStr
     * @return
     */
    void addLabelInDB(String labelStr);

    ResponseResult getLabelsData(int size);

    /**
     * 仅用于用于修改文章时同步修改数据库中的标签数据
     *
     * @param oldLabels
     * @param newLabels
     */
    void updateLabelInDB(String oldLabels, String newLabels);
}
