# Conveyor Setup Guide for RAKCHA Desktop

## Overview

This guide explains the best practices for setting up Conveyor in your CI/CD pipeline, based on official Conveyor documentation.

## Installation Methods

### Method 1: Official GitHub Action (Recommended for Simple Setups)

The easiest way to use Conveyor in GitHub Actions is with the official action:

```yaml
- name: Run Conveyor
  uses: hydraulic-software/conveyor/actions/build@v20.0
  with:
    command: make site
    signing_key: ${{ secrets.SIGNING_KEY }}
    agree_to_license: 1
```

**Pros:**
- Simplest setup
- Handles caching automatically
- Official support

**Cons:**
- Less control over the process
- May not fit all custom workflows

### Method 2: Manual Installation with Caching (Current Implementation)

For more control, we manually install Conveyor:

```yaml
- name: Cache Conveyor installation
  uses: actions/cache@v4
  id: cache-conveyor
  with:
    path: |
      ~/.cache/hydraulic/conveyor
      ~/conveyor
    key: ${{ runner.os }}-conveyor-${{ env.CONVEYOR_VERSION }}

- name: Download and setup Conveyor
  if: steps.cache-conveyor.outputs.cache-hit != 'true'
  run: |
    curl -Lf https://downloads.hydraulic.dev/conveyor/conveyor-20.0-linux-amd64.tar.gz -o conveyor.tar.gz
    tar -xzf conveyor.tar.gz
    mkdir -p ~/conveyor
    find . -name "conveyor" -type f -executable -exec cp {} ~/conveyor/conveyor \;
    rm -rf conveyor.tar.gz conveyor-20.0-*
```

**Pros:**
- Full control over the build process
- Can integrate with custom workflows
- Explicit caching strategy

**Cons:**
- More verbose
- Requires manual maintenance

## Best Practices

### 1. Caching Strategy

**✅ DO:**
- Cache the Conveyor installation between builds
- Cache the Conveyor disk cache (`~/.cache/hydraulic/conveyor`)
- Use version-specific cache keys

**❌ DON'T:**
- Download Conveyor on every build (may get throttled)
- Ignore the disk cache (slows builds significantly)

### 2. Credentials Management

Create a separate `ci.conveyor.conf` file:

```hocon
include required("conveyor.conf")

app {
    signing-key = ${?SIGNING_KEY}
    
    mac.certificate = apple.cer
    windows.certificate = windows.cer
    
    mac.notarization {
        app-specific-password = ${?APPLE_ASP}
        team-id = "YOUR_TEAM_ID"
        apple-id = "your@email.com"
    }
}
```

Store sensitive data in GitHub Secrets:
- `SIGNING_KEY`: Your root signing key
- `APPLE_ASP`: Apple app-specific password
- Upload certificate files to your repository (they're not secrets)

### 3. Site URL Configuration

Override the site URL in CI:

```hocon
app.site.base-url = "https://username.github.io/repo-name"
```

### 4. Resource Requirements

Ensure your CI runners have:
- **Disk:** At least 11GB free (10GB cache + 1GB buffer)
- **RAM:** At least 32GB
- **CPU:** Multiple cores for parallel builds
- **Internet:** Required for downloading dependencies

If resources are limited, use:
```bash
conveyor make site --parallelism=2
```

### 5. Build Performance

**Speed up builds:**
- Preserve the disk cache between builds
- Use `--parallelism` flag appropriately
- Cache Maven/Gradle dependencies
- Use fast file systems (avoid Windows runners if possible)

### 6. Signing Key Management

**Options:**

1. **Generate temporary key** (Development/Testing):
   ```bash
   conveyor keys generate
   ```

2. **Use stored key** (Production):
   - Export your key: `conveyor keys export`
   - Store in GitHub Secrets
   - Reference via environment variable

3. **Use HSM/Cloud Signing** (Enterprise):
   - SignPath
   - Azure Trusted Signing
   - AWS CloudHSM

### 7. Update Modes

Configure in `conveyor.conf`:

```hocon
app.updates = aggressive  // Check on every launch (recommended for active development)
app.updates = background  // Chrome-style silent updates
app.updates = none        // Disable auto-updates
```

## Troubleshooting

### Build Fails: "Conveyor binary not found"

The tarball structure may vary. Use `find` to locate the binary:

```bash
tar -tzf conveyor.tar.gz  # List archive contents
find . -name "conveyor" -type f -executable  # Find the binary
```

### Disk Space Issues

Reduce cache size:
```bash
conveyor --cache-size=5GB make site
```

### Signing Errors

Check:
1. `SIGNING_KEY` secret is set
2. Certificate files are in the correct location
3. Paths in `ci.conveyor.conf` are correct

### macOS Notarization Fails

Verify:
1. Apple ID credentials are correct
2. App-specific password is valid
3. Team ID matches your developer account

## Migration Path

If you want to switch to the official GitHub Action:

1. Remove manual installation steps
2. Replace with:
   ```yaml
   - uses: hydraulic-software/conveyor/actions/build@v20.0
     with:
       command: make site
       signing_key: ${{ secrets.SIGNING_KEY }}
       extra_flags: -f ci.conveyor.conf
       agree_to_license: 1
   ```

## References

- [Official Conveyor Documentation](https://conveyor.hydraulic.dev/20.0/)
- [CI/CD Guide](https://conveyor.hydraulic.dev/20.0/continuous-integration/)
- [GitHub Actions Guide](https://conveyor.hydraulic.dev/20.0/continuous-integration/#using-github-actions)
- [JVM Configuration](https://conveyor.hydraulic.dev/20.0/configs/jvm/)
- [Signing Keys](https://conveyor.hydraulic.dev/20.0/configs/keys-and-certificates/)

## Current Workflow Status

Our current implementation uses **Method 2** (Manual Installation with Caching) because it provides:
- ✅ Better integration with our Maven build
- ✅ Explicit control over caching
- ✅ Easier debugging
- ✅ Custom build steps

The workflow:
1. Builds the JAR with Maven
2. Caches Conveyor installation
3. Downloads Conveyor (if not cached)
4. Creates CI-specific configuration
5. Builds packages with Conveyor
6. Deploys to GitHub Pages

## Next Steps

1. ✅ Add `SIGNING_KEY` secret to repository
2. ⚠️ Optional: Add Apple certificates for macOS notarization
3. ⚠️ Optional: Add Windows certificate for code signing
4. ✅ Test the workflow on a feature branch
5. ✅ Deploy to production
