package Jh1.project1.repository;

import Jh1.project1.domain.Member;
import Jh1.project1.exception.MyDbException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 1. JDBC - DriverManager 사용
 * 2. JDBC - DataSource, JdbcUtils 사용
 */

/**
 * 테이블 생성 schema
 *
 drop table member if exists cascade;
 create table member (
 id long not null default 0,
 name varchar(20),
 member_id varchar(20),
 password varchar(20),
 money integer not null default 0,
 primary key (member_id)
 );
 *
 */
@Slf4j
@Repository
public class H2MemberRepository extends AbstractMemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exceptionTranslator;
    private static long sequence = 0L;
    public H2MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {

        String sql = "insert into member(id, name, member_id, password, money) values(?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        //커넥션 조회, 커넥션 동기화
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            member.setId(++sequence);

            pstmt.setLong(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getLoginId());
            pstmt.setString(4, member.getPassword());
            pstmt.setInt(5, member.getMoney());

            pstmt.executeUpdate(); //결과바인딩
            return member;
        } catch (SQLException e) { //예외 발생시 스프링 예외 변환기 실행
            DataAccessException resultEx = exceptionTranslator.translate("save", sql, e);

            log.info("e.getErrorCode={}", e.getErrorCode());
            //log.info("resultEx", resultEx);
            //log.info("resultEx.getClass()={}", resultEx.getClass()); //DataIntegrityViolationException, DuplicateKeyException

            throw resultEx;
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public Member findByLoginIdH2(String loginId) {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, loginId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setLoginId(rs.getString("member_id"));
                member.setName(rs.getString("name"));
                member.setPassword(rs.getString("password"));
                member.setMoney(rs.getInt("money"));

                return member;
            } else {
                // delete 후 회원이 없을 경우
                //throw new NoSuchElementException("member not found memberId=" + loginId);
                return null;
            }
        } catch (SQLException e) {
            throw exceptionTranslator.translate("findByLoginId", sql, e);
        } finally {
            close(con, pstmt, rs);
        }
    }


    @Override
    public void update(String loginId, int money) {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, loginId);

            int resultSize = pstmt.executeUpdate(); //executeUpdate() 은 int를 반환하는데 영향받은 DB row 수를 반환한다
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            throw exceptionTranslator.translate("update", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }


    @Override
    public void delete(String loginId) {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();//쿼리로 데이터를 삭제한 다음
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, loginId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw exceptionTranslator.translate("delete", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        //Connection con = DBConncectionUtil.getConnection();

        //Connection con = dataSource.getConnection();

        //DataSourceUtils : 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내 사용
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={} class={}", con, con.getClass());
        return con;
    }

}
