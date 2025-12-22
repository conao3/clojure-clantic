.PHONY: all
all:

.PHONY: repl
repl:
	clj -M:dev

.PHONY: test
test:
	clojure -M:dev -m cognitect.test-runner

.PHONY: clean
clean:
	rm -rf .cpcache target
