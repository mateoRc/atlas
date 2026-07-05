# Atlas

Atlas is the HTTP search service for Vaultsh. It scans shared raw Markdown
content and emits best-effort search telemetry to Forge.

## Run

Run the complete stack from the sibling `lab` repository:

```sh
cd ../lab
docker compose up --build
```

Atlas is private in the standard Compose stack. Use Vaultsh's `search` command
or run Atlas separately on port 8081 for direct API development.

Direct search example:

```sh
curl -H "Authorization: Bearer $ATLAS_AUTH_TOKEN" \
  "http://localhost:8081/search?q=Kafka"
```

```json
{
  "query": "Kafka",
  "count": 1,
  "results": [
    {
      "path": "/cv/skills.md",
      "line_number": 10,
      "line": "- **Messaging:** Kafka and RabbitMQ"
    }
  ]
}
```

All `/search` requests require `Authorization: Bearer <ATLAS_AUTH_TOKEN>`.
`/healthz` remains public for container health checks.

Atlas recursively scans `ATLAS_CONTENT_ROOT`, which defaults to
`/app/content`, and returns matching paths, one-based line numbers, and line
text as JSON. Queries are case-insensitive.

Deployment requires `ATLAS_AUTH_TOKEN` for searches and `FORGE_AUTH_TOKEN` for
telemetry. Store both as deployment secrets.

## Test

```sh
docker build --target build .
```

Architecture and roadmap documentation lives in the sibling `lab` repository.
