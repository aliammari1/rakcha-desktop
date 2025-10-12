# Release Files

This directory will be automatically populated by GitHub Actions with the latest installer files.

## Available Downloads

The GitHub Actions workflow will build and place the following files here:

- `rakcha_*_amd64.deb` - Debian/Ubuntu package installer
- `RAKCHA-portable.tar.gz` - Portable application archive

## Automatic Builds

Release files are automatically generated when:
- Code is pushed to the main branch
- A new tag is created
- The workflow is manually triggered

The files are built using jpackage with the following configurations:
- **Linux .deb package**: Native installer with dependency resolution
- **Linux app-image**: Portable application bundled with Java runtime