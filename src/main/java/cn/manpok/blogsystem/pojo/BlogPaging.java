package cn.manpok.blogsystem.pojo;

/**
 * 分页用
 */
public class BlogPaging<T> {

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 总条数
     */
    private long total;

    /**
     * 当前页码
     */
    private int currentPage;

    /**
     * 没有更多的数据了
     */
    private boolean noMore;

    /**
     * 数据
     */
    private T data;

    public BlogPaging(int pageSize, long total, int currentPage, T data) {
        this.pageSize = pageSize;
        this.total = total;
        this.currentPage = currentPage;
        this.data = data;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isNoMore() {
        return ((long) currentPage * pageSize) == total;
    }

    @Override
    public String toString() {
        return "BlogPaging{" +
                "pageSize=" + pageSize +
                ", total=" + total +
                ", currentPage=" + currentPage +
                ", data=" + data +
                '}';
    }
}
