package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Notice;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;

public interface NoticeService {

    PageResult<Notice> page(PageParam param);

    void create(String title, String content);

    void update(Long id, String title, String content);

    void delete(Long id);

    Notice detail(Long id);

    String getLatest();
}
