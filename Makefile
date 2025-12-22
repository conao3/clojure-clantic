.PHONY: all
all:

.PHONY: repl
repl:
	clj -M:dev:repl

.PHONY: update
update:
	clojure -Sdeps '{:deps {com.github.liquidz/antq {:mvn/version "RELEASE"}}}' -M -m antq.core --upgrade --force

.PHONY: test
test:
	clojure -M:dev -m cognitect.test-runner

.PHONY: lint
lint:
	clj-kondo --lint src test dev

.PHONY: clean
clean:
	rm -rf .cpcache target
