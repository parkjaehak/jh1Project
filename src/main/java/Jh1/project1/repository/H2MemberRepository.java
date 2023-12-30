package Jh1.project1.repository;

import Jh1.project1.domain.Member;
import Jh1.project1.exception.MyDbException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 1. JDBC - DriverManager 사용
 * 2. JDBC - DataSource, JdbcUtils 사용
 */

@Slf4j
@Repository
public class H2MemberRepository extends AbstractMemberRepository {

    private final DataSource dataSource;

    public H2MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getLoginId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("save error", e);
            throw new MyDbException();
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
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
                member.setLoginId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return Optional.of(member);
            } else {
                // delete 후 회원이 없을 경우
                throw new NoSuchElementException("member not found memberId=" + loginId);
            }
        } catch (SQLException e) {
            log.error("findByLoginId error", e);
            throw new MyDbException();
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
            log.error("update error", e);
            throw new MyDbException();
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
            log.error("delete error", e);
            throw new MyDbException();
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
