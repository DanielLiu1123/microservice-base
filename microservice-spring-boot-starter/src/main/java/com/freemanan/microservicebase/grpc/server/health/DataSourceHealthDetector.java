package com.freemanan.microservicebase.grpc.server.health;

import com.freemanan.microservicebase.grpc.GrpcProperties;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Detects the health of data source.
 *
 * @author Freeman
 * @since 2022/8/17
 */
public class DataSourceHealthDetector implements HealthDetector {

    private static final Logger log = LoggerFactory.getLogger(DataSourceHealthDetector.class);

    private final DataSource dataSource;
    private final GrpcProperties grpcProperties;

    public DataSourceHealthDetector(GrpcProperties grpcProperties, DataSource dataSource) {
        Assert.notNull(dataSource, "dataSource can't be null");
        Assert.notNull(grpcProperties, "grpcProperties can't be null");
        this.dataSource = dataSource;
        this.grpcProperties = grpcProperties;
    }

    @Override
    public ServingStatus check(HealthCheckRequest request) {
        try (Connection conn = dataSource.getConnection()) {
            String validationQuerySql =
                    grpcProperties.getServer().getHealth().getDataSource().getValidationQuery();
            PreparedStatement statement = conn.prepareStatement(validationQuerySql);
            statement.executeQuery();
            statement.close();
        } catch (SQLException e) {
            log.warn("DataSource health check failed!", e);
            return ServingStatus.NOT_SERVING;
        }
        return ServingStatus.SERVING;
    }
}
