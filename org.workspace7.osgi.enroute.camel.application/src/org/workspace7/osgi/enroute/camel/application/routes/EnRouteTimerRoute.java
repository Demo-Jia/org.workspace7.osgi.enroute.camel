package org.workspace7.osgi.enroute.camel.application.routes;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name="org.workspace7.osgi.enroute.camel.timer",
		   property="name=timerRoute",
		   immediate=true,
		   service=RouteBuilder.class)
public class EnRouteTimerRoute extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(EnRouteTimerRoute.class);
	
	@Activate
	void start(Map<String, Object> props){
		System.out.println("EnRouteTimerRoute.start() --"+props);
	}
	
	@Override
	public void configure() throws Exception {
		logger.info("Configuring Route");
		from("timer://enRoute?delay=5s")
		.setBody(constant("Srimathe Ramanujaya Namaha!"))
		.log("${body}");
		
	}

}
