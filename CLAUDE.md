# CLAUDE.md

## smart-doc issue triage & fix workflow

### Ground rules

- **Latest source only.** smart-doc does not backport fixes or investigate old releases.
  Every issue is evaluated against the current `master` source of this repository.
- **Never bump versions** for a bug fix. The `pom.xml` version is managed by the release
  process.
- **The plugin is a separate project.** Only touch
  [smart-doc-maven-plugin](../../smart-doc-maven-plugin) when the fix itself requires
  plugin code changes. A core fix never needs a plugin rebuild.
- **No old-version compatibility work.** If a reporter is on an old version, the answer
  is to upgrade — do not try to make the fix work on that version.
- **English only in this community project.** Commit messages, PR titles and
  descriptions, code comments, Javadoc, and documentation must be written in English.

### Step 0 — Prepare the environment

1. Make sure both local source checkouts are current:

   ```bash
   # smart-doc core
   git pull
   mvn install -DskipTests

   # plugin (only update the checkout; no rebuild needed for a core fix)
   cd ../smart-doc-maven-plugin && git pull
   ```

2. Use the official test project for end-to-end verification. Clone it next to the
   repositories if it is missing:

   ```bash
   git clone https://github.com/smart-doc-group/smart-doc-example-cn.git \
     ../smart-doc-example-cn
   ```

   **Do not change the smart-doc or plugin version in the example project.** Community
   releases update it automatically; overriding it defeats the purpose of testing the
   locally installed build.

### Step 1 — Reproduce in the example project

Write a small, focused reproduction in `smart-doc-example-cn` — a controller, DTO, enum,
or JUnit test that mirrors the issue:

```bash
cd ../smart-doc-example-cn
# focused test (preferred)
mvn -Dtest=IssueXxxTest test
# or the actual plugin goal, e.g.
mvn smart-doc:torna-rest -Dfile.encoding=UTF-8
```

- Prefer a focused JUnit test over a full plugin run: plugin goals bind to the `compile`
  phase and can fail for unrelated reasons.
- Do not modify smart-doc core just to "protect" a case that has not been reproduced.

### Step 2 — If it does not reproduce: stop

- The issue is **closed as not reproducible** (or answered with "please upgrade to the
  latest version").
- Do not investigate old versions, do not guess at a fix, and do not add defensive code
  for an unconfirmed scenario.
- Ask the reporter for the missing context only if it is cheap to obtain (exact version,
  full stack trace, minimal project); otherwise close.

### Step 3 — If it reproduces: fix smart-doc core

1. Change the source in this repository. Do not change the version.
2. Install the current build into the local Maven repository:

   ```bash
   mvn install -DskipTests
   ```

   A plain `install` is all that is needed — the plugin resolves the freshly installed
   artifact on its next run.

3. Re-run the reproduction in `smart-doc-example-cn`. No other build or dependency change
   is required.
4. Run the relevant unit tests in this repository as well (`mvn test`, or a focused
   `-Dtest=...`).

### Step 4 — Verify and submit

- The fix is ready when the reproduction passes and the existing test suite still passes.
- Commit the smart-doc change and open a PR against the latest `master`.
- **Keep the reproduction test case in `smart-doc-example-cn`** — do not delete it. Commit
  the test/controller/DTO/enum additions and open a PR in the example project as well, so
  the regression stays covered.
- Do not include version bumps, old-version compatibility shims, or unrelated refactors in
  either PR.
