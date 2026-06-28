package dev.blumek.party.relationships.infrastructure.persistence;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import dev.blumek.party.relationships.application.RelationshipFinder;
import dev.blumek.party.relationships.application.RelationshipQuery;
import dev.blumek.party.relationships.application.RelationshipSummary;

@Repository
@Profile("jdbc")
class JdbcRelationshipFinder implements RelationshipFinder {

    private final JdbcClient jdbc;

    JdbcRelationshipFinder(final JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<RelationshipSummary> find(final RelationshipQuery query) {
        final var sql = new StringBuilder(
                "select id, from_party, from_role, to_party, to_role, type, valid_from, valid_to"
                        + " from relationships.relationship where ");
        final var params = new LinkedHashMap<String, Object>();
        params.put("owner", query.owner().value());
        sql.append(direction(query, params));
        if (query.type() != null) {
            sql.append(" and type = :type");
            params.put("type", query.type());
        }
        var spec = jdbc.sql(sql.toString());
        for (final var param : params.entrySet()) {
            spec = spec.param(param.getKey(), param.getValue());
        }
        return spec.query((rs, rowNum) -> new RelationshipSummary(
                rs.getObject("id", UUID.class).toString(),
                rs.getObject("from_party", UUID.class).toString(),
                rs.getString("from_role"),
                rs.getObject("to_party", UUID.class).toString(),
                rs.getString("to_role"),
                rs.getString("type"),
                rs.getObject("valid_from", LocalDate.class),
                rs.getObject("valid_to", LocalDate.class))).list();
    }

    private static String direction(final RelationshipQuery query, final LinkedHashMap<String, Object> params) {
        final var role = query.role();
        if (role != null) {
            params.put("role", role);
        }
        return switch (query.direction()) {
            case OUTGOING -> "from_party = :owner" + (role == null ? "" : " and to_role = :role");
            case INCOMING -> "to_party = :owner" + (role == null ? "" : " and from_role = :role");
            case ANY -> role == null
                    ? "(from_party = :owner or to_party = :owner)"
                    : "((from_party = :owner and to_role = :role) or (to_party = :owner and from_role = :role))";
        };
    }
}
