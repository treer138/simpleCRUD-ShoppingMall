package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.Entity.Category;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.CategoryApiRequest;
import com.example.study.model.network.response.CategoryApiResponse;
import com.example.study.repository.CategoryRepository;
import com.example.study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryApiLogicService extends BaseService<CategoryApiRequest, CategoryApiResponse, Category> {

    @Autowired
    UserRepository userRepository;


    @Override
    public Header<CategoryApiResponse> create(Header<CategoryApiRequest> request) {

        CategoryApiRequest body = request.getData();

        Category category = Category.builder()
                .type(body.getType())
                .title(body.getTitle())
                .build()
                ;
        Category newCategory = baseRepository.save(category);


        return Header.OK(response(newCategory));
    }

    @Override
    public Header<CategoryApiResponse> read(Long id) {

        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

    @Override
    public Header<CategoryApiResponse> update(Header<CategoryApiRequest> request) {

        CategoryApiRequest body = request.getData();


       return baseRepository.findById(body.getId())
                .map(category -> {
                    category.setType(body.getType());
                    category.setTitle(body.getTitle());
                    return category;
                })
                .map(newCategory -> baseRepository.save(newCategory))
                .map(this::response)
                .map(Header::OK)
                .orElseGet(() -> Header.ERROR("데이터 없음"));


    }

    @Override
    public Header delete(Long id) {

       return baseRepository.findById(id)
                .map(category -> {
                    baseRepository.delete(category);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("데이터 없음"));

    }

    @Override
    public Header<List<CategoryApiResponse>> search(Pageable pageable) {

        Page<Category> categoryies = baseRepository.findAll(pageable);

        List<CategoryApiResponse> categoryList = categoryies.stream()
                .map(this::response)
                .collect(Collectors.toList())
                ;

        return Header.OK(categoryList);
    }

    private CategoryApiResponse response(Category category){

        CategoryApiResponse body = CategoryApiResponse.builder()
                .id(category.getId())
                .title(category.getTitle())
                .type(category.getType())
                .build()
                ;


        return body;
    }
}
