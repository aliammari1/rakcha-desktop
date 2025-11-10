# RAKCHA JPackage Setup Guide

This directory contains packaging configuration for creating native installers using JPackage.

## Directory Structure

```
src/packaging/
├── RAKCHA.properties       # Common launcher properties
├── windows/
│   ├── RAKCHA.ico         # Windows application icon
│   └── RAKCHA-launcher.bat # Windows launcher script
├── linux/
│   ├── RAKCHA.png         # Linux application icon
│   └── RAKCHA-launcher.sh  # Linux launcher script
└── macos/
    ├── RAKCHA.icns        # macOS application icon
    ├── entitlements.plist  # macOS security entitlements
    └── RAKCHA-launcher.sh  # macOS launcher script
```

## Icon Requirements

### Windows (.ico)
- Format: ICO
- Recommended sizes: 16x16, 32x32, 48x48, 64x64, 128x128, 256x256
- Current: Copy `src/main/resources/Logo.ico` to `src/packaging/windows/RAKCHA.ico`

### Linux (.png)
- Format: PNG
- Recommended size: 256x256 or 512x512
- Current: Copy `src/main/resources/Logo.png` to `src/packaging/linux/RAKCHA.png`

### macOS (.icns)
- Format: ICNS (Apple Icon Image)
- Required sizes: 16x16, 32x32, 64x64, 128x128, 256x256, 512x512, 1024x1024
- Create using: `png2icns RAKCHA.icns Logo.png` (requires imagemagick)
- Or use online converter: https://cloudconvert.com/png-to-icns

## Building Native Installers

### Prerequisites
1. Java 21 JDK installed
2. Maven 3.6+ installed
3. Platform-specific tools:
   - **Windows**: WiX Toolset 3.x for MSI installers
   - **macOS**: Xcode command line tools
   - **Linux**: RPM tools or DEB tools

### Build Steps

1. **Clean and compile the project**:
   ```bash
   mvn clean compile
   ```

2. **Package the application**:
   ```bash
   mvn clean package
   ```

3. **Create native installer**:
   ```bash
   mvn jpackage:jpackage
   ```

   Or combine all steps:
   ```bash
   mvn clean install
   ```

### Output Location

The native installer will be created in:
```
target/dist/
├── RAKCHA-1.0.8.msi       # Windows installer
├── RAKCHA-1.0.8.exe       # Windows executable installer
├── RAKCHA-1.0.8.deb       # Debian/Ubuntu installer
├── RAKCHA-1.0.8.rpm       # RedHat/Fedora installer
├── RAKCHA-1.0.8.dmg       # macOS disk image
└── RAKCHA-1.0.8.pkg       # macOS package installer
```

## Configuration Details

### JVM Options
The following JVM options are configured for optimal performance:

- **Memory**: 256MB initial, 1024MB maximum
- **Encoding**: UTF-8 for file and character encoding
- **JavaFX Access**: Module opens for third-party library compatibility
- **Logging**: Logback configuration from classpath

### Platform-Specific Settings

#### Windows
- Desktop shortcut: Yes
- Start menu: Yes (under "RAKCHA" folder)
- Directory chooser: Yes
- Per-user install: No (system-wide)
- Upgrade UUID: Configured for proper upgrades

#### Linux
- Desktop shortcut: Yes
- Menu group: AudioVideo
- Package name: rakcha
- Categories: Video, AudioVideo, Player
- Supported formats: DEB, RPM

#### macOS
- Bundle identifier: tn.esprit.rakcha
- App Store category: Entertainment
- Entitlements: Network, camera, audio, file access
- Signing: Optional (configure for distribution)

## Customization

### Adding File Associations
Edit `pom.xml` to add file associations:
```xml
<fileassociations>
  <fileassociation>
    <extension>rakcha</extension>
    <mimetype>application/x-rakcha</mimetype>
    <description>RAKCHA Project File</description>
  </fileassociation>
</fileassociations>
```

### Changing JVM Memory
Edit the `<javaoptions>` section in `pom.xml`:
```xml
<javaoption>-Xms512m</javaoption>  <!-- Initial heap -->
<javaoption>-Xmx2048m</javaoption> <!-- Maximum heap -->
```

### Code Signing (macOS)
Uncomment and configure in `pom.xml`:
```xml
<macsign>true</macsign>
<macsigningkeyusername>Developer ID Application: ESPRIT</macsigningkeyusername>
```

## Troubleshooting

### Icon Not Showing
- Ensure icon files exist in `src/packaging/[platform]/` directories
- Verify icon format and sizes
- Check Maven build output for warnings

### Installer Creation Failed
- Verify platform-specific tools are installed (WiX, RPM, etc.)
- Check Java version (must be JDK 21)
- Review Maven output for specific errors

### Application Won't Start
- Check JVM options are correct
- Verify main class: `com.esprit.MainApp`
- Ensure all dependencies are included
- Review application logs

### Missing Dependencies
- Run `mvn dependency:tree` to verify all dependencies
- Check `target/classpath-jars/` for all required JARs
- Ensure JavaFX platform-specific JARs are included

## Testing the Installer

1. Install the generated package
2. Run the application from the installed location
3. Verify all features work correctly
4. Test on clean system (virtual machine recommended)
5. Check for proper uninstallation

## Distribution

After successful testing:

1. **Windows**: Distribute `.msi` or `.exe` file
2. **macOS**: Distribute `.dmg` or `.pkg` file (signed for Gatekeeper)
3. **Linux**: Distribute `.deb` or `.rpm` file (or both)

## Additional Resources

- [JPackage Documentation](https://docs.oracle.com/en/java/javase/21/jpackage/)
- [Maven JPackage Plugin](https://github.com/akman/jpackage-maven-plugin)
- [JavaFX Packaging](https://openjfx.io/openjfx-docs/#install-javafx)
