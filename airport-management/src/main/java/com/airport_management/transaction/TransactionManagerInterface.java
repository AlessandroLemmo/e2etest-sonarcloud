package com.airport_management.transaction;


public interface TransactionManagerInterface {
	
	public <T> T doInTransaction(TransactionCode<T> code);
}
