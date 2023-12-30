package Jh1.project1.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static Jh1.project1.ConnectionConst.*;

/**
 * 1. JDBC가 제공하는 DriverManager는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.
 */
@Slf4j
public class DBConncectionUtil {
    public static Connection getConnection() {
        try {
            // 실제로 DriverManager가 가져오는 부분
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
