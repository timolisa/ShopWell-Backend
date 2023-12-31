package com.shopwell.api.services;

import com.shopwell.api.model.VOs.request.CategoryRegistrationVO;
import com.shopwell.api.model.entity.Category;

public interface CategoryService {
    Category registerCategory(CategoryRegistrationVO categoryRegistrationVO);

    Category findProductByCategory(String categoryName);
}
