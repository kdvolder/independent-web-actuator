package com.example.subapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.invoke.convert.ConversionServiceParameterValueMapper;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

public class SideApplicationBootstrap implements SmartLifecycle {
	
	private AnnotationConfigServletWebServerApplicationContext ctx;
	
	public SideApplicationBootstrap(ApplicationContext hostAppCtx) {
		this.ctx = new AnnotationConfigServletWebServerApplicationContext();
//		this.ctx.scan(this.getClass().getPackageName());
		this.ctx.registerBean("webEndpoints", WebEndpointDiscoverer.class, () -> 
			//Create WebEndpointDiscoverer that is completely unfiltered
			new WebEndpointDiscoverer(hostAppCtx, 
				new ConversionServiceParameterValueMapper(ApplicationConversionService.getSharedInstance()),
				EndpointMediaTypes.DEFAULT,
				List.of(), List.of(), List.of()
			)
		);
		this.ctx.register(SideAppConfig.class);
	}
	
	@Configuration
	@EnableWebMvc
	static class SideAppConfig {
		
		@Autowired
		WebEndpointDiscoverer webEndpoints;
		
		@Autowired
		Environment env;
		
		@EventListener
		void onContextStarted(ContextStartedEvent evt) {
			MutablePropertySources sources = ((StandardEnvironment)env).getPropertySources();
			for (PropertySource<?> s : sources) {
				System.out.println(s);
			}
			
//			for (ExposableWebEndpoint ep : webEndpoints.getEndpoints()) {
//				System.out.println(ep);
//			}
		}
		
		
		@Bean
		TomcatServletWebServerFactory tomcatFactory() {
			return new TomcatServletWebServerFactory(9999);
		}
		
		@Bean
		DispatcherServlet dispatcherServlet() {
			return new DispatcherServlet();
		}
		
        @Bean
        WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier
//				ServletEndpointsSupplier servletEndpointsSupplier, 
//				ControllerEndpointsSupplier controllerEndpointsSupplier,
//				EndpointMediaTypes endpointMediaTypes, 
//                CorsEndpointProperties corsProperties
//				WebEndpointProperties webEndpointProperties
//				Environment environment
        ) {
            List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
            Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
            allEndpoints.addAll(webEndpoints);
//			allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
//			allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
            String basePath = "/";
            EndpointMapping endpointMapping = new EndpointMapping(basePath);
            boolean shouldRegisterLinksMapping = true;
            return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, EndpointMediaTypes.DEFAULT,
                    null, new EndpointLinksResolver(allEndpoints, basePath),
                    shouldRegisterLinksMapping, WebMvcAutoConfiguration.pathPatternParser);
        }
		
		
	}
	

	@Override
	public void start() {
		this.ctx.refresh();
		this.ctx.start();
	}

	@Override
	public void stop() {
		this.ctx.stop();
	}

	@Override
	public boolean isRunning() {
		return ctx.isRunning();
	}

}
