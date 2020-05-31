### Maven commands

**Documentation:**
- [Available changes](https://docs.liquibase.com/change-types/home.html)
- [Maven plugin documentation](https://docs.liquibase.com/tools-integrations/maven/commands/home.html) 
- [Best practices](https://docs.liquibase.com/concepts/bestpractices.html)
- [Workflows](https://docs.liquibase.com/workflows/liquibase-community/home.html)

**Apply changesets:**
- Apply pending changesets to the DB: `./mvnw liquibase:update` 
- See the generated SQL for applying pending changesets (see target/liquibase): `./mvnw liquibase:updateSQL`
- Apply changesets, rollback and reapply changesets: `./mvnw liquibase:updateTestingRollback`

**Work with checksums:**
- Clear all checksums, checksums are recalculated upon next Liquibase run: `./mvnw liquibase:clearCheckSums`
- Mark all changesets as applied (used if you manually updated the DB): `./mvnw liquibase:changelogSync`
- See the generated SQL uset to mark all pending changesets as applied: `./mvnw liquibase:changelogSyncSQL`

**Tags:**
- Tag a changesets (used to rollback on tag): `./mvnw liquibase:tag -Dliquibase.tag=checkpoint`

**Rollback changesets:**
- Rollback changesets (1 file): `./mvnw liquibase:rollback -Dliquibase.rollbackCount=1`
- Rollback changesets (everything after the tag): `./mvnw liquibase:rollback -Dliquibase.rollbackTag=checkpoint`
- See generated SQL for rollback (see target/liquibase): `./mvnw liquibase:rollbackSQL -Dliquibase.rollbackCount=1`
- See generated SQL for the rollback required to revert pending (un-applied) changesets: ` ./mvnw liquibase:futureRollbackSQL`

**Locks:**
- List DB locks: `./mvnw liquibase:listLocks`
- Release DB locks: `./mvnw liquibase:releaseLocks`

**Misc:**
- See pending changesets: `./mvnw liquibase:status`
- Generate schema changesets from existing DB (used for adding liquibase to existing DB): `./mvnw liquibase:generateChangeLog`
- Generate data changesets from existing DB (used for adding liquibase to existing DB): `./mvnw liquibase:generateChangeLog -Dliquibase.diffTypes=data`
- Generate a diff between two DBs (additional properties config required): `./mvnw liquibase:diff`
- Generate DB documentation: `./mvnw liquibase:dbDoc`
- Drop all DB changes: `./mvnw liquibase:dropAll`

### Notes

- Changeset: An atomic piece of DB migration code. Can be defined in XML, SQL, YAML and JSON.
- Changelog: An aggregation of changesets.
- **DATABASECHANGELOG:** Table that tracks which changesets were applied to the DB.
- **DATABASECHANGELOGLOCK:** Table that tracks the locks associated with Liquibase migrations.
- Contexts, labels and preconditions are used to conditionally run changesets.
