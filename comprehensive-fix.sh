#!/bin/bash

# Comprehensive script to fix all compilation errors after User model refactoring
cd /workspaces/rakcha-desktop

echo "Starting comprehensive compilation fixes..."

# 1. Fix resultSet.getInt("num_telephone") to resultSet.getString("num_telephone")
echo "Fixing phone number retrieval from database..."
find src -name "*.java" -exec sed -i 's/resultSet\.getInt("num_telephone")/resultSet.getString("num_telephone")/g' {} \;

# 2. Fix User.getId() Long to int conversions - add .intValue()
echo "Fixing Long to int conversions for user IDs..."
find src -name "*.java" -exec sed -i 's/user\.getId()/user.getId().intValue()/g' {} \;
find src -name "*.java" -exec sed -i 's/\.getId()\.intValue()\.intValue()/.getId().intValue()/g' {} \; # Fix double conversions

# 3. Fix int literals to Long.valueOf()
echo "Fixing int literals to Long..."
find src -name "*.java" -exec sed -i 's/new Seat(\([0-9]\+\),/new Seat(Long.valueOf(\1),/g' {} \;
find src -name "*.java" -exec sed -i 's/new User(\([0-9]\+\),/new User(Long.valueOf(\1),/g' {} \;
find src -name "*.java" -exec sed -i 's/new Admin(\([0-9]\+\),/new Admin(Long.valueOf(\1),/g' {} \;
find src -name "*.java" -exec sed -i 's/new Client(\([0-9]\+\),/new Client(Long.valueOf(\1),/g' {} \;

# 4. Fix phone number int to String conversions
echo "Fixing phone number type conversions..."
find src -name "*.java" -exec sed -i 's/Integer\.valueOf(\([^)]*phoneNumber[^)]*\))/String.valueOf(\1)/g' {} \;
find src -name "*.java" -exec sed -i 's/new User([^,]*, [^,]*, [^,]*, \([0-9]\+\),/new User(\1, \2, \3, "\4",/g' {} \;

# 5. Fix specific parameter type issues
echo "Fixing parameter type mismatches..."
find src -name "*.java" -exec sed -i 's/statement\.setInt(\([0-9]\+\), [^)]*\.getId())/statement.setLong(\1, \2.getId())/g' {} \;
find src -name "*.java" -exec sed -i 's/statement\.setInt(\([0-9]\+\), [^)]*\.getPhoneNumber())/statement.setString(\1, \2.getPhoneNumber())/g' {} \;

# 6. Fix resultSet.getInt("id") to resultSet.getLong("id") for all ID fields
echo "Fixing ID retrieval from database..."
find src -name "*.java" -exec sed -i 's/resultSet\.getInt("id")/resultSet.getLong("id")/g' {} \;

# 7. Fix method calls that expect int but get Long
echo "Fixing method calls with type mismatches..."
find src -name "*.java" -exec sed -i 's/([^)]*)\.getId())/(\1.getId().intValue())/g' {} \;

echo "Compilation fixes applied. Testing compilation..."

mvn compile -q

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Still has compilation errors. Running detailed compile to see remaining issues..."
    mvn compile
fi
