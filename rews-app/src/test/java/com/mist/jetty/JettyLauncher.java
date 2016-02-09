package com.mist.jetty;

import com.google.common.collect.ImmutableList;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class JettyLauncher {

    public static void main(String[] args) throws Exception {
        // Use internal Jetty logger
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        Logger log = Log.getLogger(JettyLauncher.class);

        String context = null;
        String webAppDir = null;
        Integer port = null;

        for(int i=0; i < args.length;i++){
            if("-context".equals(args[i])){
                context=args[i+1];
            } else if("-webapp".equals(args[i])){
                webAppDir=args[i+1];
            } else if("-port".equals(args[i])){
                port=Integer.valueOf(args[i+1]);
            }
        }

        if(context == null){
            context = "/";
        }
        if(webAppDir == null){
            webAppDir = "src/main/webapp";
        }
        if(port == null){
            port = 8080;
        }

        log.info("Starting server context: " + context + ", webapp: " + webAppDir + ", port: " + port);

        Server server = new Server(port);
        server.setStopAtShutdown(true);

        WebAppContext web = new WebAppContext();
        web.setContextPath(context);
        web.setResourceBase(webAppDir);
        web.setConfigurationClasses(
                ImmutableList.<String>builder()
                    .add(WebAppContext.getDefaultConfigurationClasses())
                    .add(EnvConfiguration.class.getCanonicalName())
                    .add(PlusConfiguration.class.getCanonicalName())
                    .build().toArray(new String[web.getConfigurationClasses().length+1])
        );
        web.setThrowUnavailableOnStartupException(true);

        web.setParentLoaderPriority(true);
        server.setHandler(web);

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        server.addEventListener(mBeanContainer);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
