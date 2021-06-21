new="4.1.0-SNAPSHOT"
old="4.0.3"

find ../ -type f \( -name "pom.xml" -o -name "build.gradle*"  -o -name "README.md*" \) \
  -exec sed -i -e "s/<revision>$old<\/revision>/<revision>$new<\/revision>/g; s/escpos-coffee:$old/escpos-coffee:$new/g" {} \;
