MIGRATION_LABEL = "changeSet-v001"

migration:
	./gradlew liquibaseDiff -PdiffChangeLogFile=src/main/resources/db/changelog/migrations/${MIGRATION_LABEL}.yaml
	@echo "\n  - include:" >> src/main/resources/db/changelog/db.changelog-master.yaml
	@echo "      file: classpath:db/changelog/migrations/$(MIGRATION_LABEL).yaml" >> src/main/resources/db/changelog/db.changelog-master.yaml