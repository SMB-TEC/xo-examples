package com.smbtec.cdo.examples.family;

import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.Query.Result;
import com.buschmais.cdo.api.Query.Result.CompositeRowObject;
import com.buschmais.cdo.api.TransactionAttribute;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.api.Neo4jCdoProvider;
import com.smbtec.cdo.examples.family.domain.Human;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		CdoUnit cdoUnit = new CdoUnit("", "", URI.create("memory:///"),
				Neo4jCdoProvider.class, getTypes(), ValidationMode.AUTO,
				TransactionAttribute.REQUIRES, new Properties());

		CdoManagerFactory cdoManagerFactory = Cdo
				.createCdoManagerFactory(cdoUnit);
		CdoManager cdoManager = cdoManagerFactory.createCdoManager();

		Human janice = cdoManager.create(Human.class);
		janice.setFirstname("Janice");

		Human matt = cdoManager.create(Human.class);
		matt.setFirstname("Matt");
		Human suzie = cdoManager.create(Human.class);
		suzie.setFirstname("Suzie");
		janice.getChildren().add(matt);
		janice.getChildren().add(suzie);

		Human cathy = cdoManager.create(Human.class);
		cathy.setFirstname("Cathy");
		Human jack = cdoManager.create(Human.class);
		jack.setFirstname("Jack");
		suzie.getChildren().add(cathy);
		suzie.getChildren().add(jack);

		Human tom = cdoManager.create(Human.class);
		tom.setFirstname("Tom");
		Human cindy = cdoManager.create(Human.class);
		cindy.setFirstname("Cindy");
		matt.getChildren().add(tom);
		matt.getChildren().add(cindy);

		Result<CompositeRowObject> result = cdoManager
				.createQuery(
						"MATCH (grandma)-->()-->(grandchild) WHERE grandma={janice} RETURN grandchild")
				.withParameter("janice", janice).execute();
		for (CompositeRowObject row : result) {
			LOGGER.info(row.get("grandchild", Human.class).getFirstname());
		}

	}

	private static Set<Class<?>> getTypes() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(Human.class);
		return classes;
	}

}