# Atlas

The Java/Spring Boot search service used by Vaultsh. Backend Lab owns its
[architecture and roadmap](https://github.com/mateoRc/lab/tree/main/content/docs).

## Develop

Run the integrated stack:

```sh
cd ../lab
docker compose up --build
```

For direct API development, run Atlas on port 8081 and provide:

- `ATLAS_CONTENT_ROOT`: Markdown root; default `/app/content`
- `ATLAS_AUTH_TOKEN`: required search bearer token
- `FORGE_URL` and `FORGE_AUTH_TOKEN`: optional telemetry integration
- `TELEMETRY_QUEUE_SIZE`: bounded telemetry queue capacity; default `1000`

## Test

```sh
docker build --target build .
```
