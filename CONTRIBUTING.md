# Contributing to TaskForge

Thanks for considering a contribution! This is primarily a portfolio/learning project, but issues and PRs are welcome.

## Getting set up

See the [README](README.md#running-in-vs-code-step-by-step) for full local setup instructions.

## Workflow

1. Fork the repo and create a branch from `main`: `git checkout -b feature/your-feature-name`
2. Make your changes. Keep commits focused and messages descriptive.
3. Run the test suite before opening a PR:
   ```bash
   cd backend && mvn test
   cd frontend && npm test -- --watchAll=false
   ```
4. Push your branch and open a pull request describing what changed and why.

## Code style

- **Backend**: standard Java conventions, Lombok for boilerplate, records for DTOs. Keep controllers thin — business logic belongs in the service layer.
- **Frontend**: functional components + hooks. Keep API calls in `src/services/api.js`, not scattered in components.

## Reporting bugs / requesting features

Open a GitHub issue with steps to reproduce (for bugs) or a clear description of the desired behavior (for features).
