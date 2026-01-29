package com.sxps.xsps.service.impl;

import com.github.pagehelper.PageHelper;
import com.sxps.xsps.mapper.NoticeMapper;
import com.sxps.xsps.pojo.Notice;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper mapper;

    @Override
    public PageResult<Notice> page(PageParam param) {

        PageHelper.startPage(param.getPage(), param.getSize());
        List<Notice> list = mapper.page(param);

        return new PageResult<>(list);
    }

    @Override
    public void create(String title, String content) {
        Notice n = new Notice();
        n.setTitle(title);
        n.setContent(content);
        n.setIsPublished(1);
        mapper.insert(n);
    }

    @Override
    public void update(Long id, String title, String content) {
        Notice n = new Notice();
        n.setId(id);
        n.setTitle(title);
        n.setContent(content);
        mapper.update(n);
    }

    @Override
    public void delete(Long id) {
        mapper.delete(id);
    }

    @Override
    public Notice detail(Long id) {
        return mapper.find(id);
    }

    // 实现新增的方法
    @Override
    public String getLatest() {
        return mapper.selectLatest();
    }
}
