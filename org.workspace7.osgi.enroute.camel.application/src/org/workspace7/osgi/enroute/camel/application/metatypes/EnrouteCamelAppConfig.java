package org.workspace7.osgi.enroute.camel.application.metatypes;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(id="org.workspace7.osgi.enroute.camel")
public interface EnrouteCamelAppConfig {

	@AttributeDefinition(type=AttributeType.STRING,required=true)
	String camelContextId();

	@AttributeDefinition(type=AttributeType.STRING,required=true)
	String camelRouteId();

	@AttributeDefinition(type=AttributeType.BOOLEAN,defaultValue="true")
	boolean active();

}
