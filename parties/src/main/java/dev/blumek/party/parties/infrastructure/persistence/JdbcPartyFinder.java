package dev.blumek.party.parties.infrastructure.persistence;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import dev.blumek.party.parties.application.PartyFinder;
import dev.blumek.party.parties.application.PartySearchCriteria;
import dev.blumek.party.parties.application.PartySummary;

@Repository
@Profile("jdbc")
class JdbcPartyFinder implements PartyFinder {

    private final JdbcClient jdbc;

    JdbcPartyFinder(final JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PartySummary> search(final PartySearchCriteria criteria) {
        final var heads = queryHeads(criteria);
        if (heads.isEmpty()) {
            return List.of();
        }
        final var ids = heads.stream().map(PartyHead::id).toList();
        final var roles = rolesByParty(ids);
        final var identifiers = identifiersByParty(ids);
        return heads.stream()
                .map(head -> summary(head, roles, identifiers))
                .toList();
    }

    private List<PartyHead> queryHeads(final PartySearchCriteria criteria) {
        final var sql = new StringBuilder(
                "select id, type, given_name, family_name, legal_name from parties.party where 1 = 1");
        final var params = new LinkedHashMap<String, Object>();
        if (criteria.type() != null) {
            sql.append(" and type = :type");
            params.put("type", criteria.type());
        }
        if (criteria.nameContains() != null) {
            sql.append(" and (coalesce(given_name, '') || ' ' || coalesce(family_name, '') ilike :name escape '\\'"
                    + " or coalesce(legal_name, '') ilike :name escape '\\')");
            params.put("name", "%" + escapeLike(criteria.nameContains()) + "%");
        }
        if (criteria.role() != null) {
            sql.append(" and exists (select 1 from parties.party_role r where r.party_id = party.id and r.name = :role)");
            params.put("role", criteria.role());
        }
        if (criteria.identifier() != null) {
            sql.append(" and exists (select 1 from parties.party_identifier i"
                    + " where i.party_id = party.id and i.value = :identifier)");
            params.put("identifier", criteria.identifier());
        }
        var spec = jdbc.sql(sql.toString());
        for (final var param : params.entrySet()) {
            spec = spec.param(param.getKey(), param.getValue());
        }
        return spec.query((rs, rowNum) -> new PartyHead(rs.getObject("id", UUID.class), rs.getString("type"),
                rs.getString("given_name"), rs.getString("family_name"), rs.getString("legal_name"))).list();
    }

    private static String escapeLike(final String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private Map<UUID, Set<String>> rolesByParty(final List<UUID> ids) {
        return jdbc.sql("select party_id, name from parties.party_role where party_id in (:ids)")
                .param("ids", ids)
                .query((rs, rowNum) -> Map.entry(rs.getObject("party_id", UUID.class), rs.getString("name")))
                .list().stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
    }

    private Map<UUID, Set<PartySummary.IdentifierSummary>> identifiersByParty(final List<UUID> ids) {
        return jdbc.sql("select party_id, kind, value from parties.party_identifier where party_id in (:ids)")
                .param("ids", ids)
                .query((rs, rowNum) -> Map.entry(rs.getObject("party_id", UUID.class),
                        new PartySummary.IdentifierSummary(rs.getString("kind"), rs.getString("value"))))
                .list().stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
    }

    private static PartySummary summary(final PartyHead head, final Map<UUID, Set<String>> roles,
            final Map<UUID, Set<PartySummary.IdentifierSummary>> identifiers) {
        return new PartySummary(head.id().toString(), head.type(), head.displayName(),
                roles.getOrDefault(head.id(), Set.of()),
                identifiers.getOrDefault(head.id(), Set.of()));
    }

    private record PartyHead(UUID id, String type, String givenName, String familyName, String legalName) {

        String displayName() {
            return "PERSON".equals(type) ? givenName + " " + familyName : legalName;
        }
    }
}
