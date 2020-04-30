MIGRATION_LABEL = "changeSet-v002"

migration:
	./gradlew diffChangeLog -PdiffChangeLogFile=src/main/resources/db/changelog/migrations/${MIGRATION_LABEL}.xml
	@echo "\n  - include:" >> src/main/resources/db/changelog/db.changelog-master.yaml
	@echo "      file: db/changelog/migrations/$(MIGRATION_LABEL).xml" >> src/main/resources/db/changelog/db.changelog-master.yaml