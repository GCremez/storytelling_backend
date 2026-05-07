# Git Branching Strategy

This project uses a **GitHub Flow** model with a `develop` integration branch. Lightweight, practical, and CI/CD-friendly.

## Branch Model

```
main (production) ───┐
                     │  merge (PR with approval)
develop (integration)┤
                     │  merge (PR)
feature/* ───────────┘
```

### Branch Rules

| Branch | Purpose | Protection | Merge Strategy |
|---|---|---|---|
| `main` | Production-ready code only | Protected, no direct pushes | Squash merge from develop |
| `develop` | Integration branch for all features | Protected, no direct pushes | Squash merge from feature/* |
| `feature/*` | Individual features or improvements | Developer owns | Squash merge to develop |
| `fix/*` | Non-urgent bug fixes | Developer owns | Squash merge to develop |
| `hotfix/*` | Urgent production fixes | Developer owns | Merge to main + develop |

## Workflow

### 1. Start a new feature
```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

### 2. Work and commit
```bash
# Use conventional commits
git commit -m "feat: add email verification resend with rate limiting"
git commit -m "fix: resolve N+1 query in session choice history"
git commit -m "refactor: extract password validation to separate service"
```

### 3. Push and create PR
```bash
git push -u origin feature/your-feature-name
# Create PR: feature/* → develop
```

### 4. Merge to develop
- PR requires 1 approval (if team > 1)
- CI must pass (lint, test, build)
- Squash merge to keep develop history clean

### 5. Release to production
- When develop is stable, PR: develop → main
- Tag the release: `git tag v0.1.0 && git push origin v0.1.0`

## Commit Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Types
| Type | When to use |
|---|---|
| `feat` | New user-facing feature |
| `fix` | Bug fix |
| `refactor` | Code change that is neither feature nor fix |
| `chore` | Build config, tooling, dependencies |
| `docs` | Documentation changes |
| `test` | Test additions or modifications |
| `perf` | Performance improvement |
| `ci` | CI/CD pipeline changes |

### Examples
```
feat(api): add publish story endpoint
fix(auth): use refreshExpiration for refresh tokens
refactor: remove dead code and unused imports
chore(config): harden production configuration
test(auth): add AuthService unit tests
perf(db): fix N+1 query in choice history
```

## Naming Conventions

- **Feature branches**: `feature/add-oauth-login`, `feature/user-profile-page`
- **Fix branches**: `fix/null-pointer-in-chapter-service`, `fix/jwt-validation`
- **Hotfix branches**: `hotfix/production-db-connection`

Use kebab-case, be descriptive but concise.

## Cleanup

Delete merged branches:
```bash
git branch -d feature/merged-feature
git push origin --delete feature/merged-feature
```

## Current Branches

| Branch | Status | Description |
|---|---|---|
| `main` | Stable | Production code |
| `develop` | Active | Integration branch (8 bug/security fixes merged) |
| `feature/add-tests` | Available | Add comprehensive test suite |
| `feature/api-improvements` | Available | REST API enhancements |
| `feature/security-hardening` | Available | Additional security work |
