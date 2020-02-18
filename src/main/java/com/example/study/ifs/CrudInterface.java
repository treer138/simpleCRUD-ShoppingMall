package com.example.study.ifs;

import com.example.study.model.network.Header;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.util.List;


public interface CrudInterface<Req, Res> {

    Header<Res> create(Header<Req> request); //todo request object 추가

    Header<Res> read(Long id);

    Header<Res> update(Header<Req> request);

    Header delete(Long id);

    Header<List<Res>> search(Pageable pageable);


}
