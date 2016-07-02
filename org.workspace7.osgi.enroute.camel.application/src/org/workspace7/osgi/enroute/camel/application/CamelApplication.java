package org.workspace7.osgi.enroute.camel.application;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.core.osgi.OsgiCamelContextPublisher;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.spi.ComponentResolver;
import org.apache.camel.spi.Registry;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import osgi.enroute.configurer.api.RequireConfigurerExtender;

@RequireConfigurerExtender
@Component(name = "org.workspace7.osgi.enroute.camel",
		configurationPid = "org.workspace7.osgi.enroute.camel",
		immediate = true,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		service = Runnable.class)
public class CamelApplication implements Runnable {

	private CamelContext osgiCamelContext;
	private Registry osgiRegistry;

	@Reference(target = "(name=timerRoute)")
	RouteBuilder timerRoute;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> camelContextStarter;

	private boolean started;
	private BundleContext bundleContext;

	@Activate
	void start(BundleContext bundleContext, Map<String, Object> properties) throws Exception {

		this.bundleContext = bundleContext;

		// create OSGi Camel Context

		osgiRegistry = new OsgiServiceRegistry(bundleContext);
		osgiCamelContext = new OsgiDefaultCamelContext(bundleContext, osgiRegistry);

		// FIXME am I right ? why this needs to be done, any better way todo
		// in
		// OSGi
		Thread.currentThread().setContextClassLoader(osgiCamelContext.getApplicationContextClassLoader());
		// TODO register properties placeholder component using metatypes

		osgiCamelContext.setUseMDCLogging(true);
		osgiCamelContext.setUseBreadcrumb(true);

		// TODO move this to participants
		try {
			System.out.println("CamelApplication.start() - adding routes");
			osgiCamelContext.addRoutes(timerRoute);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		osgiCamelContext.getManagementStrategy().addEventNotifier(new OsgiCamelContextPublisher(bundleContext));

		camelContextStarter = executor.schedule(this, 1000, TimeUnit.MILLISECONDS);
	}

	@Deactivate
	void stop() {
		if (started) {
			try {
				osgiCamelContext.stop();
				started = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		System.out.println("CamelApplication.run()");
		try {
			osgiCamelContext.setAutoStartup(true);
			osgiCamelContext.start();
			started = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
