#!/bin/bash

# Script to fix compilation errors after model refactoring
# Changes int id to Long id and int phoneNumber to String phoneNumber

echo "Fixing UserService compilation errors..."

# Fix resultSet.getInt("id") to resultSet.getLong("id")
find /workspaces/rakcha-desktop/src -name "*.java" -exec sed -i 's/resultSet\.getInt("id")/resultSet.getLong("id")/g' {} \;

# Fix statement.setInt(x, user.getId()) to statement.setLong(x, user.getId())
find /workspaces/rakcha-desktop/src -name "*.java" -exec sed -i 's/statement\.setInt(\([0-9]\+\), user\.getId())/statement.setLong(\1, user.getId())/g' {} \;

# Fix statement.setInt(x, *.getPhoneNumber()) to statement.setString(x, *.getPhoneNumber())
find /workspaces/rakcha-desktop/src -name "*.java" -exec sed -i 's/statement\.setInt(\([0-9]\+\), \([^)]*\)\.getPhoneNumber())/statement.setString(\1, \2.getPhoneNumber())/g' {} \;

# Fix other .getLong() to .getLong() conversions that need casting
find /workspaces/rakcha-desktop/src -name "*.java" -exec sed -i 's/\.getLong(\([0-9]\+\))\.intValue()/.getLong(\1)/g' {} \;

# Fix (int) cast conversions
find /workspaces/rakcha-desktop/src -name "*.java" -exec sed -i 's/(int) \([^)]*\)\.getId()/\1.getId().intValue()/g' {} \;

echo "Fixed basic compilation patterns. Now compiling to check for remaining errors..."

cd /workspaces/rakcha-desktop
mvn compile -q

echo "Script completed. Check output above for any remaining compilation errors."
