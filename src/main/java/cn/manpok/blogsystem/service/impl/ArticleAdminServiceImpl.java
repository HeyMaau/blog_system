package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminDao;
import cn.manpok.blogsystem.dao.ICategoryDao;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IArticleAdminService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class ArticleAdminServiceImpl implements IArticleAdminService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IArticleAdminDao articleAdminDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryDao categoryDao;

    @Override
    public ResponseResult addArticle(BlogArticle blogArticle) {
        //检查分类ID是否为空
        BlogCategory queryCategory = categoryDao.findCategoryById(blogArticle.getCategoryId());
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        //检查文章类型：富文本、MD
        String type = blogArticle.getType();
        if (!type.equals(Constants.Article.TYPE_RICH_TEXT) && !type.equals(Constants.Article.TYPE_MARKDOWN)) {
            return ResponseResult.FAIL("文章类型不正确");
        }
        //检查文章类型：草稿、发表，其他类型在此API不允许
        String state = blogArticle.getState();
        if (state.equals(Constants.Article.STATE_DRAFT)) {
            //草稿类型：不允许标题、内容、摘要都为空
            if (TextUtil.isEmpty(blogArticle.getContent())
                    && TextUtil.isEmpty(blogArticle.getTitle())
                    && TextUtil.isEmpty(blogArticle.getSummary())) {
                return ResponseResult.FAIL("草稿的标题、内容、摘要均为空");
            }
        } else if (state.equals(Constants.Article.STATE_PUBLISH)) {
            //发布类型：标题、内容、摘要不允许为空
            if (TextUtil.isEmpty(blogArticle.getTitle())) {
                return ResponseResult.FAIL("文章标题为空");
            }
            if (TextUtil.isEmpty(blogArticle.getContent())) {
                return ResponseResult.FAIL("文章内容为空");
            }
            if (TextUtil.isEmpty(blogArticle.getSummary())) {
                return ResponseResult.FAIL("文章摘要为空");
            }
        } else {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        //补充数据
        blogArticle.setId(String.valueOf(snowflake.nextId()));
        BlogUser user = userService.checkUserToken();
        blogArticle.setUserId(user.getId());
        Date date = new Date();
        blogArticle.setViewCount(Constants.Article.INITIAL_VIEW_COUNT);
        blogArticle.setCreateTime(date);
        blogArticle.setUpdateTime(date);
        articleAdminDao.save(blogArticle);
        return ResponseResult.SUCCESS("添加文章成功");
    }
}
