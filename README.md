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

## Test

```sh
docker build --target build .
```

## Documentation

- [Roadmap](docs/roadmap.md)
