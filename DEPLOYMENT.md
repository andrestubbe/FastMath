# Maven Central Deployment Guide

This guide covers how to release FastMath to Maven Central.

## Prerequisites

Before deploying, ensure you have:

1. **OSSRH Account** - Registered at https://central.sonatype.com/
2. **GPG Key** - For signing artifacts
3. **Repository Access** - Your user must be authorized for groupId `io.github.andrestubbe`

## 1. Setup GPG

### Windows (Gpg4win)
```bash
# Download from https://www.gpg4win.org/
# Create key
gpg --full-generate-key

# List keys
gpg --list-keys

# Distribute public key
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### macOS
```bash
brew install gnupg
# Same commands as Windows
```

### Configure Maven Settings (`~/.m2/settings.xml`)
```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>${env.MAVEN_USERNAME}</username>
      <password>${env.MAVEN_PASSWORD}</password>
    </server>
  </servers>
  
  <profiles>
    <profile>
      <id>gpg</id>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.keyname>YOUR_KEY_ID</gpg.keyname>
        <gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

## 2. Prepare Release

### Pre-flight Checklist
- [ ] All tests pass
- [ ] Benchmarks updated
- [ ] README.md reflects latest features
- [ ] CHANGELOG.md updated
- [ ] Version is correct in pom.xml
- [ ] Native libs compiled for all platforms (Windows, Linux, macOS)

### Run Tests
```bash
mvn clean test
```

### Build Distribution
```bash
# Compile native library (Windows)
compile.bat

# Build full package
mvn clean package
```

## 3. Deploy to Maven Central

### Option A: Automated (with profiles)
```bash
# Set environment variables
set MAVEN_USERNAME=your-sonatype-username
set MAVEN_PASSWORD=your-sonatype-password
set GPG_PASSPHRASE=your-gpg-passphrase

# Deploy
mvn clean deploy -P release,gpg
```

### Option B: Manual Staging
```bash
# Stage release
mvn clean deploy -DskipTests

# Login to https://s01.oss.sonatype.org/
# Close and release staging repository manually
```

### Option C: Maven Release Plugin
```bash
# Prepare release (updates versions, creates tag)
mvn release:prepare -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.1.0-SNAPSHOT

# Perform release (deploys to Maven Central)
mvn release:perform
```

## 4. Verify Deployment

### Check Maven Central Search
- https://search.maven.org/artifact/io.github.andrestubbe/fastmath
- May take 10-30 minutes to appear

### Test Dependency Resolution
```xml
<dependency>
    <groupId>io.github.andrestubbe</groupId>
    <artifactId>fastmath</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 5. Post-Release Tasks

### GitHub Release
1. Create release at https://github.com/andrestubbe/fastmath/releases
2. Add release notes from CHANGELOG.md
3. Upload native libs as release assets:
   - `fastmath.dll` (Windows)
   - `libfastmath.so` (Linux)
   - `libfastmath.dylib` (macOS)

### Announcements
- Post to relevant subreddits (r/java, r/programming)
- Share on Twitter/X
- Submit to Hacker News "Show HN"
- Update FastJava.dev website

## Troubleshooting

### GPG Signing Fails
```bash
# Test GPG manually
gpg --sign --armor target/fastmath-1.0.0.jar

# Check key is on keyserver
gpg --keyserver keyserver.ubuntu.com --recv-keys YOUR_KEY_ID
```

### Staging Rules Violate
```bash
# Run checks manually
mvn org.sonatype.plugins:nexus-staging-maven-plugin:rc-list
mvn org.sonatype.plugins:nexus-staging-maven-plugin:rc-close -DstagingRepositoryId=comgithubandrestubbe-XXXX
mvn org.sonatype.plugins:nexus-staging-maven-plugin:rc-release -DstagingRepositoryId=comgithubandrestubbe-XXXX
```

### Version Already Exists
```bash
# Check if artifact exists
curl https://repo1.maven.org/maven2/io/github/andrestubbe/fastmath/1.0.0/

# If exists, use new version
mvn versions:set -DnewVersion=1.0.1
```

## Release Checklist

- [ ] GPG key created and published
- [ ] Sonatype account configured
- [ ] Maven settings.xml configured
- [ ] All tests passing
- [ ] Native libs compiled
- [ ] mvn clean deploy executed
- [ ] Staging repository closed and released
- [ ] GitHub release created
- [ ] Release notes published
- [ ] Social media announcement

## References

- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Setup Guide](https://central.sonatype.org/publish/requirements/gpg/)
- [Nexus Staging Plugin](https://help.sonatype.com/repomanager2/staging-releases/staging-overview)
