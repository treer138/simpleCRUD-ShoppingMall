package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.Entity.OrderDetail;
import com.example.study.model.Entity.OrderGroup;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.OrderDetailApiRequest;
import com.example.study.model.network.response.OrderDetailApiResponse;
import com.example.study.repository.ItemRepository;
import com.example.study.repository.OrderDetailRepository;
import com.example.study.repository.OrderGroupRepository;
import com.example.study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderDetailApiLogicService extends BaseService<OrderDetailApiRequest, OrderDetailApiResponse, OrderDetail> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderGroupRepository orderGroupRepository;

    @Override
    public Header<OrderDetailApiResponse> create(Header<OrderDetailApiRequest> request) {

        OrderDetailApiRequest body = request.getData();

        OrderDetail orderDetail = OrderDetail.builder()
                .status(body.getStatus())
                .arrivalDate(body.getArrivalDate())
                .quantity(body.getQuantity())
                .totalPrice(body.getTotalPrice())
                .item(itemRepository.findById(body.getId()).get())
                .orderGroup(orderGroupRepository.findById(body.getOrderHistoryId()).get())
                .build()
                ;

        OrderDetail newOrderDetail = baseRepository.save(orderDetail);

        return Header.OK(response(newOrderDetail));
    }

    @Override
    public Header<OrderDetailApiResponse> read(Long id) {

        return baseRepository.findById(id)
                .map(this :: response)
                .map(Header::OK)
                .orElseGet(()-> Header.ERROR("데이터 없음"))
                ;

    }

    @Override
    public Header<OrderDetailApiResponse> update(Header<OrderDetailApiRequest> request) {

        OrderDetailApiRequest body = request.getData();

        return baseRepository.findById(body.getId())
                .map(orderDetail ->{
                    orderDetail.setStatus(body.getStatus());
                    orderDetail.setArrivalDate(body.getArrivalDate());
                    orderDetail.setQuantity(body.getQuantity());
                    orderDetail.setTotalPrice(body.getTotalPrice());
                    orderDetail.setItem(itemRepository.findById(body.getItemId()).get());
                    orderDetail.setOrderGroup(orderGroupRepository.findById(body.getOrderHistoryId()).get());
                    return orderDetail;
                })
                .map(newOrderDetail -> baseRepository.save(newOrderDetail))
                .map(this::response)
                .map(Header::OK)
                .orElseGet(()->Header.ERROR("데이터 없음"))
                ;

    }

    @Override
    public Header delete(Long id) {

        return baseRepository.findById(id)
                .map(orderDetail -> {
                    baseRepository.delete(orderDetail);
                    return Header.OK();
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<List<OrderDetailApiResponse>> search(Pageable pageable) {

        Page<OrderDetail> orderDetailes = baseRepository.findAll(pageable);

        List<OrderDetailApiResponse> orderDetailList = orderDetailes.stream()
                .map(this::response)
                .collect(Collectors.toList())
                ;



        return Header.OK(orderDetailList);
    }

    private OrderDetailApiResponse response(OrderDetail orderDetail){

        OrderDetailApiResponse body = OrderDetailApiResponse.builder()
                .id(orderDetail.getId())
                .status(orderDetail.getStatus())
                .arrivalDate(orderDetail.getArrivalDate())
                .Quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .itemId(orderDetail.getItem().getId())
                .orderHistory(orderDetail.getOrderGroup().getId())
                .build()
                ;


        return body;
    }

}
