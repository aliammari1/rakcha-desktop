#!/bin/bash

# Quick fix script for critical compilation errors
cd /workspaces/rakcha-desktop

echo "🔧 Applying critical fixes for Product Hunt demo..."

# Fix Long to int conversions by adding .intValue()
echo "Fixing Long to int conversions..."

# 1. Fix user.getId().intValue() patterns in service classes
find src -name "*.java" -exec sed -i 's/\.getId()\.intValue()\.intValue()/.getId().intValue()/g' {} \;

# 2. Fix specific service method calls
# Fix RatingCinemaService.java
if [ -f "src/main/java/com/esprit/services/cinemas/RatingCinemaService.java" ]; then
    sed -i 's/user\.getId()/user.getId().intValue()/g' src/main/java/com/esprit/services/cinemas/RatingCinemaService.java
fi

# Fix PanierService.java
if [ -f "src/main/java/com/esprit/services/produits/PanierService.java" ]; then
    sed -i 's/user\.getId()/user.getId().intValue()/g' src/main/java/com/esprit/services/produits/PanierService.java
fi

# Fix CategoryController.java
if [ -f "src/main/java/com/esprit/controllers/films/CategoryController.java" ]; then
    sed -i 's/currentUser\.getId()/currentUser.getId().intValue()/g' src/main/java/com/esprit/controllers/films/CategoryController.java
fi

# 3. Fix phone number int to String conversions
echo "Fixing phone number conversions..."

# Fix AdminDashboardController.java phone number issues
if [ -f "src/main/java/com/esprit/controllers/users/AdminDashboardController.java" ]; then
    sed -i 's/Integer\.valueOf(\([^)]*\)phoneNumber[^)]*)/String.valueOf(\1)/g' src/main/java/com/esprit/controllers/users/AdminDashboardController.java
    sed -i 's/\.setPhoneNumber(\([0-9]\+\))/.setPhoneNumber(String.valueOf(\1))/g' src/main/java/com/esprit/controllers/users/AdminDashboardController.java
    # Fix constructor calls that mix int and String
    sed -i 's/new User(\([0-9]\+\),\([^,]*\),\([^,]*\),\([0-9]\+\),/new User(Long.valueOf(\1),\2,\3,String.valueOf(\4),/g' src/main/java/com/esprit/controllers/users/AdminDashboardController.java
fi

# Fix ProfileController.java
if [ -f "src/main/java/com/esprit/controllers/users/ProfileController.java" ]; then
    sed -i 's/\.setPhoneNumber(\([0-9]\+\))/.setPhoneNumber(String.valueOf(\1))/g' src/main/java/com/esprit/controllers/users/ProfileController.java
    # Fix comparison operators
    sed -i 's/\([0-9]\+\) != user\.getPhoneNumber()/!String.valueOf(\1).equals(user.getPhoneNumber())/g' src/main/java/com/esprit/controllers/users/ProfileController.java
fi

# Fix SignUpController.java
if [ -f "src/main/java/com/esprit/controllers/users/SignUpController.java" ]; then
    sed -i 's/\.setPhoneNumber(\([0-9]\+\))/.setPhoneNumber(String.valueOf(\1))/g' src/main/java/com/esprit/controllers/users/SignUpController.java
fi

# 4. Fix int to Long conversions
echo "Fixing int to Long conversions..."

# Fix SeatService.java
if [ -f "src/main/java/com/esprit/services/cinemas/SeatService.java" ]; then
    sed -i 's/new Seat(\([0-9]\+\),/new Seat(Long.valueOf(\1),/g' src/main/java/com/esprit/services/cinemas/SeatService.java
fi

# Fix CategoryService.java
if [ -f "src/main/java/com/esprit/services/films/CategoryService.java" ]; then
    sed -i 's/new Category(\([0-9]\+\),/new Category(Long.valueOf(\1),/g' src/main/java/com/esprit/services/films/CategoryService.java
    sed -i 's/category\.getId()/category.getId().intValue()/g' src/main/java/com/esprit/services/films/CategoryService.java
fi

# 5. Fix method parameter type mismatches
echo "Fixing method parameter types..."

# Add .intValue() to methods that expect int parameters
find src -name "*.java" -exec sed -i 's/(\([^)]*\)\.getId())/(\1.getId().intValue())/g' {} \;
find src -name "*.java" -exec sed -i 's/\.getId()\.intValue()\.intValue()/.getId().intValue()/g' {} \; # Fix double conversions

# Fix long to int conversions
find src -name "*.java" -exec sed -i 's/(int) \([^)]*\)\.getId()/\1.getId().intValue()/g' {} \;

echo "✅ Critical fixes applied!"

# Test compilation
echo "🧪 Testing compilation..."
mvn compile -q

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful! Ready for Product Hunt demo."
    
    # Generate demo materials
    echo "🎬 Generating demo materials..."
    ./generate-demo.sh
    
    echo "🚀 RAKCHA is ready for Product Hunt launch!"
    echo ""
    echo "📋 Next steps:"
    echo "1. Review generated screenshots in demo/ folder"
    echo "2. Test application functionality"
    echo "3. Submit to Product Hunt using PRODUCT_HUNT_CHECKLIST.md"
    echo "4. Monitor community response and engagement"
    
else
    echo "⚠️  Some compilation issues remain. Check errors above."
    echo "   Application may still be demonstrable with manual fixes."
    echo ""
    echo "🎯 For Product Hunt demo:"
    echo "1. Focus on working features (UI, basic functionality)"
    echo "2. Use generated screenshots and documentation"
    echo "3. Highlight technical architecture and potential"
fi

echo ""
echo "📁 Product Hunt assets:"
echo "   - PRODUCT_HUNT_README.md (submission description)"
echo "   - PRODUCT_HUNT_CHECKLIST.md (launch checklist)"
echo "   - demo/ folder (screenshots and videos)"
echo "   - README.md (updated project documentation)"
