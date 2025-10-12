# RAKCHA Desktop - Download Website

This directory contains the GitHub Pages website for distributing RAKCHA Desktop installers.

## Structure

```
website/
├── index.html          # Main download page
├── releases/           # Distribution files
│   ├── rakcha_1.0.6_amd64.deb      # Linux Debian package
│   └── RAKCHA-portable.tar.gz       # Portable app archive
└── README.md          # This file
```

## Usage

### Setting up GitHub Pages

1. Push this website directory to your GitHub repository
2. Go to repository Settings > Pages
3. Set source to "Deploy from a branch"
4. Select the branch containing the website directory
5. Set folder to `/website` (or move contents to `/docs` if preferred)

### Updating Releases

When new versions are built:

1. Update the version number in `index.html`
2. Replace files in the `releases/` directory
3. Update file sizes in the HTML if they change significantly
4. Commit and push changes

### Local Testing

You can test the website locally using any static file server:

```bash
# Using Python's built-in server
cd website
python3 -m http.server 8000

# Using Node.js serve
npx serve .

# Using PHP's built-in server
php -S localhost:8000
```

Then visit `http://localhost:8000` to preview the site.

## Features

- **Responsive Design**: Works on desktop and mobile devices
- **Modern UI**: Clean, professional appearance with animations
- **Download Tracking**: Ready for analytics integration
- **SEO Optimized**: Proper meta tags and semantic HTML
- **Accessibility**: Proper heading structure and ARIA labels

## Customization

### Updating Content

- **Logo/Branding**: Modify the header section in `index.html`
- **Features**: Update the features grid with your app's capabilities
- **System Requirements**: Adjust based on your app's needs
- **Download Links**: Update URLs in the download buttons

### Styling

- **Colors**: Modify CSS custom properties at the top of the `<style>` section
- **Fonts**: Change the Google Fonts import and font-family declarations
- **Layout**: Adjust grid configurations and spacing

### Analytics

To add Google Analytics:

1. Add your GA tracking code before the closing `</head>` tag
2. Uncomment the `gtag` tracking in the download button event listeners

## File Management

### Large Files

GitHub has file size limits. For large installers (>100MB):

1. Consider using GitHub Releases instead
2. Use Git LFS for large files
3. Host files on external CDN and update links

### Version Management

- Use semantic versioning (e.g., v1.0.6)
- Keep previous versions available for compatibility
- Update download links when releasing new versions

## Security

- Provide checksums (SHA256) for integrity verification
- Consider code signing for executables
- Use HTTPS for all download links

## License

This website template is provided as-is for the RAKCHA Desktop project.