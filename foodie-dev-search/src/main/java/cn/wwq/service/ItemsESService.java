package cn.wwq.service;

import cn.wwq.utils.PagedGridResult;

public interface ItemsESService {

    public PagedGridResult searchItems(String keywords,
                                       String sort,
                                       Integer page,
                                       Integer pageSize);
}
