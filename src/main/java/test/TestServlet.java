package test;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.jdbc.common.DatabaseType;
import org.infinispan.persistence.jdbc.configuration.*;

public class TestServlet extends HttpServlet {

        private Cache<String, String> myCache;
        private DefaultCacheManager mn;
        public void init() {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.memory().maxCount(100).persistence().passivation(true).addStore(JdbcStringBasedStoreConfigurationBuilder.class)
                        .dialect(DatabaseType.POSTGRES)
                        .table()
                        .dropOnExit(true)
                        .createOnStart(true)
                        .tableNamePrefix("anna")
                        .idColumnName("ID_COLUMN").idColumnType("VARCHAR(255)")
                        .dataColumnName("DATA_COLUMN").dataColumnType("VARCHAR(255)")
                        .timestampColumnName("TIMESTAMP_COLUMN").timestampColumnType("BIGINT")
                        .segmentColumnName("SEGMENT_COLUMN").segmentColumnType("INT")
                        .dataSource().jndiUrl("java:/comp/env/jdbc/MyLocalDB");

                DefaultCacheManager mn = new DefaultCacheManager();
                myCache =  mn.createCache("test", builder.build());
        }

        public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
                int initialNum = myCache.size();
                System.out.println("Getting size: " + initialNum);

                int count = 1000;
                String param = request.getParameter("count");
                if (param != null) {
                        count = Integer.parseInt(param);
                }
                for (int i = 0; i < count; i++) {
                        System.out.println("Putting key " + i);
                        myCache.put("key" + i, "key" + i);
                }
                //myCache.evict("key1");
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();

                out.println("<html>");
                out.println("Initial num: " + initialNum);
                out.println("</html>");
        }

        public void doPost(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
                doGet(request, response);
        }

        public void destroy() {
                myCache.stop();
                mn.stop();
        }

}
