package org.acme;

import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.clickhouse.client.ClickHouseException;
import com.clickhouse.client.ClickHouseRequest;
import com.clickhouse.client.ClickHouseRequestManager;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.jdbc.SqlExceptionUtils;

import io.agroal.api.AgroalDataSource;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ClickhouseService {

    @Inject
    AgroalDataSource defaultDataSource;

    public String getResult(String mcc) throws SQLException{
        String sql = "SELECT count() FROM cell_towers WHERE mcc=" + mcc;
        String queryId = "my-query-id";
        String result = "";
        try (Connection conn = defaultDataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.unwrap(ClickHouseRequest.class).manager(new ClickHouseRequestManager() {
                private final AtomicInteger id = new AtomicInteger(0);

                @Override
                public String createQueryId() {
                    return "my-query-" + id.incrementAndGet();
                }
            });
            try (ClickHouseResponse resp = stmt.unwrap(ClickHouseRequest.class).query(sql, queryId).executeAndWait()) {
                result = resp.firstRecord().getValue(0).asString();
            } catch (ClickHouseException | UncheckedIOException e) {
                throw SqlExceptionUtils.handle(e);
            }
        }
        Log.info("SHOW ME THE MONEY: " + result);
        return result;
    }
    
}
