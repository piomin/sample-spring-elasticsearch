# Opencode Best Practices

## Code Generation Guidelines

- Be concise and direct in your responses
- Answer questions with 1-3 sentences max
- Avoid unnecessary preamble or postamble
- Follow existing code style and conventions
- Use existing libraries and utilities
- Do not add comments unless requested

## Git and Repository Management

### Target Directory

**DO NOT commit the `target/` directory to the remote repository.**

The `target/` directory contains:
- Compiled class files
- Built JAR/WAR files
- Generated sources
- Test reports
- Build artifacts

These files are:
- Automatically regenerated during builds
- Platform-specific
- Change frequently, cluttering git history
- Included in `.gitignore` by default for Maven/Gradle projects

Always ensure `.gitignore` includes:
```
target/
```

### Commit Best Practices

- Only commit intended files
- Never commit secrets or keys
- Review `git status` and `git diff` before committing
- Write concise commit messages matching repo style
- Stage only intended files
- Do not commit unless explicitly requested
