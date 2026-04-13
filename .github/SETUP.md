# GitHub Setup Guide

This document guides maintainers on setting up the CBPS Engine GitHub repository.

## 1. Repository Configuration

### Settings

1. **Settings → General**
   - Description: "Lightweight Java game engine with ECS, OpenGL 4.1, and LWJGL"
   - Website: (optional) https://github.com/comboom-sucht/cbps-engine
   - Topics: `game-engine`, `java`, `lwjgl`, `ecs`, `opengl`

2. **Settings → Code security & analysis**
   - Enable: "Dependabot alerts"
   - Enable: "Dependabot security updates"
   - Enable: "Secret scanning"

3. **Settings → Branch protection rules** (for `main`)
   - Require status checks before merging
   - Include:
     - `build` (CI/CD)
     - `tests` (CI/CD)
   - Require branches to be up to date

### Branch Rules

```
main        → Production releases (tagged)
develop     → Development, tested features
feature/*   → Feature branches (short-lived)
bugfix/*    → Bug fix branches
```

## 2. Enable GitHub Wiki

1. **Settings → Features**
   - ✅ Enable "Wikis"
   - Default wiki homepage: `Home.md`

2. **Wiki → Home**
   - Add sidebar with navigation
   - Link to Getting Started, API Reference, Examples, etc.

3. **Import wiki content** from `docs/wiki/*.md`:
   ```bash
   cd cbps-engine.wiki
   git add .
   git commit -m "Initial wiki pages"
   git push
   ```

## 3. Setup Releases

1. **Releases → Create a new release**
   - Tag: `v0.1.0-alpha`
   - Title: `CBPS Engine 0.1.0-alpha`
   - Description: (auto-generated from CHANGELOG)
   - Upload: `target/CBPSEngine-full.jar`

2. **GitHub Actions** automatically creates releases on tag push:
   - Trigger: `push` tags matching `v*`
   - Runs: `.github/workflows/release.yml`
   - Creates release, uploads JAR

## 4. Enable CI/CD

1. **Actions → General**
   - Allow: All actions and reusable workflows
   - Fork pull request workflows: ✅ Run workflows from fork pull requests

2. **Workflows** in `.github/workflows/`:
   - `build.yml` – Tests on push/PR (Ubuntu, macOS, Windows)
   - `release.yml` – Builds release on tag push

3. **View results**: Actions tab → Workflows

## 5. Configure Discussions (Optional)

1. **Settings → Features**
   - ✅ Enable "Discussions"
   - Welcome message: "Ask questions, share ideas, discuss game dev!"

2. **Discussion categories**:
   - Announcements
   - General
   - Q&A
   - Show & Tell

## 6. Enable Issues & Project

1. **Settings → Features**
   - ✅ Enable "Issues"
   - ✅ Enable "Projects"

2. **Create Project**:
   - Type: Table (or Board)
   - Views:
     - Backlog
     - In Progress
     - In Review
     - Done
   - Auto-add issues with labels

## 7. License & Legal

1. **LICENSE** file ✅ (already added)
   - MIT License from "comboom.sucht"
   - GitHub auto-detects and shows badge

2. **Contributing**
   - `.github/CONTRIBUTING.md` ✅ (already added)
   - GitHub shows on PR creation

## 8. Security Setup

1. **Security → Code scanning with GitHub Advanced Security**
   - CodeQL: Enabled automatically
   - Alerts: Review and address

2. **Dependabot**
   - Creates PRs for dependency updates
   - Configure in `.github/dependabot.yml` (optional)

## 9. Example Dependabot Config

Optional: Create `.github/dependabot.yml`:

```yaml
version: 2
updates:
  - package-ecosystem: maven
    directory: "/"
    schedule:
      interval: weekly
    open-pull-requests-limit: 5
```

## 10. Releases on Maven Central (Future)

When ready to publish to Maven Central:

1. Create account at https://oss.sonatype.org
2. Add to `pom.xml`:
   ```xml
   <distributionManagement>
       <repository>
           <id>ossrh</id>
           <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
       </repository>
       <snapshotRepository>
           <id>ossrh</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
       </snapshotRepository>
   </distributionManagement>
   ```
3. Add GitHub Secrets:
   - `OSSRH_USERNAME`
   - `OSSRH_PASSWORD`
   - `GPG_PASSPHRASE`
4. Update workflows to call `mvn deploy`

## 11. Badges for README

Add to `README.md` shields:

```markdown
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![GitHub Workflow](https://img.shields.io/github/actions/workflow/status/comboom-sucht/cbps-engine/build.yml)
![Latest Release](https://img.shields.io/github/v/release/comboom-sucht/cbps-engine)
![Download](https://img.shields.io/github/downloads/comboom-sucht/cbps-engine/total)
```

## 12. Repository Secrets

1. **Settings → Security → Secrets and variables → Actions**
   - No secrets needed for public repo, but add if:
     - Publishing to Maven Central
     - Deploying to GitHub Pages
     - Publishing to artifact registry

## Final Checklist

- [ ] Repository description & topics set
- [ ] Branch protection on `main`
- [ ] Wiki enabled with content imported
- [ ] CI/CD workflows enabled & passing
- [ ] First release created (v0.1.0-alpha)
- [ ] LICENSE file present & recognized
- [ ] README with quick start & badges
- [ ] CONTRIBUTING.md visible on PR creation
- [ ] Issues & Discussions enabled
- [ ] Code of Conduct (optional: .github/CODE_OF_CONDUCT.md)
- [ ] Security scanning enabled

---

**Repository Ready for Public!** 🚀

Next steps:
- Share with community
- Gather feedback via Issues/Discussions
- Plan v0.2.0 with mesh loading, textures, audio
