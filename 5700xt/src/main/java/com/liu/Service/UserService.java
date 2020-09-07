package com.liu.Service;

import com.liu.pojo.Gpu;

import java.util.List;

public interface UserService {
    /*
    * 保存商品
    * */
    public void save(Gpu gpu);
    /*
    * 根据条件查询商品
    * */
    public List<Gpu> findGpu(Gpu gpu);
}
