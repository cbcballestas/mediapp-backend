package com.mitocode.service.impl;

import com.mitocode.model.Sign;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.ISignRepo;
import com.mitocode.service.ISignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignServiceImpl extends CRUDImpl<Sign, Integer> implements ISignService {

    //@Autowired
    private final ISignRepo repo;


    @Override
    protected IGenericRepo<Sign, Integer> getRepo() {
        return repo;
    }
}
