package com.heidelpay.samples.shop.dao;

import java.io.Serializable;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * do not take this as a serious id generator...
 *
 */
public class RandomIntIdGenerator implements IdentifierGenerator {

	public static final String name = "randomIdGenerator";

	private Random random = new Random(123);
	private static final int UPPER_BOUND = 10000;
	
	
	@Override
	public Serializable generate(SharedSessionContractImplementor arg0, Object arg1) throws HibernateException {
		
		return  new Long(Math.abs( arg1.hashCode() + random.nextInt(UPPER_BOUND) + new Long( System.currentTimeMillis() ).hashCode() ));
	}
	
	
	
	
}
