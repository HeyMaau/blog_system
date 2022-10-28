package cn.manpok.blogsystem.service;

public interface ILabelService {

    /**
     * 增加数据库中标签的计数
     * 仅用于发布文章时，修改文章不适用
     *
     * @param labelStr
     * @return
     */
    void addLabelInDB(String labelStr);
}
