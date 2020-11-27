package com.airport_management.transaction;

import com.mongodb.Function;
import com.airport_management.repository.mongo.RepositoryMongo;


@FunctionalInterface
public interface TransactionCode<T> extends Function<RepositoryMongo, T> { 
	
}
