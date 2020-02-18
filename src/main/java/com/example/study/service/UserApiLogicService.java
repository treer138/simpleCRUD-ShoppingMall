package com.example.study.service;

import com.example.study.model.Entity.OrderDetail;
import com.example.study.model.Entity.OrderGroup;
import com.example.study.model.Entity.User;
import com.example.study.model.enumclass.UserStatus;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.UserApiRequest;
import com.example.study.model.network.response.ItemApiResponse;
import com.example.study.model.network.response.OrderGroupApiResponse;
import com.example.study.model.network.response.UserApiResponse;
import com.example.study.model.network.response.UserOrderInfoApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserApiLogicService extends BaseService<UserApiRequest, UserApiResponse, User> {


    @Autowired
    private OrderGroupApiLogicService orderGroupApiLogicService;

    @Autowired
    private ItemApiLogicService itemApiLogicService;

    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {

        //1. request data
        UserApiRequest userApiRequest = request.getData();

        User user = User.builder()
                .account(userApiRequest.getAccount())
                .password(userApiRequest.getAccount())
                .status(UserStatus.REGISTERED)
                .phoneNumber(userApiRequest.getPhoneNumber())
                .email(userApiRequest.getEmail())
                .registeredAt(LocalDateTime.now())
                .build();
        User newUser = baseRepository.save(user);

        //3. 생성된 데이터 기준으로 리턴 시키기

        return Header.OK(response(newUser));
    }

    @Override
    public Header<UserApiResponse> read(Long id) {

        //id -> repository getOne, getById
        //user -> userApiResponse return
        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {
        // 1. data
            UserApiRequest userApiRequest = request.getData();

        // 2. id -> user 데이터
           Optional<User> optional = baseRepository.findById(userApiRequest.getId());

         return optional.map(user -> {
               // 3. update
               user.setAccount(userApiRequest.getAccount())
                   .setPassword(userApiRequest.getPassword())
                   .setPhoneNumber(userApiRequest.getPhoneNumber())
                   .setStatus(userApiRequest.getStatus())
                   .setEmail(userApiRequest.getEmail())
                   .setRegisteredAt(userApiRequest.getRegisteredAt())
                   .setUnregisteredAt(userApiRequest.getUnregisteredAt())
                   ;
               return user;
           })
                 .map(user -> baseRepository.save(user))
                 .map(this::response)
                 .map(Header::OK)
                 .orElseGet(() -> Header.ERROR("데이터 없음"));


        // 4. userApiResponse

    }

    @Override
    public Header delete(Long id) {
        Optional<User> optional = baseRepository.findById(id);

        return optional.map(user -> {
            baseRepository.delete(user);
            return Header.OK();
        })
        .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

    @Override
    public Header<List<UserApiResponse>> search(Pageable pageable) {

            Page<User> users = baseRepository.findAll(pageable);

            List<UserApiResponse> userApiResponseList = users.stream()
                    .map(this::response)
                    .collect(Collectors.toList());

            return Header.OK(userApiResponseList);
    }

    private UserApiResponse response(User user) {
        //user -> userApiResponse

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword()) // todo 암호화, 길이
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .registeredAt(user.getRegisteredAt())
                .status(user.getStatus())
                .unregisteredAt(user.getUnregisteredAt())
                .build();

        //Header + data return

        return userApiResponse;
    }

    public Header<UserOrderInfoApiResponse> orderInfo(Long id){

        //User
        User user = baseRepository.getOne(id);
        UserApiResponse userApiResponse = response(user);


        List<OrderGroup> orderGroupList = user.getOrderGroupList();
        List<OrderGroupApiResponse> orderGroupApiResponseList = orderGroupList.stream()
                  .map(orderGroup -> {
                      OrderGroupApiResponse orderGroupApiResponse =
                              orderGroupApiLogicService.response(orderGroup);
                      //item api response
                      List<ItemApiResponse> itemApiResponseList =
                              orderGroup.getOrderDetailList().stream()
                              .map(OrderDetail::getItem)
                              .map(itemApiLogicService::response)
                              .collect(Collectors.toList())
                              ;
                      orderGroupApiResponse.setItemApiResponseList(itemApiResponseList);
                      return orderGroupApiResponse;

                })
                .collect(Collectors.toList());

            userApiResponse.setOrderGroupApiResponseList(orderGroupApiResponseList);

            UserOrderInfoApiResponse userOrderInfoApiResponse = UserOrderInfoApiResponse.builder()
                    .userApiResponse(userApiResponse)
                    .build();


            return Header.OK(userOrderInfoApiResponse);
    }
}
