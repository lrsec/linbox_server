package com.linbox.im.server;

import com.linbox.im.server.connector.tcp.ImTcpServer;
import com.linbox.im.server.monitor.IConnectorMonitor;
import com.linbox.im.server.router.ImRouterServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lrsec on 12/29/15.
 */
// For Test purpose
public class ImTcpRouterServer {
    private static Logger logger = LoggerFactory.getLogger(ImTcpRouterServer.class);

    public static void main(String[] args) {

        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring/tcp.xml", "spring/router.xml");

        try {
            IConnectorMonitor connectorMonitorService = (IConnectorMonitor)appContext.getBean("connectorMonitor");
            connectorMonitorService.start();

            ImRouterServer routerServer = (ImRouterServer) appContext.getBean("imRouterServer");
            routerServer.run();

            ImTcpServer tcpServer = (ImTcpServer)appContext.getBean("imTcpServer");
            tcpServer.run(appContext);
        } catch (Exception e) {
            logger.error("Exception in starting ImTcpServer", e);
        }
    }
}
