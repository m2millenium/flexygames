import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Transparent JDBC wrapper in front of org.h2.Driver.
 * Accepts any jdbc:h2: URL and delegates to the real H2 driver,
 * logging every SQL statement and every bound parameter value
 * (setString/setLong/setTimestamp/setDate/setObject/...) to a file.
 * Completely independent of logback/Spring/Grails.
 */
public class FlexyTraceDriver implements Driver {

    private static final Driver REAL;
    private static PrintWriter LOG;
    private static final AtomicInteger COUNTER = new AtomicInteger();

    static {
        try {
            REAL = DriverManager.getDriver("jdbc:h2:mem:__flexytrace_probe__");
            // close probe immediately (mem db dies with it)
            try (Connection c = REAL.connect("jdbc:h2:mem:__flexytrace_probe__", new Properties())) { }
        } catch (Exception e) {
            throw new RuntimeException("Cannot locate real H2 driver", e);
        }
        try {
            LOG = new PrintWriter(new FileWriter("D:/tmp/flexy-jdbc-trace.log", true), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            java.sql.DriverManager.registerDriver(new FlexyTraceDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("FlexyTraceDriver registered; real driver = " + REAL);
    }

    private static void log(String s) {
        LOG.println(s);
        LOG.flush();
    }

    @Override public boolean acceptsURL(String url) { return url != null && url.startsWith("jdbc:h2:"); }
    @Override public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) return null;
        Connection real = REAL.connect(url, info);
        log("=== FLEXTRACE: connect " + url);
        return (Connection) Proxy.newProxyInstance(
            FlexyTraceDriver.class.getClassLoader(),
            new Class<?>[]{Connection.class},
            new WrapHandler(real, Connection.class));
    }
    @Override public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) { return new DriverPropertyInfo[0]; }
    @Override public int getMajorVersion() { return 1; }
    @Override public int getMinorVersion() { return 0; }
    @Override public boolean jdbcCompliant() { return false; }
    @Override public java.util.logging.Logger getParentLogger() { return java.util.logging.Logger.getLogger("flexy"); }

    // ------------------------------------------------------------------

    private static final class WrapHandler implements InvocationHandler {
        private final Object target;
        private final Class<?> iface;
        WrapHandler(Object target, Class<?> iface) { this.target = target; this.iface = iface; }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            try {
                if ("prepareStatement".equals(name) || "prepareCall".equals(name)) {
                    Object stmt = method.invoke(target, args);
                    log("SQL [" + (stmt == null ? "?" : "") + "] " + (args != null && args.length > 0 ? args[0] : ""));
                    return (PreparedStatement) Proxy.newProxyInstance(
                        FlexyTraceDriver.class.getClassLoader(),
                        new Class<?>[]{PreparedStatement.class},
                        new PstmtHandler(stmt, (String) args[0]));
                }
                if ("createStatement".equals(name)) {
                    Object stmt = method.invoke(target, args);
                    return (Statement) Proxy.newProxyInstance(
                        FlexyTraceDriver.class.getClassLoader(),
                        new Class<?>[]{Statement.class},
                        new StmtHandler(stmt));
                }
                Object r = method.invoke(target, args);
                // wrap returned Connection from unwrap/other? not needed
                return r;
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private static final class PstmtHandler implements InvocationHandler {
        private final Object target;
        private final String sql;
        private final StringBuilder binds = new StringBuilder();
        PstmtHandler(Object target, String sql) { this.target = target; this.sql = sql; }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            try {
                if (args != null && args.length == 2 && name.startsWith("set") && isInt(args[0])) {
                    binds.append(" [").append(args[0]).append(": ").append(describe(args[1])).append("]");
                }
                if ("execute".equals(name) || "executeUpdate".equals(name) || "executeQuery".equals(name)) {
                    int id = COUNTER.incrementAndGet();
                    log("STMT#" + id + " " + sql + " Binds:" + binds);
                    binds.setLength(0);
                }
                if ("close".equals(name)) { binds.setLength(0); }
                Object r = method.invoke(target, args);
                return r;
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }

        private static boolean isInt(Object o) { return o instanceof Integer; }

        private static String describe(Object v) {
            if (v == null) return "null";
            if (v instanceof java.sql.Timestamp) return "TIMESTAMP(" + v + ")";
            if (v instanceof java.sql.Date) return "SQLDATE(" + v + ")";
            if (v instanceof java.sql.Time) return "TIME(" + v + ")";
            if (v instanceof Date) return "JDKDATE(" + v + " class=" + v.getClass().getName() + ")";
            if (v instanceof String && ((String) v).length() > 80) return "STRING(len=" + ((String) v).length() + ")";
            return v.getClass().getSimpleName() + "(" + v + ")";
        }
    }

    private static final class StmtHandler implements InvocationHandler {
        private final Object target;
        StmtHandler(Object target) { this.target = target; }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                String name = method.getName();
                if ("execute".equals(name) || "executeUpdate".equals(name) || "executeQuery".equals(name)) {
                    int id = COUNTER.incrementAndGet();
                    log("STMT#" + id + " (plain) " + (args != null && args.length > 0 ? args[0] : ""));
                }
                Object r = method.invoke(target, args);
                return r;
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }
}
