# Contributing to Cosmic Canvas

We love your input! We want to make contributing to Cosmic Canvas as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## Development Process

We use GitHub to host code, to track issues and feature requests, as well as accept pull requests.

### Pull Requests

1. Fork the repo and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. Ensure the test suite passes.
4. Make sure your code lints.
5. Issue that pull request!

### Versioning

For versioning, we follow [Semantic Versioning](https://semver.org/):

- MAJOR version for incompatible API changes
- MINOR version for backwards-compatible functionality
- PATCH version for backwards-compatible bug fixes

When submitting code changes, please include an appropriate version bump in the `app/build.gradle.kts` file:

```kotlin
versionCode = <number>
versionName = "<major>.<minor>.<patch>"
```

## Release Process

Our GitHub Actions workflows automatically:

1. Build the app when code is pushed to main
2. Create tags and releases based on version changes in build.gradle.kts
3. Include a changelog of commits in the release notes

## License

By contributing, you agree that your contributions will be licensed under the project's MIT License.