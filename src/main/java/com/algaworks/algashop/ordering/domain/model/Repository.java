package com.algaworks.algashop.ordering.domain.model;

import java.util.Optional;

public interface Repository<T extends AggregateRoot<ID>, ID> {

	Optional<T> ofId(ID id);

	boolean exists(ID id);

	long count();

	void add(T aggregateRoot);

}
