package com.mitocode.repo;

import com.mitocode.model.User;

import java.util.List;

public interface IUserRepo extends IGenericRepo<User, Integer>{

    //@Query("FROM User u WHERE u.username = :username)
    //DerivedQuery
    User findOneByUsername(String username);
}
