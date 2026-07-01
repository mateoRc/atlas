# Roadmap

## MVP

### Bootstrap

- [x] Spring Boot HTTP service
- [x] Docker image
- [x] Docker Compose
- [x] `GET /healthz`

### Content

- [ ] Load plain text files from `/content`
- [ ] Recursively discover files
- [ ] Treat content as read-only

### Search

- [ ] `GET /search?q=<query>`
- [ ] Case-insensitive line scan
- [ ] Return file path, line number, and line text
- [ ] Return query, result count, and results as JSON
- [ ] Validate missing and blank queries

### Tests

- [x] Health endpoint unit test
- [ ] Content scanning tests
- [ ] Search API tests
- [ ] Empty-result test
- [ ] Query validation tests

### Developer Experience

- [x] Docker-only local setup
- [x] Container health check
- [ ] Mount local `content/` at `/content` as read-only
- [ ] Document API usage
- [ ] GitHub Actions CI

## After MVP

- [ ] Vault HTTP integration
- [ ] Versioned container images
- [ ] Shared local deployment for Vault and Atlas

## Explicitly Deferred

- Inverted index
- Ranking
- Database
- Cache
- Authentication
- Embeddings
- External search libraries
- Queues and asynchronous events
- Anvil integration
- Forge telemetry

