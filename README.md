# Atlas

Atlas is the HTTP indexing and search service for Vault. This initial version
only provides a health endpoint.

## Run

Docker is the only local prerequisite.

```sh
docker compose up --build
```

Verify the service:

```sh
curl http://localhost:8080/healthz
```

The response is `200 OK` with the body `ok`.

Search the plain text files under `content/`:

```sh
curl "http://localhost:8080/search?q=kafka"
```

Atlas recursively scans the mounted directory and returns matching paths,
one-based line numbers, and line text as JSON. Queries are case-insensitive.

## Test

```sh
docker build --target build .
```

## Documentation

- [Roadmap](docs/roadmap.md)
