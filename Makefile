COMPOSE ?= docker compose

.PHONY: env up build rebuild down all logs restart clean ps prune

env:
	cp env.template .env

up:
	$(COMPOSE) up --build -d

build:
	$(COMPOSE) build

rebuild:
	$(COMPOSE) build --no-cache

down:
	$(COMPOSE) down

all: env clean rebuild up

logs:
	$(COMPOSE) logs -f

restart: down up

clean:
	$(COMPOSE) down -v --remove-orphans

ps:
	while true; do clear; $(COMPOSE) ps; sleep 3; done

prune:
	docker system prune -f
	docker volume prune -f || true
