# Atlas

Atlas is the HTTP indexing and search service for Vault. It scans shared
content and emits best-effort search telemetry to Forge.

## Run locally

Run Atlas with Vault and their shared content through the sibling `lab`
repository. Docker is the only local prerequisite.

```sh
cd ../lab
docker compose up --build
```

Verify the service:

```sh
curl http://localhost:8081/healthz
```

The response is `200 OK` with the body `ok`.

Search the plain text files under `content/`:

```sh
curl -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q=messaging%3A%20Kafka"
```

Example response:

```json
{
  "query": "messaging: Kafka",
  "count": 1,
  "results": [
    {
      "path": "/cv/skills.txt",
      "line_number": 35,
      "line": "messaging: Kafka"
    }
  ]
}
```

Search is case-insensitive and accepts URL-encoded phrases:

```sh
curl -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q=BACKEND"
curl -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q=distributed%20systems"
curl -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q=Spring%20Boot"
```

All `/search` requests require `Authorization: Bearer <ATLAS_AUTH_TOKEN>`.
`/healthz` remains public for container health checks. In the standard Compose
deployment Atlas has no published host port; the direct examples above apply
only when running Atlas separately for development.

Useful checks:

```sh
curl -i http://localhost:8081/healthz
curl -i -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q="
curl -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q=term-that-does-not-exist"
```

Atlas recursively scans `ATLAS_CONTENT_ROOT`, which defaults to
`/app/content`, and returns matching paths, one-based line numbers, and line
text as JSON. Queries are case-insensitive.

Deployment requires `ATLAS_AUTH_TOKEN` for incoming searches and
`FORGE_AUTH_TOKEN` for outgoing telemetry. Keep both values in the deployment
secret store rather than the image or repository.

## Test

```sh
docker build --target build .
```

## Documentation

Long-form documentation and the Atlas roadmap are maintained in the sibling
`lab` repository under `content/docs/`.
