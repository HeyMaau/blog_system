package cn.manpok.blogsystem.pojo;

/**
 * 分页用
 */
public class BlogPaging {

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 总条数
     */
    private int total;

    /**
     * 当前页码
     */
    private int currentPage;

    /**
     * 数据
     */
    private Object data;

    public BlogPaging(int pageSize, int total, int currentPage, Object data) {
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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
