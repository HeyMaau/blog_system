package cn.manpok.blogsystem.utils;

/**
 * 分页查询相关工具类
 */
public class PageUtil {

    /**
     * 检查页数、每页条数是否正确
     *
     * @param page
     * @param size
     */
    public static PageInfo checkPageParam(int page, int size) {
        if (page < Constants.Page.DEFAULT_PAGE) {
            page = Constants.Page.DEFAULT_PAGE;
        }
        if (size < Constants.Page.DEFAULT_SIZE) {
            size = Constants.Page.DEFAULT_SIZE;
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.page = page;
        pageInfo.size = size;
        return pageInfo;
    }

    public static class PageInfo {
        public int page;
        public int size;
    }
}
