MIGRATION_LABEL = "changeSet-v001"

migration:
	./gradlew liquibaseDiff -PdiffChangeLogFile=src/main/resources/db/changelog/migrations/${MIGRATION_LABEL}.xml
	@echo "\n  - include:" >> src/main/resources/db/changelog/db.changelog-master.yaml
	@echo "      file: db/changelog/migrations/$(MIGRATION_LABEL).xml" >> src/main/resources/db/changelog/db.changelog-master.yaml