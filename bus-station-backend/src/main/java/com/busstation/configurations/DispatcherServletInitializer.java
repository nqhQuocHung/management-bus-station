package com.busstation.configurations;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {
                HibernateConfiguration.class,
                TilesConfiguration.class,
                SecurityConfiguration.class,
        };
    }



    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {
                WebAppContextConfig.class,
        };
    }



    @Override
    protected String[] getServletMappings() {
        return new String[] {
                "/"
        };
    }
}
