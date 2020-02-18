package com.example.study.service;

import com.example.study.model.Entity.Partner;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.PartnerApiRequest;
import com.example.study.model.network.response.PartnerApiResponse;
import com.example.study.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerApiLogicService  extends BaseService<PartnerApiRequest, PartnerApiResponse, Partner> {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Header<PartnerApiResponse> create(Header<PartnerApiRequest> request) {

        PartnerApiRequest body = request.getData();

        Partner partner = Partner.builder()
                .name(body.getName())
                .status(body.getStatus())
                .address(body.getAddress())
                .callCenter(body.getCallCenter())
                .partnerNumber(body.getPartnerNumber())
                .businessNumber(body.getBusinessNumber())
                .ceoName(body.getCeoName())
                .registeredAt(LocalDateTime.now())
                .category(categoryRepository.getOne(body.getId()))
                .build()
                ;

        Partner newPartner = baseRepository.save(partner);

        return Header.OK(response(newPartner));
    }

    @Override
    public Header<PartnerApiResponse> read(Long id) {

        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

    @Override
    public Header<PartnerApiResponse> update(Header<PartnerApiRequest> request) {

        PartnerApiRequest body = request.getData();

       return baseRepository.findById(body.getId())
                .map(partner -> {
                    partner.setName(body.getName());
                    partner.setStatus(body.getStatus());
                    partner.setAddress(body.getAddress());
                    partner.setCallCenter(body.getCallCenter());
                    partner.setPartnerNumber(body.getPartnerNumber());
                    partner.setBusinessNumber(body.getBusinessNumber());
                    partner.setCeoName(body.getCeoName());

                    return partner;

                })
                .map(newPartner -> baseRepository.save(newPartner))
                .map(this::response)
                .map(Header::OK)
                .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

    @Override
    public Header delete(Long id) {
       return baseRepository.findById(id)
                .map(partner -> {
                    baseRepository.delete(partner);
                    return Header.OK();
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

    @Override
    public Header<List<PartnerApiResponse>> search(Pageable pageable) {

        Page<Partner> partners = baseRepository.findAll(pageable);

        List<PartnerApiResponse> partnerApiResponseList = partners.stream()
                .map(this::response)
                .collect(Collectors.toList());

        return Header.OK(partnerApiResponseList);
    }

    private PartnerApiResponse response(Partner partner) {

        PartnerApiResponse body = PartnerApiResponse.builder()
                .name(partner.getName())
                .status(partner.getStatus())
                .address(partner.getAddress())
                .callCenter(partner.getCallCenter())
                .partnerNumber(partner.getPartnerNumber())
                .businessNumber(partner.getBusinessNumber())
                .ceoName(partner.getCeoName())
                .registeredAt(LocalDateTime.now())
                .categoryId(partner.getCategory().getId())
                .build()
                ;

        return body;

    }
}
