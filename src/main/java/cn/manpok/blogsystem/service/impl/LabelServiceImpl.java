package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ILabelDao;
import cn.manpok.blogsystem.pojo.BlogLabel;
import cn.manpok.blogsystem.service.ILabelService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        if (labelStr.contains(Constants.Label.LABEL_SEPARATOR)) {
            String[] split = labelStr.split(Constants.Label.LABEL_SEPARATOR);
            labelList.addAll(Arrays.asList(split));
        } else {
            labelList.add(labelStr);
        }
        for (String labelName : labelList) {
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
    }
}