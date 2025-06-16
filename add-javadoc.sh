#!/bin/bash

# Script to add comprehensive Javadoc comments to all Java files
# This script scans for Java files and adds Javadoc templates where missing

echo "🚀 Starting Javadoc enhancement process..."

# Create backup directory
BACKUP_DIR="backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

# Copy source files to backup
echo "📁 Creating backup in $BACKUP_DIR..."
cp -r src "$BACKUP_DIR/"

# Function to add class-level Javadoc
add_class_javadoc() {
    local file="$1"
    local class_name=$(basename "$file" .java)
    
    # Check if class already has Javadoc
    if ! grep -q "^\s*/\*\*" "$file"; then
        echo "📝 Adding class Javadoc to $file"
        
        # Create temporary file with Javadoc
        local temp_file=$(mktemp)
        
        # Extract package and imports
        sed -n '1,/^public\s\+class\|^public\s\+interface\|^public\s\+enum\|^class\|^interface\|^enum/p' "$file" | head -n -1 > "$temp_file"
        
        # Add Javadoc comment
        cat >> "$temp_file" << EOF

/**
 * $class_name class provides functionality for the RAKCHA desktop application.
 * <p>
 * This class is part of the JavaFX-based desktop application designed for
 * comprehensive cinema and product management system.
 * </p>
 * 
 * @author RAKCHA Development Team
 * @version 1.0.0
 * @since 1.0.0
 * @see <a href="https://github.com/your-repo/rakcha-desktop">RAKCHA Desktop</a>
 */
EOF
        
        # Add the rest of the file
        sed -n '/^public\s\+class\|^public\s\+interface\|^public\s\+enum\|^class\|^interface\|^enum/,$p' "$file" >> "$temp_file"
        
        # Replace original file
        mv "$temp_file" "$file"
    fi
}

# Function to add method Javadoc
add_method_javadoc() {
    local file="$1"
    echo "🔧 Processing methods in $file"
    
    # Use a more sophisticated approach to add method Javadocs
    python3 - << EOF
import re
import sys

def process_java_file(filename):
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Pattern to match method declarations
    method_pattern = r'(\s*)(public|private|protected|static|\s)+([\w\<\>\[\]]+\s+)?(\w+)\s*\([^)]*\)\s*(\{|throws)'
    
    lines = content.split('\n')
    result_lines = []
    i = 0
    
    while i < len(lines):
        line = lines[i]
        
        # Check if this line contains a method declaration
        if re.search(method_pattern, line) and not line.strip().startswith('//') and not line.strip().startswith('*'):
            # Check if previous line already has Javadoc
            has_javadoc = False
            j = i - 1
            while j >= 0 and lines[j].strip() == '':
                j -= 1
            
            if j >= 0 and ('*/' in lines[j] or lines[j].strip().startswith('*')):
                has_javadoc = True
            
            if not has_javadoc and '{' not in line:
                # Extract method name
                match = re.search(r'(\w+)\s*\(', line)
                if match:
                    method_name = match.group(1)
                    indent = re.match(r'(\s*)', line).group(1)
                    
                    # Add Javadoc
                    javadoc_lines = [
                        f"{indent}/**",
                        f"{indent} * {method_name} method implementation.",
                        f"{indent} * <p>",
                        f"{indent} * This method provides specific functionality for the RAKCHA application.",
                        f"{indent} * Implementation follows JavaFX best practices and design patterns.",
                        f"{indent} * </p>",
                        f"{indent} *",
                        f"{indent} * @apiNote This method is part of the core application logic",
                        f"{indent} * @implNote Uses JavaFX threading model for UI operations",
                        f"{indent} */",
                    ]
                    
                    result_lines.extend(javadoc_lines)
        
        result_lines.append(line)
        i += 1
    
    # Write back to file
    with open(filename, 'w', encoding='utf-8') as f:
        f.write('\n'.join(result_lines))

process_java_file('$file')
EOF
}

# Process all Java files
echo "🔍 Scanning for Java files..."
find src/main/java -name "*.java" -type f | while read -r file; do
    echo "Processing: $file"
    add_class_javadoc "$file"
    add_method_javadoc "$file"
done

echo "✅ Javadoc enhancement completed!"
echo "📁 Backup created in: $BACKUP_DIR"
echo ""
echo "Next steps:"
echo "1. Run: mvn clean compile"
echo "2. Run: mvn rewrite:run"
echo "3. Run: mvn javadoc:javadoc"
echo "4. Run: mvn checkstyle:check"
