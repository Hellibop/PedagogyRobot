Adopting a consistent and clear naming convention for Git branches, tickets, and merges is crucial for maintaining an organized and efficient workflow. Below are some widely accepted standards and best practices:

---

# Git Branch Naming Conventions
A good branch name should clearly describe its purpose. Here are some common patterns:

### Format:
`<type>/<issue-id>-<short-description>`


### Examples:
- `feature/PROJ-123-add-user-authentication`
- `bugfix/PROJ-456-fix-login-error`
- `hotfix/PROJ-789-critical-security-patch`
- `chore/PROJ-101-update-dependencies`
- `docs/PROJ-202-update-readme`

### Common Branch Types:
- **feature/**: For new features or functionality.
- **bugfix/**: For fixing bugs.
- **hotfix/**: For urgent fixes, typically in production.
- **chore/**: For maintenance tasks (e.g., updating dependencies).
- **docs/**: For documentation updates.
- **refactor/**: For code refactoring.
- **test/**: For adding or updating tests.


### Tips:
- Use **lowercase letters** and **hyphens (`-`)** for readability.
- Include a **ticket ID** (e.g., Jira ticket number) if applicable.
- Keep the **description concise but meaningful**.

---

# Ticket Naming Conventions

Tickets in Trello should follow a clear and consistent format to ensure easy understanding and prioritization.

### Format:
`<type>: <short-description>`
### Examples:
- `feature/PROJ-123-add-user-authentication`
- `bugfix/PROJ-456-fix-login-error`
- `hotfix/PROJ-789-critical-security-patch`
- `chore/PROJ-101-update-dependencies`
- `docs/PROJ-202-update-readme`

### Tips:
- Use **lowercase letters** and **hyphens (`-`)** for readability.
- Include a **ticket ID** (e.g., Jira ticket number) if applicable.
- Keep the **description concise but meaningful**.

---

# Ticket Naming Conventions

Tickets in Trello should follow a clear and consistent format to ensure easy understanding and prioritization.

### Format:
`<type>: <short-description>`

### Examples:
- **Feature**: Add user authentication
- **Bug**: Fix login error on mobile devices
- **Chore**: Update dependencies to latest versions
- **Refactor**: Improve performance of search API

### Common Ticket Types:
- **Feature**: For new functionality.
- **Bug**: For issues or defects.
- **Chore**: For maintenance tasks.
- **Refactor**: For code improvements.
- **Spike**: For research or exploration tasks.

### Tips:
- Use **clear and actionable language**.
- Include **context or user stories** if necessary.
- Reference **related tickets or dependencies**.

---

# Merge/Pull Request Naming Conventions

Merge or pull request (PR) titles should summarize the changes and provide context.

### Format:
`<type>: <short-description>`

### Examples:
- **Feature**: Add user authentication
- **Bugfix**: Resolve login error on mobile devices
- **Chore**: Update dependencies
- **Refactor**: Improve search API performance

### Important:
- **Reference the ticket ID** in the PR title or description (e.g., `Closes PROJ-123`).
- Use the PR description to provide details, such as:
  - What changes were made?
  - Why were they made?
  - Any relevant screenshots, links, or testing instructions.

---

# Commit Message Conventions

Commit messages should be clear and follow a consistent format.

### Format:
`<type>: <short-description>`

### Examples:
- `feat: add user authentication`
- `fix: resolve login error`
- `chore: update dependencies`
- `docs: update README`

### Common Commit Types:
- **feat**: For new features.
- **fix**: For bug fixes.
- **chore**: For maintenance tasks.
- **docs**: For documentation changes.
- **refactor**: For code refactoring.
- **test**: For test-related changes.

### Tips:
- Keep the **first line under 50 characters**.
- Add a **detailed description** in the body if necessary.
