package com.sky.service;

import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {

    void insertBatch(List<DishFlavor> dishFlavorList);
}
