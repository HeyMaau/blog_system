package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ILooperDao;
import cn.manpok.blogsystem.pojo.BlogLooper;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ILooperService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class LooperServiceImpl implements ILooperService {

    @Autowired
    private ILooperDao looperDao;

    @Autowired
    private Snowflake snowflake;

    @Override
    public ResponseResult uploadLooper(BlogLooper blogLooper) {
        //检查参数
        if (TextUtil.isEmpty(blogLooper.getTitle())) {
            return ResponseResult.FAIL("轮播图标题为空");
        }
        if (TextUtil.isEmpty(blogLooper.getImageUrl())) {
            return ResponseResult.FAIL("轮播图图片URL为空");
        }
        if (TextUtil.isEmpty(blogLooper.getTargetUrl())) {
            return ResponseResult.FAIL("轮播图目标URL为空");
        }
        //补充数据
        blogLooper.setId(String.valueOf(snowflake.nextId()));
        blogLooper.setState(Constants.DEFAULT_STATE);
        Date date = new Date();
        blogLooper.setCreateTime(date);
        blogLooper.setUpdateTime(date);
        looperDao.save(blogLooper);
        return ResponseResult.SUCCESS("上传轮播图成功");
    }

    @Override
    public ResponseResult getLooper(String looperID) {
        BlogLooper queryLooper = looperDao.findLooperById(looperID);
        if (queryLooper == null) {
            return ResponseResult.FAIL("轮播图不存在");
        }
        return ResponseResult.SUCCESS("获取轮播图成功").setData(queryLooper);
    }

    @Override
    public ResponseResult getLoopers(int page, int size) {
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "updateTime");
        Page<BlogLooper> queryLoopers = looperDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取所有轮播图成功").setData(queryLoopers);
    }
}
