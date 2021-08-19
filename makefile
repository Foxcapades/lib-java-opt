.PHONY: build
build:
	@./gradlew build

.PHONY: gen-docs
gen-docs:
	@./gradlew javadoc
	@rm -rf docs
	@cp -r build/docs/javadoc docs

.PHONY: test
test:
	@./gradlew test
