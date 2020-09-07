package com.liu.Service.impl;

import com.liu.Dao.GpuDao;
import com.liu.Service.UserService;
import com.liu.pojo.Gpu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    GpuDao gpuDao;
    @Override
    public void save(Gpu gpu) {
        this.gpuDao.save(gpu);
    }

    @Override
    public List<Gpu> findGpu(Gpu gpu) {
        //声明查询条件
        Example<Gpu> example=Example.of(gpu);
        //根据条件进行查询
        return this.gpuDao.findAll(example);
    }
}
