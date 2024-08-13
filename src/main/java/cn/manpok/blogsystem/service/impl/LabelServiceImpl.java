package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ILabelDao;
import cn.manpok.blogsystem.pojo.BlogLabel;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ILabelService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class LabelServiceImpl implements ILabelService {

    @Autowired
    private ILabelDao labelDao;

    @Autowired
    private Snowflake snowflake;

    @Override
    public void addLabelInDB(String labelStr) {
        List<String> labelList = new ArrayList<>();
        String[] split = labelStr.split(Constants.Label.LABEL_SEPARATOR);
        labelList.addAll(Arrays.asList(split));
        for (String labelName : labelList) {
            addLabelCount(labelName);
        }
    }

    /**
     * 标签统计数加1
     *
     * @param labelName
     */
    private void addLabelCount(String labelName) {
        BlogLabel queryLabel = labelDao.findLabelByName(labelName);
        Date date = new Date();
        if (queryLabel == null) {
            queryLabel = new BlogLabel();
            queryLabel.setId(String.valueOf(snowflake.nextId()));
            queryLabel.setName(labelName);
            queryLabel.setCount(Constants.Label.INITIAL_COUNT);
            queryLabel.setCreateTime(date);
            queryLabel.setUpdateTime(date);
            labelDao.save(queryLabel);
        } else {
            long count = queryLabel.getCount();
            queryLabel.setCount(++count);
            queryLabel.setUpdateTime(date);
        }
    }

    /**
     * 标签统计数-1
     *
     * @param labelName
     */
    private void subtractLabelCount(String labelName) {
        BlogLabel queryLabel = labelDao.findLabelByName(labelName);
        long count = queryLabel.getCount();
        queryLabel.setCount(--count);
        queryLabel.setUpdateTime(new Date());
    }

    @Override
    public ResponseResult getLabelsData(int size) {
        Pageable pageable = PageRequest.of(Constants.Page.DEFAULT_PAGE - 1, size, Sort.Direction.DESC, "count");
        Page<BlogLabel> all = labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取标签数据成功").setData(all);
    }

    @Override
    public void updateLabelInDB(String oldLabels, String newLabels) {
        //将新标签装进set里
        String[] newLabelArr = newLabels.split(Constants.Label.LABEL_SEPARATOR);
        Set<String> newLabelSet = new HashSet();
        Collections.addAll(newLabelSet, newLabelArr);
        //遍历旧的标签数组
        String[] oldLabelArr = oldLabels.split(Constants.Label.LABEL_SEPARATOR);
        for (String label : oldLabelArr) {
            //1、如果set中没有，说明已经被删除了，将数据库中对应标签计数-1
            if (!newLabelSet.contains(label)) {
                subtractLabelCount(label);
            } else {
                //2、如果set中有，说明新旧标签一致，则移除set中的数据
                newLabelSet.remove(label);
            }
        }
        //3、set中剩下的数据，就是要新增的数据
        for (String label : newLabelSet) {
            addLabelCount(label);
        }
    }
}
