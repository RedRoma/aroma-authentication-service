
package tech.aroma.authentication.service.server;

/*
 * Copyright 2016 RedRoma, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.SocketException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.authentication.service.ModuleAuthenticationService;
import tech.aroma.authentication.service.operations.ModuleAuthenticationOperations;
import tech.aroma.data.cassandra.ModuleCassandraDataRepositories;
import tech.aroma.data.cassandra.ModuleCassandraDevCluster;
import tech.aroma.thrift.authentication.service.AuthenticationService;
import tech.aroma.thrift.authentication.service.AuthenticationServiceConstants;
import tech.sirwellington.alchemy.annotations.access.Internal;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * This Main Class runs the Authentication Service on a Server Socket.
 * 
 * @author SirWellington
 */
@Internal
public final class TcpServer
{

    private final static Logger LOG = LoggerFactory.getLogger(TcpServer.class);
    private static final int DEFAULT_PORT = AuthenticationServiceConstants.SERVICE_PORT;

    public static void main(String[] args) throws TTransportException, SocketException
    {
        int port = getPortFromArgs(args);
        
        
        Injector injector = Guice.createInjector(new ModuleAuthenticationOperations(),
                                                 new ModuleAuthenticationService(),
                                                 new ModuleCassandraDataRepositories(),
                                                 new ModuleCassandraDevCluster());

        AuthenticationService.Iface authenticationService = injector.getInstance(AuthenticationService.Iface.class);
        AuthenticationService.Processor processor = new AuthenticationService.Processor<>(authenticationService);

        TServerSocket socket = new TServerSocket(port);
        socket.getServerSocket().setSoTimeout((int) SECONDS.toMillis(30));

        TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(socket)
            .protocolFactory(new TBinaryProtocol.Factory())
            .processor(processor)
            .requestTimeout(60)
            .requestTimeoutUnit(SECONDS)
            .minWorkerThreads(5)
            .maxWorkerThreads(100);
        
        LOG.info("Starting Authentication Service at port {}", port);
        
        TThreadPoolServer server = new TThreadPoolServer(serverArgs);
        server.serve();
        server.stop();
    }

    private static int getPortFromArgs(String[] args)
    {
        OptionParser parser = new OptionParser();
        parser.accepts("port").withRequiredArg();
        OptionSet options = parser.parse(args);

        Integer port = DEFAULT_PORT;
        if (!options.has("port"))
        {
            return port;
        }

        try
        {
            port = Integer.valueOf(options.valueOf("port").toString());
            Preconditions.checkArgument(port > 0, "Port must be at least 0");
        }
        catch (Exception ex)
        {
            LOG.warn("Bad --port argument. Using default {}", port, ex);
        }

        return port;
    }
}
